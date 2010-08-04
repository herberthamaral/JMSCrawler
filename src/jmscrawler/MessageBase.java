/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jmscrawler;


import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

/**
 *
 * @author herberth
 */
public class MessageBase {
    protected Connection con;
    protected Destination destination;
    protected Session session;
    protected Context jndiContext;

    public MessageBase(String queue)
    {
        System.setProperty("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
        System.setProperty("java.naming.factory.url.pkgs", "org.jnp.interfaces");
        System.setProperty("java.naming.provider.url", "localhost");
        try {

            jndiContext = new InitialContext();
            ConnectionFactory factory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");

            con = factory.createConnection();
            session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
            destination = (Destination) jndiContext.lookup(queue);
            con.start();

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void finish()
    {
        try{
            session.close();
            con.close();
            jndiContext.close();
        }catch(Exception e){

            e.printStackTrace();
        }
    }
}
