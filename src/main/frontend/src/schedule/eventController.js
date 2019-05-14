'use strict';


//=========================================================================
// Calendar Event Controller
//=========================================================================/

angular.module('Schedule').controller('Schedule.EventController', [

		'$scope',
		'$q',
		'$timeout',
		'$state',
		'$uibModal',
		'$uibModalInstance',

		'errorsService',

		'demographicService',
		'securityService',
		'keyBinding',
		'focus',
		'type', 'parentScope', 'label', 'editMode', 'data',

	function (
		$scope, $q, $timeout, $state, $uibModal, $uibModalInstance,

		messagesFactory,

		demographicService,
		securityService,
		keyBinding,
		focus,
		type, parentScope, label, editMode, data
	)
{
	$scope.parentScope = parentScope;
	let controller = this;

	//=========================================================================
	// Access Control
	//=========================================================================/


	//=========================================================================
	// Local scope variables
	//=========================================================================/

	controller.tabEnum = Object.freeze({
		appointment:0,
		appointmentHistory:1
	});
	controller.activeTab = controller.tabEnum.appointment;

	controller.selectedProvider = null;
	controller.selectedResource = null;

	$scope.label = label;
	$scope.editMode = editMode;

	$scope.keyBinding = keyBinding;
	$scope.eventUuid = null;

	$scope.schedule = data.schedule;

	$scope.eventData = {
		startDate: null,
		startTime: null,
		endDate: null,
		endTime: null,
		reason: null,
		notes: null
	};

	$scope.timeInterval = data.timeInterval;

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
		startDate: 'Start Date',
		startTime: 'Start Time',
		endDate: 'End Date',
		endTime: 'End Time'
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

		var momentStart = data.startTime;
		var momentEnd = data.endTime;

		$scope.eventData.startTime = Juno.Common.Util.formatMomentTime(momentStart, $scope.timepickerFormat);
		$scope.eventData.endTime = Juno.Common.Util.formatMomentTime(momentEnd, $scope.timepickerFormat);
		$scope.eventData.startDate = Juno.Common.Util.formatMomentDate(momentStart);
		$scope.eventData.endDate = Juno.Common.Util.formatMomentDate(momentEnd);

		$scope.lastEventLength = momentEnd.diff(momentStart, 'minutes');

		// maintain a list of the 'active' templates based on start time
		$scope.setActiveTemplateEvents();

		//$scope.eventStatusOptions.push("");
		for(var key in $scope.eventStatuses)
		{
			if($scope.eventStatuses.hasOwnProperty(key))
			{
				$scope.eventStatusOptions.push($scope.eventStatuses[key]);
			}
		}
		$scope.defaultEventStatus = data.defaultEventStatus;
		$scope.setSelectedEventStatus(data.eventData.eventStatusCode);


		if(editMode)
		{
			$scope.eventUuid = data.eventData.appointmentNo;
			$scope.eventData.reason = data.eventData.reason;
			$scope.eventData.notes = data.eventData.notes;

			// either load the patient data and init the autocomplete
			// or ensure the patient model is clear
			$scope.initPatientAutocomplete(data.eventData.demographicNo).then(function() {
				$scope.initialized = true;
			});
			$scope.selectedSiteName = data.eventData.site;
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

		controller.changeTab(controller.tabEnum.appointment);
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

	// Make a list of the types of appointments available for this appointment
	$scope.setActiveTemplateEvents = function setActiveTemplateEvents()
	{
		// Get templates that happen during the time period
		var momentStart = Juno.Common.Util.getDateAndTimeMoment(
			$scope.eventData.startDate, $scope.formattedTime($scope.eventData.startTime));
		var momentEnd = Juno.Common.Util.getDateAndTimeMoment(
			$scope.eventData.endDate, $scope.formattedTime($scope.eventData.endTime));

		var activeEvents = [];

		// Loop through the events for this day
		for(var i = 0; i < data.events.length; i++)
		{
			if(data.events[i].rendering != "background" || data.events[i].resourceId != $scope.schedule.uuid)
			{
				continue;
			}

			var event = angular.copy(data.events[i]);

			// if start time is before event end time or if end time is after event start
			event.start = Juno.Common.Util.getDatetimeNoTimezoneMoment(event.start);
			event.end = Juno.Common.Util.getDatetimeNoTimezoneMoment(event.end);

			if(momentStart.isValid() && momentEnd.isValid() &&
				event.start.isValid() && event.end.isValid() &&
				momentStart.isBefore(event.end) && momentEnd.isAfter(event.start))
			{
				event.availabilityType = data.availabilityTypes[event.scheduleTemplateCode];
				activeEvents.push(event);
			}
		}

		$scope.activeTemplateEvents = activeEvents;
	};

	$scope.adjustEndDatetime = function adjustEndDatetime(lengthMinutes)
	{
		// adjusts the end time to the specified length or
		// adjusts to keep the event length the same as it last was

		var momentStart = Juno.Common.Util.getDateAndTimeMoment(
			$scope.eventData.startDate, $scope.formattedTime($scope.eventData.startTime));

		if(momentStart.isValid())
		{
			var newEventLength = Juno.Common.Util.exists(lengthMinutes) ?
				lengthMinutes : $scope.lastEventLength;

			var momentEnd = momentStart.add(newEventLength, 'minutes');

			$scope.eventData.endDate = Juno.Common.Util.formatMomentDate(momentEnd);
			$scope.eventData.endTime = Juno.Common.Util.formatMomentTime(momentEnd, $scope.timepickerFormat);
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
			$scope.displayMessages, 'startDate', 'Start Time', true);

		Juno.Common.Util.validateTimeString($scope.formattedTime($scope.eventData.startTime),
			$scope.displayMessages, 'startTime', 'Start Time', true);

		Juno.Common.Util.validateDateString($scope.eventData.endDate,
			$scope.displayMessages, 'endDate', 'End Time', true);

		Juno.Common.Util.validateTimeString($scope.formattedTime($scope.eventData.endTime),
			$scope.displayMessages, 'endTime', 'End Time', true);

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


		parentScope.saveEvent(
			editMode,
			{
				appointmentNo: $scope.eventUuid,
				startTime: startDatetime,
				endTime: endDatetime,
				reason: $scope.eventData.reason,
				notes: $scope.eventData.notes,
				providerNo: $scope.schedule.uuid,
				eventStatusCode: $scope.selectedEventStatus.displayLetter,
				demographicNo: $scope.demographicModel.demographicNo,
				site: $scope.selectedSiteName
			}
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
	$scope.$watch('startDate', function(newStartDate, oldStartDate)
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
	$scope.$watch('startTime', function(newStartTime, oldStartTime)
	{
		// avoid running first time this fires during initialization
		if(newStartTime !== oldStartTime)
		{
			$scope.setActiveTemplateEvents();
			$scope.adjustEndDatetime();
		}
	});

	// when the end date is changed, track the event length
	$scope.$watch('endDate', function(newEndDate, oldEndDate)
	{
		// avoid running first time this fires during initialization
		if(newEndDate !== oldEndDate)
		{
			$scope.setActiveTemplateEvents();
			$scope.updateLastEventLength();
		}
	});

	// when the end time is changed, track the event length
	$scope.$watch('endTime', function(newEndTime, oldEndTime)
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

	$scope.hasSites = function hasSites()
	{
		return (parentScope.siteOptions.length > 0)
	}

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
			console.log($scope.displayMessages.field_errors()['location']);
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

	$scope.newDemographic = function newDemographic(size)
	{
		var modalInstance = $uibModal.open(
			{
				templateUrl: 'src/patient/newPatient.jsp',
				controller: 'Patient.NewPatientController as newPatientCtrl',
				backdrop: 'static',
				size: size
			});

		modalInstance.result.then(
			function success(results)
			{
				console.log(results);
				console.log('patient #: ', results.demographicNo);

				$scope.initPatientAutocomplete(results.demographicNo)
			},
			function error(errors)
			{
				console.log('Modal dismissed at: ' + new Date());
				console.log(errors);
			});

		console.log($('#myModal'));
	};

	controller.changeTab = function changeTab(tabId)
	{
		controller.activeTab = tabId;
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
