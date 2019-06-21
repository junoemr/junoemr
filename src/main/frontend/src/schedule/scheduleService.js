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
import {AppointmentApi} from '../../generated/api/AppointmentApi';
import {ScheduleApi} from '../../generated/api/ScheduleApi';

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

			appointmentApi: new AppointmentApi($http, $httpParamSerializer,
			'../ws/rs'),
			scheduleApi: new ScheduleApi($http, $httpParamSerializer,
			'../ws/rs'),

			eventStatuses: {},
			rotateStatuses: [],

			getStatusByCode: function getStatusByCode(code)
			{
				return service.eventStatuses[code];
			},
			getNextRotateStatus: function(currentStatus)
			{
				var nextStatusCode = currentStatus;

				//TODO a better way to get these
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
		};

		return service;
	}
]);
