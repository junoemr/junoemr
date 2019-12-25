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

angular.module('Admin.Integration').component('iceFallAdminActivity',
{
	templateUrl: 'src/admin/integration/iceFall/activity/iceFallAdminActivity.jsp',
	bindings: {},
	controller: ['$scope', '$http', '$httpParamSerializer', 'NgTableParams', function ($scope, $http, $httpParamSerializer, NgTableParams)
	{
		let ctrl = this;

		let iceFallApi = new IceFallApi($http, $httpParamSerializer, '../ws/rs');
		ctrl.filterStatuses = [{"value": "%", "label": "ANY"}, {"value": "ERROR", "label": "ERROR"},{"value": "SENT", "label": "SENT"}];

		ctrl.statusFilter = "%";
		ctrl.startDate = Juno.Common.Util.formatMomentDate(moment());
		ctrl.endDate = Juno.Common.Util.formatMomentDate(moment());
		ctrl.sortMode = "dateSent";
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
							dateSent: 'desc',
						},
						paginationMaxBlocks: 3,
						paginationMinBlocks: 2,
						dataset: ctrl.logEntries
					},
					{
						// called when sort order changes
						getData: function (params) {
							ctrl.sortMode = params.orderBy();

							return iceFallApi.getLogEntries({
								status: ctrl.statusFilter,
								startDate: ctrl.startDate +"T01:01:01",
								endDate: ctrl.endDate +"T23:59:59",
								page: params.page(),
								pageSize: params.count()
							}).then(
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
	}]
});