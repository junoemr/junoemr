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

import {ScheduleApi, SitesApi} from "../../../generated";

angular.module('Settings').component('scheduleSettings',
	{
		templateUrl: 'src/settings/schedule/scheduleSettings.jsp',
		bindings: {
			pref: "=",
			encounterForms: "<",
			eforms: "<",
		},
		controller: [
			'$http',
			'$httpParamSerializer',
			'$uibModal',
			'$stateParams',
			function(
				$http,
				$httpParamSerializer,
				$uibModal,
				$stateParams,
			)
			{
				const ctrl = this;

				ctrl.sitesApi = new SitesApi($http, $httpParamSerializer, '../ws/rs');
				ctrl.scheduleApi = new ScheduleApi($http, $httpParamSerializer, '../ws/rs');
				ctrl.siteOptions = [];
				ctrl.scheduleOptions = [];

				ctrl.appointmentReasonOptions = [
					{
						value: "DEFAULT_ALL",
						label: "Show category and reason",
					},
					{
						value: "REASON_ONLY",
						label: "Show reason only",
					},
					{
						value: "NONE",
						label: "Off ",
					}];

				ctrl.$onInit = (): void =>
				{
					ctrl.pref = ctrl.pref || $stateParams.pref;
					ctrl.encounterForms = ctrl.encounterForms.content || [];
					ctrl.eforms = ctrl.eforms || [];

					ctrl.sitesApi.getSiteList().then(
						function success(rawResults)
						{
							var results = rawResults.data.body;
							var out = [];
							if (angular.isArray(results))
							{
								for (var i = 0; i < results.length; i++)
								{
									out.push({
										id: results[i].siteId,
										value: results[i].name,
										label: results[i].name,
										color: results[i].bgColor,
									});
								}
							}
							ctrl.siteOptions = out;
						}
					);
					ctrl.scheduleApi.getScheduleGroups().then(
						function success(rawResults)
						{
							var results = rawResults.data.body;
							for (var i = 0; i < results.length; i++)
							{
								var scheduleData = results[i];

								results[i].label = results[i].name;
								results[i].value = results[i].identifier;

								ctrl.scheduleOptions.push(scheduleData);
							}
						});


					//this needs to be done to do the weird checkbox lists. basically add a property to each encounterList object called checked:[true|false]
					ctrl.pref.appointmentScreenForms = ctrl.pref.appointmentScreenForms || [];
					for (let i = 0; i < ctrl.pref.appointmentScreenForms.length; i++)
					{
						const selected = ctrl.encounterForms.filter((form) => form.formName === ctrl.pref.appointmentScreenForms[i]);
						if (selected != null)
						{
							for (let x = 0; x < selected.length; x++)
							{
								if (selected[x].formName === ctrl.pref.appointmentScreenForms[i])
								{
									selected[x].checked = true;
								}
							}
						}
					}

					//this needs to be done to do the weird checkbox lists. basically add a property to each encounterList object called checked:[true|false]
					ctrl.pref.appointmentScreenEforms = ctrl.pref.appointmentScreenEforms || [];
					for (let i = 0; i < ctrl.pref.appointmentScreenEforms.length; i++)
					{
						const selected = ctrl.eforms.filter((eform) => eform.id === ctrl.pref.appointmentScreenEforms[i]);
						if (selected != null)
						{
							for (let x = 0; x < selected.length; x++)
							{
								if (selected[x].id === ctrl.pref.appointmentScreenEforms[i])
								{
									selected[x].checked = true;
								}
							}
						}
					}
				}

				ctrl.selectEncounterForms = (): void =>
				{
					const selected = ctrl.encounterForms.filter((form) => form.checked);
					const tmp = [];
					for (let i = 0; i < selected.length; i++)
					{
						tmp.push(selected[i].formName);
					}
					ctrl.pref.appointmentScreenForms = tmp;
				};

				ctrl.selectEForms = (): void =>
				{
					const selected = ctrl.eforms.filter((eform) => eform.checked);
					const tmp = [];
					for (let i = 0; i < selected.length; i++)
					{
						tmp.push(selected[i].id);
					}
					ctrl.pref.appointmentScreenEforms = tmp;
				};

				ctrl.removeQuickLinks = (): void =>
				{
					var newList = [];

					for (var i = 0; i < ctrl.pref.appointmentScreenQuickLinks.length; i++)
					{
						if (ctrl.pref.appointmentScreenQuickLinks[i].checked == null || ctrl.pref.appointmentScreenQuickLinks[i].checked == false)
						{
							newList.push(ctrl.pref.appointmentScreenQuickLinks[i]);
						}
					}
					ctrl.pref.appointmentScreenQuickLinks = newList;
				};

				ctrl.openQuickLinkModal = (): void =>
				{
					var modalInstance = $uibModal.open(
						{
							templateUrl: 'src/settings/quickLink.jsp',
							controller: 'QuickLinkController'
						});

					modalInstance.result.then(function(selectedItem)
					{
						if (selectedItem != null)
						{
							if (selectedItem != null && selectedItem.name != null && selectedItem.url != null)
							{
								ctrl.pref.appointmentScreenQuickLinks.push(selectedItem);
							}
						}
					});
				};
			}]
	});