/**
 * A BookGUI is a wrapper for the Book that allows it to be shown on a GUI through a label.
 * Sets up all the basics.
 */

import javax.swing.*;
import java.awt.*;

public class BookGUI extends JLabel {
	
	private Book book;

	// The constructor that does the setting up
	public BookGUI(Book b){
		this.book = b;
		setPreferredSize(new Dimension(100, 155));
		// TODO: If icon doesn't exist, use a default.
		setIcon(new ImageIcon("images/" + book.getISBN() + ".jpg"));
		setToolTipText(book.getName());
	}
}