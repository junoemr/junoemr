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
import DemographicWaitingList from "../../../../lib/waitingList/model/DemographicWaitingList";

angular.module('Record.Details').component('additionalInformationSection', {
	templateUrl: 'src/record/details/components/additionalInformationSection/additionalInformationSection.jsp',
	bindings: {
		ngModel: "=",
		validations: "=",
		componentStyle: "<?"
	},
	controller: [
		"$scope",
		"staticDataService",
		"waitingListService",
		function ($scope, staticDataService, waitingListService)
	{
		let ctrl = this;

		$scope.LABEL_POSITION = LABEL_POSITION;
		ctrl.waitingListNames = [];

		ctrl.archivedChartOptions = staticDataService.getArchivedChartOptions();
		ctrl.securityQuestions = staticDataService.getSecurityQuestions();
		ctrl.securityQuestions.unshift({label: "--", value: null});
		ctrl.rxInteractionLevels = staticDataService.getRxInteractionLevels();

		ctrl.dateOfRequestValid = true;
		ctrl.waitingListOptions = [];
		ctrl.demographicWaitList = null;

		ctrl.$onInit = async () =>
		{
			ctrl.validations["dateOfRequest"] = Juno.Validations.validationCustom(() => ctrl.dateOfRequestValid);
			ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;

			const results = await Promise.all([
				waitingListService.getActiveWaitingLists(),
				waitingListService.getActiveDemographicWaitList(ctrl.ngModel.id),
				]);
			ctrl.waitingListOptions = results[0].map((waitList) =>
			{
				return {
					label: waitList.name,
					value: waitList.id,
				};
			});

			ctrl.demographicWaitList = results[1] || new DemographicWaitingList();
			ctrl.updateDemographicWaitingList(ctrl.demographicWaitList.waitListId);
		}

		// ensure the model is in sync with the local waitList object
		ctrl.updateDemographicWaitingList = (value) =>
		{
			if (value)
			{
				if(!ctrl.demographicWaitList.dateAddedToWaitList)
				{
					ctrl.demographicWaitList.dateAddedToWaitList = moment();
				}
				ctrl.ngModel.waitList = ctrl.demographicWaitList;
			}
			else
			{
				ctrl.ngModel.waitList = null;
			}
		}
	}

	]
});