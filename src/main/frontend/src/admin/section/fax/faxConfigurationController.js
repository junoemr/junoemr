angular.module("Admin.Section.Fax").controller('Admin.Section.Fax.FaxConfigurationController', [
	"$uibModal",
	"faxAccountService",
	"providerService",
	"systemPreferenceService",
	function ($uibModal,
	          faxAccountService,
	          providerService,
	          systemPreferenceService)
	{
		var controller = this;
		controller.faxAccountList = [];
		controller.loggedInProvider = null;
		controller.masterFaxDisabled = true;
		controller.masterFaxEnabledInbound = false;
		controller.masterFaxEnabledOutbound = false;

		controller.initialize = function()
		{
			// if the current provider number is unknown, retrieve it.
			if(controller.loggedInProvider == null)
			{
				providerService.getMe().then(
					function success(response)
					{
						controller.loggedInProvider = response;
					},
					function error(error)
					{
						console.error(error);
					}
				)
			}
			systemPreferenceService.isPreferenceEnabled("masterFaxEnabledInbound", controller.masterFaxEnabledInbound).then(
				function success(response)
				{
					controller.masterFaxEnabledInbound = response;
					controller.updateMasterFaxDisabledStatus();
				},
				function error(error)
				{
					console.error(error);
				}
			);
			systemPreferenceService.isPreferenceEnabled("masterFaxEnabledOutbound", controller.masterFaxEnabledOutbound).then(
				function success(response)
				{
					controller.masterFaxEnabledOutbound = response;
					controller.updateMasterFaxDisabledStatus();
				},
				function error(error)
				{
					console.error(error);
				}
			);

			faxAccountService.listAccounts().then(
				function success(response)
				{
					controller.faxAccountList = response;
				},
				function error(error)
				{
					console.error(error);
				}
			)
		};

		controller.editNewFaxAccount = function editNewFaxAccount()
		{
			controller.editFaxAccount();
		};
		controller.editFaxAccount = function editFaxAccount(faxAccount)
		{
			let isNewAcct = true;
			if(faxAccount)
			{
				isNewAcct = false;
			}

			var modalInstance = $uibModal.open(
				{
					templateUrl: 'src/admin/section/fax/faxConfigurationEdit.jsp',
					controller: 'Admin.Section.Fax.FaxConfigurationEditController as faxConfigEditController',
					backdrop: 'static',
					windowClass: 'faxEditModal',
					resolve:
						{
							faxAccount: function()
							{
								return faxAccount;
							},
							masterFaxEnabledInbound: function()
							{
								return controller.masterFaxEnabledInbound;
							},
							masterFaxEnabledOutbound: function()
							{
								return controller.masterFaxEnabledOutbound;
							}
						}
				});

			modalInstance.result.then(
				// the object passed back on closing
				function success(updatedAccount)
				{
					if(isNewAcct)
					{
						// new accounts get added to the account list
						controller.faxAccountList.push(updatedAccount);
					}
					else
					{
						// clear the existing properties and replace with the updated ones
						angular.copy(updatedAccount, faxAccount);
					}
				},
				function error(errors)
				{
					// do nothing on dismissal
				});
		};

		controller.saveMasterFaxEnabledStateInbound = function saveMasterFaxEnabledState()
		{
			controller.setSystemProperty("masterFaxEnabledInbound", controller.masterFaxEnabledInbound);
			controller.updateMasterFaxDisabledStatus();
		};
		controller.saveMasterFaxEnabledStateOutbound = function saveMasterFaxEnabledState()
		{
			controller.setSystemProperty("masterFaxEnabledOutbound", controller.masterFaxEnabledOutbound);
			controller.updateMasterFaxDisabledStatus();
		};

		controller.updateMasterFaxDisabledStatus = function updateMasterFaxDisabledStatus()
		{
			controller.masterFaxDisabled = !controller.masterFaxEnabledInbound && !controller.masterFaxEnabledOutbound;
		};
		controller.setSystemProperty = function setSystemProperty(key, value)
		{
			systemPreferenceService.setPreference(key, value).then(
				function success(response)
				{
				},
				function error(error)
				{
					console.error(error);
				}
			);
		};

		controller.initialize();
	}
]);