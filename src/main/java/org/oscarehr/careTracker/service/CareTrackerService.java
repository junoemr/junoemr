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
package org.oscarehr.careTracker.service;


import org.oscarehr.common.dao.Icd9Dao;
import org.oscarehr.common.model.Icd9;
import org.oscarehr.dataMigration.model.dx.DxCode;
import org.oscarehr.decisionSupport2.dao.DsRuleDao;
import org.oscarehr.decisionSupport2.transfer.DsRuleUpdateInput;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.careTracker.converter.CareTrackerEntityToModelConverter;
import org.oscarehr.careTracker.dao.CareTrackerDao;
import org.oscarehr.careTracker.entity.CareTrackerItem;
import org.oscarehr.careTracker.entity.CareTrackerItemGroup;
import org.oscarehr.careTracker.model.CareTracker;
import org.oscarehr.careTracker.search.CareTrackerCriteriaSearch;
import org.oscarehr.careTracker.transfer.CareTrackerCreateTransfer;
import org.oscarehr.careTracker.transfer.CareTrackerItemCreateUpdateTransfer;
import org.oscarehr.careTracker.transfer.CareTrackerItemGroupCreateUpdateTransfer;
import org.oscarehr.careTracker.transfer.CareTrackerUpdateTransfer;
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
public class CareTrackerService
{
	@Autowired
	private CareTrackerDao careTrackerDao;

	@Autowired
	private ProviderDataDao providerDao;

	@Autowired
	private DemographicDao demographicDao;

	@Autowired
	private DsRuleDao dsRuleDao;

	@Autowired
	private Icd9Dao icd9Dao;

	@Autowired
	private CareTrackerEntityToModelConverter careTrackerEntityToModelConverter;

	public RestSearchResponse<CareTracker> executeCriteriaSearch(CareTrackerCriteriaSearch criteriaSearch, int page, int perPage)
	{
		List<org.oscarehr.careTracker.entity.CareTracker> careTrackers = careTrackerDao.criteriaSearch(criteriaSearch);
		int total = careTrackerDao.criteriaSearchCount(criteriaSearch);

		return RestSearchResponse.successResponse(careTrackerEntityToModelConverter.convert(careTrackers), page, perPage, total);
	}

	public CareTracker addNewCareTrackerCopy(String creatingProviderId, Integer careTrackerIdToCopy)
	{
		return addNewCareTrackerCopy(
				creatingProviderId,
				careTrackerIdToCopy,
				Optional.empty(),
				Optional.empty(),
				Optional.of(" (copy)"));
	}

	public CareTracker addNewProviderCareTrackerCopy(String creatingProviderId, Integer careTrackerIdToCopy, String ownerProviderId)
	{
		ProviderData providerData = providerDao.find(ownerProviderId);
		String suffix = " (copy for " + providerData.getDisplayName() + ")";
		return addNewCareTrackerCopy(
				creatingProviderId,
				careTrackerIdToCopy,
				Optional.of(providerData),
				Optional.empty(),
				Optional.of(suffix));
	}

	public CareTracker addNewDemographicCareTrackerCopy(String creatingProviderId, Integer careTrackerIdToCopy, Integer ownerDemographicId)
	{
		Demographic demographic = demographicDao.find(ownerDemographicId);
		String suffix = " (copy for " + demographic.getDisplayName() + ")";
		return addNewCareTrackerCopy(
				creatingProviderId,
				careTrackerIdToCopy,
				Optional.empty(),
				Optional.of(demographic),
				Optional.of(suffix));
	}

	public CareTracker addNewCareTracker(String creatingProviderId, CareTrackerCreateTransfer creationTransfer)
	{
		org.oscarehr.careTracker.entity.CareTracker entity = new org.oscarehr.careTracker.entity.CareTracker();
		entity.setName(creationTransfer.getName());
		entity.setDescription(creationTransfer.getDescription());
		entity.setEnabled(creationTransfer.isEnabled());

		List<CareTrackerItemGroup> careTrackerItemGroups = Optional.ofNullable(creationTransfer.getCareTrackerItemGroups()).orElse(new ArrayList<>())
				.stream()
				.map((group) -> createNewGroup(group, entity))
				.collect(Collectors.toList());
		entity.setCareTrackerItemGroups(careTrackerItemGroups);
		entity.setCareTrackerItems(careTrackerItemGroups
				.stream()
				.filter((group) -> group.getCareTrackerItems() != null)
				.flatMap((group) -> group.getCareTrackerItems().stream())
				.collect(Collectors.toList()));

		entity.setIcd9Triggers(convertIcd9Triggers(creationTransfer.getTriggerCodes()));
		entity.setCreatedBy(creatingProviderId);
		careTrackerDao.persist(entity);
		return careTrackerEntityToModelConverter.convert(entity);
	}

