package org.cogroo.tools;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.Charsets;
import org.cogroo.tools.checker.RuleDefinition;

import com.google.common.io.Resources;

public class RuleParser {

	
	private RuleParser() {
		
	}
	
	public static RuleDefinition getRuleDefinition(String fileName) {
		try {
			URL url = Resources.getResource(fileName);
			for (String line : Resources.readLines(url, Charsets.UTF_8)) {
				
				line = line.trim();
				
				if (line.length() == 0) continue;
				
				System.out.println("Uva: " + line);
				
				String[] fields = line.split("=", 2);

				if (fields.length < 2) continue;
				
				System.out.println("Campo: " + fields[0] + ", valor: " + fields[1]);
				
				// TODO: Criar um hash com myHash[field[0].trim()] = field[1].trim()
			}
			
			// TODO: return Rule(hash["id"], hash["bla"] ...)
			
			// TODO: Add cogroo-ruta/definitions to project resources.
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
