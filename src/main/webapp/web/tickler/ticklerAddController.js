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

		// holds the patient typeahead selection
		$scope.demographicSearch = null;

		//=========================================================================
		// Watches
		//=========================================================================

		$scope.$watch('demographicSearch',
			function(new_value)
			{
				console.log('watching demographicSearch: ', new_value);

				if(Juno.Common.Util.exists(new_value))
				{
					$scope.updateDemographicNo(new_value.demographicNo);
				}
				else
				{
					// no selection
					$scope.updateDemographicNo(null);
				}
			}, true);

		$scope.tickler = {
			template:
			{
				id: 1,
				name: ''
			},
			serviceDateDate: new Date(),
			serviceDateTime: new Date(),
			suggestedTextId: 0
		};

		$scope.priorities = ['Low', 'Normal', 'High'];

		ticklerService.getTextSuggestions().then(function(data)
		{
			$scope.textSuggestions = data.content;
			$scope.textSuggestions.unshift(
			{
				id: 0,
				suggestedText: ''
			});
		}, function(reason)
		{
			alert(reason);
		});

		$scope.close = function()
		{
			$uibModalInstance.close(false);
		};

		$scope.validate = function()
		{
			var t = $scope.tickler;
			$scope.errors = [];

			if (t.demographic == null)
			{
				$scope.errors.push('You must select a patient');
			}
			if (t.taskAssignedTo == null || t.taskAssignedTo.length == 0)
			{
				$scope.errors.push('You must assign a provider');
			}
			if (t.message == null || t.message.length == 0)
			{
				$scope.errors.push('Message is required');
			}
			if ($scope.errors.length > 0)
			{
				return false;
			}
			return true;
		};

		$scope.save = function()
		{
			$scope.showErrors = true;
			if (!$scope.validate())
			{
				return;
			}

			var t = {};
			t.demographicNo = $scope.tickler.demographicNo;
			t.taskAssignedTo = $scope.tickler.taskAssignedTo;
			t.priority = $scope.tickler.priority;
			t.status = 'A';
			t.message = $scope.tickler.message;


			var givenDate = $scope.tickler.serviceDateDate;
			var givenTime = $scope.tickler.serviceDateTime;
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

		$scope.updateDemographicNo = function(demographicNo)
		{
			if(Juno.Common.Util.exists(demographicNo))
			{
				demographicService.getDemographic(demographicNo).then(function(data)
				{
					// update the selected value on the tickler object
					$scope.tickler.demographic = data;
					console.log('set $scope.tickler.demographic: ', $scope.tickler.demographic);
				});
			}
			else
			{
				$scope.tickler.demographic = null;
			}
		};

		// initialization
		if (Juno.Common.Util.exists($stateParams.demographicNo))
		{
			console.log('initializing demographicSearch pre-selected', $stateParams.demographicNo);
			demographicService.getDemographic($stateParams.demographicNo).then(function(data)
			{
				$scope.demographicSearch = {
					demographicNo: $stateParams.demographicNo,
					firstName: data.firstName,
					lastName: data.lastName
				};
			});
		}

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
			$scope.tickler.taskAssignedTo = model;
			$scope.tickler.taskAssignedToName = label;
		};

		$scope.setSuggestedText = function()
		{
			var results = $filter('filter')($scope.textSuggestions,
			{
				id: $scope.tickler.suggestedTextId
			}, true);

			if (results != null)
			{
				$scope.tickler.message = results[0].suggestedText;
			}
		};
	}
]);
