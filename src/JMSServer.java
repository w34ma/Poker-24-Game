
import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.naming.NamingException;


public class JMSServer {
	
	public int count = 0;
	public JMSHelper jmsHelper;
	public int flag = 0;
	MessageConsumer queueReader;
	MessageProducer topicSender;
	
	public JMSServer() throws NamingException, JMSException {
		jmsHelper = new JMSHelper();
	}
	
	public void start() throws JMSException {
		 queueReader = jmsHelper.createQueueReader();
		 topicSender = jmsHelper.createTopicSender();
	}
	
	public JMSMessage receiveMessage(MessageConsumer queueReader) throws JMSException {
		try {
			System.out.println("JMSServer: start receiving message");
			Message jmsMessage = queueReader.receive();
			Serializable _player = ((ObjectMessage)jmsMessage).getObject();
			if (_player instanceof JMSMessage) {
				JMSMessage player = (JMSMessage)_player;
				System.out.println("JMSServer: received message" + player);
				return player;
			}
			throw new JMSException("reason?");
		} catch(JMSException e) {
			System.err.println("JMSServer: Failed to receive message "+e);
			throw e;
		}
	}
	
	public void broadcastMessage(MessageProducer topicSender, Message jmsMessage) throws JMSException {
		try {
			System.out.println("JMSServer: start broadcast message");
			topicSender.send(jmsMessage);
			System.out.println("JMSServer: Finish broadcast message");
		} catch(JMSException e) {
			System.err.println("JMSServer: Failed to boardcast message "+e);
			throw e;
		}
	}
}
