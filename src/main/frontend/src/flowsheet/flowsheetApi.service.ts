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
import {DemographicApi, DiseaseRegistryApi, FlowsheetServiceApi, FlowsheetsServiceApi, MeasurementsApi, PreventionsApi} from "../../generated";
import FlowsheetModel from "../lib/flowsheet/model/FlowsheetModel";
import FlowsheetTransferToModelConverter from "../lib/flowsheet/converter/FlowsheetTransferToModelConverter";
import FlowsheetModelToTransferConverter from "../lib/flowsheet/converter/FlowsheetModelToTransferConverter";
import {DxCodingSystem} from "../lib/dx/model/DxCodingSystem";
import DxCodeTransferToModelConverter from "../lib/dx/converter/DxCodeTransferToModelConverter";
import DxCodeModel from "../lib/dx/model/DxCodeModel";
import FlowsheetItemDataModel from "../lib/flowsheet/model/FlowsheetItemDataModel";
import FlowsheetItemDataTransferToModelConverter from "../lib/flowsheet/converter/FlowsheetItemDataTransferToModelConverter";
import FlowsheetItemDataModelToTransferConverter from "../lib/flowsheet/converter/FlowsheetItemDataModelToTransferConverter";

angular.module("Flowsheet").service("flowsheetApiService", [
	'$http',
	'$httpParamSerializer',
	function(
		$http,
		$httpParamSerializer)
	{
		const service = this;
		service.flowsheetApi = new FlowsheetServiceApi($http, $httpParamSerializer, '../ws/rs');
		service.flowsheetsApi = new FlowsheetsServiceApi($http, $httpParamSerializer, '../ws/rs');
		service.demographicApi = new DemographicApi($http, $httpParamSerializer, '../ws/rs');
		service.preventionsApi = new PreventionsApi($http, $httpParamSerializer, '../ws/rs');
		service.measurementsApi = new MeasurementsApi($http, $httpParamSerializer, '../ws/rs');
		service.diseaseRegistryApi = new DiseaseRegistryApi($http, $httpParamSerializer, '../ws/rs');

		service.flowsheetModelConverter = new FlowsheetTransferToModelConverter();
		service.flowsheetTransferConverter = new FlowsheetModelToTransferConverter();
		service.dxCodeTransferToModelConverter = new DxCodeTransferToModelConverter();
		service.flowsheetItemDataTransferToModelConverter = new FlowsheetItemDataTransferToModelConverter();
		service.flowsheetItemDataModelToTransferConverter = new FlowsheetItemDataModelToTransferConverter();

		service.searchFlowsheets = async (
			enabled: boolean,
			includeClinicLevel: boolean,
			includeProviderLevel: boolean,
			providerId: string,
			includeDemographicLevel: boolean,
			demographicId: number,
			page: number,
			perPage: number): Promise<Array<FlowsheetModel>> =>
		{
			return service.flowsheetModelConverter.convertList(
				(await service.flowsheetsApi.searchFlowsheets(
					enabled, includeClinicLevel, includeProviderLevel, providerId, includeDemographicLevel, demographicId, page, perPage)).data.body);
		}

		service.getFlowsheet = async (flowsheetId: number): Promise<FlowsheetModel> =>
		{
			return service.flowsheetModelConverter.convert((await service.flowsheetApi.getFlowsheet(flowsheetId)).data.body);
		}

		service.createFlowsheet = async (flowsheet: FlowsheetModel): Promise<FlowsheetModel> =>
		{
			const transfer = service.flowsheetTransferConverter.convert(flowsheet);
			return service.flowsheetModelConverter.convert((await service.flowsheetApi.createFlowsheet(transfer)).data.body);
		}

		service.updateFlowsheet = async (flowsheetId: number, flowsheet: FlowsheetModel): Promise<FlowsheetModel> =>
		{
			const transfer = service.flowsheetTransferConverter.convert(flowsheet);
			return service.flowsheetModelConverter.convert((await service.flowsheetApi.updateFlowsheet(flowsheetId, transfer)).data.body);
		}

		service.setFlowsheetEnabled = async (flowsheetId: number, enabled: boolean): Promise<boolean> =>
		{
			return (await service.flowsheetApi.setFlowsheetEnabledState(flowsheetId, enabled)).data.body;
		}

		service.deleteFlowsheet = async (flowsheetId: number): Promise<boolean> =>
		{
			return (await service.flowsheetApi.deleteFlowsheet(flowsheetId)).data.body;
		}

		service.searchPreventionTypes = async (keyword: string): Promise<Array<string>> =>
		{
			return (await service.preventionsApi.searchPreventionTypes(keyword)).data;
		}

		service.searchMeasurementTypes = async (keyword: string, page?: number, perPage?: number): Promise<Array<string>> =>
		{
			return (await service.measurementsApi.searchMeasurementTypes(keyword, page, perPage)).data;
		}

		service.searchDxCodes = async (codingSystem: DxCodingSystem, keyword: string, page?: number, perPage?: number): Promise<Array<DxCodeModel>> =>
		{
			return service.dxCodeTransferToModelConverter.convertList((await service.diseaseRegistryApi.searchDxCodes(codingSystem, keyword, page, perPage)).data.body);
		}

		service.getDemographicFlowsheet = async (demographicId: number, flowsheetId: number): Promise<FlowsheetModel> =>
		{
			return service.flowsheetModelConverter.convert((await service.demographicApi.getFlowsheetForDemographic(demographicId, flowsheetId)).data.body);
		}

		service.addFlowsheetItemData = async (demographicId: number, flowsheetId: number, flowsheetItemId: number, data: FlowsheetItemDataModel): Promise<FlowsheetItemDataModel> =>
		{
			const transferOut = service.flowsheetItemDataModelToTransferConverter.convert(data);
			const transferIn = (await service.demographicApi.addFlowsheetItemData(demographicId, flowsheetId, flowsheetItemId, transferOut)).data.body;
			return service.flowsheetItemDataTransferToModelConverter.convert(transferIn);
		}

		service.cloneFlowsheetForClinic = async (flowsheetId: number): Promise<FlowsheetModel> =>
		{
			return service.flowsheetModelConverter.convert((await service.flowsheetApi.cloneFlowsheet(flowsheetId)).data.body);
		}
		service.cloneFlowsheetForProvider = async (flowsheetId: number, providerId: string): Promise<FlowsheetModel> =>
		{
			return service.flowsheetModelConverter.convert((await service.flowsheetApi.cloneFlowsheet(flowsheetId, providerId)).data.body);
		}
		service.cloneFlowsheetForDemographic = async (flowsheetId: number, demographicId: number): Promise<FlowsheetModel> =>
		{
			return service.flowsheetModelConverter.convert((await service.flowsheetApi.cloneFlowsheet(flowsheetId, null, demographicId)).data.body);
		}
	}
]);