import {AppointmentApi} from '../../generated/api/AppointmentApi';
import {ScheduleApi} from '../../generated/api/ScheduleApi';
import {SitesApi} from '../../generated/api/SitesApi';
import {ProviderPreferenceApi} from '../../generated/api/ProviderPreferenceApi';

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
	'providersService',
	'formService',
	'focusService',
	'securityService',
	'scheduleService',
	'uiCalendarConfig',
	'errorsService',
	'globalStateService',

	function (
		$scope,
		$stateParams,
		$q,
		$http,
		$httpParamSerializer,
		$uibModal,
		$state,
		loadedSettings,
		providerService,
		providersService,
		formService,
		focusService,
		securityService,
		scheduleService,
		uiCalendarConfig,
		messagesFactory,
		globalStateService
	)
	{
		let controller = this;

		// XXX: put this address somewhere else
		$scope.appointmentApi = new AppointmentApi($http, $httpParamSerializer,
			'../ws/rs');

		$scope.scheduleApi = new ScheduleApi($http, $httpParamSerializer,
			'../ws/rs');

		$scope.sitesApi = new SitesApi($http, $httpParamSerializer,
			'../ws/rs');

		$scope.providerPreferenceApi = new ProviderPreferenceApi($http, $httpParamSerializer,
			'../ws/rs');

		controller.providerSettings = loadedSettings;
		controller.calendarMinColumnWidth = 250;

		//=========================================================================
		// Local scope variables
		//=========================================================================/

		$scope.calendarName = 'cpCalendar';
		$scope.initialized = false;
		$scope.calendarLoading = false;
		$scope.customLoading = false;
		$scope.displayMessages = messagesFactory.factory();

		$scope.uiConfig = {};
		$scope.uiConfigApplied = {
			calendar: {}
		};
		$scope.eventSources = [];

		// cpCalendar control object.  The cpCalendar directive puts its control API methods in this object.
		$scope.cpCalendarControl = {};

		// Parameters from directive controller
		$scope.scheduleOptions = [];
		$scope.resourceOptionHash = {};
		$scope.selectedResources = false;
		$scope.showNoResources = false;

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
		$scope.scheduleTimeInterval = null;

		$scope.selectedSlotLabelInterval = {hours: 1};
		$scope.availabilityTypes = {};
		$scope.events = [];
		$scope.scheduleTemplates = {};
		$scope.sites = {};
		$scope.siteOptions = [];
		$scope.sitesEnabled = false;
		$scope.selectedSiteName = null;

		$scope.openingDialog = false;
		$scope.dialog = null;

		// Global State parameters
		controller.calendarViewEnum = Object.freeze({
			agendaDay: 'agendaDay',
			agendaWeek: 'agendaWeek',
			agendaMonth: 'month',
		});
		$scope.calendarViewDefault = controller.calendarViewEnum.agendaDay;
		$scope.calendarViewName = null;

		controller.scheduleViewEnum = Object.freeze({
			schedule: 'schedule',
			all: 'all',
		});
		controller.selectedScheduleView = controller.scheduleViewEnum.all;
		controller.scheduleTypeEnum = Object.freeze({
			group: 'GROUP',
			provider: 'PROVIDER',
		});
		$scope.selectedSchedule = null;

		controller.refreshSettings = {
			timerVariable: null,
			defaultAutoRefreshMinutes: 3,
			preferredAutoRefreshMinutes: null
		};

		$scope.defaultDate = globalStateService.global_settings.schedule.date_selected;
		$scope.datepickerSelectedDate = null;

		controller.formLinks = {
			enabled: true,
			maxLength: 2,
			formNameMap: {},
			eFormNameMap: {},
			quickLinkMap: {},
		};

		$scope.init = function init()
		{
			$scope.uiConfig.calendar.defaultView = $scope.getCalendarViewName();

			$scope.loadAvailabilityTypes().then(function ()
			{
				scheduleService.loadEventStatuses().then(function ()
				{
					$scope.loadScheduleOptions().then(function ()
					{
						controller.loadResourceHash().then(function ()
						{
							$scope.loadSiteOptions().then(function ()
							{
								controller.loadExtraLinkData().then(function ()
								{
									$scope.loadDefaultSelections();
									$scope.setEventSources();

									controller.initEventsAutoRefresh();

									$scope.applyUiConfig($scope.uiConfig);

									controller.loadWatches();
									$scope.initialized = true;
								});
							});
						});
					});
				});
			});
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
			return $scope.siteOptions;
		};

		$scope.getCalendarViewName = function calendarViewName()
		{
			var viewName = $scope.calendarViewName;

			if (!Juno.Common.Util.exists(viewName))
			{
				viewName = globalStateService.global_settings.schedule.view_selected;
				if (!Juno.Common.Util.exists(viewName))
				{
					viewName = $scope.calendarViewDefault;
				}
				return viewName;
			}

			return viewName;
		};

		$scope.calendarEvents = function calendarEvents(start, end, timezone, callback)
		{
			$scope.loadScheduleEvents($scope.selectedSchedule, $scope.selectedSiteName, start, end).then(
				function success()
				{
					try
					{
						callback($scope.events);
					}
					catch (err)
					{
						// the callback throws an error on first load, ignore
					}
				}
			);
		};

		$scope.showTimeIntervals = function showTimeIntervals()
		{
			return $scope.getCalendarViewName() !== controller.calendarViewEnum.agendaMonth;
		};

		controller.changeToSchedule = function (resourceId, view)
		{
			$scope.selectedSchedule = null;
			var scheduleOptions = $scope.getScheduleOptions();

			for (var i = 0; i < scheduleOptions.length; i++)
			{
				if (scheduleOptions[i].uuid === resourceId)
				{
					$scope.selectedSchedule = scheduleOptions[i];
					break;
				}
			}

			// set the selected schedule to an object not in the options list.
			// this will show as an empty option when selected, but will be removed once de-selected.
			// this allows selection of inactive providers schedules
			if($scope.selectedSchedule === null)
			{
				var scheduleData = $scope.resourceOptionHash[resourceId];

				scheduleData.label = scheduleData.title;
				scheduleData.uuid = scheduleData.id;
				scheduleData.value = scheduleData.id;
				scheduleData.identifier = scheduleData.id;
				scheduleData.identifierType = controller.scheduleTypeEnum.provider;
				scheduleData.providerNos = [resourceId];

				$scope.selectedSchedule = scheduleData;
			}

			$scope.calendarViewName = view;
			$scope.onScheduleChanged();
		};

		/* changes the calender view
		view must be one of agendaDay, agendaWeek, agendaMonth enum values*/
		$scope.changeCalendarView = function changeCalendarView(view)
		{
			if ($scope.calendarViewName !== view)
			{
				// save the new view to global state so it gets picked up in rendering
				$scope.calendarViewName = view;
				globalStateService.global_settings.schedule.view_selected = $scope.calendarViewName;

				$scope.calendar().fullCalendar('changeView', $scope.getCalendarViewName());
			}
		};

		/* chances the schedule view type
		* must be one of the all or schedule enum values*/
		controller.changeScheduleView = function (view)
		{
			if (controller.selectedScheduleView !== view)
			{
				$scope.providerPreferenceApi.updateProviderSetting(securityService.getUser().providerNo, "schedule.view", view)
					.then(
						function success()
						{
							controller.selectedScheduleView = view;
							$scope.refetchEvents();
						},
						function failure()
						{
							$scope.displayMessages.add_standard_error("Failed to update provider setting");
						}
					);
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

		$scope.refetchEvents = function refetchEvents()
		{
			$scope.calendar().fullCalendar('refetchEvents');
		};

		$scope.$on('schedule:refreshEvents', function (event, data)
		{
			$scope.refetchEvents();
		});

		$scope.changeDate = function changeDate(date)
		{
			globalStateService.global_settings.schedule.date_selected = date;
			$scope.uiConfig.calendar.defaultDate = date;
			$scope.calendar().fullCalendar('gotoDate', date);
		};

		$scope.isAgendaView = function isAgendaView()
		{
			return ($scope.uiConfigApplied.calendar.resources === null || $scope.uiConfigApplied.calendar.resources === false)
		};
		$scope.isResourceView = function isResourceView()
		{
			return ($scope.uiConfigApplied.calendar.resources !== null && $scope.uiConfigApplied.calendar.resources !== false)
		};
		$scope.isScheduleView = function ()
		{
			return (controller.selectedScheduleView === controller.scheduleViewEnum.schedule);
		};
		$scope.isAgendaDayView = function()
		{
			return ( $scope.isAgendaView() && $scope.getCalendarViewName() === controller.calendarViewEnum.agendaDay)
		};
		$scope.isAgendaWeekView = function()
		{
			return ( $scope.isAgendaView() && $scope.getCalendarViewName() === controller.calendarViewEnum.agendaWeek)
		};
		$scope.isAgendaMonthView = function()
		{
			return ( $scope.isAgendaView() && $scope.getCalendarViewName() === controller.calendarViewEnum.agendaMonth)
		};


		//=========================================================================
		// Private methods
		//=========================================================================/

		$scope.getSelectedSchedule = function getSelectedSchedule(scheduleOptions)
		{
			// priority: last used from global state, then preference setting,
			// then default (first in the list)
			if ($scope.selectedSchedule !== null)
			{
				return $scope.selectedSchedule;
			}

			var selectedUuid = controller.providerSettings.groupNo;
			if (Juno.Common.Util.exists(selectedUuid))
			{
				// only choose it if it can be found in the options list
				for (var i = 0; i < scheduleOptions.length; i++)
				{
					if (selectedUuid === scheduleOptions[i].uuid)
					{
						return scheduleOptions[i];
					}
				}
			}

			if (scheduleOptions.length > 0)
			{
				// select the first schedule in the list by default
				return scheduleOptions[0];
			}

			return null;
		};

		controller.getSelectedScheduleView = function ()
		{
			var preference = controller.providerSettings.viewSelected;
			if (Juno.Common.Util.exists(preference) && (preference === controller.scheduleViewEnum.schedule))
			{
				return controller.scheduleViewEnum.schedule;
			}
			return controller.scheduleViewEnum.all;
		};

		controller.getSelectedSite = function ()
		{
			var preference = controller.providerSettings.siteSelected;
			if (Juno.Common.Util.exists(preference))
			{
				return preference;
			}
			return null;
		};

		controller.hasPatientSelected = function hasPatientSelected(calEvent)
		{
			return Juno.Common.Util.exists(calEvent.data.demographicNo)
				&& Number(calEvent.data.demographicNo) > 0;
		};

		$scope.setCalendarLoading = function setCalendarLoading(isLoading)
		{
			$scope.calendarLoading = isLoading;
		};
		$scope.setCustomLoading = function setCustomLoading(isLoading)
		{
			$scope.customLoading = isLoading;
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

		$scope.getSelectedTimeInterval = function getSelectedTimeInterval(
			timeIntervalOptions, defaultTimeInterval)
		{
			// priority: last used from global state, then preference setting,
			// then default

			var selectedTimeInterval = null;

			var timeInterval = $scope.scheduleTimeInterval;
			if (timeInterval === null)
			{
				var preference = controller.providerSettings.period;
				if (Juno.Common.Util.exists(preference) && Juno.Common.Util.isIntegerString(preference))
				{
					timeInterval = "00:" + Juno.Common.Util.pad0(preference) + ":00";
				}
			}

			if (Juno.Common.Util.exists(timeInterval))
			{
				// only choose it if it can be found in the options list
				for (var i = 0; i < timeIntervalOptions.length; i++)
				{
					if (timeInterval === timeIntervalOptions[i].value)
					{
						selectedTimeInterval = timeIntervalOptions[i].value;
						break;
					}
				}
			}

			if (selectedTimeInterval === null)
			{
				return defaultTimeInterval;
			}

			return selectedTimeInterval;
		};

		$scope.getScheduleMinTime = function getScheduleMinTime()
		{
			var timeStr = "08:00";
			var preference = controller.providerSettings.startHour;
			if (Juno.Common.Util.exists(preference) && Juno.Common.Util.isIntegerString(preference)
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
			if (Juno.Common.Util.exists(preference) && Juno.Common.Util.isIntegerString(preference)
				&& Number(preference) > 0 && Number(preference) < 24)
			{
				timeStr = Juno.Common.Util.pad0(preference) + ":00";
			}
			return timeStr;
		};

		$scope.loadScheduleEvents = function loadScheduleEvents(selectedSchedule, siteName, start, end)
		{
			var deferred = $q.defer();

			$scope.setCustomLoading(true);
			if($scope.isAgendaWeekView())
			{
				// full calendar likes to ask for a subset of the week days when hidden days are set,
				// but we need to always ask for the whole week here so that we know what days to hide
				end = angular.copy(start).endOf('week');
				start = angular.copy(start).startOf('week');
			}

			// Get date strings to pass to the backend.  The calendar provides datetime that describe
			// and inclusive start time and exclusive end time, so one second is removed from
			// the end time to convert to the correct date.
			var startDateString = start.format(Juno.Common.Util.settings.date_format);
			var endDateString = end.subtract(1, 'seconds').format(Juno.Common.Util.settings.date_format);

			$scope.scheduleApi.getCalendarSchedule(
				selectedSchedule.identifier,
				selectedSchedule.identifierType,
				$scope.isScheduleView(),
				startDateString,
				endDateString,
				$scope.getScheduleMinTime(),
				$scope.getScheduleMaxTime(),
				siteName,
				$scope.timeIntervalMinutes()
			).then(
				function (results)
				{
					// console.info('================== load events ===================');
					var hasVisibleSchedules = results.data.body.visibleSchedules;
					$scope.showNoResources = !hasVisibleSchedules;
					$scope.uiConfig.calendar.hiddenDays = [];

					if (selectedSchedule.identifierType === controller.scheduleTypeEnum.group)
					{
						var providerNos = results.data.body.providerIdList;

						// Set the calendar to resource mode.  All of these values need to be set.
						$scope.selectedResources = controller.buildSelectedResources(providerNos);
						$scope.uiConfig.calendar.resources = $scope.selectedResources;

						// always show day view in resource mode
						$scope.uiConfig.calendar.defaultView = controller.calendarViewEnum.agendaDay;
						$scope.calendarViewName = controller.calendarViewEnum.agendaDay;
					}
					else
					{
						// Reset everything to single-provider view mode
						$scope.uiConfig.calendar.defaultView = $scope.getCalendarViewName();
						$scope.calendarViewName = $scope.getCalendarViewName();
						$scope.uiConfig.calendar.resources = false;

						var hiddenDays = results.data.body.hiddenDaysList;
						// only hide days in week/month views. limiting day view causes re-fetch errors when changing to week view
						// hiding all days causes an error in fullCalendar, rely on the no schedules screen to hide it
						if (hiddenDays.length !== 7 && ($scope.isAgendaWeekView() || $scope.isAgendaMonthView()))
						{
							$scope.uiConfig.calendar.hiddenDays = hiddenDays; // hide days without schedules
						}
					}
					$scope.applyUiConfig($scope.uiConfig);
					$scope.events = results.data.body.eventList;

					$scope.setCustomLoading(false);
					deferred.resolve(results.data.body);
				},
				function failure(results)
				{
					$scope.displayMessages.add_standard_error("Failed to load events");
					deferred.reject(results.data.body);
				}
			);

			return deferred.promise;
		};

		// TODO: change this, perhaps?  It is getting the resource details from the groups
		controller.buildSelectedResources = function buildSelectedResources(providerNos)
		{
			var selectedResources = [];

			for (var i = 0; i < providerNos.length; i++)
			{
				var resourceOption = $scope.resourceOptionHash[providerNos[i]];
				if (resourceOption)
				{
					selectedResources.push(resourceOption);
				}
				else
				{
					console.warn('Attempt to load invalid resource id: ' + providerNos[i]);
				}
			}

			return selectedResources;
		};
		$scope.setCalendarResources = function setCalendarResources(resourceView)
		{
			if (resourceView)
			{
				$scope.uiConfig.calendar.resources = $scope.selectedResources;
			}
			else
			{
				$scope.uiConfig.calendar.resources = false;
			}

			$scope.applyUiConfig($scope.uiConfig);
		};

		$scope.saveEvent = function saveEvent(editMode, calendarAppointment)
		{
			var deferred = $q.defer();

			if (editMode)
			{
				this.appointmentApi.updateAppointment(calendarAppointment).then(
					function success(result)
					{
						deferred.resolve(result.data);
					},
					function failure(result)
					{
						$scope.displayMessages.add_standard_error("Failed to update appointment");
						deferred.reject(result.data);
					}
				);
			}
			else
			{
				$scope.appointmentApi.addAppointment(calendarAppointment).then(
					function success(result)
					{
						deferred.resolve(result.data);
					},
					function failure(result)
					{
						$scope.displayMessages.add_standard_error("Failed to add appointment");
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

			if (adjustStartTime)
			{
				movedAppointment.startTime = Juno.Common.Util.formatMomentDateTimeNoTimezone(startMoment.add(delta.asMinutes(), 'minutes'));
			}
			movedAppointment.endTime = Juno.Common.Util.formatMomentDateTimeNoTimezone(endMoment.add(delta.asMinutes(), 'minutes'));

			$scope.saveEvent(true, movedAppointment).then(
				function success(data)
				{
					deferred.resolve(data.body);
				},
				function failure(data)
				{
					deferred.reject(data.body);
				}
			);


			return deferred.promise;
		};
		controller.moveEventSuccess = function (eventData, calEvent)
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
			for (var i = 0; i < $scope.events.length; i++)
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

			if (status === 'SUCCESS')
			{
				return true;
			}

			var errorMessage = ((results || {}).error || {}).message;
			var validationErrorArray = ((results || {}).error || {}).validationErrors;

			if (Array.isArray(validationErrorArray))
			{
				displayErrors.add_standard_error(errorMessage);
				//for(var error in validationErrorArray)
				for (var i = 0; i < validationErrorArray.length; i++)
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
				function success(result)
				{
					deferred.resolve(result.data);

				},
				function failure(result)
				{
					$scope.displayMessages.add_standard_error("Failed to delete appointment");
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

					calEvent.color = scheduleService.getStatusByCode(newStatus).color;
					calEvent.data.eventStatusCode = newStatus;

					$scope.updateEvent(calEvent);

					$scope.setCalendarLoading(false);
				},
				function failure()
				{
					$scope.displayMessages.add_standard_error("Failed to update status");
				}
			);
		};

		//=========================================================================
		// Event Handlers
		//=========================================================================/

		controller.buildEventLink = function buildEventLink($rootElem, map, className)
		{
			for(var id in map)
			{
				var displayName = map[id] || "NA";
				var shortName = Juno.Common.Util.trimToLength(displayName, controller.formLinks.maxLength);

				$rootElem.append($("<div>", {
					class: "event-item event-blk-label event-form-link",
				}).append($("<a>", {
					class: "event-label " + className,
					text: shortName,
					title: displayName,
					'data-id': id
				})));
			}
		};

		$scope.onEventRender = function onEventRender(event, element, view)
		{
			// appointment event type
			if (event.rendering !== 'background')
			{
				let eventElement = element.find('.fc-content');
				eventElement.html(require('./view-event.html'));

				let statusElem = eventElement.find('.icon-status');
				let labelElem = eventElement.find('.event-label');
				let detailElem = eventElement.find('.event-details');
				let selfBookElem = eventElement.find('.self-book-indicator');
				// var eventSite = $scope.sites[event.data.site];

				/* set up status icon + color/hover etc. */
				let eventStatus = scheduleService.eventStatuses[event.data.eventStatusCode];
				if (Juno.Common.Util.exists(eventStatus))
				{
					statusElem.attr("title", eventStatus.name);

					if (Juno.Common.Util.exists(eventStatus.icon))
					{
						// class matches the icon name without the extension
						statusElem.addClass("icon-" + eventStatus.icon.substr(0, eventStatus.icon.indexOf('.')));
					}
					else
					{
						statusElem.text(eventStatus.displayLetter);
					}

					if (Juno.Common.Util.exists(eventStatus.sortOrder))
					{
						statusElem.addClass("rotate");
					}
				}
				else
				{
					statusElem.attr("title", "Unknown").text("?");
				}

				/* set up event display text (name, reason, notes, etc.)*/
				let eventName = "";
				let eventReason = "";
				let eventNotes = "";

				if (event.data.doNotBook)
				{
					eventName = "Do Not Book";
				}
				else if (!Juno.Common.Util.isBlank(event.data.demographicName))
				{
					eventName = event.data.demographicName;
				}
				else if (!Juno.Common.Util.isBlank(event.data.appointmentName))
				{
					eventName = event.data.appointmentName;
				}

				if (!Juno.Common.Util.isBlank(event.data.reason))
				{
					eventReason = event.data.reason;
				}
				if (!Juno.Common.Util.isBlank(event.data.notes))
				{
					eventNotes = event.data.notes;
				}

				var detailText = "";
				if (!Juno.Common.Util.isBlank(eventReason))
				{
					detailText += "(" + eventReason + ")";
				}

				let eventTitle = eventName + "\n" +
					"Reason: " + eventReason + "\n" +
					"Notes: " + eventNotes;
				eventElement.attr("title", eventTitle);

				//disable demographic specific links if there is no attached demographic;
				if (!controller.hasPatientSelected(event))
				{
					var linkElements = eventElement.find('.event-encounter, .event-invoice, .event-demographic, .event-rx');
					linkElements.hide();
				}

				// mark self booked appointments
				if(Juno.Common.Util.exists(event.data.tagSelfBooked) && event.data.tagSelfBooked)
				{
					selfBookElem.addClass('visible');
					selfBookElem.attr("title", "Self Booked");
					detailElem.parent().addClass('show-self-booked');
				}

				var maxNameLengthProp = controller.providerSettings.patientNameLength;
				if (Juno.Common.Util.exists(maxNameLengthProp)
					&& Juno.Common.Util.isIntegerString(maxNameLengthProp)
					&& Number(maxNameLengthProp) > 0)
				{
					eventName = Juno.Common.Util.trimToLength(eventName, Number(maxNameLengthProp));
				}

				labelElem.text(eventName);
				detailElem.append(detailText);

				/* generate html links for forms/eForms based off the provider settings */
				if(controller.formLinks.enabled && controller.hasPatientSelected(event))
				{
					let formContainerElem = eventElement.find('.inline-flex');
					/* generate form links */
					controller.buildEventLink(formContainerElem, controller.formLinks.formNameMap, "onclick-open-form");
					/* generate eForm links */
					controller.buildEventLink(formContainerElem, controller.formLinks.eFormNameMap, "onclick-open-eform");
					/* generate quick links */
					controller.buildEventLink(formContainerElem, controller.formLinks.quickLinkMap, "onclick-open-quicklink");
				}

			}
			else //background events (appointment slots)
			{
				element.html(require('./view-backgroundEvent.html'));
				var scheduleCodeElement = element.find(".background-event-schedulecode");

				if (Juno.Common.Util.exists(event.color))
				{
					scheduleCodeElement.css("background-color", event.color)
				}

				var availabilityType = event.availabilityType;
				if(Juno.Common.Util.exists(availabilityType))
				{
					if (Juno.Common.Util.exists(availabilityType.systemCode))
					{
						if(availabilityType.systemCodeVisible)
						{
							scheduleCodeElement.text(availabilityType.systemCode);
							// scheduleCodeElement.addClass("code-visible");
						}

						if (Juno.Common.Util.exists(availabilityType.name))
						{
							scheduleCodeElement.attr("title", availabilityType.name);
						}
					}
				}
				if (Juno.Common.Util.exists(event.start) && event.start.minute() === 0) // on the hour
				{
					element.find(".background-event-schedulecode,.background-event-body").addClass("hour-marker");
				}
			}

		};

		$scope.onViewRender = function onViewRender(view, element)
		{
			if ($scope.isInitialized())
			{
				$scope.datepickerSelectedDate = Juno.Common.Util.formatMomentDate(moment($scope.calendar().fullCalendar('getDate')));
			}

			// Voodoo to set the resource view column width from https://stackoverflow.com/a/39297864
			$("#ca-calendar").css('min-width', $('.fc-resource-cell,.fc-day-header').length * controller.calendarMinColumnWidth);
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
			$('.fc-agendaDay-button').click(function ()
			{
				$("#schedule_container").css('min-width', $('.fc-resource-cell,.fc-day-header').length * controller.calendarMinColumnWidth);
			});
		};

		$scope.onHeaderClick = function onHeaderClick(jsEvent)
		{
			var resourceId = jsEvent.currentTarget.dataset.resourceId;
			if ($(jsEvent.target).is(".onclick-daysheet"))
			{
				controller.openDaysheet(resourceId);
			}
			else if ($(jsEvent.target).is(".onclick-search"))
			{
				console.info("onclick-search clicked");
			}
			else if ($(jsEvent.target).is(".onclick-day-view"))
			{
				console.info("onclick-day-view clicked");
				controller.changeToSchedule(resourceId, controller.calendarViewEnum.agendaDay);
			}
			else if ($(jsEvent.target).is(".onclick-week-view"))
			{
				console.info("onclick-week-view clicked");
				controller.changeToSchedule(resourceId, controller.calendarViewEnum.agendaWeek);
			}
			else if ($(jsEvent.target).is(".onclick-month-view"))
			{
				console.info("onclick-month-view clicked");
				controller.changeToSchedule(resourceId, controller.calendarViewEnum.agendaMonth);
			}
		};

		$scope.onEventClick = function onEventClick(calEvent, jsEvent, view)
		{
			var $target = $(jsEvent.target);
			if ($target.is(".onclick-event-status:not(.disabled)"))
			{
				$scope.rotateEventStatus(calEvent);
			}
			else if ($target.is(".onclick-event-encounter:not(.disabled)"))
			{
				controller.openEncounterPage(calEvent);
			}
			else if ($target.is(".onclick-event-invoice:not(.disabled)"))
			{
				controller.openBillingPage(calEvent);
			}
			else if ($target.is(".onclick-event-demographic:not(.disabled)"))
			{
				controller.openMasterRecord(calEvent);
			}
			else if ($target.is(".onclick-event-rx:not(.disabled)"))
			{
				controller.openRxPage(calEvent);
			}
			else if ($target.is(".onclick-open-form"))
			{
				controller.openFormLink($target.attr('data-id'), calEvent.data.demographicNo, calEvent.data.appointmentNo);
			}
			else if ($target.is(".onclick-open-eform"))
			{
				controller.openEFormLink($target.attr('data-id'), calEvent.data.demographicNo, calEvent.data.appointmentNo);
			}
			else if ($target.is(".onclick-open-quicklink"))
			{
				controller.openQuickLink($target.attr('data-id'), calEvent.data.demographicNo, calEvent.data.appointmentNo);
			}
			else
			{
				$scope.openEditEventDialog(calEvent);
			}
		};

		controller.openEncounterPage = function openEncounterPage(calEvent)
		{
			if (calEvent.data.demographicNo !== 0)
			{
				var startMoment = Juno.Common.Util.getDatetimeNoTimezoneMoment(calEvent.data.startTime);
				var params = {
					providerNo: calEvent.resourceId,
					curProviderNo: calEvent.data.userProviderNo,
					demographicNo: calEvent.data.demographicNo,
					userName: calEvent.data.userFirstName + " " + calEvent.data.userLastName,
					reason: calEvent.data.reason,
					curDate: Juno.Common.Util.formatMomentDate(moment()),
					providerview: calEvent.resourceId,

					appointmentNo: calEvent.data.appointmentNo,
					appointmentDate: Juno.Common.Util.formatMomentDate(startMoment),
					startTime: Juno.Common.Util.formatMomentTime(startMoment),
					status: calEvent.data.eventStatusCode,
					apptProvider_no: calEvent.resourceId,
					encType: "face to face encounter with client",
				};
				window.open(scheduleService.getEncounterLink(params));
			}
		};
		controller.openBillingPage = function openBillingPage(calEvent)
		{
			if (calEvent.data.demographicNo !== 0)
			{
				var startMoment = Juno.Common.Util.getDatetimeNoTimezoneMoment(calEvent.data.startTime);

				var params = {
					demographic_no: calEvent.data.demographicNo,
					demographic_name: calEvent.data.demographicName,
					providerNo: calEvent.resourceId,
					providerview: calEvent.resourceId,
					user_no: calEvent.data.userProviderNo,

					billRegion: calEvent.data.billingRegion,
					billForm: calEvent.data.billingForm,
					hotclick: "",
					bNewForm: 1,

					apptProvider_no: calEvent.resourceId,
					appointment_no: calEvent.data.appointmentNo,
					appointmentDate: Juno.Common.Util.formatMomentDate(startMoment),
					status: calEvent.data.eventStatusCode,
					start_time: Juno.Common.Util.formatMomentTime(startMoment),

					referral_no_1: calEvent.data.billingRdohip,
				};
				window.open(scheduleService.getBillingLink(params));
			}
		};
		controller.openMasterRecord = function openMasterRecord(calEvent)
		{
			if (calEvent.data.demographicNo !== 0)
			{
				var params = {
					demographicNo: calEvent.data.demographicNo
				};
				$state.go('record.details', params);
			}
		};
		controller.openRxPage = function openRxPage(calEvent)
		{
			if (calEvent.data.demographicNo !== 0)
			{
				var params = {
					demographicNo: calEvent.data.demographicNo,
					providerNo: calEvent.resourceId,
				};
				window.open(scheduleService.getRxLink(params));
			}
		};

		controller.openDaysheet = function openDaysheet(resourceId)
		{
			var formattedDate = $scope.datepickerSelectedDate;
			var win = window.open('../report/reportdaysheet.jsp' +
				'?dsmode=all' +
				'&provider_no=' + encodeURIComponent(resourceId) +
				'&sdate=' + encodeURIComponent(formattedDate) +
				'&edate=' + encodeURIComponent(formattedDate),
				'daysheet', 'height=700,width=1024,scrollbars=1');
			win.focus();
		};

		controller.openFormLink = function openFormLink(formName, demographicNo, appointmentNo)
		{
			var url = "../form/forwardshortcutname.jsp" +
				"?formname=" + encodeURIComponent(formName) +
				"&demographic_no="+ encodeURIComponent(demographicNo) +
				"&appointmentNo="+ encodeURIComponent(appointmentNo);

			if(formName === "__intakeForm")
			{
				url = "../provider/formIntake.jsp?demographic_no=" + encodeURIComponent(demographicNo);
			}

			var win = window.open(url,
				"Form_"+ encodeURIComponent(demographicNo) +"_" + encodeURIComponent(formName),
				'height=700,width=1024,scrollbars=1');
			win.focus();
		};
		controller.openEFormLink = function openEFormLink(eFormId, demographicNo, appointmentNo)
		{
			var url = "../eform/efmformadd_data.jsp" +
				"?fid=" + encodeURIComponent(eFormId) +
				"&demographic_no="+ encodeURIComponent(demographicNo) +
				"&appointment=" + encodeURIComponent(appointmentNo);
			var win = window.open(url,
				"Eform_"+ encodeURIComponent(demographicNo) +"_" + encodeURIComponent(eFormId),
				'height=700,width=1024,scrollbars=1');
			win.focus();
		};
		controller.openQuickLink = function openQuickLink(url, demographicNo, appointmentNo)
		{
			if(!url.startsWith("http://") || !url.startsWith("https://"))
			{
				url = "https://" + url;
			}
			var win = window.open(url,
				"quickLink_"+ url,
				'height=700,width=1024,scrollbars=1');
			win.focus();
		};


		$scope.openCreateEventDialog = function openCreateEventDialog(
			start, end, jsEvent, view, resource)
		{
			// XXX: share as much code as possible with edit event
			if (!securityService.hasPermission('scheduling_create'))
			{
				return;
			}

			// if already opening a dialog or have one open, ignore and return
			if ($scope.openingDialog || $scope.dialog)
			{
				return;
			}
			$scope.openingDialog = true;

			var scheduleUuid = null;
			var displayName = "";
			if (Juno.Common.Util.exists(resource))
			{
				scheduleUuid = resource.id;
				displayName = resource.display_name;
			}
			else if ($scope.selectedSchedule !== null)
			{
				scheduleUuid = $scope.selectedSchedule.uuid;
			}

			var data = {
				schedule: {
					uuid: scheduleUuid,
					display_name: displayName
				},
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
					type: [function ()
					{
						return 'create_edit_event'
					}],
					label: [function ()
					{
						return 'Appointment'
					}],
					parentScope: [function ()
					{
						return $scope
					}],
					data: [function ()
					{
						return data
					}],
					editMode: [function ()
					{
						return false
					}],
					keyBinding: [function ()
					{
						return {
							bindKeyGlobal: function ()
							{
							}
						}
					}],
					focus: [function ()
					{
						return focusService
					}],
				},
				windowClass: "juno-modal",
			});

			$scope.dialog.result.catch(function (res)
			{
				if (!(res === 'cancel' || res === 'escape key press'))
				{
					throw res;
				}
			});

			// when the dialog closes clear the variable
			$scope.dialog.closed.then(function ()
			{
				$scope.dialog = null;
			});
			$scope.openingDialog = false;
		};

		$scope.openEditEventDialog = function openEditEventDialog(calEvent)
		{
			if (!securityService.hasPermission('scheduling_edit'))
			{
				return;
			}

			// if already opening a dialog or have one open, ignore and return
			if ($scope.openingDialog || $scope.dialog)
			{
				return;
			}

			$scope.openingDialog = true;

			var scheduleUuid = calEvent.resourceId;
			var displayName = calEvent.data.demographicName;

			if (displayName == null)
			{
				displayName = '';
			}

			var data = {
				schedule: {
					uuid: scheduleUuid,
					display_name: displayName
				},
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
					type: [function ()
					{
						return 'create_edit_event'
					}],
					label: [function ()
					{
						return 'Appointment'
					}],
					parentScope: [function ()
					{
						return $scope
					}],
					data: [function ()
					{
						return data
					}],
					editMode: [function ()
					{
						return true
					}],
					keyBinding: [function ()
					{
						return {
							bindKeyGlobal: function ()
							{
							}
						}
					}],
					focus: [function ()
					{
						return focusService
					}],
				},
				windowClass: "juno-modal",
			});

			$scope.dialog.result.catch(function (res)
			{
				if (!(res === 'cancel' || res === 'escape key press'))
				{
					throw res;
				}
			});

			// when the dialog closes clear the variable
			$scope.dialog.closed.then(function ()
			{
				$scope.dialog = null;
			});

			$scope.openingDialog = false;
		};

		$scope.onEventDrop = function onEventDrop(
			calEvent, delta, revertFunc, jsEvent, ui, view)
		{
			if (!securityService.hasPermission('scheduling_create'))
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

				},
				function error(errors)
				{
					console.log('failed to save event', errors);

					// revert on fail
					revertFunc();
					$scope.setCalendarLoading(false);
				});
		};

		$scope.onEventResize = function onEventResize(calEvent, delta, revertFunc, jsEvent, ui, view)
		{
			if (!securityService.hasPermission('scheduling_create'))
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

				},
				function error(errors)
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
			var selectedSiteName = $scope.selectedSiteName;
			if (!Juno.Common.Util.exists(selectedSiteName))
			{
				$scope.selectedSiteName = null;
			}

			$scope.providerPreferenceApi.updateProviderSetting(securityService.getUser().providerNo, "schedule.site", $scope.selectedSiteName)
				.then(
					function success()
					{
						controller.providerSettings.siteSelected = $scope.selectedSiteName;
						$scope.refetchEvents();
					},
					function failure()
					{
						$scope.displayMessages.add_standard_error("Failed to update provider setting");
					}
				);
		};

		$scope.onScheduleChanged = function onScheduleChanged()
		{
			var selectedSchedule = $scope.selectedSchedule;

			if (!Juno.Common.Util.exists(selectedSchedule))
			{
				return;
			}

			$scope.providerPreferenceApi.updateProviderSetting(securityService.getUser().providerNo, "myGroupNo", selectedSchedule.identifier)
				.then(
					function success()
					{
						controller.providerSettings.groupNo = selectedSchedule.identifier;

						var isGroupSchedule = (selectedSchedule.identifierType === controller.scheduleTypeEnum.group);
						if (isGroupSchedule)
						{
							$scope.uiConfig.calendar.defaultView = controller.calendarViewEnum.agendaDay;
							$scope.setCalendarResources(true);
						}
						else
						{
							$scope.calendarViewName = $scope.getCalendarViewName();
							$scope.uiConfig.calendar.defaultView = $scope.getCalendarViewName();
							$scope.setCalendarResources(false);
						}
						$scope.refetchEvents();
					},
					function failure()
					{
						$scope.displayMessages.add_standard_error("Failed to update provider setting");
					}
				);
		};

		$scope.onTimeIntervalChanged = function onTimeIntervalChanged()
		{
			$scope.scheduleTimeInterval = $scope.selectedTimeInterval;
			var intervalInMin = $scope.scheduleTimeInterval.split(':')[1];

			$scope.providerPreferenceApi.updateProviderSetting(securityService.getUser().providerNo, "everyMin", intervalInMin)
				.then(
					function success()
					{
						controller.providerSettings.period = Number(intervalInMin);

						// updating the config will automatically trigger an events refresh
						$scope.uiConfig.calendar.slotDuration = $scope.selectedTimeInterval;
						$scope.uiConfig.calendar.slotLabelInterval = $scope.selectedSlotLabelInterval;

						// ensure the selected date doesn't change on events refresh
						$scope.uiConfig.calendar.defaultDate = $scope.calendar().fullCalendar('getDate');
						$scope.applyUiConfig($scope.uiConfig);
					},
					function failure()
					{
						$scope.displayMessages.add_standard_error("Failed to update provider setting");
					}
				);
		};


		//=========================================================================
		// Init methods pulled from the directive controller
		//=========================================================================/

		// Loads the schedule dropdown options from the API.  Sets the following:
		// $scope.schedule_options - the array used to build the schedule selection dropdown.
		$scope.loadScheduleOptions = function loadScheduleOptions()
		{
			var deferred = $q.defer();

			$scope.scheduleApi.getScheduleGroups().then(
				function success(rawResults)
				{
					var results = rawResults.data.body;
					for (var i = 0; i < results.length; i++)
					{
						var scheduleData = results[i];

						results[i].uuid = results[i].identifier;

						results[i].label = results[i].name;
						results[i].value = results[i].identifier;

						$scope.scheduleOptions.push(scheduleData);
					}
					deferred.resolve(results);
				},
				function failure(results)
				{
					$scope.displayMessages.add_standard_error("Failed to load schedule groups");
					deferred.reject(results);
				}
			);
			return deferred.promise;
		};

		// $scope.resourceOptionHash - table to look up schedule information by providerNo.  This is
		//                             used to create the resource view headers.
		controller.loadResourceHash = function loadResourceHash()
		{
			var deferred = $q.defer();

			providersService.getAll().then(
				function success(results)
				{
					for (var i = 0; i < results.length; i++)
					{
						var providerNo = Number(results[i].providerNo);
						$scope.resourceOptionHash[providerNo] = {
							'id': providerNo,
							'title': results[i].name,
							'display_name': results[i].name
						};
					}
					deferred.resolve($scope.resourceOptionHash);
				},
				function failure()
				{
					$scope.displayMessages.add_standard_error("Failed to load resource hash");
					deferred.reject($scope.resourceOptionHash);
				}
			);

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
					if ($scope.sitesEnabled)
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

		controller.loadSitesEnabled = function loadSitesEnabled()
		{
			var deferred = $q.defer();

			$scope.sitesApi.getSitesEnabled().then(
				function success(rawResults)
				{
					var enabled = rawResults.data.body;
					$scope.sitesEnabled = enabled;
					deferred.resolve(enabled);
				},
				function failure(results)
				{
					$scope.displayMessages.add_standard_error("Failed to load sites enabled");
					deferred.reject(results.data.body);
				}
			);

			return deferred.promise;
		};
		$scope.loadSites = function loadSites()
		{
			var deferred = $q.defer();

			$scope.sitesApi.getSiteList().then(
				function success(rawResults)
				{
					var results = rawResults.data.body;
					var out = [];
					if (angular.isArray(results))
					{
						for (var i = 0; i < results.length; i++)
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
				},
				function failure(results)
				{
					$scope.displayMessages.add_standard_error("Failed to load sites");
					deferred.reject(results);
				}
			);

			return deferred.promise;
		};


		$scope.loadAvailabilityTypes = function loadAvailabilityTypes()
		{
			var deferred = $q.defer();
			var availabilityTypes = {};

			$scope.scheduleApi.getScheduleTemplateCodes().then(
				function success(rawResults)
				{
					var results = rawResults.data.body;
					for (var i = 0; i < results.length; i++)
					{
						var result = results[i];

						availabilityTypes[result.code] = angular.copy(result);
					}

					$scope.availabilityTypes = availabilityTypes;
					deferred.resolve(availabilityTypes);
				},
				function failure(results)
				{
					$scope.displayMessages.add_standard_error("Failed to load availablity types");
					deferred.reject(results.data.body);
				}
			);

			return deferred.promise;
		};

		controller.loadExtraLinkData = function loadExtraLinkData()
		{
			var deferred = $q.defer();
			if(controller.formLinks.enabled)
			{
				var lengthProp = controller.providerSettings.appointmentScreenLinkNameDisplayLength;
				if (Juno.Common.Util.exists(lengthProp)
					&& Juno.Common.Util.isNumber(lengthProp)
					&& lengthProp > 0)
				{
					controller.formLinks.maxLength = lengthProp;
				}

				var eFormIds = controller.providerSettings.appointmentScreenEforms;
				if(Juno.Common.Util.exists(eFormIds) && eFormIds.length > 0)
				{
					formService.getAllEForms().then(
						function success(eFormList)
						{
							for (var i = 0; i < eFormList.length; i++)
							{
								var id = eFormList[i].id;
								if(eFormIds.includes(id))
								{
									var name = eFormList[i].formName;
									controller.formLinks.eFormNameMap[id] = name;
								}
							}
							deferred.resolve();
						},
						function failure()
						{
							$scope.displayMessages.add_standard_error("Failed to load eform data");
							deferred.reject();
						}
					);
				}
				else
				{
					deferred.resolve();
				}

				var formIds = controller.providerSettings.appointmentScreenForms;
				if(Juno.Common.Util.exists(formIds))
				{
					for(var i=0; i< formIds.length; i++)
					{
						var formId = formIds[i];
						controller.formLinks.formNameMap[formId] = formId;
					}
				}
				var enableIntakeForm = controller.providerSettings.intakeFormEnabled;
				if(Juno.Common.Util.exists(enableIntakeForm) && enableIntakeForm)
				{
					controller.formLinks.formNameMap['__intakeForm'] = "Intake Form";
				}

				var quickLinkIds = controller.providerSettings.appointmentScreenQuickLinks;
				if(Juno.Common.Util.exists(quickLinkIds))
				{
					for(var i=0; i< quickLinkIds.length; i++)
					{
						var linkName = quickLinkIds[i].name;
						var linkUrl = quickLinkIds[i].url;
						controller.formLinks.quickLinkMap[linkUrl] = linkName;
					}
				}
			}
			return deferred.promise;
		};
		controller.loadEformData = function loadEFormData(eFormId)
		{
			var deferred = $q.defer();
			eFormService.loadEForm(eFormId).then(
				function success(data)
				{
					deferred.resolve(data);
				},
				function failure(data)
				{
					$scope.displayMessages.add_standard_error("Failed to load eform " + eFormId);
					deferred.reject(data);
				}
			);
			return deferred.promise;
		};

		$scope.loadDefaultSelections = function loadDefaultSelections()
		{
			$scope.selectedSchedule = $scope.getSelectedSchedule($scope.scheduleOptions);
			$scope.selectedSiteName = controller.getSelectedSite();
			controller.selectedScheduleView = controller.getSelectedScheduleView();

			$scope.selectedTimeInterval = $scope.getSelectedTimeInterval(
				$scope.timeIntervalOptions, $scope.defaultTimeInterval);
			$scope.uiConfig.calendar.slotDuration = $scope.selectedTimeInterval;
			$scope.uiConfig.calendar.slotLabelInterval = $scope.selectedSlotLabelInterval;

			$scope.uiConfig.calendar.minTime = $scope.getScheduleMinTime();
			$scope.uiConfig.calendar.maxTime = $scope.getScheduleMaxTime();

			// scroll so that one hour ago is the top of the calendar
			$scope.uiConfig.calendar.scrollTime = moment().subtract(1, 'hours').format('HH:mm:ss');
		};

		//=========================================================================
		// Watches
		//=========================================================================/

		controller.loadWatches = function loadWatches()
		{
			$scope.$watch('datepickerSelectedDate', function (newValue, oldValue)
			{
				if (newValue !== oldValue)
				{
					var momentDate = Juno.Common.Util.getDateMoment(newValue);
					$scope.changeDate(momentDate);
				}
			});

			$scope.$watch('selectedSiteName', function (newValue, oldValue)
			{
				if (newValue !== oldValue)
				{
					$scope.onSiteChanged();
				}
			});
			$scope.$watch('selectedTimeInterval', function (newValue, oldValue)
			{
				if (newValue !== oldValue)
				{
					$scope.onTimeIntervalChanged();
				}
			});
		};


		//=========================================================================
		// Config Array
		//=========================================================================/

		$scope.applyUiConfig = function applyUiConfig(uiConfig)
		{
			$scope.uiConfigApplied = angular.copy(uiConfig);
		};

		controller.initEventsAutoRefresh = function initEventsAutoRefresh()
		{
			var deferred = $q.defer();

			// if there is already a refresh set up, stop it
			var refresh = controller.refreshSettings.timerVariable;
			if (refresh !== null)
			{
				clearInterval(refresh);
			}

			// get the refresh interval from preferences, or use default
			var minutes = controller.refreshSettings.preferredAutoRefreshMinutes;
			if (!Juno.Common.Util.exists(minutes) || !Juno.Common.Util.isIntegerString(minutes))
			{
				minutes = controller.refreshSettings.defaultAutoRefreshMinutes;
			}
			else
			{
				minutes = parseInt(minutes);
			}

			if (minutes > 0)
			{
				// start the auto refresh and save its ID to global state
				controller.refreshSettings.timerVariable = setInterval($scope.refetchEvents, minutes * 60 * 1000);
			}
			deferred.resolve();

			return deferred.promise;
		};

		// Any changes to this array need to be applied by calling applyUiConfig()
		$scope.uiConfig = {
			calendar: {
				height: 'parent',
				nowIndicator: true,
				header: false,
				columnHeader: true,
				views: {
					day: {
						columnHeaderFormat: 'dddd MMMM Do'
					},
					week: {
						columnHeaderFormat: 'dddd MMM Do'
					},
					month: {
						columnHeaderFormat: 'dddd'
					},
				},
				hiddenDays: [],

				allDaySlot: false,
				agendaEventMinHeight: 18,

				defaultView: null,
				defaultDate: $scope.defaultDate,
				slotDuration: $scope.selectedTimeInterval,
				snapDuration: '00:05:00',
				slotLabelInterval: $scope.selectedSlotLabelInterval,
				slotLabelFormat: 'h A',
				slotEventOverlap: false,
				lazyFetching: false, //for dev use

				resources: false, // contains the resource hash properties for each schedule in group view
				resourceOrder: 'id', // display order for multiple schedules, relies on a resource hash property

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