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

import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN, JUNO_STYLE, LABEL_POSITION} from "../../../../common/components/junoComponentConstants";
import { MHA_PATIENT_CONNECTION_ACTIONS } from "../mhaPatientConnection/mhaPatientConnectionConstants"

angular.module('Record.Details').component('demographicSection', {
	templateUrl: 'src/record/details/components/demographicSection/demographicSection.jsp',
	bindings: {
		ngModel: "=",
		validations: "=",
		componentStyle: "<?"
	},
	controller: [
			"staticDataService",
			"$scope",
			"$uibModal",
		function (staticDataService, $scope, $uibModal)
		{
			let ctrl = this;

			ctrl.genderOptions = staticDataService.getGenders();
			ctrl.spokenLanguages = staticDataService.getSpokenLanguages();
			ctrl.languages =  staticDataService.getEngFre();
			ctrl.titles = staticDataService.getTitles();
			ctrl.titles.push({"label": "--", "value": ''})
			ctrl.aboriginalStatuses = staticDataService.getAboriginalStatuses();
			ctrl.countries = staticDataService.getCountries();

			// a list displaying the connected MHA sites.
			ctrl.mhaSites = "";

			ctrl.dobValid = true;

			$scope.LABEL_POSITION = LABEL_POSITION;
			$scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
			$scope.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

			ctrl.$onInit = () =>
			{
				ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT

				ctrl.validations["dateOfBirth"] = Juno.Validations.validationCustom(() => ctrl.dobValid);
			}

			ctrl.onValidChange = (valid) =>
			{
				console.log(valid)
			}

			ctrl.onMHASiteListChange = (sites) =>
			{
				if (sites.length > 0)
				{
					ctrl.mhaSites = sites.reduce((acc, site) => acc = `${acc}, ${site}`);
				}
			}

			ctrl.canOpenPatientModal = () =>
			{
				return (ctrl.mhaSites !== '');
			}

			ctrl.openPatientModal = async () =>
			{
				if (ctrl.canOpenPatientModal())
				{
					try
					{
						let connectionChange = await $uibModal.open(
								{
									component: 'mhaPatientDetailsModal',
									backdrop: 'static',
									windowClass: "juno-simple-modal-window",
									resolve: {
										style: () => ctrl.componentStyle,
										demographic: () => ctrl.ngModel,
									}
								}
						).result;

						if (connectionChange)
						{
							$scope.$broadcast(MHA_PATIENT_CONNECTION_ACTIONS.REFRESH, null)
						}
					} catch (err)
					{
						// user pressed ESC key
					}
				}
			}
		}]
});