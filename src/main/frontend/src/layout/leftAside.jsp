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

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<!-- Start patient List template -->
<div id="patient-list-template"
	 class="flex-column"
	 ng-controller="PatientList.PatientListController as patientListCtrl">

	<div class="form-group btn-aside-container">
		<button class="btn btn-icon btn-aside"
				ng-click="bodyCtrl.toggleShowPatientList()">
			<span class="icon icon-aside"></span>
		</button>
	</div>

	<div class="patient-list"
		ng-if="bodyCtrl.showPatientList">
		<!-- patient list header -->
		<div class="flex-row align-items-center patient-list-header">
			<div class="col-sm-12">
				<input type="text" class="form-control"
				placeholder="<bean:message key="patientList.search" bundle="ui"/>"
				ng-model="query"/>
			</div>
		</div>

		<!-- main content pane -->
		<div class="patient-list-content flex-column">
			<ul class="nav nav-tabs" id="patient-list-nav">
				<li ng-class="{'active' : patientListCtrl.activeTab === patientListCtrl.tabEnum.appointments}">
					<a class="round-top" data-toggle="tab"
					   ng-click="patientListCtrl.changeTab(patientListCtrl.tabEnum.appointments);">Appointments
					</a>
				</li>
				<li ng-class="{'active' : patientListCtrl.activeTab === patientListCtrl.tabEnum.recent}">
					<a class="round-top" data-toggle="tab"
					   ng-click="patientListCtrl.changeTab(patientListCtrl.tabEnum.recent);">Patients
					</a>
				</li>
			</ul>

			<!-- tab contents -->
			<div class="content-controller flex-row align-items-center">
				<div class="form-inline flex-row flex-grow col-md-12">
					<div class="form-group">
						<button class="btn btn-icon visible"
								ng-disabled="patientListCtrl.isRecentPatientView()"
								ng-click="patientListCtrl.stepBack()">
							<span class="icon icon-left"></span>
						</button>
					</div>
					<div class="form-group">
						<button class="btn btn-icon visible"
								ng-disabled="patientListCtrl.isRecentPatientView()"
								ng-click="patientListCtrl.stepForward()">
							<span class="icon icon-right"></span>
						</button>
					</div>
					<div class="form-group">
						<ca-field-date
							ca-template="bare"
							ca-date-picker-id="patient-list-select-date"
							ca-name="Date"
							ca-model="patientListCtrl.datepickerSelectedDate"
							ca-orientation="auto"
							ca-disabled="patientListCtrl.isRecentPatientView()"
						></ca-field-date>
					</div>
					<div class="form-group">
						<button class="btn btn-icon"
								ng-click="patientListCtrl.refresh()">
							<span class="icon icon-refresh"></span>
						</button>
					</div>
				</div>
			</div>

			<!-- the displayed list of patients -->
			<div class="content-display flex-grow overflow-scroll">
				<div class="list-group">
					<a ng-repeat="patient in patientListCtrl.activePatientList | filter:query"
						class="list-group-item">

						<div ng-if="patientListCtrl.isAppointmentPatientView()"
								class="flex-row vertical-align justify-content-between">
							<div class="col-md-6 list-group-clickable"
								ng-click="patientListCtrl.goToRecord(patient)">
								<h6>{{patient.name}}</h6>
								<span>{{patient.startTime}} {{patient.reason}}</span>
							</div>
							<div class="col-md-6">
								<juno-appointment-status-select
								ca-name="aside-appt-status-{{patient.appointmentNo}}"
								ca-no-label="true"
								ca-model="patient.status"
								ca-options="patientListCtrl.eventStatusOptions"
								ca-change="patientListCtrl.updateAppointmentStatus(patient)"
								>
								</juno-appointment-status-select>
							</div>
						</div>
						<div ng-if="patientListCtrl.isRecentPatientView()"
						     ng-click="patientListCtrl.goToRecord(patient)"
						     class="list-group-clickable">
							<h6>{{patient.name}}</h6>
						</div>
					</a>
				</div>
			</div>
		</div>
	</div>
</div>