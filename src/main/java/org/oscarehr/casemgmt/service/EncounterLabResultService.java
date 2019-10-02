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
import org.oscarehr.util.LoggedInInfo;
import oscar.oscarEncounter.pageUtil.NavBarDisplayDAO;
import oscar.oscarLab.ca.on.CommonLabResultData;
import oscar.oscarLab.ca.on.LabResultData;
import oscar.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class EncounterLabResultService extends EncounterSectionService
{
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

		CommonLabResultData comLab = new CommonLabResultData();
		ArrayList<LabResultData> labs = comLab.populateLabResultsData(
				loggedInInfo, "", demographicNo, "", "",
				"","U");
		Collections.sort(labs);

		/*
		//set text for lefthand module title
		Dao.setLeftHeading(messages.getMessage(request.getLocale(), "oscarEncounter.LeftNavBar.Labs"));

		//set link for lefthand module title
		String winName = "Labs" + bean.demographicNo;
		String url = "popupPage(700,599,'" + winName + "','" + request.getContextPath() + "/lab/DemographicLab.jsp?demographicNo=" + bean.demographicNo + "'); return false;";
		Dao.setLeftURL(url);

		//we're going to display popup menu of 2 selections - row display and grid display
		String menuId = "2";
		Dao.setRightHeadingID(menuId);
		Dao.setRightURL("return !showMenu('" + menuId + "', event);");
		Dao.setMenuHeader(messages.getMessage("oscarEncounter.LeftNavBar.LabMenuHeading"));

		winName = "AllLabs" + bean.demographicNo;
		url = "popupPage(700,1000, '" + winName + "','" + request.getContextPath() + "/lab/CumulativeLabValues2.jsp?demographic_no=" + bean.demographicNo + "')";
		Dao.addPopUpUrl(url);
		Dao.addPopUpText(messages.getMessage("oscarEncounter.LeftNavBar.LabMenuItem1"));

		url = "popupPage(700,1000, '" + winName + "','" + request.getContextPath() + "/lab/CumulativeLabValues.jsp?demographic_no=" + bean.demographicNo + "')";
		Dao.addPopUpUrl(url);
		Dao.addPopUpText(messages.getMessage("oscarEncounter.LeftNavBar.LabMenuItem2"));
		 */

		//now we add individual module items
		LabResultData result;
		String labDisplayName, label;
		//String bgcolour = "FFFFCC";
		StringBuilder func;
		int hash;
		for( int idx = 0; idx < labs.size(); ++idx )
		{

			EncounterSectionNote sectionNote = new EncounterSectionNote();

			result =  labs.get(idx);
			Date date = result.getDateObj();
			//String formattedDate = DateUtils.formatDate(date,request.getLocale());
			//func = new StringBuilder("popupPage(700,960,'");
			label = result.getLabel();

			if ( result.isMDS() ){
				if (label == null || label.equals("")) labDisplayName = result.getDiscipline();
				else labDisplayName = label;
				//url = request.getContextPath() + "/oscarMDS/SegmentDisplay.jsp?providerNo="+bean.providerNo+"&segmentID="+result.segmentID+"&status="+result.getReportStatus();
			}else if (result.isCML()){
				if (label == null || label.equals("")) labDisplayName = result.getDiscipline();
				else labDisplayName = label;
				//url = request.getContextPath() + "/lab/CA/ON/CMLDisplay.jsp?providerNo="+bean.providerNo+"&segmentID="+result.segmentID;
			}else if (result.isHL7TEXT()){
				if (label == null || label.equals("")) labDisplayName = result.getDiscipline();
				else labDisplayName = label;
				//url = request.getContextPath() + "/lab/CA/ALL/labDisplay.jsp?providerNo="+bean.providerNo+"&segmentID="+result.segmentID;
			}else {
				if (label == null || label.equals("")) labDisplayName = result.getDiscipline();
				else labDisplayName = label;
				//url = request.getContextPath() + "/lab/CA/BC/labDisplay.jsp?segmentID="+result.segmentID+"&providerNo="+bean.providerNo;
			}

			NavBarDisplayDAO.Item item = NavBarDisplayDAO.Item();
			//item.setLinkTitle(labDisplayName + " " + formattedDate);
			labDisplayName = StringUtils.maxLenString(labDisplayName, MAX_LEN_TITLE, CROP_LEN_TITLE, ELLIPSES); // +" "+formattedDate;
			//hash = winName.hashCode();
			//hash = hash < 0 ? hash * -1 : hash;
			//func.append(hash + "','" + url + "'); return false;");

			sectionNote.setText(labDisplayName);

			LocalDateTime serviceDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
			sectionNote.setUpdateDate(serviceDate);

			/*
			item.setTitle(labDisplayName);
			item.setURL(func.toString());
			item.setDate(date);
			 */

			//item.setBgColour(bgcolour);
			//Dao.addItem(item);

			out.add(sectionNote);
		}

		return EncounterNotes.limitedEncounterNotes(out, offset, limit);
	}
}
