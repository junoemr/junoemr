<%--

    Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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

    This software was written for the
    Department of Family Medicine
    McMaster University
    Hamilton
    Ontario, Canada

--%>
<div class="col-sm-12"  id="consult-request-section">
	<div class="row">
		<div class="col-md-12">
			<h2>Consultation Request</h2>
			<hr>
		</div>
	</div>
	<div class="col-md-12 alert alert-success" ng-show="$ctrl.consultSaving">
		Saving...
	</div>
	<juno-security-check show-placeholder="true" permissions="$ctrl.SecurityPermissions.ConsultationRead">
		<div class="row">
			<div id="left_pane" class="col-md-2">
				<label class="control-label">Patient Details</label>
				<div class="demographic">
					<p>{{$ctrl.consult.demographic.displayName}} ({{$ctrl.consult.demographic.title}})</p>
					<p>DOB: {{$ctrl.consult.demographic.displayDateOfBirth}} ({{$ctrl.consult.demographic.displayAge}})</p>
					<p>Sex: {{$ctrl.consult.demographic.displaySex}}</p>
					<p>HIN: {{$ctrl.consult.demographic.healthNumber}} - {{$ctrl.consult.demographic.healthNumberVersion}}</p>
					<p>Address:</p>
					<address>
					{{$ctrl.consult.demographic.address.displayLine1of2}}<br/>
					{{$ctrl.consult.demographic.address.displayLine2of2}}
					</address>
					<p>Phone (H): {{$ctrl.consult.demographic.homePhone.formattedForDisplay}}</p>
					<p>Phone (W): {{$ctrl.consult.demographic.workPhone.formattedForDisplay}}</p>
					<p>Phone (C): {{$ctrl.consult.demographic.cellPhone.formattedForDisplay}}</p>
					<p>Email: {{$ctrl.consult.demographic.email}}</p>
					<p>MRP: {{$ctrl.consult.demographic.mrpProvider.displayName}}</p>
				</div>
				<br/>
				<div id="consult_status">
					<label class="control-label">Consultation Status:</label>
					<div class="form-group">
						<select class="form-control" ng-model="$ctrl.consult.status"
							ng-required="true"
							    ng-options="status.value as status.name for status in $ctrl.statuses">
						</select>
					</div>
				</div>
				<br/>
				<button type="button" class="btn btn-small btn-primary" ng-click="$ctrl.attachFiles()">Attachments</button>
				<ol style="padding-left:20px;">
					<li ng-repeat="attachment in $ctrl.consult.attachments">
						<a ng-click="$ctrl.openAttach(attachment)" title="{{attachment.displayName}}">{{attachment.shortName}}</a>
					</li>
				</ol>
			</div><!-- Left pane End -->

			<div id="right_pane" class="col-md-10">
				<div class="col-md-12">
					<div class="col-md-6"><!-- Letterhead -->
						<label>Letterhead</label>
						<div class="well">
							<form class="form-horizontal">
								<div class="form-group">
									<%--<label class="col-sm-2 control-label">Name</label>--%>
									<div class="col-sm-12">
										<juno-select
												ng-model="$ctrl.selectedLetterhead"
												options="$ctrl.letterheadOptions"
												on-change="$ctrl.changeLetterhead(option.data)">
										</juno-select>
									</div>
								</div>
								<div class="form-group">
									<div class="col-sm-12">
										<label>Address:</label>
										{{$ctrl.consult.letterheadAddress}}
									</div>
									<div class="col-sm-12">
										<label>Phone:</label>
										{{$ctrl.consult.letterheadPhone}}
									</div>
									<div class="col-sm-12">
										<label>Fax: </label>
										{{$ctrl.consult.letterheadFax}}
									</div>
								</div>
							</form>
						</div>
					</div><!-- Letterhead End-->
					<div class="col-md-6"><!-- Specialist -->
						<label>Specialist</label>
						<div class="well">
							<form class="form-horizontal">
								<div class="form-group">
									<div class="col-sm-6">
										<juno-select
												ng-model="$ctrl.consult.serviceId"
												options="$ctrl.serviceOptions"
												title="Service">
										</juno-select>
									</div>

									<div class="col-sm-6">
										<select id="specId" class="form-control"
												title="Consultant"
												ng-model="$ctrl.consult.professionalSpecialist"
												ng-options="spec.name for spec in $ctrl.specialists">
										</select>
									</div>
								</div>
								<div class="form-group">
									<div class="col-sm-12">
										<label>Address:</label>
										{{$ctrl.consult.professionalSpecialist.streetAddress}}
									</div>
									<div class="col-sm-12">
										<label>Phone:</label>
										{{$ctrl.consult.professionalSpecialist.phoneNumber}}
									</div>
									<div class="col-sm-12">
										<label>Fax: </label>
										{{$ctrl.consult.professionalSpecialist.faxNumber}}
									</div>
								</div>
							</form>
						</div>
					</div><!-- Specialist End -->
				</div>
				<div class="clear"></div>
			
				<div class="col-md-12"><!-- Referral -->
					<form class="consult-request-form">
						<div class="form-group col-md-6">
							<juno-select ng-model="$ctrl.consult.providerNo"
								placeholder="Referral Practitioner"
								options="$ctrl.providers"
								label="Referral Practitioner"
								label-position="$ctrl.labelPosition.TOP"
								on-change="$ctrl.onReferralPractitionerSelected(value)"
								component-style="$ctrl.resolve.style">
							</juno-select>
						</div>
						<div class="form-group col-md-6">
							<label class="control-label">Referral Date</label>
							<juno-datepicker-popup juno-model="$ctrl.consult.referralDate" show-icon="true" type="Input"> </juno-datepicker-popup>
						</div>
						<div class="form-group col-md-6">
							<label class="control-label">Urgency</label>
							<select id="urgency" class="form-control"
									ng-model="$ctrl.consult.urgency"
									ng-required="true"
									ng-options="urgency.value as urgency.name for urgency in $ctrl.urgencies"></select>
						</div>
						<div class="form-group col-md-6">
							<label class="control-label">Send To</label>
							<select id="sendTo" class="form-control" ng-model="$ctrl.consult.sendTo" ng-required="true" ng-options="sendTo for sendTo in $ctrl.consult.sendToList"/>
						</div>
						<div class="form-group col-md-6">
							<label class="control-label">Referrer Instructions</label>
							<textarea cols="80" rows="4" class="form-control" readOnly>{{$ctrl.consult.professionalSpecialist.annotation}}</textarea>
						</div>

						<div class="form-group col-md-6">
							<label class="control-label">Last Follow-up Date</label>
							<juno-datepicker-popup juno-model="$ctrl.consult.followUpDate" show-icon="true" type="Input"> </juno-datepicker-popup>
						</div>

						<div class="form-group col-md-6">
							<div class="well">
								<div>
									<label class="control-label">Patient Will Book</label>
									<input class="form-control big-checkbox" type="checkbox" id="willBook"
									       ng-model="$ctrl.consult.patientWillBook"/>
								</div>
								<div class="appointment-date-time-select">
									<div class="date-select-wrapper">
										<label class="control-label">Appointment Date</label>
										<juno-datepicker-popup juno-model="$ctrl.consult.appointmentDate"
										                       show-icon="true" type="Input"
										                       disable-input="$ctrl.consult.patientWillBook">
										</juno-datepicker-popup>
									</div>
									<div class="time-select-wrapper">
										<label class="control-label">Appointment Time</label>
										<div class="time-select">
											<select class="form-control"
											        ng-model="$ctrl.consult.appointmentHour"
											        ng-options="hour for hour in $ctrl.hours"
											        ng-change="$ctrl.setAppointmentTime()"
											        ng-disabled="$ctrl.consult.patientWillBook">
											</select>
											<span>:</span>
											<select class="form-control"
											        ng-model="$ctrl.consult.appointmentMinute"
											        ng-options="minute for minute in $ctrl.minutes"
											        ng-change="$ctrl.setAppointmentTime()"
											        ng-disabled="$ctrl.consult.patientWillBook">
											</select>
										</div>
									</div>
								</div>
							</div>
						</div>

						<div class="form-group col-md-12">
							<label class="control-label">Appointment Notes</label>
							<textarea rows="5" class="form-control" ng-model="$ctrl.consult.statusText"></textarea>
						</div>

						<div class="form-group col-md-12">
							<label class="control-label">Reason for Consultation</label>
							<textarea rows="4" class="form-control" ng-model="$ctrl.consult.reasonForReferral"></textarea>
						</div>

						<div class="form-group col-md-12" id="clinical-note"><!-- Clinic Notes -->
								<label class="control-label">Pertinent clinical information</label>
								<div class="info-add-buttons">
									<button type="button" class="btn btn-sm btn-success" ng-click="$ctrl.getFamilyHistory('clinicalInfo');">
										<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
										Family History
									</button>
									<button type="button" class="btn btn-sm btn-success" ng-click="$ctrl.getMedicalHistory('clinicalInfo');">
										<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
										Medical History
									</button>
									<button type="button" class="btn btn-sm btn-success" ng-click="$ctrl.getSocialHistory('clinicalInfo');">
										<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
										Social History
									</button>
									<button type="button" class="btn btn-sm btn-success" ng-click="$ctrl.getOngoingConcerns('clinicalInfo');">
										<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
										Ongoing Concerns
									</button>
									<button type="button" class="btn btn-sm btn-success" ng-click="$ctrl.getDxRegistry('clinicalInfo');">
										<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
										Dx Registry
									</button>
									<button type="button" class="btn btn-sm btn-success" ng-click="$ctrl.getOtherMeds('clinicalInfo');">
										<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
										Other Meds
									</button>
									<button type="button" class="btn btn-sm btn-success" ng-click="$ctrl.getReminders('clinicalInfo');">
										<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
										Reminders
									</button>
								</div>
								<textarea id="clinicalInfo" cols="80" rows="5" class="form-control"
									placeholder="Use the buttons above to insert data from the patients chart"
									ng-model="$ctrl.consult.clinicalInfo">
								</textarea>
						</div>

						<div class="form-group col-md-12" id="concurrent-problem"><!-- Concurrent Problem -->
							<div>
								<label class="control-label">Significant Concurrent Problems</label>
								<div class="info-add-buttons">

									<button type="button" class="btn btn-sm btn-success" ng-click="$ctrl.getFamilyHistory('concurrentProblems');">
										<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
										Family History
									</button>
									<button type="button" class="btn btn-sm btn-success" ng-click="$ctrl.getMedicalHistory('concurrentProblems');">
										<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
										Medical History
									</button>
									<button type="button" class="btn btn-sm btn-success" ng-click="$ctrl.getSocialHistory('concurrentProblems');">
										<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
										Social History
									</button>
									<button type="button" class="btn btn-sm btn-success" ng-click="$ctrl.getOngoingConcerns('concurrentProblems');">
										<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
										Ongoing Concerns
									</button>
									<button type="button" class="btn btn-sm btn-success" ng-click="$ctrl.getDxRegistry('concurrentProblems');">
										<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
										Dx Registry
									</button>
									<button type="button" class="btn btn-sm btn-success" ng-click="$ctrl.getOtherMeds('concurrentProblems');">
										<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
										Other Meds
									</button>
									<button type="button" class="btn btn-sm btn-success" ng-click="$ctrl.getReminders('concurrentProblems');">
										<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
										Reminders
									</button>
								</div>
								<textarea id="concurrentProblems"
										  rows="5"
										  class="form-control"
										  placeholder="Use the buttons above to insert data from the patients chart"
										  ng-model="$ctrl.consult.concurrentProblems">
								</textarea>
							</div>
						</div>

						<div class="form-group col-md-12">
							<label class="control-label">Current Medications</label>
							<div class="info-add-buttons">
								<button type="button" class="btn btn-sm btn-success" ng-click="$ctrl.getOtherMeds('currentMeds');">
									<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
									Other Meds
								</button>
							</div>
							<textarea id="currentMeds"
									  cols="80"
									  rows="5"
									  class="form-control"
									  ng-model="$ctrl.consult.currentMeds"
									  placeholder="Use the button above to insert Other Meds data from the patients chart">
							</textarea>
						</div>

						<div class="form-group col-md-12"><!-- Alergies / Current Medications -->
							<label class="control-label">Allergies</label>
							<textarea id="allergies" rows="5" class="form-control" ng-model="$ctrl.consult.allergies"></textarea>
						</div>
					</form>
					<hr>
				</div><!-- Appointment End -->
			</div><!-- Right pane End -->
		</div>
		<div class="wrapper-action col-sm-12"><!-- Action Buttons -->
			<button type="button" class="btn btn-large btn-success action"
			        ng-disabled="$ctrl.loadingQueue.isLoading"
			        ng-click="$ctrl.save()">
				Save
			</button>&nbsp;
			<button type="button" class="btn btn-large btn-success action"
			        ng-disabled="$ctrl.loadingQueue.isLoading"
			        ng-click="$ctrl.saveAndPrint()">
				Save & Print
			</button>&nbsp;
			<button type="button" class="btn btn-large btn-success action"
			        ng-disabled="$ctrl.loadingQueue.isLoading"
			        ng-click="$ctrl.saveAndFax()">
				Save & Fax
			</button>&nbsp;
			<button type="button" class="btn btn-large btn-success action"
			        ng-disabled="$ctrl.loadingQueue.isLoading"
			        ng-click="$ctrl.eSend()"
			        ng-show="$ctrl.eSendEnabled">
				Save & Send Electronically
			</button>&nbsp;
			<button type="button" class="btn btn-large btn-warning action"
			        ng-disabled="$ctrl.loadingQueue.isLoading"
			        ng-click="$ctrl.print($ctrl.consult.id)"
			        ng-show="$ctrl.consult.id">
				Print Preview
			</button>&nbsp;
			<button type="button" class="btn btn-large btn-default action"
			        ng-disabled="$ctrl.loadingQueue.isLoading"
			        ng-click="$ctrl.close()">
				Close
			</button>&nbsp;
		</div>
	</juno-security-check>
</div>
