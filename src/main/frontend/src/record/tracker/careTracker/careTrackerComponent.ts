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

import {SecurityPermissions} from "../../../common/security/securityConstants";
import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN, LABEL_POSITION} from "../../../common/components/junoComponentConstants";
import CareTrackerItemGroupModel from "../../../lib/careTracker/model/CareTrackerItemGroupModel";
import CareTrackerItemModel from "../../../lib/careTracker/model/CareTrackerItemModel";
import CareTrackerItemDataModel from "../../../lib/careTracker/model/CareTrackerItemDataModel";
import CareTrackerModel from "../../../lib/careTracker/model/CareTrackerModel";

angular.module('Record.Tracker.CareTracker').component('careTracker',
	{
		templateUrl: 'src/record/tracker/careTracker/careTracker.jsp',
		bindings: {
			componentStyle: "<?",
			embeddedView: "<?",
		},
		controller: [
			'$scope',
			'$state',
			'$stateParams',
			'careTrackerApiService',
			function (
				$scope,
				$state,
				$stateParams,
				careTrackerApiService,
			)
			{
				const ctrl = this;
				ctrl.embeddedView = true;

				ctrl.SecurityPermissions = SecurityPermissions;
				ctrl.LABEL_POSITION = LABEL_POSITION;
				ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
				ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

				ctrl.careTracker = null as CareTrackerModel;
				ctrl.demographicId = null;

				ctrl.filter = {
					item: {
						textFilter: null, // free text filter items
						showHidden: false, // show hidden/out of range/invalid items
					},
					data: {
						beforeDate: null, // only show entries dated before this date
						afterDate: null,  // only show entries dated after this date
						maxEntries: null, // only show n most recent entries
					},
				};

				ctrl.filterOptions = {
					dataMaxOptions: [
						{ label: "Show All", value: null },
						{ label: "1", value: 1 },
						{ label: "2", value: 2 },
						{ label: "3", value: 3 },
						{ label: "4", value: 4 },
						{ label: "5", value: 5 },
					],
				};

				ctrl.$onInit = async (): Promise<void> =>
				{
					ctrl.demographicId = $stateParams.demographicNo;
					ctrl.careTracker = await careTrackerApiService.getDemographicCareTracker(ctrl.demographicId, $stateParams.careTrackerId);
				}

				ctrl.clearFilters = (): void =>
				{
					ctrl.filter.item.textFilter = null;
					ctrl.filter.item.showHidden = false;
					ctrl.filter.data.beforeDate = null;
					ctrl.filter.data.afterDate = null;
					ctrl.filter.data.maxEntries = null;
				};

				ctrl.showCareTrackerGroup = (group: CareTrackerItemGroupModel): boolean =>
				{
					// filter visible items. show group if one or more items visible
					let visibleItems = group.careTrackerItems.filter((item: CareTrackerItemModel) => ctrl.showCareTrackerItem(item));
					return (visibleItems.length > 0);
				}
				ctrl.showCareTrackerItem = (item: CareTrackerItemModel): boolean =>
				{
					if(item)
					{
						// only show hidden items if the showHidden filter is set
						if(item.hidden && !ctrl.filter.item.showHidden)
						{
							return false;
						}
						// only show the item if a keyword matches
						const keyword = ctrl.filter.item.textFilter;
						if(!Juno.Common.Util.isBlank(keyword))
						{
							return ctrl.textFilterMatch(item.name, keyword)
								|| ctrl.textFilterMatch(item.typeCode, keyword)
								|| ctrl.textFilterMatch(item.description, keyword)
						}
						return true;
					}
					return false;
				}
				ctrl.textFilterMatch = (toCheck: string, keyword: string): boolean =>
				{
					return toCheck && toCheck.toLowerCase().includes(keyword.toLowerCase());
				}

				ctrl.onPrint = (): void =>
				{
					window.print();
				}

				ctrl.onSaveAll = async (): Promise<void> =>
				{
					const expectedResponseCount: number = ctrl.careTracker.getItemCount();

					/*
					 We need to tell each item to submit it's pending data value.
					 When that is done, combine all the new items data into a single note string and add it to the existing open note
					 */
					await Promise.race([
						new Promise((resolve, reject) =>
						{
							const map = new Map();
							$scope.$broadcast("careTracker.savePendingData",
								(item: CareTrackerItemModel,
								 selected?: boolean,
								 newItemData?: CareTrackerItemDataModel): void =>
								{
									map.set(item.id, {item: item, data: newItemData, selected: Boolean(selected)});
									if (map.size >= expectedResponseCount)
									{
										ctrl.appendToCurrentNote(Array.from(map.values()));
										resolve();
									}
								});
						}),
						new Promise((resolve, reject) =>
						{
							window.setTimeout(() => reject(), 10000);
						})
					]);
				}

				ctrl.appendToCurrentNote = (itemDataPairs: object[]) =>
				{
					const message = itemDataPairs
						.filter((result: any) => result.data && result.selected)
						.map((result: any) => {
						const item = result.item as CareTrackerItemModel;
						const data = result.data as CareTrackerItemDataModel;
						return item.toString() + ": " + data.toString();
					}).join("\n");

					$scope.$emit("appendToCurrentNote", message);
				}
			}]
	});