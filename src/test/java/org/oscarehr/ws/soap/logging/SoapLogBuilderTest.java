/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */

package org.oscarehr.ws.soap.logging;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.oscarehr.ws.common.annotation.LogHeaderInbound;
import org.oscarehr.ws.common.annotation.MaskParameter;
import org.oscarehr.ws.common.annotation.SkipContentLoggingInbound;
import org.oscarehr.ws.external.soap.logging.SoapLogBuilder;
import org.oscarehr.ws.external.soap.logging.model.SoapServiceLog;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

public class SoapLogBuilderTest
{
    public void noAnnotationsStub()
    {
    }

    @SkipContentLoggingInbound
    public void skipContentLoggingStub()
    {
    }

    @LogHeaderInbound
    public void logHeaderInbound()
    {

    }

    @MaskParameter
    public void maskParameterStubDefault()
    {
    }

    @MaskParameter(fields = "mask_me")
    public void maskParameterStubSpecifiedField()
    {
    }

    @MaskParameter(fields = {"secret0", "secret1"})
    public void maskParameterStubArray()
    {
    }

    private static HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
    private SoapLogBuilder logBuilder;

    @BeforeClass
    public static void setupMockRequest()
    {
        when(mockRequest.getRequestURL()).thenReturn(new StringBuffer("www.oscarhost.ca"));
    }

    @Before
    public void makeSoapLogBuilder()
    {
        logBuilder = new SoapLogBuilder();
    }

    @Test
    public void testBaseCaseNoAnnotations()
    {
        String testData = makeSoapMessage("color", "red", "shape", "square");
        String expectData = makeSoapMessageBodyOnly("color", "red", "shape", "square");

        configureBuilder(testData, "noAnnotationsStub");
        SoapServiceLog logEntry = logBuilder.buildSoapLog();

        assertThat(logEntry.getPostData(), equalTo(expectData));
    }

    @Test
    public void testStripHeader()
    {
        String expectData = makeSoapMessageBodyOnly("color", "red", "shape", "square");
        String testData = makeSoapMessage("color", "red", "shape", "square",true);

        configureBuilder(testData, "noAnnotationsStub");
        SoapServiceLog logEntry = logBuilder.buildSoapLog();

        assertThat(logEntry.getPostData(), equalTo(expectData));
    }

    @Test
    public void testLogHeaderInboundAnnotation()
    {
        String expectData = makeSoapMessage("color", "red", "shape", "square", true);
        String testData = makeSoapMessage("color", "red", "shape", "square",true);

        configureBuilder(testData, "logHeaderInbound");
        SoapServiceLog logEntry = logBuilder.buildSoapLog();

        assertThat(logEntry.getPostData(), equalTo(expectData));
    }

    @Test
    public void testDefaultSkipContentLogging()
    {
        String testData = makeSoapMessage("lab_provider", "medical labs ltd.", "labData", "really_long_string");
        String expected = SkipContentLoggingInbound.SKIP_CONTENT_LOGGING_INBOUND;

        configureBuilder(testData, "skipContentLoggingStub");
        SoapServiceLog logEntry = logBuilder.buildSoapLog();

        assertThat(logEntry.getPostData(), equalTo(expected));
    }

    @Test
    public void testDefaultParameterMasking()
    {
        String testData = makeSoapMessage("user", "my name", "password", "my secret");
        String expected = makeSoapMessageBodyOnly("user", "my name", "password", MaskParameter.MASK);

        configureBuilder(testData, "maskParameterStubDefault");
        SoapServiceLog logEntry = logBuilder.buildSoapLog();

        assertThat(logEntry.getPostData(), equalTo(expected));
    }

    @Test
    public void testDefaultParameterMaskingMissing()
    {
        String testData = makeSoapMessage("user", "my name", "pazzword", "my zecret");
        String expectData = makeSoapMessageBodyOnly("user", "my name", "pazzword", "my zecret");

        configureBuilder(testData, "maskParameterStubDefault");
        SoapServiceLog logEntry = logBuilder.buildSoapLog();

        assertThat(logEntry.getPostData(), equalTo(expectData));
    }

