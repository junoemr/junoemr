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
<juno-modal id="schedule-modal">
	<modal-title>
		<i class="icon icon-modal-header icon-calendar-add"></i>
		<div class="align-baseline">
			<h3 ng-hide="eventController.editMode">Add Appointment</h3>
			<h3 ng-show="eventController.editMode">Modify Appointment</h3>
			<h4>with {{eventController.providerModel.displayName}}</h4>
		</div>
	</modal-title>
	<modal-ctl-buttons>
		<button type="button" class="btn btn-icon" aria-label="Close"
		        ng-click="eventController.cancel()"
		        title="Cancel">
			<i class="icon icon-modal-ctl icon-close"></i>
		</button>
	</modal-ctl-buttons>

	<modal-body>
		<div class="tabs-heading">
			<ul class="nav nav-tabs round-top">
				<li class="active">
					<a class="round-top-left" data-toggle="tab" ng-click="eventController.changeTab(eventController.tabEnum.appointment);">
						Appointment</a>
				</li>
				<li>
					<a data-toggle="tab" ng-click="eventController.changeTab(eventController.tabEnum.history);">
						History
					</a>
				</li>
			</ul>
		</div>
		<form class="tabs-body"
		      ng-submit="save()">
			<div class="alert-container">
				<div class="row">
					<ca-info-messages
							ca-errors-object="displayMessages"
							ca-field-value-map="fieldValueMapping"
							ca-prepend-name-to-field-errors="false">
					</ca-info-messages>
				</div>
			</div>

			<div id="tabAppointmentEdit" class="tab-pane"
			     ng-show="eventController.isTabActive(eventController.tabEnum.appointment)">

				<div ng-show="!isInitialized() || isWorking()"
				     ng-include="'src/common/spinner.jsp'">
				</div>
				<div ng-show="isInitialized() && !isWorking()">
					<div class="tab-bar-inputs form-row">
						<div class="col-sm-8 pull-right">
							<juno-appointment-status-select
									ca-name="event-appt-status"
									ca-no-label="true"
									ca-input-size="col-md-12"
									ca-model="eventController.selectedEventStatus"
									ca-options="eventController.eventStatuses"
									ca-change="eventController.onStatusChange()"
							>
							</juno-appointment-status-select>
						</div>
					</div>
					<div>
						<div class="form-row">
							<!-- patient search -->
							<div class="form-group col-md-6" title="Patient"
							     ng-hide="eventData.doNotBook">
								<div class="row">
									<div ng-class="{'col-md-8': eventController.showPatientChartLinks(),
									                'col-md-12': !eventController.showPatientChartLinks() }">
										<label class="control-label"
										       for="input-patient">
											Patient
										</label>
										<juno-patient-search-typeahead
												id="input-patient"
												juno-icon-right="true"
												juno-model="eventController.patientTypeahead"
												juno-add-button-title="New Demographic"
												juno-on-add-fn="newDemographic"
										>
										</juno-patient-search-typeahead>
									</div>
									<div class="col-md-4 lower-content padding-left-0"
									     ng-if="eventController.showPatientChartLinks()">
										<div class="btn-group modal-patient-links pull-right">
											<button type="button" class="btn btn-default"
											        ng-click="eventController.openEncounterPage()">
												<span>E</span>
											</button>
											<button type="button" class="btn btn-default"
											        ng-click="eventController.openBillingPage()">
												<span>B</span>
											</button>
											<button type="button" class="btn btn-default"
											        ng-click="eventController.openMasterRecord()">
												<span>M</span>
											</button>
											<button type="button" class="btn btn-default"
											        ng-click="eventController.openRxWindow()">
												<span>Rx</span>
											</button>
										</div>
									</div>
								</div>
							</div>
							<div class="form-group col-md-6" title="Patient"
							     ng-show="eventData.doNotBook">
								<label class="control-label"
								       for="input-patient-dnb">
									Patient
								</label>
								<div>
									<input type="text"
									       id="input-patient-dnb"
									       ng-readonly="true"
									       class="form-control"
									       value="Do Not Book"
									/>
								</div>
							</div>
							<!-- patient type/critical -->
							<div class="col-md-6">
								<div class="row">
									<div class="col-md-8">
										<ca-field-select
												ca-name="type"
												ca-title="Appointment Type"
												ca-template="label"
												ca-model="eventData.type"
												ca-options="eventController.appointmentTypeList"
												ca-empty-option="true"
										>
										</ca-field-select>
									</div>
									<div class="col-md-4 lower-content">
										<ca-field-boolean
												ca-form-group-class="vertical-align"
												ca-name="check-critical"
												ca-title="Critical"
												ca-label-size="col-md-6"
												ca-input-size="col-md-6"
												ca-model="eventData.critical"
												ca-template="juno"
										>
										</ca-field-boolean>
									</div>
								</div>
							</div>
						</div>
						<div class="form-row">
							<div class="form-group col-md-6 info-frame-container">
								<div ng-show="isPatientSelected()">
									<label class="control-label">Demographic</label>
									<demographic-card
											demographic-model="eventController.demographicModel.data">
									</demographic-card>
								</div>
							</div>
							<div class="col-md-6 lower-content">
								<div class="row">
									<div class="col-md-5">
										<ca-field-date
												ca-title="Date"
												ca-date-picker-id="select-date"
												ca-name="startDate"
												ca-model="eventData.startDate"
												ca-error="{{displayMessages.field_errors()['startDate']}}"
												ca-orientation="auto"
										></ca-field-date>
									</div>
									<div class="col-md-4 padding-left-0">
										<ca-field-time
												ca-title="Time"
												ca-name="startTime"
												ca-model="eventData.startTime"
												ca-error="{{displayMessages.field_errors()['startTime']}}"
												ca-template="no_button"
												ca-disable-widget="true"
												ca-minute-step="parentScope.timeIntervalMinutes()">
										</ca-field-time>
									</div>
									<div class="col-md-3 padding-left-0">
										<ca-field-text
												ca-title="Duration"
												ca-name="duration"
												ca-model="eventData.duration"
												ca-error="{{displayMessages.field_errors()['duration']}}"
												ca-rows="1">
										</ca-field-text>
									</div>
								</div>
								<div class="row">
									<div class="col-md-6">
										<!-- reason type -->
										<ca-field-select
												ca-template="label"
												ca-name="reason-code"
												ca-title="Reason Type"
												ca-model="eventData.reasonCode"
												ca-options="eventController.reasonCodeList"
												ca-empty-option="false"
										>
										</ca-field-select>
									</div>
									<div class="col-md-6 padding-left-0">
										<!-- location/site -->
										<ca-field-select
												ca-hide="!eventController.sitesEnabled"
												ca-name="site"
												ca-title="Site"
												ca-template="label"
												ca-model="eventData.site"
												ca-error="{{displayMessages.field_errors()['site']}}"
												ca-options="eventController.siteOptions"
										>
										</ca-field-select>
									</div>
								</div>
							</div>
						</div>
						<div class="form-row">
							<!-- show patient alerts -->
							<div class="col-md-12">
								<span class="form-control-static alert-message">{{eventController.demographicModel.data.alert}}</span>
							</div>
						</div>
						<div class="form-row">
							<div class="col-md-6">
								<!-- notes selection-->
								<ca-field-text
										ca-name="notes"
										ca-title="Notes"
										ca-model="eventData.notes"
										ca-rows="1"
								>
								</ca-field-text>
							</div>
							<div class="col-md-6">
								<!-- reason -->
								<ca-field-text
										ca-title="Reason"
										ca-name="event_reason"
										ca-model="eventData.reason"
										ca-rows="1">
								</ca-field-text>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div id="tabAppointmentHistory" class="tab-pane"
			     ng-show="eventController.isTabActive(eventController.tabEnum.history)">

				<h5 class="edit-history-header">Edit History</h5>

				<div class="content-display flex-grow overflow-scroll">
					<div class="list-group">
						<span ng-repeat="record in eventController.eventHistory"
						   class="list-group-item">

							<div>
								<span ng-if="$first">{{record.updateUserDisplayName}} created appointment on {{record.formattedCreateDate}} at {{record.formattedCreateTime}}</span>
								<span ng-if="!$first">{{record.updateUserDisplayName}} updated appointment on {{record.formattedUpdateDate}} at {{record.formattedUpdateTime}}</span>
							</div>
						</span>
					</div>
				</div>
			</div>
		</form>
	</modal-body>

	<modal-footer>
		<div class="pull-left">
			<button
					type="button"
					class="btn btn-danger"
					ng-show="eventController.hasAppointmentId()"
					juno-confirm-click="eventController.del()"
					juno-confirm-message="Are you sure you want to delete this appointment?"
					ng-disabled="isWorking()">Delete
			</button>
		</div>
		<div class="pull-right">
			<button
					type="button"
					class="btn btn-default"
					ng-click="eventController.saveDoNotBook()"
					ng-disabled="isWorking() || eventController.isDoubleBookPrevented">Do Not Book
			</button>

			<button
					type="button"
					class="btn btn-default"
					ng-click="eventController.saveAndReceipt()"
					ng-disabled="isWorking() || eventController.isDoubleBookPrevented">Receipt
			</button>

			<button
					type="button"
					class="btn btn-default"
					tooltip-placement="top"
					tooltip-append-to-body="true"
					uib-tooltip="{{eventController.keyBinding.getTooltip(keyBindSettings, 'ctrl+enter')}}"
					ng-click="eventController.saveAndPrint()"
					ng-disabled="isWorking() || eventController.isDoubleBookPrevented">Print
			</button>

			<button
					type="submit"
					class="btn btn-primary"
					tooltip-placement="top"
					tooltip-append-to-body="true"
					uib-tooltip="{{eventController.keyBinding.getTooltip(keyBindSettings, 'ctrl+enter')}}"
					ng-show="!eventController.editMode"
					ng-click="eventController.save()"
					ng-class="{
						'double-book': (eventController.isDoubleBook && !eventController.isDoubleBookPrevented),
					    'double-book-prevented':eventController.isDoubleBookPrevented}"
					ng-disabled="isWorking() || eventController.isDoubleBookPrevented">Create
			</button>

			<button
					type="submit"
					class="btn btn-primary"
					tooltip-placement="top"
					tooltip-append-to-body="true"
					uib-tooltip="{{eventController.keyBinding.getTooltip(keyBindSettings, 'ctrl+enter')}}"
					ng-show="eventController.editMode"
					ng-click="eventController.save()"
					ng-class="{
						'double-book': (eventController.isDoubleBook && !eventController.isDoubleBookPrevented),
					    'double-book-prevented':eventController.isDoubleBookPrevented}"
					ng-disabled="isWorking() || eventController.isDoubleBookPrevented">Modify
			</button>

			<%--<button--%>
					<%--type="button"--%>
					<%--class="btn btn-primary"--%>
					<%--tooltip-placement="top"--%>
					<%--tooltip-append-to-body="true"--%>
					<%--uib-tooltip="{{keyBinding.getTooltip(keyBindSettings, 'ctrl+shift+enter')}}"--%>
					<%--ng-click="eventController.saveAndBill()"--%>
					<%--ng-show="numInvoices == 0"--%>
					<%--ng-disabled="isWorking()">Modify &amp; Bill--%>
			<%--</button>--%>

		</div>
	</modal-footer>
	<div id="start-time-auto-wrapper"></div>
	<div id="end-time-auto-wrapper"></div>
</juno-modal>
