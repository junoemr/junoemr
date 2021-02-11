import {TELEHEALTH_SESSION_STATUS} from "./mhaServiceConstants";
import {MhaDemographicApi, MhaIntegrationApi, ScheduleApi} from "../../../generated";

angular.module("Common.Services").service("mhaService", [
	'$http', '$q', '$httpParamSerializer',
	function($http, $q, $httpParamSerializer)
	{
		let service = {};

		const mhaDemographicApi = new MhaDemographicApi($http, $httpParamSerializer, '../ws/rs');
		const mhaIntegrationApi = new MhaIntegrationApi($http, $httpParamSerializer, '../ws/rs');

		// get use display name for telehealth status
		service.telehealthStatusToDisplayName = (status) =>
		{
			switch(status)
			{
				case TELEHEALTH_SESSION_STATUS.CALL_ENDED:
					return "Call Over";
				case TELEHEALTH_SESSION_STATUS.IN_CALL:
					return "In Session";
				case TELEHEALTH_SESSION_STATUS.INBOUND:
				case TELEHEALTH_SESSION_STATUS.OUTBOUND:
				case TELEHEALTH_SESSION_STATUS.PENDING:
					return "Patient Waiting";
				default:
					return "Status Unknown";
			}
		}

		// check if a demographic is confirmed on the site
		service.isDemographicConfirmed = async (demographicNo, site) =>
		{
			if (demographicNo)
			{
				let integrations = (await mhaIntegrationApi.searchIntegrations(site)).data.body;
				if (integrations.length > 0)
				{
					const integration = integrations[0];
					return (await mhaDemographicApi.isPatientConfirmed(integration.id, demographicNo)).data.body;
				}
			}

			return false;
		}

		return service;
	}
]);