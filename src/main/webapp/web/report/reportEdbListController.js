angular.module('Report').controller('Report.ReportEdbListController', [

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
			version: '',
			region: 'ON'
		}; //todo: grab region from somewhere

		$scope.generateReport = function()
		{
			$log.log('run edb report');
			var startDate = $filter('date')($scope.params.startDate, 'yyyy-MM-dd');
			var endDate = $filter('date')($scope.params.endDate, 'yyyy-MM-dd');

			var url = '';

			if ($scope.params.region === 'BC')
			{
				if ($scope.params.version == '05')
				{
					url = '../report/reportbcedblist2007.jsp?startDate=' + startDate + '&endDate=' + endDate;
				}
				else
				{
					url = '../report/reportbcedblist.jsp?startDate=' + startDate + '&endDate=' + endDate;
				}
			}

			if ($scope.params.region === 'ON')
			{
				if ($scope.params.version == '05')
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