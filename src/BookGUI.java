/**
 * A BookGUI is a wrapper for the Book that allows it to be shown on a GUI through a label.
 * Sets up all the basics.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Comparator;

public class BookGUI extends JButton implements ActionListener {

	private boolean DEBUG = LibraryGUI.DEBUG;
	private Book book;
	private Library library;
	private LibraryGUI libraryGUI;

	// The constructor that does the setting up
	public BookGUI(Book book, Library library, LibraryGUI libraryGUI){
		this.book = book;
		this.library = library;
		this.libraryGUI = libraryGUI;

		setPreferredSize(new Dimension(LibraryGUI.preferredWidth, LibraryGUI.preferredHeight));

		// Checks if the image exists, and tries to download it if it doesn't.
		// If it fails, uses a default image.
		File image = new File("images/" + book.getISBN() + ".jpg");
		boolean imageExists = image.exists();
		if(!imageExists){
			try {
				Library.downloadImage(book);
				imageExists = true;
			}
			catch(Exception x){
				if(DEBUG) System.err.println("Image not downloaded for book: " + book.getISBN());
			}
		}
		if(imageExists) setIcon(new ImageIcon("images/" + book.getISBN() + ".jpg"));
		else setIcon(new ImageIcon("images/default.jpg"));

		setToolTipText(book.getName());
		addActionListener(this);
	}

	public Book getBook () { return book; };

	public boolean equals (Book book) { return this.book.equals(book); }

	// Comparator for sorting by book name
	public static Comparator<BookGUI> nameComparator = new Comparator<BookGUI>(){
		public int compare(BookGUI one, BookGUI two){
			return Book.nameComparator.compare(one.book, two.book);
		}
	};

	// Comparator for sorting by author 
	public static Comparator<BookGUI> authorComparator = new Comparator<BookGUI>(){
		public int compare(BookGUI one, BookGUI two){
			return Book.authorComparator.compare(one.book, two.book);
		}
	};

	// Comparator for sorting by series
	public static Comparator<BookGUI> seriesComparator = new Comparator<BookGUI>(){
		public int compare(BookGUI one, BookGUI two){
			return Book.seriesComparator.compare(one.book, two.book);
		}
	};

	public void actionPerformed(ActionEvent e) {
		new FullBookGUI(book, library, libraryGUI, this).setVisible(true);
	}
}