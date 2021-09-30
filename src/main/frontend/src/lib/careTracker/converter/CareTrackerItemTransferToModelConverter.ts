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

import {CareTrackerItem, CareTrackerItemAlert} from "../../../../generated";
import CareTrackerItemModel from "../model/CareTrackerItemModel";
import DsRuleTransferToModelConverter from "../../decisionSupport/converter/DsRuleTransferToModelConverter";
import AbstractConverter from "../../conversion/AbstractConverter";
import AlertModel from "../model/AlertModel";
import CareTrackerItemDataTransferToModelConverter from "./CareTrackerItemDataTransferToModelConverter";

export default class CareTrackerItemTransferToModelConverter extends AbstractConverter<CareTrackerItem, CareTrackerItemModel>
{
	protected readonly ruleToModelConverter = new DsRuleTransferToModelConverter();
	protected readonly careTrackerItemDataTransferToModelConverter = new CareTrackerItemDataTransferToModelConverter();

	public convert(careTrackerItem: CareTrackerItem): CareTrackerItemModel
	{
		if (!careTrackerItem)
		{
			return null;
		}

		const model = new CareTrackerItemModel(careTrackerItem.id);
		model.name = careTrackerItem.name;
		model.description = careTrackerItem.description;
		model.guideline = careTrackerItem.guideline;
		model.type = careTrackerItem.type;
		model.typeCode = careTrackerItem.typeCode;
		model.hidden = careTrackerItem.hidden;
		model.valueType = careTrackerItem.valueType;
		model.valueLabel = careTrackerItem.valueLabel;
		model.careTrackerItemAlerts = this.convertAllAlerts(careTrackerItem.careTrackerItemAlerts);
		model.data = this.careTrackerItemDataTransferToModelConverter.convertList(careTrackerItem.data);
		model.rules = this.ruleToModelConverter.convertList(careTrackerItem.rules);

		return model;
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
