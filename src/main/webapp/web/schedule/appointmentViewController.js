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

		$scope.me = me;
		$scope.appointment = appointment;
		$scope.statusList = statusList.content;
		$scope.appointmentUpdate = {};

		$scope.appointmentWriteAccess = false;

		$scope.getStatus = function getStatus(status)
		{

			for (var x = 0; x < $scope.statusList.length; x++)
			{
				console.log(JSON.stringify($scope.statusList[x]));
				if ($scope.statusList[x].status == status)
				{
					return $scope.statusList[x].description;
				}
			}
			return status;
		};

		$scope.close = function close()
		{
			$uibModalInstance.close(false);
		};

		$scope.deleteAppointment = function deleteAppointment()
		{
			if (confirm('Are you sure you want to delete this appointment?'))
			{
				scheduleService.deleteAppointment($scope.appointment.id).then(
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


		$scope.editProvider = function editProvider()
		{
			$scope.showProviderFormControl = true;
			$scope.appointmentUpdate.providerNo = $scope.appointment.providerNo;
			$scope.appointmentUpdate.providerName = $scope.appointment.provider.lastName + "," + $scope.appointment.provider.lastName;
		};

		$scope.updateProvider = function updateProvider(item, model, label)
		{
			$scope.needsUpdate = true;
			$scope.appointment.providerNo = model;
			$scope.appointment.providerName = label;
			$scope.showProviderFormControl = false;
		};

		$scope.cancelProviderUpdate = function cancelProviderUpdate()
		{
			$scope.appointmentUpdate.providerNo = null;
			$scope.appointmentUpdate.providerName = null;

			$scope.showProviderFormControl = false;

		};

		$scope.showAppointmentHistory = function showAppointmentHistory()
		{
			scheduleService.appointmentHistory($scope.appointment.demographicNo).then(
				function success(results)
				{
					alert(JSON.stringify(results));
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		$scope.noShowAppointment = function noShowAppointment()
		{
			scheduleService.noShowAppointment($scope.appointment.id).then(
				function success(results)
				{
					$uibModalInstance.close(true);
				},
				function error(errors)
				{
					console.log(errors);
				});

		};

		$scope.cancelAppointment = function cancelAppointment()
		{
			scheduleService.cancelAppointment($scope.appointment.id).then(
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