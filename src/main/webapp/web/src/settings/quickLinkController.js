angular.module('Settings').controller('Settings.QuickLinkController', [

	'$scope',
	'$uibModalInstance',

	function(
		$scope,
		$uibModalInstance)
	{

		$scope.qll = {};

		$scope.close = function()
		{
			$uibModalInstance.close();
		};

		$scope.addQuickLink = function(qlForm)
		{
			if (qlForm.$valid)
			{
				//	alert($scope.qll.toSource());
				$uibModalInstance.close($scope.qll);
			}
		};
	}
]);