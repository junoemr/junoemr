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

import {DemographicApi, MeasurementsApi} from "../../../generated";
import MeasurementModel from "../../lib/measurement/model/measurementModel";
import MeasurementTransferToModelConverter from "../../lib/measurement/converter/MeasurementTransferToModelConverter";
import PagedResponse from "../../lib/common/response/PagedResponse";
import MeasurementTypeModel from "../../lib/measurement/model/measurementTypeModel";
import MeasurementTypeTransferToModelConverter from "../../lib/measurement/converter/MeasurementTypeTransferToModelConverter";

angular.module("Common.Services").service("measurementApiService", [
	'$http',
	'$httpParamSerializer',
	function(
		$http,
		$httpParamSerializer)
	{
		const service = this;
		service.measurementsApi = new MeasurementsApi($http, $httpParamSerializer, '../ws/rs');
		service.demographicApi = new DemographicApi($http, $httpParamSerializer, '../ws/rs');

		service.measurementTransferToModelConverter = new MeasurementTransferToModelConverter();
		service.measurementTypeTransferToModelConverter = new MeasurementTypeTransferToModelConverter();

		service.searchMeasurementTypes = async (keyword: string, page?: number, perPage?: number): Promise<PagedResponse<MeasurementTypeModel>> =>
		{
			const transfer = (await service.measurementsApi.searchMeasurementTypes(keyword, page, perPage)).data;
			return new PagedResponse<MeasurementTypeModel>(service.measurementTypeTransferToModelConverter.convertList(transfer.body), transfer.headers);
		}

		service.getDemographicMeasurements = async (demographicId: number): Promise<MeasurementModel[]> =>
		{
			return service.measurementTransferToModelConverter.convertList((await service.demographicApi.getDemographicMeasurements(demographicId)).data.body);
		}
	}
]);