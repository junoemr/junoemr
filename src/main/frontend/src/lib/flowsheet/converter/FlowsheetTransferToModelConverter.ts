'use strict';

/*

    Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
    This software is published under the GPL GNU General Public License.
    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2
    of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

    This software was written for
    CloudPractice Inc.
    Victoria, British Columbia
    Canada

 */

import FlowsheetModel from "../model/FlowsheetModel";
import {Flowsheet, FlowsheetItem, FlowsheetItemGroup} from "../../../../generated";
import FlowsheetItemGroupModel from "../model/FlowsheetItemGroupModel";
import FlowsheetItemModel from "../model/FlowsheetItemModel";

export default class FlowsheetTransferToModelConverter
{
	convert(flowsheetTransfer: Flowsheet): FlowsheetModel
	{
		if (!flowsheetTransfer)
		{
			return null;
		}

		const flowsheetModel = new FlowsheetModel();
		flowsheetModel.id = flowsheetTransfer.id;
		flowsheetModel.name = flowsheetTransfer.name;
		flowsheetModel.description = flowsheetTransfer.description;
		flowsheetModel.enabled = flowsheetTransfer.enabled;
		flowsheetModel.systemManaged = flowsheetTransfer.systemManaged;
		flowsheetModel.flowsheetItemGroups = this.convertAllGroups(flowsheetTransfer.flowsheetItemGroups);

		return flowsheetModel;
	}

	convertAll(flowsheetTransfers: Array<Flowsheet>): Array<FlowsheetModel>
	{
		return flowsheetTransfers.map((transfer) => this.convert(transfer));
	}

	convertAllGroups(itemGroups: Array<FlowsheetItemGroup>): Array<FlowsheetItemGroupModel>
	{
		return itemGroups.map(itemGroup =>
		{
			const groupModel = new FlowsheetItemGroupModel();
			groupModel.id = itemGroup.id;
			groupModel.name = itemGroup.name;
			groupModel.description = itemGroup.description;
			groupModel.flowsheetItems = this.convertAllItems(itemGroup.flowsheetItems);

			return groupModel;
		});
	}

	convertAllItems(items: Array<FlowsheetItem>): Array<FlowsheetItemModel>
	{
		return items.map(item =>
		{
			const model = new FlowsheetItemModel();
			model.id = item.id;
			model.name = item.name;
			model.description = item.description;
			model.guideline = item.guideline;
			model.type = item.type;
			model.typeCode = item.typeCode;
			model.hidden = item.hidden;
			model.valueType = item.valueType;
			model.valueLabel = item.valueLabel;
			model.flowsheetItemAlerts = item.flowsheetItemAlerts;
			model.data = item.data;
			model.rules = item.rules;

			return model;
		});
	}

}
