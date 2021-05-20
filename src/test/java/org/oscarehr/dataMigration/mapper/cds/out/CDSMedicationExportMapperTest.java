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
package org.oscarehr.dataMigration.mapper.cds.out;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CDSMedicationExportMapperTest
{
	@Autowired
	@InjectMocks
	private CDSMedicationExportMapper cdsMedicationExportMapper;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testToStringOrNull_Null()
	{
		assertNull(cdsMedicationExportMapper.toStringOrNull((Integer) null));
		assertNull(cdsMedicationExportMapper.toStringOrNull((Boolean) null));
	}

	@Test
	public void testToStringOrNull_String()
	{
		assertEquals("10", cdsMedicationExportMapper.toStringOrNull(10));
	}

	@Test
	public void testToStringOrNull_Boolean()
	{
		assertEquals("true", cdsMedicationExportMapper.toStringOrNull(true));
	}

}
