angular.module('Tickler').controller('Tickler.TicklerAddController', [

	'$scope',
	'$uibModalInstance',
	'$filter',
	'$stateParams',
	'demographicService',
	'providerService',
	'ticklerService',

	function(
		$scope,
		$uibModalInstance,
		$filter,
		$stateParams,
		demographicService,
		providerService,
		ticklerService)
	{

		var controller = this;

		// holds the patient typeahead selection
		controller.demographicSearch = null;

		//=========================================================================
		// Watches
		//=========================================================================

		$scope.$watch('demographicSearch',
			function(new_value)
			{
				console.log('watching demographicSearch: ', new_value);

				if (Juno.Common.Util.exists(new_value))
				{
					controller.updateDemographicNo(new_value.demographicNo);
				}
				else
				{
					// no selection
					controller.updateDemographicNo(null);
				}
			}, true);

		controller.tickler = {
			template:
			{
				id: 1,
				name: ''
			},
			serviceDateDate: new Date(),
			serviceDateTime: new Date(),
			suggestedTextId: 0
		};

		controller.priorities = ['Low', 'Normal', 'High'];

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
			t.demographicNo = controller.tickler.demographicNo;
			t.taskAssignedTo = controller.tickler.taskAssignedTo;
			t.priority = controller.tickler.priority;
			t.status = 'A';
			t.message = controller.tickler.message;


			var givenDate = controller.tickler.serviceDateDate;
			var givenTime = controller.tickler.serviceDateTime;
			givenDate.setHours(givenTime.getHours());
			givenDate.setMinutes(givenTime.getMinutes());

			t.serviceDate = givenDate;

			ticklerService.add(t).then(function(data)
			{
				$uibModalInstance.close(true);
			}, function(reason)
			{
				alert(reason);
			});


		};

		controller.updateDemographicNo = function(demographicNo)
		{
			if (Juno.Common.Util.exists(demographicNo))
			{
				demographicService.getDemographic(demographicNo).then(function(data)
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

		// initialization
		if (Juno.Common.Util.exists($stateParams.demographicNo))
		{
			console.log('initializing demographicSearch pre-selected', $stateParams.demographicNo);
			demographicService.getDemographic($stateParams.demographicNo).then(function(data)
			{
				controller.demographicSearch = {
					demographicNo: $stateParams.demographicNo,
					firstName: data.firstName,
					lastName: data.lastName
				};
			});
		}

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
	}
]);