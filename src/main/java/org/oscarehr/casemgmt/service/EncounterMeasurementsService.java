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

import java.util.UUID;
import org.oscarehr.casemgmt.dto.EncounterNotes;
import org.oscarehr.casemgmt.dto.EncounterSectionMenuItem;
import org.oscarehr.casemgmt.dto.EncounterSectionNote;
import org.oscarehr.common.dao.AdmissionDao;
import org.oscarehr.common.dao.MeasurementGroupStyleDao;
import org.oscarehr.common.model.Admission;
import org.oscarehr.common.model.MeasurementGroupStyle;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.measurements.model.FlowSheetUserCreated;
import org.oscarehr.measurements.model.Flowsheet;
import org.oscarehr.measurements.service.FlowsheetService;
import org.oscarehr.util.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import oscar.OscarProperties;
import oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementsDataBean;
import oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementsDataBeanHandler;
import oscar.oscarEncounter.pageUtil.EctDisplayMeasurementsAction;
import oscar.oscarEncounter.pageUtil.NavBarDisplayDAO;
import oscar.oscarResearch.oscarDxResearch.bean.dxResearchBeanHandler;
import oscar.util.ConversionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import oscar.util.StringUtils;

public class EncounterMeasurementsService extends EncounterSectionService
{
	public static final String SECTION_ID = "measurements";
	private static final String SECTION_TITLE_KEY = "oscarEncounter.Index.measurements";
	private static final String SECTION_TITLE_COLOUR = "#344887";
	private static final String SECTION_MENU_HEADER_KEY = "oscarEncounter.LeftNavBar.InputGrps";

	@Autowired
	private SecurityInfoManager securityInfoManager;

	@Autowired
	private FlowsheetService flowsheetService;

