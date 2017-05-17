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

		scheduleService.getStatuses().then(function(data)
		{
			$scope.statuses = data.content;
		}, function(reason)
		{
			alert(reason);
		});

		$scope.getAppointmentTextStyle = function(patient)
		{
			if (patient.demographicNo == 0)
			{
				return {
					'color': 'white'
				};
			}
		};

		//TODO:this gets called alot..should switch to a dictionary.
		$scope.getAppointmentStyle = function(patient)
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


		$scope.today = function()
		{
			$scope.appointmentDate = new Date();
		};

		$scope.today();

		$scope.clear = function()
		{
			$scope.appointmentDate = null;
		};

		$scope.open = function($event)
		{
			$event.preventDefault();
			$event.stopPropagation();
			$scope.opened = true;
		};

		Date.prototype.AddDays = function(noOfDays)
		{
			this.setTime(this.getTime() + (noOfDays * (1000 * 60 * 60 * 24)));
			return this;
		};

		$scope.switchDay = function(n)
		{
			var dateNew = $scope.appointmentDate;
			dateNew.AddDays(n);

			$scope.appointmentDate = dateNew;

			var formattedDate = $filter('date')(dateNew, 'yyyy-MM-dd');

			$scope.changeApptList(formattedDate);


		};

		$scope.changeApptDate = function()
		{
			if ($scope.appointmentDate == undefined)
			{
				$scope.today();
			}
			var formattedDate = $filter('date')($scope.appointmentDate, 'yyyy-MM-dd');
			$scope.changeApptList(formattedDate);
		};

		$scope.changeApptList = function(day)
		{

			temp = 0;

			$scope.currenttab = $scope.tabItems[temp];
			var lastIndx = $scope.currenttab.url.lastIndexOf("/");
			$scope.currenttab.url = $scope.currenttab.url.slice(0, lastIndx + 1) + day;
			$scope.showFilter = true;
			$scope.refresh();

		};

		$scope.addNewAppointment = function()
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

			modalInstance.result.then(function(data)
			{
				$scope.switchDay(0);
			}, function(reason)
			{
				alert(reason);
			});
		};

		$scope.viewAppointment = function(apptNo)
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

			modalInstance.result.then(function(data)
			{
				$scope.switchDay(0);

			}, function(reason)
			{
				alert(reason);
			});
		};
	}
]);
