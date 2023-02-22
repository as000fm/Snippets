package snippets;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Login {

	public static void main(String[] args) {
		// Create two text fields for username and password
		JTextField username = new JTextField();
		JTextField password = new JPasswordField();
		// Create two labels for username and password
		Object[] message = { "Username:", username, "Password:", password };
		// Create an option pane with OK and Cancel buttons
		int option = JOptionPane.showConfirmDialog(null, message, "Login", JOptionPane.OK_CANCEL_OPTION);
		// Check if OK button is clicked
		if (option == JOptionPane.OK_OPTION) {
			// Get the input values from text fields
			String user = username.getText();
			String pass = password.getText();
			// Verify the user credentials against a predefined username and password
			if (user.equals("stackoverflow") && pass.equals("stackoverflow")) {
				// Login successful, do something here
				System.out.println("Login successful!");
			} else {
				// Login failed, show an error message here
				System.out.println("Login failed!");
			}
		} else {
			// Cancel button is clicked, do something here
			System.out.println("Login canceled!");
		}
	}

}
