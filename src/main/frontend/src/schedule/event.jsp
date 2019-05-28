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
			<button type="button" class="close" aria-label="Maximaze">
				<a class="icon icon-modal-ctl icon-modal-max"></a>
			</button>
			<button type="button" class="close" aria-label="Minimize">
				<a class="icon icon-modal-ctl icon-modal-min"></a>
			</button>
			<button type="button" class="close" aria-label="Close" ng-click="cancel()">
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
				<li>
					<a data-toggle="tab" ng-click="eventController.changeTab(eventController.tabEnum.reoccurring);">
						Reoccurring</a>
				</li>
				<li>
					<a data-toggle="tab" ng-click="eventController.changeTab(eventController.tabEnum.history);">
						History
					</a>
				</li>
			</ul>
		</div>
		<div class="tabs-body">
			<div id="tabAppointmentEdit" class="tab-pane"
			     ng-show="eventController.activeTab == eventController.tabEnum.appointment">

				<form ng-submit="save()" ng-init="eventController.init()">
					<div ng-show="!isInitialized() || isWorking()" ng-include="'src/common/spinner.jsp'"></div>
					<div ng-show="isInitialized() && !isWorking()" class="row">
						<div class="tab-bar-inputs form-horizontal">
							<div class="col-sm-5">
							</div>
							<div class="col-sm-7">
								<ca-field-select
										ca-name="appt-status"
										ca-template="appt_status"
										ca-no-label="true"
										ca-input-size="col-md-12"
										ca-model="eventController.selectedEventStatus"
										ca-options="eventController.eventStatuses"
								>
								</ca-field-select>
							</div>
						</div>
						<div class="alert-container">
							<div class="row">
								<ca-info-messages
										ca-errors-object="displayMessages"
										ca-field-value-map="fieldValueMapping"
										ca-prepend-name-to-field-errors="false">
								</ca-info-messages>
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
									<ca-field-select
											ca-name="type"
											ca-title="Type"
											ca-template="label"
											ca-label-size="col-md-2"
											ca-input-size="col-md-10"
											ca-model="eventData.type"
											ca-options="eventController.appointmentTypeList"
											ca-empty-option="true"
									>
									</ca-field-select>
								</div>
							</div>
							<div class="row">
								<div class="col-md-6">
									<!-- patient details display -->
									<div class="form-group label-top info-frame-container"
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
																{{ demographicModel.data.birthDate }}
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
																{{ demographicModel.data.sex }}
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
																	<span class="patient-health-number" ng-if="demographicModel.data.healthNumber">
																		{{demographicModel.data.healthNumber}}
																		{{demographicModel.data.ontarioVersionCode}}
																	</span>
																	<%--<button type="button"--%>
																	        <%--aria-label="Check Eligibility"--%>
																	        <%--title="{{demographicModel.eligibilityText}}"--%>
																	        <%--class="btn"--%>
																	        <%--ng-class="{--%>
																							<%--'btn-addon': (demographicModel.checkingEligibility || demographicModel.eligibility == null) && !demographicModel.pollingEligibility,--%>
																							<%--'btn-warning': demographicModel.pollingEligibility,--%>
																							<%--'btn-success': demographicModel.eligibility == 'eligible',--%>
																							<%--'btn-danger': demographicModel.eligibility == 'ineligible' }"--%>
																	        <%--ng-click="demographicModel.getEligibility(true, true)">--%>
																		<%--<i class="fa fa-user" aria-hidden="true"></i>--%>
																	<%--</button>--%>
																</span>
																<%--<p ng-if="!demographicModel.data.healthNumber"--%>
																   <%--class="form-control-static"></p>--%>
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
																<span class="form-control-static">{{demographicModel.data.addressLine}}</span>
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
																<span class="form-control-static">{{demographicModel.data.phoneNumberPrimary}}</span>
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
																<span class="form-control-static">{{demographicModel.data.email}}</span>
															</div>
														</div>
													</div>
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
												<div class="col-md-6">
													<ca-field-date
															ca-label-size="col-md-2"
															ca-input-size="col-md-10"
															ca-template="bare"
															ca-date-picker-id="select-date"
															ca-name="startDate"
															ca-model="eventData.startDate"
															ca-orientation="auto"
													></ca-field-date>
												</div>
												<div class="col-md-6">
													<ca-field-text
															ca-label-size="col-md-4"
															ca-input-size="col-md-8"
															ca-title="Duration"
															ca-name="duration"
															ca-model="eventData.duration"
															ca-rows="1"
															ca-change="onDurationChange()">
													</ca-field-text>
												</div>
											</div>
											<div class="row">
												<div class="col-md-6">
													<ca-field-time
															ca-label-size="col-md-4"
															ca-input-size="col-md-8"
															ca-title="Time"
															ca-name="startTime"
															ca-model="eventData.startTime"
															ca-minute-step="parentScope.timeIntervalMinutes()">
													</ca-field-time>
												</div>
												<div class="col-md-6">
													<div class="row">
														<div class="col-md-6">
															<ca-field-boolean
																	ca-name="check-am"
																	ca-title="am"
																	ca-label-size="col-md-5"
																	ca-input-size="col-md-7"
																	ca-model="eventController.amSelected"
																	ca-template="juno"
															>
															</ca-field-boolean>
														</div>
														<div class="col-md-6">
															<ca-field-boolean
																	ca-name="check-pm"
																	ca-title="pm"
																	ca-label-size="col-md-5"
																	ca-input-size="col-md-7"
																	ca-model="eventController.pmSelected"
																	ca-template="juno"
															>
															</ca-field-boolean>
														</div>
													</div>
												</div>
											</div>
											<div class="row">
												<div class="col-md-6">
													<ca-field-boolean
															ca-name="check-do-not-book"
															ca-title="Do Not Book"
															ca-label-size="col-md-8"
															ca-input-size="col-md-4"
															ca-model="eventData.doNotBook"
															ca-template="juno"
													>
													</ca-field-boolean>
												</div>
												<div class="col-md-6">
													<div class="row">
														<div class="col-md-6">
														</div>
														<div class="col-md-6">
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
											<%--<div class="row">--%>
												<%--<div class="col-md-9"></div>--%>
												<%--<div class="col-md-3">--%>
													<%--<ca-field-toggle--%>
															<%--ca-name="check-critical"--%>
															<%--ca-title="Critical"--%>
															<%--ca-label-size="col-md-4"--%>
															<%--ca-input-size="col-md-8"--%>
															<%--ca-model="eventController.critical"--%>
															<%--ca-template="juno">--%>
													<%--</ca-field-toggle>--%>
												<%--</div>--%>
											<%--</div>--%>
										</div>
									</div>
								</div>
							</div>
							<div class="row">
								<div class="col-md-6">
									<!-- location/site -->
									<ca-field-select
											ca-name="site"
											ca-title="Site"
											ca-template="label"
											ca-label-size="col-md-2"
											ca-input-size="col-md-10"
											ca-model="eventController.selectedSiteName"
											ca-options="eventController.siteOptions"
									>
									</ca-field-select>
									<%--<div class="form-group">--%>
										<%--<label for="select-site" class="flex-row-label">Site</label>--%>
										<%--<select id="select-site" class="flex-row-content"></select>--%>
									<%--</div>--%>

									<%--<!-- provider selection -->--%>
									<%--<div class="flex-row">--%>
										<%--<label for="select-provider" class="flex-row-label">Provider</label>--%>
										<%--<select id="select-provider" class="flex-row-content"--%>
										        <%--ng-model="eventController.selectedProvider">--%>
										        <%--&lt;%&ndash;ng-options="faxAccount.displayName for faxAccount in faxSendReceiveController.faxAccountList">&ndash;%&gt;--%>
										<%--</select>--%>
									<%--</div>--%>
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

									<%--<div class="form-group">--%>
										<%--<label for="select-resources" class="flex-row-label">Notes</label>--%>
										<%--<input id="select-resources" class="flex-row-content" type="text"/>--%>
									<%--</div>--%>
								</div>
							</div>
							<div class="row">
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
											<%--ca-max-characters="600"--%>
											ca-rows="1">
									</ca-field-text>
								</div>




								<%--<div class="row form-horizontal stacked-labels">--%>
									<%--<div class="col-sm-6">--%>

										<%--<div class="form-group"--%>
										     <%--ng-class="{ 'has-error': displayMessages.field_errors()['startDate'] }">--%>

											<%--<label class="control-label col-sm-12">--%>
												<%--Start Time:--%>
											<%--</label>--%>

											<%--<div class="col-sm-6">--%>
												<%--<ca-field-date--%>
														<%--ca-template="bare"--%>
														<%--ca-date-picker-id="start-date-picker"--%>
														<%--ca-name="startDate"--%>
														<%--ca-model="eventData.startDate"--%>
														<%--ca-orientation="auto"--%>
												<%--></ca-field-date>--%>
											<%--</div>--%>

											<%--<div class="col-sm-6 time-wrapper">--%>
												<%--<ca-field-time--%>
														<%--ca-template="bare"--%>
														<%--ca-name="startTime"--%>
														<%--ca-model="eventData.startTime"--%>
														<%--ca-minute-step="parentScope.timeIntervalMinutes()">--%>
												<%--</ca-field-time>--%>
											<%--</div>--%>

										<%--</div>--%>

										<%--<div class="form-group"--%>
										     <%--ng-class="{ 'has-error': displayMessages.field_errors()['endDate'] }">--%>

											<%--<label class="control-label col-sm-12">--%>
												<%--End Time:--%>
											<%--</label>--%>

											<%--<div class="col-sm-6">--%>
												<%--<ca-field-date--%>
														<%--ca-template="bare"--%>
														<%--ca-date-picker-id="end-date-picker"--%>
														<%--ca-name="endDate"--%>
														<%--ca-model="eventData.endDate"--%>
														<%--ca-orientation="auto"--%>
												<%--></ca-field-date>--%>
											<%--</div>--%>

											<%--<div class="col-sm-6 time-wrapper">--%>
												<%--<ca-field-time--%>
														<%--ca-template="bare"--%>
														<%--ca-name="endTime"--%>
														<%--ca-model="eventData.endTime"--%>
														<%--ca-minute-step="parentScope.timeIntervalMinutes()">--%>
												<%--</ca-field-time>--%>
											<%--</div>--%>
										<%--</div>--%>

										<%--<div class="form-group">--%>
											<%--<label for="input-event-status" class="control-label col-sm-12">--%>
												<%--Status:--%>
											<%--</label>--%>
											<%--<div class="col-sm-10">--%>
												<%--<select id="input-event-status"--%>
												        <%--class="form-control"--%>
												        <%--ng-model="selectedEventStatus"--%>
												        <%--ng-options="option as option.name for option in eventStatusOptions">--%>
													<%--<!-- XXX: don't put in a blank options--%>
													<%--<option value=""></option>--%>
													<%---->--%>
												<%--</select>--%>
											<%--</div>--%>
											<%--<span class="event-color"--%>
											      <%--style="background-color: {{selectedEventStatus.color}};"></span>--%>
										<%--</div>--%>

										<%--<div--%>
												<%--class="form-group"--%>
												<%--ng-class="{'has-error': displayMessages.field_errors()['location']}"--%>
												<%--ng-show="hasSites()">--%>
											<%--<label for="input-site" class="control-label col-sm-12">--%>
												<%--Site:--%>
											<%--</label>--%>
											<%--<div class="col-sm-10">--%>
												<%--<select id="input-site"--%>
												        <%--class="form-control"--%>
												        <%--ng-model="selectedSiteName">--%>
													<%--<option--%>
															<%--ng-repeat="option in parentScope.siteOptions"--%>
															<%--value="{{option.name}}"--%>
															<%--style="background-color: {{option.color}}">--%>
														<%--{{option.display_name}}--%>
													<%--</option>--%>
												<%--</select>--%>
											<%--</div>--%>
										<%--</div>--%>

									<%--</div>--%>

									<%--<div class="col-sm-6"--%>
									     <%--ng-show="activeTemplateEvents.length > 0">--%>

										<%--<label class="control-label">Availability during appointment:</label>--%>

										<%--<div class="availability"--%>
										     <%--ng-repeat="templateEvent in activeTemplateEvents | limitTo: 4">--%>

											<%--<div class="pull-left color">--%>
												<%--<div class="event-color"--%>
												     <%--style="background-color: {{templateEvent.color}};">--%>
												<%--</div>--%>
											<%--</div>--%>
											<%--<div class="pull-left info">--%>
												<%--<div>{{ templateEvent.availabilityType.description}}</div>--%>

												<%--<div class="availability-detail">--%>
													<%--{{ templateEvent.start.format('h:mma') }} ---%>
													<%--{{ templateEvent.end.format('h:mma') }}--%>
												<%--</div>--%>
												<%--<a class="availability-detail"--%>
												   <%--ng-show="templateEvent.availabilityType.duration != null"--%>
												   <%--href=""--%>
												   <%--ng-click="setEventLength(templateEvent.availabilityType.duration)">--%>
													<%--({{ templateEvent.availabilityType.duration }} minutes)--%>
												<%--</a>--%>
											<%--</div>--%>
										<%--</div>--%>

										<%--<div class="availability" ng-show="activeTemplateEvents.length > 4">--%>
											<%--<div class="availability-detail more-availability">--%>
												<%--and {{ activeTemplateEvents.length - 4 }} more...--%>
											<%--</div>--%>
										<%--</div>--%>

									<%--</div>--%>
								<%--</div>--%>

								<%--<div class="row form-horizontal stacked-labels">--%>
									<%--<div class="col-sm-12">--%>
										<%--<ca-field-text--%>
												<%--ca-label-size="col-sm-12"--%>
												<%--ca-input-size="col-sm-12"--%>
												<%--ca-title="Reason"--%>
												<%--ca-name="event_reason"--%>
												<%--ca-model="eventData.reason"--%>
												<%--ca-max-characters="300"--%>
												<%--ca-rows="1">--%>
										<%--</ca-field-text>--%>
									<%--</div>--%>
								<%--</div>--%>

								<%--<div class="row form-horizontal stacked-labels">--%>
									<%--<div class="col-sm-12">--%>
										<%--<ca-field-text--%>
												<%--ca-label-size="col-sm-12"--%>
												<%--ca-input-size="col-sm-12"--%>
												<%--ca-title="Notes"--%>
												<%--ca-name="event_notes"--%>
												<%--ca-model="eventData.notes"--%>
												<%--ca-max-characters="600"--%>
												<%--ca-rows="2">--%>
										<%--</ca-field-text>--%>
									<%--</div>--%>
								<%--</div>--%>





								<%--<div class="pull-left">--%>
									<%--<button type="button"--%>
									        <%--class="btn btn-danger"--%>
									        <%--ng-show="editMode"--%>
									        <%--ng-click="del()"--%>
									        <%--ng-disabled="isWorking()">--%>
										<%--Delete--%>
									<%--</button>--%>
								<%--</div>--%>
							</div>
						</div>
					</div>
				</form>
			</div>
			<div id="tabReoccurring" class="tab-pane"
			     ng-show="eventController.activeTab == eventController.tabEnum.reoccurring">
				<span>TO DO</span>
			</div>
			<div id="tabHistory" class="tab-pane"
			     ng-show="eventController.activeTab == eventController.tabEnum.history">
				<span>TO DO</span>
			</div>
		</div>
	</div>

	<div class="modal-footer">
		<div class="bottom-buttons">
			<div class="pull-right">
				<button
						type="button"
						class="btn btn-default"
						ng-click="save()"
						ng-disabled="isWorking()">Receipt
				</button>

				<button
						type="button"
						class="btn btn-default"
						tooltip-placement="top"
						tooltip-append-to-body="true"
						uib-tooltip="{{keyBinding.getTooltip(keyBindSettings, 'ctrl+enter')}}"
						ng-click="saveAndPrint()"
						ng-disabled="isWorking()">Print
				</button>

				<button
						type="submit"
						class="btn btn-primary"
						tooltip-placement="top"
						tooltip-append-to-body="true"
						uib-tooltip="{{keyBinding.getTooltip(keyBindSettings, 'ctrl+enter')}}"
						ng-show="!editMode"
						ng-click="save()"
						ng-disabled="isWorking()">Create
				</button>

				<button
						type="submit"
						class="btn btn-primary"
						tooltip-placement="top"
						tooltip-append-to-body="true"
						uib-tooltip="{{keyBinding.getTooltip(keyBindSettings, 'ctrl+enter')}}"
						ng-show="editMode"
						ng-click="save()"
						ng-disabled="isWorking()">Modify
				</button>

				<button
						type="button"
						class="btn btn-primary"
						tooltip-placement="top"
						tooltip-append-to-body="true"
						uib-tooltip="{{keyBinding.getTooltip(keyBindSettings, 'ctrl+shift+enter')}}"
						ng-click="saveAndBill()"
						ng-show="numInvoices == 0"
						ng-disabled="isWorking()">Modify &amp; Bill
				</button>

			</div>
		</div>
	</div>
	<div id="start-time-auto-wrapper"></div>
	<div id="end-time-auto-wrapper"></div>
</div>
