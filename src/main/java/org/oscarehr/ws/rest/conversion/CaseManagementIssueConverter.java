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
package org.oscarehr.ws.rest.conversion;

import org.oscarehr.casemgmt.model.CaseManagementIssue;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.ws.rest.to.model.CaseManagementIssueTo1;

public class CaseManagementIssueConverter extends AbstractConverter<CaseManagementIssue, CaseManagementIssueTo1> {
	
	@Override
	public CaseManagementIssue getAsDomainObject(LoggedInInfo loggedInInfo, CaseManagementIssueTo1 t) throws ConversionException {
	// TODO-legacy Auto-generated method stub
	return null;
	}

	@Override
	public CaseManagementIssueTo1 getAsTransferObject(LoggedInInfo loggedInInfo, CaseManagementIssue issue) throws ConversionException {
		CaseManagementIssueTo1 issueTo = new CaseManagementIssueTo1();		
		IssueConverter issueConverter = new IssueConverter();
		issueTo.setAcute(issue.isAcute());
		issueTo.setCertain(issue.isCertain());
		issueTo.setDemographic_no(""+issue.getDemographic_no());
		issueTo.setId(issue.getId());
		issueTo.setIssue(issueConverter.getAsTransferObject(loggedInInfo, issue.getIssue()));
		issueTo.setIssue_id(issue.getIssue_id());
		issueTo.setMajor(issue.isMajor());
		issueTo.setProgram_id(issue.getProgram_id());
		issueTo.setResolved(issue.isResolved());
		issueTo.setType(issue.getType());
		// Not sure what this one links to t.setUnchecked(issue.);
		issueTo.setUpdate_date(issue.getUpdate_date());
		return issueTo;
	}

	// Probably not great to mix models in a single converter, but I can't think of anything better at the moment.
	public static CaseManagementIssueTo1 getAsTransferObject(org.oscarehr.encounterNote.model.CaseManagementIssue issue)
	{
		CaseManagementIssueTo1 issueTo = new CaseManagementIssueTo1();

		issueTo.setAcute(issue.getAcute());
		issueTo.setCertain(issue.getCertain());
		issueTo.setDemographic_no(issue.getDemographic().getId().toString());
		issueTo.setId(issue.getId());
		issueTo.setIssue(IssueConverter.getAsTransferObject(issue.getIssue()));
		issueTo.setIssue_id(issue.getIssue().getIssueId());
		issueTo.setMajor(issue.getMajor());
		issueTo.setProgram_id(issue.getProgramId());
		issueTo.setResolved(issue.getResolved());
		issueTo.setType(issue.getType());
		issueTo.setUpdate_date(issue.getUpdateDate());

		return issueTo;
	}
}
