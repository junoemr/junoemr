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

import {JUNO_STYLE, LABEL_POSITION} from "../../../../common/components/junoComponentConstants";

angular.module('Record.Details').component('additionalInformationSection', {
	templateUrl: 'src/record/details/components/additionalInformationSection/additionalInformationSection.jsp',
	bindings: {
		ngModel: "=",
		validations: "=",
		componentStyle: "<?"
	},
	controller: [ "staticDataService", "$scope", function (staticDataService, $scope)
	{
		let ctrl = this;

		$scope.LABEL_POSITION = LABEL_POSITION;
		ctrl.waitingListNames = [];

		ctrl.archivedChartOptions = staticDataService.getArchivedChartOptions();
		ctrl.securityQuestions = staticDataService.getSecurityQuestions();
		ctrl.securityQuestions.unshift({label: "--", value: null});
		ctrl.rxInteractionLevels = staticDataService.getRxInteractionLevels();

		ctrl.dateOfRequestValid = true;

		ctrl.$onInit = () =>
		{
			ctrl.validations["dateOfRequest"] = Juno.Validations.validationCustom(() => ctrl.dateOfRequestValid);
			ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT
		}

		ctrl.$doCheck = () =>
		{
			// if (ctrl.ngModel && (!ctrl.waitingListNames || ctrl.waitingListNames.length !== ctrl.ngModel.waitingListNames.length))
			// {
			// 	ctrl.waitingListNames =
			// 			ctrl.ngModel.waitingListNames.map((item) => Object.assign({}, item, {label: item.name, value: item.id}));
			// }
			// TODO re-implement before release
		}
	}

	]
});