/**
 * A BookGUI is a wrapper for the Book that allows it to be shown on a GUI through a label.
 * Sets up all the basics.
 */

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class BookGUI extends JLabel {
	
	private boolean DEBUG = LibraryGUI.DEBUG;
	private Book book;

	// The constructor that does the setting up
	public BookGUI(Book b){
		this.book = b;

		setPreferredSize(new Dimension(100, 155));

		// Checks if the image exists, and tries to download it if it doesn't.
		// If it fails, uses a default image.
		File image = new File("images/" + book.getISBN() + ".jpg");
		boolean imageExists = image.exists();
		if(!imageExists){
			try {
				Library.downloadImage(book);
				imageExists = true;
			}
			catch(Exception e){
				if(DEBUG) System.err.println("Image not downloaded for book: " + book.getISBN());
			}
		}
		if(imageExists) setIcon(new ImageIcon("images/" + book.getISBN() + ".jpg"));
		else setIcon(new ImageIcon("images/default.jpg"));

		setToolTipText(book.getName());
	}
}