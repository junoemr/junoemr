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
<style>
	.clear {clear: both;}
	.inline {display: inline;}
	.btn-large {padding: 11px 19px; font-size: 17.5px;}
	.btn-tall {padding: 0px 10px; height: 42px;}
	.wrapper-action {
	    background-color: #FFFFFF;
	    border: 1px solid #FFFFFF;
	    bottom: 0;
	    opacity: 0.4;
	    padding-bottom: 4px;
	    padding-top: 4px;
	    position: fixed;
	    text-align: right;
	    width: 80%;
	    z-index: 999;
	}
	.wrapper-action:hover{
		background-color:#f5f5f5;
		border: 1px solid #E3E3E3;
		box-shadow: 0 1px 1px rgba(0, 0, 0, 0.05) inset;
		opacity:1;
		filter:alpha(opacity=100); /* For IE8 and earlier */
	}
	.attachment-modal-window .modal-dialog {
		width: 60%;
		min-width: 600px;
	}
</style>

<div class="col-md-12">
	<h2>Consultation Response</h2>
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
			<select class="form-control" ng-model="consultResponseCtrl.consult.status" ng-required="true" ng-options="status.value as status.name for status in consultResponseCtrl.statuses"/>
		</div>
	</div>
	<br/>
	<button type="button" class="btn btn-small btn-primary" ng-click="consultResponseCtrl.attachFiles()">Attachments</button>
	<ol style="padding-left:20px;">
		<li ng-repeat="attachment in consultResponseCtrl.consult.attachments">
			<a ng-click="consultResponseCtrl.openAttach(attachment)" title="{{consultResponseCtrl.attachment.displayName}}">{{attachment.shortName}}</a>
		</li>
	</ol>
</div><!-- Left pane End -->

