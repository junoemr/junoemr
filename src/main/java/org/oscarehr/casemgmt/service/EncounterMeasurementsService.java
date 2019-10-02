/*
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
import org.oscarehr.common.dao.FlowsheetDao;
import org.oscarehr.common.model.Flowsheet;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.springframework.beans.factory.annotation.Autowired;
import oscar.OscarProperties;
import oscar.oscarEncounter.oscarMeasurements.MeasurementTemplateFlowSheetConfig;
import oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementsDataBean;
import oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementsDataBeanHandler;
import oscar.oscarEncounter.pageUtil.EctDisplayMeasurementsAction;
import oscar.oscarEncounter.pageUtil.NavBarDisplayDAO;
import oscar.oscarResearch.oscarDxResearch.bean.dxResearchBeanHandler;
import oscar.util.ConversionUtils;
import oscar.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class EncounterMeasurementsService extends EncounterSectionService
{
	@Autowired
	private SecurityInfoManager securityInfoManager;

	@Autowired
	FlowsheetDao flowsheetDao;

	public EncounterNotes getNotes(
			LoggedInInfo loggedInInfo,
			String roleName,
			String providerNo,
			String demographicNo,
			String appointmentNo,
			String programId,
			Integer limit,
			Integer offset
	)
	{
		List<EncounterSectionNote> out = new ArrayList<>();

		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_measurement", "r", null))
		{
			return EncounterNotes.noNotes();
		}

		//String menuId = "3"; //div id for popup menu
		//String roleName = request.getSession().getAttribute("userrole") + "," + request.getSession().getAttribute("user");
		//String uuid="";
		//String eChartUUID = request.getParameter("eChartUUID");

		////set text for lefthand module title
		//Dao.setLeftHeading(messages.getMessage(request.getLocale(), "oscarEncounter.Index.measurements"));

		////set link for lefthand module title
		//String winName = "measurements" + bean.demographicNo;
		//String url = "popupPage(600,1000,'" + winName + "','" + request.getContextPath() + "/oscarEncounter/oscarMeasurements/SetupHistoryIndex.do')";
		//Dao.setLeftURL(url.toString());

		////we're going to display a pop up menu of measurement groups
		//Dao.setRightHeadingID(menuId);
		//Dao.setMenuHeader(messages.getMessage("oscarEncounter.LeftNavBar.InputGrps"));
		//Dao.setRightURL("return !showMenu('" + menuId + "', event);");

		com.quatro.service.security.SecurityManager securityMgr = new com.quatro.service.security.SecurityManager();

		ArrayList<String> flowsheets = MeasurementTemplateFlowSheetConfig.getInstance().getUniveralFlowsheets();

		if (!OscarProperties.getInstance().getBooleanProperty("new_flowsheet_enabled", "true"))
		{
			flowsheets.remove("diab3");
		}

		//int hash;
		//for (int f = 0; f < flowsheets.size(); f++)
		for (String flowsheetName: flowsheets)
		{
			EncounterSectionNote sectionNote = new EncounterSectionNote();

			//NavBarDisplayDAO.Item item = NavBarDisplayDAO.Item();
			//String flowsheetName = flowsheets.get(f);

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

				String dispname = MeasurementTemplateFlowSheetConfig.getInstance().getDisplayName(flowsheetName);

				//winName = flowsheetName + bean.demographicNo;
				//uuid = UUID.randomUUID().toString();
				//hash = Math.abs(winName.hashCode());
				//url = "popupPage(700,1000,'" + hash + "','" + request.getContextPath() + "/oscarEncounter/oscarMeasurements/TemplateFlowSheet.jsp?uuid=" + uuid + "&demographic_no="
				//		+ bean.demographicNo + "&template=" + flowsheetName + "&echartUUID="+ eChartUUID + "');return false;";
				//item.setLinkTitle(dispname);
				dispname = StringUtils.maxLenString(dispname, MAX_LEN_TITLE, CROP_LEN_TITLE, ELLIPSES);
				sectionNote.setText(dispname);

				//item.setTitle(dispname);
				//item.setURL(url);

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
		dxResearchBeanHandler dxRes = new dxResearchBeanHandler(demographicNo);
		Vector dxCodes = dxRes.getActiveCodeListWithCodingSystem();
		flowsheets = MeasurementTemplateFlowSheetConfig.getInstance().getFlowsheetsFromDxCodes(dxCodes);
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

		/*
		MeasurementGroupStyleDao groupDao = SpringUtils.getBean(MeasurementGroupStyleDao.class);
		List<MeasurementGroupStyle> groups = groupDao.findAll();
		//now we grab measurement groups for popup menu
		for (int j = 0; j < groups.size(); j++)
		{

			MeasurementGroupStyle group = groups.get(j);
			winName = group.getGroupName() + bean.demographicNo;
			hash = Math.abs(winName.hashCode());
			url = "popupPage(500,1000,'" + hash + "','" + request.getContextPath() + "/oscarEncounter/oscarMeasurements/SetupMeasurements.do?groupName=" + group.getGroupName() +
					"&echartUUID=" + eChartUUID + "');measurementLoaded('" + hash + "')";
			Dao.addPopUpUrl(url);
			Dao.addPopUpText(group.getGroupName());
		}

		//if there are none, we tell user
		if (bean.measurementGroupNames.size() == 0) {
			Dao.addPopUpUrl("");
			Dao.addPopUpText("None");
		}
		 */

		//finally we add specific measurements to module item list
		Integer demo = Integer.valueOf(demographicNo);
		oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementsDataBeanHandler hd =
				new oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementsDataBeanHandler(demo);
		List<EctMeasurementsDataBean> measureTypes = hd.getMeasurementsData();

		/*
		if (loggedInInfo.getCurrentFacility().isIntegratorEnabled())
		{
			EctMeasurementsDataBeanHandler.addRemoteMeasurementsTypes(loggedInInfo,measureTypes,demo);
		}
		 */

		//oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementsDataBean data;
		//for (int idx = 0; idx < measureTypes.size(); ++idx)
		for(EctMeasurementsDataBean data:  measureTypes)
		{
			//data = (oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementsDataBean) measureTypes.get(idx);
			String title = data.getTypeDisplayName();
			String type = data.getType();

			//winName = type + bean.demographicNo;
			//hash = Math.abs(winName.hashCode());

			hd = new EctMeasurementsDataBeanHandler(demo, data.getType());
			List<EctMeasurementsDataBean> measures = hd.getMeasurementsData();

			/*
			if (loggedInInfo.getCurrentFacility().isIntegratorEnabled())
			{
				EctMeasurementsDataBeanHandler.addRemoteMeasurements(loggedInInfo,measures,data.getType(),demo);
			}
			 */

			if (measures.size() > 0)
			{
				EncounterSectionNote sectionNote = new EncounterSectionNote();

				//NavBarDisplayDAO.Item item = NavBarDisplayDAO.Item();
				data = measures.get(0);
				Date date = data.getDateObservedAsDate();
				if (date == null)
				{
					date = data.getDateEnteredAsDate();
				}
				sectionNote.setUpdateDate(ConversionUtils.toNullableLocalDate(date).atStartOfDay());

				//Not sure what the standard should be for showing remote data in the left and right hand sides but im not sure this looks right.
				//if(data.getRemoteFacility() != null){
				//	item.setBgColour("#ffcccc");
				//}

				//String formattedDate = DateUtils.formatDate(date, request.getLocale());

				//item.setLinkTitle(title + " " + data.getDataField() + " " + formattedDate);
				title = EctDisplayMeasurementsAction.padd(title, data.getDataField());
				//String tmp = "<span class=\"measureCol1\">" + title + "</span>";

				sectionNote.setText(title);

				sectionNote.setValue(data.getDataField());

				//tmp += "<span class=\"measureCol2\">" + data.getDataField() + "&nbsp;</span>";
				//item.setValue(data.getDataField());
				//tmp += "<span class=\"measureCol3\">" + formattedDate + "</span><br style=\"clear:both\">";

				//item.setTitle(tmp);
				//item.setDate(date);

				//item.setURL("popupPage(300,800,'" + hash + "','" + request.getContextPath() + "/oscarEncounter/oscarMeasurements/SetupDisplayHistory.do?type=" + type + "'); return false;");
				//Dao.addItem(item);


				out.add(sectionNote);
			}
		}

		//Dao.sortItems(NavBarDisplayDAO.DATESORT_ASC);
		//Collections.sort(out, new EncounterSectionNote.SortAlphabetic());

		return EncounterNotes.limitedEncounterNotes(out, offset, limit);
	}
}
