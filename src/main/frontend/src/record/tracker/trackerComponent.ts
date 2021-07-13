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

import {SecurityPermissions} from "../../common/security/securityConstants";
import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN, LABEL_POSITION} from "../../common/components/junoComponentConstants";
import FlowsheetModel from "../../lib/flowsheet/model/FlowsheetModel";
import DxRecordModel from "../../lib/dx/model/DxRecordModel";
import DxCodeModel from "../../lib/dx/model/DxCodeModel";

angular.module('Record.Tracker').component('healthTracker',
	{
		templateUrl: 'src/record/tracker/tracker.jsp',
		bindings: {
			componentStyle: "<?",
			user: "<",
		},
		controller: [
			'$state',
			'$stateParams',
			'$uibModal',
			'demographicApiService',
			'flowsheetApiService',
			function (
				$state,
				$stateParams,
				$uibModal,
				demographicApiService,
				flowsheetApiService)
			{
				const ctrl = this;
				ctrl.SecurityPermissions = SecurityPermissions;
				ctrl.LABEL_POSITION = LABEL_POSITION;
				ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
				ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

				ctrl.flowsheets = [] as FlowsheetModel[];
				ctrl.triggerdFlowsheets = [] as FlowsheetModel[];
				ctrl.selectedFlowsheet = null as FlowsheetModel;
				ctrl.activeDxRecords = [];

				ctrl.accordianListItems = [
					{
						name: "Standard Flowsheets",
						expanded: false,
						items: [], // will be the list of clinic flowsheets
					},
					{
						name: "My Flowsheets",
						expanded: false,
						items: [], // will be the list of provider flowsheets
					},
					{
						name: "Patient Flowsheets",
						expanded: false,
						items: [], // will be the list of demographic flowsheets
					}
				];

				ctrl.$onInit = async (): Promise<void> =>
				{
					ctrl.demographicNo = $stateParams.demographicNo;
					ctrl.flowsheets = await flowsheetApiService.searchFlowsheets(true, true, true, ctrl.user.providerNo, true, ctrl.demographicNo, 1, 100);
					ctrl.activeDxRecords = await demographicApiService.getActiveDxRecords(ctrl.demographicNo);

					if($stateParams.flowsheetId)
					{
						ctrl.selectedFlowsheet = ctrl.flowsheets.find((flowsheet) => flowsheet.id === Number($stateParams.flowsheetId));
					}

					ctrl.initFlowsheetLists(ctrl.flowsheets);
				}

				ctrl.initFlowsheetLists = (flowsheets: FlowsheetModel[]): void =>
				{
					const clinicFlowsheetItems = ctrl.accordianListItems[0].items;
					const providerFlowsheetItems = ctrl.accordianListItems[1].items;
					const demographicFlowsheetItems = ctrl.accordianListItems[2].items;

					// sort all flowsheets by level (clinic, provider, demographic)
					flowsheets.forEach((flowsheet: FlowsheetModel) =>
					{
						if(flowsheet.ownerDemographicId)
						{
							demographicFlowsheetItems.push(flowsheet);
						}
						else if(flowsheet.ownerProviderId)
						{
							providerFlowsheetItems.push(flowsheet);
						}
						else
						{
							clinicFlowsheetItems.push(flowsheet);
						}
					});

					// find triggered flowsheets, and ensure only the more specific one appears when related flowsheets are found
					// a flowsheet is related if it has a parent ID
					const flowsheetMap = new Map();

					// put all base level flowsheets into a map
					ctrl.getTriggeredFlowsheets(clinicFlowsheetItems).forEach((flowsheet: FlowsheetModel) => {
						flowsheetMap.set(flowsheet.id, flowsheet);
					});

					// overwrite mapped values with provider specific version where possible
					ctrl.getTriggeredFlowsheets(providerFlowsheetItems).forEach((flowsheet: FlowsheetModel) => {
						const key = flowsheet.parentFlowsheetId ? flowsheet.parentFlowsheetId : flowsheet.id;
						flowsheetMap.set(key, flowsheet);
					});

					// overwrite mapped values again with demographic specific version where possible
					ctrl.getTriggeredFlowsheets(demographicFlowsheetItems).forEach((flowsheet: FlowsheetModel) => {
						const key = flowsheet.parentFlowsheetId ? flowsheet.parentFlowsheetId : flowsheet.id;
						flowsheetMap.set(key, flowsheet);
					});
					ctrl.triggerdFlowsheets = Array.from(flowsheetMap.values());
				}

				ctrl.getTriggeredFlowsheets = (flowsheets: FlowsheetModel[]): FlowsheetModel[] =>
				{
					const activeCodes: DxCodeModel[] = ctrl.activeDxRecords.map((dxRecord: DxRecordModel) => dxRecord.dxCode);
					return flowsheets.filter((flowsheet: FlowsheetModel) =>
					{
						for(let activeCode of activeCodes)
						{
							for (let triggerCode of flowsheet.triggerCodes)
							{
								if (triggerCode.codingSystem === activeCode.codingSystem && triggerCode.code === activeCode.code)
								{
									return true;
								}
							}
						}
						return false;
					})
				}

				ctrl.onFlowsheetSelect = (flowsheet): void =>
				{
					ctrl.selectedFlowsheet = flowsheet;

					const state = $state.includes("**.flowsheet") ? "." : ".flowsheet";
					$state.go(state,
						{
							demographicNo: ctrl.demographicNo,
							flowsheetId: flowsheet.id,
						});
				}

				ctrl.onManageFlowsheets = (): void =>
				{
					$state.go("record.configureFlowsheets",
						{
							demographicId: ctrl.demographicNo,
						});
				}
			}]
	});