angular.module("Admin.Integration.Fax").controller('Admin.Integration.Fax.FaxSendReceiveController', [
	'NgTableParams',
	'providerService',
	"faxAccountService",
	"faxInboundService",
	"faxOutboundService",
	function (NgTableParams,
	          providerService,
	          faxAccountService,
	          faxInboundService,
	          faxOutboundService)
	{
		var controller = this;
		controller.systemStatusEnum = Object.freeze({"sent":"SENT", "queued":"QUEUED", "error":"ERROR"});
		controller.notificationStatusEnum = Object.freeze({"notify":"NOTIFY", "dismissed":"SILENT"});
		controller.tabEnum = Object.freeze({"inbox":0, "outbox":1});
		controller.activeTab = controller.tabEnum.outbox;
		controller.loggedInProviderNo = null;

		controller.nextPullTime = null;
		controller.nextPushTime = null;

		controller.selectedFaxAccount = null;
		controller.faxAccountList = [];
		controller.outboxItemList = [];
		controller.inboxItemList = [];

		// ngTable object for storing search parameters
		controller.inbox =
		{
			search: {
				page: 1,
				count: 10,
				sorting: {
					DateSent: "desc"
				}
			},
			startDate: null,
			endDate: null
		};
		// ngTable object for storing search parameters
		controller.outbox =
			{
				search: {
					page: 1,
					count: 10,
					sorting: {
						DateSent: "desc"
					}
				},
				startDate: null,
				endDate: null
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
				controller.outbox.search,
				{
					getData: function(params)
					{
						controller.outbox.search = params.url();

						let defaults = {
							page: controller.outbox.search.page,
							perPage: controller.outbox.search.count
						};
						let searchParams = {
							startDate:  controller.formatOptionalDateParam(controller.outbox.startDate),
							endDate: controller.formatOptionalDateParam(controller.outbox.endDate),
						};
						var searchListHelper = new Juno.Common.SearchListHelper(defaults, searchParams);
						return faxAccountService.getOutbox(controller.selectedFaxAccount.id, searchListHelper).then(
							function success(response)
							{
								controller.outboxItemList = response.data;
								controller.tableParamsOutbox.total(response.meta.total);
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
			controller.loadNextPushTime();
		};

		controller.loadInboxItems = function ()
		{
			controller.tableParamsInbox = new NgTableParams(
				controller.inbox.search,
				{
					getData: function (params)
					{
						controller.inbox.search = params.url();
						console.info(controller.inbox);

						let defaults = {
							page: controller.inbox.search.page,
							perPage: controller.inbox.search.count
						};
						let searchParams = {
							startDate:  controller.formatOptionalDateParam(controller.inbox.startDate),
							endDate: controller.formatOptionalDateParam(controller.inbox.endDate),
						};
						var searchListHelper = new Juno.Common.SearchListHelper(defaults, searchParams);
						return faxAccountService.getInbox(controller.selectedFaxAccount.id, searchListHelper).then(
							function success(response)
							{
								controller.inboxItemList = response.data;
								controller.tableParamsInbox.total(response.meta.total);
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
			controller.loadNextPullTime();
		};

		controller.resendFax = function(outboxItem)
		{
			outboxItem.systemStatus = 'RESEND';
			faxOutboundService.resendOutboundFax(outboxItem.id).then(
				function success(response)
				{
					angular.copy(response, outboxItem);
					console.info(outboxItem);
					if(outboxItem.systemStatus === controller.systemStatusEnum.error)
					{
						alert(outboxItem.systemStatusMessage);
					}
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
					controller.nextPullTime = null;
					console.error(error);
					alert(error);
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
					controller.nextPushTime = null;
					console.error(error);
					alert(error);
				}
			);
		};

		controller.changeTab = function(tabId)
		{
			controller.activeTab = tabId;
		};

		controller.dismissNotification = function(outboxItem)
		{
			faxOutboundService.setNotificationStatus(outboxItem.id, controller.notificationStatusEnum.dismissed).then(
				function success(response)
				{
					angular.copy(response, outboxItem);
				},
				function error(error)
				{
					console.error(error);
					alert(error);
				}
			);
		};

		controller.archive = function(outboxItem)
		{
			faxOutboundService.archive(outboxItem.id).then(
				function success(response)
				{
					angular.copy(response, outboxItem);
				},
				function error(error)
				{
					console.error(error);
					alert(error);
				}
			);
		};

		controller.openDocument = function(documentId)
		{
			let openDocumentWindow = function()
			{
				let url = "../dms/showDocument.jsp";
				let params = "segmentID="+documentId+"&providerNo="+ controller.loggedInProviderNo + "&status=A&inWindow=true&chartView&demoName=";
				let windowName = "ShowDocument" + documentId;
				window.open(url + "?" + params, windowName, "scrollbars=1,width=1024,height=768");
			};

			// if the current provider number is unknown, retrieve it before opening the new window.
			if(controller.loggedInProviderNo == null)
			{
				providerService.getMe().then(
					function success(response)
					{
						controller.loggedInProviderNo = response.providerNo;
						openDocumentWindow();
					},
					function error(error)
					{
						console.error(error);
					}
				)
			}
			else
			{
				openDocumentWindow();
			}
		};

		controller.viewDownloadFile = function(outboundId)
		{
			let url = faxOutboundService.getDownloadUrl(outboundId);
			let windowName = "ViewFaxFile" + outboundId;
			window.open(url, windowName, "scrollbars=1,width=1024,height=768");
		};

		controller.formatOptionalDateParam = function(dateObj)
		{
			return Juno.Common.Util.isUndefinedOrNull(dateObj)? null : moment(dateObj).format('YYYY-MM-DD')
		};

		controller.initialize();
	}
]);