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

package org.oscarehr.ws.external.soap.logging;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.annotation.Annotation;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxrs.utils.AnnotationUtils;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.common.annotation.LogHeaderInbound;
import org.oscarehr.ws.common.annotation.MaskParameter;
import org.oscarehr.ws.common.annotation.SkipContentLoggingInbound;
import org.oscarehr.ws.common.annotation.SkipContentLoggingOutbound;
import org.oscarehr.ws.external.soap.logging.model.SoapServiceLog;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import java.lang.reflect.Method;
import java.util.*;

/**
 * This class keeps track of all data needed to create a single entry for a request to response SOAP exchange
 */
public class SoapLogBuilder
{
    private StringBuilder errorMessage;
    private Date createdAt;

    // Transport level data
    private String ip;
    private String httpMethod;
    private String url;
    private String rawPostData;

    // Method/Authentication level data
    private Method soapMethod;
    private String providerNo;

    // Response data
    private String rawOutput;

    public SoapLogBuilder()
    {
        errorMessage = new StringBuilder();
        this.createdAt = new Date();
    }

    /**
     * Cache relevant transport data
     */
    public SoapLogBuilder addTransportData(HttpServletRequest request, String rawPostData)
    {
        this.ip = request.getRemoteAddr();
        this.url = request.getRequestURL().toString();
        this.httpMethod = request.getMethod();

        this.rawPostData = rawPostData;

        return this;
    }

    /**
     * Cache relevant method data
     */
    public SoapLogBuilder addMethodData(HttpServletRequest request, Method soapMethod)
    {
        this.soapMethod = soapMethod;

        // Log the provider at the method level, because the transport level interceptor is processed
        // before authentication

        LoggedInInfo info = LoggedInInfo.getLoggedInInfoFromRequest(request);

        if (info != null)
        {
            this.providerNo = info.getLoggedInProviderNo();
        }

        return this;
    }

    /**
     * Cache relevant SOAP response data
     */
    public SoapLogBuilder addResponseData(String rawOutput)
    {
        this.rawOutput = rawOutput;

        return this;
    }

    /**
     * Cache exception data
     */
    public SoapLogBuilder addErrorData(Exception exception)
    {
        if (errorMessage.length() != 0)
        {
            this.errorMessage.append(System.getProperty("line.separator"));
        }

        this.errorMessage.append(exception.getClass().getSimpleName() + ": " + exception.getMessage());

        return this;
    }


    /**
     * Generates a new SOAP service log from the data cached
     *
     * @return SOAP service log
     */
    public SoapServiceLog buildSoapLog()
    {
        SoapServiceLog logEntry = new SoapServiceLog();

        logEntry.setCreatedAt(this.createdAt);
        logEntry.setDuration(calculateElapsedTimeMilliSeconds());
        logEntry.setIp(this.ip);
        logEntry.setHttpMethod(this.httpMethod);
        logEntry.setUrl(this.url);
        logEntry.setProviderNo(this.providerNo);
        logEntry.setErrorMessage(this.errorMessage.toString());

        if (this.soapMethod != null)
        {
            logEntry.setSoapMethod(this.soapMethod.getName());

            // inbound
            String postData = generatePostData(isSoapMethodAnnotatedWith(LogHeaderInbound.class));

            if (isSoapMethodAnnotatedWith(SkipContentLoggingInbound.class))
            {
                logEntry.setPostData(SkipContentLoggingInbound.SKIP_CONTENT_LOGGING_INBOUND);
            }
            else
            {
                logEntry.setPostData(postData);
            }


            // outbound
            if (isSoapMethodAnnotatedWith(SkipContentLoggingOutbound.class))
            {
                logEntry.setRawOutput(SkipContentLoggingOutbound.SKIP_CONTENT_LOGGING_OUTBOUND);
            }
            else
            {
                logEntry.setRawOutput(this.rawOutput);
            }
        }

        return logEntry;
    }

    /**
     * Determine the time needed to process the request in milliseconds
     *
     * @return elapsed time in ms
     */
    public long calculateElapsedTimeMilliSeconds()
    {
        return new Date().getTime() - this.createdAt.getTime();
    }

    /**
     * Generate a String which contains the data posted to the Soap service, transformed by applying all relevant
     * annotations to the Soap service's method.
     *
     * @return Transformed post data.
     */
    private String generatePostData(boolean keepHeader)
    {
        try
        {
            String xml = this.rawPostData;
            if (!keepHeader && isPostBodyParseable())
            {
                xml = stripTag("Body", xml, true);
            }

            if (isSoapMethodAnnotatedWith(MaskParameter.class) && isPostBodyParseable())
            {
                String[] parametersToMask = getMaskParameters();
                xml = applyParameterMasking(parametersToMask, xml);
            }

            return xml;
        }
        catch (Exception e)
        {
            MiscUtils.getLogger().error("Error while sanitizing post data: " + e.getMessage(), e);
            return "Error while sanitizing post data: " + e.getMessage();
        }
    }

