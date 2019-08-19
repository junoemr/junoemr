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


/*
import org.apache.commons.lang.StringEscapeUtils;
import org.oscarehr.casemgmt.dto.EncounterSectionNote;
import org.oscarehr.decisionSupport.service.DSService;
import org.oscarehr.common.model.Dxresearch;
import org.oscarehr.decisionSupport.model.DSConsequence;
import org.oscarehr.decisionSupport.model.DSGuideline;
import org.oscarehr.decisionSupport.service.DSService;
import org.oscarehr.renal.CkdScreener;
import org.oscarehr.util.LoggedInInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import oscar.OscarProperties;
import oscar.oscarEncounter.pageUtil.NavBarDisplayDAO;
import oscar.util.OscarRoleObjectPrivilege;
import oscar.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
 */

import org.oscarehr.casemgmt.dto.EncounterSectionNote;
import org.oscarehr.util.LoggedInInfo;

import java.util.ArrayList;
import java.util.List;

public class EncounterDecisionSupportService extends EncounterSectionService
{
	//@Autowired
	//DSService dsService;

	public List<EncounterSectionNote> getNotes(
			LoggedInInfo loggedInInfo,
			String roleName,
			String providerNo,
			String demographicNo,
			String appointmentNo,
			String programId)
	{
		List<EncounterSectionNote> out = new ArrayList<>();
		/*

		Vector v = OscarRoleObjectPrivilege.getPrivilegeProp("_newCasemgmt.decisionSupportAlerts");

		if (OscarRoleObjectPrivilege.checkPrivilege(roleName, (Properties) v.get(0), (Vector) v.get(1)))
		{
			return out;
		}
		 */

		////set lefthand module heading and link
		//String winName = "dsalert" + bean.demographicNo;
		//String url = "popupPage(500,950,'" + winName + "','" + request.getContextPath() + "/oscarEncounter/decisionSupport/guidelineAction.do?method=list&provider_no=" + bean.providerNo + "&demographic_no=" + bean.demographicNo + "&parentAjaxId=" + cmd + "'); return false;";
		//Dao.setLeftHeading(messages.getMessage(request.getLocale(), "global.decisionSupportAlerts"));
		//Dao.setLeftURL(url);

		////set the right hand heading link
		//winName = "AddeForm" + bean.demographicNo;

		//Dao.setRightURL(url);
		//Dao.setRightHeadingID(cmd);  //no menu so set div id to unique id for this action

		//StringBuilder javascript = new StringBuilder("<script type=\"text/javascript\">");
		//String js = "";

		//WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServlet().getServletContext());
		//DSService dsService = (DSService) ctx.getBean("dsService");

		//List<DSGuideline> dsGuidelines = dsService.getDsGuidelinesByProvider(providerNo);

		//String BGCOLOUR = request.getParameter("hC");


		//ORN CKD Pilot Code
		// XXX: No one is using the ORN_PILOT property
		/*
		if (
				OscarProperties.getInstance().getProperty("ORN_PILOT", "yes").equalsIgnoreCase("yes") && (
						OscarProperties.getInstance().getProperty("ckd_notification_scheme", "dsa").equals("dsa") ||
						OscarProperties.getInstance().getProperty("ckd_notification_scheme", "dsa").equals("all")
				)
		)
		{
			CkdScreener ckdScreener = new CkdScreener();
			List<String> reasons = new ArrayList<String>();
			boolean match = ckdScreener.screenDemographic(Integer.parseInt(bean.demographicNo), reasons, null);
			boolean notify = false;

			for (Dxresearch dr : dxResearchDao.find(Integer.parseInt(bean.demographicNo), "OscarCode", "CKDSCREEN"))
			{
				//we have an active one, we should notify
				if (dr.getStatus() == 'A')
				{
					notify = true;
				}
			}
			for (Dxresearch dr : dxResearchDao.find(Integer.parseInt(bean.demographicNo), "icd9", "585"))
			{
				if (dr.getStatus() == 'A')
				{
					notify = false;
				}
			}
			if (!notify)
			{
				//there's no active ones, but let's look at the latest one
				List<Dxresearch> drs = dxResearchDao.find(Integer.parseInt(bean.demographicNo), "OscarCode", "CKDSCREEN");
				if (drs.size() > 0)
				{
					Dxresearch dr = drs.get(0);
					Calendar aYearAgo = Calendar.getInstance();
					aYearAgo.add(Calendar.MONTH, -12);
					if (dr.getUpdateDate().before(aYearAgo.getTime()))
					{
						notify = true;
						//reopen it
						dr.setStatus('A');
						dr.setUpdateDate(new Date());
						dxResearchDao.merge(dr);
						//need some way to notify that tab to reload
						javascript.append("jQuery(document).ready(function(){reloadNav('Dx');});");
					}
				}
			}
			if (match && notify)
			{
				NavBarDisplayDAO.Item item = NavBarDisplayDAO.Item();
				url = "popupPage(500,950,'" + winName + "','" + request.getContextPath() + "/renal/CkdDSA.do?method=detail&demographic_no=" + bean.demographicNo + "&parentAjaxId=" + cmd + "'); return false;";
				item.setURL(url);
				item.setLinkTitle("Based on guidelines, a CKD screening should be performed.");
				item.setTitle("Screen for CKD");
				item.setColour("#ff5409;");
				Dao.addItem(item);
			}
		}
		 */


		/*
		for (DSGuideline dsGuideline : dsGuidelines)
		{
			if (OscarProperties.getInstance().getProperty("dsa.skip." + dsGuideline.getTitle().replaceAll(" ", "_"), "false").equals("true"))
			{
				continue;
			}
			try
			{
				List<DSConsequence> dsConsequences = dsGuideline.evaluate(loggedInInfo, bean.demographicNo);
				if (dsConsequences == null) continue;
				for (DSConsequence dsConsequence : dsConsequences)
				{
					if (dsConsequence.getConsequenceType() != DSConsequence.ConsequenceType.warning)
						continue;

					NavBarDisplayDAO.Item item = NavBarDisplayDAO.Item();
					winName = dsConsequence.getConsequenceType().toString() + bean.demographicNo;

					url = "popupPage(500,950,'" + winName + "','" + request.getContextPath() + "/oscarEncounter/decisionSupport/guidelineAction.do?method=detail&guidelineId=" + dsGuideline.getId() + "&provider_no=" + bean.providerNo + "&demographic_no=" + bean.demographicNo + "&parentAjaxId=" + cmd + "'); return false;";
					//Date date = (Date)curform.get("formDateAsDate");
					//String formattedDate = DateUtils.getDate(date,dateFormat,request.getLocale());
					String key = StringUtils.maxLenString(dsConsequence.getText(), MAX_LEN_KEY, CROP_LEN_KEY, ELLIPSES);
					item.setLinkTitle(dsGuideline.getTitle());
					key = StringEscapeUtils.escapeJavaScript(key);
					js = "itemColours['" + key + "'] = '" + BGCOLOUR + "'; autoCompleted['" + key + "'] = \"" + url + "\"; autoCompList.push('" + key + "');";
					javascript.append(js);
					url += "return false;";
					item.setURL(url);
					String strTitle = StringUtils.maxLenString(dsGuideline.getTitle(), MAX_LEN_TITLE, CROP_LEN_TITLE, ELLIPSES);
					item.setTitle(strTitle);
					if (dsConsequence.getConsequenceStrength() == DSConsequence.ConsequenceStrength.warning)
					{
						item.setColour("#ff5409;");
					}
					//item.setDate(new Date());
					Dao.addItem(item);
				}
			} catch (Exception e)
			{
				logger.error("Unable to evaluate patient against a DS guideline '" + dsGuideline.getTitle() + "' of UUID '" + dsGuideline.getUuid() + "'", e);
			}

			javascript.append("</script>");
			Dao.setJavaScript(javascript.toString());
		 */
		return out;
	}
}
