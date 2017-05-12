
angular.module('Layout').controller('Layout.BodyCtrl', [
	'$rootScope',
	'$scope',
	'providerService',
	'securityService',
	function($rootScope, $scope, providerService, securityService)
	{
		var controller = {};

		//=========================================================================
		// Initialization
		//=========================================================================

		controller.init = function init()
		{
			providerService.getMe().then(
				function success(data)
				{
					securityService.setUser(data);
				},
				function error(reason)
				{
					console.log(reason);
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
				console.log("Layout.BodyCtrl updating showPatientList", value);
				controller.showPatientList = value;
			});

		$rootScope.$on('$stateChangeStart',
			function()
			{
				console.log("Layout.BodyCtrl $stateChangeStart setting showPatientList to true");
				controller.showPatientList =  true;
			});

		return controller;
	}
]);

