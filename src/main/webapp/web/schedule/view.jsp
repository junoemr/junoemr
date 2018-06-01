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
<div id="center-panel" class="">

	<div ng-show="!isSchedulingEnabled()"
		 class="alert alert-danger">
		Scheduling addon is not enabled.
	</div>

	<div ng-show="isSchedulingEnabled()">

		<div class="schedule-options">

			<div ng-show="!isInitialized()">
				<p>Loading...</p>
			</div>

			<div ng-show="isInitialized()">

				<div class="alert alert-info"
					 ng-show="!hasSchedules()">
					Please set up a schedule at <a href="#/schedule/admin/schedule">Schedule Admin</a>
				</div>

				<div ng-show="hasSchedules()">

					<div class="pull-left form-inline">

						<div class="form-group">
							<label for="schedule-select">Schedule:</label>
							<select id="schedule-select"
									class="form-control"
									ng-change="onScheduleChanged()"
									ng-model="selectedSchedule"
									ng-options="option as option.name for option in getScheduleOptions()">
							</select>
						</div>
						<button type="button"
								class="btn btn-icon-bare"
								title="Refresh Schedule"
								ng-click="refetchEvents()">
							<i class="fa fa-refresh"></i>
						</button>
					</div>

					<div class="pull-left form-inline">
						<div ng-show="hasSites()">
							<div class="form-group">
								<label for="site-select">Site:</label>
								<select id="site-select"
										class="form-control"
										ng-change="onSiteChanged()"
										ng-model="selectedSite"
										ng-options="option as option.display_name for option in getSiteOptions()">
								</select>
							</div>
						</div>
					</div>

					<div class="pull-right form-inline">

						<a href="#/schedule/daysheet"
						   class="btn btn-success">
							Daysheet
						</a>

						<button type="button"
								class="btn btn-black"
								ng-click="showLegend()">
							Legend
						</button>

						<div class="form-group"
							 ng-show="showTimeIntervals()">
							<label for="interval-select">Time Interval:</label>
							<select id="interval-select"
									class="form-control"
									ng-change="onTimeIntervalChanged()"
									ng-model="selectedTimeInterval"
									ng-options="option for option in getTimeIntervalOptions()">
							</select>
					</div>


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

					</div>

				</div>
			</div>
		</div>



		<div
			id="ca-calendar"
			class="calendar"
			cp-calendar="uiConfig"
			cp-calendar-control="cpCalendarControl"
			cp-calendar-selected-schedule="selectedSchedule"
			cp-calendar-selected-time-interval="selectedTimeInterval"
			cp-calendar-calendar-api-adapter="Schedule.CalendarApiAdapter"
			cp-calendar-access-control = "securityService"
			cp-calendar-auto-complete="autoCompleteService"
			cp-calendar-global-state = "globalStateService"
			cp-calendar-patient-model="demographicService"
		></div>

	</div>
</div>