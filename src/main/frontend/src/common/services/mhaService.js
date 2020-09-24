import {TELEHALTH_SESSION_STATUS} from "./mhaServiceConstants";

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
				case TELEHALTH_SESSION_STATUS.CALL_ENDED:
					return "Call Over";
				case TELEHALTH_SESSION_STATUS.IN_CALL:
					return "In Session";
				case TELEHALTH_SESSION_STATUS.INBOUND:
				case TELEHALTH_SESSION_STATUS.OUTBOUND:
				case TELEHALTH_SESSION_STATUS.PENDING:
					return "Patient Waiting";
				default:
					return "Status Unknown";
			}
		}

		return service;
	}
]);