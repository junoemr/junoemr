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


import org.oscarehr.flowsheet.dao.FlowsheetItemDao;
import org.oscarehr.flowsheet.entity.FlowsheetItem;
import org.oscarehr.flowsheet.entity.ItemType;
import org.oscarehr.flowsheet.model.FlowsheetItemData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class FlowsheetDataService
{
	@Autowired
	private FlowsheetItemDao flowsheetItemDao;

	public FlowsheetItemData addFlowsheetItemData(Integer flowsheetItemId, FlowsheetItemData itemData)
	{
		FlowsheetItem flowsheetItem = flowsheetItemDao.find(flowsheetItemId);
		if(ItemType.MEASUREMENT.equals(flowsheetItem.getType()))
		{
			return addFlowsheetMeasurement(flowsheetItem, itemData);
		}
		else if(ItemType.PREVENTION.equals(flowsheetItem.getType()))
		{
			return addPreventionMeasurement(flowsheetItem, itemData);
		}
		else
		{
			throw new ValidationException("Invalid flowsheet item type: " + flowsheetItem.getType());
		}
	}

	private FlowsheetItemData addFlowsheetMeasurement(FlowsheetItem flowsheetItem, FlowsheetItemData itemData)
	{
		throw new ValidationException("TODO"); //TODO
	}

	private FlowsheetItemData addPreventionMeasurement(FlowsheetItem flowsheetItem, FlowsheetItemData itemData)
	{
		throw new ValidationException("TODO"); //TODO
	}
}
