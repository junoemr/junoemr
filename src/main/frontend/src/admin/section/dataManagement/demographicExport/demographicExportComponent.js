import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN, JUNO_STYLE, LABEL_POSITION} from "../../../../common/components/junoComponentConstants";

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
angular.module('Admin.Section.DataManagement').component('demographicExport',
	{
		templateUrl: 'src/admin/section/dataManagement/demographicExport/demographicExport.jsp',
		bindings: {
			componentStyle: "<?",
		},
		controller: [
			'$scope',
			'$http',
			'$httpParamSerializer',
			'$q',
			'$uibModal',
			'demographicsService',
			function (
				$scope,
				$http,
				$httpParamSerializer,
				$q,
				$uibModal,
				demographicsService)
			{
				let ctrl = this;

				$scope.LABEL_POSITION = LABEL_POSITION;
				$scope.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;
				$scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;

				ctrl.exportTypeOptions = Object.freeze(
					[
						{
							label: 'CDS 5',
							value: 'CDS_5',
						},
					]
				);
				ctrl.demographicSetOptions = [];
				ctrl.exportToggleOptions = {
					exPersonalHistory: false,
					exFamilyHistory: false,
					exPastHealth: false,
					exProblemList: false,
					exRiskFactors: false,
					exAllergiesAndAdverseReactions: false,
					exMedicationsAndTreatments: false,
					exImmunizations: false,
					exLaboratoryResults: false,
					exAppointments: false,
					exClinicalNotes: false,
					exReportsReceived: false,
					exAlertsAndSpecialNeeds: false,
					exCareElements: false,
				}

				ctrl.selectedExportType = ctrl.exportTypeOptions[0].value;
				ctrl.selectedSet = null;

				ctrl.exportRunning = false;

				ctrl.$onInit = () =>
				{
					ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;

					ctrl.loadDemographicSets();
				}

				ctrl.loadDemographicSets = () =>
				{
					let deferred = $q.defer();
					demographicsService.getDemographicSetNames().then(
						function success(response)
						{
							let names = response.data;
							ctrl.demographicSetOptions = names.map(setName =>
							{
								return {
									label: setName,
									value: setName
								};
							});

							if(ctrl.demographicSetOptions.length > 0)
							{
								ctrl.selectedSet = ctrl.demographicSetOptions[0].value;
							}
							deferred.resolve(names);
						},
						function failure(response)
						{
							deferred.reject(response.data);
						}
					);
					return deferred.promise;
				}

				ctrl.onSelectAll = () =>
				{
					let toggleValue = false;
					// if there are any false values, we want to set all values to 'true'
					for(const value of Object.values(ctrl.exportToggleOptions))
					{
						if(value === false)
						{
							toggleValue = true;
							break;
						}
					}
					for(const key of Object.keys(ctrl.exportToggleOptions))
					{
						ctrl.exportToggleOptions[key] = toggleValue;
					}
				}

				ctrl.onExport = () =>
				{
					if (ctrl.selectedExportType && ctrl.selectedSet)
					{
						ctrl.exportRunning = true
						Juno.Common.Util.showProgressBar($uibModal,
							"Exporting Patient Set",
							ctrl.componentStyle,
							ctrl.fetchExportProgress,
							() =>
							{
								ctrl.exportRunning = false;
							}
						);

						let url = demographicsService.demographicExport(ctrl.selectedExportType, ctrl.selectedSet, ctrl.exportToggleOptions);
						let windowName = "export";
						window.open(url, windowName, "scrollbars=1,width=1024,height=768");
					}
				}

				ctrl.fetchExportProgress = async () =>
				{
					let pollingData = {};
					try
					{
						pollingData = (await demographicsService.demographicExportProgress()).data;
					}
					catch (e)
					{
						console.error("Polling Error", e);
					}
					return pollingData;
				}

				ctrl.canRunExport = () =>
				{
					return (!ctrl.exportRunning && ctrl.selectedSet);
				}
			}]
	});