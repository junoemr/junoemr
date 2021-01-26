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
angular.module('Admin.Section.DataManagement').component('demographicImport',
	{
		templateUrl: 'src/admin/section/dataManagement/demographicImport/demographicImport.jsp',
		bindings: {
			componentStyle: "<?",
		},
		controller: [
			'$scope',
			'demographicsService',
			function ($scope, demographicsService)
			{
				let ctrl = this;

				$scope.LABEL_POSITION = LABEL_POSITION;
				$scope.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;
				$scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;

				ctrl.importTypeOptions = Object.freeze(
					[
						{
							label: 'CDS 5.0',
							value: 'CDS_5',
						},
					]
				);
				ctrl.mergeOptions = Object.freeze(
					[
						{
							label: 'Skip duplicates',
							value: 'SKIP',
						},
						{
							label: 'Merge duplicates',
							value: 'MERGE',
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


				ctrl.selectedImportSource = ctrl.importSourceOptions[0].value;
				ctrl.selectedMergeStrategy = ctrl.mergeOptions[0].value;
				ctrl.selectedImportType = ctrl.importTypeOptions[0].value;
				ctrl.selectedFiles = [];

				ctrl.importRunning = false;

				ctrl.$onInit = function inInit()
				{
					ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT
				}

				ctrl.onRunImport = async function onRunImport()
				{
					ctrl.importRunning = true;

					let formattedFileList = await ctrl.formatSelectedFiles();
					console.info("formattedFileList", formattedFileList);

					demographicsService.demographicImport(
						ctrl.selectedImportType,
						ctrl.selectedImportSource,
						ctrl.selectedMergeStrategy,
						formattedFileList).then((response) =>
						{
							console.info("files uploaded success");
						}
					).finally(() =>
					{
						ctrl.importRunning = false;
					});
				}

				ctrl.formatSelectedFiles = async function formatSelectedFiles()
				{
					const encodedFiles = await Promise.all(Array.from(this.selectedFiles).map( async (file) =>
					{
						return {
							name: file.name,
							type: file.type,
							size: file.size,
							data: await this.toBase64(file),
						};
					}));

					return encodedFiles;
				}

				ctrl.toBase64 = function toBase64(file)
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

				ctrl.onFileSelected = (files) =>
				{
					ctrl.selectedFiles = files;
					$scope.$apply();
				}
				ctrl.canRunImport = () =>
				{
					return ctrl.selectedFiles && ctrl.selectedFiles.length > 0;
				}

			}]
	});