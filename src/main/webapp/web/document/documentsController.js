angular.module('Document').controller('Document.DocumentsController', [

	'$scope',
	'securityService',

	function(
		$scope,
		securityService)
	{
		var controller = this;
		controller.me = null;

		$scope.$watch(function()
		{
			return securityService.getUser();
		}, function(newVal)
		{
			controller.me = newVal;

			if (newVal != null)
			{
				window.open('../dms/documentReport.jsp?function=provider&functionid=' + controller.me.providerNo, 'edocView', 'height=700,width=1024');
			}
		}, true);


		controller.openPopup = function openPopup()
		{
			window.open('../dms/documentReport.jsp?function=provider&functionid=' + controller.me.providerNo, 'edocView', 'height=700,width=1024');
		};
	}
]);