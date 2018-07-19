
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
		$scope.selectedSiteName = null;
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

		$scope.hasSites = function hasSites()
		{
			return $scope.callCalendarMethod("has_sites");
		};

		$scope.getTimeIntervalOptions = function getTimeIntervalOptions()
		{
			return $scope.callCalendarMethod("get_time_interval_options");
		};

		$scope.getScheduleOptions = function getScheduleOptions()
		{
			return $scope.callCalendarMethod("get_schedule_options");
		};

		$scope.getSiteOptions = function getSiteOptions()
		{
			var result = $scope.callCalendarMethod("get_site_options");

			if(!angular.isDefined($scope.selectedSite) || $scope.selectedSite === null)
			{
				$scope.selectedSite = result[0];
			}

			return result;
		};

		$scope.viewName = function viewName()
		{
			return $scope.callCalendarMethod("view_name");
		};

		$scope.showTimeIntervals = function showTimeIntervals()
		{
			return $scope.viewName() !== 'month';
		};

		$scope.onScheduleChanged = function onScheduleChanged()
		{
			console.log("-- Schedule Changed ----------------------------");
			return $scope.callCalendarMethod("on_schedule_changed", [$scope.selectedSchedule, $scope.selectedSiteName]);
		};

		$scope.onSiteChanged = function onSiteChanged()
		{
			console.log("-- Site Changed ----------------------------");
			return $scope.callCalendarMethod("on_schedule_changed", [$scope.selectedSchedule, $scope.selectedSiteName]);
		};

		$scope.onTimeIntervalChanged = function onTimeIntervalChanged()
		{
			console.log("-- Time Changed ----------------------------");
			$scope.callCalendarMethod("on_time_interval_changed", [$scope.selectedTimeInterval]);
		};

		$scope.changeView = function changeView(view)
		{
			return $scope.callCalendarMethod('change_view', [view]);
		};

		$scope.showLegend = function showLegend()
		{
			return $scope.callCalendarMethod('show_legend');
		};

		$scope.isAgendaView = function isAgendaView()
		{
			return ($scope.viewName() != 'resourceDay')
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