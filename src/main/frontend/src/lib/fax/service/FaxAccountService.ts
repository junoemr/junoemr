import {FaxAccountApi, FaxInboundApi, FaxOutboundApi} from "../../../../generated";
import {API_BASE_PATH} from "../../constants/ApiConstants";
import FaxAccountToUpdateInputConverter from "../converter/FaxAccountToUpdateInputConverter";
import FaxAccountToCreateInputConverter from "../converter/FaxAccountToCreateInputConverter";
import FaxAccountToModelConverter from "../converter/FaxAccountToModelConverter";
import FaxAccount from "../model/FaxAccount";
import FaxInboxResult from "../model/FaxInboxResult";
import FaxInboxResultToModelConverter from "../converter/FaxInboxResultToModelConverter";
import FaxOutboxResultToModelConverter from "../converter/FaxOutboxResultToModelConverter";
import FaxOutboxResult from "../model/FaxOutboxResult";

export default class FaxAccountService
{
	protected faxAccountApi: FaxAccountApi;
	protected faxInboundApi: FaxInboundApi;
	protected faxOutboundApi: FaxOutboundApi;

	protected faxAccountToCreateInputConverter = new FaxAccountToCreateInputConverter();
	protected faxAccountToUpdateInputConverter = new FaxAccountToUpdateInputConverter();
	protected faxAccountToModelConverter = new FaxAccountToModelConverter();
	protected faxInboxResultToModelConverter = new FaxInboxResultToModelConverter();
	protected faxOutboxResultToModelConverter = new FaxOutboxResultToModelConverter();

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	constructor()
	{
		const $http = angular.injector(["ng"]).get("$http");
		const $httpParamSerializer = angular.injector(["ng"]).get("$httpParamSerializer");

		this.faxAccountApi = new FaxAccountApi($http, $httpParamSerializer, API_BASE_PATH);
		this.faxInboundApi = new FaxInboundApi($http, $httpParamSerializer, API_BASE_PATH);
		this.faxOutboundApi = new FaxOutboundApi($http, $httpParamSerializer, API_BASE_PATH);
	}

	public isAccountEnabled = async (id: number): Promise<boolean> =>
	{
		return (await this.faxAccountApi.isEnabled(id)).data.body;
	};

	public getAccountSettings = async (id: number): Promise<FaxAccount> =>
	{
		return this.faxAccountToModelConverter.convert(
			(await this.faxAccountApi.getAccountSettings(id)).data.body);
	};

	public createAccountSettings = async (model: FaxAccount): Promise<FaxAccount> =>
	{
		let input = this.faxAccountToCreateInputConverter.convert(model);
		return this.faxAccountToModelConverter.convert(
			(await this.faxAccountApi.createAccountSettings(input)).data.body);
	};

	public updateAccountSettings = async (model: FaxAccount): Promise<FaxAccount> =>
	{
		let input = this.faxAccountToUpdateInputConverter.convert(model);
		return this.faxAccountToModelConverter.convert(
			(await this.faxAccountApi.updateAccountSettings(model.id, input)).data.body);
	};

	public testFaxConnection = async (model: FaxAccount): Promise<boolean> =>
	{
		if (model.id)
		{
			let input = this.faxAccountToUpdateInputConverter.convert(model);
			return (await this.faxAccountApi.testExistingFaxConnection(model.id, input)).data.body;
		}
		else
		{
			let input = this.faxAccountToCreateInputConverter.convert(model);
			return (await this.faxAccountApi.testFaxConnection(input)).data.body;
		}
	}

	//todo search should take search model?
	public getAccounts = async (page: number = 1, perPage: number = 10): Promise<FaxAccount[]> =>
	{
		return this.faxAccountToModelConverter.convertList(
			(await this.faxAccountApi.listAccounts(page, perPage)).data.body);
	}

	public getInbox = async (id: number, page: number, perPage: number,
	                              startDate: any,
	                              endDate: any): Promise<FaxInboxResult[]> =>
	{
		return this.faxInboxResultToModelConverter.convertList(
			(await this.faxAccountApi.getInbox(id, page, perPage, endDate, startDate)).data.body);
	}

	public getOutbox = async (id: number, page: number, perPage: number,
	                          startDate: any,
	                          endDate: any,
	                          combinedStatus: string,
	                          archived: string): Promise<FaxOutboxResult[]> =>
	{
		return this.faxOutboxResultToModelConverter.convertList(
			(await this.faxAccountApi.getOutbox(id, page, perPage, endDate, startDate, combinedStatus, archived)).data.body);
	}
}