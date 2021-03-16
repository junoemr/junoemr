angular.module('Record.Summary').controller('Record.Summary.RecordPrintController', [

	'$scope',
	'$uibModal',
	'$uibModalInstance',
	'$stateParams',
	'selectedNoteList',
	'providerService',

	function(
		$scope,
		$uibModal,
		$uibModalInstance,
		$stateParams,
		selectedNoteList,
		providerService)
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
			selectedSite: 0,
		};

		controller.siteSelection = providerService.getMe().then(
			function (user)
			{
				providerService.getSitesByProvider(user.providerNo).then(
					function success(result)
					{
						if (result == null)
						{
							controller.siteSelection = 0;
						}
						else
						{
							controller.siteSelection = result;
						}
					},
					function error(result)
					{
						console.error("Failed to fetch provider sites: " + result);
					})
			});

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
			let site = $scope.recordPrintCtrl.siteSelection.site;

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

			if (site != null)
			{
				controller.pageOptions.selectedSite = site;
			}
			else
			{
				controller.pageOptions.selectedSite = 0;
			}

			let ops = encodeURIComponent(JSON.stringify(controller.pageOptions));
			window.open('../ws/rs/recordUX/' + $stateParams.demographicNo + '/print?printOps=' + ops, '_blank');
		};
	}
]);