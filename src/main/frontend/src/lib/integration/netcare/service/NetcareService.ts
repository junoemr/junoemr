// ==========================================================================
// Public Methods
// ==========================================================================
import {NetcareApi} from "../../../../../generated";
import {API_BASE_PATH} from "../../../constants/ApiConstants";

export default class NetcareService
{
	// constants
	protected commonWindowName: string = "NetcareLoginWindow";

	// local variables
	protected netcareApi: NetcareApi;

	protected userId: string;
	protected conformanceCode: string;
	protected launcherUrl: string;

	/**
	 * constructor
	 * ensure init completes before use of the service
	 */
	constructor()
	{
		const $http = angular.injector(["ng"]).get("$http");
		const $httpParamSerializer = angular.injector(["ng"]).get("$httpParamSerializer");

		this.netcareApi = new NetcareApi($http, $httpParamSerializer, API_BASE_PATH);
	}

	public async init(): Promise<void>
	{
		let response = await Promise.all([
			this.netcareApi.getLauncherUrl(),
			this.netcareApi.getConformanceCode(),
		]);
		this.launcherUrl = response[0].data.body;
		this.conformanceCode = response[1].data.body;

		this.userId = "PLBTEST1"; //todo
	}

	/**
	 * async constructor method to ensure init has completed before returnig a new instance of the service
	 */
	public static async create(): Promise<NetcareService>
	{
		let service = new NetcareService();
		await service.init();
		return service;
	}

	public submitLoginForm(healthNumber: string): void
	{
		let form = this.buildForm("a1",
			"plb.albertanetcare.ca/cha/PLBLogin.htm" +
			"?contextView=EMRPatient" +
			"&userID=" + encodeURIComponent(this.userId) +
			"&applicationName=Aligndex+EMPI" +
			"&entryPointName=Search+for+a+Patient-EMR" +
			"&EMRPatient.Id.idType=AB_ULI" +
			"&EMRPatient.Id.id=" + encodeURIComponent(healthNumber) +
			"&confCode=" + encodeURIComponent(this.conformanceCode),
			"Logon");
		NetcareService.submitForm(form);
	}

	public submitLogoutForm(): void
	{
		let form = this.buildForm("a", "plb.albertanetcare.ca/concerto/Logout.htm", "Logoff");
		NetcareService.submitForm(form);
	}

	private static submitForm(form: HTMLFormElement): void
	{
		document.getElementsByTagName('body')[0].appendChild(form);
		form.submit();
		form.remove();
	}

	private buildForm(formName: string, commandLineValue: string, submitValue: string): HTMLFormElement
	{
		let form = document.createElement("form");
		form.setAttribute('name', formName);
		form.setAttribute('method', "GET");
		form.setAttribute('action', this.launcherUrl);
		form.setAttribute('target', this.commonWindowName);

		let applicationInput = document.createElement("input");
		applicationInput.setAttribute('type', "hidden");
		applicationInput.setAttribute('name', "CTX_Application");
		applicationInput.setAttribute('value', "Citrix.MPS.App.Portal.PLB");

		let commandLineInput = document.createElement("input");
		commandLineInput.setAttribute('type', "hidden");
		commandLineInput.setAttribute('name', "NFuse_AppCommandLine");
		commandLineInput.setAttribute('value', commandLineValue);

		let submitInput = document.createElement("input");
		commandLineInput.setAttribute('type', "submit");
		commandLineInput.setAttribute('value', submitValue);

		form.appendChild(applicationInput);
		form.appendChild(commandLineInput);
		form.appendChild(submitInput);

		return form as HTMLFormElement;
	}
}