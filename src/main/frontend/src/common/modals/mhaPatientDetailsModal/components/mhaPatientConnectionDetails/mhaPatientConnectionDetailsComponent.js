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

angular.module('Common.Components.MhaPatientDetailsModal').component('mhaPatientConnectionDetails',
	{
		templateUrl: 'src/common/modals/mhaPatientDetailsModal/components/mhaPatientConnectionDetails/mhaPatientConnectionDetails.jsp',
		bindings: {
			profile: "<", // Type MhaPatient
			integration: "<", // Type MhaIntegration
		},
		controller: [
			'$scope',
			function ($scope)
			{
				const ctrl = this;
				const STATUS_DATE_FORMAT = "LL";

				$scope.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

				ctrl.patientAccess = null; // Type MhaPatientAccess
				ctrl.verifying = false;

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

				ctrl.loadPatientAccess = async () =>
				{
					if (ctrl.profile && ctrl.integration)
					{
						ctrl.patientAccess = await ctrl.profile.getPatientAccessRecord(ctrl.integration);
						$scope.$apply();
					}
				}

				$scope.$watch("$ctrl.profile", ctrl.loadPatientAccess);
				$scope.$watch("$ctrl.integration", ctrl.loadPatientAccess);
			}]
	});
