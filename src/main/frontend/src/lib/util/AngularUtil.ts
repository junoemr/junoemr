/**
 * get the angular $http service
 */
export function getAngular$http(): ng.IHttpService
{
	return angular.injector(["ng"]).get("$http");
}

/**
 * get the angular $httpParamSerializer service
 */
export function getAngular$httpParamSerializer():  (d: any) => any
{
	return angular.injector(["ng"]).get("$httpParamSerializer");
}