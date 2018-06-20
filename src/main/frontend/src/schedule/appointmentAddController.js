angular.module('Schedule').controller('Schedule.AppointmentAddController', [

	'$scope',
	'$filter',
	'$uibModalInstance',
	'$timeout',
	'demographicService',
	'demographicsService',
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
		demographicsService,
		me,
		providerService,
		scheduleService,
		apptDate)
	{

		var controller = this;

		controller.types = [];

		controller.urgencies = [
		{
			value: '',
			label: 'Normal'
		},
		{
			value: 'critical',
			label: 'Critical'
		}];

		controller.me = me;

		controller.appointment = {
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
				controller.types = results.types;
				controller.types.unshift(
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

		controller.selectType = function selectType()
		{
			var type = null;

			for (var x = 0; x < controller.types.length; x++)
			{
				if (controller.types[x].name == controller.appointment.type)
				{
					type = controller.types[x];
					break;
				}
			}
			if (type != null)
			{
				controller.appointment.duration = type.duration;
				controller.appointment.location = type.location;
				controller.appointment.notes = type.notes;
				controller.appointment.reason = type.reason;
				controller.appointment.resources = type.resources;

			}

		};

		controller.close = function close()
		{
			if (controller.needsUpdate)
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

		controller.validate = function validate()
		{
			var t = controller.appointment;

			controller.errors = [];

			if (t.demographic == null)
			{
				controller.errors.push('You must select a patient');
			}
			if (t.providerNo == null)
			{
				controller.errors.push('You must select a provider');
			}
			if (t.startTime == null || t.startTime.length == 0)
			{
				controller.errors.push('start time is required');
			}
			if (t.duration == null || t.duration.length == 0)
			{
				controller.errors.push('start time is required');
			}

			if (controller.errors.length > 0)
			{
				return false;
			}
			return true;
		};

		controller.save = function save()
		{
			controller.showErrors = true;
			if (!controller.validate())
			{
				return;
			}

			var x = {};
			x.status = controller.appointment.status;
			x.appointmentDate = controller.appointment.appointmentDate;
			x.startTime12hWithMedian = controller.appointment.startTime;
			x.type = controller.appointment.type;
			x.duration = controller.appointment.duration;
			x.providerNo = controller.appointment.providerNo;
			x.reason = controller.appointment.reason;
			x.notes = controller.appointment.notes;
			x.location = controller.appointment.location;
			x.resources = controller.appointment.resources;
			x.urgency = controller.appointment.critical;
			x.demographicNo = controller.appointment.demographicNo;

			// TODO: make sure this works with the updated backend service (response changed)
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


		controller.updateDemographicNo = function updateDemographicNo(item, model, label)
		{

			demographicService.getDemographic(model).then(
				function success(results)
				{
					controller.appointment.demographicNo = results.demographicNo;
					controller.appointment.demographicName = '';
					controller.appointment.demographic = results;

				},
				function error(errors)
				{
					console.log(errors);
				});

		};

		controller.searchPatients = function searchPatients(term)
		{
			var search = {
				type: 'Name',
				'term': term,
				active: true,
				integrator: false,
				outofdomain: true
			};
			return demographicsService.search(search, 0, 25).then(
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

		controller.searchProviders = function searchProviders(val)
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


		controller.updateProviderNo = function updateProviderNo(item, model, label)
		{
			controller.appointment.providerNo = model;
			controller.appointment.providerName = label;
		};
	}
]);