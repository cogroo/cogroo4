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

import java.util.ArrayList;
import java.util.List;

import br.ccsl.cogroo.entities.Tag;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Case;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Class;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Gender;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Mood;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Number;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Person;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Punctuation;
import br.usp.pcs.lta.cogroo.tools.checker.rules.model.TagMask.Tense;


/**
 * Implements a {@link Tag} for POS tagging annotation
 * 
 * @author William Colen
 *
 */
public class MorphologicalTag extends Tag implements Cloneable {

	private static final long serialVersionUID = -2138045068621137338L;
	
	//LegacyTagInterpreter interpreter = new LegacyTagInterpreter();
	
	private Class clazz;
	private Gender gender;
	private Number number;
	private Case caze;
	private Person person;
	private Tense tense;
	private Mood mood;
	private Punctuation punctuation;
//	private Finiteness finiteness;

//	public MorphologicalTag( )
//	{
//		super("");
//	}
//	
//	public MorphologicalTag(String tagAsString) {
//		super(tagAsString);		
//	}
	
	public Class getClazzE() {
		return this.clazz;
	}

	public void setClazz(Class theClass) {
		this.clazz = theClass;
	}
	
	public Gender getGenderE() {
		return this.gender;
	}

	public void setGender(Gender value)
	{
		this.gender = value;
	}
	
	public Case getCase() {
		return this.caze;
	}
	
	public void setCase (Case caze) {
		this.caze = caze;
	}
	
	public Mood getMood() {
		return this.mood;
	}
	
	public void setMood (Mood mood) {
		this.mood = mood;
	}
	
	public Tense getTense() {
		return this.tense;
	}
	
	public void setTense (Tense tense) {
		this.tense = tense;
	}
	
	
	public Punctuation getPunctuation() {
		return this.punctuation;
	}
	
	public void setPunctuation (Punctuation punctuation) {
		this.punctuation = punctuation;
	}
	
	public Number getNumberE() {
		return this.number;
	}
	
	public void setNumber(Number value)
	{
		this.number = value;
	}

	public Person getPersonE() {
		return this.person;
	}

	public void setPerson(Person thePerson) {
		this.person = thePerson;
	}
	
//	public Finiteness getFinitenessE() {
//		return this.finiteness;
//	}
//	
//	public void setFiniteness(Finiteness theFiniteness) {
//		this.finiteness = theFiniteness;
//	}
	
	public boolean match(MorphologicalTag tag) {

		if(tag.getClazzE() != null ^ this.getClazzE() != null) {
			return false;
		} else if (this.getClazzE() != null) {
			if( this.getClazzE() != tag.getClazzE() ) return false;
		}
		
		if(tag.getGenderE() != null ^ this.getGenderE() != null) {
			return false;
		} else if (this.getGenderE() != null) {
			if(dontMatch(tag.getGenderE())) return false;
		}

		if(tag.getNumberE() != null ^ this.getNumberE() != null) {
			return false;
		} else if (this.getNumberE() != null) {
			
			if(dontMatch(tag.getNumberE())) return false;
		}

		if(tag.getCase() != null ^ this.getCase() != null) {
			return false;
		} else if (this.getCase() != null) {
			if( this.getCase() != tag.getCase() ) return false;
		}
		//Person.FIRST_THIRD; Person.NONE_FIRST_THIRD
		
		if(tag.getPersonE() != null ^ this.getPersonE() != null) {
			return false;
		} else if (this.getPersonE() != null) {
			if( dontMatch(tag.getPersonE()) ) return false;
		}

		if(tag.getTense() != null ^ this.getTense() != null) {
			return false;
		} else if (this.getTense() != null) {
			if( this.getTense() != tag.getTense() ) return false;
		}
		
		if(tag.getMood() != null ^ this.getMood() != null) {
			return false;
		} else if (this.getMood() != null) {
			if( this.getMood() != tag.getMood() ) return false;
		}
		
		if(tag.getPunctuation() != null ^ this.getPunctuation() != null) {
			return false;
		} else if (this.getPunctuation() != null) {
			if( this.getPunctuation() != tag.getPunctuation() ) return false;
		}
//		if(tag.getFinitenessE() != null ^ this.getFinitenessE() != null) {
//			return false;
//		} else if (this.getFinitenessE() != null) {
//			if( this.getFinitenessE() != tag.getFinitenessE() ) return false;
//		}
		return true;
	}
	
