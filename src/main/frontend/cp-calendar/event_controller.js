'use strict';


//=========================================================================
// Calendar Event Controller
//=========================================================================/

angular.module('cpCalendar').controller(
	'cpCalendar.EventController',

	[
		'$scope',
		'$q',
		'$timeout',
		'$state',
		'$uibModalInstance',

		'errorsService',
		'cpCalendar.Util',


		'access_control',
		'key_binding',
		'focus',
		'type', 'parent_scope', 'label', 'edit_mode', 'data',

	function (
		$scope, $q, $timeout, $state, $uibModalInstance,

		messages_factory, util,

		access_control,
		key_binding, focus,
		type, parent_scope, label, edit_mode, data
	)
{
	$scope.parent_scope = parent_scope;


	//=========================================================================
	// Access Control
	//=========================================================================/


	//=========================================================================
	// Local scope variables
	//=========================================================================/

	$scope.access_control= access_control;

	$scope.label = label;
	$scope.edit_mode = edit_mode;

	$scope.key_binding = key_binding;
	$scope.event_uuid = data.uuid;
	$scope.num_invoices = data.num_invoices;
	$scope.tag_names = data.tag_names;

	$scope.schedule = data.schedule;

	$scope.availability_types = data.availability_types;
	$scope.schedule_templates = data.schedule_templates;

	$scope.event_data = {
		start_time: null,
		end_time: null,
		reason: null,
		description: null
	};

	$scope.time_interval = data.time_interval;
	$scope.start_date = null;
	$scope.start_time = null;
	$scope.end_date = null;
	$scope.end_time = null;

	$scope.last_event_length = null;

	$scope.patient = parent_scope.calendar_api_adapter.patient_model;
	$scope.autocomplete_values = {};

	$scope.active_template_events = [];

	$scope.event_statuses = $scope.parent_scope.event_statuses;
	$scope.event_status_options = [];
	$scope.selected_event_status = null;
	$scope.default_event_status = null;

	$scope.selected_site_name = null;

	$scope.timepicker_format = "h:mm A";

	$scope.field_value_mapping = {
		start_date: 'Start Date',
		start_time: 'Start Time',
		end_date: 'End Date',
		end_time: 'End Time'
	};
	$scope.display_messages = messages_factory.factory();

	$scope.initialized = false;
	$scope.working = false;

	//=========================================================================
	// Init
	//=========================================================================/

	$scope.init = function init()
	{
		if(!access_control.has_permission('scheduling_create'))
		{
			$timeout(function()
			{
				$scope.cancel();
			});
		}

		var moment_start = moment(data.start_time);
		var moment_end = moment(data.end_time);

		$scope.start_time = util.get_time_string(moment_start, $scope.timepicker_format);
		$scope.end_time = util.get_time_string(moment_end, $scope.timepicker_format);
		$scope.start_date = util.get_date_string(moment_start);
		$scope.end_date = util.get_date_string(moment_end);

		console.log('start_time');
		console.log($scope.start_date);
		console.log($scope.start_time);

		$scope.last_event_length = moment_end.diff(moment_start, 'minutes');

		// maintain a list of the 'active' templates based on start time
		$scope.set_active_template_events();

		for(var key in $scope.event_statuses)
		{
			if($scope.event_statuses.hasOwnProperty(key))
			{
				$scope.event_status_options.push($scope.event_statuses[key]);
			}
		}
		$scope.default_event_status = data.default_event_status;
		$scope.set_selected_event_status(data.event_status_uuid);


		if(edit_mode)
		{
			$scope.event_data.reason = data.reason;
			$scope.event_data.description = data.description;

			// either load the patient data and init the autocomplete
			// or ensure the patient model is clear
			$scope.patient.uuid = data.demographics_patient_uuid;
			$scope.init_patient_autocomplete().then(function() {
				$scope.initialized = true;
			});
			$scope.selected_site_name = data.selected_site_name;
		}
		else
		{
			// create mode: adjust the end date (if needed)
			// and clear the patient model
			$scope.adjust_end_datetime();
			$scope.patient.clear();

			// autofocus the patient field
			focus.element("#input-patient");

			$scope.initialized = true;
		}
	};

	$scope.init_patient_autocomplete = function init_patient_autocomplete()
	{
		var deferred = $q.defer();

		if(util.exists($scope.patient.uuid) && $scope.patient.uuid != 0)
		{
			parent_scope.autocomplete.init_autocomplete_values(
				{ patient: $scope.patient.uuid },
				$scope.autocomplete_values).then(
				function(results)
				{
					$scope.autocomplete_values = results.data;
					$scope.patient.fill_data($scope.autocomplete_values.patient.data);
					deferred.resolve();
				},
				function(errors)
				{
					console.log('error initializing patient autocomplete', errors);
					$scope.patient.clear();
					deferred.resolve();
				});
		}
		else
		{
			$scope.patient.clear();
			deferred.resolve();
		}

		return deferred.promise;
	};

	//=========================================================================
	// Private methods
	//=========================================================================/

	$scope.get_inclusive_days = function get_inclusive_days(
		moment_start, moment_end)
	{
  		var days = [];
  		var moment_day = moment_start.clone().hour(0).minute(0);
  		while(moment_day.isBefore(moment_end))
  		{
  			days.push(moment_day.clone());
  			moment_day.add(1, 'day');
  		}
  		return days;
	};

	$scope.set_selected_event_status = function set_selected_event_status(uuid)
	{
		var event_status_uuid = $scope.default_event_status;
		if(util.exists(uuid))
		{
			event_status_uuid = uuid;
		}

		if(!util.exists(event_status_uuid) ||
			!util.exists($scope.event_statuses[event_status_uuid]))
		{
			// if not set or found just pick the first one
			event_status_uuid = $scope.event_status_options[0].uuid;
		}

		$scope.selected_event_status = $scope.event_statuses[event_status_uuid];
	};

	$scope.set_active_template_events = function set_active_template_events()
	{
		if(!($scope.schedule || {}).events)
		{
			return;
		}

		// Get templates that happen during the time period

		var moment_start = util.get_date_and_time_moment(
			$scope.start_date, $scope.formatted_time($scope.start_time));
		var moment_end = util.get_date_and_time_moment(
			$scope.end_date, $scope.formatted_time($scope.end_time));

		var active_events = [];

		for(var i = 0; i < $scope.schedule.events.length; i++)
		{
			var event = angular.copy($scope.schedule.events[i]);

			if(!event.availability_type)
			{
				continue;
			}

			// if start time is before event end time or if end time is after event start
			event.start = util.get_datetime_no_timezone_moment(event.start);
			event.end = util.get_datetime_no_timezone_moment(event.end);

			if(moment_start.isValid() && moment_end.isValid() &&
				event.start.isValid() && event.end.isValid() &&
				moment_start.isBefore(event.end) && moment_end.isAfter(event.start))
			{
				active_events.push(event);
			}
		}

		$scope.active_template_events = active_events;
	};

	$scope.adjust_end_datetime = function adjust_end_datetime(length_minutes)
	{
		// adjusts the end time to the specified length or
		// adjusts to keep the event length the same as it last was

		var moment_start = util.get_date_and_time_moment(
			$scope.start_date, $scope.formatted_time($scope.start_time));
		if(moment_start.isValid())
		{
			var new_event_length = util.exists(length_minutes) ?
				length_minutes : $scope.last_event_length;

			var moment_end = moment_start.add(new_event_length, 'minutes');

			$scope.end_date = util.get_date_string(moment_end);
			$scope.end_time = util.get_time_string(moment_end, $scope.timepicker_format);
		}
	};

	$scope.update_last_event_length = function update_last_event_length()
	{
		// saves the current event length, if the date/times are valid

		var moment_start = util.get_date_and_time_moment(
			$scope.start_date, $scope.formatted_time($scope.start_time));
		var moment_end = util.get_date_and_time_moment(
			$scope.end_date, $scope.formatted_time($scope.end_time));

		if(moment_start.isValid() && moment_end.isValid())
		{
			var event_length = moment_end.diff(moment_start, 'minutes');
			if(event_length > 0)
			{
			$scope.last_event_length = event_length;
			}
		}
	};

	$scope.validate_form = function validate_form()
	{
		$scope.display_messages.clear();

		util.validate_date_string($scope.start_date,
			$scope.display_messages, 'start_date', 'Start Time', true);

		util.validate_time_string($scope.formatted_time($scope.start_time),
			$scope.display_messages, 'start_time', 'Start Time', true);

		util.validate_date_string($scope.end_date,
			$scope.display_messages, 'end_date', 'End Time', true);

		util.validate_time_string($scope.formatted_time($scope.end_time),
			$scope.display_messages, 'end_time', 'End Time', true);

		// if all the date/time fields look good, validate range
		if(!$scope.display_messages.has_errors())
		{
			var start_datetime = util.get_date_and_time_moment(
				$scope.start_date, $scope.formatted_time($scope.start_time));
			var end_datetime = util.get_date_and_time_moment(
				$scope.end_date, $scope.formatted_time($scope.end_time));

			if(end_datetime.isSame(start_datetime) ||
				end_datetime.isBefore(start_datetime))
			{
				$scope.display_messages.add_standard_error("The appointment must end after it starts");
			}
		}

		return !$scope.display_messages.has_errors();
	};

	$scope.save_event = function save_event()
	{
		var deferred = $q.defer();

		var start_datetime = util.get_date_and_time_moment(
				$scope.start_date, $scope.formatted_time($scope.start_time));

		var end_datetime = util.get_date_and_time_moment(
				$scope.end_date, $scope.formatted_time($scope.end_time));

		parent_scope.calendar_api_adapter.save_event(
				edit_mode,
				$scope.event_uuid,
				start_datetime,
				end_datetime,
				$scope.event_data,
				$scope.schedule.uuid,
				$scope.selected_event_status.uuid,
				$scope.patient.uuid,
				$scope.selected_site_name
		).then(
				function(results)
				{
					if(parent_scope.calendar_api_adapter.process_save_results(results, $scope.display_messages))
					{
						deferred.resolve(results);
					}
					else
					{
						deferred.reject(results);
					}
				},
				function (results)
				{
					parent_scope.calendar_api_adapter.process_save_results(results, $scope.display_messages);
					deferred.reject();
				});

		return deferred.promise;
	};

	$scope.delete_event = function delete_event()
	{
		var deferred = $q.defer();

		parent_scope.calendar_api_adapter.delete_event($scope.event_uuid).then(function()
		{
			deferred.resolve();

		}, function()
		{
			deferred.reject();
		});

		return deferred.promise;
	};

	$scope.formatted_time = function formatted_time(time_str)
	{
		// the time picker format is HH:MM AM - need to strip spaces
		return time_str.replace(/ /g,'');
	};

	//=========================================================================
	// Watches
	//=========================================================================/

	// when the start date is changed,
	// update the active template events
	$scope.$watch('start_date', function(new_start_date, old_start_date)
	{
		// avoid running first time this fires during initialization
		if(new_start_date !== old_start_date)
		{
			$scope.set_active_template_events();
			$scope.adjust_end_datetime();
		}
	});

	// when the start time is changed,
	// update the active template events and adjust the end time
	$scope.$watch('start_time', function(new_start_time, old_start_time)
	{
		// avoid running first time this fires during initialization
		if(new_start_time !== old_start_time)
		{
			$scope.set_active_template_events();
			$scope.adjust_end_datetime();
		}
	});

	// when the end date is changed, track the event length
	$scope.$watch('end_date', function(new_end_date, old_end_date)
	{
		// avoid running first time this fires during initialization
		if(new_end_date !== old_end_date)
		{
			$scope.set_active_template_events();
			$scope.update_last_event_length();
		}
	});

	// when the end time is changed, track the event length
	$scope.$watch('end_time', function(new_end_time, old_end_time)
	{
		// avoid running first time this fires during initialization
		if(new_end_time !== old_end_time)
		{
			$scope.set_active_template_events();
			$scope.update_last_event_length();
		}
	});

	//=========================================================================
	// Public methods
	//=========================================================================/

	$scope.loaded_new_photo = function loaded_new_photo(file, event)
	{
		if(file == null)
		{
			return;
		}
		$scope.preview_patient_image = file;
		$scope.new_photo = true;
		$scope.patient.has_photo = true;
		$scope.patient.upload_photo(file);
	};

	$scope.is_working = function()
	{
		return $scope.working;
	};

	$scope.is_initialized = function()
	{
		return $scope.initialized;
	};

	$scope.is_patient_selected = function()
	{
		return util.exists($scope.patient.uuid);
	};

	$scope.clear_patient = function()
	{
		$scope.autocomplete_values.patient = null;
		$scope.patient.clear();
	};

	$scope.on_select_patient = function on_select_patient($item, $model, $label, $event)
	{
		// $item is a Patient pojo
		$scope.patient.uuid = $item.data[$item.value_field];
		$scope.patient.fill_data($item.data);
	};

	$scope.set_event_length = function set_event_length(minutes)
	{
		$scope.adjust_end_datetime(minutes);
	};

	$scope.save = function save()
	{
		if(!$scope.validate_form())
		{
				return false;
		}

		$scope.working = true;
		$scope.save_event().then(function()
		{
			$scope.parent_scope.refetch_events();
			$uibModalInstance.close();
			$scope.working = false;
		}, function()
		{
			if(!$scope.display_messages.has_standard_errors())
			{
				$scope.display_messages.add_generic_fatal_error();
			}
			$scope.working = false;
		});
	};

	$scope.delete = function()
	{
		$scope.working = true;
		$scope.delete_event().then(function()
		{
			$scope.parent_scope.refetch_events();
			$uibModalInstance.close();
			$scope.working = false;
		}, function()
		{
			$scope.display_messages.add_generic_fatal_error();
			$scope.working = false;
		});
	};

	$scope.cancel = function()
	{
		$uibModalInstance.dismiss('cancel');
	};

	$scope.save_and_bill = function()
	{
		if(!$scope.validate_form())
		{
	  		return false;
		}

		$scope.working = true;
		$scope.save_event().then(function()
		{
			$scope.parent_scope.refetch_events();
			$uibModalInstance.close();
			$scope.working = false;
			$scope.parent_scope.open_create_invoice(
				$scope.event_uuid,
				$scope.schedule.uuid,
				$scope.patient.uuid);
		}, function()
		{
			$scope.display_messages.add_generic_fatal_error();
			$scope.working = false;
		});
	};

	$scope.view_invoices = function()
	{
		$scope.parent_scope.open_view_invoices($scope.event_uuid);
	};

	$scope.create_patient = function create_patient()
	{
		var edit_mode_callback = function() { return false; };
		var on_save_callback = function() { return $scope.on_patient_modal_save; };
		var load_error_link_patient_fn = function() { return $scope.on_patient_modal_save; };

		$scope.create_patient_dialog = parent_scope.calendar_api_adapter.open_patient_dialog(
				edit_mode_callback, on_save_callback, load_error_link_patient_fn);
	};

	$scope.modify_patient = function modify_patient()
	{
		if(!$scope.patient.uuid)
		{
			return;
		}

		var edit_mode_callback = function() { return true; };
		var on_save_callback = function() { return $scope.on_patient_modal_save; };
		var load_error_link_patient_fn = function() { return $scope.on_patient_modal_save; };

		$scope.modify_patient_dialog = parent_scope.calendar_api_adapter.open_patient_dialog(
				edit_mode_callback, on_save_callback, load_error_link_patient_fn);
	};

	// for callback on create/edit patient modal
	$scope.on_patient_modal_save = function on_patient_modal_save(uuid)
	{
		// load the newly created/updated patient
		$scope.patient.uuid = uuid;
		$scope.init_patient_autocomplete();
	};

	//=========================================================================
	//  Key Bindings
	//=========================================================================

	$scope.key_bind_settings =
	{
		"ctrl+enter": {
			title: 'Ctrl+Enter',
			tooltip: 'Save',
			description: 'Save appointment',
			callback_fn: function enter_callback()
			{
				if(!$scope.is_working())
				{
					$scope.save();
				}
			},
			target: null
		},
		"ctrl+shift+enter": {
			title: 'Ctrl+Shift+Enter',
			tooltip: 'Save And Bill',
			description: 'Save and bill for appointment',
			callback_fn: function enter_callback()
			{
				if(!$scope.is_working())
				{
					$scope.save_and_bill();
				}
			},
			target: null
		}
	};
	$scope.key_binding.bind_key_global("ctrl+enter", $scope.key_bind_settings["ctrl+enter"]);
	$scope.key_binding.bind_key_global("ctrl+shift+enter", $scope.key_bind_settings["ctrl+shift+enter"]);
}]);
