import javax.swing.*;
import java.awt.*;

public class BookGUI extends JLabel {
	
	private Book book;

	public BookGUI(Book b){
		this.book = b;
		setPreferredSize(new Dimension(100, 155));
		setIcon(new ImageIcon("images/" + book.getISBN() + ".jpg"));
		setToolTipText(book.getName());
	}
}