import {TELEHEALTH_SESSION_STATUS} from "./mhaServiceConstants";

angular.module("Common.Services").service("mhaService", [
	'$http', '$q',
	function($http, $q)
	{
		let service = {};

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

		return service;
	}
]);