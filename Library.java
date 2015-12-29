/**
 * A library is a sorted set of books, that can be sorted in different ways.
 * It handles downloading of images of the books as well.
 */

import java.util.TreeSet;
import java.util.HashSet;
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

// For XML
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class Library {

	private boolean DEBUG = LibraryGUI.DEBUG;

	//private ArrayList<Book> library;
	private TreeSet<BookGUI> library;
	private static Comparator<BookGUI> currentComparator = BookGUI.seriesComparator;
	public static FileManager filemanager = new FileManager("masterlist.txt");

	// Initialise the Library
	public Library() {
		library = filemanager.readInBooks();
		if(DEBUG) { for(BookGUI book : library) System.out.println(book.getBook()); }
	}

	public void addBook(Book book) { 
		if(!containsBook(book)) library.add(new BookGUI(book)); 
	}

	// Gets the library in its currently sorted order
	public TreeSet<BookGUI> getLibrary() { return library; }

	// Gets the current comparator
	public static Comparator<BookGUI> getCurrentComparator() { return currentComparator; }

	// Gets the size of the library
	public int size() { return library.size(); }

	// Implements sorting by Name using the comparator specificed in the book class
	public void sortByName() {
		TreeSet<BookGUI> temp = new TreeSet<BookGUI>(BookGUI.nameComparator);
		currentComparator = BookGUI.nameComparator;
		temp.addAll(library);
		library = temp;
	}

	// Implements sorting by Author using the comparator specificed in the book class
	public void sortByAuthor() {
		TreeSet<BookGUI> temp = new TreeSet<BookGUI>(BookGUI.authorComparator);
		currentComparator = BookGUI.authorComparator;
		temp.addAll(library);
		library = temp;
	}

	// Implements sorting by Series using the comparator specificed in the book class
	public void sortBySeries() {
		TreeSet<BookGUI> temp = new TreeSet<BookGUI>(BookGUI.seriesComparator);
		currentComparator = BookGUI.seriesComparator;
		temp.addAll(library);
		library = temp;
	}

	// Query the API and get new books
	// TODO: Suggestion dialog for each book?
	public void checkUpSeries(int seriesID) throws NullPointerException {
		String url = "https://www.goodreads.com/series/" + seriesID + "?format=xml&key=bue8ryBjq2NoNd9BWP98hg";

		DocumentBuilderFactory dbFactory;
		DocumentBuilder dBuilder;
		Document doc;
		try{
			dbFactory = DocumentBuilderFactory.newInstance();
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(new URL(url).openStream());
		}
		catch(Exception x){
			System.err.println("Error checking up series with ID: " + seriesID + ", failed to initialise connection to xml document.");
			throw new NullPointerException("Can't initialise book information");
		}
		
		doc.getDocumentElement().normalize();

		int primaryCount = 0;
		Node primaryCountNode = doc.getElementsByTagName("series").item(0);
		if(primaryCountNode.getNodeType() == Node.ELEMENT_NODE){
			Element element = (Element) primaryCountNode;
			primaryCount = Integer.parseInt(element.getElementsByTagName("primary_work_count").item(0).getTextContent());
		}
		else throw new NullPointerException("Error checking up series with ID: " + seriesID + ", no primary_work_count.");

		NodeList series = doc.getElementsByTagName("series_work");
		for (int i = 0; i < series.getLength(); ++i) {
			Element seriesElement = (Element) series.item(i);
			String userPosition = seriesElement.getElementsByTagName("user_position").item(0).getTextContent();
			if (!userPosition.equals("")) {
				int userPos;
				try {
					userPos = Integer.parseInt(userPosition);
					if(userPos > 0 && userPos <= primaryCount){
						Node work = seriesElement.getElementsByTagName("work").item(0);
						Node bestBook = seriesElement.getElementsByTagName("best_book").item(0);
						
						Element workElement = (Element) work;
						Element bookElement = (Element) bestBook;

						int workID = Integer.parseInt(workElement.getElementsByTagName("id").item(0).getTextContent());
						int bookID = Integer.parseInt(bookElement.getElementsByTagName("id").item(0).getTextContent());
						if (DEBUG) System.out.println("Series Adder: Trying to add bookID: " + bookID);
						if (!containsBook(workID)) {
							try {
								Book newBook = new Book(bookID);
								addBook(newBook);
							}
							catch (NullPointerException x) {
								System.err.println("Book with book ID: " + bookID + " was not added to the library, as it failed to initialise.");
							}
						}
					}
				}
				catch(NumberFormatException x) {}
			}
		}
	}

	public void checkUpAllSeries() throws NullPointerException {
		HashSet<Integer> seriesChecked = new HashSet<Integer>();
		@SuppressWarnings("unchecked")
		TreeSet<BookGUI> currentLibrary = (TreeSet<BookGUI>)library.clone();
		Iterator<BookGUI> it = currentLibrary.iterator();
		while(it.hasNext()){
			int seriesID = it.next().getBook().getSeriesID();
			if(!seriesChecked.contains(seriesID)){
				seriesChecked.add(seriesID);
				checkUpSeries(seriesID);
			}
		}
	}

	/*
	// TODO: SeriesGUI maybe?
	public HashMap<String, ArrayList<Book>> getSeries(){
		// Returns the books in their sets of series.
		return null;
	}
	*/

	// Checks if we have a book by checking its ISBN versus every book we have
	public boolean containsBook (Book book) {
		Iterator<BookGUI> it = library.iterator();
		while(it.hasNext()){
			if(it.next().equals(book)) return true;
		}
		return false;
	}

	public boolean containsBook (int workID) {
		Iterator<BookGUI> it = library.iterator();
		while(it.hasNext()){
			if(it.next().getBook().getWorkID() == workID) return true;
		}
		return false;
	}

	public TreeSet<BookGUI> search(String searchKey){
		TreeSet<BookGUI> returnBooks = new TreeSet<BookGUI>(currentComparator);
		Iterator<BookGUI> it = library.iterator();
		while(it.hasNext()){
			BookGUI current = it.next();
			if(current.getBook().search(searchKey)) returnBooks.add(current);
		}
		return returnBooks;
	}

	// A method that tries to download images for all the books
	public void downloadImages () throws IOException {
		for(BookGUI book : library){
			downloadImage(book.getBook());
		}
	}

	// Downloads a books image file using their image url
	public static void downloadImage (Book book) throws IOException {
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