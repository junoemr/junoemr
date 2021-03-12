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

import org.apache.commons.lang.StringUtils;
import org.oscarehr.dataMigration.model.immunization.Immunization;
import org.oscarehr.prevention.service.PreventionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xml.cds.v5_0.Code;
import xml.cds.v5_0.ImmunizationType;
import xml.cds.v5_0.Immunizations;

import java.util.HashMap;

import static org.oscarehr.dataMigration.mapper.cds.CDSConstants.RESIDUAL_INFO_DATA_NAME_IMMUNIZATION_NEXT_DATE;
import static org.oscarehr.dataMigration.mapper.cds.CDSConstants.RESIDUAL_INFO_DATA_NAME_IMMUNIZATION_TYPE;
import static org.oscarehr.dataMigration.mapper.cds.CDSConstants.RESIDUAL_INFO_DATA_NAME_PROVIDER;

@Component
public class CDSImmunizationImportMapper extends AbstractCDSImportMapper<Immunizations, Immunization>
{
	public static final String DEFAULT_PREVENTION_TYPE = "OtherA";

	@Autowired
	private PreventionManager preventionManager;

	public CDSImmunizationImportMapper()
	{
		super();
	}

	@Override
	public Immunization importToJuno(Immunizations importStructure)
	{
		Immunization immunization = new Immunization();

		immunization.setName(importStructure.getImmunizationName());
		immunization.setPreventionType(getPreventionCode(importStructure));
		immunization.setManufacture(importStructure.getManufacturer());
		immunization.setLot(importStructure.getLotNumber());
		immunization.setRoute(importStructure.getRoute());
		immunization.setLocation(importStructure.getSite());
		immunization.setDose(importStructure.getDose());
		immunization.setDrugIdentificationNumber(getDin(importStructure));
		immunization.setAdministrationDate(toNullablePartialDateTime(importStructure.getDate()));
		immunization.setRefused(getYIndicator(importStructure.getRefusedFlag()));

		String commentStr = StringUtils.trimToNull(
				StringUtils.trimToEmpty(importStructure.getInstructions()) + "\n" +
						StringUtils.trimToEmpty(importStructure.getNotes())
		);
		immunization.setComments(commentStr);

		immunization.setNextDate(getResidualDataElementAsDate(importStructure.getResidualInfo(), RESIDUAL_INFO_DATA_NAME_IMMUNIZATION_NEXT_DATE));
		immunization.setProvider(toProviderNames(getResidualDataElementAsString(importStructure.getResidualInfo(), RESIDUAL_INFO_DATA_NAME_PROVIDER)));

		return immunization;
	}

	protected String getDin(Immunizations importStructure)
	{
		String din = null;
		Code code = importStructure.getImmunizationCode();
		if(code != null)
		{
			din = code.getValue();
		}
		return din;
	}

	protected String getPreventionCode(Immunizations importStructure)
	{
		String codeValue;
		ImmunizationType immunizationType = importStructure.getImmunizationType();
		if(immunizationType != null)
		{
			codeValue = importStructure.getImmunizationType().value();
		}
		else
		{
			codeValue = getResidualDataElementAsString(importStructure.getResidualInfo(), RESIDUAL_INFO_DATA_NAME_IMMUNIZATION_TYPE);
		}
		if(codeValue == null)
		{
			codeValue = DEFAULT_PREVENTION_TYPE;
		}
		else
		{
			HashMap<String, String> preventionHash = preventionManager.getPreventionByNameOrType(codeValue);
			if(preventionHash != null)
			{
				codeValue = preventionHash.get("name");
			}
			else
			{
				logDefaultValueUse("Unknown or invalid prevention type '" + codeValue + "'", DEFAULT_PREVENTION_TYPE);
				codeValue = DEFAULT_PREVENTION_TYPE;
			}
		}
		return codeValue;
	}
}
