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

import LoadingQueue from "../../../../../lib/util/LoadingQueue";

angular.module('Admin.Section.Lab.Olis').component('olisConfig',
	{
		templateUrl: 'src/admin/section/lab/config/olis/olisConfig.jsp',
		bindings: {

		},
		controller: [
			'systemPreferenceService',
			function (systemPreferenceService)
		{
			const ctrl = this;
			ctrl.loadingQueue = new LoadingQueue();
			ctrl.pollingPropName = "olis_polling_enabled";

			ctrl.$onInit = async () =>
			{
				ctrl.loadingQueue.pushLoadingState();
				ctrl.pollingEnabled = await systemPreferenceService.getPreference(ctrl.pollingPropName, false);
				ctrl.loadingQueue.popLoadingState();
			}

			ctrl.setPollingEnabled = (value: boolean) =>
			{
				systemPreferenceService.setPreference(ctrl.pollingPropName, value.toString());
			}

			ctrl.labSearch = () =>
			{
				const target = "_blank";
				const url = "../olis/Search.jsp";
				window.open(url, target, "scrollbars=yes, location=no, width=1024, height=800");
			};
		}],
	}
);