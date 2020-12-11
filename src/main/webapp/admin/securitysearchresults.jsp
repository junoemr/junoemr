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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ page import="java.sql.*, java.util.*, oscar.*" buffer="none" errorPage="errorpage.jsp"%>
	
<%@ page import="java.util.*" %>
<%@ page import="org.oscarehr.util.SpringUtils" %>
<%@ page import="org.oscarehr.common.model.Security" %>
<%@ page import="org.oscarehr.common.dao.SecurityDao" %>
<%@ page import="org.oscarehr.common.model.UserProperty" %>
<%@ page import="org.oscarehr.common.dao.UserPropertyDAO" %>
<%
    String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
    
    boolean isSiteAccessPrivacy=false;
	boolean authed=true;
%>

<security:oscarSec roleName="<%=roleName$%>" objectName="_admin,_admin.userAdmin" rights="*" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect("../securityError.jsp?type=_admin&type=_admin.userAdmin");%>
</security:oscarSec>
<%
	if(!authed) {
		return;
	}
%>


<security:oscarSec objectName="_site_access_privacy" roleName="<%=roleName$%>" rights="r" reverse="false">
	<%
		isSiteAccessPrivacy=true;
	%>
</security:oscarSec>
<%
	SecurityDao securityDao = SpringUtils.getBean(SecurityDao.class);
	UserPropertyDAO userPropertyDao = SpringUtils.getBean(UserPropertyDAO.class);
%>
	

<html:html locale="true">
<head>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/global.js"></script>
<title><bean:message key="admin.securitysearchresults.title" /></title>
<c:set var="ctx" value="${pageContext.request.contextPath}"
	scope="request" />
<link rel="stylesheet" href="../web.css" />
<script LANGUAGE="JavaScript">
    <!--
		function setfocus() {
		  document.searchprovider.keyword.focus();
		  document.searchprovider.keyword.select();
		}

    function onsub() {
      if(document.searchprovider.keyword.value=="") {
        alert('<bean:message key="global.msgInputKeyword"/>');
        return false;
      } else return true;
      // check input data in the future 
    }

    function encryptPIN(){
	   alert('This function has been disabled. Please contact your administrator');
    }
    //-->
    </script>
</head>


<body onLoad="setfocus()" topmargin="0" leftmargin="0" rightmargin="0">
<center>
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr bgcolor="#486ebd">
		<th align="CENTER"><font face="Helvetica" color="#FFFFFF"><bean:message
			key="admin.securitysearchresults.description" /></font></th>
	</tr>
</table>

<%--@ include file="zprovidertitlesearch.htm" --%>
<table cellspacing="0" cellpadding="0" width="100%" border="0"
	BGCOLOR="#C4D9E7">

	<form method="post" action="securitysearchresults.jsp" name="searchprovider">
	<tr valign="top">
		<td rowspan="2" align="right" valign="middle"><font
			face="Verdana" color="#0000FF"><b><i><bean:message
			key="admin.search.formSearchCriteria" /></i></b></font></td>
		<td nowrap><font size="1" face="Verdana" color="#0000FF">
		<input type="radio" name="search_mode" value="search_username"><bean:message
			key="admin.securityrecord.formUserName" /></font></td>
		<td nowrap><font size="1" face="Verdana" color="#0000FF">
		<input type="radio" checked name="search_mode"
			value="search_providerno"><bean:message
			key="admin.securityrecord.formProviderNo" /></font></td>
		<td valign="middle" rowspan="2" ALIGN="left"><input type="text"
			NAME="keyword" SIZE="17" MAXLENGTH="100"> <INPUT
			TYPE="hidden" NAME="orderby" VALUE="user_name"> 

		<INPUT TYPE="hidden" NAME="limit1" VALUE="0"> <INPUT
			TYPE="hidden" NAME="limit2" VALUE="10"> <INPUT
			TYPE="SUBMIT" NAME="button"
			VALUE='<bean:message key="admin.search.btnSubmit"/>' SIZE="17"></td>
	</tr>
	<tr>
		<td nowrap><font size="1" face="Verdana" color="#0000FF"><bean:message
			key="admin.securitysearchresults.reserved" /></font></td>
		<td nowrap><font size="1" face="Verdana" color="#0000FF">
		</font></td>
	</tr>
	</form>
</table>

<table width="100%" border="0">
	<tr>
		<td align="left"><i><bean:message key="admin.search.keywords" /></i>:
		<%=request.getParameter("keyword")%> &nbsp; <%
		boolean enc=false;
		UserProperty prop = userPropertyDao.getProp("IS_PIN_ENCRYPTED");
		if(prop == null) {
			enc=true;
		}else {
			int i = Integer.parseInt(prop.getValue());
			enc = i>0;
		}
 	if(!enc){
 %> <input type="button" name="encryptPIN" value="Encrypt PIN"
			onclick="encryptPIN()"> <%
 	}
 %>
		</td>
	</tr>
</table>
<CENTER>
<table width="100%" cellspacing="0" cellpadding="2" border="1"
	bgcolor="#ffffff">
	<tr bgcolor="#339999">
		<TH align="center" width="20%"><b><bean:message
			key="admin.securityrecord.formUserName" /></b></TH>
		<TH align="center" width="40%"><b><bean:message
			key="admin.securityrecord.formPassword" /></b></TH>
		<TH align="center" width="20%"><b><bean:message
			key="admin.securityrecord.formProviderNo" /></b></TH>
		<TH align="center" width="20%"><b><bean:message
			key="admin.securityrecord.formPIN" /></b></TH>
	</tr>

<%
	List<org.oscarehr.common.model.Security> securityList = securityDao.findAllOrderBy("user_name");
	
	//if action is good, then give me the result
	String searchMode = request.getParameter("search_mode");
	String keyword=request.getParameter("keyword").trim()+"%";
	
	// if search mode is provider_no 
	if(searchMode.equals("search_providerno"))
		securityList = securityDao.findByLikeProviderNo(keyword);
	
	// if search mode is user_name
	if(searchMode.equals("search_username"))
		securityList = securityDao.findByLikeUserName(keyword);
	
	boolean toggleLine = false;

	for(Security securityRecord : securityList) {
		
		toggleLine = !toggleLine;
%>

	<tr bgcolor="<%=toggleLine?"ivory":"white"%>">

		<td><a href='securityupdatesecurity.jsp?keyword=<%=securityRecord.getId()%>'><%= securityRecord.getUserName() %></a></td>
		<td nowrap>*********</td>
		<td align="center"><%= securityRecord.getProviderNo() %></td>
		<td align="center">****</td>
	</tr>
	<%
    }
%>

</table>
<br>

<p><bean:message key="admin.securitysearchresults.msgClickForDetail" /></p>
</center>
</body>
</html:html>
