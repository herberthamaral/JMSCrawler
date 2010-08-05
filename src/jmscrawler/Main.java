package jmscrawler;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;


/**
 *
 * @author Herberth Amaral & Denison Wallace
 */
public class Main {



    //Lista de links descobertos
    private static HashSet listaLinks;

    //lista de links já baixados
    private static HashMap listaLinksBaixados;

    //lista de domínios descobertos
    private static HashSet listaDominios;

    //lista de domínios já completamente baixados
    private static HashSet listaDominiosConcluidos;

    // número máximo de Threads de downloads que o programa pode executar
    private static final int MAX_THREADS=256;

    //número de threads atuais
    private static int NUM_THREADS=0;

    // Número máximo de iterações sem links. Isto é importante para determinar
    // quando terminamos de baixar o domínio completamente
    private static final int MAX_ITERATIONS_WITHOUT_LINKS = 6;

    //url que vamos começar
    private static String baseUrl = "http://herberthamaral.com/";

    //Um utilitário para o JMS. Ele encapsula toda a burocracia e acesso
    //ao servidor de mensagens
    private static QueueBase jms;

    public static String getBaseUrl()
    {
        return baseUrl;
    }

    /**
     * Método utilizado para reportar um novo link ou um novo domínio ao crawler
     * @param link link a ser reportado
     */
    public static synchronized void addLink(String link)
    {
        // verifica se o link está no mesmo domínio ou se o mesmo já não foi incluído
        Boolean fromAnotherDomain = false;
        if (!link.startsWith(baseUrl) && (link.startsWith("http://") || link.startsWith("https://")))
        {
            fromAnotherDomain = true;
            //se não estiver, manda para o servidor de mensagens
            try
            {
                URL url = new URL(link);
                String linkToAdd = url.getProtocol()+"://"+url.getHost()+"/";
                if(!listaDominios.contains(linkToAdd))
                {
                    System.out.println("Adicionando mais um domínio: "+linkToAdd);
                    listaDominios.add(linkToAdd);
                    jms.sendMessage(linkToAdd);
                }
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        try{
            //Adaptação para remover âncoras dos links
            link = link.split("#")[0];
        }catch(Exception e){}
        
        if(!link.startsWith(baseUrl))
                link = baseUrl+link;
        
        if(!listaLinks.contains(link) && !listaLinksBaixados.containsKey(link) && !fromAnotherDomain)
        {
            listaLinks.add(link);
            System.out.println("Adicionando mais um link: "+link);
        }
    }


    /**
     * Adiciona uma nova URL na lista de urls já concluídas. A thread de download
     * geralmente morre aqui.
     * 
     * @param url URL
     * @param content Conteúdo da URL
     */
    public static synchronized void reportDownloadedURL(String url,String content)
    {
        if (!listaLinksBaixados.containsKey(url))
            listaLinksBaixados.put(url, content);

        // fim da minha thread
        NUM_THREADS --;
        System.out.println("Finalizando thread...");
    }

    public static void main(String[] args) throws InterruptedException {
        listaLinks = new HashSet();
        listaDominios = new HashSet();
        listaDominiosConcluidos = new HashSet();
        listaLinksBaixados = new HashMap();

        jms = new QueueBase("queue/Crawl");

        listaDominios.add(baseUrl); // verificar se o jboss não contem uma URL para baixarmos
        listaDominiosConcluidos.add(baseUrl);
        listaLinks.add(baseUrl);
        
        jms.sendMessage(baseUrl);
        
        // verificar se o Jboss não contem um link pronto
        String urlFromServer = jms.getMessage();


        if(!urlFromServer.equals(""))
        {
            baseUrl = urlFromServer;
            new Thread(new Crawler(urlFromServer)).start(); // se tiver, baixa dele
        }
        else
            new Thread(new Crawler(baseUrl)).start();
        
        int numberOfIterationsWithoutLinks = 0;
        for(;;)
        {
            if (NUM_THREADS<MAX_THREADS)
            {
                System.out.println("Iniciando nova Thread...");
                NUM_THREADS++;
                if(!listaLinks.isEmpty())
                {
                    String link = listaLinks.iterator().next().toString();
                    listaLinks.remove(link);
                    numberOfIterationsWithoutLinks = 0;
                    new Thread(new Crawler(link)).start();
                }
                else
                {
                    numberOfIterationsWithoutLinks++;
                    if (numberOfIterationsWithoutLinks==MAX_ITERATIONS_WITHOUT_LINKS)
                    {
                        System.out.println("Parece que meu trabalho acabou.... tentando obter novo domínio para crawlear!");

                        String dominio = jms.getMessage();
                        if(!dominio.equals(""))
                        {
                            System.out.print("YAY! Achei um novo domínio: "+dominio);
                            baseUrl = dominio;
                            new Thread(new Crawler(dominio)).start();
                        }
                        break;
                    }
                    System.out.print("Sem novos links por enquanto... aguardando por mais...");
                    Thread.sleep(5000);
                }
            }
            else
            {
                System.out.println("Número máximo de threads rodando simultaneamente alcaçado... dormindo um pouco...");
                Thread.sleep(5000);
            }
        }
        jms.finish();
    }
}
