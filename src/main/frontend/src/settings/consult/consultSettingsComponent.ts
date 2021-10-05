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

angular.module('Settings').component('consultSettings',
	{
		templateUrl: 'src/settings/consult/consultSettings.jsp',
		bindings: {
			pref: "=",
			teams: "<",
		},
		controller: [
			'$stateParams',
			function(
				$stateParams,
			)
			{
				const ctrl = this;

				ctrl.teamOptions = [
					{
						"value": "-1",
						"label": "All"
					}];
				ctrl.pasteFormats = [
					{
						value: 'single',
						label: 'Single Line'
					},
					{
						value: 'multi',
						label: 'Multi Line'
					}];
				ctrl.letterHeadNameDefaults = [
					{
						value: '1',
						label: 'Provider (user)'
					},
					{
						value: '2',
						label: 'MRP'
					},
					{
						value: '3',
						label: 'Clinic'
					}];

				ctrl.$onInit = (): void =>
				{
					ctrl.pref = ctrl.pref || $stateParams.pref;
					ctrl.teams = ctrl.teams || [];

					//convert to value/label obj list. Add all/none

					for (let i = 0; i < ctrl.teams .length; i++)
					{
						ctrl.teamOptions.push(
							{
								"value": ctrl.teams [i],
								"label": ctrl.teams [i]
							});
					}
					ctrl.teamOptions.push(
						{
							"value": "",
							"label": "None"
						});
				}
			}]
	});