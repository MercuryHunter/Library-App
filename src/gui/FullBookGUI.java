package gui;

/**
 * The FullBookGUI allows for a window to be displayed with a book's properties
 * It allows for the editing of the boolean values attached to the book
 * as well as the deletion of books.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

// My other classes - this must be fixed, why's it all so entangled
import gui.*;
import main.*;
import util.*;

public class FullBookGUI extends JFrame implements ActionListener, ItemListener {
	
	private boolean DEBUG = true;
	private Book book;
	private JCheckBox read, owned, wantToRead;
	private Library library;
	private LibraryGUI libraryGUI;
	private BookGUI bookGUI;

	public FullBookGUI(Book book, Library library, LibraryGUI libraryGUI, BookGUI bookGUI) {
		setSize(400, 300);
		setLayout(new BorderLayout());
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		requestFocus();

		this.book = book;
		this.library = library;
		this.libraryGUI = libraryGUI;
		this.bookGUI = bookGUI;

		// Image
		JLabel image = new JLabel();
		ImageIcon icon;
		File imageFile = new File("images/" + book.getISBN() + ".jpg");
		boolean imageExists = imageFile.exists();
		if(imageExists) icon = new ImageIcon("images/" + book.getISBN() + ".jpg");
		else icon = new ImageIcon("images/default.jpg");
		image.setIcon(icon);
		add(image, BorderLayout.WEST);

		// Main Details Panel
		JPanel infoPanel = new JPanel(new GridLayout(3, 1));
		JLabel title = new JLabel(book.getName(), SwingConstants.CENTER);
		title.setFont(new Font("Serif", Font.BOLD, 32));
		JLabel author = new JLabel(book.getAuthor(), SwingConstants.CENTER);
		author.setFont(new Font("Serif", Font.BOLD, 24));
		infoPanel.add(title);
		infoPanel.add(author);

		// Sub boolean panel
		JPanel boolPanel = new JPanel(new GridLayout(3, 1));
		read = new JCheckBox("Read: ", book.getRead());
		read.setHorizontalTextPosition(SwingConstants.LEFT);
		read.setHorizontalAlignment(SwingConstants.CENTER);
		owned = new JCheckBox("Owned: ", book.getOwned());
		owned.setHorizontalTextPosition(SwingConstants.LEFT);
		owned.setHorizontalAlignment(SwingConstants.CENTER);
		wantToRead = new JCheckBox("Want to Read: ", book.getWantToRead());
		wantToRead.setHorizontalTextPosition(SwingConstants.LEFT);
		wantToRead.setHorizontalAlignment(SwingConstants.CENTER);
		read.addItemListener(this);
		owned.addItemListener(this);
		wantToRead.addItemListener(this);
		boolPanel.add(read);
		boolPanel.add(owned);
		boolPanel.add(wantToRead);
		infoPanel.add(boolPanel);

		add(infoPanel, BorderLayout.CENTER);

		// Buttons
		JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
		JButton delete = new JButton("Delete");
		JButton save = new JButton("Save");
		JButton back = new JButton("Back");
		delete.addActionListener(this);
		save.addActionListener(this);
		back.addActionListener(this);
		buttonPanel.add(delete);
		buttonPanel.add(save);
		buttonPanel.add(back);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("Delete")) {
			Library.filemanager.removeBookFiles(book);
			library.removeBook(bookGUI);
			// This is the only reason BookGUI, Library and this have libraryGUI...
			// Can't seem to get it to fix with focus changes though...
			libraryGUI.addBooksToPanel();
			this.dispose();
		}
		else if (command.equals("Save")) {
			Library.filemanager.editBookFile(book);
		}
		else if (command.equals("Back")) {
			this.dispose();
		}
	}

	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();
		boolean isSelected = e.getStateChange() == ItemEvent.SELECTED;
		if (source == read) {
			book.setRead(isSelected);
			if(DEBUG) System.out.println("Changing read to: " + isSelected);
		}
		else if (source == owned) {
			book.setOwned(isSelected);
			if(DEBUG) System.out.println("Changing owned to: " + isSelected);
		}
		else if (source == wantToRead) {
			book.setWantToRead(isSelected);
			if(DEBUG) System.out.println("Changing wantToRead to: " + isSelected);
		}
	}
}