    @Test
    public void testTargetedParameterMasking()
    {
        String testData = makeSoapMessage("mask_me", "my secret", "version", "1.0");
        String expected = makeSoapMessageBodyOnly("mask_me", MaskParameter.MASK, "version", "1.0");

        configureBuilder(testData, "maskParameterStubSpecifiedField");
        SoapServiceLog logEntry = logBuilder.buildSoapLog();

        assertThat(logEntry.getPostData(), equalTo(expected));
    }

    @Test
    public void testTargetedMaskingMissingField()
    {
        String testData = makeSoapMessage("username", "my name", "flask_me", "my whiskey");
        String expectData = makeSoapMessageBodyOnly("username", "my name", "flask_me", "my whiskey");

        configureBuilder(testData, "maskParameterStubSpecifiedField");
        SoapServiceLog logEntry = logBuilder.buildSoapLog();

        assertThat(logEntry.getPostData(), equalTo(expectData));
    }

    @Test
    public void testMultipleMasking()
    {
        String testData = makeSoapMessage("secret0", "my secret", "secret1", "my other secret");
        String expected = makeSoapMessageBodyOnly("secret0", MaskParameter.MASK, "secret1", MaskParameter.MASK);

        configureBuilder(testData, "maskParameterStubArray");
        SoapServiceLog logEntry = logBuilder.buildSoapLog();

        assertThat(logEntry.getPostData(), equalTo(expected));
    }

    @Test
    public void testMultipleMaskingOutOfOrder()
    {
        String testData = makeSoapMessage("secret1", "my secret", "secret0", "my other secret");
        String expected = makeSoapMessageBodyOnly("secret1", MaskParameter.MASK, "secret0", MaskParameter.MASK);

        configureBuilder(testData, "maskParameterStubArray");
        SoapServiceLog logEntry = logBuilder.buildSoapLog();

        assertThat(logEntry.getPostData(), equalTo(expected));
    }

    @Test
    public void testMultipleMaskingMissingOneField()
    {
        String testData = makeSoapMessage("username", "my_name", "secret1", "my other secret");
        String expected = makeSoapMessageBodyOnly("username", "my_name", "secret1", MaskParameter.MASK);

        configureBuilder(testData, "maskParameterStubArray");
        SoapServiceLog logEntry = logBuilder.buildSoapLog();

        assertThat(logEntry.getPostData(), equalTo(expected));
    }

    @Test
    public void testMultipleMaskingMissingAllFields()
    {
        String testData = makeSoapMessage("not_secret0", "red", "not_secret1", "blue");
        String expectData = makeSoapMessageBodyOnly("not_secret0", "red", "not_secret1", "blue");

        configureBuilder(testData, "maskParameterStubArray");
        SoapServiceLog logEntry = logBuilder.buildSoapLog();

        assertThat(logEntry.getPostData(), equalTo(expectData));
    }

    @Test
    public void testDoubleElementMasking()
    {
        String testData = makeSoapMessage("mask_me","my secret", "mask_me", "1234");
        String expected = makeSoapMessageBodyOnly("mask_me", MaskParameter.MASK, "mask_me", MaskParameter.MASK);

        configureBuilder(testData, "maskParameterStubSpecifiedField");
        SoapServiceLog logEntry = logBuilder.buildSoapLog();

        assertThat(logEntry.getPostData(), equalTo(expected));
    }

    @Test
    public void testEmptyBody()
    {
        String testData = "";

        configureBuilder(testData, "maskParameterStubDefault");
        SoapServiceLog logEntry = logBuilder.buildSoapLog();

        assertThat(logEntry.getPostData(), equalTo(testData));
    }

