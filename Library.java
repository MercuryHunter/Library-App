import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.Iterator;
import java.util.HashMap;

import java.io.File;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Library {
	private boolean DEBUG = LibraryGUI.DEBUG;
	private ArrayList<Book> library;
	public static FileManager filemanager = new FileManager("masterlist.txt");

	// Initialise your Library
	public Library() {
		library = filemanager.readInBooks();

		if(DEBUG) for(Book book : library) System.out.println(book);
	}

	public void addBook(Book newBook) { if(!containsBook(newBook)) library.add(newBook); }

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

	public boolean containsBook(Book book){
		String isbn = book.getISBN();
		Iterator<Book> it = library.iterator();
		while(it.hasNext()){
			if(it.next().getISBN().equals(isbn)) return true;
		}
		return false;
	}

	public void downloadImages() throws IOException {
		for(Book book : library){
			String filename = "images/" + book.getISBN() + ".jpg";
			File image = new File(filename);
			if(!image.exists()){
				URL link = new URL(book.getImage());
				InputStream in = new BufferedInputStream(link.openStream());
				ByteArrayOutputStream out = new ByteArrayOutputStream();

				byte[] buffer = new byte[1024];
				int n = 0;
				while(-1!=(n=in.read(buffer)))
					out.write(buffer, 0, n);
				
				out.close();
				in.close();
				byte[] response = out.toByteArray();

				//image.createNewFile();
				FileOutputStream fos = new FileOutputStream(image);
				fos.write(response);
				fos.close();
			}
		}
	}
}