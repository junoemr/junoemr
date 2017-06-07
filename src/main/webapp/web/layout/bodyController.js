angular.module('Layout').controller('Layout.BodyController', [
	'$rootScope',
	'$scope',
	'providerService',
	'securityService',
	function($rootScope, $scope, providerService, securityService)
	{
		var controller = this;

		//=========================================================================
		// Initialization
		//=========================================================================

		controller.init = function init()
		{
			providerService.getMe().then(
				function success(results)
				{
					securityService.setUser(results);
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		// flag for whether the patient list should be showing or not
		// can be set from controllers with $emit: $scope.$emit('configureShowPatientList', false);
		controller.showPatientList = true;

		// controllers can update the showPatientList value by calling an $emit
		// e.g. $scope.$emit('configureShowPatientList', false);
		$scope.$on('configureShowPatientList',
			function(event, value)
			{
				console.log("Layout.BodyController updating showPatientList", value);
				controller.showPatientList = value;
			});

		$rootScope.$on('$stateChangeStart',
			function()
			{
				console.log("Layout.BodyController $stateChangeStart setting showPatientList to true");
				controller.showPatientList = true;
			});
	}
]);