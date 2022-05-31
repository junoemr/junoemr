import FaxInboxSearchParams from "../../../../../lib/fax/model/FaxInboxSearchParams";
import ToastService from "../../../../../lib/alerts/service/ToastService";
import {
	JUNO_BUTTON_COLOR,
	JUNO_BUTTON_COLOR_PATTERN,
	LABEL_POSITION
} from "../../../../../common/components/junoComponentConstants";
import FaxInboxResult from "../../../../../lib/fax/model/FaxInboxResult";
import moment, {Moment} from "moment";
import FaxInboxService from "../../../../../lib/fax/service/FaxInboxService";
import PagedResponse from "../../../../../lib/common/response/PagedResponse";
import FaxAccount from "../../../../../lib/fax/model/FaxAccount";
import FaxAccountService from "../../../../../lib/fax/service/FaxAccountService";
import PhoneNumber from "../../../../../lib/common/model/PhoneNumber";
import ToastErrorHandler from "../../../../../lib/error/handler/ToastErrorHandler";

angular.module("Admin.Section.Fax").component('faxInbox', {
	templateUrl: 'src/admin/section/fax/components/faxInbox/faxInbox.jsp',
	bindings: {
		componentStyle: "<?",
		faxAccount: "<",
	},
	controller: [
		'NgTableParams',
		'providerService',
		'systemPreferenceService',
		function (NgTableParams,
		          providerService,
		          systemPreferenceService)
		{
			const ctrl = this;
			ctrl.toastService = new ToastService();
			ctrl.faxAccountService = new FaxAccountService(new ToastErrorHandler(true));
			ctrl.faxInboxService = new FaxInboxService();

			ctrl.LABEL_POSITION = LABEL_POSITION;
			ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
			ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

			ctrl.searchParams = new FaxInboxSearchParams();
			ctrl.searchParams.page = 1;
			ctrl.searchParams.perPage = 10;
			ctrl.searchParams.startDate = null;
			ctrl.searchParams.endDate = null;

			ctrl.nextPullTime = null;
			ctrl.tableParamsInbox = null;
			ctrl.loggedInProviderNo = null;
			ctrl.selectedFaxAccountId = null;
			ctrl.masterFaxEnabledInbound = false;
			ctrl.faxAccountList = [];
			ctrl.faxAccountOptions = [
				{
					value: null,
					label: "All",
					data: null,
				},
			];
			ctrl.inboxItemList = [];
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

					ctrl.nextPullTime = await ctrl.faxInboxService.getNextPullTime();
					ctrl.masterFaxEnabledInbound = await systemPreferenceService.isPreferenceEnabled("masterFaxEnabledInbound", ctrl.masterFaxEnabledInbound);

					ctrl.loadInboxItems();
				}
				catch (error)
				{
					console.error(error);
				}
				ctrl.initialized = true;
			}

			ctrl.loadInboxItems = (): void =>
			{
				ctrl.tableParamsInbox = new NgTableParams(
					{
						page: ctrl.searchParams.page,
						count: ctrl.searchParams.perPage,
						sorting: {
							DateSent: "desc"
						},
						total: 0,
					},
					{
						getData: function (ngTableParams)
						{
							let tableParams = ngTableParams.url();
							ctrl.searchParams.page = tableParams.page;
							ctrl.searchParams.perPage = tableParams.count;

							return ctrl.faxInboxService.getInbox(ctrl.searchParams).then(
								function success(response: PagedResponse<FaxInboxResult>)
								{
									ctrl.inboxItemList = response.body;
									ctrl.tableParamsInbox.total(response.total);
									return ctrl.inboxItemList;
								},
								function error(error)
								{
									console.error(error);
									ctrl.toastService.errorToast("Failed to load inbox");
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

			ctrl.openDocument = function(documentId: number): void
			{
				let openDocumentWindow = function()
				{
					let url = "../dms/showDocument.jsp";
					let params = "segmentID="+documentId+"&providerNo="+ ctrl.loggedInProviderNo + "&status=A&inWindow=true&chartView&demoName=";
					let windowName = "ShowDocument" + documentId;
					window.open(url + "?" + params, windowName, "scrollbars=1,width=1024,height=768");
				};

				// if the current provider number is unknown, retrieve it before opening the new window.
				if(ctrl.loggedInProviderNo == null)
				{
					providerService.getMe().then(
						function success(response)
						{
							ctrl.loggedInProviderNo = response.providerNo;
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

			ctrl.formatDateForDisplay = (date: Moment): string =>
			{
				return Juno.Common.Util.formatMomentDate(date) + " " + Juno.Common.Util.formatMomentTime(date);
			}

			ctrl.getFaxDisplayNumber = (phoneNumber: PhoneNumber): string =>
			{
				return phoneNumber ? phoneNumber.formattedForDisplay : "";
			}

			ctrl.nextPullTimeDisplay = () =>
			{
				if(ctrl.nextPullTime && ctrl.nextPullTime.isValid())
				{
					let now = moment();
					let minutes = ctrl.nextPullTime.diff(now, 'minutes');
					if(minutes < 0)
					{
						return "several minutes";
					}
					return (minutes + 1) + " minutes";
				}
			}
		}
	]}
);