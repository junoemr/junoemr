import FaxAccountService from "../../../lib/fax/service/FaxAccountService";
import {LABEL_POSITION} from "../../../common/components/junoComponentConstants";

angular.module("Admin.Section.Fax").component('faxSendReceive', {
	templateUrl: 'src/admin/section/fax/faxSendReceive.jsp',
	bindings: {
		componentStyle: "<?",
	},
	controller: [
		'$state',
		function ($state)
		{
			const ctrl = this;
			ctrl.faxAccountService = new FaxAccountService();
			ctrl.LABEL_POSITION = LABEL_POSITION;

			ctrl.tabEnum = Object.freeze({
				inbox:0,
				outbox:1
			});
			ctrl.activeTab = null;

			ctrl.$onInit = () =>
			{
				if($state.includes("**.inbox"))
				{
					ctrl.changeTab(ctrl.tabEnum.inbox);
				}
				else
				{
					ctrl.changeTab(ctrl.tabEnum.outbox);
				}
			}

			ctrl.changeTab = function(tabId)
			{
				ctrl.activeTab = tabId;
				switch (tabId)
				{
					case 0: {
						$state.go("admin.faxSendReceive.inbox");
						break;
					}
					case 1: {
						$state.go("admin.faxSendReceive.outbox");
						break;
					}
				}
			};

			ctrl.tabActiveClass = (index: number): string[] =>
			{
				return (index === ctrl.activeTab) ? ["active"] : [];
			}
		}
	]
});