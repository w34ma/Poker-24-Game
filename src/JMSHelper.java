import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JMSHelper {
	
	private static final String DEFAULT_HOST = "localhost";
	private static final int DEFAULT_PORT = 3700;
	
	private static final String JMS_CONNECTION_FACTORY = "jms/JPoker24GameConnectionFactory";
	 
	private static final String JMS_QUEUE = "jms/JPoker24GameQueue";
	private static final String JMS_TOPIC = "jms/JPoker24GameTopic";
	
	private Context jndiContext;
	private ConnectionFactory connectionFactory;
	private Connection connection;
	
	private Session session;
	private Queue queue;
	private Topic topic;
		
	public JMSHelper() throws NamingException, JMSException {
		this(DEFAULT_HOST);
		System.err.println("here1");
	}
	public JMSHelper(String host) throws NamingException, JMSException {
		int port = DEFAULT_PORT;
		System.err.println("here1");

		System.setProperty("org.omg.CORBA.ORBInitialHost", host);
		System.err.println("h1");

		System.setProperty("org.omg.CORBA.ORBInitialPort", ""+port);
		System.err.println("h2");

		try {
			jndiContext = new InitialContext();
			System.err.println("hz");

			connectionFactory = (ConnectionFactory)jndiContext.lookup(JMS_CONNECTION_FACTORY);
			System.err.println("h3");

			queue = (Queue)jndiContext.lookup(JMS_QUEUE);
			System.err.println("h4");

			topic = (Topic)jndiContext.lookup(JMS_TOPIC);
			System.err.println("h5");

		} catch (NamingException e) {
			System.err.println("JNDI failed: " + e);
			throw e;
		}
		try {
			connection = connectionFactory.createConnection();
			connection.start();
		} catch (JMSException e) {
			System.err.println("Failed to create connection to JMS provider: " + e);
			throw e;
		}
		System.err.println("here2");
	}
	public Session createSession() throws JMSException {
		if(session != null) {
			return session;
		} else {
			try {
				return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			} catch (JMSException e) {
				System.err.println("Failed creating session: " + e);
				throw e;
			}
		}
	}
	public ObjectMessage createMessage(Serializable obj) throws JMSException {
		try {
			return createSession().createObjectMessage(obj);
		} catch (JMSException e) {
			System.err.println("Error preparing message: " + e);
			throw e;
		}
	}
	public MessageProducer createQueueSender() throws JMSException {
		try {
			return createSession().createProducer(queue);
		} catch (JMSException e) {
			System.err.println("Failed sending to queue: " + e);
			throw e;
		}	
	}
	public MessageConsumer createQueueReader() throws JMSException {
		try {
			return createSession().createConsumer(queue);
		} catch (JMSException e) {
			System.err.println("Failed reading from queue: " + e);
			throw e;
		}	
	}
	public MessageProducer createTopicSender() throws JMSException {
		try {
			return createSession().createProducer(topic);
		} catch (JMSException e) {
			System.err.println("Failed sending to queue: " + e);
			throw e;
		}	
	}
	public MessageConsumer createTopicReader() throws JMSException {
		try {
			return createSession().createConsumer(topic);
		} catch (JMSException e) {
			System.err.println("Failed reading from queue: " + e);
			throw e;
		}	
	}
	
	@Override
	public void finalize() {

		try {
			session.close();
			connection.close();
			jndiContext.close();
		} catch (NamingException | JMSException e) {
			e.printStackTrace();
		}
		
	}
	
}