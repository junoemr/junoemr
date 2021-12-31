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
<%@ page import="oscar.log.LogConst"%>
<%@ page import="oscar.util.ConversionUtils"%>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="static org.oscarehr.securityLog.LogReportAction.DEFAULT_PAGE_LIMIT" %>
<%@ page import="static org.oscarehr.securityLog.LogReportAction.DEFAULT_PAGE" %>
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
	String startDate = StringUtils.trimToEmpty(request.getParameter("startDate"));
	String endDate = StringUtils.trimToEmpty(request.getParameter("endDate"));
	String selectedProviderNo = StringUtils.trimToNull(request.getParameter("providerNo"));
	String selectedContentType = StringUtils.trimToEmpty(request.getParameter("contentType"));
	String selectedActionType = StringUtils.trimToEmpty(request.getParameter("actionType"));
	String selectedDemographicNo = StringUtils.trimToEmpty(request.getParameter("demographicNo"));
	String pageNoStr = StringUtils.trimToNull(request.getParameter("page"));
	String perPageStr = StringUtils.trimToNull(request.getParameter("perPage"));

	List<OscarLog> resultList = (List<OscarLog>) request.getAttribute("resultList");
	Integer totalResultCount = (Integer) request.getAttribute("total");

	totalResultCount = (totalResultCount != null)? totalResultCount : 0;
	boolean showAll = (selectedProviderNo == null);
	Integer pageNo = (StringUtils.isNumeric(pageNoStr))? Integer.parseInt(pageNoStr): DEFAULT_PAGE;
	Integer perPage = (StringUtils.isNumeric(pageNoStr))? Integer.parseInt(perPageStr): DEFAULT_PAGE_LIMIT;
	boolean disableLastPageBtn = (pageNo <= 1);
	boolean disableNextPageBtn = (pageNo * perPage > totalResultCount);

	ProviderDataDao providerDao = SpringUtils.getBean(ProviderDataDao.class);
	List<ProviderData> providerList;
	HashMap<String, String> providerNameMap = new HashMap<String, String>();
	HashMap<String, String> contentTypeMap = new LinkedHashMap<String, String>();
	HashMap<String, String> actionTypeMap = new LinkedHashMap<String, String>();
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

	/* Map the log content constants to visible labels */
	contentTypeMap.put(LogConst.CON_ADMIN, "Admin");
	contentTypeMap.put(LogConst.CON_ALLERGY, "Allergy");
	contentTypeMap.put(LogConst.CON_APPT, "Appointment");
	contentTypeMap.put(LogConst.CON_DEMOGRAPHIC, "Demographic");
	contentTypeMap.put(LogConst.CON_DOCUMENT, "Document");
	contentTypeMap.put(LogConst.CON_CME_NOTE, "Encounter Note");
	contentTypeMap.put(LogConst.CON_EFORM_DATA, "E-Form");
	contentTypeMap.put(LogConst.CON_EFORM_TEMPLATE, "E-Form Template");
	contentTypeMap.put(LogConst.CON_FAX, "Fax");
	contentTypeMap.put(LogConst.CON_FORM, "Form");
	contentTypeMap.put(LogConst.CON_HL7_LAB, "Lab");
	contentTypeMap.put(LogConst.CON_LOGIN, "Login");
	contentTypeMap.put(LogConst.CON_MEDICATION, "Medication");
	contentTypeMap.put(LogConst.CON_PHARMACY, "Pharmacy");
	contentTypeMap.put(LogConst.CON_PRESCRIPTION, "Prescription");
	contentTypeMap.put(LogConst.CON_SECURITY, "Security");
	contentTypeMap.put(LogConst.CON_SYSTEM, "System");

	/* Map the log action constants to visible labels */
	actionTypeMap.put(LogConst.ACTION_ACCESS, "Access");
	actionTypeMap.put(LogConst.ACTION_ADD, "Add");
	actionTypeMap.put(LogConst.ACTION_DELETE, "Delete");
	actionTypeMap.put(LogConst.ACTION_DOWNLOAD, "Download");
	actionTypeMap.put(LogConst.ACTION_EDIT, "Edit");
	actionTypeMap.put(LogConst.ACTION_READ, "Read");
	actionTypeMap.put(LogConst.ACTION_SENT, "Sent");
	actionTypeMap.put(LogConst.ACTION_UPDATE, "Update");

