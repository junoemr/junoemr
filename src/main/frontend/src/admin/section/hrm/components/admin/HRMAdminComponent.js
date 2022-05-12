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

import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN, JUNO_STYLE, LABEL_POSITION} from "../../../../../common/components/junoComponentConstants";;
import HrmService from "../../../../../lib/integration/hrm/service/HrmService";
import {HRMStatus} from "../../../../../lib/integration/hrm/model/HrmFetchResults";
import moment from "moment";
import SystemPreferenceService from "../../../../../lib/system/service/SystemPreferenceService";
import {SecurityPermissions} from "../../../../../common/security/securityConstants";
import ToastService from "../../../../../lib/alerts/service/ToastService";

angular.module('Admin.Section').component('hrmAdmin',
	{
		templateUrl: 'src/admin/section/hrm/components/admin/HRMAdmin.jsp',
		bindings: {},
		controller: ['$scope', '$http', '$httpParamSerializer', 'securityRolesService',
			function ($scope, $http, $httpParamSerializer, securityRolesService)
			{
				let ctrl = this;
				const hrmService = new HrmService();
				const systemPreferenceService = new SystemPreferenceService($http, $httpParamSerializer);
				const toastService = new ToastService();

				ctrl.COMPONENT_STYLE = JUNO_STYLE.DEFAULT;
				ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
				ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;
				ctrl.LABEL_POSITION = LABEL_POSITION.TOP;

				ctrl.pollingEnabled = false;
				ctrl.pollingInterval = 0;

				const pollingEnabledPreference = SystemPreferences.HrmPollingEnabled;
				const pollingIntervalProperty = SystemPreferences.HrmPollingInterval;

				ctrl.$onInit = async () =>
				{
					try
					{
						ctrl.COMPONENT_STYLE = ctrl.COMPONENT_STYLE || JUNO_STYLE.DEFAULT;

						const results = await Promise.all([
							systemPreferenceService.getPreference(pollingIntervalProperty, 3600),
							systemPreferenceService.isPreferenceEnabled(pollingEnabledPreference),
						]);

						ctrl.pollingInterval = Math.floor(results[0]/60);
						ctrl.pollingEnabled = results[1];
						ctrl.latestResults = await hrmService.getLastResults();
					}
					finally
					{
						$scope.apply();
					}
				};

				ctrl.togglePolling = async (checked) =>
				{
					try
					{
						await systemPreferenceService.setPreference(pollingEnabledPreference, checked);
					}
					catch (exception)
					{
						console.error(exception);
						toastService.errorToast("Could not enable polling, please try again later");
					}
					finally
					{
						$scope.$apply();
					}
				}

				ctrl.fetchHRMDocs = async () =>
				{
					try
					{
						ctrl.working = true;
						ctrl.latestResults = await hrmService.fetchNewHRMDocuments();
					}
					catch (exception)
					{
						console.error(exception);
						toastService.errorToast("Could not fetch documents, contact support for assistance");
					}
					finally
					{
						ctrl.working = false;
						$scope.$apply();
					}
				}

				ctrl.getSummaryText = (hrmStatus) =>
				{
					if (!ctrl.pollingEnabled)
					{
						return "Disabled";
					}
					if (!hrmStatus)
					{
						return "Not yet polled";
					}
					else if (hrmStatus === HRMStatus.SUCCESS)
					{
						return "OK: No problems detected";
					}
					else if (hrmStatus === HRMStatus.HAS_ERRORS)
					{
						return "WARNING: One or more documents had problems. Consult the security log or contact support for assistance";
					}
					else
					{
						return "ERROR: Consult the security log or contact support for assistance";
					}
				}

				ctrl.getSummaryClass = (hrmStatus) =>
				{
					if (!ctrl.pollingEnabled || !hrmStatus)
					{
						return "off";
					}
					else if (hrmStatus === HRMStatus.SUCCESS)
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

				ctrl.lastCheckedMessage = () =>
				{
					if (!ctrl.latestResults)
					{
						return "Results have not been downloaded today";
					}
					else
					{
						const duration = moment.duration(moment().diff(ctrl.latestResults.endTime));
						return `Last checked ${Math.floor(duration.asMinutes())} minutes ago`;
					}
				}

				ctrl.canRead = () =>
				{
					return securityRolesService.hasSecurityPrivileges(SecurityPermissions.HrmRead);
				}
			}]
	});