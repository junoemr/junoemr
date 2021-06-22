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

import MhaPatientService from "../../../../../lib/integration/myhealthaccess/service/MhaPatientService";
import {sexToHuman} from "../../../../../lib/demographic/model/Sex";

angular.module('Common.Components.MhaPatientDetailsModal').component('mhaPatientProfileDetails',
{
	templateUrl: 'src/common/modals/mhaPatientDetailsModal/components/mhaPatientProfileDetails/mhaPatientProfileDetails.jsp',
	bindings: {
		demographicNo: "<",
		integrationId: "<?"
	},
	controller: [
		'$scope',
		function ($scope)
		{
			const BIRTHDATE_FORMAT_STRING = "LL"

			const ctrl = this;
			const mhaPatientService = new MhaPatientService();

			ctrl.profile = null; // Type MhaPatient

			ctrl.formatBirthdate = (mhaPatient) =>
			{
				if (mhaPatient)
				{
					return mhaPatient.birthDate.format(BIRTHDATE_FORMAT_STRING);
				}
				return null;
			}

			ctrl.formatSex = (mhaPatient) =>
			{
				if (mhaPatient)
				{
					return sexToHuman(mhaPatient.sex);
				}
				return null;
			}

			ctrl.loadMhaProfile = async () =>
			{
				if (ctrl.demographicNo && ctrl.integrationId)
				{
					ctrl.profile = await mhaPatientService.profileForDemographic(ctrl.integrationId, ctrl.demographicNo);
					$scope.$apply();
				}
			}

			$scope.$watch("$ctrl.integrationId", ctrl.loadMhaProfile);
			$scope.$watch("$ctrl.demographicNo", ctrl.loadMhaProfile);
		}]
});
