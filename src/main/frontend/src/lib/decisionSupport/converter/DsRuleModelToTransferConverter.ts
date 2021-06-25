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

import {DsRule} from "../../../../generated";
import DsRuleModel from "../model/DsRuleModel";

export default class DsRuleModelToTransferConverter
{
	convert(model: DsRuleModel): DsRule
	{
		let transfer = {} as DsRule;
		transfer.id = model.id;
		transfer.name = model.name;
		transfer.description = model.description;
		transfer.systemManaged = model.systemManaged;
		transfer.conditions = model.conditions;
		transfer.consequences = model.consequences;

		return transfer;
	}

	convertAll(ruleModels: Array<DsRuleModel>): Array<DsRule>
	{
		return ruleModels.map((transfer) => this.convert(transfer));
	}
}
