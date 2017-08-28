angular.module('Report').controller('Report.ReportOldPatientsController', [

	'$scope',
	'$log',
	'providerService',

	function(
		$scope,
		$log,
		providerService)
	{

		var controller = this;

		controller.params = {
			providerNo: '',
			age: 65
		};

		controller.searchProviders = function searchProviders(val)
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
		controller.updateProviderNo = function updateProviderNo(item, model, label)
		{
			controller.params.providerNo = model;
			controller.params.name = label;
		};

		controller.generateReport = function generateReport()
		{
			var p = controller.params;
			var url = '../report/reportpatientchartlistspecial.jsp?provider_no=' + (p.providerNo === '' ? '*' : p.providerNo) + '&age=' + p.age;
			window.open(url, 'report_oldpatients', 'height=900,width=700');
		};
	}
]);