<div id="right_pane" class="col-md-10" ng-show="consultResponseCtrl.consultReadAccess">
	<div class="col-md-6"><!-- Letterhead -->
		<h4>Letterhead:</h4>
		<div class="well">
			<div>
				<select id="letterhead" class="form-control" 
						ng-model="consult.letterheadName" 
						ng-options="letterhead.id as letterhead.name for letterhead in consultResponseCtrl.consult.letterheadList"
						ng-change="consultResponseCtrl.changeLetterhead()">
				</select>
			</div>
			<p class="letterheadDetails">
				<address>
					<label>Address:</label> <span style="white-space:nowrap">{{consult.letterheadAddress}}</span><br/>
					<label>Phone:</label> {{consult.letterheadPhone}} <br/>
					<label>Fax:</label>
					<select id="letterheadFax" class="form-control inline" style="width: auto;"
							ng-model="consultResponseCtrl.consult.letterheadFax"
							ng-options="fax.faxNumber as fax.faxUser for fax in consultResponseCtrl.consult.faxList">
					</select>
				</address>
			</p>
		</div>
	</div><!-- Letterhead End-->
	<div class="col-md-6"><!-- Specialist -->
		<h4>Referring Doctor:</h4>
		<div class="well">
			<div>
				<select id="refDocId" class="form-control"
						title="Referring Doctor"
						ng-model="consultResponseCtrl.consult.referringDoctor"
						ng-options="refDoc.name for refDoc in consultResponseCtrl.consult.referringDoctorList">
				</select>
			</div>
			<p class="referringDoctorDetails">
				<address>
					<label>Address:</label> {{consultResponseCtrl.consult.referringDoctor.streetAddress}}<br/>
					<label>Phone:</label> {{consultResponseCtrl.consult.referringDoctor.phoneNumber}} <br/>
					<label>Fax:</label> {{consultResponseCtrl.consult.referringDoctor.faxNumber}}<br />
				</address>
			</p>
		</div>
	</div><!-- Specialist End -->
	<div class="clear"></div>
	
	<div class="col-md-12">
		<div class="well">
			<div class="col-md-6">
				<div class="form-group">
					<label class="control-label">Response Date:</label>
					<input id="dp-responseDate" type="text" 
						class="form-control inline" 
						style="width:60%" 
						ng-model="consultResponseCtrl.consult.responseDate" 
						placeholder="Response Date" 
						datepicker-popup="yyyy-MM-dd" 
						datepicker-append-to-body="true" 
						is-open="consultResponseCtrl.page.respDatePicker" 
						ng-click="consultResponseCtrl.page.respDatePicker=true"
					/>
				</div>
			</div>
			<div class="col-md-6">
				<div class="form-group">
					<label class="control-label">Send To:</label>
					<select id="sendTo" class="form-control inline" 
						style="width:70%" 
						ng-model="consultResponseCtrl.consult.sendTo" 
						ng-required="true" 
						ng-options="sendTo for sendTo in consultResponseCtrl.consult.sendToList"
					/>
				</div>
			</div>
			<div class="clear"></div>
		</div>
	</div>
	<div class="col-md-12"><!-- Referral -->
		<div class="well">
			<div class="col-md-6">
				<div class="form-group">
					<label class="control-label">Referral Date:</label>
					<input id="dp-referralDate" type="text" 
						class="form-control inline" 
						style="width:60%" 
						ng-model="consultResponseCtrl.consult.referralDate" 
						placeholder="Referral Date" 
						datepicker-popup="yyyy-MM-dd" 
						datepicker-append-to-body="true" 
						is-open="consultResponseCtrl.page.refDatePicker" 
						ng-click="consultResponseCtrl.page.refDatePicker=true"
					/>
				</div>
				<div class="form-group">
					<label class="control-label">Urgency:</label>
					<select id="urgency" 
						class="form-control inline" 
						style="width:70%" 
						ng-model="consultResponseCtrl.consult.urgency" 
						ng-required="true" 
						ng-options="urgency.value as urgency.name for urgency in consultResponseCtrl.urgencies"/>
				</div>
			</div>
			<div class="col-md-6">
				<div class="form-group">
					<label class="control-label">Referrer Instructions:</label>
					<textarea cols="80" rows="2" class="form-control" readOnly>{{consultResponseCtrl.consult.referringDoctor.annotation}}</textarea>
				</div>
			</div>
			<div class="clear"></div>
			<div class="col-md-12"><!-- Reason for Consultation -->
				<label class="control-label">Reason for Consultation:</label>
				<textarea cols="120" rows="2" class="form-control" ng-model="consultResponseCtrl.consult.reasonForReferral"></textarea>
			</div><!-- Reason End -->
		</div>
	</div><!-- Referral End -->
	
	
	<div class="col-md-12"><!-- Appointment -->
		<div class="well" id="appointmentDetail">
			<div class="col-md-6">
				<div class="form-group">
					<label class="control-label">Appointment Date:</label>
					<input id="dp-appointmentDate" type="text" 
						class="form-control inline" 
						style="width:50%" 
						ng-model="consultResponseCtrl.consult.appointmentDate" 
						placeholder="Appointment Date"  
						datepicker-popup="yyyy-MM-dd" 
						datepicker-append-to-body="true" 
						is-open="consultResponseCtrl.page.aptDatePicker" 
						ng-click="consultResponseCtrl.page.aptDatePicker=true"/>
				</div>
				<div class="form-group">
					<label class="control-label">Appointment Time:</label>
					<span style="white-space:nowrap;">
						<select class="form-control inline" style="width:20%;" 
								ng-model="consultResponseCtrl.consult.appointmentHour"
								ng-options="hour for hour in consultResponseCtrl.hours"
								ng-change="consultResponseCtrl.changeAppointmentTime()">
						</select> :
						<select class="form-control inline" style="width:20%;" 
								ng-model="consultResponseCtrl.consult.appointmentMinute"
								ng-options="minute for minute in consultResponseCtrl.minutes"
								ng-change="consultResponseCtrl.changeAppointmentTime()">
						</select>
					</span>
				</div>
				<div class="form-group">
					<label class="control-label">Last Follow-up Date:</label>
					<input id="dp-followUpDate" type="text" 
						class="form-control inline" 
						style="width:50%" 
						ng-model="consultResponseCtrl.consult.followUpDate" 
						placeholder="Follow Up Date"  
						datepicker-popup="yyyy-MM-dd" 
						datepicker-append-to-body="true" 
						is-open="consultResponseCtrl.page.lfdDatePicker" 
						ng-click="consultResponseCtrl.page.lfdDatePicker=true"/>
				</div>
			</div>
			<div class="col-md-6">
				<label class="control-label">Appointment Notes:</label>
				<div class="form-group">
					<textarea cols="80" rows="4" 
						class="form-control" 
						ng-model="consultResponseCtrl.consult.appointmentNote"></textarea>
				</div>
			</div>
			<div class="clear"></div>
		</div>
	</div><!-- Appointment End -->
	
	<div class="col-md-12"><!-- Consultation Response -->
		<h4>Examination:</h4>
		<div class="well">
			<textarea cols="120" rows="3" class="form-control" ng-model="consultResponseCtrl.consult.examination"></textarea>
		</div>
		<h4>Impression:</h4>
		<div class="well">
			<textarea cols="120" rows="3" class="form-control" ng-model="consultResponseCtrl.consult.impression"></textarea>
		</div>
		<h4>Plan:</h4>
		<div class="well">
			<textarea cols="120" rows="3" class="form-control" ng-model="consultResponseCtrl.consult.plan"></textarea>
		</div>
	</div><!-- Response End -->
	<div class="clear"></div>
	
	<div id="clinical-note" class="col-md-6"><!-- Clinic Notes -->
		<div>
			<h4>Pertinent clinical information:</h4>
			<p>
				<button type="button" class="btn btn-tall btn-success" ng-click="consultResponseCtrl.getFamilyHistory('clinicalInfo');">Family<br/>History</button>
				<button type="button" class="btn btn-tall btn-success" ng-click="consultResponseCtrl.getMedicalHistory('clinicalInfo');">Medical<br/>History</button>
				<button type="button" class="btn btn-tall btn-success" ng-click="consultResponseCtrl.getOngoingConcerns('clinicalInfo');">Ongoing<br/>Concerns</button>
				<button type="button" class="btn btn-tall btn-success" ng-click="consultResponseCtrl.getOtherMeds('clinicalInfo');">Other<br/>Meds</button>
				<button type="button" class="btn btn-tall btn-success" ng-click="consultResponseCtrl.getReminders('clinicalInfo');">Reminders</button>
			</p>					
			<div class="well">
				<div>
					<textarea id="clinicalInfo" cols="80" rows="4" class="form-control" 
						placeholder="Use the buttons above to insert data from the patients chart"
						ng-model="consultResponseCtrl.consult.clinicalInfo"></textarea>
				</div>
			</div>
			<div class="clear"></div>
		</div>
	</div>
	
	<div id="concurrent-problem" class="col-md-6"><!-- Concurrent Problem -->
		<div>
			<h4>Significant Concurrent Problems:</h4>
			<p>
				<button type="button" class="btn btn-tall btn-success" ng-click="consultResponseCtrl.getFamilyHistory('concurrentProblems');">Family<br/>History</button>
				<button type="button" class="btn btn-tall btn-success" ng-click="consultResponseCtrl.getMedicalHistory('concurrentProblems');">Medical<br/>History</button>
				<button type="button" class="btn btn-tall btn-success" ng-click="consultResponseCtrl.getOngoingConcerns('concurrentProblems');">Ongoing<br/>Concerns</button>
				<button type="button" class="btn btn-tall btn-success" ng-click="consultResponseCtrl.getOtherMeds('concurrentProblems');">Other<br/>Meds</button>
				<button type="button" class="btn btn-tall btn-success" ng-click="consultResponseCtrl.getReminders('concurrentProblems');">Reminders</button>
			</p>						
			<div class="well">
				<div>
					<textarea id="concurrentProblems" cols="80" 
						rows="4" class="form-control" 
						placeholder="Use the buttons above to insert data from the patients chart"
						ng-model="consultResponseCtrl.consult.concurrentProblems"></textarea>
				</div>
			</div>
			<div class="clear"></div>
		</div>
	</div>
	<div class="clear"></div>
	
	<div class="col-md-6"><!-- Alergies / Current Medications -->
		<h4>Allergies:</h4>
		<div class="well">
			<textarea cols="80" rows="4" class="form-control" ng-model="consultResponseCtrl.consult.allergies"></textarea>
		</div>
	</div><!-- Alergies End -->	
	<div class="col-md-6">
		<h4>Current Medications: 
			<button type="button" class="btn btn-success" style="padding:0px 10px;" ng-click="consultResponseCtrl.getOtherMeds('currentMeds');">
				Other Meds
			</button>
		</h4>
		
		<div class="well">
			<textarea id="currentMeds" cols="80" 
				rows="4" 
				class="form-control" 
				ng-model="consultResponseCtrl.consult.currentMeds" 
				placeholder="Use the button above to insert Other Meds data from the patients chart">
			</textarea>
		</div>
	</div><!-- Current Medications End -->	
	<div class="clear"></div>
</div><!-- Right pane End -->

<div class="wrapper-action" ng-show="consultReadAccess"><!-- Action Buttons -->
	<button type="button" class="btn btn-large btn-warning action" 
			ng-click="consultResponseCtrl.printPreview()" 
			ng-show="consultResponseCtrl.consult.id!=null && consultChanged<=0">
		Print Preview
	</button>&nbsp;
	<button type="button" class="btn btn-large btn-warning action" 
			ng-click="consultResponseCtrl.sendFax()" 
			ng-show="consultResponseCtrl.consult.id!=null && consultChanged<=0">
		Send Fax
	</button>&nbsp;
	<button type="button" class="btn btn-large btn-primary action" 
			ng-click="consultResponseCtrl.save()" 
			ng-show="consultResponseCtrl.consultChanged>0">
		Save
	</button>&nbsp;
	<button type="button" class="btn btn-large btn-default action" 
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
