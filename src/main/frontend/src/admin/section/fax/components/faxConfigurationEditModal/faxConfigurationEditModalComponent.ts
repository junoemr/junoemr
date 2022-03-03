import FaxAccountService from "../../../../../lib/fax/service/FaxAccountService";
import FaxAccount from "../../../../../lib/fax/model/FaxAccount";
import {FaxAccountConnectionStatusType} from "../../../../../lib/fax/model/FaxAccountConnectionStatusType";
import {
	JUNO_BUTTON_COLOR,
	JUNO_BUTTON_COLOR_PATTERN,
	JUNO_STYLE,
	LABEL_POSITION
} from "../../../../../common/components/junoComponentConstants";
import ToastService from "../../../../../lib/alerts/service/ToastService";

angular.module("Admin.Section.Fax").component('faxConfigurationEditModal', {
	templateUrl: 'src/admin/section/fax/components/faxConfigurationEditModal/faxConfigurationEditModal.jsp',
	bindings: {
		modalInstance: "<",
		resolve: "<",
	},
	controller: [
		'$scope',
		'$uibModal',
		function ($scope, $uibModal)
		{
			const ctrl = this;
			ctrl.faxAccountService = new FaxAccountService();
			ctrl.toastService = new ToastService();

			ctrl.LABEL_POSITION = LABEL_POSITION;
			ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
			ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

			ctrl.coverLetterOptions = [
				{
					label: "None",
					value: null,
				},
				{
					label: "Basic",
					value: "Basic",
				},
				{
					label: "Standard",
					value: "Standard",
				},
				{
					label: "Company",
					value: "Company",
				},
				{
					label: "Personal",
					value: "Personal",
				},
			];

			ctrl.$onInit = () =>
			{
				ctrl.componentStyle = ctrl.resolve.style || JUNO_STYLE.DEFAULT;
				if (ctrl.resolve.faxAccount)
				{
					ctrl.faxAccount = angular.copy(ctrl.resolve.faxAccount);
					if (!ctrl.faxAccount.connectionStatus)
					{
						ctrl.setDefaultConnectionStatus();
					}
				}
				else
				{
					ctrl.faxAccount = new FaxAccount();
				}
				// get the master flag status for inbound/outbound settings
				ctrl.masterFaxEnabledInbound = ctrl.resolve.masterFaxEnabledInbound;
				ctrl.masterFaxEnabledOutbound = ctrl.resolve.masterFaxEnabledOutbound;

				// switch off settings that are disabled and un-editable.
				// if this is not done, form errors may prevent saving changes to other section
				if (!ctrl.masterFaxEnabledInbound)
				{
					ctrl.faxAccount.enableInbound = false;
				}
				if (!ctrl.masterFaxEnabledOutbound)
				{
					ctrl.faxAccount.enableOutbound = false;
				}
			};

			ctrl.isModalEditMode = (): boolean =>
			{
				return Boolean(ctrl.faxAccount.id);
			}

			ctrl.saveSettings = function (form)
			{
				ctrl.submitDisabled = true;
				if (!form.$valid)
				{
					ctrl.toastService.errorToast("The form contains errors");
					ctrl.submitDisabled = false;
					return;
				}

				let closeSuccess = function (updatedAccount)
				{
					// keep these settings
					updatedAccount.connectionStatus = ctrl.faxAccount.connectionStatus;
					ctrl.modalInstance.close(updatedAccount);
				};
				let closeError = function (error)
				{
					console.error(error);
					ctrl.submitDisabled = false;
				};

				if (ctrl.isModalEditMode())
				{
					ctrl.faxAccountService.updateAccountSettings(ctrl.faxAccount).then(
						closeSuccess,
						closeError
					)
				}
				else
				{
					ctrl.faxAccountService.createAccountSettings(ctrl.faxAccount).then(
						closeSuccess,
						closeError
					)
				}
			};

			ctrl.deleteConfig = async () =>
			{
				const confirm = await Juno.Common.Util.confirmationDialog(
					$uibModal,
					"Confirm Action", "Are you sure you want to delete this fax integration?",
					ctrl.componentStyle);
				if(confirm)
				{
					//todo
				}
			}

			ctrl.cancel = function cancel()
			{
				ctrl.modalInstance.dismiss('cancel');
			};

			ctrl.testConnection = function ()
			{
				ctrl.faxAccountService.testFaxConnection(ctrl.faxAccount).then(
					function success(response)
					{
						if (response)
						{
							ctrl.faxAccount.connectionStatus = FaxAccountConnectionStatusType.Success;
						}
						else
						{
							ctrl.faxAccount.connectionStatus = FaxAccountConnectionStatusType.Failure;
						}
						$scope.$apply();
					},
					function error(error)
					{
						console.error(error);
						ctrl.faxAccount.connectionStatus = FaxAccountConnectionStatusType.Unknown;
					}
				)
			};
			ctrl.setDefaultConnectionStatus = function ()
			{
				ctrl.faxAccount.connectionStatus = FaxAccountConnectionStatusType.Unknown;
			};

			ctrl.getConnectionStatusClass = (): string[] =>
			{
				switch (ctrl.faxAccount.connectionStatus)
				{
					case FaxAccountConnectionStatusType.Success: return ["connection-status-success"];
					case FaxAccountConnectionStatusType.Failure: return ["connection-status-failure"];
					case FaxAccountConnectionStatusType.Unknown:
					default: return ["connection-status-unknown"];
				}
			}
			ctrl.getConnectionIconClass = (): string =>
			{
				switch (ctrl.faxAccount.connectionStatus)
				{
					case FaxAccountConnectionStatusType.Success: return "icon-check";
					case FaxAccountConnectionStatusType.Failure: return "icon-private";
					case FaxAccountConnectionStatusType.Unknown:
					default: return "icon-question";
				}
			}
		}
	]
});