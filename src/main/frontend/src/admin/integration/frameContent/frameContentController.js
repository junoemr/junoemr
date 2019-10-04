angular.module('Admin.Integration').controller('Admin.Integration.FrameContentController', [
	'$scope',
	'$http',
	'$stateParams',
	function ($scope, $http, $stateParams)
	{
		let controller = this;

		controller.frameUrl = $stateParams.frameUrl;
	}]
);