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


		'demographicService',
		'securityService',
		'keyBinding',
		'focus',
		'type', 'parentScope', 'label', 'editMode', 'data',

	function (
		$scope, $q, $timeout, $state, $uibModalInstance,

		messagesFactory, //util,

		demographicService,
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
	$scope.eventUuid = null;

	$scope.schedule = data.schedule;

	/*
	$scope.numInvoices = data.event_data.num_invoices;
	$scope.tagNames = data.event_data.tag_names;


	$scope.availabilityTypes = data.availabilityTypes;
	$scope.scheduleTemplates = data.scheduleTemplates;
	*/

	$scope.eventData = {
		startDate: null,
		startTime: null,
		endDate: null,
		endTime: null,
		reason: null,
		notes: null
	};

	$scope.timeInterval = data.time_interval;

	$scope.lastEventLength = null;

	$scope.patientTypeahead = {};
	$scope.autocompleteValues = {};

	$scope.activeTemplateEvents = [];

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

	$scope.demographicModel = {
		demographicNo: null,
		fullName: null,
		hasPhoto: true,
		patientPhotoUrl: '/imageRenderingServlet?source=local_client&clientId=0',
		data: {
			birthDate: null,
			healthNumber: null,
			ontarioVersionCode: null,
			phoneNumberPrimary: null
		},
		clear: function clear()
		{
			this.demographicNo = null;
			this.fullName = null;
			this.patientPhotoUrl = '/imageRenderingServlet?source=local_client&clientId=0';
			this.data.birthDate = null;
			this.data.healthNumber = null;
			this.data.ontarioVersionCode = null;
			this.data.phoneNumberPrimary = null;
		},
		fillData: function fillData(data)
		{
			this.demographicNo = data.demographicNo;
			this.fullName = Juno.Common.Util.formatName(data.firstName, data.lastName);
			this.patientPhotoUrl = '/imageRenderingServlet?source=local_client&clientId=' + (data.demographicNo? data.demographicNo: 0);

			var dateOfBirth = null;
			if(Juno.Common.Util.exists(data.dob))
			{
				// XXX: Perhaps put this in util?  Is this date format common for juno?
				dateOfBirth = moment(data.dob, "YYYY-MM-DDTHH:mm:ss.SSS+ZZZZ", false);
				console.log(dateOfBirth);
			}
			else
			{
				dateOfBirth = Juno.Common.Util.getDateMomentFromComponents(
					data.dobYear, data.dobMonth, data.dobDay);
			}
			this.data.birthDate = Juno.Common.Util.formatMomentDate(dateOfBirth);


			this.data.healthNumber = data.hin;
			// XXX: no version code when loaded from autocomplete?  Does that matter?
			this.data.ontarioVersionCode = data.ver;
			this.data.phoneNumberPrimary = data.phone;
		},
		uploadPhoto: function uploadPhoto(file){}
	};

	//=========================================================================
	// Init
	//=========================================================================/

	$scope.init = function init()
	{
		if (!securityService.hasPermission('scheduling_create'))
		{
			$timeout(function ()
			{
				$scope.cancel();
			});
		}

		$scope.demographicModel.clear();

		console.log(data);
		//var momentStart = Juno.Common.Util.getDateAndTimeMoment(data.start_time);
		//var momentEnd = Juno.Common.Util.getDateAndTimeMoment(data.end_time);
		var momentStart = data.start_time;
		var momentEnd = data.end_time;

		$scope.eventData.startTime = Juno.Common.Util.formatMomentTime(momentStart, $scope.timepickerFormat);
		$scope.eventData.endTime = Juno.Common.Util.formatMomentTime(momentEnd, $scope.timepickerFormat);
		$scope.eventData.startDate = Juno.Common.Util.formatMomentDate(momentStart);
		$scope.eventData.endDate = Juno.Common.Util.formatMomentDate(momentEnd);

		$scope.lastEventLength = momentEnd.diff(momentStart, 'minutes');

		// maintain a list of the 'active' templates based on start time
		$scope.setActiveTemplateEvents();

		/*
		$scope.eventStatusOptions = [];
		$scope.eventStatusOptions.push({
			color: "#FFFFFF",
			display_letter: "",
			icon: "",
			name: "",
			rotates: true,
			sort_order: 1,
			system_code: null,
			uuid: null
		});
		console.log($scope.eventStatusOptions);
		*/

		//$scope.eventStatusOptions.push("");
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
			$scope.eventUuid = data.event_data.appointment_uuid;
			$scope.eventData.reason = data.event_data.reason;
			$scope.eventData.notes = data.event_data.notes;

			// either load the patient data and init the autocomplete
			// or ensure the patient model is clear
			$scope.initPatientAutocomplete(data.event_data.demographics_patient_uuid).then(function() {
				$scope.initialized = true;
			});
			$scope.selectedSiteName = data.event_data.site;
		}
		else
		{
			// create mode: adjust the end date (if needed)
			// and clear the patient model
			$scope.adjustEndDatetime();
			$scope.demographicModel.clear();

			// autofocus the patient field
			focus.element("#input-patient");

			$scope.initialized = true;
		}
	};

	$scope.initPatientAutocomplete = function initPatientAutocomplete(demographicNo)
	{
		var deferred = $q.defer();

		if(Juno.Common.Util.exists(demographicNo) && demographicNo != 0)
		{
			demographicService.getDemographic(demographicNo).then(function(data)
			{
				$scope.patientTypeahead = data;
				deferred.resolve();
			},
			function(errors)
			{
				console.log('error initializing patient autocomplete', errors);
				$scope.demographicModel.clear();
				deferred.resolve();
			});
		}
		else
		{
			$scope.demographicModel.clear();
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

	$scope.setSelectedEventStatus = function setSelectedEventStatus(selectedCode)
	{
		var eventStatusCode = $scope.defaultEventStatus;

		if(Juno.Common.Util.exists(selectedCode))
		{
			eventStatusCode = selectedCode;
		}

		if(!Juno.Common.Util.exists(eventStatusCode) ||
			!Juno.Common.Util.exists($scope.eventStatuses[eventStatusCode]))
		{
			// if not set or found just pick the first one
			eventStatusCode = $scope.eventStatusOptions[0].displayLetter;
		}

		$scope.selectedEventStatus = $scope.eventStatuses[eventStatusCode];
	};

	$scope.setActiveTemplateEvents = function setActiveTemplateEvents()
	{
		if(!($scope.schedule || {}).events)
		{
			return;
		}

		// Get templates that happen during the time period

		var momentStart = Juno.Common.Util.getDateAndTimeMoment(
			$scope.eventData.startDate, $scope.formattedTime($scope.eventData.startTime));
		var momentEnd = Juno.Common.Util.getDateAndTimeMoment(
			$scope.eventData.endDate, $scope.formattedTime($scope.eventData.endTime));

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
			$scope.startDate, $scope.formattedTime($scope.eventData.startTime));
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
			$scope.eventData.startDate, $scope.formattedTime($scope.eventData.startTime));
		var momentEnd = Juno.Common.Util.getDateAndTimeMoment(
			$scope.eventData.endDate, $scope.formattedTime($scope.eventData.endTime));

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

		Juno.Common.Util.validateDateString($scope.eventData.startDate,
			$scope.displayMessages, 'start_date', 'Start Time', true);

		Juno.Common.Util.validateTimeString($scope.formattedTime($scope.eventData.startTime),
			$scope.displayMessages, 'start_time', 'Start Time', true);

		Juno.Common.Util.validateDateString($scope.eventData.endDate,
			$scope.displayMessages, 'end_date', 'End Time', true);

		Juno.Common.Util.validateTimeString($scope.formattedTime($scope.eventData.endTime),
			$scope.displayMessages, 'end_time', 'End Time', true);

		// if all the date/time fields look good, validate range
		if(!$scope.displayMessages.has_errors())
		{
			var startDatetime = Juno.Common.Util.getDateAndTimeMoment(
				$scope.eventData.startDate, $scope.formattedTime($scope.eventData.startTime));
			var endDatetime = Juno.Common.Util.getDateAndTimeMoment(
				$scope.eventData.endDate, $scope.formattedTime($scope.eventData.endTime));

			if(endDatetime.isSame(startDatetime) ||
				endDatetime.isBefore(startDatetime))
			{
				$scope.displayMessages.addStandardError("The appointment must end after it starts");
			}
		}

		return !$scope.displayMessages.has_errors();
	};

	$scope.saveEvent = function saveEvent()
	{
		var deferred = $q.defer();

		var startDatetime = Juno.Common.Util.getDateAndTimeMoment(
				$scope.eventData.startDate, $scope.formattedTime($scope.eventData.startTime));

		var endDatetime = Juno.Common.Util.getDateAndTimeMoment(
				$scope.eventData.endDate, $scope.formattedTime($scope.eventData.endTime));

		console.log($scope.schedule);
		console.log($scope.demographicModel);
		parentScope.saveEvent(
			editMode,
			$scope.eventUuid,
			startDatetime,
			endDatetime,
			$scope.eventData,
			$scope.schedule.uuid,
			$scope.selectedEventStatus.displayLetter,
			$scope.demographicModel.demographicNo,
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

	$scope.loadPatientFromTypeahead = function loadPatientFromTypeahead(patientTypeahead)
	{
		$scope.demographicModel.fillData(patientTypeahead);
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

	$scope.$watch('patientTypeahead', function()
	{
		$scope.loadPatientFromTypeahead($scope.patientTypeahead);
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
		$scope.demographicModel.hasPhoto = true;
		$scope.demographicModel.uploadPhoto(file);
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
		return Juno.Common.Util.exists($scope.demographicModel.demographicNo);
	};

	$scope.clearPatient = function clearPatient()
	{
		$scope.autocompleteValues.patient = null;
		$scope.demographicModel.clear();
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
			if(!$scope.displayMessages.has_standard_errors())
			{
				$scope.displayMessages.add_generic_fatal_error();
			}
			$scope.working = false;
		});
	};

	$scope.del = function del()
	{
		$scope.working = true;
		$scope.deleteEvent().then(function()
		{
			$scope.parentScope.refetchEvents();
			$uibModalInstance.close();
			$scope.working = false;
		}, function()
		{
			$scope.displayMessages.add_generic_fatal_error();
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
				$scope.demographicModel.demographicNo);
		}, function()
		{
			$scope.displayMessages.add_generic_fatal_error();
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
		if(!$scope.demographicModel.demographicNo)
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
	$scope.onPatientModalSave = function onPatientModalSave(demographicNo)
	{
		// load the newly created/updated patient
		$scope.demographicModel.demographicNo = demographicNo;
		$scope.initPatientAutocomplete();
	};

	$scope.searchPatients = function searchPatients(term)
	{
		var search = {
			type: 'Name',
			'term': term,
			status: 'active',
			integrator: false,
			outofdomain: true
		};
		return demographicsService.search(search, 0, 25).then(
			function(results)
			{
				var resp = [];
				for (var x = 0; x < results.content.length; x++)
				{
					resp.push(
						{
							demographicNo: results.content[x].demographicNo,
							name: Juno.Common.Util.formatName(
								results.content[x].firstName, results.content[x].lastName)
						});
				}
				return resp;
			},
			function error(errors)
			{
				console.log(errors);
			});
	};

	$scope.selectPatient = function selectPatient(item, model, label)
	{
		console.log(item);
		console.log(model);
		console.log(label);
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
	$scope.keyBinding.bindKeyGlobal("ctrl+enter", $scope.keyBindSettings["ctrl+enter"]);
	$scope.keyBinding.bindKeyGlobal("ctrl+shift+enter", $scope.keyBindSettings["ctrl+shift+enter"]);
}]);
