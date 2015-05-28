import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.border.TitledBorder;

public class Login extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2866021079198873989L;
	JButton loginbtn = new JButton("Login");
	JButton regbtn = new JButton("Register");
	JTextField tf1 = new JTextField();
	JLabel login = new JLabel("Login Name");
	JLabel password = new JLabel("Password");
	JFrame frame = new JFrame();
	JPasswordField tf2 = new JPasswordField(10);
	JPoker24Game user;

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == regbtn) {
			user.openreg();
		} else if (arg0.getSource() == loginbtn) {
			char[] pass2 = tf2.getPassword();
			String passString2 = new String(pass2);
			try {
				user.updatelogin(tf1.getText(), passString2);
			} catch (InstantiationException | IllegalAccessException
					| ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public Login(JPoker24Game user) {

		this.user = user;

		frame.setTitle("Login");
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(loginbtn);
		buttonPanel.add(regbtn);
		frame.getContentPane().add(BorderLayout.SOUTH, buttonPanel);

		JPanel Panel2 = new JPanel();
		frame.getContentPane().add(BorderLayout.NORTH, Panel2);
		JPanel Panel3 = new JPanel();
		frame.getContentPane().add(BorderLayout.WEST, Panel3);
		JPanel Panel4 = new JPanel();
		frame.getContentPane().add(BorderLayout.EAST, Panel4);
		JPanel Panel1 = new JPanel();
		frame.getContentPane().add(BorderLayout.CENTER, Panel1);
		TitledBorder title;
		title = BorderFactory.createTitledBorder("Register");
		Panel1.setBorder(title);
		Panel1.setLayout(new GridLayout(5, 1));
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 0, 0);
		Panel1.add(login, c);
		c.gridx = 0;
		c.gridy = 1;
		Panel1.add(tf1, c);
		c.gridx = 0;
		c.gridy = 2;
		Panel1.add(password, c);
		c.gridx = 0;
		c.gridy = 3;
		Panel1.add(tf2, c);
		regbtn.addActionListener(this);
		loginbtn.addActionListener(this);

		frame.setSize(300, 300);
		frame.setVisible(true);
	}
}