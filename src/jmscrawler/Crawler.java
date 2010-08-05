/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jmscrawler;

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
