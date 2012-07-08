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
package br.ccsl.cogroo.addon.addon.conf;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.logging.Level;

import com.sun.star.uno.AnyConverter;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.util.XMacroExpander;
import java.io.File;
import java.net.URI;
import java.util.logging.Logger;

public class AddOnConfiguration extends DefaultConfiguration {

    public static final String CONFIGURATION_ROOT_NODE = "/br.ccsl.cogroo.addon.CoGrOOImportFilter/Templates";
    public static final String CONFIGURATION_NODE_TEMPLATE_REPOSITORIES = "TemplateRepositories";
    public static final String CONFIGURATION_PROPERTY_STORE_LOCAL = "store.local";
    public static final String CONFIGURATION_PROPERTY_ADDON_HOME = "cogroo.ooointegration.addon.home";
    public static final String CONFIGURATION_PATH_SEPARATOR = "/";
    private XComponentContext context;

    public AddOnConfiguration(XComponentContext context) {
        super(context);
        this.context = context;
    }

    /**
     *
     * @param url
     * @return the expanded URL
     */
    protected String expandURL(String url) {
        try {
            url = url.substring(20);

            String str = URLDecoder.decode(url, "utf-8");

            Object obj = context.getValueByName("/singletons/com.sun.star.util.theMacroExpander");
            XMacroExpander expander = (XMacroExpander) UnoRuntime.queryInterface(XMacroExpander.class, obj);

            String res = expander.expandMacros(str);

            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getDataURL() {
        try {
            Object obj = this.getProperty(CONFIGURATION_ROOT_NODE,
                    CONFIGURATION_PROPERTY_ADDON_HOME);
            String url = expandURL(AnyConverter.toString(obj));

            return url;
        } catch (Exception e) {
            Logger.getLogger(AddOnConfiguration.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    public File getDataFolder() {
        try {
            String dataURL = getDataURL();
            if (dataURL == null) {
                return null;
            }
            return new File(new URI(dataURL));
        } catch (URISyntaxException ex) {
            Logger.getLogger(AddOnConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String getRootURL() {
        try {
            File rootFolder = getRootFolder();
            if (rootFolder == null) {
                return null;
            }
            String rootURL = rootFolder.toURI().toString();
            return rootURL.replaceFirst("file:/", "file:///");
        } catch (Exception ex) {
            Logger.getLogger(AddOnConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public File getRootFolder() { 
        try {
            String dataFolder = getDataFolder().getAbsolutePath();
            if (dataFolder == null) {
                return null;
            }
            File rootFolder = new File(dataFolder + "/../../../../../../../../");
            return rootFolder.getCanonicalFile();
        } catch (IOException ex) {
            Logger.getLogger(AddOnConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
