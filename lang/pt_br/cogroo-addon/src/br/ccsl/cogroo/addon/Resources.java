package br.ccsl.cogroo.addon;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.star.beans.PropertyState;
import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.uno.Exception;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.util.XChangesBatch;

public class Resources {

//    public static final String CONFIG_FILE = "/cogrooAddon.xml";
//    private static Properties properties;
    private static XComponentContext m_xContext;
    private static XPropertySet m_xDemoOptions;

    private Resources() {
        // prevents instatiation
    }

    private synchronized static void init() {

        if (m_xDemoOptions == null) {
            XMultiServiceFactory xConfig;
            try {
                xConfig = (XMultiServiceFactory) UnoRuntime.queryInterface(XMultiServiceFactory.class,
                        m_xContext.getServiceManager().createInstanceWithContext("com.sun.star.configuration.ConfigurationProvider",
                        m_xContext));


                Object[] args = new Object[1];
                args[0] = new PropertyValue("nodepath", 0, "/br.ccsl.cogroo.addon.CogrooConfiguration/Options",
                        PropertyState.DIRECT_VALUE);

                m_xDemoOptions = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
                        xConfig.createInstanceWithArguments("com.sun.star.configuration.ConfigurationUpdateAccess",
                        args));
            } catch (Exception ex) {
                Logger.getLogger(Resources.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void changeContext(XComponentContext xCompContext) {
        m_xContext = xCompContext;
    }

    private static void save() {
        //Committing the changes will cause or changes to be written to the registry.
        try {
            XChangesBatch xUpdateCommit =
                    (XChangesBatch) UnoRuntime.queryInterface(XChangesBatch.class, m_xDemoOptions);
            xUpdateCommit.commitChanges();
        } catch (WrappedTargetException ex) {
            Logger.getLogger(Resources.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String getProperty(String key) {
        init();
        String ret = null;
        try {
            ret = (String) m_xDemoOptions.getPropertyValue(key);
        } catch (UnknownPropertyException ex) {
            Logger.getLogger(Resources.class.getName()).log(Level.SEVERE, null, ex);
        } catch (WrappedTargetException ex) {
            Logger.getLogger(Resources.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    public static String getProperty(String key, String defaultValue) {
        init();
        String ret = getProperty(key);
        if (ret == null) {
            ret = defaultValue;
        }
        return ret;
    }

    public static synchronized void setProperty(String key, String value) {
        init();
        try {
            m_xDemoOptions.setPropertyValue(key, value);

        } catch (UnknownPropertyException ex) {
            Logger.getLogger(Resources.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(Resources.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Resources.class.getName()).log(Level.SEVERE, null, ex);
        } catch (WrappedTargetException ex) {
            Logger.getLogger(Resources.class.getName()).log(Level.SEVERE, null, ex);
        }
        save();
    }
}
