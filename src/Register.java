import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.border.TitledBorder;

public class Register extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6050814736352810690L;
	JTextField tf1 = new JTextField();
	JPasswordField tf2 = new JPasswordField(10);
	JPasswordField tf3 = new JPasswordField(10);
	JFrame frame = new JFrame();
	JButton reg = new JButton("Register");
	JButton cancel = new JButton("Cancel");
	JLabel login = new JLabel("Login Name");
	JLabel password = new JLabel("Password");
	JLabel confirm = new JLabel("Confirm Password");
	JPoker24Game user;
	Register registerwindow;
	Login loginwindow;

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == cancel) {
			user.openlogin();
		} else if (arg0.getSource() == reg) {
			char[] pass2 = tf2.getPassword();
			String passString2 = new String(pass2);
			char[] pass3 = tf3.getPassword();
			String passString3 = new String(pass3);
			try {
				user.updateregister(tf1.getText(), passString2, passString3);
			} catch (InstantiationException | IllegalAccessException
					| ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public Register(JPoker24Game user) {

		this.user = user;

		frame.setTitle("Register");
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(reg);
		buttonPanel.add(cancel);
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
		Panel1.setLayout(new GridLayout(6, 1));
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
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
		c.gridx = 0;
		c.gridy = 4;
		Panel1.add(confirm, c);
		c.gridx = 0;
		c.gridy = 5;
		Panel1.add(tf3, c);
		cancel.addActionListener(this);
		reg.addActionListener(this);

		frame.setSize(300, 300);
		frame.setVisible(false);
	}
}