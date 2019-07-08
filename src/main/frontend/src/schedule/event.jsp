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
<div class="schedule-modal">
	<div class="modal-header">
		<div class="modal-title">
			<span class="icon icon-modal-add-appt"></span>
			<div class="align-baseline">
				<h3 ng-hide="editMode">Add {{label}}</h3>
				<h3 ng-show="editMode">Modify {{label}}</h3>
				<h4>with {{eventController.providerModel.displayName}}</h4>
			</div>
		</div>
		<div class="modal-ctl-buttons">
			<div class="modal-patient-links"
			     ng-show="isPatientSelected()">
				<button type="button" class="btn btn-xs btn-default"
				        ng-click="eventController.openEncounterPage()">
					<span class="">E</span>
				</button>
				<button type="button" class="btn btn-xs btn-default"
				        ng-disabled="!eventController.hasAppointmentId()"
				        ng-click="eventController.openBillingPage()">
					<span class="">B</span>
				</button>
				<button type="button" class="btn btn-xs btn-default"
				        ng-click="eventController.openMasterRecord()">
					<span class="">M</span>
				</button>
				<button type="button" class="btn btn-xs btn-default"
				        ng-click="eventController.openRxWindow()">
					<span class="">Rx</span>
				</button>
			</div>
			<%--<button type="button" class="close" aria-label="Maximaze">--%>
				<%--<a class="icon icon-modal-ctl icon-modal-max"></a>--%>
			<%--</button>--%>
			<%--<button type="button" class="close" aria-label="Minimize">--%>
				<%--<a class="icon icon-modal-ctl icon-modal-min"></a>--%>
			<%--</button>--%>
			<button type="button" class="close" aria-label="Close" ng-click="eventController.cancel()">
				<a class="icon icon-modal-ctl icon-modal-close"></a>
			</button>
		</div>
	</div>

	<div class="modal-body">
		<div class="tabs-heading">
			<ul class="nav nav-tabs round-top">
				<li class="active">
					<a class="round-top-left" data-toggle="tab" ng-click="eventController.changeTab(eventController.tabEnum.appointment);">
						Appointment</a>
				</li>
				<!-- tab temporarily removed -->
				<%--<li>--%>
					<%--<a data-toggle="tab" ng-click="eventController.changeTab(eventController.tabEnum.reoccurring);">--%>
						<%--Reoccurring</a>--%>
				<%--</li>--%>
				<li>
					<a data-toggle="tab" ng-click="eventController.changeTab(eventController.tabEnum.history);">
						History
					</a>
				</li>
			</ul>
		</div>
		<form class="tabs-body"
		      ng-submit="save()" ng-init="eventController.init()">
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
			     ng-show="eventController.isTabActive(eventController.tabEnum.appointment)
						|| eventController.isTabActive(eventController.tabEnum.reoccurring)">

				<div ng-show="!isInitialized() || isWorking()" ng-include="'src/common/spinner.jsp'"></div>
				<div ng-show="isInitialized() && !isWorking()" class="row">
					<div class="tab-bar-inputs form-horizontal">
						<div class="col-sm-5">
						</div>
						<div class="col-sm-7">
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
					<div class="col-md-12 form-horizontal">
						<div class="row">
							<div class="col-md-6">
								<!-- patient search -->
								<div class="form-group" title="Patient"
								     ng-hide="eventData.doNotBook">
									<label for="input-patient" class="col-md-2">
										Patient
									</label>
									<juno-patient-search-typeahead
											id="input-patient"
											class="col-md-10"
											juno-model="patientTypeahead"
									>
									</juno-patient-search-typeahead>
								</div>
								<div class="form-group" title="Patient"
								     ng-show="eventData.doNotBook">
									<label for="input-patient-dnb" class="col-md-2">
										Patient
									</label>
									<div class="col-md-10">
										<input type="text"
										       id="input-patient-dnb"
										       ng-readonly="true"
										       class="form-control"
										       value="Do Not Book"
										/>
									</div>
								</div>
							</div>
							<div class="col-md-6">
								<!-- patient type/critical -->
								<div class="form-group">
									<label class="col-md-2">Type</label>
									<div class="col-md-10">
										<div class="row">
											<div class="col-md-8">
												<ca-field-select
														ca-name="type"
														ca-template="label"
														ca-no-label="true"
														ca-input-size="col-md-12"
														ca-model="eventData.type"
														ca-options="eventController.appointmentTypeList"
														ca-empty-option="true"
												>
												</ca-field-select>
											</div>
											<div class="col-md-4">
												<ca-field-boolean
														ca-name="check-critical"
														ca-title="Critical"
														ca-label-size="col-md-5"
														ca-input-size="col-md-7"
														ca-model="eventData.critical"
														ca-template="juno"
												>
												</ca-field-boolean>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="row">
							<div class="col-md-6 info-frame-container"
							     ng-show="eventController.isTabActive(eventController.tabEnum.appointment)">
								<!-- patient details display -->
								<div class="form-group label-top"
								     ng-show="isPatientSelected()">
									<label class="col-md-2">Demographic</label>
									<div class="col-md-10">
										<div class="demographic-info-frame">
											<div class="form-row">
											<div class="col-sm-8">
												<!-- patient dob -->
												<div class="form-group">
													<label class="control-label col-sm-2">
														Born:
													</label>
													<div class="col-sm-10">
														<span class="form-control-static input-sm">
															{{ eventController.demographicModel.displayData.birthDate }}
														</span>
													</div>
												</div>
											</div>
											<div class="col-sm-4">
												<!-- gender -->
												<div class="form-group">
													<label class="control-label col-sm-2">
														Sex:
													</label>
													<div class="col-sm-8">
														<span class="form-control-static input-sm">
															{{ eventController.demographicModel.data.sex }}
														</span>
													</div>
												</div>
											</div>
										</div>
											<div class="form-row">
												<div class="col-sm-12">
													<!-- patient hin -->
													<div class="form-group">
														<label class="control-label col-sm-1">
															HIN#:
														</label>
														<div class="col-sm-11">
															<span class="form-control-static">
																<span class="patient-health-number" ng-if="eventController.demographicModel.data.hin">
																	{{eventController.demographicModel.data.hin}}
																	{{eventController.demographicModel.data.ver}}
																</span>
																<%--<button type="button"--%>
																        <%--aria-label="Check Eligibility"--%>
																        <%--title="{{eventController.demographicModel.eligibilityText}}"--%>
																        <%--class="btn"--%>
																        <%--ng-class="{--%>
																						<%--'btn-addon': (eventController.demographicModel.checkingEligibility || eventController.demographicModel.eligibility == null) && !eventController.demographicModel.pollingEligibility,--%>
																						<%--'btn-warning': eventController.demographicModel.pollingEligibility,--%>
																						<%--'btn-success': eventController.demographicModel.eligibility == 'eligible',--%>
																						<%--'btn-danger': eventController.demographicModel.eligibility == 'ineligible' }"--%>
																        <%--ng-click="eventController.demographicModel.getEligibility(true, true)">--%>
																	<%--<i class="fa fa-user" aria-hidden="true"></i>--%>
																<%--</button>--%>
															</span>
														</div>
													</div>
												</div>
											</div>
											<div class="form-row">
												<div class="col-sm-12">
													<!-- patient address -->
													<div class="form-group">
														<label class="control-label col-sm-1">
															Address:
														</label>
														<div class="col-sm-11">
															<span class="form-control-static">{{eventController.demographicModel.displayData.addressLine}}</span>
														</div>
													</div>
												</div>
											</div>
											<div class="form-row">
												<div class="col-sm-12">
													<!-- patient phone -->
													<div class="form-group">
														<label class="control-label col-sm-1">
															Phone:
														</label>
														<div class="col-sm-11">
															<span class="form-control-static">{{eventController.demographicModel.data.phone}}</span>
														</div>
													</div>
												</div>
											</div>
											<div class="form-row">
												<div class="col-sm-12">
													<!-- patient email -->
													<div class="form-group">
														<label class="control-label col-sm-1">
															Email:
														</label>
														<div class="col-sm-11">
															<span class="form-control-static">{{eventController.demographicModel.data.email}}</span>
														</div>
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
							<!-- repeat booking units/period -->
							<div class="col-md-6"
							     ng-show="eventController.isTabActive(eventController.tabEnum.reoccurring)">
								<div class="form-group">
									<label class="col-md-2">Repeat</label>
									<div class="col-md-10">
										<div class="row">
											<div class="col-md-4">
												<ca-field-text
														ca-name="repeat-units"
														ca-title="Repeat"
														ca-no-label="true"
														ca-input-size="col-md-12"
														ca-model="eventController.repeatBookingData.units"
														ca-error="{{displayMessages.field_errors()['repeatUnits']}}"
														ca-rows="1"
												>
												</ca-field-text>
											</div>
											<div class="col-md-6">
												<ca-field-select
														ca-name="repeat-period"
														ca-title="period"
														ca-template="label"
														ca-no-label="true"
														ca-input-size="col-md-12"
														ca-model="eventController.repeatBookingData.period"
														ca-options="eventController.repeatBooking.periodOptions"
												>
												</ca-field-select>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class="col-md-6">
								<!-- date and time info/options -->
								<div class="form-group label-top">
									<label class="col-md-2">Session Date</label>
									<div class="col-md-10">
										<div class="row">
											<div class="col-md-12">
												<ca-field-date
														ca-input-size="col-md-12"
														ca-no-label="true"
														ca-date-picker-id="select-date"
														ca-name="startDate"
														ca-model="eventData.startDate"
														ca-error="{{displayMessages.field_errors()['startDate']}}"
														ca-orientation="auto"
												></ca-field-date>
											</div>
										</div>
										<div class="row">
											<div class="col-md-6">
												<ca-field-text
														ca-label-size="col-md-4"
														ca-input-size="col-md-8"
														ca-title="Duration"
														ca-name="duration"
														ca-model="eventData.duration"
														ca-error="{{displayMessages.field_errors()['duration']}}"
														ca-rows="1">
												</ca-field-text>
											</div>

											<div class="col-md-6">
												<ca-field-time
														ca-label-size="col-md-4"
														ca-input-size="col-md-8"
														ca-title="Time"
														ca-name="startTime"
														ca-model="eventData.startTime"
														ca-error="{{displayMessages.field_errors()['startTime']}}"
														ca-template="no_button"
														ca-disable-widget="true"
														ca-minute-step="parentScope.timeIntervalMinutes()">
												</ca-field-time>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="row"
						     ng-show="eventController.isTabActive(eventController.tabEnum.appointment)">
							<!-- show patient alerts -->
							<div class="col-md-6">
								<div class="form-group">
									<label class="col-md-2">
									</label>
									<div class="col-md-10">
										<span class="form-control-static alert-message">{{eventController.demographicModel.data.alert}}</span>
									</div>
								</div>
							</div>
							<!-- show additional alerts -->
							<div class="col-md-6">
							</div>
						</div>
						<div class="row"
						     ng-show="eventController.isTabActive(eventController.tabEnum.appointment)">
							<div class="col-md-6">
								<!-- location/site -->
								<ca-field-select
										ca-hide="!eventController.sitesEnabled"
										ca-name="site"
										ca-title="Site"
										ca-template="label"
										ca-label-size="col-md-2"
										ca-input-size="col-md-10"
										ca-model="eventData.site"
										ca-error="{{displayMessages.field_errors()['site']}}"
										ca-options="eventController.siteOptions"
								>
								</ca-field-select>
							</div>
							<div class="col-md-6">
								<!-- reason type -->
								<ca-field-select
										ca-template="label"
										ca-label-size="col-md-2"
										ca-input-size="col-md-10"
										ca-name="reason-code"
										ca-title="Reason Type"
										ca-model="eventData.reasonCode"
										ca-options="eventController.reasonCodeList"
										ca-empty-option="false"
								>
								</ca-field-select>
							</div>
						</div>
						<div class="row"
						     ng-show="eventController.isTabActive(eventController.tabEnum.appointment)">
							<div class="col-md-6">
								<!-- notes selection-->
								<ca-field-text
										ca-name="notes"
										ca-title="Notes"
										ca-label-size="col-md-2"
										ca-input-size="col-md-10"
										ca-model="eventData.notes"
										ca-rows="1"
								>
								</ca-field-text>
							</div>
							<div class="col-md-6">
								<!-- reason -->
								<ca-field-text
										ca-label-size="col-md-2"
										ca-input-size="col-md-10"
										ca-title="Reason"
										ca-name="event_reason"
										ca-model="eventData.reason"
										ca-rows="1">
								</ca-field-text>
							</div>
						</div>

						<!-- repeat booking units/period -->
						<div class="row"
						     ng-show="eventController.isTabActive(eventController.tabEnum.reoccurring)">
							<div class="col-md-6">
								<div class="form-group">
									<label class="col-md-2">Ends</label>
									<div class="col-md-10">
										<ca-field-date
												ca-input-size="col-md-12"
												ca-no-label="true"
												ca-date-picker-id="repeat-select-end-date"
												ca-name="endDate"
												ca-model="eventController.repeatBookingData.endDate"
												ca-error="{{displayMessages.field_errors()['repeatEndDate']}}"
												ca-orientation="auto"
										></ca-field-date>
									</div>
								</div>
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
								<span ng-if="$last">Provider {{record.creator}} created appointment on {{record.formattedCreateDate}} at {{record.formattedCreateTime}}</span>
								<span ng-if="!$last">Provider {{record.lastUpdateUser}} updated appointment on {{record.formattedUpdateDate}} at {{record.formattedUpdateTime}}</span>
							</div>
						</span>
					</div>
				</div>
			</div>
		</form>
	</div>

	<div class="modal-footer">
		<div class="bottom-buttons">
			<div class="pull-left">
				<button
						type="button"
						class="btn btn-danger"
						ng-show="eventController.hasAppointmentId()"
						ng-click="eventController.del()"
						ng-disabled="isWorking() || eventController.isDoubleBookPrevented">Delete
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
						uib-tooltip="{{keyBinding.getTooltip(keyBindSettings, 'ctrl+enter')}}"
						ng-click="eventController.saveAndPrint()"
						ng-disabled="isWorking() || eventController.isDoubleBookPrevented">Print
				</button>

				<button
						type="submit"
						class="btn btn-primary"
						tooltip-placement="top"
						tooltip-append-to-body="true"
						uib-tooltip="{{keyBinding.getTooltip(keyBindSettings, 'ctrl+enter')}}"
						ng-show="!editMode"
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
						uib-tooltip="{{keyBinding.getTooltip(keyBindSettings, 'ctrl+enter')}}"
						ng-show="editMode"
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
		</div>
	</div>
	<div id="start-time-auto-wrapper"></div>
	<div id="end-time-auto-wrapper"></div>
</div>
