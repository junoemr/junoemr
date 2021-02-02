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
import org.oscarehr.casemgmt.dto.EncounterSectionMenuItem;
import org.oscarehr.casemgmt.dto.EncounterSectionNote;
import org.oscarehr.common.dao.OscarLogDao;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import oscar.OscarProperties;
import oscar.oscarEncounter.pageUtil.EctDisplayLabAction2;
import oscar.oscarLab.ca.all.web.LabDisplayHelper;
import oscar.oscarLab.ca.on.CommonLabResultData;
import oscar.oscarLab.ca.on.LabResultData;
import oscar.util.ConversionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

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

		if (OscarProperties.getInstance().getBooleanProperty("HL7TEXT_LABS", "yes"))
		{
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
		} else
		{
			String url = sectionParams.getContextPath() + "/lab/CumulativeLabValues2.jsp" +
					"?demographic_no=" + sectionParams.getDemographicNo();

			addMenuItem(
					menuItems, null,
					"oscarEncounter.LeftNavBar.LabMenuItem1",
					"popupPage(700,1000, '" + winName + "','" + url + "')"
			);
		}

		String url = sectionParams.getContextPath() + "/lab/CumulativeLabValues.jsp" +
				"?demographic_no=" + sectionParams.getDemographicNo();

		addMenuItem(
				menuItems, null,
				"oscarEncounter.LeftNavBar.LabMenuItem2",
				"popupPage(700,1000, '" + winName + "','" + url + "')"
		);

		return menuItems;
	}

	public EncounterNotes getNotes(
			SectionParameters sectionParams, Integer limit,
			Integer offset
	)
	{
		List<EncounterSectionNote> out = new ArrayList<>();

		if(!securityInfoManager.hasPrivilege(sectionParams.getLoggedInInfo(), "_lab",
				"r", null))
		{
			return EncounterNotes.noNotes();
		}


		LinkedHashMap<String,LabResultData> accessionMap = new LinkedHashMap<String,LabResultData>();
		CommonLabResultData comLab = new CommonLabResultData();
		ArrayList<LabResultData> labs = comLab.populateLabResultsData(sectionParams.getLoggedInInfo(),
				"", sectionParams.getDemographicNo(), "", "",
				"", "U");

		Collections.sort(labs);

		for (int i = 0; i < labs.size(); i++)
		{
			LabResultData result = labs.get(i);
			if (result.accessionNumber == null || result.accessionNumber.equals(""))
			{
				accessionMap.put("noAccessionNum" + i + result.labType, result);
			}
			else
			{
				if (!accessionMap.containsKey(result.accessionNumber + result.labType))
				{
					accessionMap.put(result.accessionNumber + result.labType, result);
				}
			}
		}

		labs = new ArrayList<LabResultData>(accessionMap.values());

		for (int j = 0; j < labs.size(); j++)
		{
			EncounterSectionNote sectionNote = new EncounterSectionNote();

			LabResultData result = labs.get(j);

			// Date
			Date date = EctDisplayLabAction2.getServiceDate(sectionParams.getLoggedInInfo(), result);
			LocalDateTime serviceDate = ConversionUtils.toLocalDateTime(date);
			sectionNote.setUpdateDate(serviceDate);

			// Colour
			if (result.isAbnormal())
			{
				sectionNote.setColour("red");
			}

			// Title
			String label = result.getLabel();
			String labDisplayName;
			if (label == null || label.equals(""))
			{
				labDisplayName = result.getDiscipline();
			}
			else
			{
				labDisplayName = label;
			}

			// Put stars around a lab if it is not read
			String labRead = "";
			if (!oscarLogDao.hasRead(sectionParams.getProviderNo(), "lab", result.segmentID))
			{
				labRead = "*";
			}

			if (labDisplayName == null)
			{
				labDisplayName = "";
			}

			sectionNote.setText(EncounterSectionService.getTrimmedText(labRead + labDisplayName + labRead));

			sectionNote.setTitle(EncounterSectionService.formatTitleWithLocalDateTime(labDisplayName, serviceDate));

			// Link onClick
			String remoteFacilityIdQueryString = "";
			if (result.getRemoteFacilityId() != null)
			{
				try
				{
					remoteFacilityIdQueryString = "&remoteFacilityId=" +
							encodeUrlParam(result.getRemoteFacilityId().toString());

					String remoteLabKey = LabDisplayHelper.makeLabKey(
							Integer.parseInt(result.getLabPatientId()), result.getSegmentID(),
							result.labType, result.getDateTime());

					remoteFacilityIdQueryString = remoteFacilityIdQueryString +
							"&remoteLabKey=" + encodeUrlParam(remoteLabKey);
				}
				catch (Exception e)
				{
					MiscUtils.getLogger().error("Error", e);
				}
			}

			String url;
			if ( result.isMDS() )
			{
				url = sectionParams.getContextPath() + "/oscarMDS/SegmentDisplay.jsp" +
						"?demographicId=" + encodeUrlParam(sectionParams.getDemographicNo())+
						"&providerNo=" + encodeUrlParam(sectionParams.getProviderNo()) +
						"&segmentID=" + encodeUrlParam(result.segmentID) +
						"&multiID=" + encodeUrlParam(result.multiLabId) +
						"&status=" + encodeUrlParam(result.getReportStatus()) +
						remoteFacilityIdQueryString;
			}
			else if (result.isCML())
			{
				url = sectionParams.getContextPath() + "/lab/CA/ON/CMLDisplay.jsp" +
						"?demographicId=" + encodeUrlParam(sectionParams.getDemographicNo())+
						"&providerNo=" + encodeUrlParam(sectionParams.getProviderNo()) +
						"&segmentID="+ encodeUrlParam(result.segmentID) +
						"&multiID=" + encodeUrlParam(result.multiLabId) +
						remoteFacilityIdQueryString;
			}
			else if (result.isHL7TEXT())
			{
				url = sectionParams.getContextPath() + "/lab/CA/ALL/labDisplay.jsp" +
						"?demographicId=" + encodeUrlParam(sectionParams.getDemographicNo())+
						"&providerNo=" + encodeUrlParam(sectionParams.getProviderNo()) +
						"&segmentID=" + encodeUrlParam(result.segmentID) +
						"&multiID=" + encodeUrlParam(result.multiLabId) +
						remoteFacilityIdQueryString;
			}
			else
			{
				url = sectionParams.getContextPath() + "/lab/CA/BC/labDisplay.jsp" +
						"?demographicId=" + encodeUrlParam(sectionParams.getDemographicNo())+
						"&segmentID=" + encodeUrlParam(result.segmentID) +
						"&providerNo=" + encodeUrlParam(sectionParams.getProviderNo()) +
						"&multiID=" + encodeUrlParam(result.multiLabId) +
						remoteFacilityIdQueryString;
			}

			String winName = "AllLabs" + sectionParams.getDemographicNo();
			int hash = winName.hashCode();
			hash = hash < 0 ? hash * -1 : hash;

			sectionNote.setOnClick("popupPage(700,960,'" + hash + "', '" + url + "');");

			out.add(sectionNote);
		}

		return EncounterNotes.limitedEncounterNotes(out, offset, limit);
	}
}
