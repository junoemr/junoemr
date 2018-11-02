angular.module("Admin.Integration.Fax").controller('Admin.Integration.Fax.FaxSendReceiveController', [
	'NgTableParams',
	"faxAccountService",
	"faxOutboundService",
	function (NgTableParams,
	          faxAccountService,
	          faxOutboundService)
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
		};

		controller.initialize = function()
		{
			faxAccountService.listAccounts().then(
				function success(response)
				{
					controller.faxAccountList = response;
					if(controller.faxAccountList.length > 0)
					{
						controller.selectedFaxAccount = controller.faxAccountList[0];
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

		controller.resendFax = function(outboxItem)
		{
			outboxItem.systemStatus = 'RESEND';
			faxOutboundService.resendOutboundFax(outboxItem.id).then(
				function success(response)
				{
					angular.copy(response, outboxItem);
				},
				function error(error)
				{
					outboxItem.systemStatus = 'ERROR';
					console.error(error);
					alert(error);
				}
			);
		};

		controller.initialize();
	}
]);