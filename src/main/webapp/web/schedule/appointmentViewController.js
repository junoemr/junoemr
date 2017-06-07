angular.module('Schedule').controller('Schedule.AppointmentViewController', [

	'$scope',
	'$filter',
	'$uibModalInstance',
	'$timeout',
	'demographicService',
	'me',
	'providerService',
	'scheduleService',
	'appointment',
	'statusList',

	function(
		$scope,
		$filter,
		$uibModalInstance,
		$timeout,
		demographicService,
		me,
		providerService,
		scheduleService,
		appointment,
		statusList)
	{

		var controller = this;

		controller.me = me;
		controller.appointment = appointment;
		controller.statusList = statusList.content;
		controller.appointmentUpdate = {};

		controller.appointmentWriteAccess = false;

		controller.getStatus = function getStatus(status)
		{

			for (var x = 0; x < controller.statusList.length; x++)
			{
				console.log(JSON.stringify(controller.statusList[x]));
				if (controller.statusList[x].status == status)
				{
					return controller.statusList[x].description;
				}
			}
			return status;
		};

		controller.close = function close()
		{
			$uibModalInstance.close(false);
		};

		controller.deleteAppointment = function deleteAppointment()
		{
			if (confirm('Are you sure you want to delete this appointment?'))
			{
				scheduleService.deleteAppointment(controller.appointment.id).then(
					function success(results)
					{
						$uibModalInstance.close(true);
					},
					function error(errors)
					{
						console.log(errors);
					});
			}
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


		controller.editProvider = function editProvider()
		{
			controller.showProviderFormControl = true;
			controller.appointmentUpdate.providerNo = controller.appointment.providerNo;
			controller.appointmentUpdate.providerName = controller.appointment.provider.lastName + "," + controller.appointment.provider.lastName;
		};

		controller.updateProvider = function updateProvider(item, model, label)
		{
			controller.needsUpdate = true;
			controller.appointment.providerNo = model;
			controller.appointment.providerName = label;
			controller.showProviderFormControl = false;
		};

		controller.cancelProviderUpdate = function cancelProviderUpdate()
		{
			controller.appointmentUpdate.providerNo = null;
			controller.appointmentUpdate.providerName = null;

			controller.showProviderFormControl = false;

		};

		controller.showAppointmentHistory = function showAppointmentHistory()
		{
			scheduleService.appointmentHistory(controller.appointment.demographicNo).then(
				function success(results)
				{
					alert(JSON.stringify(results));
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		controller.noShowAppointment = function noShowAppointment()
		{
			scheduleService.noShowAppointment(controller.appointment.id).then(
				function success(results)
				{
					$uibModalInstance.close(true);
				},
				function error(errors)
				{
					console.log(errors);
				});

		};

		controller.cancelAppointment = function cancelAppointment()
		{
			scheduleService.cancelAppointment(controller.appointment.id).then(
				function success(results)
				{
					$uibModalInstance.close(true);
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

	}
]);