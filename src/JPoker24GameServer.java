import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.rmi.*;
import java.rmi.server.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class JPoker24GameServer extends UnicastRemoteObject implements
		Remoteinterface {

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

	public JPoker24GameServer() throws RemoteException {
	}

	public synchronized int registerservice(String name, String password,
			String password2) throws RemoteException {
		try {
			File f = null;
			f = new File("UserInfo.txt");
			f.createNewFile();

			BufferedReader br = new BufferedReader(new FileReader(
					"UserInfo.txt"));
			String thisline;
			while ((thisline = br.readLine()) != null) {
				String[] parts = thisline.split(" ");
				String typename = parts[0];
				if (typename.equals(name)) {
					JOptionPane.showMessageDialog(new JFrame(),
							"User name is oppucied, please use another one",
							"Duplicated Name", JOptionPane.ERROR_MESSAGE);
					return -1;
				}
			}

			FileWriter fstream = new FileWriter("UserInfo.txt", true);
			BufferedWriter fbw = new BufferedWriter(fstream);
			fbw.write(name + " " + password + "\n");
			fbw.close();

			File onlinef = null;
			onlinef = new File("OnlineUser.txt");
			onlinef.createNewFile();

			FileWriter fstream1 = new FileWriter("OnlineUser.txt", true);
			BufferedWriter fbw1 = new BufferedWriter(fstream1);
			fbw1.write(name + " " + password + "\n");
			fbw1.close();

		} catch (IOException e) {
			System.out.println("File I/O error!");
		}
		return 0;
	}

	public synchronized int loginservice(String name, String password)
			throws RemoteException {
		try {
			String thisline;
			String checkname = null;
			String checkpassword = null;
			File onlinef = null;
			onlinef = new File("OnlineUser.txt");
			onlinef.createNewFile();
			BufferedReader logged = new BufferedReader(new FileReader(
					"OnlineUser.txt"));
			// check whether the user has already logged in
			while ((thisline = logged.readLine()) != null) {
				String[] parts = thisline.split(" ");
				checkname = parts[0];
				checkpassword = parts[1];
				if (checkname.equals(name) && checkpassword.equals(password)) {
					JOptionPane.showMessageDialog(new JFrame(),
							"The user has already loggin in the game",
							"Already logged in", JOptionPane.ERROR_MESSAGE);
					return -1;
				}
			}
			// check whether the use has registered
			File f = null;
			f = new File("UserInfo.txt");
			f.createNewFile();
			BufferedReader br = new BufferedReader(new FileReader(
					"UserInfo.txt"));
			int status = 0;

			while ((thisline = br.readLine()) != null) {
				String[] parts1 = thisline.split(" ");
				checkname = parts1[0];
				checkpassword = parts1[1];
				if (checkname.equals(name)) {
					if (checkpassword.equals(password)) {
						status = 1;// the user information match, can log in
					} else {
						// the user information not matching, cannot log in
						status = 1;
						JOptionPane.showMessageDialog(new JFrame(),
								"Invalid password",
								"Invalid  login name/password",
								JOptionPane.ERROR_MESSAGE);
						return -1;
					}
				}
			}

			if (status == 0) {// means the user have not register
				JOptionPane.showMessageDialog(new JFrame(),
						"Invalid login name, please go to register first",
						"Invalid  login name/password",
						JOptionPane.ERROR_MESSAGE);
				return -1;
			}

			FileWriter fstream1 = new FileWriter("OnlineUser.txt", true);
			BufferedWriter fbw1 = new BufferedWriter(fstream1);
			fbw1.write(name + " " + password + "\n");
			fbw1.close();
		} catch (IOException e) {
			System.out.println("File I/O error!");
		}
		return 0;
	}

	public synchronized int logoutservice(String name) throws RemoteException {
		try {
			String thisline;
			List<String> lst = new ArrayList<String>();
			Writer writer = null;
			File onlinef = null;
			onlinef = new File("OnlineUser.txt");
			onlinef.createNewFile();
			BufferedReader logged = new BufferedReader(new FileReader(
					"OnlineUser.txt"));
			while ((thisline = logged.readLine()) != null) {
				String[] parts = thisline.split(" ");
				String checkname = parts[0];
				if (checkname.equals(name)) {
					continue;
				} else {
					lst.add(thisline);
				}
			}
			logged.close();

			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("OnlineUser.txt"), "utf-8"));
			for (int i = 0; i < lst.size(); i++) {
				writer.write(lst.get(i) + "\n");
			}
			writer.close();
		} catch (IOException e) {
			System.out.println("File I/O error!");
		}
		return 0;
	}
}