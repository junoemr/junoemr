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

<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>

<%@ page import="org.oscarehr.util.SpringUtils" %>
<%@ page import="java.util.List" %>
<%@ page import="org.oscarehr.demographic.service.DemographicService" %>
<%@ page import="org.oscarehr.demographic.search.DemographicCriteriaSearch" %>
<%@ page import="org.oscarehr.demographic.dao.DemographicDao" %>
<%@ page import="org.oscarehr.demographic.entity.Demographic" %>
<%@ page import="java.time.temporal.ChronoUnit" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="oscar.util.ConversionUtils" %>
<jsp:useBean id="reportMainBean" class="oscar.AppointmentMainBean"
			 scope="session" />
<jsp:useBean id="providerNameBean" class="oscar.Dict" scope="page" />
<%  if(!reportMainBean.getBDoConfigure()) { %>
<%@ include file="reportMainBeanConn.jspf"%>
<% } %>
<%
      String roleName2$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
      boolean authed2=true;
%>
<security:oscarSec roleName="<%=roleName2$%>" objectName="_report,_admin.reporting" rights="r" reverse="<%=true%>">
	<%authed2=false; %>
	<%response.sendRedirect("../securityError.jsp?type=_report&type=_admin.reporting");%>
</security:oscarSec>
<%
if(!authed2) {
	return;
}
%>

<%
	int offset = 0;
	int limit = 50;
	if(request.getParameter("offset") != null)
	{
		offset = Integer.parseInt(request.getParameter("offset"));
	}
	if(request.getParameter("limit") != null)
	{
		limit = Integer.parseInt(request.getParameter("limit"));
	}

	DemographicService demographicService = (DemographicService)SpringUtils.getBean("demographic.service.DemographicService");
	DemographicDao demographicDao = (DemographicDao)SpringUtils.getBean("demographic.dao.DemographicDao");
	DemographicService.STATUS_MODE statusMode = DemographicService.STATUS_MODE.active;
	DemographicCriteriaSearch.SORT_MODE sortMode = DemographicCriteriaSearch.SORT_MODE.DemographicLastName;
	DemographicService.SEARCH_MODE searchMode = DemographicService.SEARCH_MODE.demographicNo;
	DemographicCriteriaSearch demoSearch = demographicService.buildDemographicSearch("", searchMode, statusMode, sortMode);
	demoSearch.setLimit(limit);
	demoSearch.setOffset(offset);

	List<Demographic> demographics = demographicDao.criteriaSearch(demoSearch);

%>
<html:html locale="true">
<head>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
<title><bean:message key="report.reportactivepatientlist.title" />
</title>
<link rel="stylesheet" href="../css/receptionistapptstyle.css">
</head>
<body onLoad="setfocus()" topmargin="0" leftmargin="0" rightmargin="0">

<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr bgcolor="#486ebd">
		<th align=CENTER><font face="Helvetica" color="#FFFFFF"><bean:message
			key="report.reportactivepatientlist.msgTitle" /></font></th>
		<th align="right" width="10%" NOWRAP><input type="button"
			name="Button" value="<bean:message key="global.btnPrint" />"
			onClick="window.print()"> <input type="button" name="Button"
			value="<bean:message key="global.btnCancel" />"
			onClick="window.close()"></th>
	</tr>
</table>

<CENTER>
<table width="100%" border="1" bgcolor="#ffffff" cellspacing="0"
	cellpadding="1">
	<tr bgcolor="silver">
		<TH align="center" width="12%" nowrap><b><bean:message
			key="report.reportactivepatientlist.msgLastName" /></b></TH>
		<TH align="center" width="12%"><b><bean:message
			key="report.reportactivepatientlist.msgFirstName" /> </b></TH>
		<TH align="center" width="5%"><b><bean:message
			key="report.reportactivepatientlist.msgChart" /></b></TH>
		<TH align="center" width="5%"><b><bean:message
			key="report.reportactivepatientlist.msgAge" /></b></TH>
		<TH align="center" width="5%"><b><bean:message
			key="report.reportactivepatientlist.msgSex" /></b></TH>
		<TH align="center" width="10%"><b><bean:message
			key="report.reportactivepatientlist.msgHIN" /></b></TH>
		<TH align="center" width="5%"><b><bean:message
			key="report.reportactivepatientlist.msgVer" /></b></TH>
		<TH align="center" width="16%"><b><bean:message
			key="report.reportactivepatientlist.msgMCDoc" /></b></TH>
		<TH align="center" width="10%"><b><bean:message
			key="report.reportactivepatientlist.msgDateJoined" /></b></TH>
		<TH align="center" width="15%"><b><bean:message
			key="report.reportactivepatientlist.msgPhone" /></b></TH>
	</tr>
	<%
		boolean isOddRow = false;

		for (Demographic demographic : demographics)
		{
			isOddRow = !isOddRow;
			long age = ChronoUnit.YEARS.between(demographic.getDateOfBirth(), LocalDate.now());

			// Someone will inevitably complain about fields showing up as "null"
			String chartNo = demographic.getChartNo() != null ? demographic.getChartNo() : "";
			String hin = demographic.getHin() != null ? demographic.getHin() : "";
			String ver = demographic.getVer() != null ? demographic.getVer() : "";
			String mrp = demographic.getProviderNo() != null ? demographic.getProviderNo() : "";
			String dateJoined = "";
			if (demographic.getDateJoined() != null)
			{
				dateJoined = ConversionUtils.toDateString(demographic.getDateJoined());
			}
			String phone = demographic.getPhone() != null ? demographic.getPhone() : "";

%>
	<tr bgcolor="<%=isOddRow ? "ivory" : "white"%>">
		<td nowrap><%=demographic.getLastName()%></td>
		<td nowrap><%=demographic.getFirstName()%></td>
		<td align="center"><%=chartNo%></td>
		<td align="center"><%=age%></td>
		<td align="center"><%=demographic.getSex()%></td>
		<td><%=hin%></td>
		<td align="center"><%=ver%></td>
		<td><%=mrp%></td>
		<td><%=dateJoined%></td>
		<td><%=phone%></td>
	</tr>
	<%
		}
	%>

</table>
<br>
<%
  int nLastPage = offset - limit;
  int nNextPage= limit + offset;

  if(nLastPage >= 0)
  {
%> <a
	href="reportactivepatientlist.jsp?offset=<%=nLastPage%>&limit=<%=limit%>"><bean:message
	key="report.reportactivepatientlist.msgLastPage" /></a> | <%
  }
  if(demographics.size() == limit)
  {
%> <a
	href="reportactivepatientlist.jsp?offset=<%=nNextPage%>&limit=<%=limit%>">
<bean:message key="report.reportactivepatientlist.msgNextPage" /></a> <%
  }
%>

</body>
</html:html>
