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
import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN, JUNO_STYLE, LABEL_POSITION} from "../../../../common/components/junoComponentConstants";
import {DemographicsApi, SitesApi} from "../../../../../generated";
import FileSaver from "file-saver";

angular.module('Admin.Section.DataManagement').component('demographicImport',
	{
		templateUrl: 'src/admin/section/dataManagement/demographicImport/demographicImport.jsp',
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

				ctrl.sitesApi = new SitesApi($http, $httpParamSerializer, '../ws/rs');
				ctrl.demographicsApi = new DemographicsApi($http, $httpParamSerializer, '../ws/rs');

				$scope.LABEL_POSITION = LABEL_POSITION;
				$scope.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;
				$scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
				ctrl.maxFileSize = 512; //MB

				ctrl.importTypeOptions = Object.freeze(
					[
						{
							label: 'CDS 5',
							value: 'CDS_5',
						},
					]
				);
				ctrl.mergeOptions = Object.freeze(
					[
						{
							label: 'Skip duplicates',
							value: 'SKIP',
							description: "Duplicates will not be imported",
						},
						{
							label: 'Merge duplicates',
							value: 'MERGE',
							description: "Duplicates will be merged with their existing file. The demographic master file will not be updated, " +
								"and imported chart notes, documents etc. will be added to the existing record. This may cause chart elements to appear duplicated.",
						},
					]
				);

				ctrl.importSourceOptions = Object.freeze(
					[
						{
							label: 'default',
							value: 'UNKNOWN',
						},
						{
							label: 'Juno',
							value: 'JUNO',
						},
						{
							label: 'Wolf',
							value: 'WOLF',
						},
						{
							label: 'Mediplan',
							value: 'MEDIPLAN',
						},
						{
							label: 'Medaccess',
							value: 'MEDACCESS',
						},
						{
							label: 'Accuro',
							value: 'ACCURO',
						},
					]
				);
				ctrl.siteOptions = [];

				ctrl.selectedImportSource = ctrl.importSourceOptions[0].value;
				ctrl.selectedMergeStrategy = ctrl.mergeOptions[0].value;
				ctrl.selectedImportType = ctrl.importTypeOptions[0].value;
				ctrl.selectedSite = null;
				ctrl.selectedFiles = [];

				ctrl.results = null;
				ctrl.importRunning = false;
				ctrl.sitesEnabled = false;
				ctrl.pollingPromise = null;

				ctrl.$onInit = () =>
				{
					ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;

					// set up multisite selection if needed
					ctrl.loadSitesEnabled().then((enabled) =>
					{
						if (enabled)
						{
							ctrl.loadSites().then((siteOptions) =>
							{
								ctrl.siteOptions = siteOptions;
								ctrl.selectedSite = siteOptions[0].value;
							});
						}
					});
				}
				ctrl.$onDestroy = () =>
				{
					if(ctrl.pollingPromise)
					{
						$interval.cancel(ctrl.pollingPromise);
					}
				}

				ctrl.onRunImport = async () =>
				{
					if(!ctrl.canRunImport())
					{
						return;
					}

					ctrl.importRunning = true;
					let formattedFileList = await ctrl.formatSelectedFiles();

					let importPromiseDefer = $q.defer();

					ctrl.demographicsApi.demographicImport(
						ctrl.selectedImportType,
						ctrl.selectedImportSource,
						ctrl.selectedMergeStrategy,
						ctrl.selectedSite,
						formattedFileList
					).then((result) =>
					{
						const processId = result.data.body;
						let complete = false;
						ctrl.pollingPromise = $interval(async () =>
						{
							if(!complete)
							{
								let pollingData;
								try
								{
									pollingData = (await ctrl.demographicsApi.demographicImportProgress(processId)).data.body;
									complete = pollingData.complete;

									if (complete)
									{
										ctrl.demographicsApi.demographicImportResults(processId
										).then((response) =>
										{
											importPromiseDefer.resolve(response);
										}).catch((error) =>
										{
											importPromiseDefer.reject(error);
										});
									}
									else
									{
										importPromiseDefer.notify(pollingData);
									}
								}
								catch (error)
								{
									importPromiseDefer.reject(error);
								}
							}
						}, 500, 0, true);
					}).catch((error) =>
					{
						importPromiseDefer.reject(error);
					});

					let loadingPromise = Juno.Common.Util.showProgressBar(
						$uibModal,
						$q,
						importPromiseDefer,
						"Importing Patient Files",
						ctrl.componentStyle,
					);

					loadingPromise.then((response) =>
						{
							ctrl.results = response.data.body;
						}
					).catch(() =>
						{
							Juno.Common.Util.errorAlert($uibModal, "Error", "Internal Server Error. Import Not Completed");
						}
					).finally(() =>
						{
							$interval.cancel(ctrl.pollingPromise);
							ctrl.importRunning = false;
						}
					);
				}

				ctrl.onDownloadLogFiles = async () =>
				{
					if(ctrl.results && ctrl.results.logFileNames && ctrl.results.logFileNames.length > 0 && ctrl.results.processId)
					{
						const doc = await ctrl.demographicsApi.downloadImportLogs(
							ctrl.results.processId,
							ctrl.results.logFileNames,
							{responseType: "blob"});
						FileSaver.saveAs(new Blob([doc.data], {type: doc.data.type}), "importLogs.zip");
					}
				}

				ctrl.formatSelectedFiles = async () =>
				{
					return await Promise.all(Array.from(ctrl.selectedFiles).map( async (file) =>
					{
						return {
							name: file.name,
							type: file.type,
							size: file.size,
							data: await this.toBase64(file),
						};
					}));
				}

				ctrl.toBase64 = (file) =>
				{
					return new Promise((resolve, reject) =>
					{
						const reader = new FileReader();
						reader.readAsDataURL(file);

						reader.onload = () =>
						{
							// only get the base 64 data, omit the meta info
							const base64result = reader.result;
							const base64resultData = base64result.split(",")[1];
							resolve(base64resultData);
						};
						reader.onerror = (error) => reject(error);
					});
				}

				ctrl.loadSitesEnabled = () =>
				{
					let deferred = $q.defer();

					ctrl.sitesApi.getSitesEnabled().then(
						function success(rawResults)
						{
							let enabled = rawResults.data.body;
							ctrl.sitesEnabled = enabled;
							deferred.resolve(enabled);
						},
						function failure(results)
						{
							deferred.reject(results.data.body);
						}
					);

					return deferred.promise;
				};
				ctrl.loadSites = () =>
				{
					let deferred = $q.defer();

					ctrl.sitesApi.getSiteList().then(
						function success(rawResults)
						{
							let results = rawResults.data.body;
							let out = [];
							if (angular.isArray(results))
							{
								for (let i = 0; i < results.length; i++)
								{
									out.push({
										uuid: results[i].siteId,
										value: results[i].name,
										label: results[i].name,
										color: results[i].bgColor,
									});
								}
							}
							deferred.resolve(out);
						},
						function failure(results)
						{
							deferred.reject(results);
						}
					);

					return deferred.promise;
				};

				ctrl.onFileSelected = (files) =>
				{
					ctrl.selectedFiles = files;
					$scope.$apply();
				}

				ctrl.getSelectedMergeDescription = () =>
				{
					return ctrl.mergeOptions.find(option => option.value === ctrl.selectedMergeStrategy).description;
				}

				ctrl.canRunImport = () =>
				{
					return (!ctrl.importRunning && Boolean(ctrl.selectedFiles) && ctrl.selectedFiles.length > 0);
				}

				ctrl.canDownloadLogs = () =>
				{
					return (!ctrl.importRunning && ctrl.results && ctrl.results.logFileNames && ctrl.results.logFileNames.length > 0);
				}
			}]
	});