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
import CareTrackerModel from "../lib/careTracker/model/CareTrackerModel";

angular.module('CareTracker').component('careTrackerManager',
	{
		templateUrl: 'src/careTracker/careTrackerManager.jsp',
		bindings: {
			componentStyle: "<?",
			user: "<?",
		},
		controller: [
			'$state',
			'$stateParams',
			'$uibModal',
			'careTrackerApiService',
			'securityRolesService',
			function (
				$state,
				$stateParams,
				$uibModal,
				careTrackerApiService,
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

					ctrl.careTrackers = await careTrackerApiService.searchCareTrackers(
						null,
						true,
						ctrl.manageProviderLevel(),
						ctrl.userId,
						ctrl.manageDemographicLevel(),
						ctrl.demographicId,
						1, 100);
					ctrl.separateCareTrackerLevels(ctrl.careTrackers);
					ctrl.isLoading = false;
				}

				ctrl.separateCareTrackerLevels = (careTrackers: CareTrackerModel[]): void =>
				{
					ctrl.tablesConfig = [
						{
							name: "Clinic Care Trackers",
							visible: true,
							enableEdit: ctrl.userCanEditClinicLevel(),
							enableClone: ctrl.userCanCreateClinicLevel(),
							enableDelete: ctrl.userCanDeleteClinicLevel(),
							items: [],
						},
						{
							name: "My Care Trackers",
							visible: ctrl.manageProviderLevel(),
							enableEdit: ctrl.userCanEdit(),
							enableClone: ctrl.userCanCreate(),
							enableDelete: ctrl.userCanDelete(),
							items: [],
						},
						{
							name: "Patient Care Trackers",
							visible: ctrl.manageDemographicLevel(),
							enableEdit: ctrl.userCanEdit(),
							enableClone: ctrl.userCanCreate(),
							enableDelete: ctrl.userCanDelete(),
							items: [],
						}
					];
					// sort all careTrackers by level (clinic, provider, demographic)
					careTrackers.forEach((careTracker: CareTrackerModel) =>
					{
						if (careTracker.isDemographicLevel())
						{
							ctrl.tablesConfig[2].items.push(careTracker);
						}
						else if (careTracker.isProviderLevel())
						{
							ctrl.tablesConfig[1].items.push(careTracker);
						}
						else
						{
							ctrl.tablesConfig[0].items.push(careTracker);
						}
					});
				}

				ctrl.userCanEdit = (): boolean =>
				{
					return securityRolesService.hasSecurityPrivileges(SecurityPermissions.CareTrackerUpdate);
				}
				ctrl.userCanEditClinicLevel = (): boolean =>
				{
					return ctrl.userCanEdit() && securityRolesService.hasSecurityPrivileges(SecurityPermissions.AdminUpdate);
				}
				ctrl.userCanCreate = (): boolean =>
				{
					return securityRolesService.hasSecurityPrivileges(SecurityPermissions.CareTrackerCreate);
				}
				ctrl.userCanCreateClinicLevel = (): boolean =>
				{
					return ctrl.userCanCreate() && securityRolesService.hasSecurityPrivileges(SecurityPermissions.AdminCreate);
				}
				ctrl.userCanDelete = (): boolean =>
				{
					return securityRolesService.hasSecurityPrivileges(SecurityPermissions.CareTrackerDelete);
				}
				ctrl.userCanDeleteClinicLevel = (): boolean =>
				{
					return ctrl.userCanDelete() && securityRolesService.hasSecurityPrivileges(SecurityPermissions.AdminDelete);
				}

				ctrl.manageProviderLevel = (): boolean =>
				{
					return Boolean(ctrl.userId);
				}
				ctrl.manageDemographicLevel = (): boolean =>
				{
					return Boolean(ctrl.demographicId);
				}

				ctrl.onCareTrackerNew = (): void =>
				{
					ctrl.toCareTrackerEdit(null);
				}

				ctrl.onCareTrackerEdit = (careTracker): void =>
				{
					ctrl.toCareTrackerEdit(careTracker.id);
				}

				ctrl.onCareTrackerDelete = async (careTracker: CareTrackerModel): Promise<void> =>
				{
					const userOk : boolean = await Juno.Common.Util.confirmationDialog($uibModal,
						"Confirm Delete",
						"You are about to delete care tracker " + careTracker.name + "." +
						"Are you sure you want to delete this care tracker?");

					if (userOk)
					{
						ctrl.isLoading = true;
						try
						{
							await careTrackerApiService.deleteCareTracker(careTracker.id);
							ctrl.careTrackers = ctrl.careTrackers.filter((entry) => entry.id !== careTracker.id);
							ctrl.separateCareTrackerLevels(ctrl.careTrackers);
						}
						finally
						{
							ctrl.isLoading = false;
						}
					}
				}

				ctrl.onCloneCareTracker = async (careTracker: CareTrackerModel): Promise<void> =>
				{
					const options = ctrl.getCloneOptions(careTracker);
					let selection = null;
					if(options.length < 1)
					{
						return;
					}
					if(options.length === 1)
					{
						const confirm = await Juno.Common.Util.confirmationDialog($uibModal,
							"Copy Care Tracker",
							"Are you sure you want to copy this care tracker for " + options[0].label,
							ctrl.componentStyle);
						if(confirm)
						{
							selection = options[0].value;
						}
					}
					else
					{
						selection = await Juno.Common.Util.openSelectDialog($uibModal,
							"Copy Care Tracker",
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
							let careTrackerClone: CareTrackerModel = null;
							if(selection === accessLevels.CLINIC)
							{
								careTrackerClone = await careTrackerApiService.cloneCareTrackerForClinic(careTracker.id);
							}
							else if(selection === accessLevels.PROVIDER)
							{
								careTrackerClone = await careTrackerApiService.cloneCareTrackerForProvider(careTracker.id, ctrl.userId);
							}
							else if(selection === accessLevels.DEMOGRAPHIC)
							{
								careTrackerClone = await careTrackerApiService.cloneCareTrackerForDemographic(careTracker.id, ctrl.demographicId);
							}
							else
							{
								return;
							}
							ctrl.careTrackers.push(careTrackerClone);
							ctrl.separateCareTrackerLevels(ctrl.careTrackers);
						}
						finally
						{
							ctrl.isLoading = false;
						}
					}
				}

				ctrl.getCloneOptions = (careTracker: CareTrackerModel): object[] =>
				{
					const options = [];
					if(!careTracker.isDemographicLevel() && !careTracker.isProviderLevel() && ctrl.userCanCreateClinicLevel())
					{
						options.push({label: "All Users", value: accessLevels.CLINIC});
					}
					if (!careTracker.isDemographicLevel() && ctrl.manageProviderLevel())
					{
						options.push({label: "Just Me", value: accessLevels.PROVIDER});
					}
					if (ctrl.manageDemographicLevel())
					{
						options.push({label: "Current patient only", value: accessLevels.DEMOGRAPHIC});
					}
					return options;
				}

				ctrl.onToggleCareTrackerEnabled = async (careTracker: CareTrackerModel): Promise<void> =>
				{
					ctrl.isLoading = true;
					try
					{
						careTracker.enabled = await careTrackerApiService.setCareTrackerEnabled(careTracker.id, !careTracker.enabled);
					}
					finally
					{
						ctrl.isLoading = false;
					}
				}

				ctrl.toggleCareTrackerEnabledLabel = (careTracker: CareTrackerModel): string =>
				{
					return careTracker.enabled ? "Disable" : "Enable";
				}

				ctrl.toCareTrackerEdit = (id: number): void =>
				{
					if($state.includes("**.admin.**"))
					{
						$state.go('admin.editCareTracker',
							{
								careTrackerId: id,
							});
					}
					else if($state.includes("**.settings.**"))
					{
						$state.go('settings.editCareTracker',
							{
								careTrackerId: id,
							});
					}
					else if($state.includes("**.record.**"))
					{
						$state.go('record.editCareTracker',
							{
								careTrackerId: id,
								demographicNo: ctrl.demographicId,
							});
					}
				}
			}]
	});
