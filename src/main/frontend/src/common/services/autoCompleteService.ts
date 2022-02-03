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
import Demographic from "../../lib/demographic/model/Demographic";

angular.module("Common.Services").service("autoCompleteService", [
	"$q",
	"demographicService",
	function (
		$q,
		demographicService
	)
	{
		const service = this;

		service.init_autocomplete_values = function init_autocomplete_values(patient, autocomplete_values)
		{
			var deferred = $q.defer();

			demographicService.getDemographic(patient.patient).then(function(result: Demographic){
				deferred.resolve({
					data:{
						patient:{
							data: service.formatDemographic(result)
						}
					}
				});

			});

			return deferred.promise;
		};

		service.formatDemographic = function formatDemographic(result: Demographic)
		{
			return {
				uuid: result.id,
				full_name: result.displayName,
				birth_date: Juno.Common.Util.formatMomentDate(result.dateOfBirth),
				health_number: result.healthNumber,
				phone_number_primary: result.primaryPhone,
			};
		};
	}
]);
