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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.hl7.Hl7Const;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.dataMigration.transfer.CoPDRecordData;
import org.oscarehr.util.MiscUtils;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.util.function.BiFunction;
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
		while ((line = br.readLine()) != null)
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
	 * Attempt to repair and format the hl7 message for the COPD Parser
	 *
	 * @param message the original message string
	 * @return the formatted and fixed message string
	 */
	public String preProcessMessage(String message, ImporterExporterFactory.IMPORT_SOURCE importSource, CoPDRecordData recordData)
	{
		Instant instant = Instant.now();

		message = setHl7Version(message);
		instant = printDuration(instant, "setHl7Version");

		message = fixPRDSegment(message);
		instant = printDuration(instant, "fixPRDSegment");

		message = fixPhoneNumbers(message);
		instant = printDuration(instant, "fixPhoneNumbers");

		message = fixDateTimeNumbers(message);
		instant = printDuration(instant, "fixDateTimeNumbers");

		if(ImporterExporterFactory.IMPORT_SOURCE.WOLF.equals(importSource))
		{
			message = formatWolfZPV5SegmentNames(message);
			instant = printDuration(instant, "formatWolfZPV5SegmentNames");

			message = stripTagWhiteSpace(message);
			instant = printDuration(instant, "stripTagWhiteSpace");

			message = trimTagNewLines(message);
			instant = printDuration(instant, "trimTagNewLines");

			message = fixDashBPMeasurements(message);
			instant = printDuration(instant, "fixDashBPMeasurements");

			message = fixBackTickBPMeasurements(message);
			instant = printDuration(instant, "fixBackTickBPMeasurements");
		}
		if (ImporterExporterFactory.IMPORT_SOURCE.MEDIPLAN.equals(importSource))
		{
			message = fixTimestamps(message);
			instant = printDuration(instant, "fixTimestamps");

			message = fixTimestampsAttachments(message);
			instant = printDuration(instant, "fixTimestampsAttachments");
		}

		if (ImporterExporterFactory.IMPORT_SOURCE.MEDACCESS.equals(importSource))
		{
			message = formatMedAccessSegments(message);
			instant = printDuration(instant, "formatMedAccessSegments");

			message = stripTagWhiteSpace(message);
			instant = printDuration(instant, "stripTagWhiteSpace");

			message = fixDoubleBPMeasurements(message);
			instant = printDuration(instant, "fixDoubleBPMeasurements");

			message = fixSlashBPMeasurements(message);
			instant = printDuration(instant, "fixSlashBPMeasurements");

			message = fixZATDateString(message);
			instant = printDuration(instant, "fixZATDateString");

			message = timestampPad(message);
			instant = printDuration(instant, "timestampPad");

			message = fixReferralPractitionerNo(message);
			instant = printDuration(instant, "fixReferralPractitionerNo");
		}

		if (ImporterExporterFactory.IMPORT_SOURCE.HEALTHQUEST.equals(importSource))
		{
			message = formatHealthQuestSegments(message);
			instant = printDuration(instant, "formatHealthQuestSegments");
		}

		// should come last
		message = ensureNumeric(message, recordData);
		instant = printDuration(instant, "ensureNumeric");

		return message;
	}

	/**
	 * strip out white space in tag values ie. <ZQO.5> 80</ZQO.5> => <ZQO.5>80</ZQO.5>. The Hl7 parser
	 * sees ' 80' as invalid while '80' is valid.
	 *
	 * @param message - the message to process
	 * @return - the message with tag white space striped.
	 */
	private String trimTagNewLines(String message)
	{
		Function<String, String> trimTagNewLines = tagValue -> tagValue.replaceAll("^[\n\r]", "").replaceAll("[\n\r]$", "");

		message = foreachTag(message, Hl7Const.HL7_SEGMENT_TS_1, trimTagNewLines, Pattern.DOTALL);
		message = foreachTag(message, Hl7Const.HL7_SEGMENT_XTN_6, trimTagNewLines, Pattern.DOTALL);
		message = foreachTag(message, Hl7Const.HL7_SEGMENT_ZBA_4, trimTagNewLines, Pattern.DOTALL);
		message = foreachTag(message, Hl7Const.HL7_SEGMENT_ZQO_4, trimTagNewLines, Pattern.DOTALL);
		message = foreachTag(message, Hl7Const.HL7_SEGMENT_ZQO_5, trimTagNewLines, Pattern.DOTALL);
		message = foreachTag(message, Hl7Const.HL7_SEGMENT_ZQO_6, trimTagNewLines, Pattern.DOTALL);
		message = foreachTag(message, Hl7Const.HL7_SEGMENT_ZQO_7, trimTagNewLines, Pattern.DOTALL);
		message = foreachTag(message, Hl7Const.HL7_SEGMENT_ZQO_8, trimTagNewLines, Pattern.DOTALL);
		return message;
	}

	/**
	 * iterate over each tag with name=tagName found in the message. Allowing modification
	 * to its content
	 *
	 * @param message  message to process
	 * @param tagName  the tag on which the callback is triggered
	 * @param callback the callback to call for all instances of the tag (tag content in -> , -> modified content out).
	 * @return a modified message.
	 */
	private String foreachTag(String message, String tagName, Function<String, String> callback)
	{
		return foreachTag(message, tagName, callback, 0);
	}

	/**
	 * iterate over each tag with name=tagName found in the message. Allowing modification
	 * to its content
	 *
	 * @param message      message to process
	 * @param tagName      the tag on which the callback is triggered
	 * @param callback     the callback to call for all instances of the tag (tag content in -> , -> modified content out).
	 * @param patternFlags the regex Pattern flags
	 * @return a modified message.
	 */
	private String foreachTag(String message, String tagName, Function<String, String> callback, int patternFlags)
	{
		/* match a pattern of <tagName>A</tagName> where A is the tagValue */
		Pattern tagPattern = Pattern.compile("<" + tagName + ">(.*?)<\\/" + tagName + ">", patternFlags);
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
	 * iterate over each segment with found in the message. Allowing modification
	 * to its content. callback has 2 params, segment sequence(always in the first field of hl7) and the value of the specified segmentField
	 *
	 * @param message      message to process
	 * @param segment      the segment on which to run
	 * @param segmentField the segment field on which the callback is triggered
	 * @param callback     the callback to call for all instances of the tag (tag content in -> , -> modified content out).
	 * @return a modified message.
	 */
	private String foreachTagWithSequence(String message, BiFunction<String, String, String> callback, String segment, String segmentField)
	{
		/* match a pattern of <segment.1>A</segment.1>B<segment.segmentField>C</segment.segmentField>
		* where A is the segment ID, and C is the tagValue*/
		String tagName = segment + "\\." + segmentField;
		Pattern tagPattern = Pattern.compile(
				"<" + segment + "\\.1>(.*?)<\\/" + segment + "\\.1>" + // segment ID
				"(.*?)" + // inbetween
				"<" + tagName + ">(.*?)<\\/" + tagName + ">", Pattern.DOTALL); // specific field
		Matcher tagMatcher = tagPattern.matcher(message);

		StringBuffer sb = new StringBuffer(message.length());
		while (tagMatcher.find())
		{
			String newContent = callback.apply(tagMatcher.group(1), tagMatcher.group(3));
			tagMatcher.appendReplacement(sb,
					"<" + segment + "\\.1>" + tagMatcher.group(1) + "<\\/" + segment + "\\.1>"
					+ tagMatcher.group(2) +
					"<" + tagName + ">" + newContent + "</" + tagName + ">");
		}
		tagMatcher.appendTail(sb);
		return sb.toString();
	}

	/**
	 * Fix timestamp strings. Mediplan outputs unknown timestamps like, 00000 or 00000000 this causes parsing exceptions.
	 * This function switches <TS.1>00000[0000]</TS.1> to, <TS.1>00010101</TS.1>
	 *
	 * @param message the message to process
	 * @return fixed message
	 */
	private String fixTimestamps(String message)
	{
		final Pattern timeStampPattern = Pattern.compile("(\\d{8})(\\d{2})(\\d{4})$");

		Function<String, String> callback = timeStamp ->
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
		};

		return foreachTag(message, Hl7Const.HL7_SEGMENT_TS_1, callback);
	}

	/**
	 * fix timestamps in ZAT segments (attachments)
	 *
	 * @param message the message to fix
	 * @return the fixed message
	 */
	public String fixTimestampsAttachments(String message)
	{
		Function<String, String> callback = timeStamp ->
		{
			if (timeStamp.contains("00000"))
			{
				return HL7_TIMESTAMP_BEGINNING_OF_TIME;
			}
			return timeStamp;
		};

		return foreachTag(message, Hl7Const.HL7_SEGMENT_ZAT_2, callback);
	}

	/**
	 * strip out white space in tag values ie. <ZQO.5> 80</ZQO.5> => <ZQO.5>80</ZQO.5>. The Hl7 parser
	 * sees ' 80' as invalid while '80' is valid.
	 *
	 * @param message - the message to process
	 * @return - the message with tag white space striped.
	 */
	private String stripTagWhiteSpace(String message)
	{
		Function<String, String> trimValueCallback = tagValue -> StringUtils.trimToEmpty(tagValue);

		message = foreachTag(message, Hl7Const.HL7_SEGMENT_TS_1, trimValueCallback);
		message = foreachTag(message, Hl7Const.HL7_SEGMENT_ZBA_31, trimValueCallback);
		message = foreachTag(message, Hl7Const.HL7_SEGMENT_ZQO_4, trimValueCallback);
		message = foreachTag(message, Hl7Const.HL7_SEGMENT_ZQO_5, trimValueCallback);
		message = foreachTag(message, Hl7Const.HL7_SEGMENT_ZQO_6, trimValueCallback);
		message = foreachTag(message, Hl7Const.HL7_SEGMENT_ZQO_7, trimValueCallback);
		message = foreachTag(message, Hl7Const.HL7_SEGMENT_ZQO_8, trimValueCallback);
		return message;
	}

	/**
	 * Insure that some numeric elements of the hl7 message are indeed numeric. If not replace with "0".
	 *
	 * @param message - the message to operate on.
	 * @return - the resulting message
	 */
	private String ensureNumeric(String message, CoPDRecordData recordData)
	{
		message = ensureNumeric(message, recordData, Hl7Const.HL7_SEGMENT_ZBA, "31");
		message = ensureNumeric(message, recordData, Hl7Const.HL7_SEGMENT_ZQO, "4","5","6","7","8");
		return message;
	}
	private String ensureNumeric(String message, CoPDRecordData recordData, String segment, String...segmentFields)
	{
		Function<String, String> callback = tagValue ->
		{
			String messageSegment = tagValue;
			for(String segmentField : segmentFields)
			{
				messageSegment = ensureNumericField(messageSegment, recordData, segment, segmentField);
			}
			return messageSegment;
		};

		return foreachTag(message, segment, callback, Pattern.DOTALL);
	}
	private String ensureNumericField(String message, CoPDRecordData recordData, String segment, String segmentField)
	{
		BiFunction<String, String, String> ensureNumericCallback = (setId, tagValue) ->
		{
			try
			{
				Float.parseFloat(tagValue);
				return tagValue;
			}
			catch (NumberFormatException e)
			{
				String dataMessage = Hl7Const.getReadableSegmentName(segment, segmentField) +
						"=> Invalid numeric value of '" + tagValue + "'. Value was set to 0";
				recordData.addMessage(segment, setId, dataMessage);
				logger.warn("[" + segment + "." + segmentField + "] Replacing invalid numeric value:" + tagValue + " with: \"0\"");
				return "0";
			}
		};

		return foreachTagWithSequence(message, ensureNumericCallback, segment, segmentField);
	}

	/**
	 * Fix double blood pressure measurements in the ZQO.4 / ZQO.5 tags.
	 * Some times blood pressure is recorded as "num num" but the COPD spec only allows "num".
	 * To fix simply take the first number.
	 *
	 * @param message - the message to fix
	 * @return - the fixed message
	 */
	private String fixDoubleBPMeasurements(String message)
	{
		Function<String, String> deleteDoubleValue = tagValue ->
		{
			if (tagValue.contains(" "))
			{
				String[] nums = tagValue.split(" ");
				return nums[0];
			}
			else
			{
				return tagValue;
			}
		};

		message = foreachTag(message, Hl7Const.HL7_SEGMENT_ZQO_4, deleteDoubleValue);
		message = foreachTag(message, Hl7Const.HL7_SEGMENT_ZQO_5, deleteDoubleValue);
		return message;
	}

	/**
	 * fix BP measurements of the form "/<num>" convert to "<num>".
	 *
	 * @param message - message to operate on
	 * @return - the transformed message
	 */
	private String fixSlashBPMeasurements(String message)
	{
		Function<String, String> fixBPSlash = tagValue ->
		{
			if(StringUtils.trimToEmpty(tagValue).startsWith("/"))
			{
				return tagValue.substring(tagValue.indexOf("/") + 1);
			}
			else
			{
				return tagValue;
			}
		};

		message = foreachTag(message, Hl7Const.HL7_SEGMENT_ZQO_4, fixBPSlash);
		message = foreachTag(message, Hl7Const.HL7_SEGMENT_ZQO_5, fixBPSlash);
		return message;
	}

	/**
	 * fix BP measurements of the form "<num>-" convert to "<num>".
	 *
	 * @param message - message to operate on
	 * @return - the transformed message
	 */
	private String fixDashBPMeasurements(String message)
	{
		Function<String, String> fixBPDash = tagValue ->
		{
			if (StringUtils.trimToEmpty(tagValue).endsWith("-"))
			{
				return tagValue.substring(0, tagValue.indexOf("-"));
			}
			else
			{
				return tagValue;
			}
		};

		message = foreachTag(message, Hl7Const.HL7_SEGMENT_ZQO_4, fixBPDash);
		message = foreachTag(message, Hl7Const.HL7_SEGMENT_ZQO_5, fixBPDash);
		return message;
	}

	/**
	 * fix BP measurements of the form "<num>`" convert to "<num>".
	 *
	 * @param message - message to operate on
	 * @return - the transformed message
	 */
	private String fixBackTickBPMeasurements(String message)
	{
		Function<String, String> fixBPBackTick = tagValue ->
		{
			if (StringUtils.trimToEmpty(tagValue).endsWith("`"))
			{
				return tagValue.substring(0, tagValue.indexOf("`"));
			}
			else
			{
				return tagValue;
			}
		};

		message = foreachTag(message, Hl7Const.HL7_SEGMENT_ZQO_4, fixBPBackTick);
		message = foreachTag(message, Hl7Const.HL7_SEGMENT_ZQO_5, fixBPBackTick);
		return message;
	}

	/**
	 * Some ZAT.2 date strings do not follow the spec and include a timestamp instead of a date. This causes parsing errors.
	 * If there is a timestamp in ZAT.2 simply strip the timestamp information.
	 *
	 * @param message message to operate on
	 * @return fixed date string
	 */
	private String fixZATDateString(String message)
	{
		Function<String, String> fixZATDate = tagValue ->
		{
			if (tagValue.length() > 8)
			{ // ZAT.2 length is 8 in CoPD spec (YYYYMMDD)
				return tagValue.substring(0, 8);
			}
			else
			{
				return tagValue;
			}
		};

		return foreachTag(message, Hl7Const.HL7_SEGMENT_ZAT_2, fixZATDate);
	}

	/**
	 * pad timestamp values (TS.1), insuring they have an even number of characters. If they are odd a '0' is appended
	 *
	 * @param message - message on which the timestamps will be padded
	 * @return - the modified message
	 */
	private String timestampPad(String message)
	{
		Function<String, String> padTimestamps = tagValue ->
		{
			if (tagValue.length() % 2 != 0)
			{
				return tagValue + "0";
			}
			return tagValue;
		};

		return foreachTag(message, Hl7Const.HL7_SEGMENT_TS_1, padTimestamps);
	}

	/**
	 * CoPD requires hl7V2.4, but often the value is not correctly set. this enforces the version
	 */
	private String setHl7Version(String message)
	{
		Pattern versionPattern = Pattern.compile("<VID\\.1>(.*?)<\\/VID\\.1>");
		Matcher versionPatternMatcher = versionPattern.matcher(message);

		StringBuffer sb = new StringBuffer(message.length());
		while (versionPatternMatcher.find())
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
		Function<String, String> fixPhoneNumbersOnlyNum = tagValue -> tagValue.replaceAll("[^\\d.]", "");
		Function<String, String> fixPhoneNumbersNoSpace = tagValue -> tagValue.replaceAll("\\s", "");
		Function<String, String> fixPhoneNumbersNoNewLine = tagValue -> tagValue.replace("\n", "");

		message = foreachTag(message, Hl7Const.HL7_SEGMENT_XTN_1, fixPhoneNumbersNoNewLine, Pattern.DOTALL);
		message = foreachTag(message, Hl7Const.HL7_SEGMENT_XTN_1, fixPhoneNumbersNoSpace);
		message = foreachTag(message, Hl7Const.HL7_SEGMENT_XTN_6, fixPhoneNumbersOnlyNum);
		message = foreachTag(message, Hl7Const.HL7_SEGMENT_XTN_7, fixPhoneNumbersOnlyNum);


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
		while (timePatternMatcher.find())
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
		while (m.find())
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
		if (!xmlPRD.contains("<PRD.1>"))
		{
			Pattern patt = Pattern.compile("<(\\/?PRD)\\.([0-9]+)>");
			Matcher m = patt.matcher(xmlPRD);
			StringBuffer sb = new StringBuffer(xmlPRD.length());
			while (m.find())
			{
				String segmentNumStr = m.group(2);
				Integer segmentNumber = Integer.parseInt(segmentNumStr);
				String replacement = "<" + m.group(1) + "." + (segmentNumber - 1) + ">";

				m.appendReplacement(sb, replacement);
				logger.info("Replace:" + m.group(0) + " -> " + replacement);
			}
			m.appendTail(sb);
			xmlPRD = sb.toString();
		}
		return xmlPRD;
	}

	/**
	 * Referral practitioner numbers in Alberta have extra non-numeric characters.
	 * Juno treats it as a single number.
	 *
	 * @param message message on which referral number will be stripped of non-numeric characters
	 * @return modified message
	 */
	private String fixReferralPractitionerNo(String message)
	{
		Function<String, String> fixReferralProvider = tagValue -> tagValue.replaceAll("-", "");

		message = foreachTag(message, Hl7Const.HL7_SEGMENT_XCN_1, fixReferralProvider);
		message = foreachTag(message, Hl7Const.HL7_SEGMENT_ZBA_29, fixReferralProvider);

		return message;
	}

	/**
	 * Format a provider name. Replaces the first space with a | pipe when there is none.
	 *
	 * @param message message on which provider name will have a | pipe inserted.
	 * @return modified message
	 */
	private String formatWolfZPV5SegmentNames(String message)
	{
		Function<String, String> fixProviderName = tagValue ->
		{
			if (tagValue.contains("|"))
			{
				return tagValue;
			}
			else if (tagValue.indexOf(' ') == -1)
			{
				return tagValue + "|";
			}
			else
			{
				int index = tagValue.indexOf(' ');
				String firstName = tagValue.substring(0, index);
				String lastName = "";
				if (index > tagValue.length())
				{
					lastName = tagValue.substring(index + 1);
				}
				return firstName + "|" + lastName;
			}
		};

		message = foreachTag(message, Hl7Const.HL7_SEGMENT_ZPV_5, fixProviderName);

		return message;
	}

	/**
	 * Largely here to replace any bad characters present in the import data.
	 * "Bad" characters in context of this importer are ones that conflict with the usual HL7 reserved characters:
	 * '|', '&', '~', '\', '^'
	 * The presence of any of these characters (usually via some HTML or XML encoding) breaks the HAPI parser.
	 *
	 * @param message message to clean
	 * @return message stripped of problematic encoded segments
	 */
	private String formatMedAccessSegments(String message)
	{
		// XML-encoded chars
		message = message.replaceAll("&#xD;", "");
		message = message.replaceAll("&#x2022;", "");
		message = message.replaceAll("&#xA0;", "");
		message = message.replaceAll("&#13", "");

		return message;
	}

	/**
	 * Strips out <OBX.2/> from the message
	 *
	 * @param message message to clean
	 * @return message stripped of problematic encoded segments
	 */
	private String formatHealthQuestSegments(String message)
	{
		// XML-encoded chars
		message = message.replaceAll("<OBX.2/>", "");

		return message;
	}

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
		while (m.find())
		{
			logger.info(m.group(0));
		}

		return message;
	}

	private Instant printDuration(Instant start, String what)
	{
		Instant now = Instant.now();
		logger.info("[DURATION] " + what + " took " + Duration.between(start, now));
		return now;
	}
}
