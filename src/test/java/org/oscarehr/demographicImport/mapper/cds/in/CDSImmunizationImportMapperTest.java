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
package org.oscarehr.demographicImport.mapper.cds.in;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import xml.cds.v5_0.ImmunizationType;
import xml.cds.v5_0.Immunizations;
import xml.cds.v5_0.ObjectFactory;
import org.oscarehr.prevention.service.PreventionManager;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.oscarehr.demographicImport.mapper.cds.in.CDSImmunizationImportMapper.DEFAULT_PREVENTION_TYPE;

public class CDSImmunizationImportMapperTest
{
	@Autowired
	@InjectMocks
	private CDSImmunizationImportMapper cdsImmunizationImportMapper;

	@Mock
	private PreventionManager preventionManager;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
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
		Mockito.when(preventionManager.getPreventionByNameOrType(Mockito.anyString())).thenAnswer(new Answer()
		{
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				Object[] args = invocationOnMock.getArguments();
				return actualPreventionManager.getPreventionByNameOrType((String) args[0]);
			}
		});

		int errors = 0;
		for(ImmunizationType type : ImmunizationType.values())
		{
			String errorMessage = "immunization type " + type + " has no match";

			switch(type)
			{
				//TODO all of these should have a non-default matching
				case CHOL_ECOL:
				case MEN:
				case MEN_B:
				case PNEU:
				case TDAP_IPV:
				case TYPH:
				case TYPH_HA:
				case MMR_VAR:
				case ROT:
				case ZOS:
				case B_ATX:
				case CMV_IG:
				case D_ATX:
				case HB_IG:
				case IG:
				case RAB_IG:
				case RSV_AB:
				case RSV_IG:
				case T_IG:
				case VAR_IG:
				case VIG:
					errors += checkTypeMatch(errorMessage, DEFAULT_PREVENTION_TYPE, cdsImmunizationImportMapper.getPreventionCode(getImportStructure(type)));
					break;
				default:
					errors += checkNonDefaultTypeMatch(errorMessage, type.value(), cdsImmunizationImportMapper.getPreventionCode(getImportStructure(type)));
					break;
			}
		}
		assertEquals("There are " + errors + " invalid CDS 5.0 prevention matches",0, errors);
	}

	private int checkTypeMatch(String message, String expected, String actual)
	{
		boolean isEqual = expected.equals(actual);
		if(!isEqual)
		{
			MiscUtils.getLogger().error(message + " expected: <" + expected + "> but was <" + actual + ">");
		}
		return isEqual ? 0 : 1;
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
