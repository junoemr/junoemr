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
<!--
/*
 *
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version. *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA. *
 *
 * <OSCAR Service Group>
 */
-->
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%@ page errorPage="../errorpage.jsp"%>
<%@ page import="java.util.*"%>
<%@ page import="java.sql.*"%>
<%@ page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ page import="oscar.login.*"%>
<%@ page import="oscar.log.*"%>
<%
if(session.getAttribute("user") == null )
	response.sendRedirect("../logout.jsp");
String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
String curUser_no = (String)session.getAttribute("user");
%>
<security:oscarSec roleName="<%=roleName$%>"
	objectName="_admin,_admin.userAdmin,_admin.torontoRfq" rights="r"
	reverse="<%=true%>">
	<%response.sendRedirect("../noRights.html");%>
</security:oscarSec>

<%
    boolean isSiteAccessPrivacy=false;
%>

<security:oscarSec objectName="_site_access_privacy" roleName="<%=roleName$%>" rights="r" reverse="false">
	<%isSiteAccessPrivacy=true; %>
</security:oscarSec>


<%
  String ip = request.getRemoteAddr();
  String msg = "Unlock";
  //LoginList llist = null;
  LoginCheckLogin cl = new LoginCheckLogin();
  ArrayList<String> vec = cl.findLockList();
  if(vec == null) vec = new ArrayList<String>();
  
  if (request.getParameter("submit") != null && request.getParameter("submit").equals("Unlock")) {
    // unlock
    if(request.getParameter("userName") != null && request.getParameter("userName").length()>0) {
      String userName = request.getParameter("userName");
      vec.remove(userName);
      cl.unlock(userName);
	  LogAction.addLog(curUser_no, "unlock", "adminUnlock", userName, ip);
      msg = "The login account " + userName + " was unlocked.";
    }
  } 
  
  //multi-office limit
  if (isSiteAccessPrivacy && vec.size() > 0) {
	  DBHelp dbObj = new DBHelp(); 
	  String sqlString = "select user_name from security p inner join providersite s ON p.provider_no = s.provider_no WHERE s.site_id IN (SELECT site_id from providersite where provider_no=" + curUser_no + ")";
	  
	  ResultSet rs = dbObj.searchDBRecord(sqlString);
	  List<String> userList = new ArrayList<String>();
	  if (rs.next()) {
		  userList.add(rs.getString(1));
	  }
	  
	  for(int i=0; i<vec.size(); i++) {
		  if (!userList.contains(vec.get(i))) {
			  vec.remove(vec.get(i));
		  }
	  }
  }
  
%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html locale="true">
<head>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
<title>Unlock</title>
<script type="text/javascript" language="JavaScript">

      <!--
		
	    function onSearch() {
	    }
//-->

      </script>
</head>
<body bgcolor="ivory" onLoad="setfocus()" style="margin: 0px">
<table BORDER="0" CELLPADDING="0" CELLSPACING="0" WIDTH="100%">
	<tr>
		<td align="left">&nbsp;</td>
	</tr>
</table>

<center>
<table BORDER="1" CELLPADDING="0" CELLSPACING="0" WIDTH="80%">
	<tr BGCOLOR="#CCFFFF">
		<th><%=msg%></th>
	</tr>
</table>
</center>
<form method="post" name="baseurl" action="unLock.jsp">
<table width="100%" border="0" cellspacing="2" cellpadding="2">
	<tr>
		<td>&nbsp;</td>
	</tr>
	<tr bgcolor="#EEEEFF">
		<td align="right"><b>Role name</b></td>
		<td><select name="userName">
			<% for(int i=0; i<vec.size(); i++) { %>
			<option value="<%=vec.get(i) %>"><%=vec.get(i) %></option>
			<% } %>
		</select> <input type="submit" name="submit" value="Unlock" /></td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td align="center" bgcolor="#CCCCFF" colspan="2"><input
			type="button" name="Cancel"
			value="<bean:message key="admin.resourcebaseurl.btnExit"/>"
			onClick="window.close()" /></td>
	</tr>
</table>
</form>

</body>
</html:html>
