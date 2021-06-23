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
			// setting this triggers the ca-focus-field directive to set the focus on the card-data element
			controller.focusInput = "card-data";
		};

		controller.parseDataAndClose = function parseDataAndClose(dataString)
		{
			if(!Juno.Common.Util.isBlank(dataString))
			{
				var cardInfo = Oscar.HealthCardParser.parse(dataString);

				//TODO-legacy detect and display any errors
				$uibModalInstance.close(cardInfo);
			}
		};

		controller.onEnterKeyDown = function onEnterKeyDown()
		{
			controller.parseDataAndClose(controller.cardDataString);
		};

		controller.cancel = function cancel()
		{
			$uibModalInstance.dismiss('cancel');
		};

		controller.init();
	}
]);