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
<div class="row">
	<div class="col-md-8 col-sm-12 md-margin-top" >
		<div class="form-group col-sm-6">
			<label>Start Hour (0-23):</label>
			<input ng-model="$ctrl.pref.startHour" placeholder="Start Hour" class="form-control" type="text">
		</div>
		<div class="form-group col-sm-6">
			<label>End Hour (0-23):</label>
			<input ng-model="$ctrl.pref.endHour" placeholder="End Hour" class="form-control" type="text">
		</div>
		<div class="form-group col-sm-6">
			<label>Period:</label>
			<input ng-model="$ctrl.pref.period" placeholder="Period" class="form-control" type="text">
		</div>
		<ca-field-select
				class="col-sm-6"
				ca-name="settings-schedule-groupNo"
				ca-title="Group No"
				ca-template="label"
				ca-model="$ctrl.pref.groupNo"
				ca-options="$ctrl.scheduleOptions"
		>
		</ca-field-select>
		<ca-field-select
				class="col-sm-6"
		<%--ca-hide="!eventController.sitesEnabled"--%>
				ca-name="settings-schedule-site"
				ca-title="Site"
				ca-template="label"
				ca-model="$ctrl.pref.siteSelected"
				ca-options="$ctrl.siteOptions"
				ca-empty-option="true"
		>
		</ca-field-select>
		<ca-field-select
				class="col-sm-6"
				ca-name="settings-schedule-reasonOpts"
				ca-title="Display Appointment Reason"
				ca-template="label"
				ca-model="$ctrl.pref.appointmentReasonDisplayLevel"
				ca-options="$ctrl.appointmentReasonOptions"
		>
		</ca-field-select>

		<div class="form-group col-sm-6">
			<label>Length of patient name to display on appointment screen:</label>
			<input ng-model="$ctrl.pref.patientNameLength" placeholder="Length" class="form-control" type="text">
		</div>
		<div class="form-group col-sm-6">
			<label>Length of link and form names to display on appointment screen (> 0):</label>
			<input ng-model="$ctrl.pref.appointmentScreenLinkNameDisplayLength" placeholder="Length" class="form-control" type="text">
		</div>
		<div class="form-group col-sm-6">
			<label>Use classic eChart</label>
			<div class="controls">
				<label class="checkbox-inline" for="radioh-0">
					<input ng-model="$ctrl.pref.hideOldEchartLinkInAppointment" ng-value="false" id="radioh-0" type="radio">
					Enable
				</label>
				<label class="checkbox-inline" for="radioh-1">
					<input ng-model="$ctrl.pref.hideOldEchartLinkInAppointment" ng-value="true" id="radioh-1" type="radio">
					Disable
				</label>
			</div>
		</div>
		<div class="form-group col-sm-6">
			<label>Enable Intake Form</label>
			<div class="controls">
				<label class="checkbox-inline" for="radio-intake-0">
					<input ng-model="$ctrl.pref.intakeFormEnabled" ng-value="true" id="radio-intake-0" type="radio">
					Enable
				</label>
				<label class="checkbox-inline" for="radio-intake-1">
					<input ng-model="$ctrl.pref.intakeFormEnabled" ng-value="false" id="radio-intake-1" type="radio">
					Disable
				</label>
			</div>
		</div>

		<div class="form-group col-sm-6">
			<label>Encounter Forms to display on appointment screen</label>
			<div style="height:10em;border:solid grey 1px;overflow:auto;white-space:nowrap">
				<span ng-repeat="f in $ctrl.encounterForms">
					<input type="checkbox" ng-model="f.checked" ng-change="$ctrl.selectEncounterForms()">{{f.formName}}<br/>
				</span>
			</div>
		</div>
		<div class="form-group col-sm-6">
			<label>Eforms to display on appointment screen</label>
			<div style="height:10em;border:solid grey 1px;overflow:auto;white-space:nowrap">
				<span ng-repeat="f in $ctrl.eforms">
					<input type="checkbox" ng-model="f.checked" ng-change="$ctrl.selectEForms()">{{f.formName}}<br/></span>
			</div>
		</div>
		<div class="form-group col-sm-6">
			<label>Quick links to display on appointment screen</label>
			<div style="height:10em;border:solid grey 1px;overflow:auto;white-space:nowrap">
				<span ng-repeat="q in $ctrl.pref.appointmentScreenQuickLinks">
					<input type="checkbox" ng-model="$ctrl.q.checked">{{q.name}}<br/></span>
				<button class="btn-sm" ng-click="removeQuickLinks()">Remove</button>
				<button class="btn-sm" ng-click="openQuickLinkModal()">Add</button>
			</div>
		</div>
		<div class="form-group col-sm-6">
			<label>Specify which appointment types are included by the appointment counter:</label>
			<div class="controls">
				<div class="form-group">
					<ca-field-boolean
							ca-name="checkApptCountEnabled"
							ca-title="Enable Counter"
							ca-template="juno"
							ca-model="$ctrl.pref.appointmentCountEnabled"
							ca-value="false">
				</div>
				<div  ng-if="$ctrl.pref.appointmentCountEnabled">
					<div class="form-group">
						<ca-field-boolean
								ca-name="checkApptCountCanceled"
								ca-title="Include cancelled appointments"
								ca-template="juno"
								ca-model="$ctrl.pref.appointmentCountIncludeCancelled"
								ca-value="false">
					</div>
					<div class="form-group">
						<ca-field-boolean
								ca-name="checkApptCountNoShow"
								ca-title="Include no-show appointments"
								ca-template="juno"
								ca-model="$ctrl.pref.appointmentCountIncludeNoShow"
								ca-value="false">
					</div>
					<div class="form-group">
						<ca-field-boolean
								ca-name="checkApptCountNoDemographic"
								ca-title="Include appointments not associated with a patient"
								ca-template="juno"
								ca-model="$ctrl.pref.appointmentCountIncludeNoDemographic"
								ca-value="false">
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
