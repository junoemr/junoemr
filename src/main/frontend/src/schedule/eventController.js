'use strict';


//=========================================================================
// Calendar Event Controller
//=========================================================================/

angular.module('Schedule').controller('Schedule.EventController', [

		'$scope',
		'$q',
		'$timeout',
		'$state',
		'$uibModalInstance',

		'errorsService',
		//'cpCalendar.Juno.Common.Util',


		'securityService',
		'keyBinding',
		'focus',
		'type', 'parentScope', 'label', 'editMode', 'data',

	function (
		$scope, $q, $timeout, $state, $uibModalInstance,

		messagesFactory, //util,

		securityService,
		keyBinding, focus,
		type, parentScope, label, editMode, data
	)
{
	$scope.parentScope = parentScope;


	//=========================================================================
	// Access Control
	//=========================================================================/


	//=========================================================================
	// Local scope variables
	//=========================================================================/

	$scope.label = label;
	$scope.editMode = editMode;

	$scope.keyBinding = keyBinding;
	$scope.eventUuid = data.uuid;
	$scope.numInvoices = data.numInvoices;
	$scope.tagNames = data.tagNames;

	$scope.schedule = data.schedule;

	$scope.availabilityTypes = data.availabilityTypes;
	$scope.scheduleTemplates = data.scheduleTemplates;

	$scope.eventData = {
		start_time: null,
		end_time: null,
		reason: null,
		description: null
	};

	$scope.timeInterval = data.timeInterval;
	$scope.startDate = null;
	$scope.startTime = null;
	$scope.endDate = null;
	$scope.endTime = null;

	$scope.lastEventLength = null;

	$scope.patient = parentScope.patientModel;
	$scope.autocompleteValues = {};

	$scope.activeTemplateEvents = [];

	console.log($scope.parentScope);
	$scope.eventStatuses = $scope.parentScope.eventStatuses;
	$scope.eventStatusOptions = [];
	$scope.selectedEventStatus = null;
	$scope.defaultEventStatus = null;

	$scope.selectedSiteName = null;

	$scope.timepickerFormat = "h:mm A";

	$scope.fieldValueMapping = {
		start_date: 'Start Date',
		start_time: 'Start Time',
		end_date: 'End Date',
		end_time: 'End Time'
	};
	$scope.displayMessages = messagesFactory.factory();

	$scope.initialized = false;
	$scope.working = false;

	//=========================================================================
	// Init
	//=========================================================================/

	$scope.init = function init()
	{
		if(!securityService.hasPermission('scheduling_create'))
		{
			$timeout(function()
			{
				$scope.cancel();
			});
		}

		var momentStart = data.start_time;
		var momentEnd = data.end_time;

		$scope.startTime = Juno.Common.Util.formatMomentTime(momentStart, $scope.timepickerFormat);
		$scope.endTime = Juno.Common.Util.formatMomentTime(momentEnd, $scope.timepickerFormat);
		$scope.startDate = Juno.Common.Util.formatMomentDate(momentStart);
		$scope.endDate = Juno.Common.Util.formatMomentDate(momentEnd);

		console.log('start_time');
		console.log($scope.startDate);
		console.log($scope.startTime);

		$scope.lastEventLength = momentEnd.diff(momentStart, 'minutes');

		// maintain a list of the 'active' templates based on start time
		$scope.setActiveTemplateEvents();

		for(var key in $scope.eventStatuses)
		{
			if($scope.eventStatuses.hasOwnProperty(key))
			{
				$scope.eventStatusOptions.push($scope.eventStatuses[key]);
			}
		}
		$scope.defaultEventStatus = data.defaultEventStatus;
		$scope.setSelectedEventStatus(data.event_status_uuid);


		if(editMode)
		{
			$scope.eventData.reason = data.reason;
			$scope.eventData.description = data.description;

			// either load the patient data and init the autocomplete
			// or ensure the patient model is clear
			$scope.patient.uuid = data.demographics_patient_uuid;
			$scope.initPatientAutocomplete().then(function() {
				$scope.initialized = true;
			});
			$scope.selectedSiteName = data.selected_site_name;
		}
		else
		{
			// create mode: adjust the end date (if needed)
			// and clear the patient model
			$scope.adjustEndDatetime();
			$scope.patient.clear();

			// autofocus the patient field
			focus.element("#input-patient");

			$scope.initialized = true;
		}
	};

	$scope.initPatientAutocomplete = function initPatientAutocomplete()
	{
		var deferred = $q.defer();

		if(Juno.Common.Util.exists($scope.patient.uuid) && $scope.patient.uuid != 0)
		{
			parentScope.autocomplete.init_autocomplete_values(
				{ patient: $scope.patient.uuid },
				$scope.autocompleteValues).then(
				function(results)
				{
					$scope.autocompleteValues = results.data;
					$scope.patient.fillData($scope.autocompleteValues.patient.data);
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
		momentStart, momentEnd)
	{
  		var days = [];
  		var momentDay = momentStart.clone().hour(0).minute(0);
  		while(momentDay.isBefore(momentEnd))
  		{
  			days.push(momentDay.clone());
  			momentDay.add(1, 'day');
  		}
  		return days;
	};

	$scope.setSelectedEventStatus = function setSelectedEventStatus(uuid)
	{
		var eventStatusUuid = $scope.defaultEventStatus;
		if(Juno.Common.Util.exists(uuid))
		{
			eventStatusUuid = uuid;
		}

		if(!Juno.Common.Util.exists(eventStatusUuid) ||
			!Juno.Common.Util.exists($scope.eventStatuses[eventStatusUuid]))
		{
			// if not set or found just pick the first one
			eventStatusUuid = $scope.eventStatusOptions[0].uuid;
		}

		$scope.selectedEventStatus = $scope.eventStatuses[eventStatusUuid];
	};

	$scope.setActiveTemplateEvents = function setActiveTemplateEvents()
	{
		if(!($scope.schedule || {}).events)
		{
			return;
		}

		// Get templates that happen during the time period

		var momentStart = Juno.Common.Util.getDateAndTimeMoment(
			$scope.startDate, $scope.formattedTime($scope.startTime));
		var momentEnd = Juno.Common.Util.getDateAndTimeMoment(
			$scope.endDate, $scope.formattedTime($scope.endTime));

		var active_events = [];

		for(var i = 0; i < $scope.schedule.events.length; i++)
		{
			var event = angular.copy($scope.schedule.events[i]);

			if(!event.availability_type)
			{
				continue;
			}

			// if start time is before event end time or if end time is after event start
			event.start = Juno.Common.Util.getDatetimeNoTimezoneMoment(event.start);
			event.end = Juno.Common.Util.getDatetimeNoTimezoneMoment(event.end);

			if(momentStart.isValid() && momentEnd.isValid() &&
				event.start.isValid() && event.end.isValid() &&
				momentStart.isBefore(event.end) && momentEnd.isAfter(event.start))
			{
				active_events.push(event);
			}
		}

		$scope.activeTemplateEvents = active_events;
	};

	$scope.adjustEndDatetime = function adjustEndDatetime(length_minutes)
	{
		// adjusts the end time to the specified length or
		// adjusts to keep the event length the same as it last was

		var momentStart = Juno.Common.Util.getDateAndTimeMoment(
			$scope.startDate, $scope.formattedTime($scope.startTime));
		if(momentStart.isValid())
		{
			var new_event_length = Juno.Common.Util.exists(length_minutes) ?
				length_minutes : $scope.lastEventLength;

			var momentEnd = momentStart.add(new_event_length, 'minutes');

			$scope.endDate = Juno.Common.Util.formatMomentDate(momentEnd);
			$scope.endTime = Juno.Common.Util.formatMomentTime(momentEnd, $scope.timepickerFormat);
		}
	};

	$scope.updateLastEventLength = function updateLastEventLength()
	{
		// saves the current event length, if the date/times are valid

		var momentStart = Juno.Common.Util.getDateAndTimeMoment(
			$scope.startDate, $scope.formattedTime($scope.startTime));
		var momentEnd = Juno.Common.Util.getDateAndTimeMoment(
			$scope.endDate, $scope.formattedTime($scope.endTime));

		if(momentStart.isValid() && momentEnd.isValid())
		{
			var eventLength = momentEnd.diff(momentStart, 'minutes');
			if(eventLength > 0)
			{
			$scope.lastEventLength = eventLength;
			}
		}
	};

	$scope.validateForm = function validateForm()
	{
		$scope.displayMessages.clear();

		Juno.Common.Util.validateDateString($scope.startDate,
			$scope.displayMessages, 'start_date', 'Start Time', true);

		Juno.Common.Util.validateTimeString($scope.formattedTime($scope.startTime),
			$scope.displayMessages, 'start_time', 'Start Time', true);

		Juno.Common.Util.validateDateString($scope.endDate,
			$scope.displayMessages, 'end_date', 'End Time', true);

		Juno.Common.Util.validateTimeString($scope.formattedTime($scope.endTime),
			$scope.displayMessages, 'end_time', 'End Time', true);

		// if all the date/time fields look good, validate range
		if(!$scope.displayMessages.has_errors())
		{
			var startDatetime = Juno.Common.Util.getDateAndTimeMoment(
				$scope.startDate, $scope.formattedTime($scope.startTime));
			var endDatetime = Juno.Common.Util.getDateAndTimeMoment(
				$scope.endDate, $scope.formattedTime($scope.endTime));

			if(endDatetime.isSame(startDatetime) ||
				endDatetime.isBefore(startDatetime))
			{
				$scope.displayMessages.addStandardError("The appointment must end after it starts");
			}
		}

		return !$scope.displayMessages.hasErrors();
	};

	$scope.saveEvent = function saveEvent()
	{
		var deferred = $q.defer();

		var startDatetime = Juno.Common.Util.getDateAndTimeMoment(
				$scope.startDate, $scope.formattedTime($scope.startTime));

		var endDatetime = Juno.Common.Util.getDateAndTimeMoment(
				$scope.endDate, $scope.formattedTime($scope.endTime));

		parentScope.saveEvent(
			editMode,
			$scope.eventUuid,
			startDatetime,
			endDatetime,
			$scope.eventData,
			$scope.schedule.uuid,
			$scope.selectedEventStatus.uuid,
			$scope.patient.uuid,
			$scope.selectedSiteName
		).then(
			function(results)
			{
				if(parentScope.processSaveResults(results, $scope.displayMessages))
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
				parentScope.processSaveResults(results, $scope.displayMessages);
				deferred.reject();
			});

		return deferred.promise;
	};

	$scope.deleteEvent = function deleteEvent()
	{
		var deferred = $q.defer();

		parentScope.deleteEvent($scope.eventUuid).then(function()
		{
			deferred.resolve();

		}, function()
		{
			deferred.reject();
		});

		return deferred.promise;
	};

	$scope.formattedTime = function formattedTime(time_str)
	{
		// the time picker format is HH:MM AM - need to strip spaces
		return time_str.replace(/ /g,'');
	};

	//=========================================================================
	// Watches
	//=========================================================================/

	// when the start date is changed,
	// update the active template events
	$scope.$watch('start_date', function(newStartDate, oldStartDate)
	{
		// avoid running first time this fires during initialization
		if(newStartDate !== oldStartDate)
		{
			$scope.setActiveTemplateEvents();
			$scope.adjustEndDatetime();
		}
	});

	// when the start time is changed,
	// update the active template events and adjust the end time
	$scope.$watch('start_time', function(newStartTime, oldStartTime)
	{
		// avoid running first time this fires during initialization
		if(newStartTime !== oldStartTime)
		{
			$scope.setActiveTemplateEvents();
			$scope.adjustEndDatetime();
		}
	});

	// when the end date is changed, track the event length
	$scope.$watch('end_date', function(newEndDate, oldEndDate)
	{
		// avoid running first time this fires during initialization
		if(newEndDate !== oldEndDate)
		{
			$scope.setActiveTemplateEvents();
			$scope.updateLastEventLength();
		}
	});

	// when the end time is changed, track the event length
	$scope.$watch('end_time', function(newEndTime, oldEndTime)
	{
		// avoid running first time this fires during initialization
		if(newEndTime !== oldEndTime)
		{
			$scope.setActiveTemplateEvents();
			$scope.updateLastEventLength();
		}
	});

	//=========================================================================
	// Public methods
	//=========================================================================/

	$scope.loadedNewPhoto = function loadedNewPhoto(file, event)
	{
		if(file == null)
		{
			return;
		}
		$scope.preview_patient_image = file;
		$scope.new_photo = true;
		$scope.patient.has_photo = true;
		$scope.patient.uploadPhoto(file);
	};

	$scope.isWorking = function isWorking()
	{
		return $scope.working;
	};

	$scope.isInitialized = function isInitialized()
	{
		return $scope.initialized;
	};

	$scope.isPatientSelected = function isPatientSelected()
	{
		return Juno.Common.Util.exists($scope.patient.uuid);
	};

	$scope.clearPatient = function clearPatient()
	{
		$scope.autocompleteValues.patient = null;
		$scope.patient.clear();
	};

	$scope.onSelectPatient = function onSelectPatient($item, $model, $label, $event)
	{
		// $item is a Patient pojo
		$scope.patient.uuid = $item.data[$item.value_field];
		$scope.patient.fillData($item.data);
	};

	$scope.setEventLength = function setEventLength(minutes)
	{
		$scope.adjustEndDatetime(minutes);
	};

	$scope.save = function save()
	{
		if(!$scope.validateForm())
		{
				return false;
		}

		$scope.working = true;
		$scope.saveEvent().then(function()
		{
			$scope.parentScope.refetchEvents();
			$uibModalInstance.close();
			$scope.working = false;
		}, function()
		{
			if(!$scope.displayMessages.hasStandardErrors())
			{
				$scope.displayMessages.addGenericFatalError();
			}
			$scope.working = false;
		});
	};

	$scope.delete = function()
	{
		$scope.working = true;
		$scope.deleteEvent().then(function()
		{
			$scope.parentScope.refetchEvents();
			$uibModalInstance.close();
			$scope.working = false;
		}, function()
		{
			$scope.displayMessages.addGenericFatalError();
			$scope.working = false;
		});
	};

	$scope.cancel = function cancel()
	{
		$uibModalInstance.dismiss('cancel');
	};

	$scope.saveAndBill = function saveAndBill()
	{
		if(!$scope.validateForm())
		{
	  		return false;
		}

		$scope.working = true;
		$scope.saveEvent().then(function()
		{
			$scope.parentScope.refetchEvents();
			$uibModalInstance.close();
			$scope.working = false;
			$scope.parentScope.openCreateInvoice(
				$scope.eventUuid,
				$scope.schedule.uuid,
				$scope.patient.uuid);
		}, function()
		{
			$scope.displayMessages.addGenericFatalError();
			$scope.working = false;
		});
	};

	$scope.viewInvoices = function viewInvoices()
	{
		$scope.parentScope.open_view_invoices($scope.eventUuid);
	};

	$scope.createPatient = function createPatient()
	{
		var editModeCallback = function() { return false; };
		var onSaveCallback = function() { return $scope.onPatientModalSave; };
		var loadErrorLinkPatientFn = function() { return $scope.onPatientModalSave; };

		$scope.create_patient_dialog = parentScope.calendar_api_adapter.openPatientDialog(
				editModeCallback, onSaveCallback, loadErrorLinkPatientFn);
	};

	$scope.modify_patient = function modify_patient()
	{
		if(!$scope.patient.uuid)
		{
			return;
		}

		var editModeCallback = function() { return true; };
		var onSaveCallback = function() { return $scope.onPatientModalSave; };
		var loadErrorLinkPatientFn = function() { return $scope.onPatientModalSave; };

		$scope.modify_patient_dialog = parentScope.calendar_api_adapter.openPatientDialog(
				editModeCallback, onSaveCallback, loadErrorLinkPatientFn);
	};

	// for callback on create/edit patient modal
	$scope.onPatientModalSave = function onPatientModalSave(uuid)
	{
		// load the newly created/updated patient
		$scope.patient.uuid = uuid;
		$scope.initPatientAutocomplete();
	};

	//=========================================================================
	//  Key Bindings
	//=========================================================================

	$scope.keyBindSettings =
	{
		"ctrl+enter": {
			title: 'Ctrl+Enter',
			tooltip: 'Save',
			description: 'Save appointment',
			callback_fn: function enter_callback()
			{
				if(!$scope.isWorking())
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
				if(!$scope.isWorking())
				{
					$scope.saveAndBill();
				}
			},
			target: null
		}
	};
	$scope.keyBinding.bind_key_global("ctrl+enter", $scope.keyBindSettings["ctrl+enter"]);
	$scope.keyBinding.bind_key_global("ctrl+shift+enter", $scope.keyBindSettings["ctrl+shift+enter"]);
}]);
