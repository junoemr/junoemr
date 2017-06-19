'use strict';

/*

    Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
    This software is published under the GPL GNU General Public License.
    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2
    of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

    This software was written for the
    Department of Family Medicine
    McMaster University
    Hamilton
    Ontario, Canada

*/

angular.module('Patient').controller('Patient.NewPatientController', [

	'$scope',
	'$uibModal',
	'$uibModalInstance',
	'demographicService',
	'securityService',
	'programService',
	'staticDataService',

	function(
		$scope,
		$uibModal,
		$uibModalInstance,
		demographicService,
		securityService,
		programService,
		staticDataService)
	{
		var controller = this;

		controller.demographic = {};

		//get access right for creating new patient
		securityService.hasRight("_demographic", "w").then(
			function success(results)
			{
				controller.hasRight = results;
			},
			function error(errors)
			{
				console.log(errors);
			});

		//get programs to be selected
		programService.getPrograms().then(
			function success(results)
			{
				controller.programs = results;
				if (controller.programs.length == 1)
				{
					controller.demographic.admissionProgramId = controller.programs[0].id;
				}
			},
			function error(errors)
			{
				console.log(errors);
			});

		//get genders to be selected
		controller.genders = staticDataService.getGenders();

		controller.save = function save(ngModelContoller)
		{
			console.log(controller.demographic.lastName);
			console.log(controller.demographic.firstName);
			console.log(controller.demographic.dobYear);
			console.log(controller.demographic.dobMonth);
			console.log(controller.demographic.dobDay);
			console.log(controller.demographic.sex);
			console.log(controller.demographic);
			console.log(ngModelContoller.$valid);
			console.log($scope);

			if (ngModelContoller.$valid)
			{
				console.log("Saving...");

				if (!controller.isCorrectDate(controller.demographic.dobYear,
						controller.demographic.dobMonth, controller.demographic.dobDay))
				{
					alert("Incorrect Date of Birth!");
					return;
				}

				controller.demographic.dateOfBirth =
					controller.demographic.dobYear + '-' +
					controller.demographic.dobMonth + "-" +
					controller.demographic.dobDay;

				controller.demographic.patientStatusDate = new Date();
				controller.demographic.dateJoined = new Date();
				controller.demoRetVal = {};

				demographicService.saveDemographic(controller.demographic).then(
					function success(results)
					{
						console.log(results);
						controller.demoRetVal = results;
						$uibModalInstance.close(results);
					},
					function error(errors)
					{
						console.log(errors);
					});

			}
			else
			{
				console.log("ERR!!");
			}
		};

		controller.ok = function ok()
		{
			$uibModalInstance.close(controller.selected.item);
		};

		controller.cancel = function cancel()
		{
			$uibModalInstance.dismiss('cancel');
		};

		controller.capName = function capName()
		{
			if (controller.demographic.lastName != null)
			{
				controller.demographic.lastName = controller.demographic.lastName.toUpperCase();
			}

			if (controller.demographic.firstName != null)
			{
				controller.demographic.firstName = controller.demographic.firstName.toUpperCase();
			}
		};

		controller.isCorrectDate = function isCorrectDate(year, month, day)
		{
			var d = new Date(year, month - 1, day);

			if (d == "Invalid Date") return false;
			if (d.getFullYear() != year) return false;
			if (d.getMonth() != month - 1) return false;
			if (d.getDate() != day) return false;

			return true;
		};
	}
]);