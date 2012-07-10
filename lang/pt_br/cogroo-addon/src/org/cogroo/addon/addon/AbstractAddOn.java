/**
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

package org.cogroo.addon.addon;

import java.util.ArrayList;
import java.util.List;

import com.sun.star.frame.XDispatch;
import com.sun.star.frame.XDispatchProvider;
import com.sun.star.frame.XFrame;
import com.sun.star.frame.XStatusListener;
import com.sun.star.lang.XInitialization;
import com.sun.star.lang.XServiceInfo;
import com.sun.star.uno.Exception;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.util.URL;

public abstract class AbstractAddOn implements XDispatchProvider, XDispatch,
		XInitialization, XServiceInfo {
	public static final String PROTOCOL_URL = "dk.abj.openoffice.addon:";
	static public final String SERVICE = "com.sun.star.frame.ProtocolHandler";
	protected XComponentContext context;
	protected XFrame frame;
	protected List<XStatusListener> listeners = new ArrayList<XStatusListener>();

	public AbstractAddOn(XComponentContext context) {
		this.context = context;
	}

	public void addStatusListener(XStatusListener listeners, URL arg1) {
		this.listeners.add(listeners);
	}

	public void removeStatusListener(XStatusListener listeners, URL arg1) {
		this.listeners.remove(listeners);
	}

	public void initialize(Object[] arg0) throws Exception {
		if (arg0.length > 0) {
			this.frame = (XFrame) UnoRuntime.queryInterface(XFrame.class,
					arg0[0]);
		}
	}

	public String[] getSupportedServiceNames() {
		return new String[] { SERVICE };
	}

	public boolean supportsService(String arg0) {
		if (SERVICE.equals(arg0)) {
			return true;
		}

		return false;
	}
}
