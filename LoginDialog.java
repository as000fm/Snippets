package snippets;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
public class LoginDialog extends JDialog {
	private JTextField usernameField;

	private JPasswordField passwordField;

	private boolean isLoginSuccessful;

	public LoginDialog() {
		super((JDialog) null, "Login", true);
		setResizable(false);

		usernameField = new JTextField(20);
		passwordField = new JPasswordField(20);
		isLoginSuccessful = false;

		JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
		panel.add(new JLabel("Username:"));
		panel.add(usernameField);
		panel.add(new JLabel("Password:"));
		panel.add(passwordField);

		JButton loginButton = new JButton("Login");
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				isLoginSuccessful = true;
				dispose();
			}
		});

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(loginButton);
		buttonPanel.add(cancelButton);

		getContentPane().setLayout(new GridLayout(2, 1));
		getContentPane().add(panel);
		getContentPane().add(buttonPanel);

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public String getUsername() {
		return usernameField.getText();
	}

	public String getPassword() {
		return new String(passwordField.getPassword());
	}

	public boolean isLoginSuccessful() {
		return isLoginSuccessful;
	}

	public static void main(String[] args) {
		LoginDialog loginDialog = new LoginDialog();

		if (loginDialog.isLoginSuccessful()) {
			String username = loginDialog.getUsername();
			String password = loginDialog.getPassword();

			System.out.println("Username: " + username);
			System.out.println("Password: " + password);
		}
	}
}
