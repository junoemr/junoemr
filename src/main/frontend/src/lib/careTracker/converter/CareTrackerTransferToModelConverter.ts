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
import {CareTracker, CareTrackerItem, CareTrackerItemAlert, CareTrackerItemGroup} from "../../../../generated";
import CareTrackerItemGroupModel from "../model/CareTrackerItemGroupModel";
import CareTrackerItemModel from "../model/CareTrackerItemModel";
import DsRuleTransferToModelConverter from "../../decisionSupport/converter/DsRuleTransferToModelConverter";
import AbstractConverter from "../../conversion/AbstractConverter";
import DxCodeTransferToModelConverter from "../../dx/converter/DxCodeTransferToModelConverter";
import AlertModel from "../model/AlertModel";
import CareTrackerItemDataTransferToModelConverter from "./CareTrackerItemDataTransferToModelConverter";

export default class CareTrackerTransferToModelConverter extends AbstractConverter<CareTracker, CareTrackerModel>
{
	public convert(careTracker: CareTracker): CareTrackerModel
	{
		if (!careTracker)
		{
			return null;
		}

		const careTrackerModel = new CareTrackerModel();
		careTrackerModel.id = careTracker.id;
		careTrackerModel.name = careTracker.name;
		careTrackerModel.description = careTracker.description;
		careTrackerModel.enabled = careTracker.enabled;
		careTrackerModel.systemManaged = careTracker.systemManaged;
		careTrackerModel.careTrackerItemGroups = this.convertAllGroups(careTracker.careTrackerItemGroups);
		careTrackerModel.triggerCodes = new DxCodeTransferToModelConverter().convertList(careTracker.triggerCodes);
		careTrackerModel.parentCareTrackerId = careTracker.parentCareTrackerId;
		careTrackerModel.ownerProviderId = careTracker.ownerProviderId;
		careTrackerModel.ownerDemographicId = careTracker.ownerDemographicId;

		return careTrackerModel;
	}

	private convertAllGroups(itemGroups: CareTrackerItemGroup[]): CareTrackerItemGroupModel[]
	{
		return itemGroups.map(itemGroup =>
		{
			const groupModel = new CareTrackerItemGroupModel();
			groupModel.id = itemGroup.id;
			groupModel.name = itemGroup.name;
			groupModel.description = itemGroup.description;
			groupModel.careTrackerItems = this.convertAllItems(itemGroup.careTrackerItems);

			return groupModel;
		});
	}

	private convertAllItems(items: CareTrackerItem[]): CareTrackerItemModel[]
	{
		const ruleToModelConverter = new DsRuleTransferToModelConverter();
		return items.map(item =>
		{
			const model = new CareTrackerItemModel();
			model.id = item.id;
			model.name = item.name;
			model.description = item.description;
			model.guideline = item.guideline;
			model.type = item.type;
			model.typeCode = item.typeCode;
			model.hidden = item.hidden;
			model.valueType = item.valueType;
			model.valueLabel = item.valueLabel;
			model.careTrackerItemAlerts = this.convertAllAlerts(item.careTrackerItemAlerts);
			model.data = new CareTrackerItemDataTransferToModelConverter().convertList(item.data);
			model.rules = ruleToModelConverter.convertList(item.rules);

			return model;
		});
	}

	private convertAllAlerts(alerts: CareTrackerItemAlert[]): AlertModel[]
	{
		return alerts.map(alert =>
		{
			const alertModel = new AlertModel();
			alertModel.message = alert.message;
			alertModel.severityLevel = alert.severityLevel;
			return alertModel;
		});
	}
}
