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

					<div class="pull-left form-inline"
						 ng-show="showScheduleSelect()">

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
								ng-click="refetch_events()">
							<i class="fa fa-refresh"></i>
						</button>
					</div>

<%--					<div class="pull-left form-inline"
						 ng-show="!show_schedule_select()">

						<button type="button"
								class="btn btn-primary"
								ng-click="select_resources()">
							Select Schedules
						</button>

						<button type="button"
								class="btn btn-icon-bare"
								title="Refresh Schedules"
								ng-click="refetch_events()">
							<i class="fa fa-refresh"></i>
						</button>

					</div>--%>

					<div class="pull-right form-inline">

						<a href="#/schedule/daysheet"
						   class="btn btn-success">
							Daysheet
						</a>

						<button type="button"
								class="btn btn-black"
								ng-click="show_legend()">
							Legend
						</button>

						<div class="form-group"
							 ng-show="show_time_intervals()">
							<label for="interval-select">Time Interval:</label>
							<select id="interval-select"
									class="form-control"
									ng-change="on_time_interval_changed()"
									ng-model="selectedTimeInterval"
									ng-options="option for option in get_time_interval_options()">
							</select>
					</div>


						<div class="form-group">
							<div class="btn-group" role="group">
								<button type="button"
										class="btn"
										ng-class=" { 'btn-addon': view_name() != 'agendaDay', 'btn-primary': view_name() == 'agendaDay' } "
										viewName="agendaDay"
										ng-click="change_view('agendaDay')">
									Day
								</button>
								<button type="button"
										class="btn"
										ng-class=" { 'btn-addon': view_name() != 'agendaWeek', 'btn-primary': view_name() == 'agendaWeek' } "
										viewName="agendaWeek"
										ng-click="change_view('agendaWeek')">
									Week
								</button>
								<button type="button"
										class="btn"
										ng-class=" { 'btn-addon': view_name() != 'month', 'btn-primary': view_name() == 'month' } "
										viewName="month"
										ng-click="change_view('month')">
									Month
								</button>
								<button type="button"
										class="btn"
										ng-class=" { 'btn-addon': view_name() != 'resourceDay', 'btn-primary': view_name() == 'resourceDay' } "
										viewName="resourceDay"
										ng-click="change_view('resourceDay')">
									Group
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