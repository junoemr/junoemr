angular.module('Report').oscarApp.controller('Report.ReportSHMentalHealthController',

	'$scope',
	'$log',
	'$filter',

	function(
		$scope,
		$log,
		$filter)
	{
		$scope.params = {
			startDate: new Date()
		};

		$scope.generateReport = function()
		{
			$log.log('run sh mental health report');
			var startDate = $filter('date')($scope.params.startDate, 'yyyy-MM-dd');

			var url = '../PMmodule/StreetHealthIntakeReportAction.do?startDate=' + startDate;
			window.open(url, 'report_sh', 'height=900,width=700');

		};
	});