	public CareTracker updateCareTracker(String updatingProviderId, Integer careTrackerId, CareTrackerUpdateTransfer updateTransfer)
	{
		org.oscarehr.careTracker.entity.CareTracker entity = careTrackerDao.find(careTrackerId);
		if(entity.isSystemManaged())
		{
			throw new IllegalArgumentException("System managed care tracker can not be updated");
		}

		entity.setName(updateTransfer.getName());
		entity.setDescription(updateTransfer.getDescription());
		entity.setEnabled(updateTransfer.isEnabled());

		// diff the groups and items within the care tracker
		List<CareTrackerItemGroup> currentCareTrackerItemGroups = entity.getCareTrackerItemGroups();

		if(updateTransfer.getCareTrackerItemGroups() != null)
		{
			currentCareTrackerItemGroups.forEach((group) -> mergeExistingGroup(updatingProviderId, group, updateTransfer.getCareTrackerItemGroups()));
			currentCareTrackerItemGroups.addAll(
					updateTransfer.getCareTrackerItemGroups()
							.stream()
							.filter((group) -> (group.getId() == null))
							.map((group) -> createNewGroup(group, entity))
							.collect(Collectors.toList())
			);
		}

		entity.setCareTrackerItemGroups(currentCareTrackerItemGroups);
		entity.setCareTrackerItems(currentCareTrackerItemGroups
				.stream()
				.filter((group) -> group.getCareTrackerItems() != null)
				.flatMap((group) -> group.getCareTrackerItems().stream())
				.collect(Collectors.toList()));

		entity.setIcd9Triggers(convertIcd9Triggers(updateTransfer.getTriggerCodes()));
		careTrackerDao.merge(entity);

		//TODO figure out how to return the new version of the entity correctly.
		// reload seems to prevent the merge cascade from updating entities properly
		// so for now we must remove deleted items from the returned entity
		entity.setCareTrackerItemGroups(
				entity.getCareTrackerItemGroups()
						.stream()
						.filter((group) -> group.getDeletedAt() == null)
						.collect(Collectors.toList()));
		entity.getCareTrackerItemGroups().forEach(
				(group) -> group.setCareTrackerItems(
						group.getCareTrackerItems()
								.stream()
								.filter((item) -> item.getDeletedAt() == null)
								.collect(Collectors.toList())));
		entity.setCareTrackerItems(
				entity.getCareTrackerItems()
						.stream()
						.filter((item) -> item.getDeletedAt() == null)
						.collect(Collectors.toList()));

		return careTrackerEntityToModelConverter.convert(entity);
	}

	public CareTracker getCareTracker(Integer careTrackerId)
	{
		return careTrackerEntityToModelConverter.convert(careTrackerDao.find(careTrackerId));
	}

	public void deleteCareTracker(String deletingProviderId, Integer careTrackerId)
	{
		org.oscarehr.careTracker.entity.CareTracker careTrackerEntity = careTrackerDao.find(careTrackerId);
		if(careTrackerEntity.isSystemManaged())
		{
			throw new IllegalArgumentException("System managed care tracker can not be deleted");
		}

		careTrackerEntity.setDeletedAt(LocalDateTime.now());
		careTrackerEntity.setDeletedBy(deletingProviderId);
		careTrackerEntity.setUpdatedBy(deletingProviderId);

		careTrackerEntity.getCareTrackerItemGroups().forEach((group) ->
		{
			group.setDeletedAt(LocalDateTime.now());
			group.setDeletedBy(deletingProviderId);
		});
		careTrackerEntity.getCareTrackerItems().forEach((item) ->
		{
			item.setDeletedAt(LocalDateTime.now());
			item.setDeletedBy(deletingProviderId);
		});

		careTrackerDao.merge(careTrackerEntity);
	}

	public boolean setCareTrackerEnabled(String updatingProviderId, Integer careTrackerId, boolean enabled)
	{
		org.oscarehr.careTracker.entity.CareTracker careTrackerEntity = careTrackerDao.find(careTrackerId);
		careTrackerEntity.setEnabled(enabled);
		careTrackerEntity.setUpdatedBy(updatingProviderId);
		careTrackerDao.merge(careTrackerEntity);
		return careTrackerEntity.isEnabled();
	}

