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
import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.ticklers.entity.Tickler;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.managers.TicklerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oscar.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class EncounterTicklerService extends EncounterSectionService
{
	public static final String SECTION_ID = "tickler";
	protected static final String SECTION_TITLE_KEY = "global.viewTickler";
	protected static final String SECTION_TITLE_COLOUR = "#FF6600";

	private static final String BEFORE_COLOUR = "#FF0000";

	@Autowired
	protected SecurityInfoManager securityInfoManager;

	@Autowired
	TicklerManager ticklerManager;

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
		String pathedit = "";
		if( org.oscarehr.common.IsPropertiesOn.isTicklerPlusEnable() )
		{
			pathedit = sectionParams.getContextPath() + "/Tickler.do" +
					"?method=edit" +
					"&tickler.demographic_webName=" + encodeUrlParam(buildTicklerName(
					sectionParams.getPatientFirstName(), sectionParams.getPatientLastName())) +
					"&tickler.demographicNo=" + encodeUrlParam(sectionParams.getDemographicNo());
		}
		else
		{
			pathedit = sectionParams.getContextPath() + "/tickler/ticklerAdd.jsp" +
					"?demographic_no=" + sectionParams.getDemographicNo() +
					"&name=" + encodeUrlParam(buildTicklerName(sectionParams.getPatientFirstName(), sectionParams.getPatientLastName())) +
					"&chart_no=" + encodeUrlParam(((sectionParams.getChartNo() != null) ? sectionParams.getChartNo(): "")) +
					"&bFirstDisp=false" +
					"&doctor_no=" + encodeUrlParam(sectionParams.getFamilyDoctorNo()) + // despite the name, the bean loads it as demo.provider_no
					"&search_mode=search_name" +                     // This is required.  The default search mode may not be search name.  Since we forward the name, we want to search on that.
					"&orderby=last_name" +                           // Just to make sure that the order also isn't affected by a property override.
					"&originalpage=" + encodeUrlParam(sectionParams.getContextPath() + "/tickler/ticklerAdd.jsp") +
					"&parentAjaxId=" + SECTION_ID +
					"&updateParent=true";
		}

		//set right hand heading link
		String winName = "AddTickler" + sectionParams.getDemographicNo();
		return "popupPage(500,600,'" + winName + "','" + pathedit + "');";
	}

	@Override
	protected String getOnClickTitle(SectionParameters sectionParams)
	{
		String pathview = "";
		if(org.oscarehr.common.IsPropertiesOn.isTicklerPlusEnable())
		{
			pathview = sectionParams.getContextPath() + "/Tickler.do" +
					"?filter.demographic_webName="+ encodeUrlParam(buildTicklerName(sectionParams.getPatientFirstName(), sectionParams.getPatientLastName())) +
					"&filter.demographicNo=" + encodeUrlParam(sectionParams.getDemographicNo()) +
					"&filter.assignee=";
		}
		else
		{
			pathview = sectionParams.getContextPath() + "/tickler/ticklerMain.jsp" +
					"?demoview=" + encodeUrlParam(sectionParams.getDemographicNo()) +
					"&parentAjaxId=" + SECTION_ID;
		}

		String winName = "AddTickler" + sectionParams.getDemographicNo();
		return "popupPage(500,900,'" + winName + "','" + pathview + "');";
	}

	@Override
	public EncounterNotes getNotes(
			SectionParameters sectionParams, Integer limit,
			Integer offset
	)
	{
		List<EncounterSectionNote> notes = new ArrayList<>();

		if (!securityInfoManager.hasPrivilege(sectionParams.getLoggedInInfo(), "_tickler", "r", null))
		{
			return EncounterNotes.noNotes();
		}

		List<Tickler> ticklers = ticklerManager.findActiveByDemographicNo(
				sectionParams.getLoggedInInfo(),
				Integer.parseInt(sectionParams.getDemographicNo()),
				limit,
				offset,
				AbstractDao.SORT_ASC
		);

		for (Tickler t : ticklers)
		{
			EncounterSectionNote sectionNote = new EncounterSectionNote();

			// Date
			LocalDateTime serviceDate = t.getServiceDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
			sectionNote.setUpdateDate(serviceDate);

			// Colour
			if(serviceDate.isBefore(LocalDate.now().atStartOfDay()))
			{
				sectionNote.setColour(BEFORE_COLOUR);
			}

			// title
			sectionNote.setText(EncounterSectionService.getTrimmedText(t.getMessage()));

			// Link title
			sectionNote.setTitle(EncounterSectionService.formatTitleWithLocalDateTime(t.getMessage(), serviceDate));

			// onClick
			String winName = StringUtils.maxLenString(t.getMessage(), MAX_LEN_TITLE, MAX_LEN_TITLE, "");
			int hash = Math.abs(winName.hashCode());
			String url;
			if (org.oscarehr.common.IsPropertiesOn.isTicklerPlusEnable())
			{
				url = sectionParams.getContextPath() + "/Tickler.do?method=view&id=" + t.getId();
			}
			else
			{
				url = sectionParams.getContextPath() + "/tickler/ticklerMain.jsp" +
						"?demoview=" + encodeUrlParam(sectionParams.getDemographicNo()) +
						"&parentAjaxId=" + SECTION_ID;
			}
			String onClickNote = "popupPage(500,900,'" + hash + "','" + url + "');";

			sectionNote.setOnClick(onClickNote);

			notes.add(sectionNote);
		}

		int noteCount = ticklerManager.getActiveByDemographicNoCount(
				sectionParams.getLoggedInInfo(),
				Integer.parseInt(sectionParams.getDemographicNo())
		);

		return new EncounterNotes(notes, offset, limit, noteCount);
	}

	private String buildTicklerName(String patientFirstName, String patientLastName)
	{
		return patientLastName + "," + patientFirstName;
	}
}
