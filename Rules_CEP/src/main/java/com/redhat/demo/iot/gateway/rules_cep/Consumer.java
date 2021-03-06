package com.redhat.demo.iot.gateway.rules_cep;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Consumer  implements ExceptionListener  {

	private ActiveMQConnectionFactory 	connectionFactory;
	private	Connection				  	connection;
	private Session						session;
	private Destination					destination;
	private MessageConsumer				consumer;
	private String						queueName;
	private String						brokerURL;
	private String						uid;
	private String						passwd;
	
	public Consumer(String queueName, String brokerURL, String uid, String passwd) throws JMSException {
		
		this.queueName = queueName;
		this.brokerURL = brokerURL;
		this.uid		= uid;
		this.passwd		= passwd;
		
		init_consumer(queueName, brokerURL, uid, passwd);

	}
	
	private void init_consumer(String queueName, String brokerURL, String uid, String passwd) throws JMSException {
		  // Create a ConnectionFactory
        connectionFactory = new ActiveMQConnectionFactory(uid, passwd, brokerURL);

        // Create a Connection
        connection = connectionFactory.createConnection();
        connection.start();

        connection.setExceptionListener(this);

        // Create a Session
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Create the destination (Topic or Queue)
        destination = session.createQueue(queueName);

        // Create a MessageConsumer from the Session to the Topic or Queue
        consumer = session.createConsumer(destination);
	}
	
	 public String run(int waitTimeout) {
		 TextMessage 	textMessage;
		 String 		text=null;
		 
         try {
             // Wait for a message
             Message message = consumer.receive(waitTimeout);

             if (message instanceof TextMessage) {
                 textMessage = (TextMessage) message;
                 text = textMessage.getText();
             } 
         } catch (Exception e) {
             System.out.println("Caught: " + e);
             e.printStackTrace();
             System.out.println("---- Re-establishing connection ---");
             try {
				init_consumer(queueName, brokerURL, uid, passwd);
			} catch (JMSException e1) {
				System.out.println("Caught while trying to reconnect: " + e);
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
         }
         
         return text;
     }

	 public void close() throws JMSException {

         consumer.close();
         session.close();
         connection.close();

	 }

	public void onException(JMSException arg0) {
		// TODO Auto-generated method stub
		
	}
}

