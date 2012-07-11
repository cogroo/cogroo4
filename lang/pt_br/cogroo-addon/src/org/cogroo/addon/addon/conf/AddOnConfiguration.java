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
package org.cogroo.addon.addon.conf;

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

    public static final String CONFIGURATION_ROOT_NODE = "/org.cogroo.addon.CoGrOOImportFilter/Templates";
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
            File rootFolder = new File(dataFolder + "/../../../../../");
            return rootFolder.getCanonicalFile();
        } catch (IOException ex) {
            Logger.getLogger(AddOnConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
