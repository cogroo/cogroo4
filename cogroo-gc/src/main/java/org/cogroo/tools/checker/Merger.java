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
package org.cogroo.tools.checker;

import org.cogroo.entities.impl.MorphologicalTag;

import org.cogroo.tools.checker.rules.model.TagMask;
import org.cogroo.tools.checker.rules.model.TagMask.Class;
import org.cogroo.tools.checker.rules.model.TagMask.Gender;
import org.cogroo.tools.checker.rules.model.TagMask.Number;

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
    if (allTags != null && allTags.length >= 1
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
