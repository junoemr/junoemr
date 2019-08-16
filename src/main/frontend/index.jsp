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
	<link rel="shortcut icon" href="../images/favi_32.png">

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
<div ng-controller="Layout.NavBarController as navBarCtrl"
     ng-init="navBarCtrl.init()"
     ng-show="navBarCtrl.me != null"
     id="index-header">
	<nav class="nav">
		<div class="container-fluid">
			<div class="navbar-header">
				<button class="btn btn-icon"
				        ng-click="navBarCtrl.loadClassicUi();"
				        title="<bean:message key="global.goToClassic" bundle="ui"/>" border="0">
					<a class="icon icon-logo"></a>
				</button>
			</div>
			<div class="navbar-collapse collapse" id="main-nav-collapse">
				<div class="navbar-left">
					<form class="vertical-align patient-search-form" role="search">
						<div class="form-group breakpoint-sm-visible" ng-cloak>
							<juno-patient-search-typeahead
									juno-model="navBarCtrl.demographicSearch"
									juno-icon-left="true"
									juno-placeholder="<bean:message key="navbar.searchPatients" bundle="ui"/>"
							>
							</juno-patient-search-typeahead>
						</div>
						<div class="form-group">
							<button class="btn btn-icon btn-visible"
							        title="<bean:message key="navbar.searchPatients" bundle="ui"/>"
							        ng-click="navBarCtrl.onPatientSearch(null)">
								<i class="icon icon-user-search"></i>
							</button>
						</div>
						<div class="form-group">
							<button class="btn btn-icon btn-visible"
							        title="<bean:message key="navbar.newPatient" bundle="ui"/>"
							        ng-click="navBarCtrl.newDemographic()">
								<i class="icon icon-user-add"></i>
							</button>
						</div>
					</form>
				</div>

				<!-- Large view -->
				<ul class="nav navbar-nav breakpoint-lg-visible-exclusive" ng-cloak>
					<li ng-repeat="item in navBarCtrl.menuItems"
						ng-class="{'active': navBarCtrl.isActive(item) }">

						<a href="javascript:void(0)"
						   ng-if="!item.dropdown"
						   ng-click="navBarCtrl.transition(item)">{{item.label}}
							<span ng-if="navBarCtrl.getCountForLabel(item) > 0"
								  class="badge badge-danger">{{item.labelCount}}</span>
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
				<ul class="nav navbar-nav breakpoint-md-visible-exclusive" ng-cloak>
					<li ng-repeat="item in navBarCtrl.menuItems | filter: navBarCtrl.mediumNavItemFilter(false)"
						ng-class="{'active': navBarCtrl.isActive(item) }">

						<%--<a ng-click="navBarCtrl.transition(item)" data-toggle="tab" >{{item.label}}
							<span ng-if="item.extra.length>0">({{item.extra}})</span>
						</a>--%>

						<a href="javascript:void(0)"
						   ng-if="!item.dropdown"
						   ng-click="navBarCtrl.transition(item)">{{item.label}}
							<span ng-if="navBarCtrl.getCountForLabel(item) > 0"
								  class="badge badge-danger">{{item.labelCount}}</span>
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

				<!--Small View-->
				<ul class="nav navbar-nav breakpoint-sm-visible-exclusive" ng-cloak>
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
									<span ng-if="navBarCtrl.getCountForLabel(item) > 0"
										  class="badge badge-danger">{{item.labelCount}}</span>
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

				<!-- Mobile View -->
				<ul class="nav navbar-nav breakpoint-mb-visible-exclusive" ng-cloak>
					<li ng-repeat="item in navBarCtrl.menuItems | filter: navBarCtrl.mobileNavItemFilter(false)"
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
							<li ng-repeat="item in navBarCtrl.menuItems | filter: navBarCtrl.mobileNavItemFilter(true)"
							    ng-class="{'active': navBarCtrl.isActive(item) }">
								<a href="javascript:void(0)"
								   ng-if="!item.dropdown"
								   ng-click="navBarCtrl.transition(item)">{{item.label}}
									<span ng-if="navBarCtrl.getCountForLabel(item) > 0"
										  class="badge badge-danger">{{item.labelCount}}</span>
								</a>

								<a href="javascript:void(0)"
								   ng-if="item.dropdown"
								   ng-repeat="dropdownItem in item.dropdownItems"
								   ng-class="{'active': navBarCtrl.isActive(dropdownItem) }"
								   ng-click="navBarCtrl.transition(dropdownItem)">
									{{dropdownItem.label}}
								</a>
							</li>
						</ul>
					</li>
				</ul>

				<div class="vertical-align pull-right navbar-right-menu">
					<div class="nav navbar-text pull-right" ng-cloak>
						<div class="flex-row">
							<button class="btn btn-icon hand-hover"
							        title="<bean:message key="navbar.scratchpad" bundle="ui"/>"
							        ng-click="navBarCtrl.openScratchpad()">
								<i class="icon icon-write"></i>
							</button>

							<button class="btn btn-icon hand-hover"
							        title="<bean:message key="navbar.messenger" bundle="ui"/>"
							        ng-click="navBarCtrl.openMessenger()">
								<i class="icon icon-chat"></i>
								<span ng-show="navBarCtrl.unreadMessageTotal > 0"
								      class="badge badge-danger">{{navBarCtrl.unreadMessageTotal}}
								</span>
								<a ng-click="navBarCtrl.openMessenger(navBarCtrl.messengerMenu)"
								   title="{{navBarCtrl.messengerMenu.label}}"
								   class="hand-hover">{{navBarCtrl.messengerMenu.extra}}</a>
							</button>

							<div class="flex-row justify-content-center align-items-center">

								<%--<button class="btn btn-icon dropdown-toggle hand-hover"--%>
								      <%--data-toggle="dropdown"--%>
								      <%--title="<bean:message key="navbar.user" bundle="ui"/>">--%>
									<%--<i class="icon icon-user-md"></i>--%>
								<%--</button>--%>
								<%--<span></span>--%>
								<div class="dropdown-toggle hand-hover flex-row justify-content-center align-items-center"
								      data-toggle="dropdown"
								      title="<bean:message key="navbar.user" bundle="ui"/>">
									<i class="icon icon-user-md"></i>
									<span>
									{{navBarCtrl.me.firstName}}
									</span>
								</div>

								<ul class="dropdown-menu" role="menu">
									<li ng-repeat="item in navBarCtrl.userMenuItems">
										<a ng-click="navBarCtrl.transition(item)"
										   ng-class="{'more-tab-highlight': navBarCtrl.isActive(item) }"
										   class="hand-hover">{{item.label}}</a>
									</li>
								</ul>
								<%--<ul class="dropdown-menu" role="menu">--%>
									<%--<li ng-repeat="item in navBarCtrl.userMenuItems">--%>
										<%--<a ng-click="navBarCtrl.transition(item)"--%>
										   <%--ng-class="{'more-tab-highlight': navBarCtrl.isActive(item) }"--%>
										   <%--class="hand-hover">{{item.label}}</a>--%>
									<%--</li>--%>
								<%--</ul>--%>
							</div>

						</div>


						<%--<span>--%>
							<%--<a ng-click="navBarCtrl.openScratchpad()"--%>
							   <%--title="<bean:message key="navbar.scratchpad" bundle="ui"/>"--%>
							   <%--class="hand-hover">--%>
								<%--<span class="fa fa-pencil-square"></span>--%>
							<%--</a>--%>
						<%--</span>--%>
						<%--<span ng-show="navBarCtrl.messageRights === true">--%>
								<%--<a ng-click="navBarCtrl.openMessenger()"--%>
								   <%--title="<bean:message key="navbar.messenger" bundle="ui"/>"--%>
								   <%--class="hand-hover">--%>
									<%--<span class="fa fa-envelope"></span>--%>
									<%--<span ng-show="navBarCtrl.unreadMessageTotal > 0"--%>
									      <%--class="badge badge-danger">{{navBarCtrl.unreadMessageTotal}}--%>
									<%--</span>--%>
								<%--</a>--%>
								<%--<a ng-click="navBarCtrl.openMessenger(navBarCtrl.messengerMenu)"--%>
								   <%--title="{{navBarCtrl.messengerMenu.label}}"--%>
								   <%--class="hand-hover">{{navBarCtrl.messengerMenu.extra}}</a>--%>

								<%--<span ng-if="!$last"></span>--%>
						<%--</span>--%>
						<%--<span class="dropdown-toggle hand-hover"--%>
						      <%--data-toggle="dropdown"--%>
						      <%--title="<bean:message key="navbar.user" bundle="ui"/>">--%>
								<%--<span class="fa fa-user"></span>--%>
								<%--{{navBarCtrl.me.firstName}}--%>
							<%--</span>--%>
						<%--<ul class="dropdown-menu" role="menu">--%>
							<%--<li ng-repeat="item in navBarCtrl.userMenuItems">--%>
								<%--<a ng-click="navBarCtrl.transition(item)"--%>
								   <%--ng-class="{'more-tab-highlight': navBarCtrl.isActive(item) }"--%>
								   <%--class="hand-hover">{{item.label}}</a>--%>
							<%--</li>--%>
						<%--</ul>--%>
					</div>
				</div>
			</div>
			<!--/.nav-collapse -->
		</div>
	</nav>
</div>
<!-- nav bar is done here -->

<!-- main content pane -->
<div class="flex-column" id="index-content">
	<div class="flex-row flex-grow">
		<div id="content-left-pane"
		     ng-class="{
					'expanded': bodyCtrl.showPatientList,
					'collapsed': !bodyCtrl.showPatientList }"
		     ng-include="'src/layout/leftAside.jsp'">
		</div>
		<div id="content-center-pane"
		     class="flex-grow overflow-hidden"
		     ui-view
		     ng-cloak>
		</div>
		<div id="content-right-pane">
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
