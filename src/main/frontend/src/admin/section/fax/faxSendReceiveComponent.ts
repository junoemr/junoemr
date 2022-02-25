import FaxAccountService from "../../../lib/fax/service/FaxAccountService";
import moment from "moment";

angular.module("Admin.Section.Fax").component('faxSendReceive', {
	templateUrl: 'src/admin/section/fax/faxSendReceive.jsp',
	bindings: {
		componentStyle: "<?",
	},
	controller: [
		'NgTableParams',
		'providerService',
		"faxInboundService",
		"faxOutboundService",
		function (NgTableParams,
		          providerService,
		          faxInboundService,
		          faxOutboundService)
		{
			const controller = this;
			controller.faxAccountService = new FaxAccountService();

			controller.displayStatus = Object.freeze({
				all: {
					value: null,
					label: "All"
				},
				error: {
					value: "ERROR",
					label: "Error"
				},
				queued: {
					value: "QUEUED",
					label: "Queued"
				},
				inProgress: {
					value: "IN_PROGRESS",
					label: "In Progress"
				},
				integrationFailed: {
					value: "INTEGRATION_FAILED",
					label: "Failed"
				},
				integrationSuccess: {
					value: "INTEGRATION_SUCCESS",
					label: "Delivered"
				}
			});
			controller.archivedStatus = Object.freeze({
				all: {
					value: null,
					label: "All"
				},
				active: {
					value: false,
					label: "Active"
				},
				archived: {
					value: true,
					label: "Archived"
				}
			});

			controller.systemStatusEnum = Object.freeze({
				sent:"SENT",
				queued:"QUEUED",
				error:"ERROR"
			});
			controller.notificationStatusEnum = Object.freeze({
				notify:"NOTIFY",
				dismissed:"SILENT"
			});
			controller.tabEnum = Object.freeze({
				inbox:0,
				outbox:1
			});
			controller.activeTab = controller.tabEnum.outbox;
			controller.loggedInProviderNo = null;
			controller.displayNotificationColumn = false;

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
				endDate: null,
				displayStatus: controller.displayStatus.all,
				archivedStatus: controller.archivedStatus.all
			};

			controller.initialize = function()
			{
				controller.faxAccountService.getAccounts().then(
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
								combinedStatus: controller.outbox.displayStatus.value,
								archived: controller.outbox.archivedStatus.value
							};

							// @ts-ignore
							var searchListHelper = new Juno.Common.SearchListHelper(defaults, searchParams);
							return controller.faxAccountService.getOutbox(controller.selectedFaxAccount.id, searchListHelper).then(
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

							let defaults = {
								page: controller.inbox.search.page,
								perPage: controller.inbox.search.count
							};
							let searchParams = {
								startDate:  controller.formatOptionalDateParam(controller.inbox.startDate),
								endDate: controller.formatOptionalDateParam(controller.inbox.endDate),
							};

							// @ts-ignore
							var searchListHelper = new Juno.Common.SearchListHelper(defaults, searchParams);
							return controller.faxAccountService.getInbox(controller.selectedFaxAccount.id, searchListHelper).then(
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
				// the resend will create a new record for some resend attempts, in that case force a reload of the table items
				let requireFullRefresh = (outboxItem.combinedStatus === controller.displayStatus.integrationFailed.value);

				// set a temp status to provider feedback/disable resend button
				outboxItem.combinedStatus = controller.displayStatus.inProgress.value;
				faxOutboundService.resendOutboundFax(outboxItem.id).then(
					function success(response)
					{
						angular.copy(response, outboxItem);
						if(outboxItem.systemStatus === controller.systemStatusEnum.error)
						{
							alert(outboxItem.systemStatusMessage);
						}

						if(requireFullRefresh)
						{
							controller.loadOutboxItems();
						}
					},
					function error(error)
					{
						outboxItem.combinedStatus = controller.displayStatus.error.value;
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
			controller.getStatusDisplayLabel = function(statusEnum)
			{
				let displayLabel = null;
				switch(statusEnum)
				{
					case controller.displayStatus.error.value :                 displayLabel = controller.displayStatus.error.label; break;
					case controller.displayStatus.queued.value :                displayLabel = controller.displayStatus.queued.label; break;
					case controller.displayStatus.inProgress.value :            displayLabel = controller.displayStatus.inProgress.label; break;
					case controller.displayStatus.integrationFailed.value :     displayLabel = controller.displayStatus.integrationFailed.label; break;
					case controller.displayStatus.integrationSuccess.value :    displayLabel = controller.displayStatus.integrationSuccess.label; break;
					default: displayLabel = "Unknown";
				}
				return displayLabel;
			};

			controller.initialize();
		}
	]
});