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

oscarApp.controller('NewPatientCtrl', [

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
        staticDataService) {

        $scope.demographic = {};

        //get access right for creating new patient
        securityService.hasRight("_demographic", "w").then(function(data) {
            $scope.hasRight = data;
        });

        //get programs to be selected
        programService.getPrograms().then(
            function success(data) {
                $scope.programs = data;
                if ($scope.programs.length == 1) {
                    $scope.demographic.admissionProgramId = $scope.programs[0].id;
                }
            });

        //get genders to be selected
        $scope.genders = staticDataService.getGenders();

        $scope.saver = function saver(ngModelContoller) {
            console.log($scope.demographic.lastName);
            console.log($scope.demographic.firstName);
            console.log($scope.demographic.dobYear);
            console.log($scope.demographic.dobMonth);
            console.log($scope.demographic.dobDay);
            console.log($scope.demographic.sex);
            console.log($scope.demographic);
            console.log(ngModelContoller.$valid);
            console.log($scope);

            if (ngModelContoller.$valid) {
                console.log("Saving...");

                if (!$scope.isCorrectDate($scope.demographic.dobYear,
                        $scope.demographic.dobMonth, $scope.demographic.dobDay)) {
                    alert("Incorrect Date of Birth!");
                    return;
                }

                $scope.demographic.dateOfBirth =
                    $scope.demographic.dobYear + '-' +
                    $scope.demographic.dobMonth + "-" +
                    $scope.demographic.dobDay;

                $scope.demographic.patientStatusDate = new Date();
                $scope.demographic.dateJoined = new Date();
                $scope.demoRetVal = {};

                demographicService.saveDemographic($scope.demographic).then(
                    function success(data) {
                        console.log(data);
                        $scope.demoRetVal = data;
                        $uibModalInstance.close(data);
                    },
                    function(errorMessage) {
                        console.log("saveDemo " + errorMessage);
                    });

            } else {
                console.log("ERR!!");
            }
        };

        $scope.ok = function() {
            $uibModalInstance.close($scope.selected.item);
        };

        $scope.cancel = function() {
            $uibModalInstance.dismiss('cancel');
        };

        $scope.capName = function() {
            if ($scope.demographic.lastName != null) {
                $scope.demographic.lastName = $scope.demographic.lastName.toUpperCase();
            }

            if ($scope.demographic.firstName != null) {
                $scope.demographic.firstName = $scope.demographic.firstName.toUpperCase();
            }
        };

        $scope.isCorrectDate = function isCorrectDate(year, month, day) {
            var d = new Date(year, month - 1, day);

            if (d == "Invalid Date") return false;
            if (d.getFullYear() != year) return false;
            if (d.getMonth() != month - 1) return false;
            if (d.getDate() != day) return false;

            return true;
        };
    }
]);