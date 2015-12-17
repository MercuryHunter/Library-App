import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LibraryGUI extends JFrame implements ActionListener {
	
	private Library library;
	private JPanel pnlBook;

	public LibraryGUI(){
		// The only constructor that will be used
		setSize(600, 400);
		setLayout(new BorderLayout());
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Panel for the buttons at top Series, Author, Name, Search Box
		JPanel pnlButtons = new JPanel();
		pnlButtons.setLayout(new GridLayout(1, 4)); //RxC
		// TODO: Make custom button with different look? Allow for up/down sorting?
		JButton btnSeries = new JButton("Series");
		JButton btnAuthor = new JButton("Author");
		JButton btnName = new JButton("Name");
		// TODO: Add transparent text that disappears
		JTextField txtSearch = new JTextField();
		txtSearch.setActionCommand("Search");

		// Add to Panel
		pnlButtons.add(btnSeries);
		pnlButtons.add(btnAuthor);
		pnlButtons.add(btnName);
		pnlButtons.add(txtSearch);

		// Add ActionListener to the buttons and search field.
		btnSeries.addActionListener(this);
		btnAuthor.addActionListener(this);
		btnName.addActionListener(this);
		txtSearch.addActionListener(this);

		add(pnlButtons, BorderLayout.NORTH);

		library = new Library();
	}

	// ActionListener method, use the class to handle action events, instead
	public void actionPerformed(ActionEvent e){
		String command = e.getActionCommand();
		if (command.equals("Series")){

		}
		else if (command.equals("Author")){

		}
		else if (command.equals("Name")){

		}
		else if (command.equals("Search")){

		}
		else{
			System.err.println("What did you just doooo...");
			// TODO: Throw error?
		}
	}

	public static void main(String[] args){
		LibraryGUI gui = new LibraryGUI();
		gui.setVisible(true);
	}
}