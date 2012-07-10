/**
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

package org.cogroo.addon.addon.conf;

import com.sun.star.beans.XHierarchicalPropertySet;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.XNameAccess;
import com.sun.star.container.XNameContainer;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.lang.XSingleServiceFactory;
import com.sun.star.uno.Exception;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.util.XChangesBatch;

public class DefaultConfiguration {

	public static final String CONFIGURATION_SERVICE = "com.sun.star.configuration.ConfigurationProvider";
	public static final String CONFIGURATION_READ_ONLY_VIEW = "com.sun.star.configuration.ConfigurationAccess";
	public static final String CONFIGURATION_UPDATABLE_VIEW = "com.sun.star.configuration.ConfigurationUpdateAccess";
	protected XMultiServiceFactory configProvider;

	public DefaultConfiguration(XComponentContext context) {
		// this.context = context;
		XMultiServiceFactory factory = (XMultiServiceFactory) UnoRuntime
				.queryInterface(XMultiServiceFactory.class, context
						.getServiceManager());
		this.init(factory);
	}

	public DefaultConfiguration(XMultiServiceFactory factory) {
		this.init(factory);
	}

	protected void init(XMultiServiceFactory factory) {
		try {
			this.configProvider = (XMultiServiceFactory) UnoRuntime
					.queryInterface(XMultiServiceFactory.class, factory
							.createInstance(CONFIGURATION_SERVICE));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public Object getRootNode(String configPath, boolean readonly) {
		try {
			if (readonly) {
				com.sun.star.beans.PropertyValue path = new com.sun.star.beans.PropertyValue();
				path.Name = "nodepath";
				path.Value = configPath;

				Object[] args = new Object[1];
				args[0] = path;

				return this.configProvider.createInstanceWithArguments(
						CONFIGURATION_READ_ONLY_VIEW, args);
			} else {
				com.sun.star.beans.PropertyValue aPathArgument = new com.sun.star.beans.PropertyValue();
				aPathArgument.Name = "nodepath";
				aPathArgument.Value = configPath;

				com.sun.star.beans.PropertyValue aModeArgument = new com.sun.star.beans.PropertyValue();
				aModeArgument.Name = "EnableAsync";
				aModeArgument.Value = new Boolean(true);

				Object[] args = new Object[2];
				args[0] = aPathArgument;
				args[1] = aModeArgument;

				return this.configProvider.createInstanceWithArguments(
						CONFIGURATION_UPDATABLE_VIEW, args);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return null;
	}

	public Object getProperty(String configPath, String property) {
		Object theObject = null;
		try {
			Object root = this.getRootNode(configPath, true);

			XHierarchicalPropertySet props = (XHierarchicalPropertySet) UnoRuntime
					.queryInterface(XHierarchicalPropertySet.class, root);
			theObject = props.getHierarchicalPropertyValue(property);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return theObject;
	}

	public Object getChildNode(Object parent, String child) {
		Object theObject = null;
		try {
			XNameAccess childNode = (XNameAccess) UnoRuntime.queryInterface(
					XNameAccess.class, parent);

			if (childNode.hasByName(child)) {

				theObject = childNode.getByName(child);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return theObject;
	}

	public void setProperty(String configPath, String property, String value) {
		try {

			com.sun.star.beans.PropertyValue aPathArgument = new com.sun.star.beans.PropertyValue();
			aPathArgument.Name = "nodepath";
			aPathArgument.Value = configPath;

			com.sun.star.beans.PropertyValue aModeArgument = new com.sun.star.beans.PropertyValue();
			aModeArgument.Name = "EnableAsync";
			aModeArgument.Value = new Boolean(true);

			Object[] args = new Object[2];
			args[0] = aPathArgument;
			args[1] = aModeArgument;

			Object xViewRoot = this.configProvider.createInstanceWithArguments(
					CONFIGURATION_UPDATABLE_VIEW, args);

			XNameAccess props = (XNameAccess) UnoRuntime.queryInterface(
					XNameAccess.class, xViewRoot);

			if (props.hasByName(property)) {
				XPropertySet properties = (XPropertySet) UnoRuntime
						.queryInterface(XPropertySet.class, xViewRoot);

				properties.setPropertyValue(property, value);
			} else {
				// get the container
				XNameContainer setUpdate = (XNameContainer) UnoRuntime
						.queryInterface(XNameContainer.class, xViewRoot);

				// create a new detached set element (instance of
				// DataSourceDescription)
				XSingleServiceFactory elementFactory = (XSingleServiceFactory) UnoRuntime
						.queryInterface(XSingleServiceFactory.class, setUpdate);

				// the new element is the result !
				Object prop = elementFactory.createInstance();
				// insert it - this also names the element
				setUpdate.insertByName(property, prop);
			}

			// commit the changes
			this.commit(xViewRoot);

			// now clean up
			((XComponent) UnoRuntime
					.queryInterface(XComponent.class, xViewRoot)).dispose();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * Commit the XChangeBatch control
	 * 
	 * @param root
	 */
	public void commit(Object root) {
		try {
			XChangesBatch xUpdateControl = (XChangesBatch) UnoRuntime
					.queryInterface(XChangesBatch.class, root);
			xUpdateControl.commitChanges();
		} catch (WrappedTargetException e) {
			e.printStackTrace();
		}
	}

	public void dispose(Object obj) {
		((XComponent) UnoRuntime.queryInterface(XComponent.class, obj))
				.dispose();
	}
}
