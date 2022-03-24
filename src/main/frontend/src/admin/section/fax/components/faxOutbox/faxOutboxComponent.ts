import ToastService from "../../../../../lib/alerts/service/ToastService";
import FaxOutboxSearchParams from "../../../../../lib/fax/model/FaxOutboxSearchParams";
import FaxOutboxResult from "../../../../../lib/fax/model/FaxOutboxResult";
import {
	JUNO_BUTTON_COLOR,
	JUNO_BUTTON_COLOR_PATTERN,
	LABEL_POSITION
} from "../../../../../common/components/junoComponentConstants";
import {FaxStatusCombinedType} from "../../../../../lib/fax/model/FaxStatusCombinedType";
import moment, {Moment} from "moment";
import FaxOutboxService from "../../../../../lib/fax/service/FaxOutboxService";
import PagedResponse from "../../../../../lib/common/response/PagedResponse";
import {FaxNotificationStatusType} from "../../../../../lib/fax/model/FaxNotificationStatusType";
import FaxAccount from "../../../../../lib/fax/model/FaxAccount";
import FaxAccountService from "../../../../../lib/fax/service/FaxAccountService";
import PhoneNumber from "../../../../../lib/common/model/PhoneNumber";

angular.module("Admin.Section.Fax").component('faxOutbox', {
	templateUrl: 'src/admin/section/fax/components/faxOutbox/faxOutbox.jsp',
	bindings: {
		componentStyle: "<?",
		faxAccount: "<",
	},
	controller: [
		'NgTableParams',
		'systemPreferenceService',
		function (NgTableParams,
		          systemPreferenceService)
		{
			const ctrl = this;
			ctrl.toastService = new ToastService();
			ctrl.faxAccountService = new FaxAccountService();
			ctrl.faxOutboxService = new FaxOutboxService();

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
					label: "Integration Failed"
				},
				{
					value: FaxStatusCombinedType.IntegrationSuccess,
					label: "Delivered"
				},
			]);

			ctrl.archivedOptions = Object.freeze({
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

			ctrl.searchParams = new FaxOutboxSearchParams();
			ctrl.searchParams.page = 1;
			ctrl.searchParams.perPage = 10;
			ctrl.searchParams.startDate = null;
			ctrl.searchParams.endDate = null;
			ctrl.searchParams.combinedStatus = null;
			ctrl.searchParams.archived = null;

			ctrl.nextPushTime = null;
			ctrl.displayNotificationColumn = false;
			ctrl.selectedFaxAccountId = null;
			ctrl.faxAccountList = [];
			ctrl.faxAccountOptions = [
				{
					value: null,
					label: "All",
					data: null,
				},
			];
			ctrl.initialized = false;

			ctrl.$onInit = async () =>
			{
				try
				{
					ctrl.faxAccountList = (await ctrl.faxAccountService.getAccounts()).body;
					ctrl.faxAccountList.map((faxAccount: FaxAccount) =>
					{
						return {
							value: faxAccount.id,
							label: faxAccount.displayName,
							data: faxAccount,
						}
					}).forEach((option: object) => ctrl.faxAccountOptions.push(option));

					ctrl.nextPushTime = await ctrl.faxOutboxService.getNextPushTime();
					ctrl.masterFaxEnabledOutbound = await systemPreferenceService.isPreferenceEnabled("masterFaxEnabledOutbound", ctrl.masterFaxEnabledOutbound);

					ctrl.loadOutboxItems();
				}
				catch (error)
				{
					console.error(error);
				}
				ctrl.initialized = true;
			}

			ctrl.loadOutboxItems = function()
			{
				ctrl.tableParamsOutbox = new NgTableParams(
					{
						page: ctrl.searchParams.page,
						count: ctrl.searchParams.perPage,
						sorting: {
							DateSent: "desc"
						},
						total: 0,
					},
					{
						getData: function(ngTableParams)
						{
							let tableParams = ngTableParams.url();
							ctrl.searchParams.page = tableParams.page;
							ctrl.searchParams.perPage = tableParams.count;

							return ctrl.faxOutboxService.getOutbox(ctrl.searchParams).then(
								function success(response: PagedResponse<FaxOutboxResult>)
								{
									ctrl.outboxItemList = response.body;
									ctrl.tableParamsOutbox.total(response.total);
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
			};

			ctrl.updateSelectedAccount = (value: number, option: any) =>
			{
				ctrl.selectedFaxAccountId = value;
				ctrl.searchParams.faxAccount = option.data ? option.data : null;
			}

			ctrl.resendFax = (outboxItem: FaxOutboxResult): void =>
			{
				// the resend will create a new record for some resend attempts, in that case force a reload of the table items
				let requireFullRefresh = outboxItem.isCombinedStatusIntegrationFailed;

				// set a temp status to provider feedback/disable resend button
				outboxItem.combinedStatus = FaxStatusCombinedType.InProgress;
				ctrl.faxOutboxService.resendOutboundFax(outboxItem.id).then(
					function success(response: FaxOutboxResult)
					{
						angular.copy(response, outboxItem);
						if(outboxItem.isInternalStatusError)
						{
							ctrl.toastService.errorToast(outboxItem.systemStatusMessage);
						}

						if(requireFullRefresh)
						{
							ctrl.loadOutboxItems();
						}
					},
					function error(error)
					{
						outboxItem.combinedStatus = FaxStatusCombinedType.Error;
						console.error(error);
						ctrl.toastService.errorToast("Failed to queue fax for resend");
					}
				);
			};

			ctrl.dismissNotification = function(outboxItem: FaxOutboxResult)
			{
				ctrl.faxOutboxService.setNotificationStatus(outboxItem.id, FaxNotificationStatusType.Silent).then(
					function success(response)
					{
						angular.copy(response, outboxItem);
					},
					function error(error)
					{
						console.error(error);
						ctrl.toastService.errorToast("Failed to mark item as read");
					}
				);
			};

			ctrl.archive = function(outboxItem: FaxOutboxResult)
			{
				ctrl.faxOutboxService.archive(outboxItem.id).then(
					function success(response)
					{
						angular.copy(response, outboxItem);
					},
					function error(error)
					{
						console.error(error);
						ctrl.toastService.errorToast("Failed to archive record");
					}
				);
			};

			ctrl.viewDownloadFile = (outboundId: number) =>
			{
				ctrl.faxOutboxService.download(outboundId);
			};

			ctrl.getStatusDisplayLabel = (combinedStatus: FaxStatusCombinedType) =>
			{
				return ctrl.statusFilterOptions.find((option) => combinedStatus === option.value).label;
			}

			ctrl.getFaxDisplayNumber = (phoneNumber: PhoneNumber): string =>
			{
				return phoneNumber ? phoneNumber.formattedForDisplay : "";
			}

			ctrl.getStatusIcon = (combinedStatus: FaxStatusCombinedType) =>
			{
				switch (combinedStatus)
				{
					case FaxStatusCombinedType.Queued: return "icon-clock";
					case FaxStatusCombinedType.InProgress: return "icon-send";
					case FaxStatusCombinedType.Error:
					case FaxStatusCombinedType.IntegrationFailed: return "icon-private";
					case FaxStatusCombinedType.IntegrationSuccess: return "icon-check";
				}
			}

			ctrl.getBadgeClasses = (combinedStatus: FaxStatusCombinedType) =>
			{
				switch (combinedStatus)
				{
					case FaxStatusCombinedType.Queued:return "badge-queued";
					case FaxStatusCombinedType.InProgress:return "badge-in-progress";
					case FaxStatusCombinedType.Error:
					case FaxStatusCombinedType.IntegrationFailed:return "badge-error";
					case FaxStatusCombinedType.IntegrationSuccess:return "badge-complete";
				}
			}

			ctrl.formatDateForDisplay = (date: Moment): string =>
			{
				if(date && date.isValid())
				{
					return Juno.Common.Util.formatMomentDate(date) + " " + Juno.Common.Util.formatMomentTime(date);
				}
				return "";
			}

			ctrl.hideResendButton = (outboxItem: FaxOutboxResult) =>
			{
				return outboxItem.archived
					|| outboxItem.isCombinedStatusInQueued
					|| outboxItem.isCombinedStatusInProgress
					|| outboxItem.isCombinedStatusSent
			}

			ctrl.nextPushTimeDisplay = () =>
			{
				if(ctrl.nextPushTime && ctrl.nextPushTime.isValid())
				{
					let now = moment();
					let minutes = ctrl.nextPushTime.diff(now, 'minutes');
					minutes = (minutes < 0) ? 0 : minutes;
					return (minutes + 1) + " minutes";
				}
			}
		}
	]}
);