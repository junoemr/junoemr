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
import FlowsheetModel from "../lib/flowsheet/model/FlowsheetModel";
import FlowsheetItemModel from "../lib/flowsheet/model/FlowsheetItemModel";
import FlowsheetItemGroupModel from "../lib/flowsheet/model/FlowsheetItemGroupModel";
import {FlowsheetItemType} from "../lib/flowsheet/model/FlowsheetItemType";
import {FlowsheetItemValueType} from "../lib/flowsheet/model/FlowsheetItemValueType";
import DxCodeModel from "../lib/dx/model/DxCodeModel";
import {DxCodingSystem} from "../lib/dx/model/DxCodingSystem";

angular.module('Flowsheet').component('flowsheetEdit',
	{
		templateUrl: 'src/flowsheet/flowsheetEdit.jsp',
		bindings: {
			componentStyle: "<?",
		},
		controller: [
			'$state',
			'$stateParams',
			'$uibModal',
			'flowsheetApiService',
			function (
				$state,
				$stateParams,
				$uibModal,
				flowsheetApiService,
			)
			{
				const ctrl = this;

				ctrl.SecurityPermissions = SecurityPermissions;
				ctrl.LABEL_POSITION = LABEL_POSITION;
				ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
				ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

				ctrl.isLoading = true;

				ctrl.$onInit = async (): Promise<void> =>
				{
					ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;
					if($stateParams.flowsheetId)
					{
						ctrl.flowsheet = await flowsheetApiService.getFlowsheet($stateParams.flowsheetId);
					}
					else
					{
						ctrl.flowsheet = new FlowsheetModel();
					}
					ctrl.isLoading = false;
				}
				ctrl.isNewFlowsheet = (): boolean =>
				{
					return Juno.Common.Util.isBlank(ctrl.flowsheet.id);
				}

				ctrl.onAddNewGroup = async (): Promise<void> =>
				{
					let groupName = await Juno.Common.Util.openInputDialog($uibModal,
						"Add Flowsheet Group",
						"Please enter a name for this group",
						ctrl.componentStyle,
						"Ok",
						"Enter group name here",
						255);

					if(groupName)
					{
						const newGroup = new FlowsheetItemGroupModel();
						newGroup.name = groupName;
						ctrl.flowsheet.flowsheetItemGroups.push(newGroup);
					}
				}

				ctrl.onRenameGroup = async (itemGroup): Promise<void> =>
				{
					let groupName = await Juno.Common.Util.openInputDialog($uibModal,
						"Rename Flowsheet Group",
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
					ctrl.onAddTriggerCode(DxCodingSystem.ICD9);
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
						ctrl.flowsheet.triggerCodes.push(selection.data);
					}
				}

				ctrl.onDeleteTriggerCode = async (triggerCode: DxCodeModel): Promise<void> =>
				{
					let confirmation = await Juno.Common.Util.confirmationDialog($uibModal,
						"Remove flowsheet trigger",
						"Are you sure you want to remove this trigger code from the flowsheet?",
						ctrl.componentStyle);

					if(confirmation)
					{
						ctrl.flowsheet.triggerCodes = ctrl.flowsheet.triggerCodes.filter((entry) => entry !== triggerCode);
					}
				}

				ctrl.onAddNewMeasurementItem = (itemGroup): void =>
				{
					ctrl.onAddNewItem(itemGroup, FlowsheetItemType.MEASUREMENT);
				}

				ctrl.onAddNewPreventionItem = (itemGroup): void =>
				{
					ctrl.onAddNewItem(itemGroup, FlowsheetItemType.PREVENTION);
				}

				ctrl.onAddNewItem = async (itemGroup, type): Promise<void> =>
				{
					const isMeasurementType = (type === FlowsheetItemType.MEASUREMENT);
					const typeLabel = (isMeasurementType) ? "measurement" : "prevention";
					const callback = (isMeasurementType) ? ctrl.lookupMeasurements : ctrl.lookupPreventions;

					const selection = await Juno.Common.Util.openTypeaheadDialog($uibModal,
						"Add flowsheet " + typeLabel,
						"Search for a " + typeLabel + " within the system",
						callback,
						ctrl.componentStyle,
						"Ok",
						"Search " + typeLabel + "s");
					if(selection)
					{
						const data = selection.data;
						let newItem = new FlowsheetItemModel();
						if(isMeasurementType)
						{
							newItem.name = data.name;
							newItem.type = FlowsheetItemType.MEASUREMENT;
							newItem.typeCode = data.code;
							newItem.description = data.description;
							newItem.guideline = data.instructions;
							newItem.valueType = FlowsheetItemValueType.STRING;
						}
						else
						{
							newItem.name = data.name;
							newItem.type = FlowsheetItemType.PREVENTION;
							newItem.typeCode = data.code;
							newItem.description = data.description;
							newItem.valueType = FlowsheetItemValueType.STRING;
						}
						itemGroup.flowsheetItems.push(newItem);
					}
				}

				ctrl.onRemoveItem = async (item, itemGroup): Promise<void> =>
				{
					let confirmation = await Juno.Common.Util.confirmationDialog($uibModal,
						"Remove flowsheet item",
						"Are you sure you want to remove this item from the flowsheet group?",
						ctrl.componentStyle);

					if(confirmation)
					{
						itemGroup.flowsheetItems = itemGroup.flowsheetItems.filter((entry) => !(entry.type === item.type && entry.typeCode === item.typeCode));
					}
				}

				ctrl.onRemoveGroup = async (group): Promise<void> =>
				{
					const confirmation = await Juno.Common.Util.confirmationDialog($uibModal,
						"Remove flowsheet group",
						"Are you sure you want to remove this group (and all items within it) from the flowsheet?",
						ctrl.componentStyle);

					if(confirmation)
					{
						ctrl.flowsheet.flowsheetItemGroups = ctrl.flowsheet.flowsheetItemGroups.filter((entry) => !(entry.name === group.name));
					}
				}

				ctrl.lookupPreventions = async (searchTerm): Promise<object[]> =>
				{
					const searchResults = await flowsheetApiService.searchPreventionTypes(searchTerm);
					return searchResults.body.map((result) =>
					{
						return {
							label: result.name,
							value: result.code,
							data: result,
						}
					});
				}

				ctrl.lookupMeasurements = async (searchTerm): Promise<object[]> =>
				{
					const searchResults = await flowsheetApiService.searchMeasurementTypes(searchTerm);
					return searchResults.body.map((result) =>
					{
						return {
							label: result.name + "(" + result.code + ")",
							value: result.code,
							data: result,
						}
					});
				}

				ctrl.lookupIcd9Codes = async (searchTerm): Promise<object[]> =>
				{
					const searchResults: DxCodeModel[] = await flowsheetApiService.searchDxCodes(DxCodingSystem.ICD9, searchTerm);
					return searchResults.map((result) =>
					{
						return {
							label: result.code + " (" + result.description + ")",
							value: result.code,
							data: result,
						}
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
					if(ctrl.isNewFlowsheet())
					{
						ctrl.flowsheet = await flowsheetApiService.createFlowsheet(ctrl.flowsheet);
					}
					else
					{
						ctrl.flowsheet = await flowsheetApiService.updateFlowsheet(ctrl.flowsheet.id, ctrl.flowsheet);
					}
				}
			}]
	});
