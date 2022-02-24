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
package oscar.oscarLab.ca.all.parsers.OLIS;

import org.junit.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OLISRequestSortKeyTest
{
	@Test
	public void testCompare_null()
	{
		OLISRequestSortKey sortKeyN = new OLISRequestSortKey(null, null, null, null, null, null);
		int result = sortKeyN.compareTo(null);
		assertTrue("object should be before null", firstBefore(result));
	}

	@Test
	public void testCompare_compareSelf()
	{
		OLISRequestSortKey sortKeyA = new OLISRequestSortKey(null, "AAA", null, null, null, null);
		int result = sortKeyA.compareTo(sortKeyA);
		assertEquals(0, result);
	}

	/**
	 * dates should be reverse-chronological order (newest first)
	 */
	@Test
	public void testCompare_collectionDateOrdering()
	{
		OLISRequestSortKey sortKeyA1 = new OLISRequestSortKey(
				ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()),
				null, null, null, null, null);
		OLISRequestSortKey sortKeyA2 = new OLISRequestSortKey(
				ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()),
				null, null, null, null, null);
		OLISRequestSortKey sortKeyB = new OLISRequestSortKey(
				ZonedDateTime.of(2000, 2, 1, 0, 0, 0, 0, ZoneId.systemDefault()),
				null, null, null, null, null);
		OLISRequestSortKey sortKeyN = new OLISRequestSortKey(null, null, null, null, null, null);


		int result = sortKeyA1.compareTo(sortKeyA2);
		assertEquals("A1 should be equal to A2", 0, result);

		result = sortKeyA1.compareTo(sortKeyB);
		assertTrue("B should be before A", firstAfter(result));

		result = sortKeyB.compareTo(sortKeyA1);
		assertTrue("B should be before A", firstBefore(result));

		result = sortKeyA1.compareTo(sortKeyN);
		assertTrue("A should be before null", firstBefore(result));
	}

	@Test
	public void testCompare_placerGroupNoOrdering()
	{
		OLISRequestSortKey sortKeyA1 = new OLISRequestSortKey(null, "AAA", null, null, null, null);
		OLISRequestSortKey sortKeyA2 = new OLISRequestSortKey(null, "AAA", null, null, null, null);
		OLISRequestSortKey sortKeyB = new OLISRequestSortKey(null, "BBB", null, null, null, null);
		OLISRequestSortKey sortKeyN = new OLISRequestSortKey(null, null, null, null, null, null);

		int result = sortKeyA1.compareTo(sortKeyA2);
		assertEquals("A1 should be equal to A2", 0, result);

		result = sortKeyA1.compareTo(sortKeyB);
		assertTrue("A should be before B", firstBefore(result));

		result = sortKeyB.compareTo(sortKeyA1);
		assertTrue("A should be before B", firstAfter(result));

		result = sortKeyA1.compareTo(sortKeyN);
		assertTrue("A should be before null", firstBefore(result));
	}

	@Test
	public void testCompare_RequestKeyOrdering()
	{
		OLISRequestSortKey sortKeyA1 = new OLISRequestSortKey(null, null, "AAA", null, null, null);
		OLISRequestSortKey sortKeyA2 = new OLISRequestSortKey(null, null, "AAA", null, null, null);
		OLISRequestSortKey sortKeyB = new OLISRequestSortKey(null, null, "BBB", null, null, null);
		OLISRequestSortKey sortKeyN = new OLISRequestSortKey(null, null, null, null, null, null);


		int result = sortKeyA1.compareTo(sortKeyA2);
		assertEquals("A1 should be equal to A2", 0, result);

		result = sortKeyA1.compareTo(sortKeyB);
		assertTrue("A should be before B", firstBefore(result));

		result = sortKeyB.compareTo(sortKeyA1);
		assertTrue("A should be before B", firstAfter(result));

		result = sortKeyA1.compareTo(sortKeyN);
		assertTrue("A should be before null", firstBefore(result));
	}

	@Test
	public void testCompare_nomenclatureKeyOrdering()
	{
		OLISRequestSortKey sortKeyA1 = new OLISRequestSortKey(null, null, null, "200000.064.000", null, null);
		OLISRequestSortKey sortKeyA2 = new OLISRequestSortKey(null, null, null, "200000.064.000", null, null);
		OLISRequestSortKey sortKeyB = new OLISRequestSortKey(null, null, null, "200000.066.000", null, null);
		OLISRequestSortKey sortKeyN = new OLISRequestSortKey(null, null, null, null, null, null);


		int result = sortKeyA1.compareTo(sortKeyA2);
		assertEquals("A1 should be equal to A2", 0, result);

		result = sortKeyA1.compareTo(sortKeyB);
		assertTrue("A should be before B", firstBefore(result));

		result = sortKeyB.compareTo(sortKeyA1);
		assertTrue("A should be before B", firstAfter(result));

		result = sortKeyA1.compareTo(sortKeyN);
		assertTrue("A should be before null", firstBefore(result));
	}

	@Test
	public void testCompare_AltNameOrdering()
	{
		OLISRequestSortKey sortKeyA1 = new OLISRequestSortKey(null, null, null, null, "AAA", null);
		OLISRequestSortKey sortKeyA2 = new OLISRequestSortKey(null, null, null, null, "AAA", null);
		OLISRequestSortKey sortKeyB = new OLISRequestSortKey(null, null, null, null, "BBB", null);
		OLISRequestSortKey sortKeyN = new OLISRequestSortKey(null, null, null, null, null, null);


		int result = sortKeyA1.compareTo(sortKeyA2);
		assertEquals("A1 should be equal to A2", 0, result);

		result = sortKeyA1.compareTo(sortKeyB);
		assertTrue("A should be before B", firstBefore(result));

		result = sortKeyB.compareTo(sortKeyA1);
		assertTrue("A should be before B", firstAfter(result));

		result = sortKeyA1.compareTo(sortKeyN);
		assertTrue("A should be before null", firstBefore(result));
	}

	@Test
	public void testCompare_seqIdOrdering()
	{
		OLISRequestSortKey sortKeyA1 = new OLISRequestSortKey(null, null, null, null, null, 1);
		OLISRequestSortKey sortKeyA2 = new OLISRequestSortKey(null, null, null, null, null, 1);
		OLISRequestSortKey sortKeyB = new OLISRequestSortKey(null, null, null, null, null, 2);
		OLISRequestSortKey sortKeyN = new OLISRequestSortKey(null, null, null, null, null, null);


		int result = sortKeyA1.compareTo(sortKeyA2);
		assertEquals("A1 should be equal to A2", 0, result);

		result = sortKeyA1.compareTo(sortKeyB);
		assertTrue("A should be before B", firstBefore(result));

		result = sortKeyB.compareTo(sortKeyA1);
		assertTrue("A should be before B", firstAfter(result));

		result = sortKeyA1.compareTo(sortKeyN);
		assertTrue("A should be before null", firstBefore(result));
	}

	/**
	 * test to make sure the date check takes precedence over other orderings
	 */
	@Test
	public void testCompare_collectionDatePriority()
	{
		OLISRequestSortKey sortKeyA = new OLISRequestSortKey(
				ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()),
				"AAA", "2", "3", "4", 5);
		OLISRequestSortKey sortKeyB = new OLISRequestSortKey(
				ZonedDateTime.of(2000, 2, 1, 0, 0, 0, 0, ZoneId.systemDefault()),
				"BBB", "3", "4", "5", 6);

		int result = sortKeyA.compareTo(sortKeyB);
		assertTrue("B should be before A", firstAfter(result));
	}

	/**
	 * test to make sure the placerGroupNo check takes precedence over other orderings
	 */
	@Test
	public void testCompare_placerGroupNoPriority()
	{
		OLISRequestSortKey sortKeyA = new OLISRequestSortKey(
				ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()),
				"2", "2", "3", "4", 5);
		OLISRequestSortKey sortKeyB = new OLISRequestSortKey(
				ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()),
				"1", "3", "4", "5", 6);

		int result = sortKeyA.compareTo(sortKeyB);
		assertTrue("B should be before A", firstAfter(result));
	}

	/**
	 * test to make sure the testKey check takes precedence over other orderings
	 */
	@Test
	public void testCompare_testKeyPriority()
	{
		OLISRequestSortKey sortKeyA = new OLISRequestSortKey(
				ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()),
				"0", "ZZZ", "3", "4", 5);
		OLISRequestSortKey sortKeyB = new OLISRequestSortKey(
				ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()),
				"0", "BBB", "4", "5", 6);

		int result = sortKeyA.compareTo(sortKeyB);
		assertTrue("B should be before A", firstAfter(result));
	}

	/**
	 * test to make sure the nomenclature check takes precedence over other orderings
	 */
	@Test
	public void testCompare_nomenclaturePriority()
	{
		OLISRequestSortKey sortKeyA = new OLISRequestSortKey(null, "0", "0", "ZZZ", "4", 5);
		OLISRequestSortKey sortKeyB = new OLISRequestSortKey(null, "0", "0", "BBB", "5", 6);

		int result = sortKeyA.compareTo(sortKeyB);
		assertTrue("B should be before A", firstAfter(result));
	}

	/**
	 * test to make sure the altName check takes precedence over other orderings
	 */
	@Test
	public void testCompare_altNamePriority()
	{
		OLISRequestSortKey sortKeyA = new OLISRequestSortKey(null, "0", "0", null, "ZZZ", 5);
		OLISRequestSortKey sortKeyB = new OLISRequestSortKey(null, "0", "0", null, "BBB", 6);

		int result = sortKeyA.compareTo(sortKeyB);
		assertTrue("B should be before A", firstAfter(result));
	}


	// simple method to make logic easier to read
	private boolean firstBefore(int result)
	{
		return result < 0;
	}

	// simple method to make logic easier to read
	private boolean firstAfter(int result)
	{
		return result > 0;
	}
}
