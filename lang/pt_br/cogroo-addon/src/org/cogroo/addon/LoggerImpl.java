/**
 * Copyright (C) 2012 cogroo <cogroo@cogroo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cogroo.addon;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerImpl {

	/** get the platform's String to mark the end of each line */
	public static final String lineSeparator = System
			.getProperty("line.separator");

	private static final String path = Resources
			.getProperty("LOG_FILE_PATTERN");
	// private static final String path =
	// "%t/br.usp.pcs.lta.cogroo.ooointegration.%u/logs/trace_%g.log";

	private static boolean initialized = false;

	private static FileHandler handler;

	private static final Level defaultLevel = Level.parse(Resources
			.getProperty("LOG_LEVEL"));
	// private static final Level defaultLevel = Level.ALL;

	private static final String tempDir = getTmpFolder();

	private static final String homeDir = System.getProperty("user.name");

	private static final String userName = System.getProperty("user.name");

	private static void init() {
		if (!initialized) {
			try {
				// syntax:
				// http://java.sun.com/j2se/1.4.2/docs/api/java/util/logging
				// /FileHandler.html
				createPath(path);

				handler = new FileHandler(replacePath(path), 100000, 5, true);
				handler.setFormatter(new SimpleFormatter());

			} catch (SecurityException e) {
				// can't log from here
				e.printStackTrace();
			} catch (IOException e) {
				// can't log from here
				e.printStackTrace();
			}
		}
		initialized = true;
	}

	public static String getPath() {
		return replacePath(path);
	}

	public static boolean isInitialized() {
		return initialized;
	}

	public static Logger getLogger(String name) {
		init();
		Logger l = Logger.getLogger(name);
		l.addHandler(handler);
		l.setLevel(defaultLevel);
		return l;
	}

	private static String replacePath(String oriPath) {
		String newPath = oriPath.replace("%t", tempDir);
		newPath = newPath.replace("%h", homeDir);
		newPath = newPath.replace("%u", userName);
		try {
			newPath = new File(newPath).getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return newPath;
	}

	private static void createPath(String oriPath) {
		File parent = new File(replacePath(oriPath)).getParentFile();
		if (!parent.exists())
			parent.mkdirs();
	}

	private static String getTmpFolder() {
		String tmp = System.getProperty("java.io.tmpdir");
		// workaround for MAC OSX
		if (tmp.startsWith("/var/folders/"))
			tmp = "/tmp/";
		return tmp;
	}
}
