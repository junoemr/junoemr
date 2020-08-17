<%--
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
--%>
<div class="availability-settings">
	<panel component-style="$ctrl.componentStyle">
		<panel-header>
			<div class="queues-header juno-text">
				<h6 class="d-inline-block">Availability Settings</h6>
				<juno-check-box label="Enabled"
				                ng-model="$ctrl.settingsModel.enabled"
				                component-style="$ctrl.componentStyle">
				</juno-check-box>
			</div>
		</panel-header>
		<panel-body>
			<div class="availability-wrapper" ng-if="$ctrl.settingsModel.enabled">
				<div class="time-selection">
					<div class="time-selection-row">
						<div class="col-time-selection-enabled">
							Active
						</div>
						<div class="col-time-selection-label">
							Day
						</div>
						<div class="col-time-selection-start">
							Start Time
						</div>
						<div class="col-time-selection-end">
							End Time
						</div>
					</div>
					<div class="time-selection-row" ng-repeat="day in $ctrl.settingsModel.bookingHours">
						<div class="col-time-selection-enabled">
							<juno-check-box ng-model="day.enabled"
							                component-style="$ctrl.componentStyle">
							</juno-check-box>
						</div>
						<div class="col-time-selection-label">
							<label>{{$ctrl.getDayLabel(day.weekdayNumber)}}</label>
						</div>
						<div class="col-time-selection-start">
							<juno-time-select ng-model="day.startTime"
							                  label="From"
							                  disabled="!day.enabled"
							                  component-style="$ctrl.componentStyle"
							>
							</juno-time-select>
						</div>
						<div class="col-time-selection-end">
							<juno-time-select ng-model="day.endTime"
							                  label="To"
							                  disabled="!day.enabled"
							                  component-style="$ctrl.componentStyle"
							>
							</juno-time-select>
						</div>
					</div>
				</div>
			</div>
		</panel-body>
	</panel>
</div>