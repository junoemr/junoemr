'use strict';

// Helper functions for cpCalendar
angular.module('cpCalendar').factory(
	'cpCalendar.Util',
	[

		function (
		) {
			var service = {
				settings: {
					datetime_format: "YYYY-MM-DDTHH:mm:ssZ",
					datetime_no_timezone_format: "YYYY-MM-DDTHH:mm:ss",
					time_format: "h:mma",
					date_format: "YYYY-MM-DD",
					dayofweek_format: "dddd"
				}
			};


			// ===============================================================================================
			// Public Methods
			// ===============================================================================================

			// Use this to check for existence of value.  It will allow anything that
			// we want to be written to a text box when prefilling
			service.exists = function exists(value)
			{
				// Don't allow undefined or null
				if (value === null || value === undefined)
				{
					return false;
				}

				// Don't allow empty strings
				if (value === '')
				{
					return false;
				}

				return true;
			};

			service.is_integer_string = function is_integer_string(string)
			{
				var parsed_string = parseInt(string);

				if (/^-?\d+$/.test(string.toString()))
				{
					return true;
				}

				return false;
			};

			service.escape_html = function escape_html(str)
			{
				var entityMap = {
					"&": "&amp;",
					"<": "&lt;",
					">": "&gt;",
					'"': '&quot;',
					"'": '&#39;',
					"/": '&#x2F;'
				};

				return String(str).replace(/[&<>"'\/]/g, function (s)
				{
					return entityMap[s];
				});
			};

			service.get_datetime_moment = function get_datetime_moment(datetime_string)
			{
				return moment.utc(datetime_string,
					service.settings.datetime_format, true);
			};

			service.get_datetime_no_timezone_moment = function get_datetime_no_timezone_moment(datetime_string)
			{
				return moment.utc(datetime_string,
					service.settings.datetime_no_timezone_format, true);
			};

			service.get_date_string = function get_date_string(moment)
			{
				return moment.format(service.settings.date_format);
			};

			service.get_date_moment = function get_date_moment(date_string)
			{
				return moment.utc(date_string, service.settings.date_format, true);
			};

			service.get_time_moment = function get_time_moment(time_string)
			{
				return moment.utc(time_string, service.settings.time_format, true);
			};

			service.get_date_and_time_moment = function get_combined_moment(date_string, time_string)
			{
				return moment.utc(date_string + " " + time_string,
					service.settings.date_format + " " +
					service.settings.time_format, true);
			};

			service.get_time_string = function get_time_string(moment, format)
			{
				if(!format) {
					format = service.settings.time_format;
				}
				return moment.format(format);
			};

			service.validate_date_string = function validate_date_string(
				date_string, display_errors, field, field_display_name, required)
			{
				if(Juno.Common.Util.exists(date_string))
				{
					var moment = service.get_date_moment(date_string);
					if(!moment.isValid())
					{
						display_errors.add_field_error(field, field_display_name + ' is invalid');
					}
				}
				else if(required)
				{
					display_errors.add_field_error(field, field_display_name + 'is required');
				}
			};

			service.validate_time_string = function validate_time_string(
				time_string, display_errors, field, field_display_name, required)
			{
				if (Juno.Common.Util.exists(time_string))
				{
					var moment = service.get_time_moment(time_string);
					if (!moment.isValid())
					{
						display_errors.add_field_error(field, field_display_name + 'is invalid');
					}
				}
				else if (required)
				{
					display_errors.add_field_error(field, field_display_name + 'is required');
				}
			};

			service.datetime_range_overlaps = function datetime_range_overlaps(
				first_start_moment, first_end_moment,
				second_start_moment, second_end_moment)
			{
				// the first must start before the second ends,
				// and the second must end after the first starts
				return (first_end_moment === null ||
					second_start_moment === null ||
					second_start_moment.isBefore(first_end_moment)) &&
					(second_end_moment === null ||
						first_start_moment === null ||
						second_end_moment.isAfter(first_start_moment));
			};

			service.sort_by_property_fn = function sort_by_property_fn(sort_col, sort_dir)
			{
				if (['ASC', 'DESC'].indexOf(sort_dir) < 0)
				{
					sort_dir = 'ASC';
				}

				return function (a, b)
				{
					if (a[sort_col] > b[sort_col])
					{
						return (sort_dir == 'ASC') ? 1 : -1;
					}
					else if (a[sort_col] < b[sort_col])
					{
						return (sort_dir == 'ASC') ? -1 : 1;
					}
					return 0;
				};
			};

			service.create_availability_events = function create_availability_events(
					event_array, availability, availability_types, start, end)
			{
				var event_start = service.get_datetime_moment(availability.start_time);
				var event_end = service.get_datetime_moment(availability.end_time);

				// use fullCalendar moment to ensure stripTime is available on object (used by calendar)
				event_start = $.fullCalendar.moment(event_start);
				event_end = $.fullCalendar.moment(event_end);

				var availability_type = availability_types[
						availability.availability_type_uuid];

				if(service.exists(availability_type) &&
						service.datetime_range_overlaps(
								event_start, event_end, start, end))
				{
					service.create_background_events(event_array, event_start, event_end,
							availability_type, availability.schedule_uuid);
				}
			};

			service.create_relation_events = function create_relation_events(
					event_array, schedule_templates, availability_types, relation, start, end)
			{
				console.log(relation);
				console.log(schedule_templates);
				var template = schedule_templates[relation.schedule_template_uuid];
				console.log(template);

				var relation_start = service.exists(relation.start_date) ?
						moment(relation.start_date) : null;
				var relation_end = service.exists(relation.end_date) ?
						moment(relation.end_date) : null;
				console.log(relation_start);

				// go through each day that is visible on the calendar
				// and if the schedule template relation is on the day,
				// create the template events for that day

				var processing_date = moment.utc().year(start.year()).month(
						start.month()).date(start.date()).hour(
						0).minute(0).second(0).milliseconds(0);

				while(processing_date.unix() <= end.unix())
				{
					if((relation_start === null ||
							relation_start.unix() <= processing_date.unix()) &&
							(relation_end === null ||
									relation_end.unix() >= processing_date.unix()))
					{
						service.create_template_events(
								event_array, availability_types, template, relation, processing_date);
					}

					processing_date.add(1, 'd');
				}
			};


			// ===============================================================================================
			// Private Methods
			// ===============================================================================================

			service.create_background_events = function create_background_events(
					event_array, event_start, event_end, availability_type, schedule_uuid)
			{
				// get the available time ranges given the existing events and
				// the desired time range
				var available_time_ranges = service.get_available_time_ranges(
						event_array, event_start, event_end);

				for(var k = 0; k < available_time_ranges.length; k++)
				{
					// create an event for all the available time gaps
					event_array.push(
							{
								start: available_time_ranges[k].start,
								end: available_time_ranges[k].end,
								color: availability_type.color,
								rendering: 'background',
								resourceId: schedule_uuid,
								availability_type: availability_type
							});
				}
			};

			service.get_available_time_ranges = function get_available_time_ranges(
					events, start_time, end_time)
			{
				var overlapping_events = service.get_overlapping_events(
						events, start_time, end_time);

				// if there are no overlapping events, then the whole range is available
				if(overlapping_events.length === 0)
				{
					return [ { start: start_time, end: end_time } ];
				}

				// otherwise there are overlapping events: go through and find the gaps
				var available_time_ranges = [];

				var available_time_range = {};
				for(var i = 0; i < overlapping_events.length; i++)
				{
					var overlapping = overlapping_events[i];

					// if the overlap starts in the middle of the event,
					// the last available time slot ends there
					if(!service.exists(available_time_range.end) &&
							overlapping.start.isAfter(start_time))
					{
						available_time_range.end = overlapping.start;

						// if ended without a start, the start was the start_time
						if(!service.exists(available_time_range.start))
						{
							available_time_range.start = start_time;
						}
					}

					// if a valid available time range has been found,
					// add it and reset the working hash
					if(service.exists(available_time_range.start) &&
							service.exists(available_time_range.end) &&
							available_time_range.start.isBefore(available_time_range.end))
					{
						available_time_ranges.push(angular.copy(available_time_range));
						available_time_range = {};
					}

					// if the overlap starts in the middle of the event,
					// the last available time slot ends there
					if(!service.exists(available_time_range.start) &&
							overlapping.end.isBefore(end_time))
					{
						available_time_range.start = overlapping.end;
					}
				}

				// started an availability slot at the end of the range, end it and add
				if(!service.exists(available_time_range.end) &&
						service.exists(available_time_range.start))
				{
					available_time_range.end = end_time;
					available_time_ranges.push(angular.copy(available_time_range));
				}

				return available_time_ranges;
			};

			service.get_overlapping_events = function get_overlapping_events(
					events, start_time, end_time)
			{
				var overlaps = [];
				for(var i = 0; i < events.length; i++)
				{
					var bg_event = events[i];
					if(service.datetime_range_overlaps(
							start_time, end_time, bg_event.start, bg_event.end))
					{
						overlaps.push(bg_event);
					}
				}

				overlaps.sort(service.sort_by_property_fn('start'));

				return overlaps;
			};

			service.create_template_events = function create_template_events(
					event_array, availability_types, template, relation, moment_date)
			{
				var day = moment_date.format('dddd').toLowerCase();

				// find the time periods defined for the template on the
				// given day of the week
				var process_time_periods = [];
				for(var i = 0; i < template.schedule_template_days.length; i++)
				{
					var template_day = template.schedule_template_days[i];
					if(day == template_day.day_of_the_week)
					{
						process_time_periods =
								template_day.schedule_template_time_periods;
						break;
					}
				}

				// for each time period, calculate the start and end times
				// and create the events for non-overlapping time ranges
				for(var j = 0; j < process_time_periods.length; j++)
				{
					var time_period = process_time_periods[j];

					var event_start = moment_date.clone().hour(
							time_period.start_time.split(':')[0]).minute(
							time_period.start_time.split(':')[1]);

					var event_end = moment_date.clone().hour(
							time_period.end_time.split(':')[0]).minute(
							time_period.end_time.split(':')[1]);

					if(event_end.hour() === 0 && event_end.minute() === 0)
					{
						event_end.add(1, 'day');
					}

					var availability_type = availability_types[
							time_period.availability_type_uuid];

					if(service.exists(availability_type))
					{
						service.create_background_events(
								event_array, event_start, event_end,
								availability_type, relation.schedule_uuid);
					}
				}
			};

			return service;
		}
	]
);
