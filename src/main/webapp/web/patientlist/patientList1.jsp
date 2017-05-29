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
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<div class="list-patients">
	<div class="list-group-item list-group-item-head">
		<table id="listDate">
			<thead>
				<tr>
					<th>
						<a href="javascript:void(0)" ng-click="manageConfiguration()">
							<span class="glyphicon glyphicon-cog"></span>
						</a>
					</th>
					<th class="pull-right">
						<h6>
							<a href="javascript:void(0)" ng-click="switchDay(-1)">
								<span class="glyphicon glyphicon-chevron-left"></span>
							</a>
						</h6>
					</th>
					<th class="list-patient-date-cell">
						<%--<span class="glyphicon glyphicon-calendar"></span>--%>
						<a href="#"
							 ng-model="$parent.appointmentDate"
							 ng-change="changeApptDate()"
							 ng-click="appointmentDatePicker = true"
							 uib-datepicker-popup="yyyy-MM-dd"
							 datepicker-append-to-body="true"
							 is-open="appointmentDatePicker"
							 datepicker-options="dateOptions"
							 show-button-bar="false"
							 popup-placement="bottom"
							 bundle="ui"
							 title="{{appointmentDate | date:'yyyy-MM-dd' }}"
						>{{appointmentDate | date:'yyyy-MM-dd' }}</a>
					</th>
					<th class="pull-left">
						<h6>
							<a href="javascript:void(0)" ng-click="switchDay(+1)">
								<span class="glyphicon glyphicon-chevron-right"></span>
							</a>
						</h6>
					</th>
					<th style="text-align:right">
						<a href="javascript:void(0)" ng-click="refresh()">
							<span class="glyphicon glyphicon-refresh" title="<bean:message key="patientList.refresh" bundle="ui"/>"></span>
						</a>
					</th>
				</tr>
			</thead>
		</table>
	</div>

	<div id="patient-list">
		<a ng-repeat="patient in patients | offset:currentPage*pageSize | limitTo:pageSize | filter:query" class="list-group-item default hand-hover"
			ng-click="goToRecord(patient)" ng-dblclick="viewAppointment(patient.appointmentNo)">
			<!--
			<span ng-if="patient.status.length>0 && patient.status != 't'" class="badge">{{patient.status}}</span>
			-->
			<h5 class="list-group-item-heading pull-right patient-time" >{{patient.startTime}}</h5>
			<h4 class="list-group-item-heading patient-name" >{{patient.name}}</h5>

			<p class="list-group-item-text" ng-if="patient.demographicNo != 0" ng-show="patientListConfig.showReason" ><bean:message key="provider.appointmentProviderAdminDay.Reason"/>: {{patient.reason}}  </p>
		</a>
	</div>


</div>
