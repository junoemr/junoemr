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

<div class="tickler-page-header"
     ng-if="!ticklerListCtrl.inDemographicView()">
	<!-- TODO-legacy -->
</div>
<juno-security-check show-placeholder="true" permissions="ticklerListCtrl.SecurityPermissions.TicklerRead">
	<div class="col-xs-12" id="tickler-list-page">
		<form name="searchForm" id="search-form" class="no-print">
			<div class="row search-filters">
				<div class="col-lg-2 col-sm-4 col-xs-6">
					<label>Service Start Date</label>
					<input ng-model="ticklerListCtrl.search.serviceStartDate" type="text"
						id="serviceStartDate"
						name="serviceStartDate"
						class="form-control"
						uib-datepicker-popup="yyyy-MM-dd"
						datepicker-append-to-body="true"
						is-open="data.isOpen"
						ng-click="data.isOpen = true"
						placeholder="<bean:message key="tickler.list.serviceStartDate" bundle="ui"/>"
					>
				</div>
				<div class="col-lg-2 col-sm-4 col-xs-6">
					<label>Service End Date</label>
					<input ng-model="ticklerListCtrl.search.serviceEndDate" type="text"
						id="serviceEndDate"
						name="serviceEndDate"
						class="form-control"
						uib-datepicker-popup="yyyy-MM-dd"
						datepicker-append-to-body="true"
						is-open="data2.isOpen"
						ng-click="data2.isOpen = true"
						placeholder="<bean:message key="tickler.list.serviceEndDate" bundle="ui"/>"
					>
				</div>
				<div class="col-lg-2 col-sm-3 col-xs-6">
					<label>Status</label>
					<select ng-model="ticklerListCtrl.search.status" name="status" id="status" class="form-control" >
						<option value=""><bean:message key="tickler.list.status" bundle="ui"/></option>
						<option value="A"><bean:message key="tickler.list.status.active" bundle="ui"/></option>
						<option value="C"><bean:message key="tickler.list.status.completed" bundle="ui"/></option>
						<option value="D"><bean:message key="tickler.list.status.deleted" bundle="ui"/></option>
					</select>
				</div>
				<div class="col-lg-2 col-sm-3 col-xs-6">
					<label>Priority</label>
					<select ng-model="ticklerListCtrl.search.priority"
							name="priority"
							id="priority"
							class="form-control"
							ng-init="ticklerListCtrl.search.priority=''">
						<option value=""><bean:message key="tickler.list.priority" bundle="ui"/></option>
						<option value="Normal"><bean:message key="tickler.list.priority.normal" bundle="ui"/></option>
						<option value="High"><bean:message key="tickler.list.priority.high" bundle="ui"/></option>
						<option value="Low"><bean:message key="tickler.list.priority.low" bundle="ui"/></option>
					</select>
				</div>

				<div class="col-lg-2 col-sm-3 col-xs-6">
					<label>Assignee</label>
					<select ng-model="ticklerListCtrl.search.taskAssignedTo" name="taskAssignedTo"
							id="taskAssignedTo"
							class="form-control"
							ng-model="ticklerListCtrl.search.taskAssignedTo"
							data-ng-options="a.providerNo as a.name for a in ticklerListCtrl.providers"
							ng-init="ticklerListCtrl.search.taskAssignedTo=''">
							<option value=""><bean:message key="tickler.list.assignee" bundle="ui"/></option>
					</select>
				</div>
				<div class="col-lg-2 col-sm-3 col-xs-6">
					<label>Creator</label>
					<select ng-model="ticklerListCtrl.search.creator"
							name="creator"
							id="creator"
							class="form-control"
							ng-model="ticklerListCtrl.search.creator"
							data-ng-options="a.providerNo as a.name for a in ticklerListCtrl.providers" ng-init="ticklerListCtrl.search.creator=''">
						<option value=""><bean:message key="tickler.list.creator" bundle="ui"/></option>
					</select>
				</div>
				<div class="col-lg-2 col-sm-3 col-xs-6">
					<label>MRP</label>
					<select ng-model="ticklerListCtrl.search.mrp" name="mrp"
							id="mrp"
							class="form-control"
							ng-model="ticklerListCtrl.search.mrp"
							data-ng-options="a.providerNo as a.name for a in ticklerListCtrl.providers"
							ng-init="ticklerListCtrl.search.mrp=''">
						<option value=""><bean:message key="tickler.list.allMRP" bundle="ui"/></option>
					</select>
				</div>
			</div>

			<div class="row search-buttons">
				<div class="col-xs-12">
					<button class="btn btn-primary" type="button" ng-click="ticklerListCtrl.doSearch()" ><bean:message key="global.search" bundle="ui"/></button>
					<button class="btn btn-default" type="button" ng-click="ticklerListCtrl.clear()" ><bean:message key="global.clear" bundle="ui"/></button>

					<button class="btn btn-default" type="button" ng-click="ticklerListCtrl.printArea()"><span class="glyphicon glyphicon-print"></span> Print List</button>
				</div>
			</div>
		</form>

		<table ng-table="ticklerListCtrl.tableParams" show-filter="false" class="table table-striped table-bordered tickler-table">
			<tbody>
				<tr ng-repeat="tickler in $data">
					<td>
						<input type="checkbox" ng-model="tickler.checked" class="no-print">
					</td>
					<td>
						<button ng-click="ticklerListCtrl.editTickler(tickler)"
						        ng-disabled="!ticklerListCtrl.canEdit()"
						        class="btn btn-xs btn-primary no-print">
							<bean:message key="global.edit" bundle="ui"/>
						</button>
					</td>
					<td data-title="'<bean:message key="tickler.list.header.patientName" bundle="ui"/>'" sortable="'DemographicName'">
						<a ng-href="{{'#!/record/' + tickler.demographicNo + '/details'}}">{{tickler.demographicName}}</a>
					</td>
					<td data-title="'<bean:message key="tickler.list.header.creator"  bundle="ui"/>'" sortable="'Creator'">
						{{tickler.creatorName}}
					</td>
					<td data-title="'<bean:message key="tickler.list.header.serviceDate" bundle="ui"/>'" sortable="'ServiceDate'">
						{{tickler.serviceDate | date: 'yyyy-MM-dd'}}
					</td>
					<td data-title="'<bean:message key="tickler.list.header.creationDate" bundle="ui"/>'"  sortable="'UpdateDate'">
						{{tickler.updateDate | date: 'yyyy-MM-dd HH:mm'}}
					</td>
					<td data-title="'<bean:message key="tickler.list.header.priority" bundle="ui"/>'" sortable="'Priority'">
						{{tickler.priority}}
					</td>
					<td data-title="'<bean:message key="tickler.list.header.taskAssignedTo" bundle="ui"/>'" sortable="'TaskAssignedTo'">
						{{tickler.taskAssignedToName}}
					</td>
					<td data-title="'<bean:message key="tickler.list.header.status" bundle="ui"/>'" sortable="'Status'">
						{{tickler.statusName}}
					</td>
					<td data-title="'<bean:message key="tickler.list.header.message" bundle="ui"/>'">
						{{tickler.message | cut:true:50}}
						<span ng-if="tickler.ticklerLinks !== null && tickler.ticklerLinks.length > 0">
							<a target="lab" href="{{tickler.ticklerLinkUrl}}">ATT</a>
						</span>
					</td>
					<td data-title="'<bean:message key="tickler.list.header.comments" bundle="ui"/>'">
						<span ng-if="tickler.ticklerComments != null">
							<a ng-show="ticklerListCtrl.canEdit()">
								<span class="glyphicon glyphicon-comment" ng-click="ticklerListCtrl.showComments(tickler)"></span>
							</a>
						</span>
					</td>
					<td data-title="'<bean:message key="tickler.list.header.note" bundle="ui"/>'">
						<a ng-show="ticklerListCtrl.canEdit()"
						   ng-click="ticklerListCtrl.editNote2(tickler)"
						   class="hand-hover no-print">
							<span class="glyphicon glyphicon-edit" ></span>
						</a>
					</td>
				</tr>
			</tbody>

			<tfoot class="no-print">
				<tr>
					<td colspan="12" class="white">
						<a ng-click="ticklerListCtrl.checkAll($data)"><bean:message key="tickler.list.checkAll" bundle="ui"/></a> -
						<a ng-click="ticklerListCtrl.checkNone($data)"><bean:message key="tickler.list.checkNone" bundle="ui"/></a> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
						<button class="btn btn-danger"
						        type="button"
						        ng-disabled="!ticklerListCtrl.canDelete()"
						        ng-click="ticklerListCtrl.deleteTicklers()">
							<bean:message key="tickler.list.delete" bundle="ui"/>
						</button>
						<button class="btn btn-warning"
						        type="button"
						        ng-disabled="!ticklerListCtrl.canEdit()"
						        ng-click="ticklerListCtrl.completeTicklers()">
							<bean:message key="tickler.list.complete" bundle="ui"/>
						</button>
						<button class="btn btn-success"
						        name="button" type="button"
						        ng-disabled="!ticklerListCtrl.canCreate()"
						        ng-click="ticklerListCtrl.addTickler()">
							<bean:message key="tickler.list.add" bundle="ui"/>
						</button>
					</td>
				</tr>
			</tfoot>

		</table>
	</div>
</juno-security-check>

