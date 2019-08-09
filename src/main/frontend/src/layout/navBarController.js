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
	'$q',
	'$timeout',
	'$location',
	'$state',
	'$uibModal',
	'$interval',
	'securityService',
	'personaService',
	'billingService',
	'consultService',
	'inboxService',
	'messageService',
	'ticklerService',

	function(
		$rootScope,
		$scope,
		$q,
		$timeout,
		$location,
		$state,
		$uibModal,
		$interval,
		securityService,
		personaService,
		billingService,
		consultService,
		inboxService,
		messageService,
		ticklerService)
	{
		var controller = this;

		// Controller-level variables to contain intervals
		controller.updateInterval = undefined;
		controller.intervalLength = 60000 * 5;

		//=========================================================================
		// Initialization
		//=========================================================================

		controller.init = function init()
		{
			controller.activeConsultationTotal = 0;
			controller.ticklerTotal = 0;
			controller.unAckLabDocTotal = 0;
			controller.unreadMessageTotal = 0;
			controller.demographicSearch = null;

			billingService.getBillingRegion().then(
				function success(results)
				{
					controller.billRegion = results.message;
				},
				function error(errors)
				{
					console.log(errors);
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
				function success(results)
				{
					if (results.content !== null)
					{
						controller.searchRights = results.content[0];
						controller.newDemographicRights = results.content[1];
						controller.messageRights = results.content[2];
					}
				},
				function error(errors)
				{
					console.log(errors);
				});

			personaService.getDashboardMenu().then(
				function success(results)
				{
					if (results.menus)
					{
						controller.dashboardMenu = results.menus.menu;
					}
				},
				function error(errors)
				{
					console.log(errors);
				});

			personaService.getNavBar().then(
				function success(results)
				{
					controller.currentProgram = results.currentProgram.program;

					if (results.programDomain.program instanceof Array)
					{
						controller.programDomain = results.programDomain.program;
					}
					else
					{
						controller.programDomain = [results.programDomain.program];
					}

					controller.unreadMessagesCount = results.unreadMessagesCount;
					controller.unreadPatientMessagesCount = results.unreadPatientMessagesCount;
					controller.updateCounts();
					controller.demographicSearchDropDownItems = results.menus.patientSearchMenu.items;
					controller.menuItems = results.menus.menu.items;
					controller.userMenuItems = results.menus.userMenu.items;
					controller.messengerMenu = results.menus.messengerMenu.items;


					if (!angular.isDefined(controller.updateInterval))
					{
						controller.updateInterval = $interval(function()
						{
							controller.updateCounts();
						}, controller.intervalLength);
					}

				},
				function error(errors)
				{
					console.log(errors);
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

		$scope.$watch(function()
			{
				return controller.demographicSearch;
			},
			function(new_value)
			{
				if (new_value != null && !new_value.isTypeaheadSearchQuery)
				{
					// selection from the patient search typeahead changed
					if (new_value.moreResults)
					{
						// the 'more results' option was selected
						controller.goToPatientSearch(new_value.searchQuery);
					}
					else
					{
						// patient was selected
						controller.goToPatientRecord(new_value.demographicNo);
					}

					// clear the selection
					controller.demographicSearch = null;
				}
			}, true);

		// for intervals
		$scope.$on('$destroy', function()
		{
			$scope.cancelIntervals();
		});

		//=========================================================================
		// Methods
		//=========================================================================

		// Need to do this so that requests aren't going off in the background after leaving new UI
		controller.cancelIntervals = function cancelIntervals()
		{
			if (angular.isDefined(controller.updateInterval))
			{
				$interval.cancel(controller.updateInterval);
				controller.updateInterval = undefined;
			}
		};

		/**
		 * Wrapper for all of the functions that we want to periodically get updated counts for.
		 */
		controller.updateCounts = function updateCounts()
		{
			controller.getUnAckLabDocCount();
			controller.getUnreadMessageCount();
			controller.getOverdueTicklerCount();
			controller.getActiveConsultationCount();
		};

		/**
		 * Used to generically update count for various elements.
		 * @param item Item object with label we want to display an updated count for
		 * @return associated value the controller has stored, or 0 if we don't recognize item's label
		 */
		controller.getCountForLabel = function getCountForLabel(item)
		{
			if (item.label === "Inbox")
			{
				item.labelCount = controller.unAckLabDocTotal;
				return controller.unAckLabDocTotal;
			}
			else if (item.label === "Ticklers")
			{
				item.labelCount = controller.ticklerTotal;
				return controller.ticklerTotal;
			}
			else if (item.label === "Consultations")
			{
				item.labelCount = controller.activeConsultationTotal;
				return controller.activeConsultationTotal;
			}
			item.labelCount = 0;
			return 0;
		};

		controller.getUnAckLabDocCount = function getUnAckLabDocCount()
		{
			inboxService.getUnAckLabDocCount().then(
				function success(results)
				{
					controller.unAckLabDocTotal = results;
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		controller.getUnreadMessageCount = function getUnreadMessageCount()
		{
			messageService.getUnreadCount().then(
				function success(results)
				{
					controller.unreadMessageTotal = results;
				},
				function error(errors)
				{
					console.log(errors);
				});

		};

		controller.getOverdueTicklerCount = function getOverdueTicklerCount()
		{
			ticklerService.search(
				{
					status: 'A',
					creator: controller.me.providerNo,
					overdueOnly: 'property'
				}, 0, 6).then(
					function success(results)
					{
						controller.ticklerTotal = results.total;
					},
					function error(errors)
					{
						console.log(errors);
					}
				);
		};

		controller.getActiveConsultationCount = function getActiveConsultationCount()
		{
			consultService.getTotalRequests(
				{
					search: 'status=1'
				}).then(
					function success(results)
					{
						controller.activeConsultationTotal = results.data;
					},
					function error(errors)
					{
						console.log(errors);
					}
				);
		};

		controller.getNavBar = function getNavBar()
		{
			personaService.getNavBar().then(
				function success(results)
				{
					controller.currentProgram = results.currentProgram.program;
					if (results.programDomain.program instanceof Array)
					{
						controller.programDomain = results.programDomain.program;
					}
					else
					{
						controller.programDomain = [results.programDomain.program];
					}

					controller.unreadMessagesCount = results.unreadMessagesCount;
					controller.unreadPatientMessagesCount = results.unreadPatientMessagesCount;
					controller.demographicSearchDropDownItems = results.menus.patientSearchMenu.items;
					controller.menuItems = results.menus.menu.items;
					controller.userMenuItems = results.menus.userMenu.items;
					controller.messengerMenu = results.menus.messengerMenu.items;
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		// when patient typeahead search button is clicked
		controller.onPatientSearch = function onPatientSearch(search)
		{
			if (search === null)
			{
				controller.goToPatientSearch();

			}
			else if (search.isTypeaheadSearchQuery) // should only happen when search isTypeaheadSearchQuery
			{
				controller.goToPatientSearch(search.searchQuery);
			}

			// clear the selection
			controller.demographicSearch = null;
		};

		controller.goToPatientSearch = function goToPatientSearch(search)
		{
			$state.go('search',
			{
				term: search
			},
			{
				reload: 'search'
			});
		};

		controller.goToPatientRecord = function goToPatientRecord(demographicNo)
		{
			$state.go('record.details',
			{
				demographicNo: demographicNo,
				hideNote: true
			},
			{
				reload: 'record.details'
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
					url = "../billing.do?billRegion=CLINICAID&action=invoice_reports";
					wname = "billing";
				}
				else if (item.label === "Admin")
				{
					url = "../administration/";
					wname = "admin";
				}
				else if (item.label === "eDocs")
				{
					url = "../dms/documentReport.jsp?function=provider&functionid=" +
						encodeURIComponent(controller.me.providerNo);
					wname = "edocView";
				}
				else
				{
					$timeout(function()
					{
						$state.go(item.state);
					});
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

		controller.loadClassicUi = function()
		{
			window.location = "../provider/providercontrol.jsp";
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

		controller.openScratchpad = function ()
		{
			var win = window.open('../scratch/index.jsp',
				'scratch', 'height=700,width=1024,scrollbars=1');
			win.focus();
		};

		controller.newDemographic = function newDemographic(size)
		{
			var modalInstance = $uibModal.open(
			{
				templateUrl: 'src/patient/newPatient.jsp',
				controller: 'Patient.NewPatientController as newPatientCtrl',
				backdrop: 'static',
				size: size
			});

			modalInstance.result.then(
				function success(results)
				{
					console.log(results);
					console.log('patient #: ', results.demographicNo);
					console.log($location.path());

					$location.path('/record/' +
						encodeURIComponent(results.demographicNo) +
						'/details');

					console.log($location.path());
				},
				function error(errors)
				{
					console.log('Modal dismissed at: ' + new Date());
					console.log(errors);
				});

			console.log($('#myModal'));
		};

		controller.isActive = function(tab)
		{
			return ($state.current.name === tab.state);
		};

		controller.changeProgram = function changeProgram(programId)
		{
			personaService.setCurrentProgram(programId).then(
				function success(results)
				{
					controller.getNavBar();
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		// For some reason Angular does not allow for the evaluation of the inverse of custom filters, thus, we have the the following masterpiece
		// If inverse === false, return true if the given item is supposed to be shown outside the 'more' dropdown on the medium view
		// If inverse === true, return the inverse of the above statement,
		controller.navItemFilter = function navItemFilter(labelsToShow, inverse)
		{
			return function(item)
			{
				var filterValue = $.inArray(item.label, labelsToShow) !== -1;
				if (inverse === true)
				{
					return !filterValue;
				}

				return filterValue;
			};
		};
		controller.mediumNavItemFilter = function mediumNavItemFilter(inverse)
		{
			return controller.navItemFilter(['Dashboard', 'Schedule', 'Inbox', 'Consultations', 'Ticklers'], inverse);
		};
		controller.smallNavItemFilter = function smallNavItemFilter(inverse)
		{
			return controller.navItemFilter(['Dashboard'], inverse);
		};
		controller.mobileNavItemFilter = function mobileNavItemFilter(inverse)
		{
			return controller.navItemFilter([], inverse);
		};
	}
]);