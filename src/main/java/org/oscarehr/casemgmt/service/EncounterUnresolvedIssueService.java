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
import org.oscarehr.casemgmt.model.CaseManagementIssue;
import org.oscarehr.util.CppUtils;
import org.oscarehr.util.LoggedInInfo;
import oscar.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class EncounterUnresolvedIssueService extends EncounterSectionService
{
	private CaseManagementManager caseManagementMgr;

	public void setCaseManagementManager(CaseManagementManager caseManagementMgr)
	{
		this.caseManagementMgr = caseManagementMgr;
	}

	public List<EncounterSectionNote> getNotes(
			LoggedInInfo loggedInInfo,
			String roleName,
			String providerNo,
			String demographicNo,
			String appointmentNo,
			String programId)
	{
		List<EncounterSectionNote> out = new ArrayList<>();

		//String providerNo=loggedInInfo.getLoggedInProviderNo();

		// set lefthand module heading and link
		//navBarDisplayDAO.setLeftHeading(messages.getMessage(request.getLocale(), "oscarEncounter.NavBar.unresolvedIssues"));

		//navBarDisplayDAO.setLeftURL("$('check_issue').value='';document.caseManagementViewForm.submit();");

		// set righthand link to same as left so we have visual consistency with other modules
		//String url = "return false;";
		//navBarDisplayDAO.setRightURL(url);
		//navBarDisplayDAO.setRightHeadingID(cmd); // no menu so set div id to unique id for this action
		//String programId = (String) loggedInInfo.getSession().getAttribute("case_program_id");

		// grab all of the diseases associated with patient and add a list item for each

		List<CaseManagementIssue> issues = null;
		int demographicId = Integer.parseInt(demographicNo);
		issues = caseManagementMgr.getIssues(demographicId);
		issues = caseManagementMgr.filterIssues(loggedInInfo, providerNo, issues, programId);

		List<CaseManagementIssue> issues_unr = new ArrayList<CaseManagementIssue>();

		//only list unresolved issues
		for(CaseManagementIssue issue : issues)
		{
			if(containsIssue(CppUtils.cppCodes,issue.getIssue().getCode()))
			{
				continue;
			}

			if(!issue.isResolved())
			{
				boolean dup=false;
				for(CaseManagementIssue tmp: issues_unr)
				{
					if(issue.getIssue_id() == tmp.getIssue_id())
					{
						dup=true;
						break;
					}
				}

				if(!dup)
				{
					issues_unr.add(issue);
				}
			}
		}


		for (int idx = 0; idx < issues_unr.size(); ++idx)
		{
			//NavBarDisplayDAO.Item item = NavBarDisplayDAO.Item();
			EncounterSectionNote sectionNote = new EncounterSectionNote();

			CaseManagementIssue issue = issues_unr.get(idx);
			String tmp = issue.getIssue().getDescription();

			String strTitle = StringUtils.maxLenString(tmp, MAX_LEN_TITLE, CROP_LEN_TITLE, ELLIPSES);

			sectionNote.setText(strTitle);

			out.add(sectionNote);

			//item.setTitle(strTitle);
			//item.setLinkTitle(tmp);
			//url = "setIssueCheckbox('"+issue.getId()+"');return filter(false);";
			//item.setURL(url);
			//navBarDisplayDAO.addItem(item);
		}

		return out;
	}

	public static boolean containsIssue(String[]  issues, String issueCode)
	{
		for (String caseManagementIssue : issues)
		{
			if (caseManagementIssue.equals(issueCode))
			{
				return(true);
			}
		}

		return false;
	}
}
