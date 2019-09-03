
require('jquery');
require('jquery-ui/ui/widgets/draggable');
require('jquery-ui/themes/base/draggable.css');
require('jquery-ui/ui/widgets/resizable');
require('jquery-ui/themes/base/resizable.css');
require('moment');
require('angular');
require('angular-ui-bootstrap');
require('angular-ui-router');
require('angular-ui-router/release/stateEvents');
require('fullcalendar-scheduler');
require('fullcalendar/dist/fullcalendar.min.css');
require('fullcalendar-scheduler/dist/scheduler.min.css');
require('ng-table/bundles/ng-table');
require('ng-table/bundles/ng-table.css');
require('angular-resource');
require('ng-infinite-scroll');
require('angular-loading-bar');
require('angular-loading-bar/build/loading-bar.min.css');
require('bootstrap-datepicker');
require('bootstrap-datepicker/dist/css/bootstrap-datepicker.css');
require('bootstrap-timepicker');
require('bootstrap-sass');
require('ngstorage');
require('pym.js');
require('./scss/juno.scss');
require('font-awesome/css/font-awesome.min.css');


var oscarApp = angular.module('oscarProviderViewModule', [
	'ui.router',
	'infinite-scroll',
	'ui.router.state.events',
	'ngResource',
	'ui.bootstrap',
	'ui.calendar',
	'angular-loading-bar',
	'ngTable',
	'ngStorage',
	'Common',
	'Common.Services',
	'Common.Filters',
	'Common.Directives',
	'Common.Components',
	'Common.Util',
	'Layout',
	'Tickler',
	'Record',
	'Record.Summary',
	'Record.Tracker',
	'Record.Details',
	'Record.PHR',
	'Record.Forms',
	'Schedule',
	'Settings',
	'Report',
	'Patient',
	'Patient.Search',
	'Inbox',
	'Help',
	'Document',
	'Dashboard',
	'Consults',
	'Admin',
	'Admin.Integration',
	'Admin.Integration.Know2act',
	'Admin.Integration.Fax'
]);

