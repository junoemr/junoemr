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
	<juno-security-check show-placeholder="true" permissions="scheduleController.SecurityPermissions.AppointmentRead">
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
									<i class="icon icon-arrow-left"></i>
								</button>
							</div>
							<div class="form-group">
								<button class="btn btn-icon"
								        title="Next Day"
								        ng-click="stepForward()">
									<i class="icon icon-arrow-right"></i>
								</button>
							</div>
						</div>

						<div class="form-group">
							<ca-field-date
									ca-template="bare"
									ca-date-picker-id="schedule-view-select-date"
									ca-name="Date"
									ca-model="scheduleController.datepickerSelectedDate"
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
								ca-model="scheduleController.selectedSiteName"
								ca-options="getSiteOptions()"
						>
						</ca-field-select>

						<div class="form-group">
							<select id="schedule-select"
							        class="form-control"
							        ng-change="onScheduleChanged()"
							        ng-model="scheduleController.selectedSchedule"
							        ng-options="option as option.label for option in getScheduleOptions()">
							</select>
						</div>
						<div class="form-group"
						     ng-show="showTimeIntervals()">
							<ca-field-select
									ca-name="interval-select"
									ca-no-label="true"
									ca-template="label"
									ca-model="scheduleController.selectedTimeInterval"
									ca-options="getTimeIntervalOptions()"
							>
							</ca-field-select>
						</div>
						<%--<div class="form-group divider-vertical"></div>--%>
					</div>

					<div class="form-group pull-right">
						<div class="form-group">
							<div class="btn-group" role="group" ng-show="isAgendaView()">
								<button type="button"
								        class="btn btn-sm"
								        ng-class=" { 'btn-addon': !isAgendaDayView(),
								                     'btn-primary': isAgendaDayView() }"
								        viewName="agendaDay"
								        ng-click="changeCalendarView(scheduleController.calendarViewEnum.agendaDay)">
									Day
								</button>
								<button type="button"
								        class="btn btn-sm"
								        ng-class=" { 'btn-addon': !isAgendaWeekView(),
								                     'btn-primary': isAgendaWeekView() }"
								        viewName="agendaWeek"
								        ng-click="changeCalendarView(scheduleController.calendarViewEnum.agendaWeek)">
									Week
								</button>
								<button type="button"
								        class="btn btn-sm"
								        ng-class=" { 'btn-addon': !isAgendaMonthView(),
								                     'btn-primary': isAgendaMonthView() }"
								        viewName="month"
								        ng-click="changeCalendarView(scheduleController.calendarViewEnum.agendaMonth)">
									Month
								</button>
							</div>
						</div>
						<div class="form-group">
							<div class="btn-group" role="group">
								<button type="button"
								        class="btn btn-sm"
								        title="Do not hide unscheduled days and providers"
								        ng-class=" { 'btn-addon': isScheduleView(),
								                     'btn-primary': !isScheduleView() }"
								        ng-click="scheduleController.changeScheduleView(scheduleController.scheduleViewEnum.all)">
									All
								</button>
								<button type="button"
								        class="btn btn-sm"
								        title="Hide unscheduled days and providers"
								        ng-class=" { 'btn-addon': !isScheduleView(),
								                     'btn-primary': isScheduleView() }"
								        ng-click="scheduleController.changeScheduleView(scheduleController.scheduleViewEnum.schedule)">
									Scheduled
								</button>
							</div>
						</div>
						<div class="form-group">
							<button class="btn btn-icon"
							        title="Refresh Appointments"
							        ng-click="refetchEvents()">
								<i class="icon icon-refresh"></i>
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
			<div class="zero-state-message" ng-show="showNoResources && isInitialized()">
				<h1 ng-show="isResourceView() && (selectedSiteName == null || (selectedSiteName != null && isScheduleView()))">
					No Providers Scheduled
				</h1>
				<h1 ng-show="isAgendaView() && (selectedSiteName == null || (selectedSiteName != null && isScheduleView()))">
					Provider Not Scheduled
				</h1>
				<h1 ng-show="isResourceView() && selectedSiteName != null && !isScheduleView()">
					No Providers Assigned to Site
				</h1>
				<h1 ng-show="isAgendaView() && selectedSiteName != null && !isScheduleView()">
					Provider Not Assigned to Site
				</h1>
			</div>
			<div class="loading-screen flex-row flex-grow justify-content-center"
			     ng-show="customLoading || !isInitialized()"
			>
				<div class="flex-column justify-content-center vertical-align">
					<juno-loading-indicator
						message="Loading"
						indicator-type="dot-pulse">
					</juno-loading-indicator>
				</div>
			</div>
			<div class="loading-screen-small flex-row flex-grow justify-content-center"
			     ng-show="calendarLoading && !customLoading"
			>
				<div class="flex-column justify-content-center vertical-align">
					<juno-loading-indicator
						indicator-type="dot-pulse">
					</juno-loading-indicator>
				</div>
			</div>

			<div class="info-message-container">
				<ca-info-messages
						ca-errors-object="displayMessages"
						ca-prepend-name-to-field-errors="false">
				</ca-info-messages>
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
	</juno-security-check>
</div>
