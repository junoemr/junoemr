angular.module("Admin.Integration.Fax").controller('Admin.Integration.Fax.FaxSendReceiveController', [
	'NgTableParams',
	"faxAccountService",
	"faxInboundService",
	"faxOutboundService",
	function (NgTableParams,
	          faxAccountService,
	          faxInboundService,
	          faxOutboundService)
	{
		var controller = this;
		controller.systemStatusEnum = Object.freeze({"sent":"SENT", "queued":"QUEUED", "error":"ERROR"});
		controller.tabEnum = Object.freeze({"inbox":0, "outbox":1});
		controller.activeTab = controller.tabEnum.outbox;

		controller.nextPullTime = null;
		controller.nextPushTime = null;

		controller.selectedFaxAccount = null;
		controller.faxAccountList = [];
		controller.outboxItemList = [];
		controller.inboxItemList = [];

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
			controller.loadNextPushTime();
			controller.loadNextPullTime();
		};

		controller.loadOutboxItems = function()
		{
			controller.tableParamsOutbox = new NgTableParams(
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

		controller.loadInboxItems = function ()
		{
			controller.tableParamsInbox = new NgTableParams(
				controller.search,
				{
					getData: function (params)
					{
						controller.search = params.url();
						return faxAccountService.getInbox(controller.selectedFaxAccount.id, controller.search.page, controller.search.count).then(
							function success(response)
							{
								controller.inboxItemList = response;
								return controller.inboxItemList;
							},
							function error(error)
							{
								console.error(error);
								alert("Failed to load inbox");
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
					outboxItem.systemStatus = controller.systemStatusEnum.error;
					console.error(error);
					alert(error);
				}
			);
		};

		controller.loadNextPullTime = function()
		{
			faxInboundService.getNextPullTime().then(
				function success(response)
				{
					controller.nextPullTime = response;
				},
				function error(error)
				{
					console.error(error);
					alert(error);
					controller.nextPullTime = null;
				}
			);
		};
		controller.loadNextPushTime = function()
		{
			faxOutboundService.getNextPushTime().then(
				function success(response)
				{
					controller.nextPushTime = response;
				},
				function error(error)
				{
					console.error(error);
					alert(error);
					controller.nextPushTime = null;
				}
			);
		};

		controller.changeTab = function(tabId)
		{
			controller.activeTab = tabId;
		};

		controller.initialize();
	}
]);