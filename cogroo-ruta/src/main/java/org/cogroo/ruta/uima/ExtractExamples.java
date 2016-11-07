package org.cogroo.ruta.uima;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import opennlp.tools.cmdline.TerminateToolException;

import org.cogroo.analyzer.ComponentFactory;
import org.cogroo.analyzer.InitializationException;
import org.cogroo.checker.GrammarChecker;
import org.cogroo.tools.checker.RuleDefinition;
import org.cogroo.tools.checker.rules.model.Example;

public class ExtractExamples {
	
	public static void main(String[] args) {

	    ComponentFactory factory = null;
	    try {
	      factory = ComponentFactory.create(new Locale("pt", "BR"));
	    } catch(InitializationException e) {
	      e.printStackTrace();
	    }
	    GrammarChecker cogroo;
	    try {
	      cogroo = new GrammarChecker(factory.createPipe());
	    } catch(IOException e) {
	      e.printStackTrace();
	      throw new TerminateToolException(1, "Could not create pipeline!");
	    }

	    printExamples(cogroo.getRuleDefinitions(), true);
	    
	}
	
	 private static void printExamples(Collection<RuleDefinition> rulesDefinition, boolean correct) {
		    
		    for (RuleDefinition def : rulesDefinition) {
		    	StringBuilder tp = new StringBuilder();
		    	
		    	for (Example ex : def.getExamples()) {
		          tp.append(ex.getIncorrect()).append("\n");
		      }
		    	tp.append("\n");
		    	for (Example ex : def.getExamples()) {
			          tp.append(ex.getCorrect()).append("\n");
			      }
		    	String id = pad_zero(def.getId());
		    	write("exemplos/rule_" + id + ".txt", tp.toString());
		    	
		      	
		      for (Example ex : def.getExamples()) {
		        if(correct) {
		          System.out.println(ex.getCorrect());
		        } else {
		          System.out.println(ex.getIncorrect());
		        } 
		      }
		    }
		  }

	 private static final Pattern pattern = Pattern.compile(".*?(\\d+)"); 
	private static String pad_zero(String id) {
		Matcher m = pattern.matcher(id);
		if(m.matches()) {
			String theMatch = m.group(1);
			id = id.replace(theMatch, String.format("%03d", Integer.parseInt(theMatch)));
		}
		return id;
	}

	private static void write(String file, String data) {
		Writer writer = null;

    	try {
    	    writer = new BufferedWriter(new OutputStreamWriter(
    	          new FileOutputStream(file), "utf-8"));
    	    writer.write(data);
    	} catch (IOException ex) {
    	  // report
    	} finally {
    	   try {writer.close();} catch (Exception ex) {}
    	}
	}

}
