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

		controller.dateOptions = {
			showWeeks: false
		};

		scheduleService.getStatuses().then(
			function success(results)
			{
				controller.statuses = results.content;
			},
			function error(errors)
			{
				console.log(errors);
			});

		controller.getAppointmentTextStyle = function getAppointmentTextStyle(patient)
		{
			if (patient.demographicNo == 0)
			{
				return {
					'color': 'white'
				};
			}
		};

		//TODO:this gets called alot..should switch to a dictionary.
		controller.getAppointmentStyle = function getAppointmentStyle(patient)
		{
			if (patient.demographicNo == 0)
			{
				return {
					'background-color': 'black'
				};
			}

			if (controller.statuses != null)
			{
				for (var i = 0; i < controller.statuses.length; i++)
				{
					if (controller.statuses[i].status == patient.status)
					{
						return {
							'background-color': controller.statuses[i].color
						};
					}
				}
			}

			return {};
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

			temp = 0;

			controller.currenttab = patientListState.tabItems[temp];
			var lastIndx = controller.currenttab.url.lastIndexOf("/");
			controller.currenttab.url = controller.currenttab.url.slice(0, lastIndx + 1) + day;
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