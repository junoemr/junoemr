'use strict';

window.Juno = window.Juno || {};
Juno.Common = Juno.Common || {};

Juno.Common.Util = {};

Juno.Common.Util.exists = function exists(object) {
	// not undefined and not null
	return angular.isDefined(object) && object !== null;
};

Juno.Common.Util.isBlank = function isBlank(object) {
	// undefined or null or empty string
	return !Juno.Common.Util.exists(object) || object === "";
};

