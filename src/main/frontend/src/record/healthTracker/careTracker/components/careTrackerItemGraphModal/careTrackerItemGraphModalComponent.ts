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

import CareTrackerItemData from "../../../../../lib/careTracker/model/CareTrackerItemData";
import {Moment} from "moment";
import CareTrackerItem from "../../../../../lib/careTracker/model/CareTrackerItem";

angular.module('Record.Tracker.CareTracker').component('careTrackerItemGraphModal',
	{
		templateUrl: 'src/record/healthTracker/careTracker/components/careTrackerItemGraphModal/careTrackerItemGraphModal.jsp',
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
				ctrl.series = [];
				ctrl.data = [];
				ctrl.options = [];
				ctrl.colours = [
					"#455899",
					"#30bfbf",
				];

				ctrl.$onInit = (): void =>
				{
					ctrl.model = angular.copy(ctrl.resolve.model); // use a copy so we don't modify the real model
					ctrl.model.sortDataByObservationDate(true);
					const dataPoints = ctrl.model.data.filter((value: CareTrackerItemData) => value.observationDateTime && value.observationDateTime.isValid())

					ctrl.labels = ctrl.formatLabels(dataPoints);
					ctrl.series = ctrl.formatSeries(ctrl.model);
					ctrl.data = ctrl.formatData(ctrl.model, dataPoints);
					ctrl.options = ctrl.formatOptions(dataPoints);
					ctrl.isLoading = false;
				}

				ctrl.formatLabels = (dataPoints: CareTrackerItemData[]): object[] =>
				{
					return dataPoints.map((data: CareTrackerItemData) =>
					{
						return data.observationDateTime.toDate();
					});
				}

				ctrl.formatSeries = (model: CareTrackerItem): string[] =>
				{
					if(model.valueTypeIsBloodPressure())
					{
						return ["Systolic", "Diastolic"];
					}
					else
					{
						return ["Recorded Value"];
					}
				}

				ctrl.formatData = (model: CareTrackerItem, dataPoints: CareTrackerItemData[]): number[][] =>
				{
					if(model.valueTypeIsBloodPressure())
					{
						let systolic = dataPoints.map((data: CareTrackerItemData) => Number(data.value.split('/')[0]));
						let diastolic = dataPoints.map((data: CareTrackerItemData) => Number(data.value.split('/')[1]));

						return[systolic, diastolic];
					}
					else
					{
						return [dataPoints.map((data: CareTrackerItemData) => Number(data.value))];
					}
				}

				ctrl.formatOptions = (dataPoints: CareTrackerItemData[]): object =>
				{
					const axisDaysThreshold = 30; // after this, only months are displayed on x-axis
					const axisMonthsThreshold = 365 * 2; // after this, only years are displayed on x-axis

					const firstDate: Moment = dataPoints[0].observationDateTime;
					const lastDate: Moment = dataPoints[dataPoints.length - 1].observationDateTime;
					const timeScaleDays: number = Math.abs(lastDate.diff(firstDate, 'days'));
					const unit = (timeScaleDays > axisDaysThreshold) ? ((timeScaleDays > axisMonthsThreshold) ? "year" : "month") : "day";

					return ctrl.options = {
						elements: {
							line: {
								// makes lines straight instead of curved on a spline etc.
								tension: 0,
							},
						},
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