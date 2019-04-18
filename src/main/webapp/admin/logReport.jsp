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
<%@ page import="org.oscarehr.common.model.OscarLog"%>
<%@ page import="org.oscarehr.provider.dao.ProviderDataDao"%>
<%@ page import="org.oscarehr.provider.model.ProviderData"%>
<%@ page import="org.oscarehr.util.SpringUtils"%>
<%@ page import="oscar.util.ConversionUtils"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>

<%
String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
String curUser_no = (String)session.getAttribute("user");
boolean isSiteAccessPrivacy=false;
boolean authed=true;
%>

<security:oscarSec roleName="<%=roleName$%>" objectName="_admin,_admin.reporting" rights="r" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect("../securityError.jsp?type=_admin&type=_admin.reporting");%>
</security:oscarSec>
<%
	if(!authed) {
		return;
	}
%>

<security:oscarSec objectName="_site_access_privacy" roleName="<%=roleName$%>" rights="r" reverse="false">
	<%isSiteAccessPrivacy=true; %>
</security:oscarSec>


<%
	String tdTitleColor = "#CCCC99";
	String tdSubtitleColor = "#CCFF99";
	String startDate = StringUtils.trimToEmpty(request.getParameter("startDate"));
	String endDate = StringUtils.trimToEmpty(request.getParameter("endDate"));
	String selectedProviderNo = StringUtils.trimToNull(request.getParameter("providerNo"));
	String selectedContentType = request.getParameter("contentType");
	boolean showAll = (selectedProviderNo == null);

	ProviderDataDao providerDao = SpringUtils.getBean(ProviderDataDao.class);
	List<ProviderData> providerList;

	List<OscarLog> resultList = (List<OscarLog>)request.getAttribute("resultList");
	HashMap<String, String> providerNameMap = new HashMap<String, String>();
	// select provider list
	if(isSiteAccessPrivacy)
	{
		providerList = providerDao.findByProviderSite(curUser_no);
	}
	else
	{
		providerList = providerDao.findAll();
	}

	if(startDate.isEmpty())
	{
		startDate = ConversionUtils.toDateString(LocalDate.now());
	}
	if(endDate.isEmpty())
	{
		endDate = ConversionUtils.toDateString(LocalDate.now());
	}

	/* put all the provider names in a map for easy lookup.
	These names should always match the result set providers due to the site filtering,
	so finding them outside of the result set is ok */
	for(ProviderData provider : providerList)
	{
		providerNameMap.put(provider.getId(), provider.getDisplayName());
	}

%>

