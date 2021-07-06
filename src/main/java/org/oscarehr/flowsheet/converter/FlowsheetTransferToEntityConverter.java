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
import org.oscarehr.decisionSupport2.dao.DsRuleDao;
import org.oscarehr.decisionSupport2.transfer.DsRuleUpdateInput;
import org.oscarehr.flowsheet.dao.FlowsheetDao;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class FlowsheetTransferToEntityConverter extends AbstractModelConverter<FlowsheetCreateTransfer, Flowsheet>
{
	@Autowired
	private FlowsheetDao flowsheetDao;

	@Autowired
	private DsRuleDao dsRuleDao;

	@Override
	public Flowsheet convert(FlowsheetCreateTransfer input)
	{
		if(input == null)
		{
			return null;
		}
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

		// diff the groups and items within the flowsheet
		List<FlowsheetItemGroup> currentFlowsheetItemGroups = Optional.ofNullable(flowsheet.getFlowsheetItemGroups()).orElse(new ArrayList<>());

		if(input.getFlowsheetItemGroups() != null)
		{
			currentFlowsheetItemGroups.forEach((group) -> mergeExistingGroup(group, input.getFlowsheetItemGroups()));
			currentFlowsheetItemGroups.addAll(
					input.getFlowsheetItemGroups()
							.stream()
							.filter((group) -> (group.getId() == null))
							.map((group) -> createNewGroup(group, flowsheet))
							.collect(Collectors.toList())
			);
		}

		flowsheet.setFlowsheetItemGroups(currentFlowsheetItemGroups);
		flowsheet.setFlowsheetItems(currentFlowsheetItemGroups
				.stream()
				.filter((group) -> group.getFlowsheetItems() != null)
				.flatMap((group) -> group.getFlowsheetItems().stream())
				.collect(Collectors.toList()));
		return flowsheet;
	}

	protected FlowsheetItemGroup mergeExistingGroup(FlowsheetItemGroup existingGroupEntity, List<FlowsheetItemGroupCreateUpdateTransfer> groupInputList)
	{
		Optional<FlowsheetItemGroupCreateUpdateTransfer> matchingInput = groupInputList
				.stream()
				.filter((input) -> existingGroupEntity.getId().equals(input.getId()))
				.findFirst();

		List<FlowsheetItem> flowsheetItems = Optional.ofNullable(existingGroupEntity.getFlowsheetItems()).orElse(new ArrayList<>());
		if(matchingInput.isPresent())
		{
			FlowsheetItemGroupCreateUpdateTransfer input = matchingInput.get();
			BeanUtils.copyProperties(input, existingGroupEntity, "id", "flowsheetItems");

			flowsheetItems.forEach((item) -> mergeExistingItem(item, input.getFlowsheetItems()));
			flowsheetItems.addAll(
					input.getFlowsheetItems()
							.stream()
							.filter((item) -> (item.getId() == null))
							.map((item) -> createNewItem(item, existingGroupEntity.getFlowsheet(), existingGroupEntity))
							.collect(Collectors.toList())
			);
			existingGroupEntity.setFlowsheetItems(flowsheetItems);
		}
		else
		{
			existingGroupEntity.setDeletedAt(LocalDateTime.now());
			flowsheetItems.forEach((item) -> item.setDeletedAt(LocalDateTime.now()));
			existingGroupEntity.setFlowsheetItems(flowsheetItems);
		}
		return existingGroupEntity;
	}
	protected FlowsheetItemGroup createNewGroup(FlowsheetItemGroupCreateUpdateTransfer groupInput, Flowsheet flowsheetEntity)
	{
		FlowsheetItemGroup group = new FlowsheetItemGroup();
		group.setFlowsheet(flowsheetEntity);
		BeanUtils.copyProperties(groupInput, group, "id", "flowsheetItems");

		// all items in a new group will be new
		List<FlowsheetItemCreateUpdateTransfer> inputItems = Optional.ofNullable(groupInput.getFlowsheetItems()).orElse(new ArrayList<>());
		group.setFlowsheetItems(
				inputItems
				.stream()
				.map((item) -> createNewItem(item, flowsheetEntity, group))
				.collect(Collectors.toList())
		);
		return group;
	}

	protected FlowsheetItem mergeExistingItem(FlowsheetItem existingItemEntity, List<FlowsheetItemCreateUpdateTransfer> itemInputList)
	{
		Optional<FlowsheetItemCreateUpdateTransfer> matchingInput = itemInputList
				.stream()
				.filter((input) -> existingItemEntity.getId().equals(input.getId()))
				.findFirst();

		if(matchingInput.isPresent())
		{
			FlowsheetItemCreateUpdateTransfer input = matchingInput.get();
			BeanUtils.copyProperties(input, existingItemEntity, "id");
			existingItemEntity.setDsRules(input.getRules().stream().map((rule) -> dsRuleDao.find(rule.getId())).collect(Collectors.toSet()));
		}
		else
		{
			existingItemEntity.setDeletedAt(LocalDateTime.now());
		}
		return existingItemEntity;
	}

	protected FlowsheetItem createNewItem(FlowsheetItemCreateUpdateTransfer itemInput, Flowsheet flowsheetEntity, FlowsheetItemGroup groupEntity)
	{
		FlowsheetItem item = new FlowsheetItem();
		item.setFlowsheet(flowsheetEntity);
		item.setFlowsheetItemGroup(groupEntity);
		BeanUtils.copyProperties(itemInput, item);

		List<DsRuleUpdateInput> itemRules = Optional.ofNullable(itemInput.getRules()).orElse(new ArrayList<>());
		item.setDsRules(itemRules.stream().map((rule) -> dsRuleDao.find(rule.getId())).collect(Collectors.toSet()));

		return item;
	}
}
