angular.module('Record.Details').controller('Record.Details.SwipecardController', [

	'$scope',
	'$uibModal',
	'$uibModalInstance',

	function (
		$scope,
		$uibModal,
		$uibModalInstance,
	)
	{
		let controller = this;

		controller.cardDataString = null;
		controller.focusInput = null;

		controller.init = function init()
		{

			controller.loadWatches();

			// setting this triggers the ca-focus-field directive to set the focus on the card-data element
			controller.focusInput = "card-data";
		};

		controller.loadWatches = function loadWatches()
		{
			$scope.$watch('swipecardController.cardDataString', function (newValue, oldValue)
			{
				if (newValue !== oldValue && !Juno.Common.Util.isBlank(newValue))
				{
					controller.parseDataAndClose(newValue);
				}
			});
		};

		controller.parseDataAndClose = function parseDataAndClose(dataString)
		{
			var cardInfo = Oscar.HealthCardParser.parse(dataString);
			//TODO detect and display any errors
			$uibModalInstance.close(cardInfo);
		};

		controller.cancel = function cancel()
		{
			$uibModalInstance.dismiss('cancel');
		};

		controller.init();
	}
]);