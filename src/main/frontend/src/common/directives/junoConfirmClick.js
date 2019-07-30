angular.module('Common').directive(
	'junoConfirmClick',
	[
	function(
	)
	{
		var scope = {
			onConfirm:'&junoConfirmClick',
			onCancel: '&junoConfirmCancel',
		};

		var linkFunction = function linkFunction($scope, element, attribute, controller)
		{
			var message = attribute.junoConfirmMessage || "Are you sure?";
			element.bind('click', function (event)
			{
				if (window.confirm(message))
				{
					$scope.onConfirm();
				}
				else
				{
					$scope.onCancel();
				}
			});
		};
		return {
			scope: scope,
			restrict: 'A',
			link: linkFunction,
		}
	}
]);
