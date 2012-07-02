/**
 * Copyright (C) 2008 William Colen<colen at users.sourceforge.net>
 *
 * http://lingualquanta.sourceforge.net/ooointegration
 *
 * This file is part of Lingual Quanta OpenOffice.org Integration.
 *
 * OOoIntegration is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Publicas published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OOoIntegration is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OOoIntegration.  If not, see <http://www.gnu.org/licenses/>.
 *
 * NOTICE:
 * Some peaces of the code came from other projects I studied.
 * I have to thanks the developers of:
 * 	CoGrOO
 * 		Site: cogroo.sourceforge.net
 * 		License: LGPL
 * 		Used as a sample of Grammar Checker Addon.
 * 	LinguageTool
 * 		Site: www.languagetool.org
 * 		License: LGPL
 * 		Used as a sample of Grammar Checker Addon that
 * 		implements the XGrammarChecker interface.
 *  	dxf2calc
 *  		Site: www.abj.dk/dxf2calc
 * 		License: SODA-WARE
 *  		Was used as more sofisticated sample of OOo Addon, specially
 *  		as a sample of how to find the Addon folder during runtime and
 *  		how do a nice About dialog box.
 */
package br.ccsl.cogroo.addon;

public class CogrooException extends InternationalizedException {

  public static final String STANDARD_MESSAGE_CATALOG = "CogrooException_Messages";

  /**
   * Creates a new exception with a null message.
   */
  public CogrooException() {
    super();
  }

  /**
   * Creates a new exception with the specified cause and a null message.
   *
   * @param aCause
   *          the original exception that caused this exception to be thrown, if any
   */
  public CogrooException(Throwable aCause) {
    super(aCause);
  }

  /**
   * Creates a new exception with a the specified message.
   *
   * @param aResourceBundleName
   *          the base name of the resource bundle in which the message for this exception is
   *          located.
   * @param aMessageKey
   *          an identifier that maps to the message for this exception. The message may contain
   *          placeholders for arguments as defined by the
   *          {@link java.text.MessageFormat MessageFormat} class.
   * @param aArguments
   *          The arguments to the message. <code>null</code> may be used if the message has no
   *          arguments.
   */
  public CogrooException(String aResourceBundleName, String aMessageKey, Object[] aArguments) {
    super(aResourceBundleName, aMessageKey, aArguments);
  }

  /**
   * Creates a new exception with the specified message and cause.
   *
   * @param aResourceBundleName
   *          the base name of the resource bundle in which the message for this exception is
   *          located.
   * @param aMessageKey
   *          an identifier that maps to the message for this exception. The message may contain
   *          placeholders for arguments as defined by the
   *          {@link java.text.MessageFormat MessageFormat} class.
   * @param aArguments
   *          The arguments to the message. <code>null</code> may be used if the message has no
   *          arguments.
   * @param aCause
   *          the original exception that caused this exception to be thrown, if any
   */
  public CogrooException(String aResourceBundleName, String aMessageKey, Object[] aArguments,
          Throwable aCause) {
    super(aResourceBundleName, aMessageKey, aArguments, aCause);
  }

  /**
   * Creates a new exception with a message from the {@link #STANDARD_MESSAGE_CATALOG}.
   *
   * @param aMessageKey
   *          an identifier that maps to the message for this exception. The message may contain
   *          placeholders for arguments as defined by the
   *          {@link java.text.MessageFormat MessageFormat} class.
   * @param aArguments
   *          The arguments to the message. <code>null</code> may be used if the message has no
   *          arguments.
   */
  public CogrooException(String aMessageKey, Object[] aArguments) {
    super(STANDARD_MESSAGE_CATALOG, aMessageKey, aArguments);
  }

  /**
   * Creates a new exception with the specified cause and a message from the
   * {@link #STANDARD_MESSAGE_CATALOG}.
   *
   * @param aMessageKey
   *          an identifier that maps to the message for this exception. The message may contain
   *          placeholders for arguments as defined by the
   *          {@link java.text.MessageFormat MessageFormat} class.
   * @param aArguments
   *          The arguments to the message. <code>null</code> may be used if the message has no
   *          arguments.
   * @param aCause
   *          the original exception that caused this exception to be thrown, if any
   */
  public CogrooException(String aMessageKey, Object[] aArguments, Throwable aCause) {
    super(STANDARD_MESSAGE_CATALOG, aMessageKey, aArguments, aCause);
  }
}
