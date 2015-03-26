import java.awt.*;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
	JPoker24Game user;
	JPanel init = new JPanel();
	JPanel init1 = new JPanel();
	JPanel init2 = new JPanel();
	JPanel mainp = new JPanel();

	public class MyWindowListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			try {
				user.updatelogout(user.getusername());
			} catch (InstantiationException | IllegalAccessException
					| ClassNotFoundException | SQLException e1) {
				// TODO Auto-generated catch block
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
			try {
				user.updatelogout(user.getusername());
			} catch (InstantiationException | IllegalAccessException
					| ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.exit(0);
		}
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
			init.setLayout(new BoxLayout(init, BoxLayout.Y_AXIS));
			init.add(texts[i]);
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
		setVisible(true);
		addWindowListener(new MyWindowListener());
	}

}