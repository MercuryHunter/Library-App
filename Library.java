/**
 * A library is a sorted set of books, that can be sorted in different ways.
 * It handles downloading of images of the books as well.
 */

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.Iterator;
//import java.util.HashMap;

// Downloading Images
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

	// Initialise the Library
	public Library() {
		library = filemanager.readInBooks();
		if(DEBUG) for(Book book : library) System.out.println(book);
	}

	public void addBook(Book newBook) throws IOException { 
		if(!containsBook(newBook)) library.add(newBook); 
		downloadImage(newBook);
	}

	// Gets the library in its currently sorted order
	public ArrayList<Book> getLibrary() { return library; }

	// Gets the size of the library
	public int size() { return library.size(); }

	// Implements sorting by Name using the comparator specificed in the book class
	public void sortByName() { Collections.sort(library, Book.nameComparator); }

	// Implements sorting by Author using the comparator specificed in the book class
	public void sortByAuthor() { Collections.sort(library, Book.authorComparator); }

	// Implements sorting by Series using the comparator specificed in the book class
	public void sortBySeries() { Collections.sort(library, Book.seriesComparator); }

	// TODO
	public void checkUpSeries() {
		// Query the API and get new books
	}

	/*
	// TODO: SeriesGUI maybe?
	public HashMap<String, ArrayList<Book>> getSeries(){
		// Returns the books in their sets of series.
		return null;
	}
	*/

	// Checks if we have a book by checking its ISBN versus every book we have
	public boolean containsBook(Book book) {
		String isbn = book.getISBN();
		Iterator<Book> it = library.iterator();
		while(it.hasNext()){
			if(it.next().getISBN().equals(isbn)) return true;
		}
		return false;
	}

	// A method that tries to download images for all the books
	public void downloadImages() throws IOException {
		for(Book book : library){
			downloadImage(book);
		}
	}

	// Downloads a books image file using their image url
	public void downloadImage(Book book) throws IOException {
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