angular.module('Report').controller('Report.ReportPatientChartListController', [

	'$scope',
	'$log',
	'providerService',

	function(
		$scope,
		$log,
		providerService)
	{
		$scope.params = {
			providerNo: ''
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
			if (p.providerNo == '')
			{
				alert('Please enter a provider');
				return false;
			}
			var url = '../report/reportpatientchartlist.jsp?provider_no=' + p.providerNo;
			window.open(url, 'report_patientchartlist', 'height=900,width=700');
		};
	}
]);