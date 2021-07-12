import {SitesApi} from "../../generated/api/SitesApi";
import {ScheduleApi} from "../../generated/api/ScheduleApi";

angular.module('Settings').controller('Settings.SettingsController', [

	'$scope',
	'$http',
	'$httpParamSerializer',
	'$state',
	'$uibModal',
	'$filter',
	'providerList',
	'user',
	'billingServiceTypes',
	'loadedSettings',
	'providerService',
	'encounterForms',
	'eforms',
	'teams',
	'groupNames',
	'loadedApps',
	'appService',

	function(
		$scope,
		$http,
		$httpParamSerializer,
		$state,
		$uibModal,
		$filter,
		providerList,
		user,
		billingServiceTypes,
		loadedSettings,
		providerService,
		encounterForms,
		eforms,
		teams,
		groupNames,
		loadedApps,
		appService)
	{

		var controller = this;

		controller.sitesApi = new SitesApi($http, $httpParamSerializer,
		'../ws/rs');
		controller.scheduleApi = new ScheduleApi($http, $httpParamSerializer,
			'../ws/rs');

		$scope.$emit('configureShowPatientList', false);

		controller.providerList = providerList;
		controller.user = user;
		controller.billingServiceTypes = billingServiceTypes;
		controller.pref = loadedSettings;
		controller.encounterForms = encounterForms.content;
		controller.eforms = eforms;
		controller.loadedApps = loadedApps;

		controller.siteOptions = [];
		controller.scheduleOptions = [];

		if (controller.pref.recentPatients == null)
		{
			controller.pref.recentPatients = "8";
		}

		//convert to value/label object list from string array
		controller.formGroupNames = [
		{
			"value": "",
			"label": "None"
		}];
		for (var i = 0; i < groupNames.length; i++)
		{
			controller.formGroupNames.push(
			{
				"value": groupNames[i],
				"label": groupNames[i]
			});
		}


		//convert to value/label obj list. Add all/none
		controller.teams = [
		{
			"value": "-1",
			"label": "All"
		}];
		for (var i = 0; i < teams.length; i++)
		{
			controller.teams.push(
			{
				"value": teams[i],
				"label": teams[i]
			});
		}
		controller.teams.push(
		{
			"value": "",
			"label": "None"
		});

		//add none -option to start.
		controller.billingServiceTypesMod = [];
		angular.copy(controller.billingServiceTypes, controller.billingServiceTypesMod);
		controller.billingServiceTypesMod.splice(0, 0,
		{
			"type": "no",
			"name": "--None--"
		});

		//this needs to be done to do the weird checkbox lists. basically add a property to each encounterList object called checked:[true|false]
		for (var i = 0; i < controller.pref.appointmentScreenForms.length; i++)
		{
			var selected = $filter('filter')(controller.encounterForms,
			{
				formName: controller.pref.appointmentScreenForms[i]
			});
			if (selected != null)
			{
				for (var x = 0; x < selected.length; x++)
				{
					if (selected[x].formName === controller.pref.appointmentScreenForms[i])
					{
						selected[x].checked = true;
					}
				}
			}
		}

		//this needs to be done to do the weird checkbox lists. basically add a property to each encounterList object called checked:[true|false]
		for (var i = 0; i < controller.pref.appointmentScreenEforms.length; i++)
		{
			var selected = $filter('filter')(controller.eforms,
			{
				id: controller.pref.appointmentScreenEforms[i]
			});
			if (selected != null)
			{
				for (var x = 0; x < selected.length; x++)
				{
					if (selected[x].id === controller.pref.appointmentScreenEforms[i])
					{
						selected[x].checked = true;
					}
				}
			}
		}


		controller.tabs = [
		{
			id: 0,
			displayName: 'Persona',
			path: 'persona'
		},
		{
			id: 1,
			displayName: 'General',
			path: 'general'
		},
		{
			id: 2,
			displayName: 'Schedule',
			path: 'schedule'
		},
		{
			id: 3,
			displayName: 'Billing',
			path: 'billing'
		},
		{
			id: 4,
			displayName: 'Rx',
			path: 'rx'
		},
		{
			id: 5,
			displayName: 'Master Demographic',
			path: 'masterdemo'
		},
		{
			id: 6,
			displayName: 'Consultations',
			path: 'consults'
		},
		{
			id: 7,
			displayName: 'Documents',
			path: 'documents'
		},
		{
			id: 8,
			displayName: 'Summary',
			path: 'summary'
		},
		{
			id: 9,
			displayName: 'eForms',
			path: 'eforms'
		},
		{
			id: 10,
			displayName: 'Inbox',
			path: 'inbox'
		},
		{
			id: 11,
			displayName: 'Programs',
			path: 'programs'
		},
		{
			id: 12,
			displayName: 'Integration',
			path: 'integration'
		}, ];
		controller.pageSizes = [
		{
			value: 'PageSize.A4',
			label: 'A4'
		},
		{
			value: 'PageSize.A6',
			label: 'A6'
		}];

		controller.rxInteractionWarningLevels = [
		{
			value: '0',
			label: 'Not Specified'
		},
		{
			value: '1',
			label: 'Low'
		},
		{
			value: '2',
			label: 'Medium'
		},
		{
			value: '3',
			label: 'High'
		},
		{
			value: '4',
			label: 'None'
		}];

		controller.staleDates = [
		{
			value: 'A',
			label: 'All'
		},
		{
			value: '0',
			label: '0'
		},
		{
			value: '-1',
			label: '-1'
		},
		{
			value: '-2',
			label: '2'
		},
		{
			value: '-3',
			label: '3'
		},
		{
			value: '-4',
			label: '4'
		},
		{
			value: '-5',
			label: '5'
		},
		{
			value: '-6',
			label: '6'
		},
		{
			value: '-7',
			label: '7'
		},
		{
			value: '-8',
			label: '8'
		},
		{
			value: '-9',
			label: '9'
		},
		{
			value: '-10',
			label: '10'
		},
		{
			value: '-11',
			label: '11'
		},
		{
			value: '-12',
			label: '12'
		},
		{
			value: '-13',
			label: '13'
		},
		{
			value: '-14',
			label: '14'
		},
		{
			value: '-15',
			label: '15'
		},
		{
			value: '-16',
			label: '16'
		},
		{
			value: '-17',
			label: '17'
		},
		{
			value: '-18',
			label: '18'
		},
		{
			value: '-19',
			label: '19'
		},
		{
			value: '-20',
			label: '20'
		},
		{
			value: '-21',
			label: '21'
		},
		{
			value: '-22',
			label: '22'
		},
		{
			value: '-23',
			label: '23'
		},
		{
			value: '-24',
			label: '24'
		},
		{
			value: '-25',
			label: '25'
		},
		{
			value: '-26',
			label: '26'
		},
		{
			value: '-27',
			label: '27'
		},
		{
			value: '-28',
			label: '28'
		},
		{
			value: '-29',
			label: '29'
		},
		{
			value: '-30',
			label: '30'
		},
		{
			value: '-31',
			label: '31'
		},
		{
			value: '-32',
			label: '32'
		},
		{
			value: '-33',
			label: '33'
		},
		{
			value: '-34',
			label: '34'
		},
		{
			value: '-35',
			label: '35'
		},
		{
			value: '-36',
			label: '36'
		}, ];

		controller.olisLabs = [
		{
			value: '',
			label: ''
		},
		{
			value: '5552',
			label: 'Gamma-Dynacare'
		},
		{
			value: '5407',
			label: 'CML'
		},
		{
			value: '5687',
			label: 'LifeLabs'
		}];

		controller.pasteFormats = [
		{
			value: 'single',
			label: 'Single Line'
		},
		{
			value: 'multi',
			label: 'Multi Line'
		}];

		controller.letterHeadNameDefaults = [
		{
			value: '1',
			label: 'Provider (user)'
		},
		{
			value: '2',
			label: 'MRP'
		},
		{
			value: '3',
			label: 'Clinic'
		}];

		if (controller.pref.consultationLetterHeadNameDefault == null)
		{
			controller.pref.consultationLetterHeadNameDefault = "1";
		}

		if (controller.pref.cppRemindersStartDate == null)
		{
			controller.pref.cppRemindersStartDate = false;
		}

		if ($state.current.data !== undefined)
		{
			controller.currentTab = $filter('filter')(controller.tabs, {path: $state.current.data.tab})[0];
		}
		else
		{
			controller.currentTab = controller.tabs[0];
		}




		controller.init = function()
		{
			controller.sitesApi.getSiteList().then(
				function success(rawResults)
				{
					var results = rawResults.data.body;
					var out = [];
					if (angular.isArray(results))
					{
						for (var i = 0; i < results.length; i++)
						{
							out.push({
								id: results[i].siteId,
								value: results[i].name,
								label: results[i].name,
								color: results[i].bgColor,
							});
						}
					}
					controller.siteOptions = out;
				}
			);
			controller.scheduleApi.getScheduleGroups().then(
				function success(rawResults)
				{
					var results = rawResults.data.body;
					for (var i = 0; i < results.length; i++)
					{
						var scheduleData = results[i];

						results[i].label = results[i].name;
						results[i].value = results[i].identifier;

						controller.scheduleOptions.push(scheduleData);
					}
				});
		};

		controller.isActive = function(tab)
		{
			return (tab != null && controller.currentTab != null && tab.id == controller.currentTab.id);
		};

		controller.changeTab = function(tab)
		{
			controller.currentTab = tab;
			$state.go('settings.' + tab.path);
		};

		controller.save = function()
		{
			var newList = [];
			for (var i = 0; i < controller.pref.appointmentScreenQuickLinks.length; i++)
			{
				if (controller.pref.appointmentScreenQuickLinks[i].checked == null || controller.pref.appointmentScreenQuickLinks[i].checked == false)
				{
					newList.push(
							{
								name: controller.pref.appointmentScreenQuickLinks[i].name,
								url: controller.pref.appointmentScreenQuickLinks[i].url
							});
				}
			}
			controller.pref.appointmentScreenQuickLinks = newList;

			if (controller.validateSettings())
			{
				providerService.saveSettings(controller.user.providerNo, controller.pref).then(function (data)
				{
					alert('saved');
				});
			}
		};

		controller.validateSettings = function()
		{
			let isValid = true;
			// check eform fields are valid
			if (!controller.pref.eformPopupWidth || !controller.pref.eformPopupHeight)
			{
				alert("Eform width and height fields must be filled in.");
				isValid = false;
			}

			if (isNaN(controller.pref.appointmentScreenLinkNameDisplayLength))
			{
				alert("The value of link and form names displayed on appointment screen must be a positive number.");
				isValid = false;
			}
			else if (controller.pref.appointmentScreenLinkNameDisplayLength.length > 3)
			{
				alert("Please reduce the length of the link and form names that you want to display to a value under 999.");
				isValid = false;
			}
			else
			{
				let intVal = parseInt(controller.pref.appointmentScreenLinkNameDisplayLength);
				if (intVal < 1)
				{
					alert("The value of link and form names displayed on appointment screen must be a positive number.");
					isValid = false;
				}
			}

			return isValid;
		};

		controller.cancel = function()
		{
			controller.pref = {};
			$state.go('dashboard');
		};

		controller.selectEncounterForms = function()
		{
			var selected = $filter('filter')(controller.encounterForms,
			{
				checked: true
			});
			var tmp = [];
			for (var i = 0; i < selected.length; i++)
			{
				tmp.push(selected[i].formName);
			}
			controller.pref.appointmentScreenForms = tmp;
		};

		controller.selectEForms = function()
		{
			var selected = $filter('filter')(controller.eforms,
			{
				checked: true
			});
			var tmp = [];
			for (var i = 0; i < selected.length; i++)
			{
				tmp.push(selected[i].id);
			}
			controller.pref.appointmentScreenEforms = tmp;
		};

		controller.removeQuickLinks = function()
		{
			var newList = [];

			for (var i = 0; i < controller.pref.appointmentScreenQuickLinks.length; i++)
			{
				if (controller.pref.appointmentScreenQuickLinks[i].checked == null || controller.pref.appointmentScreenQuickLinks[i].checked == false)
				{
					newList.push(controller.pref.appointmentScreenQuickLinks[i]);
				}
			}
			controller.pref.appointmentScreenQuickLinks = newList;
		};


		controller.openChangePasswordModal = function()
		{
			/*
        var modalInstance = $uibModal.open({
        	templateUrl: 'src/settings/changePassword.jsp',
            controller: 'ChangePasswordController'
        });
     */
			window.open('../provider/providerchangepassword.jsp', 'change_password', 'width=750,height=500');
		};


		controller.openQuickLinkModal = function()
		{
			var modalInstance = $uibModal.open(
			{
				templateUrl: 'src/settings/quickLink.jsp',
				controller: 'QuickLinkController'
			});

			modalInstance.result.then(function(selectedItem)
			{
				if (selectedItem != null)
				{
					if (selectedItem != null && selectedItem.name != null && selectedItem.url != null)
					{
						controller.pref.appointmentScreenQuickLinks.push(selectedItem);
					}
				}
			});
		};

		controller.editDocumentTemplates = function()
		{
			window.open('../admin/displayDocumentDescriptionTemplate.jsp', 'document_templates', 'width=700,height=450');
		};

		controller.showProviderColourPopup = function()
		{
			window.open('../provider/providerColourPicker.jsp', 'provider_colour', 'width=700,height=450');
		};

		controller.showDefaultEncounterWindowSizePopup = function()
		{
			window.open('../setProviderStaleDate.do?method=viewEncounterWindowSize', 'encounter_window_sz', 'width=700,height=450');
		};

		controller.openConfigureEChartCppPopup = function()
		{
			window.open('../provider/CppPreferences.do', 'configure_echart_cpp', 'width=700,height=450');
		};

		controller.openManageAPIClientPopup = function()
		{
			window.open('../provider/clients.jsp', 'api_clients', 'width=700,height=450');
		};

		controller.openMyOscarUsernamePopup = function()
		{
			window.open('../provider/providerIndivoIdSetter.jsp', 'invivo_setter', 'width=700,height=450');
		};

		controller.authenticate = function(app)
		{
			window.open('../apps/oauth1.jsp?id=' + app.id, 'appAuth', 'width=700,height=450');
		};

		controller.refreshAppList = function()
		{
			console.log("refresh", controller.loadedApps);
			appService.getApps().then(function(data)
				{
					controller.loadedApps = data;
				},
				function(errorMessage)
				{
					console.log("applist:" + errorMessage);
				}
			);
			///
			console.log("refresh", controller.loadedApps);
		};

		controller.init();
	}
]);