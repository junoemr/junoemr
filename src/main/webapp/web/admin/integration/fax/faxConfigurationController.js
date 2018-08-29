angular.module("Admin.Integration.Fax").controller('Admin.Integration.Fax.FaxConfigurationController', [
	"faxConfigService",
	function (faxConfigService)
	{
		var controller = this;

		controller.faxAccountList = [];
		controller.connectionStatusEnum = Object.freeze({"unknown":1, "success":2, "failure":3});


		controller.initialize = function()
		{
			faxConfigService.listAccounts().then(
				function success(response)
				{
					controller.faxAccountList = response;
					for(let i=0; i< controller.faxAccountList.length; i++)
					{
						controller.faxAccountList[i].connectionStatus = controller.connectionStatusEnum.unknown;
					}
				},
				function error(error)
				{
					console.error(error);
				}
			)
		};

		controller.addNewAccount = function()
		{
			let newAccountSettings = {
				enabled: true,
				connectionStatus: controller.connectionStatusEnum.unknown,
				accountLogin: '',
				password: ''
			};
			controller.faxAccountList.push(newAccountSettings);
		};

		controller.saveSettings = function (faxAccount)
		{
			if (faxAccount.id)
			{
				faxConfigService.updateAccountSettings(faxAccount.id, faxAccount).then(
					function success(response)
					{
						console.info("settings saved");
						faxAccount = response;
					},
					function error(error)
					{
						console.error(error);
					}
				)
			}
			else
			{
				faxConfigService.addAccountSettings(faxAccount).then(
					function success(response)
					{
						console.info("settings saved");
						faxAccount = response;
					},
					function error(error)
					{
						console.error(error);
					}
				)
			}
		};
		controller.testConnection = function(faxAccount)
		{
			faxConfigService.testConnection(faxAccount).then(
				function success(response)
				{
					if(response)
					{
						faxAccount.connectionStatus = controller.connectionStatusEnum.success;
					}
					else
					{
						faxAccount.connectionStatus = controller.connectionStatusEnum.failure;
					}
				},
				function error(error)
				{
					console.error(error);
					faxAccount.connectionStatus = controller.connectionStatusEnum.unknown;
				}
			)
		};

		controller.initialize();
	}
]);