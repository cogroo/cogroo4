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

package br.ccsl.cogroo.tools.checker;

import br.ccsl.cogroo.entities.impl.MorphologicalTag;
import br.ccsl.cogroo.tools.checker.rules.model.TagMask;
import br.ccsl.cogroo.tools.checker.rules.model.TagMask.Class;
import br.ccsl.cogroo.tools.checker.rules.model.TagMask.Gender;
import br.ccsl.cogroo.tools.checker.rules.model.TagMask.Number;

public class Merger {

  private static TagMask toTagMask(MorphologicalTag tag) {
    TagMask mask = new TagMask();
    if (tag.getClazzE() != null) {
      mask.setClazz(tag.getClazzE());
    }

    if (tag.getGenderE() != null) {
      mask.setGender(tag.getGenderE());
    }

    if (tag.getNumberE() != null) {
      mask.setNumber(tag.getNumberE());
    }

    if (tag.getCase() != null) {
      mask.setCase(tag.getCase());
    }

    if (tag.getPersonE() != null) {
      mask.setPerson(tag.getPersonE());
    }

    if (tag.getTense() != null) {
      mask.setTense(tag.getTense());
    }

    if (tag.getMood() != null) {
      mask.setMood(tag.getMood());
    }

    if (tag.getPunctuation() != null) {
      mask.setPunctuation(tag.getPunctuation());
    }

    return mask;
  }

  public static void generalizePOSTags(MorphologicalTag tag,
      MorphologicalTag[] allTags) {

    // this is generally true, we set gender neutral to avoid false positives.
    if (tag.getClazzE().equals(Class.NUMERAL)) {
      tag.setGender(Gender.NEUTRAL);
    }

    if (allTags == null || allTags.length == 0) {
      // lets try to generalize gender and number....
      if (tag.getGenderE() != null) {
        tag.setGender(Gender.NEUTRAL);
      }
      if (tag.getNumberE() != null) {
        tag.setNumber(Number.NEUTRAL);
      }
    } else
    // now we try we check if the "tag" can be generalized in terms of gender
    // and number
    if (allTags != null && allTags.length > 1
        && (tag.getGenderE() != null || tag.getNumberE() != null)) {
      TagMask noGender = toTagMask(tag);
      noGender.setGender(null);

      TagMask noNumber = toTagMask(tag);
      noGender.setGender(null);

      boolean is2g = false;
      boolean isSP = false;

      for (MorphologicalTag test : allTags) {
        if (test.match(noGender)) {
          if (test.getGenderE() != null
              && !test.getGenderE().equals(tag.getGenderE())) {
            is2g = true;
            if (isSP) {
              break;
            }
          }
        }
        if (test.getNumberE() != null && test.match(noNumber)) {
          if (!test.getNumberE().equals(tag.getNumberE())) {
            isSP = true;
            if (is2g) {
              break;
            }
          }
        }
      }

      if (is2g) {
        tag.setGender(Gender.NEUTRAL);
      }
      if (isSP) {
        tag.setNumber(Number.NEUTRAL);
      }
    }
  }

}
