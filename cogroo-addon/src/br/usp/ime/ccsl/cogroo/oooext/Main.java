package br.usp.ime.ccsl.cogroo.oooext;

import java.util.ArrayList;
import java.util.List;

import br.ccsl.cogroo.entities.Mistake;
import br.usp.ime.ccsl.cogroo.oooext.addon.contextmenu.ContextMenuInterceptor;
import br.usp.ime.ccsl.cogroo.oooext.dialogs.about.AboutThread;
import br.usp.ime.ccsl.cogroo.oooext.dialogs.reporterror.ErrorReportDialogThread;
import br.usp.ime.ccsl.cogroo.oooext.util.SelectedTextExtractor;

import com.sun.star.beans.NamedValue;
import com.sun.star.beans.PropertyValue;
import com.sun.star.frame.XController;
import com.sun.star.frame.XModel;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.Locale;
import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.lib.uno.helper.Factory;
import com.sun.star.lib.uno.helper.WeakBase;
import com.sun.star.linguistic2.ProofreadingResult;
import com.sun.star.linguistic2.SingleProofreadingError;
import com.sun.star.linguistic2.XLinguServiceEventListener;
import com.sun.star.registry.XRegistryKey;
import com.sun.star.text.TextMarkupType;
import com.sun.star.text.XTextDocument;
import com.sun.star.ui.XContextMenuInterception;
import com.sun.star.ui.XContextMenuInterceptor;
import com.sun.star.uno.AnyConverter;
import com.sun.star.uno.Type;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;


