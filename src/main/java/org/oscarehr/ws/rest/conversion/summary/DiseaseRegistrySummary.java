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

package org.oscarehr.ws.rest.conversion.summary;

import org.apache.commons.lang3.ObjectUtils;
import org.oscarehr.dataMigration.model.dx.DxRecord;
import org.oscarehr.dataMigration.model.dx.DxRecord.Status;
import org.oscarehr.dx.service.DxResearchService;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.ws.rest.to.model.SummaryItemTo1;
import org.oscarehr.ws.rest.to.model.SummaryTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DiseaseRegistrySummary implements Summary
{
	@Autowired
	private DxResearchService dxResearchService;

	@Override
	public SummaryTo1 getSummary(LoggedInInfo loggedInInfo, Integer demographicNo,
		String summaryCode)
	{
		List<DxRecord> dxCodes = dxResearchService.getAssignedDxRecords(demographicNo);
		List<SummaryItemTo1> diagnosisList = dxCodes.stream()
			.filter(code -> code.getStatus().equals(Status.ACTIVE))
			.map(this::mapDxCode)
			.sorted((SummaryItemTo1 i1, SummaryItemTo1 i2) -> ObjectUtils.compare(i2.getDate(), i1.getDate(), true))
			.collect(Collectors.toList());

		for(int i = 0; i < diagnosisList.size(); i++){
			diagnosisList.get(i).setId(i);
		}

		SummaryTo1 summaryGroup = new SummaryTo1("Disease Registry", 0, SummaryTo1.DISEASE_REGISTRY_CODE);
		summaryGroup.setSummaryItem(diagnosisList);
		return summaryGroup;
	}

	public SummaryItemTo1 mapDxCode(DxRecord record)
	{
		SummaryItemTo1 summaryItem = new SummaryItemTo1(0, record.getDxCode().getDescription(), "","dx_reg");
		summaryItem.setDate(ConversionUtils.toLegacyDate(record.getStartDate()));

		return summaryItem;
	}
}