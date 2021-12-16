/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */

import {JUNO_STYLE, } from "../../../../common/components/junoComponentConstants";;
import SystemPreferenceService from "../../../../lib/system/service/SystemPreferenceService";

angular.module('Admin.Section').component('hrmSettings',
	{
		templateUrl: 'src/admin/section/hrm/settings/HRMSettings.jsp',
		bindings: {},
		controller: ['$scope', '$http', '$httpParamSerializer', '$state', '$uibModal', function ($scope, $http, $httpParamSerializer, $state, $uibModal)
		{
			let ctrl = this;
			let systemPreferenceService = new SystemPreferenceService($http, $httpParamSerializer);

			ctrl.user = "";
			ctrl.address = "";
			ctrl.port = "";
			ctrl.remotePath = "";
			ctrl.interval = 0;
			ctrl.working = false;
			ctrl.latestResults = null;

			ctrl.$onInit = async () => {
				ctrl.COMPONENT_STYLE = ctrl.COMPONENT_STYLE || JUNO_STYLE.DEFAULT;

				let propertyValues = await systemPreferenceService.getProperties(
					"omd.hrm.user",
					"omd.hrm.address",
					"omd.hrm.port",
					"omd.hrm.remote_path",
					"omd.hrm.poll_interval_sec"
				);

				ctrl.user = propertyValues["omd.hrm.user"];
				ctrl.address = propertyValues["omd.hrm.address"];
				ctrl.port = propertyValues["omd.hrm.port"];
				ctrl.remotePath = "/" + propertyValues["omd.hrm.remote_path"];
				ctrl.interval = (parseInt(propertyValues["omd.hrm.poll_interval_sec"]))/60;

				$scope.$apply();
			};
		}]
	});