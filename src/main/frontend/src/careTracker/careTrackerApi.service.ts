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
import {CareTrackerApi, CareTrackersApi, DemographicApi, DiseaseRegistryApi, PreventionsApi} from "../../generated";
import CareTrackerModel from "../lib/careTracker/model/CareTrackerModel";
import CareTrackerTransferToModelConverter from "../lib/careTracker/converter/CareTrackerTransferToModelConverter";
import CareTrackerModelToTransferConverter from "../lib/careTracker/converter/CareTrackerModelToTransferConverter";
import {DxCodingSystem} from "../lib/dx/model/DxCodingSystem";
import DxCodeTransferToModelConverter from "../lib/dx/converter/DxCodeTransferToModelConverter";
import DxCodeModel from "../lib/dx/model/DxCodeModel";
import CareTrackerItemDataModelToCreateTransferConverter from "../lib/careTracker/converter/CareTrackerItemDataModelToCreateTransferConverter";
import CareTrackerItemTransferToModelConverter from "../lib/careTracker/converter/CareTrackerItemTransferToModelConverter";
import CareTrackerItemModel from "../lib/careTracker/model/CareTrackerItemModel";
import CareTrackerItemDataModel from "../lib/careTracker/model/CareTrackerItemDataModel";
import CareTrackerItemDataTransferToModelConverter from "../lib/careTracker/converter/CareTrackerItemDataTransferToModelConverter";

angular.module("CareTracker").service("careTrackerApiService", [
	'$http',
	'$httpParamSerializer',
	function(
		$http,
		$httpParamSerializer)
	{
		const service = this;
		service.careTrackerApi = new CareTrackerApi($http, $httpParamSerializer, '../ws/rs');
		service.careTrackersApi = new CareTrackersApi($http, $httpParamSerializer, '../ws/rs');
		service.demographicApi = new DemographicApi($http, $httpParamSerializer, '../ws/rs');
		service.preventionsApi = new PreventionsApi($http, $httpParamSerializer, '../ws/rs');
		service.diseaseRegistryApi = new DiseaseRegistryApi($http, $httpParamSerializer, '../ws/rs');

		// inbound converters
		service.careTrackerTransferToModelConverter = new CareTrackerTransferToModelConverter();
		service.careTrackerItemTransferToModelConverter = new CareTrackerItemTransferToModelConverter();
		service.careTrackerItemDataTransferToModelConverter = new CareTrackerItemDataTransferToModelConverter();

		// outbound converters
		service.careTrackerModelToTransferConverter = new CareTrackerModelToTransferConverter();
		service.careTrackerItemDataModelToCreateTransferConverter = new CareTrackerItemDataModelToCreateTransferConverter();
		service.dxCodeTransferToModelConverter = new DxCodeTransferToModelConverter();

		service.searchCareTrackers = async (
			enabled: boolean,
			includeClinicLevel: boolean,
			includeProviderLevel: boolean,
			providerId: string,
			includeDemographicLevel: boolean,
			demographicId: number,
			page: number,
			perPage: number): Promise<Array<CareTrackerModel>> =>
		{
			return service.careTrackerTransferToModelConverter.convertList(
				(await service.careTrackersApi.search(
					enabled, includeClinicLevel, includeProviderLevel, providerId, includeDemographicLevel, demographicId, page, perPage)).data.body);
		}

		service.getCareTracker = async (careTrackerId: number): Promise<CareTrackerModel> =>
		{
			return service.careTrackerTransferToModelConverter.convert((await service.careTrackerApi.getCareTracker(careTrackerId)).data.body);
		}

		service.createCareTracker = async (careTracker: CareTrackerModel): Promise<CareTrackerModel> =>
		{
			const transfer = service.careTrackerModelToTransferConverter.convert(careTracker);
			return service.careTrackerTransferToModelConverter.convert((await service.careTrackerApi.createCareTracker(transfer)).data.body);
		}

		service.updateCareTracker = async (careTrackerId: number, careTracker: CareTrackerModel): Promise<CareTrackerModel> =>
		{
			const transfer = service.careTrackerModelToTransferConverter.convert(careTracker);
			return service.careTrackerTransferToModelConverter.convert((await service.careTrackerApi.updateCareTracker(careTrackerId, transfer)).data.body);
		}

		service.setCareTrackerEnabled = async (careTrackerId: number, enabled: boolean): Promise<boolean> =>
		{
			return (await service.careTrackerApi.setEnabledState(careTrackerId, enabled)).data.body;
		}

		service.deleteCareTracker = async (careTrackerId: number): Promise<boolean> =>
		{
			return (await service.careTrackerApi.deleteCareTracker(careTrackerId)).data.body;
		}

		service.searchPreventionTypes = async (keyword: string): Promise<object[]> =>
		{
			return (await service.preventionsApi.searchPreventionTypes(keyword)).data;
		}

		service.searchDxCodes = async (codingSystem: DxCodingSystem, keyword: string, page?: number, perPage?: number): Promise<DxCodeModel[]> =>
		{
			return service.dxCodeTransferToModelConverter.convertList((await service.diseaseRegistryApi.searchDxCodes(codingSystem, keyword, page, perPage)).data.body);
		}

		service.getDemographicCareTracker = async (demographicId: number, careTrackerId: number): Promise<CareTrackerModel> =>
		{
			return service.careTrackerTransferToModelConverter.convert((await service.demographicApi.getCareTrackerForDemographic(demographicId, careTrackerId)).data.body);
		}

		service.getDemographicCareTrackerItem = async (demographicId: number, careTrackerId: number, careTrackerItemId: number): Promise<CareTrackerItemModel> =>
		{
			return service.careTrackerItemTransferToModelConverter.convert((await service.demographicApi.getCareTrackerItemForDemographic(demographicId, careTrackerId, careTrackerItemId)).data.body);
		}

		service.addCareTrackerItemData = async (demographicId: number, careTrackerId: number, careTrackerItemId: number, data: CareTrackerItemDataModel): Promise<CareTrackerItemDataModel> =>
		{
			const transferOut = service.careTrackerItemDataModelToCreateTransferConverter.convert(data);
			const transferIn = (await service.demographicApi.addCareTrackerItemData(demographicId, careTrackerId, careTrackerItemId, transferOut)).data.body;
			return service.careTrackerItemDataTransferToModelConverter.convert(transferIn);
		}

		service.cloneCareTrackerForClinic = async (careTrackerId: number): Promise<CareTrackerModel> =>
		{
			return service.careTrackerTransferToModelConverter.convert((await service.careTrackerApi.cloneCareTracker(careTrackerId)).data.body);
		}
		service.cloneCareTrackerForProvider = async (careTrackerId: number, providerId: string): Promise<CareTrackerModel> =>
		{
			return service.careTrackerTransferToModelConverter.convert((await service.careTrackerApi.cloneCareTracker(careTrackerId, providerId)).data.body);
		}
		service.cloneCareTrackerForDemographic = async (careTrackerId: number, demographicId: number): Promise<CareTrackerModel> =>
		{
			return service.careTrackerTransferToModelConverter.convert((await service.careTrackerApi.cloneCareTracker(careTrackerId, null, demographicId)).data.body);
		}
	}
]);