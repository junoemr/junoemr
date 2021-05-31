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
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%
    String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
    boolean authed=true;
    String providerNo = (String)request.getAttribute("provider_no");
%>
<security:oscarSec roleName="<%=roleName$%>"
        objectName="_admin,_admin.userAdmin" rights="r"
        reverse="<%=true%>">
        <%authed=false; %>
        <%response.sendRedirect("../securityError.jsp?type=_admin&type=_admin.userAdmin");%>
</security:oscarSec>
<%
	if(!authed) {
		return;
	}
%>


<%@ page errorPage="errorpage.jsp"%>
<%@ page import="oscar.log.LogAction"%>
<%@ page import="oscar.log.LogConst"%>
<%@ page import="org.oscarehr.util.SpringUtils" %>
<%@ page import="org.oscarehr.common.model.Security" %>
<%@ page import="org.oscarehr.common.dao.SecurityDao" %>
<%@ page import="java.security.MessageDigest" %>
<%@ page import="oscar.OscarProperties" %>
<%@ page import="oscar.Misc" %>
<%@ page import="oscar.MyDateFormat" %>
<%@ page import="org.oscarehr.managers.SecurityInfoManager" %>
<%
	SecurityDao securityDao = SpringUtils.getBean(SecurityDao.class);
	SecurityInfoManager securityInfoManager =SpringUtils.getBean(SecurityInfoManager.class);
%>

<html:html locale="true">
<head>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/global.js"></script>
<title><bean:message key="admin.securityupdate.title" /></title>
</head>
<link rel="stylesheet" href="../web.css" />
<body topmargin="0" leftmargin="0" rightmargin="0">
<center>
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr bgcolor="#486ebd">
		<th align="CENTER"><font face="Helvetica" color="#FFFFFF"><bean:message
			key="admin.securityupdate.description" /></font></th>
	</tr>
</table>
<%
	StringBuilder sbTemp = new StringBuilder();
    MessageDigest md = MessageDigest.getInstance("SHA");
    byte[] btNewPasswd= md.digest(request.getParameter("password").getBytes());
	for (byte b : btNewPasswd)
	{
		sbTemp.append(b);
	}

	String sPin = request.getParameter("pin");
	if (OscarProperties.getInstance().isPINEncripted())
	{
		sPin = Misc.encryptPIN(request.getParameter("pin"));
	}

	int rowsAffected = 0;

	String username = request.getParameter("user_name");

	Security overlappingEntry = securityDao.findByUserName(username);
	Security security = securityDao.find(Integer.parseInt(request.getParameter("security_no")));

	if (securityInfoManager.userCanModify((String)session.getAttribute("user"), request.getParameter("provider_no")))
	{
		if(security != null && (overlappingEntry == null || security.equals(overlappingEntry)))
		{
			security.setUserName(request.getParameter("user_name"));
			security.setProviderNo(request.getParameter("provider_no"));
			security.setBExpireset(request.getParameter("b_ExpireSet")==null?0:Integer.parseInt(request.getParameter("b_ExpireSet")));
			security.setDateExpiredate(MyDateFormat.getSysDate(request.getParameter("date_ExpireDate")));
			security.setBLocallockset(request.getParameter("b_LocalLockSet")==null?0:Integer.parseInt(request.getParameter("b_LocalLockSet")));
			security.setBRemotelockset(request.getParameter("b_RemoteLockSet")==null?0:Integer.parseInt(request.getParameter("b_RemoteLockSet")));

			if(request.getParameter("password") == null || !"*********".equals(request.getParameter("password")))
			{
				security.setPassword(sbTemp.toString());
			}

			if(request.getParameter("pin") == null || !"****".equals(request.getParameter("pin")))
			{
				security.setPin(sPin);
			}

			if (request.getParameter("forcePasswordReset") != null && request.getParameter("forcePasswordReset").equals("1"))
			{
				security.setForcePasswordReset(Boolean.TRUE);
			}
			else
			{
				security.setForcePasswordReset(Boolean.FALSE);
			}

			securityDao.saveEntity(security);
			rowsAffected = 1;
		}


		if (rowsAffected == 1)
		{
			LogAction.addLog((String) request.getSession().getAttribute("user"), LogConst.ACTION_UPDATE, LogConst.CON_SECURITY,
			request.getParameter("security_no") + "->" + request.getParameter("user_name"), request.getRemoteAddr());
			%>
			<p>
			<h2><bean:message key="admin.securityupdate.msgUpdateSuccess" /> <%=request.getParameter("provider_no")%></h2>
			<%
		}
		else if (security != null && !security.equals(overlappingEntry))
		{
			%>
			<h2><bean:message key="admin.securityupdate.msgUpdateUsernameConflict"/></h2>
			<%
		}
		else
		{
			%>
			<h1><bean:message key="admin.securityupdate.msgUpdateFailure" /><%= request.getParameter("provider_no") %>.</h1>
			<%
		}
	}
	else
	{
	%>
		<h1><bean:message key="admin.securityaddsecurity.msgProviderNoAuthorization" /></h1>
	<%
	}
%>

</center>
</body>
</html:html>
