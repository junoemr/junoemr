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

import {DsCondition, DsConsequence, DsRule} from "../../../../generated";
import DsRuleModel from "../model/DsRuleModel";
import DsRuleConditionModel from "../model/DsRuleConditionModel";
import DsRuleConsequenceModel from "../model/DsRuleConsequenceModel";
import AbstractConverter from "../../conversion/AbstractConverter";

export default class DsRuleTransferToModelConverter extends AbstractConverter<DsRule, DsRuleModel>
{
	public convert(transfer: DsRule): DsRuleModel
	{
		let model = new DsRuleModel();
		model.id = transfer.id;
		model.name = transfer.name;
		model.description = transfer.description;
		model.systemManaged = transfer.systemManaged;
		model.conditions = this.convertConditions(transfer.conditions);
		model.consequences = this.convertConsequences(transfer.consequences);

		return model;
	}

	private convertConditions(transfers: Array<DsCondition>): Array<DsRuleConditionModel>
	{
		return transfers.map((transfer) =>
		{
			const model = new DsRuleConditionModel();
			model.id = transfer.id;
			model.type = transfer.type;
			model.value = transfer.value;
			return model;
		});
	}

	private convertConsequences(transfers: Array<DsConsequence>): Array<DsRuleConsequenceModel>
	{
		return transfers.map((transfer) =>
		{
			const model = new DsRuleConsequenceModel();
			model.id = transfer.id;
			model.type = transfer.type;
			model.severityLevel = transfer.severityLevel;
			model.message = transfer.message;
			return model;
		});
	}
}
