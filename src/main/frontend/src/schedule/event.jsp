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
<div class="flexH modal-header">
	<div class="flexH flexGrow modal-title">
		<span class="icon icon-modal-add-appt"></span>
		<span ng-hide="editMode">Add {{label}}</span>
		<span ng-show="editMode">Modify {{label}}</span>
	</div>
	<div class="flexH modal-ctl-buttons">
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

<div class="modal-body flexV flexGrow">
	<div class="tabs-heading">
		<ul class="nav nav-tabs">
			<li class="active">
				<a data-toggle="tab" ng-click="eventController.changeTab(eventController.tabEnum.appointment);">
					Appointment</a>
			</li>
			<li>
				<a data-toggle="tab" ng-click="eventController.changeTab(eventController.tabEnum.appointmentHistory);">
					Appointment History
				</a>
			</li>
		</ul>
	</div>
	<div class="tabs-body flexV flexGrow">
		<div id="tabAppointmentEdit" class="tab-pane flexV flexGrow"
		     ng-show="eventController.activeTab == eventController.tabEnum.appointment">

			<form ng-submit="save()" class="flexV flexGrow" ng-init="init()">
				<div ng-show="!isInitialized() || isWorking()" ng-include="'src/common/spinner.jsp'"></div>
				<div ng-show="isInitialized() && !isWorking()" class="flexV flexGrow">
					<div class="flexV flexGrow">
						<ca-info-messages
								ca-errors-object="displayMessages"
								ca-field-value-map="fieldValueMapping"
								ca-prepend-name-to-field-errors="false">
						</ca-info-messages>
						<div class="flexH flexGrow">
							<!-- Left column -->
							<div class="flexV flexGrow">
								<!-- patient search -->
								<div class="flex-row" title="Patient">
									<label for="input-patient" class="flex-row-label">
										Patient
									</label>
									<juno-patient-search-typeahead
											id="input-patient"
											class="flex-row-content"
											juno-model="patientTypeahead"
									>
									</juno-patient-search-typeahead>
								</div>

								<!-- patient details display -->
								<div class="flex-row label-top">
									<label class="flex-row-label">Demographic</label>
									<div class="flexV display-frame-lg info-frame flexGrow">
										<!-- patient image -->
										<div class="col-sm-3 patient-image" ng-show="isPatientSelected()">
											<div class="file-upload-drop-box">
												<span class="upload-text" ng-if="!demographicModel.hasPhoto">Upload Photo</span>
												<img class="patient-photo" title="Click to change image"
												     ng-src="{{demographicModel.patientPhotoUrl}}"
												     align="middle"/>
											</div>
										</div>
										<!-- patient name -->
										<div class="col-sm-9 form-horizontal form-compact" ng-show="isPatientSelected()">
											<div class="form-group">
												<label class="control-label col-sm-4">
													Selected Patient:
												</label>
												<div class="col-sm-8">
													<div class="form-control-static">
														<a ng-href="#!/record/{{ demographicModel.demographicNo }}/summary" target="_blank">{{ demographicModel.fullName }}</a>
													</div>
												</div>
											</div>

											<!-- patient hin -->
											<div class="form-group">
												<label class="control-label col-sm-4">
													Health Number:
												</label>
												<div class="col-sm-8">
													<span ng-if="demographicModel.data.healthNumber">
														<span class="patient-health-number">
															{{demographicModel.data.healthNumber}}
															{{demographicModel.data.ontarioVersionCode}}
														</span>
														<button type="button"
														        aria-label="Check Eligibility"
														        title="{{demographicModel.eligibilityText}}"
														        class="btn"
														        ng-class="{
																				'btn-addon': (demographicModel.checkingEligibility || demographicModel.eligibility == null) && !demographicModel.pollingEligibility,
																				'btn-warning': demographicModel.pollingEligibility,
																				'btn-success': demographicModel.eligibility == 'eligible',
																				'btn-danger': demographicModel.eligibility == 'ineligible' }"
														        ng-click="demographicModel.getEligibility(true, true)">
															<i class="fa fa-user" aria-hidden="true"></i>
														</button>
													</span>
													<p ng-if="!demographicModel.data.healthNumber"
													   class="form-control-static"></p>
												</div>
											</div>

											<!-- patient dob -->
											<div class="form-group">
												<label class="control-label col-sm-4">
													Birth Date:
												</label>
												<div class="col-sm-8">
													<div class="form-control-static">
														{{ demographicModel.data.birthDate }}
													</div>
												</div>
											</div>

											<!-- patient phone -->
											<div class="form-group">
												<label class="control-label col-sm-4">
													Phone Number:
												</label>
												<div class="col-sm-8">
													<div class="form-control-static">
														{{ demographicModel.data.phoneNumberPrimary }}
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>

								<!-- provider selection -->
								<div class="flex-row">
									<label for="select-provider" class="flex-row-label">Provider</label>
									<select id="select-provider" class="flex-row-content"
									        ng-model="eventController.selectedProvider">
									        <%--ng-options="faxAccount.displayName for faxAccount in faxSendReceiveController.faxAccountList">--%>
									</select>
								</div>

								<!-- resources selection-->
								<div class="flex-row">
									<label for="select-resources" class="flex-row-label">Notes</label>
									<input id="select-resources" class="flex-row-content" type="text"/>
								</div>
							</div>
							<!-- Right column -->
							<div class="flexV flexGrow">
								<div class="flex-row">
									<label for="select-type" class="flex-row-label">Type</label>
									<select id="select-type" class="flex-row-content"
									<%--ng-model="eventController.selectedFaxAccount"--%>
									<%--ng-options="faxAccount.displayName for faxAccount in faxSendReceiveController.faxAccountList">--%>
									>

									</select>
								</div>
								<!-- date and time info/options -->
								<div class="flex-row label-top">
									<label for="select-date" class="flex-row-label">Session Date</label>
									<div class="flex-row-content label-center display-frame-lg">
										<div class="flexH flexGrow">
											<div class="flexV flexGrow">
												<div class="flex-row">
													<select id="select-date" class="flex-row-content"></select>
												</div>
												<div class="flex-row">
													<label for="input-time" class="flex-row-label med">Time</label>
													<input id="input-time" class="flex-row-content sml" type="text"/>
												</div>
											</div>
											<div class="flexV flexGrow">
												<div class="flex-row">
													<label for="input-duration" class="flex-row-label med">Duration</label>
													<input id="input-duration" class="flex-row-content sml" type="text"/>
												</div>
												<div class="flexH flexGrow">
													<div class="flexV flexGrow">
														<div class="flex-row">
															<label for="check-am" class="flex-row-label sml">am</label>
														<%--<ca-field-toggle id="check-am"--%>
														                 <%--form-group-class="flex-row"--%>
														                 <%--label-size="flex-row-label"--%>
														                 <%--title="am"--%>
														                 <%--ca-template="juno" >--%>

														<%--</ca-field-toggle>--%>
															<label class="juno-checkbox">
																<input id="check-am" type="checkbox">
																<span class="checkmark"></span>
															</label>
														</div>
													</div>
													<div class="flexV flexGrow">
														<div class="flex-row">
															<label for="check-pm" class="flex-row-label med">pm</label>
															<label class="juno-checkbox">
																<input id="check-pm" type="checkbox">
																<span class="checkmark"></span>
															</label>
														</div>
														<div class="flex-row">
															<label for="check-critical" class="flex-row-label med">Critical</label>
															<label class="juno-checkbox">
																<input id="check-critical" type="checkbox">
																<span class="checkmark"></span>
															</label>
														</div>
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
								<!-- location/site -->
								<div class="flex-row">
									<label for="select-site" class="flex-row-label">Site</label>
									<select id="select-site" class="flex-row-content"></select>
								</div>

								<div class="flex-row">
									<label for="input-reason" class="flex-row-label">Reason</label>
									<input id="input-reason" type="text" class="flex-row-content">
								</div>
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
						<div class="bottom-buttons flexGrow">
							<div class="pull-right">
								<button
										type="button"
										class="btn btn-default"
										ng-click="cancel()"
										ng-disabled="isWorking()">Cancel
								</button>

								<button
										type="button"
										class="btn btn-primary"
										tooltip-placement="top"
										tooltip-append-to-body="true"
										uib-tooltip="{{keyBinding.getTooltip(keyBindSettings, 'ctrl+enter')}}"
										>Print
								</button>

								<button
										type="submit"
										class="btn btn-success"
										tooltip-placement="top"
										tooltip-append-to-body="true"
										uib-tooltip="{{keyBinding.getTooltip(keyBindSettings, 'ctrl+enter')}}"
										ng-show="!editMode"
										ng-disabled="isWorking()">Create
								</button>

								<button
										type="submit"
										class="btn btn-success"
										tooltip-placement="top"
										tooltip-append-to-body="true"
										uib-tooltip="{{keyBinding.getTooltip(keyBindSettings, 'ctrl+enter')}}"
										ng-show="editMode"
										ng-disabled="isWorking()">Modify
								</button>

								<button
										type="button"
										class="btn btn-success"
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
				</div>
			</form>
		</div>
		<div id="tabAppointmentHistory" class="tab-pane"
		     ng-show="eventController.activeTab == eventController.tabEnum.appointmentHistory">
			<span>TO DO</span>
		</div>
	</div>
</div>

<div id="start-time-auto-wrapper"></div>
<div id="end-time-auto-wrapper"></div>
