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
import {ADMIN_PAGE_EVENTS} from "../../../adminConstants";

angular.module('Admin.Integration').component('iceFallAdminSettings',
{
	templateUrl: 'src/admin/integration/iceFall/settings/iceFallAdminSettings.jsp',
	bindings: {},
	controller: ['$scope', '$http', '$httpParamSerializer', function ($scope, $http, $httpParamSerializer)
	{
		let ctrl = this;
		let iceFallApi = new IceFallApi($http, $httpParamSerializer, '../ws/rs');

		ctrl.iceFallSettings = null;

		iceFallApi.getIceFallSettings().then(
				function success(result)
				{
					ctrl.iceFallSettings = result.data.body;
				},
				function error(result)
				{
					console.error("Failed to get ice fall status. With error: ");
				}
		);

		ctrl.setIceFallVisible = function (visible)
		{
			ctrl.iceFallSettings.visible = visible;
			ctrl.saveIceFallSettings();
		};

		ctrl.saveIceFallSettings = function ()
		{
			iceFallApi.setIceFallSettings(ctrl.iceFallSettings);
		};

	}]
});