angular.module("Admin.Integration.Fax").controller('Admin.Integration.Fax.FaxConfigurationController', [
	"faxService",
	function (faxService)
	{
		var controller = this;
		controller.settings = {};
		controller.settings.enabled = false;

		controller.connectionStatusEnum = Object.freeze({"unknown":1, "success":2, "failure":3});
		controller.connectionStatus = controller.connectionStatusEnum.unknown;

		controller.loadSettings = function()
		{
			faxService.getAccountSettings().then(
				function success(response)
				{
					controller.settings = response;
				},
				function error(error)
				{
					console.error(error);
				}
			)
		};
		controller.saveSettings = function()
		{
			faxService.setAccountSettings(controller.settings).then(
				function success(response)
				{
					console.info("settings saved");
					controller.settings = response;
				},
				function error(error)
				{
					console.error(error);
				}
			)
		};
		controller.testConnection = function()
		{
			faxService.testConnection(controller.settings).then(
				function success(response)
				{
					if(response)
					{
						controller.connectionStatus = controller.connectionStatusEnum.success;
					}
					else
					{
						controller.connectionStatus = controller.connectionStatusEnum.failure;
					}
				},
				function error(error)
				{
					console.error(error);
					controller.connectionStatus = controller.connectionStatusEnum.unknown;
				}
			)
		};

		controller.loadSettings();
	}
]);