	private boolean dontMatch(Gender g) {
		return this.getGenderE() != g && !(this.getGenderE() == Gender.NEUTRAL || g == Gender.NEUTRAL);
	}
	
	private boolean dontMatch(Number g) {
		return this.getNumberE() != g && !(this.getNumberE() == Number.NEUTRAL || g == Number.NEUTRAL);
	}
	
	private boolean dontMatch(Person mp) {
		Person tp = this.getPersonE();
		//Person.FIRST_THIRD; Person.NONE_FIRST_THIRD
		//  this     mask
		//  1        1/3 0/1/3
		
		boolean matchFirst = (tp == Person.FIRST || tp == Person.FIRST_THIRD || tp == Person.NONE_FIRST_THIRD) &&
								(mp == Person.FIRST || mp == Person.FIRST_THIRD || mp == Person.NONE_FIRST_THIRD);
		
		boolean thirdFirst = (tp == Person.THIRD || tp == Person.FIRST_THIRD || tp == Person.NONE_FIRST_THIRD) &&
		(mp == Person.THIRD || mp == Person.FIRST_THIRD || mp == Person.NONE_FIRST_THIRD);
		
		boolean neutral = tp == Person.NONE_FIRST_THIRD || mp == Person.NONE_FIRST_THIRD;
		
		return !( mp == tp || matchFirst || thirdFirst || neutral);
	}
	
	// restricted means that match will fail if the mask has a property that this tag doesn't
	public boolean match(TagMask tagMask, boolean restricted)
	{
		if(tagMask.getClazz() != null) {
			if( ( this.getClazzE() != null || restricted ) && this.getClazzE() != tagMask.getClazz()) 
				return false;
		}
		
		if(tagMask.getGender() != null) {
			if( ( this.getGenderE() != null || restricted ) && dontMatch(tagMask.getGender())) 
				return false;
		}
		
		if(tagMask.getNumber() != null) {
			if( ( this.getNumberE() != null || restricted ) && dontMatch(tagMask.getNumber())) 
				return false;
		}
		
		if(tagMask.getCase() != null) {
			if( ( this.getCase() != null || restricted ) && this.getCase() != tagMask.getCase()) 
				return false;
		}
		
		if(tagMask.getPerson() != null) {
			if( ( this.getPersonE() != null || restricted ) && dontMatch(tagMask.getPerson())) 
				return false;
		}
		
		if(tagMask.getTense() != null) {
			if( ( this.getTense() != null || restricted ) && this.getTense() != tagMask.getTense()) 
				return false;
		}
		
		if(tagMask.getMood() != null) {
			if( ( this.getMood() != null || restricted ) && this.getMood() != tagMask.getMood()) 
				return false;
		}
		
//		if(tagMask.getFiniteness() != null) {
//			if( ( this.getFinitenessE() != null || restricted ) && this.getFinitenessE() != tagMask.getFiniteness()) 
//				return false;
//		}
		
		if(tagMask.getPunctuation() != null) {
			if( ( this.getPunctuation() != null || restricted ) && this.getPunctuation() != tagMask.getPunctuation()) 
				return false;
		}
	
		return true;		
	}

	@Override
	public boolean match(TagMask tagMask) {
		return match(tagMask, true);
	}
	

