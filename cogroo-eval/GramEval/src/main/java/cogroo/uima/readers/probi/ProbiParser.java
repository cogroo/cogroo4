package cogroo.uima.readers.probi;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import opennlp.tools.util.ObjectStream;


public class ProbiParser  implements ObjectStream<ProbiEntry>{
	
	private BufferedReader probiReader;

	public ProbiParser(File file, String encoding) throws FileNotFoundException, UnsupportedEncodingException {
	  InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream (file), encoding );
		probiReader = new BufferedReader (inputStreamReader);
		
	}
	
	public List<ProbiEntry> getEntries() throws Exception {
		
		String line = probiReader.readLine();
		
		List<ProbiEntry> entries = new ArrayList<ProbiEntry>(11600);
		while (line != null) {
			
				ProbiEntry entry = ProbiEntry.fromString(line);
				entries.add(entry);
			
			line = probiReader.readLine();
		}
		
		return Collections.unmodifiableList(entries);
	}

	public ProbiEntry read() throws IOException {
		String line = probiReader.readLine();
		if(line == null) {
			return null;
		}
		return ProbiEntry.fromString(line);
	}

	public void reset() throws IOException, UnsupportedOperationException {
		probiReader.reset();
	}

	public void close() throws IOException {
		probiReader.close();		
	}

}
