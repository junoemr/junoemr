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

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/caisi-tag.tld" prefix="caisi"%>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%@ page import="org.oscarehr.common.model.PreventionsLotNrs"%>
<%@ page import="org.oscarehr.common.dao.PreventionsLotNrsDao"%>
<%@ page import="org.oscarehr.util.SpringUtils"%>
<%@ page import="java.util.List" %>
<%@ page errorPage="errorpage.jsp"%>

<jsp:useBean id="apptMainBean" class="oscar.AppointmentMainBean" scope="session" />

<%
	String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
	boolean authed = true;
%>

<security:oscarSec roleName="<%=roleName$%>" objectName="_admin" rights="r" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect("../securityError.jsp?type=_admin");%>
</security:oscarSec>
<%
	if(!authed) {
		return;
	}

	String deepcolor = "#CCCCFF";
	String weakcolor = "#EEEEFF" ;
%>


<html:html locale="true">
	<head>
		<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
		<title><bean:message key="admin.lotnrsearchresults.title" /></title>
		<link rel="stylesheet" href="../web.css" />
	</head>
	<body onLoad="setfocus()" topmargin="0" leftmargin="0" rightmargin="0">
	<center>
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr bgcolor="<%=deepcolor%>">
				<th><bean:message key="admin.lotnrsearchresults.description" /></th>
			</tr>
		</table>
		<form method="post" action="lotnrsearchresults.jsp" name="searchlotnr">
			<table cellspacing="0" cellpadding="0" width="100%" border="0" BGCOLOR="<%=weakcolor%>">
				<tr valign="top">
					<td rowspan="2" align="right" valign="middle">
						<font face="Verdana" color="#0000FF">
							<b><i><bean:message key="admin.search.formSearchCriteria" /></i></b>
						</font>
					</td>
					<td nowrap>
						<font size="1" face="Verdana" color="#0000FF">
							<input type="radio"
									<%=request.getParameter("search_mode").equals("search_prev")?"checked":""%>
								   name="search_mode" value="search_prev"><bean:message
								key="admin.lotnrsearch.prevention" />
						</font>
					</td>
					<td valign="middle" rowspan="2" ALIGN="left">
						<input type="text"
							   NAME="keyword" SIZE="17"
							   MAXLENGTH="100">
						<INPUT TYPE="hidden" NAME="orderby" VALUE="prevention_type">
						<INPUT TYPE="hidden" NAME="dboperation" VALUE="lotnr_search_prevention">
						<INPUT TYPE="hidden" NAME="limit1" VALUE="0">
						<INPUT TYPE="hidden" NAME="limit2" VALUE="10">
						<INPUT TYPE="SUBMIT"
							   NAME="button"
							   VALUE=<bean:message key="admin.lotnrsearchresults.btnSubmit"/>SIZE="17">
					</td>
				</tr>
			</table>
		</form>
		<table width="100%" border="0">
			<tr>
				<td align="left"><i><bean:message key="admin.search.keywords" /></i>
				: <%=request.getParameter("keyword")%></td>
			</tr>
		</table>
		<CENTER>
			<table width="100%" cellspacing="2" cellpadding="2" border="0" bgcolor="ivory">
				<tr bgcolor="<%=deepcolor%>">
					<TH align="center" width="25%">
						<b><bean:message key="admin.lotnrsearchresults.prevention" /></b>
					</TH>
					<TH align="center" width="25%">
						<b><bean:message key="admin.lotnrsearchresults.lotnr" /> </b>
					</TH>
				</tr>
	<%
		PreventionsLotNrsDao preventionsLotNrsDao = SpringUtils.getBean(PreventionsLotNrsDao.class);
		int nItems = 0;
		boolean bodd = false;
		String keyword = request.getParameter("keyword").trim();
		String prevention = keyword + "%";
		//find active lot number records only
		int limit1 = Integer.parseInt(request.getParameter("limit1"));
		int limit2 = Integer.parseInt(request.getParameter("limit2"));

		List<PreventionsLotNrs> preventionsLotNrs = preventionsLotNrsDao.findPagedData(prevention, false, limit1, limit2);
		for (PreventionsLotNrs pRec : preventionsLotNrs)
		{
			bodd = !bodd;
			nItems++;
	%>
				<tr bgcolor="<%=bodd ? "white" : weakcolor%>">
					<td><%=pRec.getPreventionType()%></td>
					<td>
						<a href="lotnrdeleterecordhtm.jsp?prevention=<%=pRec.getPreventionType()%>&lotnr=<%=pRec.getId()%>">
							<%= pRec.getLotNr()%>
						</a>
					</td>
				</tr>
	<%
		}
	%>
			</table>
			<br>
<%
  String strLimit1 = request.getParameter("limit1");
  String strLimit2 = request.getParameter("limit2");
  
  int nNextPage = Integer.parseInt(strLimit2) + Integer.parseInt(strLimit1);
  int nLastPage = Integer.parseInt(strLimit1) - Integer.parseInt(strLimit2);
  if(nLastPage >= 0)
  {
%>
			<a href="lotnrsearchresults.jsp?keyword=<%=request.getParameter("keyword")%>&search_mode=<%=request.getParameter("search_mode")%>&limit1=<%=nLastPage%>&limit2=<%=strLimit2%>">
				<bean:message key="admin.lotnrsearchresults.btnLastPage" /></a> |
<%
  }

  if(nItems == Integer.parseInt(strLimit2))
  {
%> <a
	href="lotnrsearchresults.jsp?keyword=<%=request.getParameter("keyword")%>&search_mode=<%=request.getParameter("search_mode")%>&limit1=<%=nNextPage%>&limit2=<%=strLimit2%>">
			<bean:message key="admin.lotnrsearchresults.btnNextPage" />
		</a>
<%
  }
%>
			<p><bean:message key="admin.lotnrsearchresults.msgClickForEditing" /></p>
		</center>
	</center>
	</body>
</html:html>
