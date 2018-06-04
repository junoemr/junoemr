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
	<div class="col-md-12 alert alert-success" ng-show="consultRequestCtrl.consultSaving">
		Saving...
	</div>
	<div class="row">
		<div id="left_pane" class="col-md-2" ng-show="consultRequestCtrl.consultReadAccess">
			<label class="control-label">Patient Details</label>
			<div class="demographic">
				<p>{{consultRequestCtrl.consult.demographic.lastName}}, {{consultRequestCtrl.consult.demographic.firstName}} ({{consultRequestCtrl.consult.demographic.title}})</p>
				<p>DOB: {{consultRequestCtrl.consult.demographic.dateOfBirth | date:'yyyy-MM-dd'}} ({{consultRequestCtrl.consult.demographic.age.years}})</p> 		
				<p>Sex: {{consultRequestCtrl.consult.demographic.sexDesc}}</p> 
				<p>HIN: {{consultRequestCtrl.consult.demographic.hin}} - {{consultRequestCtrl.consult.demographic.ver}}</p> 
				<p>Address:</p> 
				<address>
				{{consultRequestCtrl.consult.demographic.address.address}}<br/>
				{{consultRequestCtrl.consult.demographic.address.city}}, {{consultRequestCtrl.consult.demographic.address.province}}, {{consultRequestCtrl.consult.demographic.address.postal}}<br>
				</address>
				<p>Phone (H): {{consultRequestCtrl.consult.demographic.phone}}</p>
				<p>Phone (W): {{consultRequestCtrl.consult.demographic.alternativePhone}}</p>
				<p>Phone (C): {{consultRequestCtrl.consult.demographic.cellPhone}}</p>
				<p>Email: {{consultRequestCtrl.consult.demographic.email}}</p>
				<p>MRP: {{consultRequestCtrl.consult.demographic.provider.firstName}}, {{consultRequestCtrl.consult.demographic.provider.lastName}}</p>
			</div>
			<br/>
			<div id="consult_status">
				<label class="control-label">Consultation Status:</label>
				<div class="form-group">
					<select class="form-control" ng-model="consultRequestCtrl.consult.status"
						ng-required="true" 
						    ng-options="status.value as status.name for status in consultRequestCtrl.statuses">
					</select>
				</div>
			</div>
			<br/>
			<button type="button" class="btn btn-small btn-primary" ng-click="consultRequestCtrl.attachFiles()">Attachments</button>
			<ol style="padding-left:20px;">
				<li ng-repeat="attachment in consultRequestCtrl.consult.attachments">
					<a ng-click="consultRequestCtrl.openAttach(attachment)" title="{{attachment.displayName}}">{{attachment.shortName}}</a>
				</li>
			</ol>
		</div><!-- Left pane End -->

		<div id="right_pane" class="col-md-10" ng-show="consultRequestCtrl.consultReadAccess">
			<div class="col-md-12">
				<div class="col-md-6"><!-- Letterhead -->
					<label>Letterhead</label>
					<div class="well">
						<form class="form-horizontal">
							<div class="form-group">
								<%--<label class="col-sm-2 control-label">Name</label>--%>
								<div class="col-sm-12">
									<select id="letterhead" class="form-control"
											ng-model="consultRequestCtrl.consult.letterheadName"
											ng-options="letterhead.id as letterhead.name for letterhead in consultRequestCtrl.consult.letterheadList"
											ng-change="consultRequestCtrl.changeLetterhead(consultRequestCtrl.consult.letterheadName)">
									</select>
								</div>
							</div>
							<div class="form-group">
								<div class="col-sm-12">
									<label>Address:</label>
									{{consultRequestCtrl.consult.letterheadAddress}}
								</div>
								<div class="col-sm-12">
									<label>Phone:</label>
									{{consultRequestCtrl.consult.letterheadPhone}}
								</div>
								<div class="col-sm-12">
									<label>Fax: </label>
									{{consultRequestCtrl.consult.letterheadFax}}
									<select id="letterheadFax" class="form-control inline" style="width: auto;"
											ng-model="consultRequestCtrl.consult.letterheadFax"
											ng-options="fax.faxNumber as fax.faxUser for fax in consultRequestCtrl.consult.faxList">
										<option value="" disabled selected>Select Fax</option>
									</select>
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
									<select id="serviceId" class="form-control"
											title="Service"
											ng-model="consultRequestCtrl.consult.serviceId"
											ng-options="service.serviceId as service.serviceDesc for service in consultRequestCtrl.consult.serviceList"
											ng-required="true"
											ng-change="consultRequestCtrl.changeService(consultRequestCtrl.consult.serviceId)">
									</select>
								</div>

								<div class="col-sm-6">
									<select id="specId" class="form-control"
											title="Consultant"
											ng-model="consultRequestCtrl.consult.professionalSpecialist"
											ng-options="spec.name for spec in consultRequestCtrl.specialists">
									</select>
								</div>
							</div>
							<div class="form-group">
								<div class="col-sm-12">
									<label>Address:</label>
									{{consultRequestCtrl.consult.professionalSpecialist.streetAddress}}
								</div>
								<div class="col-sm-12">
									<label>Phone:</label>
									{{consultRequestCtrl.consult.professionalSpecialist.phoneNumber}}
								</div>
								<div class="col-sm-12">
									<label>Fax: </label>
									{{consultRequestCtrl.consult.professionalSpecialist.faxNumber}}
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
						<label class="control-label">Referral Date</label>
						<%--<input id="dp-referralDate" type="text" class="form-control" ng-model="consult.referralDate" placeholder="Referral Date" datepicker-popup="yyyy-MM-dd" datepicker-append-to-body="true" is-open="page.refDatePicker" ng-click="page.refDatePicker=true"/>--%>
						<juno-datepicker-popup juno-model="consultRequestCtrl.consult.referralDate" show-icon="true" type="Input"> </juno-datepicker-popup>
					</div>
					<div class="form-group col-md-6">
						<label class="control-label">Urgency</label>
						<select id="urgency" class="form-control"
								ng-model="consultRequestCtrl.consult.urgency"
								ng-required="true"
								ng-options="urgency.value as urgency.name for urgency in consultRequestCtrl.urgencies"></select>
					</div>
					<div class="form-group col-md-6">
						<label class="control-label">Send To</label>
						<select id="sendTo" class="form-control" ng-model="consultRequestCtrl.consult.sendTo" ng-required="true" ng-options="sendTo for sendTo in consultRequestCtrl.consult.sendToList"/>
					</div>
					<div class="form-group col-md-6">
						<label class="control-label">Referrer Instructions</label>
						<textarea cols="80" rows="4" class="form-control" readOnly>{{consultRequestCtrl.consult.professionalSpecialist.annotation}}</textarea>
					</div>
		
					<div class="form-group col-md-6">
						<label class="control-label">Appointment Date</label>
						<%--<input id="dp-appointmentDate"--%>
							<%--type="text"--%>
							<%--class="form-control"--%>
							<%--ng-model="consultRequestCtrl.consult.appointmentDate"--%>
							<%--placeholder="Appointment Date"--%>
							<%--datepicker-popup="yyyy-MM-dd"--%>
							<%--datepicker-append-to-body="true"--%>
							<%--is-open="page.aptDatePicker"--%>
							<%--ng-click="page.aptDatePicker=true"--%>
							<%--ng-disabled="consultRequestCtrl.consult.patientWillBook"/>--%>
						<juno-datepicker-popup juno-model="consultRequestCtrl.consult.appointmentDate" show-icon="true" type="Input" disable-input="consultRequestCtrl.consult.patientWillBook"> </juno-datepicker-popup>
					</div>
					<div class="form-group col-md-6">
						<label class="control-label">Appointment Time</label>
						<span>
							<select class="form-control"
									ng-model="consultRequestCtrl.consult.appointmentHour"
									ng-options="hour for hour in consultRequestCtrl.hours"
									ng-change="consultRequestCtrl.setAppointmentTime()" ng-disabled="consultRequestCtrl.consult.patientWillBook">
							</select> :
							<select class="form-control"
									ng-model="consultRequestCtrl.consult.appointmentMinute"
									ng-options="minute for minute in consultRequestCtrl.minutes"
									ng-change="consultRequestCtrl.setAppointmentTime()" ng-disabled="consultRequestCtrl.consult.patientWillBook">
							</select>
						</span>
						<%--<div class="input-group bootstrap-timepicker timepicker">--%>
							<%--<input id="timepicker1" type="text"--%>
								<%--class="form-control input-small"--%>
								<%--data-provide="timepicker"--%>
								<%--placeholder="Appointment Time"--%>
								<%--ng-model="consultRequestCtrl.appointmentTimeInput"--%>
								<%--ng-change="consultRequestCtrl.changeAppointmentTime()">--%>
							<%--<span class="input-group-addon"><i class="glyphicon glyphicon-time"></i></span>--%>
						<%--</div>--%>
					</div>
					<div class="form-group col-md-6">
						<label class="control-label">Last Follow-up Date</label>
						<%--<input id="dp-followUpDate" type="text" class="form-control" --%>
							<%--ng-model="consultRequestCtrl.consult.followUpDate" --%>
							<%--placeholder="Follow Up Date"  --%>
							<%--datepicker-popup="yyyy-MM-dd" --%>
							<%--datepicker-append-to-body="true" --%>
							<%--is-open="consultRequestCtrl.page.lfdDatePicker" --%>
							<%--ng-click="consultRequestCtrl.page.lfdDatePicker=true"/>--%>
						<juno-datepicker-popup juno-model="consultRequestCtrl.consult.followUpDate" show-icon="true" type="Input"> </juno-datepicker-popup>

					</div>
					<div class="form-group col-md-6">
						<label class="control-label">Patient Will Book</label>
						<input class="form-control big-checkbox" type="checkbox" id="willBook" ng-model="consultRequestCtrl.consult.patientWillBook"/>
					</div>

					<div class="form-group col-md-12">
						<label class="control-label">Appointment Notes</label>
						<textarea rows="5" class="form-control" ng-model="consultRequestCtrl.consult.statusText"></textarea>
					</div>

					<div class="form-group col-md-12">
						<label class="control-label">Reason for Consultation</label>
						<textarea rows="4" class="form-control" ng-model="consultRequestCtrl.consult.reasonForReferral"></textarea>
					</div>

					<div class="form-group col-md-12" id="clinical-note"><!-- Clinic Notes -->
							<label class="control-label">Pertinent clinical information</label>
							<div class="info-add-buttons">
								<button type="button" class="btn btn-sm btn-success" ng-click="consultRequestCtrl.getFamilyHistory('clinicalInfo');">
									<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
									Family History
								</button>
								<button type="button" class="btn btn-sm btn-success" ng-click="consultRequestCtrl.getMedicalHistory('clinicalInfo');">
									<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
									Medical History
								</button>
								<button type="button" class="btn btn-sm btn-success" ng-click="consultRequestCtrl.getSocialHistory('clinicalInfo');">
									<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
									Social History
								</button>
								<button type="button" class="btn btn-sm btn-success" ng-click="consultRequestCtrl.getOngoingConcerns('clinicalInfo');">
									<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
									Ongoing Concerns
								</button>
								<button type="button" class="btn btn-sm btn-success" ng-click="consultRequestCtrl.getOtherMeds('clinicalInfo');">
									<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
									Other Meds
								</button>
								<button type="button" class="btn btn-sm btn-success" ng-click="consultRequestCtrl.getReminders('clinicalInfo');">
									<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
									Reminders
								</button>
							</div>					
							<textarea id="clinicalInfo" cols="80" rows="5" class="form-control" 
								placeholder="Use the buttons above to insert data from the patients chart"
								ng-model="consultRequestCtrl.consult.clinicalInfo">
							</textarea>
					</div>

					<div class="form-group col-md-12" id="concurrent-problem"><!-- Concurrent Problem -->
						<div>
							<label class="control-label">Significant Concurrent Problems</label>
							<div class="info-add-buttons">

								<button type="button" class="btn btn-sm btn-success" ng-click="consultRequestCtrl.getFamilyHistory('concurrentProblems');">
									<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
									Family History
								</button>
								<button type="button" class="btn btn-sm btn-success" ng-click="consultRequestCtrl.getMedicalHistory('concurrentProblems');">
									<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
									Medical History
								</button>
								<button type="button" class="btn btn-sm btn-success" ng-click="consultRequestCtrl.getSocialHistory('concurrentProblems');">
									<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
									Social History
								</button>
								<button type="button" class="btn btn-sm btn-success" ng-click="consultRequestCtrl.getOngoingConcerns('concurrentProblems');">
									<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
									Ongoing Concerns
								</button>
								<button type="button" class="btn btn-sm btn-success" ng-click="consultRequestCtrl.getOtherMeds('concurrentProblems');">
									<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
									Other Meds
								</button>
								<button type="button" class="btn btn-sm btn-success" ng-click="consultRequestCtrl.getReminders('concurrentProblems');">
									<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
									Reminders
								</button>
							</div>
							<textarea id="concurrentProblems"
									  rows="5"
									  class="form-control"
									  placeholder="Use the buttons above to insert data from the patients chart"
									  ng-model="consultRequestCtrl.consult.concurrentProblems">
							</textarea>
						</div>
					</div>

					<div class="form-group col-md-12">
						<label class="control-label">Current Medications</label>
						<div class="info-add-buttons">
							<button type="button" class="btn btn-sm btn-success" ng-click="consultRequestCtrl.getOtherMeds('currentMeds');">
								<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
								Other Meds
							</button>
						</div>
						<textarea id="currentMeds"
								  cols="80"
								  rows="5"
								  class="form-control"
								  ng-model="consultRequestCtrl.consult.currentMeds"
								  placeholder="Use the button above to insert Other Meds data from the patients chart">
						</textarea>
					</div>

					<div class="form-group col-md-12"><!-- Alergies / Current Medications -->
						<label class="control-label">Allergies</label>
						<%--<div class="col-md-12">
							<button type="button" class="btn btn-sm btn-success" ng-click="getAllergies('allergies');">
								<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
								Allergies
							</button>
						</div>--%>
						<textarea id="allergies" rows="5" class="form-control" ng-model="consultRequestCtrl.consult.allergies"></textarea>
					</div>
				</form>
				<hr>
			</div><!-- Appointment End -->
			<%--<div class="col-md-12"><!-- Reason for Consultation -->
				<h4>Reason for Consultation:</h4>
				<div class="well">
					<textarea cols="120" rows="4" class="form-control" ng-model="consult.reasonForReferral"></textarea>
				</div>
			</div><!-- Reason End -->
			<div class="clear"></div>
			
			<div id="clinical-note" class="col-md-6"><!-- Clinic Notes -->
				<div>
					<h4>Pertinent clinical information:</h4>
					<p>
						<button type="button" class="btn btn-tall btn-success" ng-click="getFamilyHistory('clinicalInfo');">Family<br/>History</button>
						<button type="button" class="btn btn-tall btn-success" ng-click="getMedicalHistory('clinicalInfo');">Medical<br/>History</button>
						<button type="button" class="btn btn-tall btn-success" ng-click="getOngoingConcerns('clinicalInfo');">Ongoing<br/>Concerns</button>
						<button type="button" class="btn btn-tall btn-success" ng-click="getOtherMeds('clinicalInfo');">Other<br/>Meds</button>
						<button type="button" class="btn btn-tall btn-success" ng-click="getReminders('clinicalInfo');">Reminders</button>
					</p>					
					<div class="well">
						<div>
							<textarea id="clinicalInfo" cols="80" rows="5" class="form-control" placeholder="Use the buttons above to insert data from the patients chart"
								ng-model="consult.clinicalInfo"></textarea>
						</div>
					</div>
					<div class="clear"></div>
				</div>
			</div>
			
			<div id="concurrent-problem" class="col-md-6"><!-- Concurrent Problem -->
				<div>
					<h4>Significant Concurrent Problems:</h4>
					<p>
						<button type="button" class="btn btn-tall btn-success" ng-click="getFamilyHistory('concurrentProblems');">Family<br/>History</button>
						<button type="button" class="btn btn-tall btn-success" ng-click="getMedicalHistory('concurrentProblems');">Medical<br/>History</button>
						<button type="button" class="btn btn-tall btn-success" ng-click="getOngoingConcerns('concurrentProblems');">Ongoing<br/>Concerns</button>
						<button type="button" class="btn btn-tall btn-success" ng-click="getOtherMeds('concurrentProblems');">Other<br/>Meds</button>
						<button type="button" class="btn btn-tall btn-success" ng-click="getReminders('concurrentProblems');">Reminders</button>
					</p>						
					<div class="well">
						<div>
							<textarea id="concurrentProblems" cols="80" rows="5" class="form-control" placeholder="Use the buttons above to insert data from the patients chart"
								ng-model="consult.concurrentProblems"></textarea>
						</div>
					</div>
					<div class="clear"></div>
				</div>
			</div>
			<div class="clear"></div>
			
			<div class="col-md-6"><!-- Alergies / Current Medications -->
				<h4>Allergies:</h4>
				<div class="well">
					<textarea cols="80" rows="5" class="form-control" ng-model="consult.allergies"></textarea>
				</div>
			</div><!-- Alergies End -->	
			<div class="col-md-6">
				<h4>Current Medications: <button type="button" class="btn btn-success" style="padding:0px 10px;" ng-click="getOtherMeds('currentMeds');">Other Meds</button></h4>
				
				<div class="well">
					<textarea id="currentMeds" cols="80" rows="5" class="form-control" ng-model="consult.currentMeds" placeholder="Use the button above to insert Other Meds data from the patients chart"></textarea>
				</div>
			</div><!-- Current Medications End -->	
			<div class="clear"></div>--%>
		</div><!-- Right pane End -->
	</div>
	<div class="wrapper-action col-sm-12" ng-show="consultRequestCtrl.consultReadAccess"><!-- Action Buttons -->
		<button type="button" class="btn btn-large btn-warning action" 
				ng-click="consultRequestCtrl.printPreview()" 
				ng-show="consultRequestCtrl.consult.id!=null && consultRequestCtrl.consultChanged<=0">
			Print Preview
		</button>&nbsp;
		<button type="button" class="btn btn-large btn-warning action" 
				ng-click="consultRequestCtrl.sendFax()" 
				ng-show="consultRequestCtrl.consult.id!=null && consultRequestCtrl.consultChanged<=0">
			Send Fax
		</button>&nbsp;
		<button type="button" class="btn btn-large btn-warning action" 
				ng-click="consultRequestCtrl.eSend()" 
				ng-show="consultRequestCtrl.eSendEnabled && consult.id!=null && consultRequestCtrl.consultChanged<=0">
			Send Electronically
		</button>&nbsp;
		<button type="button" class="btn btn-large btn-success action" 
				ng-click="consultRequestCtrl.save()" 
				ng-show="consultRequestCtrl.consultChanged>0">
			Save
		</button>&nbsp;
		<button type="button" class="btn btn-large btn-default action" 
				ng-click="consultRequestCtrl.close()">
			Close
		</button>&nbsp;
	</div>

	<div ng-show="consultRequestCtrl.consultReadAccess != null && consultRequestCtrl.consultReadAccess == false"
		class="col-lg-12">
		<h3 class="text-danger">
			<span class="glyphicon glyphicon-warning-sign"></span>You don't have access to view consult
		</h3>
	</div>
</div>
