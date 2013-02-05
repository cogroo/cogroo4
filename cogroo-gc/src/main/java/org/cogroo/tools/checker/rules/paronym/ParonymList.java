package org.cogroo.tools.checker.rules.paronym;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.cogroo.analyzer.InitializationException;

public class ParonymList {

	/**
	 * Structure that stores from an input list words and its closer paronyms
	 * for possible error checking.
	 */
	private final Map<String, String> paronymsMap;

	public ParonymList() {
		paronymsMap = Collections.unmodifiableMap(parseConfiguration());
	}

	public Map<String, String> parseConfiguration() {
		InputStream input = ParonymList.class.getClassLoader()
				.getResourceAsStream("rules/paronymy/paronym.txt");

		Map<String, String> map = new HashMap<String, String>();

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					input, "UTF-8"));

			while (reader.ready()) {
				String line = reader.readLine().toLowerCase();

				if (line.length() > 0) {

					/* This line is a comment */
					if (line.charAt(0) == '#')
						continue;

						String[] words = line.split(",");
						if (words != null) {
							map.put(words[0], words[1]);
							map.put(words[1], words[0]);
						}
//					}
				}
			}
			return map;

		} catch (UnsupportedEncodingException e) {
			// Shouldn't happen because every system contains the utf-8 encode.
			throw new InitializationException(
					"Enconding problem while reading the verbs.txt file", e);
		} catch (IOException e) {
			throw new InitializationException(
					"Could not read the paronyms.txt file", e);
		}
	}

	public Map<String, String> getParonymsMap() {
		return paronymsMap;
	}
	
}
