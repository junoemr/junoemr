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

		$scope.getTime = function(hour, minutes)
		{
			var d = new Date();
			d.setHours(hour);
			d.setMinutes(minutes);
			return d;
		};

		$scope.params = {
			providerNo: '',
			type: '',
			startDate: new Date(),
			endDate: new Date(),
			startTime: $scope.getTime(8, 0),
			endTime: $scope.getTime(18, 0)
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

		$scope.reset = function()
		{
			$scope.params = {
				providerNo: '',
				type: '',
				startDate: new Date(),
				endDate: new Date(),
				startTime: $scope.getTime(8, 0),
				endTime: $scope.getTime(18, 0)
			};
		};
	}
]);