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

angular.module('PatientList').controller('PatientList.PatientListController', [

	'$scope',
	'$http',
	'$state',
	'$uibModal',
	'Navigation',
	'personaService',

	function(
		$scope,
		$http,
		$state,
		$uibModal,
		Navigation,
		personaService)
	{

		$scope.sidebar = Navigation;

		$scope.showFilter = true;
		$scope.patientListConfig = {};


		$scope.goToRecord = function(patient)
		{
			if (patient.demographicNo != 0)
			{
				var params = {
					demographicNo: patient.demographicNo
				};
				if (angular.isDefined(patient.appointmentNo))
				{
					params.appointmentNo = patient.appointmentNo;
					params.encType = "face to face encounter with client";
				}
				console.log("params", params);
				$state.go('record.summary', params);
			}
		};

		//for filter box
		$scope.query = '';


		$scope.isActive = function(temp)
		{
			if ($scope.currenttab === null)
			{
				return false;
			}
			return temp === $scope.currenttab.id;
		};

		$scope.isMoreActive = function(temp)
		{
			if ($scope.currentmoretab === null)
			{
				return false;
			}
			return temp === $scope.currentmoretab.id;
		};

		$scope.showPatientList = function()
		{
			$scope.$emit('configureShowPatientList', true);
		};

		$scope.hidePatientList = function()
		{
			$scope.$emit('configureShowPatientList', false);
		};

		$scope.changeMoreTab = function(temp, filter)
		{
			var beforeChangeTab = $scope.currentmoretab;
			$scope.currentmoretab = $scope.moreTabItems[temp];

			$scope.showFilter = true;
			$scope.currenttab = null;
			$scope.refresh(filter);
		};

		$scope.changeTab = function(temp, filter)
		{
			console.log('change tab - ' + temp);
			$scope.currenttab = $scope.tabItems[temp];
			$scope.showFilter = true;
			$scope.currentmoretab = null;
			$scope.refresh(filter);

		};

		$scope.getMoreTabClass = function(id)
		{
			if ($scope.currentmoretab != null && id == $scope.currentmoretab.id)
			{
				return "more-tab-highlight";
			}
			return "";
		};

		$scope.currentPage = 0;
		$scope.pageSize = 8;
		$scope.patients = null;

		$scope.numberOfPages = function()
		{
			if ($scope.nPages == null || $scope.nPages == 0)
			{
				return 1;
			}
			return $scope.nPages;
		};


		$scope.$on('updatePatientListPagination', function(event, data)
		{
			console.log('updatePatientListPagination=' + data);
			$scope.nPages = Math.ceil(data / $scope.pageSize);
			console.log('nPages=' + $scope.nPages);
		});


		$scope.changePage = function(pageNum)
		{
			$scope.currentPage = pageNum;
			//broadcast the change page
			$scope.$broadcast('updatePatientList',
			{
				currentPage: $scope.currentPage,
				pageSize: $scope.pageSize
			});
		};


		//  $scope.$watch("currentPage", function(newValue, oldValue) {
		//     console.log('currentPage changes from ' + oldValue + ' to ' + newValue);
		//   });

		$scope.$on('togglePatientListFilter', function(event, data)
		{
			console.log("received a togglePatientListFilter event:" + data);
			$scope.showFilter = data;
		});


		$scope.process = function(tab, filter)
		{
			if (tab.url != null)
			{

				var d = undefined;
				if (tab.httpType == 'POST')
				{
					d = filter != null ? JSON.stringify(filter) :
					{}
				}

				$http(
				{
					url: tab.url,
					dataType: 'json',
					data: d,
					method: tab.httpType,
					headers:
					{
						"Content-Type": "application/json"
					}
				}).then(
					function success(response)
					{


						$scope.template = tab.template;
						Navigation.load($scope.template);

						$scope.currentPage = 0;

						if (response.data.patients instanceof Array)
						{
							$scope.patients = response.data.patients;
						}
						else if (response.data.patients == undefined)
						{
							$scope.patients = [];
						}
						else
						{
							var arr = new Array();
							arr[0] = response.data.patients;
							$scope.patients = arr;
						}

						$scope.nPages = 1;
						if ($scope.patients != null && $scope.patients.length > 0)
						{
							$scope.nPages = Math.ceil($scope.patients.length / $scope.pageSize);
						}

					},
					function error(error)
					{
						alert('error loading data for patient list:' + error);
					});
			}
			else
			{
				$scope.changePage($scope.currentPage);
				$scope.currentPage = 0;
				$scope.nPages = 1;
				$scope.template = tab.template;
				Navigation.load($scope.template);
			}
		};

		$scope.refresh = function(filter)
		{

			if ($scope.currenttab != null)
			{
				$scope.process($scope.currenttab, filter);
			}
			if ($scope.currentmoretab != null)
			{
				$scope.process($scope.currentmoretab, filter);
			}

		};

		personaService.getPatientLists().then(function(persona)
		{
			if (persona.patientListTabItems.length == undefined)
			{
				$scope.tabItems = [persona.patientListTabItems];
			}
			else
			{
				$scope.tabItems = persona.patientListTabItems;
			}
			$scope.moreTabItems = persona.patientListMoreTabItems;
			$scope.changeTab(0);
		}, function(reason)
		{
			alert(reason);
		});

		personaService.getPatientListConfig().then(function(patientListConfig)
		{
			$scope.patientListConfig = patientListConfig;
			$scope.pageSize = $scope.patientListConfig.numberOfApptstoShow;
		}, function(reason)
		{
			alert(reason);
		});





		$scope.manageConfiguration = function()
		{
			var modalInstance = $uibModal.open(
			{
				templateUrl: 'patientlist/patientListConfiguration.jsp',
				controller: 'PatientListConfigController',
				backdrop: false,
				size: 'lg',
				resolve:
				{
					config: function()
					{
						return $scope.patientListConfig;
					}
				}
			});

			modalInstance.result.then(function(patientListConfig)
			{
				personaService.setPatientListConfig(patientListConfig).then(function(patientListConfig)
				{
					$scope.patientListConfig = patientListConfig;
					$scope.pageSize = $scope.patientListConfig.numberOfApptstoShow;
					$scope.$emit('updatePatientListPagination', $scope.patients.length);
				}, function(reason)
				{
					alert(reason);
				});

			}, function(reason)
			{
				console.log(reason);
			});
		};
	}
]);
