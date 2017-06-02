angular.module('Report').controller('Report.ReportOldPatientsController', [

	'$scope',
	'$log',
	'providerService',

	function(
		$scope,
		$log,
		providerService)
	{
		$scope.params = {
			providerNo: '',
			age: 65
		};

		$scope.searchProviders = function searchProviders(val)
		{
			var search = {
				searchTerm: val,
				active: true
			};
			return providerService.searchProviders(search, 0, 10).then(
				function success(response)
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
			var url = '../report/reportpatientchartlistspecial.jsp?provider_no=' + (p.providerNo === '' ? '*' : p.providerNo) + '&age=' + p.age;
			window.open(url, 'report_oldpatients', 'height=900,width=700');
		};
	}
]);