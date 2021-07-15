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
package org.oscarehr.flowsheet.service;


import org.oscarehr.common.dao.Icd9Dao;
import org.oscarehr.common.model.Icd9;
import org.oscarehr.dataMigration.model.dx.DxCode;
import org.oscarehr.decisionSupport2.dao.DsRuleDao;
import org.oscarehr.decisionSupport2.transfer.DsRuleUpdateInput;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.flowsheet.converter.FlowsheetEntityToModelConverter;
import org.oscarehr.flowsheet.dao.FlowsheetDao;
import org.oscarehr.flowsheet.entity.FlowsheetItem;
import org.oscarehr.flowsheet.entity.FlowsheetItemGroup;
import org.oscarehr.flowsheet.model.Flowsheet;
import org.oscarehr.flowsheet.search.FlowsheetCriteriaSearch;
import org.oscarehr.flowsheet.transfer.FlowsheetCreateTransfer;
import org.oscarehr.flowsheet.transfer.FlowsheetItemCreateUpdateTransfer;
import org.oscarehr.flowsheet.transfer.FlowsheetItemGroupCreateUpdateTransfer;
import org.oscarehr.flowsheet.transfer.FlowsheetUpdateTransfer;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.ws.rest.response.RestSearchResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class FlowsheetService
{
	@Autowired
	private FlowsheetDao flowsheetDao;

	@Autowired
	private ProviderDataDao providerDao;

	@Autowired
	private DemographicDao demographicDao;

	@Autowired
	private DsRuleDao dsRuleDao;

	@Autowired
	private Icd9Dao icd9Dao;

	@Autowired
	private FlowsheetEntityToModelConverter flowsheetEntityToModelConverter;

	public RestSearchResponse<Flowsheet> executeCriteriaSearch(FlowsheetCriteriaSearch criteriaSearch, int page, int perPage)
	{
		List<org.oscarehr.flowsheet.entity.Flowsheet> flowsheets = flowsheetDao.criteriaSearch(criteriaSearch);
		int total = flowsheetDao.criteriaSearchCount(criteriaSearch);

		return RestSearchResponse.successResponse(flowsheetEntityToModelConverter.convert(flowsheets), page, perPage, total);
	}

	public Flowsheet addNewFlowsheetCopy(String creatingProviderId, Integer flowsheetIdToCopy)
	{
		return addNewFlowsheetCopy(
				creatingProviderId,
				flowsheetIdToCopy,
				Optional.empty(),
				Optional.empty(),
				Optional.of(" (copy)"));
	}

	public Flowsheet addNewProviderFlowsheetCopy(String creatingProviderId, Integer flowsheetIdToCopy, String ownerProviderId)
	{
		ProviderData providerData = providerDao.find(ownerProviderId);
		String suffix = " (copy for " + providerData.getDisplayName() + ")";
		return addNewFlowsheetCopy(
				creatingProviderId,
				flowsheetIdToCopy,
				Optional.of(providerData),
				Optional.empty(),
				Optional.of(suffix));
	}

	public Flowsheet addNewDemographicFlowsheetCopy(String creatingProviderId, Integer flowsheetIdToCopy, Integer ownerDemographicId)
	{
		Demographic demographic = demographicDao.find(ownerDemographicId);
		String suffix = " (copy for " + demographic.getDisplayName() + ")";
		return addNewFlowsheetCopy(
				creatingProviderId,
				flowsheetIdToCopy,
				Optional.empty(),
				Optional.of(demographic),
				Optional.of(suffix));
	}

	public Flowsheet addNewFlowsheet(String creatingProviderId, FlowsheetCreateTransfer creationTransfer)
	{
		org.oscarehr.flowsheet.entity.Flowsheet entity = new org.oscarehr.flowsheet.entity.Flowsheet();
		entity.setName(creationTransfer.getName());
		entity.setDescription(creationTransfer.getDescription());
		entity.setEnabled(creationTransfer.isEnabled());

		List<FlowsheetItemGroup> flowsheetGroups = Optional.ofNullable(creationTransfer.getFlowsheetItemGroups()).orElse(new ArrayList<>())
				.stream()
				.map((group) -> createNewGroup(group, entity))
				.collect(Collectors.toList());
		entity.setFlowsheetItemGroups(flowsheetGroups);
		entity.setFlowsheetItems(flowsheetGroups
				.stream()
				.filter((group) -> group.getFlowsheetItems() != null)
				.flatMap((group) -> group.getFlowsheetItems().stream())
				.collect(Collectors.toList()));

		entity.setIcd9Triggers(convertIcd9Triggers(creationTransfer.getTriggerCodes()));
		entity.setCreatedBy(creatingProviderId);
		flowsheetDao.persist(entity);
		return flowsheetEntityToModelConverter.convert(entity);
	}

	public Flowsheet updateFlowsheet(String updatingProviderId, Integer flowsheetId, FlowsheetUpdateTransfer updateTransfer)
	{
		org.oscarehr.flowsheet.entity.Flowsheet entity = flowsheetDao.find(flowsheetId);
		entity.setName(updateTransfer.getName());
		entity.setDescription(updateTransfer.getDescription());
		entity.setEnabled(updateTransfer.isEnabled());

		// diff the groups and items within the flowsheet
		List<FlowsheetItemGroup> currentFlowsheetItemGroups = entity.getFlowsheetItemGroups();

		if(updateTransfer.getFlowsheetItemGroups() != null)
		{
			currentFlowsheetItemGroups.forEach((group) -> mergeExistingGroup(updatingProviderId, group, updateTransfer.getFlowsheetItemGroups()));
			currentFlowsheetItemGroups.addAll(
					updateTransfer.getFlowsheetItemGroups()
							.stream()
							.filter((group) -> (group.getId() == null))
							.map((group) -> createNewGroup(group, entity))
							.collect(Collectors.toList())
			);
		}

		entity.setFlowsheetItemGroups(currentFlowsheetItemGroups);
		entity.setFlowsheetItems(currentFlowsheetItemGroups
				.stream()
				.filter((group) -> group.getFlowsheetItems() != null)
				.flatMap((group) -> group.getFlowsheetItems().stream())
				.collect(Collectors.toList()));

		entity.setIcd9Triggers(convertIcd9Triggers(updateTransfer.getTriggerCodes()));
		flowsheetDao.merge(entity);

		//TODO figure out how to return the new version of the entity correctly.
		// reload seems to prevent the merge cascade from updating entities properly
		// so for now we must remove deleted items from the returned entity
		entity.setFlowsheetItemGroups(
				entity.getFlowsheetItemGroups()
						.stream()
						.filter((group) -> group.getDeletedAt() == null)
						.collect(Collectors.toList()));
		entity.getFlowsheetItemGroups().forEach(
				(group) -> group.setFlowsheetItems(
						group.getFlowsheetItems()
								.stream()
								.filter((item) -> item.getDeletedAt() == null)
								.collect(Collectors.toList())));
		entity.setFlowsheetItems(
				entity.getFlowsheetItems()
						.stream()
						.filter((item) -> item.getDeletedAt() == null)
						.collect(Collectors.toList()));

		return flowsheetEntityToModelConverter.convert(entity);
	}

	public Flowsheet getFlowsheet(Integer flowsheetId)
	{
		return flowsheetEntityToModelConverter.convert(flowsheetDao.find(flowsheetId));
	}

	public void deleteFlowsheet(String deletingProviderId, Integer flowsheetId)
	{
		org.oscarehr.flowsheet.entity.Flowsheet flowsheetEntity = flowsheetDao.find(flowsheetId);
		flowsheetEntity.setDeletedAt(LocalDateTime.now());
		flowsheetEntity.setDeletedBy(deletingProviderId);
		flowsheetEntity.setUpdatedBy(deletingProviderId);

		flowsheetEntity.getFlowsheetItemGroups().forEach((group) ->
		{
			group.setDeletedAt(LocalDateTime.now());
			group.setDeletedBy(deletingProviderId);
		});
		flowsheetEntity.getFlowsheetItems().forEach((item) ->
		{
			item.setDeletedAt(LocalDateTime.now());
			item.setDeletedBy(deletingProviderId);
		});

		flowsheetDao.merge(flowsheetEntity);
	}

	public boolean setFlowsheetEnabled(String updatingProviderId, Integer flowsheetId, boolean enabled)
	{
		org.oscarehr.flowsheet.entity.Flowsheet flowsheetEntity = flowsheetDao.find(flowsheetId);
		flowsheetEntity.setEnabled(enabled);
		flowsheetEntity.setUpdatedBy(updatingProviderId);
		flowsheetDao.merge(flowsheetEntity);
		return flowsheetEntity.isEnabled();
	}

	private Flowsheet addNewFlowsheetCopy(String creatingProviderId,
	                                      Integer flowsheetIdToCopy,
	                                      Optional<ProviderData> providerOwner,
	                                      Optional<Demographic> demographicOwner,
	                                      Optional<String> nameSuffix)
	{
		org.oscarehr.flowsheet.entity.Flowsheet flowsheetToCopy = flowsheetDao.find(flowsheetIdToCopy);
		org.oscarehr.flowsheet.entity.Flowsheet entity = new org.oscarehr.flowsheet.entity.Flowsheet(flowsheetToCopy); // copy constructor
		entity.setCreatedBy(creatingProviderId);
		entity.setUpdatedBy(creatingProviderId);
		entity.setSystemManaged(false); // copies are never system managed

		entity.setOwnerProvider(providerOwner.orElse(null));
		entity.setOwnerDemographic(demographicOwner.orElse(null));
		nameSuffix.ifPresent((suffix) -> entity.setName(flowsheetToCopy.getName() + suffix));

		// for now, we don't want multiple levels of parent 'chaining', so all copies will reference the original's parent.
		// if there is no parent then we can set the version being copied as the 'top level' parent
		if(!flowsheetToCopy.getOptionalParentFlowsheet().isPresent())
		{
			entity.setParentFlowsheet(flowsheetToCopy);
		}

		flowsheetDao.persist(entity);
		return flowsheetEntityToModelConverter.convert(entity);
	}

	private Set<Icd9> convertIcd9Triggers(List<DxCode> triggerCodes)
	{
		return triggerCodes.stream().map((input) -> icd9Dao.findByCode(input.getCode())).collect(Collectors.toSet());
	}

	private FlowsheetItemGroup mergeExistingGroup(String currentUserId, FlowsheetItemGroup existingGroupEntity, List<FlowsheetItemGroupCreateUpdateTransfer> groupInputList)
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

			flowsheetItems.forEach((item) -> mergeExistingItem(currentUserId, item, input.getFlowsheetItems()));
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
			existingGroupEntity.setDeletedBy(currentUserId);
			flowsheetItems.forEach((item) -> {
				item.setDeletedAt(LocalDateTime.now());
				item.setDeletedBy(currentUserId);
			});
			existingGroupEntity.setFlowsheetItems(flowsheetItems);
		}
		return existingGroupEntity;
	}
	private FlowsheetItemGroup createNewGroup(FlowsheetItemGroupCreateUpdateTransfer groupInput,
	                                          org.oscarehr.flowsheet.entity.Flowsheet flowsheetEntity)
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

	private FlowsheetItem mergeExistingItem(String currentUserId,
	                                        FlowsheetItem existingItemEntity,
	                                        List<FlowsheetItemCreateUpdateTransfer> itemInputList)
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
			existingItemEntity.setDeletedBy(currentUserId);
		}
		return existingItemEntity;
	}

	private FlowsheetItem createNewItem(FlowsheetItemCreateUpdateTransfer itemInput,
	                                    org.oscarehr.flowsheet.entity.Flowsheet flowsheetEntity,
	                                    FlowsheetItemGroup groupEntity)
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
