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

package oscar.util;

import junit.framework.Assert;
import org.junit.Test;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class ConversionUtilsTest
{
	@Test
	public void toIntList_ValidIntegersAsStrings_ExpectConversion()
	{
		List<String> stringList = new ArrayList<>();
		stringList.add("1");
		stringList.add("2");
		stringList.add("3");
		stringList.add("4");
		stringList.add("5");

		List<Integer> expectedList = new ArrayList<>();
		expectedList.add(1);
		expectedList.add(2);
		expectedList.add(3);
		expectedList.add(4);
		expectedList.add(5);
		List<Integer> resultList = ConversionUtils.toIntList(stringList);

		assertThat(expectedList, is(resultList));
	}

	@Test
	public void toIntList_NonIntegersAsString_ExpectSetToZero()
	{
		List<String> stringList = new ArrayList<>();
		stringList.add("1");
		stringList.add("2");
		stringList.add("not an integer");
		stringList.add("0.3");
		stringList.add("4L");

		List<Integer> expectedList = new ArrayList<>();
		expectedList.add(1);
		expectedList.add(2);
		expectedList.add(0);
		expectedList.add(0);
		expectedList.add(0);

		List<Integer> resultList = ConversionUtils.toIntList(stringList);

		assertThat(expectedList, is(resultList));
	}

	@Test
	public void fromTimeString_NullParameter_ExpectNull()
	{
		Assert.assertNull(ConversionUtils.fromTimeString(null));
	}

	@Test
	public void fromTimeString_EmptyStringParameter_ExpectNull()
	{
		Assert.assertNull(ConversionUtils.fromTimeString(""));
	}

	@Test
	public void fromTimeString_InvalidTimeString_ExpectNull()
	{
		Assert.assertNull(ConversionUtils.fromTimeString("not a time string"));
	}

	@Test
	public void fromTimeString_ValidTimeFormat_ExpectValidDate() throws ParseException
	{
		String timeString = "12:30:45";
		SimpleDateFormat format = new SimpleDateFormat(ConversionUtils.DEFAULT_TIME_PATTERN);
		Date validDate = format.parse(timeString);
		assertThat(validDate, is(ConversionUtils.fromTimeString(timeString)));
	}

	@Test
	public void fromTimeString_ValidFormatSingleHour_ExpectValidDate() throws ParseException
	{
		String timeString = "7:20:00";
		SimpleDateFormat format = new SimpleDateFormat(ConversionUtils.DEFAULT_TIME_PATTERN);
		Date validDate = format.parse(timeString);
		assertThat(validDate, is(ConversionUtils.fromTimeString(timeString)));
	}

	@Test
	public void fromTimeString_ValidFormatSingleMinute_ExpectValidDate() throws ParseException
	{
		String timeString = "10:0:27";
		SimpleDateFormat format = new SimpleDateFormat(ConversionUtils.DEFAULT_TIME_PATTERN);
		Date validDate = format.parse(timeString);
		assertThat(validDate, is(ConversionUtils.fromTimeString(timeString)));
	}

	@Test
	public void fromTimeString_ValidFormatSingleSecond_ExpectValidDate() throws ParseException
	{
		String timeString = "09:30:0";
		SimpleDateFormat format = new SimpleDateFormat(ConversionUtils.DEFAULT_TIME_PATTERN);
		Date validDate = format.parse(timeString);
		assertThat(validDate, is(ConversionUtils.fromTimeString(timeString)));
	}

	@Test
	public void fromTimeString_MissingSeconds_ExpectNull()
	{
		assertNull(ConversionUtils.fromTimeString("12:10"));
	}

	@Test
	public void fromTimeString_MissingMinutesAndSeconds_ExpectNull()
	{
		assertNull(ConversionUtils.fromTimeString("0"));
	}

	@Test
	public void fromTimeStringNoSeconds_ValidString_ExpectValidDate() throws ParseException
	{
		String validFormat = "12:30";
		SimpleDateFormat formatter = new SimpleDateFormat(ConversionUtils.TIME_PATTERN_NO_SEC);
		Date validDate = formatter.parse(validFormat);
		assertThat(validDate, is(ConversionUtils.fromTimeStringNoSeconds(validFormat)));
	}

	@Test
	public void fromTimeStringNoSeconds_SingleDigitHourMinute_ExpectValidDate() throws ParseException
	{
		String validFormat = "0:3";
		SimpleDateFormat formatter = new SimpleDateFormat(ConversionUtils.TIME_PATTERN_NO_SEC);
		Date validDate = formatter.parse(validFormat);
		assertThat(validDate, is(ConversionUtils.fromTimeStringNoSeconds(validFormat)));
	}

	@Test
	public void toTimeStringNoSeconds_NullParameter_ExpectEmptyString()
	{
		Assert.assertEquals("", ConversionUtils.toTimeStringNoSeconds(null));
	}

	@Test
	public void toTimeStringNoSeconds_TodayDate_ExpectDateString() {
		Date today = new Date();
		SimpleDateFormat timeFormat = new SimpleDateFormat(ConversionUtils.TIME_PATTERN_NO_SEC);
		String expectedTime = timeFormat.format(today);
		Assert.assertEquals(expectedTime, ConversionUtils.toTimeStringNoSeconds(today));
	}

	@Test
	public void fromDateString_NullDateString_ExpectNull()
	{
		Assert.assertNull(ConversionUtils.fromDateString(null, null));
	}

	@Test
	public void fromDateString_EmptyDateString_ExpectNull()
	{
		Assert.assertNull(ConversionUtils.fromDateString("", null));
	}

	@Test
	public void fromDateString_DefaultTSPattern_ExpectDate()
	{
		String dateString = "2019-04-23 09:30:15";
		Date returnedDate = ConversionUtils.fromDateString(dateString, ConversionUtils.DEFAULT_TS_PATTERN);
		SimpleDateFormat formatter = new SimpleDateFormat(ConversionUtils.DEFAULT_TS_PATTERN);
		Assert.assertEquals(dateString, formatter.format(returnedDate));
	}

	@Test
	public void fromTimestampString_NullParameter_ExpectNull()
	{
		Assert.assertNull(ConversionUtils.fromTimestampString(null));

	}

	@Test
	public void fromTimestampString_EmptyParameter_ExpectNull()
	{
		Assert.assertNull(ConversionUtils.fromTimestampString(""));
	}

	@Test
	public void fromTimestampString_FixedTimestamp_ExpectDate()
	{
		Calendar fixedDate = new GregorianCalendar();
		fixedDate.set(Calendar.YEAR, 2019);
		fixedDate.set(Calendar.MONTH, 4);
		fixedDate.set(Calendar.DAY_OF_MONTH, 9);
		fixedDate.set(Calendar.HOUR_OF_DAY, 9);
		fixedDate.set(Calendar.MINUTE, 5);
		fixedDate.set(Calendar.SECOND, 15);
		fixedDate.set(Calendar.MILLISECOND, 0);

		Date expectedDay = fixedDate.getTime();

		SimpleDateFormat formatter = new SimpleDateFormat(ConversionUtils.DEFAULT_TS_PATTERN);
		String dateString = formatter.format(expectedDay);

		assertThat(expectedDay, is(ConversionUtils.fromTimestampString(dateString)));
	}

	@Test
	public void toTimestampString_NullParameter_ExpectEmptyString()
	{
		Assert.assertEquals("", ConversionUtils.toTimestampString(null));
	}

	@Test
	public void toTimestampString_DateToday_ExpectTodayTimestampString()
	{
		Date today = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(ConversionUtils.DEFAULT_TS_PATTERN);
		String expectedDateString = formatter.format(today);
		Assert.assertEquals(expectedDateString, ConversionUtils.toTimestampString(today));
	}

	@Test
	public void fromDateString_NullDateStringAndDefaultFormatPattern_ExpectNull()
	{
		Assert.assertNull(ConversionUtils.fromDateString(null, ConversionUtils.DEFAULT_DATE_PATTERN));
	}

	@Test
	public void fromDateString_EmptyDateStringAndDefaultFormatPattern_ExpectNull()
	{
		Assert.assertNull(ConversionUtils.fromDateString("", ConversionUtils.DEFAULT_DATE_PATTERN));
	}

	@Test
	public void fromDateString_FixedDateAndFormatPattern_ExpectDate()
	{
		Calendar fixedDate = new GregorianCalendar();
		fixedDate.set(Calendar.YEAR, 2019);
		fixedDate.set(Calendar.MONTH, 4);
		fixedDate.set(Calendar.DAY_OF_MONTH, 9);
		fixedDate.set(Calendar.HOUR_OF_DAY, 9);
		fixedDate.set(Calendar.MINUTE, 5);
		fixedDate.set(Calendar.SECOND, 15);
		fixedDate.set(Calendar.MILLISECOND, 0);

		Date expectedDay = fixedDate.getTime();

		SimpleDateFormat formatter = new SimpleDateFormat(ConversionUtils.DEFAULT_TS_PATTERN);
		String dateString = formatter.format(expectedDay);

		assertThat(expectedDay, is(ConversionUtils.fromDateString(dateString, ConversionUtils.DEFAULT_TS_PATTERN)));
	}

	@Test
	@SuppressWarnings("ConstantConditions")
	public void toDateString_NullUtilDateAndDefaultFormatPattern_ExpectEmptyString()
	{
		Date nullDate = null;
		Assert.assertEquals("", ConversionUtils.toDateString(nullDate, ConversionUtils.DEFAULT_TS_PATTERN));
	}

	@Test
	public void toDateString_UtilDateAndDefaultFormatPattern_ExpectDateString()
	{
		Date today = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(ConversionUtils.DEFAULT_TS_PATTERN);
		String expectedDateString = formatter.format(today);
		Assert.assertEquals(expectedDateString, ConversionUtils.toDateString(today, ConversionUtils.DEFAULT_TS_PATTERN));
	}

	@Test
	@SuppressWarnings("ConstantConditions")
	public void toDateString_NullLocalDateAndDefaultFormatPattern_ExpectEmptyString()
	{
		LocalDate nullDate = null;
		Assert.assertEquals("", ConversionUtils.toDateString(nullDate, ConversionUtils.DEFAULT_TS_PATTERN));
	}

	@Test
	public void toDateString_FixedLocalDateAndDefaultFormatPattern_ExpectDateString()
	{
		LocalDate fixedDate = LocalDate.now();
		String expectedString = fixedDate.toString();
		Assert.assertEquals(expectedString, ConversionUtils.toDateString(fixedDate, ConversionUtils.DEFAULT_DATE_PATTERN));
	}

	@Test
	@SuppressWarnings("ConstantConditions")
	public void toDateString_NullUtilDate_ExpectEmptyString()
	{
		Date legacyDate = null;
		Assert.assertEquals("", ConversionUtils.toDateString(legacyDate));
	}

	@Test
	@SuppressWarnings("ConstantConditions")
	public void toDateString_NullLocalDate_ExpectEmptyString()
	{
		LocalDate localDate = null;
		Assert.assertEquals("", ConversionUtils.toDateString(localDate));
	}

	@Test
	public void toDateString_UtilDate_ExpectDateString()
	{
		Date legacyDate = new Date();
		SimpleDateFormat legacyFormatter = new SimpleDateFormat(ConversionUtils.DEFAULT_DATE_PATTERN);
		String expectedString = legacyFormatter.format(legacyDate);
		Assert.assertEquals(expectedString, ConversionUtils.toDateString(legacyDate));
	}

	@Test
	public void toDateString_LocalDate_ExpectDateString()
	{
		LocalDate localDate = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ConversionUtils.DEFAULT_DATE_PATTERN);
		String expectedString = formatter.format(localDate);
		Assert.assertEquals(expectedString, ConversionUtils.toDateString(localDate));
	}

	@Test
	public void toDateTimeString_NullLocalDateTimeAndDefaultFormatPattern_ExpectEmptyString()
	{
		Assert.assertEquals("", ConversionUtils.toDateTimeString(null, ConversionUtils.DEFAULT_TS_PATTERN));
	}

	@Test
	public void toDateTimeNoSecString_NullParameter_ExpectEmptyString()
	{
		Assert.assertEquals("", ConversionUtils.toDateTimeNoSecString(null));
	}

	@Test
	public void toDateTimeNoSecString_LocalDateTimeOfNow_ExpectDateTimeString()
	{
		LocalDateTime today = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ConversionUtils.TS_NO_SEC_PATTERN);
		String expectedString = today.format(formatter);
		Assert.assertEquals(expectedString, ConversionUtils.toDateTimeNoSecString(today));
	}

	@Test
	public void toDateTimeString_NullParameter_ExpectEmptyString()
	{
		Assert.assertEquals("", ConversionUtils.toDateTimeString(null));
	}

	@Test
	public void toDateTimeString_LocalDateTimeOfNow_ExpectDateTimeString()
	{
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ConversionUtils.DEFAULT_TS_PATTERN);
		String expectedString = now.format(formatter);
		Assert.assertEquals(expectedString, ConversionUtils.toDateTimeString(now));
	}

	@Test
	public void toTimeString_NullParameter_ExpectEmptyString()
	{
		Assert.assertEquals("", ConversionUtils.toTimeString(null));
	}

	@Test
	public void toTimeString_FixedTime_ExpectTimeString()
	{
		Calendar expectedTime = new GregorianCalendar();
		expectedTime.set(Calendar.HOUR_OF_DAY, 13);
		expectedTime.set(Calendar.MINUTE, 32);
		expectedTime.set(Calendar.SECOND, 0);

		Date today = expectedTime.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat(ConversionUtils.DEFAULT_TIME_PATTERN);
		String expectedTimeString = formatter.format(today);

		Assert.assertEquals(expectedTimeString, ConversionUtils.toTimeString(today));
	}

	@Test
	public void fromLongString_NullParameter_ExpectZero()
	{
		long returnedVal = ConversionUtils.fromLongString(null);
		Assert.assertEquals(0L, returnedVal);
	}

	@Test
	public void fromLongString_EmptyStringParameter_ExpectZero()
	{
		long returnedVal = ConversionUtils.fromLongString("");
		Assert.assertEquals(0L, returnedVal);
	}

	@Test
	public void fromLongString_NotLongStringParameter_ExpectZero()
	{
		long returnedVal = ConversionUtils.fromLongString("not a string");
		Assert.assertEquals(0L, returnedVal);
	}

	@Test
	public void fromLongString_LongStringParameter_ExpectStringAsLong()
	{
		long returnedVal = ConversionUtils.fromLongString("12345");
		Assert.assertEquals(12345L, returnedVal);
	}

	@Test
	public void fromIntString_NullParameter_ExpectZero()
	{
		int returnedVal = ConversionUtils.fromIntString(null);
		Assert.assertEquals(0, returnedVal);
	}

	@Test
	public void fromIntString_EmptyStringParameter_ExpectZero()
	{
		int returnedVal = ConversionUtils.fromIntString("");
		Assert.assertEquals(0, returnedVal);
	}

	@Test
	public void fromIntString_NonIntStringParameter_ExpectZero()
	{
		int returnedVal = ConversionUtils.fromIntString("not an integer");
		Assert.assertEquals(0, returnedVal);
	}

	@Test
	public void fromIntString_IntStringParameter_ExpectStringAsInt()
	{
		int returnedVal = ConversionUtils.fromIntString("123456789");
		Assert.assertEquals(123456789, returnedVal);
	}

	@Test
	public void toIntString_NullParameter_ExpectZeroString()
	{
		Assert.assertEquals("0", ConversionUtils.toIntString(null));
	}

	@Test
	public void toIntString_IntParameter_ExpectIntAsString() {
		Assert.assertEquals("1", ConversionUtils.toIntString(1));
	}

	@Test
	public void toBoolString_NullParameter_ExpectZeroString()
	{
		Assert.assertEquals("0", ConversionUtils.toBoolString(null));
	}

	@Test
	public void toBoolString_FalseParameter_ExpectZeroString()
	{
		Assert.assertEquals("0", ConversionUtils.toBoolString(false));
	}

	@Test
	public void toBoolString_TrueParameter_ExpectOneString()
	{
		Assert.assertEquals("1", ConversionUtils.toBoolString(true));
	}

	@Test
	public void fromBoolString_NullParameter_ExpectFalse()
	{
		Assert.assertFalse(ConversionUtils.fromBoolString(null));
	}

	@Test
	public void fromBoolString_EmptyStringParameter_ExpectFalse()
	{
		Assert.assertFalse(ConversionUtils.fromBoolString(""));
	}

	@Test
	public void fromBoolString_FalseParameter_ExpectFalse()
	{
		Assert.assertFalse(ConversionUtils.fromBoolString("0"));
		Assert.assertFalse(ConversionUtils.fromBoolString("false"));
	}

	@Test
	public void fromBoolString_OtherStringParameter_ExpectFalse()
	{
		Assert.assertFalse(ConversionUtils.fromBoolString("any other string"));
	}

	@Test
	public void fromBoolString_TrueParameter_ExpectTrue() {

		Assert.assertTrue(ConversionUtils.fromBoolString("true"));
		Assert.assertTrue(ConversionUtils.fromBoolString("1"));
	}

	@Test
	public void hasContent_Null_ExpectFalse()
	{
		Assert.assertFalse(ConversionUtils.hasContent(null));
	}

	@Test
	public void hasContent_EmptyString_ExpectFalse()
	{
		Assert.assertFalse(ConversionUtils.hasContent(""));
		Assert.assertFalse(ConversionUtils.hasContent(" "));
	}

	@Test
	public void hasContent_SomeStr_ExpectTrue()
	{
		Assert.assertTrue(ConversionUtils.hasContent("0"));
		Assert.assertTrue(ConversionUtils.hasContent("anything you want"));
	}

	@Test
	public void fromDoubleString_NullParameter_ExpectZero()
	{
		Assert.assertEquals(0.0, ConversionUtils.fromDoubleString(null));
	}

	@Test
	public void fromDoubleString_EmptyStringParameter_ExpectZero()
	{
		Assert.assertEquals(0.0, ConversionUtils.fromDoubleString(""));
	}

	@Test
	public void fromDoubleString_NotDoubleStringParameter_ExpectZero()
	{
		Assert.assertEquals(0.0, ConversionUtils.fromDoubleString("not a double"));
	}

	@Test
	public void fromDoubleString_DoubleParameter_ExpectStringAsDouble() {
		Assert.assertEquals(0.3, ConversionUtils.fromDoubleString("0.3"));
	}

	@Test
	public void toDoubleString_NullParameter_ExpectZero()
	{
		Assert.assertEquals("0.0", ConversionUtils.toDoubleString(null));
	}

	@Test
	public void toDoubleString_DoubleParameter_ExpectDoubleAsString() {
		Assert.assertEquals("0.3", ConversionUtils.toDoubleString(0.3));
	}

	@Test
	public void toDays_NegativeLongParameter_ExpectZero()
	{
		Assert.assertEquals(0, ConversionUtils.toDays(-1L));
	}

	@Test
	public void toDays_ZeroParameter_ExpectZero()
	{
		Assert.assertEquals(0, ConversionUtils.toDays(0));
	}

	@Test
	public void toDays_LongParameterBetweenDays_ExpectFlooredDayCount()
	{
		long MS_IN_DAY = 1000 * 60 * 60 * 24;
		Assert.assertEquals(3, ConversionUtils.toDays(MS_IN_DAY * 3 + 1L));
		Assert.assertEquals(2, ConversionUtils.toDays(MS_IN_DAY * 3 - 1L));
	}

	@Test
	public void padDateTimeString_DateTimeStringSingleDigitDate_ExpectPaddedString()
	{
		Assert.assertEquals("2019-04-03 00:00", ConversionUtils.padDateTimeString("2019-04-3 00:00:00"));
	}

	@Test
	public void padDateTimeString_DateTimeStringSingleDigitMonth_ExpectPaddedString()
	{
		Assert.assertEquals("2019-04-03 12:43:08", ConversionUtils.padDateTimeString("2019-4-03 12:43:08"));
	}

	@Test
	public void padDateTimeString_MultipleSingleDigitFields_ExpectPaddedString()
	{
		Assert.assertEquals("2019-05-01 09:30:05", ConversionUtils.padDateTimeString("2019-5-1 9:30:5"));
	}

	@Test
	public void padDateTimeString_DateTimeStringSingleDigitHour_ExpectPaddedString()
	{
		Assert.assertEquals("2019-04-03 09:05:02", ConversionUtils.padDateTimeString("2019-04-03 9:05:02"));
	}

	@Test
	public void padDateTimeString_DateTimeStringSingleDigitMinute_ExpectPaddedString()
	{
		Assert.assertEquals("2019-04-03 09:05:02", ConversionUtils.padDateTimeString("2019-04-03 09:5:02"));
	}

	@Test
	public void padDateTimeString_DateTimeStringSingleDigitSecond_ExpectPaddedString()
	{
		Assert.assertEquals("2019-04-03 09:05:02", ConversionUtils.padDateTimeString("2019-04-03 09:05:2"));
	}

	@Test
	public void padDateTimeString_DateTimeStringNoSeconds_ExpectPaddedString()
	{
		Assert.assertEquals("2019-04-03 09:05", ConversionUtils.padDateTimeString("2019-04-03 9:05"));
	}

	@Test
	public void padDateTimeString_DateTimeString_ExpectSameString()
	{
		Assert.assertEquals("2019-04-03 09:05:05", ConversionUtils.padDateTimeString("2019-04-03 09:05:05"));
	}

	@Test
	public void padDateTimeString_DateString_ExpectSameString()
	{
		Assert.assertEquals("2019-04-03", ConversionUtils.padDateTimeString("2019-04-03"));
	}

	@Test
	public void padDateTimeString_DateStringSingleDigitFields_ExpectPaddedDateString()
	{
		Assert.assertEquals("2019-04-03", ConversionUtils.padDateTimeString("2019-4-3"));
	}

	@Test
	public void padDateTimeString_OtherString_ExpectSameString()
	{
		Assert.assertEquals("some other string", ConversionUtils.padDateTimeString("some other string"));
	}

	@Test
	public void padDateTimeString_DateTimeStringNoMinutesOrSeconds_ExpectPaddedString()
	{
		Assert.assertEquals("2019-04-03 09:00", ConversionUtils.padDateTimeString("2019-04-03 09"));
	}

	@Test
	public void padDateTimeString_DateTimeStringNotFourDigitYear_ExpectSameString()
	{
		Assert.assertEquals("123-04-05 06:07:08", ConversionUtils.padDateTimeString("123-04-05 06:07:08"));
		Assert.assertEquals("12345-06-07 08:09:10", ConversionUtils.padDateTimeString("12345-06-07 08:09:10"));
	}

	@Test
	public void padDateTimeString_EmptyString_ExpectSameString()
	{
		Assert.assertEquals("", ConversionUtils.padDateTimeString(""));
	}

	@Test
	public void padDateTimeString_Null_ExpectEmptyString()
	{
		Assert.assertEquals("", ConversionUtils.padDateTimeString(null));
	}

	@Test
	public void padDateString_SingleDigitMonth_ExpectPaddedString()
	{
		Assert.assertEquals("2019-04-03", ConversionUtils.padDateString("2019-4-03"));
	}

	@Test
	public void padDateString_SingleDigitDate_ExpectPaddedString()
	{
		Assert.assertEquals("2019-04-03", ConversionUtils.padDateString("2019-04-3"));
	}

	@Test
	public void padDateString_NotFourDigitYear_ExpectSameString()
	{
		Assert.assertEquals("12345-6-7", ConversionUtils.padDateString("12345-6-7"));
		Assert.assertEquals("123-4-5", ConversionUtils.padDateString("123-4-5"));
	}

	@Test
	public void padDateString_MissingDate_ExpectSameString()
	{
		Assert.assertEquals("2019-04", ConversionUtils.padDateString("2019-04"));
	}

	@Test
	public void padDateString_OnlyYear_ExpectSameString()
	{
		Assert.assertEquals("2019", ConversionUtils.padDateString("2019"));
	}

	@Test
	public void padDateString_OtherString_ExpectSameString()
	{
		Assert.assertEquals("not a date", ConversionUtils.padDateString("not a date"));
	}

	@Test
	public void getLegacyDateFromDateString_DateString_ExpectDate()
	{
		// assert that this produces the correct date instead of just not null
		String dateString = "2019-04-03";

		Calendar expectedCalendarDate = new GregorianCalendar();
		expectedCalendarDate.set(Calendar.YEAR, 2019);
		expectedCalendarDate.set(Calendar.MONTH, Calendar.APRIL);
		expectedCalendarDate.set(Calendar.DAY_OF_MONTH, 3);
		expectedCalendarDate.set(Calendar.HOUR_OF_DAY, 0);
		expectedCalendarDate.set(Calendar.MINUTE, 0);
		expectedCalendarDate.set(Calendar.SECOND, 0);
		expectedCalendarDate.set(Calendar.MILLISECOND, 0);
		Date expectedDate = expectedCalendarDate.getTime();

		assertThat(expectedDate, is(ConversionUtils.getLegacyDateFromDateString(dateString)));
	}

	@Test
	public void getLegacyDateFromDateString_DateTimeString_ExpectDate()
	{
		String dateTimeString = "2019-04-03 09:30:00";

		Calendar expectedCalendarDate = new GregorianCalendar();
		expectedCalendarDate.set(Calendar.YEAR, 2019);
		expectedCalendarDate.set(Calendar.MONTH, Calendar.APRIL);
		expectedCalendarDate.set(Calendar.DAY_OF_MONTH, 3);
		expectedCalendarDate.set(Calendar.HOUR_OF_DAY, 9);
		expectedCalendarDate.set(Calendar.MINUTE, 30);
		expectedCalendarDate.set(Calendar.SECOND, 0);
		expectedCalendarDate.set(Calendar.MILLISECOND, 0);
		Date expectedDate = expectedCalendarDate.getTime();

		assertThat(expectedDate, is(ConversionUtils.getLegacyDateFromDateString(dateTimeString)));
	}

	@Test
	public void getLegacyDateFromDateString_DateTimeStringWithoutSecond_ExpectDate()
	{
		String dateTimeString = "2019-04-03 09:30";

		Calendar expectedCalendarDate = new GregorianCalendar();
		expectedCalendarDate.set(Calendar.YEAR, 2019);
		expectedCalendarDate.set(Calendar.MONTH, Calendar.APRIL);
		expectedCalendarDate.set(Calendar.DAY_OF_MONTH, 3);
		expectedCalendarDate.set(Calendar.HOUR_OF_DAY, 9);
		expectedCalendarDate.set(Calendar.MINUTE, 30);
		expectedCalendarDate.set(Calendar.SECOND, 0);
		expectedCalendarDate.set(Calendar.MILLISECOND, 0);
		Date expectedDate = expectedCalendarDate.getTime();

		assertThat(expectedDate, is(ConversionUtils.getLegacyDateFromDateString(dateTimeString)));
	}

	@Test
	public void getLegacyDateFromDateString_DateStringSingleDigitDate_ExpectDate()
	{
		String dateString = "2019-04-3";

		Calendar expectedCalendarDate = new GregorianCalendar();
		expectedCalendarDate.set(Calendar.YEAR, 2019);
		expectedCalendarDate.set(Calendar.MONTH, Calendar.APRIL);
		expectedCalendarDate.set(Calendar.DAY_OF_MONTH, 3);
		expectedCalendarDate.set(Calendar.HOUR_OF_DAY, 0);
		expectedCalendarDate.set(Calendar.MINUTE, 0);
		expectedCalendarDate.set(Calendar.SECOND, 0);
		expectedCalendarDate.set(Calendar.MILLISECOND, 0);
		Date expectedDate = expectedCalendarDate.getTime();

		assertThat(expectedDate, is(ConversionUtils.getLegacyDateFromDateString(dateString)));
	}

	@Test
	public void getLegacyDateFromDateString_DateTimeStringSingleDigitSecond_ExpectDate()
	{
		String dateTimeString = "2019-04-03 09:30:5";

		Calendar expectedCalendarDate = new GregorianCalendar();
		expectedCalendarDate.set(Calendar.YEAR, 2019);
		expectedCalendarDate.set(Calendar.MONTH, Calendar.APRIL);
		expectedCalendarDate.set(Calendar.DAY_OF_MONTH, 3);
		expectedCalendarDate.set(Calendar.HOUR_OF_DAY, 9);
		expectedCalendarDate.set(Calendar.MINUTE, 30);
		expectedCalendarDate.set(Calendar.SECOND, 5);
		expectedCalendarDate.set(Calendar.MILLISECOND, 0);
		Date expectedDate = expectedCalendarDate.getTime();

		assertThat(expectedDate, is(ConversionUtils.getLegacyDateFromDateString(dateTimeString)));
	}

	@Test
	public void getLegacyDateFromDateString_DateStringSingleDigitMonth_ExpectNull()
	{
		Assert.assertNull(ConversionUtils.getLegacyDateFromDateString("2019-4-03"));
	}

	@Test
	public void getLegacyDateFromDateString_DateTimeStringSingleDigitHour_ExpectNull()
	{
		Assert.assertNull(ConversionUtils.getLegacyDateFromDateString("2019-04-03 9:30"));
	}

	@Test
	public void getLegacyDateFromDateString_DateTimeStringSingleDigitMinute_ExpectNull()
	{
		Assert.assertNull(ConversionUtils.getLegacyDateFromDateString("2019-04-03 09:3:05"));
	}

	@Test
	public void getLegacyDateFromDateString_DateStringTwoDigitYear_ExpectNull()
	{
		Assert.assertNull(ConversionUtils.getLegacyDateFromDateString("19-04-03"));
	}

	@Test
	public void getLegacyDateFromDateString_NullParameter_ExpectNull()
	{
		Assert.assertNull(ConversionUtils.getLegacyDateFromDateString(null));
	}

	@Test
	public void getLegacyDateFromDateString_UnknownDateFormat_ExpectNull()
	{
		Assert.assertNull(ConversionUtils.getLegacyDateFromDateString("2019-0403"));
	}

	@Test
	public void getLegacyDateFromDateString_NonDateString_ExpectNull()
	{
		Assert.assertNull(ConversionUtils.getLegacyDateFromDateString("not a date"));
	}

	@Test
	public void toNullableLegacyDate_NullParameter_ExpectNull()
	{
		Assert.assertNull(ConversionUtils.toNullableLegacyDate(null));
	}

	@Test
	public void toNullableLegacyDate_LocalDateAtNow_ExpectDate()
	{
		Calendar today = new GregorianCalendar();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		Date expectedDate = today.getTime();

		LocalDate comparisonDate = expectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		assertThat(expectedDate, is(ConversionUtils.toNullableLegacyDate(comparisonDate)));
	}

	@Test
	public void toLegacyDate_FixedLocalDate_ExpectDate() throws ParseException
	{
		String fixedDateString = "2019-04-30";
		LocalDate fixedLocalDate = LocalDate.parse(fixedDateString);
		SimpleDateFormat formatter = new SimpleDateFormat(ConversionUtils.DEFAULT_DATE_PATTERN);
		Date expectedDate = formatter.parse(fixedDateString);
		assertThat(expectedDate, is(ConversionUtils.toLegacyDate(fixedLocalDate)));
	}

	@Test
	public void toNullableLegacyDateTime_NullParameter_ExpectNull()
	{
		Assert.assertNull(ConversionUtils.toNullableLegacyDateTime(null));
	}

	@Test
	public void toNullableLegacyDateTime_FixedLocalDateTime_ExpectDate()
	{
		Date expectedDate = new Date();
		LocalDateTime equalLocalDate = LocalDateTime.ofInstant(expectedDate.toInstant(), ZoneId.systemDefault());
		assertThat(expectedDate, is(ConversionUtils.toNullableLegacyDateTime(equalLocalDate)));
	}

	@Test
	public void toLegacyDateTime_FixedLocalDateTime_ExpectDate()
	{
		Date expectedDate = new Date();
		LocalDateTime equalLocalDate = LocalDateTime.ofInstant(expectedDate.toInstant(), ZoneId.systemDefault());
		assertThat(expectedDate, is(ConversionUtils.toLegacyDateTime(equalLocalDate)));
	}

	@Test
	@SuppressWarnings("ConstantConditions")
	public void toNullableLocalDate_NullString_ExpectNull()
	{
		String nullString = null;
		Assert.assertNull(ConversionUtils.toNullableLocalDate(nullString));
	}

	@Test
	public void toNullableLocalDate_EmptyString_ExpectNull()
	{
		Assert.assertNull(ConversionUtils.toNullableLocalDate(""));
	}

	@Test
	public void toNullableLocalDate_FixedDateString_ExpectLocalDate()
	{
		String fixedDateString = "2019-04-30";
		LocalDate expectedDate = LocalDate.of(2019, 4, 30);
		assertThat(expectedDate, is(ConversionUtils.toNullableLocalDate(fixedDateString)));
	}

	@Test
	@SuppressWarnings("ConstantConditions")
	public void toNullableLocalDate_NullUtilDate_ExpectNull()
	{
		Date legacyDate = null;
		Assert.assertNull(ConversionUtils.toNullableLocalDate(legacyDate));
	}

	@Test
	public void toNullableLocalDate_FixedDate_ExpectLocalDate()
	{
		LocalDate expectedLocalDate = LocalDate.of(2019, 4,30);
		Calendar expectedTime = new GregorianCalendar();
		expectedTime.set(Calendar.YEAR, 2019);
		expectedTime.set(Calendar.MONTH, Calendar.APRIL);
		expectedTime.set(Calendar.DAY_OF_MONTH, 30);
		expectedTime.set(Calendar.HOUR, 2);
		expectedTime.set(Calendar.MINUTE, 25);
		expectedTime.set(Calendar.SECOND, 0);
		expectedTime.set(Calendar.MILLISECOND, 0);
		Date fixedDate = expectedTime.getTime();
		assertThat(expectedLocalDate, is(ConversionUtils.toNullableLocalDate(fixedDate)));
	}

	@Test
	public void toZonedLocalDate_DateString_ExpectLocalDate()
	{
		String dateString = "2019-04-30 18:40:33";
		LocalDate expectedDate = LocalDate.of(2019, 4, 30);
		Date legacyDate = ConversionUtils.getLegacyDateFromDateString(dateString);
		assertThat(expectedDate, is(ConversionUtils.toZonedLocalDate(legacyDate)));
	}

	@Test
	public void toZonedLocalDate_DateStringSpecifiedFormatter_ExpectLocalDate()
	{
		String dateString = "2019-04-03T11:59:59Z";
		LocalDate expectedDate = LocalDate.parse(dateString, DateTimeFormatter.ISO_ZONED_DATE_TIME);
		assertThat(expectedDate, is(ConversionUtils.toZonedLocalDate(dateString, DateTimeFormatter.ISO_ZONED_DATE_TIME)));
	}

	@Test
	public void toLocalDate_DateString_ExpectLocalDate()
	{
		String dateString = "2019-04-30";
		LocalDate expectedDate = LocalDate.of(2019, 4, 30);
		assertThat(expectedDate, is(ConversionUtils.toLocalDate(dateString)));
	}

	@Test
	public void toLocalDate_DateStringSpecifiedFormatter_ExpectLocalDate()
	{
		String dateString = "2019-04-30";
		LocalDate expectedDate = LocalDate.of(2019, 4, 30);
		assertThat(expectedDate, is(ConversionUtils.toLocalDate(dateString, DateTimeFormatter.ISO_LOCAL_DATE)));
	}

	@Test
	public void toZonedLocalDate_FixedDate_ExpectLocalDate()
	{
		LocalDate expectedLocalDate = LocalDate.of(2019, 4,30);
		Calendar expectedTime = new GregorianCalendar();
		expectedTime.set(Calendar.YEAR, 2019);
		expectedTime.set(Calendar.MONTH, Calendar.APRIL);
		expectedTime.set(Calendar.DAY_OF_MONTH, 30);
		Date fixedDate = expectedTime.getTime();
		assertThat(expectedLocalDate, is(ConversionUtils.toZonedLocalDate(fixedDate)));
	}

	@Test
	public void toNullableLocalDateTime_NullParameter_ExpectNull()
	{
		Assert.assertNull(ConversionUtils.toNullableLocalDateTime(null));
	}

	@Test
	public void toNullableLocalDateTime_FixedDate_ExpectSameDate()
	{
		Date today = new Date();
		LocalDateTime expectedTime = LocalDateTime.ofInstant(today.toInstant(), ZoneId.systemDefault());
		assertThat(expectedTime, is(ConversionUtils.toNullableLocalDateTime(today)));
	}

	@Test
	public void toLocalDateTime_FixedDate_ExpectLocalDateTime()
	{
		LocalDateTime expectedDateTime = LocalDateTime.of(2019, 4, 30, 12, 30, 45);
		Calendar expectedTime = new GregorianCalendar();
		expectedTime.set(Calendar.YEAR, 2019);
		expectedTime.set(Calendar.MONTH, Calendar.APRIL);
		expectedTime.set(Calendar.DAY_OF_MONTH, 30);
		expectedTime.set(Calendar.HOUR_OF_DAY, 12);
		expectedTime.set(Calendar.MINUTE, 30);
		expectedTime.set(Calendar.SECOND, 45);
		expectedTime.set(Calendar.MILLISECOND, 0);
		Date fixedDate = expectedTime.getTime();
		assertThat(expectedDateTime, is(ConversionUtils.toLocalDateTime(fixedDate)));
	}

	@Test
	public void toNullableLocalDateTime_BackAndForthDateConversions_ExpectSameLocalDateTime()
	{
		String startTime = "2019-04-30T23:30:45-08:00";
		String parseFormat = "yyyy-MM-dd'T'HH:mm:ssxxx";
		LocalDateTime expectedLocalDateTime = LocalDateTime.parse(startTime, DateTimeFormatter.ISO_ZONED_DATE_TIME);
		Date transformUtilDate = ConversionUtils.getLegacyDateFromDateString(startTime, parseFormat);

		LocalDateTime comparisonLocalDatetime = ConversionUtils.toNullableLocalDateTime(transformUtilDate);
		transformUtilDate = java.sql.Timestamp.valueOf(comparisonLocalDatetime);
		comparisonLocalDatetime = ConversionUtils.toNullableLocalDateTime(transformUtilDate);

		assertThat(expectedLocalDateTime, is(comparisonLocalDatetime));
	}

	@Test
	@SuppressWarnings("deprecation")
	public void combineDateAndTime_TodayDateAndFixedTime_ExpectTodayDate()
	{
		Calendar desiredTime = new GregorianCalendar();
		desiredTime.set(Calendar.DAY_OF_MONTH, 16);
		desiredTime.set(Calendar.MONTH, 7);
		desiredTime.set(Calendar.YEAR, 1969);
		desiredTime.set(Calendar.HOUR_OF_DAY, 13);
		desiredTime.set(Calendar.MINUTE, 32);
		desiredTime.set(Calendar.SECOND, 0);

		Date today = new Date();
		Date timeToCombine = desiredTime.getTime();

		Date combined = ConversionUtils.combineDateAndTime(today, timeToCombine);

		Assert.assertEquals(today.getDate(), combined.getDate());
		Assert.assertEquals(today.getMonth(), combined.getMonth());
		Assert.assertEquals(today.getYear(), combined.getYear());
	}

	@Test
	@SuppressWarnings("deprecation")
	public void combineDateAndTime_TodayDateAndFixedTime_ExpectFixedTime()
	{
		Calendar desiredTime = new GregorianCalendar();
		desiredTime.set(Calendar.DAY_OF_MONTH, 16);
		desiredTime.set(Calendar.MONTH, Calendar.JULY);
		desiredTime.set(Calendar.YEAR, 1969);
		desiredTime.set(Calendar.HOUR_OF_DAY, 13);
		desiredTime.set(Calendar.MINUTE, 32);
		desiredTime.set(Calendar.SECOND, 0);

		Date today = new Date();
		Date timeToCombine = desiredTime.getTime();

		Date combined = ConversionUtils.combineDateAndTime(today, timeToCombine);

		Assert.assertEquals(combined.getHours(), 13);
		Assert.assertEquals(combined.getMinutes(), 32);
		Assert.assertEquals(combined.getSeconds(), 0);
	}

	@Test
	@SuppressWarnings("deprecation")
	public void combineDateAndTime_DifferentFixedDates_ExpectFirstDate()
	{
		Calendar desiredTime = new GregorianCalendar();
		desiredTime.set(Calendar.DAY_OF_MONTH, 16);
		desiredTime.set(Calendar.MONTH, Calendar.JULY);
		desiredTime.set(Calendar.YEAR, 1969);
		desiredTime.set(Calendar.HOUR_OF_DAY, 13);
		desiredTime.set(Calendar.MINUTE, 32);
		desiredTime.set(Calendar.SECOND, 0);
		Date timeToCombine = desiredTime.getTime();

		Calendar baseTime = new GregorianCalendar();
		baseTime.set(Calendar.DAY_OF_MONTH, 8);
		baseTime.set(Calendar.MONTH, Calendar.MARCH);
		baseTime.set(Calendar.YEAR, 2006);
		baseTime.set(Calendar.HOUR_OF_DAY, 17);
		baseTime.set(Calendar.MINUTE, 17);
		baseTime.set(Calendar.SECOND, 17);
		Date baseDate = baseTime.getTime();

		Date combined = ConversionUtils.combineDateAndTime(baseDate, timeToCombine);

		Assert.assertNotSame(timeToCombine.getYear(), combined.getYear());
		Assert.assertNotSame(timeToCombine.getMonth(), combined.getMonth());
		Assert.assertNotSame(timeToCombine.getDate(), combined.getDate());
	}

	@Test
	public void toNullableLocalTime_TimeString_ExpectNull()
	{
		String dateString = "";
		assertThat(null, is(ConversionUtils.toNullableLocalTime(dateString)));
	}

	@Test
	public void toLocalTime_TimeString_ExpectLocalTime()
	{
		String dateString = "09:30:00";
		LocalTime expectedTime = LocalTime.of(9, 30, 0);
		assertThat(expectedTime, is(ConversionUtils.toLocalTime(dateString)));
	}

	@Test
	public void toLocalTime_TimeStringSpecifiedFormatter_ExpectLocalTime()
	{
		String dateString = "21:45:00";
		LocalTime expectedTime = LocalTime.of(21, 45, 0);
		assertThat(expectedTime, is(ConversionUtils.toLocalTime(dateString, DateTimeFormatter.ISO_TIME)));
	}

	@Test
	public void toTimestamp_ServerTimeZoneLocalDateTime_ExpectServerTimeZoneTimestamp()
	{
		LocalDateTime currTime = LocalDateTime.now();
		Timestamp timestamp = ConversionUtils.toTimestamp(currTime);
		Assert.assertEquals("expected timestamp to equal LocalDateTime",currTime, timestamp.toLocalDateTime());
	}
}