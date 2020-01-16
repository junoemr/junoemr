'use strict';


//=========================================================================
// Calendar Event Controller
//=========================================================================/

import {ScheduleApi} from "../../generated/api/ScheduleApi";
import {AppointmentApi} from "../../generated/api/AppointmentApi";
import {SitesApi} from "../../generated";

angular.module('Schedule').component('eventComponent', {
	templateUrl: "src/schedule/event.jsp",
	bindings: {
		modalInstance: "<", // modalInstance is the parent $uibModalInstance
		resolve: "<",
	},
	controllerAs: "eventController",
	controller: [
		'$scope',
		'$q',
		'$http',
		'$httpParamSerializer',
		'$timeout',
		'$state',
		'$uibModal',
		'errorsService',
		'demographicService',
		'providerService',
		'securityService',
		'scheduleService',

		function (
			$scope,
			$q,
			$http,
			$httpParamSerializer,
			$timeout,
			$state,
			$uibModal,
			messagesFactory,
			demographicService,
			providerService,
			securityService,
			scheduleService,
		)
		{

			let controller = this;

			$scope.scheduleApi = new ScheduleApi($http, $httpParamSerializer,
				'../ws/rs');

			$scope.appointmentApi = new AppointmentApi($http, $httpParamSerializer,
				'../ws/rs');

			let sitesApi = new SitesApi($http, $httpParamSerializer, '../ws/rs');
			//=========================================================================
			// Access Control
			//=========================================================================/


			//=========================================================================
			// Local scope variables
			//=========================================================================/

			controller.useOldEchart = true;
			controller.tabEnum = Object.freeze({
				appointment: 0,
				repeatBooking: 1,
				history: 2,
			});
			controller.activeTab = controller.tabEnum.appointment;

			controller.appointmentTypeList = [];
			controller.reasonCodeList = [];

			$scope.eventUuid = null;

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
				virtual: false,
			};

			controller.repeatBooking =
				{
					disabled: !securityService.getUser().superAdmin,
					max_bookings_limit: 100,
					toggleEnum: Object.freeze({
						on: 'on',
						off: 'off',
					}),
					endTypeEnum: Object.freeze({
						date: 'date',
						after: 'after',
					}),
					intervalOptions: [
						// interval values should match moment.js time units
						{
							label: 'days',
							value: 'days'
						},
						{
							label: 'weeks',
							value: 'weeks'
						},
						{
							label: 'months',
							value: 'months'
						},
					],
					frequencyOptions: [
						{
							label: '1x',
							value: 1
						},
						{
							label: '2x',
							value: 2
						},
						{
							label: '3x',
							value: 3
						},
					],
				};
			controller.repeatBookingData = {
				enabled: controller.repeatBooking.toggleEnum.off,
				frequency: controller.repeatBooking.frequencyOptions[0].value,
				interval: controller.repeatBooking.intervalOptions[0].value,
				endDate: Juno.Common.Util.formatMomentDate(moment().add(1, 'days')),
				endAfterNumber: 1,
				endType: controller.repeatBooking.endTypeEnum.date,
			};
			controller.repeatBookingDates = null;

			controller.eventHistory = [];

			controller.patientTypeahead = {};
			$scope.autocompleteValues = {};

			controller.eventStatuses = scheduleService.eventStatuses;

			$scope.eventStatusOptions = [];
			controller.selectedEventStatus = null;
			$scope.defaultEventStatus = null;

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
			controller.siteOptions = [];

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

				clear: function clear()
				{
					this.demographicNo = null;
					this.data = {};
				},
				fillData: function fillData(data)
				{
					this.data = data;
					this.demographicNo = data.demographicNo;
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
			};

			//=========================================================================
			// Init
			//=========================================================================/

			controller.$onInit = function init()
			{
				if (!securityService.hasPermission('scheduling_create'))
				{
					$timeout(function ()
					{
						$scope.cancel();
					});
				}

				// resolve data from opener
				controller.loadedSettings = controller.resolve.loadedSettings;
				controller.parentScope = controller.resolve.parentScope;
				controller.keyBinding = controller.resolve.keyBinding;
				controller.editMode = controller.resolve.editMode;
				var focus = controller.resolve.focus;
				var data = controller.resolve.data;

				$scope.events = data.events;
				$scope.scheduleId = data.scheduleId;

				controller.sitesEnabled = controller.parentScope.hasSites();

				controller.keyBinding.bindKeyGlobal("ctrl+enter", $scope.keyBindSettings["ctrl+enter"]);
				controller.keyBinding.bindKeyGlobal("ctrl+shift+enter", $scope.keyBindSettings["ctrl+shift+enter"]);


				// load required data
				controller.loadAppointmentReasons();
				controller.loadAppointmentTypes();
				controller.providerModel.loadData(data.scheduleId);

				var momentStart = data.startTime;
				var momentEnd = data.endTime;
				$scope.eventData.startTime = Juno.Common.Util.formatMomentTime(momentStart, $scope.timepickerFormat);
				$scope.eventData.startDate = Juno.Common.Util.formatMomentDate(momentStart);

				for (var key in controller.eventStatuses)
				{
					if (controller.eventStatuses.hasOwnProperty(key))
					{
						$scope.eventStatusOptions.push(controller.eventStatuses[key]);
					}
				}
				$scope.defaultEventStatus = data.defaultEventStatus;
				controller.setSelectedEventStatus(data.eventData.eventStatusCode);
				$scope.eventData.eventStatusModifier = data.eventData.eventStatusModifier;
				$scope.eventData.duration = momentEnd.diff(momentStart, 'minutes');

				if (controller.editMode)
				{
					$scope.eventUuid = data.eventData.appointmentNo;
					$scope.eventData.reason = data.eventData.reason;
					$scope.eventData.notes = data.eventData.notes;
					$scope.eventData.type = data.eventData.type;
					$scope.eventData.reasonCode = data.eventData.reasonCode;
					$scope.eventData.doNotBook = data.eventData.doNotBook;
					$scope.eventData.critical = data.eventData.urgency === 'critical';
					$scope.eventData.site = data.eventData.site;
					$scope.eventData.virtual = data.eventData.virtual;

					controller.checkEventConflicts(); // uses the eventData

					// either load the patient data and init the autocomplete
					// or ensure the patient model is clear
					controller.demographicModel.loadData(data.eventData.demographicNo).then(
						function success()
						{
							if ($scope.isPatientSelected())
							{
								controller.patientTypeahead = controller.demographicModel.data;
							}
							else
							{
								// to initialize typeahead value without a selected demographic model
								controller.patientTypeahead.isTypeaheadSearchQuery = true;
								controller.patientTypeahead.searchQuery = data.eventData.appointmentName;
							}

							$timeout(controller.loadWatches);
							$scope.initialized = true;
						});
					controller.loadAppointmentHistory($scope.eventUuid);
				}
				else //create new
				{
					// clear the patient model
					controller.demographicModel.clear();
					$scope.eventData.site = controller.parentScope.selectedSiteName;

					focus.element("#input-patient");
					controller.checkEventConflicts(); // uses the eventData

					$timeout(controller.loadWatches);
					$scope.initialized = true;
				}

				controller.changeTab(controller.tabEnum.appointment);

				sitesApi.getSitesByProvider(controller.providerModel.providerNo).then(
						function success(results)
						{
							// get all sites assigned to the provider on which the appointment is to be booked.
							controller.siteOptions = [];
							for(let site of results.data.body)
							{
								if (site.siteId != null)
								{
									controller.siteOptions.push({
										label: site.name,
										value: site.name,
										uuid: site.siteId,
										color: site.bgColor,
									});
								}
							}

							sitesApi.getSitesByProvider(securityService.getUser().providerNo).then(
									function success(userSites)
									{
										// filter out sites that the current user is not assigned to.
										let filteredSites = [];
										for(let site of controller.siteOptions)
										{
											if (userSites.data.body.find(el => el.name === site.value))
											{
												filteredSites.push(site);
											}
										}
										controller.siteOptions = filteredSites;

										if (controller.sitesEnabled && !controller.isValidSiteValue($scope.eventData.site))
										{
											// get site for the provider being booked on to.
											sitesApi.getProviderSiteBySchedule(controller.providerModel.providerNo, $scope.eventData.startDate).then(
													function success(result)
													{// assign to schedule site that we are booking in to.
														let site = controller.siteOptions.find(el => el.uuid === result.data.body.siteId);
														if (site)
														{
															$scope.eventData.site = site.value;
														}
														else
														{
															controller.assignDefaultSite();
														}
													},
													function error(result)
													{
														controller.assignDefaultSite();
													}
											);
										}
									},
									function error(result)
									{
										console.error("Failed to lookup sites for the current user, with error: " + result);
									}
							);

						},
						function error(results)
						{
							console.error("Failed to get provider Site assignment with error: " + results);
						}
				);
			};

			//=========================================================================
			// Private methods
			//=========================================================================/

			//assign a default site
			controller.assignDefaultSite = function()
			{
				// set default site selection
				if (controller.siteOptions[0])
				{
					$scope.eventData.site = controller.siteOptions[0].value;
				}
				else if (controller.siteOptions.length === 0)
				{// no sites available
					let noSitesSite = {
						label: 	"No Sites Available",
						value: 	"No Sites Available",
						uuid: 	null,
						color: 	null,
					};
					controller.siteOptions = [noSitesSite];
					$scope.eventData.site =  "No Sites Available";
				}
			};


			controller.setSelectedEventStatus = function setSelectedEventStatus(selectedCode)
			{
				var eventStatusCode = $scope.defaultEventStatus;

				if (Juno.Common.Util.exists(selectedCode))
				{
					eventStatusCode = selectedCode;
				}

				if (!Juno.Common.Util.exists(eventStatusCode) ||
					!Juno.Common.Util.exists(controller.eventStatuses[eventStatusCode]))
				{
					// if not set or found just pick the first one
					eventStatusCode = $scope.eventStatusOptions[0].displayLetter;
				}

				controller.selectedEventStatus = eventStatusCode;
			};

			controller.loadAppointmentReasons = function loadAppointmentReasons()
			{
				var deferred = $q.defer();

				$scope.scheduleApi.getAppointmentReasons().then(
					function success(rawResults)
					{
						var results = rawResults.data.body;
						var out = [];
						if (angular.isArray(results))
						{
							for (var i = 0; i < results.length; i++)
							{
								out.push({
									label: results[i].label,
									value: results[i].id,
								});
							}
						}
						controller.reasonCodeList = out;

						// set the default selected option
						if (!Juno.Common.Util.exists($scope.eventData.reasonCode))
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
						if (angular.isArray(results))
						{
							for (var i = 0; i < results.length; i++)
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
			controller.getTypeDataByTypeValue = function (typeValue)
			{
				var data = {};
				for (var i = 0; i < controller.appointmentTypeList.length; i++)
				{
					if (controller.appointmentTypeList[i].value === typeValue)
					{
						data = controller.appointmentTypeList[i].data;
						break;
					}
				}
				return data;
			};

			controller.checkEventConflicts = function ()
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

				if (momentStart.isValid() && momentEnd.isValid() && momentStart.isSameOrBefore(momentEnd))
				{
					// Loop through the events for this day
					for (var i = 0; i < $scope.events.length; i++)
					{
						var event = $scope.events[i];

						// filter events that should not be checked (background, wrong schedule, etc.)
						if (event.rendering === "background"
							|| event.resourceId != $scope.scheduleId
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
			controller.loadAppointmentHistory = function (appointmentId)
			{
				var deferred = $q.defer();

				$scope.appointmentApi.getEditHistory(appointmentId).then(
					function success(results)
					{
						controller.eventHistory = results.data.body;
						var date_format = 'DD MMMM YYYY';
						var time_format = 'hh:mm A';

						for (var i = 0; i < controller.eventHistory.length; i++)
						{
							controller.eventHistory[i].formattedUpdateDate = Juno.Common.Util.formatMomentDate(moment(controller.eventHistory[i].updateDateTime), date_format);
							controller.eventHistory[i].formattedCreateDate = Juno.Common.Util.formatMomentDate(moment(controller.eventHistory[i].createDateTime), date_format);

							controller.eventHistory[i].formattedUpdateTime = Juno.Common.Util.formatMomentTime(moment(controller.eventHistory[i].updateDateTime), time_format);
							controller.eventHistory[i].formattedCreateTime = Juno.Common.Util.formatMomentTime(moment(controller.eventHistory[i].createDateTime), time_format);
						}
						deferred.resolve(controller.eventHistory);
					}
				);

				return deferred.promise;
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

				if (controller.sitesEnabled && !controller.isValidSiteValue($scope.eventData.site))
				{
					$scope.displayMessages.add_field_error('site', "A valid site must be selected");
				}

				Juno.Common.Util.validateIntegerString(controller.repeatBookingData.endAfterNumber,
					$scope.displayMessages, 'repeatEndAfterNumber', 'Repeat End After', false, true, false);

				Juno.Common.Util.validateDateString(controller.repeatBookingData.endDate,
					$scope.displayMessages, 'repeatEndOnDate', 'Repeat End Date', false);

				return !$scope.displayMessages.has_errors();
			};

			controller.updateRepeatBookingDates = function updateRepeatBookingDates()
			{
				controller.repeatBookingDates = [];
				if(controller.isRepeatBookingEnabled())
				{
					controller.repeatBookingDates = controller.generateRepeatBookingDateList(controller.repeatBooking.max_bookings_limit);
				}
			};
			controller.removeRepeatBookingDate = function removeRepeatBookingDate(dataObj)
			{
				controller.repeatBookingDates = controller.repeatBookingDates.filter(function(e) { return e !== dataObj })
			};

			controller.generateRepeatBookingDateList = function generateRepeatBookingDateList(limit)
			{
				var dateList = [];
				var startDate = moment($scope.eventData.startDate);
				var endDate = moment(controller.repeatBookingData.endDate);
				var maxRepeats = controller.repeatBookingData.endAfterNumber;

				var interval = controller.repeatBookingData.interval;
				var frequency = controller.repeatBookingData.frequency;

				var bUseEndDate = controller.isRepeatBookingEndTypeDate();
				var bUseMaxRepeat = controller.isRepeatBookingEndTypeAfter();
				$scope.displayMessages.remove_field_error('repeatEndAfterNumber');
				$scope.displayMessages.remove_field_error('repeatEndOnDate');

				var count = 0;
				var lastDate = startDate;
				while(true)
				{
					var nextDate = lastDate.add(frequency, interval);
					count += 1;

					if((bUseMaxRepeat && count > maxRepeats) || (bUseEndDate && nextDate.isAfter(endDate, 'day')))
					{
						break;
					}
					if(count > limit)
					{
						if(bUseMaxRepeat)
						{
							$scope.displayMessages.add_field_error('repeatEndAfterNumber', "limit of " + limit);
						}
						if(bUseEndDate)
						{
							$scope.displayMessages.add_field_error('repeatEndOnDate', "limit of " + limit);
						}
						break;
					}

					dateList.push(Juno.Common.Util.formatMomentDate(nextDate));
					lastDate = nextDate;
				}
				return dateList;
			};

			$scope.saveEvent = function saveEvent()
			{
				var deferred = $q.defer();

				var startDatetime = Juno.Common.Util.getDateAndTimeMoment(
					$scope.eventData.startDate, $scope.formattedTime($scope.eventData.startTime));

				var endDatetime = controller.calculateEndTime();

				var demographicNo = ($scope.eventData.doNotBook) ? null : controller.demographicModel.demographicNo;
				var appointmentName = (demographicNo == null && Juno.Common.Util.exists(controller.patientTypeahead.searchQuery)) ?
					controller.patientTypeahead.searchQuery : null;

				var repeatOnDates = null;
				if(controller.isRepeatBookingEnabled())
				{
					repeatOnDates = controller.repeatBookingDates;
				}
				controller.parentScope.saveEvent(
					controller.editMode,
					{
						appointmentNo: $scope.eventUuid,
						startTime: startDatetime,
						endTime: endDatetime,
						type: $scope.eventData.type,
						reason: $scope.eventData.reason,
						reasonCode: $scope.eventData.reasonCode,
						notes: $scope.eventData.notes,
						providerNo: $scope.scheduleId,
						eventStatusCode: controller.selectedEventStatus,
						eventStatusModifier: $scope.eventData.eventStatusModifier,
						demographicNo: demographicNo,
						appointmentName: appointmentName,
						site: $scope.eventData.site,
						doNotBook: $scope.eventData.doNotBook,
						urgency: (($scope.eventData.critical) ? 'critical' : null),
						virtual: $scope.eventData.virtual,
					},
					repeatOnDates,

				).then(
					function (results)
					{
						if (controller.parentScope.processSaveResults(results, $scope.displayMessages))
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
						controller.parentScope.processSaveResults(results, $scope.displayMessages);
						deferred.reject();
					});

				return deferred.promise;
			};

			$scope.deleteEvent = function deleteEvent()
			{
				var deferred = $q.defer();

				controller.parentScope.deleteEvent($scope.eventUuid).then(function ()
				{
					deferred.resolve();

				}, function ()
				{
					deferred.reject();
				});

				return deferred.promise;
			};

			$scope.formattedTime = function formattedTime(time_str)
			{
				// the time picker format is HH:MM AM - need to strip spaces
				return time_str.replace(/ /g, '');
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

			controller.autofillDataFromType = function (typeValue)
			{
				var typeData = controller.getTypeDataByTypeValue(typeValue);

				if (Juno.Common.Util.exists(typeData.duration) &&
					typeData.duration > 0)
				{
					$scope.eventData.duration = typeData.duration;
				}
				if (Juno.Common.Util.exists(typeData.location) &&
					controller.isValidSiteValue(typeData.location))
				{
					$scope.eventData.site = typeData.location;
				}
				if (Juno.Common.Util.exists(typeData.notes) &&
					!Juno.Common.Util.isBlank(typeData.notes))
				{
					$scope.eventData.notes = typeData.notes;
				}
				if (Juno.Common.Util.exists(typeData.reason) &&
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
				$scope.$watch('eventController.patientTypeahead', function (newValue, oldValue)
				{
					if (newValue != oldValue)
					{
						$scope.loadPatientFromTypeahead(controller.patientTypeahead);
					}
				}, true);
				$scope.$watch('[eventData.startTime, eventData.duration]', function (newValue, oldValue)
				{
					if (newValue != oldValue)
					{
						controller.checkEventConflicts();
					}
				});
				$scope.$watch('eventData.type', function (newValue, oldValue)
				{
					if (newValue != oldValue)
					{
						controller.autofillDataFromType(newValue);
					}
				});
				$scope.$watch('[' +
					'eventController.repeatBookingData.enabled,' +
					'eventController.repeatBookingData.frequency,' +
					'eventController.repeatBookingData.interval,' +
					'eventController.repeatBookingData.endType, ' +
					'eventController.repeatBookingData.endDate,' +
					'eventController.repeatBookingData.endAfterNumber' +
					']',
					function (newValue, oldValue)
					{
						if (newValue !== oldValue)
						{
							controller.updateRepeatBookingDates();
						}
					});
			};

			//=========================================================================
			// Public methods
			//=========================================================================/

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
			controller.hasAppointmentId = function hasAppointmentId()
			{
				return Juno.Common.Util.exists($scope.eventUuid);
			};
			controller.inEditMode = function inEditMode()
			{
				return controller.editMode;
			};
			controller.isRepeatBookingEnabled = function isRepeatBookingEnabled()
			{
				return (!controller.inEditMode() && controller.repeatBookingData.enabled === controller.repeatBooking.toggleEnum.on);
			};
			controller.isRepeatBookingEndTypeDate = function isRepeatBookingEndTypeDate()
			{
				return controller.repeatBookingData.endType === controller.repeatBooking.endTypeEnum.date;
			};
			controller.isRepeatBookingEndTypeAfter = function isRepeatBookingEndTypeAfter()
			{
				return controller.repeatBookingData.endType === controller.repeatBooking.endTypeEnum.after;
			};

			$scope.hasSites = function hasSites()
			{
				return (controller.siteOptions.length > 0)
			};
			controller.isValidSiteValue = function (valueToTest)
			{
				for (var i = 0; i < controller.siteOptions.length; i++)
				{
					if (controller.siteOptions[i].value === valueToTest && controller.siteOptions[i].uuid !== null)
					{
						return true;
					}
				}
				return false;
			};
			controller.showPatientChartLinks = function showPatientChartLinks()
			{
				return (controller.hasAppointmentId() && $scope.isPatientSelected());
			};

			controller.changeTab = function changeTab(tabId)
			{
				controller.activeTab = tabId;
			};
			controller.isTabActive = function (tabId)
			{
				return (tabId === controller.activeTab);
			};

			$scope.clearPatient = function clearPatient()
			{
				$scope.autocompleteValues.patient = null;
				controller.demographicModel.clear();
			};

			controller.save = function save()
			{
				if (!$scope.validateForm())
				{
					return false;
				}

				$scope.working = true;
				$scope.saveEvent().then(function ()
				{
					controller.parentScope.refetchEvents();
					controller.modalInstance.close();
					$scope.working = false;
				}, function ()
				{
					console.log($scope.displayMessages.field_errors()['location']);
					if (!$scope.displayMessages.has_standard_errors())
					{
						$scope.displayMessages.add_generic_fatal_error();
					}
					$scope.working = false;
				});
			};

			controller.del = function del()
			{
				$scope.working = true;
				$scope.deleteEvent().then(function ()
				{
					controller.parentScope.refetchEvents();
					controller.modalInstance.close();
					$scope.working = false;
				}, function ()
				{
					$scope.displayMessages.add_generic_fatal_error();
					$scope.working = false;
				});
			};

			controller.cancel = function cancel()
			{
				controller.modalInstance.dismiss('cancel');
			};

			controller.saveAndBill = function saveAndBill()
			{
				if (!$scope.validateForm())
				{
					return false;
				}

				$scope.working = true;
				$scope.saveEvent().then(function ()
				{
					controller.parentScope.refetchEvents();
					controller.modalInstance.close();
					$scope.working = false;
					controller.parentScope.openCreateInvoice(
						$scope.eventUuid,
						$scope.scheduleId,
						controller.demographicModel.demographicNo);
				}, function ()
				{
					$scope.displayMessages.add_generic_fatal_error();
					$scope.working = false;
				});
			};

			controller.saveAndPrint = function saveAndPrint()
			{
				if (!$scope.validateForm())
				{
					return false;
				}

				$scope.working = true;
				$scope.saveEvent().then(function (response)
				{
					controller.parentScope.refetchEvents();
					controller.modalInstance.close();
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
				}, function ()
				{
					$scope.displayMessages.add_generic_fatal_error();
					$scope.working = false;
				});
			};
			controller.saveAndReceipt = function saveAndPrint()
			{
				if (!$scope.validateForm())
				{
					return false;
				}

				$scope.working = true;
				$scope.saveEvent().then(function (response)
				{
					controller.parentScope.refetchEvents();
					controller.modalInstance.close();
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

				}, function ()
				{
					$scope.displayMessages.add_generic_fatal_error();
					$scope.working = false;
				});
			};
			controller.saveDoNotBook = function saveDoNotBook()
			{
				if (!$scope.validateForm())
				{
					return false;
				}

				$scope.working = true;
				$scope.clearPatient();
				$scope.eventData.doNotBook = true;
				$scope.saveEvent().then(function ()
				{
					controller.parentScope.refetchEvents();
					controller.modalInstance.close();
					$scope.working = false;
				}, function ()
				{
					$scope.displayMessages.add_generic_fatal_error();
					$scope.working = false;
				});
			};

			$scope.viewInvoices = function viewInvoices()
			{
				controller.parentScope.open_view_invoices($scope.eventUuid);
			};

			$scope.createPatient = function createPatient()
			{
				var editModeCallback = function ()
				{
					return false;
				};
				var onSaveCallback = function ()
				{
					return $scope.onPatientModalSave;
				};
				var loadErrorLinkPatientFn = function ()
				{
					return $scope.onPatientModalSave;
				};

				$scope.create_patient_dialog = controller.parentScope.calendar_api_adapter.openPatientDialog(
					editModeCallback, onSaveCallback, loadErrorLinkPatientFn);
			};

			$scope.modify_patient = function modify_patient()
			{
				if (!$scope.isPatientSelected())
				{
					return;
				}

				var editModeCallback = function ()
				{
					return true;
				};
				var onSaveCallback = function ()
				{
					return $scope.onPatientModalSave;
				};
				var loadErrorLinkPatientFn = function ()
				{
					return $scope.onPatientModalSave;
				};

				$scope.modify_patient_dialog = controller.parentScope.calendar_api_adapter.openPatientDialog(
					editModeCallback, onSaveCallback, loadErrorLinkPatientFn);
			};

			// for callback on create/edit patient modal
			$scope.onPatientModalSave = function onPatientModalSave(demographicNo)
			{
				// load the newly created/updated patient
				controller.demographicModel.loadData(demographicNo); //TODO why?
			};

			$scope.newDemographic = function newDemographic()
			{
				var modalInstance = $uibModal.open(
					{
						component: 'addDemographicModal',
						backdrop: 'static',
						windowClass: "juno-modal",
					}
				);

				modalInstance.result.then(
					function success(results)
					{
						console.log(results);
						console.log('patient #: ', results.demographicNo);

						controller.demographicModel.loadData(results.demographicNo);
						controller.patientTypeahead  = {isTypeaheadSearchQuery: true, searchQuery: results.lastName + ", " + results.firstName};
					},
					function error(errors)
					{
						console.log('Modal dismissed at: ' + new Date());
						console.log(errors);
					});

				console.log($('#myModal'));
			};

			controller.openEncounterPage = function ()
			{
				if ($scope.isPatientSelected())
				{
					if (controller.loadedSettings && controller.loadedSettings.hideOldEchartLinkInAppointment)
					{
						var params = {
							demographicNo: controller.demographicModel.demographicNo,
						};
						if (angular.isDefined($scope.eventUuid))
						{
							params.appointmentNo = $scope.eventUuid;
							params.encType = "face to face encounter with client";
						}
						$state.go('record.summary', params);
					}
					else
					{
						var params = {
							providerNo: controller.providerModel.providerNo,
							curProviderNo: controller.resolve.data.eventData.userProviderNo,
							demographicNo: controller.demographicModel.demographicNo,
							userName: "",
							reason: $scope.eventData.reason,
							curDate: Juno.Common.Util.formatMomentDate(moment()),
							providerview: controller.resolve.data.eventData.userProviderNo,

							appointmentNo: $scope.eventUuid,
							appointmentDate: $scope.eventData.startDate,
							startTime: $scope.eventData.startTime,
							status: controller.selectedEventStatus,
							apptProvider_no: controller.providerModel.providerNo,
							encType: "face to face encounter with client",
						};
						window.open(scheduleService.getEncounterLink(params), 'popupWindow',
								'height=800,width=1000,left=100,top=100,resizable=yes,scrollbars=yes,toolbar=no,menubar=no,location=no,directories=no');
					}
					controller.cancel();
				}
			};
			controller.openBillingPage = function ()
			{
				if ($scope.isPatientSelected() && Juno.Common.Util.exists($scope.eventUuid))
				{
					var params = {
						demographic_no: controller.demographicModel.demographicNo,
						demographic_name: controller.demographicModel.fullName,
						providerNo: controller.providerModel.providerNo,
						providerview: controller.providerModel.providerNo,
						user_no: controller.resolve.data.eventData.userProviderNo,

						billRegion: controller.resolve.data.eventData.billingRegion,
						billForm: controller.resolve.data.eventData.billingForm,
						hotclick: "",
						bNewForm: 1,

						apptProvider_no: controller.providerModel.providerNo,
						appointment_no: $scope.eventUuid,
						appointmentDate: $scope.eventData.startDate,
						status: controller.selectedEventStatus,
						start_time: $scope.eventData.startTime,

						referral_no_1: "",
					};
					window.open(scheduleService.getBillingLink(params));
					controller.cancel();
				}
			};
			controller.openMasterRecord = function ()
			{
				if ($scope.isPatientSelected())
				{
					var params = {
						demographicNo: controller.demographicModel.demographicNo,
					};
					$state.go('record.details', params);
					controller.cancel();
				}
			};

			controller.openRxWindow = function ()
			{
				var params = {
					demographicNo: controller.demographicModel.demographicNo,
					providerNo: controller.providerModel.providerNo,
				};
				window.open(scheduleService.getRxLink(params));
				controller.cancel();
			};

			controller.openTelehealthWindow = function openTelehealthWindow()
			{
				if (controller.demographicModel.demographicNo !== 0 && $scope.eventData.virtual)
				{
					window.open("../telehealth/myhealthaccess.do?method=openTelehealth"
						+ "&demographicNo=" + encodeURIComponent(controller.demographicModel.demographicNo)
						+ "&siteName=" + encodeURIComponent($scope.eventData.site)
						+ "&appt=" + encodeURIComponent($scope.eventUuid), "_blank");
				}
			};

			//=========================================================================
			//  Key Bindings
			//=========================================================================

			$scope.keyBindSettings = {
				"ctrl+enter": {
					title: 'Ctrl+Enter',
					tooltip: 'Save',
					description: 'Save appointment',
					callback_fn: function enter_callback()
					{
						if (!$scope.isWorking())
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
						if (!$scope.isWorking())
						{
							$scope.saveAndBill();
						}
					},
					target: null
				}
			};
		}
	]
});
