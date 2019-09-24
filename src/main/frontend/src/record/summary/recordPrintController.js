angular.module('Record.Summary').controller('Record.Summary.RecordPrintController', [

	'$uibModal',
	'$uibModalInstance',
	'$stateParams',
	'selectedNoteList',

	function(
		$uibModal,
		$uibModalInstance,
		$stateParams,
		selectedNoteList)
	{

		var controller = this;

		controller.printTypeEnum = Object.freeze({
			all: 'all',
			dates: 'dates',
			selected: 'selected',
		});

		controller.page = {
			selectedWarning: false,
		};
		controller.pageOptions = {
			printType: controller.printTypeEnum.all,
			dates: {},
			selectedList: selectedNoteList,
		};


		/*
		 *If at least one note selected, Default to Note. Other wise default to All
		 */
		if (controller.pageOptions.selectedList.length > 0)
		{
			controller.pageOptions.printType = controller.printTypeEnum.selected;
		}

		controller.printToday = function printToday()
		{
			controller.pageOptions.printType = controller.printTypeEnum.dates;
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

			if (controller.pageOptions.printType === controller.printTypeEnum.all)
			{
				queryString = queryString + '&notes2print=ALL_NOTES';
			}
			else if (controller.pageOptions.printType === controller.printTypeEnum.selected)
			{
				queryString = queryString + '&notes2print=' + controller.pageOptions.selectedList.join();
			}
			else if (controller.pageOptions.printType === controller.printTypeEnum.dates)
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

			if (controller.pageOptions.printType === controller.printTypeEnum.selected
				&& controller.pageOptions.selectedList.length === 0)
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
			if (controller.pageOptions.printType ===controller.printTypeEnum.selected
				&& controller.pageOptions.selectedList.length === 0)
			{
				controller.page.selectedWarning = true;
				return;
			}
			else
			{
				controller.page.selectedWarning = false;
			}

			var ops = encodeURIComponent(JSON.stringify(controller.pageOptions));
			window.open('../ws/rs/recordUX/' + $stateParams.demographicNo + '/print?printOps=' + ops, '_blank');



		};
	}
]);