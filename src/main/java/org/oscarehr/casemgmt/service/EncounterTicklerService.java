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

import org.oscarehr.casemgmt.dto.EncounterSectionNote;
import org.oscarehr.common.model.Tickler;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.managers.TicklerManager;
import org.oscarehr.util.LoggedInInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oscar.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class EncounterTicklerService extends EncounterSectionService
{
	@Autowired
	protected SecurityInfoManager securityInfoManager;

	@Autowired
	TicklerManager ticklerManager;

	public List<EncounterSectionNote> getNotes(LoggedInInfo loggedInInfo, String roleName, String providerNo, String demographicNo, String appointmentNo, String programId)
	{
		List<EncounterSectionNote> out = new ArrayList<>();

		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_tickler", "r", null))
		{
			return out; //The link of tickler won't show up on new CME screen.
		}

		////Set lefthand module heading and link
		//String winName = "ViewTickler" + bean.demographicNo;
		//String pathview, pathedit;
		//if (org.oscarehr.common.IsPropertiesOn.isTicklerPlusEnable())
		//{
		//	pathview = request.getContextPath() + "/Tickler.do?filter.demographic_webName=" + encode(bean) + "&filter.demographicNo=" + bean.demographicNo + "&filter.assignee=";
		//	pathedit = request.getContextPath() + "/Tickler.do?method=edit&tickler.demographic_webName=" + encode(bean) + "&tickler.demographicNo=" + bean.demographicNo;
		//} else
		//{
		//	pathview = request.getContextPath() + "/tickler/ticklerMain.jsp?demoview=" + bean.demographicNo + "&parentAjaxId=" + cmd;
		//	pathedit = request.getContextPath() + "/tickler/ticklerAdd.jsp" +
		//			"?demographic_no=" + bean.demographicNo +
		//			"&name=" + encode(bean) +
		//			"&chart_no=" + encode(((bean.chartNo != null) ? bean.chartNo : "")) +
		//			"&bFirstDisp=false" +
		//			"&doctor_no=" + bean.familyDoctorNo +         // despite the name, the bean loads it as demo.provider_no
		//			"&search_mode=search_name" +                  // This is required.  The default search mode may not be search name.  Since we forward the name, we want to search on that.
		//			"&orderby=last_name" +                        // Just to make sure that the order also isn't affected by a property override.
		//			"&originalpage=" + encode(request.getContextPath() + "/tickler/ticklerAdd.jsp") +
		//			"&parentAjaxId=" + cmd +
		//			"&updateParent=true";
		//}

		//String url = "popupPage(500,900,'" + winName + "','" + pathview + "')";
		//Dao.setLeftHeading(messages.getMessage(request.getLocale(), "global.viewTickler"));
		//Dao.setLeftURL(url);

		////set right hand heading link
		//winName = "AddTickler" + bean.demographicNo;
		//url = "popupPage(500,600,'" + winName + "','" + pathedit + "'); return false;";
		//Dao.setRightURL(url);
		//Dao.setRightHeadingID(cmd); //no menu so set div id to unique id for this action

		//String dateBegin = "1900-01-01";
		//String dateEnd = "8888-12-31";

		List<Tickler> ticklers = ticklerManager.findActiveByDemographicNo(loggedInInfo, Integer.parseInt(demographicNo));

		//Date serviceDate;
		//Date today = new Date(System.currentTimeMillis());
		//int hash;
		//long days;
		for (Tickler t : ticklers)
		{
			EncounterSectionNote sectionNote = new EncounterSectionNote();

			//NavBarDisplayDAO.Item item = NavBarDisplayDAO.Item();
			LocalDateTime serviceDate = t.getServiceDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
			//item.setDate(serviceDate);
			sectionNote.setUpdateDate(serviceDate);


			//days = (today.getTime() - serviceDate.getTime()) / (1000 * 60 * 60 * 24);
			//if (days > 0)
			if(serviceDate.isBefore(LocalDateTime.now()))
			{
				//item.setColour("#FF0000");
				sectionNote.setColour("#FF0000");
			}

			String itemHeader = StringUtils.maxLenString(t.getMessage(), MAX_LEN_TITLE, CROP_LEN_TITLE, ELLIPSES);
			//item.setLinkTitle(itemHeader + " " + DateUtils.formatDate(serviceDate, request.getLocale()));
			//item.setTitle(itemHeader);

			sectionNote.setText(itemHeader);

			//// item.setValue(String.valueOf(t.getTickler_no()));
			//winName = StringUtils.maxLenString(t.getMessage(), MAX_LEN_TITLE, MAX_LEN_TITLE, "");
			//hash = Math.abs(winName.hashCode());
			//if (org.oscarehr.common.IsPropertiesOn.isTicklerPlusEnable())
			//{
			//	url = "popupPage(500,900,'" + hash + "','" + request.getContextPath() + "/Tickler.do?method=view&id=" + t.getId() + "'); return false;";
			//} else
			//{
			//	url = "popupPage(500,900,'" + hash + "','" + request.getContextPath() + "/tickler/ticklerMain.jsp?demoview=" + bean.demographicNo + "&parentAjaxId=" + cmd + "'); return false;";
			//}

			//item.setURL(url);
			//Dao.addItem(item);
			out.add(sectionNote);
		}

		Collections.sort(out, new EncounterSectionNote.SortChronologic());

		return out;
	}
}
