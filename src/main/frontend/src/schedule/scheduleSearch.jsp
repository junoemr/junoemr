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
<juno-modal id="schedule-search-modal"
            show-loading="$ctrl.isWorking()"
>
	<modal-title>
		<i class="icon icon-modal-header icon-calendar-search"></i>
		<div class="align-baseline">
			<h3>Search For Next Available Appointment</h3>
		</div>
	</modal-title>
	<modal-ctl-buttons>
		<button type="button"
		        class="btn btn-icon"
		        aria-label="Close"
		        ng-click="$ctrl.cancel()"
		        title="Cancel">
			<i class="icon icon-modal-ctl icon-close"></i>
		</button>
	</modal-ctl-buttons>
	<modal-body>
		<div class="flex-row pane-container">
			<div class="pane search-pane">
				<h4 class="pane-header">Search Criteria</h4>
				<ca-field-select
						ca-template="label"
						ca-name="search-provider"
						ca-title="Provider"
						ca-model="$ctrl.search.provider"
						ca-options="$ctrl.providerList"
						ca-empty-option="true"
						ca-text-placeholder="Any"
				>
				</ca-field-select>
				<ca-field-select
						ca-template="label"
						ca-name="search-code"
						ca-title="Appointment Type"
						ca-model="$ctrl.search.appointmentCode"
						ca-options="$ctrl.appointmentCodeList"
						ca-empty-option="true"
						ca-text-placeholder="Any"
				>
				</ca-field-select>
				<div class="divider"></div>
				<ca-field-select
						ca-template="label"
						ca-name="search-day"
						ca-title="Day of the Week"
						ca-model="$ctrl.search.dayOfWeek"
						ca-options="$ctrl.dayOfWeekList"
						ca-empty-option="false"
				>
				</ca-field-select>

				<div class="form-group">
					<label class="control-label">Time of Day</label>
					<div class="row vertical-align">
						<ca-field-select
								ca-template="bare"
								ca-name="search-start-time"
								ca-input-size="col-md-5"
								ca-model="$ctrl.search.startTime"
								ca-options="$ctrl.timeList"
								ca-empty-option="false"
						>
						</ca-field-select>
						<span class="col-md-2">to</span>
						<ca-field-select
								ca-template="bare"
								ca-name="search-end-time"
								ca-input-size="col-md-5"
								ca-model="$ctrl.search.endTime"
								ca-options="$ctrl.timeList"
								ca-empty-option="false"
						>
						</ca-field-select>
					</div>
				</div>
			</div>
			<div class="pane result-pane flex-column flex-grow">
				<h4 class="pane-header">Search Results</h4>
				<div ng-if="( !$ctrl.isWorking() && $ctrl.resultList.length > 0)">
					<table ng-table="resultTable">
						<tr ng-repeat="result in $ctrl.resultList" ng-click="$ctrl.addAppointment(result)">
							<td data-title="'Date'">
								{{ result.scheduleSlot.appointmentDateTime | date : $ctrl.formattedDate}}
							</td>
							<td data-title="'Time'">
								{{ result.scheduleSlot.appointmentDateTime | date : $ctrl.formattedTime}}
							</td>
							<td data-title="'Provider'">
								{{ result.provider.name}}
							</td>
							<td class="appt-code" header-class="'appt-code'"
							    style="background-color: {{result.scheduleSlot.junoColor}}"
							    ng-attr-title="{{result.scheduleSlot.description}}"
							>
								{{ result.scheduleSlot.code}}
							</td>
						</tr>
					</table>
				</div>
				<div class="flex-grow"
				     ng-if="(!$ctrl.isWorking() && $ctrl.resultList.length <= 0 && !$ctrl.clean)">
					<juno-zero-state-display
							message="No Available Schedule Times"
					>
						<content>
							<i class="zero-state-image image-zero-state-calendar"></i>
						</content>
					</juno-zero-state-display>
				</div>
			</div>
		</div>
	</modal-body>
	<modal-footer>
		<button
				type="button"
				class="btn btn-default"
				ng-click="$ctrl.reset()"
				ng-disabled="$ctrl.isWorking()">Reset
		</button>
		<button
				type="button"
				class="btn btn-primary"
				ng-click="$ctrl.searchSchedules()"
				ng-disabled="$ctrl.isWorking()">Search
		</button>
	</modal-footer>
</juno-modal>