import java.rmi.*;
import java.rmi.server.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class JPoker24GameServer extends UnicastRemoteObject implements
		Remoteinterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3788462845858254702L;

	private static final String DB_FILE = "jpoker24game.sqlite";

	private Connection conn;

	private long Servertime;

	public JPoker24GameServer() throws SQLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException, RemoteException {
		Class.forName("org.sqlite.JDBC").newInstance();
		conn = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);
		Servertime = new Date().getTime();
		System.out
				.println("Database connection successful. And server time is "
						+ Servertime);
	}

	public static void main(String[] args) {
		try {
			JPoker24GameServer app = new JPoker24GameServer();
			System.setSecurityManager(new SecurityManager());
			Naming.rebind("Serverside", app);
			System.out.println("Service registered");
		} catch (Exception e) {
			System.err.println("Exception thrown: " + e);
		}
	}

	public synchronized int registerservice(String name, String password,
			String password2) throws RemoteException {
		try {
			PreparedStatement stmt = conn
					.prepareStatement("SELECT userName FROM userinfo WHERE userName = ?");
			stmt.setString(1, name);

			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				JOptionPane.showMessageDialog(new JFrame(),
						"User name is oppucied, please use another one",
						"Duplicated Name", JOptionPane.ERROR_MESSAGE);
				return -1;
			} else {
				PreparedStatement stmt1 = conn
						.prepareStatement("INSERT INTO userinfo(userName, password,gameplayed,loginTime) VALUES (?,?,?,?)");
				stmt1.setString(1, name);
				stmt1.setString(2, password);
				Random rand = new Random();
				int n = rand.nextInt(10) + 1;
				stmt1.setInt(3, n);
				stmt1.setLong(4, new Date().getTime());
				stmt1.execute();
				System.out.println("Record created in userinfo");
				// update win history table
				int records = rand.nextInt(n) + 1;
				PreparedStatement stmtwin = conn
						.prepareStatement("INSERT INTO winhistory(userName, winTime) VALUES (?,?)");
				stmtwin.setString(1, name);
				for (int i = 0; i < records; i++) {
					Random randreal = new Random();
					double x = 2 + 13 * randreal.nextDouble();
					stmtwin.setDouble(2, x);
					stmtwin.execute();
				}
			}
		} catch (SQLException | IllegalArgumentException e) {
			System.err.println("Error inserting record: " + e);
		}
		return 0;
	}

	public synchronized int loginservice(String name, String password)
			throws RemoteException {
		try {
			PreparedStatement stmt = conn
					.prepareStatement("SELECT password,loginTime FROM userinfo WHERE userName = ?");
			stmt.setString(1, name);

			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				if (!rs.getString(1).equals(password)) {
					JOptionPane.showMessageDialog(new JFrame(),
							"Invalid password", "Invalid  login name/password",
							JOptionPane.ERROR_MESSAGE);
					return -1;
				} else if (rs.getLong(2) >= Servertime) {
					JOptionPane.showMessageDialog(new JFrame(),
							"The user has already loggin in the game",
							"Already logged in", JOptionPane.ERROR_MESSAGE);
					return -1;
				} else {
					// update login time here
					PreparedStatement stmt1 = conn
							.prepareStatement("UPDATE userinfo SET loginTime = ? WHERE userName = ?");
					stmt1.setLong(1, new Date().getTime());
					stmt1.setString(2, name);
					int rows = stmt1.executeUpdate();
					if (rows > 0) {
						System.out.println("Userinfo of " + name + " updated");
					} else {
						System.err.println(name + " not found!");
					}
				}
			} else {
				JOptionPane.showMessageDialog(new JFrame(),
						"Invalid login name, please go to register first",
						"Invalid  login name/password",
						JOptionPane.ERROR_MESSAGE);
				return -1;
			}
		} catch (SQLException | IllegalArgumentException e) {
			System.err.println("Error inserting record: " + e);
		}
		return 0;
	}

	public synchronized int logoutservice(String name) throws RemoteException {
		try {
			PreparedStatement stmt = conn
					.prepareStatement("UPDATE userinfo SET loginTime = ? WHERE userName = ?");
			stmt.setInt(1, 0);
			stmt.setString(2, name);

			int rows = stmt.executeUpdate();
			if (rows > 0) {
				System.out.println("LoginTime of " + name + " updated");
			} else {
				System.err.println(name + " not found!");
			}
		} catch (SQLException | IllegalArgumentException e) {
			System.err.println("Error inserting login record: " + e);
		}
		return 0;
	}

	public synchronized String[] collectdata(String name)
			throws RemoteException {
		String[] arr = new String[5];
		try {
			PreparedStatement stmt = conn
					.prepareStatement("SELECT u2.userName,u2.loginTime,u2.gamePlayed,COUNT(w2.winTime) as win,AVG(w2.winTime) as avgWin,( SELECT COUNT(*) FROM ( SELECT u1.userName AS name,COUNT(w1.winTime) as win,u1.gamePlayed,AVG(w1.winTime) as avgWin FROM userinfo AS u1 LEFT JOIN winhistory AS w1 ON u1.userName=w1.userName GROUP BY u1.userName ) t1  WHERE ( t1.win > count(w2.winTime) ) OR  ( t1.win = count(w2.winTime) AND t1.gamePlayed < u2.gamePlayed ) OR ( t1.win = count(w2.winTime) AND t1.gamePlayed = u2.gamePlayed AND t1.avgWin < AVG(w2.winTime) ) )+1 as RANK FROM userinfo AS u2 LEFT JOIN winhistory AS w2 ON u2.userName=w2.userName WHERE u2.userName = ? GROUP BY u2.userName");
			stmt.setString(1, name);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				arr[0] = String.valueOf(rs.getInt(2));
				arr[1] = String.valueOf(rs.getInt(3));
				arr[2] = String.valueOf(rs.getInt(4));
				arr[3] = String.format("%.1f", rs.getFloat(5));
				arr[4] = String.valueOf(rs.getInt(6));
			} else {
				System.err.println("no record for user " + name);
				return null;
			}
		} catch (SQLException e) {
			System.err.println("Error inserting record: " + e);
		}
		return arr;
	}

	public synchronized String[][] rankBoard() throws RemoteException {
		String[][] arr = new String[10][5];
		try {
			PreparedStatement stmt = conn
					.prepareStatement("SELECT u2.userName, u2.loginTime, u2.gamePlayed, COUNT(w2.winTime) as win, AVG(w2.winTime) as avgWin, ( SELECT COUNT(*) FROM ( SELECT u1.userName AS name, COUNT(w1.winTime) as win, u1.gamePlayed, AVG(w1.winTime) as avgWin FROM userinfo AS u1 LEFT JOIN winhistory AS w1 ON u1.userName=w1.userName GROUP BY u1.userName ) t1 WHERE ( t1.win > count(w2.winTime) ) OR ( t1.win = count(w2.winTime) AND t1.gamePlayed < u2.gamePlayed ) OR ( t1.win = count(w2.winTime) AND t1.gamePlayed = u2.gamePlayed AND t1.avgWin < AVG(w2.winTime) ) )+1 as RANK FROM userinfo AS u2 LEFT JOIN winhistory AS w2 ON u2.userName=w2.userName GROUP BY u2.userName ORDER BY RANK ASC LIMIT 10");
			ResultSet rs = stmt.executeQuery();
			int i = 0;
			while (rs.next()) {
				arr[i][0] = String.valueOf(rs.getInt(6));
				arr[i][1] = String.valueOf(rs.getString(1));
				arr[i][2] = String.valueOf(rs.getInt(4));
				arr[i][3] = String.valueOf(rs.getInt(3));
				arr[i][4] = String.format("%.1f", rs.getFloat(5)) + "s";
				i++;
			}
		} catch (SQLException e) {
			System.err.println("Error inserting record: " + e);
		}
		return arr;
	}
}