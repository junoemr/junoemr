angular.module("Admin.Integration.Fax").controller('Admin.Integration.Fax.FaxConfigurationEditController', [
	"$uibModal",
	"$uibModalInstance",
	"faxConfigService",
	"faxAccount",
	function ($uibModal,
	          $uibModalInstance,
	          faxConfigService,
	          faxAccount)
	{
		var controller = this;
		controller.connectionStatusEnum = Object.freeze({"unknown":1, "success":2, "failure":3});
		controller.coverLetterOptions = [
			"None",
			"Basic",
			"Standard",
			"Company",
			"Personal"
		];

		controller.initialize = function()
		{
			if(faxAccount)
			{
				controller.faxAccount = angular.copy(faxAccount);
				if(!controller.faxAccount.connectionStatus)
				{
					controller.setDefaultConnectionStatus();
				}
			}
			else
			{
				controller.faxAccount = {
					enabled: true,
					enableInbound: false,
					enableOutbound: false,
					accountLogin: null,
					accountEmail: '',
					password: '',
					displayName: '',
					coverLetterOption: '',
					faxNumber: '',
					connectionStatus: controller.connectionStatusEnum.unknown
				};
			}
		};

		controller.saveSettings = function ()
		{
			let closeSuccess = function(updatedAccount)
			{
				// keep these settings
				updatedAccount.connectionStatus = controller.faxAccount.connectionStatus;
				$uibModalInstance.close(updatedAccount);
			};
			let closeError = function(error)
			{
				console.error(error);
			};

			if (controller.faxAccount.id)
			{
				faxConfigService.updateAccountSettings(controller.faxAccount.id, controller.faxAccount).then(
					closeSuccess,
					closeError
				)
			}
			else
			{
				faxConfigService.addAccountSettings(controller.faxAccount).then(
					closeSuccess,
					closeError
				)
			}
		};
		controller.cancel = function cancel()
		{
			$uibModalInstance.dismiss('cancel');
		};

		controller.testConnection = function()
		{
			faxConfigService.testConnection(controller.faxAccount).then(
				function success(response)
				{
					if(response)
					{
						controller.faxAccount.connectionStatus = controller.connectionStatusEnum.success;
					}
					else
					{
						controller.faxAccount.connectionStatus = controller.connectionStatusEnum.failure;
					}
				},
				function error(error)
				{
					console.error(error);
					controller.faxAccount.connectionStatus = controller.connectionStatusEnum.unknown;
				}
			)
		};
		controller.setDefaultConnectionStatus = function()
		{
			controller.faxAccount.connectionStatus = controller.connectionStatusEnum.unknown;
		};

		controller.initialize();
	}
]);