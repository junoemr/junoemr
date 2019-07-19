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
		<div class="flex-column flex-grow schedule-page-header">
			<div class="schedule-options flex-row flex-grow align-items-center">
				<h5 ng-show="!isInitialized()">Loading...</h5>

				<div ng-show="isInitialized()" class="form-inline flex-grow">
					<div class="pull-left">
						<div class="form-group cal-step-button-group">
							<div class="form-group">
								<button class="btn btn-icon"
								        title="Previous Day"
								        ng-click="stepBack()">
									<span class="icon icon-left-white"></span>
								</button>
							</div>
							<div class="form-group">
								<button class="btn btn-icon"
								        title="Next Day"
								        ng-click="stepForward()">
									<span class="icon icon-right-white"></span>
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

						<%--<div class="form-group divider-vertical"></div>--%>
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
							        ng-options="option as option.label for option in getScheduleOptions()">
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
						<%--<div class="form-group divider-vertical"></div>--%>
					</div>

					<div class="form-group pull-right">
						<div class="form-group"  ng-show="isResourceView()">
							<div class="btn-group" role="group">
								<button type="button"
								        class="btn btn-sm"
								        ng-class=" { 'btn-addon': isScheduleView(),
								                     'btn-primary': !isScheduleView() }"
								        ng-click="scheduleController.changeScheduleView(scheduleController.scheduleViewEnum.all)">
									All
								</button>
								<button type="button"
								        class="btn btn-sm"
								        ng-class=" { 'btn-addon': !isScheduleView(),
								                     'btn-primary': isScheduleView() }"
								        ng-click="scheduleController.changeScheduleView(scheduleController.scheduleViewEnum.schedule)">
									Schedule
								</button>
							</div>
						</div>
						<div class="form-group">
							<div class="btn-group" role="group" ng-show="isAgendaView()">
								<button type="button"
								        class="btn btn-sm"
								        ng-class=" { 'btn-addon': getCalendarViewName() !== scheduleController.calendarViewEnum.agendaDay,
								                     'btn-primary': getCalendarViewName() === scheduleController.calendarViewEnum.agendaDay }"
								        viewName="agendaDay"
								        ng-click="changeCalendarView(scheduleController.calendarViewEnum.agendaDay)">
									Day
								</button>
								<button type="button"
								        class="btn btn-sm"
								        ng-class=" { 'btn-addon': getCalendarViewName() !== scheduleController.calendarViewEnum.agendaWeek,
								                     'btn-primary': getCalendarViewName() === scheduleController.calendarViewEnum.agendaWeek }"
								        viewName="agendaWeek"
								        ng-click="changeCalendarView(scheduleController.calendarViewEnum.agendaWeek)">
									Week
								</button>
								<button type="button"
								        class="btn btn-sm"
								        ng-class=" { 'btn-addon': getCalendarViewName() !== scheduleController.calendarViewEnum.agendaMonth,
								                     'btn-primary': getCalendarViewName() === scheduleController.calendarViewEnum.agendaMonth }"
								        viewName="month"
								        ng-click="changeCalendarView(scheduleController.calendarViewEnum.agendaMonth)">
									Month
								</button>
							</div>
						</div>
						<div class="form-group">
							<button class="btn btn-icon"
							        title="Refresh Appointments"
							        ng-click="refetchEvents()">
								<span class="icon icon-refresh-white"></span>
							</button>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="schedule-page-content"
		     ng-class="{ 'no-scroll' : showNoResources}"
		>
			<%-- ng-hide on the calendar causes event render issues,
				 so zero state is shown by rendering content on top of an empty calendar and removing scroll bars --%>
			<div class="zero-state-message" ng-show="showNoResources && isInitialized() && !calendarLoading">
				<h1>No Providers Scheduled</h1>
			</div>
			<div class="loading-screen flex-row flex-grow justify-content-center"
			     ng-show="calendarLoading || !isInitialized()"
			>
				<div class="flex-column justify-content-center vertical-align">
					<h1>Loading...</h1>
					<div class="loading-dot-container">
						<div class="dot-pulse"></div>
					</div>
				</div>
			</div>
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