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

import {DemographicApi, WaitListApi} from "../../../generated";
import WaitingList from "../../lib/waitingList/model/WaitingList";
import WaitingListTransferConverter from "../../lib/waitingList/converter/WaitingListTransferConverter";
import DemographicWaitingList from "../../lib/waitingList/model/DemographicWaitingList";
import DemographicWaitingListTransferConverter
	from "../../lib/waitingList/converter/DemographicWaitingListTransferConverter";

angular.module("Common.Services").service("waitingListService", [
	'$http',
	'$httpParamSerializer',
	function (
		$http,
		$httpParamSerializer)
	{
		const service = this;
		service.waitListApi = new WaitListApi($http, $httpParamSerializer, '../ws/rs');
		service.demographicApi = new DemographicApi($http, $httpParamSerializer, '../ws/rs');

		service.waitingListTransferConverter = new WaitingListTransferConverter();
		service.demographicWaitingListTransferConverter = new DemographicWaitingListTransferConverter();

		service.getActiveWaitingLists = async (): Promise<WaitingList[]> =>
		{
			return service.waitingListTransferConverter.convertList((await service.waitListApi.getWaitLists()).data.body);
		}

		service.getActiveDemographicWaitList = async (demographicId: number): Promise<DemographicWaitingList> =>
		{
			return service.demographicWaitingListTransferConverter.convert(
				(await service.demographicApi.getActiveWaitList(demographicId)).data.body);
		}
    }
]);