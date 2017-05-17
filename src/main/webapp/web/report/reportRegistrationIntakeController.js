angular.module('Report').controller('Report.ReportRegistrationIntakeController', [

	'$scope',
	'$log',
	'$filter',

	function(
		$scope,
		$log,
		$filter)
	{
		$scope.params = {
			startDate: new Date(),
			endDate: new Date(),
			includePastForms: true
		};

		$scope.generateReport = function()
		{
			$log.log('run registration intake report');
			var startDate = $filter('date')($scope.params.startDate, 'yyyy-MM-dd');
			var endDate = $filter('date')($scope.params.endDate, 'yyyy-MM-dd');

			var url = '../PMmodule/GenericIntake/Report.do?method=report&type=quick&startDate=' + startDate + '&endDate=' + endDate + '&includePast=' + $scope.params.includePastForms;
			window.open(url, 'report_registration_intake', 'height=900,width=700');

		};
	}
]);