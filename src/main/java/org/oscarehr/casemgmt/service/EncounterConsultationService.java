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
import org.oscarehr.common.dao.UserPropertyDAO;
import org.oscarehr.common.model.UserProperty;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import oscar.util.ConversionUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EncounterConsultationService extends EncounterSectionService
{
	public static final String SECTION_ID = "consultation";
	private static final String SECTION_TITLE_KEY = "oscarEncounter.LeftNavBar.Consult";
	private static final String SECTION_TITLE_COLOUR = "#6C2DC7";

	@Autowired
	private SecurityInfoManager securityInfoManager;

	@Autowired
	private UserPropertyDAO userPropertyDAO;

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
		String winName = "newConsult" + sectionParams.getDemographicNo();
		String url = sectionParams.getContextPath() + "/oscarEncounter/oscarConsultationRequest/ConsultationFormRequest.jsp" +
				"?de=" + encodeUrlParam(sectionParams.getDemographicNo()) +
				"&teamVar=" +
				"&appNo=" + encodeUrlParam(sectionParams.getAppointmentNo());
		return "popupPage(700,960,'" + winName + "','" + url + "');";
	}

	@Override
	protected String getOnClickTitle(SectionParameters sectionParams)
	{
		String winName = "Consultation" + sectionParams.getDemographicNo();
		String url = sectionParams.getContextPath() + "/oscarEncounter/oscarConsultationRequest/DisplayDemographicConsultationRequests.jsp" +
				"?de=" + encodeUrlParam(sectionParams.getDemographicNo());
		return "popupPage(700,960,'" + winName + "', '" + url + "')";
	}

	public EncounterNotes getNotes(
			SectionParameters sectionParams, Integer limit,
			Integer offset
	)
	{
		List<EncounterSectionNote> out = new ArrayList<>();

		if(!securityInfoManager.hasPrivilege(sectionParams.getLoggedInInfo(), "_con", "r", null))
		{
			return EncounterNotes.noNotes();
		}

		//grab all consultations for patient and add list item for each
		oscar.oscarEncounter.oscarConsultationRequest.pageUtil.EctViewConsultationRequestsUtil theRequests;
		theRequests = new  oscar.oscarEncounter.oscarConsultationRequest.pageUtil.EctViewConsultationRequestsUtil();
		theRequests.estConsultationVecByDemographic(sectionParams.getLoggedInInfo(), sectionParams.getDemographicNo());

		//determine cut off period for highlighting
		UserProperty up = userPropertyDAO.getProp(sectionParams.getProviderNo(), UserProperty.CONSULTATION_TIME_PERIOD_WARNING);
		String timeperiod = null;

		if ( up != null && up.getValue() != null && !up.getValue().trim().equals(""))
		{
			timeperiod = up.getValue();
		}

		Calendar cal = Calendar.getInstance();
		int countback = -1;
		if (timeperiod != null)
		{
			countback = Integer.parseInt(timeperiod);
			countback = countback * -1;
		}
		cal.add(Calendar.MONTH, countback);
		LocalDateTime cutoffDate = ConversionUtils.toLocalDateTime(cal.getTime());

		for (int idx = theRequests.ids.size() - 1; idx >= 0; --idx )
		{
			EncounterSectionNote sectionNote = new EncounterSectionNote();

			String service = theRequests.service.get(idx);
			String dateStr = theRequests.date.get(idx);
			String status = theRequests.status.get(idx);

			LocalDateTime date;
			try
			{
				date = ConversionUtils.toNullableLocalDate(dateStr).atStartOfDay();

				//if we are after cut off date and not completed set to red
				if( date.isBefore(cutoffDate) && !status.equals("4") )
				{
					sectionNote.setColour(COLOUR_RED);
				}
			}
			catch(DateTimeParseException ex)
			{
				MiscUtils.getLogger().debug("Error creating date " + ex.getMessage(), ex);

				date = null;
			}

			String winName = "newConsult" + sectionParams.getDemographicNo();
			String url = sectionParams.getContextPath() + "/oscarEncounter/ViewRequest.do" +
					"?de=" + encodeUrlParam(sectionParams.getDemographicNo()) +
					"&requestId=" + encodeUrlParam(theRequests.ids.get(idx));
			String onClickString = "popupPage(700,960,'" + winName + "','" + url + "');";
			sectionNote.setOnClick(onClickString);

			sectionNote.setText(EncounterSectionService.getTrimmedText(service));
			sectionNote.setUpdateDate(date);
			sectionNote.setTitle(EncounterSectionService.formatTitleWithLocalDateTime(service, date));

			out.add(sectionNote);
		}

		return EncounterNotes.limitedEncounterNotes(out, offset, limit);
	}
}
