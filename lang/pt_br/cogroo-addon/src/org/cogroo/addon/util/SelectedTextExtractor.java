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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cogroo.addon.util;

import com.sun.star.container.XIndexAccess;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.text.XTextDocument;
import com.sun.star.text.XTextRange;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.view.XSelectionSupplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author colen
 */
public class SelectedTextExtractor {
    /** New line separator */
    public static final String NEWLINE = System.getProperty("line.separator");
    /** OOo component context for showing a dialog */
    private XComponentContext xcomponentcontext;
    /** OOo Writer text document */
    private XTextDocument xtextdocument;

    public SelectedTextExtractor(XComponentContext xcomponentcontext, XTextDocument xtextdocument) {
        this.xcomponentcontext = xcomponentcontext;
        this.xtextdocument = xtextdocument;
    }

    private XTextRange getRegiaoSelecionada() throws IndexOutOfBoundsException {
        // Get all selected regions of the document
        XSelectionSupplier xSelectionSupplier = (XSelectionSupplier) UnoRuntime.queryInterface(XSelectionSupplier.class, xtextdocument.getCurrentController());
        XIndexAccess xIndexAccess = (XIndexAccess) UnoRuntime.queryInterface(XIndexAccess.class, xSelectionSupplier.getSelection());

        int count = xIndexAccess.getCount();

        XTextRange regiaoSelecionada = null;

        if (count > 0) {
            try {
                // Get text inside the first selected region
                regiaoSelecionada = (XTextRange) UnoRuntime.queryInterface(XTextRange.class, xIndexAccess.getByIndex(0));
            } catch (WrappedTargetException ex) {
                Logger.getLogger(SelectedTextExtractor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return regiaoSelecionada;
    }

    private String getTextoSelecionado(XTextRange regiaoSelecionada) {
        return regiaoSelecionada.getString();
    }

    private void setTextoSelecionado(XTextRange regiaoSelecionada, String texto) {
        regiaoSelecionada.setString(texto);
    }

    public String getSelectedText() {
        XTextRange regiaoSelecionada;
        try {
            regiaoSelecionada = getRegiaoSelecionada();
            return getTextoSelecionado(regiaoSelecionada);
        } catch (IndexOutOfBoundsException ex) {
            Logger.getLogger(SelectedTextExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
}
