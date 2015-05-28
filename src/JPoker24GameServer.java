import java.rmi.*;
import java.rmi.server.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class JPoker24GameServer extends UnicastRemoteObject implements
		Remoteinterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3788462845858254702L;

	private Connection conn;

	private long Servertime;
	private long starttime;

	private int timeout = 0;

	private JMSServer jmsserver;

	public ArrayList<JMSMessage> playerinfos = new ArrayList<JMSMessage>();

	public int dbflag = 0;

	ReceivePlayer receivePlayer = new ReceivePlayer();

	private Set<RoomManagement> rooms = new HashSet<RoomManagement>();

	private static final String DB_FILE = "jpoker24game.sqlite";

	//on different machine
	// public JPoker24GameServer(String DBFile) throws SQLException,
	// InstantiationException, IllegalAccessException,
	// ClassNotFoundException, RemoteException, javax.jms.JMSException,
	// NamingException {
	// Class.forName("org.sqlite.JDBC").newInstance();
	// conn = DriverManager.getConnection("jdbc:sqlite:" + DBFile);
	// Servertime = new Date().getTime();
	// this.jmsserver = new JMSServer();
	// jmsserver.start();
	// System.out.println("jms server start");
	// }

	public JPoker24GameServer() throws SQLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException, RemoteException,
			javax.jms.JMSException, NamingException {
		Class.forName("org.sqlite.JDBC").newInstance();
		conn = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);
		Servertime = new Date().getTime();
		this.jmsserver = new JMSServer();
		jmsserver.start();
		System.out.println("jms server start");
	}

	public static void main(String[] args) {
		try {
			JPoker24GameServer app = new JPoker24GameServer();

			//JPoker24GameServer app = new JPoker24GameServer(args[0]);

			System.setSecurityManager(new SecurityManager());

			Naming.rebind("Serverside", app);

			app.receivePlayer.start();
			System.out.println("Service registered");

		} catch (Exception e) {
			System.err.println("Exception thrown: " + e);
		}
	}
	

	/**
	 * Message queue.
	 * 
	 * @author Vivi
	 *
	 */
	class ReceivePlayer extends Thread {

		@Override
		public void run() {
			while (true) {
				JMSMessage playerinfo = null;
				try {
					playerinfo = jmsserver
							.receiveMessage(jmsserver.queueReader);
				} catch (JMSException e) {
					e.printStackTrace();
				}
				new Thread(new CheckPlayer(playerinfo)).start();
			}
		}
	}

	/**
	 * Find the room contains <user name>.
	 * 
	 * @param username
	 *            should be playing game.
	 * @return
	 */
	RoomManagement findRoomByUser(String username) {
		for (RoomManagement room : rooms) {
			if (room.playerlist.contains(username))
				return room;
		}
		return null;
	}

	void serverStartGame() {

		javax.jms.Message cards;
		try {
			for (int i = 0; i < playerinfos.size(); i++) {
				try {
					dbgameplayed(playerinfos.get(i).name);
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			GamePlayMsg gamePlayMsg = new GamePlayMsg(playerinfos);
			rooms.add(new RoomManagement(playerinfos, gamePlayMsg.num));
			playerinfos = new ArrayList<JMSMessage>();
			timeout = 0;
			cards = jmsserver.jmsHelper.createMessage(gamePlayMsg);
			System.out.println("JPokerServer: GamePlayed updated!");
			jmsserver.broadcastMessage(jmsserver.topicSender, cards);
			starttime = new Date().getTime();
			System.out
					.println("JPokerServer: Since Broadcast, palyer list cleared!");
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	class CheckPlayer implements Runnable {
		public JMSMessage info;

		CheckPlayer(JMSMessage info) {
			this.info = info;
		}

		@Override
		public void run() {

			if (findRoomByUser(info.name) != null) {
				System.out.println("user " + info.name
						+ " is already playing!!!! Reject join message.");
				return;
			}
			for (JMSMessage playerinfo : playerinfos) {
				if (playerinfo.name.equals(info.name)) {
					System.out.println("wtf...");
					return;
				}
			}

			PreparedStatement stmt;
			try {
				stmt = conn
						.prepareStatement("SELECT COUNT(*), gamePlayed, AVG(winTime) from userinfo, winhistory WHERE userinfo.userName = winhistory.userName AND userinfo.userName = ? GROUP BY userinfo.userName");
				stmt.setString(1, info.name);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					info.win = "" + rs.getInt(1);
					info.total = "" + rs.getInt(2);
					info.avg = "" + Math.round(rs.getDouble(3) * 100) / 100;
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			playerinfos.add(info);
			if (playerinfos.size() == 4) {
				System.out.println("four users come then broadcast");
				serverStartGame();
			}
			// one user
			else if (playerinfos.size() == 1) {
				System.out.println("one user");
				timeout = 0;
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (!playerinfos.contains(this.info))
					return;
				// if only one player and check after 10 seconds
				// if only one user, timeout
				if (playerinfos.size() == 1) {
					System.out.println("one user and time out");
					timeout = 1;
				} else {
					// else if more than 1 user, start the game
					serverStartGame();
				}
			} else {
				if (timeout == 1) {
					// if timeout already and only 1 player, start the game when
					// the second player come
					System.out
							.println("second user come and time out then broadcast");
					serverStartGame();
				}
			}
		}
	}

	@Override
	public synchronized String compute(String username, String answer)
			throws RemoteException {
		ScriptEngineManager mgr = new ScriptEngineManager();
		ScriptEngine engine = mgr.getEngineByName("JavaScript");
		int[] cardsval = findRoomByUser(username).cardsVal;
		int elem = 0;
		String s = "";
		Set<Integer> ints = new HashSet<Integer>();

		try {
			for (int i = 0; i < answer.length(); i++) {
				if (answer.charAt(i) == '(') {
					s = s + "(";
				} else if (answer.charAt(i) == ')') {
					s = s + ")";
				} else if (answer.charAt(i) == '+') {
					s = s + "+";
				} else if (answer.charAt(i) == '/') {
					s = s + "/";
				} else if (answer.charAt(i) == '-') {
					s = s + "-";
				} else if (answer.charAt(i) == '*') {
					s = s + "*";
				} else if (answer.charAt(i) == ' ') {
					s = s + " ";
				} else {
					String snum = "";
					if (i != answer.length() - 1 && answer.charAt(i + 1) != '('
							&& answer.charAt(i + 1) != ')'
							&& answer.charAt(i + 1) != '+'
							&& answer.charAt(i + 1) != '-'
							&& answer.charAt(i + 1) != '*'
							&& answer.charAt(i + 1) != '/'
							&& answer.charAt(i + 1) != ' ') {
						elem = Integer.parseInt(answer.substring(i, i + 2));
						if (elem != cardsval[0] && elem != cardsval[1]
								&& elem != cardsval[2] && elem != cardsval[3]
								&& ints.contains(elem)) {
							return "Given a wrong input numbers";
						} else {
							ints.add(elem);
							snum = snum + elem + ".0";
							i++;
						}
					} else {
						elem = Integer.parseInt(answer.substring(i, i + 1));
						if (elem != cardsval[0] && elem != cardsval[1]
								&& elem != cardsval[2] && elem != cardsval[3]
								&& ints.contains(elem)) {
							return "Given a wrong input numbers";
						} else {
							ints.add(elem);
							snum = snum + elem + ".0";
						}
					}
					s = s + snum;
				}
			}
		} catch (NumberFormatException e) {
			return "Invalid Input";
		}

		if (ints.size() != 4)
			return "Wrong number of numbers!";

		try {
			if ((double) engine.eval(s) == 24.0) {
				System.out.println(engine.eval(s));
				javax.jms.Message results = null;
				try {
					RoomManagement curroom = findRoomByUser(username);
					results = jmsserver.jmsHelper
							.createMessage(new GameoverMsg(username, answer,
									curroom.playerlist));
					System.out
							.println("JPokerServer: Game over, we clear the player list!");
					rooms.remove(curroom);
				} catch (JMSException e) {
					e.printStackTrace();
				}
				try {
					try {
						dbgameover(username);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					System.out.println("JPokerServer: Win history updated!");
					jmsserver.broadcastMessage(jmsserver.topicSender, results);
				} catch (JMSException e) {
					e.printStackTrace();
				}
				dbflag = 1;
				return "";
			} else {
				System.out.println(engine.eval(s));
				return "Wrong Answer!";
			}
		} catch (ScriptException e) {
			e.printStackTrace();
			return "Script Exception";
		}
	}

	public synchronized void dbgameover(String username)
			throws RemoteException, SQLException {
		// update win history table
		PreparedStatement stmtwin = conn
				.prepareStatement("INSERT INTO winhistory(userName, winTime) VALUES (?,?)");
		stmtwin.setString(1, username);
		stmtwin.setDouble(2, (new Date().getTime() - starttime) / 1000.0);
		stmtwin.execute();
	}

	public synchronized void dbgameplayed(String username)
			throws RemoteException, SQLException {
		PreparedStatement stmt1 = null;
		try {
			stmt1 = conn
					.prepareStatement("UPDATE userinfo SET gamePlayed=((SELECT gamePlayed FROM userinfo WHERE userName=?) + 1) WHERE userName=?");
			stmt1.setString(1, username);
			stmt1.setString(2, username);
			int rows = stmt1.executeUpdate();
			if (rows > 0) {
				System.out.println("GamePlayed of " + username + " updated");
			} else {
				System.err.println(username + " not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateplayers(String name) {
		RoomManagement curroom = findRoomByUser(name);
		if (curroom != null) {
			if (curroom.playerlist.size() > 0) {
				if (curroom.playerlist.remove(name)) {
					System.out.println("Players are "
							+ curroom.playerlist.toString() + "...");
					QuitMsg update = new QuitMsg(name, curroom.playerlist);
					javax.jms.Message results = null;
					try {
						results = jmsserver.jmsHelper.createMessage(update);
						jmsserver.broadcastMessage(jmsserver.topicSender,
								results);
					} catch (JMSException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/*************************** ASSIGNMENT 2 PART *****************************************************/
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
				// Random rand = new Random();
				// int n = rand.nextInt(10) + 1;
				stmt1.setInt(3, 0);
				stmt1.setLong(4, new Date().getTime());
				stmt1.execute();
				System.out.println("Record created in userinfo");
				// update win history table
				// int records = rand.nextInt(n) + 1;
				// PreparedStatement stmtwin = conn
				// .prepareStatement("INSERT INTO winhistory(userName, winTime) VALUES (?,?)");
				// stmtwin.setString(1, name);
				// for (int i = 0; i < records; i++) {
				// Random randreal = new Random();
				// double x = 2 + 13 * randreal.nextDouble();
				// stmtwin.setDouble(2, x);
				// stmtwin.execute();
				// }
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
					long cur = new Date().getTime();
					stmt1.setLong(1, cur);
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
		updateplayers(name);
		System.err.println("logout: updateplayers");
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
				arr[1] = String.valueOf(rs.getInt(3));// gameplayed
				arr[2] = String.valueOf(rs.getInt(4));// win
				arr[3] = String.format("%.1f", rs.getFloat(5));// avg
				arr[4] = String.valueOf(rs.getInt(6));// rank
			} else {
				System.err.println("no record for user " + name);
				return null;
			}
		} catch (SQLException e) {
			System.err.println("Error inserting record: " + e);
		}
		return arr;
	}

	public synchronized String[][] rankBoard(String name)
			throws RemoteException {
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

	public synchronized int startgame() {
		return jmsserver.flag;
	}

}