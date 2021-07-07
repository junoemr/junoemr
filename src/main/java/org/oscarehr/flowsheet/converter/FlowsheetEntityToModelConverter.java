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
package org.oscarehr.flowsheet.converter;

import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.common.model.Icd9;
import org.oscarehr.dataMigration.model.dx.DxCode;
import org.oscarehr.dx.converter.Icd9EntityToDxCodeConverter;
import org.oscarehr.flowsheet.entity.FlowsheetItem;
import org.oscarehr.flowsheet.model.Flowsheet;
import org.oscarehr.flowsheet.model.FlowsheetItemGroup;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Component
public class FlowsheetEntityToModelConverter extends AbstractModelConverter<org.oscarehr.flowsheet.entity.Flowsheet, org.oscarehr.flowsheet.model.Flowsheet>
{
	@Autowired
	private FlowsheetItemEntityToModelConverter flowsheetItemEntityToModelConverter;

	@Autowired
	private Icd9EntityToDxCodeConverter icd9EntityToDxCodeConverter;

	@Override
	public Flowsheet convert(org.oscarehr.flowsheet.entity.Flowsheet input)
	{
		Flowsheet flowsheetModel = new Flowsheet();
		BeanUtils.copyProperties(input, flowsheetModel, "flowsheetItems", "flowsheetItemGroups", "icd9Triggers");

		flowsheetModel.setFlowsheetItemGroups(buildGroups(input));
		flowsheetModel.setTriggerCodes(buildTriggers(input));

		return flowsheetModel;
	}

	private List<FlowsheetItemGroup> buildGroups(org.oscarehr.flowsheet.entity.Flowsheet input)
	{
		// add items by group
		List<FlowsheetItemGroup> groups = new LinkedList<>();
		for(org.oscarehr.flowsheet.entity.FlowsheetItemGroup group: input.getFlowsheetItemGroups())
		{
			FlowsheetItemGroup groupModel = new FlowsheetItemGroup();
			groupModel.setId(group.getId());
			groupModel.setName(group.getName());
			groupModel.setDescription(group.getDescription());
			groupModel.setFlowsheetItems(flowsheetItemEntityToModelConverter.convert(group.getFlowsheetItems()));
			groups.add(groupModel);
		}

		// add un-grouped items
		for(FlowsheetItem item : input.getFlowsheetItems())
		{
			if(item.getFlowsheetItemGroup() == null)
			{
				List<org.oscarehr.flowsheet.model.FlowsheetItem> items = new ArrayList<>(1);
				items.add(flowsheetItemEntityToModelConverter.convert(item));
				FlowsheetItemGroup groupModel = new FlowsheetItemGroup();
				groupModel.setFlowsheetItems(items);
				groups.add(groupModel);
			}
		}
		return groups;
	}

	private List<DxCode> buildTriggers(org.oscarehr.flowsheet.entity.Flowsheet input)
	{
		Set<Icd9> icd9TriggerCodes = input.getIcd9Triggers();
		return icd9EntityToDxCodeConverter.convert(icd9TriggerCodes);
	}
}
