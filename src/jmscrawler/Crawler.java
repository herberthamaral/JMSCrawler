/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jmscrawler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;


/**
 *
 * @author herberth
 */
public class Crawler implements Runnable{

    QueueBase jms;
    String url;
    public Crawler(String urlDeInicio)
    {
        url = urlDeInicio;
        jms = new QueueBase("queue/Crawler");
        try {
            jms.setMessageListener(new MessageListener() {

                public void onMessage(Message msg) {
                    onmessage(msg);
                }
            });
        } catch (JMSException ex) {
            //Adicionar algum tratamento de erros aqui
        }
    }
    public void run() {
        try {
            LinkExtractor.Extract(url);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.print("Executando Crawler..");
    }

    public void onmessage(Message msg)
    {
        System.out.println("recebendo mensagem");
    }

}
