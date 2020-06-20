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

angular.module('Record.Details').component('careTeamSection', {
	templateUrl: 'src/record/details/components/careTeamSection/careTeamSection.jsp',
	bindings: {
		ngModel: "=",
		componentStyle: "<?"
	},
	controller: [ "$scope",
		"$uibModal",
		"staticDataService",
		"providersService",
		"demographicsService",
		"referralDoctorsService",
		function ($scope,
							$uibModal,
							staticDataService,
							providersService,
							demographicsService,
							referralDoctorsService)
	{
		let ctrl = this;

		$scope.LABEL_POSITION = LABEL_POSITION;

		ctrl.numberRegex=/^\d*$/
		ctrl.patientStatusList = [];
		ctrl.referralDoctors = [{value: "", label: "--"}];
		ctrl.rosterTermReasons = staticDataService.getRosterTerminationReasons();

		ctrl.$onInit = () =>
		{
			ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT

			providersService.getBySecurityRole("doctor").then(
					function success(data) {
						ctrl.doctors = data.map((doc) => {return {label: doc.name, value: doc.providerNo}});
						ctrl.doctors.push({label: "--", value: ""})
					}
			);

			providersService.getBySecurityRole("nurse").then(
					function success(data) {
						ctrl.nurses = data.map((doc) => {return {label: doc.name, value: doc.providerNo}});
						ctrl.nurses.push({label: "--", value: ""})
					}
			);

			providersService.getBySecurityRole("midwife").then(
					function success(data) {
						ctrl.midwives = data.map((doc) => {return {label: doc.name, value: doc.providerNo}});
						ctrl.midwives.push({label: "--", value: ""})
					}
			);

			demographicsService.getStatusList("roster").then(
					function success(data)
					{
						ctrl.rosterStatusList = data;
						ctrl.rosterStatusList.unshift(
								{
									"value": "FS",
									"label": "FS - fee for service"
								});
						ctrl.rosterStatusList.unshift(
								{
									"value": "TE",
									"label": "TE - terminated"
								});
						ctrl.rosterStatusList.unshift(
								{
									"value": "NR",
									"label": "NR - not rostered"
								});
						ctrl.rosterStatusList.unshift(
								{
									"value": "RO",
									"label": "RO - rostered"
								});
					}
			);

			demographicsService.getStatusList("patient").then(
					function success(data)
					{
						ctrl.patientStatusList = data;
						ctrl.patientStatusList.unshift(
								{
									"value": "FI",
									"label": "FI - Fired"
								});
						ctrl.patientStatusList.unshift(
								{
									"value": "MO",
									"label": "MO - Moved"
								});
						ctrl.patientStatusList.unshift(
								{
									"value": "DE",
									"label": "DE - Deceased"
								});
						ctrl.patientStatusList.unshift(
								{
									"value": "IN",
									"label": "IN - Inactive"
								});
						ctrl.patientStatusList.unshift(
								{
									"value": "AC",
									"label": "AC - Active"
								});
					}
			);
		}

		ctrl.updateReferralDoctors = (docSearchString, docReferralNo) =>
		{
			referralDoctorsService.searchReferralDoctors(docSearchString, docReferralNo, 1, 10).then(
				function success(results) {
					var referralDoctors = new Array(results.length);
					for (var i = 0; i < results.length; i++)
					{
						var displayName = results[i].lastName + ', ' + results[i].firstName;
						referralDoctors[i] = {
							label: displayName,
							value: displayName,
							referralNo: results[i].referralNo
						};
						if (results[i].specialtyType != null && results[i].specialtyType != "")
						{
							referralDoctors[i].label += " [" + results[i].specialtyType + "]";
						}
					}

					ctrl.referralDoctors = referralDoctors;
				},
				function failure(errors) {
					return [];
				}
			);
		}

		ctrl.updateReferralNo = (value) =>
		{
			ctrl.ngModel.scrReferralDocNo = value.referralNo;
		}

		ctrl.openAddPatientStatusModal = async () =>
		{
			try
			{
				let newStatus = await Juno.Common.Util.openInputDialog($uibModal, "Add Patient Status",
						"Input the new patient status", ctrl.componentStyle)

				if (newStatus)
				{
					ctrl.addNewPatientStatus(newStatus);
					this.ngModel.patientStatus = newStatus;
				}
			}
			catch (e)
			{
				//user abort (Esc key)
			}
		}

		ctrl.addNewPatientStatus = (status) =>
		{
			this.patientStatusList.push({"label": status, "value": status});
		}

		ctrl.openAddRosterStatusModal = async () =>
		{
			try
			{
				let newStatus = await Juno.Common.Util.openInputDialog($uibModal, "Add Roster Status",
						"Input the new roster status", ctrl.componentStyle)

				if (newStatus)
				{
					ctrl.addNewRosterStatus(newStatus);
					ctrl.ngModel.rosterStatus = newStatus;
				}
			}
			catch (e)
			{
				//user abort (Esc key)
			}
		}

		ctrl.addNewRosterStatus = (newStatus) =>
		{
			ctrl.rosterStatusList.push({"label": newStatus, "value": newStatus});
		}

	}]
});