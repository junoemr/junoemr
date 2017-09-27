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

<%--Place csutom styles here to override bootstap styles--%>


<div id="dashboard-page" ng-controller="Dashboard.DashboardController as dashboardCtrl">
	<div ng-if="dashboardCtrl.me != null">
		<div class="row" id="dashboard-header" >
			<div class="col-md-9 col-xs-8">
				<h3>
					<bean:message key="dashboard.welcome" bundle="ui" /> to your dashboard, {{dashboardCtrl.me.firstName}}!
				</h3>
			</div>
			<div class="col-md-3 col-xs-4 pull-right">
				<p class="text-right">Today is {{dashboardCtrl.displayDate() | date:'MMMM d, y'}}</p>
			</div>
		</div>
		<div class="row" id="dashboard-body" ng-if="dashboardCtrl.me != null">
			<div class="col-xs-12" >
				
				<div class="row dashboard-row">
					<div class="col-lg-8 col-xs-12">
						<p>
							<a ng-click="dashboardCtrl.openInbox()">
								You have
								<span ng-if="dashboardCtrl.totalInbox > 0" class="badge badge-danger">
									{{dashboardCtrl.totalInbox}}
								</span>
								<ng-pluralize count="dashboardCtrl.totalInbox"
											when="{
												'0':'no reports which are not yet acknowledged.',
												'1':'report which is not yet acknowledged.',
												'other': 'reports which are not yet acknowledged.'
											}">
								</ng-pluralize>
							</a>
						</p>
						<div ng-if="dashboardCtrl.totalInbox > 0">
							<%--Keeping incase we want to switch to using ngTable--%>
							<%--<table ng-table="inboxTableParams" class="table table-striped table-bordered table-condensed dashboard-table">
								<tbody>
									<tr ng-repeat="item in $data" ng-click="openInbox()" class="hand-hover dashboard-table-row">
										<td title="'Patient'">
											{{item.demographicName}}
										</td>
										<td title="'Category'">
											{{item.discipline}}
										</td>
										<td title="'Date'">
											{{item.discipline}}
										</td>
										<td title="'Status'">
											{{item.discipline}}
										</td>
									</tr>
								</tbody>
							</table>--%>

							<table class="table table-condensed table-striped table-bordered table-hover">
								<thead class="text-center">
									<tr>
										<th class="flag-column">Priority</th>
										<th>
											<bean:message key="dashboard.inbox.header.patient" bundle="ui" />
										</th>
										<th>
											<bean:message key="dashboard.inbox.header.category" bundle="ui" />
										</th>
										<!--  <th>Source</th> -->
										<th>
											<bean:message key="dashboard.inbox.header.date" bundle="ui" />
										</th>
										<th>
											<bean:message key="dashboard.inbox.header.status" bundle="ui" />
										</th>
									</tr>
								</thead>

								<tbody>
									<tr ng-repeat="item in dashboardCtrl.inbox" ng-hide="$index >= 5" ng-click="dashboardCtrl.openInbox()" class="hand-hover">
										<td><span ng-if="item.properity != null && item.priority != 'Routine'" class="glyphicon glyphicon-flag" style="color:red"></span></td>
										<td>{{item.demographicName}}</td>
										<td>{{item.discipline}}</td>
										<!--  <td>{{item.source}}</td> -->
										<td>{{item.dateReceived}}</td>
										<td>{{item.status}}</td>
									</tr>
								</tbody>
								<tfoot>
									<tr ng-if="inbox.length > 5">
										<td colspan="6">
											<span class="label label-success hand-hover" ui-sref="inbox"><bean:message key="dashboard.inbox.more" bundle="ui"/></span>
										</td>
									</tr>
								</tfoot>
							</table>
						</div>
					</div>
				</div>

				<div class="row dashboard-row">
					<div class="col-lg-8 col-xs-12">
						<!-- il18n problem here -->
						<p>
							<a ng-click="dashboardCtrl.openClassicMessenger()">
								You have 
								<span ng-if="dashboardCtrl.totalMessages > 0" class="badge badge-danger">
									{{dashboardCtrl.totalMessages}}
								</span>
								<ng-pluralize count="dashboardCtrl.totalMessages"
											when="{
												'0':'no unread messages.',
												'1':'unread message.',
												'other': 'unread messages.'
											}">
								</ng-pluralize>
							</a>
						</p>
						<div ng-if="dashboardCtrl.totalMessages > 0">
							<table class="table table-condensed table-striped table-bordered table-hover">
								<thead>
									<tr>
										<!-- 	<th class="flag-column"></th> -->
										<th>
											<bean:message key="dashboard.messages.header.from" bundle="ui" />
										</th>
										<th>
											<bean:message key="dashboard.messages.header.subject" bundle="ui" />
										</th>
										<th>
											<bean:message key="dashboard.messages.header.date" bundle="ui" />
										</th>
										<th>
											<bean:message key="dashboard.messages.header.patient" bundle="ui" />
										</th>
									</tr>
								</thead>
								<tbody>
									<tr ng-repeat="item in dashboardCtrl.messages" ng-hide="$index >= 5" ng-click="dashboardCtrl.viewMessage(item)" class="hand-hover">
										<!-- 	<td></td> -->
										<td>{{item.fromName}}</td>
										<td>{{item.subject}}</td>
										<td>{{item.dateOfMessage}}</td>
										<td ng-if="item.demographicNo">{{item.demographicName}}</td>
										<td ng-if="!item.demographicNo"></td>
									</tr>
								</tbody>
								<tfoot>
									<tr ng-if="dashboardCtrl.totalMessages > 5">
										<td colspan="6">
											<span class="label label-success hand-hover" ng-click="dashboardCtrl.openClassicMessenger()"><bean:message key="dashboard.messages.more" bundle="ui"/></span>
										</td>
									</tr>
								</tfoot>

							</table>
						</div>
					</div>
				</div>
				
				<div class="row dashboard-row">
					<div class="col-lg-8 col-xs-12">
						<!-- this is a bit of a problem for il18n -->
						<p>
							<a href="../web/#!/ticklers"> <%-- Not a permanent solution, figure out better way to link to ticklers --%>
								You have
								<span ng-if="dashboardCtrl.totalTicklers > 0" class="badge badge-danger">
									{{dashboardCtrl.totalTicklers}}
								</span>
								<ng-pluralize count="dashboardCtrl.totalTicklers"
											when="{
												'0':'no active ticklers.',
												'1':'active tickler.',
												'other': 'active ticklers.'
											}">
								</ng-pluralize> 
							</a>
							<span class="label label-danger" ng-if="dashboardCtrl.prefs.expiredTicklersOnly == true && dashboardCtrl.totalTicklers > 0">(Overdue)</span>
						</p>

						<div ng-if="dashboardCtrl.totalTicklers>0">
							<table class="table table-condensed table-striped table-bordered table-hover">
								<thead>
									<tr>
										<th class="flag-column">
											<span class="glyphicon glyphicon-cog hand-hover"
												  ng-click="dashboardCtrl.configureTicklers()"
												  title="Configure tickler list">
											</span>
										</th>
										<th>
											<bean:message key="dashboard.tickler.header.demographicName" bundle="ui" />
										</th>
										<th>
											<bean:message key="dashboard.tickler.header.due" bundle="ui" />
										</th>
										<th>
											<bean:message key="dashboard.tickler.header.message" bundle="ui" />
										</th>

									</tr>
								</thead>
								<tbody>
									<tr ng-repeat="item in dashboardCtrl.ticklers" ng-hide="$index >= 5" ng-click="dashboardCtrl.viewTickler(item)" class="hand-hover">
										<td>
											<span ng-if="dashboardCtrl.isTicklerHighPriority(item)"
												  class="glyphicon glyphicon-flag"
												  style="color:red"
												  title="High priority tickler">
											</span>
										</td>
										<td>{{item.demographicName}}</td>
										<td>{{item.serviceDate | date:'yyyy-MM-dd'}}</td>
										<td>{{item.message | cut:true:200 }}</td>
									</tr>
								</tbody>
								<tfoot>
									<tr ng-if="dashboardCtrl.totalTicklers > 5">
										<td colspan="6">
											<span class="label label-success hand-hover" ui-sref="ticklers"><bean:message key="dashboard.tickler.more" bundle="ui"/></span>
										</td>
									</tr>
								</tfoot>

							</table>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>