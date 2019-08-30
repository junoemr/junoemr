angular.module('Report').controller('Report.ReportDaySheetController', [

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
			type: '',
			startDate: Juno.Common.Util.formatMomentDate(moment()),
			endDate: Juno.Common.Util.formatMomentDate(moment()),
			startTime: null,
			endTime: null
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
			controller.data.providerNo = label;
		};

		controller.generateReport = function generateReport()
		{
			var p = controller.params;
			if (p.type === 'all' || p.type === 'all-nr')
			{
				var startDate = $filter('date')(p.startDate, 'yyyy-MM-dd');
				var endDate = $filter('date')(p.endDate, 'yyyy-MM-dd');

				var startTime = $filter('date')(p.startTime, 'HH:mm');
				var endTime = $filter('date')(p.endTime, 'HH:mm');

				var url = '../report/reportdaysheet.jsp?dsmode=all&provider_no=' + (p.providerNo === '' ? '*' : p.providerNo) + '&sdate=' + startDate + '&edate=' + endDate + '&sTime=' + startTime + '&eTime=' + endTime;

				if (p.type === 'all-nr')
				{
					url += '&rosteredStatus=true';
				}

				window.open(url, 'report_daysheet', 'height=900,width=700');
			}

			if (p.type === 'new')
			{
				var startDate = $filter('date')(p.startDate, 'yyyy-MM-dd');
				var url = '../report/reportdaysheet.jsp?dsmode=new&provider_no=' + (p.providerNo === '' ? '*' : p.providerNo) + '&sdate=' + startDate;
				window.open(url, 'report_daysheet', 'height=900,width=700');
			}
			if (p.type === 'lab')
			{
				var startDate = $filter('date')(p.startDate, 'yyyy-MM-dd');
				var url = '../report/printLabDaySheetAction.do?xmlStyle=labDaySheet.xml&input_date=' + startDate;
				window.open(url, 'report_daysheet', 'height=900,width=700');

			}
			if (p.type === 'billing')
			{
				var startDate = $filter('date')(p.startDate, 'yyyy-MM-dd');
				var url = '../report/printLabDaySheetAction.do?xmlStyle=billDaySheet.xml&input_date=' + startDate;
				window.open(url, 'report_daysheet', 'height=900,width=700');
			}
			if (p.type === 'tab')
			{
				var startDate = $filter('date')(p.startDate, 'yyyy-MM-dd');
				var url = '../report/tabulardaysheetreport.jsp?provider_no=' + (p.providerNo === '' ? '*' : p.providerNo) + '&sdate=' + startDate;
				window.open(url, 'report_daysheet', 'height=900,width=700');
			}
		};

		controller.reset = function reset()
		{
			controller.params = {
				providerNo: '',
				type: '',
				startDate: Juno.Common.Util.formatMomentDate(moment()),
				endDate: Juno.Common.Util.formatMomentDate(moment()),
				startTime: null,
				endTime: null
			};
		};
	}
]);