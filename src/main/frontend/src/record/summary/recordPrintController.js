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