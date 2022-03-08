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
import xml.cds.v5_0.DateFullOrPartial;
import xml.cds.v5_0.ObjectFactory;
import xml.cds.v5_0.PersonNameSimple;
import xml.cds.v5_0.ReportClass;
import xml.cds.v5_0.Reports;
import org.oscarehr.dataMigration.model.provider.ProviderModel;
import org.oscarehr.dataMigration.model.provider.Reviewer;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.oscarehr.dataMigration.mapper.cds.CDSConstants.DEFAULT_DOCUMENT_DESCRIPTION;

//TODO add this back in when testing framework works for PowerMockito Initialization
//@RunWith(PowerMockRunner.class)
//@PrepareForTest(FileFactory.class)
public class CDSReportDocumentImportMapperTest
{
	@Autowired
	@InjectMocks
	private CDSReportDocumentImportMapper cdsReportDocumentImportMapper;

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
	public void testAuthorPhysician_Null()
	{
		assertNull(cdsReportDocumentImportMapper.getAuthorPhysician(null));
	}

	@Test
	public void testAuthorPhysician_PersonNameSimple()
	{
		String expectedFirstName = "first";
		String expectedLastName = "last";

		ObjectFactory objectFactory = new ObjectFactory();
		Reports.SourceAuthorPhysician authorPhysician = objectFactory.createReportsSourceAuthorPhysician();
		PersonNameSimple personNameSimple = objectFactory.createPersonNameSimple();
		personNameSimple.setFirstName(expectedFirstName);
		personNameSimple.setLastName(expectedLastName);
		authorPhysician.setAuthorName(personNameSimple);

		ProviderModel convertedProvider = cdsReportDocumentImportMapper.getAuthorPhysician(authorPhysician);

		assertEquals(expectedFirstName, convertedProvider.getFirstName());
		assertEquals(expectedLastName, convertedProvider.getLastName());
	}

	@Test
	public void testAuthorPhysician_FreeText()
	{
		String expectedFirstName = "first";
		String expectedLastName = "last";

		ObjectFactory objectFactory = new ObjectFactory();
		Reports.SourceAuthorPhysician authorPhysician = objectFactory.createReportsSourceAuthorPhysician();
		authorPhysician.setAuthorFreeText(expectedLastName + "," + expectedFirstName);

		ProviderModel convertedProvider = cdsReportDocumentImportMapper.getAuthorPhysician(authorPhysician);

		assertEquals(expectedFirstName, convertedProvider.getFirstName());
		assertEquals(expectedLastName, convertedProvider.getLastName());
	}

	@Test
	public void testReportClass_Null()
	{
		assertNull(cdsReportDocumentImportMapper.getReportClass(null));
	}

	@Test
	public void testReportClass_Enum()
	{
		assertEquals("Cardio Respiratory Report", cdsReportDocumentImportMapper.getReportClass(ReportClass.CARDIO_RESPIRATORY_REPORT));
	}

	@Test
	public void testReviewer_Null()
	{
		assertNull(cdsReportDocumentImportMapper.getFirstReviewer(null));
	}

	@Test
	public void testReviewer_Empty()
	{
		assertNull(cdsReportDocumentImportMapper.getFirstReviewer(new ArrayList<>()));
	}

	@Test
	public void testDocumentDescription_Null()
	{
		ObjectFactory objectFactory = new ObjectFactory();
		Reports reports = objectFactory.createReports();
		reports.setClazz(null);

		assertEquals(DEFAULT_DOCUMENT_DESCRIPTION, cdsReportDocumentImportMapper.getDocumentDescription(reports));
	}

	@Test
	public void testDocumentDescription_String()
	{
		ReportClass reportClass = ReportClass.OTHER_LETTER;

		ObjectFactory objectFactory = new ObjectFactory();
		Reports reports = objectFactory.createReports();
		reports.setClazz(reportClass);

		assertEquals(reportClass.value(), cdsReportDocumentImportMapper.getDocumentDescription(reports));
	}

	@Test
	public void testReviewer_PersonNameSimple() throws DatatypeConfigurationException
	{
		String expectedFirstName = "first";
		String expectedLastName = "last";
		String expectedOhipNo = "123456";

		ObjectFactory objectFactory = new ObjectFactory();
		List<Reports.ReportReviewed> reviewers = objectFactory.createReports().getReportReviewed();
		Reports.ReportReviewed reportReviewed = objectFactory.createReportsReportReviewed();
		PersonNameSimple personNameSimple = objectFactory.createPersonNameSimple();

		XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar("2021-04-21");
		DateFullOrPartial dateFullOrPartial = objectFactory.createDateFullOrPartial();
		dateFullOrPartial.setFullDate(calendar);

		personNameSimple.setFirstName(expectedFirstName);
		personNameSimple.setLastName(expectedLastName);
		reportReviewed.setName(personNameSimple);
		reportReviewed.setReviewingOHIPPhysicianId(expectedOhipNo);
		reportReviewed.setDateTimeReportReviewed(dateFullOrPartial);
		reviewers.add(reportReviewed);

		Reviewer convertedReviewer = cdsReportDocumentImportMapper.getFirstReviewer(reviewers);

		assertEquals(expectedFirstName, convertedReviewer.getFirstName());
		assertEquals(expectedLastName, convertedReviewer.getLastName());
		assertEquals(expectedOhipNo, convertedReviewer.getOhipNumber());
		assertEquals("2021-04-21T00:00:00", convertedReviewer.getReviewDateTime().toISOString());
	}

	/* TODO add this back in when testing framework works for PowerMockito Initialization
	@Test
	public void testGetDocumentFile_BinaryFileEncoded() throws IOException, InterruptedException
	{
		GenericFile mockTempFile = new GenericFile(null);

		PowerMockito.mockStatic(FileFactory.class);
		Mockito.when(FileFactory.createTempFile(ArgumentMatchers.any(InputStream.class), ArgumentMatchers.anyString())).thenReturn(mockTempFile);

		byte[] mediaBytes = "Test File Content".getBytes();

		ObjectFactory objectFactory = new ObjectFactory();
		Reports reports = objectFactory.createReports();
		ReportContent content = objectFactory.createReportContent();

		reports.setFormat(ReportFormat.BINARY);
		reports.setFileExtensionAndVersion("pdf");
		content.setMedia(mediaBytes);
		reports.setContent(content);

		GenericFile file = cdsReportDocumentImportMapper.getDocumentFile(reports);
		assertEquals(mockTempFile, file);
	}
	*/
}