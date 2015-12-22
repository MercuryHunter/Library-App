public class TestClass {
	public static void main (String[] args) {
		FileManager fm = new FileManager("masterlist.txt");
		Book b = fm.readInBook("9780575105836");
		fm.removeBookFiles(b);
	}
}