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

package org.cogroo.addon.i18n;

import java.util.ResourceBundle;
import java.util.logging.Logger;


import java.util.PropertyResourceBundle;

import org.cogroo.addon.LoggerImpl;

public final class I18nLabelsLoader {
	
	// Logger
	protected static Logger LOGGER = LoggerImpl.getLogger(I18nLabelsLoader.class.getCanonicalName());
	

	private static final ResourceBundle PROPERTIES = PropertyResourceBundle
			.getBundle("labels");

	public static final String ADDON_NAME = getString("addon.name");
	public static final String ADDON_VERSION = getString("addon.version");
	public static final String ADDON_AUTHORS = getString("addon.authors");
	public static final String ADDON_SITE = getString("addon.site");

	public static final String ADDON_LABELS_ABOUT = getString("addon.labels.about");
	public static final String ADDON_LABELS_VERSION = getString("addon.labels.version");
	public static final String ADDON_LABELS_AUTHORS = getString("addon.labels.authors");
	public static final String ADDON_LABELS_VIEW_LICENSE = getString("addon.labels.view_license");
	public static final String ADDON_LABELS_HIDE_LICENSE = getString("addon.labels.hide_license");

        public static final String ADDON_REPORT_FROM_BROWSER  = getString("addon.labels.report_from_browser");
        public static final String ADDON_REPORT_REGISTER_FIRST = getString("addon.labels.report_error_register_first");
        public static final String ADDON_REPORT_ERROR = getString("addon.labels.report_error");
        public static final String ADDON_REPORT_ERROR_FALSENEGATIVE = getString("addon.labels.report_error.falsenegative");
        public static final String ADDON_REPORT_ERROR_FALSEPOSITVE = getString("addon.labels.report_error.falsepositive");
        public static final String ADDON_REPORT_ERROR_TEXT = getString("addon.labels.report_error.text");
        public static final String ADDON_REPORT_ERROR_COMMENTS = getString("addon.labels.report_error.comments");

        public static final String ADDON_REPORT_ERROR_SEND = getString("addon.labels.report_error.send");
        public static final String ADDON_REPORT_ERROR_CANCEL = getString("addon.labels.report_error.cancel");

        // STEP
        public static final String REPORT_STEP_NAME = getString("addon.labels.report_step_name");
        public static final String REPORT_STEP_LOGIN = getString("addon.labels.report_step_login");
        public static final String REPORT_STEP_FALSE_ERRORS = getString("addon.labels.report_step_false_errors");
        public static final String REPORT_STEP_OMISSIONS = getString("addon.labels.report_step_omissions");
        public static final String REPORT_STEP_THANKS = getString("addon.labels.report_step_thanks");
        public static final String REPORT_STEP_BUTTONS_PREV = getString("addon.labels.report_step_buttons_prev");
        public static final String REPORT_STEP_BUTTONS_NEXT = getString("addon.labels.report_step_buttons_next");
        public static final String REPORT_STEP_BUTTONS_CANCEL = getString("addon.labels.report_step_buttons_cancel");

        public static final String REPORT_STEP_BUTTONS_FINISH = getString("addon.labels.report_step_buttons_finish");
        public static final String REPORT_STEP_BUTTONS_SUBMIT = getString("addon.labels.report_step_buttons_submit");
        // login
        public static final String ADDON_LOGIN_INFO = getString("addon.labels.report_login.info");
        public static final String ADDON_LOGIN_REGISTER = getString("addon.labels.report_login.register");
        public static final String ADDON_LOGIN_LICENSE = getString("addon.labels.report_login.license");
        public static final String ADDON_LOGIN_LICENSEURL = getString("addon.labels.report_login.licenseurl");
        public static final String ADDON_LOGIN_USER = getString("addon.labels.report_login.user");
        public static final String ADDON_LOGIN_PASSWORD = getString("addon.labels.report_login.password");
        public static final String ADDON_LOGIN_ALLOW = getString("addon.labels.report_login.allow");
        public static final String ADDON_LOGIN_STATUS = getString("addon.labels.report_login.status");
        public static final String ADDON_LOGIN_STATUS_OK = getString("addon.labels.report_login.status.ok");
        public static final String ADDON_LOGIN_STATUS_NOTAUTH = getString("addon.labels.report_login.status.notauth");
        public static final String ADDON_LOGIN_STATUS_INVALIDUSER = getString("addon.labels.report_login.status.invaliduser");
        public static final String ADDON_LOGIN_STATUS_COMMUNICATIONERROR = getString("addon.labels.report_login.status.communicationerror");

