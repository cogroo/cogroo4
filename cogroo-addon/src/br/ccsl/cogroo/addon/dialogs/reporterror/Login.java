/**
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
package br.ccsl.cogroo.addon.dialogs.reporterror;

import com.sun.star.container.NoSuchElementException;
import com.sun.star.lang.WrappedTargetException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.ccsl.cogroo.addon.LoggerImpl;
import br.ccsl.cogroo.addon.addon.AbstractAddOn;
import br.ccsl.cogroo.addon.i18n.I18nLabelsLoader;

import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.XActionListener;
import com.sun.star.awt.XButton;
import com.sun.star.awt.XDialog;
import com.sun.star.awt.XFixedText;
import com.sun.star.beans.PropertyValue;
import com.sun.star.frame.DispatchDescriptor;
import com.sun.star.frame.XDispatch;
import com.sun.star.lang.EventObject;
import com.sun.star.uno.XComponentContext;
import com.sun.star.util.URL;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class Login extends AbstractAddOn {
    // Logger

    protected static Logger LOGGER = LoggerImpl.getLogger(Login.class.getCanonicalName());

    public static final String PROTOCOL_PATH = "reportErrorDialog";


    protected XDialog dialog;
    protected XFixedText licenseView;
    protected XButton licenseButton;
    protected boolean liceneDisplayed = false;
    private String text;

    private static final String ERROR_TEXT = "error.text";
    private static final String ERROR_COMMENTS = "error.comments";

    public Login(XComponentContext context, String text) {
        super(context);
        this.text = text;
    }

    protected void init() {
        try {

            final DialogBuilder builder = new DialogBuilder(this.context, 100, 100,
                    200, 220, I18nLabelsLoader.ADDON_REPORT_ERROR);

            int left = 10;
            int pos = 10;
            int width = 180;
            int weigth = 10;

            builder.addLabel(I18nLabelsLoader.ADDON_REPORT_ERROR_TEXT + ":", "error.label.text", left, pos, width, weigth);

            pos += weigth;

            builder.addTextArea(text, ERROR_TEXT, left, pos, width, weigth * 10);

            pos += weigth * 11;

            builder.addLabel(I18nLabelsLoader.ADDON_REPORT_ERROR_COMMENTS + ":", "error.label.comments", left, pos, width, weigth);

            pos += weigth;

            builder.addTextArea("", ERROR_COMMENTS, left, pos, width, weigth * 5);

            pos += weigth * 6;

            licenseButton = builder.addButton(
                    I18nLabelsLoader.ADDON_REPORT_ERROR_CANCEL,
                    "error.cancel", 35, pos, width, weigth * 2);
            licenseButton.addActionListener(new XActionListener() {

                public void actionPerformed(ActionEvent arg0) {
                    setVisible(false);

                }

                public void disposing(EventObject arg0) {
                }
            });

            XButton button = builder.addButton(I18nLabelsLoader.ADDON_REPORT_ERROR_SEND, "error.send", 95, pos, 50, 15);
            button.addActionListener(new XActionListener() {

                public void actionPerformed(ActionEvent arg0) {
                    try {

                        
                        String text = builder.getTextContent(ERROR_TEXT);
                        String comments = builder.getTextContent(ERROR_COMMENTS);
                        String user = "dummy";

                        java.net.URL url = new java.net.URL("http://localhost:8080/cogrooErrorReport");
                        Map<String, String> d = new HashMap<String, String>();
                        d.put("userName", user);
                        d.put("text", text);
                        d.put("comment", comments);
                        d.put("version", I18nLabelsLoader.ADDON_VERSION);
                        request(false, "POST", url, d);
                        setVisible(false);
                    } catch (NoSuchElementException ex) {
                        Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (java.net.ConnectException ex) {
                        Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (WrappedTargetException ex) {
                        Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                public void disposing(EventObject arg0) {
                }
            });
            this.dialog = builder.getDialog();

            dialog.execute();

        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, "Uncaught exception", e);

        }

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("<<< About.init()");
        }

    }

    /**
     * Show/Close the Aboutdialog
     *
     * @param b
     */
    public void setVisible(boolean b) {
        if (b) {
            this.init();
        } else if (dialog != null) {
            dialog.endExecute();
        }
    }

    public String getImplementationName() {
        return this.getClass().getName();

    }

    public void dispatch(URL url, PropertyValue[] arg1) {

        if (url.Protocol.equals(PROTOCOL_URL) && url.Path.equals(PROTOCOL_PATH)) {
            setVisible(true);
        }

    }

    public XDispatch queryDispatch(URL url, String arg1, int arg2) {
        if (url.Protocol.equals(PROTOCOL_URL) && url.Path.equals(PROTOCOL_PATH)) {
            return this;
        }

        return null;
    }

    public XDispatch[] queryDispatches(DispatchDescriptor[] arg0) {
        XDispatch[] lDispatcher = new XDispatch[arg0.length];

        for (int i = 0; i < arg0.length; ++i) {
            lDispatcher[i] = queryDispatch(arg0[i].FeatureURL,
                    arg0[i].FrameName, arg0[i].SearchFlags);
        }

        return lDispatcher;
    }

    private static void request(boolean quiet, String method, java.net.URL url,
            Map<String, String> body) throws IOException {

        if(LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "[issuing request: " + method + " " + url + "]");
        }
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);

        // write auth header
        // BASE64Encoder encoder = new BASE64Encoder();
        // String encodedCredential = encoder.encode( (username + ":" +
        // password).getBytes() );
        // connection.setRequestProperty("Authorization", "BASIC " +
        // encodedCredential);

        // write body if we're doing POST or PUT
//        byte buffer[] = new byte[8192];
//        int read = 0;
        if (body != null) {
            connection.setDoOutput(true);
            OutputStream output = connection.getOutputStream();
            DataOutputStream out2 = new DataOutputStream(output);
            out2.writeBytes(convert(body));
        }

        // do request
        long time = System.currentTimeMillis();
        connection.connect();

//		InputStream responseBodyStream = connection.getInputStream();
//		StringBuffer responseBody = new StringBuffer();
        // while ((read = responseBodyStream.read(buffer)) != -1)
        // {
        // responseBody.append(new String(buffer, 0, read));
        // }
        connection.disconnect();
        time = System.currentTimeMillis() - time;

        // start printing output
//		if (!quiet)
//			System.out.println("[read " + responseBody.length() + " chars in "
//					+ time + "ms]");

        // look at headers
        // the 0th header has a null key, and the value is the response line
        // ("HTTP/1.1 200 OK" or whatever)
        if (!quiet) {
            String header = null;
            String headerValue = null;
            int index = 0;
            while ((headerValue = connection.getHeaderField(index)) != null) {
                header = connection.getHeaderFieldKey(index);

                if (header == null) {
                    System.out.println(headerValue);
                } else {
                    System.out.println(header + ": " + headerValue);
                }

                index++;
            }
        }

        // dump body
//		System.out.print(responseBody);
    }

    private static String convert(Map<String, String> data) {
        StringBuilder sb = new StringBuilder();
        for (String key : data.keySet()) {
            sb.append(encode(key) + "=" + encode(data.get(key)));
            sb.append("&");
        }
        return sb.subSequence(0, sb.length() - 1).toString();
    }

    private static String encode(String text) {
        try {
            return URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
