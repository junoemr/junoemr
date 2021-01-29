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

import com.quatro.service.security.SecurityManager;
import org.oscarehr.casemgmt.dto.EncounterNotes;
import org.oscarehr.casemgmt.dto.EncounterSectionMenuItem;
import org.oscarehr.casemgmt.dto.EncounterSectionNote;
import org.oscarehr.common.dao.FlowsheetDao;
import org.oscarehr.common.dao.MeasurementGroupStyleDao;
import org.oscarehr.common.model.Flowsheet;
import org.oscarehr.common.model.MeasurementGroupStyle;
import org.oscarehr.managers.SecurityInfoManager;
import org.springframework.beans.factory.annotation.Autowired;
import oscar.OscarProperties;
import oscar.oscarEncounter.oscarMeasurements.MeasurementTemplateFlowSheetConfig;
import oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementsDataBean;
import oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementsDataBeanHandler;
import oscar.oscarEncounter.pageUtil.EctDisplayMeasurementsAction;
import oscar.oscarResearch.oscarDxResearch.bean.dxResearchBeanHandler;
import oscar.util.ConversionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

public class EncounterMeasurementsService extends EncounterSectionService
{
	public static final String SECTION_ID = "measurements";
	private static final String SECTION_TITLE_KEY = "oscarEncounter.Index.measurements";
	private static final String SECTION_TITLE_COLOUR = "#344887";
	private static final String SECTION_MENU_HEADER_KEY = "oscarEncounter.LeftNavBar.InputGrps";

	@Autowired
	private SecurityInfoManager securityInfoManager;

	@Autowired
	FlowsheetDao flowsheetDao;

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

		SecurityManager securityMgr = new SecurityManager();

		ArrayList<String> flowsheets = MeasurementTemplateFlowSheetConfig.getInstance().getUniveralFlowsheets();

		if (!OscarProperties.getInstance().getBooleanProperty("new_flowsheet_enabled", "true"))
		{
			flowsheets.remove("diab3");
		}

		for (String flowsheetName: flowsheets)
		{
			EncounterSectionNote sectionNote = new EncounterSectionNote();

			if (securityMgr.hasReadAccess("_flowsheet." + flowsheetName, sectionParams.getRoleName()))
			{
				Flowsheet fs = null;
				if ((fs = flowsheetDao.findByName(flowsheetName)) != null)
				{
					if (!fs.isEnabled())
					{
						continue;
					}
				}

				String fullDisplayName = MeasurementTemplateFlowSheetConfig.getInstance().getDisplayName(flowsheetName);

				String winName = flowsheetName + sectionParams.getDemographicNo();
				String uuid = UUID.randomUUID().toString();
				int hash = Math.abs(winName.hashCode());
				String url = sectionParams.getContextPath() + "/oscarEncounter/oscarMeasurements/TemplateFlowSheet.jsp" +
						"?uuid=" + uuid +
						"&demographic_no=" + encodeUrlParam(sectionParams.getDemographicNo())+
						"&template=" + encodeUrlParam(flowsheetName) +
						"&echartUUID=" + encodeUrlParam(sectionParams.geteChartUUID());
				String onClickString = "popupPage(700,1000,'" + hash + "','" + url + "');";
				sectionNote.setOnClick(onClickString);

				String displayName = EncounterSectionService.getTrimmedText(fullDisplayName);
				sectionNote.setText(displayName);

				sectionNote.setTitle(fullDisplayName);

				out.add(sectionNote);
			}
		}

		/*
		XXX: do we plan to use the health tracker?
		if(OscarProperties.getInstance().getBooleanProperty("health_tracker", "true"))
		{
			NavBarDisplayDAO.Item item = NavBarDisplayDAO.Item();
			//temp while testing
			String dispname = "Health Tracker";

			winName = "viewTracker" + bean.demographicNo;
			hash = Math.abs(winName.hashCode());
			url = "window.open('" + request.getContextPath() + "/oscarEncounter/oscarMeasurements/HealthTrackerPage.jspf?demographic_no=" + bean.demographicNo + "&template=tracker'," + hash + ",'height=' + screen.height + ',width=' + screen.width +',resizable=yes,scrollbars=yes, fullscreen=yes');return false;";
			item.setLinkTitle(dispname);
			dispname = StringUtils.maxLenString(dispname, MAX_LEN_TITLE, CROP_LEN_TITLE, ELLIPSES);
			item.setTitle(dispname);
			item.setURL(url);
			Dao.addItem(item);
		}
		*/

