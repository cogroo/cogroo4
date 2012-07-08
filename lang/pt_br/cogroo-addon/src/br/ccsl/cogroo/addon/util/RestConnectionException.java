/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ccsl.cogroo.addon.util;

import br.ccsl.cogroo.addon.CogrooException;

/**
 *
 * @author colen
 */
public class RestConnectionException extends CogrooException{

    public RestConnectionException(String aMessageKey, Object[] aArguments,
          Throwable aCause) {
        super(aMessageKey, aArguments, aCause);
    }

    public RestConnectionException(String aMessageKey, Object[] aArguments) {
        super(aMessageKey, aArguments);
    }
    
}
