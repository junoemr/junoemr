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
import org.oscarehr.flowsheet.dao.FlowsheetDao;
import org.oscarehr.flowsheet.dao.FlowsheetItemDao;
import org.oscarehr.flowsheet.entity.Flowsheet;
import org.oscarehr.flowsheet.entity.FlowsheetItem;
import org.oscarehr.flowsheet.entity.FlowsheetItemGroup;
import org.oscarehr.flowsheet.transfer.FlowsheetCreateTransfer;
import org.oscarehr.flowsheet.transfer.FlowsheetItemCreateUpdateTransfer;
import org.oscarehr.flowsheet.transfer.FlowsheetItemGroupCreateUpdateTransfer;
import org.oscarehr.flowsheet.transfer.FlowsheetUpdateTransfer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Component
public class FlowsheetTransferToEntityConverter extends AbstractModelConverter<FlowsheetCreateTransfer, Flowsheet>
{
	@Autowired
	private FlowsheetDao flowsheetDao;

	@Autowired
	private FlowsheetItemDao flowsheetItemDao;

	@Override
	public Flowsheet convert(FlowsheetCreateTransfer input)
	{
		// find existing flowsheet entity or create a new one based on transfer type
		Flowsheet flowsheet;
		if(input instanceof FlowsheetUpdateTransfer)
		{
			FlowsheetUpdateTransfer updateTransfer = (FlowsheetUpdateTransfer) input;
			flowsheet = flowsheetDao.find(updateTransfer.getId());
		}
		else
		{
			flowsheet = new Flowsheet();
		}
		BeanUtils.copyProperties(input, flowsheet, "id", "flowsheetItemGroups");

		List<FlowsheetItemGroup> existingGroupEntities = flowsheet.getFlowsheetItemGroups();


		List<FlowsheetItemGroup> groupEntities = new LinkedList<>();
		List<FlowsheetItem> allFlowsheetItems = new LinkedList<>();

		// perform a diff of groups. a group with one item and no name is a fake, and the item will be treated as an ungrouped item
		for(FlowsheetItemGroupCreateUpdateTransfer groupInput: input.getFlowsheetItemGroups())
		{
			FlowsheetItemGroup group;

			// existing group - find the existing entity
			if(Optional.ofNullable(groupInput.getId()).isPresent())
			{
				group = existingGroupEntities.stream()
						.filter((entity) -> groupInput.getId().equals(entity.getId()))
						.findAny().orElseThrow(() -> new IllegalStateException("flowsheet group has id with no prior flowsheet association: " + groupInput.getId()));
			}
			// item without a group (fake group)
			else if(groupInput.getFlowsheetItems().size() == 1 && !Optional.ofNullable(groupInput.getName()).isPresent())
			{
				allFlowsheetItems.add(convertItem(groupInput.getFlowsheetItems().get(0), flowsheet, null));
				continue;
			}
			else // new group
			{
				group = new FlowsheetItemGroup();
				group.setFlowsheet(flowsheet);
			}
			BeanUtils.copyProperties(groupInput, group, "id", "flowsheetItems");

			// convert items within each group
			List<FlowsheetItem> itemEntities = new LinkedList<>();
			groupInput.getFlowsheetItems().forEach((itemInput) -> itemEntities.add(convertItem(itemInput, flowsheet, group)));
			group.setFlowsheetItems(itemEntities);
			groupEntities.add(group);
		}

		flowsheet.setFlowsheetItemGroups(groupEntities);
		flowsheet.setFlowsheetItems(allFlowsheetItems);

		return flowsheet;
	}

	private FlowsheetItem convertItem(FlowsheetItemCreateUpdateTransfer itemInput, Flowsheet flowsheetEntity, FlowsheetItemGroup groupEntity)
	{
		FlowsheetItem item;
		if(Optional.ofNullable(itemInput.getId()).isPresent())
		{
			item = flowsheetItemDao.find(itemInput.getId());
		}
		else
		{
			item = new FlowsheetItem();
			item.setFlowsheet(flowsheetEntity);
			item.setFlowsheetItemGroup(groupEntity);
		}
		BeanUtils.copyProperties(itemInput, item, "id");

		return item;
	}
}
