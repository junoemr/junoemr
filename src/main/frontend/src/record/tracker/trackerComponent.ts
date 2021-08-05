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
import CareTrackerModel from "../../lib/careTracker/model/CareTrackerModel";
import DxRecordModel from "../../lib/dx/model/DxRecordModel";
import DxCodeModel from "../../lib/dx/model/DxCodeModel";

angular.module('Record.Tracker').component('healthTracker',
	{
		templateUrl: 'src/record/tracker/tracker.jsp',
		bindings: {
			componentStyle: "<?",
			user: "<",
			embeddedView: "<?",
		},
		controller: [
			'$state',
			'$stateParams',
			'$uibModal',
			'demographicApiService',
			'careTrackerApiService',
			function (
				$state,
				$stateParams,
				$uibModal,
				demographicApiService,
				careTrackerApiService)
			{
				const ctrl = this;
				ctrl.SecurityPermissions = SecurityPermissions;
				ctrl.LABEL_POSITION = LABEL_POSITION;
				ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
				ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

				ctrl.careTrackers = [] as CareTrackerModel[];
				ctrl.triggerdcareTrackers = [] as CareTrackerModel[];
				ctrl.selectedCareTracker = null as CareTrackerModel;
				ctrl.activeDxRecords = [];

				ctrl.accordianListItems = [
					{
						name: "Pinned care trackers",
						expanded: true,
						items: [], // will be the list of clinic careTrackers
					},
					{
						name: "Standard care trackers",
						expanded: false,
						items: [], // will be the list of clinic careTrackers
					},
					{
						name: "My care trackers",
						expanded: false,
						items: [], // will be the list of provider careTrackers
					},
					{
						name: "Patient care trackers",
						expanded: false,
						items: [], // will be the list of demographic careTrackers
					}
				];

				ctrl.$onInit = async (): Promise<void> =>
				{
					ctrl.embeddedView = ctrl.embeddedView || false;
					ctrl.demographicNo = $stateParams.demographicNo;
					ctrl.careTrackers = await careTrackerApiService.searchCareTrackers(true, true, true, ctrl.user.providerNo, true, ctrl.demographicNo, 1, 100);
					ctrl.activeDxRecords = await demographicApiService.getActiveDxRecords(ctrl.demographicNo);

					if($stateParams.careTrackerId)
					{
						ctrl.selectedCareTracker = ctrl.careTrackers.find((careTracker) => careTracker.id === Number($stateParams.careTrackerId));
					}

					ctrl.initCareTrackerLists(ctrl.careTrackers);
				}

				ctrl.initCareTrackerLists = (careTrackers: CareTrackerModel[]): void =>
				{
					const triggerdCareTrackers = ctrl.accordianListItems[0].items;
					const clinicCareTrackerItems = ctrl.accordianListItems[1].items;
					const providerCareTrackerItems = ctrl.accordianListItems[2].items;
					const demographicCareTrackerItems = ctrl.accordianListItems[3].items;

					// sort all careTrackers by level (clinic, provider, demographic)
					careTrackers.forEach((careTracker: CareTrackerModel) =>
					{
						if(careTracker.isDemographicLevel())
						{
							demographicCareTrackerItems.push(careTracker);
						}
						else if(careTracker.isProviderLevel())
						{
							providerCareTrackerItems.push(careTracker);
						}
						else
						{
							clinicCareTrackerItems.push(careTracker);
						}
					});

					// find triggered careTrackers, and ensure only the more specific one appears when related careTrackers are found
					// a careTracker is related if it has a parent ID
					const triggerMap = new Map();

					// put all base level careTrackers into a map
					ctrl.getTriggeredCareTrackers(clinicCareTrackerItems).forEach((careTracker: CareTrackerModel) => {
						triggerMap.set(careTracker.id, careTracker);
					});

					// overwrite mapped values with provider specific version where possible
					ctrl.getTriggeredCareTrackers(providerCareTrackerItems).forEach((careTracker: CareTrackerModel) => {
						const key = careTracker.parentCareTrackerId ? careTracker.parentCareTrackerId : careTracker.id;
						triggerMap.set(key, careTracker);
					});

					// overwrite mapped values again with demographic specific version where possible
					ctrl.getTriggeredCareTrackers(demographicCareTrackerItems).forEach((careTracker: CareTrackerModel) => {
						const key = careTracker.parentCareTrackerId ? careTracker.parentCareTrackerId : careTracker.id;
						triggerMap.set(key, careTracker);
					});
					ctrl.accordianListItems[0].items = Array.from(triggerMap.values());
					ctrl.triggerdCareTrackers = Array.from(triggerMap.values());
				}

				ctrl.getTriggeredCareTrackers = (careTrackers: CareTrackerModel[]): CareTrackerModel[] =>
				{
					const activeCodes: DxCodeModel[] = ctrl.activeDxRecords.map((dxRecord: DxRecordModel) => dxRecord.dxCode);
					return careTrackers.filter((careTracker: CareTrackerModel) =>
					{
						for(let activeCode of activeCodes)
						{
							for (let triggerCode of careTracker.triggerCodes)
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

				ctrl.onCareTrackerSelect = (careTracker): void =>
				{
					ctrl.selectedCareTracker = careTracker;

					const state = $state.includes("**.careTracker") ? "." : $state.includes("**.measurements") ? "^.careTracker" : ".careTracker";
					$state.go(state,
						{
							demographicNo: ctrl.demographicNo,
							careTrackerId: careTracker.id,
						});
				}

				ctrl.onViewAllPatientMeasurements = (): void =>
				{
					ctrl.selectedCareTracker = null;
					const state = $state.includes("**.careTracker") ? "^.measurements" : ".measurements";
					$state.go(state,
						{
							demographicNo: ctrl.demographicNo,
						});
				}

				ctrl.onManageCareTrackers = (): void =>
				{
					$state.go("record.configureHealthTracker",
						{
							demographicNo: ctrl.demographicNo,
						});
				}
			}]
	});