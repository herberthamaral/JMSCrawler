/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jmscrawler;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;


/**
 *
 * @author herberth
 */
public class Crawler implements Runnable{

    TopicBase jms;

    public Crawler(String urlDeInicio)
    {
        
    }
    public void run() {
        System.out.print("Executando Crawler..");
        jms = new TopicBase("topic/crawler");
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

    public void onmessage(Message msg)
    {
        System.out.println("recebendo mensagem");
    }

}
