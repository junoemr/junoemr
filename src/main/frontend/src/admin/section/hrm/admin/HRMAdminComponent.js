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

import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN, JUNO_STYLE, LABEL_POSITION} from "../../../../common/components/junoComponentConstants";;
import SystemPreferenceService from "../../../../lib/system/service/SystemPreferenceService";
import HrmService from "../../../../lib/integration/hrm/service/HrmService";
import {HRMStatus} from "../../../../lib/integration/hrm/model/HrmFetchResults";
import moment from "moment";

angular.module('Admin.Section').component('hrmAdmin',
	{
		templateUrl: 'src/admin/section/hrm/admin/HRMAdmin.jsp',
		bindings: {},
		controller: ['$scope', '$http', '$httpParamSerializer', '$state', '$uibModal', function ($scope, $http, $httpParamSerializer, $state, $uibModal)
		{
			let ctrl = this;
			let hrmService = new HrmService();
			
			ctrl.COMPONENT_STYLE = JUNO_STYLE.DEFAULT;
			ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
			ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

			ctrl.$onInit = async () =>
			{
				ctrl.COMPONENT_STYLE = ctrl.COMPONENT_STYLE || JUNO_STYLE.DEFAULT;
				try
				{
					ctrl.latestResults = await hrmService.getLastResults();
				}
				finally
				{
					$scope.$apply();
				}
			};
			
			ctrl.fetchHRMDocs = async () =>
			{
				try
				{
					ctrl.working = true;
					ctrl.latestResults = await hrmService.fetchNewHRMDocuments();
				}
				finally
				{
					ctrl.working = false;
					$scope.$apply();
				}
			}
			
			ctrl.getSummaryText = (hrmStatus) =>
			{
				if (hrmStatus === HRMStatus.SUCCESS)
				{
					return "OK: No problems detected";
				}
				else if (hrmStatus === HRMStatus.HAS_ERRORS)
				{
					return "WARNING: One or more documents had problems. \nConsult the security log or contact support for assistance";
				}
				else
				{
					return "ERROR: Please contact support for assistance";
				}
			}
			
			ctrl.getSummaryClass = (hrmStatus) =>
			{
				
				if (hrmStatus === HRMStatus.SUCCESS)
				{
					return "ok";
				}
				else if (hrmStatus === HRMStatus.HAS_ERRORS)
				{
					return "warn";
				}
				else
				{
					return "error";
				}
			}
			
			ctrl.lastCheckedAsMinutesAgo = () =>
			{
				if (!ctrl.latestResults)
				{
					return "-";
				}
				
				const duration = moment.duration(moment().diff(ctrl.latestResults.endTime));
				return Math.floor(duration.asMinutes());
			}
		}]
	});