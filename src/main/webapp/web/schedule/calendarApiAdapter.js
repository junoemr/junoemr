'use strict';

// Retrieves and formats the calendar data so it displays in fullcalendar.
angular.module('Schedule').factory(
	'Schedule.CalendarApiAdapter',
	[
		'$q',
		'$stateParams',
		'$uibModal',

		'resultsService',

		'cpCalendar.Util',

		'demographicService',
		'scheduleService',
/*		'Models.ScheduleTemplate',
		'Models.Schedule',
		'Models.ScheduleScheduleTemplate',
		'Models.ScheduleAvailability',
		'Models.AvailabilityType',
		'Models.EventStatus',
		'Models.Patient',
		'Models.Event',*/

		function (
			$q,
			$stateParams,
			$uibModal,

			results_factory,

			util,

			demographicService,
			scheduleService

/*			schedule_template_model,
			schedule_model,
			schedule_template_relation_model,
			schedule_availability_model,
			availability_type_model,
			event_status_model,
			patient_model,
			event_model*/
		) {

			var service = {
				event_statuses: null,
				default_event_color: null,
				//patient_model: patient_model,
			};


			// ===============================================================================================
			// Public Methods
			// ===============================================================================================


			// -----------------------------------------------------------------------------------------------
			// Data Loading/manipulation
			// -----------------------------------------------------------------------------------------------

			service.load_schedule_events = function load_schedule_events(schedule, start, end,
																		 view_name, schedule_templates, event_statuses, default_event_color, availability_types)
			{
				// Set some global state
				service.event_statuses = event_statuses;
				service.default_event_color = default_event_color;


				var deferred = $q.defer();

				var schedule_events = [];
				var background_events = [];

				// load the schedule events,
				// then create the calendar events for each event,
				// then create the background calendar events for
				//   each schedule availability and schedule template relation
				event_model.list(schedule.uuid, start.format(), end.format()).then(
					function success(results)
					{
						for(var i = 0; i < results.data.length; i++)
						{
							schedule_events.push(
								service.create_schedule_event(results.data[i]));
						}
					}).then(
					function success()
					{
						// don't bother assembling background events for month view
						if(view_name !== "month")
						{
							if(util.exists(schedule.availabilities))
							{
								for(var j = 0; j < schedule.availabilities.length; j++)
								{
									util.create_availability_events(
										background_events, schedule.availabilities[j], availability_types,
										start, end);
								}
							}

							if(util.exists(schedule.relations))
							{
								for(var k = 0; k < schedule.relations.length; k++)
								{
									util.create_relation_events(
										background_events, schedule_templates, availability_types,
										schedule.relations[k], start, end);
								}
							}
						}

					}).then(function success()
				{
					Array.prototype.push.apply(schedule_events, background_events);

					deferred.resolve(results_factory.factory(schedule_events));
				});

				return deferred.promise;

			};

			service.load_schedule_templates = function load_schedule_templates()
			{
				var deferred = $q.defer();

				var schedule_templates = {};
				var promise_array = [];

				schedule_template_model.list().then(
					function success(results)
					{
						for(var i = 0; i < results.data.length; i++)
						{
							var result = results.data[i];
							promise_array.push(schedule_template_model.load(result.uuid));
						}
					}).then(
					function success()
					{
						$q.all(promise_array).then(
							function success(results_array)
							{
								for(var i=0; i<results_array.length; i++)
								{
									var result = results_array[i].data;
									schedule_templates[result.uuid] = result;
								}
								deferred.resolve(schedule_templates);
							});
					});

				return deferred.promise;
			};

			service.load_availability_types = function load_availability_types()
			{
				var deferred = $q.defer();
				var availability_types = {};

				availability_type_model.list().then(
					function success(results)
					{
						for(var i = 0; i < results.data.length; i++)
						{
							var result = results.data[i];
							availability_types[result.uuid] = result;
						}

						deferred.resolve(availability_types);
					});

				return deferred.promise;
			};

			service.load_event_statuses = function load_event_statuses()
			{
				var deferred = $q.defer();

				event_status_model.list().then(
					function success(results)
					{
						deferred.resolve(results.data);
					});

				return deferred.promise;
			};

			service.load_schedule_options = function load_schedule_options()
			{
				var deferred = $q.defer();

				schedule_model.list().then(
					function success(results)
					{
						deferred.resolve(results.data);
					});

				return deferred.promise;
			};

			service.get_selected_schedule = function get_selected_schedule(schedule_options)
			{
				// priority: last used from global state, then preference setting,
				// then default (first in the list)
				var selected_uuid = null;
				if($stateParams.default_schedule)
				{
					// Passed in url string
					selected_uuid = $stateParams.default_schedule;
				}
				else
				{
					selected_uuid = service.get_global_state('schedule_default');
				}
				if(selected_uuid === null)
				{
					selected_uuid = service.get_global_preference_setting('schedule_default');
				}

				if(util.exists(selected_uuid))
				{
					// only choose it if it can be found in the options list
					for(var i = 0; i < schedule_options.length; i++)
					{
						if(selected_uuid === schedule_options[i].uuid)
						{
							return schedule_options[i];
						}
					}
				}

				if(schedule_options.length > 0)
				{
					// select the first schedule in the list by default
					return schedule_options[0];
				}

				return null;
			};

			service.get_selected_resources = function get_selected_resources(resource_options)
			{
				// priority: last used from global state, then preference setting,
				// then default (all the non-doctor schedules)

				var custom_resource_list =
					service.get_global_state('schedule_resources');

				if(custom_resource_list === null)
				{
					custom_resource_list = service.get_global_preference_setting(
						'schedule_resources');
				}

				return schedule_model.get_group_resources(
					custom_resource_list, resource_options);
			};

			service.get_selected_time_interval = function get_selected_time_interval(
				time_interval_options, default_time_interval)
			{
				// priority: last used from global state, then preference setting,
				// then default

				var selected_time_interval = null;

				var time_interval = service.get_global_state('schedule_time_interval');
				if(time_interval === null)
				{
					time_interval = service.get_global_preference_setting(
						'schedule_time_interval');
				}

				if(util.exists(time_interval))
				{
					// only choose it if it can be found in the options list
					for(var i = 0; i < time_interval_options.length; i++)
					{
						if(time_interval === time_interval_options[i])
						{
							selected_time_interval = time_interval_options[i];
							break;
						}
					}
				}

				if(selected_time_interval === null)
				{
					return default_time_interval;
				}

				return selected_time_interval;
			};

			service.get_schedule_min_time = function get_schedule_min_time() {
				// restrict day view if user preferences are set

				var min_time = service.get_global_preference_setting('schedule_min_time');
				if (util.exists(min_time)) {
					// format: HH24:MM:SS - expect HH24:MM in preference
					return min_time + ":00";
				}

				return null;
			};

			service.get_schedule_max_time = function get_schedule_max_time()
			{
				var max_time = service.get_global_preference_setting('schedule_max_time');
				if(util.exists(max_time))
				{
					// format: HH24:MM:SS - expect HH24:MM in preference
					return max_time + ":00";
				}

				return null;
			};


			service.load_schedule = function load_schedule(uuid)
			{
				var deferred = $q.defer();

				schedule_model.fetch(uuid).then(
					function success(results)
					{
						service.load_schedule_availabilities(results.data).then(
							function success(results)
							{
								service.load_schedule_tmpl_rels(results.data).then(
									function success(results)
									{
										deferred.resolve(results);
									}, function error(errors)
									{
										console.log('error loading schedule template rels', errors);
									});
							}, function error(errors)
							{
								console.log('error loading schedule availabilities', errors);
							});
					}, function error(errors)
					{
						console.log('error fetching schedules', errors);
					});

				return deferred.promise;
			};

			service.save_event = function save_event(
				edit_mode,
				event_uuid,
				start_datetime,
				end_datetime,
				event_data,
				schedule_uuid,
				selected_event_status_uuid,
				patient_uuid
			)
			{
				var deferred = $q.defer();

				event_model.clear();

				if(edit_mode)
				{
					event_model.uuid = event_uuid;
				}

				angular.extend(
					event_model.data,
					event_data,
					{
						start_time: start_datetime.toDate(),
						end_time: end_datetime.toDate(),
						schedule_uuid: schedule_uuid,
						event_status_uuid: selected_event_status_uuid,
						demographics_patient_uuid: patient_uuid
					});

				event_model.save().then(
					function()
					{
						deferred.resolve(results_factory.factory());
					},
					function ()
					{
						deferred.reject();
					});

				return deferred.promise;
			};

			service.delete_event = function delete_event(event_uuid)
			{
				var deferred = $q.defer();

				event_model.del(event_uuid).then(function()
				{
					deferred.resolve();

				}, function()
				{
					deferred.reject();
				});

				return deferred.promise;
			};


			//-------------------------------------------------------------------------
			// Event Handlers
			//-------------------------------------------------------------------------/

			service.drop_event = function drop_event(uuid, schedule_uuid, delta)
			{
				var deferred = $q.defer();

				event_model.load(uuid).then(
					function success()
					{
						event_model.data.start_time =
							util.get_datetime_moment(event_model.data.start_time).add(
								delta.asMilliseconds(), 'milliseconds');

						event_model.data.end_time =
							util.get_datetime_moment(event_model.data.end_time).add(
								delta.asMilliseconds(), 'milliseconds');

						event_model.data.schedule_uuid = schedule_uuid;

						event_model.save().then(
							function success()
							{
								deferred.resolve(angular.copy(event_model.data));

							}, function error(errors)
							{
								deferred.reject(errors);
							});
					});

				return deferred.promise;
			};

			service.resize_event = function resize_event(uuid, delta)
			{
				var deferred = $q.defer();

				event_model.load(uuid).then(
					function success()
					{
						event_model.data.end_time =
							util.get_datetime_moment(event_model.data.end_time).add(
								delta.asMilliseconds(), 'milliseconds');

						event_model.save().then(
							function success()
							{
								deferred.resolve(angular.copy(event_model.data));

							}, function error(errors)
							{
								deferred.reject(errors);
							});
					});

				return deferred.promise;
			};

			service.rotate_event_status = function rotate_event_status(uuid, rotate_statuses)
			{
				var deferred = $q.defer();

				event_model.load(uuid).then(
					function success()
					{
						var next_index = null;
						for(var i = 0; i < rotate_statuses.length; i++)
						{
							if(rotate_statuses[i].uuid ===
								event_model.data.event_status_uuid)
							{
								next_index = (i + 1) % rotate_statuses.length;
								break;
							}
						}

						event_model.data.event_status_uuid =
							rotate_statuses[next_index].uuid;

						event_model.save().then(
							function success()
							{
								deferred.resolve(angular.copy(event_model.data));
							});
					});

				return deferred.promise;
			};

			//-------------------------------------------------------------------------
			// Global State Manipulation
			//-------------------------------------------------------------------------/

			service.get_global_preference_setting = function get_global_preference_setting(key)
			{
/*
				if(util.exists(global_state.preferences) &&
					util.exists(global_state.preferences.settings))
				{
					return global_state.preferences.settings[key];
				}
*/

				return null;
			};

			service.get_global_state = function get_global_setting(key)
			{
				var setting = null;//global_state[key];
				if(!util.exists(setting))
				{
					setting = null;
				}
				return setting;
			};

			service.save_global_state = function save_global_setting(key, value)
			{
				//global_state[key] = value;
			};

			// -----------------------------------------------------------------------------------------------
			// Interface Actions
			// -----------------------------------------------------------------------------------------------

			service.get_create_invoice_url = function get_create_invoice_url(
				event_uuid, demographics_practitioner_uuid, demographics_patient_uuid)
			{
				var url = "#/invoice/create?schedule_event_uuid=" +
					encodeURIComponent(event_uuid);

				if(util.exists(demographics_patient_uuid))
				{
					url += "&demographics_patient_uuid=" +
						encodeURIComponent(demographics_patient_uuid);
				}

				if(util.exists(demographics_practitioner_uuid))
				{
					url += "&demographics_practitioner_uuid=" + encodeURIComponent(
						demographics_practitioner_uuid);
				}

				return url;
			};

			service.get_patient_demographic_url = function get_patient_demographic_url(event)
			{
				return "#/patient/" + encodeURIComponent(event.data.demographics_patient_uuid) + "/view";
			};

			service.get_create_chart_note_url = function get_create_chart_note_url(event)
			{
				return "#/patient/" + encodeURIComponent(event.data.demographics_patient_uuid) +
					"/chart_notes?event_uuid=" + encodeURIComponent(event.data.uuid);
			};

			service.open_patient_dialog = function open_patient_dialog(edit_mode_callback, on_save_callback,
																	   load_error_link_patient_fn)
			{
				//global_state.enable_keyboard_shortcuts = false;

				return $uibModal.open({
					animation: false,
					backdrop: 'static',
					size: 'lg',
					controller: 'Invoice.Common.Patient.FormController',
					templateUrl: 'code/invoice/common/patient/quick_form.html',
					resolve: {
						edit_mode: edit_mode_callback,
						on_save_callback: on_save_callback,
						load_error_link_patient_fn: load_error_link_patient_fn
					}
				});
			};

			service.get_schedule_height = function get_schedule_height()
			{
				// get the full window height, minus the header height, minus a buffer for schedule options
				var schedule_height = $(window).height() - $('#right-pane').offset().top - 80;
				if(schedule_height < $scope.min_height) {
					schedule_height = $scope.min_height;
				}
				return schedule_height;
			}


			// ===============================================================================================
			// Private Methods
			// ===============================================================================================

			service.create_schedule_event = function create_schedule_event(event)
			{
				var event_status_color =
					service.event_statuses[event.event_status_uuid] ?
						service.event_statuses[event.event_status_uuid].color :
						service.default_event_color;

				return {
					data: event,
					start: util.get_datetime_moment(event.start_time),
					end: util.get_datetime_moment(event.end_time),
					color: event_status_color,
					className: service.event_class(event_status_color),
					resourceId: event.schedule_uuid
				};
			};

			service.event_class = function event_class(bg_color)
			{
				var red = parseInt(bg_color.substr(1,2),16);
				var green = parseInt(bg_color.substr(3,2),16);
				var blue = parseInt(bg_color.substr(5,2),16);
				var yiq = ((red * 299) + (green * 587) + (blue * 114)) / 1000;

				return (yiq >= 128) ? 'text-dark' : 'text-light';
			};

			service.load_schedule_availabilities = function load_schedule_availabilities(schedule)
			{
				var deferred = $q.defer();

				schedule_availability_model.schedule_list(schedule).then(
					function success(results)
					{
						schedule.availabilities = results.data;
						deferred.resolve(results_factory.factory(schedule));
					}, function error(errors)
					{
						console.log('load_schedule_availabilities error', errors);
					});

				return deferred.promise;
			};

			service.load_schedule_tmpl_rels = function load_schedule_tmpl_rels(schedule)
			{
				var deferred = $q.defer();

				schedule_template_relation_model.schedule_list(schedule).then(
					function success(results)
					{
						schedule.relations = results.data;
						deferred.resolve(results_factory.factory(schedule));
					});

				return deferred.promise;
			};


			return service;
		}
	]
);
