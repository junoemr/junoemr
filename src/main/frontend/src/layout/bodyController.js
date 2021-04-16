angular.module('Layout').controller('Layout.BodyController', [
	'$rootScope',
	'$scope',
	'providerService',
	'securityService',
	'securityRolesService',
	function($rootScope, $scope, providerService, securityService, securityRolesService)
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
					controller.userLoaded = true;
				},
				function error(errors)
				{
					console.log(errors);
				});
			securityRolesService.loadUserRoles().then(() =>
			{
				controller.userRolesLoaded = true;
			});
		};

		// flag for whether the patient list should be showing or not
		// can be set from controllers with $emit: $scope.$emit('configureShowPatientList', false);
		controller.showPatientList = false;

		// used to prevent race condition on user load. the rest of the app will not render until true.
		controller.userLoaded = false;
		controller.userRolesLoaded = false;

		// controllers can update the showPatientList value by calling an $emit
		// e.g. $scope.$emit('configureShowPatientList', false);
		$scope.$on('configureShowPatientList',
			function(event, value)
			{
				controller.showPatientList = value;
			});

		controller.toggleShowPatientList = function()
		{
			controller.showPatientList = !controller.showPatientList;
		};
		controller.isLeftAsideOpen = function()
		{
			return (controller.showPatientList === true);
		};
		controller.isInitialized = () =>
		{
			return (controller.userLoaded && controller.userRolesLoaded);
		}
	}
]);