angular.module('Report').controller('Report.ReportFollowUpIntakeController', [

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
			startDate: new Date(),
			endDate: new Date(),
			includePastForms: true
		};

		controller.generateReport = function generateReport()
		{
			$log.log('run follow up intake report');
			var startDate = $filter('date')(controller.params.startDate, 'yyyy-MM-dd');
			var endDate = $filter('date')(controller.params.endDate, 'yyyy-MM-dd');

			var url = '../PMmodule/GenericIntake/Report.do?method=report&type=indepth&startDate=' + startDate + '&endDate=' + endDate + '&includePast=' + controller.params.includePastForms;
			window.open(url, 'report_followup_intake', 'height=900,width=700');

		};
	}
]);