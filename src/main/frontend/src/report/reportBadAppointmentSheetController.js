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

			var url = '../report/reportapptsheet.jsp?dsmode=all&provider_no=' + p.providerNo + '&sdate=' + startDate;
			window.open(url, 'report_badApptSheet', 'height=900,width=700');
		};
	}
]);