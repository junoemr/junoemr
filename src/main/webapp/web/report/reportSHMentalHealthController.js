angular.module('Report').controller('Report.ReportSHMentalHealthController', [

	'$scope',
	'$log',
	'$filter',

	function(
		$scope,
		$log,
		$filter)
	{
		var controller = this;

		controller.params = {
			startDate: new Date()
		};

		controller.generateReport = function generateReport()
		{
			$log.log('run sh mental health report');
			var startDate = $filter('date')(controller.params.startDate, 'yyyy-MM-dd');

			var url = '../PMmodule/StreetHealthIntakeReportAction.do?startDate=' + startDate;
			window.open(url, 'report_sh', 'height=900,width=700');

		};
	}
]);