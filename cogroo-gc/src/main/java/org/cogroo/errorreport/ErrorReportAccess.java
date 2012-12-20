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

package org.cogroo.errorreport;

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
import org.cogroo.tools.checker.rules.exception.RulesException;
import org.xml.sax.InputSource;

import org.cogroo.tools.errorreport.model.ErrorReport;


public class ErrorReportAccess {
	
	private static final Logger LOGGER = Logger.getLogger(ErrorReportAccess.class);
	private static final String ENCODING = "UTF-8";
	

	public ErrorReport getErrorReport(Reader xml) {
		// Unmarshall rules file.
		ErrorReport errorReport = null;
		try {
			InputSource inputSource = new InputSource(xml);
			JAXBContext context = JAXBContext.newInstance(ErrorReport.class);
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
			LOGGER.debug("Persisting ErrorReport in a XML...");
		String ret = null;
		try {
			StringWriter sw = new StringWriter();

			JAXBContext context = JAXBContext.newInstance(ErrorReport.class);
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