oscarApp.config(['$stateProvider', '$urlRouterProvider', '$httpProvider', function($stateProvider, $urlRouterProvider, $httpProvider)
{
	//
	// For any unmatched url, redirect to /state1
	$urlRouterProvider.otherwise("/dashboard");
	//
	// Now set up the states
	$stateProvider
		.state('dashboardManager',
		{
			url: '/dashboard/admin',
			templateUrl: 'src/dashboard/admin/DashboardManager.jsp',
			controller: 'dashboardManagerController'
		})
		.state('dashboard',
		{
			url: '/dashboard',
			templateUrl: 'src/dashboard/dashboard.jsp',
			controller: 'Dashboard.DashboardController'
		})
		.state('inbox',
		{
			url: '/inbox',
			templateUrl: 'src/inbox/inbox_popup.jsp',
			// templateUrl: 'src/inbox/inbox.jsp',
			controller: 'Inbox.InboxController'
		})
		.state('consultRequests',
		{
			url: '/consults',
			templateUrl: 'src/consults/consultRequestList.jsp',
			controller: 'Consults.ConsultRequestListController as consultRequestListCtrl'
		})
		.state('consultResponses',
		{
			url: '/consultResponses',
			templateUrl: 'src/consults/consultResponseList.jsp',
			controller: 'Consults.ConsultResponseListController as consultResponseListCtrl'
		})
		.state('billing',
		{
			url: '/billing',
			templateUrl: 'src/billing/billing_popup.jsp',
			controller: 'BillingCtrl'
		})
		.state('schedule',
		{
			url: '/schedule',
			templateUrl: 'src/schedule/view.jsp',
			controller: 'Schedule.ScheduleController as scheduleController',
			resolve:
			{
				loadedSettings: ['providerService', function (providerService)
				{
					return providerService.getSettings();
				}],
			}
		})
		.state('admin',
		{
			url: '/admin',
			templateUrl: 'src/admin/admin_popup.jsp',
			controller: 'AdminCtrl'
		})
		.state('ticklers',
		{
			url: '/ticklers',
			templateUrl: 'src/tickler/ticklerList.jsp',
			controller: 'Tickler.TicklerListController as ticklerListCtrl',
			resolve:
			{
				providers: ['providerService', function(providerService)
				{
					return providerService.searchProviders(
					{
						active: true
					});
				}],
			}
		})
		.state('search',
		{
			url: '/search',
			templateUrl: 'src/patient/search/patientSearch.jsp',
			controller: 'Patient.Search.PatientSearchController as patientSearchCtrl',
			params:
			{
				term: null
			}
		})
		.state('reports',
		{
			url: '/reports',
			templateUrl: 'src/report/reports.jsp',
			//templateUrl: 'src/report/reports_classic.jsp',
			controller: 'Report.ReportsController as reportsCtrl',
		})
		.state('documents',
		{
			url: '/documents',
			templateUrl: 'src/document/documents_classic.jsp',
			controller: 'Document.DocumentsController'
		})
		.state('settings',
		{
			url: '/settings',
			templateUrl: 'src/settings/settings.jsp',
			controller: 'Settings.SettingsController as settingsCtrl',
			resolve:
			{
				user: ['providerService', function(providerService)
				{
					return providerService.getMe();
				}],
				billingServiceTypes: ['billingService', function(billingService)
				{
					return billingService.getUniqueServiceTypes();
				}],
				providerList: ['providerService', function(providerService)
				{
					return providerService.searchProviders(
					{
						'active': true
					});
				}],
				loadedSettings: ['providerService', function(providerService)
				{
					return providerService.getSettings();
				}],
				encounterForms: ['formService', function(formService)
				{
					return formService.getAllEncounterForms();
				}],
				eforms: ['formService', function(formService)
				{
					return formService.getAllEForms();
				}],
				teams: ['providerService', function(providerService)
				{
					return providerService.getActiveTeams();
				}],
				groupNames: ['formService', function(formService)
				{
					return formService.getGroupNames();
				}],
				loadedApps: ['appService', function(appService)
				{
					return appService.getApps();
				}]
			}
		})
		.state('settings.persona',
			{
				url: '/persona',
				templateUrl: 'src/settings/settings.jsp',
				controller: 'Settings.SettingsController as settingsCtrl',
				data: {
					tab: 'persona'
				}
			})
		.state('settings.general',
			{
				url: '/general',
				templateUrl: 'src/settings/settings.jsp',
				controller: 'Settings.SettingsController as settingsCtrl',
				data: {
					tab: 'general'
				}
			})
		.state('settings.schedule',
			{
				url: '/schedule',
				templateUrl: 'src/settings/settings.jsp',
				controller: 'Settings.SettingsController as settingsCtrl',
				data: {
					tab: 'schedule'
				}
			})
		.state('settings.billing',
			{
				url: '/billing',
				templateUrl: 'src/settings/settings.jsp',
				controller: 'Settings.SettingsController as settingsCtrl',
				data: {
					tab: 'billing'
				}
			})
		.state('settings.rx',
			{
				url: '/rx',
				templateUrl: 'src/settings/settings.jsp',
				controller: 'Settings.SettingsController as settingsCtrl',
				data: {
					tab: 'rx'
				}
			})
		.state('settings.masterdemo',
			{
				url: '/masterdemo',
				templateUrl: 'src/settings/settings.jsp',
				controller: 'Settings.SettingsController as settingsCtrl',
				data: {
					tab: 'masterdemo'
				}
			})
		.state('settings.consults',
			{
				url: '/consults',
				templateUrl: 'src/settings/settings.jsp',
				controller: 'Settings.SettingsController as settingsCtrl',
				data: {
					tab: 'consults'
				}
			})
		.state('settings.documents',
			{
				url: '/documents',
				templateUrl: 'src/settings/settings.jsp',
				controller: 'Settings.SettingsController as settingsCtrl',
				data: {
					tab: 'documents'
				}
			})
		.state('settings.summary',
			{
				url: '/summary',
				templateUrl: 'src/settings/settings.jsp',
				controller: 'Settings.SettingsController as settingsCtrl',
				data: {
					tab: 'summary'
				}
			})
		.state('settings.eforms',
			{
				url: '/eforms',
				templateUrl: 'src/settings/settings.jsp',
				controller: 'Settings.SettingsController as settingsCtrl',
				data: {
					tab: 'eforms'
				}
			})
		.state('settings.inbox',
			{
				url: '/inbox',
				templateUrl: 'src/settings/settings.jsp',
				controller: 'Settings.SettingsController as settingsCtrl',
				data: {
					tab: 'inbox'
				}
			})
		.state('settings.programs',
			{
				url: '/programs',
				templateUrl: 'src/settings/settings.jsp',
				controller: 'Settings.SettingsController as settingsCtrl',
				data: {
					tab: 'programs'
				}
			})
		.state('settings.integration',
			{
				url: '/integration',
				templateUrl: 'src/settings/settings.jsp',
				controller: 'Settings.SettingsController as settingsCtrl',
				data: {
					tab: 'integration'
				}
			})
		.state('support',
		{
			url: '/support',
			templateUrl: 'src/help/support.jsp',
			controller: 'Help.SupportController'
		})
		.state('help',
		{
			url: '/help',
			templateUrl: 'src/help/help.jsp',
			controller: 'Help.HelpController'
		})
		.state('record',
		{
			url: '/record/:demographicNo',
			templateUrl: 'src/record/record.jsp',
			controller: 'Record.RecordController as recordCtrl',
			resolve:
			{
				demo: ['$stateParams', 'demographicService', function($stateParams, demographicService)
				{
					return demographicService.getDemographic($stateParams.demographicNo);
				}],
				user: ['providerService', function(providerService)
				{
					return providerService.getMe();
				}],
				properties: ['uxService', function(uxService)
				{
					return uxService.getDisplayProperties();
				}]
			}
		})
		.state('record.details',
		{
			url: '/details',
			templateUrl: 'src/record/details/details.jsp',
			controller: 'Record.Details.DetailsController as detailsCtrl'
		})
		.state('record.summary',
		{
			url: '/summary?appointmentNo&encType',
			templateUrl: 'src/record/summary/summary.jsp',
			controller: 'Record.Summary.SummaryController as summaryCtrl'
		})
		.state('record.forms',
		{
			url: '/forms',
			templateUrl: 'src/record/forms/forms.jsp',
			controller: 'Record.Forms.FormController as formCtrl',
			params: {
				formListId: 0
			}
		})
		.state('record.forms.view',
		{
			url: '/view/:type/:id?name',
			templateUrl: 'src/record/forms/forms.jsp',
			params: { name: { dynamic: true } },
			controller: 'Record.Forms.FormController as formCtrl'
		})
		.state('record.forms.add',
		{
			url: '/add/:type/:id',
			templateUrl: 'src/record/forms/forms.jsp',
			controller: 'Record.Forms.FormController as formCtrl'
		})
		.state('record.consultRequests',
		{
			url: '/consults',
			templateUrl: 'src/consults/consultRequestList.jsp',
			controller: 'Consults.ConsultRequestListController as consultRequestListCtrl'
		})
		.state('record.consultResponses',
		{
			url: '/consultResponses',
			templateUrl: 'src/consults/consultResponseList.jsp',
			controller: 'Consults.ConsultResponseListController as consultResponseListCtrl'
		})
		.state('record.consultRequest',
		{
			url: '/consult/:requestId',
			templateUrl: 'src/consults/consultRequest.jsp',
			controller: 'Consults.ConsultRequestController as consultRequestCtrl',
			resolve:
			{
				consult: ['$stateParams', 'consultService', function($stateParams, consultService)
				{
					if($stateParams.requestId === "new") {
						return consultService.getNewRequest($stateParams.demographicNo);
					}
					return consultService.getRequest($stateParams.requestId);
				}],
				user: ['providerService', function(providerService)
				{
					return providerService.getMe();
				}]
			}
		})
		.state('record.consultResponse',
		{
			url: '/consultResponse/:responseId',
			templateUrl: 'src/consults/consultResponse.jsp',
			controller: 'Consults.ConsultResponseController as consultResponseCtrl',
			resolve:
			{
				consult: ['$stateParams', 'consultService', function($stateParams, consultService)
				{
					return consultService.getResponse($stateParams.responseId, $stateParams.demographicNo);
				}],
				user: ['providerService', function(providerService)
				{
					return providerService.getMe();
				}]
			}
		})
		.state('record.tickler',
		{
			url: '/tickler',
			templateUrl: 'src/tickler/ticklerList.jsp',
			controller: 'Tickler.TicklerListController as ticklerListCtrl',
			resolve:
			{
				providers: ['providerService', function(providerService)
				{
					return providerService.searchProviders(
					{
						active: true
					});
				}]
			}
		}).state('record.tracker',
		{
			url: '/tracker',
			templateUrl: 'src/record/tracker/tracker.jsp',
			controller: 'Record.Tracker.TrackerController as trackerCtrl'
		})
		.state('record.phr',
		{
			url: '/phr',
			templateUrl: 'src/record/phr/phr.jsp',
			controller: 'Record.PHR.PHRController as phrCtrl'
		})
		// .state('admin.integration',
		.state('k2aConfig',
			{
			// url: '/admin/integration',
			url:'/k2aConfig',
			templateUrl: 'src/admin/integration/know2act/Know2actConfiguration.jsp',
			controller: 'Admin.Integration.Know2act.k2aConfigController as k2aConfigCtrl'
		})
		.state('k2aTemplate',
			{
				// url: '/admin/integration',
				url:'/k2aTemplate',
				templateUrl: 'src/admin/integration/know2act/Know2actTemplate.jsp',
				controller: 'Admin.Integration.Know2act.k2aTemplateController as k2aTemplateCtrl'
			})
		.state('k2aNotification',
			{
				url: '/k2aNotification',
				templateUrl: 'src/admin/integration/know2act/Know2actNotifications.jsp',
				controller: 'Admin.Integration.Know2act.k2aNotificationController as k2aNoteCtrl'
			})
		.state('faxConfig',
			{
				url: '/faxConfig',
				templateUrl: 'src/admin/integration/fax/faxConfiguration.jsp',
				controller: 'Admin.Integration.Fax.FaxConfigurationController as faxController'
			})
		.state('faxSendReceive',
			{
				url: '/faxSendReceive',
				templateUrl: 'src/admin/integration/fax/faxSendReceive.jsp',
				controller: 'Admin.Integration.Fax.FaxSendReceiveController as faxSendReceiveController'
			});

	// redirect to login page on 401 error.
	$httpProvider.interceptors.push(['$q', function($q) {
		return {
			'responseError': function(rejection) {
				if (rejection.status === 401 && rejection.data === "<error>Not authorized</error>")
				{ // reload will cause server to redirect
					location.reload();
				}
				return $q.reject(rejection);
			}
		};
	}]);
}]);


