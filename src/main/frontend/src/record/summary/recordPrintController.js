import {SitesApi, SystemPreferenceApi} from "../../../generated";

angular.module('Record.Summary').controller('Record.Summary.RecordPrintController', [

	'$scope',
	'$http',
	'$httpParamSerializer',
	'$uibModal',
	'$uibModalInstance',
	'$stateParams',
	'selectedNoteList',
	'providerService',

	function(
		$scope,
		$http,
		$httpParamSerializer,
		$uibModal,
		$uibModalInstance,
		$stateParams,
		selectedNoteList,
		providerService)
	{

		var controller = this;

		const DEFAULT_SITE = 0;
		controller.sitesApi = new SitesApi($http, $httpParamSerializer, '../ws/rs');
		controller.systemPreferenceApi = new SystemPreferenceApi($http, $httpParamSerializer, '../ws/rs');

		controller.defaultClinic = {value: DEFAULT_SITE, label: "Default Clinic"};
		controller.selectedSite = null;
		controller.sites = [];

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
			selectedSite: DEFAULT_SITE,
		};

		controller.$onInit = async () =>
		  {
		  	controller.isMultisiteEnabled = (await controller.systemPreferenceApi.getPropertyEnabled("multisites")).data.body;

			if(controller.isMultisiteEnabled)
			{
				await controller.getSites();
			}
		  }

		  controller.getSites = async () =>
			{
				const provider = (await providerService.getMe());
				const sites = (await controller.sitesApi.getSitesByProvider(provider.providerNo)).data.body;

				controller.sites = [];
				controller.sites.push(controller.defaultClinic);

				sites.forEach((site) =>
				{
					controller.sites.push(
					{
						label: site.name,
						value: site.siteId,
					})
				});
			}

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

		controller.checkDates = function checkDates()
		{
			let printType = controller.pageOptions.printType;
			let checkDates = true;

			if ((printType === "dates") && (controller.pageOptions.dates.start == null || controller.pageOptions.dates.end == null))
			{
				alert("Enter a date range.");
				checkDates = false;
			}
			return checkDates;
		}

		controller.cancelPrint = function cancelPrint()
		{
			$uibModalInstance.dismiss('cancel');
		};

		controller.print = function print()
		{
			let site = controller.selectedSite;
			let validDates = controller.checkDates();
			if (!validDates)
			{
				return;
			}

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

			 controller.pageOptions.selectedSite = site || DEFAULT_SITE;

			let ops = encodeURIComponent(JSON.stringify(controller.pageOptions));
			window.open('../ws/rs/recordUX/' + $stateParams.demographicNo + '/print?printOps=' + ops, '_blank');
		};
	}
]);