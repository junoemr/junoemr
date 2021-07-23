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

import CareTrackerItemDataModel from "../../../../../lib/careTracker/model/CareTrackerItemDataModel";
import {Moment} from "moment";

angular.module('Record.Tracker.CareTracker').component('careTrackerItemGraphModal',
	{
		templateUrl: 'src/record/tracker/careTracker/components/careTrackerItemGraphModal/careTrackerItemGraphModal.jsp',
		bindings: {
			modalInstance: "<",
			resolve: "<",
		},
		controller: [
			function()
			{
				const ctrl = this;
				ctrl.isLoading = true;

				ctrl.labels = [];
				ctrl.series = ["Recorded Value"];
				ctrl.data = [];
				ctrl.options = [];
				ctrl.colours = [
					"#455899",
				];

				ctrl.$onInit = (): void =>
				{
					ctrl.model = angular.copy(ctrl.resolve.model); // use a copy so we don't modify the real model
					ctrl.model.sortDataByObservationDate(true);
					ctrl.labels = ctrl.formatLabels(ctrl.model.data);
					ctrl.data = [ctrl.formatData(ctrl.model.data)];
					ctrl.options = ctrl.formatOptions(ctrl.model.data);
					ctrl.isLoading = false;
				}

				ctrl.formatLabels = (dataPoints: CareTrackerItemDataModel[]): object[] =>
				{
					return dataPoints.map((data: CareTrackerItemDataModel) =>
					{
						return data.observationDateTime.toDate();
					});
				}

				ctrl.formatData = (dataPoints: CareTrackerItemDataModel[]): number[] =>
				{
					return dataPoints.map((data: CareTrackerItemDataModel) => Number(data.value));
				}

				ctrl.formatOptions = (dataPoints: CareTrackerItemDataModel[]): object =>
				{
					const axisDaysThreshold = 30; // after this, only months are displayed on x-axis
					const axisMonthsThreshold = 365 * 2; // after this, only years are displayed on x-axis

					const firstDate: Moment = dataPoints[0].observationDateTime;
					const lastDate: Moment = dataPoints[dataPoints.length - 1].observationDateTime;
					const timeScaleDays: number = Math.abs(lastDate.diff(firstDate, 'days'));
					const unit = (timeScaleDays > axisDaysThreshold) ? ((timeScaleDays > axisMonthsThreshold) ? "year" : "month") : "day";

					return ctrl.options = {
						scales: {
							yAxes: [
								{
									id: 'y-axis-1',
									type: 'linear',
									display: true,
									position: 'left',
									// title option doesn't appear to work
									title: {
										display: true,
										text: "Value",
									},
								},
							],
							xAxes: [
								{
									id: 'x-axis-1',
									type: 'time',
									distribution: 'linear',
									display: true,
									// title option doesn't appear to work
									title: {
										display: true,
										text: "Date & Time",
									},
									time: {
										tooltipFormat: Juno.Common.Util.DisplaySettings.calendarDateFormat,
										unit: unit,
									},
								}
							],
						}
					};
				}
			}]
	});