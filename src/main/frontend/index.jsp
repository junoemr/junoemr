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
<%@page import="org.oscarehr.util.LoggedInInfo" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%
	// Force the page to un-cache itself so user cannot go back after logout
	// The 3 lines ensure that all browsers are covered
	// They are necessary for URL not showing this file name (index.jsp)
	response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
	response.setHeader("Pragma", "no-cache");
	response.setDateHeader("Expires", 0);

	session.setAttribute("useIframeResizing", "true");  //Temporary Hack
%>

<!DOCTYPE html>
<!-- ng* attributes are references into AngularJS framework -->
<html lang="en" ng-app="oscarProviderViewModule">
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="description" content="">
	<meta name="author" content="">
	<link rel="shortcut icon" href="../images/Oscar.ico">

	<title><bean:message key="global.title" bundle="ui"/></title>

	<%-- This is in the HTML to make sure it is loaded with the page --%>
	<style>
		[ng\:cloak], [ng-cloak], [data-ng-cloak], [x-ng-cloak], .ng-cloak, .x-ng-cloak {
			display: none !important;
		}
	</style>
</head>

<body ng-controller="Layout.BodyController as bodyCtrl"
	  ng-init="bodyCtrl.init()"
	  id="main-body">

<!-- Navbar -->
<nav ng-controller="Layout.NavBarController as navBarCtrl"
	 ng-init="navBarCtrl.init()"
	 ng-show="navBarCtrl.me != null"
	 class="navbar navbar-default navbar-fixed-top"
	 id="main-nav">
	<div class="container-fluid">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#main-nav-collapse">
				<span class="icon-bar"></span> <span class="icon-bar"></span> <span class="icon-bar"></span>
			</button>

			<%--<div class="navbar-text">
				<a  href="../provider/providercontrol.jsp" style="color: white;">
					Switch UI
				</a>
			</div>--%>
			<!-- link back to 'classic' view -->
			<a href="../provider/providercontrol.jsp">
				<img id="navbarlogo" src="../images/Oscar.ico"
					 title="<bean:message key="global.goToClassic" bundle="ui"/>" border="0"/>
			</a>
		</div>
		<div class="navbar-collapse collapse" id="main-nav-collapse">

			<form class="navbar-form navbar-left" role="search">
				<div class="form-group" ng-cloak>
					<juno-patient-search-typeahead
							juno-model="navBarCtrl.demographicSearch"
							juno-placeholder="<bean:message key="navbar.searchPatients" bundle="ui"/>"
							juno-on-search-fn="navBarCtrl.onPatientSearch"
							juno-on-add-fn="navBarCtrl.newDemographic"
							juno-search-button-title="<bean:message key="navbar.searchPatients" bundle="ui"/>"
							juno-add-button-title="<bean:message key="navbar.newPatient" bundle="ui"/>">
					</juno-patient-search-typeahead>
				</div>
			</form>

			<!-- Large view -->
			<ul class="nav navbar-nav visible-nav-lg" ng-cloak>
				<li ng-repeat="item in navBarCtrl.menuItems"
					ng-class="{'active': navBarCtrl.isActive(item) }">

					<a href="javascript:void(0)"
					   ng-if="!item.dropdown"
					   ng-click="navBarCtrl.transition(item)">{{item.label}}
						<span ng-if="item.label=='Inbox' && navBarCtrl.unAckLabDocTotal > 0"
							  class="badge badge-danger">{{navBarCtrl.unAckLabDocTotal}}</span>
					</a>

					<a href="javascript:void(0)"
					   ng-if="item.dropdown"
					   class="dropdown-toggle"
					   data-toggle="dropdown">{{item.label}}
						<span class="caret"></span>
					</a>

					<ul ng-if="item.dropdown"
						class="dropdown-menu"
						role="menu">
						<li ng-repeat="dropdownItem in item.dropdownItems">
							<a href="javascript:void(0)"
							   ng-click="navBarCtrl.transition(dropdownItem)">{{dropdownItem.label}}</a>
						</li>
					</ul>
				</li>
			</ul>

			<!-- Medium view -->
			<ul class="nav navbar-nav visible-nav-md" ng-cloak>
				<li ng-repeat="item in navBarCtrl.menuItems | filter: navBarCtrl.mediumNavItemFilter(false)"
					ng-class="{'active': navBarCtrl.isActive(item) }">

					<%--<a ng-click="navBarCtrl.transition(item)" data-toggle="tab" >{{item.label}}
						<span ng-if="item.extra.length>0">({{item.extra}})</span>
					</a>--%>

					<a href="javascript:void(0)"
					   ng-if="!item.dropdown"
					   ng-click="navBarCtrl.transition(item)">{{item.label}}
						<span ng-if="item.label=='Inbox' && navBarCtrl.unAckLabDocTotal > 0"
							  class="badge badge-danger">{{navBarCtrl.unAckLabDocTotal}}</span>
					</a>

					<a href="javascript:void(0)"
					   ng-if="item.dropdown"
					   class="dropdown-toggle"
					   data-toggle="dropdown">{{item.label}}
						<span class="caret"></span>
					</a>

					<ul ng-if="item.dropdown"
						class="dropdown-menu"
						role="menu">
						<li ng-repeat="dropdownItem in item.dropdownItems">
							<a href="javascript:void(0)"
							   ng-click="navBarCtrl.transition(dropdownItem)" >{{dropdownItem.label}}</a>
						</li>
					</ul>
				</li>
				<li class="dropdown hand-hover">
					<a href="javascript:void(0)" class="dropdown-toggle" data-toggle="dropdown">
						More
						<b class="caret"></b>
					</a>

					<ul class="dropdown-menu" role="menu">
						<li ng-repeat="item in navBarCtrl.menuItems | filter: navBarCtrl.mediumNavItemFilter(true)"
							ng-class="{'active': navBarCtrl.isActive(item) }">
							<a href="javascript:void(0)"

							   ng-click="navBarCtrl.transition(item)" data-toggle="tab">{{item.label}}
								<span ng-if="item.extra.length>0">({{item.extra}})</span>
							</a>
						</li>
					</ul>
				</li>
			</ul>

			<%--Small View--%>
			<ul class="nav navbar-nav visible-nav-sm" ng-cloak>
				<li ng-repeat="item in navBarCtrl.menuItems | filter: navBarCtrl.smallNavItemFilter(false)"
					ng-class="{'active': navBarCtrl.isActive(item) }">
					<a ng-click="navBarCtrl.transition(item)" data-toggle="tab">{{item.label}}
						<span ng-if="item.extra.length>0">({{item.extra}})</span>
					</a>
				</li>
				<li class="dropdown hand-hover">
					<a href="javascript:void(0)" class="dropdown-toggle" data-toggle="dropdown">
						More
						<b class="caret"></b>
					</a>

					<ul class="dropdown-menu" role="menu">
						<li ng-repeat="item in navBarCtrl.menuItems | filter: navBarCtrl.smallNavItemFilter(true)"
							ng-class="{'active': navBarCtrl.isActive(item) }">
							<a href="javascript:void(0)"
							   ng-if="!item.dropdown"
							   ng-click="navBarCtrl.transition(item)">{{item.label}}
								<span ng-if="item.label=='Inbox' && navBarCtrl.unAckLabDocTotal > 0"
									  class="badge badge-danger">{{navBarCtrl.unAckLabDocTotal}}</span>
							</a>

							<a href="javascript:void(0)"
							   ng-if="item.dropdown"
							   ng-repeat="dropdownItem in item.dropdownItems"
							   ng-class="{'active': navBarCtrl.isActive(dropdownItem) }"
							   ng-click="navBarCtrl.transition(dropdownItem)">
								{{dropdownItem.label}}
							</a>
						</li>
						<%--<li ng-repeat="item in navBarCtrl.moreMenuItems">
							<a ng-class="{'active': isActive(item) }"
								 ng-click="navBarCtrl.transition(item)">{{item.label}}
							<span ng-if="item.extra.length>0" class="label">{{item.extra}}</span></a>
						</li>--%>
					</ul>
				</li>
			</ul>

			<div class="navbar-text pull-right navbar-right-menu" ng-cloak>
				<a onClick="popup(700,1024,'../scratch/index.jsp','scratch')"
				   title="<bean:message key="navbar.scratchpad" bundle="ui"/>"
				   class="hand-hover">
					<span class="fa fa-pencil-square"></span>
				</a>
				&nbsp;
				<span ng-show="navBarCtrl.messageRights === true">
						<a ng-click="navBarCtrl.openMessenger()"
						   title="<bean:message key="navbar.messenger" bundle="ui"/>"
						   class="hand-hover">
							<span class="fa fa-envelope"></span>
							<span ng-show="navBarCtrl.unreadMessageTotal > 0"
								  class="badge badge-danger">{{navBarCtrl.unreadMessageTotal}}
							</span>
						</a>
						&nbsp;&nbsp;
						<a ng-click="navBarCtrl.openMessenger(navBarCtrl.messengerMenu)"
						   title="{{navBarCtrl.messengerMenu.label}}"
						   class="hand-hover">{{navBarCtrl.messengerMenu.extra}}</a>

						<span ng-if="!$last"></span>
					</span>
				<%--As of now we are not letting users change their program from the front end--%>
				<%--<span class="dropdown">
					<ul class="dropdown-menu" role="menu">
						<li ng-repeat="item in navBarCtrl.programDomain">
							<a ng-click="navBarCtrl.changeProgram(item.program.id)">
								<span ng-if="item.program.id === navBarCtrl.currentProgram.id">&#10004;</span>
								<span ng-if="item.program.id != navBarCtrl.currentProgram.id"></span>
								{{item.program.name}}
							</a>
						</li>
					 </ul>
				 </span>--%>

				<span class="dropdown-toggle hand-hover"
					  data-toggle="dropdown"
					  title="<bean:message key="navbar.user" bundle="ui"/>">
						<span class="fa fa-user"></span>&nbsp;{{navBarCtrl.me.firstName}}
					</span>
				<ul class="dropdown-menu" role="menu">
					<li ng-repeat="item in navBarCtrl.userMenuItems">
						<a ng-click="navBarCtrl.transition(item)"
						   ng-class="{'more-tab-highlight':  navBarCtrl.isActive(item) }"
						   class="hand-hover">{{item.label}}</a>
						<a ng-if="item.url"
						   href="{{item.url}}"
						   target="_blank">{{dropdownItem.label}}</a>
					</li>
				</ul>
			</div>
		</div>
		<!--/.nav-collapse -->
	</div>
