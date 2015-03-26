import java.rmi.registry.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import java.rmi.*;
import java.sql.SQLException;

public class JPoker24Game implements Runnable {
	private Remoteinterface r;
	int slogin;
	int sreg;
	int slogout;
	private Register registerwindow;
	private Login loginwindow;
	private Mainwindow mainwindow;
	// user personal info
	public String name;
	public String logintime;
	public String gameplayed;
	public String win;
	public String avgwin;
	public String rank;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new JPoker24Game(args[0]));
	} // "local host"

	public JPoker24Game(String host) {
		try {
			Registry registry = LocateRegistry.getRegistry(host);
			r = (Remoteinterface) registry.lookup("Serverside");
		} catch (Exception e) {
			System.err.println("Failed accessing RMI: " + e);
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
				String[][] leadresult = r.rankBoard();
				mainwindow = new Mainwindow(this, this.name, this.win,
						this.gameplayed, this.rank, this.avgwin, leadresult);
				mainwindow.setVisible(true);
			}
		} catch (RemoteException e) {
			System.err.println("Failed invoking RMI: " + e);
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
				String[][] leadresult = r.rankBoard();
				mainwindow = new Mainwindow(this, this.name, this.win,
						this.gameplayed, this.rank, this.avgwin, leadresult);
			}
		} catch (RemoteException e) {
			System.err.println("Failed invoking RMI: " + e);
		}
	}

	public void updatelogout(String a) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {
		try {
			slogout = r.logoutservice(a);
			mainwindow.setVisible(false);
			new Login(this);
		} catch (RemoteException e) {
			System.err.println("Failed invoking RMI: " + e);
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
}
