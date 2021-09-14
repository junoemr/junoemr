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

import org.apache.commons.lang3.EnumUtils;
import org.oscarehr.dataMigration.mapper.cds.CDSConstants;
import org.oscarehr.dataMigration.model.immunization.Immunization;
import org.springframework.stereotype.Component;
import xml.cds.v5_0.Code;
import xml.cds.v5_0.ImmunizationType;
import xml.cds.v5_0.Immunizations;
import xml.cds.v5_0.ResidualInformation;
import xml.cds.v5_0.YnIndicator;

import static org.oscarehr.dataMigration.mapper.cds.CDSConstants.DRUG_IDENTIFICATION_NUMBER;
import static org.oscarehr.dataMigration.mapper.cds.CDSConstants.RESIDUAL_INFO_DATA_NAME_IMMUNIZATION_NEXT_DATE;
import static org.oscarehr.dataMigration.mapper.cds.CDSConstants.RESIDUAL_INFO_DATA_NAME_IMMUNIZATION_TYPE;
import static org.oscarehr.dataMigration.mapper.cds.CDSConstants.RESIDUAL_INFO_DATA_NAME_PROVIDER;

@Component
public class CDSImmunizationExportMapper extends AbstractCDSExportMapper<Immunizations, Immunization>
{
	public CDSImmunizationExportMapper()
	{
		super();
	}

	@Override
	public Immunizations exportFromJuno(Immunization exportStructure)
	{
		Immunizations immunizations = objectFactory.createImmunizations();

		immunizations.setImmunizationName(exportStructure.getName());
		immunizations.setImmunizationType(getImmunizationType(exportStructure));
		immunizations.setManufacturer(exportStructure.getManufacture());
		immunizations.setLotNumber(exportStructure.getLot());
		immunizations.setRoute(exportStructure.getRoute());
		immunizations.setSite(exportStructure.getLocation());
		immunizations.setDose(exportStructure.getDose());

		String din = exportStructure.getDrugIdentificationNumber();
		if(din != null && !din.isEmpty())
		{
			Code drugCode = objectFactory.createCode();
			drugCode.setCodingSystem(DRUG_IDENTIFICATION_NUMBER);
			drugCode.setDescription(null); // not mentioned in spec, and we don't have this. maybe could be pulled from somewhere
			drugCode.setValue(din);
			immunizations.setImmunizationCode(drugCode);
		}
		immunizations.setDate(toNullableDateTimeFullOrPartial(exportStructure.getAdministrationDate()));
		immunizations.setRefusedFlag(getRefusalFlag(exportStructure));
		immunizations.setInstructions(null); // we don't have a separate instructions section.
		immunizations.setNotes(exportStructure.getComments());

		ResidualInformation residualInformation = objectFactory.createResidualInformation();


		// add next immunization date
		addNonNullDataElements(
				residualInformation,
				RESIDUAL_INFO_DATA_NAME_IMMUNIZATION_NEXT_DATE,
				exportStructure.getNextDate());

		// add provider info
		addNonNullDataElements(
				residualInformation,
				CDSConstants.ResidualInfoDataType.TEXT,
				RESIDUAL_INFO_DATA_NAME_PROVIDER,
				exportStructure.getProvider().getLastName() + "," + exportStructure.getProvider().getFirstName());

		/* write type as residual info only if it is not in the standard list */
		if(immunizations.getImmunizationType() == null)
		{
			addNonNullDataElements(
					residualInformation,
					CDSConstants.ResidualInfoDataType.TEXT,
					RESIDUAL_INFO_DATA_NAME_IMMUNIZATION_TYPE,
					exportStructure.getPreventionType());
		}

		immunizations.setResidualInfo(residualInformation);

		return immunizations;
	}

	protected ImmunizationType getImmunizationType(Immunization exportStructure)
	{
		ImmunizationType immunizationType = null;
		// Matching based on generated enum. Enum names can't have dashes so replace dashes with underscores
		String preventionType = exportStructure.getPreventionType().replaceAll("-", "_");
		if(EnumUtils.isValidEnumIgnoreCase(ImmunizationType.class, preventionType))
		{
			immunizationType = ImmunizationType.valueOf(preventionType.toUpperCase());
		}
		// additional values will be exported under the residual info section.
		// many type codes in Juno are not present in the CDS enum

		return immunizationType;
	}

	protected YnIndicator getRefusalFlag(Immunization exportStructure)
	{
		YnIndicator ynIndicator = objectFactory.createYnIndicator();
		ynIndicator.setBoolean(exportStructure.getRefused());
		return ynIndicator;
	}
}
