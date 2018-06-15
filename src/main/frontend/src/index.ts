
import {ScheduleApi} from "../generated/JunoInternalApi/ScheduleApi";
import {CalendarApiAdapter} from './schedule/calendarApiAdapter';

angular.module('Schedule').service(
	'Schedule.CalendarApiAdapter', CalendarApiAdapter);
