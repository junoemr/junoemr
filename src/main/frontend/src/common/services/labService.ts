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

import {LabApi} from "../../../generated";
import OlisSystemSettings from "../../lib/lab/olis/model/OlisSystemSettings";
import OlisProviderSettings from "../../lib/lab/olis/model/OlisProviderSettings";
import OlisProviderSettingsTransferToModelConverter from "../../lib/lab/olis/converter/OlisProviderSettingsTransferToModelConverter";
import OlisSystemSettingsTransferToModelConverter from "../../lib/lab/olis/converter/OlisSystemSettingsTransferToModelConverter";
import OlisSystemSettingsInputConverter from "../../lib/lab/olis/converter/OlisSystemSettingsInputConverter";

angular.module("Common.Services").service("labService", [
	'$q',
	'$http',
	'$httpParamSerializer',
	function($q,
	         $http,
	         $httpParamSerializer)
	{
		const service = this;
		const labApi = new LabApi($http, $httpParamSerializer, '../ws/rs');

		service.olisSystemSettingsTransferToModelConverter = new OlisSystemSettingsTransferToModelConverter();
		service.olisProviderSettingsTransferToModelConverter = new OlisProviderSettingsTransferToModelConverter();
		service.olisSystemSettingsInputConverter = new OlisSystemSettingsInputConverter();

		service.triggerLabPull = async (labType: string): Promise<boolean> =>
		{
			return (await labApi.triggerLabPull(labType)).data.body;
		}

		service.getOlisProviderSettings = async (): Promise<OlisProviderSettings[]> =>
		{
			return service.olisProviderSettingsTransferToModelConverter.convertList(
				(await labApi.getOlisProviderSettings()).data.body);
		}

		service.getOlisSystemSettings = async (): Promise<OlisSystemSettings> =>
		{
			return service.olisSystemSettingsTransferToModelConverter.convert(
				(await labApi.getOlisSystemSettings()).data.body);
		}

		service.updateOlisSystemSettings = async (input: OlisSystemSettings): Promise<OlisSystemSettings> =>
		{
			return service.olisSystemSettingsTransferToModelConverter.convert(
				(await labApi.updateOlisSystemSettings(service.olisSystemSettingsInputConverter.convert(input))).data.body);
		}

		return service;
	}
]);