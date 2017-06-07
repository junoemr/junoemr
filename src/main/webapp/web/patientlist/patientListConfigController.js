angular.module('PatientList').controller('PatientList.PatientListConfigController', [

	'$scope',
	'$uibModalInstance',
	'config',

	function(
		$scope,
		$uibModalInstance,
		config)
	{
		var controller = this;

		controller.patientListConfig = config;

		controller.cancel = function cancel()
		{
			$uibModalInstance.dismiss();
		};

		controller.saveConfiguration = function saveConfiguration()
		{
			$uibModalInstance.close(controller.patientListConfig);
		};
	}
]);