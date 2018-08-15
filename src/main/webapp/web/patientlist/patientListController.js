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
	'angularUtil',
	'Navigation',
	'personaService',
	'providerService',
	'patientListState',
	'scheduleService',
	'reportingService',

	function(
		$scope,
		$http,
		$state,
		$uibModal,
		angularUtil,
		Navigation,
		personaService,
		providerService,
		patientListState,
		scheduleService,
		reportingService)
	{

		var controller = this;
		controller.sidebar = Navigation;
		controller.showFilter = true;
		controller.patientListConfig = {};


		controller.goToRecord = function goToRecord(patient)
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

					if (angularUtil.inMobileView())
					{
						controller.hidePatientList();
						console.log('hiding patientlist');
					}
				}
				console.log("params", params);
				$state.go('record.summary', params);
			}
		};

		//for filter box
		controller.query = '';


		controller.isActive = function isActive(temp)
		{
			if (controller.currenttab === null)
			{
				return false;
			}
			return temp === controller.currenttab.id;
		};

		controller.isMoreActive = function isMoreActive(temp)
		{
			if (controller.currentmoretab === null)
			{
				return false;
			}
			return temp === controller.currentmoretab.id;
		};

		controller.showPatientList = function showPatientList()
		{
			$scope.$emit('configureShowPatientList', true);
		};

		controller.hidePatientList = function hidePatientList()
		{
			$scope.$emit('configureShowPatientList', false);
		};

		controller.changeMoreTab = function changeMoreTab(moreTabItemsIndex, filter)
		{
			var beforeChangeTab = controller.currentmoretab;
			controller.currentmoretab = controller.moreTabItems[moreTabItemsIndex];

			controller.showFilter = true;
			controller.currenttab = null;
			controller.refresh(filter);
		};

		controller.changeTab = function changeTab(tabItemIndex, filter)
		{
			if(controller.currenttab !== patientListState.tabItems[tabItemIndex])
			{
				controller.currenttab = patientListState.tabItems[tabItemIndex];
				controller.showFilter = true;
				controller.currentmoretab = null;
				controller.refresh(filter);
			}
		};

		controller.getMoreTabClass = function getMoreTabClass(id)
		{
			if (controller.currentmoretab != null && id == controller.currentmoretab.id)
			{
				return "more-tab-highlight";
			}
			return "";
		};

		controller.currentPage = 0;
		controller.pageSize = 8;
		controller.patients = null;

		controller.numberOfPages = function numberOfPages()
		{
			if (controller.nPages == null || controller.nPages == 0)
			{
				return 1;
			}
			return controller.nPages;
		};


		$scope.$on('updatePatientListPagination', function(event, data)
		{
			console.log('updatePatientListPagination=' + data);
			controller.nPages = Math.ceil(data / controller.pageSize);
			console.log('nPages=' + controller.nPages);
		});


		controller.changePage = function changePage(pageNum)
		{
			controller.currentPage = pageNum;
			//broadcast the change page
			$scope.$broadcast('updatePatientList',
			{
				currentPage: controller.currentPage,
				pageSize: controller.pageSize
			});
		};

		$scope.$on('togglePatientListFilter', function(event, data)
		{
			console.log("received a togglePatientListFilter event:" + data);
			controller.showFilter = data;
		});


		controller.process = function process(tab, filter)
		{
			tab.serviceMethod().then(
				function success(resultList)
				{
					controller.patients = resultList;

					controller.nPages = 1;
					if (controller.patients != null && controller.patients.length > 0)
					{
						controller.nPages = Math.ceil(controller.patients.length / controller.pageSize);
					}

					controller.template = tab.template;
					Navigation.load(controller.template);
					controller.changePage(0);
				},
				function error(error)
				{
					alert('error loading data for patient list:' + error);
				}
			);
		};

		controller.refresh = function refresh(filter)
		{

			if (controller.currenttab != null)
			{
				controller.process(controller.currenttab, filter);
			}
			if (controller.currentmoretab != null)
			{
				controller.process(controller.currentmoretab, filter);
			}

		};

		$scope.$on('juno:patientListRefresh', function()
		{
			controller.refresh();
		});

		providerService.getRecentPatientList().then(
			function success(results)
			{
				controller.recentPatientList = results;
			},
			function error(errors)
			{
				console.log(errors);
			});
		personaService.getPatientListConfig().then(
			function success(results)
			{
				controller.patientListConfig = results;
				controller.pageSize = controller.patientListConfig.numberOfApptstoShow;
			},
			function error(errors)
			{
				console.log(errors);
			});


		controller.getTabItems = function getTabItems()
		{
			return patientListState.tabItems;
		};

		controller.manageConfiguration = function manageConfiguration()
		{
			var modalInstance = $uibModal.open(
			{
				templateUrl: 'patientlist/patientListConfiguration.jsp',
				controller: 'PatientList.PatientListConfigController as patientListConfigCtrl',
				backdrop: false,
				size: 'lg',
				resolve:
				{
					config: function()
					{
						return controller.patientListConfig;
					}
				}
			});

			modalInstance.result.then(
				function success(results)
				{
					personaService.setPatientListConfig(results).then(
						function success(results)
						{
							controller.patientListConfig = results;
							controller.pageSize = controller.patientListConfig.numberOfApptstoShow;
							$scope.$emit('updatePatientListPagination', controller.patients.length);
						},
						function error(errors)
						{
							console.log(errors);
						});
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		patientListState.tabItems = [
			{
				id: 0,
				label: "Appts.",
				template: "patientlist/patientList1.jsp",
				serviceMethod: function ()
				{
					// this gets overwritten by the appointmentListController, when it sets specific dates
					return scheduleService.getAppointments('today').then(
						function success(results)
						{
							return results.patients;
						}
					);
				}
			},
			{
				id: 1,
				label: "Recent",
				template: "patientlist/recent.jsp",
				serviceMethod: function ()
				{
					return providerService.getRecentPatientList().then(
						function success(results)
						{
							controller.recentPatientList = results;
							return results;
						}
					);
				}
			}
		];
		controller.moreTabItems = [
			{
				id: 0,
				label: "Patient Sets",
				template: "patientlist/demographicSets.jsp",
				serviceMethod: function ()
				{
					return reportingService.getDemographicSetList().then(
						function success(results)
						{
							return results.content;
						}
					);
				}
			},
			{
				id: 1,
				label: "Caseload",
				template: "patientlist/program.jsp",
				serviceMethod: function ()
				{
					return Promise.resolve([]);
				}
			}
		];
		controller.changeTab(0);
	}
]);