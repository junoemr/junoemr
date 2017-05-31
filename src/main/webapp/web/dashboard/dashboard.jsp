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


<div id="dashboard-page" ng-if="me != null">
	<div class="row" id="dashboard-header" >
		<div class="col-md-9 col-xs-8">
			<h3>
				<bean:message key="dashboard.welcome" bundle="ui" /> to your dashboard, {{me.firstName}}!
			</h3>
		</div>
		<div class="col-md-3 col-xs-4 pull-right">
			<p class="text-right">Today is {{displayDate() | date:'MMMM d, y'}}</p>
		</div>
	</div>
	<div class="row" id="dashboard-body">
		<div class="col-xs-12" ng-controller="Dashboard.DashboardController">
			
			<div class="row dashboard-row">
				<div class="col-xs-12">
					<!-- il18n problem here -->
					<p>You have {{(totalInbox>0) && totalInbox || "no"}} report{{(totalInbox>1) && "s" || ""}}{{(totalInbox==0) && "s" || ""}}{{(totalInbox==null)
						&& "s" || ""}} which are not yet acknowledged.
					</p>
					<div ng-if="inbox.length > 0">
						<table class="table table-condensed table-hover">
							<thead>
								<tr>
									<th class="flag-column"></th>
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
								<tr ng-repeat="item in inbox" ng-hide="$index >= 5" ng-click="openInbox()" class="hand-hover">
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
				<div class="col-xs-12">
					<!-- il18n problem here -->
					<p>You have 
						<span ng-if="totalMessages > 0" class="badge badge-danger">
							{{totalMessages}}
						</span>
						<span ng-if="totalMessages == 0">
							no
						</span>	
						unread message{{(totalMessages != 1) && "s" || ""}}.</p>
					<div ng-if="totalMessages > 0">
						<table class="table table-condensed  table-hover">
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
								<tr ng-repeat="item in messages" ng-hide="$index >= 5" ng-click="viewMessage(item)" class="hand-hover">
									<!-- 	<td></td> -->
									<td>{{item.fromName}}</td>
									<td>{{item.subject}}</td>
									<td>{{item.dateOfMessage}}</td>
									<td ng-if="item.demographicNo">{{item.demographicName}}</td>
									<td ng-if="!item.demographicNo"></td>
								</tr>
							</tbody>
							<tfoot>
								<tr ng-if="totalMessages > 5">
									<td colspan="6">
										<span class="label label-success hand-hover" ng-click="openClassicMessenger()"><bean:message key="dashboard.messages.more" bundle="ui"/></span>
									</td>
								</tr>
							</tfoot>

						</table>
					</div>
				</div>
			</div>
			
			<div class="row dashboard-row">
				<div class="col-xs-12">
					<!-- this is a bit of a problem for il18n -->
					<p >You have {{(totalTicklers > 0) && totalTicklers || "no"}} active tickler{{(totalTicklers != 1) && "s" || ""}}. 
						<span class="label label-danger" ng-if="prefs.expiredTicklersOnly == true">(Overdue)</span>
					</p>

					<div ng-if="totalTicklers>0">
						<table class="table table-condensed  table-hover">
							<thead>
								<tr>
									<th class="flag-column">
										<span class="glyphicon glyphicon-cog hand-hover" ng-click="configureTicklers()"></span>
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
								<tr ng-repeat="item in ticklers" ng-hide="$index >= 5" ng-click="viewTickler(item)" class="hand-hover">
									<td>
										<span ng-if="isTicklerHighPriority(item)" class="glyphicon glyphicon-flag" style="color:red"></span>
									</td>
									<td>{{item.demographicName}}</td>
									<td>{{item.serviceDate | date:'yyyy-MM-dd'}}</td>
									<td>{{item.message | cut:true:200 }}</td>
								</tr>
							</tbody>
							<tfoot>
								<tr ng-if="totalTicklers > 5">
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