        // Bad intervention
        public static final String ADDON_BADINT_INFO = getString("addon.labels.report_badint.info");
        public static final String ADDON_BADINT_ERRORSFOUND = getString("addon.labels.report_badint.errorsfound");
        public static final String ADDON_BADINT_DETAILS = getString("addon.labels.report_badint.details");
        public static final String ADDON_BADINT_ERRORSLIST = getString("addon.labels.report_badint.errorslist");
        public static final String ADDON_BADINT_APPLY = getString("addon.labels.report_badint.apply");
        public static final String ADDON_BADINT_GOODINT = getString("addon.labels.report_badint_type.goodint");
        public static final String ADDON_BADINT_FALSEERROR = getString("addon.labels.report_badint_type.falseerror");
        public static final String ADDON_BADINT_BADDESCRIPTION = getString("addon.labels.report_badint_type.baddescription");
        public static final String ADDON_BADINT_BADSUGESTION = getString("addon.labels.report_badint_type.badsugestion");

        public static final String ADDON_BADINT_GOODINT_SHORT = getString("addon.labels.report_badint_type.goodint_short");
        public static final String ADDON_BADINT_FALSEERROR_SHORT = getString("addon.labels.report_badint_type.falseerror_short");
        public static final String ADDON_BADINT_BADDESCRIPTION_SHORT = getString("addon.labels.report_badint_type.baddescription_short");
        public static final String ADDON_BADINT_BADSUGESTION_SHORT = getString("addon.labels.report_badint_type.badsugestion_short");

        public static final String ADDON_BADINT_COMMENTS = getString("addon.labels.report_badint_type.comments");
        public static final String ADDON_BADINT_TYPE = getString("addon.labels.report_badint_type.type");
        public static final String ADDON_BADINT_SUGESTIONS = getString("addon.labels.report_badint.sugestion");


        public static final String ADDON_OMISSION_INFO = getString("addon.labels.report_omission.info");
        public static final String ADDON_OMISSION_SELECT = getString("addon.labels.report_omission.select");
        public static final String ADDON_OMISSION_CLASSIFY = getString("addon.labels.report_omission.classify");
        public static final String ADDON_OMISSION_EXCLUDE = getString("addon.labels.report_omission.exclude");
        public static final String ADDON_OMISSION_SELECTED_OMISSION = getString("addon.labels.report_omission.omission");
        public static final String ADDON_OMISSION_CLASSIFIED_ERRORS = getString("addon.labels.report_omission.classifiederrors");
        public static final String ADDON_OMISSION_CATEGORY = getString("addon.labels.report_omission.category");
        public static final String ADDON_OMISSION_COMMENTS = getString("addon.labels.report_omission.comments");
        public static final String ADDON_OMISSION_APPLY = getString("addon.labels.report_omission.apply");
        public static final String ADDON_OMISSION_CATEGORY_CUSTOM = getString("addon.labels.report_omission.category.custom");
        public static final String ADDON_OMISSION_CATEGORY_UNKNOWN = getString("addon.labels.report_omission.category.unknown");
        public static final String ADDON_OMISSION_REPLACE_BY = getString("addon.labels.report_omission.substituteby");

        // thanks
        public static final String ADDON_THANKS_MESSAGE = getString("addon.labels.report_thanks.message");
        public static final String ADDON_THANKS_LINK = getString("addon.labels.report_thanks.link");
        public static final String ADDON_THANKS_STATUS = getString("addon.labels.report_thanks.status");
        public static final String ADDON_THANKS_STATUS_SENDING = getString("addon.labels.report_thanks.status.sending");
        public static final String ADDON_THANKS_STATUS_ERROR = getString("addon.labels.report_thanks.status.error");
        public static final String ADDON_THANKS_STATUS_DONE = getString("addon.labels.report_thanks.status.done");

        // Error Message
        public static final String ADDON_REPORT_ERROR_SELECTION_TOO_LONG_HEADER = getString("addon.labels.report_error.selection_too_long.header");
        public static final String ADDON_REPORT_ERROR_SELECTION_TOO_LONG_BODY = getString("addon.labels.report_error.selection_too_long.body");
        public static final String ADDON_REPORT_ERROR_SELECTION_EMPTY_HEADER = getString("addon.labels.report_error.selection_empty.header");
        public static final String ADDON_REPORT_ERROR_SELECTION_EMPTY_BODY = getString("addon.labels.report_error.selection_empty.body");
        public static final String ADDON_REPORT_ERROR_SELECTION_TOO_SHORT_HEADER = getString("addon.labels.report_error.selection_too_short.header");
        public static final String ADDON_REPORT_ERROR_SELECTION_TOO_SHORT_BODY = getString("addon.labels.report_error.selection_too_short.body");

        // Context Menu
        public static final String ADDON_REPORT_ERROR_CONTEXTMENU_GC = getString("addon.labels.contextmenu.grammarchecker");
        public static final String ADDON_REPORT_ERROR_CONTEXTMENU_REPORT = getString("addon.labels.contextmenu.grammarchecker.reporterror");


	private static String getString(String key) {
		String ret = PROPERTIES.getString(key);
		if (ret == null)
		{
			LOGGER.info("I18nLabelsLoader.getString - missing key: " + key);
		}
		return ret;
	}

	private I18nLabelsLoader() {
		// Prevents instantiation.
	}

	public static void main(String[] args) {
		System.out.println(I18nLabelsLoader.ADDON_NAME);
	}

}
