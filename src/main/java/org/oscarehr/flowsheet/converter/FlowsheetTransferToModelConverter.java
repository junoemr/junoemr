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
import org.oscarehr.flowsheet.model.Flowsheet;
import org.oscarehr.flowsheet.model.FlowsheetItem;
import org.oscarehr.flowsheet.model.FlowsheetItemGroup;
import org.oscarehr.flowsheet.transfer.FlowsheetCreateTransfer;
import org.oscarehr.flowsheet.transfer.FlowsheetItemCreateUpdateTransfer;
import org.oscarehr.flowsheet.transfer.FlowsheetItemGroupCreateUpdateTransfer;
import org.oscarehr.flowsheet.transfer.FlowsheetUpdateTransfer;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class FlowsheetTransferToModelConverter extends AbstractModelConverter<FlowsheetCreateTransfer, Flowsheet>
{
	@Override
	public Flowsheet convert(FlowsheetCreateTransfer input)
	{
		if(input == null)
		{
			return null;
		}
		// find existing flowsheet entity or create a new one based on transfer type
		Flowsheet flowsheet = new Flowsheet();
		if(input instanceof FlowsheetUpdateTransfer)
		{
			FlowsheetUpdateTransfer updateTransfer = (FlowsheetUpdateTransfer) input;
			flowsheet.setId(updateTransfer.getId());
		}
		BeanUtils.copyProperties(input, flowsheet, "id", "flowsheetItemGroups");

		flowsheet.setFlowsheetItemGroups(input.getFlowsheetItemGroups().stream().map(this::convert).collect(Collectors.toList()));
		flowsheet.setTriggerCodes(input.getTriggerCodes());
		return flowsheet;
	}

	private FlowsheetItemGroup convert(FlowsheetItemGroupCreateUpdateTransfer groupTransfer)
	{
		FlowsheetItemGroup group = new FlowsheetItemGroup();
		BeanUtils.copyProperties(groupTransfer, group, "flowsheetItems");
		group.setFlowsheetItems(groupTransfer.getFlowsheetItems().stream().map(this::convert).collect(Collectors.toList()));

		return group;
	}

	private FlowsheetItem convert(FlowsheetItemCreateUpdateTransfer itemTransfer)
	{
		FlowsheetItem item = new FlowsheetItem();
		BeanUtils.copyProperties(itemTransfer, item, "rules");
		//TODO rules converter

		return item;
	}
}
