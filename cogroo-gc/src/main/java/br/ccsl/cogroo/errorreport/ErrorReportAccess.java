
package br.ccsl.cogroo.errorreport;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

import br.ccsl.cogroo.tools.checker.rules.exception.RulesException;
import br.ccsl.cogroo.tools.errorreport.model.ErrorReport;


public class ErrorReportAccess {
	
	private static final Logger LOGGER = Logger.getLogger(ErrorReportAccess.class);
	
	private static final String PACKAGE = "br.usp.pcs.lta.cogroo.errorreport.model";
	private static final String ENCODING = "UTF-8";
	

	public ErrorReport getErrorReport(Reader xml) {
		// Unmarshall rules file.
		ErrorReport errorReport = null;
		try {
			InputSource inputSource = new InputSource(xml);
			ClassLoader cl = ErrorReport.class.getClassLoader();
			if(cl == null) {
				LOGGER.error("couldn't create class loader.");
			}
			JAXBContext context = JAXBContext.newInstance(PACKAGE, cl);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			unmarshaller.setEventHandler(new ValidationEventHandler() {
				// allow unmarshalling to continue even if there are errors
				@SuppressWarnings("synthetic-access")
				public boolean handleEvent(ValidationEvent ve) {
					// ignore warnings
					if (ve.getSeverity() != ValidationEvent.WARNING) {
						ValidationEventLocator vel = ve.getLocator();
						LOGGER.warn("Line:Col[" + vel.getLineNumber() + ":" + vel.getColumnNumber() + "]:" + ve.getMessage());
					}
					return true;
				}
			});
			errorReport = (ErrorReport) unmarshaller.unmarshal(inputSource);
		} catch (JAXBException e) {
			LOGGER.error("Error parsing file", e);
			throw new RulesException("Failed reading file");
		}
		return errorReport;
	}

	public static String serialize(ErrorReport newRules) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Persisting ErrorReport in an XML...");
		String ret = null;
		try {
			StringWriter sw = new StringWriter();
			ClassLoader cl = ErrorReport.class.getClassLoader();
			if(cl == null) {
				LOGGER.error("couldn't create class loader.");
			}
			JAXBContext context = JAXBContext.newInstance(PACKAGE, cl);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, ENCODING);
			marshaller.marshal(newRules, sw);
			sw.close();
			ret = sw.toString();
		} catch (PropertyException e) {
			throw new RulesException("Failed to save the ErrorReport");
		} catch (IOException e) {
			throw new RulesException("Failed to save the ErrorReport");
		} catch (JAXBException e) {
			throw new RulesException("Failed to save the ErrorReport: " + e.getMessage());
		}
		return ret;
	}

}
