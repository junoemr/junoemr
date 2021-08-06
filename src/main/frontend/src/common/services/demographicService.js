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
angular.module("Common.Services").service("demographicService", [
    '$q',
    'junoHttp',
    function($q, junoHttp)
    {
        var service = {};

        service.apiPath = '../ws/rs/demographic';

        service.getDemographic = function getDemographic(demographicNo)
        {
            var deferred = $q.defer();

	        junoHttp.get(service.apiPath + '/' + encodeURIComponent(demographicNo),
                Juno.Common.ServiceHelper.configHeaders()).then(
                function success(results)
                {
                    if(results.data.effDate) results.data.effDate = moment(results.data.effDate).toDate();
	                if(results.data.hcRenewDate)results.data.hcRenewDate = moment(results.data.hcRenewDate).toDate();
	                if(results.data.endDate)results.data.endDate = moment(results.data.endDate).toDate();
	                if(results.data.patientStatusDate)results.data.patientStatusDate = moment(results.data.patientStatusDate).toDate();
	                if(results.data.onWaitingListSinceDate)results.data.onWaitingListSinceDate = moment(results.data.onWaitingListSinceDate).toDate();
	                if(results.data.scrPaperChartArchivedDate)results.data.scrPaperChartArchivedDate = moment(results.data.scrPaperChartArchivedDate).toDate();
	                if(results.data.dateJoined)results.data.dateJoined = moment(results.data.dateJoined).toDate();
	                if(results.data.rosterTerminationDate)results.data.rosterTerminationDate = moment(results.data.rosterTerminationDate).toDate();
	                if(results.data.rosterDate)results.data.rosterDate = moment(results.data.rosterDate).toDate();
                    deferred.resolve(results.data);
                },
                function error(errors)
                {
                    console.log("demographicServices::getDemographic error", errors);
                    deferred.reject("An error occurred while fetching demographic");
                });

            return deferred.promise;
        };

        service.saveDemographic = function saveDemographic(demographic)
        {
            var deferred = $q.defer();

	        junoHttp.post(service.apiPath, demographic).then(
                function success(results)
                {
                    deferred.resolve(results.data);
                },
                function error(errors)
                {
                    console.log("demographicServices::saveDemographic error", errors);
                    deferred.reject(errors.data.error.message);
                });

            return deferred.promise;
        };

        service.updateDemographic = function updateDemographic(demographic)
        {
            var deferred = $q.defer();

	        junoHttp.put(service.apiPath, demographic).then(
                function success(results)
                {
                    deferred.resolve(results.data);
                },
                function error(errors)
                {
                    console.log("demographicServices::updateDemographic error", errors);
                    deferred.reject("An error occurred while updating demographic");
                });

            return deferred.promise;
        };

        return service;
    }
]);