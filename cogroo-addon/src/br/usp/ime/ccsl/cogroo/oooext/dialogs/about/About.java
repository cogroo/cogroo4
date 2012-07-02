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

package br.usp.ime.ccsl.cogroo.oooext.dialogs.about;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.usp.ime.ccsl.cogroo.oooext.LoggerImpl;
import br.usp.ime.ccsl.cogroo.oooext.Resources;
import br.usp.ime.ccsl.cogroo.oooext.addon.AbstractAddOn;
import br.usp.ime.ccsl.cogroo.oooext.addon.conf.AddOnConfiguration;
import br.usp.ime.ccsl.cogroo.oooext.dialogs.reporterror.DialogBuilder;
import br.usp.ime.ccsl.cogroo.oooext.i18n.I18nLabelsLoader;

import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.Rectangle;
import com.sun.star.awt.XActionListener;
import com.sun.star.awt.XButton;
import com.sun.star.awt.XDialog;
import com.sun.star.awt.XFixedText;
import com.sun.star.awt.XWindow;
import com.sun.star.beans.PropertyValue;
import com.sun.star.frame.DispatchDescriptor;
import com.sun.star.frame.XDispatch;
import com.sun.star.lang.EventObject;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.util.URL;

public class About extends AbstractAddOn {
	// Logger
	protected static Logger LOGGER = LoggerImpl.getLogger(About.class.getCanonicalName());
	
	public static final String PROTOCOL_PATH = "aboutDialog";

	protected XDialog dialog;

	protected XFixedText licenseView;

	protected XButton licenseButton;

	protected boolean liceneDisplayed = false;

	public About(XComponentContext context) {
		super(context);
	}

	protected void init() {
		try {
			DialogBuilder builder = new DialogBuilder(this.context, 200, 200,
					180, 125, I18nLabelsLoader.ADDON_LABELS_ABOUT);

			// get the image
			AddOnConfiguration config = new AddOnConfiguration(this.context);
			String imageURL = config.getRootURL()
					+ Resources.getProperty("ICON_ABOUT");
			
			if(LOGGER.isLoggable(Level.FINE))
			{
				LOGGER.fine("About.init: imageURL: " + imageURL);
			}

			builder.addImage(imageURL, "about.image", 0, 0, 180, 45, true,
					(short) 0);

			builder.addMultiLineLabel(
					I18nLabelsLoader.ADDON_NAME	+ LoggerImpl.lineSeparator // LanguageToolsSampleAddon 
					+ I18nLabelsLoader.ADDON_LABELS_VERSION + ": " + I18nLabelsLoader.ADDON_VERSION + LoggerImpl.lineSeparator // version
					+ I18nLabelsLoader.ADDON_SITE + LoggerImpl.lineSeparator // site
					//+ I18nLabelsLoader.ADDON_LABELS_AUTHORS + ":" + LoggerImpl.lineSeparator + "\t" // Authors: 
					//+ I18nLabelsLoader.ADDON_AUTHORS // William 
					,"about", 2, 50, 176, 40);

			licenseButton = builder.addButton(
					I18nLabelsLoader.ADDON_LABELS_VIEW_LICENSE,
					"license.button", 35, 100, 50, 15);
			licenseButton.addActionListener(new XActionListener() {

				public void actionPerformed(ActionEvent arg0) {

					if (liceneDisplayed) {

						licenseView.setText("");
						XWindow window = (XWindow) UnoRuntime.queryInterface(
								XWindow.class, dialog);
						Rectangle r = window.getPosSize();

						window.setPosSize(r.X, r.Y, r.Width, (r.Height - 400),
								(short) 15);
						licenseButton
								.setLabel(I18nLabelsLoader.ADDON_LABELS_VIEW_LICENSE);
						liceneDisplayed = false;
					} else {
						XWindow window = (XWindow) UnoRuntime.queryInterface(
								XWindow.class, dialog);
						Rectangle r = window.getPosSize();

						window.setPosSize(r.X, r.Y, r.Width, (r.Height + 400),
								(short) 15);
						r = window.getPosSize();

						licenseView.setText(getLicenseText());
						licenseButton
								.setLabel(I18nLabelsLoader.ADDON_LABELS_HIDE_LICENSE);
						liceneDisplayed = true;
					}

				}

				public void disposing(EventObject arg0) {

				}

			});

			this.licenseView = builder.addMultiLineLabel("", "license.view", 2,
					127, 176, 310);

			XButton button = builder.addButton("OK", "ok", 95, 100, 50, 15);
			button.addActionListener(new XActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					setVisible(false);

				}

				public void disposing(EventObject arg0) {

				}

			});
			this.dialog = builder.getDialog();

			dialog.execute();

		} catch (Throwable e) {
			LOGGER.log(Level.SEVERE, "Uncaught exception", e);
		
		}

		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("<<< About.init()");
		}

	}

	/**
	 * Show/Close the Aboutdialog
	 * 
	 * @param b
	 */

	public void setVisible(boolean b) {
		if (b) {
			this.init();
		} else if (dialog != null) {
			dialog.endExecute();
		}
	}

	public String getImplementationName() {
		return this.getClass().getName();

	}

	public void dispatch(URL url, PropertyValue[] arg1) {

		if (url.Protocol.equals(PROTOCOL_URL) && url.Path.equals(PROTOCOL_PATH)) {
			setVisible(true);
		}

	}

	public XDispatch queryDispatch(URL url, String arg1, int arg2) {
		if (url.Protocol.equals(PROTOCOL_URL) && url.Path.equals(PROTOCOL_PATH)) {
			return this;
		}

		return null;
	}

	public XDispatch[] queryDispatches(DispatchDescriptor[] arg0) {
		XDispatch[] lDispatcher = new XDispatch[arg0.length];

		for (int i = 0; i < arg0.length; ++i) {
			lDispatcher[i] = queryDispatch(arg0[i].FeatureURL,
					arg0[i].FrameName, arg0[i].SearchFlags);
		}

		return lDispatcher;
	}

	private String getLicenseText() {
		StringBuffer licenseText = new StringBuffer();
		AddOnConfiguration config = new AddOnConfiguration(this.context);
		String licenseFile = config.getRootFolder()
				+ Resources.getProperty("LICENSE_ABOUT");

		try {
			LOGGER.fine("Will try to open license file: " + licenseFile);
			
			FileInputStream fis = new FileInputStream(new File(licenseFile));
			InputStreamReader in = new InputStreamReader(fis, "UTF-8");
			BufferedReader reader = new BufferedReader(in);
			
			String line = reader.readLine();
			while (line != null) {
				licenseText.append(line + LoggerImpl.lineSeparator);
				line = reader.readLine();
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Uncaught exception", e);
		}

		return licenseText.toString();
	}
}
