angular.module('Tickler').controller('Tickler.TicklerCommentController', [

	'$scope',
	'$uibModalInstance',
	'tickler',

	function(
		$scope,
		$uibModalInstance,
		tickler)
	{

		var controller = this;

		controller.tickler = tickler;

		controller.close = function()
		{
			$uibModalInstance.close("Someone Closed Me");
		};
	}
]);