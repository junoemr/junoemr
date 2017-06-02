angular.module('PatientList').controller('PatientList.PatientListAppointmentListController', [

	'$scope',
	'$http',
	'$q',
	'$filter',
	'$uibModal',
	'Navigation',
	'scheduleService',
	'providerService',

	function(
		$scope,
		$http,
		$q,
		$filter,
		$uibModal,
		Navigation,
		scheduleService,
		providerService)
	{

		$scope.dateOptions = {
			showWeeks: false
		};

		scheduleService.getStatuses().then(
			function success(results)
			{
				$scope.statuses = results.content;
			},
			function error(errors)
			{
				console.log(errors);
			});

		$scope.getAppointmentTextStyle = function getAppointmentTextStyle(patient)
		{
			if (patient.demographicNo == 0)
			{
				return {
					'color': 'white'
				};
			}
		};

		//TODO:this gets called alot..should switch to a dictionary.
		$scope.getAppointmentStyle = function getAppointmentStyle(patient)
		{
			if (patient.demographicNo == 0)
			{
				return {
					'background-color': 'black'
				};
			}

			if ($scope.statuses != null)
			{
				for (var i = 0; i < $scope.statuses.length; i++)
				{
					if ($scope.statuses[i].status == patient.status)
					{
						return {
							'background-color': $scope.statuses[i].color
						};
					}
				}
			}

			return {};
		};


		$scope.today = function today()
		{
			$scope.appointmentDate = new Date();
		};

		$scope.today();

		$scope.clear = function clear()
		{
			$scope.appointmentDate = null;
		};

		$scope.open = function open($event)
		{
			$event.preventDefault();
			$event.stopPropagation();
			$scope.opened = true;
		};

		Date.prototype.AddDays = function AddDays(noOfDays)
		{
			this.setTime(this.getTime() + (noOfDays * (1000 * 60 * 60 * 24)));
			return this;
		};

		$scope.switchDay = function switchDay(n)
		{
			var dateNew = $scope.appointmentDate;
			dateNew.AddDays(n);

			$scope.appointmentDate = dateNew;

			var formattedDate = $filter('date')(dateNew, 'yyyy-MM-dd');

			$scope.changeApptList(formattedDate);


		};

		$scope.changeApptDate = function changeApptDate()
		{
			if ($scope.appointmentDate == undefined)
			{
				$scope.today();
			}
			var formattedDate = $filter('date')($scope.appointmentDate, 'yyyy-MM-dd');
			$scope.changeApptList(formattedDate);
		};

		$scope.changeApptList = function changeApptList(day)
		{

			temp = 0;

			$scope.currenttab = $scope.tabItems[temp];
			var lastIndx = $scope.currenttab.url.lastIndexOf("/");
			$scope.currenttab.url = $scope.currenttab.url.slice(0, lastIndx + 1) + day;
			$scope.showFilter = true;
			$scope.refresh();

		};

		$scope.addNewAppointment = function addNewAppointment()
		{
			var modalInstance = $uibModal.open(
			{
				templateUrl: 'schedule/appointmentAdd.jsp',
				controller: 'Schedule.AppointmentAddController',
				backdrop: false,
				size: 'lg',
				resolve:
				{
					me: function()
					{
						return providerService.getMe();
					},
					apptDate: function()
					{
						return $scope.appointmentDate;
					}
				}
			});

			modalInstance.result.then(
				function success(results)
				{
					$scope.switchDay(0);
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		$scope.viewAppointment = function viewAppointment(apptNo)
		{
			var modalInstance = $uibModal.open(
			{
				templateUrl: 'schedule/appointmentView.jsp',
				controller: 'Schedule.AppointmentViewController',
				backdrop: false,
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
					$scope.switchDay(0);

				},
				function error(errors)
				{
					console.log(errors);
				});
		};
	}
]);