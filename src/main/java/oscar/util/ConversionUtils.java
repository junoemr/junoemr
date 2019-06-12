/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */
package oscar.util;

import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Yet another conversion utility class for bridging JPA entity to legacy schema mismatch. 
 */
public class ConversionUtils {

	private static final Logger logger = MiscUtils.getLogger();

	public static final String DATE_PATTERN_YEAR = "yyyy";
	public static final String DATE_PATTERN_MONTH = "MM";
	public static final String DATE_PATTERN_DAY = "dd";
	
	public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
	public static final String DEFAULT_TIME_PATTERN = "HH:mm:ss";
	public static final String TIME_PATTERN_NO_SEC = "HH:mm";
	public static final String DEFAULT_TS_PATTERN = "yyyy-MM-dd HH:mm:ss";

	public static final String TS_NO_SEC_PATTERN = "yyyy-MM-dd H:mm";

	private static final Long ZERO_LONG = new Long(0);
	private static final Integer ZERO_INT = new Integer(0);
	private static final Double ZERO_DOUBLE = new Double(0.0);
	private static final String ZERO_STRING = "0";

	private static final long MS_IN_DAY = 1000 * 60 * 60 * 24;

	private static final Pattern datePattern = Pattern.compile("(\\d{4})-(\\d{1,2})-(\\d{1,2})");
	private static final Pattern dateTimePattern = Pattern.compile("(\\d{4})-(\\d{1,2})-(\\d{1,2})\\s(\\d{1,2})[:]?(\\d{1,2})?[:]?(\\d{1,2})?");

	private ConversionUtils() {
	}

	public static List<Integer> toIntList(List<String> list) {
		List<Integer> result = new ArrayList<Integer>();
		for (String str : list) {
			result.add(fromIntString(str));
		}
		return result;
	}

	/**
	 * Converts the provided string representing time into a date object. Time must match the {@link #DEFAULT_TIME_PATTERN}.
	 * 
	 * @param timeString
	 * 		Time string to be parsed
	 * @return
	 * 		Returns the parsed string
	 */
	public static Date fromTimeString(String timeString) {
		return fromDateString(timeString, DEFAULT_TIME_PATTERN);
	}

	public static Date fromTimeStringNoSeconds(String timeString) {
		return fromDateString(timeString, TIME_PATTERN_NO_SEC);
	}
	
	public static String toTimeStringNoSeconds(Date timeString) {
		return toDateString(timeString, TIME_PATTERN_NO_SEC);
	}

	/**
	 * Converts the provided date representing time into a date object. Time must match the {@link #DEFAULT_DATE_PATTERN}.
	 * 
	 * @param dateString
	 * 		Date string to be parsed
	 * @return
	 * 		Returns the parsed string
	 */
	public static Date fromDateString(String dateString) {
		return fromDateString(dateString, DEFAULT_DATE_PATTERN);
	}

	public static Date fromTimestampString(String dateString) {
		return fromDateString(dateString, DEFAULT_TS_PATTERN);
	}

	public static String toTimestampString(Date timestamp) {
		return toDateString(timestamp, DEFAULT_TS_PATTERN);
	}

