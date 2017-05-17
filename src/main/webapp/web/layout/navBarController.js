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

angular.module('Layout').controller('Layout.NavBarController', [

	'$rootScope',
	'$scope',
	'$location',
	'$state',
	'$uibModal',
	'securityService',
	'personaService',
	'billingService',
	'inboxService',

	function($rootScope, $scope, $location, $state, $uibModal,
		securityService, personaService, billingService, inboxService)
	{
		var controller = {};

		//=========================================================================
		// Initialization
		//=========================================================================

		controller.init = function init()
		{
			controller.unAckLabDocTotal = 0;

			billingService.getBillingRegion().then(
				function success(response)
				{
					controller.billRegion = response.message;
				},
				function error(reason)
				{
					console.log(reason);
				});

			securityService.hasRights(
			{
				items: [
				{
					objectName: '_search',
					privilege: 'r'
				},
				{
					objectName: '_demographic',
					privilege: 'w'
				},
				{
					objectName: '_msg',
					privilege: 'r'
				}]
			}).then(
				function success(result)
				{
					if (result.content !== null)
					{
						controller.searchRights = result.content[0];
						controller.newDemographicRights = result.content[1];
						controller.messageRights = result.content[2];
					}
				});

			personaService.getDashboardMenu().then(
				function success(response)
				{
					if (response.menus)
					{
						controller.dashboardMenu = response.menus.menu;
					}
				},
				function error(reason)
				{
					console.log(reason);
				});

			personaService.getNavBar().then(
				function success(response)
				{
					controller.currentProgram = response.currentProgram.program;

					if (response.programDomain.program instanceof Array)
					{
						controller.programDomain = response.programDomain.program;
					}
					else
					{
						controller.programDomain = [response.programDomain.program];
					}

					controller.unreadMessagesCount = response.unreadMessagesCount;
					controller.unreadPatientMessagesCount = response.unreadPatientMessagesCount;
					controller.getUnAckLabDocCount();
					controller.demographicSearchDropDownItems = response.menus.patientSearchMenu.items;
					controller.menuItems = response.menus.menu.items;
					controller.userMenuItems = response.menus.userMenu.items;
					controller.messengerMenu = response.menus.messengerMenu.items;
				},
				function error(reason)
				{
					console.log(reason);
				});

		};

		//=========================================================================
		// Watches
		//=========================================================================

		$scope.$watch(
			function()
			{
				return securityService.getUser();
			},
			function(newVal)
			{
				controller.me = newVal;
			},
			true);


		//=========================================================================
		// Methods
		//=========================================================================

		controller.getUnAckLabDocCount = function getUnAckLabDocCount()
		{
			inboxService.getUnAckLabDocCount().then(
				function success(response)
				{
					controller.unAckLabDocTotal = response;
				},
				function error(reason)
				{
					console.log(reason);
				});
		};

		controller.getNavBar = function getNavBar()
		{
			personaService.getNavBar().then(
				function success(response)
				{
					controller.currentProgram = response.currentProgram.program;
					if (response.programDomain.program instanceof Array)
					{
						controller.programDomain = response.programDomain.program;
					}
					else
					{
						controller.programDomain = [response.programDomain.program];
					}

					controller.unreadMessagesCount = response.unreadMessagesCount;
					controller.unreadPatientMessagesCount = response.unreadPatientMessagesCount;
					controller.demographicSearchDropDownItems = response.menus.patientSearchMenu.items;
					controller.menuItems = response.menus.menu.items;
					controller.userMenuItems = response.menus.userMenu.items;
					controller.messengerMenu = response.menus.messengerMenu.items;
				},
				function error(reason)
				{
					console.log(reason);
				});
		};

		controller.loadRecord = function loadRecord(demographicNo)
		{
			$state.go('record.details',
			{
				demographicNo: demographicNo,
				hideNote: true
			});
		};

		//to help ng-clicks on buttons
		controller.transition = function transition(item)
		{
			var newWindow;

			if (angular.isDefined(item) &&
				angular.isDefined(item.state) &&
				item.state !== null)
			{

				var url = "";
				var wname = "";

				if (item.label === "Inbox")
				{
					url = "../dms/inboxManage.do?method=prepareForIndexPage";
					wname = "inbox";
				}
				else if (item.label === "Billing")
				{
					url = "../billing/CA/" + encodeURIComponent(controller.billRegion) +
						"/billingReportCenter.jsp?displaymode=billreport";
					wname = "billing";
				}
				else if (item.label === "Admin")
				{
					url = "../administration/";
					wname = "admin";
				}
				else if (item.label === "Documents")
				{
					url = "../dms/documentReport.jsp?function=provider&functionid=" +
						encodeURIComponent(controller.me.providerNo);
					wname = "edocView";
				}
				else
				{
					$state.go(item.state);
				}

				if (url !== "" && wname !== "")
				{
					newWindow = window.open(
						url, wname, 'scrollbars=1,height=700,width=1000');
					if (window.focus)
					{
						newWindow.focus();
					}
				}
			}
			else if (angular.isDefined(item) &&
				angular.isDefined(item.url) &&
				item.url !== null)
			{

				if (item.label === "Schedule")
				{
					var qs = "";
					var path = $location.path();
					path = path.substring(1); //remove leading /
					var param = path.split("/");

					if (param.length === 1)
					{
						qs = "?module=" + encodeURIComponent(param[0]);
					}
					else if (param.length === 3)
					{
						qs = "?record=" + encodeURIComponent(param[1]) +
							"&module=" + encodeURIComponent(param[2]);
					}

					window.location = item.url + qs;
					return false;

				}
				else if (angular.isDefined(item) &&
					angular.isDefined(item.openNewWindow) &&
					item.openNewWindow)
				{
					newWindow = window.open(item.url);
					if (window.focus)
					{
						newWindow.focus();
					}
				}
				else
				{
					window.location = item.url;
				}
			}
		};

		controller.goHome = function goHome()
		{
			$state.go('dashboard');
		};

		controller.goToPatientSearch = function goToPatientSearch()
		{
			$state.go('search');
		};

		controller.openMessenger = function(item)
		{
			if (controller.me != null)
			{
				if (angular.isDefined(item) &&
					angular.isDefined(item.url) &&
					item.url === 'phr')
				{
					window.open('../phr/PhrMessage.do?method=viewMessages',
						'INDIVOMESSENGER' + encodeURIComponent(controller.me.providerNo),
						'height=700,width=1024,scrollbars=1');
				}
				else if (angular.isDefined(item) &&
					angular.isDefined(item.url) &&
					item.url === 'k2a')
				{
					var win;
					if (item.extra === "-")
					{
						//If user is not logged in
						win = window.open('../apps/oauth1.jsp?id=K2A',
							'appAuth', 'width=700,height=450,scrollbars=1');
						win.focus();
					}
					else
					{
						win = window.open('../apps/notifications.jsp',
							'appAuth', 'width=450,height=700,scrollbars=1');
						win.focus();
					}
				}
				else
				{
					// by default open classic messenger
					window.open('../oscarMessenger/DisplayMessages.do?providerNo=' +
						encodeURIComponent(controller.me.providerNo),
						'msgs', 'height=700,width=1024,scrollbars=1');
				}
			}
		};

		controller.newDemographic = function(size)
		{
			var modalInstance = $uibModal.open(
			{
				templateUrl: 'patient/newPatient.jsp',
				controller: 'Patient.NewPatientController',
				size: size
			});

			modalInstance.result.then(
				function success(selectedItem)
				{
					console.log(selectedItem);
					console.log('patient #: ', selectedItem.demographicNo);
					console.log($location.path());

					$location.path('/record/' +
						encodeURIComponent(selectedItem.demographicNo) +
						'/details');

					console.log($location.path());
				},
				function error()
				{
					console.log('Modal dismissed at: ' + new Date());
				});

			console.log($('#myModal'));
		};

		controller.isActive = function(item)
		{
			if (angular.isDefined(item) &&
				angular.isDefined(item.state) &&
				item.state !== null)
			{
				return $state.is(item.state);
			}
			return false;
		};

		controller.changeProgram = function(programId)
		{
			personaService.setCurrentProgram(programId).then(
				function success(response)
				{
					controller.getNavBar();
				},
				function error(reason)
				{
					console.log(reason);
				});
		};

		controller.switchToAdvancedView = function()
		{
			$rootScope.$apply(function()
			{
				$location.path("/search");
				$location.search('term', controller.quickSearchTerm);
			});
		};

		controller.setQuickSearchTerm = function(term)
		{
			controller.quickSearchTerm = term;
		};

		return controller;
	}
]);