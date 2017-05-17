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

		$scope.close = function()
		{
			$uibModalInstance.close(false);
		};

		$scope.save = function()
		{

			personaService.updateDashboardPreferences($scope.prefs).then(function(data)
			{
				$uibModalInstance.close(true);


			}, function(reason)
			{
				$uibModalInstance.close(false);
			});

		};
	}
]);