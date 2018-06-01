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
		'globalStateService',

		function (
			$q,
			$stateParams,
			$uibModal,

			results_factory,

			util,

			demographicService,
			scheduleService,
			globalStateService

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

			service.load_schedule_events = function load_schedule_events(providerId, siteName,
				start, end, view_name, schedule_templates, event_statuses, default_event_color,
				availability_types)
			{
				//console.log("FETCHING: " + providerId + " " + start.format("YYYY-MM-DD") + " " + end.subtract(1, "seconds").format("YYYY-MM-DD"));

				var deferred = $q.defer();


				// Get date strings to pass to the backend.  The calendar provides datetime that describe
				// and inclusive start time and exclusive end time, so one second is removed from
				// the end time to convert to the correct date.
				var startDateString = start.format("YYYY-MM-DD");
				var endDateString = end.subtract(1, 'seconds').format("YYYY-MM-DD");

				scheduleService.getSchedulesForCalendar(
					providerId,
					startDateString,
					endDateString,
					siteName
				).then(function success(results)
				{
					// Transform from camel to snake.  Normally this wouldn't need to happen, but
					// this is an external library that requires a certain format.
					// TODO: maybe do this in CXF?
					deferred.resolve({data: service.snake_schedule_results(results)});
				});

				return deferred.promise;
			};

			service.snake_appointment_data = function snake_appointment_data(data)
			{
				if(data == null)
				{
					return data;
				}

				return {
					schedule_uuid: data.scheduleUuid,
					event_status_uuid: data.eventStatusUuid,
					start_time: data.startTime,
					end_time: data.endTime,
					reason: data.reason,
					num_invoices: data.numInvoices,
					demographics_patient_dob: data.demographicPatientDob,
					demographics_patient_name: data.demographicPatientName,
					demographics_patient_phone: data.demographicPatientPhone,
					demographics_patient_uuid: data.demographicPatientUuid,
					demographics_practitioner_uuid: data.demographicPractitionerUuid,
					tag_names: data.tagNames,
					tag_self_booked: data.tagSelfBooked,
					tag_self_cancelled: data.tagSelfCancelled,
					tag_system_codes: data.tagSystemCodes
				};
			};

			service.snake_availability_type = function snake_availability_type(data)
			{
				if(data == null)
				{
					return data;
				}

				return {
					name: data.name,
					color: data.color,
					preferred_event_length_minutes: data.preferredEventLengthMinutes,
					system_code: data.systemCode,
				};
			}

			service.snake_schedule_results = function snake_schedule_results(results)
			{
				if(!angular.isArray(results))
				{
					return results;
				}

				var snake_results = [];

				for(var i = 0; i < results.length; i++)
				{
					var result = results[i];
					snake_results.push({
						resourceId: result.resourceId,
						start: result.start,
						end: result.end,
						color: result.color,
						rendering: result.rendering,
						schedule_template_code: result.scheduleTemplateCode,
						className: result.className,
						availability_type: service.snake_availability_type(result.availabilityType),
						data: service.snake_appointment_data(result.data),
					});
				}

				return snake_results;
			};

			service.load_schedule_templates = function load_schedule_templates()
			{
				// XXX: can't implement this because it requires the date range and provider number
				// because oscar stores it's templates per day and provider

				var deferred = $q.defer();

				var schedule_templates = {};
				var promise_array = [];

				deferred.resolve([]);
/*				schedule_template_model.list().then(
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
					});*/

				return deferred.promise;
			};

			service.load_availability_types = function load_availability_types()
			{
				var deferred = $q.defer();
				var availability_types = {};

				scheduleService.getScheduleTemplateCodes().then(
					function success(results)
					{
						/*	Example JSON result (P is the oscar scheduletemplatecode.code)
						var example_result = {
							P: {
								color: "#000000",
								name: "Do Not Book",
								preferred_event_length_minutes: null,
								system_code: "unavailable",
							},
						};
						*/

						for(var i = 0; i < results.length; i++)
						{
							var result = results[i];

							availability_types[result.code] = {
								color: result.color,
								name: result.description,
								preferred_event_length_minutes: result.duration,
								system_code: null,
							};
						}

						deferred.resolve(availability_types);
					});

				return deferred.promise;
			};

			service.load_event_statuses = function load_event_statuses()
			{
				var deferred = $q.defer();

				scheduleService.getAppointmentStatuses().then(
					function success(results)
					{
						deferred.resolve(service.snake_appointment_statuses(results));
					}
				);

				return deferred.promise;
			};

			service.load_sites = function load_sites()
			{
				var deferred = $q.defer();

				scheduleService.getSites().then(
					function success(results)
					{
						var out = [];
						if(angular.isArray(results))
						{
							for(var i = 0; i < results.length; i++)
							{
								out.push({
									uuid: results[i].siteId,
									name: results[i].name,
									display_name: results[i].name,
								});
							}
						}
						deferred.resolve(out);
					}
				);

				return deferred.promise;
			};

			service.snake_appointment_statuses = function snake_appointment_statuses(data)
			{
				if (!angular.isArray(data))
				{
					return data;
				}

				var snake_data = [];

				for (var i = 0; i < data.length; i++)
				{
					snake_data.push({
						uuid: data[i].displayLetter,
						name: data[i].name,
						display_letter: data[i].displayLetter,
						color: data[i].color,
						rotates: data[i].rotates,
						system_code: data[i].systemCode,
						sort_order: data[i].sortOrder,
					});
				}

				return snake_data;
			};


			service.load_schedule_options = function load_schedule_options()
			{
				var deferred = $q.defer();

				scheduleService.getScheduleGroups().then(
					function success(results)
					{
						for(var i = 0; i < results.length; i++)
						{
							results[i].uuid = results[i].identifier;
						}
						deferred.resolve(results);
					}
				);

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

				return service.get_group_resources(
					custom_resource_list, resource_options);
			};

			service.get_group_resources = function get_group_resources(default_selections, schedules)
			{
				var has_defaults = util.exists(default_selections) && default_selections.length > 0;

				var selected_resources = [];
				for(var i = 0; i < schedules.length; i++)
				{
					var schedule_data = schedules[i];

					if(has_defaults &&
						default_selections.indexOf(schedule_data.uuid) > -1)
					{
						selected_resources.push(schedule_data);
					}
					else if(!has_defaults &&
						!util.exists(schedule_data.demographics_practitioner_uuid))
					{
						selected_resources.push(schedule_data);
					}
				}

				// if default selections list is set but the schedules in it no longer exist,
				// or if there are no settings and no non-doctor schedules,
				// then could end up with nothing selected, so select everything
				if(selected_resources.length === 0)
				{
					for(var j = 0; j < schedules.length; j++)
					{
						selected_resources.push(schedule_data);
					}
				}

				return selected_resources;
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

/*				var min_time = service.get_global_preference_setting('schedule_min_time');
				if (util.exists(min_time)) {
					// format: HH24:MM:SS - expect HH24:MM in preference
					return min_time + ":00";
				}

				return null;*/

				return "08:00";
			};

			service.get_schedule_max_time = function get_schedule_max_time()
			{
/*				var max_time = service.get_global_preference_setting('schedule_max_time');
				if(util.exists(max_time))
				{
					// format: HH24:MM:SS - expect HH24:MM in preference
					return max_time + ":00";
				}

				return null;*/
				return "20:00";
			};


			service.load_schedule = function load_schedule(providerId)
			{
				var deferred = $q.defer();
				deferred.resolve({data: providerId});

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
				if(util.exists(globalStateService.preferences) &&
					util.exists(globalStateService.preferences.settings))
				{
					return globalStateService.preferences.settings[key];
				}

				return null;
			};

			service.get_global_state = function get_global_setting(key)
			{
				var setting = globalStateService[key];
				if(!util.exists(setting))
				{
					setting = null;
				}
				return setting;
			};

			service.save_global_state = function save_global_setting(key, value)
			{
				globalStateService[key] = value;
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
