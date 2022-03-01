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

import org.oscarehr.hospitalReportManager.dto.HRMDemographicDocument;
import org.oscarehr.hospitalReportManager.model.HRMDocument;
import org.oscarehr.hospitalReportManager.service.HRMService;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.security.model.Permission;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.ws.rest.to.model.SummaryItemTo1;
import org.oscarehr.ws.rest.to.model.SummaryTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


@Component
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class HRMDocumentSummary implements Summary
{
	@Autowired
	HRMService hrmService;

	@Autowired
	SecurityInfoManager securityInfoManager;
	
	@Override
	public SummaryTo1 getSummary(LoggedInInfo loggedInInfo, Integer demographicNo, String summaryCode)
	{
		SummaryTo1 hrmDocumentSummary = new SummaryTo1("hrmdocument", 0, SummaryTo1.HRM_DOCUMENTS);

		// Extra paranoid, so another check at this level.
		if (!securityInfoManager.hasPrivileges(loggedInInfo.getLoggedInProviderNo(), demographicNo, Permission.HRM_READ))
		{
			return hrmDocumentSummary;
		}

		// Format of the Map is <"sendingFacility:facilityReportNumber:DeliverToId", HRMDemographicDocument">
		List<HRMDemographicDocument> documents = hrmService.getHrmDocumentsForDemographic(demographicNo);
		List<SummaryItemTo1> hrmItems = convertToSummaryItems(documents);
		hrmDocumentSummary.setSummaryItem(hrmItems);
		
		hrmDocumentSummary.setSummaryItem(hrmItems);
		
		return hrmDocumentSummary;
	}
	
	/**
	 * Convert the unsorted list HRMDocuments into a list of SummaryItems ordered by report date
	 * @param documents
	 * @return
	 */
	private List<SummaryItemTo1> convertToSummaryItems(List<HRMDemographicDocument> documents)
	{
		List<SummaryItemTo1> summaryItems = new ArrayList<>();
		
		for (HRMDemographicDocument hrmDemoDocument : documents)
		{
			HRMDocument hrmDocument = hrmDemoDocument.getHrmDocument();
			
			SummaryItemTo1 summary = new SummaryItemTo1();
			summary.setId(hrmDocument.getId());
			summary.setType("hrm");
			
			summary.setDisplayName(formatDisplayName(hrmDocument));
			summary.setDate(hrmDocument.getReportDate());
			
			String utf8 = StandardCharsets.UTF_8.toString();
			summary.setAction(String.format("../hospitalReportManager/Display.do?id=%d", hrmDocument.getId()));

			summaryItems.add(summary);
		}

		// already sorted by database query
		return summaryItems;
	}
	
	private String formatDisplayName(HRMDocument document)
	{
		String prefix = "";
		
		if (document.getReportStatus() == null)
		{
			prefix = "(Unknown) ";
		}
		else if (document.getReportStatus().equals(HRMDocument.STATUS.CANCELLED))
		{
			prefix = "(Cancelled) ";
		}
		
		String body = document.getDescription();
		
		return String.format("%s%s", prefix, body);
	}
}