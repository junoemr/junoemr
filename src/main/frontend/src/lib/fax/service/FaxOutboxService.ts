import {FaxOutboundApi} from "../../../../generated";
import {API_BASE_PATH} from "../../constants/ApiConstants";
import moment, {Moment} from "moment";
import FaxOutboxResultToModelConverter from "../converter/FaxOutboxResultToModelConverter";
import FaxOutboxSearchParams from "../model/FaxOutboxSearchParams";
import FaxOutboxResult from "../model/FaxOutboxResult";
import PagedResponse from "../../common/response/pagedRespose";

export default class FaxOutboxService
{
	protected faxOutboundApi: FaxOutboundApi;
	protected faxOutboxResultToModelConverter = new FaxOutboxResultToModelConverter();

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	constructor()
	{
		const $http = angular.injector(["ng"]).get("$http");
		const $httpParamSerializer = angular.injector(["ng"]).get("$httpParamSerializer");
		this.faxOutboundApi = new FaxOutboundApi($http, $httpParamSerializer, API_BASE_PATH);
	}

	public async getNextPushTime(): Promise<Moment>
	{
		let transfer = (await this.faxOutboundApi.getNextPushTime()).data.body;
		return moment(transfer);
	}

	public getOutbox = async (params: FaxOutboxSearchParams): Promise<PagedResponse<FaxOutboxResult>> =>
	{
		let transfer = (await this.faxOutboundApi.getOutbox(
				params.page,
				params.perPage,
				params.endDate ? Juno.Common.Util.formatMomentDate(params.endDate) : null,
				params.startDate ? Juno.Common.Util.formatMomentDate(params.startDate) : null,
				params.faxAccount?.id,
				params.combinedStatus as any,
				params.archived,
			)).data;
		return new PagedResponse(this.faxOutboxResultToModelConverter.convertList(transfer.body, transfer.headers));
	}

	 public download = (recordId: number):void =>
	 {
		 //todo how to ?

		 // this.faxOutboundApi.download();
		 // service.apiPath + '/' + id + '/download';
		 // let windowName = "ViewFaxFile" + outboundId;
		 // window.open(url, windowName, "scrollbars=1,width=1024,height=768");
	 }

	public resendOutboundFax = async (recordId: number): Promise<FaxOutboxResult> =>
	{
		return this.faxOutboxResultToModelConverter.convert(
			(await this.faxOutboundApi.resend(recordId)).data.body);
	};

	public setNotificationStatus = async (recordId: number, status: string): Promise<FaxOutboxResult> =>
	{
		return this.faxOutboxResultToModelConverter.convert(
			(await this.faxOutboundApi.setNotificationStatus(recordId, status)).data.body);
	}

	public archive = async (recordId: number): Promise<FaxOutboxResult> =>
	{
		return this.faxOutboxResultToModelConverter.convert(
			(await this.faxOutboundApi.archive(recordId)).data.body);
	};

}