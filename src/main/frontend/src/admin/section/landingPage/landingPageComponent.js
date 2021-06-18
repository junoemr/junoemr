/*
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

import {ADMIN_PAGE_EVENTS} from "../../adminConstants";

angular.module('Admin.Section').component('landingPage', {
	templateUrl: 'src/admin/section/landingPage/landingPage.jsp',
	bindings: {
	},
	controller: ['$scope', '$location', '$window', function ($scope, $location, $window)
	{
		let ctrl = this;
		ctrl.goTo = function (path, expandGroup)
		{
			$location.url(path);
			$scope.$emit(ADMIN_PAGE_EVENTS.ADMIN_EXPAND_NAV_GROUP, expandGroup);
		};

		ctrl.goToFrame = function (frameUrl, expandGroup)
		{
			let context = $window.adminLandingPage.oscarContextPath;
			$location.url('admin/frame?frameUrl=' + context + frameUrl);
			$scope.$emit(ADMIN_PAGE_EVENTS.ADMIN_EXPAND_NAV_GROUP, expandGroup);
		}
	}]
});