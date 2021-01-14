angular.module('cpCalendar')


//=========================================================================
// Main Calendar Controller
//=========================================================================/

.controller('cpCalendar.Controller', [
	'$scope',
	'$q',
	'$stateParams',
	'$uibModal',
	'$window',
	'cpCalendar.Util',
	'uiCalendarConfig',

function(
		$scope,
		$q,
		$stateParams,
		$uibModal,
		$window,
		util,
		uiCalendarConfig)
{

	//$scope.global_state = global_state;

	$scope.availability_type_model = null;

	$scope.calendar_name = 'cpCalendar';
	$scope.event_sources = [{}];

	$scope.min_height = 650;

	$scope.default_calendar_view = 'agendaWeek';
	$scope.default_time_interval = '00:15:00';
	$scope.default_event_color = "#333";
	$scope.default_auto_refresh_minutes = 3;

	$scope.time_interval_options =
			['00:05:00','00:10:00','00:15:00','00:30:00'];
	$scope.selected_time_interval = $scope.default_time_interval;

	$scope.default_date = null;

	if($stateParams.default_date)
	{
		var default_moment =
				util.get_date_moment($stateParams.default_date);
		if( default_moment.isValid() )
		{
			$scope.default_date =
					util.get_date_moment($stateParams.default_date);
		}
	}

	$scope.selected_schedule = null;
	$scope.selected_site_name = null;
	$scope.schedule_options = [];
	$scope.resource_options = [];
	$scope.resource_option_hash = {};
	$scope.selected_resources = [];

	$scope.event_statuses = {};
	$scope.sites = {};
	$scope.site_options = [];
	$scope.rotate_statuses = [];
	$scope.availability_types = {};
	$scope.schedule_templates = {};

	$scope.schedules = [];
	$scope.events = [];

	$scope.calendar_loading = false;
	$scope.initialized = false;

	$scope.opening_dialog = false;
	$scope.dialog = null;


	$scope.calendar = function calendar()
	{
		return uiCalendarConfig.calendars[$scope.calendar_name];
	};

	$scope.is_initialized = function()
	{
		return $scope.initialized;
	};

	$scope.show_schedule_select = function()
	{
		return $scope.view_name() !== 'resourceDay';
	};

	$scope.show_time_intervals = function()
	{
		return $scope.view_name() !== 'month';
	};




	//=========================================================================
	// Init
	//=========================================================================/

	$scope.init = function init()
	{
		$scope.default_date = $scope.global_state.selected_date;
		$scope.ui_config.calendar.schedulerlicensekey = "0752822575-fcs-1459294728";
				//$scope.global_state.global_settings.interface_preferences.scheduler_license_key,
		$scope.ui_config.calendar.defaultView = $scope.calendar_view_name();

		$scope.load_schedule_templates().then(function()
		{
			$scope.load_availability_types().then(function()
			{
				$scope.load_event_statuses().then(function()
				{
					$scope.load_schedule_options().then(function()
					{
						$scope.load_site_options().then(function()
						{
							$scope.load_default_selections();

							$scope.load_selected_schedules().then(function()
							{
								$scope.set_calendar_resources();

								$scope.set_event_sources();

								$scope.init_events_auto_refresh();

								$scope.apply_ui_config($scope.ui_config);
								console.log("-- Calendar Initialized ----------------------------");
								$scope.initialized = true;
							});
						});
					});
				});
			});
		});
	};

	$scope.apply_ui_config = function apply_ui_config(ui_config)
	{
		$scope.ui_config_applied = angular.copy(ui_config);
	};

	$scope.init_events_auto_refresh = function init_events_auto_refresh()
	{
		var deferred = $q.defer();

		// if there is already a refresh set up, stop it
		var refresh = $scope.get_global_state('schedule_auto_refresh');
		if(refresh !== null)
		{
			clearInterval(refresh);
		}

		// get the refresh interval from preferences, or use default
		var minutes = $scope.get_global_preference_setting(
				'schedule_auto_refresh_minutes');
		if(!util.exists(minutes) || !util.is_integer_string(minutes))
		{
			minutes = $scope.default_auto_refresh_minutes;
		}
		else
		{
			minutes = parseInt(minutes);
		}

		if(minutes > 0)
		{
			// start the auto refresh and save its ID to global state
			$scope.save_global_state('schedule_auto_refresh',
					setInterval($scope.refetch_events, minutes * 60 * 1000));
		}

		deferred.resolve();

		return deferred.promise;
	};


	//=========================================================================
	// Global State Manipulation
	//=========================================================================/

	$scope.get_global_preference_setting = function get_global_preference_setting(key)
	{
		if(util.exists($scope.global_state.preferences) &&
				util.exists($scope.global_state.preferences.settings))
		{
			return $scope.global_state.preferences.settings[key];
		}

		return null;
	};

	$scope.get_global_state = function get_global_setting(key)
	{
		var setting = $scope.global_state[key];
		if(!util.exists(setting))
		{
			setting = null;
		}
		return setting;
	};

	$scope.save_global_state = function save_global_setting(key, value)
	{
		$scope.global_state[key] = value;
	};


	//=========================================================================
	// Event Handlers
	//=========================================================================/

	$scope.on_schedule_changed = function on_schedule_changed(selected_schedule, selected_site_name)
	{
		if(!util.exists(selected_schedule))
		{
			return;
		}

		var schedule_uuid = selected_schedule.uuid;

		$scope.save_global_state('schedule_default', schedule_uuid);
		$scope.selected_schedule = $scope.calendar_api_adapter.get_selected_schedule(
				$scope.schedule_options);

		if(util.exists(selected_site_name))
		{
			$scope.selected_site_name = selected_site_name;
		}
		else
		{
			$scope.selected_site_name = null;
		}

		// reload the schedule and then events data, triggering a rerender
		$scope.load_selected_schedules().then($scope.refetch_events);
	};

	$scope.on_time_interval_changed = function on_time_interval_changed(selected_time_interval)
	{
		$scope.selected_time_interval = selected_time_interval;

		$scope.save_global_state(
				'schedule_time_interval', $scope.selected_time_interval);

		// updating the config will automatically trigger an events refresh
		$scope.ui_config.calendar.slotDuration = $scope.selected_time_interval;
		$scope.ui_config.calendar.slotLabelInterval = $scope.selected_time_interval;

		$scope.apply_ui_config($scope.ui_config);
	};

	$scope.on_resources_changed = function(selected_resource_uuids)
	{
		$scope.save_global_state('schedule_resources', selected_resource_uuids);

		var updated_resources = [];
		for(var i = 0; i < $scope.resource_options.length; i++)
		{
			var schedule = $scope.resource_options[i];
			if(selected_resource_uuids.indexOf(schedule.uuid) > -1)
			{
				schedule.name = schedule.display_name;
				updated_resources.push(schedule);
			}
		}
		$scope.selected_resources = updated_resources;

		// reload the schedules and then update the calendar config,
		// triggering a refetch events and rerender
		$scope.load_selected_schedules().then(function()
		{
			$scope.set_calendar_resources();
		});
	};

	$scope.on_view_render = function on_view_render()
	{
		if($scope.is_initialized() && $scope.calendar())
		{
			$scope.global_state.selected_date = moment(util.get_date_string(
					moment($scope.calendar().fullCalendar('getDate'))));
		}

		// Voodoo to set the resource view column width from https://stackoverflow.com/a/39297864
		$("#ca-calendar").css('min-width',$('.fc-resource-cell').length*125);
	};

	$scope.after_render = function after_render()
	{
		// Voodoo to set the resource view column width from https://stackoverflow.com/a/39297864
		$('.fc-agendaDay-button').click(function(){$("#schedule_container").css('min-width',$('.fc-resource-cell').length*125);});
	};

	$scope.on_event_clicked = function on_event_clicked(calEvent, jsEvent, view)
	{
		if($(jsEvent.target).is(".event-status.rotate"))
		{
			$scope.rotate_event_status(calEvent);
		}
		else if($(jsEvent.target).is(".event-invoice.edit"))
		{
			$scope.open_view_invoices(calEvent.data.uuid);
		}
		else if($(jsEvent.target).is(".event-invoice"))
		{
			$scope.open_create_invoice(
					calEvent.data.uuid,
					calEvent.data.schedule_uuid,
					calEvent.data.demographics_patient_uuid);
		}
		else if($(jsEvent.target).is(".event-demographic") ||
				$(jsEvent.target).parent().is(".event-demographic"))
		{
			$scope.open_patient_demographic(calEvent);
		}
		else if($(jsEvent.target).is(".event-note") ||
				$(jsEvent.target).parent().is(".event-note"))
		{
			$scope.open_create_chart_note(calEvent);
		}
		else
		{
			$scope.open_edit_event_dialog(calEvent);
		}
	};

	$scope.on_event_drop = function on_event_drop(
			calEvent, delta, revertFunc, jsEvent, ui, view)
	{
		if(!$scope.access_control.has_permission('scheduling_create') )
		{
			revertFunc();
			return;
		}

		// event was dragged and dropped on the calendar:
		// load then update the start and end time based on the delta
		$scope.set_calendar_loading(true);

		$scope.calendar_api_adapter.drop_event(calEvent.data.uuid, calEvent.resourceId, delta).then(
			function success(event_data)
			{
				calEvent.data.start_time = event_data.start_time;
				calEvent.data.end_time = event_data.end_time;
				calEvent.data.schedule_uuid = event_data.schedule_uuid;

				$scope.update_event(calEvent);

				$scope.set_calendar_loading(false);

			}, function error(errors)
			{
				console.log('failed to save event', errors);

				// revert on fail
				revertFunc();
				$scope.set_calendar_loading(false);
			});
	};

	$scope.on_event_resize = function on_event_resize(
			calEvent, delta, revertFunc, jsEvent, ui, view)
	{
		if(!$scope.access_control.has_permission('scheduling_create') )
		{
			revertFunc();
			return;
		}

		// event was extended by dragging the end of the event on the calendar:
		// load then update the end time based on the delta
		$scope.set_calendar_loading(true);

		$scope.calendar_api_adapter.resize_event(calEvent.data.uuid, delta).then(
				function success(event_data)
				{
					calEvent.data.end_time = event_data.end_time;

					$scope.update_event(calEvent);

					$scope.set_calendar_loading(false);

				}, function error(errors)
				{
					console.log('failed to resize event', errors);

					// revert on fail
					revertFunc();
					$scope.set_calendar_loading(false);
				});
	};

	$scope.get_icon_path = function get_icon_path(icon, status_modifier)
	{
		if(!util.exists(icon))
		{
			return "";
		}

		var modifier_string = "";

		if(util.exists(status_modifier))
		{
			modifier_string = status_modifier;
		}

		return "../images/" + modifier_string + icon;
	};

	$scope.on_event_render = function(event, element, view)
	{
		if(event.rendering !== 'background')
		{
			var event_site_html = '';
			var event_site = $scope.sites[event.data.site];

			if(util.exists(event_site))
			{
				event_site_html += "<span style='background-color: " + event_site.color + "'>&nbsp;</span>"
			}


			var event_status_html = '';
			var event_status =
					$scope.event_statuses[event.data.event_status_uuid];

			if(util.exists(event_status) && util.exists(event_status.icon) &&
				util.exists(event) && util.exists(event.data))
			{
				event_status_html += "<img src='" + $scope.get_icon_path(event_status.icon, event.data.event_status_modifier) + "' />";
			}
			else
			{
				event_status_html = '<span class="event-status';
				if(util.exists(event_status))
				{
					var event_status_rotate = util.exists(event_status.sort_order);
					if(event_status_rotate)
					{
						event_status_html += ' rotate ';
					}
					event_status_html += '"' + ' title="' + util.escape_html(event_status.name) + '">' +
							util.escape_html(event_status.display_letter) + '</span>';
				}
				else
				{
					event_status_html += '" title="Unknown">?</span>';
				}
			}

			var event_invoice_html = '<span class="event-invoice';
			if(event.data.num_invoices > 0)
			{
				event_invoice_html += ' edit" title="View Invoice' +
						(event.data.num_invoices > 1 ? "s" : "") + '">B</span>';
			}
			else
			{
				event_invoice_html += '" title="Create Invoice">$</span>';
			}

			var event_details = "";
			if(util.exists(event.data.demographics_patient_name))
			{
				event_details = util.escape_html(event.data.demographics_patient_name);
				if(util.exists(event.data.reason))
				{
					event_details += " (" + util.escape_html(event.data.reason) + ")"
				}
			}
			else if(util.exists(event.data.reason))
			{
				event_details = util.escape_html(event.data.reason);
			}

			var event_details_html = "<span class='event-details' title='" + event_details + "'>" +
					event_details + "</span>";

			var event_demographic_html = "";
			if($scope.access_control.has_one_of_permissions(['patient_view', 'patient_manage']) &&
					util.exists(event.data.demographics_patient_uuid))
			{
				event_demographic_html =
						'<span class="event-demographic" title="View Patient">' +
						'<i class="fa fa-user"></i></span>';
			}

			var event_note_html = "";
			if($scope.access_control.has_one_of_permissions(['chart_note_view', 'chart_note_manage']) &&
					util.exists(event.data.demographics_patient_uuid))
			{
				event_note_html =
						'<span class="event-note" title="Add Patient Note">' +
						'<i class="fa fa-file-text-o"></i></span>';
			}

			var event_tags_html = '';
			var tag_class = element.hasClass('text-light') ? 'icon-white' : '';

			if(util.exists(event.data.tag_names))
			{
				event_tags_html = '<span class="event-tags" title="' +
						event.data.tag_names.join(", ") +
						'"><i class="icon ' + tag_class + ' icon-tags"/></span>';
			}

			$(element).find('.fc-content').html(event_site_html + event_status_html + event_invoice_html +
					event_demographic_html + event_note_html + event_tags_html + event_details_html);
		}
	};


	//=========================================================================
	// Control Methods
	//=========================================================================/

	$scope.change_view = function(view)
	{
		// if switching to or from resourceDay view, need to update schedules
		var reload_schedules = false;
		if(view === 'resourceDay' ||
				$scope.get_global_state('schedule_view_name') === 'resourceDay')
		{
			reload_schedules = true;
		}

		// save the new view to global state so it gets picked up in rendering
		$scope.save_global_state('schedule_view_name', view);

		if(reload_schedules)
		{
			$scope.load_selected_schedules().then(
					function success()
					{
						$scope.set_calendar_resources();
						$scope.update_calendar_view();
					});
		}
		else
		{
			$scope.update_calendar_view();
		}
	};

	$scope.select_resources = function()
	{
		// if already opening a dialog or have one open, ignore and return
		if($scope.opening_dialog || $scope.dialog)
		{
			return;
		}
		$scope.opening_dialog = true;

		var preselections = [];
		for(var i = 0; i < $scope.selected_resources.length; i++)
		{
			preselections.push($scope.selected_resources[i].uuid);
		}

		$scope.dialog = $uibModal.open({
			animation: false,
			backdrop: 'static',
			controller: 'Schedule.SelectResourcesController',
			templateUrl: 'code/schedule/select_resources.html',
			resolve: {
				resource_options: function() { return $scope.resource_options; },
				selected_resources: function() { return preselections; },
				on_update_callback: function() { return $scope.on_resources_changed; }
			}
		});

		// when the dialog closes clear the variable
		$scope.dialog.closed.then(function() {
			$scope.dialog = null;
		});

		$scope.opening_dialog = false;
	};

	$scope.show_legend = function()
	{
		// if already opening a dialog or have one open, ignore and return
		if($scope.opening_dialog || $scope.dialog)
		{
			return;
		}
		$scope.opening_dialog = true;

		var data = {
			event_statuses: $scope.event_statuses,
			availability_types: $scope.availability_types
		};

		$scope.dialog = $uibModal.open({
			animation: false,
			backdrop: 'static',
			controller: 'Schedule.LegendController',
			templateUrl: 'code/schedule/legend.html',
			resolve: {
				data: function() { return data }
			}
		});

		// when the dialog closes clear the variable
		$scope.dialog.closed.then(function() {
			$scope.dialog = null;
		});

		$scope.opening_dialog = false;
	};

	$scope.refetch_events = function()
	{
		$scope.calendar().fullCalendar('refetchEvents');
	};

	$scope.update_event = function update_event(cal_event)
	{
		$scope.calendar().fullCalendar('updateEvent', cal_event);
	};

	$scope.update_calendar_view = function update_calendar_view()
	{
		$scope.ui_config.calendar.defaultView = $scope.calendar_view_name();
		$scope.apply_ui_config($scope.ui_config);
	};

	$scope.open_create_event_dialog = function open_create_event_dialog(
			start, end, jsEvent, view, resource)
	{
		if(!$scope.access_control.has_permission('scheduling_create') )
		{
			return;
		}

		// if already opening a dialog or have one open, ignore and return
		if($scope.opening_dialog || $scope.dialog)
		{
			return;
		}
		$scope.opening_dialog = true;

		var schedule_uuid = null;
		var display_name = "";
		if(util.exists(resource))
		{
			schedule_uuid = resource.id;
			display_name = resource.display_name;
		}
		else if($scope.selected_schedule !== null)
		{
			schedule_uuid = $scope.selected_schedule.uuid;
		}

		var schedule = $scope.get_loaded_schedule(schedule_uuid);
		if(schedule !== null)
		{
			var default_event_status = schedule.new_event_status_uuid;

			var modal_schedule = angular.copy(schedule);
			modal_schedule.display_name = display_name;

			var data = {
				schedule: modal_schedule,
				default_event_status: default_event_status,
				start_time: start,
				end_time: end,
				time_interval: $scope.time_interval_minutes(),
				schedule_templates: $scope.schedule_templates,
				availability_types: $scope.availability_types,
				sites: $scope.sites
			};

			$scope.dialog = $uibModal.open({
				animation: false,
				backdrop: 'static',
				controller: 'cpCalendar.EventController',
				templateUrl: 'src/schedule/event.jsp',
				resolve: {
					type: [function() { return 'create_edit_event' }],
					label: [function() { return 'Appointment' }],
					parent_scope: [function() { return $scope }],
					data: [function() { return data }],
					edit_mode: [function() { return false }],
					access_control: [function() {return $scope.access_control}],
					key_binding: [function() {return {bindKeyGlobal: function(){}}}],
					focus: [function() {return $scope.calendar_api_adapter.focus}],
				}
			});

			$scope.dialog.result.catch(function(res) {
				if(!(res === 'cancel' || res === 'escape key press'))
				{
					throw res;
				}
			});

			// when the dialog closes clear the variable
			$scope.dialog.closed.then(function() {
				$scope.dialog = null;
			});
		}

		$scope.opening_dialog = false;
	};

	$scope.open_edit_event_dialog = function open_edit_event_dialog(calEvent)
	{
		if(!$scope.access_control.has_permission('scheduling_create') )
		{
			return;
		}

		// if already opening a dialog or have one open, ignore and return
		if($scope.opening_dialog || $scope.dialog)
		{
			return;
		}
		$scope.opening_dialog = true;

		var data = angular.extend({}, calEvent.data);

		data.start_time = calEvent.start;
		data.end_time = calEvent.end;
		data.num_invoices = calEvent.data.num_invoices;

		data.schedule =
				$scope.get_loaded_schedule(calEvent.data.schedule_uuid);

		data.time_interval = $scope.time_interval_minutes();
		data.selected_site_name = calEvent.data.site;

		data.schedule_templates = $scope.schedule_templates;
		data.availability_types = $scope.availability_types;

		$scope.dialog = $uibModal.open({
			animation: false,
			backdrop: 'static',
			controller: 'cpCalendar.EventController',
			templateUrl: 'src/schedule/event.jsp',
			resolve: {
				type: function() { return 'create_edit_event' },
				label: function() { return 'Appointment' },
				parent_scope: function() { return $scope },
				data: function() { return data },
				edit_mode: function() { return true },
				access_control: function() {return $scope.access_control},
				key_binding: function() {return {bind_key_global: function(){}}},
				focus: function() {return $scope.calendar_api_adapter.focus},
			}
		});

		$scope.dialog.result.catch(function(res) {
			if(!(res === 'cancel' || res === 'escape key press'))
			{
				throw res;
			}
		});

		// when the dialog closes clear the variable
		$scope.dialog.closed.then(function() {
			$scope.dialog = null;
		});

		$scope.opening_dialog = false;
	};

	$scope.rotateEventStatus = function rotateEventStatus(calEvent)
	{
		if(!$scope.access_control.has_permission('scheduling_create') )
		{
			return;
		}

		$scope.setCalendarLoading(true);

		$scope.calendar_api_adapter.rotate_event_status(calEvent.data.uuid, $scope.rotate_statuses).then(
				function success(event_data)
				{

					var event_status_uuid = event_data.event_status_uuid;
					var event_status_color =
							$scope.event_statuses[event_status_uuid] ?
									$scope.event_statuses[event_status_uuid].color :
									$scope.default_event_color;

					calEvent.data.event_status_uuid = event_status_uuid;
					calEvent.color = event_status_color;
					// This is being set to an array because of a bug:
					// https://github.com/fullcalendar/fullcalendar/issues/4011
					calEvent.className = [$scope.calendar_api_adapter.event_class(event_status_color)];

					$scope.update_event(calEvent);

					$scope.set_calendar_loading(false);
				});


		eventModel.load(uuid).then(
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
	};

	$scope.rotateEventStatus = function rotateEventStatus(uuid, rotate_statuses)
	{
		var deferred = this.$q.defer();

		eventModel.load(uuid).then(
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

	$scope.open_create_invoice = function open_create_invoice(
			event_uuid, schedule_uuid, demographics_patient_uuid)
	{
		var schedule =
				$scope.get_loaded_schedule(schedule_uuid);

		var url = $scope.calendar_api_adapter.get_create_invoice_url(event_uuid,
				schedule.demographics_practitioner_uuid, demographics_patient_uuid);

		window.window_scope = $scope;
		$window.open(url, '_blank');
	};

	$scope.open_patient_demographic = function open_patient_demographic(event)
	{
		var url = $scope.calendar_api_adapter.get_patient_demographic_url(event);
		$window.open(url, '_blank');
	};

	$scope.open_create_chart_note = function open_create_chart_note(event)
	{
		var url = $scope.calendar_api_adapter.get_create_chart_note_url(event);
		$window.open(url, '_blank');
	};


	//=========================================================================
	// Get information
	//=========================================================================/

	$scope.get_schedule_height = function get_schedule_height()
	{
		if ($scope.calendar_api_adapter)
		{
			$scope.calendar_api_adapter.get_schedule_height();
		}
	};

	// This gets the view name, but if it's resourceDay, it will get agendaDay.
	// TODO-legacy Not sure why this works this way.  Maybe it uses it to get a day of events for each
	//      resource in the resource list?
	$scope.calendar_view_name = function calendar_view_name()
	{
		var view = $scope.view_name();
		if(view == 'resourceDay')
		{
			view = 'agendaDay';
		}
		return view;
	};

	$scope.set_calendar_loading = function set_calendar_loading(is_loading)
	{
		$scope.calendar_loading = is_loading;
	};

	$scope.get_loaded_schedule = function get_loaded_schedule(uuid)
	{
		var schedule = null;
		for(var i = 0; i < $scope.schedules.length; i++)
		{
			if($scope.schedules[i].uuid === uuid)
			{
				schedule = $scope.schedules[i];
				break;
			}
		}
		return schedule;
	};


	//=========================================================================
	// Get information and maybe do something
	//=========================================================================/

	$scope.view_name = function view_name()
	{
		var view_name = $scope.get_global_state('schedule_view_name');

		if(!util.exists(view_name))
		{
			view_name = $scope.get_global_preference_setting(
					'schedule_view_name');
		}

		if(!util.exists(view_name))
		{
			view_name = $scope.default_calendar_view;
		}

		$scope.save_global_state('schedule_view_name', view_name);

		return view_name;
	};

	$scope.calendar_events = function(start, end, timezone, callback)
	{
		console.log("-- Load Calendar Events ------------------------------------------");
		console.log(start.toString());
		console.log(end.toString());
		console.log("------------------------------------------------------------------");

		// load the events for each of the loaded schedules
		var promise_array = [];
		for(var i = 0; i < $scope.schedules.length; i++)
		{
			promise_array.push(
					$scope.calendar_api_adapter.load_schedule_events(
							$scope.schedules[i], $scope.selected_site_name, start, end, $scope.view_name(), $scope.schedule_templates,
							$scope.event_statuses, $scope.default_event_color, $scope.availability_types));
		}

		// once all the events are loaded, concat them together and callback
		$q.all(promise_array).then(
			function success(results_array)
			{
				var schedule_events = [];
				for(var i = 0; i < results_array.length; i++)
				{
					// Pull the relations out of the result
					var schedule = $scope.schedules[i];
					var events = results_array[i];

					if(events && angular.isArray(events.data))
					{
						console.log('-- schedule templates ---------------------');
						schedule.events = events.data;
						//$scope.extract_data_from_events(events.data, schedule, $scope.schedule_templates);
					}

					// Display the result
					schedule_events.push(results_array[i].data);
				}

				$scope.events = Array.prototype.concat.apply([], schedule_events);

				try
				{
					callback($scope.events);
				}
				catch(err)
				{
					// the callback throws an error on first load, ignore
				}
			});
	};

	// Get background events and populate schedule.releations and schedule_templates.
	$scope.extract_data_from_events = function extract_data_from_events(
		event_array, schedule, schedule_templates)
	{
		var relation_array = [];

		for(var i = 0; i < event_array.length; i++)
		{
			var event = event_array[i];

			if(event.availability_type)
			{
				schedule.relations.push({
					schedule_template_uuid: event.schedule_template_code,
					start_date: event.start,
					end_date: event.end,
					schedule_uuid: null
				});
				schedule_templates[event.schedule_template_code] = {

				};
			}
		}
	};



	// ===============================================================================================
	// Data retrieval methods
	// ===============================================================================================

	$scope.load_schedule_templates = function load_schedule_templates()
	{
		var deferred = $q.defer();

		$scope.calendar_api_adapter.load_schedule_templates().then(function success(results)
		{
			$scope.schedule_templates = results;
			deferred.resolve($scope.schedule_templates);
		});

		return deferred.promise;
	};

	// Loads the list of availability types.  Sets the following:
	// $scope.availability_types - hash of availability types with uuid as key.
	$scope.load_availability_types = function load_availability_types()
	{
		var deferred = $q.defer();

		$scope.calendar_api_adapter.load_availability_types().then(function success(results)
		{
			$scope.availability_types = results;
			deferred.resolve($scope.availability_types);
		});

		return deferred.promise;
	};

	// Loads the list of event statuses from the API (i.e. appointment statuses).  Sets the following:
	// $scope.event_statuses - a table to look up a status by uuid.
	// $scope.rotate_statuses - an array to describe how to cycle through statuses.
	$scope.load_event_statuses = function load_event_statuses()
	{
		var deferred = $q.defer();

		$scope.event_statuses = {};
		$scope.rotate_statuses = [];
		$scope.calendar_api_adapter.load_event_statuses().then(
			function success(results)
			{
				for(var i = 0; i < results.length; i++)
				{
					var result = results[i];
					$scope.event_statuses[result.uuid] = result;
					if(result.rotates)
					{
						$scope.rotate_statuses.push(result);
					}
				}

				deferred.resolve(results);
			});

		return deferred.promise;
	};

	// Load the list of available sites from the API.  Sets the following:
	// $scope.sites - a table to lookup a site's info by name
	// $scope.site_options - the options for the site selection dropdown
	$scope.load_site_options = function load_site_options()
	{
		var deferred = $q.defer();

		$scope.calendar_api_adapter.load_sites().then(
			function success(results)
			{
				$scope.sites = {};
				$scope.site_options = [];
				if(angular.isArray(results) && results.length > 0)
				{
					// Fill up lookup table
					for(var i = 0; i < results.length; i++)
					{
						$scope.sites[results[i].name] = results[i];
					}

					// Create the dropdown options
					$scope.site_options = [
						{
							uuid: null,
							name: null,
							display_name: "All",
						}
					];

					$scope.site_options = $scope.site_options.concat(results);
				}

				deferred.resolve(results);
			});

		return deferred.promise;
	};

	// Loads the schedule dropdown options from the API.  Sets the following:
	// $scope.schedule_options - the array used to build the schedule selection dropdown.
	// $scope.resource_option_hash - table to look up schedule information by providerNo.  This is
	//                               used to create the resource view headers.
	$scope.load_schedule_options = function load_schedule_options()
	{
		var deferred = $q.defer();

		$scope.calendar_api_adapter.load_schedule_options().then(
				function success(results)
				{
					for(var i = 0; i < results.length; i++)
					{
						var schedule_data = results[i];

						$scope.schedule_options.push(schedule_data);

						// Get the possible resources by inferring that the group is a provider
						// by checking if the array has one entry and matches the identifier
						// Also uses fields specific to Juno.
						// TODO-legacy: CHANGE THIS!!
						if(
							angular.isArray(schedule_data.providerNos) &&
							schedule_data.providerNos.length == 1 &&
							schedule_data.providerNos[0].toString() == schedule_data.identifier
						)
						{
							var providerNo = schedule_data.providerNos[0];

							$scope.resource_option_hash[providerNo] = {
								'id': providerNo,
								'uuid': providerNo,
								'name': providerNo,
								'title': schedule_data.name,
								'display_name': schedule_data.name
							};
						}
					}
					deferred.resolve(results);
				});

		return deferred.promise;
	};

	// TODO-legacy: change this, perhaps?  It is getting the resource details from the groups
	$scope.build_selected_resources = function build_selected_resources(providerNos)
	{
		var selected_resources = [];

		for(var i = 0; i < providerNos.length; i++)
		{
			selected_resources.push($scope.resource_option_hash[providerNos[i]]);
		}

		return selected_resources;
	};

	$scope.load_selected_schedules = function load_selected_schedules()
	{
		var deferred = $q.defer();

		var promise_array = [];

		if(
			util.exists($scope.selected_schedule.providerNos) &&
			angular.isArray($scope.selected_schedule.providerNos)
		)
		{
			// TODO-legacy: this is really gross and I don't like it
			// Potentially put this in the calendarApiAdapter
			angular.forEach($scope.selected_schedule.providerNos, function(providerNo)
			{
				promise_array.push($scope.calendar_api_adapter.load_schedule(providerNo.toString()));
			});

			if($scope.selected_schedule.providerNos.length > 1)
			{
				// Set the calendar to resource mode.  All of these values need to be set.
				$scope.selected_resources = $scope.build_selected_resources($scope.selected_schedule.providerNos);
				$scope.ui_config.calendar.resources = $scope.selected_resources;
				$scope.ui_config.calendar.defaultView = "resourceDay";

				// save the new view to global state so it gets picked up in rendering
				$scope.save_global_state("schedule_view_name", "resourceDay");
				$scope.update_calendar_view();
			}
			else
			{
				// Reset everything to single-provider view mode
				$scope.ui_config.calendar.defaultView = "agendaWeek";
				$scope.save_global_state("schedule_view_name", "agendaWeek");
				$scope.ui_config.calendar.resources = false;
			}

			$scope.apply_ui_config($scope.ui_config);
		}
		else if(
			$scope.view_name() !== 'resourceDay' &&
			$scope.selected_schedule !== null
		)
		{
			promise_array.push(
				$scope.calendar_api_adapter.load_schedule($scope.selected_schedule.uuid));
		}
		else
		{
			angular.forEach($scope.selected_resources, function(selected)
			{
				promise_array.push($scope.calendar_api_adapter.load_schedule(selected.uuid));
			});
		}

		// Loop through the schedules added above and add them to the schedule list.
		$q.all(promise_array).then(
				function success(results_array)
				{
					$scope.schedules = [];
					for(var i=0; i<results_array.length; i++)
					{
						$scope.schedules[i] = results_array[i].data;
					}
					deferred.resolve(results_array);
				}, function error(errors)
				{
					console.log('errors');
				});

		return deferred.promise;
	};




	// ===============================================================================================
	// Schedule config methods
	// ===============================================================================================

	// TODO-legacy: is this used anywhere?
	$scope.set_schedule_hour_range = function set_schedule_hour_range()
	{
		// restrict day view if user preferences are set

		var min_time = $scope.get_global_preference_setting('schedule_min_time');
		if(util.exists(min_time))
		{
			// format: HH24:MM:SS - expect HH24:MM in preference
			$scope.ui_config.calendar.minTime = min_time + ":00";
		}

		var max_time = $scope.get_global_preference_setting('schedule_max_time');
		if(util.exists(max_time))
		{
			// format: HH24:MM:SS - expect HH24:MM in preference
			$scope.ui_config.calendar.maxTime = max_time + ":00";
		}

		// scroll so that one hour ago is the top of the calendar
		$scope.ui_config.calendar.scrollTime = moment().subtract(1, 'hours').format('HH:mm:ss');
	};

	$scope.load_default_selections = function load_default_selections()
	{
		$scope.selected_schedule = $scope.calendar_api_adapter.get_selected_schedule(
				$scope.schedule_options);

		$scope.selected_resources = $scope.calendar_api_adapter.get_selected_resources(
				$scope.resource_options);

		$scope.selected_time_interval = $scope.calendar_api_adapter.get_selected_time_interval(
			$scope.time_interval_options, $scope.default_time_interval);
		$scope.ui_config.calendar.slotDuration = $scope.selected_time_interval;
		$scope.ui_config.calendar.slotLabelInterval = $scope.selected_time_interval;

		$scope.ui_config.calendar.minTime = $scope.calendar_api_adapter.get_schedule_min_time();
		$scope.ui_config.calendar.maxTime = $scope.calendar_api_adapter.get_schedule_max_time();

		// scroll so that one hour ago is the top of the calendar
		$scope.ui_config.calendar.scrollTime = moment().subtract(1, 'hours').format('HH:mm:ss');

	};

	$scope.set_calendar_resources = function set_calendar_resources()
	{
		if($scope.view_name() === 'resourceDay')
		{
			$scope.ui_config.calendar.resources = $scope.selected_resources;
		}
		else
		{
			$scope.ui_config.calendar.resources = false;
		}

		$scope.apply_ui_config($scope.ui_config);
	};

	$scope.set_event_sources = function set_event_sources()
	{
		$scope.event_sources.push($scope.calendar_events);
	};

	$scope.time_interval_minutes = function time_interval_minutes()
	{
		return parseInt($scope.selected_time_interval.split(":")[1]);
	};

	$scope.create_invoice_callback = function create_invoice_callback()
	{
		// check that the calendar exists before running refetch,
		// in case the window was closed before the callback
		if($scope.calendar())
		{
			$scope.refetch_events();
		}
	};

	$scope.open_view_invoices = function open_view_invoices(event_uuid)
	{
		$window.open(
				"#/invoice/list?schedule_event_uuid=" +
				encodeURIComponent(event_uuid),
				'_blank');
	};


	//=========================================================================
	// Watches
	//=========================================================================/

	$scope.$watch(
			function() { return $scope.global_state.selected_date },
			function(date)
			{
				if($scope.calendar())
				{
					$scope.calendar().fullCalendar('gotoDate', date);
				}
			});


	//=========================================================================
	// Config Array
	//=========================================================================/

	// Any changes to this array need to be applied by calling apply_ui_config()
	$scope.ui_config = {
		calendar: {
			height: $scope.get_schedule_height(),
			nowIndicator: true,
			header: {
				left: 'title',
				center: '',
				right: 'today prev,next'
			},

			allDaySlot: false,

			defaultView: null,
			defaultDate: $scope.default_date,
			slotDuration: $scope.selected_time_interval,
			slotLabelInterval: $scope.selected_time_interval,
			slotLabelFormat: 'h:mma',

			loading: $scope.set_calendar_loading,

			selectable: true,
			select: $scope.open_create_event_dialog,
			eventClick: $scope.on_event_clicked,
			eventRender: $scope.on_event_render,
			viewRender: $scope.on_view_render,
			eventAfterAllRender: $scope.after_render,

			editable: true,
			eventDrop: $scope.on_event_drop,
			eventResize: $scope.on_event_resize
		}
	};
}]);
