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
package org.oscarehr.common.hl7.copd.mapper.mediplan;

import ca.uhn.hl7v2.HL7Exception;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.allergy.model.Allergy;
import org.oscarehr.common.hl7.copd.mapper.AllergyMapper;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.dataMigration.service.ImporterExporterFactory;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.oscarehr.util.MiscUtils;

public class AllergyMapperMediplan extends AllergyMapper
{
	private static final Logger logger = MiscUtils.getLogger();
	public static final String MEDIPLAN_ALLERGY_NOTE_ID = "MEDIPLAN ALLERGY SECTION";

	public AllergyMapperMediplan(ZPD_ZTR message, int providerRep)
	{
		super(message, providerRep, ImporterExporterFactory.IMPORT_SOURCE.MEDIPLAN);
	}

	@Override
	public int getNumAllergies() throws HL7Exception
	{
		int count = 0;
		for (int i =0; i < provider.getZALReps(); i++)
		{
			if (isMedicalAllergyNoteMediplan(i))
			{
				count ++;
			}
		}
		return count;
	}

	@Override
	public Allergy getAllergy(int rep) throws HL7Exception
	{
		rep = translateAllergyRepToZALRep(rep);

		Allergy allergy = new Allergy();
		String description = provider.getZAL(rep).getZal5_alertTextSent().getValue().replace(" / ", "\n");
		if (description == null)
		{
			description = "INVALID/MISSING DESCRIPTION";
			logger.warn("Missing allergy description. values set to:" + description);
		}
		allergy.setStartDate(provider.getZAL(rep).getZal2_dateOfAlert().getTs1_TimeOfAnEvent().getValueAsDate());
		allergy.setEntryDate(provider.getZAL(rep).getZal2_dateOfAlert().getTs1_TimeOfAnEvent().getValueAsDate());
		allergy.setDescription(description);

		allergy.setArchived(false);
		allergy.setTypeCode(0);// TODO can numeric code be mapped from string in IAM.2.1?
		allergy.setDrugrefId("0");
		allergy.setSeverityOfReaction(Allergy.SEVERITY_CODE_UNKNOWN);
		allergy.setOnsetOfReaction(Allergy.ONSET_CODE_UNKNOWN);

		allergy.setReaction("");
		allergy.setPosition(0);
		return allergy;
	}

	@Override
	public CaseManagementNote getAllergyNote(int rep) throws HL7Exception
	{
		rep = translateAllergyRepToZALRep(rep);
		String nteNote = StringUtils.trimToNull(provider.getZAL(rep).getZal5_alertTextSent().getValue().replace(" / ", "\n"));
		CaseManagementNote note = null;
		if (nteNote != null)
		{
			note = new CaseManagementNote();
			note.setNote(nteNote.replaceAll("~crlf~", "\n"));
			note.setObservationDate(provider.getZAL(rep).getZal2_dateOfAlert().getTs1_TimeOfAnEvent().getValueAsDate());
			note.setUpdateDate(provider.getZAL(rep).getZal2_dateOfAlert().getTs1_TimeOfAnEvent().getValueAsDate());
		}
		return note;
	}

	private boolean isMedicalAllergyNoteMediplan(int rep) throws HL7Exception
	{
		String ZALText = provider.getZAL(rep).getZal5_alertTextSent().getValue();
		if ( ZALText != null)
		{
			return ZALText.indexOf(MEDIPLAN_ALLERGY_NOTE_ID) == 0;
		}
		return false;
	}

	/**
	 * convert allergy rep which is an absolute offset to a ZAL rep which may have holes.
	 * @param rep
	 * @return
	 * @throws HL7Exception
	 */
	private int translateAllergyRepToZALRep(int rep) throws HL7Exception
	{
		int count = 0;
		for (int i =0; i < provider.getZALReps(); i++)
		{
			if (isMedicalAllergyNoteMediplan(i))
			{
				count ++;
			}

			if (count > rep)
			{
				return i;
			}
		}
		return count;
	}

}
