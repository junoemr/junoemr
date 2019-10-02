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
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.springframework.beans.factory.annotation.Autowired;
import oscar.oscarResearch.oscarDxResearch.bean.dxResearchBean;
import oscar.oscarResearch.oscarDxResearch.bean.dxResearchBeanHandler;
import oscar.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EncounterDiseaseRegistryService extends EncounterSectionService
{
	protected static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.n";

	@Autowired
	protected SecurityInfoManager securityInfoManager;

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

		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_dxresearch", "r", null))
		{
			return EncounterNotes.noNotes();
		}

		////set lefthand module heading and link
		//String winName = "Disease" + bean.demographicNo;
		//String url = "popupPage(580,900,'" + winName + "','" + request.getContextPath() + "/oscarResearch/oscarDxResearch/setupDxResearch.do?demographicNo=" + bean.demographicNo + "&providerNo=" + bean.providerNo + "&quickList=')";
		//Dao.setLeftHeading(messages.getMessage(request.getLocale(), "oscarEncounter.LeftNavBar.DxRegistry"));
		//Dao.setLeftURL(url);

		////set righthand link to same as left so we have visual consistency with other modules
		//url += "; return false;";
		//Dao.setRightURL(url);
		//Dao.setRightHeadingID(cmd);  //no menu so set div id to unique id for this action

		////grab all of the diseases associated with patient and add a list item for each
		//String dbFormat = "yyyy-MM-dd";
		//DateFormat formatter = new SimpleDateFormat(dbFormat);
		//String serviceDateStr;
		//Date date;
		dxResearchBeanHandler hd = new dxResearchBeanHandler(demographicNo);
		List<dxResearchBean> diseases = hd.getDxResearchBeans();

		//for (int idx = 0; idx < diseases.size(); ++idx)
		for(dxResearchBean dxBean: diseases)
		{
			EncounterSectionNote sectionNote = new EncounterSectionNote();

			//NavBarDisplayDAO.Item item = NavBarDisplayDAO.Item();
			//dxResearchBean dxBean = (dxResearchBean) diseases.get(idx);

			if (!dxBean.getStatus().equals("A"))
				continue;

			if (dxBean.getStatus() != null && dxBean.getStatus().equalsIgnoreCase("C"))
			{
				sectionNote.setColour("000000");
			}

			String dateStr = dxBean.getEnd_date();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
			LocalDateTime date = LocalDateTime.parse(dateStr, formatter);
			sectionNote.setUpdateDate(date);

			//try
			//{
			//	date = formatter.parse(dateStr);
			//	//Date sDate = formatter.parse(startDate);
			//	//serviceDateStr = DateUtils.formatDate(sDate, request.getLocale());
			//	item.setDate(date);
			//}
			//catch (ParseException ex)
			//{
			//	MiscUtils.getLogger().debug("EctDisplayDxAction: Error creating date " + ex.getMessage());
			//	serviceDateStr = "Error";
			//	//date = new Date(System.currentTimeMillis());
			//	date = null;
			//}

			String strTitle = StringUtils.maxLenString(dxBean.getDescription(), MAX_LEN_TITLE, CROP_LEN_TITLE, ELLIPSES);

			sectionNote.setText(strTitle);
			//item.setTitle(strTitle);
			//item.setLinkTitle(dxBean.getDescription() + " " + serviceDateStr);
			//item.setURL("return false;");

			//Dao.addItem(item);
			out.add(sectionNote);
		}

		Collections.sort(out, new EncounterSectionNote.SortChronologicAsc());


		return EncounterNotes.limitedEncounterNotes(out, offset, limit);
	}
}
