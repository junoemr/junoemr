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

angular.module('Record.Tracker.CareTracker').component('careTracker',
	{
		templateUrl: 'src/record/tracker/careTracker/careTracker.jsp',
		bindings: {
			componentStyle: "<?",
		},
		controller: [
			'$state',
			'$stateParams',
			'careTrackerApiService',
			function (
				$state,
				$stateParams,
				careTrackerApiService,
			)
			{
				const ctrl = this;

				ctrl.SecurityPermissions = SecurityPermissions;
				ctrl.LABEL_POSITION = LABEL_POSITION;
				ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
				ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

				ctrl.careTracker = null;
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
					if(!item || (!ctrl.filter.item.showHidden && item.hidden))
					{
						return false;
					}
					return !(!Juno.Common.Util.isBlank(ctrl.filter.item.textFilter) &&
						(!item.name || !item.name.toLowerCase().includes(ctrl.filter.item.textFilter.toLowerCase())) &&
						(!item.typeCode || !item.typeCode.toLowerCase().includes(ctrl.filter.item.textFilter.toLowerCase())) &&
						(!item.description || !item.description.toLowerCase().includes(ctrl.filter.item.textFilter.toLowerCase())));
				}

				ctrl.onPrint = (): void =>
				{
					window.print();
				}
			}]
	});