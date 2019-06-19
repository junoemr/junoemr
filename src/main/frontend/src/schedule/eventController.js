'use strict';


//=========================================================================
// Calendar Event Controller
//=========================================================================/

import {ScheduleApi} from "../../generated/api/ScheduleApi";

angular.module('Schedule').controller('Schedule.EventController', [

	'$scope',
	'$q',
	'$http',
	'$httpParamSerializer',
	'$timeout',
	'$state',
	'$uibModal',
	'$uibModalInstance',
	'errorsService',
	'demographicService',
	'providerService',
	'securityService',
	'keyBinding',
	'focus',
	'type', 'parentScope', 'label', 'editMode', 'data',

	function (
		$scope,
		$q,
		$http,
		$httpParamSerializer,
		$timeout, $state, $uibModal, $uibModalInstance,
		messagesFactory,
		demographicService,
		providerService,
		securityService,
		keyBinding,
		focus,
		type, parentScope, label, editMode, data
	)
{
	$scope.parentScope = parentScope;
	let controller = this;

	$scope.scheduleApi = new ScheduleApi($http, $httpParamSerializer,
		'../ws/rs');

	//=========================================================================
	// Access Control
	//=========================================================================/


	//=========================================================================
	// Local scope variables
	//=========================================================================/

	controller.tabEnum = Object.freeze({
		appointment:0,
		reoccurring:1,
		history:2
	});
	controller.activeTab = controller.tabEnum.appointment;

	controller.appointmentTypeList = [];
	controller.reasonCodeList = [];

	$scope.label = label;
	$scope.editMode = editMode;

	$scope.keyBinding = keyBinding;
	$scope.eventUuid = null;

	$scope.schedule = data.schedule;

	$scope.eventData = {
		startDate: null,
		startTime: null,
		reason: null,
		reasonCode: null,
		notes: null,
		type: null,
		duration: null,
		doNotBook: false,
		critical: false,
		site: null,
	};

	$scope.timeInterval = data.timeInterval;

	$scope.patientTypeahead = {};
	$scope.autocompleteValues = {};

	$scope.activeTemplateEvents = [];

	controller.eventStatuses = $scope.parentScope.eventStatuses;

	$scope.eventStatusOptions = [];
	controller.selectedEventStatus = null;
	$scope.defaultEventStatus = null;

	// filter site options to only include valid sites.
	controller.siteOptions = $scope.parentScope.siteOptions.filter(function(value, index, arr)
	{
		return (value.uuid != null);
	});



	controller.sitesEnabled = $scope.parentScope.hasSites();

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

	controller.isDoubleBook = false;
	controller.isDoubleBookPrevented = false;

	controller.providerModel = {
		providerNo: null,
		firstName: null,
		lastName: null,
		displayName: "",
		title: null,
		loadData: function loadData(id)
		{
			var model = this;
			model.providerNo = id;
			providerService.getProvider(id).then(
				function success(results)
				{
					model.firstName = results.firstName;
					model.lastName = results.lastName;
					model.title = 'Dr.'; //results.title;
					model.displayName = Juno.Common.Util.toTrimmedString(model.title) + ' ' +
						Juno.Common.Util.toTrimmedString(model.firstName) + ' ' +
						Juno.Common.Util.toTrimmedString(model.lastName);
				}
			);
		}
	};

	controller.demographicModel = {
		demographicNo: null,
		data: {},
		displayData: {
			birthDate: null,
			fullName: null,
			hasPhoto: true,
			patientPhotoUrl: '/imageRenderingServlet?source=local_client&clientId=0',
			addressLine: null,
		},

		clear: function clear()
		{
			this.demographicNo = null;
			this.data = {};
			this.displayData = {
				birthDate: null,
				fullName: null,
				hasPhoto: true,
				patientPhotoUrl: '/imageRenderingServlet?source=local_client&clientId=0',
				addressLine: null,
			};
		},
		fillData: function fillData(data)
		{
			this.data = data;

			this.demographicNo = data.demographicNo;
			this.displayData.fullName = Juno.Common.Util.formatName(data.firstName, data.lastName);
			this.displayData.patientPhotoUrl = '/imageRenderingServlet?source=local_client&clientId=' + (data.demographicNo? data.demographicNo: 0);

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
			this.displayData.birthDate = Juno.Common.Util.formatMomentDate(dateOfBirth);

			if(Juno.Common.Util.exists(data.address))
			{
				this.displayData.addressLine =
					Juno.Common.Util.noNull(data.address.address) + ' ' +
					Juno.Common.Util.noNull(data.address.city) + ' ' +
					Juno.Common.Util.noNull(data.address.province) + ' ' +
					Juno.Common.Util.noNull(data.address.postal);
			}
		},
		loadData: function loadData(demographicNo)
		{
			var deferred = $q.defer();

			if (Juno.Common.Util.exists(demographicNo) && demographicNo !== 0)
			{
				demographicService.getDemographic(demographicNo).then(
					function (data)
					{
						controller.demographicModel.fillData(data);
						deferred.resolve();
					},
					function (errors)
					{
						console.log('error initializing patient autocomplete', errors);
						controller.demographicModel.clear();
						deferred.resolve();
					});
			}
			else
			{
				controller.demographicModel.clear();
				deferred.resolve();
			}

			return deferred.promise;
		},
		uploadPhoto: function uploadPhoto(file){}
	};

	//=========================================================================
	// Init
	//=========================================================================/

	controller.init = function init()
	{
		if (!securityService.hasPermission('scheduling_create'))
		{
			$timeout(function ()
			{
				$scope.cancel();
			});
		}

		controller.loadAppointmentReasons();
		controller.loadAppointmentTypes();
		controller.providerModel.loadData(data.schedule.uuid);

		var momentStart = data.startTime;
		var momentEnd = data.endTime;
		$scope.eventData.startTime = Juno.Common.Util.formatMomentTime(momentStart, $scope.timepickerFormat);
		$scope.eventData.startDate = Juno.Common.Util.formatMomentDate(momentStart);

		// maintain a list of the 'active' templates based on start time
		$scope.setActiveTemplateEvents();

		for(var key in controller.eventStatuses)
		{
			if(controller.eventStatuses.hasOwnProperty(key))
			{
				$scope.eventStatusOptions.push(controller.eventStatuses[key]);
			}
		}
		$scope.defaultEventStatus = data.defaultEventStatus;
		controller.setSelectedEventStatus(data.eventData.eventStatusCode);

		if(editMode)
		{
			$scope.eventUuid = data.eventData.appointmentNo;
			$scope.eventData.reason = data.eventData.reason;
			$scope.eventData.notes = data.eventData.notes;
			$scope.eventData.type = data.eventData.type;
			$scope.eventData.reasonCode = data.eventData.reasonCode;
			$scope.eventData.doNotBook = data.eventData.doNotBook;
			$scope.eventData.critical = data.eventData.urgency === 'critical';
			$scope.eventData.duration = momentEnd.diff(momentStart, 'minutes');
			$scope.eventData.site = data.eventData.site;

			controller.checkEventConflicts(); // uses the eventData

			// either load the patient data and init the autocomplete
			// or ensure the patient model is clear
			controller.demographicModel.loadData(data.eventData.demographicNo).then(
				function success()
				{
					if ($scope.isPatientSelected())
					{
						$scope.patientTypeahead = controller.demographicModel.data;
					}
					else
					{
						// to initialize typeahead value without a selected demographic model
						$scope.patientTypeahead.isTypeaheadSearchQuery = true;
						$scope.patientTypeahead.searchQuery = data.eventData.appointmentName;
					}

					$timeout(controller.loadWatches);
					$scope.initialized = true;
				});
		}
		else
		{
			// create mode: adjust the end date (if needed)
			// and clear the patient model
			controller.demographicModel.clear();
			$scope.eventData.site = $scope.parentScope.selectedSiteName;
			// set the default site selection if the current one is invalid
			if(controller.sitesEnabled && !controller.isValidSiteValue($scope.eventData.site))
			{
				$scope.eventData.site = controller.siteOptions[0].value;
			}

			controller.setTimeAndDurationByTemplate($scope.activeTemplateEvents[0], parentScope.timeIntervalMinutes());

			focus.element("#input-patient");

			controller.checkEventConflicts(); // uses the eventData

			$timeout(controller.loadWatches);
			$scope.initialized = true;
		}

		controller.changeTab(controller.tabEnum.appointment);
	};

	//=========================================================================
	// Private methods
	//=========================================================================/

	controller.setSelectedEventStatus = function setSelectedEventStatus(selectedCode)
	{
		var eventStatusCode = $scope.defaultEventStatus;

		if(Juno.Common.Util.exists(selectedCode))
		{
			eventStatusCode = selectedCode;
		}

		if(!Juno.Common.Util.exists(eventStatusCode) ||
			!Juno.Common.Util.exists(controller.eventStatuses[eventStatusCode]))
		{
			// if not set or found just pick the first one
			eventStatusCode = $scope.eventStatusOptions[0].displayLetter;
		}

		controller.selectedEventStatus = eventStatusCode;
	};

	// Make a list of the types of appointments available for this appointment
	$scope.setActiveTemplateEvents = function setActiveTemplateEvents()
	{
		// Get templates that happen during the time period
		var momentStart = Juno.Common.Util.getDateAndTimeMoment(
			$scope.eventData.startDate, $scope.formattedTime($scope.eventData.startTime));
		var activeEvents = [];

		// Loop through the events for this day
		for(var i = 0; i < data.events.length; i++)
		{
			// filter events that should not be checked (non-background, wrong schedule, etc.)
			if(data.events[i].rendering !== "background" || data.events[i].resourceId != $scope.schedule.uuid)
			{
				continue;
			}

			var event = angular.copy(data.events[i]);

			// if start time is between event start and end
			event.start = Juno.Common.Util.getDatetimeNoTimezoneMoment(event.start);
			event.end = Juno.Common.Util.getDatetimeNoTimezoneMoment(event.end);

			if(momentStart.isValid() && event.start.isValid() && event.end.isValid() &&
				momentStart.isBefore(event.end) && momentStart.isSameOrAfter(event.start))
			{
				//TODO refactor availability type lists
				var extendedAvailabilityType = data.availabilityTypes[event.scheduleTemplateCode];
				if(Juno.Common.Util.exists(extendedAvailabilityType))
				{
					event.availabilityType = extendedAvailabilityType;
				}
				else
				{
					event.availabilityType.duration = event.availabilityType.preferredEventLengthMinutes;
				}
				activeEvents.push(event);
			}
		}

		$scope.activeTemplateEvents = activeEvents;
	};

	controller.setTimeAndDurationByTemplate = function setTimeAndDurationByTemplate(templateEvent, defaultDuration)
	{
		var duration = defaultDuration;
		if(Juno.Common.Util.exists(templateEvent) && Juno.Common.Util.exists(templateEvent.availabilityType))
		{
			$scope.eventData.startTime = Juno.Common.Util.formatMomentTime(templateEvent.start, $scope.timepickerFormat);

			var templateDuration = templateEvent.availabilityType.duration;
			if(Juno.Common.Util.exists(templateDuration)
				&& Juno.Common.Util.isIntegerString(templateDuration))
			{
				duration = templateDuration;
			}
		}
		$scope.eventData.duration = duration;
	};

	controller.loadAppointmentReasons = function loadAppointmentReasons()
	{
		var deferred = $q.defer();

		$scope.scheduleApi.getAppointmentReasons().then(
			function success(rawResults)
			{
				var results = rawResults.data.body;
				var out = [];
				if(angular.isArray(results))
				{
					for(var i = 0; i < results.length; i++)
					{
						out.push({
							label: results[i].label,
							value: results[i].id,
						});
					}
				}
				controller.reasonCodeList = out;

				// set the default selected option
				if(!Juno.Common.Util.exists($scope.eventData.reasonCode))
				{
					$scope.eventData.reasonCode = controller.reasonCodeList[0].value;
				}
				deferred.resolve(controller.reasonCodeList);
			});

		return deferred.promise;
	};

	controller.loadAppointmentTypes = function loadAppointmentTypes()
	{
		var deferred = $q.defer();

		$scope.scheduleApi.getAppointmentTypes().then(
			function success(rawResults)
			{
				var results = rawResults.data.body;
				var out = [];
				if(angular.isArray(results))
				{
					for(var i = 0; i < results.length; i++)
					{
						out.push({
							label: results[i].name,
							value: results[i].name,
							data: {
								id: results[i].id,
								location: results[i].location,
								duration: results[i].duration,
								notes: results[i].notes,
								reason: results[i].reason,
								resources: results[i].resources,
							}
						});
					}
				}
				controller.appointmentTypeList = out;
				deferred.resolve(controller.appointmentTypeList);
			});

		return deferred.promise;
	};
	controller.getTypeDataByTypeValue = function(typeValue)
	{
		var data = {};
		for(var i=0; i < controller.appointmentTypeList.length; i++)
		{
			if(controller.appointmentTypeList[i].value === typeValue)
			{
				data = controller.appointmentTypeList[i].data;
				break;
			}
		}
		return data;
	};

	controller.checkEventConflicts = function()
	{
		//TODO a better way to access the modal content window
		var modalContent = $(".modal-content");

		// Get templates that happen during the time period
		var momentStart = Juno.Common.Util.getDateAndTimeMoment(
			$scope.eventData.startDate, $scope.formattedTime($scope.eventData.startTime));
		var momentEnd = controller.calculateEndTime();

		controller.isDoubleBook = false;
		controller.isDoubleBookPrevented = false;
		modalContent.removeClass("double-book double-book-prevented");

		if(momentStart.isValid() && momentEnd.isValid() && momentStart.isSameOrBefore(momentEnd))
		{
			// Loop through the events for this day
			for (var i = 0; i < data.events.length; i++)
			{
				var event = data.events[i];

				// filter events that should not be checked (background, wrong schedule, etc.)
				if (event.rendering === "background"
					|| event.resourceId != $scope.schedule.uuid
					|| $scope.eventUuid == event.data.appointmentNo)
				{
					continue;
				}


				// if start time is between event start and end
				var eventStart = Juno.Common.Util.getDatetimeNoTimezoneMoment(event.start);
				var eventEnd = Juno.Common.Util.getDatetimeNoTimezoneMoment(event.end);
				var eventDoNotBook = event.data.doNotBook;

				if (eventStart.isValid() && eventEnd.isValid() &&
					((momentStart.isSameOrAfter(eventStart) && momentStart.isBefore(eventEnd)) ||
					(momentEnd.isAfter(eventStart) && momentEnd.isSameOrBefore(eventEnd))))
				{
					controller.isDoubleBook = true;
					if (eventDoNotBook)
					{
						controller.isDoubleBookPrevented = true;
						break;
					}
				}
			}

			if (controller.isDoubleBookPrevented)
			{
				modalContent.addClass("double-book-prevented");
			}
			else if (controller.isDoubleBook)
			{
				modalContent.addClass("double-book");
			}
		}
		else
		{
			console.warn("unable to check double booking, invalid event time/duration", momentStart, momentEnd);
		}
	};

	$scope.validateForm = function validateForm()
	{
		$scope.displayMessages.clear();

		Juno.Common.Util.validateDateString($scope.eventData.startDate,
			$scope.displayMessages, 'startDate', 'Session Date', true);

		Juno.Common.Util.validateTimeString($scope.formattedTime($scope.eventData.startTime),
			$scope.displayMessages, 'startTime', 'Start Time', true);

		Juno.Common.Util.validateIntegerString($scope.eventData.duration,
			$scope.displayMessages, 'duration', 'Duration', true, true, true);

		if(controller.sitesEnabled && !controller.isValidSiteValue($scope.eventData.site))
		{
			$scope.displayMessages.add_field_error('site', "A valid site must be selected");
		}

		return !$scope.displayMessages.has_errors();
	};

	$scope.saveEvent = function saveEvent()
	{
		var deferred = $q.defer();

		var startDatetime = Juno.Common.Util.getDateAndTimeMoment(
				$scope.eventData.startDate, $scope.formattedTime($scope.eventData.startTime));

		var endDatetime = controller.calculateEndTime();

		var demographicNo = ($scope.eventData.doNotBook)? null : controller.demographicModel.demographicNo;
		var appointmentName = (demographicNo == null && Juno.Common.Util.exists($scope.patientTypeahead.searchQuery))?
			$scope.patientTypeahead.searchQuery : null;

		parentScope.saveEvent(
			editMode,
			{
				appointmentNo: $scope.eventUuid,
				startTime: startDatetime,
				endTime: endDatetime,
				type: $scope.eventData.type,
				reason: $scope.eventData.reason,
				reasonCode: $scope.eventData.reasonCode,
				notes: $scope.eventData.notes,
				providerNo: $scope.schedule.uuid,
				eventStatusCode: controller.selectedEventStatus,
				demographicNo: demographicNo,
				appointmentName: appointmentName,
				site: $scope.eventData.site,
				doNotBook: $scope.eventData.doNotBook,
				urgency: (($scope.eventData.critical)? 'critical' : null),
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

	controller.calculateEndTime = function calculateEndTime()
	{
		var momentStart = Juno.Common.Util.getDateAndTimeMoment(
				$scope.eventData.startDate, $scope.formattedTime($scope.eventData.startTime));
		return momentStart.add($scope.eventData.duration, 'minutes');
	};

	$scope.loadPatientFromTypeahead = function loadPatientFromTypeahead(patientTypeahead)
	{
		controller.demographicModel.loadData(patientTypeahead.demographicNo);
	};

	controller.autofillDataFromType = function(typeValue)
	{
		var typeData = controller.getTypeDataByTypeValue(typeValue);

		if(Juno.Common.Util.exists(typeData.duration) &&
			typeData.duration > 0)
		{
			$scope.eventData.duration = typeData.duration;
		}
		if(Juno.Common.Util.exists(typeData.location) &&
			controller.isValidSiteValue(typeData.location))
		{
			$scope.eventData.site = typeData.location;
		}
		if(Juno.Common.Util.exists(typeData.notes) &&
			!Juno.Common.Util.isBlank(typeData.notes))
		{
			$scope.eventData.notes = typeData.notes;
		}
		if(Juno.Common.Util.exists(typeData.reason) &&
			!Juno.Common.Util.isBlank(typeData.reason))
		{
			$scope.eventData.reason = typeData.reason;
		}
	};

	//=========================================================================
	// Watches
	//=========================================================================/

	controller.loadWatches = function loadWatches()
	{
		$scope.$watch('patientTypeahead', function(newValue, oldValue)
		{
			if(newValue != oldValue)
			{
				$scope.loadPatientFromTypeahead($scope.patientTypeahead);
			}
		}, true);
		$scope.$watch('[eventData.startTime, eventData.duration]', function(newValue, oldValue)
		{
			if(newValue != oldValue)
			{
				controller.checkEventConflicts();
			}
		});
		$scope.$watch('eventData.type', function(newValue, oldValue)
		{
			if(newValue != oldValue)
			{
				controller.autofillDataFromType(newValue);
			}
		});
	};

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
		controller.demographicModel.hasPhoto = true;
		controller.demographicModel.uploadPhoto(file);
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
		return Juno.Common.Util.exists(controller.demographicModel.demographicNo);
	};

	$scope.hasSites = function hasSites()
	{
		return (controller.siteOptions.length > 0)
	};
	controller.isValidSiteValue = function(valueToTest)
	{
		for(var i=0; i < controller.siteOptions.length; i++)
		{
			if(controller.siteOptions[i].value === valueToTest)
			{
				return true;
			}
		}
		return false;
	};

	$scope.clearPatient = function clearPatient()
	{
		$scope.autocompleteValues.patient = null;
		controller.demographicModel.clear();
	};

	controller.save = function save()
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

	controller.del = function del()
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

	controller.cancel = function cancel()
	{
		$uibModalInstance.dismiss('cancel');
	};

	controller.saveAndBill = function saveAndBill()
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
				controller.demographicModel.demographicNo);
		}, function()
		{
			$scope.displayMessages.add_generic_fatal_error();
			$scope.working = false;
		});
	};

	controller.saveAndPrint = function saveAndPrint()
	{
		if(!$scope.validateForm())
		{
			return false;
		}

		$scope.working = true;
		$scope.saveEvent().then(function(response)
		{
			$scope.parentScope.refetchEvents();
			$uibModalInstance.close();
			$scope.working = false;

			if (Juno.Common.Util.exists(response) &&
				Juno.Common.Util.exists(response.body) &&
				Juno.Common.Util.exists(response.body.appointmentNo))
			{
				var win = window.open('../appointment/appointmentcontrol.jsp' +
					'?displaymode=PrintCard' +
					'&appointment_no=' + encodeURIComponent(response.body.appointmentNo),
					'printappointmentcard', 'height=700,width=1024,scrollbars=1');
				win.focus();
			}
			else
			{
				console.error('invalid response data', response);
			}
		}, function()
		{
			$scope.displayMessages.add_generic_fatal_error();
			$scope.working = false;
		});
	};
	controller.saveAndReceipt = function saveAndPrint()
	{
		if(!$scope.validateForm())
		{
			return false;
		}

		$scope.working = true;
		$scope.saveEvent().then(function(response)
		{
			$scope.parentScope.refetchEvents();
			$uibModalInstance.close();
			$scope.working = false;

			if (Juno.Common.Util.exists(response) &&
				Juno.Common.Util.exists(response.body) &&
				Juno.Common.Util.exists(response.body.appointmentNo))
			{
				var win = window.open('../appointment/printappointment.jsp' +
					'?appointment_no=' + encodeURIComponent(response.body.appointmentNo),
					'printappointment', 'height=700,width=1024,scrollbars=1');
				win.focus();
			}
			else
			{
				console.error('invalid response data', response);
			}

		}, function()
		{
			$scope.displayMessages.add_generic_fatal_error();
			$scope.working = false;
		});
	};
	controller.saveDoNotBook = function saveDoNotBook()
	{
		if(!$scope.validateForm())
		{
			return false;
		}

		$scope.working = true;
		$scope.clearPatient();
		$scope.eventData.doNotBook = true;
		$scope.saveEvent().then(function()
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
		if(!$scope.isPatientSelected())
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
		controller.demographicModel.loadData(demographicNo); //TODO why?
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

				controller.demographicModel.loadData(results.demographicNo);
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
