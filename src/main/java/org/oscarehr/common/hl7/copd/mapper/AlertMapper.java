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
package org.oscarehr.common.hl7.copd.mapper;

import ca.uhn.hl7v2.HL7Exception;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.hl7.copd.model.v24.group.ZPD_ZTR_PROVIDER;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.demographicImport.service.CoPDImportService;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.oscarehr.util.MiscUtils;
import oscar.util.ConversionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AlertMapper
{

	private static final Logger logger = MiscUtils.getLogger();
	private final ZPD_ZTR message;
	private final ZPD_ZTR_PROVIDER provider;

	private final CoPDImportService.IMPORT_SOURCE importSource;

	public AlertMapper()
	{
		message = null;
		provider = null;
		importSource = CoPDImportService.IMPORT_SOURCE.UNKNOWN;
	}
	public AlertMapper(ZPD_ZTR message, int providerRep, CoPDImportService.IMPORT_SOURCE importSource)
	{
		this.message = message;
		this.provider = message.getPATIENT().getPROVIDER(providerRep);
		this.importSource = importSource;
	}

	public int getNumAlerts()
	{
		return provider.getZALReps();
	}

	public List<CaseManagementNote> getReminderNoteList() throws HL7Exception
	{
		int numNotes = getNumAlerts();
		List<CaseManagementNote> noteList = new ArrayList<>(numNotes);
		for(int i = 0; i < numNotes; i++)
		{
			CaseManagementNote note = getReminderNote(i);
			if(note != null)
			{
				noteList.add(note);
			}
		}
		return noteList;
	}

	public CaseManagementNote getReminderNote(int rep) throws HL7Exception
	{
		CaseManagementNote note = null;

		String noteText = getAlertText(rep);
		if(noteText != null)
		{
			note = new CaseManagementNote();

			Date date = getAlertDate(rep);
			note.setObservationDate(date);
			note.setUpdateDate(date);
			note.setNote(noteText.replaceAll("~crlf~", "\n"));
		}
		return note;
	}

	public Date getAlertDate(int rep) throws HL7Exception
	{
		String dateStr = provider.getZAL(rep).getZal2_dateOfAlert().getTs1_TimeOfAnEvent().getValue();
		if(dateStr==null || dateStr.trim().isEmpty() || dateStr.equals("00000000"))
		{
			return null;
		}
		return ConversionUtils.fromDateString(dateStr, "yyyyMMdd");
	}

	public String getAlertText(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getZAL(rep).getZal5_alertTextSent().getValue());
	}
}