	@Autowired
	MeasurementGroupStyleDao groupDao;

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
		return "";
	}

	@Override
	protected String getOnClickTitle(SectionParameters sectionParams)
	{
		String winName = "measurements" + sectionParams.getDemographicNo();
		String url = sectionParams.getContextPath() + "/oscarEncounter/oscarMeasurements/SetupHistoryIndex.do";
		return "popupPage(600,1000,'" + winName + "', '" + url + "');";
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
		List<MeasurementGroupStyle> groups = groupDao.findAll();

		List<EncounterSectionMenuItem> menuItems = new ArrayList<>();

		//now we grab measurement groups for popup menu
		for (int j = 0; j < groups.size(); j++)
		{

			MeasurementGroupStyle group = groups.get(j);
			String winName = group.getGroupName() + sectionParams.getDemographicNo();
			int hash = Math.abs(winName.hashCode());
			String url = sectionParams.getContextPath() + "/oscarEncounter/oscarMeasurements/SetupMeasurements.do" +
					"?groupName=" + encodeUrlParam(group.getGroupName()) +
					"&echartUUID=" + encodeUrlParam(sectionParams.geteChartUUID());

			addMenuItem(
					menuItems,
					group.getGroupName(),
					null,
					"popupPage(500,1000, '" + hash + "','" + url + "');junoEncounter.measurementLoaded('" + hash + "');"
			);
		}

		//if there are none, we tell user
		if (menuItems.size() == 0)
		{
			addMenuItem(
				menuItems,
				"None",
				null,
				""
			);
		}

		return menuItems;
	}

	public EncounterNotes getNotes(
			SectionParameters sectionParams, Integer limit,
			Integer offset
	)
	{
		List<EncounterSectionNote> out = new ArrayList<>();

		if (!securityInfoManager.hasPrivilege(sectionParams.getLoggedInInfo(),
				"_measurement", "r", null))
		{
			return EncounterNotes.noNotes();
		}


		// Add universal flowsheets
		List<String> flowsheets = flowsheetService.getUniversalFlowsheetNames();

		if (!OscarProperties.getInstance().getBooleanProperty("new_flowsheet_enabled", "true"))
		{
			flowsheets.remove("diab3");
		}

		out.addAll(createEncounterSectionNoteList(flowsheets, sectionParams));


		// Next we add dx triggered flowsheets to the module items
		dxResearchBeanHandler dxRes = new dxResearchBeanHandler(sectionParams.getDemographicNo());
		List<String> dxCodes = dxRes.getActiveCodeListWithCodingSystem();
		List<String> dxFlowsheets = flowsheetService.getFlowsheetNamesFromDxCodes(dxCodes);

		out.addAll(createEncounterSectionNoteList(dxFlowsheets, sectionParams));


		// Next we add program based flowsheets
		List<String> programs = new ArrayList<String>();
		AdmissionDao admissionDao = (AdmissionDao) SpringUtils.getBean("admissionDao");
		List<Admission> admissions = admissionDao.getCurrentAdmissions(
			Integer.parseInt(sectionParams.getDemographicNo()));
		for (Admission admission : admissions)
		{
			programs.add(String.valueOf(admission.getProgramId()));
		}
		List<String> programFlowsheets = flowsheetService.getFlowsheetNamesFromProgram(programs);

		out.addAll(createEncounterSectionNoteList(programFlowsheets, sectionParams));



		// Finally we add specific measurements to module item list
		Integer demo = Integer.valueOf(sectionParams.getDemographicNo());
		EctMeasurementsDataBeanHandler hd =
				new EctMeasurementsDataBeanHandler(demo);
		List<EctMeasurementsDataBean> measureTypes = hd.getMeasurementsData();


		List<EncounterSectionNote> measurementTypeNotes = new ArrayList<>();
		for(EctMeasurementsDataBean data:  measureTypes)
		{
			String title = data.getTypeDisplayName();
			String type = data.getType();


			hd = new EctMeasurementsDataBeanHandler(demo, data.getType());
			List<EctMeasurementsDataBean> measures = hd.getMeasurementsData();

			if (measures.size() > 0)
			{
				EncounterSectionNote sectionNote = new EncounterSectionNote();

				data = measures.get(0);
				LocalDateTime date = ConversionUtils.toLocalDateTime(data.getDateObservedAsDate());
				if (date == null)
				{
					date = ConversionUtils.toLocalDateTime(data.getDateEnteredAsDate());
				}
				sectionNote.setUpdateDate(date);

				title = EctDisplayMeasurementsAction.padd(title, data.getDataField());

				sectionNote.setText(title);

				sectionNote.setTitle(
						EncounterSectionService.formatTitleWithLocalDateTime(title + " " + data.getDataField(), date));

				sectionNote.setValue(data.getDataField());

				String winName = type + sectionParams.getDemographicNo();
				int hash = Math.abs(winName.hashCode());
				String url = sectionParams.getContextPath() + "/oscarEncounter/oscarMeasurements/SetupDisplayHistory.do" +
						"?type=" + encodeUrlParam(type);
				String onClickString = "popupPage(300,800,'" + hash + "','" + url + "');";
				sectionNote.setOnClick(onClickString);

				measurementTypeNotes.add(sectionNote);
			}
		}

		Collections.sort(measurementTypeNotes, new EncounterSectionNote.SortChronologicDescTextAsc());

		out.addAll(measurementTypeNotes);

		return EncounterNotes.limitedEncounterNotes(out, offset, limit);
	}

	private Flowsheet getFlowsheetFromSystemCache(String flowsheetName)
	{
		List<Flowsheet> systemFlowsheets = flowsheetService.getSystemFlowsheets();
		for (Flowsheet systemFlowsheet : systemFlowsheets)
		{
			if (systemFlowsheet.getName().equals(flowsheetName))
			{
				return systemFlowsheet;
			}
		}
		return null;
	}

	private Flowsheet getFlowsheetFromDatabaseCache(String flowsheetName)
	{
		List<Flowsheet> databaseFlowsheets = flowsheetService.getDatabaseFlowsheets();
		for (Flowsheet databaseFlowsheet : databaseFlowsheets)
		{
			if (databaseFlowsheet.getName().equals(flowsheetName))
			{
				return databaseFlowsheet;
			}
		}
		return null;
	}

	private FlowSheetUserCreated getFlowsheetFromUserCreatedCache(String flowsheetName)
	{
		List<FlowSheetUserCreated> userCreatedFlowsheets = flowsheetService.getUserCreatedFlowsheets();
		for (FlowSheetUserCreated userCreated : userCreatedFlowsheets)
		{
			if (userCreated.getName().equals(flowsheetName))
			{
				return userCreated;
			}
		}
		return null;
	}

	private List<EncounterSectionNote> createEncounterSectionNoteList(List<String> flowsheets, SectionParameters sectionParams)
	{
		String roleName$ = sectionParams.getRoleName();
		String demographicNo = sectionParams.getDemographicNo();
		String eChartUUID = sectionParams.geteChartUUID();
		String contextPath = sectionParams.getContextPath();

		com.quatro.service.security.SecurityManager securityMgr = new com.quatro.service.security.SecurityManager();
		List<EncounterSectionNote> encounterSectionNotes = new ArrayList<>();
		for (String flowsheetName : flowsheets)
		{
			EncounterSectionNote note = new EncounterSectionNote();
			if (securityMgr.hasReadAccess("_flowsheet." + flowsheetName, roleName$))
			{
				String displayName;
				Flowsheet fs = getFlowsheetFromSystemCache(flowsheetName);
				FlowSheetUserCreated userCreated = getFlowsheetFromUserCreatedCache(flowsheetName);
				if (fs == null)
				{
					fs = getFlowsheetFromDatabaseCache(flowsheetName);
				}

				if (fs != null && fs.isEnabled())
				{
					displayName = fs.getDisplayName();
				}
				else if (userCreated != null && !userCreated.getArchived())
				{
					displayName = userCreated.getDisplayName();
				}
				else
				{
					continue;
				}

				String winName = flowsheetName + demographicNo;
				note.setTitle(displayName);
				note.setText(getTrimmedText(displayName));

				String uuid = UUID.randomUUID().toString();
				int hash = Math.abs(winName.hashCode());
				String url = hash + "','" + contextPath + "/oscarEncounter/oscarMeasurements/TemplateFlowSheet.jsp" +
					"?uuid=" + uuid +
					"&demographic_no=" + demographicNo +
					"&template=" + flowsheetName +
					"&echartUUID=" + eChartUUID;

				String onClickString = "popupPage(700,1000,'" + url + "');";

				note.setOnClick(onClickString);

				encounterSectionNotes.add(note);
			}
		}
		return encounterSectionNotes;
	}
}
