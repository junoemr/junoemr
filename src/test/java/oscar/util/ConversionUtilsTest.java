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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import junit.framework.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class ConversionUtilsTest
{

	@Test
	public void testToIntList() {
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

		stringList.add("not-a-string");
		stringList.add("0.3");
		stringList.add("4L");

		expectedList.add(0);
		expectedList.add(0);
		expectedList.add(0);

		resultList = ConversionUtils.toIntList(stringList);

		assertThat(expectedList, is(resultList));
	}

	@Test
	public void testFromTimeString() throws ParseException
	{
		Assert.assertNull(ConversionUtils.fromTimeString(null));
		Assert.assertNull(ConversionUtils.fromTimeString(""));
		Assert.assertNull(ConversionUtils.fromTimeString("not a time string"));

		String timeString;
		SimpleDateFormat format = new SimpleDateFormat(ConversionUtils.DEFAULT_TIME_PATTERN);
		Date validDate;

		timeString = "12:30:45";
		validDate = format.parse(timeString);
		assertThat(validDate, is(ConversionUtils.fromTimeString(timeString)));

		timeString = "7:20:00";
		validDate = format.parse(timeString);
		assertThat(validDate, is(ConversionUtils.fromTimeString(timeString)));

		timeString = "10:0:27";
		validDate = format.parse(timeString);
		assertThat(validDate, is(ConversionUtils.fromTimeString(timeString)));

		timeString = "09:30:0";
		validDate = format.parse(timeString);
		assertThat(validDate, is(ConversionUtils.fromTimeString(timeString)));

		timeString = "12:10";
		assertNull(ConversionUtils.fromTimeString(timeString));

		timeString = "1:1";
		assertNull(ConversionUtils.fromTimeString(timeString));

		timeString = "0";
		assertNull(ConversionUtils.fromTimeString(timeString));
	}

	@Test
	public void testFromTimeStringNoSeconds() throws ParseException {
		Date validDate;
		String validFormat;
		SimpleDateFormat format = new SimpleDateFormat(ConversionUtils.TIME_PATTERN_NO_SEC);

		validFormat = "12:30";
		validDate = format.parse(validFormat);
		assertThat(validDate, is(ConversionUtils.fromTimeStringNoSeconds(validFormat)));

		validFormat = "0:3";
		validDate = format.parse(validFormat);
		assertThat(validDate, is(ConversionUtils.fromTimeStringNoSeconds(validFormat)));
	}

	@Test
	public void testToTimeStringNoSeconds() {
		Assert.assertEquals("", ConversionUtils.toTimeStringNoSeconds(null));
		Date today = new Date();
		SimpleDateFormat timeFormat = new SimpleDateFormat(ConversionUtils.TIME_PATTERN_NO_SEC);
		String expectedTime = timeFormat.format(today);
		Assert.assertEquals(expectedTime, ConversionUtils.toTimeStringNoSeconds(today));
	}

	@Test
	public void testFromDateString() {
		Assert.assertNull(ConversionUtils.fromDateString(null, null));
		Assert.assertNull(ConversionUtils.fromDateString("", null));
		String dateString = "2019-04-23 09:30:15";

		Date returnedDate = ConversionUtils.fromDateString(dateString, ConversionUtils.DEFAULT_TS_PATTERN);
		SimpleDateFormat formatter = new SimpleDateFormat(ConversionUtils.DEFAULT_TS_PATTERN);
		Assert.assertEquals(dateString, formatter.format(returnedDate));
	}

	@Test
	public void testFromTimestampString() {
		Assert.assertNull(ConversionUtils.fromTimestampString(null));
		Assert.assertNull(ConversionUtils.fromTimestampString(""));

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
	public void testToTimestampString() {
		Assert.assertEquals("", ConversionUtils.toTimestampString(null));
		Date today = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(ConversionUtils.DEFAULT_TS_PATTERN);
		String expectedDateString = formatter.format(today);
		Assert.assertEquals(expectedDateString, ConversionUtils.toTimestampString(today));
	}

	// This gets called with a wide variety of different formats that we shouldn't try to cover
	// Ensure that for a ConversionUtils level format and for one fed in time format it works as expected
	@Test
	public void testToDateString() {
		Assert.assertEquals("", ConversionUtils.toDateTimeString(null));
		Assert.assertEquals("", ConversionUtils.toDateTimeNoSecString(null));
	}

	@Test
	public void testToDateTimeString() {
		Assert.assertEquals("", ConversionUtils.toDateTimeString(null));

		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ConversionUtils.DEFAULT_TS_PATTERN);

		String expectedString = now.format(formatter);

		Assert.assertEquals(expectedString, ConversionUtils.toDateTimeString(now));
	}

	@Test
	public void testToTimeString() {
		Assert.assertEquals("", ConversionUtils.toTimeString(null));

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
	public void testFromLongString () {
		long returnedVal = ConversionUtils.fromLongString(null);
		Assert.assertEquals(0L, returnedVal);
		returnedVal = ConversionUtils.fromLongString("");
		Assert.assertEquals(0L, returnedVal);
		returnedVal = ConversionUtils.fromLongString("not a string");
		Assert.assertEquals(0L, returnedVal);
		returnedVal = ConversionUtils.fromLongString("12345");
		Assert.assertEquals(12345L, returnedVal);
	}

	@Test
	public void testFromIntString() {
		int returnedVal = ConversionUtils.fromIntString(null);
		Assert.assertEquals(0, returnedVal);
		returnedVal = ConversionUtils.fromIntString("");
		Assert.assertEquals(0, returnedVal);
		returnedVal = ConversionUtils.fromIntString("not an integer");
		Assert.assertEquals(0, returnedVal);
		returnedVal = ConversionUtils.fromIntString("123456789");
		Assert.assertEquals(123456789, returnedVal);
	}

	@Test
	public void testToIntString() {
		Assert.assertEquals("0", ConversionUtils.toIntString(null));
		Assert.assertEquals("1", ConversionUtils.toIntString(1));
	}

	@Test
	public void testToBoolString() {
		Assert.assertEquals("0", ConversionUtils.toBoolString(null));
		Assert.assertEquals("0", ConversionUtils.toBoolString(false));
		Assert.assertEquals("1", ConversionUtils.toBoolString(true));
	}

	@Test
	public void testFromBoolString() {
		Assert.assertFalse(ConversionUtils.fromBoolString(""));
		Assert.assertFalse(ConversionUtils.fromBoolString(null));
		Assert.assertFalse(ConversionUtils.fromBoolString("0"));

		Assert.assertTrue(ConversionUtils.fromBoolString("false"));
		Assert.assertTrue(ConversionUtils.fromBoolString("true"));
		Assert.assertTrue(ConversionUtils.fromBoolString("1"));
		Assert.assertTrue(ConversionUtils.fromBoolString("you can shove whatever you want in here"));
	}

	@Test
	public void testFromDoubleString() {
		Assert.assertEquals(0.0, ConversionUtils.fromDoubleString(null));
		Assert.assertEquals(0.0, ConversionUtils.fromDoubleString(""));
		Assert.assertEquals(0.3, ConversionUtils.fromDoubleString("0.3"));
		Assert.assertEquals(0.0, ConversionUtils.fromDoubleString("not a double"));
	}

	@Test
	public void testToDoubleString() {
		Assert.assertEquals("0.3", ConversionUtils.toDoubleString(0.3));
		Assert.assertEquals("0", ConversionUtils.toDoubleString(null));
	}

	@Test
	public void testToDaysFromLong() {
		long MS_IN_DAY = 1000 * 60 * 60 * 24;

		Assert.assertEquals(0, ConversionUtils.toDays(-1L));
		Assert.assertEquals(0, ConversionUtils.toDays(0));
		Assert.assertEquals(1, ConversionUtils.toDays(MS_IN_DAY));
		Assert.assertEquals(3, ConversionUtils.toDays(MS_IN_DAY * 3 + 1L));
	}

	@Test
	public void testPadDateString() {
		Assert.assertEquals("2019-04-03 00:00", ConversionUtils.padDateString("2019-4-3"));
		Assert.assertEquals("2019-04-03 00:00", ConversionUtils.padDateString("2019-04-3"));
		Assert.assertEquals("2019-04-03 00:00", ConversionUtils.padDateString("2019-4-03"));
		Assert.assertEquals("1-2-3", ConversionUtils.padDateString("1-2-3"));
		Assert.assertEquals("not a date", ConversionUtils.padDateString("not a date"));
		Assert.assertEquals("2019-04-03 12:43:08", ConversionUtils.padDateString("2019-4-3 12:43:08"));
		Assert.assertEquals("2019-04-03 12:43:08", ConversionUtils.padDateString("2019-4-03 12:43:08"));
		Assert.assertEquals("2019-04-03 12:43:08", ConversionUtils.padDateString("2019-04-3 12:43:08"));
		Assert.assertEquals("2019-04-03 09:05:02", ConversionUtils.padDateString("2019-04-03 9:05:02"));
		Assert.assertEquals("2019-04-03 09:05:02", ConversionUtils.padDateString("2019-04-03 09:5:02"));
		Assert.assertEquals("2019-04-03 09:05:02", ConversionUtils.padDateString("2019-04-03 09:05:2"));
		Assert.assertEquals("2019-04-03 09:05:02", ConversionUtils.padDateString("2019-04-03 9:5:2"));
		Assert.assertEquals("2019-04-03 09:05:02", ConversionUtils.padDateString("2019-4-3 9:05:02"));
		Assert.assertEquals("2019-04-03 09:05", ConversionUtils.padDateString("2019-04-03 9:05"));
	}

	@Test
	public void testGetLegacyDateFromDateString() {
		Assert.assertNotNull(ConversionUtils.getLegacyDateFromDateString("2019-04-03"));
		Assert.assertNotNull(ConversionUtils.getLegacyDateFromDateString("2019-04-03 09:30:00"));
		Assert.assertNotNull(ConversionUtils.getLegacyDateFromDateString("2019-04-03 09:30"));
		Assert.assertNotNull(ConversionUtils.getLegacyDateFromDateString("2019-04-3"));

		Assert.assertNull(ConversionUtils.getLegacyDateFromDateString("2019-4-03"));
		Assert.assertNull(ConversionUtils.getLegacyDateFromDateString("2019-4-3"));
		Assert.assertNull(ConversionUtils.getLegacyDateFromDateString("2019-04-03 9:30"));
		Assert.assertNull(ConversionUtils.getLegacyDateFromDateString("2019-04-03 9:30:05"));
		Assert.assertNull(ConversionUtils.getLegacyDateFromDateString("19-04-03"));
		Assert.assertNull(ConversionUtils.getLegacyDateFromDateString("2019-0403"));
		Assert.assertNull(ConversionUtils.getLegacyDateFromDateString("not a date"));
		Assert.assertNull(ConversionUtils.getLegacyDateFromDateString(null));
	}

	@Test
	public void testToNullableLegacyDate() {
		Assert.assertNull(ConversionUtils.toNullableLegacyDate(null));
		Calendar today = new GregorianCalendar();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		Date expectedDate = today.getTime();
		assertThat(expectedDate, is(ConversionUtils.toNullableLegacyDate(LocalDate.now())));

	}

	@Test
	public void testToNullableLegacyDateTime() {
		Assert.assertNull(ConversionUtils.toNullableLegacyDateTime(null));
		Date expectedDate = new Date();
		LocalDateTime equalLocalDate = LocalDateTime.ofInstant(expectedDate.toInstant(), ZoneId.systemDefault());
		assertThat(expectedDate, is(ConversionUtils.toNullableLegacyDateTime(equalLocalDate)));
	}

	@Test
	@SuppressWarnings("ConstantConditions")
	public void testToNullableLocalDate() {
		LocalDate today = LocalDate.now();
		Date legacyDate = new Date();

		assertThat(today, is(ConversionUtils.toNullableLocalDate(legacyDate)));

		legacyDate = null;
		Assert.assertNull(ConversionUtils.toNullableLocalDate(legacyDate));
	}

	@Test
	public void testToNullableLocalDateTime() {
		Assert.assertNull(ConversionUtils.toNullableLocalDateTime(null));
		Date today = new Date();
		LocalDateTime expectedTime = LocalDateTime.ofInstant(today.toInstant(), ZoneId.systemDefault());
		assertThat(expectedTime, is(ConversionUtils.toNullableLocalDateTime(today)));
	}

	// Date A + Date B = Date A with timestamp of date B
	@Test
	@SuppressWarnings("deprecation")
	public void testCombineDateAndTime() {
		Calendar desiredTime = new GregorianCalendar();
		desiredTime.set(Calendar.DAY_OF_YEAR, 16);
		desiredTime.set(Calendar.MONTH, 7);
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

		Assert.assertEquals(today.getDate(), combined.getDate());
		Assert.assertEquals(today.getMonth(), combined.getMonth());
		Assert.assertEquals(today.getYear(), combined.getYear());

		Assert.assertNotSame(timeToCombine.getYear(), combined.getYear());
		Assert.assertNotSame(timeToCombine.getMonth(), combined.getMonth());
		Assert.assertNotSame(timeToCombine.getDate(), combined.getDate());
	}
}