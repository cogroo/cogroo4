package org.cogroo.errorreport;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;

import org.cogroo.ResourcesUtil;
import org.cogroo.tools.errorreport.model.ErrorReport;
import org.junit.Before;
import org.junit.Test;


public class ErrorReportAccessTest {
	
	private ErrorReportAccess errorReportAccess;

	@Before
	public void setup() {
		this.errorReportAccess = new ErrorReportAccess();
	}
	
	@Test
	public void testCanRead() throws IOException {
		String xml = ResourcesUtil.getResourceAsString(this.getClass(), "ErrorReport1.xml");
		StringReader sr = new StringReader(xml);
		
		ErrorReport er = errorReportAccess.getErrorReport(sr);
		assertNotNull(er.getOmissions());
		assertNotNull(er.getBadInterventions());
	}
	
	@Test
    public void testSerialize() throws IOException {
	  ErrorReport er = new ErrorReport();
	  er.setText("A text");
	  er.setVersion("1.0.0");
	  
	  String output = errorReportAccess.serialize(er);
	  
        assertNotNull(output);
        assertTrue(output.contains(">A text<"));
    }

}
