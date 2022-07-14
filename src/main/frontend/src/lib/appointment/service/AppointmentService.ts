import {AppointmentApi} from "../../../../generated";
import {API_BASE_PATH} from "../../constants/ApiConstants";

export default class AppointmentService
{
	protected readonly appointmentApi: AppointmentApi;

	constructor()
	{
		const $http = angular.injector(["ng"]).get("$http");
		const $httpParamSerializer = angular.injector(["ng"]).get("$httpParamSerializer");

		this.appointmentApi = new AppointmentApi($http, $httpParamSerializer, API_BASE_PATH);
	}

	// todo implement appointment model conversion, return appointment model type
	public async getAppointment(appointmentId: number): Promise<any>
	{
		return (await this.appointmentApi.getAppointment(appointmentId)).data.body;
	}
}