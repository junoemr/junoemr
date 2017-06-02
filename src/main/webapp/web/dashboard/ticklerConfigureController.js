angular.module('Dashboard').controller('Dashboard.TicklerConfigureController', [

	'$scope',
	'$uibModalInstance',
	'personaService',
	'prefs',

	function(
		$scope,
		$uibModalInstance,
		personaService,
		prefs)
	{

		$scope.prefs = prefs.dashboardPreferences;

		$scope.close = function close()
		{
			$uibModalInstance.close(false);
		};

		$scope.save = function save()
		{

			personaService.updateDashboardPreferences($scope.prefs).then(
				function success(results)
				{
					$uibModalInstance.close(true);
				},
				function error(errors)
				{
					$uibModalInstance.close(false);
					console.log(errors);
				});

		};
	}
]);