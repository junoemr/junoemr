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

import java.util.ArrayList;
import java.util.List;
import org.oscarehr.casemgmt.dto.EncounterNotes;
import org.oscarehr.casemgmt.dto.EncounterSectionMenuItem;
import org.oscarehr.casemgmt.dto.EncounterSectionNote;
import org.oscarehr.common.dao.Hl7TextInfoDao;
import org.oscarehr.common.dao.OscarLogDao;
import org.oscarehr.labs.transfer.BasicLabInfo;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.security.model.Permission;
import org.oscarehr.util.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import oscar.OscarProperties;

public class EncounterLabResultService extends EncounterSectionService
{
	public static final String SECTION_ID = "labs";
	protected static final String SECTION_TITLE_KEY = "oscarEncounter.LeftNavBar.Labs";
	protected static final String SECTION_TITLE_COLOUR = "#A0509C";
	protected static final String SECTION_MENU_HEADER_KEY = "oscarEncounter.LeftNavBar.LabMenuHeading";

	@Autowired
	private SecurityInfoManager securityInfoManager;

	@Autowired
	private OscarLogDao oscarLogDao;

	@Override
	public String getSectionId()
	{
		return SECTION_ID;
	}

	@Override
	protected String getSectionTitle()
	{
		return SECTION_TITLE_KEY;
	}

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
		// No link, just a menu
		return "";
	}

	@Override
	protected String getOnClickTitle(SectionParameters sectionParams)
	{
		String winName = "Labs" + sectionParams.getDemographicNo();
		String url = sectionParams.getContextPath() + "/lab/DemographicLab.jsp" +
				"?demographicNo=" + sectionParams.getDemographicNo();

		return "popupPage(700,599,'" + winName + "', '" + url + "')";
	}

	@Override
	protected String getMenuId()
	{
		return SECTION_ID;
	}

	@Override
	protected String getMenuHeaderKey()
	{
		return SECTION_MENU_HEADER_KEY;
	}

	@Override
	protected List<EncounterSectionMenuItem> getMenuItems(SectionParameters sectionParams)
	{
		// we're going to display popup menu of 2 selections - row display and grid display


		List<EncounterSectionMenuItem> menuItems = new ArrayList<>();

		String winName = "AllLabs" + sectionParams.getDemographicNo();

		String url = sectionParams.getContextPath() + "/lab/CumulativeLabValues3.jsp" +
				"?demographic_no=" + encodeUrlParam(sectionParams.getDemographicNo());

		addMenuItem(
			menuItems, null,
			"oscarEncounter.LeftNavBar.LabMenuItem1",
			"popupPage(700,1000, '" + winName + "','" + url + "')"
		);

		if (OscarProperties.getInstance().getProperty("labs.hide_old_grid_display",
				"false").equals("false"))
		{
			String gridUrl = sectionParams.getContextPath() + "/lab/CumulativeLabValues2.jsp" +
					"?demographic_no=" + sectionParams.getDemographicNo();

			addMenuItem(
					menuItems, null,
					"oscarEncounter.LeftNavBar.LabMenuItem1old",
					"popupPage(700,1000, '" + winName + "','" + gridUrl + "')"
			);
		}

		url = sectionParams.getContextPath() + "/lab/CumulativeLabValues.jsp" +
				"?demographic_no=" + sectionParams.getDemographicNo();

		addMenuItem(
				menuItems, null,
				"oscarEncounter.LeftNavBar.LabMenuItem2",
				"popupPage(700,1000, '" + winName + "','" + url + "')"
		);

		return menuItems;
	}

	public EncounterNotes getNotes(SectionParameters sectionParams, Integer limit, Integer offset)
	{
		List<EncounterSectionNote> out = new ArrayList<>();

		try
		{
			securityInfoManager.requireAllPrivilege(sectionParams.getLoggedInInfo().getLoggedInProviderNo(), Permission.LAB_READ);
		}
		catch (SecurityException e)
		{
			return EncounterNotes.noNotes();
		}

		Hl7TextInfoDao hl7TextInfoDao = SpringUtils.getBean(Hl7TextInfoDao.class);
		List<BasicLabInfo> basicLabInfos = hl7TextInfoDao.listBasicInfoByDemographicNo(
			sectionParams.getDemographicNo(), offset, limit);

		for (BasicLabInfo basicLabInfo : basicLabInfos)
		{
			EncounterSectionNote sectionNote = new EncounterSectionNote();
			sectionNote.setUpdateDate(basicLabInfo.getObservationDateTime());
			if (basicLabInfo.getAbnormal())
			{
				sectionNote.setColour("red");
			}

			String segmentID = Integer.toString(basicLabInfo.getLabId());
			String labLabel = basicLabInfo.getLabel();
			if (!oscarLogDao.hasRead(sectionParams.getProviderNo(), "lab", segmentID))
			{
				labLabel = "*" + labLabel + "*";
			}

			sectionNote.setText(EncounterSectionService.getTrimmedText(labLabel));
			sectionNote.setTitle(
				EncounterSectionService.formatTitleWithLocalDateTime(labLabel,
					basicLabInfo.getObservationDateTime()));

			String url = getLabDisplayUrl(basicLabInfo, sectionParams);

			// Force popup window name based on demographic, so the window is reused when viewing multiple labs
			int windowName = Math.abs(("AllLabs" + sectionParams.getDemographicNo()).hashCode());
			sectionNote.setOnClick("popupPage(700,960,'" + windowName + "', '" + url + "');");
			out.add(sectionNote);
		}

		EncounterNotes notes = new EncounterNotes();
		notes.setOffset(offset);
		notes.setLimit(limit);
		notes.setNoteCount(hl7TextInfoDao.countByDemographicNo(sectionParams.getDemographicNo()));
		notes.setEncounterSectionNotes(out);
		return notes;
	}

	private String getLabDisplayUrl(BasicLabInfo basicLabInfo, SectionParameters sectionParameters)
	{
		String url = sectionParameters.getContextPath() + "/lab/CA/ALL/labDisplay.jsp";
		url = url + "?demographicId=" + encodeUrlParam(sectionParameters.getDemographicNo())+
			"&providerNo=" + encodeUrlParam(sectionParameters.getProviderNo()) +
			"&segmentID=" + encodeUrlParam(Integer.toString(basicLabInfo.getLabId())) +
			"&status=" + encodeUrlParam(basicLabInfo.getReportStatus());

		return url;
	}
}
