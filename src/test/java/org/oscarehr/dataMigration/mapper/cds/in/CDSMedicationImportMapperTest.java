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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.oscarehr.dataMigration.logger.cds.CDSImportLogger;
import org.oscarehr.dataMigration.model.medication.FrequencyCode;
import org.oscarehr.dataMigration.model.provider.Provider;
import org.oscarehr.dataMigration.service.context.PatientImportContext;
import org.oscarehr.dataMigration.service.context.PatientImportContextService;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import oscar.oscarDemographic.pageUtil.Util;
import xml.cds.v5_0.DateFullOrPartial;
import xml.cds.v5_0.DateTimeFullOrPartial;
import xml.cds.v5_0.MedicationsAndTreatments;
import xml.cds.v5_0.ObjectFactory;
import xml.cds.v5_0.PersonNameSimple;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Util.class)
@SuppressStaticInitializationFor("oscar.oscarDemographic.pageUtil.Util")
public class CDSMedicationImportMapperTest
{
	@Autowired
	@InjectMocks
	private CDSMedicationImportMapper cdsMedicationImportMapper;

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

		PowerMockito.mockStatic(Util.class);

		try
		{
			PowerMockito.doReturn("1")
				.when(Util.class, "leadingNum", "1");
			PowerMockito.doReturn("2")
				.when(Util.class, "leadingNum", "2");
			PowerMockito.doReturn("")
				.when(Util.class, "leadingNum", "");
			PowerMockito.doReturn("1")
				.when(Util.class, "leadingNum", "1 dosage");
			PowerMockito.doReturn("")
				.when(Util.class, "leadingNum", "dosage");
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void testToIntOrNull_Null()
	{
		assertNull(cdsMedicationImportMapper.toIntOrNull(null));
	}

	@Test
	public void testToIntOrNull_Empty()
	{
		assertNull(cdsMedicationImportMapper.toIntOrNull(""));
	}

	@Test
	public void testToIntOrNull_Valid()
	{
		assertEquals(Integer.valueOf(12), cdsMedicationImportMapper.toIntOrNull("12"));
	}

	@Test
	public void testToIntOrNull_Invalid()
	{
		assertNull(cdsMedicationImportMapper.toIntOrNull("NO"));
	}

	@Test
	public void testToBooleanOrNull_Null()
	{
		assertNull(cdsMedicationImportMapper.toBooleanOrNull(null));
	}

	@Test
	public void testToBooleanOrNull_Empty()
	{
		assertNull(cdsMedicationImportMapper.toBooleanOrNull(""));
	}

	@Test
	public void testToBooleanOrNull_True()
	{
		assertTrue(cdsMedicationImportMapper.toBooleanOrNull("true"));
	}

	@Test
	public void testToBooleanOrNull_False()
	{
		assertFalse(cdsMedicationImportMapper.toBooleanOrNull("false"));
	}

	@Test
	public void testToBooleanOrNull_Invalid()
	{
		assertFalse(cdsMedicationImportMapper.toBooleanOrNull("asdasd"));
	}

	@Test
	public void testGetFormattedFrequency_Null()
	{
		ObjectFactory objectFactory = new ObjectFactory();
		MedicationsAndTreatments medicationsAndTreatments = objectFactory.createMedicationsAndTreatments();
		medicationsAndTreatments.setFrequency(null);

		assertNull(cdsMedicationImportMapper.getFormattedFrequency(medicationsAndTreatments));
	}

	@Test
	public void testGetFormattedFrequency_Formatted()
	{
		String frequencyCode = "1 times daily";
		ObjectFactory objectFactory = new ObjectFactory();
		MedicationsAndTreatments medicationsAndTreatments = objectFactory.createMedicationsAndTreatments();
		medicationsAndTreatments.setFrequency(frequencyCode);

		FrequencyCode result = cdsMedicationImportMapper.getFormattedFrequency(medicationsAndTreatments);
		assertEquals(frequencyCode, result.getCode());
	}

	@Test
	public void testGetFormattedFrequency_PrnFormatted()
	{
		String frequencyCode = "PRN 1 times daily";
		String expectedFrequencyCode = "1 times daily";

		ObjectFactory objectFactory = new ObjectFactory();
		MedicationsAndTreatments medicationsAndTreatments = objectFactory.createMedicationsAndTreatments();
		medicationsAndTreatments.setFrequency(frequencyCode);

		FrequencyCode result = cdsMedicationImportMapper.getFormattedFrequency(medicationsAndTreatments);
		assertEquals(expectedFrequencyCode, result.getCode());
	}

	@Test
	public void testGetPrescribingProvider_Null()
	{
		ObjectFactory objectFactory = new ObjectFactory();
		MedicationsAndTreatments medicationsAndTreatments = objectFactory.createMedicationsAndTreatments();
		medicationsAndTreatments.setPrescribedBy(null);

		assertNull(cdsMedicationImportMapper.getPrescribingProvider(medicationsAndTreatments));
	}

	@Test
	public void testGetPrescribingProvider_Filled()
	{
		String expectedFirstName = "first";
		String expectedLastName = "last";
		String expectedOhipNumber = "123456";

		ObjectFactory objectFactory = new ObjectFactory();
		MedicationsAndTreatments medicationsAndTreatments = objectFactory.createMedicationsAndTreatments();
		MedicationsAndTreatments.PrescribedBy prescribedBy = objectFactory.createMedicationsAndTreatmentsPrescribedBy();
		PersonNameSimple personNameSimple = objectFactory.createPersonNameSimple();
		personNameSimple.setFirstName(expectedFirstName);
		personNameSimple.setLastName(expectedLastName);

		prescribedBy.setName(personNameSimple);
		prescribedBy.setOHIPPhysicianId(expectedOhipNumber);

		medicationsAndTreatments.setPrescribedBy(prescribedBy);
		Provider provider = cdsMedicationImportMapper.getPrescribingProvider(medicationsAndTreatments);

		assertEquals(expectedFirstName, provider.getFirstName());
		assertEquals(expectedLastName, provider.getLastName());
		assertEquals(expectedOhipNumber, provider.getOhipNumber());
	}

	@Test
	public void testGetEndDate_NoDatesNoFrequency()
	{
		LocalDate expectedEndDate = LocalDate.now();

		ObjectFactory objectFactory = new ObjectFactory();
		MedicationsAndTreatments medicationsAndTreatments = objectFactory.createMedicationsAndTreatments();
		medicationsAndTreatments.setStartDate(null);
		medicationsAndTreatments.setPrescriptionWrittenDate(null);
		medicationsAndTreatments.setFrequency(null);

		assertEquals(expectedEndDate, cdsMedicationImportMapper.getEndDate(medicationsAndTreatments).toLocalDate());
	}

	@Test
	/**
	 * In the case of only a starting date, the end date will be the same as the start date
	 */
	public void testGetEndDate_StartDateNoFrequency() throws DatatypeConfigurationException
	{
		LocalDate expectedEndDate = LocalDate.of(2021, 1, 25);

		ObjectFactory objectFactory = new ObjectFactory();
		MedicationsAndTreatments medicationsAndTreatments = objectFactory.createMedicationsAndTreatments();

		DateFullOrPartial dateFullOrPartial = objectFactory.createDateFullOrPartial();
		dateFullOrPartial.setFullDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(expectedEndDate.toString()));

		medicationsAndTreatments.setStartDate(dateFullOrPartial);
		medicationsAndTreatments.setPrescriptionWrittenDate(null);
		medicationsAndTreatments.setFrequency(null);

		assertEquals(expectedEndDate, cdsMedicationImportMapper.getEndDate(medicationsAndTreatments).toLocalDate());
	}

