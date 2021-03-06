package gui;

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
import java.util.TreeSet;

// My other classes - this must be fixed, why's it all so entangled
import gui.*;
import main.*;
import util.*;

public class LibraryGUI extends JFrame implements ActionListener {
	
	public static final boolean DEBUG = true;
	private static final long serialVersionUID = 1L;

	// Size of window, and from them, preferred sizes of books;
	public static final int width = 630, height = 600;
	public static final int numBooksInRow = 6;
	public static final int preferredWidth = (width - 30)/numBooksInRow, preferredHeight = 155;

	private Library library;
	private JScrollPane scrollPane;
	private JPanel pnlBook;
	HintTextField txtSearch;

	// Sets up the Main GUI for viewing
	public LibraryGUI(){
		// The only constructor that will be used
		setSize(width, height);
		setLayout(new BorderLayout());
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ToolTipManager.sharedInstance().setInitialDelay(200);

		JMenuBar menuBar = new JMenuBar();
		JMenu editMenu = new JMenu("Edit");
		JMenu sortMenu = new JMenu("Sort");
		JMenu findMenu = new JMenu("Find");
		menuBar.add(editMenu);
		menuBar.add(sortMenu);
		menuBar.add(findMenu);

		setJMenuBar(menuBar);

		// TODO: Make custom button with different look? Allow for up/down sorting?
		JMenuItem add = new JMenuItem("Add");
		JMenuItem remove = new JMenuItem("Remove");
		editMenu.add(add);
		editMenu.add(remove);

		JMenuItem series = new JMenuItem("Series");
		JMenuItem author = new JMenuItem("Author");
		JMenuItem name = new JMenuItem("Name");
		sortMenu.add(series);
		sortMenu.add(author);
		sortMenu.add(name);

		JMenuItem findSeries = new JMenuItem("New Books By Series");
		JMenuItem findAuthor = new JMenuItem("New Books By Author");
		findMenu.add(findSeries);
		findMenu.add(findAuthor);
		findSeries.setActionCommand("Find Series");
		findAuthor.setActionCommand("Find Author");

		// Add ActionListener to the menu items
		add.addActionListener(this);
		series.addActionListener(this);
		author.addActionListener(this);
		name.addActionListener(this);
		findSeries.addActionListener(this);
		findAuthor.addActionListener(this);

		// Search Text Field
		// TODO: Letter by letter searching and removing.
		txtSearch = new HintTextField("Search");
		txtSearch.setActionCommand("Search");
		txtSearch.addActionListener(this);
		add(txtSearch, BorderLayout.NORTH);

		// Initialise the library
		library = new Library(this);
		try {
			library.downloadImages();
		}
		catch (IOException x){
			System.err.println("An error occurred while downloading one or more image files.");
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
	public void addBooksToPanel () {
		addBooksToPanel(library.getLibrary());
	}

	private void addBooksToPanel (TreeSet<BookGUI> lib) {
		pnlBook.removeAll();

		pnlBook.setLayout(new GridLayout(lib.size()/numBooksInRow + 1, numBooksInRow, 0, 0));
		for(BookGUI book : lib) {
			/*
			if(book.getActionListeners.length == 0)
				book.addActionListener(new ActionListener() {

			    @Override
			    public void actionPerformed(ActionEvent e) {
			    	new FullBookGUI(book, library, libraryGUI, this).setVisible(true);
			    }

			});
			*/
			pnlBook.add(book);
		}

		pnlBook.revalidate();
		pnlBook.repaint();

		// Do this so that the screen refreshes
		scrollPane.revalidate();
		scrollPane.repaint();
	}

	// ActionListener method, use the class to handle action events
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		// Add a book with the given ISBN, should it exist
		if (command.equals("Add")) {
			String reply = JOptionPane.showInputDialog(null, "Enter the input here:\n(ISBN: or Nothing)");
			if(reply != null && reply.length() > 0){
				boolean isISBN = false;
				if(reply.contains("ISBN:")){
					reply = reply.replace("ISBN:", "");
					reply = reply.replaceAll("[^0-9A-Za-z]", "");
					isISBN = true;
				}
				if(DEBUG) System.out.println("Trying to query: \"" + reply + "\"");
				try {
					Book temporary = new Book(reply, isISBN);
					library.addBook(temporary);
					addBooksToPanel();
				}
				catch (NullPointerException x) {
					if(DEBUG) x.printStackTrace();
					JOptionPane.showMessageDialog(null, "Book not found, or book was deleted.");
				}
				catch (Exception x) {
					if(DEBUG) x.printStackTrace();
					JOptionPane.showMessageDialog(null, "An unknown error occured.");
				}
			}
		}
		else if (command.equals("Series")) {
			library.sortBySeries();
			addBooksToPanel();
		}
		else if (command.equals("Author")) {
			library.sortByAuthor();
			addBooksToPanel();
		}
		else if (command.equals("Name")) {
			library.sortByName();
			addBooksToPanel();
		}
		else if (command.equals("Search")) {
			addBooksToPanel(library.search(txtSearch.getText()));
		}
		// TODO: Functionality
		else if (command.equals("Find Series")) {
			try {
				library.checkUpAllSeries();
				addBooksToPanel();
			}
			catch (NullPointerException x) {
				System.err.println("Error in reading in new series - please check your internet connection.");
				if(DEBUG) x.printStackTrace();
			}
		}
		// TODO: Functionality
		else if (command.equals("Find Author")) {

		}
		else {
			System.err.println("What did you just doooo...");
			System.exit(0);
		}
	}

	/* implements WindowFocusListener
	// Refresh on Focus Changes
	@Override
	public void windowGainedFocus(WindowEvent e) {
		System.out.println("Gained Focus");
		addBooksToPanel();
	}
	
	@Override
	public void windowLostFocus(WindowEvent e) {
		addBooksToPanel();
	}
	*/

	// Main method that just starts a dynamic instance of this class
	public static void main(String[] args) {
		LibraryGUI gui = new LibraryGUI();
		gui.setVisible(true);
	}
}