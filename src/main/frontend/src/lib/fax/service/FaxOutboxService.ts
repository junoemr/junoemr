import {FaxOutboundApi} from "../../../../generated";
import {API_BASE_PATH} from "../../constants/ApiConstants";
import moment, {Moment} from "moment";
import FaxOutboxResultToModelConverter from "../converter/FaxOutboxResultToModelConverter";
import FaxOutboxSearchParams from "../model/FaxOutboxSearchParams";
import FaxOutboxResult from "../model/FaxOutboxResult";
import PagedResponse from "../../common/response/PagedResponse";

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

	public async getOutbox(params: FaxOutboxSearchParams): Promise<PagedResponse<FaxOutboxResult>>
	{
		let transfer = (await this.faxOutboundApi.getOutbox(
			params.page,
			params.perPage,
			(params.endDate && params.endDate.isValid()) ? Juno.Common.Util.formatMomentDate(params.endDate) : null,
			(params.startDate && params.startDate.isValid()) ? Juno.Common.Util.formatMomentDate(params.startDate) : null,
			params.faxAccount?.id,
			params.combinedStatus as any,
			params.archived,
		)).data;
		return new PagedResponse(this.faxOutboxResultToModelConverter.convertList(transfer.body, transfer.headers));
	}

	public async download(recordId: number): Promise<void>
	{
		// it would be nice to figure out how to dynamically get this url somehow
		let url = "../ws/rs/faxOutbound/" + recordId + "/download";
		window.open(url, "_blank");
	}

	public async resendOutboundFax(recordId: number): Promise<FaxOutboxResult>
	{
		return this.faxOutboxResultToModelConverter.convert(
			(await this.faxOutboundApi.resend(recordId)).data.body);
	};

	public async setNotificationStatus(recordId: number, status: string): Promise<FaxOutboxResult>
	{
		return this.faxOutboxResultToModelConverter.convert(
			(await this.faxOutboundApi.setNotificationStatus(recordId, status)).data.body);
	}

	public async archive(recordId: number): Promise<FaxOutboxResult>
	{
		return this.faxOutboxResultToModelConverter.convert(
			(await this.faxOutboundApi.archive(recordId)).data.body);
	};

}