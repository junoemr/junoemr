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
import {DecisionSupportApi} from "../../generated";
import DsRuleTransferToModelConverter from "../lib/decisionSupport/converter/DsRuleTransferToModelConverter";
import DsRuleModel from "../lib/decisionSupport/model/DsRuleModel";
import DsRuleModelToTransferConverter from "../lib/decisionSupport/converter/DsRuleModelToTransferConverter";

angular.module("DecisionSupport").service("decisionSupportApiService", [
	'$http',
	'$httpParamSerializer',
	function(
		$http,
		$httpParamSerializer)
	{
		const service = this;
		service.decisonSupportApi = new DecisionSupportApi($http, $httpParamSerializer, '../ws/rs');
		service.ruleToModelConverter = new DsRuleTransferToModelConverter();
		service.ruleToTransferConverter = new DsRuleModelToTransferConverter();

		service.getRules = async (): Promise<Array<DsRuleModel>> =>
		{
			return service.ruleToModelConverter.convertList((await service.decisonSupportApi.getRules()).data.body);
		}

		service.createRule = async (model: DsRuleModel): Promise<DsRuleModel> =>
		{
			const transfer = service.ruleToTransferConverter.convert(model);
			return service.ruleToModelConverter.convert((await service.decisonSupportApi.createRule(transfer)).data.body);
		}
	}
]);