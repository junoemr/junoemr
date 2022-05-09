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

import {JUNO_STYLE, LABEL_POSITION, JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN} from "../../../../../common/components/junoComponentConstants";
import HrmUserSettings from "../../../../../lib/integration/hrm/model/HrmUserSettings"
import SystemPreferenceService from "../../../../../lib/system/service/SystemPreferenceService";
import ToastService from "../../../../../lib/alerts/service/ToastService";
import {SecurityPermissions} from "../../../../../common/security/securityConstants";
import HrmService from "../../../../../lib/integration/hrm/service/HrmService";

angular.module('Admin.Section').component('hrmSettings',
	{
		templateUrl: 'src/admin/section/hrm/components/settings/HRMSettings.jsp',
		bindings: {},
		controller: ['$scope', '$http', '$httpParamSerializer', 'securityRolesService',
			function ($scope, $http, $httpParamSerializer, securityRolesService)
			{
				let ctrl = this;
				const systemPreferenceService = new SystemPreferenceService($http, $httpParamSerializer);
				const hrmWebService = new HrmService();
				const toastService = new ToastService();


				ctrl.userSettings = new HrmUserSettings();
				ctrl.orginalSettings = new HrmUserSettings();

				ctrl.newDecryptionKey = "";

				ctrl.isWorking = false;
				ctrl.isReadOnly = true;
				ctrl.isReadOnlyKey = true;

				ctrl.USERNAME_KEY = "omd.hrm.user";
				ctrl.MAILBOX_ADDRESS_KEY = "omd.hrm.address";
				ctrl.REMOTE_PATH_KEY = "omd.hrm.remote_path";
				ctrl.PORT_KEY = "omd.hrm.port";

				ctrl.LABEL_POSITION = LABEL_POSITION.TOP;
				ctrl.COMPONENT_STYLE = JUNO_STYLE.DEFAULT;
				ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
				ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

				ctrl.canEdit = () =>
				{
					return securityRolesService.hasSecurityPrivileges(SecurityPermissions.HrmUpdate);
				}

				ctrl.$onInit = async () =>
				{
					ctrl.COMPONENT_STYLE = ctrl.COMPONENT_STYLE || JUNO_STYLE.DEFAULT;
					await ctrl.fetchUserSettings();
					$scope.$apply();
				};

				ctrl.onEdit = () =>
				{
					ctrl.originalSettings = angular.copy(ctrl.userSettings)
					ctrl.isReadOnly = false;
				}

				ctrl.onCancel = () =>
				{
					ctrl.userSettings = angular.copy(ctrl.originalSettings);
					ctrl.isReadOnly = true;
					$scope.$apply();
				}

				ctrl.onSave = async () =>
				{
					ctrl.isWorking = true;

					try
					{
						await Promise.all([
							systemPreferenceService.setPreference(ctrl.USERNAME_KEY, ctrl.userSettings.userName),
							systemPreferenceService.setPreference(ctrl.MAILBOX_ADDRESS_KEY, ctrl.userSettings.mailBoxAddress),
							systemPreferenceService.setPreference(ctrl.REMOTE_PATH_KEY, ctrl.userSettings.remotePath),
							systemPreferenceService.setPreference(ctrl.PORT_KEY, ctrl.userSettings.port),
						]);

						toastService.successToast("HRM settings updated");
					}
					catch(err)
					{
						toastService.errorToast("There was an error updating your HRM settings");
					}
					finally
					{
						// Refresh values based on the only source of truth, which is the backend.
						await ctrl.fetchUserSettings();
						ctrl.isReadOnly = true;
						ctrl.isWorking = false;
						$scope.$apply();
					}
				}

				ctrl.fetchUserSettings = async() =>
				{
					let propertyValues = await systemPreferenceService.getPreferences(
						ctrl.USERNAME_KEY,
						ctrl.MAILBOX_ADDRESS_KEY,
						ctrl.REMOTE_PATH_KEY,
						ctrl.PORT_KEY,
					);

					let settings = new HrmUserSettings();
					settings.userName = propertyValues[ctrl.USERNAME_KEY];
					settings.mailBoxAddress = propertyValues[ctrl.MAILBOX_ADDRESS_KEY];
					settings.remotePath = propertyValues[ctrl.REMOTE_PATH_KEY];
					settings.port = propertyValues[ctrl.PORT_KEY];

					ctrl.userSettings = settings;
				}

				ctrl.onEditKey = () =>
				{
					ctrl.isReadKey = false;
				}

				ctrl.onCancelKey = () =>
				{
					ctrl.newDecryptionKey = "";
					ctrl.isReadOnlyKey = true;
					$scope.$apply();
				}
			}]
	});