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
		},
		controller: [
			'$state',
			'$stateParams',
			'demographicApiService',
			'flowsheetApiService',
			function (
				$state,
				$stateParams,
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
						name: "All Flowsheets",
						expanded: false,
						items: [], // will be the list of flowsheets
					}
				];

				ctrl.$onInit = async () =>
				{
					ctrl.demographicNo = $stateParams.demographicNo;
					ctrl.flowsheets = await flowsheetApiService.getAllFlowsheets();
					ctrl.activeDxRecords = await demographicApiService.getActiveDxRecords(ctrl.demographicNo);
					ctrl.accordianListItems[0].items = ctrl.flowsheets;

					if($stateParams.flowsheetId)
					{
						ctrl.selectedFlowsheet = ctrl.flowsheets.find((flowsheet) => flowsheet.id === Number($stateParams.flowsheetId));
					}

					ctrl.findTriggeredFlowsheets();
				}

				ctrl.findTriggeredFlowsheets = (): void =>
				{
					const activeCodes: DxCodeModel[] = ctrl.activeDxRecords.map((dxRecord: DxRecordModel) => dxRecord.dxCode);
					ctrl.triggerdFlowsheets = ctrl.flowsheets.filter((flowsheet: FlowsheetModel) =>
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
					});
				}

				ctrl.onFlowsheetSelect = (flowsheet) =>
				{
					ctrl.selectedFlowsheet = flowsheet;
					$state.transitionTo('record.tracker.flowsheet',
						{
							demographicNo: ctrl.demographicNo,
							flowsheetId: flowsheet.id,
						},
						{
							notify: false
						});
				}
			}]
	});