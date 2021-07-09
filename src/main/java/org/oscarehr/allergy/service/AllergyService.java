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
package org.oscarehr.allergy.service;

import org.oscarehr.allergy.dao.AllergyDao;
import org.oscarehr.allergy.model.Allergy;
import org.oscarehr.common.dao.PartialDateDao;
import org.oscarehr.common.model.PartialDate;
import org.oscarehr.dataMigration.converter.in.AllergyModelToDbConverter;
import org.oscarehr.dataMigration.model.common.ResidualInfo;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.oscarehr.encounterNote.service.EncounterNoteService;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.provider.service.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oscar.util.ConversionUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service("allergy.service.AllergyService")
@Transactional
public class AllergyService
{
	@Autowired
	private AllergyDao allergyDao;

	@Autowired
	private PartialDateDao partialDateDao;

	@Autowired
	private ProviderService providerService;

	@Autowired
	private AllergyModelToDbConverter allergyModelToDbConverter;

	@Autowired
	private EncounterNoteService encounterNoteService;

	public void saveNewAllergy(org.oscarehr.dataMigration.model.allergy.Allergy allergy, Demographic dbDemographic)
	{
		Allergy dbAllergy = allergyModelToDbConverter.convert(allergy);
		dbAllergy.setDemographicNo(dbDemographic.getId());

		if(allergy.getStartDate() != null)
		{
			dbAllergy.setStartDateFormat(allergy.getStartDate().getFormatString());
		}
		addNewAllergy(dbAllergy);

		String annotation = allergy.getAnnotation();
		List<ResidualInfo> residualInfoList = allergy.getResidualInfo();

		Optional<CaseManagementNote> allergyNoteOptional = encounterNoteService.buildBaseAnnotationNote(annotation, residualInfoList);
		if(allergyNoteOptional.isPresent())
		{
			CaseManagementNote allergyNote = allergyNoteOptional.get();
			ProviderData providerData = providerService.getProvider(dbAllergy.getProviderNo());
			allergyNote.setProvider(providerData);
			allergyNote.setSigningProvider(providerData);
			allergyNote.setDemographic(dbDemographic);
			allergyNote.setObservationDate(ConversionUtils.toLegacyDate(allergy.getEntryDateTime().toLocalDate()));
			encounterNoteService.saveAllergyNote(allergyNote, dbAllergy);
		}
	}

	public void saveNewAllergies(List<org.oscarehr.dataMigration.model.allergy.Allergy> allergyList, Demographic dbDemographic)
	{
		for(org.oscarehr.dataMigration.model.allergy.Allergy allergy : allergyList)
		{
			saveNewAllergy(allergy, dbDemographic);
		}
	}

	public Allergy addNewAllergy(Allergy allergy)
	{
		//set any missing default values
		if(allergy.getEntryDate() == null)
		{
			allergy.setEntryDate(new Date());
		}

		allergyDao.persist(allergy);
		partialDateDao.setPartialDate(PartialDate.TABLE_ALLERGIES, allergy.getId(), PartialDate.ALLERGIES_STARTDATE, allergy.getStartDateFormat());

		return allergy;
	}

	/**
	 * Wrapper for updating an old allergy entry.
	 * Handles updating the allergy as well as any associated partial_date entry.
	 * @param allergy allergy model to update
	 */
	public void update(Allergy allergy)
	{
		allergyDao.merge(allergy);
		partialDateDao.setPartialDate(PartialDate.TABLE_ALLERGIES, allergy.getId(), PartialDate.ALLERGIES_STARTDATE, allergy.getStartDateFormat());
	}
}
