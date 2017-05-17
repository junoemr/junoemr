angular.module('Report').oscarApp.controller('Report.ReportFollowUpIntakeController', [

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
			$log.log('run follow up intake report');
			var startDate = $filter('date')($scope.params.startDate, 'yyyy-MM-dd');
			var endDate = $filter('date')($scope.params.endDate, 'yyyy-MM-dd');

			var url = '../PMmodule/GenericIntake/Report.do?method=report&type=indepth&startDate=' + startDate + '&endDate=' + endDate + '&includePast=' + $scope.params.includePastForms;
			window.open(url, 'report_followup_intake', 'height=900,width=700');

		};
	}
]);