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
import {DemographicFlowsheetApi, FlowsheetServiceApi, FlowsheetsServiceApi} from "../../../generated";

angular.module("Record.Flowsheet").service("flowsheetApiService", [
	'$http',
	'$httpParamSerializer',
	function(
		$http,
		$httpParamSerializer)
	{
		const service = this;
		service.flowsheetApi = new FlowsheetServiceApi($http, $httpParamSerializer, '../ws/rs');
		service.flowsheetsApi = new FlowsheetsServiceApi($http, $httpParamSerializer, '../ws/rs');
		service.demographicFlowsheetsApi = new DemographicFlowsheetApi($http, $httpParamSerializer, '../ws/rs');

		service.getDemographicFlowsheet = async (demographicId: number, flowsheetId: number): Promise<any> =>
		{
			return (await service.demographicFlowsheetsApi.getFlowsheetForDemographic(demographicId, flowsheetId)).data.body;
		}

		service.getAllFlowsheets = async (): Promise<any> =>
		{
			return (await service.flowsheetsApi.getFlowsheets()).data.body;
		}

		service.addFlowsheetItemData = async (demographicId: number, flowsheetId: number, flowsheetItemId: number, data: object): Promise<any> =>
		{
			return (await service.demographicFlowsheetsApi.addFlowsheetItemData(demographicId, flowsheetId, flowsheetItemId, data)).data.body;
		}
	}
]);