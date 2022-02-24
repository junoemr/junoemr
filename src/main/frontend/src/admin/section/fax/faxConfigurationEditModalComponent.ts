angular.module("Admin.Section.Fax").component('faxConfigurationEditModal', {
	templateUrl: 'src/admin/section/fax/faxConfigurationEditModal.jsp',
	bindings: {
		modalInstance: "<",
		resolve: "<",
	},
	controller: [
		"faxAccountService",
		function (faxAccountService)
		{
			const controller = this;
			controller.connectionStatusEnum = Object.freeze({"unknown": 1, "success": 2, "failure": 3});
			controller.coverLetterOptions = [
				"None",
				"Basic",
				"Standard",
				"Company",
				"Personal"
			];

			controller.$onInit = () =>
			{
				if (controller.resolve.faxAccount)
				{
					controller.faxAccount = angular.copy(controller.resolve.faxAccount);
					if (!controller.faxAccount.connectionStatus)
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
				// get the master flag status for inbound/outbound settings
				controller.masterFaxEnabledInbound = controller.resolve.masterFaxEnabledInbound;
				controller.masterFaxEnabledOutbound = controller.resolve.masterFaxEnabledOutbound;

				// switch off settings that are disabled and un-editable.
				// if this is not done, form errors may prevent saving changes to other section
				if (!controller.masterFaxEnabledInbound)
				{
					controller.faxAccount.enableInbound = false;
				}
				if (!controller.masterFaxEnabledOutbound)
				{
					controller.faxAccount.enableOutbound = false;
				}
			};

			controller.saveSettings = function (form)
			{
				controller.submitDisabled = true;
				if (!form.$valid)
				{
					alert("The form contains errors");
					controller.submitDisabled = false;
					return;
				}

				let closeSuccess = function (updatedAccount)
				{
					// keep these settings
					updatedAccount.connectionStatus = controller.faxAccount.connectionStatus;
					controller.modalInstance.close(updatedAccount);
				};
				let closeError = function (error)
				{
					console.error(error);
					controller.submitDisabled = false;
				};

				if (controller.faxAccount.id)
				{
					faxAccountService.updateAccountSettings(controller.faxAccount.id, controller.faxAccount).then(
						closeSuccess,
						closeError
					)
				}
				else
				{
					faxAccountService.addAccountSettings(controller.faxAccount).then(
						closeSuccess,
						closeError
					)
				}
			};
			controller.cancel = function cancel()
			{
				controller.modalInstance.dismiss('cancel');
			};

			controller.testConnection = function ()
			{
				faxAccountService.testConnection(controller.faxAccount).then(
					function success(response)
					{
						if (response)
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
			controller.setDefaultConnectionStatus = function ()
			{
				controller.faxAccount.connectionStatus = controller.connectionStatusEnum.unknown;
			};
		}
	]
});