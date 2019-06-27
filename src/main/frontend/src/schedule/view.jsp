<%--

	Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
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

	This software was written for
	CloudPractice Inc.
	Victoria, British Columbia
	Canada

--%>
<div id="center-panel" class="schedule-center-panel">

	<div ng-show="!isSchedulingEnabled()"
		 class="alert alert-danger">
		Scheduling addon is not enabled.
	</div>

	<div ng-show="isSchedulingEnabled()">

		<div class="alert alert-info"
		     ng-show="isInitialized() && !hasSchedules()">
			Please set up a schedule at <a href="#/schedule/admin/schedule">Schedule Admin</a>
		</div>
		<div ng-show="hasSchedules()">
			<div class="flex-column flex-grow schedule-page-header">
				<div ng-show="!isInitialized()">
					<p>Loading...</p>
				</div>
				<div ng-show="isInitialized()"
				     class="schedule-options flex-row flex-grow align-items-center">
					<div class="form-inline flex-grow">
						<div class="pull-left">
							<div class="form-group cal-step-button-group">
								<div class="form-group">
									<button class="btn btn-icon"
									        ng-click="stepBack()">
										<span class="icon icon-left"></span>
									</button>
								</div>
								<div class="form-group">
									<button class="btn btn-icon"
									        ng-click="stepForward()">
										<span class="icon icon-right"></span>
									</button>
								</div>
							</div>

							<div class="form-group">
								<ca-field-date
										ca-template="bare"
										ca-date-picker-id="schedule-view-select-date"
										ca-name="Date"
										ca-model="datepickerSelectedDate"
										ca-orientation="auto"
								></ca-field-date>
							</div>

							<div class="form-group divider-vertical"></div>
							<ca-field-select
									ca-hide="!hasSites()"
									ca-name="site"
									ca-title="Site"
									ca-template="label"
									ca-no-label="true"
									ca-model="selectedSiteName"
									ca-options="getSiteOptions()"
							>
							</ca-field-select>

							<div class="form-group">
								<select id="schedule-select"
								        class="form-control"
								        ng-change="onScheduleChanged()"
								        ng-model="selectedSchedule"
								        ng-options="option as option.name for option in getScheduleOptions()">
								</select>
							</div>
							<div class="form-group"
							     ng-show="showTimeIntervals()">
								<ca-field-select
										ca-name="interval-select"
										ca-no-label="true"
										ca-template="label"
										ca-model="selectedTimeInterval"
										ca-options="getTimeIntervalOptions()"
								>
								</ca-field-select>
							</div>
							<div class="form-group divider-vertical"></div>
						</div>

						<div class="form-group pull-right">
							<div class="form-group">
								<div class="btn-group" role="group" ng-show="isAgendaView()">
									<button type="button"
									        class="btn"
									        ng-class=" { 'btn-addon': viewName() != 'agendaDay', 'btn-primary': viewName() == 'agendaDay' } "
									        viewName="agendaDay"
									        ng-click="changeView('agendaDay')">
										Day
									</button>
									<button type="button"
									        class="btn"
									        ng-class=" { 'btn-addon': viewName() != 'agendaWeek', 'btn-primary': viewName() == 'agendaWeek' } "
									        viewName="agendaWeek"
									        ng-click="changeView('agendaWeek')">
										Week
									</button>
									<button type="button"
									        class="btn"
									        ng-class=" { 'btn-addon': viewName() != 'month', 'btn-primary': viewName() == 'month' } "
									        viewName="month"
									        ng-click="changeView('month')">
										Month
									</button>
								</div>
							</div>
							<div class="form-group">
								<button class="btn btn-icon"
								        ng-click="refetchEvents()">
									<span class="icon icon-refresh"></span>
								</button>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="schedule-page-content">
				<div
						id="ca-calendar"
						class="calendar"
						ui-calendar="uiConfigApplied.calendar"
						calendar="cpCalendar"
						ng-model="eventSources"
						ng-enabled="initialized"
				></div>
			</div>
		</div>
	</div>
</div>