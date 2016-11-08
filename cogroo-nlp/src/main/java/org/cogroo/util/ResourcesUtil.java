package org.cogroo.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ResourcesUtil {
	
	public static File getResourceFile(Class<?> theClass, String fileName) {
		return new File(theClass.getResource(fileName).getFile());
	}
	
	public static String getResourceAsString(Class<?> theClass, String fileName) throws IOException {
		
		File f = new File(theClass.getResource(fileName).getFile());
		FileReader fr = new FileReader(f);
		BufferedReader br = new BufferedReader(fr);
		StringBuilder sb = new StringBuilder();
		String line = br.readLine();
		while(line != null) {
			sb.append(line + "\n");
			line = br.readLine();
		}
		br.close();
		return sb.toString();
	}

}