	// with restricted true, match fails if the tag does not have the property.
	public boolean matchExact(TagMask tagMask, boolean restricted) {
	       if(tagMask.getClazz() != null || restricted ) {
	            if( this.getClazzE() != tagMask.getClazz()) 
	                return false;
	        }
	        
	        if(tagMask.getGender() != null || restricted ) {
	            if( this.getGenderE() != tagMask.getGender() /*&& !Gender.NEUTRAL.equals(this.getGenderE())*/) 
	                return false;
	        }
	        
	        if(tagMask.getNumber() != null || restricted ) {
	            if( this.getNumberE() != tagMask.getNumber() /*&& !Number.NEUTRAL.equals(this.getNumberE())*/) 
	                return false;
	        }
	        
	        if(tagMask.getCase() != null || restricted ) {
	            if( this.getCase() != tagMask.getCase()) 
	                return false;
	        }
	        
	        if(tagMask.getPerson() != null || restricted ) {
	            if( this.getPersonE() != tagMask.getPerson()) 
	                return false;
	        }
	        
	        if(tagMask.getTense() != null || restricted ) {
	            if( this.getTense() != tagMask.getTense()) 
	                return false;
	        }
	        
	        if(tagMask.getMood() != null || restricted ) {
	            if( this.getMood() != tagMask.getMood()) 
	                return false;
	        }
	        
//	        if(tagMask.getFiniteness() != null || restricted ) {
//	            if( this.getFinitenessE() != tagMask.getFiniteness()) 
//	                return false;
//	        }
	        
	        if(tagMask.getPunctuation() != null || restricted ) {
	            if( this.getPunctuation() != tagMask.getPunctuation()) 
	                return false;
	        }
	    
	        return true;
	    }

	@Override
	public String toVerboseString() {
		return toString();
	}
	
	public List<String> getAsTagList() {
		List<String> res = new ArrayList<String>();
		
		if(this.getClazzE() != null) {
			res.add(this.getClazzE().toString() );
		}
		
		if(this.getGenderE() != null) {
			res.add(this.getGenderE().toString() );
		}
		
		if(this.getNumberE() != null) {
			res.add(this.getNumberE().toString() );
		}
		
		if(this.getCase() != null) {
			res.add(this.getCase().toString() );
		}
		
		if(this.getPersonE() != null) {
			res.add(this.getPersonE().toString() );
		}
		
		if(this.getTense() != null) {
			res.add(this.getTense().toString() );
		}
		
		if(this.getMood() != null) {
			res.add(this.getMood().toString() );
		}
		
//		if(this.getFinitenessE() != null) {
//			res.add(this.getFinitenessE().toString() );
//		}
		
		if(this.getPunctuation() != null) {
			res.add(this.getPunctuation().toString() );
		}
		
		return res;
	}

	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();
		if(this.getClazzE() != null) {
			res.append(this.getClazzE().value() + ",");
		}
		
		if(this.getGenderE() != null) {
			res.append(this.getGenderE().value() + ",");
		}
		
		if(this.getNumberE() != null) {
			res.append(this.getNumberE().value() + ",");
		}
		
		if(this.getCase() != null) {
			res.append(this.getCase().value() + ",");
		}
		
		if(this.getPersonE() != null) {
			res.append(this.getPersonE().value() + ",");
		}
		
		if(this.getTense() != null) {
			res.append(this.getTense().value() + ",");
		}
		
		if(this.getMood() != null) {
			res.append(this.getMood().value() + ",");
		}
		
//		if(this.getFinitenessE() != null) {
//			res.append(this.getFinitenessE().value() + ",");
//		}
		
		if(this.getPunctuation() != null) {
			res.append(this.getPunctuation().value() + ",");
		}
		
		if( res.length() > 0 ) {
			return res.substring(0, res.length() - 1);
		} else {
			return null;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		final MorphologicalTag other = (MorphologicalTag) obj;
		return this.match(other);
	}
	
	@Override
	public MorphologicalTag clone() {
		try {
			return (MorphologicalTag) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
		
	}
	
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
}
