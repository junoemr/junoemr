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

<link href="<%=request.getContextPath() %>/library/bootstrap/3.0.0/css/bootstrap.css" rel="stylesheet">
<link rel="stylesheet" href="<%=request.getContextPath() %>/css/font-awesome.min.css">
<!-- Hide the main program nav as a fix for having angular in an iframe -->
<link rel="stylesheet" href="<%=request.getContextPath() %>/web/admin/integration/know2act/Know2actHideNavBars.css">

<title>FAX Title</title>

<div class="fax-config">
	<div ng-repeat="faxAccount in faxController.faxAccountList">
		<div>
			<label class="switch">
				<input id="input-fax-enabled-{{faxAccount.id}}" type="checkbox"
				       ng-model="faxAccount.enabled"/>
				<span class="slider"></span>
			</label>
			<label for="input-fax-enabled-{{faxAccount.id}}">ENABLED</label>
		</div>
		<div>
			<div>
				<label class="input-label" for="input-fax-account-id-{{faxAccount.id}}">User Number</label>
				<input id="input-fax-account-id-{{faxAccount.id}}" type="text"
				       ng-model="faxAccount.accountLogin">
				<label for="input-fax-account-pw-{{faxAccount.id}}">Password</label>
				<input id="input-fax-account-pw-{{faxAccount.id}}" type="password"
				       ng-model="faxAccount.password">
			</div>
			<div>
				<label class="input-label">Connection Status</label>
				<button type="button" class="btn"
				        ng-click="faxController.testConnection(faxAccount)">
					Test Connection
				</button>
				<div style="display: inline-block;">
					<div class="connection-status-indicator"
					      ng-show="faxAccount.connectionStatus == faxController.connectionStatusEnum.unknown">
						<span class="glyphicon">Unknown</span>
					</div>
					<div class="connection-status-indicator success"
					      ng-show="faxAccount.connectionStatus == faxController.connectionStatusEnum.success">
						<span class="glyphicon">Success</span>
					</div>
					<div class="connection-status-indicator failure"
					      ng-show="faxAccount.connectionStatus == faxController.connectionStatusEnum.failure">
						<span class="glyphicon">Failure</span>
					</div>
				</div>
			</div>
			<div>
				<button type="button" class="btn btn-success"
				        ng-click="faxController.saveSettings(faxAccount)">
					SAVE SETTINGS
				</button>
			</div>
		</div>
	</div>
	<button type="button" class="btn btn-success"
	        ng-click="faxController.addNewAccount()"
	        ng-show="faxController.faxAccountList.length == 0">
		Add New Account
	</button>
</div>



