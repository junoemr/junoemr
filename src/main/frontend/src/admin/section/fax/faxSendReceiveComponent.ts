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
			ctrl.LABEL_POSITION = LABEL_POSITION;

			ctrl.tabEnum = Object.freeze({
				inbox: 0,
				outbox: 1
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
					case ctrl.tabEnum.inbox:
					{
						$state.go("admin.faxSendReceive.inbox");
						break;
					}
					case ctrl.tabEnum.outbox:
					{
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