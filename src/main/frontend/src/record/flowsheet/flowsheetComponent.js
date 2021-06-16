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

angular.module('Record.Flowsheet').component('flowsheet',
	{
		templateUrl: 'src/record/flowsheet/flowsheet.jsp',
		bindings: {
			componentStyle: "<?",
		},
		controller: [
			'$state',
			'$stateParams',
			'flowsheetApiService',
			function (
				$state,
				$stateParams,
				flowsheetApiService,
			)
			{
				const ctrl = this;

				ctrl.SecurityPermissions = SecurityPermissions;
				ctrl.LABEL_POSITION = LABEL_POSITION;
				ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
				ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

				ctrl.flowsheet = null;
				ctrl.demographicId = null;

				ctrl.filter = {
					textFilter: null, // free text filter items
					showHiddenItems: false, // show hidden/out of range/invalid items
					dataBeforeDate: null, // only show entries dated before this date
					dataAfterDate: null,  // only show entries dated after this date
					dataMaxEntries: null, // only show n most recent entries
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

				ctrl.$onInit = async () =>
				{
					ctrl.demographicId = $stateParams.demographicNo;
					ctrl.flowsheet = await flowsheetApiService.getDemographicFlowsheet(ctrl.demographicId, $stateParams.flowsheetId);
				}

				ctrl.toHealthTracker = () =>
				{
					$state.transitionTo('record.tracker',
						{
							demographicNo: $stateParams.demographicNo,
						},
						{
							notify: false
						});
				}

				ctrl.clearFilters = () =>
				{
					ctrl.filter.textFilter = null;
					ctrl.filter.showHiddenItems = false;
					ctrl.filter.dataBeforeDate = null;
					ctrl.filter.dataAfterDate = null;
					ctrl.filter.dataMaxEntries = null;
				};

				ctrl.showFlowsheetGroup = (group) =>
				{
					// filter visible items. show group if one or more items visible
					let visibleItems = group.flowsheetItems.filter((item) => ctrl.showFlowsheetItem(item));
					return (visibleItems.length > 0);
				}
				ctrl.showFlowsheetItem = (item) =>
				{
					if(!item)
					{
						return false;
					}

					if(!ctrl.filter.showHiddenItems && item.hidden)
					{
						return false;
					}
					else if (!Juno.Common.Util.isBlank(ctrl.filter.textFilter))
					{
						console.info(ctrl.filter.textFilter, item.name, item.typeCode, item.description);
						if ((!item.name || !item.name.toLowerCase().includes(ctrl.filter.textFilter.toLowerCase())) &&
							(!item.typeCode || !item.typeCode.toLowerCase().includes(ctrl.filter.textFilter.toLowerCase())) &&
							(!item.description || !item.description.toLowerCase().includes(ctrl.filter.textFilter.toLowerCase())))
						{
							return false;
						}
					}
					return true;
				}
			}]
	});