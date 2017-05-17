angular.module('PatientList').controller('PatientList.PatientListConfigController', [

	'$scope',
	'$uibModalInstance',
	'config',

	function(
		$scope,
		$uibModalInstance,
		config)
	{

		$scope.patientListConfig = config;

		$scope.cancel = function()
		{
			$uibModalInstance.dismiss();
		};

		$scope.saveConfiguration = function()
		{
			$uibModalInstance.close($scope.patientListConfig);
		};

		$scope.saveConfiguration = function()
		{
			$uibModalInstance.close($scope.patientListConfig);
		};

	}
]);