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

<!-- this CSS makes it so the modals don't have the vertical sliding animation. Not sure if I will keep this or how I will use this yet -->

<div class="consult-page-header"
ng-if="consultResponseListCtrl.hideSearchPatient != true">
	<!-- TODO-legacy -->
</div>
<juno-security-check show-placeholder="true" permissions="consultResponseListCtrl.SecurityPermissions.ConsultationRead">
	<div class="col-lg-12">
		<form name="searchForm" id="search-form">

			<div class="row search-filters consultation-search-filter">
				<div class="col-lg-3 col-xs-6">
					<label>Referral Start Date</label>
					<juno-datepicker-popup
							juno-model="consultResponseListCtrl.search.referralStartDate"
							placeholder="<bean:message key="consult.list.referralStartDate" bundle="ui"/>"
							show-icon="true"
							type="Input">
					</juno-datepicker-popup>
				</div>
				<div class="col-lg-3 col-xs-6">
					<label>Referral End Date</label>
					<juno-datepicker-popup
							juno-model="consultResponseListCtrl.search.referralEndDate"
							placeholder="<bean:message key="consult.list.referralEndDate" bundle="ui"/>"
							show-icon="true"
							type="Input">
					</juno-datepicker-popup>
				</div>
				<div class="col-lg-3 col-xs-6">
					<label>Status</label>
					<select class="form-control"
							ng-model="consultResponseListCtrl.search.status"
							name="status"
							id="status"
							ng-options="status.value as status.name for status in consultResponseListCtrl.statuses">
						<option value="">
							<bean:message key="consult.list.status.all" bundle="ui"/>
						</option>
					</select>
				</div>
				<div class="col-lg-3 col-xs-6">
					<label>Team</label>
					<select ng-model="consultResponseListCtrl.search.team"
							name="team"
							id="team"
							class="form-control"
							<%--<!--ng-init="search.team='<bean:message key="consult.list.team.all" bundle="ui"/>'"-->--%>
							ng-options="t for t in consultResponseListCtrl.teams">
						<%--<option value="">--%>
							<%--<bean:message key="consult.list.team.all" bundle="ui"/>--%>
						<%--</option>--%>
					</select>
				</div>

				<div class="col-lg-3 col-xs-6">
					<label>Appointment Start Date</label>
					<juno-datepicker-popup
							juno-model="consultResponseListCtrl.search.appointmentStartDate"
							placeholder="<bean:message key="consult.list.appointmentStartDate" bundle="ui"/>"
							show-icon="true"
							type="Input">
					</juno-datepicker-popup>
				</div>
				<div class="col-lg-3 col-xs-6">
					<label>Appointment End Date</label>
					<juno-datepicker-popup
							juno-model="consultResponseListCtrl.search.appointmentEndDate"
							placeholder="<bean:message key="consult.list.appointmentEndDate" bundle="ui"/>"
							show-icon="true"
							type="Input">
					</juno-datepicker-popup>
				</div>

				<div class="col-lg-3 col-xs-6" ng-hide="consultResponseListCtrl.onRecordPage">
					<label>Patient</label>
					<div class="input-group">
						<input type="text"
						       ng-disabled="!consultResponseListCtrl.canAccessDemographics()"
							   ng-model="consultResponseListCtrl.demographicName"
							   placeholder="<bean:message key="consult.list.patient" bundle="ui"/>"
							   uib-typeahead="pt.demographicNo as pt.name for pt in consultResponseListCtrl.searchPatients($viewValue)"
							   typeahead-on-select="consultResponseListCtrl.updateDemographicNo($item, $model, $label)"
							   class="form-control"/>
						<span class="input-group-btn">
							<button class="btn btn-default"
							        ng-disabled="!consultResponseListCtrl.canAccessDemographics()"
									ng-click="consultResponseListCtrl.removeDemographicAssignment()">
								<span class="glyphicon glyphicon-remove"></span>
							</button>
						</span>
					</div>
				</div>

				<div class="col-lg-3 col-xs-6">
					<label>MRP</label>
					<div class="input-group">
						<input type="text"
							   ng-model="consultResponseListCtrl.consult.mrpName"
							   placeholder="<bean:message key="consult.list.mrp" bundle="ui"/>"
							   uib-typeahead="pvd as pvd.name for pvd in consultResponseListCtrl.searchMrps($viewValue)"
							   typeahead-on-select="consultResponseListCtrl.updateMrpNo($model)"
							   class="form-control"/>
						<span class="input-group-btn">
							<button class="btn btn-default"
									ng-click="consultResponseListCtrl.removeMrpAssignment()">
								<span class="glyphicon glyphicon-remove"></span>
							</button>
						</span>
					</div>
				</div>
			</div>

			<div class="row search-buttons">
				<div class="col-xs-12">
					<button class="btn btn-primary" type="button" ng-click="consultResponseListCtrl.doSearch()">
						<bean:message key="global.search" bundle="ui"/>
					</button>
					<button class="btn btn-default" type="button" ng-click="consultResponseListCtrl.clear()">
						<bean:message key="global.clear" bundle="ui"/>
					</button>
					<button class="btn btn-success" type="button"
							ng-click="consultResponseListCtrl.addConsult()"
							ng-disabled="!consultResponseListCtrl.canCreateConsults() || consultResponseListCtrl.demographicNo==null"
							title="<bean:message key="consult.list.newRemindFill" bundle="ui"/>">
						<bean:message key="consult.list.newResponse" bundle="ui"/>
					</button>
				</div>
			</div>
		</form>

		<table ng-table="consultResponseListCtrl.tableParams"
			   show-filter="false"
			   class="table table-striped table-bordered">
			<tbody>
				<tr ng-repeat="consult in $data">
					<td>
						<button ng-disabled="!consultResponseListCtrl.canEditConsults()"
						        ng-click="consultResponseListCtrl.editConsult(consult)"
						        class="btn btn-xs btn-primary hand-hover">
							<bean:message key="global.edit" bundle="ui"/>
						</button>
					</td>
					<td data-title="'<bean:message key="consult.list.header.patient" bundle="ui"/>'" sortable="'Demographic'">
						{{consult.demographic.formattedName}}</td>
					<td data-title="'<bean:message key="consult.list.header.referringDoctor" bundle="ui"/>'" sortable="'ReferringDoctor'">{{consult.referringDoctor.formattedName}}</td>
					<td data-title="'<bean:message key="consult.list.header.team" bundle="ui"/>'" sortable="'Team'">{{consult.teamName}}</td>
					<td data-title="'<bean:message key="consult.list.header.status" bundle="ui"/>'" sortable="'Status'">{{consult.statusDescription}}</td>
					<td data-title="'<bean:message key="consult.list.header.priority" bundle="ui"/>'" class="{{consult.urgencyColor}}" sortable="'Urgency'">{{consult.urgencyDescription}}</td>
					<td data-title="'<bean:message key="consult.list.header.mrp" bundle="ui"/>'" sortable="'Provider'">{{consult.provider.formattedName}}</td>

					<td data-title="'<bean:message key="consult.list.header.appointmentDate" bundle="ui"/>'" sortable="'AppointmentDate'">
						{{consult.appointmentDate | date: 'yyyy-MM-dd HH:mm'}}</td>
					<td data-title="'<bean:message key="consult.list.header.lastFollowUp" bundle="ui"/>'" sortable="'FollowUpDate'">
						{{consult.lastFollowUp | date: 'yyyy-MM-dd'}}</td>
					<td data-title="'<bean:message key="consult.list.header.referralDate" bundle="ui"/>'" sortable="'ReferralDate'">
						{{consult.referralDate | date: 'yyyy-MM-dd'}}</td>
					<td data-title="'<bean:message key="consult.list.header.responseDate" bundle="ui"/>'" sortable="'ResponseDate'">
						{{consult.responseDate | date: 'yyyy-MM-dd'}}</td>
				</tr>
			</tbody>

			<tfoot>
				<!-- <tr>
					<td colspan="12" class="white">
						<a ng-click="checkAll()" class="hand-hover"><bean:message key="consult.list.checkAll" bundle="ui"/></a> - <a ng-click="checkNone()" class="hand-hover"><bean:message key="consult.list.checkNone" bundle="ui"/></a>
					</td>
				</tr> -->
			</tfoot>

		</table>
	</div>
</juno-security-check>
