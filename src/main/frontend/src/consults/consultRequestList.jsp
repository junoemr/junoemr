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
<style>
.modal.fade {
	opacity: 1;
}

.modal.fade .modal-dialog, .modal.in .modal-dialog {
	-webkit-transform: translate(0, 0);
	-ms-transform: translate(0, 0);
	transform: translate(0, 0);
}

.state1 {}
.state2 {background-color: #e6e6e6 !important;} /*#f5f5f5*/
.state3 {background-color: #d9d9d9 !important;} /*#e6e6e6*/
.state6 {background-color: #cccccc !important;} /*cccccc*/

</style>

<div class="consult-page-header"
     ng-if="consultRequestListCtrl.hideSearchPatient != true">
	<!-- TODO-legacy -->
</div>
<juno-security-check show-placeholder="true" permissions="consultRequestListCtrl.SecurityPermissions.ConsultationRead">
	<div class="col-lg-12 consult-page">
		<form name="searchForm" id="search-form">

			<div class="row search-filters consultation-search-filter">
				<div class="col-lg-3 col-xs-6">
					<juno-datepicker-popup juno-model="consultRequestListCtrl.search.referralStartDate"
						placeholder="<bean:message key="consult.list.referralStartDate" bundle="ui"/>"
						show-icon="true"
						type="Input">
					</juno-datepicker-popup>
				</div>
				<div class="col-lg-3 col-xs-6">
					<juno-datepicker-popup juno-model="consultRequestListCtrl.search.referralEndDate"
						placeholder="<bean:message key="consult.list.referralEndDate" bundle="ui"/>"
						show-icon="true"
						type="Input">
					</juno-datepicker-popup>
				</div>
				<div class="col-lg-3 col-xs-6">
					<select class="form-control" ng-model="consultRequestListCtrl.search.status"
							name="status" id="status"
							ng-options="status.value as status.name for status in consultRequestListCtrl.statuses">
						<option value=""><bean:message key="consult.list.status.all" bundle="ui"/></option>
					</select>
				</div>
				<div class="col-lg-3 col-xs-6">
					<select ng-model="consultRequestListCtrl.search.team" name="team" id="team"
						class="form-control" ng-options="t for t in consultRequestListCtrl.teams">
					</select>
				</div>
				<div class="col-lg-3 col-xs-6">
					<juno-datepicker-popup juno-model="consultRequestListCtrl.search.appointmentStartDate"
						placeholder="<bean:message key="consult.list.appointmentStartDate" bundle="ui"/>"
						show-icon="true"
						type="Input">
					</juno-datepicker-popup>
				</div>
				<div class="col-lg-3 col-xs-6">
					<juno-datepicker-popup juno-model="consultRequestListCtrl.search.appointmentEndDate"
						placeholder="<bean:message key="consult.list.appointmentEndDate" bundle="ui"/>"
						show-icon="true"
						type="Input">
					</juno-datepicker-popup>
				</div>

				<div class="col-lg-3 col-xs-6" ng-hide="consultRequestListCtrl.hideSearchPatient">
					<div class="input-group">

						<input type="text"
						       ng-disabled="!consultRequestListCtrl.canAccessDemographics()"
							   ng-model="consultRequestListCtrl.demographicName"
							   placeholder="<bean:message key="consult.list.patient" bundle="ui"/>"
							   uib-typeahead="pt.demographicNo as pt.name for pt in consultRequestListCtrl.searchPatients($viewValue)"
							   typeahead-on-select="consultRequestListCtrl.updateDemographicNo($item, $model, $label)"
							   class="form-control"
						/>
						<span class="input-group-btn">
							<button class="btn btn-default"
							        ng-disabled="!consultRequestListCtrl.canAccessDemographics()"
							        ng-click="consultRequestListCtrl.removeDemographicAssignment()">
								<span class="glyphicon glyphicon-remove">
								</span>
							</button>
						</span>
					</div>
				</div>

				<div class="col-lg-3 col-xs-6">
					<div class="input-group">
						<input type="text" ng-model="consultRequestListCtrl.consult.mrpName" placeholder="<bean:message key="consult.list.mrp" bundle="ui"/>"
							uib-typeahead="pvd as pvd.name for pvd in consultRequestListCtrl.searchMrps($viewValue)"
							typeahead-on-select="consultRequestListCtrl.updateMrpNo($model)"
							class="form-control"
						/>
						<span class="input-group-btn">
							<button class="btn btn-default" ng-click="consultRequestListCtrl.removeMrpAssignment()">
								<span class="glyphicon glyphicon-remove">
								</span>
							</button>
						</span>
					</div>
				</div>
			</div>

			<div class="row search-buttons">
				<div class="col-xs-12">
					<button class="btn btn-primary" type="button" ng-click="consultRequestListCtrl.doSearch()">
						<bean:message key="global.search" bundle="ui"/>
					</button>
					<button class="btn btn-default" type="button" ng-click="consultRequestListCtrl.clear()">
						<bean:message key="global.clear" bundle="ui"/>
					</button>
					<button class="btn btn-success" type="button" ng-click="consultRequestListCtrl.addConsult()"
							ng-disabled="!consultRequestListCtrl.canCreateConsults() || consultRequestListCtrl.search.demographicNo==null"
							title="<bean:message key="consult.list.newRemindFill" bundle="ui"/>">
						<bean:message key="consult.list.new" bundle="ui"/>
					</button>
					<button class="btn btn-default" type="button"
					        ng-disabled="!consultRequestListCtrl.canAccessConsultConfig()"
						ng-click="consultRequestListCtrl.popup(700,960,'<%=request.getContextPath()%>/oscarEncounter/oscarConsultationRequest/config/ShowAllServices.jsp','<bean:message key="oscarEncounter.oscarConsultationRequest.ViewConsultationRequests.msgConsConfig"/>')">
						<bean:message key="oscarEncounter.oscarConsultationRequest.ViewConsultationRequests.msgEditSpecialists"/>
					</button>
				</div>
			</div>
		</form>

		<table ng-table="consultRequestListCtrl.tableParams" show-filter="false" class="table table-striped table-bordered">
			<tbody>
				<tr ng-repeat="consult in $data">
					<td>
						<button ng-disabled="!consultRequestListCtrl.canEditConsults()"
								ng-click="consultRequestListCtrl.editConsult(consult)"
						        class="btn btn-xs btn-primary noprint">
							<bean:message key="global.edit" bundle="ui"/>
						</button>
					</td>
					<td data-title="'<bean:message key="consult.list.header.patient" bundle="ui"/>'" sortable="'Demographic'">
						{{consult.demographic.formattedName}}</td>
					<td data-title="'<bean:message key="consult.list.header.service" bundle="ui"/>'" sortable="'Service'">{{consult.serviceName}}</td>
					<td data-title="'<bean:message key="consult.list.header.consultant" bundle="ui"/>'" sortable="'Consultant'">{{consult.consultant.formattedName}}</td>
					<td data-title="'<bean:message key="consult.list.header.team" bundle="ui"/>'" sortable="'Team'">{{consult.teamName}}</td>
					<td data-title="'<bean:message key="consult.list.header.status" bundle="ui"/>'" sortable="'Status'">{{consult.statusDescription}}</td>
					<td data-title="'<bean:message key="consult.list.header.priority" bundle="ui"/>'" class="{{consult.urgencyColor}}" sortable="'Urgency'">{{consult.urgencyDescription}}</td>
					<td data-title="'<bean:message key="consult.list.header.mrp" bundle="ui"/>'" sortable="'MRP'">{{consult.mrp.formattedName}}</td>

					<td data-title="'<bean:message key="consult.list.header.appointmentDate" bundle="ui"/>'" sortable="'AppointmentDate'">
						{{consult.appointmentDate | date: 'yyyy-MM-dd HH:mm'}}</td>
					<td data-title="'<bean:message key="consult.list.header.lastFollowUp" bundle="ui"/>'" sortable="'FollowUpDate'">
						{{consult.lastFollowUp | date: 'yyyy-MM-dd'}}</td>
					<td data-title="'<bean:message key="consult.list.header.referralDate" bundle="ui"/>'" sortable="'ReferralDate'">
						{{consult.referralDate | date: 'yyyy-MM-dd'}} <strong class="text-danger" ng-show="consult.outstanding" title="<bean:message key="consult.list.outstanding" bundle="ui"/>">!</strong></td>
				</tr>
			</tbody>

			<tfoot>
				<!--<tr>
					<td colspan="12" class="white">
						<a ng-click="checkAll()" class="hand-hover"><bean:message key="consult.list.checkAll" bundle="ui"/></a> - <a ng-click="checkNone()" class="hand-hover"><bean:message key="consult.list.checkNone" bundle="ui"/></a>
					</td>
				</tr>-->
			</tfoot>
		</table>

	</div>
</juno-security-check>

