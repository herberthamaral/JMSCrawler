/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jmscrawler;


import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageListener;
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
public class TopicBase {
    private TopicSession session;
    private Context jndiContext;
    private TopicPublisher publisher;
    private TopicSubscriber subscriber;
    private Topic topic;
    private TopicConnection con;
    private MessageListener listener;

    public TopicBase(String _topic)
    {
        System.setProperty("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
        System.setProperty("java.naming.factory.url.pkgs", "org.jnp.interfaces");
        System.setProperty("java.naming.provider.url", "localhost"); //pegar esta propriedade do JNDI
        try {

            jndiContext = new InitialContext();
            topic =  (Topic) jndiContext.lookup(_topic);
            
            TopicConnectionFactory topicFactory = (TopicConnectionFactory) jndiContext.lookup("ConnnectionFactory");
            
            con = topicFactory.createTopicConnection();
            
            session = con.createTopicSession(false, TopicSession.AUTO_ACKNOWLEDGE);
            con.start();
            
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

   public void setMessageListener(MessageListener listener) throws JMSException
   {
       this.listener = listener;
       subscriber.setMessageListener(listener);
   }

    public void sendMessage() throws JMSException
    {
        TopicPublisher publihser = session.createPublisher(topic);
        TextMessage message = (TextMessage) session.createMessage(); // será se rola uma mensagem de um objeto com mais indicadores das nossas regras de negócio?
        publihser.publish(message);
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
