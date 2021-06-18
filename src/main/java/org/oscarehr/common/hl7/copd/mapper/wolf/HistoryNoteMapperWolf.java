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
package org.oscarehr.common.hl7.copd.mapper.wolf;

import ca.uhn.hl7v2.HL7Exception;
import org.apache.commons.lang.StringUtils;
import org.oscarehr.common.hl7.copd.mapper.HistoryNoteMapper;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.dataMigration.service.CoPDImportService;
import org.oscarehr.dataMigration.transfer.CoPDRecordData;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import oscar.util.ConversionUtils;

import java.util.Date;

/**
 * Wolf specific import logic
 */
public class HistoryNoteMapperWolf extends HistoryNoteMapper
{
	public HistoryNoteMapperWolf(ZPD_ZTR message, int providerRep, CoPDRecordData recordData) throws HL7Exception
	{
		super(message, providerRep, CoPDImportService.IMPORT_SOURCE.WOLF, recordData);
	}

	@Override
	public CaseManagementNote getFamilyHistoryNote(int rep) throws HL7Exception
	{
		Date diagnosisDate = getFamHistDiagnosisDate(rep);
		if(diagnosisDate == null)
		{
			/* Wolf has stated that this field gets used for relationships & family related diseases,
			 * and that if the date is missing or the description is 'unknown', the data can be ignored,
			 * since it indicates a relationship that does not have enough info for the transfer */
			return null;
		}

		CaseManagementNote note = new CaseManagementNote();

		String relation = getFamHistRelationshipToPatient(rep);
		String diagnosisDescription  = getFamHistDiagnosisDescription(rep);
		String causeOfDeath = getFamHistCauseOfDeath(rep);
		String comments = getFamHistComments(rep);
		String wolfCustom = getWolfCustom(rep);

		String noteText = relation + ": ";
		if(diagnosisDescription != null)
		{
			noteText += StringUtils.trimToEmpty(diagnosisDescription + "\n");
		}
		if(wolfCustom != null)
		{
			noteText += StringUtils.trimToEmpty(wolfCustom + "\n");
		}
		if(causeOfDeath != null)
		{
			noteText += "Cause of death: " + StringUtils.trimToEmpty(causeOfDeath) + "\n";
		}
		if(comments != null)
		{
			noteText += StringUtils.trimToEmpty(comments + "\n");
		}

		note.setNote(StringUtils.trim(noteText.replaceAll("~crlf~", "\n")) + " - " + ConversionUtils.toDateString(diagnosisDate));
		note.setObservationDate(diagnosisDate);
		note.setUpdateDate(diagnosisDate);

		return note;
	}

	public String getWolfCustom(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getZHF(rep).getZhf9_wolf_custom().getValue());
	}

	@Override
	protected String getOtherContactInfo(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getNK1(rep).getNk134_JobStatus().getValue());
	}

}
