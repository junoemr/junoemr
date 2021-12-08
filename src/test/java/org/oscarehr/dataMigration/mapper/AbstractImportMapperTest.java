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
package org.oscarehr.dataMigration.mapper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.oscarehr.dataMigration.logger.cds.CDSImportLogger;
import org.oscarehr.dataMigration.mapper.cds.in.CDSImportMapper;
import org.oscarehr.dataMigration.model.common.PartialDate;
import org.oscarehr.dataMigration.service.context.PatientImportContext;
import org.oscarehr.dataMigration.service.context.PatientImportContextService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

public class AbstractImportMapperTest
{
	@Autowired
	@InjectMocks
	private CDSImportMapper cdsImportMapper;

	@Mock
	protected PatientImportContextService patientImportContextService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testCoalescePartialDatesToDateTime_Null()
	{
		assertNull(cdsImportMapper.coalescePartialDatesToDateTime((PartialDate) null));
		assertNull(cdsImportMapper.coalescePartialDatesToDateTime(null, null));
		assertNull(cdsImportMapper.coalescePartialDatesToDateTime(null, null, null));
	}

	@Test
	public void testCoalescePartialDatesToDateTime_FirstChoice()
	{
		PartialDate expectedPartialDate = new PartialDate(2021, 2, 3);
		LocalDateTime expectedDateTime = LocalDateTime.of(2021, 2, 3, 0,0,0);

		assertEquals(expectedDateTime, cdsImportMapper.coalescePartialDatesToDateTime(expectedPartialDate));
	}

	@Test
	public void testCoalescePartialDatesToDateTime_SecondChoice()
	{
		PartialDate expectedPartialDate = new PartialDate(2021, 2, 3);
		LocalDateTime expectedDateTime = LocalDateTime.of(2021, 2, 3, 0,0,0);

		assertEquals(expectedDateTime, cdsImportMapper.coalescePartialDatesToDateTime(null, expectedPartialDate));
	}

	@Test
	public void testCoalescePartialDatesToDateTimeWithDefault()
	{
		LocalDate expectedDate = LocalDate.of(2021, 2, 3);

		PatientImportContext importContext = Mockito.mock(PatientImportContext.class);
		when(patientImportContextService.getContext()).thenReturn(importContext);
		when(importContext.getDefaultDate()).thenReturn(expectedDate);
		when(importContext.getImportLogger()).thenReturn(Mockito.mock(CDSImportLogger.class));

		assertEquals(expectedDate.atStartOfDay(), cdsImportMapper.coalescePartialDatesToDateTimeWithDefault("Test", null, null));
	}
}