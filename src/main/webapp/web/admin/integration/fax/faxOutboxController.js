angular.module("Admin.Integration.Fax").controller('Admin.Integration.Fax.FaxOutboxController', [
	'NgTableParams',
	"faxAccountService",
	function (NgTableParams,
	          faxAccountService)
	{
		var controller = this;

		controller.selectedFaxAccount = null;
		controller.faxAccountList = [];
		controller.outboxItemList = [];

		controller.page = 1;
		controller.perPage = 10;

		controller.initialize = function()
		{
			faxAccountService.listAccounts().then(
				function success(response)
				{
					controller.faxAccountList = response;
					if(controller.faxAccountList.length > 0)
					{
						controller.selectedFaxAccount = controller.faxAccountList[0];
						controller.loadOutboxItems();
					}
				},
				function error(error)
				{
					console.error(error);
				}
			);
		};

		controller.loadOutboxItems = function()
		{
			controller.tableParams = new NgTableParams(
				{
					page: controller.page,
					count: controller.perPage,
					sorting: {
						DateSent: "desc"
					}
				},
				{
					getData: function(params) {
						return faxAccountService.getOutbox(controller.selectedFaxAccount.id, controller.page, controller.perPage).then(
							function success(response)
							{
								controller.outboxItemList = response;
								return controller.outboxItemList;
							},
							function error(error)
							{
								console.error(error);
								alert("Failed to load outbox");
							}
						);
					}
				}
			);
		};

		controller.initialize();
	}
]);