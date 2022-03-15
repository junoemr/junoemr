import FaxAccountService from "../../../lib/fax/service/FaxAccountService";
import {
	JUNO_BUTTON_COLOR,
	JUNO_BUTTON_COLOR_PATTERN,
	LABEL_POSITION
} from "../../../common/components/junoComponentConstants";
import {SecurityPermissions} from "../../../common/security/securityConstants";
import FaxAccount from "../../../lib/fax/model/FaxAccount";
import ToastService from "../../../lib/alerts/service/ToastService";
import {SYSTEM_PROPERTIES} from "../../../common/services/systemPreferenceServiceConstants";

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
			ctrl.toastService = new ToastService();

			ctrl.LABEL_POSITION = LABEL_POSITION;
			ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
			ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

			ctrl.faxAccountList = [];
			ctrl.faxAccountSelectStates = [];
			ctrl.loggedInProvider = null;
			ctrl.masterFaxDisabled = true;
			ctrl.masterFaxEnabledInbound = false;
			ctrl.masterFaxEnabledOutbound = false;
			ctrl.activeAccount = null;
			ctrl.initialized = false;

			ctrl.$onInit = async () =>
			{
				let responses = await Promise.all([
					providerService.getMe(),
					systemPreferenceService.isPreferenceEnabled("masterFaxEnabledInbound", ctrl.masterFaxEnabledInbound),
					systemPreferenceService.isPreferenceEnabled("masterFaxEnabledOutbound", ctrl.masterFaxEnabledOutbound),
					ctrl.faxAccountService.getAccounts(),
					ctrl.faxAccountService.getActiveAccount(),
				]);

				ctrl.loggedInProvider = responses[0];
				ctrl.masterFaxEnabledInbound = responses[1];
				ctrl.masterFaxEnabledOutbound = responses[2];
				ctrl.faxAccountList = responses[3].body;
				ctrl.activeAccount = responses[4];

				ctrl.updateMasterFaxDisabledStatus();

				// initialize selection checkboxes
				ctrl.faxAccountList.forEach((account: FaxAccount) =>
				{
					ctrl.faxAccountSelectStates[account.id] = false;
				});

				if(ctrl.activeAccount)
				{
					ctrl.faxAccountSelectStates[ctrl.activeAccount.id] = true;
				}
				ctrl.initialized = true;
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
						windowClass: 'juno-modal tall',
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
					function success(updatedAccount: FaxAccount)
					{
						if(isNewAcct)
						{
							// new accounts get added to the account list
							ctrl.faxAccountList.push(updatedAccount);
							if(ctrl.faxAccountList.length === 1)
							{
								ctrl.faxAccountSelectStates[updatedAccount.id] = true;
								ctrl.setActiveAccount(true, updatedAccount);
							}
						}
						else if(updatedAccount == null) // deleted
						{
							// remove deleted account from the list
							ctrl.faxAccountList = ctrl.faxAccountList.filter((account) => (account.id !== faxAccount.id));

							if(ctrl.faxAccountList.length > 0)
							{
								let firstActive = ctrl.faxAccountList[0];
								ctrl.faxAccountSelectStates[firstActive.id] = true;
								ctrl.setActiveAccount(true, firstActive);
							}
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

			ctrl.setActiveAccount = (value: boolean, faxAccount: FaxAccount) =>
			{
				if(value)
				{
					ctrl.activeAccount = faxAccount;

					// set all other selected states to false (unchecked)
					ctrl.faxAccountList.filter((account: FaxAccount) => account.id !== faxAccount.id)
						.forEach((account: FaxAccount) => ctrl.faxAccountSelectStates[account.id] = false);
					ctrl.setSystemProperty(SYSTEM_PROPERTIES.ACTIVE_FAX_ACCOUNT, faxAccount.id);
				}
				else
				{
					ctrl.activeAccount = null;
				}
			};

			ctrl.updateMasterFaxDisabledStatus = function updateMasterFaxDisabledStatus()
			{
				ctrl.masterFaxDisabled = !(ctrl.masterFaxEnabledInbound || ctrl.masterFaxEnabledOutbound);
			};

			ctrl.setSystemProperty = function setSystemProperty(key, value)
			{
				systemPreferenceService.setPreference(key, value)
					.catch((error) =>
						{
							console.error(error);
							ctrl.toastService.errorToast("Failed to save preference setting.");
						}
					);
			};

			ctrl.toRingCentralLogin = () =>
			{
				location.href = "../oauth"		// TODO: figure out how to bind a better name later, ie: /fax/ringcentral/oauth
			}

		}
	]
});