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

package org.oscarehr.casemgmt.dto;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

public class EncounterSectionNoteTest
{
	private static Logger logger = Logger.getLogger(EncounterSectionNoteTest.class);

	EncounterSectionNote sectionNote1;
	EncounterSectionNote sectionNote2;
	EncounterSectionNote sectionNote3;

	@Before
	public void setup()
	{
		sectionNote1 = new EncounterSectionNote();
		sectionNote1.setUpdateDate(LocalDateTime.parse("2019-01-01T00:00:00"));
		sectionNote1.setText("Test Note 1");

		sectionNote2 = new EncounterSectionNote();
		sectionNote2.setUpdateDate(LocalDateTime.parse("2019-01-02T00:00:00"));
		sectionNote2.setText("Test Note");

		sectionNote3 = new EncounterSectionNote();
		sectionNote3.setUpdateDate(LocalDateTime.parse("2019-01-02T00:00:00"));
		sectionNote3.setText("Test Note");
	}

	@Test
	public void testCompareAscGood()
	{
		int resultAscGood = EncounterSectionNote.compare(sectionNote1, sectionNote2, true, false, false, false);
		assertEquals(resultAscGood, -1);
	}

	@Test
	public void testCompareAscBad()
	{
		int resultAscBad = EncounterSectionNote.compare(sectionNote2, sectionNote1, true, false, false, false);
		assertEquals(resultAscBad, 1);
	}

	@Test
	public void testCompareDescGood()
	{
		int resultDescGood = EncounterSectionNote.compare(sectionNote2, sectionNote1, false, false, false, false);
		assertEquals(resultDescGood, -1);
	}

	@Test
	public void testCompareDescBad()
	{
		int resultDescBad = EncounterSectionNote.compare(sectionNote1, sectionNote2, false, false, false, false);
		assertEquals(resultDescBad, 1);
	}

	@Test
	public void testCompareSame()
	{
		int resultSame = EncounterSectionNote.compare(sectionNote2, sectionNote3, true, false, false, false);
		assertEquals(resultSame, 0);

		resultSame = EncounterSectionNote.compare(sectionNote2, sectionNote3, false, false, false, false);
		assertEquals(resultSame, 0);
	}
}
