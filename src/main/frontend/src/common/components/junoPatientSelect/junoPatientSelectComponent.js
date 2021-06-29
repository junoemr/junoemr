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

import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN, JUNO_STYLE, LABEL_POSITION} from "../junoComponentConstants";

angular.module('Common.Components').component('junoPatientSelect', {
	templateUrl: 'src/common/components/junoPatientSelect/junoPatientSelect.jsp',
	bindings: {
		ngModel: "=",
		componentStyle: "<?",
		showPatientCard: "<?",
		showDemographicAdd: "<?",
	},
	controller: ['$scope', '$uibModal', 'demographicsService', function ($scope, $uibModal, demographicsService)
	{
		let ctrl = this;

		$scope.LABEL_POSITION = LABEL_POSITION;
		$scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
		$scope.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

		ctrl.patientOptions = [];
		ctrl.demographicNo = null;

		ctrl.$onInit = () =>
		{
			ctrl.label = ctrl.label || "";
			ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;
			ctrl.showPatientCard = ctrl.showPatientCard || false;
			ctrl.showDemographicAdd = ctrl.showDemographicAdd == null ? true : ctrl.showDemographicAdd;
		}

		$scope.$watch('ngModel', (newDemo) =>
		{
			if (newDemo)
			{
				ctrl.demographicNo = newDemo.demographicNo;
			}
		})

		ctrl.loadPatientOptions = async (searchTerm) =>
		{
			let demographics = (await demographicsService.quickSearch(searchTerm)).data;

			ctrl.patientOptions = [];
			demographics.forEach((demo) =>
			{
				const lastName = demo.lastName ? demo.lastName.toUpperCase() : "";
				const firstName = demo.firstName ? demo.firstName.toUpperCase() : "";

				ctrl.patientOptions.push(
					{
						label: `${lastName}, ${firstName}`,
						value: demo.demographicNo,
						obj: demo,
					});
			});
		}

		ctrl.getPatientOptions = async (searchTerm) =>
		{
			await ctrl.loadPatientOptions(searchTerm);
			return ctrl.patientOptions;
		}

		ctrl.onDemographicSelected = (demo) =>
		{
			ctrl.ngModel = demo.obj;
		}

		ctrl.openNewDemographicModal = async () =>
		{
			try
			{
				let result = await $uibModal.open(
					{
						component: 'addDemographicModal',
						backdrop: 'static',
						windowClass: "juno-modal",
					}).result;

				if (result)
				{

					const lastName = result.lastName ? result.lastName.toUpperCase() : "";
					const firstName = result.firstName ? result.firstName.toUpperCase() : "";
					// select the newly created demographic
					ctrl.patientOptions.push(
						{
							label: `${lastName}, ${firstName}`,
							value: result.demographicNo,
							obj: result,
						});
					ctrl.demographicNo = result.demographicNo;
					ctrl.ngModel = result;
				}
			}
			catch (err)
			{
				//ESC key
			}
		}

		ctrl.componentClasses = () =>
		{
			return [ctrl.componentStyle];
		}

		ctrl.headerRowClasses = () =>
		{
			return ctrl.showPatientCard ? "" : "no-patient-card";
		}
	}],
});
