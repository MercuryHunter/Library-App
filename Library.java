import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.HashMap;

public class Library {
	private boolean DEBUG = true;
	private ArrayList<Book> library;
	public static FileManager filemanager = new FileManager("masterlist.txt");

	// Initialise your Library
	public Library() {
		library = filemanager.readInBooks();

		if(DEBUG) for(Book book : library) System.out.println(book);
	}

	public void addBook(Book newBook) { library.add(newBook); }

	public ArrayList<Book> getLibrary() { return library; }

	public void sortByName() { Collections.sort(library, Book.nameComparator); }

	public void sortByAuthor() { Collections.sort(library, Book.authorComparator); }

	public void sortBySeries() { Collections.sort(library, Book.seriesComparator); }

	// TODO
	public void checkUpSeries() {
		// Query the API and get new books
	}

	//Series, set of books
	public HashMap<String, ArrayList<Book>> getSeries(){
		// Returns the books in their sets of series.
		return null;
	}
}