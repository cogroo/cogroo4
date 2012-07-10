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

package org.cogroo.addon.dialogs.about;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.cogroo.addon.LoggerImpl;


import com.sun.star.uno.XComponentContext;

public class AboutThread extends Thread {
	// Logger
	protected static Logger LOGGER = LoggerImpl.getLogger(AboutThread.class.getCanonicalName());
	
	private XComponentContext xCompContext;

	public AboutThread(XComponentContext xCompContext) {
		LOGGER.fine("Called About constructor.");
		this.xCompContext = xCompContext;
	}

	@Override
	public void run() {
		LOGGER.fine("Called About run().");
		try {
			LOGGER.fine("Will create AboutDialog");
			About ad = new About(this.xCompContext);
			ad.setVisible(true);
			LOGGER.fine("Finished");
		} catch (Throwable e) {
			LOGGER.log(Level.SEVERE, "Uncaught exception", e);
		}
	}
}
