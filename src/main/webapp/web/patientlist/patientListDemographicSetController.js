angular.module('PatientList').controller('PatientList.PatientListDemographicSetController', [
	'$scope',
	'$http',
	'reportingService',

	function(
		$scope,
		$http,
		reportingService)
	{

		var controller = this;

		reportingService.getDemographicSetList().then(
			function success(results)
			{
				controller.sets = results.content;
			},
			function error(error)
			{
				alert('Failed to get sets lists.');
			});
	}
]);