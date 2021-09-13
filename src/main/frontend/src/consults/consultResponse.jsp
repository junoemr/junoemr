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


<div class="col-md-12" id="consult-response-section">
	<div class="row">
		<div class="col-md-12">
			<h2>Consultation Response</h2>
			<hr>
		</div>
	</div>


	<div class="col-md-12 alert alert-success" ng-show="consultResponseCtrl.consultSaving">
		Saving...
	</div>

	<div id="left_pane" class="col-md-2" ng-show="consultResponseCtrl.consultReadAccess">
		<label class="control-label">Patient Details:</label>
		<div class="demographic">
			<p>{{consultResponseCtrl.consult.demographic.lastName}}, {{consultResponseCtrl.consult.demographic.firstName}} ({{consultResponseCtrl.consult.demographic.title}})</p>
			<p>DOB: {{consultResponseCtrl.consult.demographic.dateOfBirth | date:'yyyy-MM-dd'}} ({{consultResponseCtrl.consult.demographic.age.years}})</p>
			<p>Sex: {{consultResponseCtrl.consult.demographic.sexDesc}}</p>
			<p>HIN: {{consultResponseCtrl.consult.demographic.hin}} - {{consultResponseCtrl.consult.demographic.ver}}</p>
			<p>Address:</p>
			<address>
			{{consultResponseCtrl.consult.demographic.address.address}}<br/>
			{{consultResponseCtrl.consult.demographic.address.city}}, {{consultResponseCtrl.consult.demographic.address.province}}, {{consultResponseCtrl.consult.demographic.address.postal}}<br>
			</address>
			<p>Phone (H): {{consultResponseCtrl.consult.demographic.phone}}</p>
			<p>Phone (W): {{consultResponseCtrl.consult.demographic.alternativePhone}}</p>
			<p>Phone (C): {{consultResponseCtrl.consult.demographic.cellPhone}}</p>
			<p>Email: {{consultResponseCtrl.consult.demographic.email}}</p>
			<p>MRP: {{consultResponseCtrl.consult.demographic.provider.firstName}}, {{consultResponseCtrl.consult.demographic.provider.lastName}}</p>
		</div>
		<br/>
		<div id="consult_status">
			<label class="control-label">Consultation Status:</label>
			<div class="form-group">
				<select class="form-control"
						ng-model="consultResponseCtrl.consult.status"
						ng-required="true"
						ng-options="status.value as status.name for status in consultResponseCtrl.statuses">
				</select>
			</div>
		</div>
		<br/>
		<button type="button"
				class="btn btn-small btn-primary"
				ng-click="consultResponseCtrl.attachFiles()">
			Attachments
		</button>
		<ol style="padding-left:20px;">
			<li ng-repeat="attachment in consultResponseCtrl.consult.attachments">
				<a ng-click="consultResponseCtrl.openAttach(attachment)"
				   title="{{consultResponseCtrl.attachment.displayName}}">
					{{attachment.shortName}}
				</a>
			</li>
		</ol>
	</div><!-- Left pane End -->

	<div id="right_pane" class="col-md-10" ng-show="consultResponseCtrl.consultReadAccess">
		<div class="col-md-6"><!-- Letterhead -->
			<label>Letterhead</label>
			<div class="well">
				<form class="form-horizontal">
					<div class="form-group">
						<div class="col-sm-12">
							<select id="letterhead" class="form-control"
									ng-model="consultResponseCtrl.consult.letterhead"
									ng-options="letterhead.name for letterhead in consultResponseCtrl.consult.letterheadList track by letterhead.id"
									ng-change="consultResponseCtrl.changeLetterhead(consultResponseCtrl.consult.letterhead)">
							</select>
						</div>
					</div>
					<div class="form-group">
						<div class="col-sm-12">
							<label>Address:</label>
							{{consultResponseCtrl.consult.letterheadAddress}}
						</div>
						<div class="col-sm-12">
							<label>Phone:</label>
							{{consultResponseCtrl.consult.letterheadPhone}}
						</div>
						<div class="col-sm-12">
							<label>Fax: </label>
							{{consultResponseCtrl.consult.letterheadFax}}
						</div>
					</div>
				</form>
			</div>
		</div><!-- Letterhead End-->
		<div class="col-md-6"><!-- Specialist -->
			<label>Referring Doctor</label>
			<div class="well">
				<form class="form-horizontal">
					<div class="form-group">
						<div class="col-sm-12">
							<select id="refDocId" class="form-control"
									title="Referring Doctor"
									ng-model="consultResponseCtrl.consult.referringDoctor"
									ng-options="refDoc as refDoc.name for refDoc in consultResponseCtrl.consult.referringDoctorList">
							</select>
						</div>
					</div>

					<div class="form-group">
						<div class="col-sm-12">
							<label>Address:</label>
							{{consultResponseCtrl.consult.referringDoctor.streetAddress}}
						</div>
						<div class="col-sm-12">
							<label>Phone:</label>
							{{consultResponseCtrl.consult.referringDoctor.phoneNumber}}
						</div>
						<div class="col-sm-12">
							<label>Fax: </label>
							{{consultResponseCtrl.consult.referringDoctor.faxNumber}}
						</div>
					</div>
				</form>
			</div>
		</div><!-- Specialist End -->
		<div class="clear"></div>

		<div class="col-md-12">
			<form class="consult-response-form">
				<div class="form-group col-md-6">
					<label class="control-label">Response Date</label>
					<juno-datepicker-popup
							juno-model="consultResponseCtrl.consult.responseDate"
							show-icon="true"
							type="Input">
					</juno-datepicker-popup>
				</div>
				<div class="form-group col-md-6">
					<label class="control-label">Send To</label>
					<select id="sendTo" class="form-control inline"
							ng-model="consultResponseCtrl.consult.sendTo"
							ng-required="true"
							ng-options="sendTo for sendTo in consultResponseCtrl.consult.sendToList">
					</select>
				</div>
				<div class="form-group col-md-6">
					<label class="control-label">Referral Date</label>
					<juno-datepicker-popup
							juno-model="consultResponseCtrl.consult.referralDate"
							show-icon="true"
							type="Input">
					</juno-datepicker-popup>
				</div>
				<div class="form-group col-md-6">
					<label class="control-label">Urgency</label>
					<select id="urgency"
							class="form-control inline"
							ng-model="consultResponseCtrl.consult.urgency"
							ng-required="true"
							ng-options="urgency.value as urgency.name for urgency in consultResponseCtrl.urgencies">
					</select>
				</div>
				<div class="form-group col-md-12">
					<label class="control-label">Referrer Instructions</label>
					<textarea rows="4" class="form-control" readOnly>{{consultResponseCtrl.consult.referringDoctor.annotation}}</textarea>
				</div>

				<div class="form-group col-md-6">
					<label class="control-label">Last Follow-up Date</label>
					<juno-datepicker-popup
							juno-model="consultResponseCtrl.consult.followUpDate"
							show-icon="true"
							type="Input">
					</juno-datepicker-popup>
				</div>

				<div class="form-group col-md-6">
					<div class="well">
						<div class="appointment-date-time-select">
							<div class="date-select-wrapper">
								<label class="control-label">Appointment Date</label>
								<juno-datepicker-popup juno-model="consultResponseCtrl.consult.appointmentDate"
								                       show-icon="true"
								                       type="Input">
								</juno-datepicker-popup>
							</div>
							<div class="time-select-wrapper">
								<label class="control-label">Appointment Time</label>
								<div class="time-select">
									<select class="form-control"
									        ng-model="consultResponseCtrl.consult.appointmentHour"
									        ng-options="hour for hour in consultResponseCtrl.hours"
									        ng-change="consultResponseCtrl.setAppointmentTime()">
									</select>
									<span>:</span>
									<select class="form-control"
									        ng-model="consultResponseCtrl.consult.appointmentMinute"
									        ng-options="minute for minute in consultResponseCtrl.minutes"
									        ng-change="consultResponseCtrl.setAppointmentTime()">
									</select>
								</div>
							</div>
						</div>
					</div>
				</div>

				<div class="col-md-12 form-group"><!-- Reason for Consultation -->
					<label class="control-label">Reason for Consultation</label>
					<textarea rows="4" class="form-control" ng-model="consultResponseCtrl.consult.reasonForReferral"></textarea>
				</div><!-- Reason End -->

				<div class="form-group col-md-12">
					<label class="control-label">Appointment Notes</label>
					<textarea rows="5"
							  class="form-control"
							  ng-model="consultResponseCtrl.consult.appointmentNote">
					</textarea>
				</div>

				<div class="form-group col-md-12">
					<label class="control-label">Examination</label>
					<textarea  rows="5" class="form-control" ng-model="consultResponseCtrl.consult.examination">
					</textarea>

				</div>
				<div class="form-group col-md-12">
					<label class="control-label">Impression</label>
					<textarea  rows="5" class="form-control" ng-model="consultResponseCtrl.consult.impression"></textarea>
				</div>

				<div class="form-group col-md-12">
					<label class="control-label">Plan</label>
					<textarea  rows="5" class="form-control" ng-model="consultResponseCtrl.consult.plan"></textarea>
				</div>

				<div id="clinical-note" class="form-group col-md-12 info-add-buttons"><!-- Clinic Notes -->
					<label class="control-label">Pertinent clinical information</label>
					<div class="info-add-buttons">
						<button type="button" class="btn btn-sm btn-success" ng-click="consultResponseCtrl.getFamilyHistory('clinicalInfo');">
							<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
							Family History
						</button>
						<button type="button" class="btn btn-sm btn-success" ng-click="consultResponseCtrl.getMedicalHistory('clinicalInfo');">
							<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
							Medical History
						</button>
						<button type="button" class="btn btn-sm btn-success" ng-click="consultResponseCtrl.getSocialHistory('clinicalInfo');">
							<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
							Social History
						</button>
						<button type="button" class="btn btn-sm btn-success" ng-click="consultResponseCtrl.getOngoingConcerns('clinicalInfo');">
							<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
							Ongoing Concerns
						</button>
						<button type="button" class="btn btn-sm btn-success" ng-click="consultResponseCtrl.getOtherMeds('clinicalInfo');">
							<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
							Other Meds
						</button>
						<button type="button" class="btn btn-sm btn-success" ng-click="consultResponseCtrl.getReminders('clinicalInfo');">
							<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
							Reminders
						</button>
					</div>
					<textarea id="clinicalInfo" cols="80" rows="5" class="form-control"
							  placeholder="Use the buttons above to insert data from the patients chart"
							  ng-model="consultResponseCtrl.consult.clinicalInfo">
					</textarea>
				</div>

				<div id="concurrent-problem" class="form-group col-md-12"><!-- Concurrent Problem -->
					<div>
						<label class="control-label">Significant Concurrent Problems</label>
						<div class="info-add-buttons">
							<button type="button" class="btn btn-sm btn-success" ng-click="consultResponseCtrl.getFamilyHistory('concurrentProblems');">
								<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
								Family History
							</button>
							<button type="button" class="btn btn-sm btn-success" ng-click="consultResponseCtrl.getMedicalHistory('concurrentProblems');">
								<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
								Medical History
							</button>
							<button type="button" class="btn btn-sm btn-success" ng-click="consultResponseCtrl.getSocialHistory('concurrentProblems');">
								<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
								Social History
							</button>
							<button type="button" class="btn btn-sm btn-success" ng-click="consultResponseCtrl.getOngoingConcerns('concurrentProblems');">
								<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
								Ongoing Concerns
							</button>
							<button type="button" class="btn btn-sm btn-success" ng-click="consultResponseCtrl.getOtherMeds('concurrentProblems');">
								<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
								Other Meds
							</button>
							<button type="button" class="btn btn-sm btn-success" ng-click="consultResponseCtrl.getReminders('concurrentProblems');">
								<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
								Reminders
							</button>
						</div>
						<textarea id="concurrentProblems"
								  rows="5"
								  class="form-control"
								  placeholder="Use the buttons above to insert data from the patients chart"
								  ng-model="consultResponseCtrl.consult.concurrentProblems"></textarea>
					</div>
				</div>

				<div class="form-group col-md-12"><!-- Alergies / Current Medications -->
					<label class="control-label">Allergies</label>
					<textarea rows="5" class="form-control" ng-model="consultResponseCtrl.consult.allergies"></textarea>
				</div><!-- Alergies End -->

				<div class="form-group col-md-12">
					<label class="control-label">Current Medications
					</label>
					<div class="info-add-buttons">
						<button type="button" class="btn btn-sm btn-success" ng-click="consultResponseCtrl.getOtherMeds('currentMeds');">
							<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
							Other Meds
						</button>
					</div>

					<textarea id="currentMeds"
							  cols="80"
							  rows="5"
							  class="form-control"
							  ng-model="consultResponseCtrl.consult.currentMeds"
							  placeholder="Use the button above to insert Other Meds data from the patients chart">
					</textarea>
				</div><!-- Current Medications End -->
			</form>
		</div>
	</div><!-- Right pane End -->

	<div class="wrapper-action col-sm-12" ng-show="consultResponseCtrl.consultReadAccess"><!-- Action Buttons -->
		<button type="button" class="btn btn-large btn-warning action"
		        ng-disabled="consultResponseCtrl.loadingQueue.isLoading"
				ng-click="consultResponseCtrl.printPreview()"
				ng-show="consultResponseCtrl.consult.id!=null && consultChanged<=0">
			Print Preview
		</button>&nbsp;
		<button type="button" class="btn btn-large btn-warning action"
		        ng-disabled="consultResponseCtrl.loadingQueue.isLoading"
				ng-click="consultResponseCtrl.sendFax()"
				ng-show="consultResponseCtrl.consult.id!=null && consultChanged<=0">
			Send Fax
		</button>&nbsp;
		<button type="button" class="btn btn-large btn-primary action"
		        ng-disabled="consultResponseCtrl.loadingQueue.isLoading"
				ng-click="consultResponseCtrl.save()"
				ng-show="consultResponseCtrl.consultChanged>0">
			Save
		</button>&nbsp;
		<button type="button" class="btn btn-large btn-default action"
		        ng-disabled="consultResponseCtrl.loadingQueue.isLoading"
				ng-click="consultResponseCtrl.close()">
			Close
		</button>&nbsp;
	</div>

	<div ng-show="consultReadAccess != null && consultReadAccess == false"
		class="col-lg-12">
		<h3 class="text-danger">
			<span class="glyphicon glyphicon-warning-sign"></span>You don't have access to view consult
		</h3>
	</div>
</div>
