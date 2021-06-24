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
import {ItemType} from "../lib/flowsheet/FlowsheetConstants";
import FlowsheetItemModel from "../lib/flowsheet/model/FlowsheetItemModel";
import {FlowsheetItem} from "../../generated";

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
				ctrl.ItemType = ItemType;
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
					// @ts-ignore
					return Juno.Common.Util.isBlank(ctrl.flowsheet.id);
				}

				ctrl.onAddNewGroup = async (): Promise<void> =>
				{
					// @ts-ignore
					let groupName = await Juno.Common.Util.openInputDialog($uibModal,
						"Add Flowsheet Group",
						"Please enter a name for this group",
						ctrl.componentStyle,
						"Ok",
						"Enter group name here",
						255);

					if(groupName)
					{
						const newGroup = {
							name: groupName,
							description: null,
						}
						ctrl.flowsheet.flowsheetItemGroups.push(newGroup);
					}
				}

				ctrl.onRenameGroup = async (itemGroup): Promise<void> =>
				{
					// @ts-ignore
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

				ctrl.onAddNewMeasurementItem = (itemGroup): void =>
				{
					ctrl.onAddNewItem(itemGroup, ItemType.MEASUREMENT);
				}

				ctrl.onAddNewPreventionItem = (itemGroup): void =>
				{
					ctrl.onAddNewItem(itemGroup, ItemType.PREVENTION);
				}

				ctrl.onAddNewItem = (itemGroup, type): void =>
				{
					const isMeasurementType = (type === ItemType.MEASUREMENT);
					const typeLabel = (isMeasurementType) ? "measurement" : "prevention";
					const callback = (isMeasurementType) ? ctrl.lookupMeasurements : ctrl.lookupPreventions;

					// @ts-ignore
					Juno.Common.Util.openTypeaheadDialog($uibModal,
						"Add flowsheet " + typeLabel,
						"Search for a " + typeLabel + " within the system",
						callback,
						ctrl.componentStyle,
						"Ok",
						"Search " + typeLabel + "s",
					).then((selection) =>
					{
						if(selection)
						{
							const data = selection.data;
							let newItem = new FlowsheetItemModel();
							if(isMeasurementType)
							{
								newItem.name = data.name;
								newItem.type = FlowsheetItem.TypeEnum.MEASUREMENT;
								newItem.typeCode = data.code;
								newItem.description = data.description;
								newItem.guideline = data.instructions;
								newItem.valueType = FlowsheetItem.ValueTypeEnum.STRING;
							}
							else
							{
								newItem.name = data.name;
								newItem.type = FlowsheetItem.TypeEnum.PREVENTION;
								newItem.typeCode = data.code;
								newItem.description = data.description;
								newItem.valueType = FlowsheetItem.ValueTypeEnum.STRING;
							}
							itemGroup.flowsheetItems.push(newItem);
						}
					}).catch((reason) =>
					{
						// cancelled modal
					});
				}

				ctrl.lookupPreventions = async (searchTerm): Promise<Array<object>> =>
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

				ctrl.lookupMeasurements = async (searchTerm): Promise<Array<object>> =>
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

				ctrl.onCancel = (): void =>
				{
					$state.transitionTo('admin.configureFlowsheets',
						{},
						{
							notify: false
						});
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
