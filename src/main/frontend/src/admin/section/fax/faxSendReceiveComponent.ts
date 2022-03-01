import FaxAccountService from "../../../lib/fax/service/FaxAccountService";
import {LABEL_POSITION} from "../../../common/components/junoComponentConstants";
import FaxAccount from "../../../lib/fax/model/FaxAccount";

angular.module("Admin.Section.Fax").component('faxSendReceive', {
	templateUrl: 'src/admin/section/fax/faxSendReceive.jsp',
	bindings: {
		componentStyle: "<?",
	},
	controller: [
		function ()
		{
			const ctrl = this;
			ctrl.faxAccountService = new FaxAccountService();
			ctrl.LABEL_POSITION = LABEL_POSITION;

			ctrl.tabEnum = Object.freeze({
				inbox:0,
				outbox:1
			});
			ctrl.activeTab = ctrl.tabEnum.outbox;

			ctrl.selectedFaxAccountId = null;
			ctrl.selectedFaxAccount = null;
			ctrl.faxAccountOptions = [];

			ctrl.$onInit = async () =>
			{
				try
				{
					ctrl.faxAccountList = await ctrl.faxAccountService.getAccounts();
					ctrl.faxAccountOptions = ctrl.faxAccountList.map((faxAccount: FaxAccount) =>
					{
						return {
							value: faxAccount.id,
							label: faxAccount.displayName,
							data: faxAccount,
						}
					});
					if(ctrl.faxAccountOptions.length > 0)
					{
						ctrl.updateSelectedAccount(ctrl.faxAccountOptions[0].value, ctrl.faxAccountOptions[0]);
					}
				}
				catch (error)
				{
					console.error(error);
				}
			};

			ctrl.updateSelectedAccount = (value: number, option: any) =>
			{
				ctrl.selectedFaxAccountId = value;
				ctrl.selectedFaxAccount = option.data;
			}

			ctrl.changeTab = function(tabId)
			{
				ctrl.activeTab = tabId;
			};
		}
	]
});