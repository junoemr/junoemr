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
import CareTrackerModel from "../lib/flowsheet/model/CareTrackerModel";

angular.module('Flowsheet').component('flowsheetManager',
	{
		templateUrl: 'src/flowsheet/flowsheetManager.jsp',
		bindings: {
			componentStyle: "<?",
			user: "<?",
		},
		controller: [
			'$state',
			'$stateParams',
			'$uibModal',
			'flowsheetApiService',
			'securityRolesService',
			function (
				$state,
				$stateParams,
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

				ctrl.tablesConfig = [];

				enum accessLevels {
					CLINIC = 1,
					PROVIDER = 2,
					DEMOGRAPHIC = 3,
				}

				ctrl.$onInit = async (): Promise<void> =>
				{
					ctrl.userId = ctrl.user?.providerNo || null;
					ctrl.demographicId = $stateParams.demographicNo || null;

					ctrl.flowsheets = await flowsheetApiService.searchFlowsheets(
						null,
						true,
						ctrl.manageProviderLevel(),
						ctrl.userId,
						ctrl.manageDemographicLevel(),
						ctrl.demographicId,
						1, 100);
					ctrl.separateFlowsheetLevels(ctrl.flowsheets);
					ctrl.isLoading = false;
				}

				ctrl.separateFlowsheetLevels = (flowsheets: CareTrackerModel[]): void =>
				{
					ctrl.tablesConfig = [
						{
							name: "Clinic Flowsheets",
							visible: true,
							enableEdit: ctrl.userCanEditClinicLevel(),
							enableClone: ctrl.userCanCreateClinicLevel(),
							enableDelete: ctrl.userCanDeleteClinicLevel(),
							items: [],
						},
						{
							name: "My Flowsheets",
							visible: ctrl.manageProviderLevel(),
							enableEdit: ctrl.userCanEdit(),
							enableClone: ctrl.userCanCreate(),
							enableDelete: ctrl.userCanDelete(),
							items: [],
						},
						{
							name: "Patient Flowsheets",
							visible: ctrl.manageDemographicLevel(),
							enableEdit: ctrl.userCanEdit(),
							enableClone: ctrl.userCanCreate(),
							enableDelete: ctrl.userCanDelete(),
							items: [],
						}
					];
					// sort all flowsheets by level (clinic, provider, demographic)
					flowsheets.forEach((flowsheet: CareTrackerModel) =>
					{
						if (flowsheet.isDemographicLevel())
						{
							ctrl.tablesConfig[2].items.push(flowsheet);
						}
						else if (flowsheet.isProviderLevel())
						{
							ctrl.tablesConfig[1].items.push(flowsheet);
						}
						else
						{
							ctrl.tablesConfig[0].items.push(flowsheet);
						}
					});
				}

				ctrl.userCanEdit = (): boolean =>
				{
					return securityRolesService.hasSecurityPrivileges(SecurityPermissions.CARE_TRACKER_UPDATE);
				}
				ctrl.userCanEditClinicLevel = (): boolean =>
				{
					return ctrl.userCanEdit() && securityRolesService.hasSecurityPrivileges(SecurityPermissions.ADMIN_UPDATE);
				}
				ctrl.userCanCreate = (): boolean =>
				{
					return securityRolesService.hasSecurityPrivileges(SecurityPermissions.CARE_TRACKER_CREATE);
				}
				ctrl.userCanCreateClinicLevel = (): boolean =>
				{
					return ctrl.userCanCreate() && securityRolesService.hasSecurityPrivileges(SecurityPermissions.ADMIN_CREATE);
				}
				ctrl.userCanDelete = (): boolean =>
				{
					return securityRolesService.hasSecurityPrivileges(SecurityPermissions.CARE_TRACKER_DELETE);
				}
				ctrl.userCanDeleteClinicLevel = (): boolean =>
				{
					return ctrl.userCanDelete() && securityRolesService.hasSecurityPrivileges(SecurityPermissions.ADMIN_DELETE);
				}

				ctrl.manageProviderLevel = (): boolean =>
				{
					return Boolean(ctrl.userId);
				}
				ctrl.manageDemographicLevel = (): boolean =>
				{
					return Boolean(ctrl.demographicId);
				}

				ctrl.onFlowsheetNew = (): void =>
				{
					ctrl.toFlowsheetEdit(null);
				}

				ctrl.onFlowsheetEdit = (flowsheet): void =>
				{
					ctrl.toFlowsheetEdit(flowsheet.id);
				}

				ctrl.onFlowsheetDelete = async (flowsheet: CareTrackerModel): Promise<void> =>
				{
					const userOk : boolean = await Juno.Common.Util.confirmationDialog($uibModal,
						"Confirm Delete",
						"You are about to delete flowsheet " + flowsheet.name + "." +
						"Are you sure you want to delete this flowsheet?");

					if (userOk)
					{
						ctrl.isLoading = true;
						try
						{
							await flowsheetApiService.deleteFlowsheet(flowsheet.id);
							ctrl.flowsheets = ctrl.flowsheets.filter((entry) => entry.id !== flowsheet.id);
							ctrl.separateFlowsheetLevels(ctrl.flowsheets);
						}
						finally
						{
							ctrl.isLoading = false;
						}
					}
				}

				ctrl.onCloneFlowsheet = async (flowsheet: CareTrackerModel): Promise<void> =>
				{
					const options = ctrl.getCloneOptions(flowsheet);
					let selection = null;
					if(options.length < 1)
					{
						return;
					}
					if(options.length === 1)
					{
						const confirm = await Juno.Common.Util.confirmationDialog($uibModal,
							"Copy flowsheet",
							"Are you sure you want to copy this flowsheet for " + options[0].label,
							ctrl.componentStyle);
						if(confirm)
						{
							selection = options[0].value;
						}
					}
					else
					{
						selection = await Juno.Common.Util.openSelectDialog($uibModal,
							"Copy flowsheet",
							"Select who this copy can be used by.",
							options,
							ctrl.componentStyle,
							"Create Copy",
							"Select user level");
					}

					if(selection)
					{
						ctrl.isLoading = true;
						try
						{
							let flowsheetClone: CareTrackerModel = null;
							if(selection === accessLevels.CLINIC)
							{
								flowsheetClone = await flowsheetApiService.cloneFlowsheetForClinic(flowsheet.id);
							}
							else if(selection === accessLevels.PROVIDER)
							{
								flowsheetClone = await flowsheetApiService.cloneFlowsheetForProvider(flowsheet.id, ctrl.userId);
							}
							else if(selection === accessLevels.DEMOGRAPHIC)
							{
								flowsheetClone = await flowsheetApiService.cloneFlowsheetForDemographic(flowsheet.id, ctrl.demographicId);
							}
							else
							{
								return;
							}
							ctrl.flowsheets.push(flowsheetClone);
							ctrl.separateFlowsheetLevels(ctrl.flowsheets);
						}
						finally
						{
							ctrl.isLoading = false;
						}
					}
				}

				ctrl.getCloneOptions = (flowsheet: CareTrackerModel): object[] =>
				{
					const options = [];
					if(!flowsheet.isDemographicLevel() && !flowsheet.isProviderLevel() && ctrl.userCanCreateClinicLevel())
					{
						options.push({label: "All Users", value: accessLevels.CLINIC});
					}
					if (!flowsheet.isDemographicLevel() && ctrl.manageProviderLevel())
					{
						options.push({label: "Just Me", value: accessLevels.PROVIDER});
					}
					if (ctrl.manageDemographicLevel())
					{
						options.push({label: "Current patient only", value: accessLevels.DEMOGRAPHIC});
					}
					return options;
				}

				ctrl.onToggleFlowsheetEnabled = async (flowsheet: CareTrackerModel): Promise<void> =>
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

				ctrl.toggleFlowsheetEnabledLabel = (flowsheet: CareTrackerModel): string =>
				{
					return flowsheet.enabled ? "Disable" : "Enable";
				}

				ctrl.toFlowsheetEdit = (flowsheetId: number): void =>
				{
					if($state.includes("**.admin.**"))
					{
						$state.go('admin.editFlowsheet',
							{
								flowsheetId: flowsheetId,
							});
					}
					else if($state.includes("**.settings.**"))
					{
						$state.go('settings.editFlowsheet',
							{
								flowsheetId: flowsheetId,
							});
					}
					else if($state.includes("**.record.**"))
					{
						$state.go('record.editFlowsheet',
							{
								flowsheetId: flowsheetId,
								demographicNo: ctrl.demographicId,
							});
					}
				}
			}]
	});
