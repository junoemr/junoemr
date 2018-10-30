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
<security:oscarSec roleName="<%=roleName$%>" objectName="_admin.fax" rights="r" reverse="<%=true%>">
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

<div class="fax-outbox">
	<div class="fax-outbox-header">
		<h1>Fax Outbox</h1>
	</div>
	<div class="fax-outbox-body">
		<div class="flex-row">
			<label class="flex-row-label" for="input-fax-outbox-select-account">Account</label>
			<select class="flex-row-content" id="input-fax-outbox-select-account"
			        ng-model="faxOutboxController.selectedFaxAccount"
			        ng-options="faxAccount.displayName for faxAccount in faxOutboxController.faxAccountList">
			</select>
		</div>

		<table ng-table="faxOutboxController.tableParams" show-filter="false" class="table table-striped table-bordered">
			<tbody>
				<tr ng-repeat="item in faxOutboxController.outboxItemList">
					<td data-title="'Date Sent'"    sortable="'DateSent'">{{item.systemDateSent}}</td>
					<td data-title="'Sent By'"      sortable="'SentBy'">{{item.providerNo}}</td>
					<td data-title="'Fax Type'"     sortable="'FaxType'">{{item.fileType}}</td>
					<td data-title="'Sent Status'"  sortable="'SentStatus'">{{item.systemStatus}}</td>
					<td data-title="'Sent To'"      sortable="'SentTo'">{{item.toFaxNumber}}</td>
					<td data-title="'Remote Date Queued'">{{item.integrationDateQueued}}</td>
					<td data-title="'Remote Date Sent'">{{item.integrationDateSent}}</td>
					<td data-title="'Remote Status'">{{item.integrationStatus}}</td>
				</tr>
			</tbody>
		</table>
	</div>
	<div class="fax-outbox-footer">
	</div>
</div>