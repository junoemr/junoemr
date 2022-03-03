import FaxInboxSearchParams from "../../../../../lib/fax/model/FaxInboxSearchParams";
import ToastService from "../../../../../lib/alerts/service/ToastService";
import {
	JUNO_BUTTON_COLOR,
	JUNO_BUTTON_COLOR_PATTERN,
	LABEL_POSITION
} from "../../../../../common/components/junoComponentConstants";
import FaxAccountService from "../../../../../lib/fax/service/FaxAccountService";
import FaxInboxResult from "../../../../../lib/fax/model/FaxInboxResult";
import {Moment} from "moment";

angular.module("Admin.Section.Fax").component('faxInbox', {
	templateUrl: 'src/admin/section/fax/components/faxInbox/faxInbox.jsp',
	bindings: {
		componentStyle: "<?",
		faxAccount: "<",
	},
	controller: [
		'NgTableParams',
		'providerService',
		"faxInboundService",
		function (NgTableParams,
		          providerService,
		          faxInboundService)
		{
			const ctrl = this;
			ctrl.toastService = new ToastService();
			ctrl.faxAccountService = new FaxAccountService();

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

			ctrl.$onInit = () =>
			{
				ctrl.searchParams.faxAccount = ctrl.faxAccount;
			}

			ctrl.loadInboxItems = (): void =>
			{
				ctrl.tableParamsInbox = new NgTableParams(
					{
						page: ctrl.searchParams.page,
						count: ctrl.searchParams.perPage,
						sorting: {
							DateSent: "desc"
						}
					},
					{
						getData: function (ngTableParams)
						{
							let tableParams = ngTableParams.url();
							ctrl.searchParams.page = tableParams.page;
							ctrl.searchParams.perPage = tableParams.count;

							return ctrl.faxAccountService.getInbox(ctrl.searchParams).then(
								function success(response: FaxInboxResult[])
								{
									//todo search results object with meta info?
									ctrl.inboxItemList = response;
									// ctrl.tableParamsInbox.total(response.meta.total);
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
				ctrl.loadNextPullTime();
			};

			ctrl.loadNextPullTime = (): void =>
			{
				faxInboundService.getNextPullTime().then(
					function success(response)
					{
						ctrl.nextPullTime = response;
					},
					function error(error)
					{
						ctrl.nextPullTime = null;
						console.error(error);
						ctrl.toastService.errorToast("Failed to load inbox polling time");
					}
				);
			};

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
		}
	]}
);