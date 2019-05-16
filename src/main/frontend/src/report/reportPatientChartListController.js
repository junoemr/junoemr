angular.module('Report').controller('Report.ReportPatientChartListController', [

	'$scope',
	'$log',
	'providerService',

	function(
		$scope,
		$log,
		providerService)
	{

		var controller = this;

		controller.params = {};

		controller.searchProviders = function searchProviders(val)
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
		controller.updateProviderNo = function updateProviderNo(item, model, label)
		{
			controller.params.providerNo = model;
			controller.params.name = label;
		};

		controller.generateReport = function generateReport()
		{
			var p = controller.params;
			if (!p.providerNo)
			{
				alert('Please enter a provider');
				return false;
			}
			var url = '../report/reportpatientchartlist.jsp?provider_no=' + p.providerNo;
			window.open(url, 'report_patientchartlist', 'height=900,width=700');
		};
	}
]);