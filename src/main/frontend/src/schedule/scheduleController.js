
import {AppointmentApi} from '../../generated/api/AppointmentApi';
import {ScheduleApi} from '../../generated/api/ScheduleApi';

angular.module('Schedule').controller('Schedule.ScheduleController', [

	'$scope',
	'$stateParams',
	'$q',
	'$http',
	'$httpParamSerializer',
	'$uibModal',
	'$state',
	'loadedSettings',
	'providerService',
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
		$state,
		loadedSettings,
		providerService,
		focusService,
		scheduleService,
		securityService,
		uiCalendarConfig
	)
	{
		let controller = this;

		// XXX: put this address somewhere else
		$scope.appointmentApi = new AppointmentApi($http, $httpParamSerializer,
			'../ws/rs');

		$scope.scheduleApi = new ScheduleApi($http, $httpParamSerializer,
			'../ws/rs');

		controller.providerSettings = loadedSettings;

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
		$scope.timeIntervalOptions = [
			{
				label: '5 min intervals',
				value: '00:05:00'
			},
			{
				label: '10 min intervals',
				value: '00:10:00'
			},
			{
				label: '15 min intervals',
				value: '00:15:00'
			},
			{
				label: '20 min intervals',
				value: '00:20:00'
			},
			{
				label: '30 min intervals',
				value: '00:30:00'
			}];
		$scope.defaultTimeInterval = $scope.timeIntervalOptions[2].value;
		$scope.selectedTimeInterval = $scope.defaultTimeInterval;
		$scope.selectedSlotLabelInterval = {hours: 1};
		$scope.defaultAutoRefreshMinutes = 3;
		$scope.defaultCalendarView = 'agendaWeek';
		$scope.eventStatuses = {};
		$scope.rotateStatuses = [];
		$scope.availabilityTypes = {};
		$scope.resourceOptionHash = {};
		$scope.events = [];
		$scope.scheduleTemplates = {};
		$scope.sites = {};
		$scope.sitesEnabled = false;

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

		$scope.scheduleService = scheduleService;

		$scope.datepickerSelectedDate = null;

		$scope.init = function init()
		{
			$scope.uiConfig.calendar.defaultView = $scope.calendarViewName();

			// XXX: loadScheduleTemplates seems to not be used

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
			return $scope.initialized && Juno.Common.Util.exists($scope.calendar());
		};

		$scope.hasSchedules = function hasSchedules()
		{
			return $scope.schedules.length > 0;
		};

		$scope.hasSites = function hasSites()
		{
			return $scope.sitesEnabled;
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
						$scope.schedules[i].uuid, $scope.selectedSiteName, start, end));
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

		controller.changeToSchedule = function(resourceId, view)
		{
			var scheduleOptions = $scope.getScheduleOptions();

			for(var i=0; i < scheduleOptions.length; i++)
			{
				if(scheduleOptions[i].uuid === resourceId)
				{
					$scope.selectedSchedule = scheduleOptions[i];
					break;
				}
			}

			//TODO prevent the double schedule loading calls
			$scope.onScheduleChanged();
			$scope.changeView(view);
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

		$scope.stepBack = function stepBack()
		{
			$scope.calendar().fullCalendar('prev');
		};
		$scope.stepForward = function stepForward()
		{
			$scope.calendar().fullCalendar('next');
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

		$scope.changeDate = function changeDate(date)
		{
			$scope.calendar().fullCalendar('gotoDate', date);
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
			if($scope.selectedSchedule !== null)
			{
				return $scope.selectedSchedule;
			}

			var selectedUuid = controller.providerSettings.groupNo;
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

		controller.hasPatientSelected = function hasPatientSelected(calEvent)
		{
			return Juno.Common.Util.exists(calEvent.data.demographicNo)
				&& Number(calEvent.data.demographicNo) > 0;
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

		$scope.updateEvent = function updateEvent(calEvent)
		{
			$scope.calendar().fullCalendar('updateEvent', calEvent);
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
					for(var i = 0; i < results.length; i++)
					{
						var result = results[i];

						availabilityTypes[result.code] = angular.copy(result);
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
			$scope.uiConfig.calendar.slotLabelInterval = $scope.selectedSlotLabelInterval;

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
				var preference = controller.providerSettings.period;
				if(Juno.Common.Util.exists(preference) && Juno.Common.Util.isIntegerString(preference))
				{
					timeInterval = "00:" + Juno.Common.Util.pad0(preference) + ":00";
				}
			}

			if(Juno.Common.Util.exists(timeInterval))
			{
				// only choose it if it can be found in the options list
				for(var i = 0; i < timeIntervalOptions.length; i++)
				{
					if(timeInterval === timeIntervalOptions[i].value)
					{
						selectedTimeInterval = timeIntervalOptions[i].value;
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
			var timeStr = "08:00";
			var preference = controller.providerSettings.startHour;
			if(Juno.Common.Util.exists(preference) && Juno.Common.Util.isIntegerString(preference)
				&& Number(preference) > 0 && Number(preference) < 24)
			{
				timeStr = Juno.Common.Util.pad0(preference) + ":00";
			}
			return timeStr;
		};

		$scope.getScheduleMaxTime = function getScheduleMaxTime()
		{
			var timeStr = "20:00";
			var preference = controller.providerSettings.endHour;
			if(Juno.Common.Util.exists(preference) && Juno.Common.Util.isIntegerString(preference)
				&& Number(preference) > 0 && Number(preference) < 24)
			{
				timeStr = Juno.Common.Util.pad0(preference) + ":00";
			}
			return timeStr;
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
				$scope.getScheduleMinTime(),
				$scope.getScheduleMaxTime(),
				siteName,
				$scope.timeIntervalMinutes()
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

		$scope.saveEvent = function saveEvent(editMode, calendarAppointment)
		{
			var deferred = $q.defer();

			if(editMode)
			{
				this.appointmentApi.updateAppointment(calendarAppointment).then(
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
				$scope.appointmentApi.addAppointment(calendarAppointment).then(
					function(result)
					{
						deferred.resolve(result.data);
					},
					function (result)
					{
						deferred.reject(result.data);
					}
				);
			}

			return deferred.promise;
		};

		$scope.moveEvent = function moveEvent(appointment, delta, adjustStartTime)
		{
			var deferred = $q.defer();

			var startMoment = Juno.Common.Util.getDatetimeNoTimezoneMoment(appointment.startTime);
			var endMoment = Juno.Common.Util.getDatetimeNoTimezoneMoment(appointment.endTime);

			var movedAppointment = angular.copy(appointment);

			if(adjustStartTime)
			{
				movedAppointment.startTime = Juno.Common.Util.formatMomentDateTimeNoTimezone(startMoment.add(delta.asMinutes(), 'minutes'));
			}
			movedAppointment.endTime = Juno.Common.Util.formatMomentDateTimeNoTimezone(endMoment.add(delta.asMinutes(), 'minutes'));

			$scope.saveEvent(true, movedAppointment).then(
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
		controller.moveEventSuccess = function(eventData, calEvent)
		{
			var startMoment = moment(eventData.startTime, "YYYY-MM-DDTHH:mm:ss.SSS+ZZZZ", false);
			var endMoment = moment(eventData.endTime, "YYYY-MM-DDTHH:mm:ss.SSS+ZZZZ", false);

			calEvent.start = Juno.Common.Util.formatMomentTime(
				startMoment, Juno.Common.Util.settings.datetime_no_timezone_format);
			calEvent.end = Juno.Common.Util.formatMomentTime(
				endMoment, Juno.Common.Util.settings.datetime_no_timezone_format);

			calEvent.data.startTime = calEvent.start;
			calEvent.data.endTime = calEvent.end;

			calEvent.data.providerNo = eventData.providerNo;
			calEvent.resourceId = eventData.providerNo;

			/* update the event in the main list of events.
			this gets passed to the modal and used for date collision checking*/
			for(var i=0; i < $scope.events.length; i++)
			{
				if ($scope.events[i].rendering !== "background" && $scope.events[i].data.appointmentNo === calEvent.data.appointmentNo)
				{
					$scope.events[i] = angular.copy(calEvent);
					break;
				}
			}
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

		$scope.rotateEventStatus = function rotateEventStatus(calEvent)
		{
			$scope.setCalendarLoading(true);

			var appointmentNo = calEvent.data.appointmentNo;

			$scope.appointmentApi.setNextStatus(appointmentNo).then(
				function success(response)
				{
					var newStatus = response.data.body;

					calEvent.color = $scope.eventStatuses[newStatus].color;
					calEvent.data.eventStatusCode = newStatus;

					$scope.updateEvent(calEvent);

					$scope.setCalendarLoading(false);
				},
				function failure(response)
				{

				}
			);
		};

		$scope.getBillingLink = function getBillingLink(calEvent)
		{
			var startMoment = Juno.Common.Util.getDatetimeNoTimezoneMoment(calEvent.data.startTime);

			var appointmentDate = Juno.Common.Util.formatMomentDate(startMoment);
			var startTime = Juno.Common.Util.formatMomentTime(startMoment);

			var providerNo = calEvent.resourceId;

			var referralNoParameter = "";
			if(calEvent.data.billingRdohip)
			{
				referralNoParameter = "&referral_no_1=" + encodeURIComponent(calEvent.data.billingRdohip);
			}

			return "../billing.do" +
				"?billRegion=" + encodeURIComponent(calEvent.data.billingRegion) +
				"&billForm=" + encodeURIComponent(calEvent.data.billingForm) +
				"&hotclick=" +
				"&appointment_no=" + encodeURIComponent(calEvent.data.appointmentNo) +
				"&demographic_name=" + encodeURIComponent(calEvent.data.demographicName) +
				"&status=" + encodeURIComponent(calEvent.data.eventStatusCode) +
				"&demographic_no=" + encodeURIComponent(calEvent.data.demographicNo) +
				"&providerview=" + encodeURIComponent(providerNo) +
				"&user_no=" + encodeURIComponent(calEvent.data.userProviderNo) +
				"&apptProvider_no=" + encodeURIComponent(providerNo) +
				"&appointment_date=" + encodeURIComponent(appointmentDate) +
				"&start_time=" + encodeURIComponent(startTime) +
				"&bNewForm=1" + referralNoParameter;
		};

		$scope.getEncounterLink = function getEncounterLink(calEvent)
		{
			// XXX: Perhaps link to the new encounter page?  Put in an option to choose new or old.
			var providerNo = calEvent.resourceId;

			var startMoment = Juno.Common.Util.getDatetimeNoTimezoneMoment(calEvent.data.startTime);

			var appointmentDate = Juno.Common.Util.formatMomentDate(startMoment);
			var startTime = Juno.Common.Util.formatMomentTime(startMoment);

			return "../oscarEncounter/IncomingEncounter.do" +
				"?providerNo=" + encodeURIComponent(providerNo) +
				"&appointmentNo=" + encodeURIComponent(calEvent.data.appointmentNo) +
				"&demographicNo=" + encodeURIComponent(calEvent.data.demographicNo) +
				"&curProviderNo=" + encodeURIComponent(calEvent.data.userProviderNo) +
				"&reason=" + encodeURIComponent(calEvent.data.reason) +
				"&encType=" + encodeURIComponent("face to face encounter with client") +

				"&userName=" + encodeURIComponent(calEvent.data.userFirstName + " " + calEvent.data.userLastName) +
				"&curDate=" + encodeURIComponent(Juno.Common.Util.formatMomentDate(moment())) +

				"&appointmentDate=" + encodeURIComponent(appointmentDate) +
				"&startTime=" + encodeURIComponent(startTime) +
				"&status=" + encodeURIComponent(calEvent.data.eventStatusCode) +
				"&apptProvider_no=" + encodeURIComponent(providerNo) +
				"&providerview=" + encodeURIComponent(providerNo);
		};

		$scope.getRxLink = function getRxLink(calEvent)
		{
			if (calEvent.data.demographicNo != 0)
			{
				var providerNo = calEvent.resourceId;

				return "../oscarRx/choosePatient.do" +
					"?providerNo=" + encodeURIComponent(providerNo) +
					"&demographicNo=" + encodeURIComponent(calEvent.data.demographicNo);
			}
		};


		//=========================================================================
		// Event Handlers
		//=========================================================================/

		$scope.onEventRender = function onEventRender(event, element, view)
		{
			if(event.rendering !== 'background')
			{
				let eventElement = element.find('.fc-content');
				eventElement.html(require('./view-event.html'));

				let statusElem = eventElement.find('.icon-status');
				let detailElem = eventElement.find('.event-details');

				// var eventSiteHtml = '';
				// var eventSite = $scope.sites[event.data.site];

				// if(Juno.Common.Util.exists(eventSite))
				// {
				// 	eventSiteHtml += "<span style='background-color: " + eventSite.color + "'>&nbsp;</span>"
				// }


				// var eventStatusHtml = '';
				let eventStatus = $scope.eventStatuses[event.data.eventStatusCode];



				if(Juno.Common.Util.exists(eventStatus))
				{
					statusElem.attr("title", Juno.Common.Util.escapeHtml(eventStatus.name));

					if (Juno.Common.Util.exists(eventStatus.icon)
						&& Juno.Common.Util.exists(event)
						&& Juno.Common.Util.exists(event.data))
					{
						// class matches the icon name without the extension
						statusElem.addClass("icon-status-" + eventStatus.icon.substr(0, eventStatus.icon.indexOf('.')));
					}
					else
					{
						statusElem.text(Juno.Common.Util.escapeHtml(eventStatus.displayLetter));
					}

					if(Juno.Common.Util.exists(eventStatus.sortOrder))
					{
						statusElem.addClass("rotate");
					}
				}
				else
				{
					statusElem.attr("title", "Unknown").text("?");
				}
				let eventDetails = "";
				if(event.data.doNotBook)
				{
					eventDetails = "Do Not Book";
					if(!Juno.Common.Util.isBlank(event.data.reason))
					{
						eventDetails += " (" + Juno.Common.Util.escapeHtml(event.data.reason) + ")"
					}
				}
				else if(!Juno.Common.Util.isBlank(event.data.demographicName))
				{
					eventDetails = Juno.Common.Util.escapeHtml(event.data.demographicName);
					if(!Juno.Common.Util.isBlank(event.data.reason))
					{
						eventDetails += " (" + Juno.Common.Util.escapeHtml(event.data.reason) + ")"
					}
				}
				else if(!Juno.Common.Util.isBlank(event.data.appointmentName))
				{
					eventDetails = Juno.Common.Util.escapeHtml(event.data.appointmentName);
					if(!Juno.Common.Util.isBlank(event.data.reason))
					{
						eventDetails += " (" + Juno.Common.Util.escapeHtml(event.data.reason) + ")"
					}
				}
				else if(!Juno.Common.Util.isBlank(event.data.reason))
				{
					eventDetails = Juno.Common.Util.escapeHtml(event.data.reason);
				}
				detailElem.text(eventDetails);

				if(!controller.hasPatientSelected(event))
				{
					//disable demographic specific links if there is no attached demographic;
					var linkElements = eventElement.find('.event-encounter, .event-invoice, .event-demographic, .event-rx');
					linkElements.hide();
				}
			}
			else
			{
				element.html(require('./view-backgroundEvent.html'));
				if(Juno.Common.Util.exists(event.color))
				{
					element.find(".background-event-schedulecode").css("background-color", Juno.Common.Util.escapeHtml(event.color))
				}
				if(Juno.Common.Util.exists(event.scheduleTemplateCode))
				{
					element.find(".background-event-schedulecode").text(event.scheduleTemplateCode);
				}
				if(Juno.Common.Util.exists(event.start) && event.start.minute() === 0) // on the hour
				{
					element.find(".background-event-schedulecode,.background-event-body").addClass("hour-marker");
				}
			}

		};

		$scope.onViewRender = function onViewRender(view, element)
		{
			if($scope.isInitialized() && $scope.calendar())
			{
				$scope.selectedDate = moment(Juno.Common.Util.formatMomentDate(
					moment($scope.calendar().fullCalendar('getDate'))));
				$scope.datepickerSelectedDate = Juno.Common.Util.formatMomentDate($scope.selectedDate);
			}

			// Voodoo to set the resource view column width from https://stackoverflow.com/a/39297864
			$("#ca-calendar").css('min-width',$('.fc-resource-cell').length*200);
			element.addClass('calendar-background');
		};

		$scope.onResourceRender = function onResourceRender(resourceObj, labelTds, bodyTds)
		{
			labelTds.html(require('./view-columnControl.html'));

			labelTds.find(".hdr-label").text(resourceObj.display_name);

			// append data to the root element so it can be accessed by click events
			labelTds.find(".column-ctl-root").attr("data-resourceId", resourceObj.id);
			labelTds.on('click', $scope.onHeaderClick);
		};

		$scope.afterRender = function afterRender()
		{
			// Voodoo to set the resource view column width from https://stackoverflow.com/a/39297864
			$('.fc-agendaDay-button').click(function()
			{
				$("#schedule_container").css('min-width',$('.fc-resource-cell').length*200);
			});
		};

		$scope.onHeaderClick = function onHeaderClick(jsEvent)
		{
			var resourceId = jsEvent.currentTarget.dataset.resourceId;
			if($(jsEvent.target).is(".onclick-daysheet"))
			{
				controller.openDaysheet(resourceId);
			}
			else if($(jsEvent.target).is(".onclick-search"))
			{
				console.info("onclick-search clicked");
			}
			else if($(jsEvent.target).is(".onclick-week-view"))
			{
				console.info("onclick-week-view clicked");
				controller.changeToSchedule(resourceId, 'agendaWeek');
			}
			else if($(jsEvent.target).is(".onclick-month-view"))
			{
				console.info("onclick-month-view clicked");
				controller.changeToSchedule(resourceId, 'month');
			}
		};

		$scope.onEventClick = function onEventClick(calEvent, jsEvent, view)
		{
			if($(jsEvent.target).is(".event-status.rotate:not(.disabled)"))
			{
				$scope.rotateEventStatus(calEvent);
			}
			else if($(jsEvent.target).is(".onclick-event-encounter:not(.disabled)"))
			{
				window.open($scope.getEncounterLink(calEvent));
			}
			else if($(jsEvent.target).is(".onclick-event-invoice:not(.disabled)"))
			{
				window.open($scope.getBillingLink(calEvent));
			}
			else if($(jsEvent.target).is(".onclick-event-demographic:not(.disabled)"))
			{
				$scope.openPatientDemographic(calEvent);
			}
			else if($(jsEvent.target).is(".onclick-event-rx:not(.disabled)"))
			{
				window.open($scope.getRxLink(calEvent));
			}
			else
			{
				$scope.openEditEventDialog(calEvent);
			}
		};

		$scope.openCreateEventDialog = function openCreateEventDialog(
			start, end, jsEvent, view, resource)
		{
			// XXX: share as much code as possible with edit event
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
					defaultEventStatus: 't', //defaultEventStatus,
					startTime: start,
					endTime: end,
					timeInterval: $scope.timeIntervalMinutes(),
					scheduleTemplates: $scope.scheduleTemplates,
					availabilityTypes: $scope.availabilityTypes,
					sites: $scope.sites,
					events: $scope.events,
					eventData: {}
				};

				$scope.dialog = $uibModal.open({
					animation: false,
					backdrop: 'static',
					controller: 'Schedule.EventController as eventController',
					templateUrl: 'src/schedule/event.jsp',
					resolve: {
						type: [function() { return 'create_edit_event' }],
						label: [function() { return 'Appointment' }],
						parentScope: [function() { return $scope }],
						data: [function() { return data }],
						editMode: [function() { return false }],
						keyBinding: [function() {return {bindKeyGlobal: function(){}}}],
						focus: [function() {return focusService}],
					},
					windowClass: "juno-modal",
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
			if (calEvent.data.demographicNo != 0)
			{
				var params = {
					demographicNo: calEvent.data.demographicNo
				};

				if (angular.isDefined(calEvent.data.appointmentNo))
				{
					params.appointmentNo = calEvent.data.appointmentNo;
					params.encType = "face to face encounter with client";
				}

				$state.go('record.summary', params);
			}
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

			var scheduleUuid = calEvent.resourceId;
			var displayName = calEvent.data.demographicName;

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

				var data = {
					schedule: modalSchedule,
					defaultEventStatus: null, //defaultEventStatus,
					startTime: calEvent.start,
					endTime: calEvent.end,
					timeInterval: $scope.timeIntervalMinutes(),
					availabilityTypes: $scope.availabilityTypes,
					sites: $scope.sites,
					events: $scope.events,
					eventData: calEvent.data
				};

				$scope.dialog = $uibModal.open({
					animation: false,
					backdrop: 'static',
					controller: 'Schedule.EventController as eventController',
					templateUrl: 'src/schedule/event.jsp',
					resolve: {
						type: [function() { return 'create_edit_event' }],
						label: [function() { return 'Appointment' }],
						parentScope: [function() { return $scope }],
						data: [function() { return data }],
						editMode: [function() { return true }],
						keyBinding: [function() {return {bindKeyGlobal: function(){}}}],
						focus: [function() {return focusService}],
					},
					windowClass: "juno-modal",
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

			var appointment = angular.copy(calEvent.data);
			appointment.providerNo = calEvent.resourceId;

			$scope.moveEvent(appointment, delta, true).then(
				function success(eventData)
				{
					controller.moveEventSuccess(eventData, calEvent);
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
			$scope.setCalendarLoading(true);

			var appointment = angular.copy(calEvent.data);
			appointment.providerNo = calEvent.resourceId;

			$scope.moveEvent(appointment, delta, false).then(
				function success(eventData)
				{
					controller.moveEventSuccess(eventData, calEvent);
					$scope.setCalendarLoading(false);

				}, function error(errors)
				{
					console.log('failed to resize event', errors);

					// revert on fail
					revertFunc();
					$scope.setCalendarLoading(false);
				}
			);
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
			$scope.uiConfig.calendar.slotLabelInterval = $scope.selectedSlotLabelInterval;

			// ensure the selected date doesn't change on events refresh
			$scope.uiConfig.calendar.defaultDate = $scope.calendar().fullCalendar('getDate');
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

			scheduleService.getScheduleGroups().then(
				function success(results)
				{
					for(var i = 0; i < results.length; i++)
					{
						var scheduleData = results[i];

						results[i].uuid = results[i].identifier;

						results[i].label = results[i].name;
						results[i].value = results[i].identifier;

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

			controller.loadSitesEnabled().then(
				function success()
				{
					if($scope.sitesEnabled)
					{
						$scope.loadSites().then(
							function success(results)
							{
								$scope.sites = {};
								$scope.siteOptions = [];
								if (angular.isArray(results) && results.length > 0)
								{
									// Fill up lookup table
									for (var i = 0; i < results.length; i++)
									{
										$scope.sites[results[i].name] = results[i];
									}

									// Create the dropdown options
									$scope.siteOptions = [
										{
											uuid: null,
											value: null,
											label: "All Sites",
										}
									];

									$scope.siteOptions = $scope.siteOptions.concat(results);
								}

								deferred.resolve(results);
							});
					}
					else
					{
						deferred.resolve();
					}
				}
			);
			return deferred.promise;
		};

		controller.loadSitesEnabled = function()
		{
			var deferred = $q.defer();

			scheduleService.getSitesEnabled().then(
				function success(enabled)
				{
					$scope.sitesEnabled = enabled;
					deferred.resolve(enabled);
				}
			);

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
								value: results[i].name,
								label: results[i].name,
								color: results[i].bgColor,
							});
						}
					}
					deferred.resolve(out);
				}
			);

			return deferred.promise;
		};

		controller.openDaysheet = function(resourceId)
		{
			var formattedDate = Juno.Common.Util.formatMomentDate($scope.selectedDate);
			var win = window.open('../report/reportdaysheet.jsp' +
				'?dsmode=all' +
				'&provider_no=' + encodeURIComponent(resourceId) +
				'&sdate=' + encodeURIComponent(formattedDate) +
				'&edate=' + encodeURIComponent(formattedDate),
				'daysheet', 'height=700,width=1024,scrollbars=1');
			win.focus();
		};


		//=========================================================================
		// Watches
		//=========================================================================/

		$scope.$watch('scheduleService.selectedDate', function(newValue, oldValue)
		{
			// avoid running first time this fires during initialization
			if(newValue !== oldValue)
			{
				$scope.changeDate(newValue);
			}
		});

		$scope.$watch('datepickerSelectedDate', function(newValue, oldValue)
		{
			if($scope.isInitialized())
			{
				var momentDate = Juno.Common.Util.getDateMoment(newValue);
				$scope.changeDate(momentDate);
			}
		});

		$scope.$watch('selectedSiteName', function(newValue, oldValue)
		{
			if($scope.isInitialized())
			{
				$scope.onSiteChanged();
			}
		});
		$scope.$watch('selectedTimeInterval', function(newValue, oldValue)
		{
			if($scope.isInitialized())
			{
				$scope.onTimeIntervalChanged();
			}
		});


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
				height: 'auto', //$scope.get_schedule_height(),
				nowIndicator: true,
				header: false,

				allDaySlot: false,

				defaultView: null,
				defaultDate: $scope.defaultDate,
				slotDuration: $scope.selectedTimeInterval,
				snapDuration: '00:05:00',
				slotLabelInterval: $scope.selectedSlotLabelInterval,
				slotLabelFormat: 'h:mma',

				loading: $scope.setCalendarLoading,

				selectable: true,
				select: $scope.openCreateEventDialog,
				eventClick: $scope.onEventClick,
				eventRender: $scope.onEventRender,
				viewRender: $scope.onViewRender,
				resourceRender: $scope.onResourceRender,
				navLinkDayClick: $scope.onHeaderClick,
				navLinkWeekClick: $scope.onHeaderClick,
				eventAfterAllRender: $scope.afterRender,

				editable: true,
				eventDrop: $scope.onEventDrop,
				eventResize: $scope.onEventResize,
				schedulerLicenseKey: "GPL-My-Project-Is-Open-Source",
			}
		};

		$scope.init();
	}
]);