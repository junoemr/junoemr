angular.module('Record.Summary').controller('Record.Summary.RecordPrintController', [

	'$scope',
	'$uibModal',
	'$uibModalInstance',
	'$filter',
	'mod',
	'action',
	'$stateParams',
	'summaryService',

	function(
		$scope,
		$uibModal,
		$uibModalInstance,
		$filter,
		mod,
		action,
		$stateParams,
		summaryService)
	{

		$scope.pageOptions = {};
		$scope.pageOptions.printType = {};
		$scope.pageOptions.dates = {};
		$scope.page = {};
		$scope.page.selectedWarning = false;

		/*
		 *If mod length > 0 than the user has selected a note. = Default to Note
		 *Other wise default to All
		 */
		var atleastOneSelected = false;
		for (var i = 0; i < mod.length; i++)
		{
			if (mod[i].isSelected)
			{
				atleastOneSelected = true;
				i = mod.length;
			}
		}

		if (atleastOneSelected)
		{
			console.log("mod len ", mod.length);
			$scope.pageOptions.printType = 'selected';
		}
		else
		{
			console.log("printType = all");
			$scope.pageOptions.printType = 'all';
		}

		$scope.printToday = function printToday()
		{
			$scope.pageOptions.printType = 'dates';
			var date = new Date();
			$scope.pageOptions.dates.start = date;
			$scope.pageOptions.dates.end = date;
		};

		$scope.cancelPrint = function cancelPrint()
		{
			$uibModalInstance.dismiss('cancel');
		};

		$scope.clearPrint = function clearPrint()
		{
			$scope.pageOptions = {};
			$scope.pageOptions.printType = {};
		};


		$scope.sendToPhr = function sendToPhr()
		{
			var queryString = "demographic_no=" + $stateParams.demographicNo;
			queryString = queryString + "&module=echart";

			if ($scope.pageOptions.printType == 'all')
			{
				queryString = queryString + '&notes2print=ALL_NOTES';
			}
			else if ($scope.pageOptions.printType == 'selected')
			{
				//get array
				var selectedList = [];
				for (var i = 0; i < mod.length; i++)
				{
					if (mod[i].isSelected)
					{
						selectedList.push(mod[i].noteId);
					}
				}
				queryString = queryString + '&notes2print=' + selectedList.join();
			}
			else if ($scope.pageOptions.printType == 'dates')
			{
				queryString = queryString + '&notes2print=ALL_NOTES';
				queryString = queryString + '&startDate=' + $scope.pageOptions.dates.start.getTime();
				queryString = queryString + '&endDate=' + $scope.pageOptions.dates.end.getTime();
			}

			if ($scope.pageOptions.cpp)
			{
				queryString = queryString + '&printCPP=true';
			}
			if ($scope.pageOptions.cpp)
			{
				queryString = queryString + '&printRx=true';
			}
			if ($scope.pageOptions.cpp)
			{
				queryString = queryString + '&printLabs=true';
			}
			console.log("QS" + queryString);

			if ($scope.pageOptions.printType === 'selected' && selectedList.length == 0)
			{
				$scope.page.selectedWarning = true;
				return;
			}
			else
			{
				$scope.page.selectedWarning = false;
			}

			window.open('../SendToPhr.do?' + queryString, '_blank');
		};

		$scope.print = function print()
		{
			//console.log('processList',mod);
			console.log($scope.pageOptions);
			var selectedList = [];
			for (var i = 0; i < mod.length; i++)
			{
				if (mod[i].isSelected)
				{
					selectedList.push(mod[i].noteId);
				}
			}
			console.log("selected list", selectedList);

			if ($scope.pageOptions.printType === 'selected' && selectedList.length == 0)
			{
				$scope.page.selectedWarning = true;
				return;
			}
			else
			{
				$scope.page.selectedWarning = false;
			}

			$scope.pageOptions.selectedList = selectedList;
			var ops = encodeURIComponent(JSON.stringify($scope.pageOptions));
			window.open('../ws/rs/recordUX/' + $stateParams.demographicNo + '/print?printOps=' + ops, '_blank');



		};
	}
]);