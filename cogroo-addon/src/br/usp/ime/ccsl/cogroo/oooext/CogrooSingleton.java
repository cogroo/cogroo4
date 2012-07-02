/**
 * Copyright (C) 2008 CoGrOO Team (cogroo AT gmail DOT com)
 * 
 * CoGrOO Team (cogroo AT gmail DOT com)
 * LTA, PCS (Computer and Digital Systems Engineering Department),
 * Escola Politécnica da Universidade de São Paulo
 * Av. Prof. Luciano Gualberto, trav. 3, n. 380
 * CEP 05508-900 - São Paulo - SP - BRAZIL
 * 
 * http://cogroo.sourceforge.net/
 * 
 * This file is part of CoGrOO.
 * 
 * CoGrOO is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public as published by 
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * CoGrOO is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with CoGrOO. If not, see <http://www.gnu.org/licenses/>.
 */
package br.usp.ime.ccsl.cogroo.oooext;

import java.io.File;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.usp.ime.ccsl.cogroo.oooext.addon.conf.AddOnConfiguration;
import br.usp.pcs.lta.cogroo.configuration.LegacyRuntimeConfiguration;
import br.usp.pcs.lta.cogroo.configuration.RuntimeConfigurationI;
import br.usp.pcs.lta.cogroo.entity.Mistake;
import br.usp.pcs.lta.cogroo.grammarchecker.Cogroo;
import br.usp.pcs.lta.cogroo.tools.checker.rules.applier.RulesProvider;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.Rule;
import br.usp.pcs.lta.cogroo.tools.checker.rules.util.RulesContainerHelper;

import com.sun.star.uno.XComponentContext;

public class CogrooSingleton {

    private static Cogroo COGROO;
    private static CogrooSingleton instance = null;

    // Logger
    protected static Logger LOGGER = LoggerImpl.getLogger(CogrooSingleton.class.getCanonicalName());
    private RuntimeConfigurationI cogrooConfig;
    private XComponentContext context;

    private CogrooSingleton() {
        // prevents instantiation
    }

    public static synchronized CogrooSingleton getInstance(XComponentContext context) {

        if (instance == null) {
            instance = new CogrooSingleton();
            instance.init(context);
        }
        return instance;
    }

    private String root = null;
    private final Object flag = new Object();

    private String getRoot() {
        if(root == null) {
            synchronized(flag) {
                File f;
                AddOnConfiguration config = new AddOnConfiguration(context);
                try {
                    f = config.getDataFolder();
                    root = f.getCanonicalPath();
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed getting execution path.", e);
                }
            }
        }
        return root;
    }

    private void init(XComponentContext context) {
        this.context = context;
        String home = getRoot();
        

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("Will start CoGrOO using home path: " + home);
        }

        try {
            //RulesProperties.setRootFolder(instance.getRoot());
            cogrooConfig = new LegacyRuntimeConfiguration(home);
            COGROO = new Cogroo(cogrooConfig);
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, "Error in CoGrOO initialization.", e);
        }

    }

    public synchronized int checkSentence(final String paraText, List<Mistake> outMistakes) {
       int end = -1;
        try {
           end = COGROO.checkFirstSentence(paraText, outMistakes);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Internal error.", e);
        }
        return end;
    }

    public synchronized void ignoreRule(final String ruleidentifier) {
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("Will add rule to ignored list: " + ruleidentifier);
        }
        cogrooConfig.getChecker().ignore(ruleidentifier);
    }

    public synchronized void resetIgnoredRules() {
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("Will reset ignored rule list.");
        }
        cogrooConfig.getChecker().resetIgnored();
    }

    private List<Rule> rules = null;

    public String[] getCategories(){
        String[] ret = null;
        try {
            if(rules == null) {
                synchronized(flag) {

                    rules = new RulesContainerHelper(getRoot()).getContainerForXMLAccess().getComponent(RulesProvider.class).getRules().getRule();
                }
            }
            SortedSet cat = new TreeSet<String>();
            for (Rule r : rules) {
                cat.add(r.getGroup());
            }
            ret = (String[])cat.toArray(new String[cat.size()]);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Internal error.", e);
        }
        
        return ret;
    }
}
