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

<html ng-app="k2aReportByTemplate">

<head>
<link href="<%=request.getContextPath()%>/css/bootstrap.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-1.9.1.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/library/bootstrap/3.0.0/js//bootstrap.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/library/angular.min.js"></script>
<script src="<%=request.getContextPath()%>/web/common/reportByTemplateServices.js"></script>
</head>

<style>
body{background-color:#f5f5f5;}
</style>

<body>
	<div ng-controller="k2aReportByTemplate">
		<div data-ng-hide="k2aActive">
			A K2A instance is unavailable for this OSCAR instance. Please authenticate a K2A instance or contact an administrator for support.
		</div>
		<div data-ng-show="k2aActive && !k2aReports">
			Please authenticate with K2A using your username and password <a href="" onclick="window.open('<%=request.getContextPath()%>/web/#/settings');">here</a> under the integration tab. Once completed please refresh the page.
		</div>
		<div data-ng-show="k2aActive && k2aReports">
			<div>
				<h5>{{message}}</h5>
			</div>
			<h4><bean:message key="oscarReport.oscarReportByTemplate.msgDownloadFromK2A" /></h4>
			<input type="button" value="<bean:message key="oscarReport.oscarReportByTemplate.msgK2ABrowse" />" class="btn btn-primary upload" onclick="window.open('https://www.know2act.org/#/ws/rs/posts/browse/Report');" />
			<input type="button" value="<bean:message key="oscarReport.oscarReportByTemplate.msgRefresh" />" class="btn btn-primary upload" onclick="location.reload();" /><br />
			<table class="table table-condensed table-striped" id="k2aReportTbl">
				<thead>
			    	<tr >
			        	<th><bean:message key="oscarReport.oscarReportByTemplate.msgName" /></th>
			            <th><bean:message key="oscarReport.oscarReportByTemplate.msgAuthor" /></th>
			            <th><bean:message key="oscarReport.oscarReportByTemplate.msgCreated" /></th>
			            <th><bean:message key="oscarReport.oscarReportByTemplate.msgDownload" /><th/>
			        </tr>
			   	</thead>
			
			    <tbody>
			    	<tr ng-repeat-start="k2aReport in k2aReports">
			        	<td>{{k2aReport.name}} <a ng-if="k2aReport.postVersions" data-toggle="collapse" data-target="#k2aReport{{k2aReport.id}}" class="accordion-toggle">+</a></td>
			            <td>{{k2aReport.author}}</td>
			            <td>{{k2aReport.createdAt | date:'yyyy-MM-dd HH:mm:ss'}}</td>
			            <td valign="middle">
			            	<button ng-if="!k2aReport.postVersions" ng-click="saveK2AReport(k2aReport.id)"><i class="icon-download-alt" title="<bean:message key="oscarReport.oscarReportByTemplate.msgDownload"/>"></i></button>
			            </td>
			        </tr>
			        <tr ng-repeat-end ng-if="k2aReport.postVersions">
			        	<td colspan="12" style="padding:0px">
			        		<div class="accordian-body collapse" id="k2aReport{{k2aReport.id}}">
			        			<table class="table table-condensed table-striped">
			        				<thead>
								    	<tr >
								        	<th><bean:message key="oscarReport.oscarReportByTemplate.msgName" /></th>
								            <th><bean:message key="oscarReport.oscarReportByTemplate.msgAuthor" /></th>
								            <th><bean:message key="oscarReport.oscarReportByTemplate.msgUpdated" /></th>
								            <th><bean:message key="oscarReport.oscarReportByTemplate.msgDownload" /><th/>
								        </tr>
								   	</thead>
			        				<tr ng-repeat="k2aReportVersion in k2aReport.postVersions">
					        			<td>{{k2aReportVersion.name}}</td>
							            <td>{{k2aReportVersion.author}}</td>
							            <td>{{k2aReportVersion.updatedAt | date:'yyyy-MM-dd HH:mm:ss'}}</td>
							            <td valign="middle">
							            	<button ng-click="saveK2AReport(k2aReportVersion.id)"><i class="icon-download-alt" title="<bean:message key="oscarReport.oscarReportByTemplate.msgDownload"/>"></i></button>
							            </td>
							        </tr>
							    </table>
			        		</div>
			        	</td>
			        </tr>
			    </tbody>
			</table>
		</div>
		<script>
			var app = angular.module("k2aReportByTemplate", ['reportByTemplateServices']);
			
			app.controller("k2aReportByTemplate", function($scope,reportByTemplateService) {
				message = "";
				
				checkStatus = function(){
				    reportByTemplateService.isK2AInit().then(function(data){
				    	console.log("data coming back",data);
				    	$scope.k2aActive = data.success;
				    	console.log($scope.k2aActive );
					});
				}
			    checkStatus();
			    
			    getAllK2AReports = function(){
			    	reportByTemplateService.getAllK2AReports().then(function(data){
			    		console.log("data coming back",data);
			    		$scope.k2aReports = data;
			    		console.log($scope.k2aReports);
			    	});
			    }
			    getAllK2AReports();
			    
			    $scope.saveK2AReport = function(id){
			    	reportByTemplateService.getK2AReportById(id).then(function(data){
			    		$scope.message = data;
			    		console.log($scope.message);
			    		refreshParent();
			    	});
			    }   
			    
			    window.onunload = refreshParent;
				function refreshParent() {
					window.opener.document.location.href = "<%=request.getContextPath()%>/oscarReport/reportByTemplate/homePage.jsp";
				}
			});
		
		</script>
	</div>
</body>
</html>