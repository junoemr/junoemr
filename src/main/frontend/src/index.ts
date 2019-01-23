
import {ScheduleApi} from "../generated/api/ScheduleApi";
import {CalendarApiAdapter} from './schedule/calendarApiAdapter';

angular.module('Schedule').service(
	'Schedule.CalendarApiAdapter', CalendarApiAdapter);
