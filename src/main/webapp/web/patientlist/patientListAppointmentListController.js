angular.module('PatientList').controller('PatientList.PatientListAppointmentListController', [

	'$scope',
	'$http',
	'$q',
	'$filter',
	'$uibModal',
	'Navigation',
	'scheduleService',
	'providerService',
	'patientListState',

	function(
		$scope,
		$http,
		$q,
		$filter,
		$uibModal,
		Navigation,
		scheduleService,
		providerService,
		patientListState)
	{

		var controller = this;

		controller.statusCodeMap = new Map();
		controller.statuses = null;


		controller.dateOptions = {
			showWeeks: false
		};

		scheduleService.getStatuses().then(
			function success(results)
			{
				controller.statuses = results.content;
				controller.statusCodeMap = new Map(controller.statuses.map(i => [i.status, i]));
			},
			function error(errors)
			{
				console.log(errors);
			});

		controller.getAppointmentStatusByStatusCode = function (statusCode)
		{
			return controller.statusCodeMap.get(statusCode);
		};
		controller.getAppointmentStatusColourByStatusCode = function (statusCode)
		{
			let status = controller.statusCodeMap.get(statusCode);
			let colour = "#000000";
			if(status)
			{
				colour = controller.statusCodeMap.get(statusCode).color;
			}
			return colour;
		};

		controller.today = function today()
		{
			controller.appointmentDate = new Date();
		};

		controller.today();

		controller.clear = function clear()
		{
			controller.appointmentDate = null;
		};

		controller.open = function open($event)
		{
			$event.preventDefault();
			$event.stopPropagation();
			controller.opened = true;
		};

		Date.prototype.AddDays = function AddDays(noOfDays)
		{
			this.setTime(this.getTime() + (noOfDays * (1000 * 60 * 60 * 24)));
			return this;
		};

		controller.switchDay = function switchDay(n)
		{
			var dateNew = controller.appointmentDate;
			dateNew.AddDays(n);

			controller.appointmentDate = dateNew;

			var formattedDate = $filter('date')(dateNew, 'yyyy-MM-dd');

			controller.changeApptList(formattedDate);


		};

		controller.changeApptDate = function changeApptDate()
		{
			if (controller.appointmentDate == undefined)
			{
				controller.today();
			}
			var formattedDate = $filter('date')(controller.appointmentDate, 'yyyy-MM-dd');
			controller.changeApptList(formattedDate);
		};

		controller.changeApptList = function changeApptList(day)
		{
			controller.currenttab = patientListState.tabItems[0];
			controller.currenttab.serviceMethod = function ()
			{
				return scheduleService.getAppointments(day).then(
					function success(results)
					{
						return results.patients;
					}
				);
			};
			controller.showFilter = true;
			$scope.$emit('juno:patientListRefresh');
		};

		controller.addNewAppointment = function addNewAppointment()
		{
			var modalInstance = $uibModal.open(
			{
				templateUrl: 'schedule/appointmentAdd.jsp',
				controller: 'Schedule.AppointmentAddController as appointmentAddCtrl',
				backdrop: 'static',
				size: 'lg',
				resolve:
				{
					me: function()
					{
						return providerService.getMe();
					},
					apptDate: function()
					{
						return controller.appointmentDate;
					}
				}
			});

			modalInstance.result.then(
				function success(results)
				{
					controller.switchDay(0);
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		controller.viewAppointment = function viewAppointment(apptNo)
		{
			var modalInstance = $uibModal.open(
			{
				templateUrl: 'schedule/appointmentView.jsp',
				controller: 'Schedule.AppointmentViewController as appointmentViewCtrl',
				backdrop: 'static',
				size: 'lg',
				resolve:
				{
					me: function()
					{
						return providerService.getMe();
					},
					appointment: function()
					{
						return scheduleService.getAppointment(apptNo);
					},
					statusList: function()
					{
						return scheduleService.getStatuses();
					}
				}
			});

			modalInstance.result.then(
				function success(results)
				{
					controller.switchDay(0);
				},
				function error(errors)
				{
					console.log(errors);
				});
		};
	}
]);