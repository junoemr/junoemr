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
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import com.sun.istack.Nullable;
import junit.framework.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

	// Revisit the test cases here later
	@Test
	public void testFromTimeString() throws ParseException
	{
		List<String> validTimeFormats = new ArrayList<>();
		validTimeFormats.add("12:30:45");
		validTimeFormats.add("7:20:00");
		validTimeFormats.add("10:0:27");
		validTimeFormats.add("09:30:0");
		validTimeFormats.add("0:0:55");
		validTimeFormats.add("0:45:0");
		validTimeFormats.add("0:0:0");
		SimpleDateFormat format = new SimpleDateFormat(ConversionUtils.DEFAULT_TIME_PATTERN);
		Date validDate;
		Date returnedDate;
		for (String validTimeFormat : validTimeFormats)
		{
			validDate = format.parse(validTimeFormat);
			returnedDate = ConversionUtils.fromTimeString(validTimeFormat);
			assertThat(validDate, is(returnedDate));

		}

		List<String> invalidTimeFormats = new ArrayList<>();
		invalidTimeFormats.add("12:10");
		invalidTimeFormats.add("0:30");
		invalidTimeFormats.add("20:0");
		invalidTimeFormats.add("1:1");
		invalidTimeFormats.add("00");

		for (String timeFormat : invalidTimeFormats)
		{
			assertNull(ConversionUtils.fromTimeString(timeFormat));
		}
	}

	@Test
	public void testFromTimeStringNoSeconds() throws ParseException {
		Date validDate;
		Date returnedDate;
		SimpleDateFormat format = new SimpleDateFormat(ConversionUtils.TIME_PATTERN_NO_SEC);
		List<String> validTimeFormats = new ArrayList<>();
		validTimeFormats.add("12:30");
		validTimeFormats.add("5:15");
		validTimeFormats.add("20:0");
		validTimeFormats.add("1:3");

		for (String validFormat : validTimeFormats)
		{
			validDate = format.parse(validFormat);
			returnedDate = ConversionUtils.fromTimeStringNoSeconds(validFormat);
			assertThat(validDate, is(returnedDate));
		}
	}

	@Test
	public void testToTimeStringNoSeconds() {

		Assert.assertEquals("", ConversionUtils.toTimeStringNoSeconds(null));
		Date today = new Date();
		SimpleDateFormat timeFormat = new SimpleDateFormat(ConversionUtils.TIME_PATTERN_NO_SEC);
		String expectedTime = timeFormat.format(today);
		Assert.assertEquals(expectedTime, ConversionUtils.toTimeStringNoSeconds(today));
	}

	// Multiple places call this - need to ensure we support all the different formats
	@Test
	public void testFromDateString() {
		List<String> validDateStrings = new ArrayList<>();
		List<String> invalidDateStrings = new ArrayList<>();
	}

	@Test
	public void testFromTimestampString() {
		Assert.assertNull(ConversionUtils.fromTimestampString(null));
		Assert.assertNull(ConversionUtils.fromTimestampString(""));

		// also check that this actually matches
		Assert.assertNotNull(ConversionUtils.fromTimestampString("2019-04-09 09:05:15"));
	}

	@Test
	public void testToTimestampString() {
		List<Date> validDates = new ArrayList<>();
		List<Date> invalidDates = new ArrayList<>();
	}

	// this is called everywhere... going to be a very large set of tests
	// since we need to accommodate all of our currently supported SimpleDateFormats
	@Test
	public void testToDateString() {

	}

	@Test
	public void testToDateTimeString() {

	}

	@Test
	public void testToTimeString() {
		List<String> validTimeStrings = new ArrayList<>();
		List<String> invalidTimeStrings = new ArrayList<>();
	}

	@Test
	public void testToDateTimeNoSecString() {

	}

	@Test
	public void testFromLongString () {
		List<String> validLongStrings = new ArrayList<>();
		List<String> invalidLongStrings = new ArrayList<>();
	}

	@Test
	public void testFromIntString() {
		List<String> validIntStrings = new ArrayList<>();
		List<String> invalidIntStrings = new ArrayList<>();
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
		// technically this is what happens!!!!
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
		// We may want to consider whether this is the behavior we really want
		// Look at what else relies on this behavior
		Assert.assertEquals("0", ConversionUtils.toDoubleString(null));
	}

	@Test
	public void testToDaysFromDate() {
		// test exists as a stub but we do not currently use this function
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
		// *** getLegacyDateFromDateString(dateString, ConversionUtils.DEFAULT_TS_PATTERN) ***
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
		LocalDateTime differentLocalDate = LocalDateTime.now();
		assertThat(expectedDate, not(ConversionUtils.toNullableLegacyDateTime(differentLocalDate)));
	}

	// Need to figure out a more proper way to assert that we can properly go
	// String -> LocalDate
	// Date -> LocalDate
	@Test
	@Nullable
	public void testToNullableLocalDate() {
		LocalDate today = LocalDate.now();
		Date legacyDate = new Date();
		String dateString = legacyDate.toString();

		assertThat(today, is(ConversionUtils.toNullableLocalDate(dateString)));
		assertThat(today, is(ConversionUtils.toNullableLocalDate(legacyDate)));

		dateString = null;
		Assert.assertNull(ConversionUtils.toNullableLocalDate(dateString));

		legacyDate = null;
		Assert.assertNull(ConversionUtils.toNullableLocalDate(legacyDate));
	}

	// IsNewBorn
	// getInbox [endDate, startDate]
	// getOutbox [endDate, startDate]
	@Test
	public void testToLocalDate() {

	}

	// DocumentConverter
	@Test
	public void testToNullableLocalDateTime() {

	}

	// LocalDateTime == Date
	@Test
	public void testToLocalDateTime() {

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