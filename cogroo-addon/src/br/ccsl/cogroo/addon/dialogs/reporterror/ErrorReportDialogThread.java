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

package br.ccsl.cogroo.addon.dialogs.reporterror;

import java.util.logging.Level;
import java.util.logging.Logger;

import br.ccsl.cogroo.addon.LoggerImpl;
import br.ccsl.cogroo.addon.dialogs.MessageBox;
import br.ccsl.cogroo.addon.i18n.I18nLabelsLoader;

import com.sun.star.awt.MessageBoxButtons;

import com.sun.star.uno.XComponentContext;

public class ErrorReportDialogThread extends Thread {
	// Logger
	protected static Logger LOGGER = LoggerImpl.getLogger(ErrorReportDialogThread.class.getCanonicalName());
	
	private XComponentContext xCompContext;

        private String text;

        public void setText(String aText) {
            text = aText;
        }

	public ErrorReportDialogThread(XComponentContext xCompContext) {
		LOGGER.fine("Called ReportError constructor.");
		this.xCompContext = xCompContext;
	}

	@Override
	public void run() {
		LOGGER.fine("Called ErrorReportDialogThread run().");
                if(text == null || text.length() == 0) {
                    LOGGER.fine("Text was null or empty. Will return.");
                    MessageBox mb = new MessageBox(xCompContext);
                    mb.showMessageBox(
                            I18nLabelsLoader.ADDON_REPORT_ERROR_SELECTION_EMPTY_HEADER,
                            I18nLabelsLoader.ADDON_REPORT_ERROR_SELECTION_EMPTY_BODY,
                            "warningbox", MessageBoxButtons.BUTTONS_OK);
                    return;
                }

                if(text.length() > 255) {
                    LOGGER.fine("Text too long: " + text.length());
                    MessageBox mb = new MessageBox(xCompContext);
                    short result = mb.showMessageBox(
                            I18nLabelsLoader.ADDON_REPORT_ERROR_SELECTION_TOO_LONG_HEADER,
                            I18nLabelsLoader.ADDON_REPORT_ERROR_SELECTION_TOO_LONG_BODY,
                            "warningbox", MessageBoxButtons.BUTTONS_OK_CANCEL);
                    if(result == MessageBoxButtons.BUTTONS_OK){
                        text = text.substring(0, 255);
                    } else {
                         LOGGER.fine("User canceled");
                        return;
                    }
                    LOGGER.fine("Result: " + result);
                }

                if(text.split("\\s+").length < 3) {
                    LOGGER.fine("Text too short: " + text.length());
                    MessageBox mb = new MessageBox(xCompContext);
                    short result = mb.showMessageBox(
                            I18nLabelsLoader.ADDON_REPORT_ERROR_SELECTION_TOO_SHORT_HEADER,
                            I18nLabelsLoader.ADDON_REPORT_ERROR_SELECTION_TOO_SHORT_BODY + " \"" + text + "\"",
                            "warningbox", MessageBoxButtons.BUTTONS_OK_CANCEL);
                    if(result == MessageBoxButtons.BUTTONS_OK){
                        //text = text.substring(0, 255);
                    } else {
                         LOGGER.fine("User canceled");
                        return;
                    }
                    LOGGER.fine("Result: " + result);
                }

                ErrorReportDialog errorReportDialog = null;
		try {

                    LOGGER.fine("Will create UnoDialogSample dialog");

                    errorReportDialog = new ErrorReportDialog(this.xCompContext, xCompContext.getServiceManager());
                    errorReportDialog.initialize(text);
                    errorReportDialog.executeDialog();
                    LOGGER.fine("Finished");
                        
		} catch( Exception e ) {
                    LOGGER.log(Level.SEVERE, "Uncaught exception", e);
                } finally{
                    //make sure always to dispose the component and free the memory!
                    if (errorReportDialog != null){
                        if (errorReportDialog.m_xComponent != null){
                            errorReportDialog.m_xComponent.dispose();
                        }
                    }
                }
        }
}
