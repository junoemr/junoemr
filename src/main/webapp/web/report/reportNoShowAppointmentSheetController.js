angular.module('Report').oscarApp.controller('Report.ReportNoShowAppointmentSheetController', [

	'$scope',
	'$log',
	'$filter',
	'providerService',

	function(
		$scope,
		$log,
		$filter,
		providerService)
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
			var url = '../report/reportnoshowapptlist.jsp?provider_no=' + p.providerNo + '&sdate=' + startDate;
			window.open(url, 'report_noShowApptSheet', 'height=900,width=700');
		};
	}
]);