/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jmscrawler;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.jms.TopicConnection;
import javax.jms.TopicSubscriber;

/**
 *
 * @author herberth
 */
public class QueueBase {
    private Session session;
    private Context jndiContext;
    private TopicPublisher publisher;
    private MessageConsumer consumer;
    private Destination destination;
    private TopicConnection con;
    private MessageListener listener;

    public QueueBase(String _topic)
    {
        System.setProperty("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
        System.setProperty("java.naming.factory.url.pkgs", "org.jnp.interfaces");
        System.setProperty("java.naming.provider.url", "localhost"); //pegar esta propriedade do JNDI
        try {

            jndiContext = new InitialContext();
            destination =  (Destination) jndiContext.lookup(_topic);
            
            ConnectionFactory factory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");
            
            con = (TopicConnection) factory.createConnection();
            
            session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
            consumer = session.createConsumer(destination);

            con.start();
            
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

   public void setMessageListener(MessageListener listener) throws JMSException
   {
       this.listener = listener;
       consumer.setMessageListener(listener);
   }

    public void sendMessage(String text)
    {
        try
        {
            MessageProducer producer = session.createProducer(destination);
            TextMessage message = (TextMessage) session.createTextMessage(text); // será se rola uma mensagem de um objeto com mais indicadores das nossas regras de negócio?
            producer.send(message);
        }
        catch(Exception e)
        {
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
