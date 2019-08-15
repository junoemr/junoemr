angular.module('Tickler').controller('Tickler.TicklerAddController', [

	'$scope',
	'$uibModalInstance',
	'$filter',
	'$stateParams',
	'demographicService',
	'demographicsService',
	'providerService',
	'ticklerService',

	function(
		$scope,
		$uibModalInstance,
		$filter,
		$stateParams,
		demographicService,
		demographicsService,
		providerService,
		ticklerService)
	{

		var controller = this;

		// holds the patient typeahead selection
		controller.demographicSearch = null;

		//=========================================================================
		// Watches
		//=========================================================================

		// $scope.$watch('controller.demographicSearch',

		// 	function(new_value)
		// 	{
		// 		console.log('watching demographicSearch: ', new_value);

		// 		if (Juno.Common.Util.exists(new_value))
		// 		{
		// 			controller.updateDemographicNo(new_value);
		// 		}
		// 		else
		// 		{
		// 			// no selection
		// 			controller.updateDemographicNo(null);
		// 		}
		// 	}, true);

		controller.tickler = {
			template:
			{
				id: 1,
				name: ''
			},
			serviceDateDate: new Date(),
			serviceDateTime: "12:00 AM",
			suggestedTextId: 0
		};

		controller.priorities = ['Low', 'Normal', 'High'];

		// initialization
		controller.init = function init()
		{
			if (Juno.Common.Util.exists($stateParams.demographicNo))
			{
				console.log('initializing demographicSearch pre-selected', $stateParams.demographicNo);
				demographicService.getDemographic($stateParams.demographicNo).then(function(data)
				{
					controller.demographicSearch = {
						demographicNo: data.demographicNo,
						firstName: data.firstName,
						lastName: data.lastName,
						name: data.lastName + "," + data.firstName // For display purposes
					};
					controller.updateDemographicNo(data);
				});
			}

			$('#timepicker').timepicker({defaultTime: controller.tickler.serviceDateTime});
		};

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

		controller.close = function()
		{
			$uibModalInstance.close(false);
		};

		controller.validate = function()
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
			if (controller.errors.length > 0)
			{
				return false;
			}
			return true;
		};

		controller.save = function()
		{
			controller.showErrors = true;
			if (!controller.validate())
			{
				return;
			}

			var t = {};
			t.demographicNo = controller.tickler.demographic.demographicNo;
			t.taskAssignedTo = controller.tickler.taskAssignedTo;
			t.priority = controller.tickler.priority;
			t.status = 'A';
			t.message = controller.tickler.message;

			var givenDate = controller.tickler.serviceDateDate;
			var givenTime = moment(controller.tickler.serviceDateTime, 'hh:mm A');
			givenDate.setHours(givenTime.get('hour'));
			givenDate.setMinutes(givenTime.get('minute'));

			t.serviceDate = givenDate;
			ticklerService.add(t).then(function(data)
			{
				$uibModalInstance.close(true);
			}, function(reason)
			{
				alert(reason);
			});


		};

		controller.updateDemographicNo = function updateDemographicNo(demo)
		{
			if (Juno.Common.Util.exists(demo))
			{
				demographicService.getDemographic(demo.demographicNo).then(function(data)
				{
					// update the selected value on the tickler object
					controller.tickler.demographic = data;
					console.log('set controller.tickler.demographic: ', controller.tickler.demographic);
				});
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

		controller.searchProviders = function(val)
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

		controller.updateProviderNo = function(item, model, label)
		{
			controller.tickler.taskAssignedTo = model;
			controller.tickler.taskAssignedToName = label;
		};

		controller.setSuggestedText = function()
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
			controller.tickler.serviceDateDate = moment().add(num, 'months').toDate();
		};
	}
]);