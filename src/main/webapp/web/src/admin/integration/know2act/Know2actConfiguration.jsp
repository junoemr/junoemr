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
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/WEB-INF/security.tld" prefix="security" %>
<%
	String roleName$ = (String) session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
	boolean authed = true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_admin,_admin.misc" rights="r" reverse="<%=true%>">
	<%authed = false; %>
	<%response.sendRedirect("../securityError.jsp?type=_admin&type=_admin.misc");%>
</security:oscarSec>
<%
	if (!authed) {
		return;
	}
%>


<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link href="<%=request.getContextPath() %>/library/bootstrap/3.0.0/css/bootstrap.css" rel="stylesheet">
<link rel="stylesheet" href="<%=request.getContextPath() %>/css/font-awesome.min.css">
<!-- Hide the main program nav as a fix for having angular in an iframe -->
<link rel="stylesheet" href="<%=request.getContextPath() %>/web/admin/integration/know2act/Know2actHideNavBars.css">

<title><bean:message key="admin.admin.Know2ActConfig"/></title>

<div class="k2a-config">
	<div class="page-header">
		<h4><bean:message key="admin.admin.Know2ActConfig"/>
			<span class="small" data-ng-show="k2aConfigCtrl.k2aActive"><bean:message key="admin.k2a.active"/></span>
		</h4>
	</div>
	<div data-ng-show="k2aConfigCtrl.k2aActive">
		<h4><bean:message key="admin.k2a.preventionsListTitle"/>
			<span class="small">{{k2aConfigCtrl.currentPreventionRulesSet}}</span>
		</h4>
		<table class="table table-bordered table-condensed">
			<tr>
				<th><bean:message key="admin.k2a.table.filename"/></th>
				<th><bean:message key="admin.k2a.table.dateCreated"/></th>
				<th><bean:message key="admin.k2a.table.createdBy"/></th>
				<th>&nbsp;</th>
			</tr>
			<tr data-ng-repeat="preventionRuleSet in k2aConfigCtrl.availablePreventionRuleSets | limitTo:k2aConfigCtrl.PrevListQuantity">
				<td>{{preventionRuleSet.name}}</td>
				<td>{{preventionRuleSet.created_at}}</td>
				<td>{{preventionRuleSet.author}}</td>
				<td>
					<button class="btn btn-default btn-sm" ng-click="k2aConfigCtrl.loadPreventionRuleById(preventionRuleSet)"><bean:message key="admin.k2a.load"/></button>
				</td>
			</tr>
		</table>
		<button class="btn btn-default btn-sm pull-right" ng-click="k2aConfigCtrl.increasePrevListQuantity()"><bean:message key="admin.k2a.loadMore"/></button>

	</div>
	<div data-ng-hide="k2aConfigCtrl.k2aActive">
		<form action="Know2actConfiguration.jsp" method="POST">
			<fieldset>
				<div class="form-group col-xs-5">
					<label><bean:message key="admin.k2a.clinicName"/>
						<span class="small">(<bean:message key="admin.k2a.clinicName.reason"/>)</span>
					</label>
					<div class="controls">
						<input class="form-control" name="clinicName" ng-model="k2aConfigCtrl.clinicName" type="text" maxlength="255"/>
					</div>
					<input type="button" class="btn btn-primary" ng-disabled="k2aConfigCtrl.clinicName==null || k2aConfigCtrl.clinicName==''"
					       value="<bean:message key="admin.k2a.initbtn"/>"
					       ng-click="k2aConfigCtrl.initK2A()"/>
				</div>
			</fieldset>
		</form>
	</div>
</div>
