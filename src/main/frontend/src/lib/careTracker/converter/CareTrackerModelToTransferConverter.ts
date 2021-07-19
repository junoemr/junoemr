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

import CareTrackerModel from "../model/CareTrackerModel";
import {CareTracker, CareTrackerItem, CareTrackerItemGroup} from "../../../../generated";
import CareTrackerItemGroupModel from "../model/CareTrackerItemGroupModel";
import CareTrackerItemModel from "../model/CareTrackerItemModel";
import DsRuleModelToTransferConverter from "../../decisionSupport/converter/DsRuleModelToTransferConverter";
import AbstractConverter from "../../conversion/AbstractConverter";
import DxCodeModelToTransferConverter from "../../dx/converter/DxCodeModelToTransferConverter";

export default class CareTrackerModelToTransferConverter extends AbstractConverter<CareTrackerModel, CareTracker>
{
	public convert(careTrackerModel: CareTrackerModel): CareTracker
	{
		if (!careTrackerModel)
		{
			return null;
		}

		return {
			id: careTrackerModel.id,
			name: careTrackerModel.name,
			description: careTrackerModel.description,
			enabled: careTrackerModel.enabled,
			careTrackerItemGroups: this.convertAllGroups(careTrackerModel.careTrackerItemGroups),
			triggerCodes: new DxCodeModelToTransferConverter().convertList(careTrackerModel.triggerCodes),
		} as CareTracker;
	}

	private convertAllGroups(itemGroups: Array<CareTrackerItemGroupModel>): Array<CareTrackerItemGroup>
	{
		return itemGroups.map(itemGroup =>
		{
			const groupModel = {} as CareTrackerItemGroup;
			groupModel.id = itemGroup.id;
			groupModel.name = itemGroup.name;
			groupModel.description = itemGroup.description;
			groupModel.careTrackerItems = this.convertAllItems(itemGroup.careTrackerItems);

			return groupModel;
		});
	}

	private convertAllItems(items: Array<CareTrackerItemModel>): Array<CareTrackerItem>
	{
		const ruleToTransferConverter = new DsRuleModelToTransferConverter();
		return items.map(item =>
		{
			const model = {} as CareTrackerItem;
			model.id = item.id;
			model.name = item.name;
			model.description = item.description;
			model.guideline = item.guideline;
			model.type = item.type;
			model.typeCode = item.typeCode;
			model.hidden = item.hidden;
			model.valueType = item.valueType;
			model.valueLabel = item.valueLabel;
			model.rules = ruleToTransferConverter.convertList(item.rules);

			// data and alerts are not submitted with the careTracker transfer
			model.data = null;
			model.careTrackerItemAlerts = null;

			return model;
		});
	}

}
