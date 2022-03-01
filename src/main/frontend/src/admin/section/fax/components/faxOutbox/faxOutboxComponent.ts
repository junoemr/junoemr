import ToastService from "../../../../../lib/alerts/service/ToastService";
import FaxOutboxSearchParams from "../../../../../lib/fax/model/FaxOutboxSearchParams";
import FaxOutboxResult from "../../../../../lib/fax/model/FaxOutboxResult";
import {
	JUNO_BUTTON_COLOR,
	JUNO_BUTTON_COLOR_PATTERN,
	LABEL_POSITION
} from "../../../../../common/components/junoComponentConstants";
import FaxAccountService from "../../../../../lib/fax/service/FaxAccountService";
import {FaxStatusCombinedType} from "../../../../../lib/fax/model/FaxStatusCombinedType";
import {Moment} from "moment";

angular.module("Admin.Section.Fax").component('faxOutbox', {
	templateUrl: 'src/admin/section/fax/components/faxOutbox/faxOutbox.jsp',
	bindings: {
		componentStyle: "<?",
		faxAccount: "<",
	},
	controller: [
		'NgTableParams',
		'providerService',
		"faxOutboundService",
		function (NgTableParams,
		          providerService,
		          faxOutboundService)
		{
			const ctrl = this;
			ctrl.toastService = new ToastService();
			ctrl.faxAccountService = new FaxAccountService();

			ctrl.LABEL_POSITION = LABEL_POSITION;
			ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
			ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

			ctrl.statusFilterOptions = Object.freeze([
				{
					value: null,
					label: "All"
				},
				{
					value: FaxStatusCombinedType.Error,
					label: "Error"
				},
				{
					value: FaxStatusCombinedType.Queued,
					label: "Queued"
				},
				{
					value: FaxStatusCombinedType.InProgress,
					label: "In Progress"
				},
				{
					value: FaxStatusCombinedType.IntegrationFailed,
					label: "Failed"
				},
				{
					value: FaxStatusCombinedType.IntegrationSuccess,
					label: "Delivered"
				},
			]);

			ctrl.archivedStatus = Object.freeze({
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

			ctrl.systemStatusEnum = Object.freeze({
				sent:"SENT",
				queued:"QUEUED",
				error:"ERROR"
			});
			ctrl.notificationStatusEnum = Object.freeze({
				notify:"NOTIFY",
				dismissed:"SILENT"
			});

			ctrl.searchParams = new FaxOutboxSearchParams();
			ctrl.searchParams.page = 1;
			ctrl.searchParams.perPage = 10;
			ctrl.searchParams.startDate = null;
			ctrl.searchParams.endDate = null;
			ctrl.searchParams.combinedStatus = null;
			ctrl.searchParams.archived = false;

			ctrl.nextPushTime = null;
			ctrl.displayNotificationColumn = false;

			ctrl.$onInit = () =>
			{
				ctrl.searchParams.faxAccount = ctrl.faxAccount;
			}

			ctrl.loadOutboxItems = function()
			{
				ctrl.tableParamsOutbox = new NgTableParams(
					{
						page: ctrl.searchParams.page,
						count: ctrl.searchParams.perPage,
						sorting: {
							DateSent: "desc"
						}
					},
					{
						getData: function(ngTableParams)
						{
							let tableParams = ngTableParams.url();
							ctrl.searchParams.page = tableParams.page;
							ctrl.searchParams.perPage = tableParams.count;

							return ctrl.faxAccountService.getOutbox(ctrl.searchParams).then(
								function success(response: FaxOutboxResult[])
								{
									ctrl.outboxItemList = response;
									// ctrl.tableParamsOutbox.total(response.meta.total);
									return ctrl.outboxItemList;
								},
								function error(error)
								{
									console.error(error);
									ctrl.toastService.errorToast("Failed to load outbox");
								}
							);
						}
					}
				);
				ctrl.loadNextPushTime();
			};

			ctrl.loadNextPushTime = function()
			{
				faxOutboundService.getNextPushTime().then(
					function success(response)
					{
						ctrl.nextPushTime = response;
					},
					function error(error)
					{
						ctrl.nextPushTime = null;
						console.error(error);
						ctrl.toastService.errorToast("Failed to load outbox polling time");
					}
				);
			};

			ctrl.resendFax = function(outboxItem)
			{
				// the resend will create a new record for some resend attempts, in that case force a reload of the table items
				let requireFullRefresh = (outboxItem.combinedStatus === ctrl.displayStatus.integrationFailed.value);

				// set a temp status to provider feedback/disable resend button
				outboxItem.combinedStatus = ctrl.displayStatus.inProgress.value;
				faxOutboundService.resendOutboundFax(outboxItem.id).then(
					function success(response)
					{
						angular.copy(response, outboxItem);
						if(outboxItem.systemStatus === ctrl.systemStatusEnum.error)
						{
							alert(outboxItem.systemStatusMessage);
						}

						if(requireFullRefresh)
						{
							ctrl.loadOutboxItems();
						}
					},
					function error(error)
					{
						outboxItem.combinedStatus = ctrl.displayStatus.error.value;
						console.error(error);
						ctrl.toastService.errorToast("Failed to queue fax for resend");
					}
				);
			};

			ctrl.dismissNotification = function(outboxItem)
			{
				faxOutboundService.setNotificationStatus(outboxItem.id, ctrl.notificationStatusEnum.dismissed).then(
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

			ctrl.archive = function(outboxItem)
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

			ctrl.viewDownloadFile = function(outboundId)
			{
				let url = faxOutboundService.getDownloadUrl(outboundId);
				let windowName = "ViewFaxFile" + outboundId;
				window.open(url, windowName, "scrollbars=1,width=1024,height=768");
			};

			ctrl.getStatusDisplayLabel = (combinedStatus: FaxStatusCombinedType) =>
			{
				return ctrl.statusFilterOptions.find((option) => combinedStatus === option.value).label;
			}

			ctrl.formatDateForDisplay = (date: Moment): string =>
			{
				if(date && date.isValid())
				{
					return Juno.Common.Util.formatMomentDate(date) + " " + Juno.Common.Util.formatMomentTime(date);
				}
				return "";
			}

			ctrl.hideResendButton = (item: FaxOutboxResult) =>
			{
				return item.archived ||
					(item.combinedStatus != FaxStatusCombinedType.Queued
					&& item.combinedStatus != FaxStatusCombinedType.Error
					&& item.combinedStatus != FaxStatusCombinedType.IntegrationFailed)
			}
		}
	]}
);