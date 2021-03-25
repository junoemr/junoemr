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
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/WEB-INF/security.tld" prefix="security" %>
<%
	String roleName$ = (String) session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
	boolean authorized = true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_admin" rights="r" reverse="<%=true%>">
	<%authorized = false; %>
	<%response.sendRedirect("../securityError.jsp?type=_admin");%>
</security:oscarSec>
<%
	if (!authorized) {
		return;
	}
%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html lang="en" ng-app="oscarProviderViewModule">
<head></head>
<body ng-controller="Admin.Section.Fax.FaxConfigurationController as faxController">

<!--
<link href="<%=request.getContextPath() %>/library/bootstrap/3.0.0/css/bootstrap.css" rel="stylesheet">
<link rel="stylesheet" href="<%=request.getContextPath() %>/css/font-awesome.min.css">
-->
<!-- Hide the main program nav as a fix for having angular in an iframe -->
<!--
<link rel="stylesheet" href="<%=request.getContextPath() %>/web/admin/section/know2act/Know2actHideNavBars.css">
-->

<title><bean:message bundle="ui" key="admin.fax.acct.window-title"/></title>

<div class="fax-config">
	<div class="fax-config-header">
		<h1><bean:message bundle="ui" key="admin.fax.acct.header"/></h1>

		<span ng-show="!faxController.loggedInProvider.superAdmin && faxController.masterFaxDisabled">
			<bean:message bundle="ui" key="admin.fax.acct.accessDisabledMessage"/>
		</span>
		<div ng-show="faxController.loggedInProvider.superAdmin">
			<!-- let super admin enable/disable faxing -->
			<div>
				<div>
					<label class="switch">
						<input id="input-master-fax-integration-enabled-inbound" type="checkbox"
						       ng-model="faxController.masterFaxEnabledInbound"
						       ng-change="faxController.saveMasterFaxEnabledStateInbound();"/>
						<span class="slider"></span>
					</label>
					<label for="input-master-fax-integration-enabled-inbound"><bean:message bundle="ui" key="admin.fax.acct.masterFaxEnabledInbound"/></label>
				</div>
				<div>
					<label class="switch">
						<input id="input-master-fax-integration-enabled-outbound" type="checkbox"
						       ng-model="faxController.masterFaxEnabledOutbound"
						       ng-change="faxController.saveMasterFaxEnabledStateOutbound();"/>
						<span class="slider"></span>
					</label>
					<label for="input-master-fax-integration-enabled-outbound"><bean:message bundle="ui" key="admin.fax.acct.masterFaxEnabledOutbound"/></label>
				</div>
			</div>
		</div>
		<button type="button" class="btn btn-primary"
		        ng-show="faxController.faxAccountList.length == 0 && !faxController.masterFaxDisabled"
		        ng-click="faxController.editNewFaxAccount()">
			<bean:message bundle="ui" key="admin.fax.acct.btn-addNew"/>
		</button>
	</div>
	<div class="fax-config-body">
		<div class="account-list" ng-repeat="faxAccount in faxController.faxAccountList">
			<div class="account-item">
				<div>
					<span class="glyphicon enabled glyphicon-ok glyphicon-lrg" ng-show="faxAccount.enabled"
					      title="<bean:message bundle="ui" key="admin.fax.acct.acctEnabled"/>"></span>
					<span class="glyphicon disabled glyphicon-remove glyphicon-lrg" ng-hide="faxAccount.enabled"
					      title="<bean:message bundle="ui" key="admin.fax.acct.acctDisabled"/>"></span>
				</div>
				<div>
					<h5><bean:message bundle="ui" key="admin.fax.acct.inbound"/>:
						<span class="glyphicon enabled glyphicon-ok glyphicon-sml" ng-show="faxAccount.enableInbound"
						      title="<bean:message bundle="ui" key="admin.fax.acct.inboundEnabled"/>"></span>
						<span class="glyphicon disabled glyphicon-remove glyphicon-sml" ng-hide="faxAccount.enableInbound"
						      title="<bean:message bundle="ui" key="admin.fax.acct.inboundDisabled"/>"></span>
					</h5>
					<h5><bean:message bundle="ui" key="admin.fax.acct.outbound"/>:
						<span class="glyphicon enabled glyphicon-ok glyphicon-sml" ng-show="faxAccount.enableOutbound"
						      title="<bean:message bundle="ui" key="admin.fax.acct.outboundEnabled"/>"></span>
						<span class="glyphicon disabled glyphicon-remove glyphicon-sml" ng-hide="faxAccount.enableOutbound"
						      title="<bean:message bundle="ui" key="admin.fax.acct.outboundDisabled"/>"></span>
					</h5>
				</div>
				<div>
					<h4>{{faxAccount.displayName}}</h4>
				</div>
				<div>
					<h4>{{faxAccount.coverLetterOption}}</h4>
				</div>
				<button type="button" class="btn btn-default"
				        <%-- TODO-legacy disable button for non-admin users? --%>
				        ng-click="faxController.editFaxAccount(faxAccount)"
						ng-disabled="faxController.masterFaxDisabled">
					<bean:message bundle="ui" key="admin.fax.acct.btn-EditAccount"/>
				</button>
			</div>
		</div>
	</div>
	<div class="fax-config-footer">

	</div>
</div>


</body>
</html>

