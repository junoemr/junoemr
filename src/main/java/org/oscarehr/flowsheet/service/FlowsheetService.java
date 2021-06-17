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
import org.oscarehr.flowsheet.dao.FlowsheetDao;
import org.oscarehr.flowsheet.model.Flowsheet;
import org.oscarehr.flowsheet.transfer.FlowsheetInboundTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class FlowsheetService
{
	@Autowired
	private FlowsheetDao flowsheetDao;

	@Autowired
	private FlowsheetEntityToModelConverter flowsheetEntityToModelConverter;

	public List<Flowsheet> getFlowsheets(int offset, int perPage)
	{
		return flowsheetEntityToModelConverter.convert(flowsheetDao.findAll(offset, perPage));
	}

	public Flowsheet addNewFlowsheet(FlowsheetInboundTransfer flowsheetTransfer)
	{
		return null; //TODO
	}

	public Flowsheet updateFlowsheet(Integer flowsheetId, FlowsheetInboundTransfer flowsheetTransfer)
	{
		return flowsheetEntityToModelConverter.convert(flowsheetDao.find(flowsheetId)); //TODO
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
		flowsheetDao.merge(flowsheetEntity);
	}

	public boolean setFlowsheetEnabled(Integer flowsheetId, boolean enabled)
	{
		org.oscarehr.flowsheet.entity.Flowsheet flowsheetEntity = flowsheetDao.find(flowsheetId);
		flowsheetEntity.setEnabled(enabled);
		flowsheetDao.merge(flowsheetEntity);
		return flowsheetEntity.isEnabled();
	}
}
