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
import org.mockito.stubbing.Answer;
import org.oscarehr.dataMigration.logger.cds.CDSImportLogger;
import org.oscarehr.dataMigration.service.context.PatientImportContext;
import org.oscarehr.dataMigration.service.context.PatientImportContextService;
import xml.cds.v5_0.ImmunizationType;
import xml.cds.v5_0.Immunizations;
import xml.cds.v5_0.ObjectFactory;
import org.oscarehr.prevention.service.PreventionManager;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.oscarehr.dataMigration.mapper.cds.in.CDSImmunizationImportMapper.DEFAULT_PREVENTION_TYPE;

public class CDSImmunizationImportMapperTest
{
	@Autowired
	@InjectMocks
	private CDSImmunizationImportMapper cdsImmunizationImportMapper;

	@Mock
	private PatientImportContextService patientImportContextService;

	@Mock
	private PreventionManager preventionManager;

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
	public void testGetPreventionCode_Null()
	{
		assertEquals(DEFAULT_PREVENTION_TYPE, cdsImmunizationImportMapper.getPreventionCode(getImportStructure(null)));
	}

	@Test
	public void testGetPreventionCode_Unmapped()
	{
		Mockito.when(preventionManager.getPreventionByNameOrType(Mockito.anyString())).thenReturn(null); // not found returns null
		assertEquals(DEFAULT_PREVENTION_TYPE, cdsImmunizationImportMapper.getPreventionCode(getImportStructure(ImmunizationType.B_ATX)));
	}

	/**
	 * This is a test to make sure all of the cds defined codes return a valid prevention type in Juno
	 */
	@Test
	public void testGetPreventionCode_AllCodesHandled()
	{
		// set up a real prevention manager because we need the real values
		PreventionManager actualPreventionManager = new PreventionManager();
		Mockito.when(preventionManager.getPreventionByNameOrType(Mockito.anyString())).thenAnswer((Answer) invocationOnMock -> {
			Object[] args = invocationOnMock.getArguments();
			return actualPreventionManager.getPreventionByNameOrType((String) args[0]);
		});

		int errors = 0;
		for(ImmunizationType type : ImmunizationType.values())
		{
			String errorMessage = "immunization type " + type + " has no match";
			errors += checkNonDefaultTypeMatch(errorMessage, type.value(), cdsImmunizationImportMapper.getPreventionCode(getImportStructure(type)));
		}
		assertEquals("There are " + errors + " invalid CDS 5.0 prevention matches",0, errors);
	}

	private int checkNonDefaultTypeMatch(String message, String expected, String actual)
	{
		boolean isNotMapped = DEFAULT_PREVENTION_TYPE.equals(actual);
		if(isNotMapped)
		{
			MiscUtils.getLogger().error(message + " expected: <" + expected + "> but was <" + actual + ">");
		}
		return isNotMapped ? 1 : 0;
	}

	private Immunizations getImportStructure(ImmunizationType type)
	{
		ObjectFactory objectFactory = new ObjectFactory();
		Immunizations immunizations = objectFactory.createImmunizations();
		immunizations.setImmunizationType(type);
		return immunizations;
	}
}
