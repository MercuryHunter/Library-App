/**
 * The FileManager handles the writing of books to file and their reading
 * on startup of the application.
 */

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileManager{

	private boolean DEBUG = LibraryGUI.DEBUG;

	private String mainFile;

	public FileManager(String mainFile){ this.mainFile = mainFile; }
	
	// Writes a book's information to file
	public void writeBookFile(Book book){ 
		try{
			PrintWriter mainWriter = new PrintWriter(new FileWriter(mainFile, true));
			mainWriter.println(book.getISBN());
			mainWriter.close();
			PrintWriter bookWriter = new PrintWriter(new FileWriter("books/" + book.getISBN() + ".txt"));
			bookWriter.println(book.getName());
			bookWriter.println(book.getAuthor());
			bookWriter.println(book.getISBN() + "|" + book.getWorkID() + "|" + book.getBookID());
			bookWriter.println(book.getSeries() + "|" + book.getPositionInSeries() + "|" + book.getSeriesID());
			bookWriter.println(book.getDescription());
			bookWriter.println(book.getOwned());
			bookWriter.println(book.getRead());
			bookWriter.println(book.getWantToRead());
			bookWriter.println(book.getImage());
			bookWriter.close();
		}
		catch(Exception x){
			System.err.println("Error saving book");
		}
	}

	// Removes a book's files, and its mention in the mainlist.
	public void removeBookFiles(Book book){
		try {
			Files.delete(Paths.get("books/" + book.getISBN() + ".txt"));
			Files.delete(Paths.get("images/" + book.getISBN() + ".jpg"));

			// Who knows how inefficient this is, thankfully, this shouldn't be used much.
			ArrayList<String> file = new ArrayList<String>();
			BufferedReader br = new BufferedReader(new FileReader(mainFile));
			String line;
			while((line = br.readLine()) != null) if(!line.equals(book.getISBN())) file.add(line);
			br.close();

			PrintWriter pw = new PrintWriter(new FileWriter(mainFile, false));
			for(String isbn : file) pw.println(isbn);
			pw.close();

		}
		catch (Exception x) {
			System.err.println("Error removing book files.");
			if (DEBUG) x.printStackTrace();
		}
	}

	// Get in all the books from the files, as the mainfile lists.
	public ArrayList<BookGUI> readInBooks(){
		ArrayList<BookGUI> books = new ArrayList<BookGUI>();
		try{
			BufferedReader mainReader = new BufferedReader(new FileReader(mainFile));
			String isbn;
			while((isbn = mainReader.readLine()) != null){
				books.add(readInBook(isbn));
			}
			mainReader.close();
		}
		catch(Exception x){
			System.err.println("Error reading in from master file");
			System.exit(0);
		}
		return books;
	}

	// Gets a single book's information by reading the file with the corresponding ISBN
	public BookGUI readInBook(String isbn){
		try{
			BufferedReader bookReader = new BufferedReader(new FileReader("books/" + isbn + ".txt"));
			String name = bookReader.readLine();
			String author = bookReader.readLine();

			String[] split = bookReader.readLine().split("\\|");
			String isbnReadIn = split[0];
			int workID = Integer.parseInt(split[1]);
			int bookID = Integer.parseInt(split[2]);

			split = bookReader.readLine().split("\\|");
			String series = split[0];
			int positionInSeries = Integer.parseInt(split[1]);
			int seriesID = Integer.parseInt(split[2]);

			String description = bookReader.readLine();

			boolean owned = Boolean.parseBoolean(bookReader.readLine());
			boolean read = Boolean.parseBoolean(bookReader.readLine());
			boolean wantToRead = Boolean.parseBoolean(bookReader.readLine());

			String image = bookReader.readLine();

			bookReader.close();

			return new BookGUI(new Book(name, author, isbnReadIn, bookID, workID, series, positionInSeries, seriesID, description, owned, read, wantToRead, image));
		}
		catch(Exception x){
			System.err.println("Error reading in single book - " + isbn);
			if (DEBUG) x.printStackTrace();
			else System.exit(0);
		}
		return null;
	}
}