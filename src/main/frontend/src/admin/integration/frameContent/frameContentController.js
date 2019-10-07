angular.module('Admin.Integration').controller('Admin.Integration.FrameContentController', [
	'$scope',
	'$http',
	'$stateParams',
	'$window',
	function ($scope, $http, $stateParams, $window)
	{
		let controller = this;

		controller.frameUrl = $stateParams.frameUrl;

		angular.element($window.frames['content-frame']).on("load", function (event)
		{
			console.log("INJECTING");
			// some admin pages require additional dependencies to display properly, inject them.
			angular.element(event.target.getElementsByTagName("head")[0]).append(
				"<script type=\"text/javascript\" src=\"../../../js/jquery-1.9.1.js\"></script>" +
				"<link href=\"../../../css/bootstrap.css\" rel=\"stylesheet\" type=\"text/css\">")
		});
	}]
);