	private CareTracker addNewCareTrackerCopy(String creatingProviderId,
	                                          Integer careTrackerIdToCopy,
	                                          Optional<ProviderData> providerOwner,
	                                          Optional<Demographic> demographicOwner,
	                                          Optional<String> nameSuffix)
	{
		org.oscarehr.careTracker.entity.CareTracker careTrackerToCopy = careTrackerDao.find(careTrackerIdToCopy);
		org.oscarehr.careTracker.entity.CareTracker entity = new org.oscarehr.careTracker.entity.CareTracker(careTrackerToCopy); // copy constructor
		entity.setCreatedBy(creatingProviderId);
		entity.setUpdatedBy(creatingProviderId);
		entity.setSystemManaged(false); // copies are never system managed

		entity.setOwnerProvider(providerOwner.orElse(null));
		entity.setOwnerDemographic(demographicOwner.orElse(null));
		nameSuffix.ifPresent((suffix) -> entity.setName(careTrackerToCopy.getName() + suffix));

		// for now, we don't want multiple levels of parent 'chaining', so all copies will reference the original's parent.
		// if there is no parent then we can set the version being copied as the 'top level' parent
		if(!careTrackerToCopy.getOptionalParentCareTracker().isPresent())
		{
			entity.setParentCareTracker(careTrackerToCopy);
		}

		careTrackerDao.persist(entity);
		return careTrackerEntityToModelConverter.convert(entity);
	}

	private Set<Icd9> convertIcd9Triggers(List<DxCode> triggerCodes)
	{
		return triggerCodes.stream().map((input) -> icd9Dao.findByCode(input.getCode())).collect(Collectors.toSet());
	}

	private CareTrackerItemGroup mergeExistingGroup(String currentUserId, CareTrackerItemGroup existingGroupEntity, List<CareTrackerItemGroupCreateUpdateTransfer> groupInputList)
	{
		Optional<CareTrackerItemGroupCreateUpdateTransfer> matchingInput = groupInputList
				.stream()
				.filter((input) -> existingGroupEntity.getId().equals(input.getId()))
				.findFirst();

		List<CareTrackerItem> careTrackerItems = Optional.ofNullable(existingGroupEntity.getCareTrackerItems()).orElse(new ArrayList<>());
		if(matchingInput.isPresent())
		{
			CareTrackerItemGroupCreateUpdateTransfer input = matchingInput.get();
			BeanUtils.copyProperties(input, existingGroupEntity, "id", "careTrackerItems");

			careTrackerItems.forEach((item) -> mergeExistingItem(currentUserId, item, input.getCareTrackerItems()));
			careTrackerItems.addAll(
					input.getCareTrackerItems()
							.stream()
							.filter((item) -> (item.getId() == null))
							.map((item) -> createNewItem(item, existingGroupEntity.getCareTracker(), existingGroupEntity))
							.collect(Collectors.toList())
			);
			existingGroupEntity.setCareTrackerItems(careTrackerItems);
		}
		else
		{
			existingGroupEntity.setDeletedAt(LocalDateTime.now());
			existingGroupEntity.setDeletedBy(currentUserId);
			careTrackerItems.forEach((item) -> {
				item.setDeletedAt(LocalDateTime.now());
				item.setDeletedBy(currentUserId);
			});
			existingGroupEntity.setCareTrackerItems(careTrackerItems);
		}
		return existingGroupEntity;
	}
	private CareTrackerItemGroup createNewGroup(CareTrackerItemGroupCreateUpdateTransfer groupInput,
	                                            org.oscarehr.careTracker.entity.CareTracker careTrackerEntity)
	{
		CareTrackerItemGroup group = new CareTrackerItemGroup();
		group.setCareTracker(careTrackerEntity);
		BeanUtils.copyProperties(groupInput, group, "id", "careTrackerItems");

		// all items in a new group will be new
		List<CareTrackerItemCreateUpdateTransfer> inputItems = Optional.ofNullable(groupInput.getCareTrackerItems()).orElse(new ArrayList<>());
		group.setCareTrackerItems(
				inputItems
						.stream()
						.map((item) -> createNewItem(item, careTrackerEntity, group))
						.collect(Collectors.toList())
		);
		return group;
	}

	private CareTrackerItem mergeExistingItem(String currentUserId,
	                                          CareTrackerItem existingItemEntity,
	                                          List<CareTrackerItemCreateUpdateTransfer> itemInputList)
	{
		Optional<CareTrackerItemCreateUpdateTransfer> matchingInput = itemInputList
				.stream()
				.filter((input) -> existingItemEntity.getId().equals(input.getId()))
				.findFirst();

		if(matchingInput.isPresent())
		{
			CareTrackerItemCreateUpdateTransfer input = matchingInput.get();
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

	private CareTrackerItem createNewItem(CareTrackerItemCreateUpdateTransfer itemInput,
	                                      org.oscarehr.careTracker.entity.CareTracker careTrackerEntity,
	                                      CareTrackerItemGroup groupEntity)
	{
		CareTrackerItem item = new CareTrackerItem();
		item.setCareTracker(careTrackerEntity);
		item.setCareTrackerItemGroup(groupEntity);
		BeanUtils.copyProperties(itemInput, item);

		List<DsRuleUpdateInput> itemRules = Optional.ofNullable(itemInput.getRules()).orElse(new ArrayList<>());
		item.setDsRules(itemRules.stream().map((rule) -> dsRuleDao.find(rule.getId())).collect(Collectors.toSet()));

		return item;
	}
}