%>
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

			function changePage(pageNo)
			{
				document.securityLogSearchForm.page.value = pageNo;
				document.securityLogSearchForm.submit();
			}
			function nextPage()
			{
				changePage(Number(document.securityLogSearchForm.page.value) + 1);
			}
			function lastPage()
			{
				changePage(Number(document.securityLogSearchForm.page.value) - 1);
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
	<form name="securityLogSearchForm" id="securityLogSearchForm" class="well form-horizontal" action="LogReportAction.do" method="POST">
		<fieldset>
			<h3>Log Admin Report
				<small>Please select the provider, start and end dates.</small>
			</h3>
			<div class="span4">
				<label for="provider_select">Provider:</label>
				<select id="provider_select" name="providerNo">
					<option value="">All</option>
					<%
						for(ProviderData provider : providerList)
						{
							String providerId = provider.getId();
							String providerName = provider.getDisplayName();
							boolean selected = providerId.equals(selectedProviderNo);
					%>
					<option value="<%=providerId%>"<%=(selected ? "selected" : "")%>><%= providerName %></option>
					<%
						}
					%>
				</select>
			</div>
			<div class="span4">
				<label for="contentType">Content Type:</label>
				<select name="contentType" id="contentType">
					<option value="">All</option>
					<%
						for(Map.Entry<String, String> entry : contentTypeMap.entrySet())
						{
							String contentValue = entry.getKey();
							String displayName = entry.getValue();
							boolean selected = contentValue.equals(selectedContentType);
					%>
					<option value="<%=contentValue%>"<%=(selected ? "selected" : "")%>><%= displayName %></option>
					<%
						}
					%>
				</select>
			</div>
			<div class="span4">
				<label for="actionType">Action:</label>
				<select name="actionType" id="actionType">
					<option value="">All</option>
					<%
						for(Map.Entry<String, String> entry : actionTypeMap.entrySet())
						{
							String actionValue = entry.getKey();
							String displayName = entry.getValue();
							boolean selected = actionValue.equals(selectedActionType);
					%>
					<option value="<%=actionValue%>"<%=(selected ? "selected" : "")%>><%= displayName %></option>
					<%
						}
					%>
				</select>
			</div>
			<div class="span4">
				<label for="startDate">Start Date:</label>
				<div class="input-append">
					<input type="text" name="startDate" id="startDate" value="<%=startDate%>" placeholder="yyyy-mm-dd"
					       pattern="^\d{4}-((0\d)|(1[012]))-(([012]\d)|3[01])$" autocomplete="off"/>
					<span class="add-on"><i class="icon-calendar"></i></span>
				</div>
			</div>
			<div class="span4">
				<label for="endDate">End Date:</label>
				<div class="input-append">
					<input type="text" name="endDate" id="endDate" value="<%=endDate%>" placeholder="yyyy-mm-dd"
					       pattern="^\d{4}-((0\d)|(1[012]))-(([012]\d)|3[01])$" autocomplete="off"/>
					<span class="add-on"><i class="icon-calendar"></i></span>
				</div>
			</div>

			<div class="span4">
				<label for="demographicNo">Demographic ID:</label>
				<div class="input-append">
					<input type="text" name="demographicNo" id="demographicNo" value="<%=selectedDemographicNo%>"
					       pattern="^\d*$" autocomplete="off"/>
				</div>
			</div>

			<div class="span8" style="padding-top:10px;">
				<input class="btn btn-primary" type="button" name="submitBtn" value="Run Report" onClick="changePage(<%=DEFAULT_PAGE%>);">
			</div>
			<input type="hidden" name="restrictBySite" value="<%=isSiteAccessPrivacy%>">
			<input type="hidden" name="page" value="<%=pageNo%>">
			<input type="hidden" name="perPage" value="<%=perPage%>">
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
		<tr bgcolor="#CCCC99">
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
			<td><%= StringUtils.trimToEmpty(oscar.util.StringUtils.filterUnsafeString(logEntry.getData())) %>&nbsp;</td>
		</tr>
		<%
		}
		%>
	</table>
	<div class="pull-right" style="padding-top:10px;">
		<p>Displaying <%=((pageNo-1) * perPage + 1)%> - <%=((pageNo-1) * perPage) + resultList.size()%> of <%=totalResultCount%> Results</p>
		<button class="btn" onClick="lastPage();" <%=(disableLastPageBtn)? "disabled":""%>>Last Page</button>
		<span class="btn disabled"><%=pageNo%></span>
		<button class="btn" onClick="nextPage();" <%=(disableNextPageBtn)? "disabled":""%>>Next Page</button>
	</div>
	<%
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
