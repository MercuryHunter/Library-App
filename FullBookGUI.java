/**
 * The FullBookGUI allows for a window to be displayed with a book's properties
 * It allows for the editing of the boolean values attached to the book
 * as well as the deletion of books.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class FullBookGUI extends JFrame implements ActionListener, ItemListener {
	
	private boolean DEBUG = LibraryGUI.DEBUG;
	private Book book;
	private JCheckBox read, owned, wantToRead;

	public FullBookGUI(Book book) {
		setSize(400, 300);
		setLayout(new BorderLayout());
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.book = book;

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
		JLabel title = new JLabel(book.getName());
		JLabel author = new JLabel(book.getAuthor());
		infoPanel.add(title);
		infoPanel.add(author);

		// Sub boolean panel
		JPanel boolPanel = new JPanel(new GridLayout(1, 3));
		read = new JCheckBox("Read: ", book.getRead());
		owned = new JCheckBox("Owned: ", book.getOwned());
		wantToRead = new JCheckBox("Want to Read: ", book.getWantToRead());
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