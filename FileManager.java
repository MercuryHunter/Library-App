import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.FileWriter;

public class FileManager{
	private String mainFile;

	public FileManager(String mainFile){
		this.mainFile = mainFile;
	}
	
	// TODO: Test this.
	public void writeBookFile(Book book){ // Get details
		try{
			PrintWriter mainWriter = new PrintWriter(new FileWriter(mainFile, true));
			mainWriter.println(book.getISBN());
			mainWriter.close();
			PrintWriter bookWriter = new PrintWriter(new FileWriter("books/" + book.getISBN() + ".txt"));
			bookWriter.println(book.getName());
			bookWriter.println(book.getAuthor());
			bookWriter.println(book.getISBN() + "|" + book.getWorkID());
			bookWriter.println(book.getSeries() + "|" + book.getPositionInSeries() + "|" + book.getSeriesID());
			bookWriter.println(book.getDescription());
			bookWriter.println(book.getOwned());
			bookWriter.println(book.getRead());
			bookWriter.println(book.getWantToRead());
			bookWriter.println(book.getImage());
			bookWriter.close();
		}
		catch(Exception e){
			System.err.println("Error saving book");
		}
	}

	// Remove a book file
	public void removeBookFile(int isbn){

	}

	// Get in all the books from the files
	public ArrayList<Book> readInBooks(){
		ArrayList<Book> books = new ArrayList<Book>();
		try{
			BufferedReader mainReader = new BufferedReader(new FileReader(mainFile));
			String isbn;
			while((isbn = mainReader.readLine()) != null){
				books.add(readInBook(isbn));
			}
			mainReader.close();
		}
		catch(Exception e){
			System.err.println("Error reading in from master file");
			System.exit(0);
		}
		return books;
	}

	public Book readInBook(String isbn){
		try{
			BufferedReader bookReader = new BufferedReader(new FileReader("books/" + isbn + ".txt"));
			String name = bookReader.readLine();
			String author = bookReader.readLine();

			String[] split = bookReader.readLine().split("\\|");
			String isbnReadIn = split[0];
			int workID = Integer.parseInt(split[1]);

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

			return new Book(name, author, isbnReadIn, workID, series, positionInSeries, seriesID, description, owned, read, wantToRead, image);
		}
		catch(Exception e){
			System.err.println("Error reading in single book - " + isbn);
			e.printStackTrace();
			System.exit(0);
		}
		return null;
	}
}