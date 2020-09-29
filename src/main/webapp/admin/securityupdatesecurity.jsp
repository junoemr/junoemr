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
<%
    String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
    boolean authed=true;
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
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ page import="java.lang.*, java.util.*, java.text.*,java.sql.*, oscar.*"	errorPage="errorpage.jsp"%>

<%@ page import="org.oscarehr.util.SpringUtils" %>
<%@ page import="org.oscarehr.common.model.Security" %>
<%@ page import="org.oscarehr.common.dao.SecurityDao" %>


<%!
	OscarProperties op = OscarProperties.getInstance();
%>

<html:html locale="true">
<head>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/checkPassword.js.jsp"></script>
<title><bean:message key="admin.securityupdatesecurity.title" /></title>
<link rel="stylesheet" type="text/css" href="bcArStyle.css">
<!-- calendar stylesheet -->
<link rel="stylesheet" type="text/css" media="all"
	href="../share/calendar/calendar.css" title="win2k-cold-1" />

<!-- main calendar program -->
<script type="text/javascript" src="../share/calendar/calendar.js"></script>

<!-- language for the calendar -->
<script type="text/javascript"
	src="../share/calendar/lang/<bean:message key="global.javascript.calendar"/>"></script>

<!-- the following script defines the Calendar.setup helper function, which makes
       adding a calendar a matter of 1 or 2 lines of code. -->
<script type="text/javascript" src="../share/calendar/calendar-setup.js"></script>
<link rel="stylesheet" href="../web.css">
<script type="text/javascript">
<!--
	function setfocus(el) {
		this.focus();
		document.updatearecord.elements[el].focus();
		document.updatearecord.elements[el].select();
	}
	function onsub() {
		if (document.updatearecord.user_name.value=="") {
			alert('<bean:message key="admin.securityrecord.formUserName" /> <bean:message key="admin.securityrecord.msgIsRequired"/>');
			setfocus('user_name');
			return false;
		}
		if (document.updatearecord.password.value=="") {
			alert('<bean:message key="admin.securityrecord.formPassword" /> <bean:message key="admin.securityrecord.msgIsRequired"/>');
			setfocus('password');
			return false;
		}
		if (document.updatearecord.password.value != "*********" && !validatePassword(document.updatearecord.password.value)) {
			setfocus('password');
			return false;
		}
		if (document.forms[0].password.value != document.forms[0].conPassword.value) {
			alert('<bean:message key="admin.securityrecord.msgPasswordNotConfirmed" />');
			setfocus('conPassword');
			return false;
		}
		if (document.updatearecord.provider_no.value=="") {
			return false;
		}
		if (document.forms[0].b_ExpireSet.checked && document.forms[0].date_ExpireDate.value.length<10) {
			alert('<bean:message key="admin.securityrecord.formDate" /> <bean:message key="admin.securityrecord.msgIsRequired"/>');
			setfocus('date_ExpireDate');
			return false;
		}
		if (document.forms[0].b_RemoteLockSet.checked || document.forms[0].b_LocalLockSet.checked) {
			if (document.forms[0].pin.value=="") {
				alert('<bean:message key="admin.securityrecord.formPIN" /> <bean:message key="admin.securityrecord.msgIsRequired"/>');
				setfocus('pin');
				return false;
			}
		}
		if (document.forms[0].pin.value != "****" && !validatePin(document.forms[0].pin.value)) {
			setfocus('pin');
			return false;
		}
		if (document.forms[0].pin.value != document.forms[0].conPin.value) {
			alert('<bean:message key="admin.securityrecord.msgPinNotConfirmed" />');
			setfocus('conPin');
			return false;
		}
		return true;
	}
//-->
</script>
</head>

<body onLoad="setfocus('user_name')" topmargin="0" leftmargin="0" rightmargin="0">
<center>
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr bgcolor="#486ebd">

		<th align="CENTER"><font face="Helvetica" color="#FFFFFF"><bean:message
			key="admin.securityupdatesecurity.description" /></font></th>
	</tr>
</table>
<table cellspacing="0" cellpadding="2" width="100%" border="0">
	<form method="post" action="securityupdate.jsp" name="updatearecord" onsubmit="return onsub()">
