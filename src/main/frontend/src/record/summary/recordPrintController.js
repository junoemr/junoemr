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

		var controller = this;

		controller.pageOptions = {};
		controller.pageOptions.printType = {};
		controller.pageOptions.dates = {};
		controller.page = {};
		controller.page.selectedWarning = false;

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
			controller.pageOptions.printType = 'selected';
		}
		else
		{
			console.log("printType = all");
			controller.pageOptions.printType = 'all';
		}

		controller.printToday = function printToday()
		{
			controller.pageOptions.printType = 'dates';
			var date = new Date();
			controller.pageOptions.dates.start = date;
			controller.pageOptions.dates.end = date;
		};

		controller.cancelPrint = function cancelPrint()
		{
			$uibModalInstance.dismiss('cancel');
		};


		controller.sendToPhr = function sendToPhr()
		{
			var queryString = "demographic_no=" + $stateParams.demographicNo;
			queryString = queryString + "&module=echart";

			if (controller.pageOptions.printType == 'all')
			{
				queryString = queryString + '&notes2print=ALL_NOTES';
			}
			else if (controller.pageOptions.printType == 'selected')
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
			else if (controller.pageOptions.printType == 'dates')
			{
				queryString = queryString + '&notes2print=ALL_NOTES';
				queryString = queryString + '&startDate=' + controller.pageOptions.dates.start.getTime();
				queryString = queryString + '&endDate=' + controller.pageOptions.dates.end.getTime();
			}

			if (controller.pageOptions.cpp)
			{
				queryString = queryString + '&printCPP=true';
			}
			if (controller.pageOptions.cpp)
			{
				queryString = queryString + '&printRx=true';
			}
			if (controller.pageOptions.cpp)
			{
				queryString = queryString + '&printLabs=true';
			}
			console.log("QS" + queryString);

			if (controller.pageOptions.printType === 'selected' && selectedList.length == 0)
			{
				controller.page.selectedWarning = true;
				return;
			}
			else
			{
				controller.page.selectedWarning = false;
			}

			window.open('../SendToPhr.do?' + queryString, '_blank');
		};

		controller.print = function print()
		{
			var selectedList = [];
			for (var i = 0; i < mod.length; i++)
			{
				if (mod[i].isSelected)
				{
					selectedList.push(mod[i].noteId);
				}
			}

			if (controller.pageOptions.printType === 'selected' && selectedList.length == 0)
			{
				controller.page.selectedWarning = true;
				return;
			}
			else
			{
				controller.page.selectedWarning = false;
			}

			controller.pageOptions.selectedList = selectedList;
			var ops = encodeURIComponent(JSON.stringify(controller.pageOptions));
			window.open('../ws/rs/recordUX/' + $stateParams.demographicNo + '/print?printOps=' + ops, '_blank');



		};
	}
]);