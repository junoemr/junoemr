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

import org.apache.log4j.Logger;
import org.oscarehr.common.xml.cds.v5_0.model.Demographics;
import org.oscarehr.demographicImport.model.demographic.Demographic;
import org.oscarehr.demographicImport.model.provider.Provider;
import oscar.util.ConversionUtils;

public class CDSDemographicImportMapper extends AbstractCDSImportMapper<Demographics, Demographic>
{
	private static final Logger logger = Logger.getLogger(CDSDemographicImportMapper.class);


	public CDSDemographicImportMapper()
	{
		super();
	}

	@Override
	public Demographic importToJuno(Demographics importStructure)
	{
		Demographic demographic = new Demographic();
		fillImportDemographic(importStructure, demographic);
		return demographic;
	}

	protected void fillImportDemographic(Demographics importStructure, Demographic demographic)
	{
		demographic.setFirstName(importStructure.getNames().getLegalName().getFirstName().getPart());
		demographic.setLastName(importStructure.getNames().getLegalName().getLastName().getPart());
		demographic.setDateOfBirth(ConversionUtils.toLocalDate(importStructure.getDateOfBirth()));
		demographic.setSex(importStructure.getGender().toString());
		demographic.setEmail(importStructure.getEmail());

		demographic.setMrpProvider(getImportPrimaryPhysician(importStructure));
	}


	protected Provider getImportPrimaryPhysician(Demographics importStructure)
	{
		Provider provider = new Provider();
		provider.setFirstName(importStructure.getPrimaryPhysician().getName().getFirstName());
		provider.setLastName(importStructure.getPrimaryPhysician().getName().getLastName());
		provider.setOhipNumber(importStructure.getPrimaryPhysician().getOHIPPhysicianId());
		provider.setPractitionerNumber(importStructure.getPrimaryPhysician().getPrimaryPhysicianCPSO());
		return provider;
	}
}
