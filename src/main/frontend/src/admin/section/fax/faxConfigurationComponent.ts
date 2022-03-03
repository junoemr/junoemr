import FaxAccountService from "../../../lib/fax/service/FaxAccountService";
import {
	JUNO_BUTTON_COLOR,
	JUNO_BUTTON_COLOR_PATTERN,
	LABEL_POSITION
} from "../../../common/components/junoComponentConstants";
import {SecurityPermissions} from "../../../common/security/securityConstants";

angular.module("Admin.Section.Fax").component('faxConfiguration', {
	templateUrl: 'src/admin/section/fax/faxConfiguration.jsp',
	bindings: {
		componentStyle: "<?",
	},
	controller: [
		"$uibModal",
		"providerService",
		"securityRolesService",
		"systemPreferenceService",
		function ($uibModal,
		          providerService,
				  securityRolesService,
		          systemPreferenceService)
		{
			const ctrl = this;
			ctrl.faxAccountService = new FaxAccountService();

			ctrl.LABEL_POSITION = LABEL_POSITION;
			ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
			ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

			ctrl.faxAccountList = [];
			ctrl.loggedInProvider = null;
			ctrl.masterFaxDisabled = true;
			ctrl.masterFaxEnabledInbound = false;
			ctrl.masterFaxEnabledOutbound = false;

			ctrl.$onInit = () =>
			{
				// if the current provider number is unknown, retrieve it.
				if(ctrl.loggedInProvider == null)
				{
					providerService.getMe().then(
						function success(response)
						{
							ctrl.loggedInProvider = response;
						},
						function error(error)
						{
							console.error(error);
						}
					)
				}
				systemPreferenceService.isPreferenceEnabled("masterFaxEnabledInbound", ctrl.masterFaxEnabledInbound).then(
					function success(response)
					{
						ctrl.masterFaxEnabledInbound = response;
						ctrl.updateMasterFaxDisabledStatus();
					},
					function error(error)
					{
						console.error(error);
					}
				);
				systemPreferenceService.isPreferenceEnabled("masterFaxEnabledOutbound", ctrl.masterFaxEnabledOutbound).then(
					function success(response)
					{
						ctrl.masterFaxEnabledOutbound = response;
						ctrl.updateMasterFaxDisabledStatus();
					},
					function error(error)
					{
						console.error(error);
					}
				);

				ctrl.faxAccountService.getAccounts().then(
					function success(response)
					{
						ctrl.faxAccountList = response;
					},
					function error(error)
					{
						console.error(error);
					}
				)
			};

			ctrl.userCanCreate = (): boolean =>
			{
				return securityRolesService.hasSecurityPrivileges(SecurityPermissions.ConfigureFaxCreate);
			}
			ctrl.userCanEdit = (): boolean =>
			{
				return securityRolesService.hasSecurityPrivileges(SecurityPermissions.ConfigureFaxUpdate);
			}

			ctrl.connectNewSRFaxAccount = () =>
			{
				ctrl.editFaxAccount(null);
			};
			ctrl.editFaxAccount = function editFaxAccount(faxAccount)
			{
				let isNewAcct = true;
				if(faxAccount)
				{
					isNewAcct = false;
				}

				var modalInstance = $uibModal.open(
					{
						component: 'faxConfigurationEditModal',
						backdrop: 'static',
						windowClass: 'juno-modal',
						resolve:
							{
								style: ctrl.componentStyle,
								faxAccount: () => faxAccount,
								masterFaxEnabledInbound: () => ctrl.masterFaxEnabledInbound,
								masterFaxEnabledOutbound: () => ctrl.masterFaxEnabledOutbound,
							}
					});

				modalInstance.result.then(
					// the object passed back on closing
					function success(updatedAccount)
					{
						if(isNewAcct)
						{
							// new accounts get added to the account list
							ctrl.faxAccountList.push(updatedAccount);
						}
						else if(updatedAccount == null) // deleted
						{
							// remove deleted account from the list
							ctrl.faxAccountList = ctrl.faxAccountList.filter((account) => (account.id !== faxAccount.id));
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

			ctrl.saveMasterFaxEnabledStateInbound = (value) =>
			{
				ctrl.masterFaxEnabledInbound = value;
				ctrl.setSystemProperty("masterFaxEnabledInbound", ctrl.masterFaxEnabledInbound);
				ctrl.updateMasterFaxDisabledStatus();
			};
			ctrl.saveMasterFaxEnabledStateOutbound = (value) =>
			{
				ctrl.masterFaxEnabledOutbound = value;
				ctrl.setSystemProperty("masterFaxEnabledOutbound", ctrl.masterFaxEnabledOutbound);
				ctrl.updateMasterFaxDisabledStatus();
			};

			ctrl.updateMasterFaxDisabledStatus = function updateMasterFaxDisabledStatus()
			{
				ctrl.masterFaxDisabled = !(ctrl.masterFaxEnabledInbound || ctrl.masterFaxEnabledOutbound);
			};
			ctrl.setSystemProperty = function setSystemProperty(key, value)
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
		}
	]
});