angular.module('PatientList').controller('PatientList.PatientListDemographicSetController', [

	'$scope',
	'Navigation',
	'$http',

	function(
		$scope,
		$http,
		Navigation)
	{

		var controller = this;

		$http(
		{
			url: '../ws/rs/reporting/demographicSets/list',
			method: "GET",
			headers:
			{
				'Content-Type': 'application/json'
			}
		}).then(
			function success(results)
			{
				controller.sets = results.data.content;
			},
			function error(error)
			{
				alert('Failed to get sets lists.');
			});
	}
]);