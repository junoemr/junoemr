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
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%
    String roleName2$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
    boolean authed2=true;
%>
<security:oscarSec roleName="<%=roleName2$%>" objectName="_report,_admin.reporting,_admin" rights="w" reverse="<%=true%>">
	<%authed2=false; %>
	<%response.sendRedirect(request.getContextPath() + "/securityError.jsp?type=_admin.reporting");%>
</security:oscarSec>
<%
	if(!authed2) {
		return;
	}
%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>

<html lang="en" ng-app="oscarProviderViewModule">
<head></head>
<body ng-controller="Admin.Section.Know2act.k2aTemplateController as k2aTemplateCtrl">

<!--
<link href="<%=request.getContextPath() %>/library/bootstrap/3.0.0/css/bootstrap.css" rel="stylesheet">
<link rel="stylesheet" href="<%=request.getContextPath() %>/web/admin/section/know2act/Know2actHideNavBars.css">
-->

<div class="k2a-report-template">
	<div data-ng-hide="k2aTemplateCtrl.k2aActive">
		A K2A instance is unavailable for this OSCAR instance.
		Please authenticate a K2A instance or contact an administrator for support.
	</div>
	<div data-ng-show="k2aTemplateCtrl.k2aActive && !k2aTemplateCtrl.k2aReports">
		Please authenticate with K2A using your username and password under the user settings integration tab.
		Once completed please refresh the page.
	</div>
	<div data-ng-show="k2aTemplateCtrl.k2aActive && k2aTemplateCtrl.k2aReports">
		<div>
			<h5>{{k2aTemplateCtrl.message}} {{k2aTemplateCtrl.K2A_URL}}</h5>
		</div>
		<h4><bean:message key="oscarReport.oscarReportByTemplate.msgDownloadFromK2A"/></h4>
		<input type="button" value="<bean:message key="oscarReport.oscarReportByTemplate.msgK2ABrowse" />"
		       class="btn btn-primary upload"
		       ng-click="k2aTemplateCtrl.openK2AUrl()"/>
		<input type="button" value="<bean:message key="oscarReport.oscarReportByTemplate.msgRefresh" />"
		       class="btn btn-primary upload"
		       onclick="location.reload();"/>
		<br/>
		<table class="table table-condensed table-striped" id="k2aReportTbl" datatable="ng" dt-options="k2aTemplateCtrl.dtOptions">
			<thead>
			<tr>
				<th>&nbsp;</th>
				<th><bean:message key="oscarReport.oscarReportByTemplate.msgName"/></th>
				<th><bean:message key="oscarReport.oscarReportByTemplate.msgAuthor"/></th>
				<th><bean:message key="oscarReport.oscarReportByTemplate.msgCreated"/></th>
			</tr>
			</thead>

			<tbody>
			<tr ng-repeat-start="k2aReport in k2aTemplateCtrl.k2aReports">
				<td valign="middle">
					<button ng-if="!k2aReport.postVersions" ng-click="k2aTemplateCtrl.saveK2AReport(k2aReport.id)" title="<bean:message key="oscarReport.oscarReportByTemplate.msgDownload"/>">
						<span class="icon-download-alt italic"></span>
					</button>
				</td>
				<td>{{k2aReport.name}}
					<a ng-if="k2aReport.postVersions" data-toggle="collapse" data-target="#k2aReport{{k2aReport.id}}" class="accordion-toggle">+</a>
				</td>
				<td>{{k2aReport.author}}</td>
				<td>{{k2aReport.createdAt | date:'yyyy-MM-dd HH:mm:ss'}}</td>
			</tr>
			<tr ng-repeat-end ng-if="k2aReport.postVersions">
				<td colspan="12" class="no-pad">
					<div class="accordian-body collapse" id="k2aReport{{k2aReport.id}}">
						<table class="table table-condensed table-striped">
							<thead>
							<tr>
								<th>&nbsp;</th>
								<th><bean:message key="oscarReport.oscarReportByTemplate.msgName"/></th>
								<th><bean:message key="oscarReport.oscarReportByTemplate.msgAuthor"/></th>
								<th><bean:message key="oscarReport.oscarReportByTemplate.msgUpdated"/></th>
							</tr>
							</thead>
							<tr ng-repeat="k2aReportVersion in k2aReport.postVersions">
								<td valign="middle">
									<button ng-click="k2aTemplateCtrl.saveK2AReport(k2aReportVersion.id)" title="<bean:message key="oscarReport.oscarReportByTemplate.msgDownload"/>">
										<span class="icon-download-alt italic"></span></button>
								</td>
								<td>{{k2aReportVersion.name}}</td>
								<td>{{k2aReportVersion.author}}</td>
								<td>{{k2aReportVersion.updatedAt | date:'yyyy-MM-dd HH:mm:ss'}}</td>
							</tr>
						</table>
					</div>
				</td>
			</tr>
			</tbody>
		</table>
	</div>
</div>
</body>
</html>