public final class Main extends WeakBase
   implements com.sun.star.linguistic2.XProofreader,
              com.sun.star.lang.XServiceInfo,
              com.sun.star.linguistic2.XLinguServiceEventBroadcaster,
              com.sun.star.task.XJobExecutor,
              com.sun.star.lang.XServiceDisplayName,
              com.sun.star.lang.XInitialization,
              com.sun.star.frame.XDispatch,
              com.sun.star.frame.XDispatchProvider,
              com.sun.star.task.XJob
{

    private XComponentContext m_xContext;

    private com.sun.star.frame.XFrame m_xFrame;
    private static final String m_implementationName = Main.class.getName();

    private static final String[] m_serviceNames = {
        "com.sun.star.linguistic2.Proofreader",
	"br.usp.ime.ccsl.cogroo.oooext.Main",
        "com.sun.star.frame.ProtocolHandler",
        "br.usp.ime.ccsl.cogroo.oooext.Job"};

    /* related to XLinguServiceEventBroadcaster */
	private List<XLinguServiceEventListener> xEventListeners = new ArrayList<XLinguServiceEventListener>();

    public Main( XComponentContext context )
    {
        changeContext(context);

        // registry menu
        

    }

    public void changeContext(XComponentContext xCompContext) {
        this.m_xContext = xCompContext;
        Resources.changeContext(xCompContext);
    }

    public static XSingleComponentFactory __getComponentFactory( String sImplementationName ) {
        XSingleComponentFactory xFactory = null;

        if ( sImplementationName.equals( m_implementationName ) )
            xFactory = new SingletonFactory();
        return xFactory;
    }

    public static boolean __writeRegistryServiceInfo( XRegistryKey xRegistryKey ) {
        return Factory.writeRegistryServiceInfo(m_implementationName,
                                                m_serviceNames,
                                                xRegistryKey);
    }

    // com.sun.star.linguistic2.XSupportedLocales:
    public com.sun.star.lang.Locale[] getLocales()
    {
        return new Locale[] { new Locale("pt", "BR", "") };
    }

    public boolean hasLocale(com.sun.star.lang.Locale aLocale)
    {
        return GCUtil.isKnownLocale(getLocales(), aLocale);
    }

    // com.sun.star.linguistic2.XProofreader:
    public boolean isSpellChecker()
    {
        return false;
    }

    public com.sun.star.linguistic2.ProofreadingResult doProofreading(final String docID,
			final String paraText, final Locale locale,
			final int startOfSentencePos, final int sugEndOfSentencePos,
			final PropertyValue[] aLanguagePortions) throws com.sun.star.lang.IllegalArgumentException
    {

		final ProofreadingResult result = new ProofreadingResult();
		result.nBehindEndOfSentencePosition = sugEndOfSentencePos
				- startOfSentencePos;
		result.xProofreader = this;
		result.aLocale = locale;
		result.aDocumentIdentifier = docID;
		result.aText = paraText;

		if (startOfSentencePos == sugEndOfSentencePos || paraText == null
				|| paraText.trim().length() == 0) {
			return result;
		}

		//result.nBehindEndOfSentencePosition = paraText.length();

		// call CoGrOO
//		try {
 			List<Mistake> mistakeList = new ArrayList<Mistake>();
                        int end = CogrooSingleton.getInstance(this.m_xContext).checkSentence(paraText.substring(startOfSentencePos), mistakeList);
                        if(end > 0) {
                           result.nBehindEndOfSentencePosition = startOfSentencePos + end;
                        }

			List<SingleProofreadingError> errorList = new ArrayList<SingleProofreadingError>();

			for (Mistake mistake : mistakeList) {
				final SingleProofreadingError e1 = new SingleProofreadingError();

				e1.nErrorType = TextMarkupType.PROOFREADING;
				e1.aFullComment = mistake.getFullMessage();
				e1.aShortComment = mistake.getShortMessage();
				e1.aSuggestions = mistake.getSuggestions().clone();
				e1.nErrorStart = mistake.getStart() + startOfSentencePos;
				e1.nErrorLength = mistake.getEnd() - mistake.getStart();
				e1.aRuleIdentifier = mistake.getRuleIdentifier();

				errorList.add(e1);
			}

			result.aErrors = errorList
					.toArray(new SingleProofreadingError[errorList.size()]);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		return result;
    }

    public void ignoreRule(String aRuleIdentifier, com.sun.star.lang.Locale aLocale) throws com.sun.star.lang.IllegalArgumentException
    {
        CogrooSingleton.getInstance(this.m_xContext).ignoreRule(aRuleIdentifier);
    }

    public void resetIgnoreRules()
    {
        CogrooSingleton.getInstance(this.m_xContext).resetIgnoredRules();
    }

    // com.sun.star.lang.XServiceInfo:
    public String getImplementationName() {
         return m_implementationName;
    }

    public boolean supportsService( String sService ) {
        int len = m_serviceNames.length;

        for( int i=0; i < len; i++) {
            if (sService.equals(m_serviceNames[i]))
                return true;
        }
        return false;
    }

    public String[] getSupportedServiceNames() {
		return getServiceNames();
    }


    // To be accessed by a static context
	public static String[] getServiceNames() {
		return m_serviceNames;
	}


    /*>> XLinguServiceEventBroadcaster */


    public boolean addLinguServiceEventListener(XLinguServiceEventListener xLinEvLis) {

		boolean ret = false;
		if (xLinEvLis == null) {
			ret = false;
		} else {
			xEventListeners.add(xLinEvLis);
			ret = true;
		}

		return ret;
    }

    public boolean removeLinguServiceEventListener(XLinguServiceEventListener xLinEvLis) {
		boolean ret = false;
		if (xLinEvLis == null) {
			ret = false;
		} else {
			if (xEventListeners.contains(xLinEvLis)) {
				xEventListeners.remove(xLinEvLis);
				ret = true;
			} else {
				ret = false;
			}
		}
		return ret;
    }

    /*<< XLinguServiceEventBroadcaster*/

    /*>> XJobExecutor */
    public void trigger(String sEvent) {
        if (sEvent.equals("about")) {
            final AboutThread aboutthread = new AboutThread(this.m_xContext);
            aboutthread.start();
        }
    }

    /*<< XJobExecutor */

    /* >> XServiceDisplayName */
    public String getServiceDisplayName(Locale arg0) {
        return "CoGrOO";
    }

    // com.sun.star.lang.XInitialization:
    public void initialize( Object[] object )
        throws com.sun.star.uno.Exception
    {
        if ( object.length > 0 )
        {
            m_xFrame = (com.sun.star.frame.XFrame)UnoRuntime.queryInterface(
                com.sun.star.frame.XFrame.class, object[0]);
        }
    }

    // com.sun.star.frame.XDispatch:
     public void dispatch( com.sun.star.util.URL aURL,
                           com.sun.star.beans.PropertyValue[] aArguments )
    {
         if ( aURL.Protocol.compareTo("br.usp.ime.ccsl.cogroo.oooext:") == 0 )
        {
            if ( aURL.Path.compareTo("ReportError") == 0 )
            {
                XTextDocument xDoc = (XTextDocument) UnoRuntime.queryInterface(
                XTextDocument.class, m_xFrame.getController().getModel());
                SelectedTextExtractor extractor = new SelectedTextExtractor(m_xContext, xDoc);

                final ErrorReportDialogThread reporterrorthread = new ErrorReportDialogThread(this.m_xContext);
                reporterrorthread.setText(extractor.getSelectedText());
                reporterrorthread.start();
            }
        }
    }

    public void addStatusListener( com.sun.star.frame.XStatusListener xControl,
                                    com.sun.star.util.URL aURL )
    {
        // add your own code here
    }

    public void removeStatusListener( com.sun.star.frame.XStatusListener xControl,
                                       com.sun.star.util.URL aURL )
    {
        // add your own code here
    }

    // com.sun.star.frame.XDispatchProvider:
    public com.sun.star.frame.XDispatch queryDispatch( com.sun.star.util.URL aURL,
                                                       String sTargetFrameName,
                                                       int iSearchFlags )
    {
        if ( aURL.Protocol.compareTo("br.usp.ime.ccsl.cogroo.oooext:") == 0 )
        {
            if ( aURL.Path.compareTo("ReportError") == 0 )
                return this;
        }
        return null;
    }

    // com.sun.star.frame.XDispatchProvider:
    public com.sun.star.frame.XDispatch[] queryDispatches(
         com.sun.star.frame.DispatchDescriptor[] seqDescriptors )
    {
        int nCount = seqDescriptors.length;
        com.sun.star.frame.XDispatch[] seqDispatcher =
            new com.sun.star.frame.XDispatch[seqDescriptors.length];

        for( int i=0; i < nCount; ++i )
        {
            seqDispatcher[i] = queryDispatch(seqDescriptors[i].FeatureURL,
                                             seqDescriptors[i].FrameName,
                                             seqDescriptors[i].SearchFlags );
        }
        return seqDispatcher;
    }

    
    public Object execute(NamedValue[] args) throws IllegalArgumentException, com.sun.star.uno.Exception {
        XModel xModel = null;
        boolean correctEvent = false;

        for (NamedValue arg : args) {
            if (arg.Name.equals("Environment")) {
                NamedValue[] values = (NamedValue[]) AnyConverter.toObject(
                        new Type("[]com.sun.star.beans.NamedValue"),
                        arg.Value);
                for (NamedValue value : values) {
                    if (value.Name.equals("EnvType")
                            && value.Value.equals("DOCUMENTEVENT")) {
                        correctEvent = true;
                    } else if (value.Name.equals("EventName")) {
                    } else if (value.Name.equals("Model")) {
                        xModel = (XModel)UnoRuntime.queryInterface(XModel.class,
                                value.Value);
                    }
                }
            }
        }


        if (correctEvent) {
            ContextMenuInterceptor interceptor = new ContextMenuInterceptor();
            XContextMenuInterceptor xInterceptor = (XContextMenuInterceptor)UnoRuntime.queryInterface(XContextMenuInterceptor.class,
                    interceptor);
            XController xController = xModel.getCurrentController();
            XContextMenuInterception xInterception = (XContextMenuInterception)UnoRuntime.queryInterface(XContextMenuInterception.class, xController);
            xInterception.registerContextMenuInterceptor(xInterceptor);
            
        }
        return null;
    }

}