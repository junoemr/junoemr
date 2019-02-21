
import {AppointmentApi} from '../../generated/api/AppointmentApi';
import {ScheduleApi} from '../../generated/api/ScheduleApi';

angular.module('Schedule').controller('Schedule.ScheduleController', [

	'$scope',
	'$stateParams',
	'$q',
	'$http',
	'$httpParamSerializer',
	'$uibModal',
	'focusService',
	'scheduleService',
	'securityService',
	'uiCalendarConfig',

	function(
		$scope,
		$stateParams,
		$q,
		$http,
		$httpParamSerializer,
		$uibModal,
		focusService,
		scheduleService,
		securityService,
		uiCalendarConfig
	)
	{
		//var controller = this;

		// XXX: put this address somewhere else
		$scope.appointmentApi = new AppointmentApi($http, $httpParamSerializer,
			'http://localhost:9090/ws/rs');

		$scope.scheduleApi = new ScheduleApi($http, $httpParamSerializer,
			'http://localhost:9090/ws/rs');


		//=========================================================================
		// Local scope variables
		//=========================================================================/

		$scope.calendarName = 'cpCalendar';
		$scope.initialized = false;
		$scope.calendarLoading = false;

		$scope.uiConfig = {};
		$scope.uiConfigApplied = {};
		$scope.eventSources = [];
		$scope.selectedSchedule = null;
		$scope.selectedSiteName = null;
		$scope.selectedTimeInterval = null;

		// cpCalendar control object.  The cpCalendar directive puts its control API methods in
		// this object.
		$scope.cpCalendarControl = {};


		// Parameters from directive controller


		$scope.schedules = [];
		$scope.scheduleOptions = [];
		$scope.resourceOptions = [];
		$scope.siteOptions = [];
		$scope.defaultEventColor = "#333";
		$scope.defaultTimeInterval = '00:15:00';
		$scope.timeIntervalOptions =
			['00:05:00','00:10:00','00:15:00','00:30:00'];
		$scope.selectedTimeInterval = $scope.defaultTimeInterval;
		$scope.defaultAutoRefreshMinutes = 3;
		$scope.defaultCalendarView = 'agendaWeek';
		$scope.eventStatuses = {};
		$scope.rotateStatuses = [];
		$scope.availabilityTypes = {};
		$scope.resourceOptionHash = {};
		$scope.events = [];
		$scope.scheduleTemplates = {};
		$scope.sites = {};

		$scope.openingDialog = false;
		$scope.dialog = null;


		// Global State parameters
		$scope.defaultDate = null;
		$scope.selectedDate = null;
		$scope.scheduleViewName = null;
		$scope.scheduleDefault = null;
		$scope.scheduleTimeInterval = null;
		$scope.scheduleAutoRefresh = null;
		$scope.scheduleAutoRefreshMinutes = null;



		$scope.init = function init()
		{
			$scope.uiConfig.calendar.defaultView = $scope.calendarViewName();

			// XXX: loadScheduleTemplates seems to not be used
			//$scope.loadScheduleTemplates().then(function()
			//{
				$scope.loadAvailabilityTypes().then(function()
				{
					$scope.loadEventStatuses().then(function()
					{
						$scope.loadScheduleOptions().then(function()
						{
							$scope.loadSiteOptions().then(function()
							{
								$scope.loadDefaultSelections();

								$scope.loadSelectedSchedules().then(function()
								{
									$scope.setCalendarResources();

									$scope.setEventSources();

									$scope.initEventsAutoRefresh();

									$scope.applyUiConfig($scope.uiConfig);
									console.log("-- Calendar Initialized ----------------------------");
									$scope.initialized = true;
								});
							});
						});
					});
				});
			//});
		};


		//=========================================================================
		// Public Methods
		//=========================================================================/

		$scope.calendar = function calendar()
		{
			return uiCalendarConfig.calendars[$scope.calendarName];
		};

		$scope.isSchedulingEnabled = function isSchedulingEnabled()
		{
			return true;
		};

		$scope.isInitialized = function isInitialized()
		{
			return $scope.initialized;
		};

		$scope.hasSchedules = function hasSchedules()
		{
			return $scope.schedules.length > 0;
		};

		$scope.hasSites = function hasSites()
		{
			return $scope.siteOptions.length > 0;
		};

		$scope.getTimeIntervalOptions = function getTimeIntervalOptions()
		{
			return $scope.timeIntervalOptions;
		};

		$scope.getScheduleOptions = function getScheduleOptions()
		{
			return $scope.scheduleOptions;
		};

		$scope.getSiteOptions = function getSiteOptions()
		{
			if(!angular.isDefined($scope.selectedSite) || $scope.selectedSite === null)
			{
				$scope.selectedSite = $scope.siteOptions[0];
			}

			return $scope.siteOptions;
		};

		$scope.viewName = function viewName()
		{
			var viewName = $scope.scheduleViewName;

			if(!Juno.Common.Util.exists(viewName))
			{
				viewName = $scope.defaultCalendarView;
			}

			return viewName;
		};

		$scope.calendarEvents = function calendarEvents(start, end, timezone, callback)
		{
			// load the events for each of the loaded schedules
			var promise_array = [];
			for(var i = 0; i < $scope.schedules.length; i++)
			{
				promise_array.push(
					$scope.loadScheduleEvents(
						$scope.schedules[i].uuid, $scope.selected_site_name, start, end));
			}

			// once all the events are loaded, concat them together and callback
			$q.all(promise_array).then(
				function success(results_array)
				{
					$scope.events = Array.prototype.concat.apply([], results_array);

					try
					{
						callback($scope.events);
					}
					catch(err)
					{
						// the callback throws an error on first load, ignore
					}
				}
			);
		};

		$scope.showTimeIntervals = function showTimeIntervals()
		{
			return $scope.viewName() !== 'month';
		};

		$scope.changeView = function changeView(view)
		{
			// if switching to or from resourceDay view, need to update schedules
			var reload_schedules = false;
			if(view === 'resourceDay' || $scope.scheduleViewName === 'resourceDay')
			{
				reload_schedules = true;
			}

			// save the new view to global state so it gets picked up in rendering
			$scope.scheduleViewName = view;

			if(reload_schedules)
			{
				$scope.loadSelectedSchedules().then(
					function success()
					{
						$scope.setCalendarResources();
						$scope.updateCalendarView();
					});
			}
			else
			{
				$scope.updateCalendarView();
			}
		};

		$scope.showLegend = function showLegend()
		{
			// if already opening a dialog or have one open, ignore and return
			if($scope.openingDialog || $scope.dialog)
			{
				return;
			}
			$scope.openingDialog = true;

			var data = {
				event_statuses: $scope.eventStatuses,
				availability_types: $scope.availabilityTypes
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

			$scope.openingDialog = false;
		};

		$scope.refetchEvents = function refetchEvents()
		{
			$scope.calendar().fullCalendar('refetchEvents');
		};

		$scope.isAgendaView = function isAgendaView()
		{
			return ($scope.viewName() != 'resourceDay')
		};


		//=========================================================================
		// Private methods
		//=========================================================================/

		$scope.getSelectedSchedule = function getSelectedSchedule(scheduleOptions)
		{
			// priority: last used from global state, then preference setting,
			// then default (first in the list)
			var selectedUuid = null;
			if($scope.scheduleDefault)
			{
				selectedUuid = $scope.scheduleDefault;
			}

			if(Juno.Common.Util.exists(selectedUuid))
			{
				// only choose it if it can be found in the options list
				for(var i = 0; i < scheduleOptions.length; i++)
				{
					if(selectedUuid === scheduleOptions[i].uuid)
					{
						return scheduleOptions[i];
					}
				}
			}

			if(scheduleOptions.length > 0)
			{
				// select the first schedule in the list by default
				return scheduleOptions[0];
			}

			return null;
		};

		$scope.loadSelectedSchedules = function loadSelectedSchedules()
		{
			var deferred = $q.defer();

			var promiseArray = [];

			if(
				Juno.Common.Util.exists($scope.selectedSchedule.providerNos) &&
				angular.isArray($scope.selectedSchedule.providerNos)
			)
			{
				// TODO: this is really gross and I don't like it
				// Potentially put this in the calendarApiAdapter
				angular.forEach($scope.selectedSchedule.providerNos, function(providerNo)
				{
					promiseArray.push($scope.loadSchedule(providerNo.toString()));
				});

				if($scope.selectedSchedule.providerNos.length > 1)
				{
					// Set the calendar to resource mode.  All of these values need to be set.
					$scope.selectedResources = $scope.buildSelectedResources($scope.selectedSchedule.providerNos);
					$scope.uiConfig.calendar.resources = $scope.selectedResources;
					$scope.uiConfig.calendar.defaultView = "resourceDay";

					// save the new view to global state so it gets picked up in rendering
					$scope.scheduleViewName = 'resourceDay';
					$scope.updateCalendarView();
				}
				else
				{
					// Reset everything to single-provider view mode
					$scope.uiConfig.calendar.defaultView = "agendaWeek";
					$scope.scheduleViewName = 'agendaWeek';
					$scope.uiConfig.calendar.resources = false;
				}

				$scope.applyUiConfig($scope.uiConfig);
			}
			else if(
				$scope.viewName() !== 'resourceDay' &&
				$scope.selectedSchedule !== null
			)
			{
				promiseArray.push(
					$scope.loadSchedule($scope.selectedSchedule.uuid));
			}
			else
			{
				angular.forEach($scope.selectedResources, function(selected)
				{
					promiseArray.push($scope.loadSchedule(selected.uuid));
				});
			}

			// Loop through the schedules added above and add them to the schedule list.
			$q.all(promiseArray).then(
				function success(resultsArray)
				{
					$scope.schedules = [];
					for(var i = 0; i < resultsArray.length; i++)
					{
						$scope.schedules[i] = resultsArray[i].data;
					}
					deferred.resolve(resultsArray);
				}, function error(errors)
				{
					console.log('errors');
				});

			return deferred.promise;
		};

		$scope.loadSchedule = function loadSchedule(providerId)
		{
			var deferred = $q.defer();

			// TODO: fill up availabilities and relations, or figure out how to show that info without them
			var schedule = {
				uuid: providerId,
				availabilities: [], // TODO: figure out if these have a Juno equivalent, I don't think
									// TODO: they do.  They are things like holidays and vacation days
				relations: [],
				events: []
			};

			deferred.resolve({data: schedule});

			return deferred.promise;
		};

		$scope.updateCalendarView = function updateCalendarView()
		{
			$scope.uiConfig.calendar.defaultView = $scope.calendarViewName();
			$scope.applyUiConfig($scope.uiConfig);
		};

		// This gets the view name, but if it's resourceDay, it will get agendaDay.
		// TODO Not sure why this works this way.  Maybe it uses it to get a day of events for each
		//      resource in the resource list?
		$scope.calendarViewName = function calendarViewName()
		{
			var view = $scope.viewName();
			if(view == 'resourceDay')
			{
				view = 'agendaDay';
			}
			return view;
		};

		$scope.setCalendarLoading = function setCalendarLoading(isLoading)
		{
			$scope.calendarLoading = isLoading;
		};

		$scope.getLoadedSchedule = function getLoadedSchedule(uuid)
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

		// TODO: change this, perhaps?  It is getting the resource details from the groups
		$scope.buildSelectedResources = function buildSelectedResources(providerNos)
		{
			var selectedResources = [];

			for(var i = 0; i < providerNos.length; i++)
			{
				selectedResources.push($scope.resourceOptionHash[providerNos[i]]);
			}

			return selectedResources;
		};

		$scope.setCalendarResources = function setCalendarResources()
		{
			if($scope.viewName() === 'resourceDay')
			{
				$scope.uiConfig.calendar.resources = $scope.selectedResources;
			}
			else
			{
				$scope.uiConfig.calendar.resources = false;
			}

			$scope.applyUiConfig($scope.uiConfig);
		};

		$scope.setEventSources = function setEventSources()
		{
			$scope.eventSources.push($scope.calendarEvents);
		};

		$scope.timeIntervalMinutes = function timeIntervalMinutes()
		{
			return parseInt($scope.selectedTimeInterval.split(":")[1]);
		};

		$scope.loadAvailabilityTypes = function loadAvailabilityTypes()
		{
			var deferred = $q.defer();
			var availabilityTypes = {};

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

						availabilityTypes[result.code] = {
							color: result.color,
							name: result.description,
							preferred_event_length_minutes: result.duration,
							system_code: null,
						};
					}

					$scope.availabilityTypes = availabilityTypes;
					deferred.resolve(availabilityTypes);
				});

			return deferred.promise;
		};

		$scope.loadDefaultSelections = function loadDefaultSelections()
		{
			$scope.selectedSchedule = $scope.getSelectedSchedule($scope.scheduleOptions);

			//$scope.selectedResources = $scope.getSelectedResources($scope.resourceOptions);

			$scope.selectedTimeInterval = $scope.getSelectedTimeInterval(
				$scope.timeIntervalOptions, $scope.defaultTimeInterval);
			$scope.uiConfig.calendar.slotDuration = $scope.selectedTimeInterval;
			$scope.uiConfig.calendar.slotLabelInterval = $scope.selectedTimeInterval;

			$scope.uiConfig.calendar.minTime = $scope.getScheduleMinTime();
			$scope.uiConfig.calendar.maxTime = $scope.getScheduleMaxTime();

			// scroll so that one hour ago is the top of the calendar
			$scope.uiConfig.calendar.scrollTime = moment().subtract(1, 'hours').format('HH:mm:ss');
		};

		$scope.getSelectedTimeInterval = function getSelectedTimeInterval(
			timeIntervalOptions, defaultTimeInterval)
		{
			// priority: last used from global state, then preference setting,
			// then default

			var selectedTimeInterval = null;

			var timeInterval = $scope.scheduleTimeInterval;
			if(timeInterval === null)
			{
				timeInterval = $scope.scheduleTimeInterval;
			}

			if(Juno.Common.Util.exists(timeInterval))
			{
				// only choose it if it can be found in the options list
				for(var i = 0; i < timeIntervalOptions.length; i++)
				{
					if(timeInterval === timeIntervalOptions[i])
					{
						selectedTimeInterval = timeIntervalOptions[i];
						break;
					}
				}
			}

			if(selectedTimeInterval === null)
			{
				return defaultTimeInterval;
			}

			return selectedTimeInterval;
		};

		$scope.getScheduleMinTime = function getScheduleMinTime()
		{
			// restrict day view if user preferences are set

			/*				var min_time = service.get_global_preference_setting('schedule_min_time');
						if (util.exists(min_time)) {
							// format: HH24:MM:SS - expect HH24:MM in preference
							return min_time + ":00";
						}

						return null;*/

			return "08:00";
		};

		$scope.getScheduleMaxTime = function getScheduleMaxTime()
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

		// Loads the list of event statuses from the API (i.e. appointment statuses).  Sets the following:
		// $scope.event_statuses - a table to look up a status by uuid.
		// $scope.rotate_statuses - an array to describe how to cycle through statuses.
		$scope.loadEventStatuses = function loadEventStatuses()
		{
			var deferred = $q.defer();

			$scope.eventStatuses = {};
			$scope.rotateStatuses = [];

			$scope.scheduleApi.getCalendarAppointmentStatuses().then(
				function success(rawResults)
				{
					var results = rawResults.data.body;

					for(var i = 0; i < results.length; i++)
					{
						var result = results[i];
						$scope.eventStatuses[result.displayLetter] = result;
						if(result.rotates)
						{
							$scope.rotateStatuses.push(result);
						}
					}

					deferred.resolve(results);
				});

			return deferred.promise;
		};

		$scope.loadScheduleEvents = function loadScheduleEvents(providerId, siteName, start, end)
		{
			var deferred = $q.defer();

			// Get date strings to pass to the backend.  The calendar provides datetime that describe
			// and inclusive start time and exclusive end time, so one second is removed from
			// the end time to convert to the correct date.
			var startDateString = start.format(Juno.Common.Util.settings.date_format);
			var endDateString = end.subtract(1, 'seconds').format(Juno.Common.Util.settings.date_format);

			$scope.scheduleApi.getCalendarEvents(
				providerId,
				startDateString,
				endDateString,
				siteName
			).then(
				function(results)
				{
					deferred.resolve(results.data.body);
				},
				function(results)
				{
					deferred.reject(results.data.body);
				}
			);

			return deferred.promise;
		};

		$scope.saveEvent = function saveEvent(
			editMode,
			eventUuid,
			startDatetime,
			endDatetime,
			eventData,
			scheduleUuid,
			selectedEventStatusCode,
			demographicNo,
			siteUuid
		)
		{
			var deferred = $q.defer();

			var dateString = Juno.Common.Util.formatMomentDate(startDatetime);

			if(editMode)
			{
				var startDatetimeString = Juno.Common.Util.formatMomentTime(
					startDatetime, Juno.Common.Util.settings.datetime_no_timezone_format);
				var endDatetimeString = Juno.Common.Util.formatMomentTime(
					endDatetime, Juno.Common.Util.settings.datetime_no_timezone_format);

				var appointment =  {
					"id": eventUuid,
					"providerNo": scheduleUuid,
					"appointmentDate": dateString,
					"startTime": startDatetimeString,
					"endTime": endDatetimeString,
					"demographicNo": demographicNo,
					"status": selectedEventStatusCode,
					"notes": eventData.notes,
					"reason": eventData.reason,
					"location": siteUuid,
				};

				this.appointmentApi.updateAppointment(appointment).then(
					function(result)
					{
						deferred.resolve(result.data);
					},
					function(result)
					{
						deferred.reject(result.data);
					}
				);
			}
			else
			{
				var startTimeString = Juno.Common.Util.formatMomentTime(startDatetime, "HH:mm");
				var duration = moment.duration(endDatetime.diff(startDatetime)).asMinutes();

				var newAppointment =  {
					"providerNo": scheduleUuid,
					"appointmentDate": dateString,
					"startTime": startTimeString,
					"duration": duration,
					"demographicNo": demographicNo,
					"status": selectedEventStatusCode,
					"notes": eventData.notes,
					"reason": eventData.reason,
					"location": siteUuid,
				};


				$scope.appointmentApi.addAppointment(newAppointment).then(
					function(result)
					{
						deferred.resolve(result.data);
					},
					function (result)
					{
						deferred.reject(result.data);
					});
			}


			return deferred.promise;
		};

		$scope.moveEvent = function moveEvent(appointment, delta)
		{
			var deferred = $q.defer();

			var eventData = {
				reason: appointment.reason,
				notes: appointment.notes
			};

			var start_moment = Juno.Common.Util.getDatetimeNoTimezoneMoment(appointment.start_time);
			var end_moment = Juno.Common.Util.getDatetimeNoTimezoneMoment(appointment.end_time);

			$scope.saveEvent(
				true,
				appointment.appointment_uuid,
				start_moment.add(delta.asMinutes(), 'minutes'),
				end_moment.add(delta.asMinutes(), 'minutes'),
				eventData,
				appointment.schedule_uuid,
				appointment.event_status_uuid,
				appointment.demographics_patient_uuid,
				appointment.site
			).then(
				function success(data)
				{
					deferred.resolve(data.body);
				},
				function success(data)
				{
					deferred.reject(data.body);
				}
			);


			return deferred.promise;
		};

		// Read the implementation-specific results and return a calendar-compatible object.
		$scope.processSaveResults = function processSaveResults(results, displayErrors)
		{
			var status = (results || {}).status;

			if(status == 'SUCCESS')
			{
				return true;
			}

			var errorMessage = ((results || {}).error || {}).message;
			var validationErrorArray = ((results || {}).error || {}).validationErrors;

			if(Array.isArray(validationErrorArray))
			{
				displayErrors.add_standard_error(errorMessage);
				//for(var error in validationErrorArray)
				for(var i = 0; i < validationErrorArray.length; i++)
				{
					var error = validationErrorArray[i];
					displayErrors.add_field_error(error.path, error.message);
				}
			}
		};

		$scope.deleteEvent = function deleteEvent(appointmentNo)
		{
			var deferred = $q.defer();

			$scope.appointmentApi.deleteAppointment(appointmentNo).then(
				function(result)
				{
					deferred.resolve(result.data);

				},
				function(result)
				{
					deferred.reject(result.data);
				}
			);

			return deferred.promise;
		};

		$scope.openCreateInvoice = function openCreateInvoice(
			event_uuid, schedule_uuid, demographics_patient_uuid)
		{
			var schedule =
				$scope.get_loaded_schedule(schedule_uuid);

			var url = $scope.calendar_api_adapter.get_create_invoice_url(event_uuid,
				schedule.demographics_practitioner_uuid, demographics_patient_uuid);

			window.window_scope = $scope;
			$window.open(url, '_blank');
		};

		$scope.getIconPath = function getIconPath(icon, statusModifier)
		{
			if(!Juno.Common.Util.exists(icon))
			{
				return "";
			}

			var modifierString = "";

			if(Juno.Common.Util.exists(statusModifier))
			{
				modifierString = statusModifier;
			}

			return "../images/" + modifierString + icon;
		};

		$scope.rotateEventStatus = function rotateEventStatus(calEvent)
		{
			if(!securityService.hasPermission('scheduling_create') )
			{
				return;
			}

			$scope.setCalendarLoading(true);

			$scope.calendar_api_adapter.rotate_event_status(calEvent.data.uuid, $scope.rotate_statuses).then(
				function success(event_data)
				{

					var event_status_uuid = event_data.event_status_uuid;
					var event_status_color =
						$scope.eventStatuses[event_status_uuid] ?
							$scope.eventStatuses[event_status_uuid].color :
							$scope.default_event_color;

					calEvent.data.event_status_uuid = event_status_uuid;
					calEvent.color = event_status_color;
					// This is being set to an array because of a bug:
					// https://github.com/fullcalendar/fullcalendar/issues/4011
					calEvent.className = [$scope.calendar_api_adapter.event_class(event_status_color)];

					$scope.updateEvent(calEvent);

					$scope.setCalendarLoading(false);
				});

			/*
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
				*/
		};


		//=========================================================================
		// Event Handlers
		//=========================================================================/

		$scope.onEventRender = function onEventRender(event, element, view)
		{
			if(event.rendering !== 'background')
			{
				var eventSiteHtml = '';
				var eventSite = $scope.sites[event.data.site];

				if(Juno.Common.Util.exists(eventSite))
				{
					eventSiteHtml += "<span style='background-color: " + eventSite.color + "'>&nbsp;</span>"
				}


				var eventStatusHtml = '';
				var eventStatus =
					$scope.eventStatuses[event.data.eventStatusCode];

				if(Juno.Common.Util.exists(eventStatus) && Juno.Common.Util.exists(eventStatus.icon) &&
					Juno.Common.Util.exists(event) && Juno.Common.Util.exists(event.data))
				{
					eventStatusHtml += "<img src='" + $scope.getIconPath(eventStatus.icon, event.data.eventStatusModifier) + "' />";
				}
				else
				{
					eventStatusHtml = '<span class="event-status';
					if(Juno.Common.Util.exists(eventStatus))
					{
						var eventStatusRotate = Juno.Common.Util.exists(eventStatus.sortOrder);
						if(eventStatusRotate)
						{
							eventStatusHtml += ' rotate ';
						}
						eventStatusHtml += '"' + ' title="' + Juno.Common.Util.escapeHtml(eventStatus.name) + '">' +
							Juno.Common.Util.escapeHtml(eventStatus.displayLetter) + '</span>';
					}
					else
					{
						eventStatusHtml += '" title="Unknown">?</span>';
					}
				}

				var eventInvoiceHtml = '<span class="event-invoice';
				if(event.data.numInvoices > 0)
				{
					eventInvoiceHtml += ' edit" title="View Invoice' +
						(event.data.numInvoices > 1 ? "s" : "") + '">B</span>';
				}
				else
				{
					eventInvoiceHtml += '" title="Create Invoice">$</span>';
				}

				console.log(event);
				var eventDetails = "";
				if(!Juno.Common.Util.isBlank(event.data.demographicName))
				{
					eventDetails = Juno.Common.Util.escapeHtml(event.data.demographicName);
					if(!Juno.Common.Util.isBlank(event.data.reason))
					{
						eventDetails += " (" + Juno.Common.Util.escapeHtml(event.data.reason) + ")"
					}
				}
				else if(!Juno.Common.Util.isBlank(event.data.reason))
				{
					eventDetails = Juno.Common.Util.escapeHtml(event.data.reason);
				}

				var eventDetailsHtml = "<span class='event-details' title='" + eventDetails + "'>" +
					eventDetails + "</span>";

				var eventDemographicHtml = "";
				if(securityService.hasOneOfPermissions(['patient_view', 'patient_manage']) &&
					Juno.Common.Util.exists(event.data.demographicNo))
				{
					eventDemographicHtml =
						'<span class="event-demographic" title="View Patient">' +
						'<i class="fa fa-user"></i></span>';
				}

				var eventNoteHtml = "";
				if(securityService.hasOneOfPermissions(['chart_note_view', 'chart_note_manage']) &&
					Juno.Common.Util.exists(event.data.demographicNo))
				{
					eventNoteHtml =
						'<span class="event-note" title="Add Patient Note">' +
						'<i class="fa fa-file-text-o"></i></span>';
				}

				var eventTagsHtml = '';
				var tagClass = element.hasClass('text-light') ? 'icon-white' : '';

				if(Juno.Common.Util.exists(event.data.tagNames))
				{
					eventTagsHtml = '<span class="event-tags" title="' +
						event.data.tagNames.join(", ") +
						'"><i class="icon ' + tagClass + ' icon-tags"/></span>';
				}

				$(element).find('.fc-content').html(eventSiteHtml + eventStatusHtml + eventInvoiceHtml +
					eventDemographicHtml + eventNoteHtml + eventTagsHtml + eventDetailsHtml);
			}
		};

		$scope.onViewRender = function onViewRender()
		{
			if($scope.isInitialized() && $scope.calendar())
			{
				$scope.selectedDate = moment(Juno.Common.Util.formatMomentDate(
					moment($scope.calendar().fullCalendar('getDate'))));
			}

			// Voodoo to set the resource view column width from https://stackoverflow.com/a/39297864
			$("#ca-calendar").css('min-width',$('.fc-resource-cell').length*125);
		};

		$scope.afterRender = function afterRender()
		{
			// Voodoo to set the resource view column width from https://stackoverflow.com/a/39297864
			$('.fc-agendaDay-button').click(function()
			{
				$("#schedule_container").css('min-width',$('.fc-resource-cell').length*125);
			});
		};

		$scope.openCreateEventDialog = function openCreateEventDialog(
			start, end, jsEvent, view, resource)
		{
			if(!securityService.hasPermission('scheduling_create') )
			{
				return;
			}

			// if already opening a dialog or have one open, ignore and return
			if($scope.openingDialog || $scope.dialog)
			{
				return;
			}
			$scope.openingDialog = true;

			var scheduleUuid = null;
			var displayName = "";
			if(Juno.Common.Util.exists(resource))
			{
				scheduleUuid = resource.id;
				displayName = resource.display_name;
			}
			else if($scope.selectedSchedule !== null)
			{
				scheduleUuid = $scope.selectedSchedule.uuid;
			}

			var schedule = $scope.getLoadedSchedule(scheduleUuid);
			if(schedule !== null)
			{
				//var defaultEventStatus = schedule.new_event_status_uuid;

				var modalSchedule = angular.copy(schedule);
				modalSchedule.display_name = displayName;

				var data = {
					schedule: modalSchedule,
					default_event_status: null, //defaultEventStatus,
					start_time: start,
					end_time: end,
					time_interval: $scope.timeIntervalMinutes(),
					schedule_templates: $scope.scheduleTemplates,
					availability_types: $scope.availabilityTypes,
					sites: $scope.sites
				};

				$scope.dialog = $uibModal.open({
					animation: false,
					backdrop: 'static',
					controller: 'Schedule.EventController',
					templateUrl: 'src/schedule/event.jsp',
					resolve: {
						type: [function() { return 'create_edit_event' }],
						label: [function() { return 'Appointment' }],
						parentScope: [function() { return $scope }],
						data: [function() { return data }],
						editMode: [function() { return false }],
						//access_control: [function() {return securityService}],
						keyBinding: [function() {return {bindKeyGlobal: function(){}}}],
						focus: [function() {return focusService}],
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

			$scope.openingDialog = false;
		};

		// XXX: make this work
		$scope.openPatientDialog = function openPatientDialog(editModeCallback, onSaveCallback,
			loadErrorLinkPatientFn)
		{
			//global_state.enable_keyboard_shortcuts = false;

			return this.$uibModal.open({
				animation: false,
				backdrop: 'static',
				size: 'lg',
				controller: 'Invoice.Common.Patient.FormController',
				templateUrl: 'code/invoice/common/patient/quick_form.html',
				resolve: {
					edit_mode: editModeCallback,
					on_save_callback: onSaveCallback,
					load_error_link_patient_fn: loadErrorLinkPatientFn
				}
			});
		};

		$scope.openPatientDemographic = function openPatientDemographic(calEvent)
		{
			console.log("Not implemented at this time.");
		};

		$scope.openCreateChartNote = function openCreateChartNote(calEvent)
		{
			// XXX: are we going to do this?

			console.log("Not implemented at this time.");
		};

		$scope.openEditEventDialog = function openEditEventDialog(calEvent)
		{
			if(!securityService.hasPermission('scheduling_edit') )
			{
				return;
			}

			// if already opening a dialog or have one open, ignore and return
			if($scope.openingDialog || $scope.dialog)
			{
				return;
			}

			$scope.openingDialog = true;

			var scheduleUuid = calEvent.data.schedule_uuid;
			var displayName = calEvent.data.demographics_patient_name;

			if(displayName == null)
			{
				displayName = '';
			}

			var schedule = $scope.getLoadedSchedule(scheduleUuid);

			if(schedule !== null)
			{
				//var defaultEventStatus = schedule.new_event_status_uuid;

				var modalSchedule = angular.copy(schedule);
				modalSchedule.display_name = displayName;

				console.log(calEvent);

				var data = {

					schedule: modalSchedule,
					default_event_status: null, //defaultEventStatus,
					start_time: calEvent.start,
					end_time: calEvent.end,
					time_interval: $scope.timeIntervalMinutes(),
					schedule_templates: $scope.scheduleTemplates,
					availability_types: $scope.availabilityTypes,
					sites: $scope.sites,
					event_data: calEvent.data
				};

				$scope.dialog = $uibModal.open({
					animation: false,
					backdrop: 'static',
					controller: 'Schedule.EventController',
					templateUrl: 'src/schedule/event.jsp',
					resolve: {
						type: [function() { return 'create_edit_event' }],
						label: [function() { return 'Appointment' }],
						parentScope: [function() { return $scope }],
						data: [function() { return data }],
						editMode: [function() { return true }],
						//access_control: [function() {return securityService}],
						keyBinding: [function() {return {bindKeyGlobal: function(){}}}],
						focus: [function() {return focusService}],
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

			$scope.openingDialog = false;
		};

		$scope.onEventClicked = function onEventClicked(calEvent, jsEvent, view)
		{
			if($(jsEvent.target).is(".event-status.rotate"))
			{
				$scope.rotateEventStatus(calEvent);
			}
			else if($(jsEvent.target).is(".event-invoice.edit"))
			{
				$scope.openViewInvoices(calEvent.data.uuid);
			}
			else if($(jsEvent.target).is(".event-invoice"))
			{
				$scope.openCreateInvoice(
					calEvent.data.uuid,
					calEvent.data.schedule_uuid,
					calEvent.data.demographics_patient_uuid);
			}
			else if($(jsEvent.target).is(".event-demographic") ||
				$(jsEvent.target).parent().is(".event-demographic"))
			{
				$scope.openPatientDemographic(calEvent);
			}
			else if($(jsEvent.target).is(".event-note") ||
				$(jsEvent.target).parent().is(".event-note"))
			{
				$scope.openCreateChartNote(calEvent);
			}
			else
			{
				$scope.openEditEventDialog(calEvent);
			}
		};

		$scope.onEventDrop = function onEventDrop(
			calEvent, delta, revertFunc, jsEvent, ui, view)
		{
			if(!securityService.hasPermission('scheduling_create') )
			{
				revertFunc();
				return;
			}

			// event was dragged and dropped on the calendar:
			// load then update the start and end time based on the delta
			$scope.setCalendarLoading(true);

			$scope.moveEvent(calEvent.data, delta).then(
				function success(eventData)
				{
					console.log(eventData);
					console.log(calEvent);

					/*
					var startMoment = Juno.Common.Util.getDatetimeNoTimezoneMoment(eventData.startTime)
					var endMoment = Juno.Common.Util.getDatetimeNoTimezoneMoment(eventData.endTime)
					*/

					var startMoment = moment(eventData.startTime, "YYYY-MM-DDTHH:mm:ss.SSS+ZZZZ", false);
					var endMoment = moment(eventData.endTime, "YYYY-MM-DDTHH:mm:ss.SSS+ZZZZ", false);

					calEvent.data.start_time = Juno.Common.Util.formatMomentTime(
						startMoment, Juno.Common.Util.settings.datetime_no_timezone_format);
					calEvent.data.end_time = Juno.Common.Util.formatMomentTime(
						endMoment, Juno.Common.Util.settings.datetime_no_timezone_format);

					calEvent.data.schedule_uuid = eventData.providerNo;
					console.log(calEvent);

					$scope.setCalendarLoading(false);

				}, function error(errors)
				{
					console.log('failed to save event', errors);

					// revert on fail
					revertFunc();
					$scope.setCalendarLoading(false);
				});
		};

		$scope.onEventResize = function onEventResize(calEvent, delta, revertFunc, jsEvent, ui, view)
		{
			if(!securityService.hasPermission('scheduling_create') )
			{
				revertFunc();
				return;
			}

			// event was extended by dragging the end of the event on the calendar:
			// load then update the end time based on the delta
			$scope.set_calendar_loading(true);

			$scope.resizeEvent(calEvent.data.uuid, delta).then(
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

		$scope.onSiteChanged = function onSiteChanged()
		{
			return $scope.onScheduleChanged();
		};

		$scope.onScheduleChanged = function onScheduleChanged()
		{
			var selectedSchedule = $scope.selectedSchedule;
			var selectedSiteName = $scope.selectedSiteName;

			if(!Juno.Common.Util.exists(selectedSchedule))
			{
				return;
			}

			if(Juno.Common.Util.exists(selectedSiteName))
			{
				$scope.selectedSiteName = selectedSiteName;
			}
			else
			{
				$scope.selectedSiteName = null;
			}

			// reload the schedule and then events data, triggering a rerender
			$scope.loadSelectedSchedules().then($scope.refetchEvents);
		};

		$scope.onTimeIntervalChanged = function onTimeIntervalChanged()
		{
			$scope.scheduleTimeInterval = $scope.selectedTimeInterval;

			// updating the config will automatically trigger an events refresh
			$scope.uiConfig.calendar.slotDuration = $scope.selectedTimeInterval;
			$scope.uiConfig.calendar.slotLabelInterval = $scope.selectedTimeInterval;

			$scope.applyUiConfig($scope.uiConfig);
		};


		//=========================================================================
		// Init methods pulled from the directive controller
		//=========================================================================/

		// Loads the schedule dropdown options from the API.  Sets the following:
		// $scope.schedule_options - the array used to build the schedule selection dropdown.
		// $scope.resourceOptionHash - table to look up schedule information by providerNo.  This is
		//                               used to create the resource view headers.
		$scope.loadScheduleOptions = function loadScheduleOptions()
		{
			var deferred = $q.defer();

			/*
				function success(results)
				{
					for(var i = 0; i < results.length; i++)
					{
						results[i].uuid = results[i].identifier;
					}
					deferred.resolve(results);
				}
			//);
			*/

			//$scope.loadScheduleOptions().then(

			scheduleService.getScheduleGroups().then(
				function success(results)
				{
					for(var i = 0; i < results.length; i++)
					{
						var scheduleData = results[i];

						results[i].uuid = results[i].identifier;

						$scope.scheduleOptions.push(scheduleData);

						// Get the possible resources by inferring that the group is a provider
						// by checking if the array has one entry and matches the identifier
						// Also uses fields specific to Juno.
						// TODO: CHANGE THIS!!
						if(
							angular.isArray(scheduleData.providerNos) &&
							scheduleData.providerNos.length == 1 &&
							scheduleData.providerNos[0].toString() == scheduleData.identifier
						)
						{
							var providerNo = scheduleData.providerNos[0];

							$scope.resourceOptionHash[providerNo] = {
								'id': providerNo,
								'uuid': providerNo,
								'name': providerNo,
								'title': scheduleData.name,
								'display_name': scheduleData.name
							};
						}
					}
					deferred.resolve(results);
				});

			return deferred.promise;
		};

		// Load the list of available sites from the API.  Sets the following:
		// $scope.sites - a table to lookup a site's info by name
		// $scope.site_options - the options for the site selection dropdown
		$scope.loadSiteOptions = function loadSiteOptions()
		{
			var deferred = $q.defer();

			$scope.loadSites().then(
				function success(results)
				{
					$scope.sites = {};
					$scope.siteOptions = [];
					if(angular.isArray(results) && results.length > 0)
					{
						// Fill up lookup table
						for(var i = 0; i < results.length; i++)
						{
							$scope.sites[results[i].name] = results[i];
						}

						// Create the dropdown options
						$scope.siteOptions = [
							{
								uuid: null,
								name: null,
								display_name: "All",
							}
						];

						$scope.siteOptions = $scope.siteOptions.concat(results);
					}

					deferred.resolve(results);
				});

			return deferred.promise;
		};

		$scope.loadSites = function loadSites()
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
								color: results[i].bgColor,
							});
						}
					}
					deferred.resolve(out);
				}
			);

			return deferred.promise;
		};


		//=========================================================================
		// Config Array
		//=========================================================================/

		$scope.applyUiConfig = function applyUiConfig(uiConfig)
		{
			$scope.uiConfigApplied = angular.copy(uiConfig);
		};

		$scope.initEventsAutoRefresh = function initEventsAutoRefresh()
		{
			var deferred = $q.defer();

			// if there is already a refresh set up, stop it
			var refresh = $scope.scheduleAutoRefresh;
			if(refresh !== null)
			{
				clearInterval(refresh);
			}

			// get the refresh interval from preferences, or use default
			var minutes = $scope.scheduleAutoRefreshMinutes;
			if(!Juno.Common.Util.exists(minutes) || !Juno.Common.Util.isIntegerString(minutes))
			{
				minutes = $scope.defaultAutoRefreshMinutes;
			}
			else
			{
				minutes = parseInt(minutes);
			}

			if(minutes > 0)
			{
				// start the auto refresh and save its ID to global state
				$scope.scheduleAutoRefresh = setInterval($scope.refetchEvents, minutes * 60 * 1000);
			}

			deferred.resolve();

			return deferred.promise;
		};

		// Any changes to this array need to be applied by calling applyUiConfig()
		$scope.uiConfig = {
			calendar: {
				height: 800,//$scope.get_schedule_height(),
				nowIndicator: true,
				header: {
					left: 'title',
					center: '',
					right: 'today prev,next'
				},

				allDaySlot: false,

				defaultView: null,
				defaultDate: $scope.defaultDate,
				slotDuration: $scope.selectedTimeInterval,
				slotLabelInterval: $scope.selectedTimeInterval,
				slotLabelFormat: 'h:mma',

				loading: $scope.setCalendarLoading,

				selectable: true,
				select: $scope.openCreateEventDialog,
				eventClick: $scope.onEventClicked,
				eventRender: $scope.onEventRender,
				viewRender: $scope.onViewRender,
				eventAfterAllRender: $scope.afterRender,

				editable: true,
				eventDrop: $scope.onEventDrop,
				eventResize: $scope.onEventResize,
				schedulerLicenseKey: "GPL-My-Project-Is-Open-Source"
			}
		};

		$scope.init();
	}
]);