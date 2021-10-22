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
package org.oscarehr.ws.rest.conversion.summary;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.oscarehr.casemgmt.model.CaseManagementIssue;
import org.oscarehr.casemgmt.model.CaseManagementNote;
import org.oscarehr.casemgmt.model.Issue;
import org.oscarehr.dataMigration.model.dx.DxCode;
import org.oscarehr.dataMigration.model.dx.DxRecord;
import org.oscarehr.dx.service.DxResearchService;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.ws.rest.to.model.SummaryItemTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class OngoingConcernDxRegSummary extends IssueNoteSummary implements Summary  {

	@Autowired
	private DxResearchService dxResearchService;

	protected void getSummaryListForIssuedNotes(LoggedInInfo loggedInInfo,Integer demographicNo, List<SummaryItemTo1> list, String[] issueCodes){

		List<DxRecord> dxRecords = dxResearchService.getAssignedDxRecords(demographicNo);
		List<Issue> issueList = new ArrayList<Issue>();
		for (int j = 0; j < issueCodes.length; ++j) {
			issueList.addAll(caseManagementMgr.getIssueInfoByCode(loggedInInfo.getLoggedInProviderNo(), issueCodes[j]));
		}
		String[] issueIds = getIssueIds(issueList);
		
		Collection<CaseManagementNote> notes = caseManagementMgr.getActiveNotes(
				"" + demographicNo, issueIds);

		String cppExts = "";

		for(CaseManagementNote note : notes) {
			String classification = null;
			Set<CaseManagementIssue> issuesForNote = note.getIssues();
			StringBuilder issueBuilder = new StringBuilder();
			for (CaseManagementIssue s : issuesForNote) {
				issueBuilder.append(s.getIssue().getCode());
				for(DxRecord dx: dxRecords) {
					DxCode dxCode = dx.getDxCode();
					Issue issue = s.getIssue();
					if(issue.getType().equals(dxCode.getCodingSystem().getValue()) && issue.getCode().equals(dxCode.getCode())){
						classification = "Dx: " + dxCode.getDescription();
					}
				}
			}
			String issueString = issueBuilder.toString();

			// Note: This statement won't ever get hit because isCppItem checks for a specific list of issues
			if( preferenceManager.isCppItem(issueString) && preferenceManager.isCustomSummaryEnabled(loggedInInfo) ){
				// Note: This statement won't ever get hit because getCppExtsItem checks for a specific list of issues
				cppExts = preferenceManager.getCppExtsItem(loggedInInfo, caseManagementMgr.getExtByNote(note.getId()), issueString);
			}

			SummaryItemTo1 summaryItem = new SummaryItemTo1(0, note.getNote() + cppExts,"action","notes"+issueString);
			summaryItem.setDate(note.getObservation_date());
			summaryItem.setEditor(note.getProviderName());
			summaryItem.setNoteId(note.getId());
			if(classification != null){
				summaryItem.setClassification(classification);
			}

			list.add(summaryItem);
		}

		// Reverse date sorting with null having lowest priority (ie: will be last).
		list.sort((SummaryItemTo1 i1, SummaryItemTo1 i2) -> ObjectUtils.compare(i2.getDate(), i1.getDate()));
		
		for(int i = 0; i < list.size(); i++){
			list.get(i).setId(i);
		}

	}
}