// For debugging purposes
/*
oscarApp.run( function($rootScope, $location) {

$rootScope.$on('$stateChangeStart',function(event, toState, toParams, fromState, fromParams){
	  console.log('$stateChangeStart to '+toState.to+'- fired when the transition begins. toState,toParams : \n',toState, toParams);
	});
	$rootScope.$on('$stateChangeError',function(event, toState, toParams, fromState, fromParams){
	  console.log('$stateChangeError - fired when an error occurs during transition.');
	  console.log(arguments);
	});
	$rootScope.$on('$stateChangeSuccess',function(event, toState, toParams, fromState, fromParams){
	  console.log('$stateChangeSuccess to '+toState.name+'- fired once the state transition is complete.');
	});
	// $rootScope.$on('$viewContentLoading',function(event, viewConfig){
	//   // runs on individual scopes, so putting it in "run" doesn't work.
	//   console.log('$viewContentLoading - view begins loading - dom not rendered',viewConfig);
	// });
	$rootScope.$on('$viewContentLoaded',function(event){
	  console.log('$viewContentLoaded - fired after dom rendered',event);
	});
	$rootScope.$on('$stateNotFound',function(event, unfoundState, fromState, fromParams){
	  console.log('$stateNotFound '+unfoundState.to+'  - fired when a state cannot be found by its name.');
	  console.log(unfoundState, fromState, fromParams);
	});

});
*/



