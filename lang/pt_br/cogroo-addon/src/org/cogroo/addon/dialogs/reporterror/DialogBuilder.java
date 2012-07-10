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
package org.cogroo.addon.dialogs.reporterror;

import com.sun.star.container.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cogroo.addon.LoggerImpl;


import com.sun.star.awt.XButton;
import com.sun.star.awt.XCheckBox;
import com.sun.star.awt.XComboBox;
import com.sun.star.awt.XControl;
import com.sun.star.awt.XControlContainer;
import com.sun.star.awt.XControlModel;
import com.sun.star.awt.XDialog;
import com.sun.star.awt.XFixedText;
import com.sun.star.awt.XListBox;
import com.sun.star.awt.XTextComponent;
import com.sun.star.awt.XToolkit;
import com.sun.star.awt.XWindow;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.XNameAccess;
import com.sun.star.container.XNameContainer;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.uno.Exception;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

public class DialogBuilder {
    // Logger

    protected static Logger LOGGER = LoggerImpl.getLogger(DialogBuilder.class.getCanonicalName());
    public static final int HORIZONTAL_ALIGNMENT_LEFT = 0;
    public static final int HORIZONTAL_ALIGNMENT_CENTER = 1;
    public static final int HORIZONTAL_ALIGNMENT_RIGHT = 2;
    private XMultiServiceFactory multiServiceFactory;
    private XComponentContext context;
    private Object oDialogModel;
    private XMultiComponentFactory multiComponentFactory;
    private XNameContainer nameContainer;
    private Object oUnoDialog;
    private XControl controller;
    private int tabcount = 0;
    protected XNameContainer m_xDlgModelNameContainer;
    protected XControlContainer m_xDlgContainer;
    private XMultiServiceFactory m_xMSFDialogModel = null;

    /**
     * Create a new dialog builder.
     *
     * @param context the OOo context
     * @param x initial horizontal positon
     * @param y initial vertical position
     * @param width initial width
     * @param height initial height
     * @param title title
     */
    public DialogBuilder(XComponentContext context, int x, int y, int width,
            int height, String title) {

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine(">>> DialogBuilder()");
        }

        this.context = context;

