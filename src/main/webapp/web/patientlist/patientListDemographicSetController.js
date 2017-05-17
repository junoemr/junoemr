angular.module('PatientList').controller('PatientList.PatientListDemographicSetController', [

	'$scope',
	'Navigation',
	'$http',

	function(
		$scope,
		$http,
		Navigation)
	{
		$http(
		{
			url: '../ws/rs/reporting/demographicSets/list',
			method: "GET",
			headers:
			{
				'Content-Type': 'application/json'
			}
		}).then(
			function success(response)
			{
				$scope.sets = response.data.content;
			},
			function error(error)
			{
				alert('Failed to get sets lists.');
			});
	}
]);