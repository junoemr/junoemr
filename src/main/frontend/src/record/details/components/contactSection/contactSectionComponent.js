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
import {ElectronicMessagingConsentStatus} from "../../../../lib/demographic/ElectronicMessagingConsentStatus";

angular.module('Record.Details').component('contactSection', {
	templateUrl: 'src/record/details/components/contactSection/contactSection.jsp',
	bindings: {
		ngModel: "=",
		validations: "=",
		componentStyle: "<?"
	},
	controller: [ "staticDataService", "$scope", function (staticDataService, $scope)
	{
		let ctrl = this;

		$scope.LABEL_POSITION = LABEL_POSITION;

		ctrl.provinces = staticDataService.getProvinces();
		ctrl.phoneNumberRegex = /^[\d-\s()]*$/;
		ctrl.electronicMessagingConsentOptions = [];

		ctrl.$onInit = () =>
		{
			ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT

			ctrl.validations = Object.assign(ctrl.validations, {
				email: Juno.Validations.validationEmail(ctrl, "ngModel.email"),
			});

			ctrl.buildElectronicMessagingConsentOptions();
		}

		ctrl.buildElectronicMessagingConsentOptions = () =>
		{
			ctrl.electronicMessagingConsentOptions = [
				{
					label: "No Consent",
					value: ElectronicMessagingConsentStatus.NONE,
				},
				{
					label: "Consented",
					value: ElectronicMessagingConsentStatus.CONSENTED,
				},
				{
					label: "Revoked",
					value: ElectronicMessagingConsentStatus.REVOKED,
				},
			];
		};

		ctrl.onConsentStatusChange = (value) =>
		{
			// Update consent timestamps for immediate UI feedback.
			// Exact value will be calculated by the backend on save.
			switch (value)
			{
				case ElectronicMessagingConsentStatus.NONE:
					ctrl.ngModel.electronicMessagingConsentGivenAt = null;
					ctrl.ngModel.electronicMessagingConsentRejectedAt = null;
					break;
				case ElectronicMessagingConsentStatus.REVOKED:
					ctrl.ngModel.electronicMessagingConsentRejectedAt = moment();
					break;
				case ElectronicMessagingConsentStatus.CONSENTED:
					ctrl.ngModel.electronicMessagingConsentGivenAt = moment();
					ctrl.ngModel.electronicMessagingConsentRejectedAt = null;
					break;
			}
		}

		/**
		 * get a string representing the date on which electronic messaging was consented to.
		 * @returns {string}
		 */
		ctrl.getElectronicMessagingConsentStatusText = () =>
		{
			if (ctrl.ngModel && ctrl.ngModel.electronicMessagingConsentStatus !== ElectronicMessagingConsentStatus.NONE)
			{
				let eventDate = ctrl.ngModel.electronicMessagingConsentGivenAt;
				if (ctrl.ngModel.electronicMessagingConsentStatus === ElectronicMessagingConsentStatus.REVOKED)
				{
					eventDate = ctrl.ngModel.electronicMessagingConsentRejectedAt;
				}

				return `On, ${Juno.Common.Util.formatMomentTime(moment(eventDate), Juno.Common.Util.settings.date_format)}`;
			}
			return "";
		}

	}]
});