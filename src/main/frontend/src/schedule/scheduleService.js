'use strict';

/*

 Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
 This software is published under the GPL GNU General Public License.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

 This software was written for the
 Department of Family Medicine
 McMaster University
 Hamilton
 Ontario, Canada

 */
import {ScheduleApi} from '../../generated/api/ScheduleApi';
import {API_BASE_PATH} from "../lib/constants/ApiConstants";

angular.module("Schedule").service("scheduleService", [
	'$http',
	'$httpParamSerializer',
	'$q',

	function(
		$http,
		$httpParamSerializer,
		$q,
	)
	{
		var service = {

			scheduleApi: new ScheduleApi($http, $httpParamSerializer, API_BASE_PATH),

			eventStatuses: {},
			rotateStatuses: [],

			getStatusByCode: function getStatusByCode(code)
			{
				return service.eventStatuses[code];
			},
			getNextRotateStatus: function(currentStatus)
			{
				var nextStatusCode = currentStatus;

				//TODO-legacy a better way to get these
				for(var i=0; i< service.rotateStatuses.length; i++)
				{
					if(service.rotateStatuses[i].displayLetter === currentStatus)
					{
						var nextStatus = service.rotateStatuses[(i + 1)% service.rotateStatuses.length];
						nextStatusCode = nextStatus.displayLetter;
						break;
					}
				}
				return nextStatusCode;
			},

			// Loads the list of event statuses from the API (i.e. appointment statuses).  Sets the following:
			// $scope.event_statuses - a table to look up a status by uuid.
			// $scope.rotate_statuses - an array to describe how to cycle through statuses.
			loadEventStatuses: function loadEventStatuses()
			{
				var deferred = $q.defer();

				service.eventStatuses = {};
				service.rotateStatuses = [];

				service.scheduleApi.getCalendarAppointmentStatuses().then(
					function success(rawResults)
					{
						var results = rawResults.data.body;

						for(var i = 0; i < results.length; i++)
						{
							var result = results[i];
							service.eventStatuses[result.displayLetter] = result;
							if(result.rotates)
							{
								service.rotateStatuses.push(result);
							}
						}

						deferred.resolve(results);
					});

				return deferred.promise;
			},


			getBillingLink: function getBillingLink(params)
			{
				return "../billing.do" +
					"?billRegion=" +        encodeURIComponent(Juno.Common.Util.toTrimmedString(params.billRegion)) +
					"&billForm=" +          encodeURIComponent(Juno.Common.Util.toTrimmedString(params.billForm)) +
					"&hotclick=" +          encodeURIComponent(Juno.Common.Util.toTrimmedString(params.hotclick)) +
					"&appointment_no=" +    encodeURIComponent(Juno.Common.Util.toTrimmedString(params.appointment_no)) +
					"&demographic_name=" +  encodeURIComponent(Juno.Common.Util.toTrimmedString(params.demographic_name)) +
					"&status=" +            encodeURIComponent(Juno.Common.Util.toTrimmedString(params.status)) +
					"&demographic_no=" +    encodeURIComponent(Juno.Common.Util.toTrimmedString(params.demographic_no)) +
					"&providerview=" +      encodeURIComponent(Juno.Common.Util.toTrimmedString(params.providerview)) +
					"&user_no=" +           encodeURIComponent(Juno.Common.Util.toTrimmedString(params.user_no)) +
					"&apptProvider_no=" +   encodeURIComponent(Juno.Common.Util.toTrimmedString(params.apptProvider_no)) +
					"&appointment_date=" +  encodeURIComponent(Juno.Common.Util.toTrimmedString(params.appointmentDate)) +
					"&start_time=" +        encodeURIComponent(Juno.Common.Util.toTrimmedString(params.start_time)) +
					"&bNewForm=" +          encodeURIComponent(Juno.Common.Util.toTrimmedString(params.bNewForm)) +
					"&referral_no_1=" +     encodeURIComponent(Juno.Common.Util.toTrimmedString(params.referral_no_1))
			},

			getEncounterLink: function getEncounterLink(params)
			{
				return "../oscarEncounter/IncomingEncounter.do" +
					"?providerNo=" +        encodeURIComponent(Juno.Common.Util.toTrimmedString(params.providerNo)) +
					"&appointmentNo=" +     encodeURIComponent(Juno.Common.Util.toTrimmedString(params.appointmentNo)) +
					"&demographicNo=" +     encodeURIComponent(Juno.Common.Util.toTrimmedString(params.demographicNo)) +
					"&curProviderNo=" +     encodeURIComponent(Juno.Common.Util.toTrimmedString(params.curProviderNo)) +
					"&reason=" +            encodeURIComponent(Juno.Common.Util.toTrimmedString(params.reason)) +
					"&encType=" +           encodeURIComponent(Juno.Common.Util.toTrimmedString(params.encType)) +

					"&userName=" +          encodeURIComponent(Juno.Common.Util.toTrimmedString(params.userName)) +
					"&curDate=" +           encodeURIComponent(Juno.Common.Util.toTrimmedString(params.curDate)) +

					"&appointmentDate=" +   encodeURIComponent(Juno.Common.Util.toTrimmedString(params.appointmentDate)) +
					"&startTime=" +         encodeURIComponent(Juno.Common.Util.toTrimmedString(params.startTime)) +
					"&status=" +            encodeURIComponent(Juno.Common.Util.toTrimmedString(params.status)) +
					"&apptProvider_no=" +   encodeURIComponent(Juno.Common.Util.toTrimmedString(params.apptProvider_no)) +
					"&providerview=" +      encodeURIComponent(Juno.Common.Util.toTrimmedString(params.providerview))
			},

			getRxLink: function getRxLink(params)
			{
				if (params.demographicNo !== 0)
				{
					return "../oscarRx/choosePatient.do" +
						"?providerNo=" + encodeURIComponent(params.providerNo) +
						"&demographicNo=" + encodeURIComponent(params.demographicNo);
				}
			}
		};
		return service;
	}
]);
