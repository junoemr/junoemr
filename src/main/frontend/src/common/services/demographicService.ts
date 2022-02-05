'use strict';

/*

 Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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

 This software was written for the
 Department of Family Medicine
 McMaster University
 Hamilton
 Ontario, Canada

 */

import {DemographicApi} from "../../../generated";
import Demographic from "../../lib/demographic/model/Demographic";
import DemographicTransferToModelConverter from "../../lib/demographic/converter/DemographicTransferToModelConverter";
import DemographicToCreateInputConverter from "../../lib/demographic/converter/DemographicToCreateInputConverter";

angular.module("Common.Services").service("demographicService", [
	'$http',
	'$httpParamSerializer',
	function (
		$http,
		$httpParamSerializer)
	{
		const service = this;
		service.demographicApi = new DemographicApi($http, $httpParamSerializer, '../ws/rs');

		service.demographicTransferToModelConverter = new DemographicTransferToModelConverter();
		service.demographicModelToCreateInputConverter = new DemographicToCreateInputConverter();

		service.getDemographic = async (demographicId: number): Promise<Demographic> =>
		{
			let transfer = (await service.demographicApi.getDemographicData(demographicId));
			console.debug(service.demographicTransferToModelConverter.convert(transfer.data.body));
			return service.demographicTransferToModelConverter.convert(transfer.data.body);
		}

		service.createDemographic = async (demographic: Demographic): Promise<Demographic> =>
		{
			const transfer = service.demographicModelToCreateInputConverter.convert(demographic);
			return service.demographicTransferToModelConverter.convert(
				(await service.demographicApi.createDemographicData(transfer)).data.body);
		}

		service.updateDemographic = async (demographic: Demographic): Promise<Demographic> =>
		{
			const transfer = service.demographicModelToTransferConverter.convert(demographic);
			return service.demographicTransferToModelConverter.convert(
				(await service.demographicApi.updateDemographicData(transfer)).data.body);
		}
    }
]);