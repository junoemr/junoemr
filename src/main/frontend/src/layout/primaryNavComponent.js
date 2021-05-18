import {BILLING_REGION} from "../billing/billingConstants";

angular.module('Layout').component("primaryNavigation", {
	bindings: {},
	templateUrl: "src/layout/primaryNav.jsp",
	controller: [
		"$rootScope",
		"$scope",
		"$q",
		"$timeout",
		"$location",
		"$state",
		"$uibModal",
		"$interval",
		"securityService",
		"personaService",
		"billingService",
		"consultService",
		"inboxService",
		"messageService",
		"providerService",
		"ticklerService",
		function ($rootScope,
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
		          providerService,
		          ticklerService)
	{
		var ctrl = this;

		ctrl.me = null;

		// Controller-level variables to contain intervals
		ctrl.updateInterval = undefined;
		ctrl.messageInterval = undefined;
		// Interval takes update times in ms, so 60s * 1000 * num_minutes
		ctrl.intervalLengthOneMinute = 60000;
		ctrl.intervalLengthFiveMinutes = 60000 * 5;

		//=========================================================================
		// Initialization
		//=========================================================================

		ctrl.init = function init()
		{
			ctrl.activeConsultationTotal = 0;
			ctrl.ticklerTotal = 0;
			ctrl.unAckLabDocTotal = 0;
			ctrl.unclaimedCount = 0;
			ctrl.unreadMessageTotal = 0;
			ctrl.demographicSearch = null;
			ctrl.consultationTeamWarning = "";
			// measured in months
			ctrl.consultationLookbackPeriod = 1;

			billingService.getBillingRegion().then(
				function success(results)
				{
					ctrl.billRegion = results.message;
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
						ctrl.searchRights = results.content[0];
						ctrl.newDemographicRights = results.content[1];
						ctrl.messageRights = results.content[2];
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
						ctrl.dashboardMenu = results.menus.menu;
					}
				},
				function error(errors)
				{
					console.log(errors);
				});

			providerService.getSettings().then(
				function success(results)
				{
					if (results.consultationTimePeriodWarning > 0)
					{
						ctrl.consultationLookbackPeriod = results.consultationTimePeriodWarning;
					}

					// If we get any result back that isn't -1, need to filter consultation count by the given team
					if (results.consultationTeamWarning !== "-1")
					{
						ctrl.consultationTeamWarning = results.consultationTeamWarning;
					}
				},
				function error(errors)
				{
					console.log("Error: ", errors);
				}
			);

			personaService.getNavBar().then(
				function success(results)
				{
					ctrl.currentProgram = results.currentProgram.program;

					if (results.programDomain.program instanceof Array)
					{
						ctrl.programDomain = results.programDomain.program;
					}
					else
					{
						ctrl.programDomain = [results.programDomain.program];
					}

					ctrl.unreadMessagesCount = results.unreadMessagesCount;
					ctrl.unreadPatientMessagesCount = results.unreadPatientMessagesCount;
					ctrl.updateCounts();
					ctrl.getUnreadMessageCount();
					ctrl.demographicSearchDropDownItems = results.menus.patientSearchMenu.items;
					ctrl.menuItems = results.menus.menu.items;
					ctrl.userMenuItems = results.menus.userMenu.items;
					ctrl.messengerMenu = results.menus.messengerMenu.items;


					if (!angular.isDefined(ctrl.updateInterval))
					{
						ctrl.updateInterval = $interval(function()
						{
							ctrl.updateCounts();
						}, ctrl.intervalLengthFiveMinutes);
					}

					// Separated into its own interval so that it can be updated more frequently
					if (!angular.isDefined(ctrl.messageInterval))
					{
						ctrl.messageInterval = $interval(function()
						{
							ctrl.getUnreadMessageCount();
						}, ctrl.intervalLengthOneMinute);
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
				ctrl.me = newVal;
			},
			true);

		$scope.$watch(function()
			{
				return ctrl.demographicSearch;
			},
			function(new_value)
			{
				if (new_value != null && !new_value.isTypeaheadSearchQuery)
				{
					// selection from the patient search typeahead changed
					if (new_value.moreResults)
					{
						// the 'more results' option was selected
						ctrl.goToPatientSearch(new_value.searchQuery);
					}
					else
					{
						// patient was selected
						ctrl.goToPatientRecord(new_value.demographicNo);
					}

					// clear the selection
					ctrl.demographicSearch = null;
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
		ctrl.cancelIntervals = function cancelIntervals()
		{
			if (angular.isDefined(ctrl.updateInterval))
			{
				$interval.cancel(ctrl.updateInterval);
				ctrl.updateInterval = undefined;
			}

			if (angular.isDefined(ctrl.messageInterval))
			{
				$interval.cancel(ctrl.messageInterval);
				ctrl.messageInterval = undefined;
			}
		};

		/**
		 * Wrapper for all of the functions that we want to periodically get updated counts for.
		 */
		ctrl.updateCounts = function updateCounts()
		{
			ctrl.getUnAckLabDocCount();
			ctrl.getOverdueTicklerCount();
			ctrl.getActiveConsultationCount();
			ctrl.getUnclaimedInboxCount();
		};

		/**
		 * Used to generically update count for various elements.
		 * @param item Item object with label we want to display an updated count for
		 * @return associated value the controller has stored, or 0 if we don't recognize item's label
		 */
		ctrl.getCountForLabel = function getCountForLabel(item)
		{
			item.labelCount = 0;
			if (item.label === "Inbox")
			{
				item.labelCount = ctrl.unAckLabDocTotal;
			}
			else if (item.label === "Ticklers")
			{
				item.labelCount = ctrl.ticklerTotal;
			}
			else if (item.label === "Consultations")
			{
				item.labelCount = ctrl.activeConsultationTotal;
			}
			return item.labelCount;
		};

		ctrl.getUnAckLabDocCount = function getUnAckLabDocCount()
		{
			inboxService.getUnAckLabDocCount().then(
				function success(results)
				{
					ctrl.unAckLabDocTotal = results;
				},
				function error(errors)
				{
					console.log(errors);
				});
		};
		ctrl.getUnclaimedInboxCount = function()
		{

			inboxService.getInboxCountByStatus(0,"N").then(
				function success(results)
				{
					ctrl.unclaimedCount = results;
				},
				function error(errors)
				{
					console.log(errors);
				}
			);
		};

		ctrl.getUnreadMessageCount = function getUnreadMessageCount()
		{
			messageService.getUnreadCount().then(
				function success(results)
				{
					ctrl.unreadMessageTotal = results;
				},
				function error(errors)
				{
					console.log(errors);
				});

		};

		ctrl.getOverdueTicklerCount = function getOverdueTicklerCount()
		{
			ticklerService.search(
				{
					status: 'A',
					assignee: ctrl.me.providerNo,
					overdueOnly: 'property'
				}, 0, 6).then(
				function success(results)
				{
					ctrl.ticklerTotal = results.total;
				},
				function error(errors)
				{
					console.log(errors);
				}
			);
		};

		ctrl.getActiveConsultationCount = function getActiveConsultationCount()
		{
			// Any consultations that should have ended after this point but haven't need to be alerted for
			var endDate = moment().subtract(ctrl.consultationLookbackPeriod, "months").toISOString();

			consultService.getTotalRequests(
				{
					invertStatus: true,
					referralEndDate: endDate,
					status: '4',
					team: ctrl.consultationTeamWarning
				}).then(
				function success(results)
				{
					ctrl.activeConsultationTotal = results.data;
				},
				function error(errors)
				{
					console.log(errors);
				}
			);
		};

		ctrl.getNavBar = function getNavBar()
		{
			personaService.getNavBar().then(
				function success(results)
				{
					ctrl.currentProgram = results.currentProgram.program;
					if (results.programDomain.program instanceof Array)
					{
						ctrl.programDomain = results.programDomain.program;
					}
					else
					{
						ctrl.programDomain = [results.programDomain.program];
					}

					ctrl.unreadMessagesCount = results.unreadMessagesCount;
					ctrl.unreadPatientMessagesCount = results.unreadPatientMessagesCount;
					ctrl.demographicSearchDropDownItems = results.menus.patientSearchMenu.items;
					ctrl.menuItems = results.menus.menu.items;
					ctrl.userMenuItems = results.menus.userMenu.items;
					ctrl.messengerMenu = results.menus.messengerMenu.items;
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		// when patient typeahead search button is clicked
		ctrl.onPatientSearch = function onPatientSearch(search)
		{
			if (search === null)
			{
				ctrl.goToPatientSearch();

			}
			else if (search.isTypeaheadSearchQuery) // should only happen when search isTypeaheadSearchQuery
			{
				ctrl.goToPatientSearch(search.searchQuery);
			}

			// clear the selection
			ctrl.demographicSearch = null;
		};

		ctrl.goToPatientSearch = function goToPatientSearch(search)
		{
			$state.go('search',
				{
					term: search
				},
				{
					reload: 'search'
				});
		};

		ctrl.goToPatientRecord = function goToPatientRecord(demographicNo)
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
		ctrl.transition = function transition(item, extraParams)
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
					switch(ctrl.billRegion)
					{
						case BILLING_REGION.CLINICAID:
							url = "../billing.do?billRegion=CLINICAID&action=invoice_reports";
							break;
						case BILLING_REGION.BC:
							url = "../billing/CA/BC/billStatus.jsp";
							break;
						case BILLING_REGION.ON:
							url = "../billing/CA/ON/billStatus.jsp";
						default:"../billing.do?billRegion=CLINICAID&action=invoice_reports";
					}
					wname = "billing";
				}
				else if (item.label === "eDocs")
				{
					url = "../dms/documentReport.jsp?function=provider&functionid=" +
						encodeURIComponent(ctrl.me.providerNo);
					wname = "edocView";
				}
				else
				{
					$timeout(function()
					{
						$state.go(item.state[0]);
					});
				}

				if(angular.isDefined(extraParams))
				{
					url += extraParams;
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
			else if (angular.isDefined(item) && angular.isDefined(item.url) &&
					item.url !== null && item.openNewTab)
			{
				window.open(item.url, "_blank");
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

		ctrl.loadClassicUi = function()
		{
			window.location = "../provider/providercontrol.jsp";
		};

		ctrl.openMessenger = function(item)
		{
			if (ctrl.me != null)
			{
				if (angular.isDefined(item) &&
					angular.isDefined(item.url) &&
					item.url === 'phr')
				{
					window.open('../phr/PhrMessage.do?method=viewMessages',
						'INDIVOMESSENGER' + encodeURIComponent(ctrl.me.providerNo),
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
						encodeURIComponent(ctrl.me.providerNo),
						'msgs', 'height=700,width=1024,scrollbars=1');
				}
			}
		};

		ctrl.openScratchpad = function ()
		{
			var win = window.open('../scratch/index.jsp',
				'scratch', 'height=700,width=1024,scrollbars=1');
			win.focus();
		};

		ctrl.newDemographic = function newDemographic(size)
		{
			var modalInstance = $uibModal.open(
				{
					component: 'addDemographicModal',
					backdrop: 'static',
					windowClass: "juno-modal",
				});

			modalInstance.result.then(
				function success(results)
				{
					$location.path('/record/' +
						encodeURIComponent(results.demographicNo) +
						'/details');
				},
				function error(errors)
				{
					console.log('Modal dismissed at: ' + new Date());
					console.log(errors);
				});

		};

		ctrl.isActive = function(tab)
		{
			return tab.state != null ? tab.state.includes($state.current.name) : false;
		};

		ctrl.changeProgram = function changeProgram(programId)
		{
			personaService.setCurrentProgram(programId).then(
				function success(results)
				{
					ctrl.getNavBar();
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		// For some reason Angular does not allow for the evaluation of the inverse of custom filters, thus, we have the the following masterpiece
		// If inverse === false, return true if the given item is supposed to be shown outside the 'more' dropdown on the medium view
		// If inverse === true, return the inverse of the above statement,
		ctrl.navItemFilter = function navItemFilter(labelsToShow, inverse)
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
		ctrl.mediumNavItemFilter = function mediumNavItemFilter(inverse)
		{
			return ctrl.navItemFilter(['Dashboard', 'Schedule', 'Inbox', 'Consultations', 'Ticklers'], inverse);
		};
		ctrl.smallNavItemFilter = function smallNavItemFilter(inverse)
		{
			return ctrl.navItemFilter(['Dashboard'], inverse);
		};
		ctrl.mobileNavItemFilter = function mobileNavItemFilter(inverse)
		{
			return ctrl.navItemFilter([], inverse);
		};
	}]
});