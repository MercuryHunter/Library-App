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
	private int workID, seriesID;
	// Description not compulsory
	private String description = "";
	// Allowed to have no image - ""
	private String image = "";
	private boolean read, owned, wantToRead;

	private boolean DEBUG = LibraryGUI.DEBUG;

	// Constructor Via ISBN and Internet
	public Book(String isbn){
		this.isbn = isbn;
		initialiseInformationISBN();
	}

	// For books already stored
	public Book(String name, String author, String isbn, int workID, String series, int positionInSeries, int seriesID, String description, boolean owned, boolean read, boolean wantToRead, String image){
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
	
	// Method to initialise via ISBN information
	private void initialiseInformationISBN(){
		try{
			String bookUrl = "https://www.goodreads.com/search/index.xml?key=bue8ryBjq2NoNd9BWP98hg&q=" + isbn;
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
			else{
				System.err.println("Error finding book");
				return;
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
		}
		catch(Exception e){
			e.printStackTrace();
		}
		Library.filemanager.writeBookFile(this);
	}

	// Accessors
	public String getName() { return name; }

	public String getAuthor() { return author; }

	public String getSeries() { return series; }

	public String getDescription() { return description.equals("") ? "." : description; }

	public String getImage() { return image.equals("") ? "." : image; }

	public String getISBN() { return isbn; }

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

	public void setWorkID(int workID) { this.workID = workID; }

	public void setSeriesID(int seriesID) { this.seriesID = seriesID; }

	public void setPositionInSeries(int positionInSeries) { this.positionInSeries = positionInSeries; }

	public void setRead(boolean read) { this.read = read; }

	public void setOwned(boolean owned) { this.owned = owned; }

	public void setWantToRead(boolean wantToRead) { this.wantToRead = wantToRead; }

	// Check if two books are equals
	public boolean equals(Book other){
		return this.isbn == other.isbn;
	}

	public String toString(){
		return String.format("<%s, %s, %s, %s, %d, %d, %d, %s, %s, %b, %b, %b>", name, author, series, isbn, positionInSeries, workID, seriesID, description, image, owned, read, wantToRead);
	}

	// Comparators for different sorts.
	public static Comparator<Book> nameComparator = new Comparator<Book>(){
		public int compare(Book one, Book two){
			return one.name.compareTo(two.name);
		}
	};

	public static Comparator<Book> authorComparator = new Comparator<Book>(){
		public int compare(Book one, Book two){
			return one.author.compareTo(two.author);
		}
	};

	public static Comparator<Book> seriesComparator = new Comparator<Book>(){
		public int compare(Book one, Book two){
			return one.series.compareTo(two.series);
		}
	};
}