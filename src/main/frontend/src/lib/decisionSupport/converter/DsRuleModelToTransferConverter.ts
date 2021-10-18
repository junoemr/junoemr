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

export default class DsRuleModelToTransferConverter extends AbstractConverter<DsRuleModel, DsRule>
{
	public convert(model: DsRuleModel): DsRule
	{
		let transfer = {} as DsRule;
		transfer.id = model.id;
		transfer.name = model.name;
		transfer.description = model.description;
		transfer.systemManaged = model.systemManaged;
		transfer.conditions = this.convertConditions(model.conditions);
		transfer.consequences = this.convertConsequences(model.consequences);

		return transfer;
	}

	private convertConditions(models: Array<DsRuleConditionModel>): Array<DsCondition>
	{
		return models.map((model) =>
		{
			const transfer = {} as DsCondition;
			transfer.id = model.id;
			transfer.type = model.type;
			transfer.value = model.value;
			return transfer;
		});
	}

	private convertConsequences(models: Array<DsRuleConsequenceModel>): Array<DsConsequence>
	{
		return models.map((model) =>
		{
			const transfer = {} as DsConsequence;
			transfer.id = model.id;
			transfer.type = model.type;
			transfer.severityLevel = model.severityLevel;
			transfer.message = model.message;
			return transfer;
		});
	}
}