<%
	SecurityDao securityDao = SpringUtils.getBean(SecurityDao.class);
	Integer securityId = Integer.valueOf(request.getParameter("keyword"));
	Security security = securityDao.find(securityId);
	
	if(security == null) {
%>
	<tr>
		<td><bean:message key="admin.securityupdatesecurity.msgFailed" /></td>
	</tr>
<%
	} 
	else {
%>
	<tr>
		<td width="50%" align="right"><bean:message
			key="admin.securityrecord.formUserName" />:</td>
		<td><input type="text" name="user_name" maxlength="30"
			value="<%= security.getUserName() %>"></td>
	</tr>
	<tr>
		<td align="right" nowrap><bean:message
			key="admin.securityrecord.formPassword" />:</td>
		<td><input type="password" name="password" value="*********" maxlength="15"> <font size="-2">(<bean:message
			key="admin.securityrecord.msgAtLeast" />
			<%=op.getProperty("password_min_length")%> <bean:message
			key="admin.securityrecord.msgSymbols" />)</font></td>
	</tr>
	<tr>
		<td align="right"><bean:message
			key="admin.securityrecord.formConfirm" />:</td>
		<td><input type="password" name="conPassword" value="*********" maxlength="15"></td>
	</tr>
	<tr>
		<td>
		<div align="right"><bean:message
			key="admin.securityrecord.formProviderNo" />:</div>
		</td>
		<td><%= security.getProviderNo() %>
		<input type="hidden" name="provider_no"
			value="<%= security.getProviderNo() %>"></td>
	</tr>
	<!-- new security -->
	<tr>
		<td align="right" nowrap><bean:message
			key="admin.securityrecord.formExpiryDate" />:</td>
		<td><input type="checkbox" name="b_ExpireSet" value="1"
			<%= security.getBExpireset()==0?"":"checked" %>> <bean:message
			key="admin.securityrecord.formDate" />: <input
			type="text" name="date_ExpireDate" id="date_ExpireDate"
			value="<%=  security.getDateExpiredate() ==null?"": security.getDateExpiredate()  %>"
			size="10" readonly /> <img src="../images/cal.gif"
			id="date_ExpireDate_cal" /></td>
	</tr>
	<tr>
		<td align="right" nowrap><bean:message
			key="admin.securityrecord.formRemotePIN" />:</td>
		<td><input type="checkbox" name="b_RemoteLockSet" value="1"
			<%= security.getBRemotelockset()==0?"":"checked" %>>
		<bean:message
			key="admin.securityrecord.formLocalPIN" />: <input type="checkbox" name="b_LocalLockSet"
			value="1" <%= security.getBLocallockset()==0?"":"checked" %>>
		</td>
	</tr>
	<!-- new security -->
	<tr>
		<td align="right" nowrap><bean:message
			key="admin.securityrecord.formPIN" />:</td>
		<td><input type="password" name="pin" value="****" size="6" maxlength="6"> <font size="-2">(<bean:message
			key="admin.securityrecord.msgAtLeast" />
			<%=op.getProperty("password_pin_min_length")%> <bean:message
			key="admin.securityrecord.msgDigits" />)</font>
		</td>
	</tr>
	<tr>
		<td align="right"><bean:message
			key="admin.securityrecord.formConfirm" />:</td>
		<td><input type="password" name="conPin" value="****" size="6" maxlength="6" /></td>
	</tr>
	
	<%
		if (!OscarProperties.getInstance().getBooleanProperty("mandatory_password_reset", "false")) {
	%>		  
		<tr>		
			<td align="right"><bean:message key="admin.provider.forcePasswordReset" />:
			</td>
			<td>
					<select name="forcePasswordReset">
							<option value="1" <% if (security != null && security.isForcePasswordReset()!= null && security.isForcePasswordReset()) { %>
					                          SELECTED <%}%>>true</option>
							<option value="0" <% if (security != null && security.isForcePasswordReset()!= null && !security.isForcePasswordReset()) { %>
					                          SELECTED <%}%>>false</option>
					</select>	
			</td>
		</tr>
   <%} %>
	
	
	<tr>
		<td colspan="2" align="center">
			<input type="hidden" name="security_no" value="<%= security.getSecurityNo() %>">
			<input type="submit" name="subbutton" value='<bean:message key="admin.securityupdatesecurity.btnSubmit"/>'>
			<input type="button" value="<bean:message key="admin.securityupdatesecurity.btnDelete"/>" onclick="window.location='securitydelete.jsp?security_no=<%=security.getSecurityNo()%>&provider_number=<%=security.getProviderNo()%>'">
		</td>
	</tr>
<%
		}
%>
	</form>
</table>

<p></p>
</center>
<script type="text/javascript">
Calendar.setup({ inputField : "date_ExpireDate", ifFormat : "%Y-%m-%d", showsTime :false, button : "date_ExpireDate_cal" });
</script>
</body>
</html:html>
