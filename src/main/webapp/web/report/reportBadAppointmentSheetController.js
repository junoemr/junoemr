angular.module('Report').controller('Report.ReportBadAppointmentSheetController', [

	'$scope',
	'$log',
	'providerService',
	'$filter',

	function(
		$scope,
		$log,
		providerService,
		$filter)
	{

		$scope.params = {
			providerNo: '',
			startDate: new Date()
		};

		$scope.searchProviders = function(val)
		{
			var search = {
				searchTerm: val,
				active: true
			};
			return providerService.searchProviders(search, 0, 10).then(function(response)
			{
				var resp = [];
				for (var x = 0; x < response.length; x++)
				{
					resp.push(
					{
						providerNo: response[x].providerNo,
						name: response[x].firstName + ' ' + response[x].lastName
					});
				}
				return resp;
			});
		};
		$scope.updateProviderNo = function(item, model, label)
		{
			$scope.params.providerNo = model;
			$scope.data.providerNo = label;
		};

		$scope.generateReport = function()
		{
			var p = $scope.params;
			var startDate = $filter('date')(p.startDate, 'yyyy-MM-dd');

			var url = '../report/reportapptsheet.jsp?dsmode=all&provider_no=' + p.providerNo + '&sdate=' + startDate;
			window.open(url, 'report_badApptSheet', 'height=900,width=700');
		};
	}
]);