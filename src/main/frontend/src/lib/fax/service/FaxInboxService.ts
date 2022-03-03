import {FaxInboundApi} from "../../../../generated";
import {API_BASE_PATH} from "../../constants/ApiConstants";
import FaxInboxResultToModelConverter from "../converter/FaxInboxResultToModelConverter";
import moment, {Moment} from "moment";
import FaxInboxSearchParams from "../model/FaxInboxSearchParams";
import FaxInboxResult from "../model/FaxInboxResult";
import PagedResponse from "../../common/response/pagedRespose";

export default class FaxInboxService
{
	protected faxInboundApi: FaxInboundApi;
	protected faxInboxResultToModelConverter = new FaxInboxResultToModelConverter();

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	constructor()
	{
		const $http = angular.injector(["ng"]).get("$http");
		const $httpParamSerializer = angular.injector(["ng"]).get("$httpParamSerializer");
		this.faxInboundApi = new FaxInboundApi($http, $httpParamSerializer, API_BASE_PATH);
	}

	public async getNextPullTime(): Promise<Moment>
	{
		let transfer = (await this.faxInboundApi.getNextPullTime()).data.body;
		return moment(transfer);
	}

	public getInbox = async (params: FaxInboxSearchParams): Promise<PagedResponse<FaxInboxResult>> =>
	{
		let transfer = (await this.faxInboundApi.getInbox(
			params.page,
			params.perPage,
			params.endDate ? Juno.Common.Util.formatMomentDate(params.endDate) : null,
			params.startDate ? Juno.Common.Util.formatMomentDate(params.startDate) : null,
			params.faxAccount.id,
		)).data;

		return new PagedResponse(this.faxInboxResultToModelConverter.convertList(transfer.body, transfer.headers));
	}

}