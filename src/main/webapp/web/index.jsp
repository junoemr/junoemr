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

	<%-- 3rd party CSS --%>
	<link href="bower_components/ng-table-bundle/ng-table.min.css" rel="stylesheet">
	<link href="bower_components/angular-loading-bar/build/loading-bar.min.css" rel="stylesheet">
	<link href="bower_components/components-font-awesome/css/font-awesome.min.css" rel="stylesheet">
	<link href="bower_components/jquery-ui/themes/base/jquery-ui.min.css" rel="stylesheet">

	<%-- combined CSS from compiled SCSS --%>
	<link href="dist/juno.css" rel="stylesheet">

	<%-- TODO move to a SCSS file and include in Juno SCSS/CSS --%>
	<link href="../library/bootstrap/3.0.0/assets/css/bootstrap3_badge_colours.css" rel="stylesheet">

</head>

<body ng-controller="Layout.BodyController as bodyCtrl"
	  ng-init="bodyCtrl.init()"
	  id="main-body">

<!-- Navbar -->
<nav ng-controller="Layout.NavBarController as navBarCtrl"
	 ng-init="navBarCtrl.init()"
	 ng-show="navBarCtrl.me != null"
	 class="navbar navbar-default navbar-fixed-top"
	 id="main-nav"
	 ng-cloak>
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
				<div class="form-group">
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
			<ul class="nav navbar-nav visible-nav-lg">
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
			<ul class="nav navbar-nav visible-nav-md">
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
			<ul class="nav navbar-nav visible-nav-sm">
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

			<div class="navbar-text pull-right navbar-right-menu">
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

<%-- third party libraries, managed with bower, combined and minified for production --%>
<!-- build:component js/components.js -->
<script type="text/javascript" src="bower_components/jquery/dist/jquery.min.js"></script>
<script type="text/javascript" src="bower_components/jquery-ui/jquery-ui.min.js"></script>
<script type="text/javascript" src="bower_components/bootstrap-sass/assets/javascripts/bootstrap.min.js"></script>
<script type="text/javascript" src="bower_components/angular/angular.min.js"></script>
<script type="text/javascript" src="bower_components/angular-ui-router/release/angular-ui-router.min.js"></script>
<script type="text/javascript" src="bower_components/angular-ui-router/release/stateEvents.js"></script>
<script type="text/javascript" src="bower_components/angular-resource/angular-resource.min.js"></script>
<script type="text/javascript" src="bower_components/angular-bootstrap/ui-bootstrap-tpls.min.js"></script>
<script type="text/javascript" src="bower_components/bootstrap-timepicker/js/bootstrap-timepicker.js"></script>
<script type="text/javascript" src="bower_components/angular-loading-bar/build/loading-bar.min.js"></script>
<script type="text/javascript" src="bower_components/ng-table-bundle/ng-table.min.js"></script>
<script type="text/javascript" src="bower_components/ngInfiniteScroll/build/ng-infinite-scroll.min.js"></script>
<script type="text/javascript" src="bower_components/pym.js/dist/pym.v1.min.js"></script>
<script type="text/javascript" src="bower_components/moment/min/moment-with-locales.min.js"></script>
<script type="text/javascript" src="bower_components/ngstorage/ngStorage.js"></script>
<!-- endbuild -->

<%-- JunoUI application code, to be combined and minified for production --%>
<%--IMPORTANT: Import non-angular javascript files before angular javacript--%>
<!-- build:js js/scripts.js -->
<script type="text/javascript" src="../share/javascript/Oscar.js"></script>

<%--Non-angular scripts--%>
<script type="text/javascript" src="common/util/util.js"></script>
<script type="text/javascript" src="common/util/serviceHelper.js"></script>
<script type="text/javascript" src="consults/common.js"></script>

<%--Angular scripts--%>
<script type="text/javascript" src="app.js"></script>

<script type="text/javascript" src="common/module.js"></script>

<%--Angular Util Functions--%>
<script type="text/javascript" src="common/util/module.js"></script>
<script type="text/javascript" src="common/util/angular-util.js"></script>
<script type="text/javascript" src="common/util/junoHttp.js"></script>

