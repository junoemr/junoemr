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

import {SecurityPermissions} from "../common/security/securityConstants";
import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN, LABEL_POSITION} from "../common/components/junoComponentConstants";

angular.module('Flowsheet').component('flowsheetManager',
	{
		templateUrl: 'src/flowsheet/flowsheetManager.jsp',
		bindings: {
			componentStyle: "<?",
		},
		controller: [
			'$state',
			'$uibModal',
			'flowsheetApiService',
			'securityRolesService',
			function (
				$state,
				$uibModal,
				flowsheetApiService,
				securityRolesService,
			)
			{
				const ctrl = this;

				ctrl.SecurityPermissions = SecurityPermissions;
				ctrl.LABEL_POSITION = LABEL_POSITION;
				ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
				ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

				ctrl.validationAlerts = [] as Array<string>;
				ctrl.isLoading = true as boolean;

				ctrl.$onInit = async (): Promise<void> =>
				{
					ctrl.flowsheets = await flowsheetApiService.getAllFlowsheets();
					ctrl.isLoading = false;
				}

				ctrl.userCanEdit = (): boolean =>
				{
					return securityRolesService.hasSecurityPrivileges(SecurityPermissions.FLOWSHEET_UPDATE);
				}
				ctrl.userCanCreate = (): boolean =>
				{
					return securityRolesService.hasSecurityPrivileges(SecurityPermissions.FLOWSHEET_CREATE);
				}
				ctrl.userCanDelete = (): boolean =>
				{
					return securityRolesService.hasSecurityPrivileges(SecurityPermissions.FLOWSHEET_DELETE);
				}

				ctrl.onFlowsheetNew = (): void =>
				{
					ctrl.toFlowsheetEdit(null);
				}

				ctrl.onFlowsheetEdit = (flowsheet): void =>
				{
					ctrl.toFlowsheetEdit(flowsheet.id);
				}

				ctrl.onFlowsheetDelete = async (flowsheet): Promise<void> =>
				{
					const userOk : boolean = await Juno.Common.Util.confirmationDialog($uibModal, "Confirm Delete",
						"You are about to delete flowsheet " + flowsheet.name + "." +
						"Are you sure you want to delete this flowsheet?");

					if (userOk)
					{
						ctrl.isLoading = true;
						try
						{
							await flowsheetApiService.deleteFlowsheet(flowsheet.id);
							ctrl.flowsheets = ctrl.flowsheets.filter((entry) => entry.id !== flowsheet.id);
						}
						finally
						{
							ctrl.isLoading = false;
						}
					}
				}

				ctrl.onToggleFlowsheetEnabled = async (flowsheet): Promise<void> =>
				{
					ctrl.isLoading = true;
					try
					{
						flowsheet.enabled = await flowsheetApiService.setFlowsheetEnabled(flowsheet.id, !flowsheet.enabled);
					}
					finally
					{
						ctrl.isLoading = false;
					}
				}

				ctrl.toggleFlowsheetEnabledLabel = (flowsheet): string =>
				{
					return flowsheet.enabled ? "Disable" : "Enable";
				}

				ctrl.toFlowsheetEdit = (flowsheetId): void =>
				{
					$state.transitionTo('admin.editFlowsheet',
						{
							flowsheetId: flowsheetId,
						},
						{
							notify: false
						});
				}
			}]
	});
