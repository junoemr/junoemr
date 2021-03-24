import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN, JUNO_STYLE, LABEL_POSITION} from "../../../../common/components/junoComponentConstants";
import {DemographicsApi} from "../../../../../generated";
import FileSaver from "file-saver";

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
			'$interval',
			function (
				$scope,
				$http,
				$httpParamSerializer,
				$q,
				$uibModal,
				$interval)
			{
				let ctrl = this;

				$scope.LABEL_POSITION = LABEL_POSITION;
				$scope.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;
				$scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;

				ctrl.demographicsApi = new DemographicsApi($http, $httpParamSerializer, '../ws/rs');

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
					exportPersonalHistory: false,
					exportFamilyHistory: false,
					exportPastHealth: false,
					exportProblemList: false,
					exportRiskFactors: false,
					exportAllergiesAndAdverseReactions: false,
					exportMedicationsAndTreatments: false,
					exportImmunizations: false,
					exportLaboratoryResults: false,
					exportAppointments: false,
					exportClinicalNotes: false,
					exportReportsReceived: false,
					exportAlertsAndSpecialNeeds: false,
					exportCareElements: false,
				}

				ctrl.selectedExportType = ctrl.exportTypeOptions[0].value;
				ctrl.selectedSet = null;

				ctrl.exportRunning = false;
				ctrl.pollingPromise = null;

				ctrl.$onInit = () =>
				{
					ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;

					ctrl.loadDemographicSets();
				}
				ctrl.$onDestroy = () =>
				{
					if(ctrl.pollingPromise)
					{
						$interval.cancel(ctrl.pollingPromise);
					}
				}

				ctrl.loadDemographicSets = () =>
				{
					let deferred = $q.defer();
					ctrl.demographicsApi.getDemographicSetNames().then(
						function success(response)
						{
							let names = response.data.body;
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

						let downloadPromiseDefer = $q.defer();

						ctrl.demographicsApi.demographicExport(
							ctrl.selectedExportType,
							ctrl.selectedSet,
							ctrl.exportToggleOptions
						).then((result) =>
						{
							const processId = result.data.body;
							let complete = false;
							ctrl.pollingPromise = $interval(async () =>
							{
								if(!complete)
								{
									try
									{
										let pollingData = (await ctrl.demographicsApi.demographicExportProgress(processId)).data.body;
										complete = pollingData.complete;

										if (complete)
										{
											ctrl.demographicsApi.demographicExportResults(
												processId,
												{responseType: "blob"}
											).then((response) =>
											{
												downloadPromiseDefer.resolve(response);
											}).catch((error) =>
											{
												downloadPromiseDefer.reject(error);
											});
										}
										else
										{
											downloadPromiseDefer.notify(pollingData);
										}
									}
									catch (error)
									{
										downloadPromiseDefer.reject(error);
									}
								}
							}, 500, 0, true);
						}).catch((error) =>
						{
							downloadPromiseDefer.reject(error);
						});

						let loadingPromise = Juno.Common.Util.showProgressBar(
							$uibModal,
							$q,
							downloadPromiseDefer,
							"Exporting Patient Set",
							ctrl.componentStyle,
						);

						loadingPromise.then((result) =>
						{
							FileSaver.saveAs(new Blob([result.data], {type: result.data.type}), ctrl.selectedSet + ".zip");
						}).catch(() =>
							{
								Juno.Common.Util.errorAlert($uibModal, "Error", "Internal Server Error. Export Not Completed");
							}
						).finally(() =>
							{
								$interval.cancel(ctrl.pollingPromise);
								ctrl.exportRunning = false;
							}
						);
					}
				}

				ctrl.canRunExport = () =>
				{
					return (!ctrl.exportRunning && ctrl.selectedSet);
				}
			}]
	});