<%--Angular Services--%>
<script type="text/javascript" src="common/services/module.js"></script>
<script type="text/javascript" src="common/services/appService.js"></script>
<script type="text/javascript" src="common/services/billingService.js"></script>
<script type="text/javascript" src="common/services/consultService.js"></script>
<script type="text/javascript" src="common/services/demographicService.js"></script>
<script type="text/javascript" src="common/services/demographicsService.js"></script>
<script type="text/javascript" src="common/services/diseaseRegistryService.js"></script>
<script type="text/javascript" src="common/services/formService.js"></script>
<script type="text/javascript" src="common/services/inboxService.js"></script>
<script type="text/javascript" src="common/services/k2aService.js"></script>
<script type="text/javascript" src="common/services/messageService.js"></script>
<script type="text/javascript" src="common/services/noteService.js"></script>
<script type="text/javascript" src="common/services/patientDetailStatusService.js"></script>
<script type="text/javascript" src="common/services/personaService.js"></script>
<script type="text/javascript" src="common/services/programService.js"></script>
<script type="text/javascript" src="common/services/providerService.js"></script>
<script type="text/javascript" src="common/services/providersService.js"></script>
<script type="text/javascript" src="common/services/scheduleService.js"></script>
<script type="text/javascript" src="common/services/securityService.js"></script>
<script type="text/javascript" src="common/services/staticDataService.js"></script>
<script type="text/javascript" src="common/services/summaryService.js"></script>
<script type="text/javascript" src="common/services/ticklerService.js"></script>
<script type="text/javascript" src="common/services/reportByTemplateService.js"></script>
<script type="text/javascript" src="common/services/uxService.js"></script>
<script type="text/javascript" src="common/services/specialistsService.js"></script>
<script type="text/javascript" src="common/services/referralDoctorsService.js"></script>
<script type="text/javascript" src="common/services/reportingService.js"></script>

<script type="text/javascript" src="common/filters/module.js"></script>
<script type="text/javascript" src="common/filters/age.js"></script>
<script type="text/javascript" src="common/filters/cut.js"></script>
<script type="text/javascript" src="common/filters/offset.js"></script>
<script type="text/javascript" src="common/filters/startFrom.js"></script>

<script type="text/javascript" src="common/directives/module.js"></script>
<script type="text/javascript" src="common/directives/typeaheadHelper.js"></script>
<script type="text/javascript" src="common/directives/patientSearchTypeahead.js"></script>
<script type="text/javascript" src="common/directives/patientTypeahead.js"></script>
<script type="text/javascript" src="common/directives/datepickerPopup.js"></script>
<script type="text/javascript" src="common/directives/jqueryUIResizable.js"></script>
<script type="text/javascript" src="common/directives/jqueryUIDraggable.js"></script>

<script type="text/javascript" src="layout/module.js"></script>
<script type="text/javascript" src="layout/bodyController.js"></script>
<script type="text/javascript" src="layout/navBarController.js"></script>

<script type="text/javascript" src="patient/module.js"></script>
<script type="text/javascript" src="patient/newPatientController.js"></script>

<script type="text/javascript" src="dashboard/module.js"></script>
<script type="text/javascript" src="dashboard/dashboardController.js"></script>
<script type="text/javascript" src="dashboard/ticklerConfigureController.js"></script>

<script type="text/javascript" src="patientlist/module.js"></script>
<script type="text/javascript" src="patientlist/patientListState.js"></script>
<script type="text/javascript" src="patientlist/patientListController.js"></script>
<script type="text/javascript" src="patientlist/patientListAppointmentListController.js"></script>
<script type="text/javascript" src="patientlist/patientListConfigController.js"></script>
<script type="text/javascript" src="patientlist/patientListDemographicSetController.js"></script>
<script type="text/javascript" src="patientlist/patientListProgramController.js"></script>

<script type="text/javascript" src="record/module.js"></script>
<script type="text/javascript" src="record/recordController.js"></script>
<script type="text/javascript" src="record/summary/module.js"></script>
<script type="text/javascript" src="record/summary/summaryController.js"></script>
<script type="text/javascript" src="record/summary/recordPrintController.js"></script>
<script type="text/javascript" src="record/summary/groupNotesController.js"></script>
<script type="text/javascript" src="record/summary/saveWarningController.js"></script>
<script type="text/javascript" src="record/forms/module.js"></script>
<script type="text/javascript" src="record/forms/formsController.js"></script>
<script type="text/javascript" src="record/details/module.js"></script>
<script type="text/javascript" src="record/details/detailsController.js"></script>
<script type="text/javascript" src="record/phr/module.js"></script>
<script type="text/javascript" src="record/phr/phrController.js"></script>

