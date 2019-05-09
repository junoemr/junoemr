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

		var controller = this;

		controller.prefs = prefs.dashboardPreferences;

		controller.close = function close()
		{
			$uibModalInstance.close(false);
		};

		controller.save = function save()
		{

			personaService.updateDashboardPreferences(controller.prefs).then(
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