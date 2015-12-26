/**
 * The LibraryGUI is the main GUI of the application, allowing a user to view
 * all the books in the library, see them in different sorted orders,
 * add new books and interact with them.
 */

// Swing elements, action listeners, exceptions
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class LibraryGUI extends JFrame implements ActionListener {
	
	// The main DEBUG variable applied to all the classes
	public static final boolean DEBUG = true;
	private static final long serialVersionUID = 1L;

	private Library library;
	private JScrollPane scrollPane;
	private JPanel pnlBook;
	HintTextField txtSearch;

	// Sets up the Main GUI for viewing
	public LibraryGUI(){
		// The only constructor that will be used
		setSize(600, 400);
		setLayout(new BorderLayout());
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ToolTipManager.sharedInstance().setInitialDelay(200);

		// Panel for the buttons at top Series, Author, Name, Search Box
		JPanel pnlButtons = new JPanel();
		pnlButtons.setLayout(new GridLayout(1, 5)); //RxC
		// TODO: Make custom button with different look? Allow for up/down sorting?
		JButton btnAdd = new JButton("Add");
		JButton btnSeries = new JButton("Series");
		JButton btnAuthor = new JButton("Author");
		JButton btnName = new JButton("Name");
		// TODO: Add transparent text that disappears
		// TODO: Letter by letter searching and removing.
		txtSearch = new HintTextField("Search");
		txtSearch.setActionCommand("Search");

		// Add to Panel
		pnlButtons.add(btnAdd);
		pnlButtons.add(btnSeries);
		pnlButtons.add(btnAuthor);
		pnlButtons.add(btnName);
		pnlButtons.add(txtSearch);

		// Add ActionListener to the buttons and search field.
		btnAdd.addActionListener(this);
		btnSeries.addActionListener(this);
		btnAuthor.addActionListener(this);
		btnName.addActionListener(this);
		txtSearch.addActionListener(this);

		add(pnlButtons, BorderLayout.NORTH);

		// Initialise the library
		library = new Library();
		try {
			library.downloadImages();
			//library.checkUpAllSeries();
		}
		catch (IOException x){
			System.err.println("An error occurred while downloading one or more image files.");
			if(DEBUG) x.printStackTrace();
		}
		catch (NullPointerException x){
			System.err.println("Error in reading in new series - please check your internet connection.");
			if(DEBUG) x.printStackTrace();
		}
		library.sortBySeries();

		pnlBook = new JPanel();
		pnlBook.setSize(375, 600);
		scrollPane = new JScrollPane(pnlBook, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane, BorderLayout.CENTER);
		addBooksToPanel();
	}


	// The method to refresh the panel by adding all the books to it by making them BookGUIs
	private void addBooksToPanel () {
		addBooksToPanel(library.getLibrary());
	}

	private void addBooksToPanel (ArrayList<BookGUI> lib) {
		pnlBook.removeAll();

		pnlBook.setLayout(new GridLayout(lib.size()/6 + 1, 6, 0, 0));
		for(BookGUI book : lib){
			pnlBook.add(book);
		}

		pnlBook.revalidate();
		pnlBook.repaint();
		// Do this so that the screen refreshes
		scrollPane.revalidate();
		scrollPane.repaint();
	}

	// ActionListener method, use the class to handle action events
	public void actionPerformed(ActionEvent e){
		String command = e.getActionCommand();
		// Add a book with the given ISBN, should it exist
		if (command.equals("Add")){
			String reply = JOptionPane.showInputDialog(null, "Enter the input here:\n(ISBN: or Nothing)");
			if(reply != null && reply.length() > 0){
				boolean isISBN = false;
				if(reply.contains("ISBN:")){
					reply = reply.replaceAll("[^0-9A-Za-z]", "");
					reply = reply.replace("ISBN:", "");
					isISBN = true;
				}
				try{
					Book temporary = new Book(reply, isISBN);
					library.addBook(temporary);
					addBooksToPanel();
				}
				catch(Exception exception){
					JOptionPane.showMessageDialog(null, "Book not found.");
				}
			}
		}
		else if (command.equals("Series")){
			library.sortBySeries();
			addBooksToPanel();
		}
		else if (command.equals("Author")){
			library.sortByAuthor();
			addBooksToPanel();
		}
		else if (command.equals("Name")){
			library.sortByName();
			addBooksToPanel();
		}
		else if (command.equals("Search")){
			addBooksToPanel(library.search(txtSearch.getText()));
		}
		else{
			System.err.println("What did you just doooo...");
			System.exit(0);
		}
	}

	// Main method that just starts a dynamic instance of this class
	public static void main(String[] args){
		LibraryGUI gui = new LibraryGUI();
		gui.setVisible(true);
	}
}

class HintTextField extends JTextField implements FocusListener {

	private final String hint;
	private boolean showingHint;

	public HintTextField(final String hint) {
		super(hint);
		this.hint = hint;
		this.showingHint = true;
		super.addFocusListener(this);
	}	

	@Override
	public void focusGained(FocusEvent e) {
		if(this.getText().isEmpty()) {
			super.setText("");
			showingHint = false;
		}
	}
	@Override
	public void focusLost(FocusEvent e) {
		if(this.getText().isEmpty()) {
			super.setText(hint);
			showingHint = true;
		}
	}

	@Override
	public String getText() {
		return showingHint ? "" : super.getText();
	}
}