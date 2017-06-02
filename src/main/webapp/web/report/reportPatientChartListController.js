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

		$scope.searchProviders = function searchProviders(val)
		{
			var search = {
				searchTerm: val,
				active: true
			};
			return providerService.searchProviders(search, 0, 10).then(
				function success(results)
				{
					var resp = [];
					for (var x = 0; x < results.length; x++)
					{
						resp.push(
						{
							providerNo: results[x].providerNo,
							name: results[x].firstName + ' ' + results[x].lastName
						});
					}
					return resp;
				},
				function error(errors)
				{
					console.log(errors);
				});
		};
		$scope.updateProviderNo = function updateProviderNo(item, model, label)
		{
			$scope.params.providerNo = model;
			$scope.data.providerNo = label;
		};

		$scope.generateReport = function generateReport()
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