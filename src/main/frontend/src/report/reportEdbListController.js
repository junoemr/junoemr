angular.module('Report').controller('Report.ReportEdbListController', [

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
			startDate: Juno.Common.Util.formatMomentDate(moment()),
			endDate: Juno.Common.Util.formatMomentDate(moment()),
			version: '',
			region: 'ON'
		}; //todo-legacy: grab region from somewhere

		controller.generateReport = function generateReport()
		{
			$log.log('run edb report');
			var startDate = $filter('date')(controller.params.startDate, 'yyyy-MM-dd');
			var endDate = $filter('date')(controller.params.endDate, 'yyyy-MM-dd');

			var url = '';

			if (controller.params.region === 'BC')
			{
				if (controller.params.version == '05')
				{
					url = '../report/reportbcedblist2007.jsp?startDate=' + startDate + '&endDate=' + endDate;
				}
				else
				{
					url = '../report/reportbcedblist.jsp?startDate=' + startDate + '&endDate=' + endDate;
				}
			}

			if (controller.params.region === 'ON')
			{
				if (controller.params.version == '05')
				{
					url = '../report/reportonedblist.jsp?startDate=' + startDate + '&endDate=' + endDate;
				}
				else
				{
					url = '../report/reportnewedblist.jsp?startDate=' + startDate + '&endDate=' + endDate;
				}
			}


			window.open(url, 'report_edb', 'height=900,width=700');

		};
	}
]);