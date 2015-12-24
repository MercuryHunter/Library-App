/**
 * A Book is like it's real counterpart, with a variety of attributes.
 * As I am using the GoodReads API, the workID and seriesID are how one can
 * communicate with the API.
 */

import java.util.Comparator;

// For XML
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.net.URL;

public class Book {

	private String name, author, series;
	private String isbn; // They can start on 0...
	private int positionInSeries;
	private int bookID, workID, seriesID;
	// Description not compulsory
	private String description = "";
	// Allowed to have no image - ""
	private String image = "";
	private boolean read, owned, wantToRead;

	private boolean DEBUG = LibraryGUI.DEBUG;

	// Constructor that takes a text query and returns a book object
	public Book(String text, boolean isIsbn) throws Exception {
		if(isIsbn) this.isbn = isbn;
		initialiseInformationQuery(text, isIsbn);
	}

	public Book(int bookID) throws Exception {
		isbn = getIsbnFromBookID(bookID);
		initialiseInformationQuery(isbn, true);
	}

	// Constructor for existing books
	public Book(String name, String author, String isbn, int bookID, int workID, String series, int positionInSeries, int seriesID, String description, boolean owned, boolean read, boolean wantToRead, String image){
		this.name = name;
		this.author = author;
		this.isbn = isbn;
		this.workID = workID;
		this.series = series;
		this.positionInSeries = positionInSeries;
		this.seriesID = seriesID;
		this.description = description;
		this.owned = owned;
		this.read = read;
		this.wantToRead = wantToRead;
		this.image = image;
	}
	
	private String getIsbnFromBookID (int bookID) throws Exception {
		String url = "https://www.goodreads.com/book/show/" + bookID + "?format=xml&key=bue8ryBjq2NoNd9BWP98hg";

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new URL(url).openStream());
		
