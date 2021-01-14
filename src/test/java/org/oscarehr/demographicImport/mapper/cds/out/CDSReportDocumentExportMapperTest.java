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
package org.oscarehr.demographicImport.mapper.cds.out;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.oscarehr.common.xml.cds.v5_0.model.Reports;
import org.oscarehr.demographicImport.model.common.PartialDateTime;
import org.oscarehr.demographicImport.model.document.Document;
import org.oscarehr.demographicImport.model.provider.Reviewer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CDSReportDocumentExportMapperTest
{
	@Autowired
	@InjectMocks
	private CDSReportDocumentExportMapper cdsReportDocumentExportMapper;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetReportReviewedList_Null()
	{
		Document exportStructure = new Document();
		exportStructure.setReviewer(null);

		assertTrue(cdsReportDocumentExportMapper.getReportReviewedList(exportStructure).isEmpty());
	}

	@Test
	public void testGetReportReviewedList_Reviewer()
	{
		String expectedFirstName = "first";
		String expectedLastName = "last";
		PartialDateTime expectedReviewDateTime = new PartialDateTime(2021, 4, 21);

		Document exportStructure = new Document();
		Reviewer reviewer = new Reviewer();
		reviewer.setFirstName(expectedFirstName);
		reviewer.setLastName(expectedLastName);
		reviewer.setReviewDateTime(expectedReviewDateTime);
		exportStructure.setReviewer(reviewer);

		List<Reports.ReportReviewed> results = cdsReportDocumentExportMapper.getReportReviewedList(exportStructure);
		assertEquals(1, results.size());
		assertEquals(expectedFirstName, results.get(0).getName().getFirstName());
		assertEquals(expectedLastName, results.get(0).getName().getLastName());
		assertEquals("2021-04-21", results.get(0).getDateTimeReportReviewed().getFullDate().toString());
	}

}
