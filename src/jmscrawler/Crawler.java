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
    }

    public void run() {
        try {
            LinkExtractor.Extract(url);
        }catch(Exception e)
        {
            System.out.println("Ooops...");
            e.printStackTrace();
        }
    }
}
