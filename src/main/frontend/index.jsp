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

	<div ng-if="bodyCtrl.isInitialized()">
		<!-- main navigation bar -->
		<primary-navigation></primary-navigation>

		<!-- main content pane -->
		<div class="flex-column" id="index-content">
			<div class="flex-row flex-grow">
				<left-aside
						id="content-left-pane"
						class="no-print"
						ng-class="{
								'expanded': bodyCtrl.showPatientList,
								'collapsed': !bodyCtrl.showPatientList }"
						expand-on="bodyCtrl.showPatientList">
				</left-aside>
				<div id="content-center-pane"
						 class="flex-grow overflow-hidden"
						 ui-view
						 ng-cloak>
				</div>
				<div id="content-right-pane">
				</div>
			</div>
		</div>

		<!-- toast alerts -->
		<toast-area class="toast-alert-area">
		</toast-area>
	</div>
<script>

	// Juno POJO namespace
	window.Juno = {};

	Juno.contextPath = '<%= request.getContextPath() %>';

</script>
</body>
</html>
