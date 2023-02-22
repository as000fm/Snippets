package snippets;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class OptionPaneOptions {
	private JPanel options;

	private Object[] items;

	public OptionPaneOptions() {
		options = new JPanel(new GridLayout(0, 3));
		options.add(createOptionPane("Plain Message", JOptionPane.PLAIN_MESSAGE));
		options.add(createOptionPane("Error Message", JOptionPane.ERROR_MESSAGE));
		options.add(createOptionPane("Information Message", JOptionPane.INFORMATION_MESSAGE));
		options.add(createOptionPane("Warning Message", JOptionPane.WARNING_MESSAGE));
		options.add(createOptionPane("Want to do something?", JOptionPane.QUESTION_MESSAGE));
		items = new Object[] { "First", "Second", "Third" };
		
		JComboBox<?> choiceCombo = new JComboBox<Object>(items);
		
		options.add(titled(new JOptionPane(choiceCombo, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION), "Question Message"));
		JFrame frame = new JFrame("JOptionPane'Options");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(options, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
	}

	static JOptionPane createOptionPane(String message, int type) {
		JOptionPane pane = new JOptionPane(message, type);
		String title = message;
		if (type == JOptionPane.QUESTION_MESSAGE) {
			title = "Question Message";
			pane.setOptionType(JOptionPane.YES_NO_CANCEL_OPTION);
		}
		return titled(pane, title);
	}

	static <T extends JComponent> T titled(T c, String title) {
		c.setBorder(BorderFactory.createTitledBorder(title));
		return c;
	}

	public static void main(String[] args) {
		OptionPaneOptions test = new OptionPaneOptions();
	}

}
