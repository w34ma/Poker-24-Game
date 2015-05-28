import java.util.ArrayList;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.naming.NamingException;

public class JMSClient implements MessageListener {

	JMSHelper jmsHelper;
	private MessageProducer queueSender;
	private MessageConsumer topicReceiver;
	GamePlayMsg gamecards;
	GameoverMsg gameover;
	QuitMsg quitplayer;
	private JPoker24Game user;
	public ArrayList<JMSMessage> playerinfos = new ArrayList<JMSMessage>();

	public JMSClient(JPoker24Game user) throws NamingException, JMSException {
		this.user = user;
		jmsHelper = new JMSHelper();
		init();
	}

	public JMSClient(String host, JPoker24Game user) throws NamingException,
			JMSException {
		this.user = user;
		jmsHelper = new JMSHelper(host);
		init();
	}

	private void init() throws JMSException {
		queueSender = jmsHelper.createQueueSender();
		System.out.println("JMSClient: create queue sender done");
		topicReceiver = jmsHelper.createTopicReader();
		System.out.println("JMSClient: create topic reader done");
		topicReceiver.setMessageListener(this);
		System.out.println("JMSClient: init done");
	}

	public void sendMessage(String name) {
		JMSMessage chatMessage = new JMSMessage(name, user.win, user.avgwin,user.gameplayed);
		if (chatMessage != null) {
			System.out.println("JMSClient: Trying to send message: "
					+ chatMessage);
			Message message = null;
			try {
				message = jmsHelper.createMessage(chatMessage);
			} catch (JMSException e) {
			}
			if (message != null) {
				try {
					queueSender.send(message);
				} catch (JMSException e) {
					System.err.println("JMSClient: Failed to send message");
				}
			}
		}
		System.out.println("JMSClient: Message send" + chatMessage);
	}

	@Override
	public void onMessage(Message jmsMessage) {
		System.out.println("JMSClient: message received!");
		Object output = null;
		try {
			output = ((ObjectMessage) jmsMessage).getObject();
		} catch (JMSException e) {
			e.printStackTrace();
		}
		if (output instanceof GamePlayMsg) {
			gamecards = (GamePlayMsg) output;
			for (JMSMessage msg : gamecards.playerinfos) {
				if (msg.name.equals(this.user.name)) {
					playerinfos = gamecards.playerinfos;
					user.mainwindow.playinggame();
					System.out
							.println("JMSClient: we have a new play board now");
				}
			}
		} else if (output instanceof GameoverMsg) {
			gameover = (GameoverMsg) output;
			if (gameover.playerlist.contains(this.user.name)) {
				user.mainwindow.gameoverboard();
				System.out.println("JMSClient: we finish the game");
			}
		} else if (output instanceof QuitMsg) {
			quitplayer = (QuitMsg) output;
			if (quitplayer.players.contains(this.user.name)) {
				user.mainwindow.pnlPlaying.updateplayer(quitplayer.players);
				System.out.println("JMSClient: we lost a user");
			}
		}
	}

}