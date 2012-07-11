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
