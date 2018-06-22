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
package org.oscarehr.demographicImport.service;

import org.apache.log4j.Logger;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.util.MiscUtils;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CoPDPreProcessorService
{
	private static final Logger logger = MiscUtils.getLogger();

	public List<String> readMessagesFromFile(GenericFile genericFile) throws IOException
	{
		logger.info("Read import file");
		File file = genericFile.getFileObject();
		InputStream is = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		StringBuffer sb = new StringBuffer();
		String line;
		while((line = br.readLine()) != null)
		{
			sb.append(line);
		}

		logger.info("Split hl7 messages");
		return separateMessages(sb.toString());
	}
	/**
	 * TODO  -- make this more efficient for larger files
	 * @param messageStr the whole file as a string
	 * @return - list of message strings
	 */
	private List<String> separateMessages(String messageStr)
	{
		List<String> messageList = new LinkedList<>();

		Pattern messagePattern = Pattern.compile("<ZPD_ZTR\\.MESSAGE>(.*?)<\\/ZPD_ZTR\\.MESSAGE>", Pattern.DOTALL);
		Matcher messagePatternMatcher = messagePattern.matcher(messageStr);
		while(messagePatternMatcher.find())
		{
			// split messages by each MESSAGE group segment in the file
			String message = "<ZPD_ZTR xmlns=\"urn:hl7-org:v2xml\">" + messagePatternMatcher.group(1) + "</ZPD_ZTR>";
			messageList.add(message);
		}
		return messageList;
	}


	/**
	 * Attempt to repair and format the hl7 message for the COPD Parser
	 * @param message the original message string
	 * @return the formatted and fixed message string
	 */
	public String preProcessMessage(String message)
	{
		message = setHl7Version(message);
		message = fixPRDSegment(message);
		message = fixPhoneNumbers(message);
		message = fixDateTimeNumbers(message);

		message = message.replaceAll("~crlf~", "\\\n");

		return message;
	}

	/**
	 * CoPD requires hl7V2.4, but often the value is not correctly set. this enforces the version
	 */
	private String setHl7Version(String message)
	{
		Pattern versionPattern = Pattern.compile("<VID\\.1>(.*?)<\\/VID\\.1>");
		Matcher versionPatternMatcher = versionPattern.matcher(message);

		StringBuffer sb = new StringBuffer(message.length());
		while(versionPatternMatcher.find())
		{
			// the hl7 version must be 2.4
			String replacement = "<VID\\.1>2.4</VID\\.1>";
			versionPatternMatcher.appendReplacement(sb, replacement);
		}
		versionPatternMatcher.appendTail(sb);
		message = sb.toString();

		return message;
	}

	/**
	 * Some phone numbers have non-numeric characters in illegal places (XTN segments fail). this strips them
	 */
	private String fixPhoneNumbers(String message)
	{
		Pattern phonePattern = Pattern.compile("<XTN\\.7>(.*?)<\\/XTN\\.7>");
		Matcher phonePatternMatcher = phonePattern.matcher(message);

		StringBuffer sb = new StringBuffer(message.length());
		while(phonePatternMatcher.find())
		{
			// strip non numeric characters from phone numbers
			String replacement = "<XTN\\.7>" + phonePatternMatcher.group(1).replaceAll("[^\\d.]", "") + "</XTN\\.7>";
			phonePatternMatcher.appendReplacement(sb, replacement);
		}
		phonePatternMatcher.appendTail(sb);
		message = sb.toString();

		return message;
	}

	/**
	 * Some date numbers have non-numeric characters in illegal places (TS segments fail). this strips them
	 */
	private String fixDateTimeNumbers(String message)
	{
		Pattern timePattern = Pattern.compile("<TS\\.1>(.*?)<\\/TS\\.1>");
		Matcher timePatternMatcher = timePattern.matcher(message);

		StringBuffer sb = new StringBuffer(message.length());
		while(timePatternMatcher.find())
		{
			// strip non numeric characters from dates
			String replacement = "<TS\\.1>" + timePatternMatcher.group(1).replaceAll("\\D", "") + "</TS\\.1>";
			timePatternMatcher.appendReplacement(sb, replacement);
		}
		timePatternMatcher.appendTail(sb);
		message = sb.toString();

		return message;
	}

	/**
	 * The CoPD spec has bad PRD segment numbering, this attempts to correct it to the hl7 standard
	 */
	private String fixPRDSegment(String message)
	{
		Pattern patt = Pattern.compile("<PRD>(.*?)<\\/PRD>", Pattern.DOTALL);
		Matcher m = patt.matcher(message);
		StringBuffer sb = new StringBuffer(message.length());
		while(m.find())
		{
			// for each PRD segment in the message, fix the PRD numbers
			String replacement = "<PRD>" + fixPRDSegmentNumbers(m.group(1)) + "</PRD>";
			m.appendReplacement(sb, replacement);
		}
		m.appendTail(sb);
		return sb.toString();
	}

	/**
	 * PRD.1 is required, but the spec has an off by one error, putting it at PRD.2, and all subsequent segments are off by 1.
	 * If this is the case, find them all and decrement them by 1 to match the regular hl7 standard
	 */
	private String fixPRDSegmentNumbers(String xmlPRD)
	{
		if(!xmlPRD.contains("<PRD.1>"))
		{
			Pattern patt = Pattern.compile("<(\\/?PRD)\\.([0-9]+)>");
			Matcher m = patt.matcher(xmlPRD);
			StringBuffer sb = new StringBuffer(xmlPRD.length());
			while(m.find())
			{
				String segmentNumStr = m.group(2);
				Integer segmentNumber = Integer.parseInt(segmentNumStr);
				String replacement = "<" + m.group(1) + "." + String.valueOf(segmentNumber - 1) + ">";

				m.appendReplacement(sb, replacement);
				logger.info("Replace:" + m.group(0) + " -> " + replacement);
			}
			m.appendTail(sb);
			xmlPRD = sb.toString();
		}
		return xmlPRD;
	}

}
