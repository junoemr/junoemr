angular.module('Help').controller('Help.SupportController', [

	'$scope',
	'$http',

	function(
		$scope,
		$http)
	{

		$scope.ospInfo = {
			name: 'OscarHost.ca',
			phone: '250-900-7373',
			contact: 'Oscarhost Support',
			email: 'support@oscarhost.ca',
			url: 'https://help.oscarhost.ca'
		};

		$scope.buildInfo = {
			versionDisplayName: '15-Beta',
			version: 'master-0000'
		};
	}
]);