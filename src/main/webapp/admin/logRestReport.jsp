<!DOCTYPE html> 
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
<%@ page errorPage="../errorpage.jsp"%>
<%@ page import="org.apache.commons.lang.StringUtils"%>
<%@ page import="org.oscarehr.log.dao.RestServiceLogDao"%>
<%@ page import="org.oscarehr.log.model.RestServiceLog, org.oscarehr.util.SpringUtils"%>
<%@ page import="java.util.List"%>
<%@ page import="oscar.OscarProperties" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>

<%
String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
String curUser_no = (String)session.getAttribute("user");
boolean authed=true;
%>

<security:oscarSec roleName="<%=roleName$%>" objectName="_admin,_admin.reporting" rights="r" reverse="<%=true%>">
	<%authed=false; %>
</security:oscarSec>
<%
	if(!authed || !OscarProperties.getInstance().isPropertyActive("admin.show_rest_log_report"))
	{
		response.sendRedirect("../securityError.jsp?type=_admin&type=_admin.reporting");
		return;
	}
	RestServiceLogDao serviceLogDao = SpringUtils.getBean(RestServiceLogDao.class);

	String pageStr = request.getParameter("page");
	String perPageStr = request.getParameter("perPage");

	int pageNo = 1;
	int perPage = 25;

	if(StringUtils.isNotBlank(pageStr))
	{
		pageNo = Integer.parseInt(pageStr);
	}
	if(StringUtils.isNotBlank(perPageStr))
	{
		perPage = Integer.parseInt(perPageStr);
	}
	if(pageNo < 1) pageNo = 1;
	int limit = Math.min(100, perPage);
	int offset = perPage * (pageNo - 1);

	List<RestServiceLog> logEntryList = serviceLogDao.findList(offset, limit);
	String tdTitleColor = "#CCCC99";
%>

<html:html locale="true">
	<head>
		<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery-1.9.1.min.js"></script>
		<script src="<%=request.getContextPath() %>/js/bootstrap.min.js"></script>

		<script type="text/javascript" src="<%=request.getContextPath() %>/js/bootstrap-datepicker.js"></script>

		<link href="<%=request.getContextPath() %>/css/bootstrap.min.css" rel="stylesheet">

		<link href="<%=request.getContextPath() %>/css/datepicker.css" rel="stylesheet" type="text/css">


		<link rel="stylesheet" href="<%=request.getContextPath() %>/css/font-awesome.min.css">
		<title>Rest Log Report</title>
		<style>

			label {
				margin-top: 6px;
				margin-bottom: 0px;
			}
		</style>

		<script>
			function onLoad()
			{
				$("#selectPerPage").val('<%=perPage%>');
			}
			function onSubmit()
			{

			}
		</script>

	</head>
	<body onload="onLoad();">
	<form name="myform" class="well form-horizontal" action="logRestReport.jsp" method="POST" onSubmit="return(onSubmit());">
		<fieldset>
			<h3>Rest Log Report</h3>
			<div class="span4">
				<label for="selectPerPage">Results Per Page:</label>
				<select id="selectPerPage" name="perPage">
					<option value="25">25</option>
					<option value="50">50</option>
					<option value="100">100</option>
				</select>
			</div>
			<div class="span8" style="padding-top:10px;">
				<input class="btn btn-primary" type="submit" name="submit" value="Run Report">
			</div>
		</fieldset>
	</form>

	<h4>Rest Log Report</h4>

	<table class="table table-bordered table-striped table-hover table-condensed">
		<tr bgcolor="<%=tdTitleColor%>">
			<TH>DateTime</TH>
			<TH>Duration (ms)</TH>
			<TH>Provider</TH>
			<TH>IP Address</TH>
			<TH>User Agent</TH>
			<TH>URL</TH>
			<TH>Request Media Type</TH>
			<TH>Method</TH>
			<TH>Request Query String</TH>
			<TH>Request Data</TH>
			<TH>Status Code</TH>
			<TH>Response Media Type</TH>
			<TH>Response Data</TH>
			<TH>Error Message</TH>
		</tr>
		<%
		for (RestServiceLog logEntry : logEntryList)
		{
		%>
		<tr align="center">
			<td><%=StringUtils.trimToEmpty(String.valueOf(logEntry.getCreatedAt()))%></td>
			<td><%=StringUtils.trimToEmpty(String.valueOf(logEntry.getDuration()))%></td>
			<td><%=StringUtils.trimToEmpty(logEntry.getProviderNo())%></td>
			<td><%=StringUtils.trimToEmpty(logEntry.getIp())%></td>
			<td><%=StringUtils.trimToEmpty(logEntry.getUserAgent())%></td>
			<td><%=StringUtils.trimToEmpty(logEntry.getUrl())%></td>
			<td><%=StringUtils.trimToEmpty(logEntry.getRequestMediaType())%></td>
			<td><%=StringUtils.trimToEmpty(logEntry.getMethod())%></td>
			<td><%=StringUtils.trimToEmpty(logEntry.getRawQueryString())%></td>
			<td><%=StringUtils.trimToEmpty(logEntry.getRawPost())%></td>
			<td><%=StringUtils.trimToEmpty(String.valueOf(logEntry.getStatusCode()))%></td>
			<td><%=StringUtils.trimToEmpty(logEntry.getResponseMediaType())%></td>
			<td><%=StringUtils.trimToEmpty(logEntry.getRawOutput())%></td>
			<td><%=StringUtils.trimToEmpty(logEntry.getErrorMessage())%></td>
		</tr>
		<%
		}
		%>
	</table>
	</body>
</html:html>
