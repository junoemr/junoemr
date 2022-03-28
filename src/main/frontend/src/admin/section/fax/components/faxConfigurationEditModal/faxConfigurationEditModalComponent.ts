import FaxAccountService from "../../../../../lib/fax/service/FaxAccountService";
import FaxAccount from "../../../../../lib/fax/model/FaxAccount";
import {
	JUNO_BUTTON_COLOR,
	JUNO_BUTTON_COLOR_PATTERN,
	JUNO_STYLE,
	LABEL_POSITION
} from "../../../../../common/components/junoComponentConstants";
import {FaxAccountType} from "../../../../../lib/fax/model/FaxAccountType";
import LoadingQueue from "../../../../../lib/util/LoadingQueue";
import FaxAccountProviderFactory from "../../../../../lib/fax/provider/FaxAccountProviderFactory";
import ToastErrorHandler from "../../../../../lib/error/handler/ToastErrorHandler";
import {FaxAccountConnectionStatus} from "../../../../../lib/fax/model/FaxAccountConnectionStatus";

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
			ctrl.faxAccountService = new FaxAccountService(new ToastErrorHandler());
			ctrl.LoadingQueue = new LoadingQueue();

			ctrl.LABEL_POSITION = LABEL_POSITION;
			ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
			ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

			ctrl.coverLetterOptions = [];
			ctrl.validations = {};
			ctrl.initialSave = false;
			ctrl.initialized = false;

			ctrl.$onInit = async () =>
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
					ctrl.faxAccount = new FaxAccount(FaxAccountType.Srfax);
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

				ctrl.faxAccountProvider = FaxAccountProviderFactory.creatAccountProvider(ctrl.faxAccount);
				//ctrl.coverLetterOptions = await ctrl.faxAccountProvider.getCoverLetterOptions();
				if(!ctrl.faxAccount.coverLetterOption && ctrl.coverLetterOptions.length > 0)
				{
					ctrl.faxAccount.coverLetterOption = ctrl.coverLetterOptions[0].value;
				}

				ctrl.setupValidations();
				ctrl.initialized = true;
				$scope.$apply();
			};

			ctrl.setupValidations = () =>
			{
				ctrl.validations = {
					accountLoginFilled: Juno.Validations.validationFieldRequired(ctrl.faxAccount, "accountLogin"),
					passwordFilled: ctrl.faxAccountProvider.passwordFieldValidation(),
					displayNameFilled: Juno.Validations.validationFieldRequired(ctrl.faxAccount, "displayName"),
					emailFilled: ctrl.faxAccountProvider.outboundEmailFieldValidation(),
					faxNumberFilled: ctrl.faxAccountProvider.outboundReturnFaxNoFieldValidation(),
				};
			}

			ctrl.isModalEditMode = (): boolean =>
			{
				return Boolean(ctrl.faxAccount.id);
			}

			ctrl.saveSettings = function ()
			{
				ctrl.LoadingQueue.pushLoadingState();
				ctrl.initialSave = true;
				if (!Juno.Validations.allValidationsValid(ctrl.validations))
				{
					Juno.Common.Util.errorAlert($uibModal, "Validation Errors", "Some fields are invalid, Please correct the highlighted fields");
					ctrl.LoadingQueue.popLoadingState()
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
					ctrl.LoadingQueue.popLoadingState();
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
				try
				{
					ctrl.LoadingQueue.pushLoadingState();
					const confirm = await Juno.Common.Util.confirmationDialog(
							$uibModal,
							"Confirm Action", "Are you sure you want to delete this fax integration?",
							ctrl.componentStyle);
					if(confirm)
					{
						await ctrl.faxAccountService.deleteAccountSettings(ctrl.faxAccount.id);
						ctrl.modalInstance.close(null);
					}
				}
				finally
				{
					ctrl.LoadingQueue.popLoadingState();
					$scope.$apply();
				}
			}

			ctrl.showConnectButton = () =>
			{
				return ctrl.isModalEditMode() &&
						ctrl.faxAccountProvider.isOauth() &&
						ctrl.faxAccount.connectionStatus !== FaxAccountConnectionStatus.Success;
			}

			ctrl.showDisconnectButton = () =>
			{
				return ctrl.isModalEditMode() &&
						ctrl.faxAccountProvider.isOauth() &&
						ctrl.faxAccount.connectionStatus === FaxAccountConnectionStatus.Success
			}

			ctrl.connectAccount = async () =>
			{
				location.href = "../fax/ringcentral/oauth";
			}

			ctrl.disconnectAccount = async () =>
			{
				try
				{
					ctrl.LoadingQueue.pushLoadingState();
					const confirm = await Juno.Common.Util.confirmationDialog(
							$uibModal,
							"Confirm Action", "Are you sure you want to disconnect the integration?",
							ctrl.componentStyle);

					if(confirm)
					{
						await ctrl.faxAccountService.disconnectAccountSettings(ctrl.faxAccount.id);
						ctrl.faxAccount.connectionStatus = FaxAccountConnectionStatus.SignedOut;
					}
				}
				finally
				{
					ctrl.LoadingQueue.popLoadingState();
					$scope.$apply();
				}
			}

			ctrl.cancel = function cancel()
			{
				ctrl.modalInstance.dismiss('cancel');
			};

			ctrl.testConnection = async () => {
				try
				{
					ctrl.LoadingQueue.pushLoadingState();

					if (ctrl.faxAccount.id)
					{
						ctrl.faxAccount.connectionStatus = await ctrl.faxAccountService.testExistingFaxConnection(ctrl.faxAccount);
					}
					else
					{
						if (await ctrl.faxAccountService.testFaxConnection(ctrl.faxAccount))
						{
							ctrl.faxAccount.connectionStatus = FaxAccountConnectionStatus.Success;
						}
						else
						{
							ctrl.faxAccount.connectionStatus = FaxAccountConnectionStatus.Failure;
						}
					}
				}
				catch (error)
				{
					ctrl.faxAccount.connectionStatus = FaxAccountConnectionStatus.Failure;
				}
				finally
				{
					ctrl.LoadingQueue.popLoadingState();
					$scope.$apply();
				}
			}

			ctrl.setDefaultConnectionStatus = function ()
			{
				ctrl.faxAccount.connectionStatus = FaxAccountConnectionStatus.Unknown;
			};

			ctrl.getConnectionStatusClass = (): string[] =>
			{
				switch (ctrl.faxAccount.connectionStatus)
				{
					case FaxAccountConnectionStatus.Success: return ["connection-status-success"];
					case FaxAccountConnectionStatus.Failure:
					case FaxAccountConnectionStatus.SignedOut: return ["connection-status-failure"];
					case FaxAccountConnectionStatus.Unknown:
					default: return ["connection-status-unknown"];
				}
			}
			ctrl.getConnectionIconClass = (): string =>
			{
				switch (ctrl.faxAccount.connectionStatus)
				{
					case FaxAccountConnectionStatus.Success: return "icon-check";
					case FaxAccountConnectionStatus.Failure:
					case FaxAccountConnectionStatus.SignedOut: return "icon-critical";
					case FaxAccountConnectionStatus.Unknown:
					default: return "icon-question";
				}
			}

			ctrl.getConnectionTestText = (): string =>
			{
				switch (ctrl.faxAccount.connectionStatus)
				{
					case FaxAccountConnectionStatus.Success: return "Fax Account Working";
					case FaxAccountConnectionStatus.Failure: return "Error Occurred";
					case FaxAccountConnectionStatus.SignedOut: return "Not Connected";
					case FaxAccountConnectionStatus.Unknown:
					default: return "Test Connection";
				}
			}

			ctrl.getConnectionText = (): string =>
			{
				return "Connect " + ctrl.faxAccountProvider.getIntegrationName();
			}

			ctrl.getDisconnectText = (): string =>
			{
				return "Disconnect " + ctrl.faxAccountProvider.getIntegrationName();
			}
		}
	]
});