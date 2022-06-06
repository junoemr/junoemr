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

import {LABEL_POSITION} from "../../common/components/junoComponentConstants";

angular.module('Settings').component('integrationSettings',
	{
		templateUrl: 'src/settings/integration/integrationSettings.jsp',
		bindings: {
			pref: "=",
		},
		controller: [
			'$stateParams',
			'appService',
			function(
				$stateParams,
				appService,
			)
			{
				const ctrl = this;
				ctrl.LABEL_POSITION = LABEL_POSITION;

				ctrl.olisLabs = [
					{
						value: '',
						label: ''
					},
					{
						value: '5552',
						label: 'Gamma-Dynacare'
					},
					{
						value: '5407',
						label: 'CML'
					},
					{
						value: '5687',
						label: 'LifeLabs'
					}];

				ctrl.loadedApps = [];

				ctrl.$onInit = (): void =>
				{
					ctrl.pref = ctrl.pref || $stateParams.pref;
					ctrl.refreshAppList();
				}

				ctrl.authenticate = (app): void =>
				{
					window.open('../apps/oauth1.jsp?id=' + app.id, 'appAuth', 'width=700,height=450');
				};

				ctrl.openManageAPIClientPopup = (): void =>
				{
					window.open('../provider/clients.jsp', 'api_clients', 'width=700,height=450');
				};

				ctrl.openMyOscarUsernamePopup = (): void =>
				{
					window.open('../provider/providerIndivoIdSetter.jsp', 'invivo_setter', 'width=700,height=450');
				};

				ctrl.refreshAppList = (): void =>
				{
					appService.getApps().then(function(data)
						{
							ctrl.loadedApps = data;
						},
						function(errorMessage)
						{
							console.log("applist:" + errorMessage);
						}
					);
				};
			}]
	});