</nav>

<!-- nav bar is done here -->

<!-- Start patient List template -->
<div class="container-fluid" id="patient-list-template"
	 ng-controller="PatientList.PatientListController as patientListCtrl">
	<div class="row">
		<div id="left-pane-hidden" class="col-xs-1" ng-if="!bodyCtrl.showPatientList">
			<button class="toggle-patient-list-button"
					type="button"
					ng-click="patientListCtrl.showPatientList()"
					title="Show Patient List">
				<span class="glyphicon glyphicon-chevron-right"></span>
			</button>
		</div>

		<div id="left-pane"
			 class="col-lg-2 col-md-3 col-sm-4 col-xs-7"
			 ng-controller="PatientList.PatientListAppointmentListController as patientListAppointmentListCtrl"
			 ng-if="bodyCtrl.showPatientList">

			<div id="left-pane-header" class="row vertical-align">
				<div class="col-sm-2 col-xs-3">
					<button class="toggle-patient-list-button pull-left"
							type="button"
							ng-click="patientListCtrl.hidePatientList()"
							title="<bean:message key="patientList.hide" bundle="ui"/>">
						<span class="glyphicon glyphicon-chevron-left"></span>
					</button>
				</div>
				<div class="col-sm-9">
					<%--<h3 class="no-margin-top" id="left-pane-header-title">Appointments</h3>--%>
					<%--<form id="patient-search" class="form-search" role="search">--%>
					<%--<span ng-show="showFilter === true" class="form-group ">--%>
					<input type="text" class="form-control"
						   placeholder="<bean:message key="patientList.search" bundle="ui"/>"
						   ng-model="query"/>
					<%--</span>--%>
					<%--</form>--%>
				</div>
				<%--NOTE: Need to give this controller access to the addNewAppointment() function before this button can be used here --%>
				<div class="col-md-2">
					<a class="hand-hover" ng-click="patientListAppointmentListCtrl.addNewAppointment()">
						<span class="glyphicon glyphicon-plus" title="Add appointment"></span>
					</a>
				</div>
			</div>

			<div id="left-pane-calendar" ng-show="patientListAppointmentListCtrl.isScheduleActive();">
				<div class="calendar-daypicker-container">
					<div uib-datepicker
						 ng-model="selectedDate"
						 datepicker-options="{showWeeks: false}"
						 class="well well-sm"
					></div>
				</div>
			</div>

			<div class="col-sm-12">
				<div class="row" ng-cloak>
					<%--<button type="button" class="btn btn-default" ng-click="refresh()" title="<bean:message key="patientList.refresh" bundle="ui"/>">
						<span class="glyphicon glyphicon-refresh"></span>
					</button>
					--%>
					<%--Remove these? --%>
					<%--<button type="button" class="btn btn-default" ng-disabled="currentPage == 0" ng-click="changePage(currentPage-1)" title="<bean:message key="patientList.pageUp" bundle="ui"/>">
						<span class="glyphicon glyphicon-circle-arrow-up"></span>
					</button>

					<button type="button" class="btn btn-default" ng-disabled="currentPage == nPages-1"  ng-click="changePage(currentPage+1)" title="<bean:message key="patientList.pageDown" bundle="ui"/>">
						<span class="glyphicon glyphicon-circle-arrow-down"></span>
					</button>--%>
					<ul class="nav nav-tabs">
						<li ng-repeat="item in patientListCtrl.getTabItems()"
							ng-class="{'active': patientListCtrl.isActive(item.id)}"
							class="hand-hover">
							<a ng-click="patientListCtrl.changeTab(item.id)" data-toggle="tab">{{item.label}}</a>
						</li>
						<%--<li class="hand-hover">--%>
							<%--<a ng-click="patientListCtrl.changeTab(0)" data-toggle="tab">Appts.</a>--%>
							<%--<a ng-click="patientListCtrl.changeTab(1)" data-toggle="tab">Recent</a>--%>
						<%--</li>--%>

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


		<!-- End patient List template -->

		<div id="right-pane"
			 ng-class="{
						'col-lg-10 col-lg-offset-2 col-md-9 col-md-offset-3 col-sm-8 col-sm-offset-4 col-xs-12': bodyCtrl.showPatientList,
						'col-xs-12 right-pane-padded': !bodyCtrl.showPatientList }"
			 ui-view
			 ng-cloak>
		</div>
	</div>
</div>
</div>

<script>

	// Juno POJO namespace
	window.Juno = {};

	Juno.contextPath = '<%= request.getContextPath() %>';

</script>

</body>
</html>