	/**
	 * Parses the date string using the specified format pattern 
	 * 
	 * @param dateString
	 * 		Date string to be parsed
	 * @param formatPattern
	 * 		Format pattern to use for parsing
	 * @return
	 * 		Returns the parsed date or null if the date can't be parsed
	 */
	public static Date fromDateString(String dateString, String formatPattern) {
		if (dateString == null || "".equals(dateString.trim())) return null;

		SimpleDateFormat format = new SimpleDateFormat(formatPattern);
		try {
			return format.parse(dateString);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * Formats date instance using the provided date format pattern
	 * 
	 * @param date
	 * 		Date to be formatted
	 * @param formatPattern
	 * 		Format pattern to apply
	 * @return
	 * 		Returns the formatted date as a string, or an empty string for 
	 * 		null date parameter.
	 */
	public static String toDateString(Date date, String formatPattern)
	{
		if (date == null)
		{
			return "";
		}

		SimpleDateFormat format = new SimpleDateFormat(formatPattern);
		return format.format(date);
	}

	/**
	 * Formats date instance using the provided date format pattern
	 *
	 * @param date
	 * 		LocalDate to be formatted
	 * @param formatPattern
	 * 		Format pattern to apply
	 * @return
	 * 		Returns the formatted date as a string, or an empty string for
	 * 		null date parameter.
	 */
	public static String toDateString(LocalDate date, String formatPattern) {
		if (date == null) {
			return "";
		}

		DateTimeFormatter format = DateTimeFormatter.ofPattern(formatPattern);
		return date.format(format);
	}

	public static String toDateTimeString(LocalDateTime date, String formatPattern) {
		if (date == null) {
			return "";
		}

		DateTimeFormatter format = DateTimeFormatter.ofPattern(formatPattern);
		return date.format(format);
	}

	/**
	 * Formats the date instance into a string keeping only the time of the day and excluding the remaining info.   
	 * 
	 * @param time
	 * 		Date to be formatted using {@link #DEFAULT_TIME_PATTERN}
	 * @return
	 * 		Returns the formatted string
	 */
	public static String toTimeString(Date time) {
		return toDateString(time, DEFAULT_TIME_PATTERN);
	}

	/**
	 * Formats the date instance into a string keeping only the date.   
	 * 
	 * @param date
	 * 		Date to be formatted using {@link #DEFAULT_DATE_PATTERN}
	 * @return
	 * 		Returns the formatted string
	 */
	public static String toDateString(Date date) {
		return toDateString(date, DEFAULT_DATE_PATTERN);
	}

	/**
	 * Formats the local date instance into a string.
	 *
	 * @param date
	 * 		LocalDate to be formatted using {@link #DEFAULT_DATE_PATTERN}
	 * @return
	 * 		Returns the formatted string
	 */
	public static String toDateString(LocalDate date)
	{
		return toDateString(date, DEFAULT_DATE_PATTERN);
	}

	public static String toDateTimeString(LocalDateTime date)
	{
		return toDateTimeString(date, DEFAULT_TS_PATTERN);
	}

	public static String toDateTimeNoSecString(LocalDateTime date)
	{
		return toDateTimeString(date, TS_NO_SEC_PATTERN);
	}

	/**
	 * Parses the specified string as a Long instance. 
	 * 
	 * @param longString
	 * 		String to be parsed as long
	 * @return
	 * 		Returns the parsed long
	 */
	public static Long fromLongString(String longString) {
		if (longString == null || longString.trim().isEmpty()) {
			return ZERO_LONG;
		}
		try {
			return Long.parseLong(longString);
		} catch (Exception e) {
			return 0L;
		}
	}

	/**
	 * Parses the specified string as an Integer instance. 
	 * 
	 * @param obj
	 * 		String to be parsed as integer
	 * @return
	 * 		Returns the parsed integer
	 */
	public static Integer fromIntString(Object obj) {
		String intString = (obj == null) ? null : obj.toString();
		if (intString == null || intString.trim().isEmpty()) {
			return ZERO_INT;
		}
		try {
			return Integer.parseInt(intString);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * Formats the specified integer as string 
	 * 
	 * @param integer
	 * 		Integer to format as a string.
	 * @return
	 * 		Returns the formatted string, or 0 for null parameter value.
	 */
	public static String toIntString(Integer integer) {
		if (integer == null) {
			return ZERO_STRING;
		}
		return integer.toString();
	}

	/**
	 * Formats the specified boolean as string 
	 * 
	 * @param b
	 * 		Boolean to format as a string.
	 * @return
	 * 		Returns 0 for false or null instance or "1" otherwise.
	 */
	public static String toBoolString(Boolean b) {
		if (b == null || b == Boolean.FALSE) {
			return ZERO_STRING;
		}
		return "1";
	}

	/**
	 * Parses the specified string as boolean.
	 * 
	 * @param str
	 * 		String to be parsed
	 * @return
	 * 		Returns false for empty, null or 0.
	 * 		Returns true only if the string is "1" or "true", false otherwise.
	 */
	public static boolean fromBoolString(String str) {
		if (!hasContent(str) || "0".equals(str))
		{
			return false;
		}

		return str.equals("1") || str.toLowerCase().equals("true");
	}

	public static boolean hasContent(String str)
	{
		return !(str == null || str.trim().isEmpty());
	}

	/**
	 * Parses the specified string as an Double instance. 
	 * 
	 * @param str
	 * 		String to be parsed as double
	 * @return
	 * 		Returns the parsed double
	 */
	public static Double fromDoubleString(String str) {
		if (str == null || str.trim().isEmpty()) {
			return ZERO_DOUBLE;
		}

		try {
			return Double.parseDouble(str);			
		} catch (Exception e) {
			return 0.0;
		}
	}

	/**
	 * Formats the specified double as string 
	 * 
	 * @param d
	 * 		Double to format as a string.
	 * @return
	 * 		Returns the formatted string, or 0 for null value.
	 */
	public static String toDoubleString(Double d) {
		if (d == null)
		{
			return "0.0";
		}
		return d.toString();
	}

	/**
	 * Gets number of days since day 0 for the specified time stamp
	 * 
	 * @param timestamp
	 * 		Time stamp to get day count for
	 * @return
	 * 		Returns the day count
	 */
	public static int toDays(Date timestamp) {
		return toDays(timestamp.getTime());
	}

	/**
	 * Gets number of days since day 0 for the specified time stamp
	 * 
	 * @param timestamp
	 * 		Time stamp to get day count for
	 * @return
	 * 		Returns the day count
	 */
	public static int toDays(long timestamp) {
		return (int) (timestamp / MS_IN_DAY);
	}

	/**
	 * Some date strings that we receive are sometimes missing leading zeroes, i.e:
	 * - 2019-04-8
	 * - 2019-4-08
	 *
	 * LocalDate can interpret these properly if we individually feed in the year, month, and day.
	 *
	 * @param dateString
	 *		Date string of form like yyyy-MM-dd
	 *		This input string is allowed to be missing leading zero on MM or dd.
	 *		Note that LocalDate *could* fix a bad year (a year like 019 or 219)
	 *		but allowing a year entry like these to be entered could cause more problems.
	 * @return dateString
	 *		Original dateString if it's already in good shape or parsing fails
	 *		Otherwise return a new dateString of format yyyy-MM-dd
	 */
	public static String padDateString(String dateString)
	{
		Matcher match;
		try
		{
			match = datePattern.matcher(dateString);
		}
		catch (IllegalStateException ex)
		{
			return dateString;
		}

		if (match.matches())
		{
			try
			{
				int year = Integer.parseInt(match.group(1));
				int month = Integer.parseInt(match.group(2));
				int day = Integer.parseInt(match.group(3));

				LocalDate desiredDate = LocalDate.of(year, month, day);
				return desiredDate.toString();
			}
			catch (NumberFormatException | DateTimeParseException ex)
			{
				return dateString;
			}
		}

		return dateString;
	}

	/**
	 * Some datetime strings we receive are missing leading zeroes in one or
	 * more of their fields. If we pull out the individual fields and feed them into
	 * LocalDateTime we can reconstruct a properly formatted string for future needs.
	 *
	 * @param dateTimeString
	 * 		Datetime string of format like yyyy-MM-dd hh:mm:ss
	 * 		yyyy must be 4 digits. MM, dd, HH, mm can all be 1 or 2 digits.
	 * 	    ss is optional and can be 1 or 2 digits.
	 * @return
	 * 		New dateTimeString of format yyyy-MM:dd hh:mm(:ss) if parsing was successful
	 * 		If string couldn't be fit to the datetime format, attempt to fit it to yyyy-MM-dd and return that
	 * 		If the internal call to try and fit to yyyy-MM-dd fails the user gets their original string back
	 */
	public static String padDateTimeString(String dateTimeString)
	{
		Matcher match;
		try
		{
			match = dateTimePattern.matcher(dateTimeString);
		}
		catch (IllegalStateException ex)
		{
			MiscUtils.getLogger().error("error matching: " + ex);
			return dateTimeString;
		}

		if (match.matches())
		{
			try
			{
				int year = Integer.parseInt(match.group(1));
				int month = Integer.parseInt(match.group(2));
				int day = Integer.parseInt(match.group(3));
				int hour = Integer.parseInt(match.group(4));
				int minute = 0;
				if (match.group(5) != null)
				{
					minute = Integer.parseInt(match.group(5));
				}
				int second = 0;
				if (match.group(6) != null)
				{
					second = Integer.parseInt(match.group(6));
				}
				LocalDateTime desiredDate = LocalDateTime.of(year, month, day, hour, minute, second);
				return desiredDate.toString().replace("T", " ");
			}
			catch (NumberFormatException | DateTimeParseException ex)
			{
				logger.warn("Error attempting to pad " + dateTimeString + ": " + ex);
				return dateTimeString;
			}
		}
		// Could have gotten a format of yyyy-MM-dd - try throwing it in the other pad function to be safe
		return padDateString(dateTimeString);
	}

	public static Date getLegacyDateFromDateString(String dateString)
	{
		return getLegacyDateFromDateString(dateString, DEFAULT_TS_PATTERN);
	}

	public static Date getLegacyDateFromDateString(String dateString, String inFormat)
	{
		Date returnDate = null;

		if (dateString == null || dateString.trim().isEmpty())
		{
			logger.warn("Cannot Coalesce a null/empty date string");
			return returnDate;
		}

		if (inFormat.length() > dateString.length())
		{
			inFormat = inFormat.substring(0, dateString.length());
		}

		try
		{
			DateTimeFormatter inFormatter = DateTimeFormatter.ofPattern(inFormat);

			// use format builder to set default missing time values
			DateTimeFormatter customFormatter = new DateTimeFormatterBuilder().append(inFormatter)
					.parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
					.parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
					.parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
					.toFormatter();

			LocalDateTime parsedDate = LocalDateTime.parse(dateString, customFormatter);
			returnDate = Date.from(parsedDate.toInstant(OffsetDateTime.now(ZoneId.systemDefault()).getOffset()));

			logger.debug("Transform " + dateString + " to " + parsedDate.format(inFormatter));
		}
		catch(DateTimeException e)
		{
			logger.error("Date parse Exception", e);
		}
		return returnDate;
	}


	public static LocalDateTime getLocalDateTimeFromSqlDateAndTime(java.sql.Date date, java.sql.Time time)
	{
		LocalDate localDate = date.toLocalDate();
		LocalTime localTime = time.toLocalTime();
		return LocalDateTime.of(localDate, localTime);
	}

	/**
	 * Creates a list of dates.  Taken from http://www.baeldung.com/java-between-dates
	 * @param startDate
	 * @param endDate
	 * @return An inclusive list of dates
	 */
	public static List<LocalDate> getDateList(LocalDate startDate, LocalDate endDate)
	{
		long numOfDaysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;

		return IntStream.iterate(0, i -> i + 1)
			.limit(numOfDaysBetween)
			.mapToObj(i -> startDate.plusDays(i))
			.collect(Collectors.toList());
	}

	public static Date toNullableLegacyDate(LocalDate localDate)
	{
		if(localDate == null) return null;
		return toLegacyDate(localDate);
	}

	public static Date toLegacyDate(LocalDate localDate)
	{
		return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	public static Date toNullableLegacyDateTime(LocalDateTime localDateTime)
	{
		if(localDateTime == null) return null;
		return toLegacyDateTime(localDateTime);
	}
	public static Date toLegacyDateTime(LocalDateTime localDateTime)
	{
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	public static LocalDate toNullableLocalDate(String dateString)
	{
		if(dateString == null || dateString.isEmpty()) return null;
		return toLocalDate(dateString);
	}
	public static LocalDate toNullableLocalDate(Date legacyDate)
	{
		if(legacyDate == null) return null;
		return toZonedLocalDate(legacyDate);
	}
	public static LocalDate toLocalDate(String dateString)
	{
		return toLocalDate(dateString, DateTimeFormatter.ISO_DATE);
	}

	public static LocalDate toLocalDate(String dateString, DateTimeFormatter dateTimeFormatter)
	{
		return LocalDate.parse(dateString, dateTimeFormatter);
	}
	public static LocalDate toNullableZonedLocalDate(String dateString)
	{
		if(dateString == null) return null;
		return toZonedLocalDate(dateString, DateTimeFormatter.ISO_DATE_TIME);
	}
	public static LocalDate toZonedLocalDate(String dateString, DateTimeFormatter dateTimeFormatter)
	{
		ZonedDateTime result = ZonedDateTime.parse(dateString, dateTimeFormatter);
		return result.toLocalDate();
	}
	public static LocalDate toZonedLocalDate(Date legacyDate)
	{
		LocalDate date = Instant
				// get the millis value to build the Instant
				.ofEpochMilli(legacyDate.getTime())
				// convert to JVM default timezone
				.atZone(ZoneId.systemDefault())
				// convert to LocalDate
				.toLocalDate();
		return date;
	}
	public static LocalDateTime toNullableLocalDateTime(Date legacyDate)
	{
		if(legacyDate == null) return null;
		return toLocalDateTime(legacyDate);
	}
	public static LocalDateTime toLocalDateTime(Date legacyDate)
	{
		return new java.sql.Timestamp(legacyDate.getTime()).toLocalDateTime();
	}


	public static Date combineDateAndTime(Date date, Date time)
	{
		Calendar calendarA = Calendar.getInstance();
		calendarA.setTime(date);
		Calendar calendarB = Calendar.getInstance();
		calendarB.setTime(time);

		calendarA.set(Calendar.HOUR_OF_DAY, calendarB.get(Calendar.HOUR_OF_DAY));
		calendarA.set(Calendar.MINUTE, calendarB.get(Calendar.MINUTE));
		calendarA.set(Calendar.SECOND, calendarB.get(Calendar.SECOND));
		calendarA.set(Calendar.MILLISECOND, calendarB.get(Calendar.MILLISECOND));

		return calendarA.getTime();
	}

	public static LocalDate dateStringToNullableLocalDate(String dateString)
	{
		if(dateString == null) return null;
		return dateStringToLocalDate(dateString);
	}

	public static LocalDate dateStringToLocalDate(String dateString)
	{
		return LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
	}

	public static LocalDateTime truncateLocalDateTime(LocalDateTime dateTime, ChronoUnit timeUnit)
	{
		switch(timeUnit)
		{
			case HOURS:
				return dateTime.truncatedTo(ChronoUnit.HOURS);
			case DAYS:
				return dateTime.truncatedTo(ChronoUnit.DAYS);
			case WEEKS:
				return dateTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).truncatedTo(ChronoUnit.DAYS);
			case MONTHS:
				return dateTime.with(TemporalAdjusters.firstDayOfMonth()).truncatedTo(ChronoUnit.DAYS);
			case YEARS:
				return dateTime.with(TemporalAdjusters.firstDayOfYear()).truncatedTo(ChronoUnit.DAYS);
			default:
				throw new UnsupportedTemporalTypeException("Unimplemented temporal type for truncation");
		}
	}
}
