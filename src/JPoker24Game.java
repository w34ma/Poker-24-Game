import java.awt.Font;
import java.rmi.registry.*;

import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import java.rmi.*;
import java.sql.SQLException;

public class JPoker24Game implements Runnable {
	public Remoteinterface r;
	int slogin;
	int sreg;
	int slogout;
	private Register registerwindow;
	private Login loginwindow;
	Mainwindow mainwindow;
	// user personal info
	public String name;
	public String logintime;
	public String gameplayed;
	public String win;
	public String avgwin;
	public String rank;
	public JMSClient jmsclient;
	public JPanel profile = new JPanel();
	public JPanel rankboard = new JPanel();

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new JPoker24Game(args[0]));
	} // "local host"

	public JPoker24Game(String host) {
		try {
			Registry registry = LocateRegistry.getRegistry(host);
			r = (Remoteinterface) registry.lookup("Serverside");
			// jmsclient.name = name;
			try {
				jmsclient = new JMSClient(host, this);
			} catch (NamingException | JMSException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			System.err.println("Failed accessing RMI1: " + e);
		}
	}
	

	public void updateregister(String a, String b, String c)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException {
		try {
			int flag = 0;
			name = a;
			if (a.length() == 0) {
				JOptionPane.showMessageDialog(new JFrame(),
						"User name is empty, type in user name", "Empty Name",
						JOptionPane.ERROR_MESSAGE);
				flag = 1;
				return;
			} else if (b.length() == 0) {
				flag = 1;
				JOptionPane.showMessageDialog(new JFrame(),
						"Password is empty, type in your password",
						"Empty Password", JOptionPane.ERROR_MESSAGE);
				return;
			} else if (!c.equals(b)) {
				flag = 1;
				JOptionPane.showMessageDialog(new JFrame(),
						"Passwords are not matched", "Wrong Password",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			sreg = r.registerservice(a, b, c);
			if (sreg == 0 && flag == 0) {
				registerwindow.frame.setVisible(false);
				String[] arr = r.collectdata(a);
				this.name = a;
				this.logintime = arr[0];
				this.gameplayed = arr[1];
				this.win = arr[2];
				this.avgwin = arr[3];
				this.rank = arr[4];
				String[][] leadresult = r.rankBoard(a);
				mainwindow = new Mainwindow(this, this.name, this.win,
						this.gameplayed, this.rank, this.avgwin, leadresult);
				mainwindow.setVisible(true);
			}
		} catch (RemoteException e) {
			System.err.println("Failed invoking RMI2: " + e);
		}
	}

	public void updatelogin(String a, String b) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {
		try {
			int flag = 0;
			name = a;
			if (a.length() == 0) {
				JOptionPane.showMessageDialog(new JFrame(),
						"User name is empty, type in user name", "Empty Name",
						JOptionPane.ERROR_MESSAGE);
				flag = 1;
				return;
			} else if (b.length() == 0) {
				flag = 1;
				JOptionPane.showMessageDialog(new JFrame(),
						"Password is empty, type in your password",
						"Empty Password", JOptionPane.ERROR_MESSAGE);
				return;
			}
			slogin = r.loginservice(a, b);
			if (slogin == 0 && flag == 0) {
				loginwindow.frame.setVisible(false);
				String[] arr = r.collectdata(a);
				this.name = a;
				this.logintime = arr[0];
				this.gameplayed = arr[1];
				this.win = arr[2];
				this.avgwin = arr[3];
				this.rank = arr[4];
				String[][] leadresult = r.rankBoard(a);
				mainwindow = new Mainwindow(this, this.name, this.win,
						this.gameplayed, this.rank, this.avgwin, leadresult);
			}
		} catch (RemoteException e) {
			System.err.println("Failed invoking RMI3: " + e);
		}
	}

	public void updatelogout(String a) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {
		try {
			slogout = r.logoutservice(a);
			mainwindow.setVisible(false);
			new Login(this);
		} catch (RemoteException e) {
			System.err.println("Failed invoking RMI4: " + e);
		}
	}

	public void openlogin() {
		loginwindow.tf1.setText(null);
		loginwindow.tf2.setText(null);
		registerwindow.tf1.setText(null);
		registerwindow.tf2.setText(null);
		registerwindow.tf3.setText(null);
		loginwindow.frame.setVisible(true);
		registerwindow.frame.setVisible(false);
	}

	public void openreg() {
		loginwindow.tf1.setText(null);
		loginwindow.tf2.setText(null);
		registerwindow.tf1.setText(null);
		registerwindow.tf2.setText(null);
		registerwindow.tf3.setText(null);
		loginwindow.frame.setVisible(false);
		registerwindow.frame.setVisible(true);
	}

	public String getusername() {
		return this.name;
	}

	public Remoteinterface getinterface() {
		return this.r;
	}

	@Override
	public void run() {
		loginwindow = new Login(this);
		registerwindow = new Register(this);
	}

	public void updateprofile(String a) throws RemoteException {
		profile = new JPanel();
		String[] arr = r.collectdata(a);

		JLabel[] texts = new JLabel[5];
		texts[0] = new JLabel(name + "\n");
		texts[1] = new JLabel("Number of Wins: " + arr[2] + "\n");
		texts[2] = new JLabel("Number of games: " + arr[1] + "\n");
		texts[4] = new JLabel("Rank: #" + arr[4] + "\n");
		texts[3] = new JLabel("Average time to win: " + arr[3] + "s\n");

		for (int i = 0; i < 5; i++) {
			texts[i].setAlignmentX((float) 0.0);
			texts[i].setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 20));
			if (i == 0) {
				texts[i].setFont(new Font("Serif", Font.BOLD, 30));
			} else if (i == 4) {
				texts[i].setFont(new Font("Serif", Font.PLAIN, 25));
			} else {
				texts[i].setFont(new Font("Serif", Font.PLAIN, 20));
			}
			profile.setLayout(new BoxLayout(profile, BoxLayout.Y_AXIS));
			profile.add(texts[i]);
		}
	}

	public void updaterankboard(String a) throws RemoteException {
		rankboard = new JPanel();
		String[][] leadresult = r.rankBoard(a);
		String[] columnNames = { "Rank", "Player", "Games won", "Games played",
				"Avg.winning times" };
		Object[][] data = new Object[10][5];
		for (int i = 0; i < leadresult.length; i++) {
			for (int j = 0; j < 5; j++) {
				data[i][j] = leadresult[i][j];
			}
		}
		JTable table = new JTable(data, columnNames);
		table.setEnabled(false);
		rankboard.add(new JScrollPane(table));
	}
}
