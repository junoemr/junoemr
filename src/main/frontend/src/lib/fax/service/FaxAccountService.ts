import {
	FaxAccountApi,
	FaxInboundApi,
	FaxOutboundApi
} from "../../../../generated";
import {API_BASE_PATH} from "../../constants/ApiConstants";
import FaxAccountToUpdateInputConverter from "../converter/FaxAccountToUpdateInputConverter";
import FaxAccountToCreateInputConverter from "../converter/FaxAccountToCreateInputConverter";
import FaxAccountToModelConverter from "../converter/FaxAccountToModelConverter";
import FaxAccount from "../model/FaxAccount";
import PagedResponse from "../../common/response/PagedResponse";
import {ErrorHandler} from "../../error/handler/ErrorHandler";
import BasicErrorHandler from "../../error/handler/BasicErrorHandler";
import {FaxAccountConnectionStatus} from "../model/FaxAccountConnectionStatus";

export default class FaxAccountService
{
	protected errorHandler: ErrorHandler;
	protected faxAccountApi: FaxAccountApi;
	protected faxInboundApi: FaxInboundApi;
	protected faxOutboundApi: FaxOutboundApi;

	protected faxAccountToCreateInputConverter = new FaxAccountToCreateInputConverter();
	protected faxAccountToUpdateInputConverter = new FaxAccountToUpdateInputConverter();
	protected faxAccountToModelConverter = new FaxAccountToModelConverter();

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	constructor(errorHandler: ErrorHandler = new BasicErrorHandler())
	{
		const $http = angular.injector(["ng"]).get("$http");
		const $httpParamSerializer = angular.injector(["ng"]).get("$httpParamSerializer");

		this.errorHandler = errorHandler;
		this.faxAccountApi = new FaxAccountApi($http, $httpParamSerializer, API_BASE_PATH);
		this.faxInboundApi = new FaxInboundApi($http, $httpParamSerializer, API_BASE_PATH);
		this.faxOutboundApi = new FaxOutboundApi($http, $httpParamSerializer, API_BASE_PATH);
	}

	public async isAccountEnabled(id: number): Promise<boolean>
	{
		try
		{
			return (await this.faxAccountApi.isEnabled(id)).data.body;
		}
		catch (error)
		{
			this.errorHandler.handleError(error);
		}
	};

	public async getAccountSettings(id: number): Promise<FaxAccount>
	{
		try
		{
			return this.faxAccountToModelConverter.convert(
				(await this.faxAccountApi.getAccountSettings(id)).data.body);
		}
		catch (error)
		{
			this.errorHandler.handleError(error);
		}
	};

	public async createAccountSettings(model: FaxAccount): Promise<FaxAccount>
	{
		try
		{
			let input = this.faxAccountToCreateInputConverter.convert(model);
			return this.faxAccountToModelConverter.convert(
				(await this.faxAccountApi.createAccountSettings(input)).data.body);
		}
		catch (error)
		{
			this.errorHandler.handleError(error);
		}
	};

	public async updateAccountSettings(model: FaxAccount): Promise<FaxAccount>
	{
		try
		{
			let input = this.faxAccountToUpdateInputConverter.convert(model);
			return this.faxAccountToModelConverter.convert(
				(await this.faxAccountApi.updateAccountSettings(model.id, input)).data.body);
		}
		catch (error)
		{
			this.errorHandler.handleError(error);
		}
	};

	public async deleteAccountSettings(id: number): Promise<boolean>
	{
		try
		{
			return (await this.faxAccountApi.deleteAccountSettings(id)).data.body;
		}
		catch (error)
		{
			this.errorHandler.handleError(error);
		}
	};

	public async disconnectAccountSettings(id: number): Promise<boolean>
	{
		try
		{
			return (await this.faxAccountApi.disconnectAccountSettings(id)).data.body;
		}
		catch (error)
		{
			this.errorHandler.handleError(error);
		}
	};

	public async testExistingFaxConnection(model: FaxAccount): Promise<FaxAccountConnectionStatus>
	{
		try
		{
				let input = this.faxAccountToUpdateInputConverter.convert(model);
				return (await this.faxAccountApi.testExistingFaxConnection(model.id, input)).data.body;
		}
		catch (error)
		{
			this.errorHandler.handleError(error);
		}
	}

	public async testFaxConnection(model: FaxAccount): Promise<boolean>
	{
		try
		{
			let input = this.faxAccountToCreateInputConverter.convert(model);
			return (await this.faxAccountApi.testFaxConnection(input)).data.body;
		}
		catch (error)
		{
			this.errorHandler.handleError(error);
		}
	}

	public async getAccounts(page: number = 1, perPage: number = 10): Promise<PagedResponse<FaxAccount>>
	{
		try
		{
			const transfer = (await this.faxAccountApi.listAccounts(page, perPage)).data;
			return new PagedResponse<FaxAccount>(this.faxAccountToModelConverter.convertList(transfer.body), transfer.headers);
		}
		catch (error)
		{
			this.errorHandler.handleError(error);
		}
	}

	public async getActiveAccount(): Promise<FaxAccount>
	{
		try
		{
			return this.faxAccountToModelConverter.convert(
				(await this.faxAccountApi.getActiveFaxAccount()).data.body);
		}
		catch (error)
		{
			this.errorHandler.handleError(error);
		}
	}

	public async getCoverLetterOptions(id: number): Promise<string[]>
	{
		try
		{
			return (await this.faxAccountApi.getCoverLetterOptions(id)).data.body;
		}
		catch (error)
		{
			this.errorHandler.handleError(error);
		}
	}
}