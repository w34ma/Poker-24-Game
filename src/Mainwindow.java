import java.awt.*;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.sql.SQLException;

import javax.swing.*;

public class Mainwindow extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1524589928144420219L;
	JButton profile = new JButton("User Profile");
	JButton play = new JButton("Play Game");
	JButton board = new JButton("Leader Board");
	JButton logout = new JButton("Logout");
	JButton btnNewgame = new JButton("New Game");
	JButton btnRestartGame = new JButton("Restart Game");

	JPoker24Game user;
	JPanel pnlWelcome = new JPanel();
	JPanel pnlStartBtn = new JPanel();
	JPanel pnlLB = new JPanel();
	JPanel pnlWaiting = new JPanel();
	Playground pnlPlaying = new Playground();
	JPanel pnlRestart = new JPanel();
	JPanel mainp = new JPanel();
	JPanel pro = new JPanel();
	JPanel rank = new JPanel();

	public class MyWindowListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			try {
				user.updatelogout(user.getusername());
			} catch (InstantiationException | IllegalAccessException
					| ClassNotFoundException | SQLException e1) {
				e1.printStackTrace();
			}
			System.exit(0);
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == board) {
			remove(mainp);
			repaint();
			rank = new JPanel();
			try {
				user.updaterankboard(user.name);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			rank = user.rankboard;
			mainp = rank;
			add(mainp, BorderLayout.CENTER);
			invalidate();
			validate();
		} else if (arg0.getSource() == profile) {
			remove(mainp);
			repaint();
			pro = new JPanel();
			try {
				user.updateprofile(user.name);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			pro = user.profile;
			mainp = pro;
			add(mainp, BorderLayout.CENTER);
			invalidate();
			validate();
		} else if (arg0.getSource() == play) {
			remove(mainp);
			repaint();
			switch (gameStatus) {
			case 0:
				mainp = pnlStartBtn;
				break;
			case 1:
				mainp = pnlWaiting;
				break;
			case 2:
				mainp = pnlPlaying;
				break;
			case 3:
				mainp = pnlRestart;
				break;
			}
			add(mainp, BorderLayout.CENTER);
			invalidate();
			validate();
		} else if (arg0.getSource() == btnNewgame
				|| arg0.getSource() == btnRestartGame) {
			gameStatus = 1;
			pnlWaiting = new JPanel();
			remove(mainp);
			repaint();
			pnlWaiting.add(new JLabel("Waiting for players......"));
			mainp = pnlWaiting;
			add(mainp, BorderLayout.CENTER);
			invalidate();
			validate();
			new waiting().execute();
		} else if (arg0.getSource() == logout) {
			try {
				user.updatelogout(user.getusername());
			} catch (InstantiationException | IllegalAccessException
					| ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
			System.exit(0);
		}
	}

	private class waiting extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() throws Exception {
			user.jmsclient.sendMessage(user.name);
			System.out.println("Message Sent:" + user.name);
			return null;
		}

		@Override
		protected void done() {
			super.done();
		}

	}

	private int gameStatus = 0; // 0 -> begin; 1 -> waiting; 2 -> playing; 3 ->
								// restart;

	/**
	 * Called by onMessage. init4.
	 */
	public void playinggame() {
		gameStatus = 2;
		remove(mainp);
		repaint();
		try {
			pnlPlaying.startGame(user.jmsclient.gamecards.playerinfos,
					user.jmsclient.gamecards, user);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		mainp = pnlPlaying;
		add(mainp, BorderLayout.CENTER);
		revalidate();
		repaint();
	}

	/**
	 * Called by onMessage.
	 */
	public void gameoverboard() {
		gameStatus = 3;
		remove(mainp);
		repaint();
		pnlRestart = new JPanel();
		repaint();
		JLabel j1 = new JLabel("Winner: " + user.jmsclient.gameover.name + "\n");
		j1.setFont(new Font("Serif", Font.PLAIN, 25));
		JLabel j2 = new JLabel(user.jmsclient.gameover.func + "\n");
		j2.setFont(new Font("Serif", Font.BOLD, 40));
		pnlRestart.setLayout(new BoxLayout(pnlRestart, BoxLayout.PAGE_AXIS));
		pnlRestart.add(j1, BorderLayout.CENTER);
		pnlRestart.add(j2, BorderLayout.CENTER);
		btnRestartGame.setPreferredSize(new Dimension(100, 40));
		btnRestartGame.addActionListener(this);
		pnlRestart.add(btnRestartGame, BorderLayout.SOUTH);
		mainp = pnlRestart;
		add(mainp, BorderLayout.CENTER);
		invalidate();
		validate();
	}

	// JPoker24Game user
	public Mainwindow(JPoker24Game user, String name, String win,
			String countgame, String currank, String avg, String[][] result) {
		this.user = user;
		setTitle("JPoker 24-Game");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 4));
		buttonPanel.add(profile);
		buttonPanel.add(play);
		buttonPanel.add(board);
		buttonPanel.add(logout);

		JLabel[] texts = new JLabel[5];
		texts[0] = new JLabel(name + "\n");
		texts[1] = new JLabel("Number of Wins: " + win + "\n");
		texts[2] = new JLabel("Number of games: " + countgame + "\n");
		texts[4] = new JLabel("Rank: #" + currank + "\n");
		texts[3] = new JLabel("Average time to win: " + avg + "s\n");
		for (int i = 0; i < 5; i++) {
			texts[i].setAlignmentX(LEFT_ALIGNMENT);
			texts[i].setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 20));
			if (i == 0) {
				texts[i].setFont(new Font("Serif", Font.BOLD, 30));
			} else if (i == 4) {
				texts[i].setFont(new Font("Serif", Font.PLAIN, 25));
			} else {
				texts[i].setFont(new Font("Serif", Font.PLAIN, 20));
			}
			pnlWelcome.setLayout(new BoxLayout(pnlWelcome, BoxLayout.Y_AXIS));
			pnlWelcome.add(texts[i]);
		}

		String[] columnNames = { "Rank", "Player", "Games won", "Games played",
				"Avg.winning times" };
		Object[][] data = new Object[10][5];
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < 5; j++) {
				data[i][j] = result[i][j];
			}
		}
		JTable table = new JTable(data, columnNames);
		table.setEnabled(false);
		pnlLB.add(new JScrollPane(table));

		// init1.add(new JLabel("This is the play game"));
		pnlStartBtn.add(btnNewgame);
		mainp = pnlWelcome;

		getContentPane().add(BorderLayout.CENTER, mainp);

		board.addActionListener(this);
		profile.addActionListener(this);
		play.addActionListener(this);
		logout.addActionListener(this);
		btnNewgame.addActionListener(this);

		getContentPane().add(BorderLayout.NORTH, buttonPanel);

		pack();
		setLocationRelativeTo(null);
		setSize(600, 400);
		setVisible(true);
		addWindowListener(new MyWindowListener());
	}

}