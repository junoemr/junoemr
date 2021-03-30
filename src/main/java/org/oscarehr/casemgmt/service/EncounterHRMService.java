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

package org.oscarehr.casemgmt.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.oscarehr.casemgmt.dto.EncounterNotes;
import org.oscarehr.casemgmt.dto.EncounterSectionNote;
import org.oscarehr.casemgmt.dto.EncounterSectionNote.SortChronologicDescTextAsc;
import org.oscarehr.common.dao.OscarLogDao;
import org.oscarehr.common.model.SecObjectName;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentDao;
import org.oscarehr.hospitalReportManager.dto.HRMDemographicDocument;
import org.oscarehr.hospitalReportManager.model.HRMDocument;
import org.oscarehr.hospitalReportManager.service.HRMService;
import org.oscarehr.managers.SecurityInfoManager;
import org.springframework.beans.factory.annotation.Autowired;
import oscar.OscarProperties;
import oscar.util.ConversionUtils;
import oscar.util.StringUtils;

public class EncounterHRMService extends EncounterSectionService
{
	public static final String SECTION_ID = "hrm";
	private static final String SECTION_TITLE_KEY = "oscarEncounter.Index.msgHRMDocuments";
	private static final String SECTION_TITLE_COLOUR = "#6699cc";
	private static final String WIN_NAME_PREFIX = "docs";

	@Autowired
	private SecurityInfoManager securityInfoManager;

	@Autowired
	HRMDocumentDao hrmDocumentDao;

	@Autowired
	HRMService hrmService;

	@Autowired
	OscarLogDao oscarLogDao;

	@Override
	public String getSectionId()
	{
		return SECTION_ID;
	}

	@Override
	protected String getSectionTitleKey()
	{
		return SECTION_TITLE_KEY;
	}

	@Override
	protected String getSectionTitleColour()
	{
		return SECTION_TITLE_COLOUR;
	}

	@Override
	protected String getOnClickTitle(SectionParameters sectionParams)
	{
		String url = sectionParams.getContextPath() + "/hospitalReportManager/displayHRMDocList.jsp" +
			"?demographic_no=" + encodeUrlParam(sectionParams.getDemographicNo());

		return "popupPage(500,1115,'" + this.getWinName(sectionParams) + "', '" + url + "');";
	}

	private String getWinName(SectionParameters sectionParams)
	{
		return WIN_NAME_PREFIX + sectionParams.getDemographicNo();
	}

	public EncounterNotes getNotes(SectionParameters sectionParams, Integer limit, Integer offset)
	{
		if(!securityInfoManager.hasPrivilege(sectionParams.getLoggedInInfo().getLoggedInProviderNo(),
				SecurityInfoManager.PRIVILEGE_LEVEL.READ, Integer.parseInt(sectionParams.getDemographicNo()), SecObjectName.OBJECT_NAME.HRM)
				|| !OscarProperties.getInstance().hasHRMDocuments())
		{
			return EncounterNotes.noNotes();
		}

		Map<String, HRMDemographicDocument> demographicDocuments = hrmService.getHrmDocumentsForDemographic(
			Integer.parseInt(sectionParams.getDemographicNo())
		);

		List<EncounterSectionNote> out = new ArrayList<>();
		for (Entry<String, HRMDemographicDocument> entry: demographicDocuments.entrySet())
		{
			EncounterSectionNote sectionNote = new EncounterSectionNote();

			HRMDocument hrmDocument = entry.getValue().getHrmDocument();
			List<Integer> duplicateIdList = entry.getValue().getDuplicateIds();

			String reportStatus = hrmDocument.getReportStatus();
			String dispFilename = hrmDocument.getReportType();
			String dispDocNo = hrmDocument.getId().toString();
			String description = hrmDocument.getDescription();

			String text = description;
			if(StringUtils.isNullOrEmpty(text))
			{
				text = dispFilename;
			}

			if(reportStatus != null && reportStatus.equalsIgnoreCase("C"))
			{
				text = "(Cancelled) " + text;
			}

			String trimmedText = EncounterSectionService.getTrimmedText(text);

			LocalDateTime date = ConversionUtils.toNullableLocalDate(
				hrmDocument.getTimeReceived()).atStartOfDay();

			sectionNote.setUpdateDate(date);

			StringBuilder duplicateLabIdQueryString = new StringBuilder();
			if (duplicateIdList!=null)
			{
				for (Integer duplicateLabIdTemp : duplicateIdList)
				{
					if (duplicateLabIdQueryString.length() > 0)
					{
						duplicateLabIdQueryString.append(',');
					}
					duplicateLabIdQueryString.append(duplicateLabIdTemp);
				}
			}

			int hash = Math.abs(this.getWinName(sectionParams).hashCode());
			String url = sectionParams.getContextPath() + "/hospitalReportManager/Display.do" +
				"?id=" + encodeUrlParam(dispDocNo) +
				"&duplicateLabIds=" + encodeUrlParam(duplicateLabIdQueryString.toString());

			String onClickString = "junoEncounter.popupPageAndReload(700,800,'" + hash + "', '" + url +"', '" + SECTION_ID + "');";
			sectionNote.setOnClick(onClickString);

			String labRead = "";
			if(!oscarLogDao.hasRead(sectionParams.getProviderNo(),"hrm", dispDocNo))
			{
				labRead = "*";
			}

			sectionNote.setText(labRead + trimmedText + labRead);
			sectionNote.setTitle(formatTitleWithLocalDateTime(trimmedText, date));

			out.add(sectionNote);
		}

		Collections.sort(out, new SortChronologicDescTextAsc());

		return EncounterNotes.limitedEncounterNotes(out, offset, limit);
	}
}