        try {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine(">>> DialogBuilder: will getServiceManager");
            }
            this.multiComponentFactory = this.context.getServiceManager();

            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine(">>> DialogBuilder: will dialogModel");
            }
            this.oDialogModel = multiComponentFactory.createInstanceWithContext(
                    "com.sun.star.awt.UnoControlDialogModel", this.context);
            // The named container is used to insert the created controls
            // into...
            m_xDlgModelNameContainer = (XNameContainer) UnoRuntime.queryInterface(XNameContainer.class, this.oDialogModel);

            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine(">>> DialogBuilder: will create dialogProperties");
            }
            XPropertySet dialogProperties = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, oDialogModel);

            dialogProperties.setPropertyValue("PositionX", new Integer(x));
            dialogProperties.setPropertyValue("PositionY", new Integer(y));
            dialogProperties.setPropertyValue("Width", new Integer(width));
            dialogProperties.setPropertyValue("Height", new Integer(height));
            dialogProperties.setPropertyValue("Title", title);
            dialogProperties.setPropertyValue("Sizeable", new Boolean(true));

            this.multiServiceFactory = (XMultiServiceFactory) UnoRuntime.queryInterface(XMultiServiceFactory.class,
                    this.oDialogModel);

            this.nameContainer = (XNameContainer) UnoRuntime.queryInterface(
                    XNameContainer.class, this.oDialogModel);

            this.oUnoDialog = multiComponentFactory.createInstanceWithContext(
                    "com.sun.star.awt.UnoControlDialog", this.context);

            // connect the UI with the model
            this.controller = (XControl) UnoRuntime.queryInterface(
                    XControl.class, this.oUnoDialog);

            XControlModel controlModel = (XControlModel) UnoRuntime.queryInterface(XControlModel.class, this.oDialogModel);
            this.controller.setModel(controlModel);


            // The XMultiServiceFactory of the dialogmodel is needed to instantiate the controls...
            m_xMSFDialogModel = (XMultiServiceFactory) UnoRuntime.queryInterface(XMultiServiceFactory.class, oDialogModel);

            // The scope of the control container is public...
            m_xDlgContainer = (XControlContainer) UnoRuntime.queryInterface(
                    XControlContainer.class, oUnoDialog);

        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, "Uncaught exception", e);
        }

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("<<< DialogBuilder()");
        }

    }

    public DialogBuilder(XComponentContext context, int x, int y, int width,
            int height, String title, int backgroundColor) {
        this(context, x, y, width, height, title);
        try {
            XPropertySet dialogProperties = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, oDialogModel);
            dialogProperties.setPropertyValue("BackgroundColor", new Integer(
                    backgroundColor));
        } catch (UnknownPropertyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (PropertyVetoException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (WrappedTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public XButton addButton(String title, String name, int x, int y,
            int width, int height) throws Exception {
        Object buttonModel = this.multiServiceFactory.createInstance("com.sun.star.awt.UnoControlButtonModel");
        XPropertySet buttonProperties = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, buttonModel);
        buttonProperties.setPropertyValue("PositionX", new Integer(x));
        buttonProperties.setPropertyValue("PositionY", new Integer(y));
        buttonProperties.setPropertyValue("Width", new Integer(width));
        buttonProperties.setPropertyValue("Height", new Integer(height));
        buttonProperties.setPropertyValue("Name", name);
        buttonProperties.setPropertyValue("TabIndex", new Short(
                (short) tabcount++));
        buttonProperties.setPropertyValue("Label", title);
        this.nameContainer.insertByName(name, buttonModel);

        XControlContainer controlContainer = (XControlContainer) UnoRuntime.queryInterface(XControlContainer.class, this.oUnoDialog);
        Object objectButton = controlContainer.getControl(name);
        XButton button = (XButton) UnoRuntime.queryInterface(XButton.class,
                objectButton);

        return button;
    }

    public XFixedText addLabel(String text, String name, int x, int y,
            int width, int height) throws Exception {
        return this.addLabel(text, name, x, y, width, height,
                HORIZONTAL_ALIGNMENT_LEFT);
    }

    public XFixedText addLabel(String text, String name, int x, int y,
            int width, int height, int alignment) throws Exception {
        Object labelModel = multiServiceFactory.createInstance("com.sun.star.awt.UnoControlFixedTextModel");
        XPropertySet labelProperties = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, labelModel);
        labelProperties.setPropertyValue("PositionX", new Integer(x));
        labelProperties.setPropertyValue("PositionY", new Integer(y));
        labelProperties.setPropertyValue("Width", new Integer(width));
        labelProperties.setPropertyValue("Height", new Integer(height));
        labelProperties.setPropertyValue("Align", new Short((short) alignment));
        labelProperties.setPropertyValue("Name", name);
        // labelProperties.setPropertyValue("TabIndex", new Short(
        // (short) tabcount++));
        labelProperties.setPropertyValue("Label", text);
        // insert the control models into the dialog model
        this.nameContainer.insertByName(name, labelModel);

        XControlContainer controlContainer = (XControlContainer) UnoRuntime.queryInterface(XControlContainer.class, this.oUnoDialog);
        Object obj = controlContainer.getControl(name);
        XFixedText field = (XFixedText) UnoRuntime.queryInterface(
                XFixedText.class, obj);

        return field;
    }

    public XFixedText addMultiLineLabel(String text, String name, int x, int y,
            int width, int height) throws Exception {
        Object labelModel = multiServiceFactory.createInstance("com.sun.star.awt.UnoControlFixedTextModel");
        XPropertySet labelProperties = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, labelModel);
        labelProperties.setPropertyValue("PositionX", new Integer(x));
        labelProperties.setPropertyValue("PositionY", new Integer(y));
        labelProperties.setPropertyValue("Width", new Integer(width));
        labelProperties.setPropertyValue("Height", new Integer(height));
        labelProperties.setPropertyValue("Name", name);
        // labelProperties.setPropertyValue("TabIndex", new Short(
        // (short) tabcount++));
        labelProperties.setPropertyValue("MultiLine", new Boolean(true));
        labelProperties.setPropertyValue("Label", text);
        // insert the control models into the dialog model
        this.nameContainer.insertByName(name, labelModel);

        XControlContainer controlContainer = (XControlContainer) UnoRuntime.queryInterface(XControlContainer.class, this.oUnoDialog);
        Object obj = controlContainer.getControl(name);
        XFixedText field = (XFixedText) UnoRuntime.queryInterface(
                XFixedText.class, obj);

        return field;
    }

    public XListBox addListBox(String[] items, String name, int x, int y,
            int width, int height) throws Exception {
        Object listModel = multiServiceFactory.createInstance("com.sun.star.awt.UnoControlListBoxModel");
        XPropertySet listProperties = (XPropertySet) UnoRuntime.queryInterface(
                XPropertySet.class, listModel);
        listProperties.setPropertyValue("PositionX", new Integer(x));
        listProperties.setPropertyValue("PositionY", new Integer(y));
        listProperties.setPropertyValue("Width", new Integer(width));
        listProperties.setPropertyValue("Height", new Integer(height));
        listProperties.setPropertyValue("Border", new Short((short) 2));
        listProperties.setPropertyValue("Name", name);
        listProperties.setPropertyValue("TabIndex", new Short(
                (short) tabcount++));
        listProperties.setPropertyValue("StringItemList", items);

        // insert the control models into the dialog model
        this.nameContainer.insertByName(name, listModel);

        XControlContainer controlContainer = (XControlContainer) UnoRuntime.queryInterface(XControlContainer.class, this.oUnoDialog);
        Object obj = controlContainer.getControl(name);
        XListBox lbox = (XListBox) UnoRuntime.queryInterface(XListBox.class,
                obj);

        return lbox;
    }

    public XComboBox addComboBox(String[] items, String name, int x, int y,
            int width, int height) throws Exception {
        Object comboBoxModel = multiServiceFactory.createInstance("com.sun.star.awt.UnoControlComboBoxModel");
        XPropertySet listProperties = (XPropertySet) UnoRuntime.queryInterface(
                XPropertySet.class, comboBoxModel);
        listProperties.setPropertyValue("Dropdown", Boolean.TRUE);
        listProperties.setPropertyValue("PositionX", new Integer(x));
        listProperties.setPropertyValue("PositionY", new Integer(y));
        listProperties.setPropertyValue("Width", new Integer(width));
        listProperties.setPropertyValue("Height", new Integer(height));
        listProperties.setPropertyValue("Border", new Short((short) 2));
        listProperties.setPropertyValue("Name", name);
        listProperties.setPropertyValue("TabIndex", new Short(
                (short) tabcount++));
        listProperties.setPropertyValue("ReadOnly", Boolean.TRUE);
        listProperties.setPropertyValue("StringItemList", items);

        // insert the control models into the dialog model
        this.nameContainer.insertByName(name, comboBoxModel);

        XControlContainer controlContainer = (XControlContainer) UnoRuntime.queryInterface(XControlContainer.class, this.oUnoDialog);
        Object obj = controlContainer.getControl(name);
        XComboBox lbox = (XComboBox) UnoRuntime.queryInterface(XComboBox.class,
                obj);

        return lbox;
    }

    /**
     * Create a single line textfield with the given content.
     *
     * @param content
     * @param name
     * @param x
     * @param y
     * @param width
     * @param height
     * @return
     * @throws Exception
     */
    public XTextComponent addTextField(String content, String name, int x,
            int y, int width, int height) throws Exception {
        Object labelModel = multiServiceFactory.createInstance("com.sun.star.awt.UnoControlEditModel");
        XPropertySet textFieldProperties = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, labelModel);
        textFieldProperties.setPropertyValue("PositionX", new Integer(x));
        textFieldProperties.setPropertyValue("PositionY", new Integer(y));
        textFieldProperties.setPropertyValue("Width", new Integer(width));
        textFieldProperties.setPropertyValue("Height", new Integer(height));
        textFieldProperties.setPropertyValue("Name", name);
        textFieldProperties.setPropertyValue("TabIndex", new Short(
                (short) tabcount++));
        textFieldProperties.setPropertyValue("MultiLine", new Boolean(false));
        textFieldProperties.setPropertyValue("Text", content);

        // insert the control models into the dialog model
        this.nameContainer.insertByName(name, labelModel);

        XControlContainer controlContainer = (XControlContainer) UnoRuntime.queryInterface(XControlContainer.class, this.oUnoDialog);
        Object obj = controlContainer.getControl(name);
        XTextComponent field = (XTextComponent) UnoRuntime.queryInterface(
                XTextComponent.class, obj);

        return field;
    }

    /**
     * Create a textarea with given content.
     *
     * @param content
     * @param name
     * @param x
     * @param y
     * @param width
     * @param height
     * @return a multiline XTextArea
     * @throws Exception
     */
    public XTextComponent addTextArea(String content, String name, int x,
            int y, int width, int height) throws Exception {
        Object labelModel = multiServiceFactory.createInstance("com.sun.star.awt.UnoControlEditModel");
        XPropertySet textAreaProperties = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, labelModel);
        textAreaProperties.setPropertyValue("PositionX", new Integer(x));
        textAreaProperties.setPropertyValue("PositionY", new Integer(y));
        textAreaProperties.setPropertyValue("Width", new Integer(width));
        textAreaProperties.setPropertyValue("Height", new Integer(height));
        textAreaProperties.setPropertyValue("Name", name);
        textAreaProperties.setPropertyValue("TabIndex", new Short(
                (short) tabcount++));
        textAreaProperties.setPropertyValue("MultiLine", new Boolean(true));
        textAreaProperties.setPropertyValue("HScroll", new Boolean(false));
        textAreaProperties.setPropertyValue("VScroll", new Boolean(false));
        textAreaProperties.setPropertyValue("Text", content);
        // insert the control models into the dialog model
        this.nameContainer.insertByName(name, labelModel);

        XControlContainer controlContainer = (XControlContainer) UnoRuntime.queryInterface(XControlContainer.class, this.oUnoDialog);
        Object obj = controlContainer.getControl(name);
        XTextComponent field = (XTextComponent) UnoRuntime.queryInterface(
                XTextComponent.class, obj);

        return field;
    }

    /**
     * Create a textarea with given content.
     *
     * @param content
     * @param name
     * @param x
     * @param y
     * @param width
     * @param height
     * @return a multiline XTextArea
     * @throws Exception
     */
    public XTextComponent addTextArea(String content, String name, int x,
            int y, int width, int height, boolean readonly) throws Exception {
        Object labelModel = multiServiceFactory.createInstance("com.sun.star.awt.UnoControlEditModel");
        XPropertySet textAreaProperties = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, labelModel);
        textAreaProperties.setPropertyValue("PositionX", new Integer(x));
        textAreaProperties.setPropertyValue("PositionY", new Integer(y));
        textAreaProperties.setPropertyValue("Width", new Integer(width));
        textAreaProperties.setPropertyValue("Height", new Integer(height));
        textAreaProperties.setPropertyValue("Name", name);
        textAreaProperties.setPropertyValue("TabIndex", new Short(
                (short) tabcount++));
        textAreaProperties.setPropertyValue("MultiLine", new Boolean(true));
        // textAreaProperties.setPropertyValue("HScroll", new Boolean(true));
        // textAreaProperties.setPropertyValue("VScroll", new Boolean(true));
        textAreaProperties.setPropertyValue("ReadOnly", new Boolean(readonly));
        textAreaProperties.setPropertyValue("Text", content);
        // insert the control models into the dialog model
        this.nameContainer.insertByName(name, labelModel);

        XControlContainer controlContainer = (XControlContainer) UnoRuntime.queryInterface(XControlContainer.class, this.oUnoDialog);
        Object obj = controlContainer.getControl(name);

        XTextComponent field = (XTextComponent) UnoRuntime.queryInterface(
                XTextComponent.class, obj);

        return field;
    }

    /**
     * Create a password field
     *
     * @param password
     *            , the content will be overriden after setting to the field
     * @param name
     * @param x
     * @param y
     * @param width
     * @param height
     * @return
     * @throws Exception
     */
    public XTextComponent addPasswordField(char[] password, String name, int x,
            int y, int width, int height) throws Exception {
        Object labelModel = multiServiceFactory.createInstance("com.sun.star.awt.UnoControlEditModel");
        XPropertySet passwordFieldProperties = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, labelModel);
        passwordFieldProperties.setPropertyValue("PositionX", new Integer(x));
        passwordFieldProperties.setPropertyValue("PositionY", new Integer(y));
        passwordFieldProperties.setPropertyValue("Width", new Integer(width));
        passwordFieldProperties.setPropertyValue("Height", new Integer(height));
        passwordFieldProperties.setPropertyValue("Name", name);
        passwordFieldProperties.setPropertyValue("TabIndex", new Short(
                (short) tabcount++));
        passwordFieldProperties.setPropertyValue("MultiLine",
                new Boolean(false));
        passwordFieldProperties.setPropertyValue("EchoChar", new Short(
                (short) 42));
        passwordFieldProperties.setPropertyValue("Text", new String(password));

        // override the content
        for (int i = 0; i < password.length; i++) {
            password[i] = Character.DIRECTIONALITY_WHITESPACE;
        }

        // insert the control models into the dialog model
        this.nameContainer.insertByName(name, labelModel);

        XControlContainer controlContainer = (XControlContainer) UnoRuntime.queryInterface(XControlContainer.class, this.oUnoDialog);
        Object obj = controlContainer.getControl(name);
        XTextComponent field = (XTextComponent) UnoRuntime.queryInterface(
                XTextComponent.class, obj);

        return field;
    }

    public void addImage(String url, String name, int x, int y, int width,
            int height, boolean scale) throws Exception {
        Object imageModel = multiServiceFactory.createInstance("com.sun.star.awt.UnoControlImageControlModel");
        XPropertySet imageProperties = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, imageModel);
        imageProperties.setPropertyValue("PositionX", new Integer(x));
        imageProperties.setPropertyValue("PositionY", new Integer(y));
        imageProperties.setPropertyValue("Width", new Integer(width));
        imageProperties.setPropertyValue("Height", new Integer(height));
        imageProperties.setPropertyValue("Name", name);
        imageProperties.setPropertyValue("ScaleImage", new Boolean(scale));
        imageProperties.setPropertyValue("ImageURL", new String(url));

        this.nameContainer.insertByName(name, imageModel);
    }

    public void addImage(String url, String name, int x, int y, int width,
            int height, boolean scale, short border) throws Exception {
        Object imageModel = multiServiceFactory.createInstance("com.sun.star.awt.UnoControlImageControlModel");
        XPropertySet imageProperties = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, imageModel);
        imageProperties.setPropertyValue("PositionX", new Integer(x));
        imageProperties.setPropertyValue("PositionY", new Integer(y));
        imageProperties.setPropertyValue("Width", new Integer(width));
        imageProperties.setPropertyValue("Height", new Integer(height));
        imageProperties.setPropertyValue("Name", name);
        imageProperties.setPropertyValue("ScaleImage", new Boolean(scale));
        imageProperties.setPropertyValue("ImageURL", new String(url));
        imageProperties.setPropertyValue("Border", new Short((short) border));
        this.nameContainer.insertByName(name, imageModel);
    }

    public XCheckBox addCheckBox(boolean state, String name, int x, int y,
            int width, int height) throws Exception {
        Object checkboxModel = multiServiceFactory.createInstance("com.sun.star.awt.UnoControlCheckBoxModel");
        XPropertySet imageProperties = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, checkboxModel);
        imageProperties.setPropertyValue("PositionX", new Integer(x));
        imageProperties.setPropertyValue("PositionY", new Integer(y));
        imageProperties.setPropertyValue("Width", new Integer(width));
        imageProperties.setPropertyValue("Height", new Integer(height));
        imageProperties.setPropertyValue("Name", name);

        short value = 0;

        if (state) {
            value = 1;
        }

        imageProperties.setPropertyValue("State", new Short(value));

        this.nameContainer.insertByName(name, checkboxModel);

        XControlContainer controlContainer = (XControlContainer) UnoRuntime.queryInterface(XControlContainer.class, this.oUnoDialog);
        Object obj = controlContainer.getControl(name);
        XCheckBox checkbox = (XCheckBox) UnoRuntime.queryInterface(
                XCheckBox.class, obj);

        return checkbox;
    }

    public XDialog getDialog() {
        try {
            // XControl xControl = (XControl) UnoRuntime.queryInterface(
            // XControl.class, this.dialog);
            //
            // XControlModel xControlModel = (XControlModel) UnoRuntime
            // .queryInterface(XControlModel.class, this.dialogModel);
            // xControl.setModel(xControlModel);

            // create a peer
            Object toolkit = this.multiComponentFactory.createInstanceWithContext("com.sun.star.awt.Toolkit",
                    this.context);

            XToolkit xToolkit = (XToolkit) UnoRuntime.queryInterface(
                    XToolkit.class, toolkit);

            XWindow xWindow = (XWindow) UnoRuntime.queryInterface(
                    XWindow.class, controller);

            xWindow.setVisible(false);

            controller.createPeer(xToolkit, null);

            return (XDialog) UnoRuntime.queryInterface(XDialog.class, oUnoDialog);
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public static void dispose(XDialog dialog) {
        XComponent xComponent = (XComponent) UnoRuntime.queryInterface(
                XComponent.class, dialog);
        xComponent.dispose();
    }

    public XNameContainer getModelContainer() {
        return this.nameContainer;
    }

    public String getTextContent(String componentName) throws NoSuchElementException, WrappedTargetException {

        XControlContainer controlContainer = (XControlContainer) UnoRuntime.queryInterface(XControlContainer.class, this.oUnoDialog);

        XTextComponent textComp = (XTextComponent) UnoRuntime.queryInterface(
                XTextComponent.class, controlContainer.getControl(componentName));

        return textComp.getText();
    }

    // http://wiki.services.openoffice.org/wiki/Documentation/DevGuide/GUI/The_Example_Listings
    /** makes a String unique by appending a numerical suffix
     * @param _xElementContainer the com.sun.star.container.XNameAccess container
     * that the new Element is going to be inserted to
     * @param _sElementName the StemName of the Element
     */
    public static String createUniqueName(XNameAccess _xElementContainer, String _sElementName) {
        boolean bElementexists = true;
        int i = 1;
        String sIncSuffix = "";
        String BaseName = _sElementName;
        while (bElementexists) {
            bElementexists = _xElementContainer.hasByName(_sElementName);
            if (bElementexists) {
                i += 1;
                _sElementName = BaseName + Integer.toString(i);
            }
        }
        return _sElementName;
    }

}
