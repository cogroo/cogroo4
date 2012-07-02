/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.ccsl.cogroo.oooext.util;

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
