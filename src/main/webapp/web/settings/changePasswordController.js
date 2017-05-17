angular.module('Settings').controller('Settings.ChangePasswordController', [

	'$scope',
	'$uibModalInstance',

	function(
		$scope,
		$uibModalInstance)
	{

		$scope.close = function()
		{
			$uibModalInstance.close("Someone Closed Me");
		};

		$scope.changePassword = function()
		{
			console.log('password saved - NOT');
			$uibModalInstance.close("Someone Saved Me");
		};
	}
]);