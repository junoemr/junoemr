import {NetcareApi, SystemPreferenceApi} from "../../../../../generated";
import {API_BASE_PATH} from "../../../constants/ApiConstants";
import NetcareConfig from "../model/NetcareConfig";
import NetcareConfigModelConverter from "../converter/NetcareConfigModelConverter";
import {SystemPreferences} from "../../../../common/services/systemPreferenceServiceConstants";

export default class NetcareService
{
	// constants
	protected commonWindowName: string = "NetcareLoginWindow";
	protected sessionKey: string = "LoggedInNetcare";
	protected sessionValue: string = "true";

	// local variables
	protected netcareApi: NetcareApi;
	protected systemPreferenceApi: SystemPreferenceApi;
	protected config: NetcareConfig;
	protected initialized: boolean = false;

	/**
	 * constructor
	 * ensure init completes before use of the service
	 */
	constructor()
	{
		const $http = angular.injector(["ng"]).get("$http");
		const $httpParamSerializer = angular.injector(["ng"]).get("$httpParamSerializer");

		this.netcareApi = new NetcareApi($http, $httpParamSerializer, API_BASE_PATH);
		this.systemPreferenceApi = new SystemPreferenceApi($http, $httpParamSerializer, API_BASE_PATH);
	}

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	public async loadConfig(): Promise<NetcareConfig>
	{
		let modelConverter = new NetcareConfigModelConverter();
		return modelConverter.convert((await this.netcareApi.getConfig()).data.body);
	}

	public async loadEnabledState(): Promise<boolean>
	{
		return (await this.systemPreferenceApi.getPreferenceEnabled(SystemPreferences.NetcareEnabled, false)).data.body;
	}

	public async submitLoginForm(healthNumber: string, userId: string = null): Promise<void>
	{
		if(!this.initialized)
		{
			await this.init();
		}

		let userIdParam = Juno.Common.Util.isBlank(userId) ? "" : "&userID=" + encodeURIComponent(userId);

		let form = this.buildForm("a1",
			this.config.loginUrl +
			"?contextView=EMRPatient" +
			userIdParam +
			"&applicationName=Aligndex+EMPI" +
			"&entryPointName=Search+for+a+Patient-EMR" +
			"&EMRPatient.Id.idType=AB_ULI" +
			"&EMRPatient.Id.id=" + encodeURIComponent(healthNumber) +
			"&confCode=" + encodeURIComponent(this.config.conformanceCode),
			"Logon");
		NetcareService.submitForm(form);

		// assume that the user has logged in to netcare, we may need to log them out
		sessionStorage.setItem(this.sessionKey, this.sessionValue);
	}

	public async submitLogoutForm(): Promise<void>
	{
		if(!this.initialized)
		{
			await this.init();
		}

		let form = this.buildForm("a", this.config.logoutUrl, "Logoff");
		NetcareService.submitForm(form);
		sessionStorage.removeItem(this.sessionKey);
	}

	public isLoggedIn(): boolean
	{
		return (sessionStorage.getItem(this.sessionKey) === this.sessionValue);
	}

	// ==========================================================================
	// Private Methods
	// ==========================================================================

	private async init(): Promise<void>
	{
		this.config = await this.loadConfig();
		this.initialized = true;
	}

	private static submitForm(form: HTMLFormElement): void
	{
		document.body.appendChild(form);
		form.submit();
		form.remove();
	}

	private buildForm(formName: string, commandLineValue: string, submitValue: string): HTMLFormElement
	{
		let form = document.createElement("form");
		form.setAttribute('name', formName);
		form.setAttribute('method', "GET");
		form.setAttribute('action', this.config.launcherUrl);
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
		submitInput.setAttribute('type', "submit");
		submitInput.setAttribute('value', submitValue);

		form.appendChild(applicationInput);
		form.appendChild(commandLineInput);
		form.appendChild(submitInput);

		return form as HTMLFormElement;
	}
}
// service is meant to be a singleton
export const netcareService = new NetcareService();