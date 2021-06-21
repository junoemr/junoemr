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

import {IceFallApi} from "../../../../../generated";

angular.module('Admin.Section').component('iceFallAdminActivity',
{
	templateUrl: 'src/admin/section/iceFall/activity/iceFallAdminActivity.jsp',
	bindings: {},
	controller: ['$scope', '$http', '$httpParamSerializer', 'NgTableParams', 'formService', function ($scope, $http, $httpParamSerializer, NgTableParams, formService)
	{
		let ctrl = this;

		let iceFallApi = new IceFallApi($http, $httpParamSerializer, '../ws/rs');
		ctrl.filterStatuses = [{"value": "%", "label": "ANY"}, {"value": "ERROR", "label": "ERROR"},{"value": "SENT", "label": "SENT"}];

		ctrl.statusFilter = "%";
		ctrl.startDate = Juno.Common.Util.formatMomentDate(moment());
		ctrl.endDate = Juno.Common.Util.formatMomentDate(moment());
		ctrl.logEntries = [];

		// search for log entries
		ctrl.search = function (page, pageSize)
		{

		};

		ctrl.$onInit = function()
		{
			ctrl.tableParams = new NgTableParams(
					{
						page: 1,
						count: 100,
						sorting:
						{
							DATE_SENT: 'desc',
						},
						paginationMaxBlocks: 3,
						paginationMinBlocks: 2,
						dataset: ctrl.logEntries
					},
					{
						getData: function (params) {

							if (!ctrl.startDate || !ctrl.endDate)
							{//start date and end date are required
								params.total(0);
								return [];
							}

							return iceFallApi.getLogEntries(
									ctrl.startDate +"T01:01:01",
									ctrl.endDate +"T23:59:59",
									params.page(),
									params.count(),
									ctrl.statusFilter,
									ctrl.orderByToApiName(params.orderBy()),
									ctrl.orderByGetDirection(params.orderBy())
									).then(
										function success(result)
										{
											ctrl.logEntries = result.data.body.logEntries;
											params.total(result.data.body.totalLogEntries);
											return ctrl.logEntries;
										},
										function error(result)
										{
											console.error("Failed to retrieve IceFall log entries! error: " + result);
										}
									)
						}
					}
			);
		};

		ctrl.doEformPopup = function (log)
		{
			if (log.instance)
			{
				formService.openEFormInstancePopup(log.demographicNo, log.fdid);
			}
			else
			{
				formService.openEFormPopup(log.demographicNo, log.fdid);
			}
		};

		ctrl.orderByToApiName = function(orderBy)
		{
			return orderBy[0].replace("-", "").replace("+", "");
		};

		ctrl.orderByGetDirection = function (orderBy)
		{
			if (orderBy[0].includes("-"))
			{
				return "DESC";
			}
			else
			{
				return "ASC";
			}
		};
	}]
});