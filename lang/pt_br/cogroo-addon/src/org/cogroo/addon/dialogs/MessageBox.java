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
package org.cogroo.addon.dialogs;

import com.sun.star.awt.Rectangle;
import com.sun.star.awt.XMessageBox;
import com.sun.star.awt.XMessageBoxFactory;
import com.sun.star.awt.XWindow;
import com.sun.star.awt.XWindowPeer;
import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XFrame;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

public class MessageBox {

    private XMultiComponentFactory m_xMCF;
    private final XComponentContext m_xContext;

    public MessageBox(XMultiComponentFactory _xMCF,  XComponentContext _xContext) {
        this.m_xMCF = _xMCF;
        this.m_xContext = _xContext;
    }
    
    public MessageBox(XComponentContext _xContext) {
        this.m_xMCF = _xContext.getServiceManager();
        this.m_xContext = _xContext;
    }
    


    /** Shows an messagebox
     * @param _xParentWindowPeer the windowpeer of the parent window
     * @param _sTitle the title of the messagebox
     * @param _sMessage the message of the messagebox
     * @param _aType string which determines the message box type: (infobox|warningbox|errorbox|querybox|messbox)
     * @param _aButtons MessageBoxButtons which buttons should be available on the message box
     */
    public short showMessageBox(XWindowPeer _xParentWindowPeer, String _sTitle, String _sMessage, String _aType, int _aButtons) {
        short nResult = -1;
        XComponent xComponent = null;
        try {
            Object oToolkit = m_xMCF.createInstanceWithContext("com.sun.star.awt.Toolkit", m_xContext);
            XMessageBoxFactory xMessageBoxFactory = (XMessageBoxFactory) UnoRuntime.queryInterface(XMessageBoxFactory.class, oToolkit);
            // rectangle may be empty if position is in the center of the parent peer
            Rectangle aRectangle = new Rectangle();
            XMessageBox xMessageBox = xMessageBoxFactory.createMessageBox(_xParentWindowPeer, aRectangle, _aType, _aButtons, _sTitle, _sMessage);
            xComponent = (XComponent) UnoRuntime.queryInterface(XComponent.class, xMessageBox);
            if (xMessageBox != null) {
                nResult = xMessageBox.execute();
            }
        } catch (com.sun.star.uno.Exception ex) {
            ex.printStackTrace(System.out);
        } finally {
            //make sure always to dispose the component and free the memory!
            if (xComponent != null) {
                xComponent.dispose();
            }
        }
        return nResult;
    }

    public short showMessageBox(XFrame _xFrame, String _sTitle, String _sMessage, String _aType, int _aButtons) {
        XWindow xWindow = _xFrame.getContainerWindow();
        XWindowPeer xWindowPeer = (XWindowPeer) UnoRuntime.queryInterface(XWindowPeer.class, xWindow);
        return showMessageBox(xWindowPeer, _sTitle, _sMessage, _aType, _aButtons);
    }

    public short showMessageBox(String _sTitle, String _sMessage, String _aType, int _aButtons) {
        return showMessageBox(getCurrentFrame(), _sTitle, _sMessage, _aType, _aButtons);
    }

    public XFrame getCurrentFrame(){
        XFrame xRetFrame = null;
        try {
          Object oDesktop = m_xMCF.createInstanceWithContext("com.sun.star.frame.Desktop", m_xContext);
          XDesktop xDesktop = (XDesktop) UnoRuntime.queryInterface(XDesktop.class, oDesktop);
          xRetFrame = xDesktop.getCurrentFrame();
        } catch (com.sun.star.uno.Exception ex) {
          ex.printStackTrace();
        }
      return xRetFrame;
    }
}
