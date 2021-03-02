/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */

package oscar.oscarEncounter.pageUtil;

import org.apache.struts.util.MessageResources;
import org.oscarehr.common.dao.AdmissionDao;
import org.oscarehr.common.dao.MeasurementGroupStyleDao;
import org.oscarehr.common.model.Admission;
import org.oscarehr.measurements.model.FlowSheetUserCreated;
import org.oscarehr.measurements.model.Flowsheet;
import org.oscarehr.common.model.MeasurementGroupStyle;
import org.oscarehr.measurements.service.FlowsheetService;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;
import oscar.OscarProperties;
import oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementsDataBean;
import oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementsDataBeanHandler;
import oscar.oscarResearch.oscarDxResearch.bean.dxResearchBeanHandler;
import oscar.util.DateUtils;
import oscar.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;



public class EctDisplayMeasurementsAction extends EctDisplayAction {
	private static final String cmd = "measurements";
	private FlowsheetService flowsheetService = SpringUtils.getBean(FlowsheetService.class);

	public boolean getInfo(EctSessionBean bean, HttpServletRequest request, NavBarDisplayDAO Dao, MessageResources messages) {

		LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);
		
		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_measurement", "r", null)) {
			return true; //Messurement link won't show up on new CME screen.
		} else {

			String menuId = "3"; //div id for popup menu
			String roleName$ = request.getSession().getAttribute("userrole") + "," + request.getSession().getAttribute("user");
			String eChartUUID = request.getParameter("eChartUUID");

			//set text for lefthand module title
			Dao.setLeftHeading(messages.getMessage(request.getLocale(), "oscarEncounter.Index.measurements"));

			//set link for lefthand module title
			String winName = "measurements" + bean.demographicNo;
			String url = "popupPage(600,1000,'" + winName + "','" + request.getContextPath() + "/oscarEncounter/oscarMeasurements/SetupHistoryIndex.do')";
			Dao.setLeftURL(url.toString());

			//we're going to display a pop up menu of measurement groups
			Dao.setRightHeadingID(menuId);
			Dao.setMenuHeader(messages.getMessage("oscarEncounter.LeftNavBar.InputGrps"));
			Dao.setRightURL("return !showMenu('" + menuId + "', event);");

			List<String> flowsheets = flowsheetService.getUniversalFlowsheetNames();

			if (!OscarProperties.getInstance().getBooleanProperty("new_flowsheet_enabled", "true")) {
				flowsheets.remove("diab3");
			}
			
			int hash;
			List<NavBarDisplayDAO.Item> universalItems = createDaoItems(flowsheets, roleName$, bean.demographicNo, eChartUUID, request.getContextPath());
			for (NavBarDisplayDAO.Item item : universalItems)
			{
				Dao.addItem(item);
			}

			//next we add dx triggered flowsheets to the module items
			dxResearchBeanHandler dxRes = new dxResearchBeanHandler(bean.demographicNo);
			List<String> dxCodes = dxRes.getActiveCodeListWithCodingSystem();
			flowsheets = flowsheetService.getFlowsheetNamesFromDxCodes(dxCodes);
			List<NavBarDisplayDAO.Item> dxItems = createDaoItems(flowsheets, roleName$, bean.demographicNo, eChartUUID, request.getContextPath());
			for (NavBarDisplayDAO.Item item : dxItems)
			{
				Dao.addItem(item);
			}

			//next we add program based flowsheets
			List<String> programs = new ArrayList<String>();
			AdmissionDao admissionDao = (AdmissionDao) SpringUtils.getBean("admissionDao");
			List<Admission> admissions = admissionDao.getCurrentAdmissions(Integer.parseInt(bean.demographicNo));
			for (Admission admission : admissions) {
				programs.add(String.valueOf(admission.getProgramId()));
			}
			flowsheets = flowsheetService.getFlowsheetNamesFromProgram(programs);
			List<NavBarDisplayDAO.Item> programItems = createDaoItems(flowsheets, roleName$, bean.demographicNo, eChartUUID, request.getContextPath());
			for (NavBarDisplayDAO.Item item : programItems)
			{
				Dao.addItem(item);
			}

			MeasurementGroupStyleDao groupDao = SpringUtils.getBean(MeasurementGroupStyleDao.class);
			List<MeasurementGroupStyle> groups = groupDao.findAll();
			//now we grab measurement groups for popup menu
			for (MeasurementGroupStyle group : groups)
			{
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

			//finally we add specific measurements to module item list
			Integer demo = Integer.valueOf(bean.getDemographicNo());
			oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementsDataBeanHandler hd = new oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementsDataBeanHandler(demo);
			oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementsDataBean data;
			List<EctMeasurementsDataBean> measureTypes = (ArrayList<EctMeasurementsDataBean>) hd.getMeasurementsDataVector();
			if (loggedInInfo.getCurrentFacility().isIntegratorEnabled()) {
				EctMeasurementsDataBeanHandler.addRemoteMeasurementsTypes(loggedInInfo,measureTypes,demo);
			}

			for (EctMeasurementsDataBean measureType : measureTypes)
			{
				data = measureType;
				String title = data.getTypeDisplayName();
				String type = data.getType();

				winName = type + bean.demographicNo;
				hash = Math.abs(winName.hashCode());

				hd = new EctMeasurementsDataBeanHandler(demo, data.getType());
				List<EctMeasurementsDataBean> measures = (ArrayList<EctMeasurementsDataBean>) hd.getMeasurementsDataVector();
				if (loggedInInfo.getCurrentFacility().isIntegratorEnabled())
				{
					EctMeasurementsDataBeanHandler.addRemoteMeasurements(loggedInInfo, measures, data.getType(), demo);
				}

				if (measures.size() > 0)
				{
					NavBarDisplayDAO.Item item = NavBarDisplayDAO.Item();
					data = measures.get(0);
					Date date = data.getDateObservedAsDate();
					if (date == null)
					{
						date = data.getDateEnteredAsDate();
					}

					String formattedDate = DateUtils.formatDate(date, request.getLocale());
					item.setLinkTitle(title + " " + data.getDataField() + " " + formattedDate);
					title = padd(title, data.getDataField());
					String tmp = "<span class=\"measureCol1\">" + title + "</span>";
					item.setValue(data.getDataField());
					item.setTitle(tmp);
					item.setDate(date);
					item.setURL("popupPage(300,800,'" + hash + "','" + request.getContextPath() + "/oscarEncounter/oscarMeasurements/SetupDisplayHistory.do?type=" + type + "'); return false;");
					Dao.addItem(item);
				}

			}
			Dao.sortItems(NavBarDisplayDAO.DATESORT_ASC);
			return true;
		}
	}

	public String getCmd() {
		return cmd;
	}

	/**
	 *truncate string to specified length so that measurements are always displayed
	 *in a column
	 */
	public String padd(String str, String data) {
		String tmp;
		int overflow = str.length() + data.length() - MAX_LEN_TITLE;
		//if we are over limit, truncate
		if (overflow > 0) {
			int maxsize = (str.length() - overflow) > 0 ? str.length() - overflow : 1;
			int minsize = maxsize > 3 ? maxsize - 3 : 0;
			String ellipses = new String();
			ellipses = org.apache.commons.lang.StringUtils.rightPad(ellipses, maxsize - minsize, '.');
			tmp = StringUtils.maxLenString(str, maxsize, minsize, ellipses);
		} else tmp = str;

		return tmp;
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

	private List<NavBarDisplayDAO.Item> createDaoItems(List<String> flowsheets, String roleName$, String demographicNo, String eChartUUID, String contextPath)
	{
		com.quatro.service.security.SecurityManager securityMgr = new com.quatro.service.security.SecurityManager();
		List<NavBarDisplayDAO.Item> itemsToAdd = new ArrayList<>();
		for (String flowsheetName : flowsheets)
		{
			NavBarDisplayDAO.Item item = NavBarDisplayDAO.Item();
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
				String uuid = UUID.randomUUID().toString();
				int hash = Math.abs(winName.hashCode());
				String url = "popupPage(700,1000,'" + hash + "','" + contextPath + "/oscarEncounter/oscarMeasurements/TemplateFlowSheet.jsp?uuid=" + uuid +
						"&demographic_no=" + demographicNo + "&template=" + flowsheetName + "&echartUUID=" + eChartUUID + "');return false;";
				item.setLinkTitle(displayName);
				displayName = StringUtils.maxLenString(displayName, MAX_LEN_TITLE, CROP_LEN_TITLE, ELLIPSES);
				item.setTitle(displayName);
				item.setURL(url);
				itemsToAdd.add(item);
			}
		}
		return itemsToAdd;
	}
}
