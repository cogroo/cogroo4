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
