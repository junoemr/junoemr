
import {ScheduleApi} from "../../generated/JunoInternalApi/ScheduleApi";
import {
	CalendarAppointmentStatus, NewAppointmentTo1, SchedulingResponse
} from "../../generated";
import {modal} from 'angular-ui-bootstrap/src/modal';
import * as moment from 'moment';
import {
	IHttpParamSerializer,
	IHttpResponse,
	IHttpService,
	IQService
} from "../../node_modules/@types/angular";
import {IStateParamsService} from "../../node_modules/@types/angular-ui-router";
import {IModalService} from "../../node_modules/@types/angular-ui-bootstrap";

export class CalendarApiAdapter
{
	static $inject = [
        '$q',
        '$stateParams',
        '$uibModal',
		'$http',
		'$httpParamSerializer',

        'resultsService',

        'cpCalendar.Util',

        'demographicsService',
        'scheduleService',
        'globalStateService',
		'focusService',
	];

	constructor(
        private $q: IQService,
        private $stateParams: IStateParamsService,
        private $uibModal: IModalService,
        private $http: IHttpService,
        private $httpParamSerializer: IHttpParamSerializer,

        private results_factory,

        private util,

        private demographicsService,
        private scheduleService,
        private globalStateService,
		public focus
	){}


	event_statuses: null;
	default_event_color: null;

	patient_model = {
		uuid: null,
		full_name: null,
		has_photo: true,
		patient_photo_url: '/imageRenderingServlet?source=local_client&clientId=0',
		clear: function clear(){
			this.uuid = null;
			this.full_name = null;
			this.patient_photo_url = '/imageRenderingServlet?source=local_client&clientId=0';
			this.data.birth_date = null;
			this.data.health_number = null;
			this.data.ontario_version_code = null;
			this.data.phone_number_primary = null;
		},
		fill_data: function fill_data(data){
			this.uuid = data.uuid;
			this.full_name = data.full_name;
			this.patient_photo_url = '/imageRenderingServlet?source=local_client&clientId=' + data.uuid;
			this.data.birth_date = data.birth_date;
			this.data.health_number = data.health_number;
			this.data.ontario_version_code = data.ontario_version_code;
			this.data.phone_number_primary = data.phone_number_primary;
		},
		upload_photo: function upload_photo(file){},
		data: {
			birth_date: null,
			health_number: null,
			ontario_version_code: null,
			phone_number_primary: null
		}
	};

	scheduleApi = new ScheduleApi(this.$http, this.$httpParamSerializer, 'http://localhost:9090/ws/rs');


	public searchPatients(term)
	{
		var search = {
			type: 'Name',
			'term': term,
			status: 'active',
			integrator: false,
			outofdomain: true
		};

		return this.demographicsService.search(search, 0, 25).then(
			function(results)
			{
				var resp = [];
				for (var x = 0; x < results.content.length; x++)
				{
					resp.push({
						value_field: 'uuid',
						data: {
							uuid: results.content[x].demographicNo,
							full_name: results.content[x].lastName + ',' + results.content[x].firstName,
							birth_date: results.content[x].dob,
							health_number: results.content[x].hin,
							phone_number_primary: results.content[x].phone
						}
					});
				}
				return resp;
			},
			function error(errors)
			{
				console.log(errors);
			});
	}

	public load_schedule_events(schedule, siteName, start, end, view_name, schedule_templates,
		event_statuses, default_event_color, availability_types)
	{
		//console.log("FETCHING: " + providerId + " " + start.format("YYYY-MM-DD") + " " + end.subtract(1, "seconds").format("YYYY-MM-DD"));

		var deferred = this.$q.defer();

		var providerId = schedule.uuid;

		// Get date strings to pass to the backend.  The calendar provides datetime that describe
		// and inclusive start time and exclusive end time, so one second is removed from
		// the end time to convert to the correct date.
		var startDateString = start.format("YYYY-MM-DD");
		var endDateString = end.subtract(1, 'seconds').format("YYYY-MM-DD");

		this.scheduleService.getSchedulesForCalendar(
			providerId,
			startDateString,
			endDateString,
			siteName
		).then((results) =>
		{
			// Transform from camel to snake.  Normally this wouldn't need to happen, but
			// this is an external library that requires a certain format.
			deferred.resolve({data: this.snake_schedule_results(results)});
		});

		return deferred.promise;
	}

