import {FaxAccountApi, FaxInboundApi, FaxOutboundApi} from "../../../../generated";
import {API_BASE_PATH} from "../../constants/ApiConstants";
import FaxAccountToUpdateInputConverter from "../converter/FaxAccountToUpdateInputConverter";
import FaxAccountToCreateInputConverter from "../converter/FaxAccountToCreateInputConverter";
import FaxAccountToModelConverter from "../converter/FaxAccountToModelConverter";
import FaxAccount from "../model/FaxAccount";
import PagedResponse from "../../common/response/pagedRespose";

export default class FaxAccountService
{
	protected faxAccountApi: FaxAccountApi;
	protected faxInboundApi: FaxInboundApi;
	protected faxOutboundApi: FaxOutboundApi;

	protected faxAccountToCreateInputConverter = new FaxAccountToCreateInputConverter();
	protected faxAccountToUpdateInputConverter = new FaxAccountToUpdateInputConverter();
	protected faxAccountToModelConverter = new FaxAccountToModelConverter();

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

	public deleteAccountSettings = async (id: number): Promise<boolean> =>
	{
		return (await this.faxAccountApi.deleteAccountSettings(id)).data.body;
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

	public getAccounts = async (page: number = 1, perPage: number = 10): Promise<PagedResponse<FaxAccount>> =>
	{
		const transfer = (await this.faxAccountApi.listAccounts(page, perPage)).data;
		return new PagedResponse<FaxAccount>(this.faxAccountToModelConverter.convertList(transfer.body), transfer.headers);
	}
}