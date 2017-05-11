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


oscarApp.controller('NavBarCtrl', [

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

		$scope.unAckLabDocTotal = 0;

		$scope.$watch(
			function()
			{
				return securityService.getUser();
			},
			function(newVal)
			{
				$scope.me = newVal;
			},
			true);


		billingService.getBillingRegion().then(
			function success(response)
			{
				$scope.billRegion = response.message;
			},
			function error(reason)
			{
				alert(reason);
			});

    securityService.hasRights({
    	items: [
    		{objectName:'_search',privilege:'r'},
    		{objectName:'_demographic',privilege:'w'},
    		{objectName:'_msg',privilege:'r'}
    	]
    }).then(
    	function success(result)
    	{
    		//console.log(result.toSource());
    		if(result.content != null)
    		{
    			$scope.searchRights = result.content[0];
    			$scope.newDemographicRights = result.content[1];
    			$scope.messageRights = result.content[2];
    		}
    	});

    personaService.getDashboardMenu().then(
    	function success(response)
    	{
    		if(response.menus)
    		{
    			$scope.dashboardMenu = response.menus.menu;
    		}
    	},
    	function error(reason)
    	{
    		alert(reason);
    	});

    personaService.getNavBar().then(
    	function success(response)
    	{
    		$scope.currentProgram = response.currentProgram.program;

    		if (response.programDomain.program instanceof Array)
    		{
    			$scope.programDomain = response.programDomain.program;
    		}
    		else
    		{
					var arr = new Array();
					arr[0] = response.programDomain.program;
					$scope.programDomain = arr;
				}

				$scope.unreadMessagesCount = response.unreadMessagesCount;
				$scope.unreadPatientMessagesCount = response.unreadPatientMessagesCount;
				$scope.getUnAckLabDocCount();
				$scope.demographicSearchDropDownItems = response.menus.patientSearchMenu.items;
				$scope.menuItems = response.menus.menu.items;
				//$scope.moreMenuItems = response.menus.moreMenu.items;
				$scope.userMenuItems = response.menus.userMenu.items;
				$scope.messengerMenu = response.menus.messengerMenu.items;
			},
			function error(reason)
			{
				alert(reason);
			});

		$scope.getUnAckLabDocCount = function()
		{
			inboxService.getUnAckLabDocCount().then(
				function success(response)
				{
	   			$scope.unAckLabDocTotal = response;
    		},
    		function error(reason)
    		{
    			alert(reason);
    		});
		};

		//reload the navbar at any time..not sure why i can't call this form the controller.
		$scope.getNavBar = function()
		{
    	personaService.getNavBar().then(
    		function success(response)
    		{
    			$scope.currentProgram = response.currentProgram.program;
					if (response.programDomain.program instanceof Array)
					{
						$scope.programDomain = response.programDomain.program;
					}
					else
					{
						var arr = new Array();
						arr[0] = response.programDomain.program;
						$scope.programDomain = arr;
					}

					$scope.unreadMessagesCount = response.unreadMessagesCount;
					$scope.unreadPatientMessagesCount = response.unreadPatientMessagesCount;

					$scope.demographicSearchDropDownItems = response.menus.patientSearchMenu.items;
					$scope.menuItems = response.menus.menu.items;
					//	$scope.moreMenuItems = response.menus.moreMenu.items;
					$scope.userMenuItems = response.menus.userMenu.items;
					$scope.messengerMenu = response.menus.messengerMenu.items;
    		},
    		function error(reason)
    		{
    			alert(reason);
    		});
		};

		$scope.loadRecord = function(demographicNo)
		{
			$state.go('record.details', {demographicNo:demographicNo, hideNote:true});
		};

		//to help ng-clicks on buttons
		$scope.transition = function (item)
		{
			if(angular.isDefined(item) &&
				angular.isDefined(item.state) &&
				item.state !== null)
			{

				url = "";
				wname="";

				if(item.label=="Inbox")
				{
					url = "../dms/inboxManage.do?method=prepareForIndexPage";
					wname="inbox";
				}
				else if(item.label=="Billing")
				{
					url = "../billing/CA/" + $scope.billRegion + "/billingReportCenter.jsp?displaymode=billreport";
					wname="billing";
				}
				else if(item.label=="Admin")
				{
					url = "../administration/";
					wname="admin";
				}
				else if(item.label=="Documents")
				{
					url = "../dms/documentReport.jsp?function=provider&functionid="+$scope.me.providerNo;
					wname="edocView";
				}
				else
				{
					$state.go(item.state);
				}

				if(url != "" && wname != "")
				{
			 		newwindow = window.open(
			 			url, wname, 'scrollbars=1,height=700,width=1000');
			 		if (window.focus)
			 		{
				 		newwindow.focus();
				 	}
				}
			}
			else if(angular.isDefined(item) &&
				angular.isDefined(item.url) &&
				item.url !== null)
			{

				if(item.label=="Schedule")
				{
					qs = "";
					path = $location.path();
					path = path.substring(1); //remove leading /
					param = path.split("/");

					if(param.length==1)
					{
						qs = "?module=" + param[0];
					}
					else if(param.length==3)
					{
						qs = "?record=" + param[1] + "&module=" + param[2];
					}

					window.location = item.url + qs;
					return false;

				}
				else if(item.openNewWindow)
				{
					newwindow = window.open(item.url);
					if (window.focus)
					{
						newwindow.focus();
					}
				}
				else if(angular.isDefinded(item) &&
					angular.isDefined(item.openNewWindow) &&
					item.openNewWindow)
				{
			 		newwindow = window.open(url);
			 		if (window.focus)
			 		{
			 			newwindow.focus();
			 		}
			 	}
			 	else
			 	{
					window.location = item.url;
				}
			}
		};

		$scope.goHome = function goHome()
		{
			$state.go('dashboard');
		};

		$scope.goToPatientSearch = function goToPatientSearch()
		{
			$state.go('search');
		};

		$scope.openMessenger = function(item)
		{
			if($scope.me != null)
			{
				if(angular.isDefined(item) &&
					angular.isDefined(item.url) &&
					item.url == 'phr')
				{
					window.open('../phr/PhrMessage.do?method=viewMessages',
						'INDIVOMESSENGER' + $scope.me.providerNo,
						'height=700,width=1024,scrollbars=1');
				}
				else if(angular.isDefined(item) &&
					angular.isDefined(item.url) &&
					item.url == 'k2a')
				{
					if(item.extra === "-")
					{ //If user is not logged in
						var win = window.open('../apps/oauth1.jsp?id=K2A',
							'appAuth', 'width=700,height=450,scrollbars=1');
						win.focus();
					}
					else
					{
						var win = window.open('../apps/notifications.jsp',
							'appAuth', 'width=450,height=700,scrollbars=1');
						win.focus();
					}
				}
				else
				{ // by default open classic messenger
					window.open('../oscarMessenger/DisplayMessages.do?providerNo=' +
						encodeURIComponent($scope.me.providerNo),
						'msgs','height=700,width=1024,scrollbars=1');
				}
			}
		};

		$scope.newDemographic = function(size)
		{
			var modalInstance = $uibModal.open({
				templateUrl: 'patient/newPatient.jsp',
	      controller: 'NewPatientCtrl',
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

		$scope.isActive = function (item)
		{
			if(angular.isDefined(item) &&
				angular.isDefined(item.state) &&
				item.state !== null)
			{
				return $state.is(item.state);
			}
			return false;
		};

		$scope.changeProgram = function(programId)
		{
  		personaService.setCurrentProgram(programId).then(
  			function success(response)
  			{
  				$scope.getNavBar();
  			},
  			function error(reason)
  			{
  				alert(reason);
  			});
  	};

  	$scope.switchToAdvancedView = function()
  	{
  		$rootScope.$apply(function() {
  			$location.path("/search");
  			$location.search('term',$scope.quickSearchTerm);
  		});
  	};

		$scope.setQuickSearchTerm = function(term)
		{
			$scope.quickSearchTerm = term;
		};

		$scope.showPatientList = function()
		{
			$scope.$emit('configureShowPatientList', true);
		};
	}
]);