/*

 user: function($stateParams, UserService) {
	  return UserService.find($stateParams.id);
	},
oscarApp.config(['$routeProvider',
					function($routeProvider) {

						when('/settings', {
							templateUrl: 'src/partials/settings-classic.jsp',
							controller: 'Settings.SettingsController'
						}).
						when('/support', {
							templateUrl: 'src/partials/support.jsp',
							controller: 'Help.SupportController'
						}).
						when('/help', {
							templateUrl: 'src/partials/help.jsp',
							controller: 'Help.HelpController'
						}).

						when('/messenger', {
							templateUrl: 'src/partials/messenger.jsp',
							controller: 'MessengerCtrl'
						}).
						when('/eform', {
							templateUrl: 'src/partials/eform.jsp',
							controller: 'EformFullCtrl'
						}).
						when('/eform2', {
							templateUrl: 'src/partials/eform2.jsp',
							controller: 'EformFull2Ctrl'
						}).

					}
]);
*/



//for dev - just to keep the cache clear
/*
oscarApp.run(function($rootScope, $templateCache) {
	$rootScope.$on('$viewContentLoaded', function() {
		$templateCache.removeAll();
		console.log("onclick of tab");
	});
});


//reset the left nav back
oscarApp.run( function($rootScope, $location) {
	$rootScope.$on( "$routeChangeStart", function(event, next, current) {
		$("#left_pane").addClass("col-md-2");
		$("#left_pane").show();
		$("#right_pane").removeClass("col-md-12");
		$("#right_pane").addClass("col-md-10");
	});
});

*/

//this is for the patient list control. Tells us which template to load
/*angular.module('oscarProviderViewModule').factory('Navigation', function($rootScope)
{
	return {
		location: '',

		load: function(msg)
		{
			this.location = msg;
		}
	};
});*/
angular.module('oscarProviderViewModule').factory('Navigation', [
	function()
	{
		return {
			location: '',

			load: function(msg)
			{
				this.location = msg;
			}
		};
	}
]);

/*

angular.module('oscarProviderViewModule').directive('oscarSecurityShow', function ($animate, securityService) {
	  return function(scope, element, attr) {
			scope.$watch(attr.oscarSecurityShow, function ngShowWatchAction(value){
				//console.log('valuee='+value.toSource());

				if(value.objectName != null && value.privilege != null) {
					//securityService.hasHigh(value.objectName,value.privilege,null);
					console.log('gtg');
				}

			  //set value to true to show, or else hide
			  $animate[value ? 'removeClass' : 'addClass'](element, 'ng-hide');
			});
	  };
});
*/
