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

angular.module('Settings').component('summarySettings',
	{
		templateUrl: 'src/settings/summary/summarySettings.jsp',
		bindings: {
			pref: "=",
		},
		controller: [
			'$stateParams',
			function(
				$stateParams,
			)
			{
				const ctrl = this;

				ctrl.staleDates = [
					{
						value: 'A',
						label: 'All'
					},
					{
						value: '0',
						label: '0'
					},
					{
						value: '-1',
						label: '-1'
					},
					{
						value: '-2',
						label: '2'
					},
					{
						value: '-3',
						label: '3'
					},
					{
						value: '-4',
						label: '4'
					},
					{
						value: '-5',
						label: '5'
					},
					{
						value: '-6',
						label: '6'
					},
					{
						value: '-7',
						label: '7'
					},
					{
						value: '-8',
						label: '8'
					},
					{
						value: '-9',
						label: '9'
					},
					{
						value: '-10',
						label: '10'
					},
					{
						value: '-11',
						label: '11'
					},
					{
						value: '-12',
						label: '12'
					},
					{
						value: '-13',
						label: '13'
					},
					{
						value: '-14',
						label: '14'
					},
					{
						value: '-15',
						label: '15'
					},
					{
						value: '-16',
						label: '16'
					},
					{
						value: '-17',
						label: '17'
					},
					{
						value: '-18',
						label: '18'
					},
					{
						value: '-19',
						label: '19'
					},
					{
						value: '-20',
						label: '20'
					},
					{
						value: '-21',
						label: '21'
					},
					{
						value: '-22',
						label: '22'
					},
					{
						value: '-23',
						label: '23'
					},
					{
						value: '-24',
						label: '24'
					},
					{
						value: '-25',
						label: '25'
					},
					{
						value: '-26',
						label: '26'
					},
					{
						value: '-27',
						label: '27'
					},
					{
						value: '-28',
						label: '28'
					},
					{
						value: '-29',
						label: '29'
					},
					{
						value: '-30',
						label: '30'
					},
					{
						value: '-31',
						label: '31'
					},
					{
						value: '-32',
						label: '32'
					},
					{
						value: '-33',
						label: '33'
					},
					{
						value: '-34',
						label: '34'
					},
					{
						value: '-35',
						label: '35'
					},
					{
						value: '-36',
						label: '36'
					},
				];

				ctrl.$onInit = (): void =>
				{
					ctrl.pref = ctrl.pref || $stateParams.pref;
				}

				ctrl.showProviderColourPopup = (): void =>
				{
					window.open('../provider/providerColourPicker.jsp', 'provider_colour', 'width=700,height=450');
				};

				ctrl.showDefaultEncounterWindowSizePopup = (): void =>
				{
					window.open('../setProviderStaleDate.do?method=viewEncounterWindowSize', 'encounter_window_sz', 'width=700,height=450');
				};

				ctrl.openConfigureEChartCppPopup = (): void =>
				{
					window.open('../provider/CppPreferences.do', 'configure_echart_cpp', 'width=700,height=450');
				};
			}]
	});