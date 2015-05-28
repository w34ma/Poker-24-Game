import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;

@SuppressWarnings("unused")
public class Playground extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JButton loginbtn = new JButton("Login");
	JLabel[] texts = new JLabel[5];
	ArrayList<JMSMessage> playerinfos;
	GamePlayMsg gamecards;
	String func;
	JPoker24Game user;
	JPanel down = new JPanel();
	JPanel right = new JPanel();
	JPanel up = new JPanel();

	public Playground() {
		down = new JPanel();
		right = new JPanel();
		up = new JPanel();
	}

	void startGame(ArrayList<JMSMessage> playerinfos, GamePlayMsg gamecards,
			JPoker24Game user) throws URISyntaxException {
		this.user = user;
		this.gamecards = gamecards;
		this.playerinfos = playerinfos;
		down.removeAll();
		right.removeAll();
		up.removeAll();
		/******************** CARDS ****************************/
		up.setPreferredSize(new Dimension(350, 150));
		up.setLayout(new BoxLayout(up, BoxLayout.LINE_AXIS));
		System.out.println("Playground : start adding cards");
		for (int i = 0; i < 4; i++) {
			JPanel p = new JPanel();
			p.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
			System.out.println("Playground : " + gamecards.cards[i]);
			URL url;
			String urlstr;
			//url = getClass().getResource("cards/" + gamecards.cards[i])
			//		.toURI().toURL();
			urlstr = "cards/" + gamecards.cards[i];
			ImageIcon card = new ImageIcon(urlstr);
			Image img = card.getImage();
			Image resized = img.getScaledInstance(60, 90,
					Image.SCALE_SMOOTH);
			p.setPreferredSize(new Dimension(90, 110));
			p.add(new JLabel(new ImageIcon(resized)));
			up.add(p);
		}

		/******************** PLAYERS ****************************/
		System.out.println("Playground : start adding players");

		right.setLayout(new BoxLayout(right, BoxLayout.PAGE_AXIS));
		for (JMSMessage playerinfo : playerinfos) {
			JPanel j = new JPanel();
			j.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(1,
					1, 1, 1, Color.BLACK), BorderFactory.createEmptyBorder(10,
					10, 10, 10)));
			j.setLayout(new BoxLayout(j, BoxLayout.PAGE_AXIS));
			j.setPreferredSize(new Dimension(180, 70));
			j.add(new JLabel(playerinfo.name));
			JLabel wininfo = new JLabel("Wins: " + playerinfo.win + "/"
					+ playerinfo.total + " " + "Avg: " + playerinfo.avg + "s");
			j.add(wininfo);
			right.add(j);
		}

		/******************** INPUTS ****************************/
		System.out.println("Playground : start adding input box");

		JLabel result = new JLabel("= 24");
		JTextField tf1 = new JTextField();
		tf1.setPreferredSize(new Dimension(300, 40));
		down.add(tf1, BorderLayout.WEST);
		down.add(result, BorderLayout.CENTER);

		tf1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String retStr = user.r.compute(user.name, tf1.getText());
					if (!retStr.equals(""))
						JOptionPane.showMessageDialog(new JFrame(), retStr,
								"Wrong Input", JOptionPane.ERROR_MESSAGE);
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
				System.out.println("Message Sent:" + tf1.getText());
			}
		});

		this.add(up, BorderLayout.CENTER);
		this.add(right, BorderLayout.EAST);
		this.add(down, BorderLayout.SOUTH);
	}

	public void updateplayer(ArrayList<String> players) {
		System.out.println("Playground : start updating quit player info");
		this.remove(right);
		this.remove(down);
		right = new JPanel();
		right.setLayout(new BoxLayout(right, BoxLayout.PAGE_AXIS));
		for (String player : players) {
			for (JMSMessage info : playerinfos) {
				if (info.name.equals(player)) {
					JPanel j = new JPanel();
					j.setBorder(new CompoundBorder(BorderFactory
							.createMatteBorder(1, 1, 1, 1, Color.BLACK),
							BorderFactory.createEmptyBorder(10, 10, 10, 10)));
					j.setLayout(new BoxLayout(j, BoxLayout.PAGE_AXIS));
					j.setPreferredSize(new Dimension(180, 70));
					j.add(new JLabel(player));
					JLabel wininfo = new JLabel("Wins: " + info.win + "/"
							+ info.total + " " + "Avg: " + info.avg + "s");
					j.add(wininfo);
					right.add(j);
					break;
				}
			}
		}
		this.add(right, BorderLayout.EAST);
		this.add(down, BorderLayout.SOUTH);
		this.revalidate();
		this.repaint();
	}
}