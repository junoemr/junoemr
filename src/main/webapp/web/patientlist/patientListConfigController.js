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

		$scope.cancel = function cancel()
		{
			$uibModalInstance.dismiss();
		};

		$scope.saveConfiguration = function saveConfiguration()
		{
			$uibModalInstance.close($scope.patientListConfig);
		};
	}
]);