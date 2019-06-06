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
<div class="container-fluid" id="patient-list-template"
     ng-controller="PatientList.PatientListController as patientListCtrl">
	<div class="row">
		<div id="left-aside-hidden" class="col-xs-1" ng-if="!bodyCtrl.showPatientList">
			<button class="toggle-patient-list-button"
			        type="button"
			        ng-click="patientListCtrl.showPatientList()"
			        title="Show Patient List">
				<span class="glyphicon glyphicon-chevron-right"></span>
			</button>
		</div>

		<div id="left-aside"
		     class="col-lg-2 col-md-3 col-sm-4 col-xs-7"
		     ng-controller="PatientList.PatientListAppointmentListController as patientListAppointmentListCtrl"
		     ng-if="bodyCtrl.showPatientList">

			<div id="left-aside-header" class="row vertical-align">
				<div class="col-sm-2 col-xs-3">
					<button class="toggle-patient-list-button pull-left"
					        type="button"
					        ng-click="patientListCtrl.hidePatientList()"
					        title="<bean:message key="patientList.hide" bundle="ui"/>">
						<span class="glyphicon glyphicon-chevron-left"></span>
					</button>
				</div>
				<div class="col-sm-9">
					<input type="text" class="form-control"
					       placeholder="<bean:message key="patientList.search" bundle="ui"/>"
					       ng-model="query"/>
				</div>
			</div>
			<div class="col-sm-12">
				<div class="row" ng-cloak>
					<ul class="nav nav-tabs">
						<li ng-repeat="item in patientListCtrl.getTabItems()"
						    ng-class="{'active': patientListCtrl.isActive(item.id)}"
						    class="hand-hover">
							<a ng-click="patientListCtrl.changeTab(item.id)" data-toggle="tab">{{item.label}}</a>
						</li>

						<li class="dropdown" ng-class="{'active': patientListCtrl.currentmoretab != null}">
							<a class="dropdown-toggle hand-hover" data-toggle="dropdown"><b class="caret"></b></a>
							<ul class="dropdown-menu dropdown-menu-right" role="menu">
								<li ng-repeat="item in patientListCtrl.moreTabItems">
									<a ng-class="patientListCtrl.getMoreTabClass(item.id)"
									   ng-click="patientListCtrl.changeMoreTab(item.id)"
									   class="hand-hover">
										{{item.label}}
										<span ng-if="item.extra.length>0" class="label">{{item.extra}}</span>
									</a>
								</li>
							</ul>
						</li>

					</ul>
					<div ng-include="patientListCtrl.sidebar.location"></div>
					<div class="col-md-2 pull-right">
						<span title="<bean:message key="patientList.pagination" bundle="ui"/>">
							{{patientListCtrl.currentPage+1}}/{{patientListCtrl.numberOfPages()}}
						</span>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>