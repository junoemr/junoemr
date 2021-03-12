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
package org.oscarehr.dataMigration.mapper.cds.in;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.oscarehr.dataMigration.logger.cds.CDSImportLogger;
import org.oscarehr.dataMigration.service.context.PatientImportContext;
import org.oscarehr.dataMigration.service.context.PatientImportContextService;
import xml.cds.v5_0.ObjectFactory;
import xml.cds.v5_0.StandardCoding;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class CDSNoteImportMapperTest
{
	@Autowired
	@InjectMocks
	private CDSProblemImportMapper cdsProblemImportMapper;

	@Mock
	private PatientImportContextService patientImportContextService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		CDSImportLogger cdsImportLoggerMock = Mockito.mock(CDSImportLogger.class);
		PatientImportContext patientImportContextMock = Mockito.mock(PatientImportContext.class);
		when(patientImportContextMock.getImportLogger()).thenReturn(cdsImportLoggerMock);
		when(patientImportContextService.getContext()).thenReturn(patientImportContextMock);
	}

	@Test
	public void testGetDiagnosisNoteText_Null()
	{
		assertEquals(AbstractCDSNoteImportMapper.DEFAULT_NOTE_TEXT, cdsProblemImportMapper.getDiagnosisNoteText(null, null));
	}

	@Test
	public void testGetDiagnosisNoteText_Description_NullCode()
	{
		String expectedDescription = "This is a note description";
		assertEquals(expectedDescription, cdsProblemImportMapper.getDiagnosisNoteText(expectedDescription, null));
	}

	@Test
	public void testGetDiagnosisNoteText_Description_UniqueCode()
	{
		String expectedDescription = "This is a note description";
		String expectedCode = "E11";
		String expectedCodeDescription = "Type 2 diabetes mellitus";
		String expectedCodeSystem = "ICD-10-CA";

		ObjectFactory objectFactory = new ObjectFactory();
		StandardCoding standardCoding = objectFactory.createStandardCoding();
		standardCoding.setStandardCode(expectedCode);
		standardCoding.setStandardCodeDescription(expectedCodeDescription);
		standardCoding.setStandardCodingSystem(expectedCodeSystem);

		String resultText = cdsProblemImportMapper.getDiagnosisNoteText(expectedDescription, standardCoding);
		assertTrue(resultText.contains(expectedCode));
		assertTrue(resultText.contains(expectedCodeSystem));
		assertTrue(resultText.contains(expectedCodeDescription));
		assertTrue(resultText.contains(expectedDescription));
	}

	@Test
	public void testGetDiagnosisNoteText_Description_CodeMatchesDescription()
	{
		String expectedCode = "E11";
		String expectedCodeDescription = "Type 2 diabetes mellitus";
		String expectedCodeSystem = "ICD-10-CA";

		ObjectFactory objectFactory = new ObjectFactory();
		StandardCoding standardCoding = objectFactory.createStandardCoding();
		standardCoding.setStandardCode(expectedCode);
		standardCoding.setStandardCodeDescription(expectedCodeDescription);
		standardCoding.setStandardCodingSystem(expectedCodeSystem);

		String resultText = cdsProblemImportMapper.getDiagnosisNoteText(expectedCodeDescription, standardCoding);
		assertTrue(resultText.contains(expectedCode));
		assertTrue(resultText.contains(expectedCodeSystem));
		assertTrue(resultText.contains(expectedCodeDescription));
		assertTrue(containsExactlyOnce(resultText, expectedCodeDescription));
	}

	private boolean containsExactlyOnce(String string, String substring)
	{
		Pattern pattern = Pattern.compile(substring);
		Matcher matcher = pattern.matcher(string);
		return matcher.find() && !matcher.find();
	}
}