		doc.getDocumentElement().normalize();
		return doc.getElementsByTagName("isbn").item(0).getTextContent();
	}

	// Method to initialise via ISBN information
	// TODO: FIX THIS UP IT COULD PROBABLY BE BETTER WITH MORE UNDERSTANDING
	private void initialiseInformationQuery(String text, boolean isIsbn) throws Exception {
		try{
			String bookUrl = "https://www.goodreads.com/search/index.xml?key=bue8ryBjq2NoNd9BWP98hg&q=\"" + text + "\"";
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new URL(bookUrl).openStream());
			
			doc.getDocumentElement().normalize();

			//System.out.println("Root element: " + doc.getDocumentElement().getNodeName());

			Node node = doc.getElementsByTagName("work").item(0);
			if(node.getNodeType() == Node.ELEMENT_NODE){
				Element element = (Element) node;

				workID = Integer.parseInt(element.getElementsByTagName("id").item(0).getTextContent());
				name = element.getElementsByTagName("title").item(0).getTextContent();
				author = element.getElementsByTagName("name").item(0).getTextContent();
				image = element.getElementsByTagName("image_url").item(0).getTextContent();
				description = ".";

				Node bookIDNode = doc.getElementsByTagName("best_book").item(0);
				Element bookIDElement = (Element) bookIDNode;
				bookID = Integer.parseInt(bookIDElement.getElementsByTagName("id").item(0).getTextContent());

				String pattern = ".+\\(.+#(\\d+)\\)";
				Pattern p = Pattern.compile(pattern);
				Matcher m = p.matcher(name);
				if(m.find())
					positionInSeries = Integer.parseInt(m.group(1));
				else positionInSeries = -1;

				if(DEBUG){
					System.out.println("Work ID:" + workID);
					System.out.println("Title:" + name);
					System.out.println("Name:" + author);
					System.out.println("Image:" + image);
				}
			}

			String seriesUrl = "https://www.goodreads.com/work/"+ workID +"/series?format=xml&key=bue8ryBjq2NoNd9BWP98hg";
			
			doc = dBuilder.parse(new URL(seriesUrl).openStream());
			doc.getDocumentElement().normalize();

			node = doc.getElementsByTagName("series").item(0);
			if(node.getNodeType() == Node.ELEMENT_NODE){
				Element element = (Element) node;

				seriesID = Integer.parseInt(element.getElementsByTagName("id").item(0).getTextContent());
				series = element.getElementsByTagName("title").item(0).getTextContent().trim();
			}
			else{
				series = "";
				seriesID = -1;
			}
			read = false;
			owned = false;
			wantToRead = false;

			if(!isIsbn){
				String url = "https://www.goodreads.com/book/show/" + bookID + "?format=xml&key=bue8ryBjq2NoNd9BWP98hg";
				doc = dBuilder.parse(new URL(url).openStream());
				
				doc.getDocumentElement().normalize();

				isbn = doc.getElementsByTagName("isbn").item(0).getTextContent();
			}

			Library.filemanager.writeBookFile(this);
		}
		catch(Exception e){
			if(DEBUG) e.printStackTrace();
			throw e;
		}
	}

	// Accessors
	public String getName() { return name; }

	public String getAuthor() { return author; }

	public String getSeries() { return series; }

	public String getDescription() { return description.equals("") ? "." : description; }

	public String getImage() { return image.equals("") ? "." : image; }

	public String getISBN() { return isbn; }

	public int getBookID() { return bookID; }

	public int getWorkID() { return workID; }

	public int getSeriesID() { return seriesID; }

	public int getPositionInSeries() { return positionInSeries; }

	public boolean getRead() { return read; }

	public boolean getOwned() { return owned; }

	public boolean getWantToRead() { return wantToRead; }

	// Mutators
	public void setName(String name) { this.name = name; }

	public void setAuthor(String author) { this.author = author; }

	public void setSeries(String series) { this.series = series; }

	public void setDescription(String description) { this.description = description; }

	public void setImage(String image) { this.image = image; }

	public void setISBN(String isbn) { this.isbn = isbn; }

	public void setBookID(int bookID) { this.bookID = bookID; }

	public void setWorkID(int workID) { this.workID = workID; }

	public void setSeriesID(int seriesID) { this.seriesID = seriesID; }

	public void setPositionInSeries(int positionInSeries) { this.positionInSeries = positionInSeries; }

	public void setRead(boolean read) { this.read = read; }

	public void setOwned(boolean owned) { this.owned = owned; }

	public void setWantToRead(boolean wantToRead) { this.wantToRead = wantToRead; }

	// Check if two books are equals
	public boolean equals(Book other){ return this.workID == other.workID; }

	// A string format of all the attributes of the object
	public String toString(){
		return String.format("<%s, %s, %s, %s, %d, %d, %d, %d, %s, %s, %b, %b, %b>", name, author, series, isbn, positionInSeries, bookID, workID, seriesID, description, image, owned, read, wantToRead);
	}

	// Comparators for sorting by book name
	public static Comparator<Book> nameComparator = new Comparator<Book>(){
		public int compare(Book one, Book two){
			return one.name.compareTo(two.name);
		}
	};

	// Comparators for sorting by author. If authors are the same, sorts by series
	// If series are the same, by position
	public static Comparator<Book> authorComparator = new Comparator<Book>(){
		public int compare(Book one, Book two){
			if(one.author.equals(two.author)) {
				if(one.series.equals(two.series))
					return one.positionInSeries - two.positionInSeries;
				return one.series.compareTo(two.series);
			}
			return one.author.compareTo(two.author);
		}
	};

	// Comparators for sorting by series, if series are the same, by position
	public static Comparator<Book> seriesComparator = new Comparator<Book>(){
		public int compare(Book one, Book two){
			if(one.series.equals(two.series)) return one.positionInSeries - two.positionInSeries;
			return one.series.compareTo(two.series);
		}
	};
}