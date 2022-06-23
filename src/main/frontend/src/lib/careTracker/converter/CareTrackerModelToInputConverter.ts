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

import CareTracker from "../model/CareTracker";
import {CareTrackerItemGroupModel, CareTrackerItemModel} from "../../../../generated";
import CareTrackerItemGroup from "../model/CareTrackerItemGroup";
import CareTrackerItem from "../model/CareTrackerItem";
import DsRuleModelToTransferConverter from "../../decisionSupport/converter/DsRuleModelToTransferConverter";
import AbstractConverter from "../../conversion/AbstractConverter";
import DxCodeModelToTransferConverter from "../../dx/converter/DxCodeModelToTransferConverter";

export default abstract class CareTrackerModelToInputConverter<T> extends AbstractConverter<CareTracker, T>
{
	public convert(careTrackerModel: CareTracker): T
	{
		if (!careTrackerModel)
		{
			return null;
		}

		return {
			name: careTrackerModel.name,
			description: careTrackerModel.description,
			enabled: careTrackerModel.enabled,
			careTrackerItemGroups: this.convertAllGroups(careTrackerModel.careTrackerItemGroups),
			triggerCodes: new DxCodeModelToTransferConverter().convertList(careTrackerModel.triggerCodes),
		} as unknown as T;
	}

	private convertAllGroups(itemGroups: Array<CareTrackerItemGroup>): Array<CareTrackerItemGroupModel>
	{
		return itemGroups.map(itemGroup =>
		{
			const groupModel = {} as CareTrackerItemGroupModel;
			groupModel.id = itemGroup.id;
			groupModel.name = itemGroup.name;
			groupModel.description = itemGroup.description;
			groupModel.careTrackerItems = this.convertAllItems(itemGroup.careTrackerItems);

			return groupModel;
		});
	}

	private convertAllItems(items: Array<CareTrackerItem>): Array<CareTrackerItemModel>
	{
		const ruleToTransferConverter = new DsRuleModelToTransferConverter();
		return items.map(item =>
		{
			const model = {} as CareTrackerItemModel;
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
