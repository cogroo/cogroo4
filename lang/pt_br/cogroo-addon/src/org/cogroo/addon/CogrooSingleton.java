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
package org.cogroo.addon;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cogroo.addon.addon.conf.AddOnConfiguration;
import org.cogroo.analyzer.ComponentFactory;
import org.cogroo.analyzer.Pipe;
import org.cogroo.checker.CheckDocument;
import org.cogroo.checker.GrammarCheckerAnalyzer;
import org.cogroo.entities.Mistake;

import org.cogroo.tools.checker.rules.model.Rule;

import com.sun.star.uno.XComponentContext;

public class CogrooSingleton {

  private static Pipe COGROO;
  private static GrammarCheckerAnalyzer gca;
  private static CogrooSingleton instance = null;

  // Logger
  protected static Logger LOGGER = LoggerImpl.getLogger(CogrooSingleton.class
      .getCanonicalName());
  private XComponentContext context;

  private CogrooSingleton() {
    // prevents instantiation
  }

  public static synchronized CogrooSingleton getInstance(
      XComponentContext context) {

    if (instance == null) {
      instance = new CogrooSingleton();
      instance.init(context);
    }
    return instance;
  }

  private String root = null;
  private final Object flag = new Object();

  private String getRoot() {
    if (root == null) {
      synchronized (flag) {
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
      // RulesProperties.setRootFolder(instance.getRoot());
      ComponentFactory factory = ComponentFactory
          .create(new Locale("pt", "BR"));
      COGROO = (Pipe) factory.createPipe();
      
      gca = new GrammarCheckerAnalyzer();
      
      COGROO.add(gca);

    } catch (Throwable e) {
      LOGGER.log(Level.SEVERE, "Error in CoGrOO initialization.", e);
    }

  }

  public synchronized int checkSentence(final String paraText,
      List<Mistake> outMistakes) {
    int end = -1;
    try {
      CheckDocument document = new CheckDocument();
      document.setText(paraText);

      COGROO.analyze(document);
      if (document.getSentences() != null && document.getSentences().size() > 0) {
        end = document.getSentences().get(0).getEnd();
        outMistakes.addAll(document.getMistakes());
      }
    } catch (Throwable e) {
      LOGGER.log(Level.SEVERE, "Internal error.", e);
    }
    return end;
  }

  public synchronized void ignoreRule(final String ruleidentifier) {
    if (LOGGER.isLoggable(Level.FINE)) {
      LOGGER.fine("Will add rule to ignored list: " + ruleidentifier);
    }
    gca.ignoreRule(ruleidentifier);
  }

  public synchronized void resetIgnoredRules() {
    if (LOGGER.isLoggable(Level.FINE)) {
      LOGGER.fine("Will reset ignored rule list.");
    }
    gca.resetIgnoredRules();
  }

  private List<Rule> rules = null;

  public String[] getCategories() {
    String[] ret = null;
//    try {
//      if (rules == null) {
//        synchronized (flag) {
//
//          rules = new RulesContainerHelper(getRoot())
//              .getContainerForXMLAccess().getComponent(RulesProvider.class)
//              .getRules().getRule();
//        }
//      }
//      SortedSet cat = new TreeSet<String>();
//      for (Rule r : rules) {
//        cat.add(r.getGroup());
//      }
//      ret = (String[]) cat.toArray(new String[cat.size()]);
//    } catch (Exception e) {
//      LOGGER.log(Level.SEVERE, "Internal error.", e);
//    }
//
    return ret;
  }
}
