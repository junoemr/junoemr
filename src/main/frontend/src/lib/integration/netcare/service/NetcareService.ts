// ==========================================================================
// Public Methods
// ==========================================================================
export default class NetcareService
{
	// constants
	protected commonWindowName: string = "NetcareLoginWindow";

	protected userId: string;
	protected confCode: string;

	constructor()
	{
		const $http = angular.injector(["ng"]).get("$http");
		const $httpParamSerializer = angular.injector(["ng"]).get("$httpParamSerializer");

		this.userId = "PLBTEST1"; //todo
		this.confCode = ""; //todo
	}

	public submitLoginForm(healthNumber: string): void
	{
		let form = this.buildForm("a1", this.buildCommandLineValue(healthNumber), "Logon");
		this.submitForm(form);
	}

	public submitLogoutForm(): void
	{
		let form = this.buildForm("a", "plb.albertanetcare.ca/concerto/Logout.htm", "Logoff");
		this.submitForm(form);
	}

	public buildCommandLineValue(healthNumber: string): string
	{
		return "plb.albertanetcare.ca/cha/PLBLogin.htm" +
			"?contextView=EMRPatient" +
			"&userID=" + encodeURIComponent(this.userId) +
			"&applicationName=Aligndex+EMPI" +
			"&entryPointName=Search+for+a+Patient-EMR" +
			"&EMRPatient.Id.idType=AB_ULI" +
			"&EMRPatient.Id.id=" + encodeURIComponent(healthNumber) +
			"&confCode=" + encodeURIComponent(this.confCode);
	}

	private submitForm(form: any): void
	{
		document.getElementsByTagName('body')[0].appendChild(form);
		form.submit();
		form.remove();
	}

	private buildForm(formName: string, commandLineValue: string, submitValue: string): any
	{
		let form = document.createElement("form");
		form.setAttribute('name', formName);
		form.setAttribute('method', "GET");
		form.setAttribute('action', "https://plb.albertanetcare.ca/Citrix/AccessPlatform16/site/launcher.aspx");
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

		return form;
	}
}