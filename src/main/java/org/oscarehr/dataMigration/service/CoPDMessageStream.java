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
package org.oscarehr.dataMigration.service;

import org.apache.log4j.Logger;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.util.MiscUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoPDMessageStream
{
	private static final Logger logger = MiscUtils.getLogger();

	private BufferedReader fileReader;
	private Pattern messagePattern = Pattern.compile("<[^><]*ZPD_ZTR\\.MESSAGE[^><]*>(.*?)<\\/[^><]*ZPD_ZTR\\.MESSAGE>", Pattern.DOTALL);
	private Pattern removeXmlTagPattern = Pattern.compile("<\\?xml.*\\?>(.*)", Pattern.DOTALL);

	public CoPDMessageStream(GenericFile CoPDFile) throws FileNotFoundException
	{
		this.fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(CoPDFile.getFileObject())));
	}

	public CoPDMessageStream(InputStream CoPDFileInputStream)
	{
		this.fileReader = new BufferedReader(new InputStreamReader(CoPDFileInputStream));
	}

	public void forEach(Consumer<? super String> action) throws Exception
	{
		String msg;
		while (!(msg = getNextMessage()).isEmpty())
		{
			action.accept(msg);
		}
	}

	public synchronized String getNextMessage() throws Exception
	{
		logger.info("loading next message...");
		StringBuffer sb = new StringBuffer();
		int character;
		while ((character = this.fileReader.read()) != -1)
		{
			sb.append((char)character);
			if ((char)character == '>' && isCompleteMessage(sb))
			{
				return buildMessage(sb);
			}
		}
		return "";
	}

	private String buildMessage(StringBuffer sb) throws Exception
	{
		return removeXmlStartTag(stripXmlNameSpace(extractMessage(stripInvalidCodePoints(sb.toString()))));
	}

	private String extractMessage(String xml)
	{
		Matcher messagePatternMatcher = messagePattern.matcher(xml);
		if (messagePatternMatcher.find())
		{
			return "<ZPD_ZTR xmlns=\"urn:hl7-org:v2xml\">" + messagePatternMatcher.group(1) + "</ZPD_ZTR>";
		}
		return "";
	}

	private boolean isCompleteMessage(StringBuffer sb)
	{
		if (sb.substring(Math.max(0, sb.length() - 1000), sb.length()).contains("ZPD_ZTR.MESSAGE"))
		{
			Matcher messagePatternMatcher = messagePattern.matcher(sb.toString());
			return messagePatternMatcher.find();
		}
		return false;
	}

	/**
	 * strip any namespace declartions / usages from an xml string
	 * @param xml - the xml to strip
	 * @return - a new xml string with the namespaces stripped.
	 */
	private String stripXmlNameSpace(String xml) throws SAXException, TransformerConfigurationException, TransformerException
	{
		XMLReader xmlReader = new XMLFilterImpl(XMLReaderFactory.createXMLReader())
		{
			@Override
			public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
			{
				String cleanName = qName;
				if(cleanName.contains(":"))
				{
					int sepIdx = cleanName.indexOf(":");
					cleanName = cleanName.substring(sepIdx + 1);
				}

				AttributesImpl keepAttributes  = new AttributesImpl();
				// keep only the xmlns="urn:hl7-org:v2xml" namespace attribute.
				for (int i =0; i < atts.getLength(); i ++)
				{
					if ((!atts.getQName(i).startsWith("xmlns:") && !atts.getQName(i).equals("xmlns")) ||
							(atts.getQName(i).equals("xmlns") && atts.getValue(i).equals("urn:hl7-org:v2xml")))
					{
						keepAttributes.addAttribute(atts.getURI(i), atts.getLocalName(i), atts.getQName(i), atts.getType(i), atts.getValue(i));
					}
				}


				super.startElement(uri, localName, cleanName, keepAttributes);
			}

			@Override
			public void endElement(String uri, String localName, String qName) throws SAXException
			{
				String cleanName = qName;
				if(cleanName.contains(":"))
				{
					int sepIdx = cleanName.indexOf(":");
					cleanName = cleanName.substring(sepIdx + 1);
				}
				super.endElement(uri, localName, cleanName);
			}
		};
		xmlReader.setFeature("http://xml.org/sax/features/namespaces", false);
		xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
		Source src = new SAXSource(xmlReader, new InputSource(new StringReader(xml)));
		StringWriter namespaceStrippedXml = new StringWriter();
		Result res = new StreamResult(namespaceStrippedXml);
		TransformerFactory.newInstance().newTransformer().transform(src, res);

		return namespaceStrippedXml.toString();
	}

	/**
	 * remove the xml start tag from an xml string
	 * @param xml - the xml in which to remove the start tag
	 * @return - the modified xml
	 */
	protected String removeXmlStartTag(String xml)
	{
		Matcher removeXmlTag = removeXmlTagPattern.matcher(xml);
		if (removeXmlTag.find())
		{
			return removeXmlTag.group(1);
		}
		return xml;
	}

	/**
	 * Accuro has sent a CoPD file containing invalid code point escape sequences. delete them.
	 * We cannot handle this in the preprocessor as the message stream will throw an error when trying
	 * to get the message.
	 * @param message - message on which to operate
	 * @return - the message with invalid code points removed.
	 */
	private String stripInvalidCodePoints(String message)
	{
		message = message.replace("&#11", "");
		message = message.replace("&#16", "");
		return message;
	}
}
