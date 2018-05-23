angular.module('Schedule').controller('Schedule.ScheduleController', [

	'$scope',

	function(
		$scope
	)
	{
		//var controller = this;


		//=========================================================================
		// Local scope variables
		//=========================================================================/

		$scope.uiConfig = {};
		$scope.selectedSchedule = null;
		$scope.selectedTimeInterval = null;
		$scope.selectedResources = [];

		// cpCalendar control object.  The cpCalendar directive puts its control API methods in
		// this object.
		$scope.cpCalendarControl = {};



		//=========================================================================
		// Public Methods
		//=========================================================================/

		$scope.isSchedulingEnabled = function isSchedulingEnabled()
		{
			return true;
			//return global_state.global_settings.addons.scheduling_enabled;
		};

		$scope.isInitialized = function isInitialized()
		{
			return $scope.callCalendarMethod("is_initialized");
		};

		$scope.hasSchedules = function hasSchedules()
		{
			return $scope.callCalendarMethod("has_schedules");
		};

		$scope.showScheduleSelect = function showScheduleSelect()
		{
			return true;
		};

		$scope.getTimeIntervalOptions = function getTimeIntervalOptions()
		{
			return $scope.callCalendarMethod("get_time_interval_options");
		};

		$scope.getScheduleOptions = function getScheduleOptions()
		{
			return $scope.callCalendarMethod("get_schedule_options");
		};

		$scope.viewName = function viewName()
		{
			return $scope.callCalendarMethod("view_name");
		};

		$scope.showScheduleSelect = function()
		{
			return $scope.viewName() !== 'resourceDay';
		};

		$scope.showTimeIntervals = function showTimeIntervals()
		{
			return $scope.viewName() !== 'month';
		};

		$scope.onScheduleChanged = function onScheduleChanged()
		{
			return $scope.callCalendarMethod("on_schedule_changed", [$scope.selectedSchedule.uuid]);
		};

		$scope.onTimeIntervalChanged = function onTimeIntervalChanged()
		{
			$scope.saveGlobalState('schedule_time_interval', $scope.selectedTimeInterval);

			// updating the config will automatically trigger an events refresh
			if($scope.uiConfig.calendar)
			{
				$scope.uiConfig.calendar.slotDuration = $scope.selected_time_interval;
				$scope.uiConfig.calendar.slotLabelInterval = $scope.selected_time_interval;
			}

		};

		$scope.changeView = function changeView(view)
		{
			return $scope.callCalendarMethod('change_view', [view]);
		};

		$scope.showLegend = function showLegend()
		{
			return $scope.callCalendarMethod('show_legend');
		};


		//=========================================================================
		// Private methods
		//=========================================================================/

		// Calls the named function on the calendar control object.  This is here to fail without
		// an error if the calendar isn't initialized yet.
		$scope.callCalendarMethod = function callCalendarMethod(name, args)
		{
			if($scope.cpCalendarControl && angular.isFunction($scope.cpCalendarControl[name]))
			{
				return $scope.cpCalendarControl[name].apply(this, args);
			}

			return null;
		};

		$scope.saveGlobalState = function saveGlobalSetting(key, value)
		{
			//$scope.global_state[key] = value;
		};
	}
]);