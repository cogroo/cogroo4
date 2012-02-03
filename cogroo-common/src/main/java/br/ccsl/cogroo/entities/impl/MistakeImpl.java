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

package br.ccsl.cogroo.entities.impl;

import java.io.Serializable;
import java.util.List;

import br.ccsl.cogroo.entities.Mistake;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.Example;

/**
 * Implements a {@link Mistake} that represents grammar errors.
 * 
 * @author Marcelo Suzumura
 * 
 */
public class MistakeImpl implements Mistake, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6496020677021379831L;
	
	private int id;
	
	private String identifier;

	private String longMessage;
	
	private String shortMessage;
	
	private String fullMessage;

	private String[] suggestions;

	private int start;

	private int end;
	
	@Deprecated
	public MistakeImpl(int id, String message, String shortMessage, String[] suggestions, int start, int end, List<Example> examples) {
		this(Integer.toString(id), message, shortMessage, suggestions, start, end, examples);
		this.id = id;
	}
	
	public MistakeImpl(String id, String message, String shortMessage, String[] suggestions, int start, int end, List<Example> examples) {
		this.identifier = id;
		
		if(shortMessage == null || shortMessage.length() == 0)
		{
			if(message.length() > 80)
				this.shortMessage = message.subSequence(0, 80).toString() + " (...)";
			else
				this.shortMessage = message;
		}
		else
		{
			this.shortMessage = shortMessage;
		}
		
		this.longMessage = message;
		
		
		this.suggestions = suggestions;
		this.start = start;
		this.end = end;
		
		if(examples != null && examples.size() > 0)
		{
			StringBuffer sb = new StringBuffer(message + "\n");		
			sb.append("Exemplos:\n");
			for (Example example : examples) {
				sb.append("  Incorreto: \t" + example.getIncorrect() + "\n");
				sb.append("  Correto:   \t" + example.getCorrect() + "\n");
			}
			
			this.fullMessage = sb.toString();
		}
		else
		{
			this.fullMessage = this.longMessage;
		}
	}
	
	@Deprecated
	public int getId() {
		return this.id;
	}
	
	public String getLongMessage() {
		return this.longMessage;
	}

	public String getShortMessage() {
		return this.shortMessage;
	}
	
	public String getFullMessage() {
		return this.fullMessage;
	}

	public String[] getSuggestions() {
		return this.suggestions;
	}

	public int getStart() {
		return this.start;
	}
	
	/**
	 * @param start the start to set
	 */
	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return this.end;
	}
	
	/**
	 * @param end the end to set
	 */
	public void setEnd(int end) {
		this.end = end;
	}
	
	

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Rule [").append(this.identifier).append("]\n");
		sb.append("Mistake [").append(this.getStart()).append("..").append(this.getEnd()).append("]\n");
		sb.append("Short Message [").append(this.getShortMessage()).append("]\n");
		sb.append("Long Message  [").append(this.getLongMessage()).append("]\n");
		sb.append("Full message [").append(this.getFullMessage()).append("]\n");
		sb.append("Suggestion ");
		if (this.getSuggestions() != null && this.getSuggestions().length > 0) {
			for (String suggestion : this.getSuggestions()) {
				sb.append("[").append(suggestion).append("]");
			}
		} else {
			sb.append("[none]");
		}
		return sb.toString();
	}

	public String getRuleIdentifier() {
		return this.identifier;
	}





}
