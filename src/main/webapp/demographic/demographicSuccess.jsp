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

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@taglib uri="/WEB-INF/caisi-tag.tld" prefix="caisi"%>

<%@ page import="java.util.ResourceBundle" %>
<%@ page import="java.net.URLDecoder" %>
<%
	java.util.ResourceBundle oscarResources = ResourceBundle.getBundle("oscarResources", request.getLocale());

	String roleName$ = session.getAttribute("userrole") + "," + session.getAttribute("user");
	boolean authed=true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_demographic" rights="w" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect(request.getContextPath() + "/securityError.jsp?type=_demographic");%>
</security:oscarSec>
<%
	if(!authed) {
		return;
	}
%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
	<title>Title</title>

</head>
<body bgproperties="fixed" topmargin="0" leftmargin="0" rightmargin="0">
<center>
	<table border="0" cellspacing="0" cellpadding="0" width="100%">
		<tr bgcolor="#486ebd">
			<th align="CENTER"><font face="Helvetica" color="#FFFFFF">
				<bean:message key="demographic.demographicaddarecord.title" /></font></th>
		</tr>
	</table>
	<p>
	<h2><bean:message key="demographic.demographicaddarecord.msgSuccessful" /></h2>

	<a href="demographiccontrol.jsp?demographic_no=<%=request.getParameter("demoNo")%>&displaymode=edit&dboperation=search_detail"><bean:message key="demographic.demographicaddarecord.goToRecord"/></a>

	<caisi:isModuleLoad moduleName="caisi">
		<br/>
		<a href="../PMmodule/ClientManager.do?id=<%=request.getParameter("demoNo")%>"><bean:message key="demographic.demographicaddarecord.goToCaisiRecord"/> (<a href="#"  onclick="popup(700,1027,'demographiccontrol.jsp?demographic_no=<%=request.getParameter("demoNo")%>&displaymode=edit&dboperation=search_detail')">New Window</a>)</a>
	</caisi:isModuleLoad>


	<caisi:isModuleLoad moduleName="caisi">
		<br/>
		<a href="../PMmodule/ClientManager.do?id=<%=request.getParameter("demoNo")%>"><bean:message key="demographic.demographicaddarecord.goToCaisiRecord"/></a>
	</caisi:isModuleLoad>


	<p></p>
	<%@ include file="footer.jsp"%>
</center>
<script language="JavaScript">
<%
	if(request.getParameter("docError") != null && request.getParameter("docError").equals("true"))
	{
%>
		alert("Document Upload Failed");
<%
	}
	if (request.getParameter("submitType") != null && URLDecoder.decode(request.getParameter("submitType")).equals(oscarResources.getString("demographic.demographicaddrecordhtm.btnAddDocs")))
	{
%>

		window.open('../dms/documentReport.jsp?function=demographic&doctype=lab&functionid=<%=request.getParameter("demoNo")%>&curUser=<%=request.getParameter("provider_no")%>&mode=add&parentAjaxId=docs', 'height=700', 'width=1027');
<%
	}
%>
</script>
</body>
</html>
