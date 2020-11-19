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
package org.oscarehr.demographicImport.mapper.cds.out;

import org.apache.commons.lang3.EnumUtils;
import org.oscarehr.common.xml.cds.v5_0.model.Code;
import org.oscarehr.common.xml.cds.v5_0.model.ImmunizationType;
import org.oscarehr.common.xml.cds.v5_0.model.Immunizations;
import org.oscarehr.common.xml.cds.v5_0.model.YnIndicator;
import org.oscarehr.demographicImport.model.immunization.Immunization;

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
			drugCode.setCodingSystem("DIN");
			drugCode.setDescription(null); //TODO
			drugCode.setValue(din);
			immunizations.setImmunizationCode(drugCode);
		}
		immunizations.setDate(toNullableDateTimeFullOrPartial(exportStructure.getAdministrationDate()));
		immunizations.setRefusedFlag(getRefusalFlag(exportStructure));
		immunizations.setInstructions(null); //TODO
		immunizations.setNotes(exportStructure.getComments());

		return immunizations;
	}

	protected ImmunizationType getImmunizationType(Immunization exportStructure)
	{
		ImmunizationType immunizationType = null;
		String preventionType = exportStructure.getPreventionType();
		if(EnumUtils.isValidEnum(ImmunizationType.class, preventionType))
		{
			immunizationType = ImmunizationType.fromValue(preventionType);
		}
		// might need to do some mapping here for additional values

		return immunizationType;
	}

	protected YnIndicator getRefusalFlag(Immunization exportStructure)
	{
		YnIndicator ynIndicator = objectFactory.createYnIndicator();
		ynIndicator.setBoolean(exportStructure.getRefused());
		return ynIndicator;
	}
}
