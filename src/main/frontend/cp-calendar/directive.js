angular.module('cpCalendar')


//=========================================================================
// cpCalendar Directive
//=========================================================================/

.directive(
		'cpCalendar',

		[
			'$parse',
			'$injector',
			'$timeout',

			function(
					$parse,
					$injector,
					$timeout
			)
			{

				var scope = {
					ui_config: '=cpCalendar',
					control: '=cpCalendarControl',
					selected_schedule: '=cpCalendarSelectedSchedule',
					selected_time_interval: '=cpCalendarSelectedTimeInterval',
					calendarWatchEvent : '&'
				};

				var link_function = function link_function($scope, element, attributes, controller) {

					//=========================================================================
					// Control methods
					//=========================================================================/

					this.is_initialized = function is_initialized() {
						return $scope.is_initialized();
					};

					this.has_schedules = function has_schedules() {
						return $scope.schedules.length > 0;
					};

					this.has_sites = function has_sites() {
						return $scope.site_options.length > 0;
					};

					this.get_schedule_options = function get_schedule_options()
					{
						return $scope.schedule_options;
					};

					this.get_site_options = function get_site_options()
					{
						return $scope.site_options;
					};

					this.on_schedule_changed = function on_schedule_changed(schedule_uuid, site)
					{
						return $scope.on_schedule_changed(schedule_uuid, site);
					};

					this.on_time_interval_changed = function on_time_interval_changed(selected_time_interval)
					{
						return $scope.on_time_interval_changed(selected_time_interval);
					};

					this.change_view = function change_view(view)
					{
						return $scope.change_view(view);
					};

					this.view_name = function view_name()
					{
						return $scope.view_name();
					};

					this.get_time_interval_options = function get_time_interval_options()
					{
						return $scope.time_interval_options;
					};

					this.show_legend = function show_legend()
					{
						return $scope.show_legend();
					};

					// Create the API to communicate from the outside
					$parse(attributes.cpCalendarControl).assign($scope.$parent, this);


					//=========================================================================
					// External models
					//=========================================================================/

					// Link up all of the models
					$scope.calendar_api_adapter = $injector.get(attributes.cpCalendarCalendarApiAdapter);
					$scope.access_control = $injector.get(attributes.cpCalendarAccessControl);
					$scope.autocomplete = $injector.get(attributes.cpCalendarAutoComplete);
					$scope.global_state = $injector.get(attributes.cpCalendarGlobalState);
					$scope.patient_model = $injector.get(attributes.cpCalendarPatientModel);


					//=========================================================================
					// Start the scheduler
					//=========================================================================/

					$timeout(function(){
						$scope.init();
					});

				};

				return {
					restrict: 'A',
					scope: scope,
					controller: 'cpCalendar.Controller',
					template: '<div><div id="cp-calendar" class="calendar" ng-model="event_sources" calendar="cpCalendar" ui-calendar="ui_config_applied.calendar" ng-enabled="initialized"></div></div>',
					replace: true,
					link: link_function
				};
			}
		]
);
