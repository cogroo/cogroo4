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
package org.cogroo.addon.dialogs.reporterror;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.cogroo.addon.LoggerImpl;
import org.cogroo.addon.dialogs.MessageBox;
import org.cogroo.addon.i18n.I18nLabelsLoader;


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
