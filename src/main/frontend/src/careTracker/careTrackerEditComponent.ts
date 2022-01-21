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
import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN, JUNO_STYLE, LABEL_POSITION} from "../common/components/junoComponentConstants";
import CareTrackerModel from "../lib/careTracker/model/CareTrackerModel";
import CareTrackerItemModel from "../lib/careTracker/model/CareTrackerItemModel";
import CareTrackerItemGroupModel from "../lib/careTracker/model/CareTrackerItemGroupModel";
import {CareTrackerItemType} from "../lib/careTracker/model/CareTrackerItemType";
import {CareTrackerItemValueType} from "../lib/careTracker/model/CareTrackerItemValueType";
import DxCodeModel from "../lib/dx/model/DxCodeModel";
import {DxCodingSystem} from "../lib/dx/model/DxCodingSystem";
import PagedResponse from "../lib/common/response/pagedRespose";
import MeasurementTypeModel from "../lib/measurement/model/measurementTypeModel";
import {JunoSelectOption} from "../lib/common/junoSelectOption";

angular.module('CareTracker').component('careTrackerEdit',
	{
		templateUrl: 'src/careTracker/careTrackerEdit.jsp',
		bindings: {
			componentStyle: "<?",
		},
		controller: [
			'$state',
			'$stateParams',
			'$uibModal',
			'careTrackerApiService',
			'measurementApiService',
			'securityRolesService',
			function (
				$state,
				$stateParams,
				$uibModal,
				careTrackerApiService,
				measurementApiService,
				securityRolesService,
			)
			{
				const ctrl = this;

				ctrl.SecurityPermissions = SecurityPermissions;
				ctrl.LABEL_POSITION = LABEL_POSITION;
				ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
				ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

				ctrl.isLoading = true as boolean;
				ctrl.careTracker = null as CareTrackerModel;

				ctrl.$onInit = async (): Promise<void> =>
				{
					ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;
					if($stateParams.careTrackerId)
					{
						ctrl.careTracker = await careTrackerApiService.getCareTracker($stateParams.careTrackerId);
						ctrl.readOnly = ctrl.careTracker.systemManaged || !ctrl.userCanEdit();
					}
					else
					{
						ctrl.careTracker = new CareTrackerModel();
						ctrl.readOnly = ctrl.careTracker.systemManaged || !ctrl.userCanCreate();
					}
					ctrl.isLoading = false;
				}

				ctrl.isNewCareTracker = (): boolean =>
				{
					if(ctrl.careTracker)
					{
						return Juno.Common.Util.isBlank(ctrl.careTracker.id);
					}
				}

				ctrl.userCanEdit = (): boolean =>
				{
					return securityRolesService.hasSecurityPrivileges(SecurityPermissions.CareTrackerUpdate);
				}

				ctrl.userCanCreate = (): boolean =>
				{
					return securityRolesService.hasSecurityPrivileges(SecurityPermissions.CareTrackerCreate);
				}

				ctrl.onAddNewGroup = async (): Promise<void> =>
				{
					let groupName = await Juno.Common.Util.openInputDialog($uibModal,
						"Add group",
						"Please enter a name for this group",
						ctrl.componentStyle,
						"Ok",
						"Enter group name here",
						255);

					if(groupName)
					{
						const newGroup = new CareTrackerItemGroupModel();
						newGroup.name = groupName;
						ctrl.careTracker.careTrackerItemGroups.push(newGroup);
					}
				}

				ctrl.onRenameGroup = async (itemGroup): Promise<void> =>
				{
					let groupName = await Juno.Common.Util.openInputDialog($uibModal,
						"Rename group",
						"Please enter a new name for this group",
						ctrl.componentStyle,
						"Ok",
						"Enter group name here",
						255);

					if(groupName)
					{
						itemGroup.name = groupName;
					}
				}

				ctrl.onAddIcd9TriggerCode = (): void =>
				{
					ctrl.onAddTriggerCode(DxCodingSystem.Icd9);
				}

				ctrl.onAddTriggerCode = async (codingSystem: DxCodingSystem): Promise<void> =>
				{
					const typeLabel = codingSystem + "";
					const callback = ctrl.lookupIcd9Codes;

					const selection = await Juno.Common.Util.openTypeaheadDialog($uibModal,
						"Add " + typeLabel + " trigger",
						"Search for a code within the system",
						callback,
						ctrl.componentStyle,
						"Ok",
						"Search codes");
					if(selection)
					{
						ctrl.careTracker.triggerCodes.push(selection.data);
					}
				}

				ctrl.onDeleteTriggerCode = async (triggerCode: DxCodeModel): Promise<void> =>
				{
					let confirmation = await Juno.Common.Util.confirmationDialog($uibModal,
						"Remove trigger",
						"Are you sure you want to remove this trigger code from the care tracker?",
						ctrl.componentStyle);

					if(confirmation)
					{
						ctrl.careTracker.triggerCodes = ctrl.careTracker.triggerCodes.filter((entry) => entry !== triggerCode);
					}
				}

				ctrl.onAddNewMeasurementItem = (itemGroup): void =>
				{
					ctrl.onAddNewItem(itemGroup, CareTrackerItemType.Measurement);
				}

				ctrl.onAddNewPreventionItem = (itemGroup): void =>
				{
					ctrl.onAddNewItem(itemGroup, CareTrackerItemType.Prevention);
				}

				ctrl.onAddNewItem = async (itemGroup, type): Promise<void> =>
				{
					const isMeasurementType = (type === CareTrackerItemType.Measurement);
					const typeLabel = (isMeasurementType) ? "measurement" : "prevention";
					const callback = (isMeasurementType) ? ctrl.lookupMeasurements : ctrl.lookupPreventions;

					const selection = await Juno.Common.Util.openTypeaheadDialog($uibModal,
						"Add Care Tracker " + typeLabel,
						"Search for a " + typeLabel + " within the system",
						callback,
						ctrl.componentStyle,
						"Ok",
						"Search " + typeLabel + "s");
					if(selection)
					{
						const data = selection.data;
						let newItem = new CareTrackerItemModel();
						if(isMeasurementType)
						{
							newItem.name = data.name;
							newItem.type = CareTrackerItemType.Measurement;
							newItem.typeCode = data.code;
							newItem.description = data.description;
							newItem.guideline = data.instructions;
							newItem.valueType = CareTrackerItemValueType.Numeric;
						}
						else
						{
							newItem.name = data.name;
							newItem.type = CareTrackerItemType.Prevention;
							newItem.typeCode = data.code;
							newItem.description = data.description;
							newItem.valueType = CareTrackerItemValueType.Numeric;
						}
						itemGroup.careTrackerItems.push(newItem);
					}
				}

				ctrl.onRemoveItem = async (item, itemGroup): Promise<void> =>
				{
					let confirmation = await Juno.Common.Util.confirmationDialog($uibModal,
						"Remove item",
						"Are you sure you want to remove this item from the care tracker group?",
						ctrl.componentStyle);

					if(confirmation)
					{
						itemGroup.careTrackerItems = itemGroup.careTrackerItems.filter((entry) => !(entry.type === item.type && entry.typeCode === item.typeCode));
					}
				}

				ctrl.onRemoveGroup = async (group): Promise<void> =>
				{
					const confirmation = await Juno.Common.Util.confirmationDialog($uibModal,
						"Remove group",
						"Are you sure you want to remove this group (and all items within it) from the care tracker?",
						ctrl.componentStyle);

					if(confirmation)
					{
						ctrl.careTracker.careTrackerItemGroups = ctrl.careTracker.careTrackerItemGroups.filter((entry) => !(entry.name === group.name));
					}
				}

				ctrl.lookupPreventions = async (searchTerm): Promise<object[]> =>
				{
					const searchResults = await careTrackerApiService.searchPreventionTypes(searchTerm);
					return searchResults.body.map((result) =>
					{
						let extraData = (result.healthCanadaType && result.healthCanadaType !== result.name) ? (" [" + result.healthCanadaType + "]") : "";
						extraData = (result.atc) ? (extraData + " - " + result.atc) : extraData;

						return {
							label: result.name + extraData,
							value: result.code,
							data: result,
						}
					});
				}

				ctrl.lookupMeasurements = async (searchTerm): Promise<JunoSelectOption[]> =>
				{
					const searchResults: PagedResponse<MeasurementTypeModel> = await measurementApiService.searchMeasurementTypes(searchTerm);
					return searchResults.body.map((result: MeasurementTypeModel) =>
					{
						return {
							label: result.name + " (" + result.code + ") " + result.instructions,
							value: result.code,
							data: result,
						} as JunoSelectOption;
					});
				}

				ctrl.lookupIcd9Codes = async (searchTerm): Promise<JunoSelectOption[]> =>
				{
					const searchResults: DxCodeModel[] = await careTrackerApiService.searchDxCodes(DxCodingSystem.Icd9, searchTerm);
					return searchResults.map((result: DxCodeModel) =>
					{
						return {
							label: result.code + " (" + result.description + ")",
							value: result.code,
							data: result,
						} as JunoSelectOption;
					});
				}

				ctrl.onCancel = (): void =>
				{
					if($state.includes("**.admin.**"))
					{
						$state.go('admin.configureHealthTracker');
					}
					else if($state.includes("**.settings.**"))
					{
						$state.go('settings.tracker');
					}
					else if($state.includes("**.record.**"))
					{
						$state.go("record.configureHealthTracker",
							{
								demographicNo: $stateParams.demographicNo,
							});
					}
					else
					{
						$state.go('dashboard');
					}
				}

				ctrl.onSave = async (): Promise<void> =>
				{
					ctrl.isLoading = true;
					try
					{
						if (ctrl.isNewCareTracker())
						{
							ctrl.careTracker = await careTrackerApiService.createCareTracker(ctrl.careTracker);
							ctrl.readOnly = ctrl.careTracker.systemManaged || !ctrl.userCanEdit();
						}
						else
						{
							ctrl.careTracker = await careTrackerApiService.updateCareTracker(ctrl.careTracker.id, ctrl.careTracker);
						}
						Juno.Common.Util.successAlert($uibModal, "Save Complete", "The changes have been applied");
					}
					finally
					{
						ctrl.isLoading = false;
					}
				}

				ctrl.canSave = (): boolean =>
				{
					return ctrl.careTracker  && !ctrl.readOnly && !Juno.Common.Util.isBlank(ctrl.careTracker.name);
				}

				ctrl.saveButtonTooltip = (): string =>
				{
					if(ctrl.careTracker)
					{
						if (ctrl.careTracker.systemManaged)
						{
							return "System managed care trackers can not be modified";
						}
						else if (!ctrl.canSave())
						{
							return "You do not have the required permissions to save this";
						}
						else
						{
							return "Save Changes";
						}
					}
				}
			}]
	});
