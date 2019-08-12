angular.module('Help').controller('Help.SupportController', [

	'$scope',
	'$http',

	function(
		$scope,
		$http)
	{

		//TODO load this dynamically
		$scope.ospInfo = {
			name: 'JunoEMR.com',
			phone: '250-900-7373',
			contact: 'Juno EMR Support',
			email: 'support@junoemr.com',
			url: 'https://help.junoemr.com'
		};

		$scope.buildInfo = {
			versionDisplayName: 'Juno',
			version: 'master-0000'
		};
	}
]);