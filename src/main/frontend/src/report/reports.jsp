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

<div class="report-page">
	<div class="report-page-header" ng-init="reportsCtrl.init()">
		<div class="flex-row flex-grow align-items-center">
			<h3>Reporting</h3>
		</div>
		<div class="pull-right control-right">
			<span class="dropdown-toggle hand-hover" data-toggle="dropdown">
				<h2><span class="glyphicon glyphicon-cog hand-hover"></span></h2>
			</span>
			<ul class="dropdown-menu" role="menu">
				<li>
					<a ng-click="reportsCtrl.editDemographicSet()">Demographic Set Edit</a>
				</li>
			</ul>
		</div>
	</div>
	<div class="report-page-content flex-row">
		<div class="report-selection">

			<form class="form-search" role="search">
				<div class="form-group" class="twitter-typeahead">
					<%-- Why do we use data-n--%>
					<select class="form-control" ng-options="a.value as a.label for a in reportsCtrl.reportGroups"
						ng-model="reportsCtrl.reportGroup">
					</select>
				</div>
				<div class="form-group" class="twitter-typeahead">
					<input type="text"  class="form-control" placeholder="Filter" ng-model="reportsCtrl.reportFilter" ng-init="reportsCtrl.reportFilter=''"/>
				</div>
			</form>
			<div class="list-group">
				<a  ng-repeat="report in reportsCtrl.getReports() | filter: reportsCtrl.reportFilter "
				 class="list-group-item default" ng-click="reportsCtrl.selectReport(report)">
					<span class="badge badge-info numberLabel">{{report.numberLabel}}</span>
					<span>{{report.name}}</span>
				</a>
			</div>
		</div>

		<div class="report-display">
			<div ng-include="reportsCtrl.reportSidebar.location"></div>
		</div>
	</div>
</div>