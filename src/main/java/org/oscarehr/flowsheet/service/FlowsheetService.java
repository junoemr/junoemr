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


import org.oscarehr.flowsheet.converter.FlowsheetEntityToModelConverter;
import org.oscarehr.flowsheet.converter.FlowsheetTransferToEntityConverter;
import org.oscarehr.flowsheet.dao.FlowsheetDao;
import org.oscarehr.flowsheet.model.Flowsheet;
import org.oscarehr.flowsheet.transfer.FlowsheetCreateTransfer;
import org.oscarehr.flowsheet.transfer.FlowsheetUpdateTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class FlowsheetService
{
	@Autowired
	private FlowsheetDao flowsheetDao;

	@Autowired
	private FlowsheetEntityToModelConverter flowsheetEntityToModelConverter;

	@Autowired
	private FlowsheetTransferToEntityConverter flowsheetTransferToEntityConverter;

	public List<Flowsheet> getFlowsheets(int offset, int perPage)
	{
		return flowsheetEntityToModelConverter.convert(flowsheetDao.findAll(offset, perPage));
	}

	public Flowsheet addNewFlowsheet(String creatingProviderId, FlowsheetCreateTransfer creationTransfer)
	{
		org.oscarehr.flowsheet.entity.Flowsheet entity = flowsheetTransferToEntityConverter.convert(creationTransfer);
		entity.setCreatedBy(creatingProviderId);
		flowsheetDao.persist(entity);
		return flowsheetEntityToModelConverter.convert(entity);
	}

	public Flowsheet updateFlowsheet(String updatingProviderId, Integer flowsheetId, FlowsheetUpdateTransfer updateTransfer)
	{
		org.oscarehr.flowsheet.entity.Flowsheet entity = flowsheetTransferToEntityConverter.convert(updateTransfer);

		// set provider updated/deleted by states
		// this can be moved to the converter logic once the logged in provider can be accessed globally
		entity.setUpdatedBy(updatingProviderId);
		entity.getFlowsheetItemGroups()
				.stream()
				.filter((group) -> group.getDeletedAt() != null)
				.forEach((group) -> group.setDeletedBy(updatingProviderId));
		entity.getFlowsheetItems()
				.stream()
				.filter((item) -> item.getDeletedAt() != null)
				.forEach((item) -> item.setDeletedBy(updatingProviderId));

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
}
