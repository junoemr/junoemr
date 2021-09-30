import {SecurityPermissions} from "../common/security/securityConstants";

angular.module('Settings').controller('Settings.SettingsController', [

	'$scope',
	'$state',
	'$uibModal',
	'user',
	'loadedSettings',
	'providerService',
	'securityRolesService',

	function(
		$scope,
		$state,
		$uibModal,
		user,
		loadedSettings,
		providerService,
		securityRolesService)
	{
		const controller = this;

		$scope.$emit('configureShowPatientList', false);

		controller.user = user;
		controller.pref = loadedSettings;
		controller.SecurityPermissions = SecurityPermissions;

		if (controller.pref.recentPatients == null)
		{
			controller.pref.recentPatients = "8";
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
		},
		{
			id: 13,
			displayName: 'Health Tracker',
			path: 'tracker'
		},
		];

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
			controller.currentTab = controller.tabs.find((tab) => tab.path === $state.current.data.tab);
		}
		else
		{
			controller.currentTab = controller.tabs[0];
		}

		controller.$onInit = () =>
		{
			// to make everything load the same pref object
			controller.changeTab(controller.currentTab);
		};

		controller.isActive = function(tab)
		{
			return (tab != null && controller.currentTab != null && tab.id === controller.currentTab.id);
		};

		controller.changeTab = function(tab)
		{
			controller.currentTab = tab;
			$state.go('settings.' + tab.path,
				{
					pref: controller.pref,
				});
		};

		controller.saveEnabled = () =>
		{
			return securityRolesService.hasSecurityPrivileges(SecurityPermissions.PreferenceUpdate);
		}
		controller.save = function()
		{
			var newList = [];
			for (var i = 0; i < controller.pref.appointmentScreenQuickLinks.length; i++)
			{
				if (controller.pref.appointmentScreenQuickLinks[i].checked == null || controller.pref.appointmentScreenQuickLinks[i].checked === false)
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
					Juno.Common.Util.successAlert($uibModal,"Success", "Saved");
				});
			}
		};

		controller.validateSettings = function()
		{
			let isValid = true;
			// check eform fields are valid
			if (!controller.pref.eformPopupWidth || !controller.pref.eformPopupHeight)
			{
				Juno.Common.Util.errorAlert($uibModal, "Validation Error","Eform width and height fields must be filled in.");
				isValid = false;
			}

			if (isNaN(controller.pref.appointmentScreenLinkNameDisplayLength))
			{
				Juno.Common.Util.errorAlert($uibModal, "Validation Error","The value of link and form names displayed on appointment screen must be a positive number.");
				isValid = false;
			}
			else if (controller.pref.appointmentScreenLinkNameDisplayLength.length > 3)
			{
				Juno.Common.Util.errorAlert($uibModal, "Validation Error","Please reduce the length of the link and form names that you want to display to a value under 999.");
				isValid = false;
			}
			else
			{
				let intVal = parseInt(controller.pref.appointmentScreenLinkNameDisplayLength);
				if (intVal < 1)
				{
					Juno.Common.Util.errorAlert($uibModal, "Validation Error","The value of link and form names displayed on appointment screen must be a positive number.");
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
	}
]);