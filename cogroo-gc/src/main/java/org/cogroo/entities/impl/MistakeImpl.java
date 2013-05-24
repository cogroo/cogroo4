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
package org.cogroo.entities.impl;

import java.io.Serializable;
import java.util.List;

import org.cogroo.entities.Mistake;

import org.cogroo.tools.checker.rules.model.Example;

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

  private String identifier;

  private String longMessage;

  private String shortMessage;

  private String fullMessage;

  private String[] suggestions;

  private String context;

  private int start;

  private int end;

  private int rulePriority;

  public MistakeImpl(String id, int priority, String message, String shortMessage,
      String[] suggestions, int start, int end, List<Example> examples,
      String text) {
    this.rulePriority = priority;
    this.identifier = id;

    if (shortMessage == null || shortMessage.length() == 0) {
      if (message.length() > 80)
        this.shortMessage = message.subSequence(0, 80).toString() + " (...)";
      else
        this.shortMessage = message;
    } else {
      this.shortMessage = shortMessage;
    }

    this.longMessage = message;

    this.suggestions = suggestions;
    this.start = start;
    this.end = end;

    if (examples != null && examples.size() > 0) {
      StringBuffer sb = new StringBuffer(message + "\n");
      sb.append("Exemplos:\n");
      for (Example example : examples) {
        sb.append("  Incorreto: \t" + example.getIncorrect() + "\n");
        sb.append("  Correto:   \t" + example.getCorrect() + "\n");
      }

      this.fullMessage = sb.toString();
    } else {
      this.fullMessage = this.longMessage;
    }

    this.setContextFromText(text);
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
   * @param start
   *          the start to set
   */
  public void setStart(int start) {
    this.start = start;
  }

  public int getEnd() {
    return this.end;
  }

  /**
   * @param end
   *          the end to set
   */
  public void setEnd(int end) {
    this.end = end;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("   Rule [").append(this.identifier).append("]\n");
    sb.append("   Mistake span [").append(this.getStart()).append("..")
        .append(this.getEnd()).append("]\n");
    if(this.context != null)
      sb.append("   Mistake text [").append(this.context.substring(getStart(), getEnd())).append("]\n");
    sb.append("   Short Message [").append(this.getShortMessage()).append("]\n");
    sb.append("   Long Message  [").append(this.getLongMessage()).append("]\n");
    sb.append("   Full message [").append(this.getFullMessage()).append("]\n");
    sb.append("   Suggestion ");
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

  public String getContext() {
    return context;
  }

  private void setContextFromText(String text) {
    if (text != null) {
      this.context = text;
    }
  }

  @Override
  public int getRulePriority() {
    return this.rulePriority;
  }

  @Override
  public void setSuggestions(String[] newSuggestions) {
    suggestions = newSuggestions;
  }

}
