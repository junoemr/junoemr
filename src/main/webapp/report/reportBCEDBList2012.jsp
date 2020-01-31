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

<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
	String roleName = session.getAttribute("userrole") + "," + session.getAttribute("user");
%>

<security:oscarSec roleName="<%=roleName%>"
				   objectName="_report,_admin.reporting"
				   rights="r"
				   reverse="<%=true%>">
	<%
		response.sendRedirect("../securityError.jsp?type=_report&type=_admin.reporting");
	%>
</security:oscarSec>


<%@ page import="org.oscarehr.util.SpringUtils" %>
<%@ page import="java.util.List" %>
<%@ page import="org.oscarehr.forms.service.FormService" %>
<%@ page import="org.oscarehr.forms.transfer.FormBCAR2012Transfer" %>
<%@ page import="java.util.LinkedHashSet" %>
<%@ page import="java.util.Set" %>

<%
	int limit = 100;
	int offset = 0;
	String startDate = request.getParameter("startDate");
	String endDate = request.getParameter("endDate");

	if (request.getParameter("resultLimit") != null)
	{
		limit = Integer.parseInt(request.getParameter("resultLimit"));
	}
	else if (request.getParameter("limit") != null)
	{
		limit = Integer.parseInt(request.getParameter("limit"));
	}

	if (request.getParameter("offset") != null)
	{
		offset = Integer.parseInt(request.getParameter("offset"));
	}

	int nextOffset = offset + limit;
	int prevOffset = offset - limit;

	FormService formService = SpringUtils.getBean(FormService.class);
	List<FormBCAR2012Transfer> arEntries = formService.getBCAR2012(startDate, endDate, limit, offset);
	request.setAttribute("entries", arEntries);

	Set<Integer> limitOptions = new LinkedHashSet<Integer>();
	limitOptions.add(limit);
	limitOptions.add(100);
	limitOptions.add(250);
	limitOptions.add(500);
	limitOptions.add(1000);

	request.setAttribute("availableLimits", limitOptions);
%>

<html:html locale="true">
	<head>
		<title>
			<bean:message key="report.reportnewdblist.title" />
		</title>

		<link rel="stylesheet" href="../css/receptionistapptstyle.css">
		<link rel="stylesheet" href="../css/reportBCAR.css">

		<script type="text/javascript">
			function onResultLimitChange()
			{
				var resultLimit = document.getElementById("resultLimit");
				var startDate = '<%=startDate%>';
				var endDate = '<%=endDate%>';
				var limit = resultLimit.options[resultLimit.selectedIndex].value;
				var offset = '<%=offset%>';
				document.location.href = 'reportBCEDBList2012.jsp?startDate=' + startDate +
					'&endDate=' + endDate +
					'&limit=' + limit +
					'&offset=' + offset;
			}
		</script>

	</head>
	<body class="edd-body">
	<table class="edd-table">
		<tr class="edd-table-header">
			<th colspan="2">
				<bean:message key="report.reportnewdblist.msgEDDList" />
			</th>
		</tr>
		<tr class="page-limiter-controls">
			<td>
				<div class="pageLimiter">
					<label for="resultLimit">
						Result Limit
					</label>
					<select name="resultLimit" id="resultLimit" onchange="onResultLimitChange()">
						<c:forEach items="${availableLimits}" var="limit">
							<option>${limit}</option>
						</c:forEach>
					</select>
				</div>
			</td>
			<td class="control-align">
				<input type="button"
					   name="buttonPrint"
					   value="<bean:message key="global.btnPrint"/>"
					   onclick="window.print()">
				<input type="button"
					   name="buttonCancel"
					   value="<bean:message key="global.btnCancel"/>"
					   onclick="window.close()">
			</td>
		</tr>
	</table>

	<table class="edd-table main-results-table">
		<tr class="edd-row result-table-header">
			<th>
				<bean:message key="report.reportnewdblist.msgEDD" />
			</th>
			<th>
				<bean:message key="report.reportnewdblist.msgName" />
			</th>
			<th>
				<bean:message key="report.reportnewdblist.msgDOB" />
			</th>
			<th>
				G<span class="minimized-header">ravida</span>
			</th>
			<th>
				<bean:message key="report.reportnewdblist.msgTerm" />
			</th>
			<th>
				<bean:message key="report.reportnewdblist.msgPhone" />
			</th>
			<th>
				<bean:message key="report.reportnewdblist.msLanguage" />
			</th>
			<th>
				<bean:message key="report.reportnewdblist.msPHN" />
			</th>
			<th>
				Doula
			</th>
			<th>
				Doula#
			</th>
		</tr>
		<c:forEach items="${entries}" var="formEntry">
			<tr class="edd-row result-row">
				<td>${formEntry.eddAsString}</td>
				<td>${formEntry.fullName}</td>
				<td>${formEntry.dateOfBirthAsString}</td>
				<td>${formEntry.gravida}</td>
				<td>${formEntry.term}</td>
				<td>${formEntry.phone}</td>
				<td>${formEntry.langPreferred}</td>
				<td>${formEntry.phn}</td>
				<td>${formEntry.doula}</td>
				<td>${formEntry.doulaNo}</td>
			</tr>
		</c:forEach>
	</table>

	<%
		if (prevOffset >= 0)
		{
	%>
	<a href="reportBCEDBList2012.jsp?startDate=<%=startDate%>&endDate=<%=endDate%>&limit=<%=limit%>&offset=<%=prevOffset%>">
		<bean:message key="report.reportnewdblist.msgLastPage" />
	</a>
	<%
		}

		if (arEntries.size() == limit)
		{
	%>
	<a href="reportBCEDBList2012.jsp?startDate=<%=startDate%>&endDate=<%=endDate%>&limit=<%=limit%>&offset=<%=nextOffset%>">
		<bean:message key="report.reportnewdblist.msgNextPage" />
	</a>
	<%
		}
	%>

	</body>
</html:html>

