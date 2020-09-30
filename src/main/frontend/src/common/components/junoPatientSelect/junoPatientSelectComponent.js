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

import {JUNO_STYLE, LABEL_POSITION} from "../junoComponentConstants";

angular.module('Common.Components').component('junoPatientSelect', {
	templateUrl: 'src/common/components/junoPatientSelect/junoPatientSelect.jsp',
	bindings: {
		ngModel: "=",
		componentStyle: "<?",
		showPatientCard: "<?"
	},
	controller: ['$scope', 'demographicsService', function ($scope, demographicsService)
	{
		let ctrl = this;

		$scope.LABEL_POSITION = LABEL_POSITION;

		ctrl.patientOptions = [];
		ctrl.demographicNo = null;

		ctrl.$onInit = () =>
		{
			ctrl.label = ctrl.label || "";
			ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;
			ctrl.showPatientCard = ctrl.showPatientCard || false;
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
				ctrl.patientOptions.push(
					{
						label: `${demo.lastName}, ${demo.firstName}`,
						value: demo.demographicNo,
						obj: demo,
					});
			});
		}

		ctrl.onDemographicSelected = (demoNo) =>
		{
			ctrl.ngModel = demoNo.obj;
		}

		ctrl.componentClasses = () =>
		{
			return [ctrl.componentStyle];
		}
	}],
});
