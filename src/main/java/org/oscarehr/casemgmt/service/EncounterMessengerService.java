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
import org.oscarehr.common.model.OscarMsgType;
import org.oscarehr.managers.SecurityInfoManager;
import org.springframework.beans.factory.annotation.Autowired;
import oscar.oscarMessenger.data.MsgMessageData;
import oscar.oscarMessenger.util.MsgDemoMap;
import oscar.util.ConversionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EncounterMessengerService extends EncounterSectionService
{
	public static final String SECTION_ID = "msgs";
	protected static final String SECTION_TITLE_KEY = "oscarEncounter.LeftNavBar.Messages";
	protected static final String SECTION_TITLE_COLOUR = "#7F462C";

	@Autowired
	private SecurityInfoManager securityInfoManager;

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
		String winName = "SendMsg" + sectionParams.getDemographicNo();
		String url = sectionParams.getContextPath() + "/oscarMessenger/SendDemoMessage.do" +
				"?demographic_no=" + encodeUrlParam(sectionParams.getDemographicNo());

		return "popupPage(700,960,'" + winName + "','" + url + "');";
	}

	@Override
	protected String getOnClickTitle(SectionParameters sectionParams)
	{
		String winName = "ViewMsg" + sectionParams.getDemographicNo();
		String url = sectionParams.getContextPath() + "/oscarMessenger/DisplayDemographicMessages.do" +
				"?orderby=date&boxType=3" +
				"&demographic_no=" + encodeUrlParam(sectionParams.getDemographicNo()) +
				"&providerNo=" + encodeUrlParam(sectionParams.getProviderNo()) +
				"&userName=" + encodeUrlParam(sectionParams.getUserName());

		return "popupPage(600,900,'" + winName + "','" + url + "');";
	}

	public EncounterNotes getNotes(
			SectionParameters sectionParams, Integer limit,
			Integer offset
	)
	{
		List<EncounterSectionNote> out = new ArrayList<>();
		if (!securityInfoManager.hasPrivilege(sectionParams.getLoggedInInfo(), "_msg", "r", null))
		{
			return EncounterNotes.noNotes();
		}

		MsgDemoMap msgDemoMap = new MsgDemoMap();
		List<String> msgList = msgDemoMap.getMsgList(sectionParams.getDemographicNo(), OscarMsgType.GENERAL_TYPE);

		for( int i=0; i<msgList.size(); i++)
		{
			EncounterSectionNote sectionNote = new EncounterSectionNote();

			String msgId = msgList.get(i);
			MsgMessageData msgData = new MsgMessageData(msgId);
			sectionNote.setText(EncounterSectionService.getTrimmedText(msgData.getSubject()));

			String msgDate = msgData.getDate();
			LocalDateTime date = ConversionUtils.toLocalDate(msgDate).atStartOfDay();
			sectionNote.setUpdateDate(date);

			sectionNote.setTitle(EncounterSectionService.formatTitleWithLocalDateTime(msgData.getSubject(), date));

			String winName = "SendMsg" + sectionParams.getDemographicNo();
			int hash = winName.hashCode();
			hash = hash < 0 ? hash * -1 : hash;
			String url = sectionParams.getContextPath() + "/oscarMessenger/ViewMessageByPosition.do" +
					"?from=encounter" +
					"&orderBy=!date" +
					"&demographic_no=" + encodeUrlParam(sectionParams.getDemographicNo()) +
					"&messagePosition=" + i;

			String onClickString = "popupPage(600,900,'" + hash + "','" + url + "');";
			sectionNote.setOnClick(onClickString);


			out.add(sectionNote);
		}

		return EncounterNotes.limitedEncounterNotes(out, offset, limit);
	}
}
