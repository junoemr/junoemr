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
import org.oscarehr.common.model.OscarMsgType;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.springframework.beans.factory.annotation.Autowired;
import oscar.oscarMessenger.data.MsgMessageData;
import oscar.oscarMessenger.util.MsgDemoMap;
import oscar.util.ConversionUtils;
import oscar.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EncounterMessengerService extends EncounterSectionService
{
	@Autowired
	private SecurityInfoManager securityInfoManager;

	//@Autowired
	//private EFormDataDao eFormDataDao;

	public List<EncounterSectionNote> getNotes(LoggedInInfo loggedInInfo, String roleName, String providerNo, String demographicNo, String appointmentNo, String programId)
	{
		List<EncounterSectionNote> out = new ArrayList<>();
		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_msg", "r", null))
		{
			return out; //Oscar message link won't show up on new CME screen.
		}

		//set text for lefthand module title
		//Dao.setLeftHeading(messages.getMessage(request.getLocale(), "oscarEncounter.LeftNavBar.Messages"));

		//set link for lefthand module title
		//String winName = "ViewMsg" + bean.demographicNo;
		//String url = "popupPage(600,900,'" + winName + "','" + request.getContextPath() + "/oscarMessenger/DisplayDemographicMessages.do?orderby=date&boxType=3&demographic_no=" + bean.demographicNo + "&providerNo=" + bean.providerNo + "&userName=" + bean.userName + "')";
		//Dao.setLeftURL(url);

		//set the right hand heading link
		//winName = "SendMsg" + bean.demographicNo;
		//url = "popupPage(700,960,'" + winName + "','"+ request.getContextPath() + "/oscarMessenger/SendDemoMessage.do?demographic_no=" + bean.demographicNo + "'); return false;";
		//Dao.setRightURL(url);
		//Dao.setRightHeadingID(cmd);  //no menu so set div id to unique id for this action

		MsgDemoMap msgDemoMap = new MsgDemoMap();
		List<String> msgList = msgDemoMap.getMsgList(demographicNo, OscarMsgType.GENERAL_TYPE);

		//MsgMessageData msgData;
		//String msgId;
		//String msgSubject;
		//String msgDate;
		//String dbFormat = "yyyy-MM-dd";
		//int hash;
		//Date date;
		for( int i=0; i<msgList.size(); i++)
		{
			EncounterSectionNote sectionNote = new EncounterSectionNote();

			String msgId = msgList.get(i);
			MsgMessageData msgData = new MsgMessageData(msgId);
			String msgSubject = StringUtils.maxLenString(msgData.getSubject(), MAX_LEN_TITLE, CROP_LEN_TITLE, ELLIPSES);
			String msgDate = msgData.getDate();

			LocalDate date = ConversionUtils.toLocalDate(msgDate);
			sectionNote.setUpdateDate(date.atStartOfDay());

			//hash = winName.hashCode();
			//hash = hash < 0 ? hash * -1 : hash;
			//url = "popupPage(600,900,'" + hash + "','" + request.getContextPath() + "/oscarMessenger/ViewMessageByPosition.do?from=encounter&orderBy=!date&demographic_no=" + bean.demographicNo + "&messagePosition="+i + "'); return false;";
			//item.setURL(url);
			//item.setTitle(msgSubject);
			//item.setLinkTitle(msgData.getSubject() + " " + msgDate);

			sectionNote.setText(msgSubject);

			out.add(sectionNote);
		}

		return out;
	}
}
