angular.module('Schedule').controller('Schedule.AppointmentAddController', [

	'$scope',
	'$filter',
	'$uibModalInstance',
	'$timeout',
	'demographicService',
	'me',
	'providerService',
	'scheduleService',
	'apptDate',

	function(
		$scope,
		$filter,
		$uibModalInstance,
		$timeout,
		demographicService,
		me,
		providerService,
		scheduleService,
		apptDate)
	{

		$scope.types = [];

		$scope.urgencies = [
		{
			value: '',
			label: 'Normal'
		},
		{
			value: 'critical',
			label: 'Critical'
		}];

		$scope.me = me;

		$scope.appointment = {
			status: 't',
			appointmentDate: $filter('date')(apptDate, 'yyyy-MM-dd'),
			startTime: '09:00 AM',
			type: '',
			duration: 15,
			providerName: me.formattedName,
			providerNo: me.providerNo,
			reason: '',
			notes: '',
			location: '',
			resources: '',
			critical: ''
		};

		scheduleService.getTypes().then(
			function success(results)
			{
				$scope.types = results.types;
				$scope.types.unshift(
				{
					name: '',
					duration: 15,
					location: '',
					notes: '',
					reason: '',
					resources: ''
				});
				console.log(JSON.stringify(results));
			},
			function error(errors)
			{
				console.log(errors);
			});

		$scope.selectType = function selectType()
		{
			var type = null;

			for (var x = 0; x < $scope.types.length; x++)
			{
				if ($scope.types[x].name == $scope.appointment.type)
				{
					type = $scope.types[x];
					break;
				}
			}
			if (type != null)
			{
				$scope.appointment.duration = type.duration;
				$scope.appointment.location = type.location;
				$scope.appointment.notes = type.notes;
				$scope.appointment.reason = type.reason;
				$scope.appointment.resources = type.resources;

			}

		};

		$scope.close = function close()
		{
			if ($scope.needsUpdate)
			{
				if (confirm("You have unsaved changes, are you sure?"))
				{
					$uibModalInstance.close(false);
				}
			}
			else
			{
				$uibModalInstance.close(false);
			}

		};

		$scope.validate = function validate()
		{
			var t = $scope.appointment;

			$scope.errors = [];

			if (t.demographic == null)
			{
				$scope.errors.push('You must select a patient');
			}
			if (t.providerNo == null)
			{
				$scope.errors.push('You must select a provider');
			}
			if (t.startTime == null || t.startTime.length == 0)
			{
				$scope.errors.push('start time is required');
			}
			if (t.duration == null || t.duration.length == 0)
			{
				$scope.errors.push('start time is required');
			}

			if ($scope.errors.length > 0)
			{
				return false;
			}
			return true;
		};

		$scope.save = function save()
		{
			$scope.showErrors = true;
			if (!$scope.validate())
			{
				return;
			}

			var x = {};
			x.status = $scope.appointment.status;
			x.appointmentDate = $scope.appointment.appointmentDate;
			x.startTime12hWithMedian = $scope.appointment.startTime;
			x.type = $scope.appointment.type;
			x.duration = $scope.appointment.duration;
			x.providerNo = $scope.appointment.providerNo;
			x.reason = $scope.appointment.reason;
			x.notes = $scope.appointment.notes;
			x.location = $scope.appointment.location;
			x.resources = $scope.appointment.resources;
			x.urgency = $scope.appointment.critical;
			x.demographicNo = $scope.appointment.demographicNo;

			console.log(JSON.stringify(x));
			scheduleService.addAppointment(x).then(
				function success(results)
				{
					$uibModalInstance.close(true);
				},
				function error(errors)
				{
					console.log(errors);
				});


		};


		$scope.updateDemographicNo = function updateDemographicNo(item, model, label)
		{

			demographicService.getDemographic(model).then(
				function success(results)
				{
					$scope.appointment.demographicNo = results.demographicNo;
					$scope.appointment.demographicName = '';
					$scope.appointment.demographic = results;

				},
				function error(errors)
				{
					console.log(errors);
				});

		};

		$scope.searchPatients = function searchPatients(term)
		{
			var search = {
				type: 'Name',
				'term': term,
				active: true,
				integrator: false,
				outofdomain: true
			};
			return demographicService.search(search, 0, 25).then(
				function(results)
				{
					var resp = [];
					for (var x = 0; x < results.content.length; x++)
					{
						resp.push(
						{
							demographicNo: results.content[x].demographicNo,
							name: results.content[x].lastName + ',' + results.content[x].firstName
						});
					}
					return resp;
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		$scope.searchProviders = function searchProviders(val)
		{
			var search = {
				searchTerm: val,
				active: true
			};
			return providerService.searchProviders(search, 0, 10).then(
				function success(results)
				{
					var resp = [];
					for (var x = 0; x < results.length; x++)
					{
						resp.push(
						{
							providerNo: results[x].providerNo,
							name: results[x].firstName + ' ' + results[x].lastName
						});
					}
					return resp;
				},
				function error(errors)
				{
					console.log(errors);
				});
		};


		$scope.updateProviderNo = function updateProviderNo(item, model, label)
		{
			$scope.appointment.providerNo = model;
			$scope.appointment.providerName = label;
		};
	}
]);