package util;

import java.io.*;

public class Config {

	public static String getKey() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("key.txt"));
			String key = br.readLine();
			br.close();
			return key;
		}
		catch (Exception e) {
			e.printStackTrace();
			System.err.println("A key file is needed for goodreads - key.txt should contain it");
			System.exit(-1);
		}
		return "";
	}

}