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
import static oscar.oscarLab.ca.all.parsers.OLIS.OLISResultSortKey.ANCILLARY_STATUS;

public class OLISResultSortKeyTest
{
	@Test
	public void testCompare_null()
	{
		OLISResultSortKey sortKeyN = new OLISResultSortKey(null, null, null, null, null, null);
		int result = sortKeyN.compareTo(null);
		assertTrue("object should be before null", firstBefore(result));
	}

	@Test
	public void testCompare_compareSelf()
	{
		OLISResultSortKey sortKeyA = new OLISResultSortKey(null, "AAA", null, null, null, null);
		int result = sortKeyA.compareTo(sortKeyA);
		assertEquals(0, result);
	}

	@Test
	public void testCompare_resultStatusOrdering()
	{
		OLISResultSortKey sortKeyA1 = new OLISResultSortKey(ANCILLARY_STATUS, null, null, null, null, null);
		OLISResultSortKey sortKeyA2 = new OLISResultSortKey(ANCILLARY_STATUS, null, null, null, null, null);
		OLISResultSortKey sortKeyB = new OLISResultSortKey("B", null, null, null, null, null);
		OLISResultSortKey sortKeyN = new OLISResultSortKey(null, null, null, null, null, null);

		int result = sortKeyA1.compareTo(sortKeyA2);
		assertEquals("A1 should be equal to A2", 0, result);

		result = sortKeyA1.compareTo(sortKeyB);
		assertTrue("A should be before B", firstBefore(result));

		result = sortKeyB.compareTo(sortKeyA1);
		assertTrue("A should be before B", firstAfter(result));

		result = sortKeyA1.compareTo(sortKeyN);
		assertTrue("A should be before null", firstBefore(result));

		result = sortKeyB.compareTo(sortKeyN);
		assertEquals("Non Ancillary status comparisons are undefined, and therefore equal", 0, result);
	}

	@Test
	public void testCompare_resultKeyOrdering()
	{
		OLISResultSortKey sortKeyA1 = new OLISResultSortKey(null, "AAA", null, null, null, null);
		OLISResultSortKey sortKeyA2 = new OLISResultSortKey(null, "AAA", null, null, null, null);
		OLISResultSortKey sortKeyB = new OLISResultSortKey(null, "BBB", null, null, null, null);
		OLISResultSortKey sortKeyN = new OLISResultSortKey(null, null, null, null, null, null);

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
	public void testCompare_nomenclatureOrdering()
	{
		OLISResultSortKey sortKeyA1 = new OLISResultSortKey(null, null, "200000.064.000", null, null, null);
		OLISResultSortKey sortKeyA2 = new OLISResultSortKey(null, null, "200000.064.000", null, null, null);
		OLISResultSortKey sortKeyB = new OLISResultSortKey(null, null, "200000.066.100", null, null, null);
		OLISResultSortKey sortKeyN = new OLISResultSortKey(null, null, null, null, null, null);

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
	public void testCompare_alternateNameOrdering()
	{
		OLISResultSortKey sortKeyA1 = new OLISResultSortKey(null, null, null, "AAA", null, null);
		OLISResultSortKey sortKeyA2 = new OLISResultSortKey(null, null, null, "AAA", null, null);
		OLISResultSortKey sortKeyB = new OLISResultSortKey(null, null, null, "BBB", null, null);
		OLISResultSortKey sortKeyN = new OLISResultSortKey(null, null, null, null, null, null);

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
	public void testCompare_subIdOrdering()
	{
		OLISResultSortKey sortKeyA1 = new OLISResultSortKey(null, null, null, null, "AAA", null);
		OLISResultSortKey sortKeyA2 = new OLISResultSortKey(null, null, null, null, "AAA", null);
		OLISResultSortKey sortKeyB = new OLISResultSortKey(null, null, null, null, "BBB", null);
		OLISResultSortKey sortKeyN = new OLISResultSortKey(null, null, null, null, null, null);

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
	public void testCompare_releaseDateOrdering()
	{
		OLISResultSortKey sortKeyA1 = new OLISResultSortKey(null, null, null, null, null,
				ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()));
		OLISResultSortKey sortKeyA2 = new OLISResultSortKey(null, null, null, null, null,
				ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()));
		OLISResultSortKey sortKeyB = new OLISResultSortKey(null, null, null, null, null,
				ZonedDateTime.of(2000, 1, 2, 0, 0, 0, 0, ZoneId.systemDefault()));
		OLISResultSortKey sortKeyN = new OLISResultSortKey(null, null, null, null, null, null);

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
	 * test to make sure the resultStatus check takes precedence over other orderings
	 */
	@Test
	public void testCompare_statusPriority()
	{
		OLISResultSortKey sortKeyA = new OLISResultSortKey("A", "AAA", "AAA", "AAA", "1", null);
		OLISResultSortKey sortKeyB = new OLISResultSortKey(ANCILLARY_STATUS, "BBB", "BBB", "BBB", "2", null);

		int result = sortKeyA.compareTo(sortKeyB);
		assertTrue("B should be before A", firstAfter(result));
	}

	/**
	 * test to make sure the resultKey check takes precedence over other orderings
	 */
	@Test
	public void testCompare_resultKeyPriority()
	{
		OLISResultSortKey sortKeyA = new OLISResultSortKey(null, "ZZZ", "AAA", "AAA", "1", null);
		OLISResultSortKey sortKeyB = new OLISResultSortKey(null, "BBB", "BBB", "BBB", "2", null);

		int result = sortKeyA.compareTo(sortKeyB);
		assertTrue("B should be before A", firstAfter(result));
	}

	/**
	 * test to make sure the nomenclature check takes precedence over other orderings
	 */
	@Test
	public void testCompare_nomenclaturePriority()
	{
		OLISResultSortKey sortKeyA = new OLISResultSortKey(null, null, "ZZZ", "AAA", "1", null);
		OLISResultSortKey sortKeyB = new OLISResultSortKey(null, null, "BBB", "BBB", "2", null);

		int result = sortKeyA.compareTo(sortKeyB);
		assertTrue("B should be before A", firstAfter(result));
	}

	/**
	 * test to make sure the altName check takes precedence over other orderings
	 */
	@Test
	public void testCompare_altNamePriority()
	{
		OLISResultSortKey sortKeyA = new OLISResultSortKey(null, null, null, "ZZZ", "1", null);
		OLISResultSortKey sortKeyB = new OLISResultSortKey(null, null, null, "BBB", "2", null);

		int result = sortKeyA.compareTo(sortKeyB);
		assertTrue("B should be before A", firstAfter(result));
	}

	/**
	 * test to make sure the subId check takes precedence over other orderings
	 */
	@Test
	public void testCompare_subIdPriority()
	{
		OLISResultSortKey sortKeyA = new OLISResultSortKey(null, null, null, null, "4",
				ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()));
		OLISResultSortKey sortKeyB = new OLISResultSortKey(null, null, null, null, "2",
				ZonedDateTime.of(2000, 2, 1, 0, 0, 0, 0, ZoneId.systemDefault()));

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
