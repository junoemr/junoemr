import FaxAccountService from "../../../../../lib/fax/service/FaxAccountService";
import FaxAccount from "../../../../../lib/fax/model/FaxAccount";
import {FaxAccountConnectionStatusType} from "../../../../../lib/fax/model/FaxAccountConnectionStatusType";

angular.module("Admin.Section.Fax").component('faxConfigurationEditModal', {
	templateUrl: 'src/admin/section/fax/components/faxConfigurationEditModal/faxConfigurationEditModal.jsp',
	bindings: {
		modalInstance: "<",
		resolve: "<",
	},
	controller: [
		'$scope',
		function ($scope)
		{
			const controller = this;
			controller.faxAccountService = new FaxAccountService();

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
					controller.faxAccount = new FaxAccount();
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
					controller.faxAccountService.updateAccountSettings(controller.faxAccount).then(
						closeSuccess,
						closeError
					)
				}
				else
				{
					controller.faxAccountService.createAccountSettings(controller.faxAccount).then(
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
				controller.faxAccountService.testFaxConnection(controller.faxAccount).then(
					function success(response)
					{
						if (response)
						{
							controller.faxAccount.connectionStatus = FaxAccountConnectionStatusType.Success;
						}
						else
						{
							controller.faxAccount.connectionStatus = FaxAccountConnectionStatusType.Failure;
						}
						$scope.$apply();
					},
					function error(error)
					{
						console.error(error);
						controller.faxAccount.connectionStatus = FaxAccountConnectionStatusType.Unknown;
					}
				)
			};
			controller.setDefaultConnectionStatus = function ()
			{
				controller.faxAccount.connectionStatus = FaxAccountConnectionStatusType.Unknown;
			};
		}
	]
});