	private snake_appointment_data(data)
	{
		if(data == null)
		{
			return data;
		}

		return {
			schedule_uuid: data.scheduleUuid,
			event_status_uuid: data.eventStatusUuid,
			event_status_modifier: data.eventStatusModifier,
			start_time: data.startTime,
			end_time: data.endTime,
			site: data.site,
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
	}

	private snake_availability_type(data)
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

	private snake_schedule_results(results)
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
				availability_type: this.snake_availability_type(result.availabilityType),
				data: this.snake_appointment_data(result.data),
			});
		}

		return snake_results;
	}

	public load_schedule_templates()
	{
		// XXX: can't implement this because it requires the date range and provider number
		// because oscar stores it's templates per day and provider

		var deferred = this.$q.defer();

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
	}

	public load_availability_types()
	{
		var deferred = this.$q.defer();
		var availability_types = {};

		this.scheduleService.getScheduleTemplateCodes().then(
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
	}

	public load_event_statuses()
	{
		var deferred = this.$q.defer();

		//this.scheduleService.getAppointmentStatuses().then(
		this.scheduleApi.getCalendarAppointmentStatuses().then(
			(results) => {
				deferred.resolve(CalendarApiAdapter.snake_appointment_statuses(results.data.body));
			}
		);

		return deferred.promise;
	}

	private static snake_appointment_statuses(data: Array<CalendarAppointmentStatus>)
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
				icon: data[i].icon,
				rotates: data[i].rotates,
				system_code: data[i].systemCode,
				sort_order: data[i].sortOrder,
			});
		}

		return snake_data;
	}

	public load_sites()
	{
		var deferred = this.$q.defer();

		this.scheduleService.getSites().then(
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
							color: results[i].bgColor,
						});
					}
				}
				deferred.resolve(out);
			}
		)

		return deferred.promise;
	}


	public load_schedule_options()
	{
		var deferred = this.$q.defer();

		this.scheduleService.getScheduleGroups().then(
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
	}

	public get_selected_schedule(schedule_options)
	{
		// priority: last used from global state, then preference setting,
		// then default (first in the list)
		var selected_uuid = null;
		if(this.$stateParams.default_schedule)
		{
			// Passed in url string
			selected_uuid = this.$stateParams.default_schedule;
		}
		else
		{
			selected_uuid = this.get_global_state('schedule_default');
		}
		if(selected_uuid === null)
		{
			selected_uuid = this.get_global_preference_setting('schedule_default');
		}

		if(this.util.exists(selected_uuid))
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
	}

	public get_selected_resources(resource_options)
	{
		return [];

		/*	XXX: probably not using this because we don't select resources in juno

		// priority: last used from global state, then preference setting,
		// then default (all the non-doctor schedules)

		var custom_resource_list =
			this.get_global_state('schedule_resources');

		if(custom_resource_list === null)
		{
			custom_resource_list = this.get_global_preference_setting(
				'schedule_resources');
		}

		return this.get_group_resources(
			custom_resource_list, resource_options);*/
	}

/*	public get_group_resources(default_selections, schedules)
	{
		var has_defaults = this.util.exists(default_selections) && default_selections.length > 0;

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
				!this.util.exists(schedule_data.demographics_practitioner_uuid))
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
	}*/

	public get_selected_time_interval(
		time_interval_options, default_time_interval)
	{
		// priority: last used from global state, then preference setting,
		// then default

		var selected_time_interval = null;

		var time_interval = this.get_global_state('schedule_time_interval');
		if(time_interval === null)
		{
			time_interval = this.get_global_preference_setting(
				'schedule_time_interval');
		}

		if(this.util.exists(time_interval))
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
	}

	public get_schedule_min_time() {
		// restrict day view if user preferences are set

		/*				var min_time = service.get_global_preference_setting('schedule_min_time');
						if (util.exists(min_time)) {
							// format: HH24:MM:SS - expect HH24:MM in preference
							return min_time + ":00";
						}

						return null;*/

		return "08:00";
	}

	public get_schedule_max_time()
	{
		/*				var max_time = service.get_global_preference_setting('schedule_max_time');
						if(util.exists(max_time))
						{
							// format: HH24:MM:SS - expect HH24:MM in preference
							return max_time + ":00";
						}

						return null;*/
		return "20:00";
	}


	public load_schedule(providerId: string, )
	{
		var deferred = this.$q.defer();

		// TODO: fill up availabilities and relations, or figure out how to show that info without them
		var schedule = {
			uuid: providerId,
			availabilities: [], // TODO: figure out if these have a Juno equivalent, I don't think
								// TODO: they do.  They are things like holidays and vacation days
			relations: []
		};

		deferred.resolve({data: schedule});

		return deferred.promise;
	}


	public save_event(
		edit_mode: string,
		event_uuid: string,
		start_datetime: moment.Moment,
		end_datetime: moment.Moment,
		event_data: {reason: string, description: string},
		schedule_uuid: string,
		selected_event_status_uuid: string,
		patient_uuid: number,
		site_uuid: string
	)
	{
		var deferred = this.$q.defer();

		var dateString: string = this.util.get_date_string(start_datetime);
		var startTimeString: string = this.util.get_time_string(start_datetime, "HH:mm");
		var duration: number = moment.duration(end_datetime.diff(start_datetime)).asMinutes();

		var appointment: NewAppointmentTo1 =  {
			"appointmentDate": dateString,
			"startTime": startTimeString,
			"duration": duration,
			"status": selected_event_status_uuid,
			"demographicNo": patient_uuid,
			"notes": event_data.description,
			"reason": event_data.reason,
			"location": site_uuid,
			"providerNo": schedule_uuid,
			//"name": string,
/*			"resources": string,
			"type": string,
			"urgency": string,*/
		};

		//this.scheduleService.addAppointment(appointment).then(
		this.scheduleApi.addAppointment(appointment).then(
			function(result: IHttpResponse<SchedulingResponse>)
			{
				deferred.resolve(result.data);
			},
			function (result)
			{
				deferred.reject(result.data);
			});

		return deferred.promise;
	}

	// Read the implementation-specific results and return a calendar-compatible object.
	public process_save_results(results, display_errors)
	{
		let status = (results || {}).status;

		if(status == 'SUCCESS')
		{
			return true;
		}

		let error_message = ((results || {}).error || {}).message;
		let validation_error_array = ((results || {}).error || {}).validationErrors;

		if(Array.isArray(validation_error_array))
		{
			display_errors.add_standard_error(error_message);
			for(let error of validation_error_array)
			{
				display_errors.add_field_error(error.path, error.message);
			}
		}
	}

	/*
	public delete_event(event_uuid)
	{
		var deferred = this.$q.defer();

		this.event_model.del(event_uuid).then(function()
		{
			deferred.resolve();

		}, function()
		{
			deferred.reject();
		});

		return deferred.promise;
	}*/


	//-------------------------------------------------------------------------
	// Event Handlers
	//-------------------------------------------------------------------------/

/*	public drop_event(uuid, schedule_uuid, delta)
	{
		var deferred = this.$q.defer();

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
	}

	public resize_event(uuid, delta)
	{
		var deferred = this.$q.defer();

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
	}

	public rotate_event_status(uuid, rotate_statuses)
	{
		var deferred = this.$q.defer();

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
	}*/

	//-------------------------------------------------------------------------
	// Global State Manipulation
	//-------------------------------------------------------------------------/

	public get_global_preference_setting(key)
	{
		if(this.util.exists(this.globalStateService.preferences) &&
			this.util.exists(this.globalStateService.preferences.settings))
		{
			return this.globalStateService.preferences.settings[key];
		}

		return null;
	}

	public get_global_state(key)
	{
		var setting = this.globalStateService[key];
		if(!this.util.exists(setting))
		{
			setting = null;
		}
		return setting;
	}

	public save_global_state(key, value)
	{
		this.globalStateService[key] = value;
	}

	// -----------------------------------------------------------------------------------------------
	// Interface Actions
	// -----------------------------------------------------------------------------------------------

	public get_create_invoice_url(
		event_uuid, demographics_practitioner_uuid, demographics_patient_uuid)
	{
		var url = "#/invoice/create?schedule_event_uuid=" +
			encodeURIComponent(event_uuid);

		if(this.util.exists(demographics_patient_uuid))
		{
			url += "&demographics_patient_uuid=" +
				encodeURIComponent(demographics_patient_uuid);
		}

		if(this.util.exists(demographics_practitioner_uuid))
		{
			url += "&demographics_practitioner_uuid=" + encodeURIComponent(
				demographics_practitioner_uuid);
		}

		return url;
	}

	public get_patient_demographic_url(event)
	{
		return "#/patient/" + encodeURIComponent(event.data.demographics_patient_uuid) + "/view";
	}

	public get_create_chart_note_url(event)
	{
		return "#/patient/" + encodeURIComponent(event.data.demographics_patient_uuid) +
			"/chart_notes?event_uuid=" + encodeURIComponent(event.data.uuid);
	}

	public open_patient_dialog(edit_mode_callback, on_save_callback,
		load_error_link_patient_fn)
	{
		//global_state.enable_keyboard_shortcuts = false;

		return this.$uibModal.open({
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
	}

	public get_schedule_height()
	{
/*		// get the full window height, minus the header height, minus a buffer for schedule options
		var schedule_height = $(window).height() - $('#right-pane').offset().top - 80;
		if(schedule_height < $scope.min_height) {
			schedule_height = $scope.min_height;
		}
		return schedule_height;*/
		return 814;
	}


	// ===============================================================================================
	// Private Methods
	// ===============================================================================================
/*
	private create_schedule_event(event)
	{
		var event_status_color =
			this.event_statuses[event.event_status_uuid] ?
				this.event_statuses[event.event_status_uuid].color :
				this.default_event_color;

		return {
			data: event,
			start: this.util.get_datetime_moment(event.start_time),
			end: this.util.get_datetime_moment(event.end_time),
			color: event_status_color,
			className: this.event_class(event_status_color),
			resourceId: event.schedule_uuid
		}
	}

	private event_class(bg_color)
	{
		var red = parseInt(bg_color.substr(1,2),16);
		var green = parseInt(bg_color.substr(3,2),16);
		var blue = parseInt(bg_color.substr(5,2),16);
		var yiq = ((red * 299) + (green * 587) + (blue * 114)) / 1000;

		return (yiq >= 128) ? 'text-dark' : 'text-light';
	}
	*/

/*
	private load_schedule_availabilities(schedule)
	{
		var deferred = this.$q.defer();

		this.schedule_availability_model.schedule_list(schedule).then(
			function success(results)
			{
				schedule.availabilities = results.data;
				deferred.resolve(this.results_factory.factory(schedule));
			}, function error(errors)
			{
				console.log('load_schedule_availabilities error', errors);
			});

		return deferred.promise;
	}

	private load_schedule_tmpl_rels(schedule)
	{
		var deferred = $q.defer();

		this.schedule_template_relation_model.schedule_list(schedule).then(
			function success(results)
			{
				schedule.relations = results.data;
				deferred.resolve(this.results_factory.factory(schedule));
			});

		return deferred.promise;
	}*/
}
