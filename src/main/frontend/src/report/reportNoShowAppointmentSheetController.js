angular.module('Report').controller('Report.ReportNoShowAppointmentSheetController', [

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

		var controller = this;

		controller.params = {
			providerNo: '',
			startDate: Juno.Common.Util.formatMomentDate(moment()),
		};

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
			var startDate = $filter('date')(p.startDate, 'yyyy-MM-dd');
			var url = '../report/reportnoshowapptlist.jsp?provider_no=' + p.providerNo + '&sdate=' + startDate;
			window.open(url, 'report_noShowApptSheet', 'height=900,width=700');
		};
	}
]);