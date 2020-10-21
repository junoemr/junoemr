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
package org.oscarehr.demographicImport.mapper.cds;

import org.oscarehr.common.xml.cds.v5_0.model.LegalName;
import org.oscarehr.common.xml.cds.v5_0.model.ObjectFactory;
import org.oscarehr.common.xml.cds.v5_0.model.OmdCds;
import org.oscarehr.demographicImport.mapper.AbstractDemographicImportExportMapper;
import org.oscarehr.demographicImport.model.demographic.Demographic;
import oscar.util.ConversionUtils;

public class CDSDemographicImportExportMapper extends AbstractDemographicImportExportMapper<OmdCds>
{
	protected final ObjectFactory objectFactory;

	public CDSDemographicImportExportMapper()
	{
		this.objectFactory = new ObjectFactory();
	}

	@Override
	public Demographic importToJuno(OmdCds importStructure, Demographic demographic)
	{
		fillImportDemographic(importStructure.getPatientRecord().getDemographics(), demographic);
		return demographic;
	}

	@Override
	public OmdCds exportFromJuno(Demographic exportStructure)
	{
		OmdCds omdCds = objectFactory.createOmdCds();
		OmdCds.PatientRecord patientRecord = objectFactory.createOmdCdsPatientRecord();
		omdCds.setPatientRecord(patientRecord);

		return exportFromJuno(exportStructure, omdCds);
	}

	@Override
	public OmdCds exportFromJuno(Demographic exportStructure, OmdCds importStructure)
	{
		OmdCds.PatientRecord.Demographics demographics = importStructure.getPatientRecord().getDemographics();
		if (demographics == null)
		{
			demographics = objectFactory.createOmdCdsPatientRecordDemographics();
		}
		fillExportDemographic(exportStructure, demographics);
		importStructure.getPatientRecord().setDemographics(demographics);
		return importStructure;
	}

	protected void fillExportDemographic(Demographic exportStructure, OmdCds.PatientRecord.Demographics demographics)
	{
		demographics.setNames(getExportNames(exportStructure));
		demographics.setDateOfBirth(ConversionUtils.toXmlGregorianCalendar(exportStructure.getDateOfBirth()));
		demographics.setGender(exportStructure.getSex());
		demographics.setEmail(exportStructure.getEmail());
	}

	protected void fillImportDemographic(OmdCds.PatientRecord.Demographics importStructure, Demographic demographic)
	{
		demographic.setFirstName(importStructure.getNames().getLegalName().getFirstName().getPart());
		demographic.setLastName(importStructure.getNames().getLegalName().getLastName().getPart());
		demographic.setDateOfBirth(ConversionUtils.toLocalDate(importStructure.getDateOfBirth()));
		demographic.setSex(importStructure.getGender());
		demographic.setEmail(importStructure.getEmail());
	}

	protected OmdCds.PatientRecord.Demographics.Names getExportNames(Demographic exportStructure)
	{
		OmdCds.PatientRecord.Demographics.Names names = objectFactory.createOmdCdsPatientRecordDemographicsNames();
		LegalName legalName = objectFactory.createLegalName();

		// first name
		LegalName.FirstName firstName = objectFactory.createLegalNameFirstName();
		firstName.setPart(exportStructure.getFirstName());
		firstName.setPartType("GIV"); //TODO lookup CDS code

		// last name
		LegalName.LastName lastName = objectFactory.createLegalNameLastName();
		lastName.setPart(exportStructure.getLastName());
		lastName.setPartType("FAMC"); //TODO lookup CDS code

		legalName.setFirstName(firstName);
		legalName.setLastName(lastName);

		names.setLegalName(legalName);
		return names;
	}
}
