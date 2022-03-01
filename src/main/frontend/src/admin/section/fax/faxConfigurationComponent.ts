import FaxAccountService from "../../../lib/fax/service/FaxAccountService";
import {LABEL_POSITION} from "../../../common/components/junoComponentConstants";

angular.module("Admin.Section.Fax").component('faxConfiguration', {
	templateUrl: 'src/admin/section/fax/faxConfiguration.jsp',
	bindings: {
		componentStyle: "<?",
	},
	controller: [
		"$uibModal",
		"providerService",
		"systemPreferenceService",
		function ($uibModal,
		          providerService,
		          systemPreferenceService)
		{
			const controller = this;
			controller.faxAccountService = new FaxAccountService();

			controller.LABEL_POSITION = LABEL_POSITION;

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

				controller.faxAccountService.getAccounts().then(
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

			controller.connectNewSRFaxAccount = () =>
			{
				controller.editFaxAccount(null);
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
						component: 'faxConfigurationEditModal',
						backdrop: 'static',
						windowClass: 'faxEditModal',
						resolve:
							{
								faxAccount: () => faxAccount,
								masterFaxEnabledInbound: () => controller.masterFaxEnabledInbound,
								masterFaxEnabledOutbound: () => controller.masterFaxEnabledOutbound,
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

			controller.saveMasterFaxEnabledStateInbound = (value) =>
			{
				controller.masterFaxEnabledInbound = value;
				controller.setSystemProperty("masterFaxEnabledInbound", controller.masterFaxEnabledInbound);
				controller.updateMasterFaxDisabledStatus();
			};
			controller.saveMasterFaxEnabledStateOutbound = (value) =>
			{
				controller.masterFaxEnabledOutbound = value;
				controller.setSystemProperty("masterFaxEnabledOutbound", controller.masterFaxEnabledOutbound);
				controller.updateMasterFaxDisabledStatus();
			};

			controller.updateMasterFaxDisabledStatus = function updateMasterFaxDisabledStatus()
			{
				controller.masterFaxDisabled = !(controller.masterFaxEnabledInbound || controller.masterFaxEnabledOutbound);
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
	]
});