    /**
     * Determine if the specified annotation is present on the SoapMethod
     *
     * @param annotation The annotation to look for
     * @return true if the specified annotation is present
     */
    private boolean isSoapMethodAnnotatedWith(Class<? extends Annotation> annotation)
    {
        return (this.soapMethod != null) && (AnnotationUtils.getMethodAnnotation(this.soapMethod, annotation) != null);
    }

    /**
     * Determine if a post body is parseable.  It is if it's not null and not empty.
     *
     * @return true if the post body can be parsed
     */
    private boolean isPostBodyParseable()
    {
        return this.rawPostData != null && !this.rawPostData.isEmpty();
    }


    /**
     * Retrieve the list of parameters to mask from the raw input
     *
     * @return array of parameters which should be masked
     */
    private String[] getMaskParameters()
    {
        MaskParameter maskParameters = AnnotationUtils.getMethodAnnotation(this.soapMethod, MaskParameter.class);
        String[] fieldNames = maskParameters.fields();
        return fieldNames;
    }


    /**
     * Apply parameter masking (if any) to the raw message to sanitize any specified XML element values in the SOAP message,
     * replacing the contents with asterisks.  If the soap method is not annotated with the MaskParameter annotation, then
     * the method
     *
     * @return postData with any specified parameter masking applied
     */
    private String applyParameterMasking(String[] parametersToMask, String xml)
    {
        try
        {
            XMLReader xmlReader = new XMLFilterImpl(XMLReaderFactory.createXMLReader())
            {
                HashSet<String> maskFields = new HashSet<>(Arrays.asList(parametersToMask));
                String beingMasked = "";

                @Override
                public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
                {
                    if (this.maskFields.contains(qName) && this.beingMasked.isEmpty())
                    {
                        this.beingMasked = qName;
                    }

                    super.startElement(uri, localName, qName, atts);
                }

                @Override
                public void endElement(String uri, String localName, String qName) throws SAXException {
                    if (this.beingMasked.equals(qName))
                    {
                        this.beingMasked = "";
                    }
                    super.endElement(uri, localName, qName);
                }

                @Override
                public void characters(char[] ch, int start, int length) throws SAXException {
                    if (!this.beingMasked.isEmpty())
                    {
                        ch = MaskParameter.MASK.toCharArray();
                        start = 0;
                        length = ch.length;
                    }

                    super.characters(ch, start, length);
                }

            };

            Source src = new SAXSource(xmlReader, new InputSource(new StringReader(xml)));
            StringWriter sanitizedPostData = new StringWriter();
            Result res = new StreamResult(sanitizedPostData);
            TransformerFactory.newInstance().newTransformer().transform(src, res);

            return sanitizedPostData.toString();
        }
        catch (Exception e)
        {
            throw new Fault(e);
        }
    }

    /**
     * strips a tag out from an xml string
     * @param tagToStrip the tag to strip
     * @param xml the xml string on which the operation is to be performed
     * @param invert if true every thing but the provided tag is stripped
     * @return xml after the strip operation is carried out
     */
    private String stripTag(String tagToStrip, String xml, boolean invert)
    {
        try
        {
            XMLReader xmlReader = new XMLFilterImpl(XMLReaderFactory.createXMLReader())
            {
                boolean skipping = invert;

                @Override
                public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
                {
                    if (tagToStrip.equals(localName))
                    {
                        skipping = !invert;
                        if (invert)
                        {
                            super.startElement(uri, localName, qName, atts);
                        }
                    }
                    else if (!skipping)
                    {
                        super.startElement(uri, localName, qName, atts);
                    }
                }

                @Override
                public void startPrefixMapping(String prefix, String uri) throws SAXException
                {
                    if (!skipping)
                    {
                        super.startPrefixMapping(prefix, uri);
                    }
                }

                @Override
                public void endElement(String uri, String localName, String qName) throws SAXException
                {
                    if (tagToStrip.equals(localName))
                    {
                        skipping = invert;
                        if (invert)
                        {
                            super.endElement(uri, localName, qName);
                        }
                    }
                    else if (!skipping)
                    {
                        super.endElement(uri, localName, qName);
                    }
                }

                @Override
                public void characters(char[] ch, int start, int length) throws SAXException
                {
                    if (!skipping)
                    {
                        super.characters(ch, start, length);
                    }
                }

            };

            Source src = new SAXSource(xmlReader, new InputSource(new StringReader(xml)));
            StringWriter sanitizedPostData = new StringWriter();
            Result res = new StreamResult(sanitizedPostData);
            TransformerFactory.newInstance().newTransformer().transform(src, res);

            return sanitizedPostData.toString();
        }
        catch (Exception e)
        {
            throw new Fault(e);
        }
    }
}