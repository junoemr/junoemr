
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
package org.oscarehr.demographicArchive.converter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.oscarehr.common.converter.AbstractConverterTest;
import org.oscarehr.demographic.entity.Demographic;
import org.oscarehr.demographic.entity.DemographicCust;
import org.oscarehr.demographicArchive.entity.DemographicCustArchive;
import org.springframework.beans.factory.annotation.Autowired;

public class DemographicCustToArchiveConverterTest extends AbstractConverterTest<DemographicCust, DemographicCustArchive>
{
	@Autowired
	@InjectMocks
	private DemographicCustToArchiveConverter demographicCustToArchiveConverter;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * ensure all fields are copied by the converter
	 */
	@Test
	public void test_convert_allFieldsCopied()
	{
		Demographic demographic = new Demographic();
		demographic.setDemographicId(1);

		DemographicCust source = new DemographicCust();
		source.setId(1);
		source.setAlert("alert!");
		source.setNotes("notes!");
		source.setMidwife("1");
		source.setNurse("2");
		source.setResident("3");
		source.setDemographic(demographic);

		DemographicCustArchive destination = demographicCustToArchiveConverter.convert(source);
		testPropertiesNotNull(destination,"demographicArchive");
	}
}