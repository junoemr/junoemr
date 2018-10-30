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

		// ngTable object for storing search parameters
		controller.search = {
			page: 1,
			count: 10,
			sorting: {
				DateSent: "desc"
			}
		}
		;

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
				controller.search,
				{
					getData: function(params)
					{
						controller.search = params.url();
						return faxAccountService.getOutbox(controller.selectedFaxAccount.id, controller.search.page, controller.search.count).then(
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