		//next we add dx triggered flowsheets to the module items
		dxResearchBeanHandler dxRes = new dxResearchBeanHandler(sectionParams.getDemographicNo());
		Vector dxCodes = dxRes.getActiveCodeListWithCodingSystem();
		flowsheets = MeasurementTemplateFlowSheetConfig.getInstance().getFlowsheetsFromDxCodes(dxCodes);
		for (int f = 0; f < flowsheets.size(); f++)
		{
			String flowsheetName = flowsheets.get(f);
			if (securityMgr.hasReadAccess("_flowsheet." + flowsheetName, sectionParams.getRoleName()))
			{
				Flowsheet fs = null;
				if ((fs = flowsheetDao.findByName(flowsheetName)) != null)
				{
					if (!fs.isEnabled())
					{
						continue;
					}
				}

				EncounterSectionNote sectionNote = new EncounterSectionNote();

				String winName = flowsheetName + sectionParams.getDemographicNo();
				String uuid = UUID.randomUUID().toString();
				int hash = Math.abs(winName.hashCode());
				String url = sectionParams.getContextPath() + "/oscarEncounter/oscarMeasurements/TemplateFlowSheet.jsp" +
						"?uuid=" + uuid +
						"&demographic_no=" + encodeUrlParam(sectionParams.getDemographicNo()) +
						"&template=" + encodeUrlParam(flowsheetName) +
						"&echartUUID=" + encodeUrlParam(sectionParams.geteChartUUID());

				String onClickString = "popupPage(700,1000,'" + hash + "','" + url + "');";
				sectionNote.setOnClick(onClickString);

				String dispname = MeasurementTemplateFlowSheetConfig.getInstance().getDisplayName(flowsheetName);
				sectionNote.setText(EncounterSectionService.getTrimmedText(dispname));
				sectionNote.setTitle(dispname);
				out.add(sectionNote);
			}
		}


		/*
		XXX: pretty sure this is only used for CAISI
		//next we add program based flowsheets
		List<String> programs = new ArrayList<String>();
		AdmissionDao admissionDao = (AdmissionDao) SpringUtils.getBean("admissionDao");
		List<Admission> admissions = admissionDao.getCurrentAdmissions(Integer.parseInt(demographicNo));
		for (Admission admission : admissions)
		{
			programs.add(String.valueOf(admission.getProgramId()));
		}
		flowsheets = MeasurementTemplateFlowSheetConfig.getInstance().getFlowsheetsFromPrograms(programs);
		for (int f = 0; f < flowsheets.size(); f++)
		{
			NavBarDisplayDAO.Item item = NavBarDisplayDAO.Item();
			String flowsheetName = flowsheets.get(f);
			if (securityMgr.hasReadAccess("_flowsheet." + flowsheetName, roleName))
			{
				Flowsheet fs = null;
				if ((fs = flowsheetDao.findByName(flowsheetName)) != null)
				{
					if (!fs.isEnabled())
					{
						continue;
					}
				}

				EncounterSectionNote sectionNote = new EncounterSectionNote();

				String dispname = MeasurementTemplateFlowSheetConfig.getInstance().getDisplayName(flowsheetName);

				//winName = flowsheetName + bean.demographicNo;
				//uuid = UUID.randomUUID().toString();
				//hash = Math.abs(winName.hashCode());
				//url = "popupPage(700,1000,'" + hash + "','" + request.getContextPath() + "/oscarEncounter/oscarMeasurements/TemplateFlowSheet.jsp?uuid=" + uuid +
				//		"&demographic_no=" + bean.demographicNo + "&template=" + flowsheetName + "&echartUUID=" + eChartUUID + "');return false;";
				//item.setLinkTitle(dispname);
				dispname = StringUtils.maxLenString(dispname, MAX_LEN_TITLE, CROP_LEN_TITLE, ELLIPSES);

				sectionNote.setText(dispname);
				out.add(sectionNote);

				//item.setTitle(dispname);
				//item.setURL(url);
				//Dao.addItem(item);
			}
		}
		 */

		//finally we add specific measurements to module item list
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
}
