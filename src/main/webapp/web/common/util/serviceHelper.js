'use strict';

window.Juno = window.Juno || {};
Juno.Common = Juno.Common || {};

Juno.Common.ServiceHelper = {};

Juno.Common.ServiceHelper.configHeaders = function configHeaders() {
	return {
		headers: {
			"Content-Type": "application/json",
			"Accept": "application/json"
		}
	};
};

Juno.Common.ServiceHelper.configHeadersWithCache = function configHeadersWithCache()
{
	return {
		headers: {
			"Content-Type": "application/json",
			"Accept": "application/json"
		},
		cache: true
	};
};