<%@ page import="java.time.LocalDate"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.HashMap" %>
<html:html locale="true">
	<head>
		<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery-1.9.1.min.js"></script>
		<script src="<%=request.getContextPath() %>/js/bootstrap.min.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath() %>/js/bootstrap-datepicker.js"></script>

		<link href="<%=request.getContextPath() %>/css/bootstrap.min.css" rel="stylesheet">
		<link href="<%=request.getContextPath() %>/css/datepicker.css" rel="stylesheet" type="text/css">

		<link rel="stylesheet" href="<%=request.getContextPath() %>/css/font-awesome.min.css">
		<title>Log Report</title>
		<script language="JavaScript">
			function setfocus()
			{
				this.focus();
			}
		</script>
		<style>
			label {
				margin-top: 6px;
				margin-bottom: 0;
			}
		</style>

	</head>
	<body>
	<form name="myform" class="well form-horizontal" action="LogReportAction.do" method="POST">
		<fieldset>
			<h3>Log Admin Report
				<small>Please select the provider, start and end dates.</small>
			</h3>

			<div class="span4">
				<label for="provider_select">Provider: </label>
				<select id="provider_select" name="providerNo">
					<option value="">All</option>
					<%
						for(ProviderData provider : providerList)
						{
							String providerId = provider.getId();
							String providerName = provider.getDisplayName();
							boolean selected = providerId.equals(selectedProviderNo);
					%>
					<option value="<%=providerId%>"<%=(selected ? "selected" : "")%>><%= providerName %>
					</option>
					<%
						}
					%>
				</select>
			</div>

			<div class="span4">
				<label for="contentType">Content Type:</label>
				<select name="contentType" id="contentType">
					<option value="">All</option>
					<option value="demographic">Demographic</option>
					<option value="document">Document</option>
					<option value="eform_data">EForm</option>
					<option value="eform_template">EForm Template</option>
					<option value="fax">Fax</option>
					<option value="cme_notes">Encounter Note</option>
					<option value="login">Log in</option>
				</select>
			</div>

			<div class="span4">
				<label for="startDate">Start Date: </label>
				<div class="input-append">
					<input type="text" name="startDate" id="startDate" value="<%=startDate%>" placeholder="yyyy-mm-dd"
					       pattern="^\d{4}-((0\d)|(1[012]))-(([012]\d)|3[01])$" autocomplete="off"/>
					<span class="add-on"><i class="icon-calendar"></i></span>
				</div>
			</div>

			<div class="span4">
				<label for="endDate">End Date: </label>
				<div class="input-append">
					<input type="text" name="endDate" id="endDate" value="<%=endDate%>" placeholder="yyyy-mm-dd"
					       pattern="^\d{4}-((0\d)|(1[012]))-(([012]\d)|3[01])$" autocomplete="off"/>
					<span class="add-on"><i class="icon-calendar"></i></span>
				</div>
			</div>

			<div class="span8" style="padding-top:10px;">
				<input class="btn btn-primary" type="submit" name="submit" value="Run Report">
			</div>
			<input type="hidden" name="restrictBySite" value="<%=isSiteAccessPrivacy%>">
		</fieldset>
	</form>
<%
	if(resultList != null)
	{
%>
	<h4><%=(selectedProviderNo == null)? "All" : providerNameMap.get(selectedProviderNo)%> - Log Report</h4>

	<button class="btn pull-right" onClick="window.print()" style="margin-bottom:4px">
		<i class="icon-print"></i> Print
	</button>


	<p>Period: ( <%=startDate%> ~ <%=endDate%>)</p>
	<table class="table table-bordered table-striped table-hover table-condensed">
		<tr bgcolor="<%=tdTitleColor%>">
			<TH>Time</TH>
			<% if(showAll)
			{ %>
			<TH>Provider</TH>
			<% } %>
			<TH>Action</TH>
			<TH>Content</TH>
			<TH>Status</TH>
			<TH>Keyword</TH>
			<TH>IP</TH>
			<TH>Demo</TH>
			<TH>Data</TH>
		</tr>
		<%
		for(OscarLog logEntry : resultList)
		{
		%>
		<tr align="center">
			<td><%=ConversionUtils.toTimestampString(logEntry.getCreated())%>&nbsp;</td>
			<% if(showAll)
			{ %>
			<td><%=StringUtils.trimToEmpty(providerNameMap.get(logEntry.getProviderNo()))%>&nbsp;</td>
			<% } %>
			<td><%=StringUtils.trimToEmpty(logEntry.getAction())%>&nbsp;</td>
			<td><%=StringUtils.trimToEmpty(logEntry.getContent())%>&nbsp;</td>
			<td><%=StringUtils.trimToEmpty(logEntry.getStatus())%>&nbsp;</td>
			<td><%=StringUtils.trimToEmpty(logEntry.getContentId())%>&nbsp;</td>
			<td><%=StringUtils.trimToEmpty(logEntry.getIp())%>&nbsp;</td>
			<td><%=(logEntry.getDemographicId() != null) ? logEntry.getDemographicId() : ""%>&nbsp;</td>
			<td><%=StringUtils.trimToEmpty(logEntry.getData())%>&nbsp;</td>
		</tr>
		<%
		}
	}
	%>
		<script type="text/javascript">
			var startDate = $("#startDate").datepicker({
				format: "yyyy-mm-dd"
			});

			var endDate = $("#endDate").datepicker({
				format: "yyyy-mm-dd"
			});
		</script>
	</body>
</html:html>