    @Test
    public void testEmptySoapBody()
    {
        String testData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><env:Envelope xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:tns=\"http://v1.soap.external.ws.oscarehr.org/\" xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ins0=\"http://v1.soap.external.ws.oscarehr.org/\">";
        testData += "<env:Body></env:Body></env:Envelope>";
        configureBuilder(testData, "maskParameterStubDefault");
        SoapServiceLog logEntry = logBuilder.buildSoapLog();

        assertThat(logEntry.getPostData().trim(), equalTo("<?xml version=\"1.0\" encoding=\"UTF-8\"?><env:Body xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\"/>"));
    }

    @Test
    public void testCustomNamespace()
    {
        String testData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><soapenv:Envelope xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:tns=\"http://v1.soap.external.ws.oscarehr.org/\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ins0=\"http://v1.soap.external.ws.oscarehr.org/\">";
        testData += "<soapenv:Body></soapenv:Body></soapenv:Envelope>";
        configureBuilder(testData, "maskParameterStubDefault");
        SoapServiceLog logEntry = logBuilder.buildSoapLog();

        assertThat(logEntry.getPostData().trim(), equalTo("<?xml version=\"1.0\" encoding=\"UTF-8\"?><soapenv:Body xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"/>"));
    }

    @Test
    public void testNullBody()
    {
        String testData = null;

        configureBuilder(testData, "maskParameterStubDefault");
        SoapServiceLog logEntry = logBuilder.buildSoapLog();

        assertThat(logEntry.getPostData(), equalTo(testData));
    }

    /**
     * Test helper method to parametrically generate SOAP messages
     *
     * @param arg0 name of the first element
     * @param arg0Value value of the first element
     * @param arg1 name of the second element
     * @param arg1Value value of the second element
     * @param includeHeader if true include header in soap message
     * @return example SOAP login message containing the elements and values specified.
     */
    private String makeSoapMessage(String arg0, String arg0Value, String arg1, String arg1Value, boolean includeHeader)
    {
        String soapMessage = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><env:Envelope xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:tns=\"http://v1.soap.external.ws.oscarehr.org/\" xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ins0=\"http://v1.soap.external.ws.oscarehr.org/\">";
        if (includeHeader)
        {
            soapMessage += "<env:Header><wsse:Security xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\"><wsse:UsernameToken wsu:Id=\"UsernameToken-1\" xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\"><wsse:Username>99</wsse:Username><wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">6144625613578</wsse:Password></wsse:UsernameToken></wsse:Security></env:Header>";
        }
        soapMessage += "<env:Body><ins0:login>%s%s</ins0:login></env:Body></env:Envelope>";

        String firstElement = "<" + arg0 + ">" + arg0Value + "</" + arg0 + ">";
        String secondElement = "<" + arg1 + ">" + arg1Value + "</" + arg1 + ">";

        return String.format(soapMessage, firstElement, secondElement);
    }

    private String makeSoapMessage(String arg0, String arg0Value, String arg1, String arg1Value)
    {
        return makeSoapMessage(arg0, arg0Value, arg1, arg1Value, false);
    }

    private String makeSoapMessageBodyOnly(String arg0, String arg0Value, String arg1, String arg1Value)
    {
        String soapMessage = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><env:Body xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\"><ins0:login xmlns:ins0=\"http://v1.soap.external.ws.oscarehr.org/\">%s%s</ins0:login></env:Body>";

        String firstElement = "<" + arg0 + ">" + arg0Value + "</" + arg0 + ">";
        String secondElement = "<" + arg1 + ">" + arg1Value + "</" + arg1 + ">";

        return String.format(soapMessage, firstElement, secondElement);
    }

    /**
     * Test helper method to configure the SOAPLogBuilder as if the data were being posted to the specified endpoint
     *
     * @param postData string containing xml to be posted
     * @param method method invoked by the SOAP endpoint
     */
    private void configureBuilder(String postData, String method)
    {
        try
        {
            Method testMethod = this.getClass().getMethod(method);
            logBuilder.addTransportData(mockRequest, postData);
            logBuilder.addMethodData(mockRequest, testMethod);
        }
        catch (NoSuchMethodException ex)
        {
            fail("Soap Logging test could not find required stub method: " + method);
        }
    }
}
