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

import org.apache.commons.lang.StringUtils;
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
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CoPDPreProcessorService
{
	public static final String HL7_TIMESTAMP_BEGINNING_OF_TIME = "19700101";
	private static final Logger logger = MiscUtils.getLogger();

	public boolean looksLikeCoPDFormat(GenericFile genericFile) throws IOException
	{
		// read first 100 lines to check file format
		File file = genericFile.getFileObject();
		InputStream is = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		StringBuffer sb = new StringBuffer();
		String line;
		int lineCount = 0;
		while((line = br.readLine()) != null)
		{
			if (lineCount > 100)
			{
				break;
			}
			sb.append(line);
			lineCount++;
		}

		//TODO make this more robust or whatever
		return sb.toString().contains("<ZPD_ZTR") || sb.toString().contains("<v2:ZPD_ZTR");
	}

	/**
	 * TODO  -- make this more efficient for larger files
	 * @param messageStr the whole file as a string
	 * @return - list of message strings
	 */
	public List<String> separateMessages(String messageStr)
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
	public String preProcessMessage(String message, CoPDImportService.IMPORT_SOURCE importSource)
	{
		message = setHl7Version(message);
		message = fixPRDSegment(message);
		message = fixPhoneNumbers(message);
		message = fixDateTimeNumbers(message);

		if(CoPDImportService.IMPORT_SOURCE.WOLF.equals(importSource))
		{
//			message = formatWolfZPV5SegmentNames(message);
			message = formatWolfFollowupSegments(message);
		}

		if (CoPDImportService.IMPORT_SOURCE.MEDIPLAN.equals(importSource))
		{
			message = fixTimestamps(message);
			message = fixTimestampsAttachments(message);
		}

		if (CoPDImportService.IMPORT_SOURCE.MEDACCESS.equals(importSource))
		{
			message = stripTagWhiteSpace(message);
			message = fixDoubleBPMeasurements(message);
			message = fixSlashBPMeasurements(message);
			message = fixZATDateString(message);

			// should come last
			message = ensureNumeric(message);
		}

		return message;
	}

	/**
	 * iterate over each tag with name=tagName found in the message. Allowing modification
	 * to its content
	 * @param message message to process
	 * @param tagName the tag on which the callback is triggered
	 * @param callback the callback to call for all instances of the tag (tag content in -> , -> modified content out).
	 * @return a modified message.
	 */
	private String foreachTag(String message, String tagName, Function<String, String> callback)
	{
		Pattern tagPattern = Pattern.compile("<" + tagName + ">(.*?)<\\/" + tagName + ">");
		Matcher tagMatcher = tagPattern.matcher(message);

		StringBuffer sb = new StringBuffer(message.length());
		while (tagMatcher.find())
		{
			String newContent = callback.apply(tagMatcher.group(1));
			tagMatcher.appendReplacement(sb, "<" + tagName + ">" + newContent + "</" + tagName + ">");
		}
		tagMatcher.appendTail(sb);
		return sb.toString();
	}

	/**
	 * Fix timestamp strings. Mediplan outputs unknown timestamps like, 00000 or 00000000 this causes parsing exceptions.
	 * This function switches <TS.1>00000[0000]</TS.1> to, <TS.1>00010101</TS.1>
	 * @param message the message to process
	 * @return fixed message
	 */
	private String fixTimestamps(String message)
	{
		Function<String, String> callback = new Function<String,String>() {

			private final Pattern timeStampPattern = Pattern.compile("(\\d{8})(\\d{2})(\\d{4})$");

			@Override
			public String apply(String timeStamp)
			{
				Matcher timeStampMatcher = timeStampPattern.matcher(timeStamp);
				if ("00000".equals(timeStamp) || "00000000".equals(timeStamp) || "00000000000".equals(timeStamp))
				{
					return HL7_TIMESTAMP_BEGINNING_OF_TIME;
				}
				else if (timeStampMatcher.find())
				{// look for timestamps with bad hour.
					try
					{
						Integer hours = Integer.parseInt(timeStampMatcher.group(2));
						if (hours > 23)
						{// sub in fake hour
							return timeStampMatcher.group(1) + "12" + timeStampMatcher.group(3);
						}
					}
					catch (NumberFormatException e)
					{
						//nop
					}
				}
				return timeStamp;
			}
		};

		return foreachTag(message, "TS.1", callback);
	}

	/**
	 * fix timestamps in ZAT segments (attachments)
	 * @param message the message to fix
	 * @return the fixed message
	 */
	public String fixTimestampsAttachments(String message)
	{
		Function<String, String> callback = new Function<String,String>() {
			@Override
			public String apply(String timeStamp)
			{
				if (timeStamp.contains("00000"))
				{
					return HL7_TIMESTAMP_BEGINNING_OF_TIME;
				}
				return timeStamp;
			}
		};

		return foreachTag(message, "ZAT.2", callback);
	}

	/**
	 * strip out white space in tag values ie. <ZQO.5> 80</ZQO.5> => <ZQO.5>80</ZQO.5>. The Hl7 parser
	 * sees ' 80' as invalid while '80' is valid.
	 * @param message - the message to process
	 * @return - the message with tag white space striped.
	 */
	private String stripTagWhiteSpace(String message)
	{
		Function<String, String> trimValueCallback = new Function<String, String>() {
			@Override
			public String apply(String tagValue)
			{
				return StringUtils.trimToEmpty(tagValue);
			}
		};

		message = foreachTag(message, "ZQO.4", trimValueCallback);
		message = foreachTag(message, "ZQO.5", trimValueCallback);
		message = foreachTag(message, "ZQO.6", trimValueCallback);
		message = foreachTag(message, "ZQO.7", trimValueCallback);
		return message;
	}

	/**
	 * Insure that some numeric elements of the hl7 message are indeed numeric. If not replace with "0".
	 * @param message - the message to operate on.
	 * @return - the resulting message
	 */
	private String ensureNumeric(String message)
	{
		Function<String, String> ensureNumeric = new Function<String, String>() {
			@Override
			public String apply(String tagValue)
			{
				try
				{
					Float.parseFloat(tagValue);
					return tagValue;
				}
				catch (NumberFormatException e)
				{
					logger.warn("Replacing invalid numeric value:" + tagValue + " with: \"0\"");
					return "0";
				}
			}
		};

		message = foreachTag(message, "ZQO.4", ensureNumeric);
		message = foreachTag(message, "ZQO.5", ensureNumeric);
		message = foreachTag(message, "ZQO.6", ensureNumeric);
		message = foreachTag(message, "ZQO.7", ensureNumeric);
		return message;
	}

	/**
	 * Fix double blood pressure measurements in the ZQO.4 / ZQO.5 tags.
	 * Some times blood pressure is recorded as "num num" but the COPD spec only allows "num".
	 * To fix simply take the first number.
	 * @param message - the message to fix
	 * @return - the fixed message
	 */
	private String fixDoubleBPMeasurements(String message)
	{
		Function<String, String> deleteDoubleValue = new Function<String, String>() {
			@Override
			public String apply(String tagValue)
			{
				if (tagValue.contains(" "))
				{
					String [] nums = tagValue.split(" ");
					return nums[0];
				}
				else
				{
					return tagValue;
				}
			}
		};

		message = foreachTag(message, "ZQO.4", deleteDoubleValue);
		return foreachTag(message, "ZQO.5", deleteDoubleValue);
	}

	/**
	 * fix BP measurements of the form "/<num>" convert to "<num>".
	 * @param message - message to operate on
	 * @return - the transformed message
	 */
	private String fixSlashBPMeasurements(String message)
	{
		Function<String, String> fixBPSlash = new Function<String, String>() {
			@Override
			public String apply(String tagValue)
			{
				if (StringUtils.trimToEmpty(tagValue).startsWith("/"))
				{
					return tagValue.substring(tagValue.indexOf("/") + 1);
				}
				else
				{
					return tagValue;
				}
			}
		};

		message = foreachTag(message, "ZQO.4", fixBPSlash);
		return foreachTag(message, "ZQO.5", fixBPSlash);
	}

	/**
	 * Some ZAT.2 date strings do not follow the spec and include a timestamp instead of a date. This causes parsing errors.
	 * If there is a timestamp in ZAT.2 simply strip the timestamp information.
	 * @param message
	 * @return
	 */
	private String fixZATDateString(String message)
	{
		Function<String, String> fixZATDate = new Function<String, String>() {
			@Override
			public String apply(String tagValue)
			{
				if (tagValue.length() > 8)
				{ // ZAT.2 length is 8 in CoPD spec (YYYYMMDD)
					return tagValue.substring(0,8);
				}
				else
				{
					return tagValue;
				}
			}
		};

		return foreachTag(message, "ZAT.2", fixZATDate);
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
		Pattern phonePattern = Pattern.compile("<XTN\\.6>(.*?)<\\/XTN\\.6>");
		Matcher phonePatternMatcher = phonePattern.matcher(message);

		StringBuffer sb = new StringBuffer(message.length());
		while(phonePatternMatcher.find())
		{
			// strip non numeric characters from phone numbers
			String replacement = "<XTN\\.6>" + phonePatternMatcher.group(1).replaceAll("[^\\d.]", "") + "</XTN\\.6>";
			phonePatternMatcher.appendReplacement(sb, replacement);
		}
		phonePatternMatcher.appendTail(sb);
		message = sb.toString();

		phonePattern = Pattern.compile("<XTN\\.7>(.*?)<\\/XTN\\.7>");
		phonePatternMatcher = phonePattern.matcher(message);

		sb = new StringBuffer(message.length());
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

//	private String formatWolfZPV5SegmentNames(String message)
//	{
//		Pattern patt = Pattern.compile("<ZPV\\.5>\\s*<firstname>(.*?)<\\/firstname>\\s*<lastname>(.*?)<\\/lastname>\\s*<ZPV\\.5>", Pattern.DOTALL);
//		Matcher m = patt.matcher(message);
//		StringBuffer sb = new StringBuffer(message.length());
//		while(m.find())
//		{
//			String firstName = m.group(1);
//			String lastName = m.group(2);
//			String replacement = "<ZPV\\.5>" + firstName + "|" + lastName + "<ZPV\\.5>";
//			m.appendReplacement(sb, replacement);
//		}
//		m.appendTail(sb);
//		return sb.toString();
//	}

	private String formatWolfFollowupSegments(String message)
	{
		message = message.replaceAll("<followup>", "<ZFU>");
		message = message.replaceAll("</followup>", "</ZFU>");

		message = message.replaceAll("<followupnumber>", "<ZFU.1>");
		message = message.replaceAll("</followupnumber>", "</ZFU.1>");

		message = message.replaceAll("<mdattending>", "<ZFU.2>");
		message = message.replaceAll("</mdattending>", "</ZFU.2>");

		message = message.replaceAll("<date>", "<ZFU.3>");
		message = message.replaceAll("</date>", "</ZFU.3>");

		message = message.replaceAll("<dateoffollowup>", "<ZFU.4>");
		message = message.replaceAll("</dateoffollowup>", "</ZFU.4>");

		message = message.replaceAll("<followupproblem>", "<ZFU.5>");
		message = message.replaceAll("</followupproblem>", "</ZFU.5>");

		message = message.replaceAll("<Done>", "<ZFU.6>");
		message = message.replaceAll("</Done>", "</ZFU.6>");

		message = message.replaceAll("<notes>", "<ZFU.7>");
		message = message.replaceAll("</notes>", "</ZFU.7>");

		message = message.replaceAll("<actiondesc>", "<ZFU.8>");
		message = message.replaceAll("</actiondesc>", "</ZFU.8>");

		message = message.replaceAll("<urgency>", "<ZFU.9>");
		message = message.replaceAll("</urgency>", "</ZFU.9>");

		Pattern patt = Pattern.compile("<ZFU>(.*?)</ZFU>", Pattern.DOTALL);
		Matcher m = patt.matcher(message);

		logger.info("followup segment:");
		while(m.find())
		{
			logger.info(m.group(0));
		}

		return message;
	}
}
