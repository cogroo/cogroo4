package br.ccsl.cogroo.addon.dialogs.reporterror;

import br.ccsl.cogroo.addon.CogrooException;
import br.ccsl.cogroo.addon.CogrooExceptionMessages;
import br.ccsl.cogroo.addon.CogrooRuntimeException;
import br.ccsl.cogroo.addon.LoggerImpl;
import br.ccsl.cogroo.addon.Resources;
import br.ccsl.cogroo.addon.community.CommunityLogic;
import br.ccsl.cogroo.addon.community.CommunityLogic.Omission;
import br.ccsl.cogroo.addon.i18n.I18nLabelsLoader;
import br.ccsl.cogroo.addon.util.RestConnectionException;

import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.ItemEvent;
import com.sun.star.awt.PushButtonType;
import com.sun.star.awt.TextEvent;
import com.sun.star.awt.XActionListener;
import com.sun.star.awt.XControl;
import com.sun.star.awt.XControlModel;
import com.sun.star.awt.XFixedHyperlink;
import com.sun.star.awt.XFixedText;
import com.sun.star.awt.XItemListener;
import com.sun.star.awt.XListBox;
import com.sun.star.awt.XTextComponent;
import com.sun.star.awt.XTextListener;
import com.sun.star.beans.XPropertySet;
import com.sun.star.lang.EventObject;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.script.BasicErrorException;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ErrorReportDialog extends WizardDialog {

    protected static Logger LOG = LoggerImpl.getLogger(ErrorReportDialog.class.getCanonicalName());

    private static final int STEP_LOGIN = 1;
    private static final int STEP_FALSE_ERRORS = 2;
    private static final int STEP_OMISSIONS = 3;
    private static final int STEP_THANKS = 4;

    /**
     * Community is the logic class responsable of comunication with the host
     */
    private CommunityLogic theCommunityLogic;

    // these vars are shared by multiple threads
    private volatile boolean isAuthenticated = false;
    private volatile boolean gotCommunicationError = false;

    //**************************************************************************
    // Buttons
    //**************************************************************************
    private String nextButtonName;
    private String previousButtonName;
    private String cancelButtonName;

    //**************************************************************************
    // Login fields
    //**************************************************************************
    private XFixedText authStatusLabel;
    private XPropertySet authProgressBar;
    private XTextComponent userNameText;
    private XTextComponent userPasswordText;
    private String authButton;
    //**************************************************************************
    // False errors fields
    //**************************************************************************
    private XListBox badIntListBox;
    private XTextComponent badIntDetails;
    private XListBox badIntClassificationDropBox;
    private XTextComponent badIntComments;
    private String badIntApplyButton;
    private XTextComponent badIntErrorsText;
    private XFixedText badIntCommentsLabel;
    
    //**************************************************************************
    // Omissions fields
    //**************************************************************************
    private XTextComponent omissionsTextToSelect;
    private String omissionsClassifyButton;
    private XTextComponent omissionsTextWithErrors;
    private XListBox omissionsErrorList;
    private String omissionsExcludeButton;
    private XListBox omissionsCategoriesDropbox;
    private XTextComponent omissionsCategoryText;
    private XTextComponent omissionsReplaceText;
    private XTextComponent omissionsCommentsText;
    private XFixedText omissionsCategoriesLabel;
    private XFixedText omissionsReplaceLabel;
    private XFixedText omissionsCommentsLabel;

    //**************************************************************************
    // Omissions fields
    //**************************************************************************
    private XFixedText omissionsCategoryLabel;
    private String omissionsApply;

    //**************************************************************************
    // Thanks fields
    //**************************************************************************
    private XFixedText thanksStatusLabel;
    private XFixedHyperlink thanksReportLink;
    private XPropertySet thanksProgressBar;


    //**************************************************************************
    // Static fields related to control position
    //**************************************************************************
    private static final int DEFAULT_DIALOG_WIDTH = 280;
    private static final int DEFAULT_DIALOG_HEIGHT = 300;
    private static final int DEFAULT_SIDE_PANE_WIDTH = 85;
    private static final int DEFAULT_PosX = 95;
    private static final int DEFAULT_WIDTH_LARGE =
            DEFAULT_DIALOG_WIDTH - DEFAULT_SIDE_PANE_WIDTH
            - 2 * (DEFAULT_PosX - DEFAULT_SIDE_PANE_WIDTH);

    //##########################################################################
    //
    // Constructor and initializers
    //
    //##########################################################################

    /**
     * Creates a new ErrorReportDialog. This dialog requires login in Cogroo
     * Comunidade. This is for advanced users.
     * @param _xContext the context
     * @param _xMCF the componet factory.
     */
    public ErrorReportDialog(XComponentContext _xContext,
            XMultiComponentFactory _xMCF) {
        super(_xContext, _xMCF);
    }

    /**
     * Initializes the dialog with a selected text. The selected text can't be
     * enpty or null.
     * @param selectedText the selected text
     */
    public void initialize(String selectedText) {

        LOG.finest(">>> initialize");

        try {
            this.theCommunityLogic = new CommunityLogic(m_xContext, selectedText);

            // set properties of the dialog
            String[] propNames = new String[] {
                "Height",   // 0
                "Moveable", // 1
                "Name",     // 2
                "PositionX",// 3
                "PositionY",// 4
                "Step",     // 5
                "TabIndex", // 6
                "Title",    // 7
                "Width"     // 8
            };
            Object[] propValues = new Object[] {
                new Integer(DEFAULT_DIALOG_HEIGHT),   // 0
                Boolean.TRUE,       // 1
                I18nLabelsLoader.ADDON_REPORT_ERROR,     // 2
                new Integer(102),   // 3
                new Integer(41),    // 4
                new Integer(-1),     // 5
                new Short((short) 0),// 6
                I18nLabelsLoader.ADDON_REPORT_ERROR + " - CoGrOO Comunidade",           // 7
                new Integer(DEFAULT_DIALOG_WIDTH)    // 8
            };
        
            super.initialize(propNames, propValues);

            this.createWindowPeer();
            this.addRoadmap(new RoadmapItemStateChangeListener(m_xMSFDialogModel));
            
            populateStep1();
            populateStep2();
            populateStep3();
            populateStep4();

            addButtons();

            new CheckTokenAndGetCategoriesThread().start();

            setCurrentStep(STEP_LOGIN);

            setControlsState();
            
            LOG.finest("<<< initialize");

        } catch(BasicErrorException e ) {
            LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
            throw new CogrooRuntimeException(CogrooExceptionMessages.INTERNAL_ERROR, new String[]{e.getLocalizedMessage()});
        }
    }


    //##########################################################################
    //
    // Populate steps with controls
    //
    //##########################################################################

    /**
     * Populate the step one, that we put loggin stuf
     */
    private void populateStep1() {
        this.insertRoadmapItem(0, true, I18nLabelsLoader.REPORT_STEP_LOGIN, 1);

        int y = 6;
        this.insertFixedTextBold(this, DEFAULT_PosX, y, DEFAULT_WIDTH_LARGE, STEP_LOGIN, I18nLabelsLoader.REPORT_STEP_LOGIN);
        y += 12; // plus one line
        this.insertMultilineFixedText(this, DEFAULT_PosX, y, DEFAULT_WIDTH_LARGE, 8*18, STEP_LOGIN, I18nLabelsLoader.ADDON_LOGIN_INFO);
        y += 8*18; // plus 15 lines

        this.insertFixedHyperlink(this, DEFAULT_PosX, y, DEFAULT_WIDTH_LARGE, STEP_LOGIN, I18nLabelsLoader.ADDON_REPORT_FROM_BROWSER, Resources.getProperty("COMMUNITY_ROOT") + "/reports/new/" + theCommunityLogic.getEscapedText());
        y += 18;

        this.insertFixedHyperlink(this, DEFAULT_PosX, y, DEFAULT_WIDTH_LARGE, STEP_LOGIN, I18nLabelsLoader.ADDON_LOGIN_REGISTER, Resources.getProperty("COMMUNITY_ROOT") + "/register");
        y += 12;
        this.insertFixedHyperlink(this, DEFAULT_PosX, y, DEFAULT_WIDTH_LARGE, STEP_LOGIN, I18nLabelsLoader.ADDON_LOGIN_LICENSE, I18nLabelsLoader.ADDON_LOGIN_LICENSEURL);
        y += 12;
        this.insertFixedText(this, DEFAULT_PosX, y + 2, 40, 1, I18nLabelsLoader.ADDON_LOGIN_USER);

        String userName = getCommunityUsername();
        if(userName == null) {
            userName = "";
        }
        XTextListener textListener = new AuthUserPasswdTextListener();
        userNameText = this.insertEditField(textListener, this, DEFAULT_PosX + 40, y, 60, STEP_LOGIN, userName);
        y += 14;
        this.insertFixedText(this, DEFAULT_PosX, y + 2, 40, 1, I18nLabelsLoader.ADDON_LOGIN_PASSWORD);
        userPasswordText = this.insertSecretEditField(textListener, this,  DEFAULT_PosX + 40, y, 60, STEP_LOGIN, "");
        y += 14;
        authButton = this.insertButton(new AuthLoginButtonListener(), DEFAULT_PosX + 40 + 60 + 10, y - 12, 40, STEP_LOGIN, I18nLabelsLoader.ADDON_LOGIN_ALLOW, ((short) PushButtonType.STANDARD_value));
        y += 4;
        this.insertFixedText(this, DEFAULT_PosX, y, DEFAULT_WIDTH_LARGE, STEP_LOGIN, I18nLabelsLoader.ADDON_LOGIN_STATUS);
        authStatusLabel = this.insertHiddenFixedStatusText(this, DEFAULT_PosX + 40, y, DEFAULT_WIDTH_LARGE, STEP_LOGIN, I18nLabelsLoader.ADDON_LOGIN_STATUS_NOTAUTH, false);

        authProgressBar = this.insertProgressBar(DEFAULT_PosX, DEFAULT_DIALOG_HEIGHT - 26 - 8, DEFAULT_WIDTH_LARGE, 1, 100);
    }

    /**
     * Populate with omissions
     */
    private void populateStep2() {
        this.insertRoadmapItem(1, true, I18nLabelsLoader.REPORT_STEP_FALSE_ERRORS, 2);

        // Bad intervention
        int y = 6;
        this.insertFixedTextBold(this, DEFAULT_PosX, y, DEFAULT_WIDTH_LARGE, STEP_FALSE_ERRORS, I18nLabelsLoader.REPORT_STEP_FALSE_ERRORS);
        y += 12;

        // help text
        this.insertMultilineFixedText(this, DEFAULT_PosX, y, DEFAULT_WIDTH_LARGE, 8*4 , STEP_FALSE_ERRORS, I18nLabelsLoader.ADDON_BADINT_INFO);
        y += 8*3 + 4;

        // label ADDON_BADINT_ERRORSLIST
        this.insertMultilineFixedText(this, DEFAULT_PosX, y, DEFAULT_WIDTH_LARGE, 8*2, STEP_FALSE_ERRORS, I18nLabelsLoader.ADDON_BADINT_ERRORSLIST);
        y += 8 + 12;

        // list BadInt
        this.badIntListBox = this.insertListBox(new BadIntListSelectonListener(), DEFAULT_PosX, y, 3*12, DEFAULT_WIDTH_LARGE, STEP_FALSE_ERRORS, theCommunityLogic.getBadInterventions());
        y += 3*12 + 4;

        // label ADDON_BADINT_ERRORSFOUND
        this.insertFixedText(this, DEFAULT_PosX, y, DEFAULT_WIDTH_LARGE, STEP_FALSE_ERRORS, I18nLabelsLoader.ADDON_BADINT_ERRORSFOUND);
        y += 8; // plus one line

        // field TEXT
        this.badIntErrorsText = this.insertMultilineEditField(this, this, DEFAULT_PosX, y, 3*12, DEFAULT_WIDTH_LARGE, STEP_FALSE_ERRORS, "", true);
        y += 3*12 + 4;

        // label ADDON_BADINT_DETAILS
        this.insertFixedText(this, DEFAULT_PosX, y, DEFAULT_WIDTH_LARGE, STEP_FALSE_ERRORS, I18nLabelsLoader.ADDON_BADINT_DETAILS);
        y += 8; // plus one line

        // field Details
        this.badIntDetails = this.insertMultilineEditField(this, this, DEFAULT_PosX, y, 3*12, DEFAULT_WIDTH_LARGE, STEP_FALSE_ERRORS, "", true);
        y += 3*12 + 4;

        // label / field ADDON_BADINT_TYPE
        this.insertFixedText(this, DEFAULT_PosX, y + 2, 40, STEP_FALSE_ERRORS, I18nLabelsLoader.ADDON_BADINT_TYPE);
        this.badIntClassificationDropBox = this.insertDropBox(new BadIntClassificationDropBoxSelectionListener(), DEFAULT_PosX + 40, y, DEFAULT_WIDTH_LARGE - 40, STEP_FALSE_ERRORS, theCommunityLogic.getClassifications());
        y += 14;

        // label ADDON_BADINT_COMMENTS
        this.badIntCommentsLabel = this.insertFixedText(this, DEFAULT_PosX, y, 40, STEP_FALSE_ERRORS, I18nLabelsLoader.ADDON_BADINT_COMMENTS);
        y += 12;

        // field Comments
        this.badIntComments = this.insertMultilineEditField(new BadIntCommentsTextListener(), this, DEFAULT_PosX, y, 2*12, DEFAULT_WIDTH_LARGE, STEP_FALSE_ERRORS, "", false);
        y += 2*12 + 4;

        // button Apply
        this.badIntApplyButton = this.insertButton(new BadIntApplyButtonListener(), DEFAULT_PosX + DEFAULT_WIDTH_LARGE - 40, y, 40, STEP_FALSE_ERRORS, I18nLabelsLoader.ADDON_BADINT_APPLY, ((short) PushButtonType.STANDARD_value));

        setIsControlEnable(badIntApplyButton, false);
    }

    /**
     * Populate with false errors
     */
    private void populateStep3() {
        this.insertRoadmapItem(2, true, I18nLabelsLoader.REPORT_STEP_OMISSIONS, 3);
        OmissionTextListener textListener = new OmissionTextListener();

        int y = 6;

        // omissions
        this.insertFixedTextBold(this, DEFAULT_PosX, y, DEFAULT_WIDTH_LARGE, STEP_OMISSIONS, I18nLabelsLoader.REPORT_STEP_OMISSIONS);
        y += 12;

        // help
        this.insertMultilineFixedText(this, DEFAULT_PosX, y, DEFAULT_WIDTH_LARGE, 8*2 , STEP_OMISSIONS, I18nLabelsLoader.ADDON_OMISSION_INFO);
        y += 8*2 + 4;

        // label / field select error
        this.insertFixedText(this, DEFAULT_PosX, y, DEFAULT_WIDTH_LARGE, STEP_OMISSIONS, I18nLabelsLoader.ADDON_OMISSION_SELECT);
        y += 12;
        this.omissionsTextToSelect = this.insertMultilineEditField(this, this, DEFAULT_PosX, y, 2*15, DEFAULT_WIDTH_LARGE, STEP_OMISSIONS, theCommunityLogic.getText(), true);
        y += 2*12 + 8;

        // add button
        this.omissionsClassifyButton = this.insertButton(new OmissionClassifyButtonListener(), DEFAULT_PosX + DEFAULT_WIDTH_LARGE - 40, y, 40, STEP_OMISSIONS, I18nLabelsLoader.ADDON_OMISSION_CLASSIFY, ((short) PushButtonType.STANDARD_value));
        y += 8;

        // label / list of errors
        this.insertFixedText(this, DEFAULT_PosX, y, DEFAULT_WIDTH_LARGE, STEP_OMISSIONS, I18nLabelsLoader.ADDON_OMISSION_CLASSIFIED_ERRORS);
        y += 8;
        this.omissionsErrorList = this.insertListBox(new OmissionListSelectionListener(), DEFAULT_PosX, y, 2*12, DEFAULT_WIDTH_LARGE, STEP_OMISSIONS, new String[] {});
        y += 2*12 + 4;

        // label / field classified error
        this.insertFixedText(this, DEFAULT_PosX, y, DEFAULT_WIDTH_LARGE, STEP_OMISSIONS, I18nLabelsLoader.ADDON_OMISSION_SELECTED_OMISSION);
        y += 8;
        this.omissionsTextWithErrors = this.insertMultilineEditField(this, this, DEFAULT_PosX, y, 2*15, DEFAULT_WIDTH_LARGE, STEP_OMISSIONS, theCommunityLogic.getText(), true);
        y += 2*15 + 4;

        // Form...
        this.omissionsCategoriesLabel = this.insertFixedText(this, DEFAULT_PosX, y, DEFAULT_WIDTH_LARGE,STEP_OMISSIONS, I18nLabelsLoader.ADDON_OMISSION_CATEGORY);
        y += 8;
        this.omissionsCategoriesDropbox = this.insertDropBox(new OmissionCategoryDropBoxSelectionListener(), DEFAULT_PosX, y, DEFAULT_WIDTH_LARGE, STEP_OMISSIONS, new String[] {});
        y += 14;
        this.omissionsCategoryLabel = this.insertFixedText(this, DEFAULT_PosX, y + 2, 40, STEP_OMISSIONS, I18nLabelsLoader.ADDON_OMISSION_CATEGORY_CUSTOM);
        this.omissionsCategoryText = this.insertEditField(textListener, this, DEFAULT_PosX + 45, y, DEFAULT_WIDTH_LARGE - 45, STEP_OMISSIONS, "");
        y += 14;
        this.omissionsReplaceLabel = this.insertFixedText(this, DEFAULT_PosX, y + 2, 40, STEP_OMISSIONS, I18nLabelsLoader.ADDON_OMISSION_REPLACE_BY);
        this.omissionsReplaceText = this.insertEditField(textListener, this, DEFAULT_PosX + 45, y, DEFAULT_WIDTH_LARGE - 45, STEP_OMISSIONS, "");
        y += 14;
        this.omissionsCommentsLabel = this.insertFixedText(this, DEFAULT_PosX, y, DEFAULT_WIDTH_LARGE, STEP_OMISSIONS, I18nLabelsLoader.ADDON_OMISSION_COMMENTS);
        y += 8;
        this.omissionsCommentsText = this.insertMultilineEditField(textListener, this, DEFAULT_PosX, y, 2*12, DEFAULT_WIDTH_LARGE, STEP_OMISSIONS, "", false);
        y += 2*12 + 4;
        this.omissionsApply = this.insertButton(new OmissionApplyButtonListener(), DEFAULT_PosX + DEFAULT_WIDTH_LARGE - 40, y, 40, STEP_OMISSIONS, I18nLabelsLoader.ADDON_OMISSION_APPLY, ((short) PushButtonType.STANDARD_value));
        this.omissionsExcludeButton = this.insertButton(new OmissionExcludeButtonListener(), DEFAULT_PosX + DEFAULT_WIDTH_LARGE - 80, y, 40, STEP_OMISSIONS, I18nLabelsLoader.ADDON_OMISSION_EXCLUDE, ((short) PushButtonType.STANDARD_value));
    }

    /**
     * Populate with thanks
     */
    private void populateStep4() {
        this.insertRoadmapItem(3, true, I18nLabelsLoader.REPORT_STEP_THANKS, 4);

        int y = 6;

        // thanks
        this.insertFixedTextBold(this, DEFAULT_PosX, y, DEFAULT_WIDTH_LARGE, STEP_THANKS, I18nLabelsLoader.REPORT_STEP_THANKS);
        y += 12; // plus one line

        // thanks msg
        this.insertMultilineFixedText(this, DEFAULT_PosX, y, DEFAULT_WIDTH_LARGE, 8*5, STEP_THANKS, I18nLabelsLoader.ADDON_THANKS_MESSAGE);
        y += 8*5 + 12; // plus 15 lines

        // status
        this.insertFixedText(this, DEFAULT_PosX, y, DEFAULT_WIDTH_LARGE, STEP_THANKS, I18nLabelsLoader.ADDON_THANKS_STATUS);
        this.thanksStatusLabel = this.insertHiddenFixedStatusText(this, DEFAULT_PosX + 40, y, DEFAULT_WIDTH_LARGE, STEP_THANKS, I18nLabelsLoader.ADDON_THANKS_STATUS, false);
        y += 12;
        
        // link
        this.thanksReportLink = this.insertFixedHyperlink(this, DEFAULT_PosX, y, DEFAULT_WIDTH_LARGE, STEP_THANKS, I18nLabelsLoader.ADDON_THANKS_LINK, Resources.getProperty("COMMUNITY_ROOT") + "/reports");

        this.thanksProgressBar = this.insertProgressBar(DEFAULT_PosX, DEFAULT_DIALOG_HEIGHT - 26 - 8, DEFAULT_WIDTH_LARGE, STEP_THANKS, 100);
    }

    /**
     * Populate with buttons (previous, next)
     */
    private void addButtons() {
        int y = DEFAULT_DIALOG_HEIGHT - 19;
        int x = DEFAULT_DIALOG_WIDTH - DEFAULT_SIDE_PANE_WIDTH - 70;

        previousButtonName = this.insertButton(new PreviousActionListener(), x, y, 40, 0, I18nLabelsLoader.REPORT_STEP_BUTTONS_PREV, ((short) PushButtonType.STANDARD_value));
        x += 50;
        nextButtonName = this.insertButton(new NextActionListener(), x, y, 40, 0, I18nLabelsLoader.REPORT_STEP_BUTTONS_NEXT, ((short) PushButtonType.STANDARD_value));
        x += 50;
        cancelButtonName = this.insertButton(this, x, y, 40, 0, I18nLabelsLoader.REPORT_STEP_BUTTONS_CANCEL, ((short) PushButtonType.CANCEL_value));
    }

    //##########################################################################
    //
    // State configuration
    //
    //##########################################################################

    private void configureStateAuth() {
        setIsControlEnable(authButton, false);
        if(isAuthenticated()) {
            authStatusLabel.setText(I18nLabelsLoader.ADDON_LOGIN_STATUS_OK);
            setDefaultButton(nextButtonName, true);
            if(userPasswordText.getText() != null && userPasswordText.getText().length() == 0) {
                userPasswordText.setText("********");
            }            
            setDefaultButton(authButton, false);
            setDefaultButton(cancelButtonName, false);
            setDefaultButton(nextButtonName, true);
        } else if (gotCommunicationError) {
            // leave previous state
            setIsControlEnable(authButton, true);
            setDefaultButton(cancelButtonName, true);
        }
    }

    private void configureStateBadInst() {

        // check if there are errors, if yes, select first. Do it to start with the first selected
        if(this.badIntListBox.getItemCount() > (short)0) {
            this.badIntListBox.selectItemPos((short)0, true);
            setBadIntDetailsForSelectedItem((short)0);
            setIsControlEnable(badIntCommentsLabel, false);
            setIsControlEnable(badIntComments, false);
            this.badIntErrorsText.setText(theCommunityLogic.getAnnotatedText(0));
        }

    }

    private void configureStateOmissions() {

        // check if there are errors, if yes, select first. Do it to start with the first selected
        if(this.omissionsErrorList.getItemCount() > (short)0) {
            this.omissionsErrorList.selectItemPos((short)0, true);
            setOmissionDetailsForSelectedItem((short)0);
        } else {
            enableOmissionDetails(false, false);
        }

    }

    //##########################################################################
    //
    // Step controls
    //
    //##########################################################################

    /**
     * Manages the update of control state
     */
    private void setControlsState() {
        configureStateAuth();
        configureStateBadInst();
        configureStateOmissions();
        setEnabledSteps();
        setEnabledButtons();
    }

    /**
     * Moves to another step
     * @param nNewID the step to move to.
     */
    private void setCurrentStep(int nNewID) {
        try {
            XPropertySet xDialogModelPropertySet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, m_xMSFDialogModel);
            int nOldStep = getCurrentStep();
            // in the following line "ID" and "Step" are mixed together.
            // In fact in this case they denot the same
            if (nNewID != nOldStep){
                xDialogModelPropertySet.setPropertyValue("Step", new Integer(nNewID));
            }
        } catch (com.sun.star.uno.Exception exception) {
            exception.printStackTrace(System.out);
        }
    }

    /**
     * Get the current step
     */
    private int getCurrentStep() {
        int step = -1;
        try {
            XPropertySet xDialogModelPropertySet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, m_xMSFDialogModel);
            step = ((Integer) xDialogModelPropertySet.getPropertyValue("Step")).intValue();
        } catch (com.sun.star.uno.Exception exception) {
            exception.printStackTrace(System.out);
        }
        return step;
    }

    /**
     * Configure the steps that are enabled.
     * @param step step to configure
     * @param isEnabled if it is enabled
     */
    private void setIsStepEnabled(int step, boolean isEnabled) {
        try {
            Object oRoadmapItem = m_xRMIndexCont.getByIndex(step - 1);
            XPropertySet xRMItemPSet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, oRoadmapItem);
            xRMItemPSet.setPropertyValue("Enabled", new Boolean(isEnabled));
        } catch (com.sun.star.uno.Exception e) {
            LOGGER.log(Level.SEVERE, "Error in setIsStepEnabled", e);
            throw new CogrooRuntimeException(e);
        }
    }

    /**
     * Checks if step is enabled.
     * @param step step to check
     * @return if the step is enabled
     */
    private boolean isStepEnabled(int step) {
        if(step < 0) {
            return false;
        }
        boolean isStepEnabled = false;
        try {
            Object oRoadmapItem = m_xRMIndexCont.getByIndex(step - 1);
            XPropertySet xRMItemPSet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, oRoadmapItem);
            isStepEnabled = ((Boolean)xRMItemPSet.getPropertyValue("Enabled")).booleanValue();
        } catch (com.sun.star.uno.Exception e) {
            LOGGER.log(Level.SEVERE, "Error in isStepEnabled", e);
            throw new CogrooRuntimeException(e);
        }
        return isStepEnabled;
    }

    /**
     * Sets the enabled steps acording to the current state
     */
    private void setEnabledSteps() {

        // gets the important data
        boolean isAuth = isAuthenticated();
        boolean hasFalseErrors = badIntListBox.getItemCount() > 0;
        int currentStep = getCurrentStep();

        boolean login = true;
        boolean falseErrors = false;
        boolean omissions = false;
        boolean thanks = false;

        if(currentStep == STEP_THANKS) {

            login = falseErrors = omissions = false;
            thanks = true;

        } else if(isAuth) {

            if(hasFalseErrors) {
                falseErrors = true;
            }

            login = omissions = true;

        }

        setIsStepEnabled(STEP_LOGIN, login);
        setIsStepEnabled(STEP_FALSE_ERRORS, falseErrors);
        setIsStepEnabled(STEP_OMISSIONS, omissions);
        setIsStepEnabled(STEP_THANKS, thanks);

    }

    /**
     * Sets the previous/next buttons
     */
    private void setEnabledButtons() {
        int currentStep = getCurrentStep();
        if(currentStep < 0) {
           return;
        }
        
        boolean isPreviousEnabled = true;
        boolean isNextEnabled = true;
        
        if(getNextStep(currentStep) < 0 && currentStep != STEP_THANKS) {
            isNextEnabled = false;
        }

         if(getPreviousStep(currentStep) < 0) {
            isPreviousEnabled = false;
        }

        setIsControlEnable(previousButtonName, isPreviousEnabled);
        setIsControlEnable(nextButtonName, isNextEnabled);

        if(currentStep == STEP_OMISSIONS) {
            setControlText(nextButtonName, I18nLabelsLoader.REPORT_STEP_BUTTONS_SUBMIT);
            if(theCommunityLogic.hasBadInterventions() || theCommunityLogic.hasOmissions()) {
                setIsControlEnable(nextButtonName, true);
            } else {
                 setIsControlEnable(nextButtonName, false);
            }
            
        } else if (currentStep == STEP_THANKS) {
            setControlText(nextButtonName, I18nLabelsLoader.REPORT_STEP_BUTTONS_FINISH);
        } else {
            setControlText(nextButtonName, I18nLabelsLoader.REPORT_STEP_BUTTONS_NEXT);
        }

    }

    /**
     * Get the next step given the current. Will check available steps to confirm.
     * @param currentStep
     * @return
     */
    private int getNextStep(int currentStep) {
        int nextStep = -1;
        
        // we find the next available step.
        for (int i = currentStep + 1; i <= STEP_THANKS; i++) {
            if(isStepEnabled(i)) {
                nextStep = i;
                break;
            }
        }

        return nextStep;
    }

    /**
     * Get the previous step given the current. Will check available steps to confirm.
     * @param currentStep
     * @return
     */
    private int getPreviousStep(int currentStep) {
        int prevStep = -1;

        // we find the next available step.
        for (int i = currentStep - 1; i >= STEP_LOGIN; i--) {
            if(isStepEnabled(i)) {
                prevStep = i;
                break;
            }
        }

        return prevStep;
    }


    //##########################################################################
    //
    // Auxiliary methods
    //
    //##########################################################################

    //**************************************************************************
    // Auth
    //**************************************************************************
    private String getCommunityUsername() {
        String userName = Resources.getProperty("COMMUNITY_USERNAME");
        return userName;
    }

    private void saveCommunityUsername(String userName) {
        Resources.setProperty("COMMUNITY_USERNAME", userName);
    }

    private String getCommunityUserToken() {
        String userName = Resources.getProperty("COMMUNITY_USERTOKEN");
        return userName;
    }

    private void saveCommunityUserToken(String userToken) {
        Resources.setProperty("COMMUNITY_USERTOKEN", userToken);
    }

    private boolean isAuthenticated() {
        return isAuthenticated;
    }

    //**************************************************************************
    // Bad interventions
    //**************************************************************************
    
    private void setBadIntDetailsForSelectedItem(short selectedItem) {
        badIntDetails.setText(theCommunityLogic.getDetailsForBadIntervention(selectedItem));
        badIntClassificationDropBox.selectItemPos(theCommunityLogic.getClassificationForBadIntervention(selectedItem), true);
        badIntComments.setText(theCommunityLogic.getCommentsForBadIntervention(selectedItem));
        badIntErrorsText.setText(theCommunityLogic.getAnnotatedText(selectedItem));
    }

    //**************************************************************************
    // Omissions
    //**************************************************************************

    private void updateOmissionCategories(String[] cat) {
        short itens = this.omissionsCategoriesDropbox.getItemCount();
        if(itens > 0) {
            this.omissionsCategoriesDropbox.removeItems((short)0, itens);
        }
        this.omissionsCategoriesDropbox.addItem(I18nLabelsLoader.ADDON_OMISSION_CATEGORY_CUSTOM, (short)0);
        this.omissionsCategoriesDropbox.addItem(I18nLabelsLoader.ADDON_OMISSION_CATEGORY_UNKNOWN, (short)1);
        this.omissionsCategoriesDropbox.addItems(cat, (short)2);

    }

    private void setOmissionDetailsForSelectedItem(short selectedItem) {
        Omission o = theCommunityLogic.getOmission(selectedItem);
        boolean isCustomCategory = false;
        if(o.getCategory() != null) {
            omissionsCategoriesDropbox.selectItem(o.getCategory(), true);
            omissionsCategoryText.setText("");
        } else if(o.getCustomCategory() != null) {
            omissionsCategoriesDropbox.selectItem(I18nLabelsLoader.ADDON_OMISSION_CATEGORY_CUSTOM, true);
            omissionsCategoryText.setText(o.getCustomCategory());
            isCustomCategory = true;
        }
        if(o.getReplaceBy() != null) {
            omissionsReplaceText.setText(o.getReplaceBy());
        } else {
            omissionsReplaceText.setText("");
        }
        if(o.getComment() != null) {
            omissionsCommentsText.setText(o.getComment());
        } else {
            omissionsCommentsText.setText("");
        }

        enableOmissionDetails(true, isCustomCategory);
    }

    private void enableOmissionDetails(boolean isEnable, boolean isCustomCategory)
    {
        setIsControlEnable(this.omissionsCategoriesLabel, isEnable);
        setIsControlEnable(this.omissionsCategoriesDropbox, isEnable);

        setIsControlEnable(this.omissionsCategoryLabel, isEnable && isCustomCategory);
        setIsControlEnable(this.omissionsCategoryText, isEnable && isCustomCategory);

        setIsControlEnable(this.omissionsReplaceLabel, isEnable);
        setIsControlEnable(this.omissionsReplaceText, isEnable);

        setIsControlEnable(this.omissionsCommentsLabel, isEnable);
        setIsControlEnable(this.omissionsCommentsText, isEnable);

        if(!isEnable) {
            omissionsReplaceText.setText("");
            omissionsCommentsText.setText("");
            omissionsCategoryText.setText("");
        }
    }


    //**************************************************************************
    // Helper methods to enable a control
    //**************************************************************************

    protected void setIsControlEnable(Object control, boolean isEnable) {
        setModelProperties(toModel(control), new String[] {"Enabled"}, new Object[]{new Boolean(isEnable)});
    }

    protected void setIsControlEnable(String control, boolean isEnable) {
        setModelProperties(getModelByName(control), new String[] {"Enabled"}, new Object[]{new Boolean(isEnable)});
    }

    protected void setControlText(String control, String text) {
        setModelProperties(getModelByName(control), new String[] {"Label"}, new Object[]{text});
    }

    protected void setDefaultButton(String control, boolean isDefault) {
        setModelProperties(getModelByName(control), new String[] {"DefaultButton"}, new Object[]{new Boolean(isDefault)});
    }

    protected XControlModel toModel(Object control) {
        XControl xc = (XControl) UnoRuntime.queryInterface(XControl.class, control);
        return xc.getModel();

    }

    protected boolean safeEquals(Object a, Object b) {

        if(a == null && b == null) {
            return true;
        } else if(a != null) {
            return a.equals(b);
        } else if(b != null) {
            return b.equals(a);
        }
        return false;
    }

    //##########################################################################
    //
    // Listeners
    //
    //##########################################################################

    //**************************************************************************
    // Auth listeners
    //**************************************************************************

    /**
     * Listener for the Login button. Creates a new thread to handle it
     */
    private class AuthLoginButtonListener implements XActionListener {

        public void actionPerformed(ActionEvent arg0) {
            new AuthenticateUserThread().start();
        }

        public void disposing(EventObject arg0) {
        }

    }

    /**
     * Monitors when user changed passwd or name
     */
    private class AuthUserPasswdTextListener implements XTextListener {

        public void textChanged(TextEvent arg0) {
            // if changed user or edited passwd, we enable Auth
            String lastUser = getCommunityUsername();
            String passwd = userPasswordText.getText();
            String user = userNameText.getText();

            boolean isPasswdEntered = passwd.length() > 0;
            boolean isUserEntered = user.length() > 0;
            if( isUserEntered && isPasswdEntered) {
                setIsControlEnable(authButton, true);
                setDefaultButton(authButton, true);
            } else {
                setIsControlEnable(authButton, false);
            }
        }

        public void disposing(EventObject arg0) {
        }

    }

    //**************************************************************************
    // Bad interventions listeners
    //**************************************************************************

    /**
     * Apply the entered details to the selected bad intervention
     */
    private class BadIntApplyButtonListener implements XActionListener {

        public void actionPerformed(ActionEvent arg0) {
            applyBadInt();
        }

        public void disposing(EventObject arg0) {
        }

    }

    private void applyBadInt() {
        if(badIntListBox.getSelectedItemPos() >= 0) {
            theCommunityLogic.setClassificationForBadIntervention(
                    badIntListBox.getSelectedItemPos(),
                    badIntClassificationDropBox.getSelectedItemPos());
            theCommunityLogic.setCommentsForBadIntervention(
                    badIntListBox.getSelectedItemPos(),
                    badIntComments.getText());

            badIntErrorsText.setText(theCommunityLogic.getAnnotatedText());
            short pos = badIntListBox.getSelectedItemPos();
            badIntListBox.removeItems(pos, (short)1);
            badIntListBox.addItem(theCommunityLogic.getBadInterventions()[pos], pos);
            badIntListBox.selectItemPos(pos, true);
            setIsControlEnable(badIntApplyButton, false);
        }
    }

    /**
     * Handles the event generated by selecting an item in bad intevention list
     */
    private class BadIntListSelectonListener implements XItemListener {

        public void itemStateChanged(ItemEvent arg0) {
            short selectedItem = badIntListBox.getSelectedItemPos();
            setBadIntDetailsForSelectedItem(selectedItem);
        }

        public void disposing(EventObject arg0) {
        }
        
    }

    /**
     * Monitors when the comments text changed
     */
    private class BadIntCommentsTextListener implements XTextListener {

        public void textChanged(TextEvent arg0) {
            // check if comment changed
            if(! badIntComments.getText().equals(theCommunityLogic.getCommentsForBadIntervention(badIntListBox.getSelectedItemPos()))) {
                setIsControlEnable(badIntApplyButton, true);
            }
        }

        public void disposing(EventObject arg0) {
        }

    }
    
    /**
     * Monitors changes in the classification dropbox
     */
    private class BadIntClassificationDropBoxSelectionListener implements XItemListener {

        public void itemStateChanged(ItemEvent arg0) {
            int pos = badIntClassificationDropBox.getSelectedItemPos();
            if(pos != theCommunityLogic.getClassificationForBadIntervention(badIntListBox.getSelectedItemPos())) {
                setIsControlEnable(badIntApplyButton, true);
            }
            boolean commentsEnabled = true;
            if(pos == 0) {
                commentsEnabled = false;
                badIntComments.setText("");
            }
            setIsControlEnable(badIntCommentsLabel, commentsEnabled);
            setIsControlEnable(badIntComments, commentsEnabled);
        }

        public void disposing(EventObject arg0) {
        }

    }


    //**************************************************************************
    // Omissions listeners
    //**************************************************************************

    /**
     * Monitors if the selected category is Custom, if yes, enables button.
     */
    private class OmissionCategoryDropBoxSelectionListener implements XItemListener {

        public void itemStateChanged(ItemEvent arg0) {
            String selectedItem = omissionsCategoriesDropbox.getSelectedItem();
            
            if(selectedItem.startsWith(I18nLabelsLoader.ADDON_OMISSION_CATEGORY_CUSTOM)) {
                setIsControlEnable(omissionsCategoryText, true);
                setIsControlEnable(omissionsCategoryLabel, true);
            } else {
                omissionsCategoryText.setText("");
                setIsControlEnable(omissionsCategoryText, false);
                setIsControlEnable(omissionsCategoryLabel, false);
            }
        }

        public void disposing(EventObject arg0) {
        }

    }

    /**
     * Monitors when the selected omission changed
     */
    private class OmissionListSelectionListener implements XItemListener {

        public void itemStateChanged(ItemEvent arg0) {
            short selectedItem = omissionsErrorList.getSelectedItemPos();
            setOmissionDetailsForSelectedItem(selectedItem);
            omissionsTextWithErrors.setText(theCommunityLogic.getOmissionsAnnotatedText(selectedItem));
        }

        public void disposing(EventObject arg0) {
        }

    }

    /**
     * When user selected an omission, he can press a button to mark it and open details
     */
    private class OmissionClassifyButtonListener implements XActionListener {

        public void actionPerformed(ActionEvent arg0) {
            try {
                int start = omissionsTextToSelect.getSelection().Min;
                int end = omissionsTextToSelect.getSelection().Max;
                if(end - start > 0 && theCommunityLogic.canAddOmission(start, end)) {

                    Omission[] omissions = theCommunityLogic.getOmissions();
                    omissionsErrorList.removeItems((short)0, (short)omissions.length);

                    Omission o = theCommunityLogic.addOmission(start, end);

                    omissions = theCommunityLogic.getOmissions();
                    int pos = -1;
                    String[] os = new String[omissions.length];
                    for (int i = 0; i < omissions.length; i++) {
                        Omission omission = omissions[i];
                        if(omission.equals(o)) {
                            pos = i;
                        }
                        os[i] = "o" + (i+1) + ": " + omission.toString();
                    }

                    omissionsErrorList.addItems(os, (short)0);
                    omissionsErrorList.selectItemPos((short)pos, true);
                    omissionsTextWithErrors.setText(theCommunityLogic.getOmissionsAnnotatedText(pos));
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, null, e);
            }
            
        }

        public void disposing(EventObject arg0) {
        }

    }

    /**
     * Delete the selected omission
     */
    private class OmissionExcludeButtonListener implements XActionListener {

        public void actionPerformed(ActionEvent arg0) {
            short pos = omissionsErrorList.getSelectedItemPos();
            if(pos >= 0) {
                Omission[] omissions = theCommunityLogic.getOmissions();
                omissionsErrorList.removeItems((short)0, (short)omissions.length);
                theCommunityLogic.removeOmission(pos);
                omissions = theCommunityLogic.getOmissions();
                String[] os = new String[omissions.length];
                for (int i = 0; i < omissions.length; i++) {
                    Omission omission = omissions[i];
                    os[i] = "o" + (i+1) + ": " + omission.toString();
                }
                omissionsErrorList.addItems(os, (short)0);
                omissionsTextWithErrors.setText(theCommunityLogic.getOmissionsAnnotatedText());

                if(omissionsErrorList.getItemCount() > 0) {
                    omissionsErrorList.selectItemPos((short)0, true);
                }
            }
            configureStateOmissions();
            setEnabledButtons();
        }

        public void disposing(EventObject arg0) {
        }

    }

    /**
     * Apply the entered details to the selected omission
     */
    private class OmissionApplyButtonListener implements XActionListener {

        public void actionPerformed(ActionEvent arg0) {
            applyOmission();
        }

        public void disposing(EventObject arg0) {
        }

    }

    private void applyOmission() {
        short pos = omissionsErrorList.getSelectedItemPos();
        if(pos >= 0) {

            String cat = omissionsCategoriesDropbox.getSelectedItem();
            String customCat = null;
            if(cat.startsWith(I18nLabelsLoader.ADDON_OMISSION_CATEGORY_CUSTOM)) {
                customCat = omissionsCategoryText.getText();
                cat = null;
            }
            String comment = omissionsCommentsText.getText();
            String replace = omissionsReplaceText.getText();

            theCommunityLogic.editOmission(pos, cat, comment, customCat, replace);

            Omission[] o = theCommunityLogic.getOmissions();
            omissionsErrorList.removeItems((short)0, (short)o.length);
            o = theCommunityLogic.getOmissions();
            String[] os = new String[o.length];
            for (int i = 0; i < o.length; i++) {
                Omission omission = o[i];
                os[i] = "o" + (i+1) + ": " + omission.toString();
            }

            omissionsErrorList.addItems(os, (short)0);
            omissionsErrorList.selectItemPos(pos, true);
            setIsControlEnable(omissionsApply, false);
            setEnabledButtons();
        }
    }

    private class OmissionTextListener implements XTextListener {

        public void textChanged(TextEvent arg0) {
            boolean changed = false;
            Omission o = theCommunityLogic.getOmission(omissionsErrorList.getSelectedItemPos());
            if(o == null) {
                return;
            }

            if(!safeEquals(omissionsCategoryText.getText(), o.getCustomCategory())) {
                changed = true;
            }

            if(!safeEquals(omissionsReplaceText.getText(), o.getReplaceBy())) {
                changed = true;
            }

            if(!safeEquals(omissionsCommentsText.getText(), o.getComment())) {
                changed = true;
            }

            if(changed) {
                setIsControlEnable(omissionsApply, true);
            }
        }

        public void disposing(EventObject arg0) {
        }

    }

    //**************************************************************************
    // Step buttons listeners
    //**************************************************************************
    private class PreviousActionListener implements XActionListener {

        public void actionPerformed(ActionEvent arg0) {
            applyDetails();
            int currentStep = getCurrentStep();
            int prevStep = getPreviousStep(currentStep);
            if(prevStep >= 0) {
                setCurrentStep(prevStep);
            }
            setEnabledSteps();
            setEnabledButtons();
        }

        public void disposing(EventObject arg0) {
        }
    }

    private class NextActionListener implements XActionListener {

        public void actionPerformed(ActionEvent arg0) {
            
            applyDetails();
            int currentStep = getCurrentStep();
            int nextStep = getNextStep(currentStep);
            if(currentStep == STEP_THANKS) {
                endExecute();
            } else if(currentStep == STEP_OMISSIONS) {
                setCurrentStep(STEP_THANKS);
                new SendReportThread().start();
            } else if(nextStep >= 0) {
                setCurrentStep(nextStep);
            }
            setEnabledSteps();
            setEnabledButtons();
            setDefaultButton(nextButtonName, true);
            
        }

        public void disposing(EventObject arg0) {
        }

    }

    protected class RoadmapItemStateChangeListener implements XItemListener {
        protected com.sun.star.lang.XMultiServiceFactory m_xMSFDialogModel;

        public RoadmapItemStateChangeListener(com.sun.star.lang.XMultiServiceFactory xMSFDialogModel) {
            m_xMSFDialogModel = xMSFDialogModel;
        }

        public void itemStateChanged(com.sun.star.awt.ItemEvent itemEvent) {
            try {
                // get the new ID of the roadmap that is supposed to refer to the new step of the dialogmodel
                int nNewID = itemEvent.ItemId;
                XPropertySet xDialogModelPropertySet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, m_xMSFDialogModel);
                int nOldStep = ((Integer) xDialogModelPropertySet.getPropertyValue("Step")).intValue();
                // in the following line "ID" and "Step" are mixed together.
                // In fact in this case they denot the same
                if (nNewID != nOldStep){
                    applyDetails();
                    xDialogModelPropertySet.setPropertyValue("Step", new Integer(nNewID));
                    setEnabledSteps();
                    setEnabledButtons();
                }
            } catch (com.sun.star.uno.Exception exception) {
                exception.printStackTrace(System.out);
            }
        }

        public void disposing(EventObject eventObject) {
        }
    }

    void applyDetails() {
        int step = getCurrentStep();
        if(step == STEP_OMISSIONS) {
            applyOmission();
        } else if(step == STEP_FALSE_ERRORS) {
            applyBadInt();
        }
    }


    //##########################################################################
    //
    // Threads to comunicate with the server
    //
    //##########################################################################

    private class CheckTokenAndGetCategoriesThread extends Thread {
        @Override
	public void run() {


            setIsControlEnable(authButton, false);
            setIsControlEnable(userNameText, false);
            setIsControlEnable(userPasswordText, false);
            
            String userName = getCommunityUsername();
            String token = getCommunityUserToken();

            if(userName != null && userName.length() > 0 && token != null && token.length() > 0) {
                 try {
                    authStatusLabel.setText( I18nLabelsLoader.ADDON_LOGIN_STATUS_NOTAUTH);
                    authProgressBar.setPropertyValue("ProgressValue", new Integer(5));
                    try {
                        authStatusLabel.setText(I18nLabelsLoader.ADDON_LOGIN_STATUS_OK);
                        // we take the chance to get the Error categories
                        isAuthenticated = false;
                        String[] categories = CommunityLogic.getCategoriesForUser(userName, token, authProgressBar);
                        if(categories != null && categories.length > 0) {
                            isAuthenticated = true;
                        }
                        updateOmissionCategories(categories);

                    }  catch (RestConnectionException ex) {
                        gotCommunicationError = true;
                        LOGGER.log(Level.SEVERE, null, ex);
                        authStatusLabel.setText(I18nLabelsLoader.ADDON_LOGIN_STATUS_COMMUNICATIONERROR);
                    } catch (CogrooException ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                        authStatusLabel.setText(I18nLabelsLoader.ADDON_LOGIN_STATUS_INVALIDUSER);
                    }
                    authProgressBar.setPropertyValue("ProgressValue", new Integer(100));
                    setControlsState();
                    
                } catch (com.sun.star.uno.Exception ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }

            
            setIsControlEnable(userNameText, true);
            setIsControlEnable(userPasswordText, true);
        }
    }

    private class AuthenticateUserThread extends Thread {
	@Override
	public void run() {
            setIsControlEnable(authButton, false);
            setIsControlEnable(userNameText, false);
            setIsControlEnable(userPasswordText, false);
            String userName = userNameText.getText();
            String passwd = userPasswordText.getText();
            try {
                String token = null;
                authStatusLabel.setText( I18nLabelsLoader.ADDON_LOGIN_STATUS_NOTAUTH);
                authProgressBar.setPropertyValue("ProgressValue", new Integer(5));
                try {
                    token = CommunityLogic.authenticateUser(userName, passwd, authProgressBar);
                    if(token != null) {
                        isAuthenticated = true;
                        saveCommunityUserToken(token);
                        saveCommunityUsername(userName);
                        authStatusLabel.setText(I18nLabelsLoader.ADDON_LOGIN_STATUS_OK);
                        // we take the chance to get the Error categories
                        String[] categories = CommunityLogic.getCategoriesForUser(userName, token, authProgressBar);
                        updateOmissionCategories(categories);
                        setDefaultButton(nextButtonName, true);
//                        setDefaultButton(authButton, false);
//                        setDefaultButton(cancelButtonName, false);
                        
                    } else {
                        isAuthenticated = false;
                        authStatusLabel.setText(I18nLabelsLoader.ADDON_LOGIN_STATUS_INVALIDUSER);
                        setDefaultButton(authButton, true);
                        setDefaultButton(nextButtonName, false);
                        setEnabledButtons();
                    }
                }  catch (RestConnectionException ex) {
                    gotCommunicationError = true;
                    LOGGER.log(Level.SEVERE, null, ex);
                    authStatusLabel.setText(I18nLabelsLoader.ADDON_LOGIN_STATUS_COMMUNICATIONERROR);
                } catch (CogrooException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                    authStatusLabel.setText(I18nLabelsLoader.ADDON_LOGIN_STATUS_INVALIDUSER);
                }
                authProgressBar.setPropertyValue("ProgressValue", new Integer(100));

                setIsControlEnable(userNameText, true);
                setIsControlEnable(userPasswordText, true);
                setControlsState();
            } catch (com.sun.star.uno.Exception ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
	}
    }

    private class SendReportThread extends Thread {


        @Override
	public void run() {
            String userName = getCommunityUsername();
            String token = getCommunityUserToken();

            if(userName != null && userName.length() > 0 && token != null && token.length() > 0) {
                 try {
                    thanksStatusLabel.setText( I18nLabelsLoader.ADDON_THANKS_STATUS_SENDING);
                    thanksProgressBar.setPropertyValue("ProgressValue", new Integer(5));
                    try {
                        thanksStatusLabel.setText(I18nLabelsLoader.ADDON_THANKS_STATUS_SENDING);

                        String link = theCommunityLogic.submitErrorReport(userName, token, thanksProgressBar);

                        thanksStatusLabel.setText(I18nLabelsLoader.ADDON_THANKS_STATUS_DONE);
//                        thanksReportLink.setText(link);
//                        thanksReportLink.setURL(link);

                        thanksProgressBar.setPropertyValue("ProgressValue", new Integer(100));

                    }  catch (RestConnectionException ex) {
                        gotCommunicationError = true;
                        LOGGER.log(Level.SEVERE, null, ex);
                        authStatusLabel.setText(I18nLabelsLoader.ADDON_THANKS_STATUS_ERROR);
                    } catch (CogrooException ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                        authStatusLabel.setText(I18nLabelsLoader.ADDON_THANKS_STATUS_ERROR);
                    }
                    authProgressBar.setPropertyValue("ProgressValue", new Integer(100));
                    setControlsState();
                } catch (com.sun.star.uno.Exception ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
