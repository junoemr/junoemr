import {SystemPreferenceApi} from "../../generated";
import TicklerAttachmentToTicklerLinkDtoConverter from "../lib/tickler/converter/TicklerAttachmentToTicklerLinkDtoConverter";
import {SystemProperties} from "../common/services/systemPreferenceServiceConstants";
import {LABEL_POSITION} from "../common/components/junoComponentConstants";
import moment from "moment";

angular.module('Tickler').component('ticklerAddComponent', {
	templateUrl: 'src/tickler/ticklerAdd.jsp',
	bindings: {
		modalInstance: "<",
		resolve: "<",
	},
	controller: [
		'$scope',
		'$filter',
		'$stateParams',
		'$http',
		'$httpParamSerializer',
		'demographicService',
		'demographicsService',
		'providerService',
		'ticklerService',
		'focusService',
		function (
			$scope,
			$filter,
			$stateParams,
			$http,
			$httpParamSerializer,
			demographicService,
			demographicsService,
			providerService,
			ticklerService,
			focusService)
		{
			const controller = this;
			let systemPreferenceApi = new SystemPreferenceApi($http, $httpParamSerializer, '../ws/rs');

			$scope.LABEL_POSITION = LABEL_POSITION;

			// holds the patient typeahead selection
			controller.demographicSearch = null;
			controller.isDisabled = false; // Save button enabled by default

			controller.defaultTicklerProviderNo = null;
			controller.defaultTicklerProviderName = null;

			controller.tickler = {
				template:
					{
						id: 1,
						name: ''
					},
				serviceDateMoment: moment().startOf('day'),
				serviceDateTime: "12:00 AM",
				suggestedTextId: 0,
				taskAssignedTo: null,
				taskAssignedToName: null,
				attachments: null,
			};

		controller.priorities = ['Low', 'Normal', 'High'];
		controller.initialDemographicNo = null;

		// initialization
		controller.$onInit = async () =>
		{
			controller.setTicklerProvider();

			controller.tickler.attachments = controller.resolve.attachment ? [controller.resolve.attachment] : null;

			if (Juno.Common.Util.exists($stateParams.demographicNo) || controller.resolve.presetDemographicNo)
			{
				controller.initialDemographicNo = $stateParams.demographicNo || controller.resolve.presetDemographicNo;
				const model = await demographicService.getDemographic(controller.initialDemographicNo);
				controller.demographicSearch = {
					demographicNo: model.id,
					firstName: model.firstName,
					lastName: model.lastName,
					name: model.displayName, // For display purposes
				};
				controller.tickler.demographic = model;
			}

			ticklerService.getTextSuggestions().then(function(data)
			{
				controller.textSuggestions = data.content;
				controller.textSuggestions.unshift(
					{
						id: 0,
						suggestedText: ''
					});
			}, function(reason)
			{
				alert(reason);
			});
		};

		controller.$postLink = () =>
		{
			if(Juno.Common.Util.isBlank(controller.initialDemographicNo))
			{
				focusService.focusRef(controller.demographicSearchRef);
			}
			else
			{
				focusService.focusRef(controller.providerSearchRef);
			}
		}

		controller.close = function close()
		{
			controller.modalInstance.close(false);
		};

		controller.validate = function validate()
		{
			var t = controller.tickler;
			controller.errors = [];

			if (t.demographic == null)
			{
				controller.errors.push('You must select a patient');
			}
			if (t.taskAssignedTo == null || t.taskAssignedTo.length == 0)
			{
				controller.errors.push('You must assign a provider');
			}
			if (t.message == null || t.message.length == 0)
			{
				controller.errors.push('Message is required');
			}
			if (!t.serviceDateMoment || !t.serviceDateMoment.isValid())
			{
				controller.errors.push('Service Date is required');
			}
			return controller.errors.length <= 0;
		};

		controller.saveWithEncounter = function saveWithEncounter()
		{
			return controller.save(true);
		}

		controller.save = function save(writeEncounter = false)
		{
			controller.isDisabled = true; // Disable save button
			controller.showErrors = true;
			if (!controller.validate())
			{
				controller.isDisabled = false; // Enable save button if validation failed
				return;
			}

			var tickler = {};
			tickler.demographicNo = controller.tickler.demographic.id;
			tickler.taskAssignedTo = controller.tickler.taskAssignedTo;
			tickler.priority = controller.tickler.priority;
			tickler.status = 'A';
			tickler.message = controller.tickler.message;
			if (controller.tickler.attachments)
			{
				tickler.attachments = (new TicklerAttachmentToTicklerLinkDtoConverter()).convertList(controller.tickler.attachments);
			}

			tickler.serviceDate = controller.tickler.serviceDateMoment.toDate();
            ticklerService.add(tickler, writeEncounter).then(
                (response) =>
                {
                    controller.modalInstance.close(true);
                }).catch((error) =>
                {
                    alert(error);
                }).finally(() =>
                {
                    controller.isDisabled = false;
                });


		};

		controller.updateDemographicNo = async function updateDemographicNo(demo)
		{
			if (Juno.Common.Util.exists(demo))
			{
				// update the selected value on the tickler object
				controller.tickler.demographic = await demographicService.getDemographic(demo.demographicNo);
			}
			else
			{
				controller.tickler.demographic = null;
			}
		};

		controller.searchPatients = function searchPatients(term)
		{
			var search = {
				type: demographicsService.SEARCH_MODE.Name,
				term: term,
				status: demographicsService.STATUS_MODE.ACTIVE,
				integrator: false,
				outofdomain: true
			};
			return demographicsService.search(search, 0, 25).then(
				function(results)
				{
					var resp = [];
					for (var x = 0; x < results.data.length; x++)
					{
						resp.push(
						{
							demographicNo: results.data[x].demographicNo,
							name: results.data[x].lastName + ',' + results.data[x].firstName
						});
					}
					return resp;
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		controller.searchProviders = function searchProviders(val)
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

		controller.updateProviderNo = function updateProviderNo(item, model, label)
		{
			controller.tickler.taskAssignedTo = model;
			controller.tickler.taskAssignedToName = label;
		};

		controller.setSuggestedText = function setSuggestedText()
		{
			var results = $filter('filter')(controller.textSuggestions,
			{
				id: controller.tickler.suggestedTextId
			}, true);

			if (results != null)
			{
				controller.tickler.message = results[0].suggestedText;
			}
		};

		controller.addMonthsFromNow = function(num)
		{
			controller.tickler.serviceDateMoment = moment().add(num, 'months');
		};

		controller.setTicklerProvider = async function setTicklerProvider()
		{
			try
			{
				let systemPrefApiResponse = await systemPreferenceApi.getPropertyValue(SystemProperties.DefaultTicklerProvider);
				controller.defaultTicklerProviderNo = parseInt(systemPrefApiResponse.data.body);

				if (systemPrefApiResponse.data.body !== null)
				{
					let providerServiceResponse = await providerService.getProvider(controller.defaultTicklerProviderNo);
					controller.setTicklerProviderAssignee(providerServiceResponse);
				}
			}
			catch (error)
			{
				console.log(error);
			}
		}

		controller.setTicklerProviderAssignee = (resp) =>
		{
			let firstName = resp.firstName || "";
			let lastName = resp.lastName || "";

			if (firstName === "" && lastName === "")
			{
				return;
			}

			let name = firstName + " " + lastName;
			controller.defaultTicklerProviderName = name;

			controller.tickler.taskAssignedTo = controller.defaultTicklerProviderNo;
			controller.tickler.taskAssignedToName = controller.defaultTicklerProviderName;
		}
	}
]});