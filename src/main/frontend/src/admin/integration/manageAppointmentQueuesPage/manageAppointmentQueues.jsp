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
<div class="manage-appointment-queues-page-admin">
	<div class="col-sm-12 queues-table-panel">
		<panel component-style="$ctrl.componentStyle">
			<panel-header>
				<div class="row queues-header juno-text">
					<div class="col-md-10">
						<h6 class="d-inline-block">Appointment Queues</h6>
					</div>
					<div class="col-md-2 pull-right">
						<juno-button ng-click="$ctrl.addQueue()"
						             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
						             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
						             component-style="$ctrl.componentStyle">
							Add New Queue
						</juno-button>
					</div>
				</div>
			</panel-header>
			<panel-body>
				<table ng-table="$ctrl.tableParams" class="table table-striped table-bordered">
					<tbody>
					<tr ng-repeat="queue in $ctrl.queueList | orderBy:$ctrl.sortMode">
						<td data-title="'Queue Name'" sortable="'name'">
							{{queue.queueName}}
						</td>
						<td data-title="'Queue size limit'">
							{{queue.queueLimit}}
						</td>
						<td data-title="'Actions'" class="actions-column">
							<div class="action-button-wrapper">
								<juno-button
										title="Edit Queue"
										component-style="$ctrl.componentStyle"
										button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
										button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.COLORED"
										ng-click="$ctrl.editQueue(queue)">
									<i class="icon icon-write"></i>Edit
								</juno-button>
								<juno-button
										title="Delete Queue"
										button-color="$ctrl.JUNO_BUTTON_COLOR.DANGER"
										button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
										component-style="$ctrl.componentStyle"
										ng-click="$ctrl.deleteQueue(queue)">
									<i class="icon icon-delete"></i>Delete
								</juno-button>
							</div>
						</td>
					</tr>
					</tbody>
				</table>
			</panel-body>
		</panel>
	</div>

	<div class="col-sm-12 on-demand-settings-panel" ng-if="$ctrl.onDemandBookingEnabled">
		<panel component-style="$ctrl.componentStyle">
			<panel-header>
				<div class="row queues-header juno-text">
					<div class="col-md-12">
						<h6 class="d-inline-block">On Demand Booking Settings</h6>
					</div>
				</div>
			</panel-header>
			<panel-body>
				<div class="on-demand-booking-wrapper">
					<div class="queue-selection">
						<juno-select
								label="Assigned On Demand Booking Queue"
								label-position="$ctrl.LABEL_POSITION.TOP"
								ng-model="$ctrl.onDemandAssignedQueue"
								options="$ctrl.onDemandQueueSelectOptions"
								component-style="$ctrl.componentStyle"
						>
						</juno-select>
					</div>
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
						<div class="time-selection-row" ng-repeat="day in $ctrl.onDemandQueueHours">
							<div class="col-time-selection-enabled">
								<juno-check-box ng-model="day.enabled"
								                component-style="$ctrl.componentStyle">
								</juno-check-box>
							</div>
							<div class="col-time-selection-label">
								<label>{{day.dayOfWeek}}</label>
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
				<div class="on-demand-booking-controls">
					<div class="button-wrapper">
						<juno-button ng-click="$ctrl.saveOnDemandBookingSettings()"
						             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
						             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
						             component-style="$ctrl.componentStyle">
							Save
						</juno-button>
					</div>
				</div>
			</panel-body>
		</panel>
	</div>
</div>
