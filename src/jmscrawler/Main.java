package jmscrawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 *
 * @author Herberth Amaral & Denison Wallace
 */
public class Main {

    /**
     * @param args the command line arguments
     */

    private static HashSet listaLinks;
    private static HashMap listaLinksBaixados;
    private static HashSet listaDominios;
    private static HashSet listaDominiosConcluidos;

    private static final int MAX_THREADS=8;
    private static int NUM_THREADS=0;
    private static final int MAX_ITERATIONS_WITHOUT_LINKS = 6;

    private static String baseUrl = "http://herberthamaral.com/";
    private static QueueBase jms;

    public static String getBaseUrl()
    {
        return baseUrl;
    }

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

        link = link.split("#")[0];
        if(!listaLinks.contains(link) && !listaLinksBaixados.containsKey(link) && !fromAnotherDomain)
        {
            
            listaLinks.add(link);
            System.out.println("Adicionando mais um link: "+link);
            // tenta iniciar nova thread para pegar mais links
            
        }
    }

    public static synchronized String getNextURLToDownload()
    {
        return null;
    }

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

        jms = new QueueBase("queue/Crawler");

        listaDominios.add(baseUrl); // verificar se o jboss não contem uma URL para baixarmos
        listaDominiosConcluidos.add(baseUrl);
        listaLinks.add(baseUrl);
        
        jms.sendMessage(baseUrl);

        jms = new QueueBase("queue/Crawler");
        try {
            jms.setMessageListener(new MessageListener() {

                public void onMessage(Message msg) {
                    TextMessage message = (TextMessage) msg;
                    try {
                        addLink(message.getText());
                    } catch (JMSException ex) {
                        System.out.println("Erro ao receber a mensagem: "+ex.toString());
                        ex.printStackTrace();
                    }
                }
            });
        } catch (JMSException ex) {
            ex.printStackTrace();
        }
        
        // verificar se o Jboss não contem um link pronto
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
                    new Thread(new Crawler(link)).start();
                }
                else
                {
                    numberOfIterationsWithoutLinks++;
                    if (numberOfIterationsWithoutLinks==MAX_ITERATIONS_WITHOUT_LINKS)
                    {
                        System.out.println("Parece que meu trabalho acabou.... see ya!");
                        break;
                    }
                    System.out.print("Sem novos links por enquanto... aguardando por mais...");
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
