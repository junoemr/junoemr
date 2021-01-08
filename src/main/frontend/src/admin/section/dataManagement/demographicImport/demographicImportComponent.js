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
			function ($scope)
			{
				let ctrl = this;

				$scope.LABEL_POSITION = LABEL_POSITION;
				$scope.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;
				$scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;


				ctrl.mergeOptions = Object.freeze(
					[
						{
							label: 'Skip duplicates',
							value: 'skip',
						},
						{
							label: 'Add new',
							value: 'new',
						},
						{
							label: 'Replace existing',
							value: 'replace',
						},
					]
				);

				ctrl.importSourceOptions = Object.freeze(
					[
						{
							label: 'unknown',
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
					]
				);


				ctrl.selectedImportSource = ctrl.importSourceOptions[0].value;
				ctrl.selectedMergeStrategy = ctrl.mergeOptions[0].value;
				ctrl.selectedFiles = [];

				ctrl.$onInit = function inInit()
				{
					ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT
				}

				ctrl.onRunImport = function onRunImport()
				{
					console.info("onRunImport", ctrl.selectedFiles);
				}

				ctrl.onFileSelected = (files) =>
				{
					ctrl.selectedFiles = files;
					$scope.$apply();
				}

			}]
	});