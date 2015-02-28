import java.awt.*;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

public class Mainwindow extends JFrame implements ActionListener {
	JButton profile = new JButton("User Profile");
	JButton play = new JButton("Play Game");
	JButton board = new JButton("Leader Board");
	JButton logout = new JButton("Logout");
	JPoker24Game user;
	JPanel init = new JPanel();
	JPanel init1 = new JPanel();
	JPanel init2 = new JPanel();
	JPanel mainp = new JPanel();

	public class MyWindowListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			user.updatelogout(user.getusername());
			System.exit(0);
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == board) {
			remove(mainp);
			repaint();
			mainp = init2;
			add(mainp, BorderLayout.CENTER);
			invalidate();
			validate();
		} else if (arg0.getSource() == profile) {
			remove(mainp);
			repaint();
			mainp = init;
			add(mainp, BorderLayout.CENTER);
			invalidate();
			validate();
		} else if (arg0.getSource() == play) {
			remove(mainp);
			repaint();
			mainp = init1;
			add(mainp, BorderLayout.CENTER);
			invalidate();
			validate();
		} else if (arg0.getSource() == logout) {
			user.updatelogout(user.getusername());
			System.exit(0);
		}
	}

	public Mainwindow(JPoker24Game user) {
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
		texts[0] = new JLabel("Kevin\n");
		texts[1] = new JLabel("Number of Wins: 10\n");
		texts[2] = new JLabel("Number of games: 20\n");
		texts[4] = new JLabel("Rank: #10\n");
		texts[3] = new JLabel("Average time to win: 12.5s\n");
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
			init.setLayout(new BoxLayout(init, BoxLayout.Y_AXIS));
			init.add(texts[i]);
		}

		String[] columnNames = { "Rank", "Player", "Games won", "Games played",
				"Avg.winning times" };
		Object[][] data = {
				{ new Integer(1), "player4", new Integer(30), new Integer(5),
						"10.4s" },
				{ new Integer(2), "player2", new Integer(10), new Integer(3),
						"13.2s" },
				{ new Integer(3), "player3", new Integer(20), new Integer(2),
						"15.1s" },
				{ new Integer(4), "player7", new Integer(15), new Integer(20),
						"12.8s" },
				{ new Integer(5), "player5", new Integer(10), new Integer(10),
						"10.2s" },
				{ new Integer(6), "player10", new Integer(9), new Integer(10),
						"17.1s" },
				{ new Integer(7), "player6", new Integer(8), new Integer(10),
						"15.4s" },
				{ new Integer(8), "player1", new Integer(7), new Integer(10),
						"16.2s" },
				{ new Integer(9), "player9", new Integer(6), new Integer(10),
						"14.1s" },
				{ new Integer(10), "player8", new Integer(5), new Integer(10),
						"18.4s" } };
		JTable table = new JTable(data, columnNames);
		table.setEnabled(false);
		init2.add(new JScrollPane(table));

		init1.add(new JLabel("This is the play game"));

		mainp = init;

		getContentPane().add(BorderLayout.CENTER, mainp);

		board.addActionListener(this);
		profile.addActionListener(this);
		play.addActionListener(this);
		logout.addActionListener(this);

		getContentPane().add(BorderLayout.NORTH, buttonPanel);

		pack();
		setLocationRelativeTo(null);
		setSize(550, 300);
		setVisible(false);
		addWindowListener(new MyWindowListener());
	}

}