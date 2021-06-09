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
package org.oscarehr.dataMigration.converter.out.note;

import org.apache.commons.lang3.math.NumberUtils;
import org.oscarehr.common.dao.PartialDateDao;
import org.oscarehr.common.model.PartialDate;
import org.oscarehr.dataMigration.converter.out.BaseDbToModelConverter;
import org.oscarehr.dataMigration.model.common.PartialDateTime;
import org.oscarehr.dataMigration.model.dx.DxCode;
import org.oscarehr.dataMigration.model.encounterNote.BaseNote;
import org.oscarehr.dataMigration.model.provider.Reviewer;
import org.oscarehr.dataMigration.service.context.PatientExportContext;
import org.oscarehr.dataMigration.service.context.PatientExportContextService;
import org.oscarehr.encounterNote.dao.CaseManagementNoteLinkDao;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.oscarehr.encounterNote.model.CaseManagementNoteLink;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

import java.time.LocalDateTime;
import java.util.List;

@Component
public abstract class BaseNoteDbToModelConverter<N extends BaseNote> extends
		BaseDbToModelConverter<CaseManagementNote, N>
{
	@Autowired
	private ProviderDataDao providerDao;

	@Autowired
	private CaseManagementNoteLinkDao caseManagementNoteLinkDao;

	@Autowired
	private PartialDateDao partialDateDao;

	@Autowired
	protected PatientExportContextService patientExportContextService;

	@Override
	public N convert(CaseManagementNote input)
	{
		if(input == null)
		{
			return null;
		}

		N exportNote = getNewNoteObject();

		exportNote.setId(String.valueOf(input.getId()));
		exportNote.setNoteText(input.getNote());
		exportNote.setRevisionId(input.getUuid());
		exportNote.setObservationDate(ConversionUtils.toNullableLocalDateTime(input.getObservationDate()));
		exportNote.setProvider(findProvider(input.getProvider()));

		Reviewer reviewer = Reviewer.fromProvider(findProvider(input.getSigningProvider()));
		if(reviewer != null)
		{
			// there is no reviewed date time stored for notes, so do our best
			LocalDateTime reviewTime = ConversionUtils.toNullableLocalDateTime(input.getUpdateDate());
			reviewTime = (reviewTime != null) ? reviewTime : LocalDateTime.now();
			reviewer.setReviewDateTime(PartialDateTime.from(reviewTime));
		}
		exportNote.setSigningProvider(reviewer);

		List<ProviderData> providerDataList = providerDao.findNoteEditors(input.getUuid());
		for(ProviderData providerData : providerDataList)
		{
			exportNote.addEditor(findProvider(providerData));
		}

		// convert attached issues
		input.getIssueNoteList()
				.stream()
				.map((link) -> link.getId().getCaseManagementIssue().getIssue())
				.forEach((issue) ->
				{
					DxCode dxCode = new DxCode();
					dxCode.setCode(issue.getCode());
					dxCode.setCodingSystem(DxCode.DxCodingSystem.fromValue(issue.getType()));
					dxCode.setDescription(issue.getDescription());
					exportNote.addIssueCode(dxCode);
				});
		return subConvert(input, exportNote);
	}

	public abstract N getNewNoteObject();

	public abstract N subConvert(CaseManagementNote input, N exportNote);

	protected String getLinkedAnnotation(CaseManagementNote input)
	{
		String noteString = null;
		CaseManagementNoteLink link = caseManagementNoteLinkDao.findLatestNoteNoteLinkById(input.getId());
		if(link != null)
		{
			CaseManagementNote note = link.getNote();
			noteString = note.getNote();
		}
		return noteString;
	}

	protected PartialDate getExtPartialDate(Long ExtId)
	{
		return partialDateDao.getPartialDate(
				org.oscarehr.common.model.PartialDate.TABLE_CASEMGMT_NOTE,
				Math.toIntExact(ExtId),
				org.oscarehr.common.model.PartialDate.FIELD_CASEMGMT_NOTE_EXT_VALUE);
	}

	protected Long convertAgeAtOnset(String onsetStr)
	{
		if(onsetStr == null)
		{
			return null;
		}
		else if(NumberUtils.isCreatable(onsetStr))
		{
			return Long.parseLong(onsetStr);
		}
		PatientExportContext context = patientExportContextService.getContext();
		String message = "Invalid AgeAtOnset value '" + onsetStr + "' could not be loaded";
		if(context != null)
		{
			context.getExportLogger().logEvent("[" + context.getCurrentProcessIdentifier() + "] " + message);
		}
		return null;
	}
}