<script type="text/javascript" src="record/tracker/module.js"></script>
<script type="text/javascript" src="record/tracker/trackerController.js"></script>

<script type="text/javascript" src="tickler/module.js"></script>
<script type="text/javascript" src="tickler/ticklerListController.js"></script>
<script type="text/javascript" src="tickler/ticklerViewController.js"></script>
<script type="text/javascript" src="tickler/ticklerAddController.js"></script>
<script type="text/javascript" src="tickler/ticklerNoteController.js"></script>
<script type="text/javascript" src="tickler/ticklerCommentController.js"></script>

<script type="text/javascript" src="schedule/module.js"></script>
<script type="text/javascript" src="schedule/scheduleController.js"></script>
<script type="text/javascript" src="schedule/appointmentAddController.js"></script>
<script type="text/javascript" src="schedule/appointmentViewController.js"></script>

<script type="text/javascript" src="admin/module.js"></script>
<script type="text/javascript" src="admin/adminController.js"></script>
<script type="text/javascript" src="admin/integration/module.js"></script>
<script type="text/javascript" src="admin/integration/know2act/module.js"></script>
<script type="text/javascript" src="admin/integration/know2act/Know2actConfigController.js"></script>
<script type="text/javascript" src="admin/integration/know2act/Know2actNotificationController.js"></script>
<script type="text/javascript" src="admin/integration/know2act/Know2actTemplateController.js"></script>

<script type="text/javascript" src="billing/billingController.js"></script>

<script type="text/javascript" src="consults/module.js"></script>
<script type="text/javascript" src="consults/consultRequestAttachmentController.js"></script>
<script type="text/javascript" src="consults/consultResponseAttachmentController.js"></script>
<script type="text/javascript" src="consults/consultRequestListController.js"></script>
<script type="text/javascript" src="consults/consultRequestController.js"></script>
<script type="text/javascript" src="consults/consultResponseListController.js"></script>
<script type="text/javascript" src="consults/consultResponseController.js"></script>

<script type="text/javascript" src="inbox/module.js"></script>
<script type="text/javascript" src="inbox/inboxController.js"></script>

<script type="text/javascript" src="patient/search/module.js"></script>
<script type="text/javascript" src="patient/search/patientSearchController.js"></script>
<script type="text/javascript" src="patient/search/remotePatientResultsController.js"></script>

<script type="text/javascript" src="report/module.js"></script>
<script type="text/javascript" src="report/reportsController.js"></script>
<script type="text/javascript" src="report/reportBadAppointmentSheetController.js"></script>
<script type="text/javascript" src="report/reportDaySheetController.js"></script>
<script type="text/javascript" src="report/reportEdbListController.js"></script>
<script type="text/javascript" src="report/reportFollowUpIntakeController.js"></script>
<script type="text/javascript" src="report/reportNoShowAppointmentSheetController.js"></script>
<script type="text/javascript" src="report/reportOldPatientsController.js"></script>
<script type="text/javascript" src="report/reportPatientChartListController.js"></script>
<script type="text/javascript" src="report/reportRegistrationIntakeController.js"></script>
<script type="text/javascript" src="report/reportSHMentalHealthController.js"></script>

<script type="text/javascript" src="document/module.js"></script>
<script type="text/javascript" src="document/documentsController.js"></script>

<script type="text/javascript" src="settings/module.js"></script>
<script type="text/javascript" src="settings/settingsController.js"></script>
<script type="text/javascript" src="settings/changePasswordController.js"></script>
<script type="text/javascript" src="settings/quickLinkController.js"></script>

<script type="text/javascript" src="help/module.js"></script>
<script type="text/javascript" src="help/supportController.js"></script>
<script type="text/javascript" src="help/helpController.js"></script>

<!-- endbuild -->

<jsp:include page="dist/templates.jsp"/>

</body>
</html>
