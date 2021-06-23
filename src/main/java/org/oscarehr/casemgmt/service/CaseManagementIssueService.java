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

import org.oscarehr.casemgmt.model.CaseManagementIssue;
import org.oscarehr.encounterNote.dao.CaseManagementIssueDao;
import org.oscarehr.encounterNote.dao.IssueDao;
import org.oscarehr.encounterNote.model.Issue;
import org.oscarehr.util.CppUtils;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.ws.rest.conversion.CaseManagementIssueConverter;
import org.oscarehr.ws.rest.to.model.CaseManagementIssueTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class CaseManagementIssueService
{
	public static final String PROPERTY_ACUTE = "acute";
	public static final String PROPERTY_CERTAIN = "certain";
	public static final String PROPERTY_MAJOR = "major";
	public static final String PROPERTY_RESOLVED = "resolved";

	@Autowired
	private CaseManagementIssueDao caseManagementIssueDao;

	@Autowired
	private IssueDao issueDao;

	private CaseManagementManager caseManagementMgr;

	public void setCaseManagementManager(CaseManagementManager caseManagementMgr)
	{
		this.caseManagementMgr = caseManagementMgr;
	}

	public CaseManagementIssueTo1 getIssueById(Integer demographicNo, Long issueId)
	{
		org.oscarehr.encounterNote.model.CaseManagementIssue issue =
				caseManagementIssueDao.findByIssueId(demographicNo, issueId);

		CaseManagementIssueConverter converter = new CaseManagementIssueConverter();

		return converter.getAsTransferObject(issue);
	}

	public CaseManagementIssueTo1 updateProperty(
			int demographicNo,
			int issueId,
			String propertyName,
			boolean propertyValue
	)
	{
		org.oscarehr.encounterNote.model.CaseManagementIssue issue =
				caseManagementIssueDao.findByIssueId(demographicNo, new Long(issueId));

		if(PROPERTY_ACUTE.equals(propertyName))
		{
			issue.setAcute(propertyValue);
		}
		else if(PROPERTY_CERTAIN.equals(propertyName))
		{
			issue.setCertain(propertyValue);
		}
		else if(PROPERTY_MAJOR.equals(propertyName))
		{
			issue.setMajor(propertyValue);
		}
		else if(PROPERTY_RESOLVED.equals(propertyName))
		{
			issue.setResolved(propertyValue);
		}
		else
		{
			throw new RuntimeException("Invalid property");
		}

		caseManagementIssueDao.persist(issue);

		CaseManagementIssueConverter converter = new CaseManagementIssueConverter();

		return converter.getAsTransferObject(issue);
	}

	public CaseManagementIssueTo1 updateIssue(
			int demographicNo,
			int issueId,
			int newIssueId
	)
	{
		org.oscarehr.encounterNote.model.CaseManagementIssue caseManagementIssue =
				caseManagementIssueDao.findByIssueId(demographicNo, new Long(issueId));

		Issue newIssue = issueDao.find(new Long(newIssueId));

		caseManagementIssue.setIssue(newIssue);

		caseManagementIssueDao.persist(caseManagementIssue);

		CaseManagementIssueConverter converter = new CaseManagementIssueConverter();
		return converter.getAsTransferObject(caseManagementIssue);
	}

	public List<CaseManagementIssue> getIssues(
			LoggedInInfo loggedInInfo,
			String demographicNo,
			String providerNo,
			String programId,
			String type
	)
	{
		// grab all of the diseases associated with patient and add a list item for each
		List<CaseManagementIssue> issues = null;

		issues = caseManagementMgr.getIssues(Integer.parseInt(demographicNo));
		issues = caseManagementMgr.filterIssues(loggedInInfo, providerNo, issues, programId);

		List<CaseManagementIssue> filteredIssues = new ArrayList<CaseManagementIssue>();

		//only list resolved issues
		for(CaseManagementIssue issue : issues)
		{
			if(containsIssue(CppUtils.cppCodes, issue.getIssue().getCode()))
			{
				continue;
			}

			String[] ticklerCode = {Issue.SUMMARY_CODE_TICKLER_NOTE};
			if(containsIssue(ticklerCode, issue.getIssue().getCode()))
			{
				continue;
			}

			if(org.oscarehr.encounterNote.model.CaseManagementIssue.ISSUE_FILTER_RESOLVED.equals(type))
			{
				if(!issue.isResolved())
				{
					continue;
				}
			}
			else if(org.oscarehr.encounterNote.model.CaseManagementIssue.ISSUE_FILTER_UNRESOLVED.equals(type))
			{
				if(issue.isResolved())
				{
					continue;
				}
			}

			boolean dup = false;
			for (CaseManagementIssue tmp : filteredIssues)
			{
				if (issue.getIssue_id() == tmp.getIssue_id())
				{
					dup = true;
					break;
				}
			}

			if (dup)
			{
				continue;
			}

			filteredIssues.add(issue);
		}

		return filteredIssues;
	}

	public List<CaseManagementIssue> getResolvedIssues(
			LoggedInInfo loggedInInfo,
			String demographicNo,
			String providerNo,
			String programId
	)
	{
		return getIssues(loggedInInfo, demographicNo, providerNo, programId,
				org.oscarehr.encounterNote.model.CaseManagementIssue.ISSUE_FILTER_RESOLVED);
	}

	public List<CaseManagementIssue> getUnresolvedIssues(
			LoggedInInfo loggedInInfo,
			String demographicNo,
			String providerNo,
			String programId
	)
	{
		return getIssues(loggedInInfo, demographicNo, providerNo, programId,
				org.oscarehr.encounterNote.model.CaseManagementIssue.ISSUE_FILTER_UNRESOLVED);
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
