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

import {JUNO_BUTTON_COLOR_PATTERN} from "../../../../components/junoComponentConstants";
import MhaPatientService from "../../../../../lib/integration/myhealthaccess/service/MhaPatientService";
import MhaPatientAccessService from "../../../../../lib/integration/myhealthaccess/service/MhaPatientAccessService";

angular.module('Common.Components.MhaPatientDetailsModal').component('mhaPatientConnectionDetails',
	{
		templateUrl: 'src/common/modals/mhaPatientDetailsModal/components/mhaPatientConnectionDetails/mhaPatientConnectionDetails.jsp',
		bindings: {
			profile: "<", // Type MhaPatient
			integration: "<", // Type MhaIntegration
			demographicNo: "<",
			disabled: "<?",
			onConnectionUpdated: "&?",
		},
		controller: [
			'$scope',
			'$uibModal',
			function ($scope, $uibModal)
			{
				const ctrl = this;
				const patientService = new MhaPatientService();
				const patientAccessService = new MhaPatientAccessService();

				$scope.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;
				$scope.VERIFICATION_CODE_LENGTH = 6;

				ctrl.patientAccess = null; // Type MhaPatientAccess

				// verification
				ctrl.loadingVerificationProfile = false;
				ctrl.verifying = false;
				ctrl.verificationCode = "";
				ctrl.verificationProfile = null; // Type MhaPatient

				ctrl.$onInit = () =>
				{
					ctrl.disabled = ctrl.disabled || false;
				}

				ctrl.startVerification = () =>
				{
					ctrl.verifying = true;
				}

				ctrl.formatStatusDate = (date) =>
				{
					if (date && date.isValid())
					{
						return date.format(Juno.Common.Util.settings.month_name_day_year);
					}
					return null;
				}

				ctrl.onCodeChange = async (code) =>
				{
					if (ctrl.integration && code && code.length >= $scope.VERIFICATION_CODE_LENGTH)
					{
						try
						{
							ctrl.loadingVerificationProfile = true;
							ctrl.verificationProfile = await patientService.getProfileByAccountIdCode(ctrl.integration.id, code);
						}
						finally
						{
							ctrl.loadingVerificationProfile = false;
							$scope.$apply();
						}
					}
				}

				ctrl.stopVerifying = () =>
				{
					ctrl.verifying = false;
					ctrl.verificationProfile = null;
					ctrl.verificationCode = "";
				}

				ctrl.confirmVerification = async () =>
				{
					try
					{
						await patientAccessService.verifyPatientByAccountIdCode(
							ctrl.integration.id,
							ctrl.verificationProfile,
							ctrl.demographicNo,
							ctrl.verificationCode);

						ctrl.notifyListenerOfConnectionUpdate();
					}
					catch(error)
					{
						console.error(error);

						Juno.Common.Util.errorAlert(
							$uibModal,
							"Some Thing Went Wrong.",
							"Patient verification failed. The code may have expired. Please try again. Please contact support if the problem persists.");
					}
				}

				ctrl.cancelVerification = async () =>
				{
					try
					{
						await patientAccessService.cancelPatientVerification(ctrl.integration.id, ctrl.profile.id);
						ctrl.notifyListenerOfConnectionUpdate();
					}
					catch(error)
					{
						console.error(error);

						Juno.Common.Util.errorAlert(
							$uibModal,
							"Some Thing Went Wrong.",
							"Could not cancel patient verification. Please contact support if the problem persists.");
					}
				}

				ctrl.confirm = async () =>
				{
					try
					{
						await patientAccessService.confirmPatient(ctrl.integration.id, ctrl.profile.id, ctrl.demographicNo);
						ctrl.notifyListenerOfConnectionUpdate();
					}
					catch(error)
					{
						console.error(error);

						Juno.Common.Util.errorAlert(
							$uibModal,
							"Some Thing Went Wrong.",
							"Could not confirm patient. Please contact support if the problem persists.");
					}
				}

				ctrl.cancelConfirmation = async () =>
				{
					try
					{
						await patientAccessService.cancelPatientConfirmation(ctrl.integration.id, ctrl.profile.id);
						ctrl.notifyListenerOfConnectionUpdate();
					}
					catch(error)
					{
						console.error(error);

						Juno.Common.Util.errorAlert(
							$uibModal,
							"Some Thing Went Wrong.",
							"Could not cancel the patients confirmation. Please contact support if the problem persists.");
					}
				}

				ctrl.notifyListenerOfConnectionUpdate = () =>
				{
					if (ctrl.onConnectionUpdated)
					{
						ctrl.onConnectionUpdated({});
					}
				}

				ctrl.loadPatientAccess = async () =>
				{
					if (ctrl.profile && ctrl.integration)
					{
						const requestProfileId = ctrl.profile.id;
						const patientAccess = await ctrl.profile.getPatientAccessRecord(ctrl.integration);

						// make sure the access record is for the currently selected profile.
						if (requestProfileId === ctrl.profile.id)
						{
							ctrl.patientAccess = patientAccess;
							ctrl.stopVerifying();
							$scope.$apply();
						}
					}
				}

				$scope.$watch("$ctrl.profile", ctrl.loadPatientAccess);
			}]
	});
