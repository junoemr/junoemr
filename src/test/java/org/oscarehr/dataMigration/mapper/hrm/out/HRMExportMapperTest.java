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
package org.oscarehr.dataMigration.mapper.hrm.out;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.oscarehr.dataMigration.model.common.PartialDate;
import org.oscarehr.dataMigration.model.provider.ProviderModel;
import org.springframework.beans.factory.annotation.Autowired;
import xml.hrm.v4_3.DateFullOrPartial;
import xml.hrm.v4_3.PersonNameSimple;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class HRMExportMapperTest
{
	@Autowired
	@InjectMocks
	private HRMExportMapper hrmExportMapper;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testPersonNameSimple_Null()
	{
		assertNull(hrmExportMapper.toPersonNameSimple(null));
	}

	@Test
	public void testPersonNameSimple()
	{
		String expectedFirstName = "first";
		String expectedLastName = "last";

		ProviderModel provider = new ProviderModel();
		provider.setFirstName(expectedFirstName);
		provider.setLastName(expectedLastName);

		PersonNameSimple personNameSimple = hrmExportMapper.toPersonNameSimple(provider);

		assertEquals(expectedFirstName, personNameSimple.getFirstName());
		assertEquals(expectedLastName, personNameSimple.getLastName());
	}

	@Test
	public void TestToNullableDateFullOrPartial_Null()
	{
		assertNull(hrmExportMapper.toNullableDateFullOrPartial((LocalDate) null));
		assertNull(hrmExportMapper.toNullableDateFullOrPartial((PartialDate) null));
	}

	@Test
	public void TestToNullableDateFullOrPartial_LocalDateDefault()
	{
		LocalDate localDate = LocalDate.of(2021, 4, 6);
		DateFullOrPartial dateFullOrPartial = hrmExportMapper.toNullableDateFullOrPartial(localDate);
		assertEquals("2021-04-06", String.valueOf(dateFullOrPartial.getFullDate()));
	}

	@Test
	public void TestToNullableDateFullOrPartial_LocalDateTimeDefault()
	{
		LocalDateTime localDateTime = LocalDateTime.of(2021, 4, 6, 13, 14, 15);
		DateFullOrPartial dateFullOrPartial = hrmExportMapper.toNullableDateFullOrPartial(localDateTime);

		assertEquals("2021-04-06T13:14:15", String.valueOf(dateFullOrPartial.getDateTime()));
	}

}