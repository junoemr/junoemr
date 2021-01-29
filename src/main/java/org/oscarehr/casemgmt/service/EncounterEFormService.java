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

import org.oscarehr.casemgmt.dto.EncounterNotes;
import org.oscarehr.casemgmt.dto.EncounterSectionNote;
import org.oscarehr.eform.dao.EFormDataDao;
import org.oscarehr.eform.model.EFormData;
import org.oscarehr.eform.service.EFormTemplateService;
import org.oscarehr.managers.SecurityInfoManager;
import org.springframework.beans.factory.annotation.Autowired;
import oscar.eform.EFormUtil;
import oscar.util.ConversionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EncounterEFormService extends EncounterSectionService
{
	public static final String SECTION_ID = "eforms";
	protected static final String SECTION_TITLE_KEY = "global.eForms";
	protected static final String SECTION_TITLE_COLOUR = "#008000";

	@Autowired
	private SecurityInfoManager securityInfoManager;

	@Autowired
	private EFormDataDao eFormDataDao;

	@Autowired
	private EFormTemplateService eFormTemplateService;

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
	protected String getOnClickPlus(SectionParameters sectionParams)
	{
		String winName = "AddeForm" + sectionParams.getDemographicNo();

		String url = sectionParams.getContextPath() + "/eform/efmformslistadd.jsp" +
				"?demographic_no=" + encodeUrlParam(sectionParams.getDemographicNo()) +
				"&appointment=" + encodeUrlParam(sectionParams.getAppointmentNo()) +
				"&parentAjaxId=" + SECTION_ID;

		return "popupPage(500,950,'" + winName + "','" + url +"');";
	}

	@Override
	protected String getOnClickTitle(SectionParameters sectionParams)
	{
		String winName = "eForm" + sectionParams.getDemographicNo();
		String url = sectionParams.getContextPath() + "/eform/efmpatientformlist.jsp" +
				"?demographic_no=" + encodeUrlParam(sectionParams.getDemographicNo()) +
				"&apptProvider=" + encodeUrlParam(sectionParams.getProviderNo()) +
				"&appointment=" + encodeUrlParam(sectionParams.getAppointmentNo()) +
				"&parentAjaxId=" + SECTION_ID;

		return "popupPage(500,950,'" + winName + "', '" + url + "')";
	}

	public EncounterNotes getNotes(
			SectionParameters sectionParams, Integer limit,
			Integer offset
	)
	{
		List<EncounterSectionNote> out = new ArrayList<>();

		if(!securityInfoManager.hasPrivilege(sectionParams.getLoggedInInfo(), "_eform", "r", null))
		{
			return EncounterNotes.noNotes();
		}

		String eformPopupHeight = getEformPopupHeight(sectionParams);
		String eformPopupWidth = getEformPopupWidth(sectionParams);

				//I've put in an arbitrary limit here of 100. Some people use a single eform/patient for
		//logging calls, etc. This makes this result set huge. People can click on the eform tab and view the full
		//history if they need to.
		List<EFormData> eFormDatas = eFormDataDao.findInstancedByDemographicId(
				Integer.parseInt(sectionParams.getDemographicNo()), 0, 100, true);

		for (EFormData eFormData : eFormDatas)
		{
			if (eFormData.isShowLatestFormOnly() && !EFormUtil.isLatestShowLatestFormOnlyPatientForm(eFormData.getId()))
			{
				continue;
			}

			EncounterSectionNote sectionNote = new EncounterSectionNote();

			String winName = eFormData.getFormName() + sectionParams.getDemographicNo();
			int hash = Math.abs(winName.hashCode());

			String url = sectionParams.getContextPath() + "/eform/efmshowform_data.jsp" +
					"?fdid=" + encodeUrlParam(eFormData.getId().toString()) +
					"&appointment=" + encodeUrlParam(sectionParams.getAppointmentNo()) +
					"&parentAjaxId=" + SECTION_ID;

			String onClickString = "popupPage( " +
					eformPopupHeight + ", " +
					eformPopupWidth + ", '" +
					hash + "', '" +
					url +"');";

			sectionNote.setOnClick(onClickString);

			String text = EncounterSectionService.getTrimmedText(eFormData.getFormName() + ": " + eFormData.getSubject());
			sectionNote.setText(text);

			sectionNote.setTitle(eFormData.getSubject());

			LocalDate date = ConversionUtils.toNullableLocalDate(eFormData.getFormDate());
			sectionNote.setUpdateDate(date.atStartOfDay());

			out.add(sectionNote);
		}

		return EncounterNotes.limitedEncounterNotes(out, offset, limit);
	}

	private String getEformPopupWidth(SectionParameters sectionParameters)
	{
		String loggedInProviderNo = sectionParameters.getLoggedInInfo().getLoggedInProviderNo();
		return eFormTemplateService.getEformPopupWidth(loggedInProviderNo).toString();

	}

	private String getEformPopupHeight(SectionParameters sectionParameters)
	{
		String loggedInProviderNo = sectionParameters.getLoggedInInfo().getLoggedInProviderNo();
		return eFormTemplateService.getEformPopupHeight(loggedInProviderNo).toString();
	}
}
