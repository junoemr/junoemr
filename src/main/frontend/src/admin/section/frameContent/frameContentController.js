angular.module('Admin.Section').controller('Admin.Section.FrameContentController', [
	'$scope',
	'$http',
	'$stateParams',
	'$window',
	function ($scope, $http, $stateParams, $window)
	{
		let controller = this;

		controller.frameUrl = $stateParams.frameUrl;
		controller.frameUrlEncoded = escape($stateParams.frameUrl);
		controller.useCompat = $stateParams.useCompat;
	}]
);