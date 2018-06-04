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
angular.module('Patient.Search').controller('Patient.Search.RemotePatientResultsController', [

	'$http',
	'$scope',
	'$uibModalInstance',
	'results',
	'total',

	function(
		$http,
		$scope,
		$uibModalInstance,
		results,
		total)
	{
		$scope.results = results;
		$scope.total = total;

		$scope.currentPage = 1;
		$scope.pageSize = 5;
		$scope.startIndex = 0;


		$scope.close = function close()
		{
			$uibModalInstance.close("Someone Closed Me");
		};

		$scope.doImport = function doImport(d)
		{
			var myUrl = '../appointment/copyRemoteDemographic.jsp?remoteFacilityId=' + d.remoteFacilityId + '&demographic_no=' + d.demographicNo;
			window.open(myUrl, "ImportDemo", "width=700, height=1027");
		};

		$scope.save = function save()
		{
			$uibModalInstance.close("Someone Saved Me");
		};

		$scope.prevPage = function prevPage()
		{
			if ($scope.startIndex == 0)
			{
				return;
			}
			$scope.currentPage--;
			$scope.startIndex = ($scope.currentPage - 1) * $scope.pageSize;

		};

		$scope.nextPage = function nextPage()
		{
			if ($scope.startIndex + $scope.pageSize > $scope.total)
			{
				return;
			}
			$scope.currentPage++;
			$scope.startIndex = ($scope.currentPage - 1) * $scope.pageSize;
		};


	}
]);