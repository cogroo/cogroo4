/**
 * Copyright (C) 2008 William Colen<colen at users.sourceforge.net>
 *
 * http://lingualquanta.sourceforge.net/ooointegration
 *
 * This file is part of Lingual Quanta OpenOffice.org Integration.
 *
 * OOoIntegration is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Publicas published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OOoIntegration is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OOoIntegration.  If not, see <http://www.gnu.org/licenses/>.
 *
 * NOTICE:
 * Some peaces of the code came from other projects I studied.
 * I have to thanks the developers of:
 * 	CoGrOO
 * 		Site: cogroo.sourceforge.net
 * 		License: LGPL
 * 		Used as a sample of Grammar Checker Addon.
 * 	LinguageTool
 * 		Site: www.languagetool.org
 * 		License: LGPL
 * 		Used as a sample of Grammar Checker Addon that
 * 		implements the XGrammarChecker interface.
 *  	dxf2calc
 *  		Site: www.abj.dk/dxf2calc
 * 		License: SODA-WARE
 *  		Was used as more sofisticated sample of OOo Addon, specially
 *  		as a sample of how to find the Addon folder during runtime and
 *  		how do a nice About dialog box.
 */

package br.usp.ime.ccsl.cogroo.oooext;

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

	private static final String path = Resources.getProperty("LOG_FILE_PATTERN");
	//private static final String path = "%t/br.usp.pcs.lta.cogroo.ooointegration.%u/logs/trace_%g.log";

	private static boolean initialized = false;

	private static FileHandler handler;

	private static final Level defaultLevel = Level.parse(Resources.getProperty("LOG_LEVEL"));
	//private static final Level defaultLevel = Level.ALL;

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
	
	public static boolean isInitialized()
	{
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
		newPath =  newPath.replace("%h", homeDir); 
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
            if (tmp.startsWith("/var/folders/")) tmp = "/tmp/";
            return tmp;
        }
}

