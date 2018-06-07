var CalendarApiAdapter = /** @class */ (function () {
    function CalendarApiAdapter($q, $stateParams, $uibModal, results_factory, util, demographicService, scheduleService, globalStateService) {
        this.$q = $q;
        this.$stateParams = $stateParams;
        this.$uibModal = $uibModal;
        this.results_factory = results_factory;
        this.util = util;
        this.demographicService = demographicService;
        this.scheduleService = scheduleService;
        this.globalStateService = globalStateService;
    }
    // TODO: implement this
    CalendarApiAdapter.prototype.get_global_state = function (key) {
        return;
    };
    CalendarApiAdapter.prototype.load_schedule_events = function (providerId, siteName, start, end, view_name, schedule_templates, event_statuses, default_event_color, availability_types) {
        //console.log("FETCHING: " + providerId + " " + start.format("YYYY-MM-DD") + " " + end.subtract(1, "seconds").format("YYYY-MM-DD"));
        var _this = this;
        var deferred = this.$q.defer();
        // Get date strings to pass to the backend.  The calendar provides datetime that describe
        // and inclusive start time and exclusive end time, so one second is removed from
        // the end time to convert to the correct date.
        var startDateString = start.format("YYYY-MM-DD");
        var endDateString = end.subtract(1, 'seconds').format("YYYY-MM-DD");
        this.scheduleService.getSchedulesForCalendar(providerId, startDateString, endDateString, siteName).then(function (results) {
            // Transform from camel to snake.  Normally this wouldn't need to happen, but
            // this is an external library that requires a certain format.
            deferred.resolve({ data: _this.snake_schedule_results(results) });
        });
        return deferred.promise;
    };
    CalendarApiAdapter.prototype.snake_appointment_data = function (data) {
        if (data == null) {
            return data;
        }
        return {
            schedule_uuid: data.scheduleUuid,
            event_status_uuid: data.eventStatusUuid,
            event_status_modifier: data.eventStatusModifier,
            start_time: data.startTime,
            end_time: data.endTime,
            site: data.site,
            reason: data.reason,
            num_invoices: data.numInvoices,
            demographics_patient_dob: data.demographicPatientDob,
            demographics_patient_name: data.demographicPatientName,
            demographics_patient_phone: data.demographicPatientPhone,
            demographics_patient_uuid: data.demographicPatientUuid,
            demographics_practitioner_uuid: data.demographicPractitionerUuid,
            tag_names: data.tagNames,
            tag_self_booked: data.tagSelfBooked,
            tag_self_cancelled: data.tagSelfCancelled,
            tag_system_codes: data.tagSystemCodes
        };
    };
    CalendarApiAdapter.prototype.snake_availability_type = function (data) {
        if (data == null) {
            return data;
        }
        return {
            name: data.name,
            color: data.color,
            preferred_event_length_minutes: data.preferredEventLengthMinutes,
            system_code: data.systemCode,
        };
    };
    CalendarApiAdapter.prototype.snake_schedule_results = function (results) {
        if (!angular.isArray(results)) {
            return results;
        }
        var snake_results = [];
        for (var i = 0; i < results.length; i++) {
            var result = results[i];
            snake_results.push({
                resourceId: result.resourceId,
                start: result.start,
                end: result.end,
                color: result.color,
                rendering: result.rendering,
                schedule_template_code: result.scheduleTemplateCode,
                className: result.className,
                availability_type: this.snake_availability_type(result.availabilityType),
                data: this.snake_appointment_data(result.data),
            });
        }
        return snake_results;
    };
    CalendarApiAdapter.prototype.load_schedule_templates = function () {
        // XXX: can't implement this because it requires the date range and provider number
        // because oscar stores it's templates per day and provider
        var deferred = this.$q.defer();
        var schedule_templates = {};
        var promise_array = [];
        deferred.resolve([]);
        /*				schedule_template_model.list().then(
                            function success(results)
                            {
                                for(var i = 0; i < results.data.length; i++)
                                {
                                    var result = results.data[i];
                                    promise_array.push(schedule_template_model.load(result.uuid));
                                }
                            }).then(
                            function success()
                            {
                                $q.all(promise_array).then(
                                    function success(results_array)
                                    {
                                        for(var i=0; i<results_array.length; i++)
                                        {
                                            var result = results_array[i].data;
                                            schedule_templates[result.uuid] = result;
                                        }
                                        deferred.resolve(schedule_templates);
                                    });
                            });*/
        return deferred.promise;
    };
    CalendarApiAdapter.prototype.load_availability_types = function () {
        var deferred = this.$q.defer();
        var availability_types = {};
        this.scheduleService.getScheduleTemplateCodes().then(function success(results) {
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
            for (var i = 0; i < results.length; i++) {
                var result = results[i];
                availability_types[result.code] = {
                    color: result.color,
                    name: result.description,
                    preferred_event_length_minutes: result.duration,
                    system_code: null,
                };
            }
            deferred.resolve(availability_types);
        });
        return deferred.promise;
    };
    CalendarApiAdapter.prototype.load_event_statuses = function () {
        var _this = this;
        var deferred = this.$q.defer();
        this.scheduleService.getAppointmentStatuses().then(function (results) {
            deferred.resolve(_this.snake_appointment_statuses(results));
        });
        return deferred.promise;
    };
    CalendarApiAdapter.prototype.load_sites = function () {
        var deferred = this.$q.defer();
        this.scheduleService.getSites().then(function success(results) {
            var out = [];
            if (angular.isArray(results)) {
                for (var i = 0; i < results.length; i++) {
                    out.push({
                        uuid: results[i].siteId,
                        name: results[i].name,
                        display_name: results[i].name,
                        color: results[i].bgColor,
                    });
                }
            }
            deferred.resolve(out);
        });
        return deferred.promise;
    };
    CalendarApiAdapter.prototype.snake_appointment_statuses = function (data) {
        if (!angular.isArray(data)) {
            return data;
        }
        var snake_data = [];
        for (var i = 0; i < data.length; i++) {
            snake_data.push({
                uuid: data[i].displayLetter,
                name: data[i].name,
                display_letter: data[i].displayLetter,
                color: data[i].color,
                icon: data[i].icon,
                rotates: data[i].rotates,
                system_code: data[i].systemCode,
                sort_order: data[i].sortOrder,
            });
        }
        return snake_data;
    };
    CalendarApiAdapter.prototype.load_schedule_options = function () {
        var deferred = this.$q.defer();
        this.scheduleService.getScheduleGroups().then(function success(results) {
            for (var i = 0; i < results.length; i++) {
                results[i].uuid = results[i].identifier;
            }
            deferred.resolve(results);
        });
        return deferred.promise;
    };
    CalendarApiAdapter.prototype.get_selected_schedule = function (schedule_options) {
        // priority: last used from global state, then preference setting,
        // then default (first in the list)
        var selected_uuid = null;
        if (this.$stateParams.default_schedule) {
            // Passed in url string
            selected_uuid = this.$stateParams.default_schedule;
        }
        else {
            selected_uuid = this.get_global_state('schedule_default');
        }
        if (selected_uuid === null) {
            selected_uuid = this.get_global_preference_setting('schedule_default');
        }
        if (this.util.exists(selected_uuid)) {
            // only choose it if it can be found in the options list
            for (var i = 0; i < schedule_options.length; i++) {
                if (selected_uuid === schedule_options[i].uuid) {
                    return schedule_options[i];
                }
            }
        }
        if (schedule_options.length > 0) {
            // select the first schedule in the list by default
            return schedule_options[0];
        }
        return null;
    };
    CalendarApiAdapter.prototype.get_selected_resources = function (resource_options) {
        return [];
        /*	XXX: probably not using this because we don't select resources in juno

        // priority: last used from global state, then preference setting,
        // then default (all the non-doctor schedules)

        var custom_resource_list =
            this.get_global_state('schedule_resources');

        if(custom_resource_list === null)
        {
            custom_resource_list = this.get_global_preference_setting(
                'schedule_resources');
        }

        return this.get_group_resources(
            custom_resource_list, resource_options);*/
    };
    /*	public get_group_resources(default_selections, schedules)
        {
            var has_defaults = this.util.exists(default_selections) && default_selections.length > 0;
    
            var selected_resources = [];
            for(var i = 0; i < schedules.length; i++)
            {
                var schedule_data = schedules[i];
    
                if(has_defaults &&
                    default_selections.indexOf(schedule_data.uuid) > -1)
                {
                    selected_resources.push(schedule_data);
                }
                else if(!has_defaults &&
                    !this.util.exists(schedule_data.demographics_practitioner_uuid))
                {
                    selected_resources.push(schedule_data);
                }
            }
    
            // if default selections list is set but the schedules in it no longer exist,
            // or if there are no settings and no non-doctor schedules,
            // then could end up with nothing selected, so select everything
            if(selected_resources.length === 0)
            {
                for(var j = 0; j < schedules.length; j++)
                {
                    selected_resources.push(schedule_data);
                }
            }
    
            return selected_resources;
        }*/
    CalendarApiAdapter.prototype.get_selected_time_interval = function (time_interval_options, default_time_interval) {
        // priority: last used from global state, then preference setting,
        // then default
        var selected_time_interval = null;
        var time_interval = this.get_global_state('schedule_time_interval');
        if (time_interval === null) {
            time_interval = this.get_global_preference_setting('schedule_time_interval');
        }
        if (this.util.exists(time_interval)) {
            // only choose it if it can be found in the options list
            for (var i = 0; i < time_interval_options.length; i++) {
                if (time_interval === time_interval_options[i]) {
                    selected_time_interval = time_interval_options[i];
                    break;
                }
            }
        }
        if (selected_time_interval === null) {
            return default_time_interval;
        }
        return selected_time_interval;
    };
    CalendarApiAdapter.prototype.get_schedule_min_time = function () {
        // restrict day view if user preferences are set
        /*				var min_time = service.get_global_preference_setting('schedule_min_time');
                        if (util.exists(min_time)) {
                            // format: HH24:MM:SS - expect HH24:MM in preference
                            return min_time + ":00";
                        }

                        return null;*/
        return "08:00";
    };
    CalendarApiAdapter.prototype.get_schedule_max_time = function () {
        /*				var max_time = service.get_global_preference_setting('schedule_max_time');
                        if(util.exists(max_time))
                        {
                            // format: HH24:MM:SS - expect HH24:MM in preference
                            return max_time + ":00";
                        }

                        return null;*/
        return "20:00";
    };
    CalendarApiAdapter.prototype.load_schedule = function (providerId) {
        var deferred = this.$q.defer();
        deferred.resolve({ data: providerId });
        return deferred.promise;
    };
    /*	public save_event(
            edit_mode,
            event_uuid,
            start_datetime,
            end_datetime,
            event_data,
            schedule_uuid,
            selected_event_status_uuid,
            patient_uuid
        )
        {
            var deferred = this.$q.defer();
    
            this.event_model.clear();
    
            if(edit_mode)
            {
                this.event_model.uuid = event_uuid;
            }
    
            angular.extend(
                this.event_model.data,
                event_data,
                {
                    start_time: start_datetime.toDate(),
                    end_time: end_datetime.toDate(),
                    schedule_uuid: schedule_uuid,
                    event_status_uuid: selected_event_status_uuid,
                    demographics_patient_uuid: patient_uuid
                });
    
            this.event_model.save().then(
                function()
                {
                    deferred.resolve(this.results_factory.factory());
                },
                function ()
                {
                    deferred.reject();
                });
    
            return deferred.promise;
        }
    
        public delete_event(event_uuid)
        {
            var deferred = this.$q.defer();
    
            this.event_model.del(event_uuid).then(function()
            {
                deferred.resolve();
    
            }, function()
            {
                deferred.reject();
            });
    
            return deferred.promise;
        }*/
    //-------------------------------------------------------------------------
    // Event Handlers
    //-------------------------------------------------------------------------/
    /*	public drop_event(uuid, schedule_uuid, delta)
        {
            var deferred = this.$q.defer();
    
            event_model.load(uuid).then(
                function success()
                {
                    event_model.data.start_time =
                        util.get_datetime_moment(event_model.data.start_time).add(
                            delta.asMilliseconds(), 'milliseconds');
    
                    event_model.data.end_time =
                        util.get_datetime_moment(event_model.data.end_time).add(
                            delta.asMilliseconds(), 'milliseconds');
    
                    event_model.data.schedule_uuid = schedule_uuid;
    
                    event_model.save().then(
                        function success()
                        {
                            deferred.resolve(angular.copy(event_model.data));
    
                        }, function error(errors)
                        {
                            deferred.reject(errors);
                        });
                });
    
            return deferred.promise;
        }
    
        public resize_event(uuid, delta)
        {
            var deferred = this.$q.defer();
    
            event_model.load(uuid).then(
                function success()
                {
                    event_model.data.end_time =
                        util.get_datetime_moment(event_model.data.end_time).add(
                            delta.asMilliseconds(), 'milliseconds');
    
                    event_model.save().then(
                        function success()
                        {
                            deferred.resolve(angular.copy(event_model.data));
    
                        }, function error(errors)
                        {
                            deferred.reject(errors);
                        });
                });
    
            return deferred.promise;
        }
    
        public rotate_event_status(uuid, rotate_statuses)
        {
            var deferred = this.$q.defer();
    
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
    
            return deferred.promise;
        }*/
    //-------------------------------------------------------------------------
    // Global State Manipulation
    //-------------------------------------------------------------------------/
    CalendarApiAdapter.prototype.get_global_preference_setting = function (key) {
        if (this.util.exists(this.globalStateService.preferences) &&
            this.util.exists(this.globalStateService.preferences.settings)) {
            return this.globalStateService.preferences.settings[key];
        }
        return null;
    };
    CalendarApiAdapter.prototype.get_global_setting = function (key) {
        var setting = this.globalStateService[key];
        if (!this.util.exists(setting)) {
            setting = null;
        }
        return setting;
    };
    CalendarApiAdapter.prototype.save_global_setting = function (key, value) {
        this.globalStateService[key] = value;
    };
    // -----------------------------------------------------------------------------------------------
    // Interface Actions
    // -----------------------------------------------------------------------------------------------
    CalendarApiAdapter.prototype.get_create_invoice_url = function (event_uuid, demographics_practitioner_uuid, demographics_patient_uuid) {
        var url = "#/invoice/create?schedule_event_uuid=" +
            encodeURIComponent(event_uuid);
        if (this.util.exists(demographics_patient_uuid)) {
            url += "&demographics_patient_uuid=" +
                encodeURIComponent(demographics_patient_uuid);
        }
        if (this.util.exists(demographics_practitioner_uuid)) {
            url += "&demographics_practitioner_uuid=" + encodeURIComponent(demographics_practitioner_uuid);
        }
        return url;
    };
    CalendarApiAdapter.prototype.get_patient_demographic_url = function (event) {
        return "#/patient/" + encodeURIComponent(event.data.demographics_patient_uuid) + "/view";
    };
    CalendarApiAdapter.prototype.get_create_chart_note_url = function (event) {
        return "#/patient/" + encodeURIComponent(event.data.demographics_patient_uuid) +
            "/chart_notes?event_uuid=" + encodeURIComponent(event.data.uuid);
    };
    CalendarApiAdapter.prototype.open_patient_dialog = function (edit_mode_callback, on_save_callback, load_error_link_patient_fn) {
        //global_state.enable_keyboard_shortcuts = false;
        return this.$uibModal.open({
            animation: false,
            backdrop: 'static',
            size: 'lg',
            controller: 'Invoice.Common.Patient.FormController',
            templateUrl: 'code/invoice/common/patient/quick_form.html',
            resolve: {
                edit_mode: edit_mode_callback,
                on_save_callback: on_save_callback,
                load_error_link_patient_fn: load_error_link_patient_fn
            }
        });
    };
    CalendarApiAdapter.prototype.get_schedule_height = function () {
        /*		// get the full window height, minus the header height, minus a buffer for schedule options
                var schedule_height = $(window).height() - $('#right-pane').offset().top - 80;
                if(schedule_height < $scope.min_height) {
                    schedule_height = $scope.min_height;
                }
                return schedule_height;*/
        return 814;
    };
    // ===============================================================================================
    // Private Methods
    // ===============================================================================================
    CalendarApiAdapter.prototype.create_schedule_event = function (event) {
        var event_status_color = this.event_statuses[event.event_status_uuid] ?
            this.event_statuses[event.event_status_uuid].color :
            this.default_event_color;
        return {
            data: event,
            start: this.util.get_datetime_moment(event.start_time),
            end: this.util.get_datetime_moment(event.end_time),
            color: event_status_color,
            className: this.event_class(event_status_color),
            resourceId: event.schedule_uuid
        };
    };
    CalendarApiAdapter.prototype.event_class = function (bg_color) {
        var red = parseInt(bg_color.substr(1, 2), 16);
        var green = parseInt(bg_color.substr(3, 2), 16);
        var blue = parseInt(bg_color.substr(5, 2), 16);
        var yiq = ((red * 299) + (green * 587) + (blue * 114)) / 1000;
        return (yiq >= 128) ? 'text-dark' : 'text-light';
    };
    CalendarApiAdapter.$inject = [
        '$q',
        '$stateParams',
        '$uibModal',
        'resultsService',
        'cpCalendar.Util',
        'demographicService',
        'scheduleService',
        'globalStateService',
    ];
    return CalendarApiAdapter;
}());
angular.module('Schedule').service('Schedule.CalendarApiAdapter', CalendarApiAdapter);
