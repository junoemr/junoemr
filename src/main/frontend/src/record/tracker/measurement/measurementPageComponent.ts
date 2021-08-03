/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */

import MeasurementModel from "../../../lib/measurement/model/measurementModel";

angular.module('Record.Tracker.Measurement').component('measurementPage',
	{
		templateUrl: 'src/record/tracker/measurement/measurementPage.jsp',
		bindings: {
			componentStyle: "<?",
		},
		controller: [
			'$stateParams',
			'measurementApiService',
			function (
				$stateParams,
				measurementApiService,
			)
			{
				const ctrl = this;
				ctrl.measurements = [];
				ctrl.measurementGroups = {}; //can't use map, not supported by ng-repeat

				ctrl.$onInit = async (): Promise<void> =>
				{
					ctrl.demographicNo = $stateParams.demographicNo;

					ctrl.measurements = await measurementApiService.getDemographicMeasurements(ctrl.demographicNo);
					ctrl.measurements.forEach((measurement: MeasurementModel) =>
					{
						const key = measurement.typeCode;
						if(ctrl.measurementGroups[key])
						{
							const list = ctrl.measurementGroups[key];
							list.push(measurement);
							list.sort((itemA: MeasurementModel, itemB: MeasurementModel) =>
							{
								// newest items at beginning of the list
								return itemB.observationDateTime.diff(itemA.observationDateTime);
							});
						}
						else
						{
							const list = [];
							list.push(measurement);
							ctrl.measurementGroups[key] = list;
						}
					});
				}
			}]
	});