	@Test
	/**
	 * In the case of only a written date, the end date will be the same as the start date
	 */
	public void testGetEndDate_WrittenDateNoFrequency() throws DatatypeConfigurationException
	{
		LocalDate expectedEndDate = LocalDate.of(2021, 1, 25);

		ObjectFactory objectFactory = new ObjectFactory();
		MedicationsAndTreatments medicationsAndTreatments = objectFactory.createMedicationsAndTreatments();

		DateTimeFullOrPartial dateTimeFullOrPartial = objectFactory.createDateTimeFullOrPartial();
		dateTimeFullOrPartial.setFullDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(expectedEndDate.toString()));

		medicationsAndTreatments.setStartDate(null);
		medicationsAndTreatments.setPrescriptionWrittenDate(dateTimeFullOrPartial);
		medicationsAndTreatments.setFrequency(null);

		assertEquals(expectedEndDate, cdsMedicationImportMapper.getEndDate(medicationsAndTreatments).toLocalDate());
	}

	@Test
	public void testGetEndDate_StartDateWithFrequency() throws DatatypeConfigurationException
	{
		LocalDate startDate = LocalDate.of(2021, 1, 1);
		String frequencyCode = "OD"; // once daily
		String amount = "50"; // 50 'pills' given
		String dosage = "2"; // 2 per use
		LocalDate expectedEndDate = LocalDate.of(2021, 1, 26); // start date + (50/2)


		ObjectFactory objectFactory = new ObjectFactory();
		MedicationsAndTreatments medicationsAndTreatments = objectFactory.createMedicationsAndTreatments();

		DateFullOrPartial dateFullOrPartial = objectFactory.createDateFullOrPartial();
		dateFullOrPartial.setFullDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(startDate.toString()));

		medicationsAndTreatments.setStartDate(dateFullOrPartial);
		medicationsAndTreatments.setFrequency(frequencyCode);
		medicationsAndTreatments.setDosage(dosage);
		medicationsAndTreatments.setQuantity(amount);

		assertEquals(expectedEndDate, cdsMedicationImportMapper.getEndDate(medicationsAndTreatments).toLocalDate());
	}

	@Test
	public void testgetDosageMinMax_singleNumber()
	{
		CDSMedicationImportMapper cdsMedicationImportMapper = new CDSMedicationImportMapper();
		String dosage = "1";
		String[] expected = {"1", "1"};
		assertArrayEquals(expected, cdsMedicationImportMapper.getDosageMinMax(dosage));
	}

	@Test
	public void testgetDosageMinMax_rangeFull()
	{
		CDSMedicationImportMapper cdsMedicationImportMapper = new CDSMedicationImportMapper();
		String dosage = "1-2";
		String[] expected = {"1", "2"};
		assertArrayEquals(expected, cdsMedicationImportMapper.getDosageMinMax(dosage));
	}

	@Test
	public void testgetDosageMinMax_rangeLowerOnly()
	{
		CDSMedicationImportMapper cdsMedicationImportMapper = new CDSMedicationImportMapper();
		String dosage = "1-";
		String[] expected = {"1", "1"};
		assertArrayEquals(expected, cdsMedicationImportMapper.getDosageMinMax(dosage));
	}

	@Test
	public void testgetDosageMinMax_rangeUpperOnly()
	{
		CDSMedicationImportMapper cdsMedicationImportMapper = new CDSMedicationImportMapper();
		String dosage = "-1";
		String[] expected = {"1", "1"};
		assertArrayEquals(expected, cdsMedicationImportMapper.getDosageMinMax(dosage));
	}

	@Test
	public void testgetDosageMinMax_emptyString()
	{
		CDSMedicationImportMapper cdsMedicationImportMapper = new CDSMedicationImportMapper();
		String dosage = "";
		String[] expected = {"", ""};
		assertArrayEquals(expected, cdsMedicationImportMapper.getDosageMinMax(dosage));
	}

	@Test
	public void testgetDosageMinMax_dashOnly()
	{
		CDSMedicationImportMapper cdsMedicationImportMapper = new CDSMedicationImportMapper();
		String dosage = "-";
		String[] expected = {};
		assertArrayEquals(expected, cdsMedicationImportMapper.getDosageMinMax(dosage));
	}

	@Test
	public void testgetDosageMinMax_null()
	{
		CDSMedicationImportMapper cdsMedicationImportMapper = new CDSMedicationImportMapper();
		assertNull(cdsMedicationImportMapper.getDosageMinMax(null));
	}
	@Test
	public void testgetDosageMinMax_nonNumericLowerOnly()
	{
		CDSMedicationImportMapper cdsMedicationImportMapper = new CDSMedicationImportMapper();
		String dosage = "1 dosage-";
		String[] expected = {"1", "1"};
		assertArrayEquals(expected, cdsMedicationImportMapper.getDosageMinMax(dosage));
	}

	@Test
	public void testgetDosageMinMax_nonNumericUpperOnly()
	{
		CDSMedicationImportMapper cdsMedicationImportMapper = new CDSMedicationImportMapper();
		String dosage = "-1 dosage";
		String[] expected = {"1","1"};
		assertArrayEquals(expected, cdsMedicationImportMapper.getDosageMinMax(dosage));
	}

	@Test
	public void testgetDosageMinMax_nonNumeric()
	{
		CDSMedicationImportMapper cdsMedicationImportMapper = new CDSMedicationImportMapper();
		String dosage = "dosage";
		String[] expected = {"", ""};
		assertArrayEquals(expected, cdsMedicationImportMapper.getDosageMinMax(dosage));
	}
}
