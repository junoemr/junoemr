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

import CareTrackerItem from "../model/CareTrackerItem";
import DsRuleTransferToModelConverter from "../../decisionSupport/converter/DsRuleTransferToModelConverter";
import AbstractConverter from "../../conversion/AbstractConverter";
import Alert from "../model/Alert";
import CareTrackerItemDataToModelConverter from "./CareTrackerItemDataToModelConverter";
import {CareTrackerItemAlertModel, CareTrackerItemModel} from "../../../../generated";

export default class CareTrackerItemToModelConverter extends AbstractConverter<CareTrackerItemModel, CareTrackerItem>
{
	protected readonly ruleToModelConverter = new DsRuleTransferToModelConverter();
	protected readonly careTrackerItemDataToModelConverter = new CareTrackerItemDataToModelConverter();

	public convert(careTrackerItemModelTransfer: CareTrackerItemModel): CareTrackerItem
	{
		if (!careTrackerItemModelTransfer)
		{
			return null;
		}

		const model = new CareTrackerItem(careTrackerItemModelTransfer.id);
		model.name = careTrackerItemModelTransfer.name;
		model.description = careTrackerItemModelTransfer.description;
		model.guideline = careTrackerItemModelTransfer.guideline;
		model.type = careTrackerItemModelTransfer.type;
		model.typeCode = careTrackerItemModelTransfer.typeCode;
		model.hidden = careTrackerItemModelTransfer.hidden;
		model.valueType = careTrackerItemModelTransfer.valueType;
		model.valueLabel = careTrackerItemModelTransfer.valueLabel;
		model.careTrackerItemAlerts = this.convertAllAlerts(careTrackerItemModelTransfer.careTrackerItemAlerts);
		model.data = this.careTrackerItemDataToModelConverter.convertList(careTrackerItemModelTransfer.data);
		model.rules = this.ruleToModelConverter.convertList(careTrackerItemModelTransfer.rules);

		return model;
	}

	private convertAllAlerts(alerts: CareTrackerItemAlertModel[]): Alert[]
	{
		return alerts.map(alert =>
		{
			const alertModel = new Alert();
			alertModel.message = alert.message;
			alertModel.severityLevel = alert.severityLevel;
			return alertModel;
		});
	}
}
