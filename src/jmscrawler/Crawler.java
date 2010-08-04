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

    MessageBase jms;


    public void run() {
        System.out.print("Executando Crawler..");
        jms = new MessageBase("crawler");
    }

}
