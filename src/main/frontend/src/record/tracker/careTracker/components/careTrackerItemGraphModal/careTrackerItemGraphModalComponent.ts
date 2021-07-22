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

				ctrl.labels = ["January", "February", "March", "April", "May", "June", "July"];
				ctrl.series = ['Series A', 'Series B'];
				ctrl.data = [
					[65, 59, 80, 81, 56, 55, 40],
					[28, 48, 40, 19, 86, 27, 90]
				];
				ctrl.onClick = function (points, evt) {
					console.log(points, evt);
				};
				ctrl.datasetOverride = [{ yAxisID: 'y-axis-1' }, { yAxisID: 'y-axis-2' }];
				ctrl.options = {
					scales: {
						yAxes: [
							{
								id: 'y-axis-1',
								type: 'linear',
								display: true,
								position: 'left'
							},
							{
								id: 'y-axis-2',
								type: 'linear',
								display: true,
								position: 'right'
							}
						]
					}
				};
			}]
	});