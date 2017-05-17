angular.module('Tickler').controller('Tickler.TicklerCommentController', [

	'$scope',
	'$uibModalInstance',
	'tickler',

	function(
		$scope,
		$uibModalInstance,
		tickler)
	{

		$scope.tickler = tickler;

		$scope.close = function()
		{
			$uibModalInstance.close("Someone Closed Me");
		};
	}
]);