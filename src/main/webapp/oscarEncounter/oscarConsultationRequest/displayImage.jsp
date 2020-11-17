<%--

    Copyright (c) 2008-2012 Indivica Inc.
    
    This software is made available under the terms of the
    GNU General Public License, Version 2, 1991 (GPLv2).
    License details are available via "indivica.ca/gplv2"
    and "gnu.org/licenses/gpl-2.0.html".
    
--%>

<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%
      String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
      boolean authed=true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_con" rights="r" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect("../../securityError.jsp?type=_con");%>
</security:oscarSec>
<%
    if (!authed)
    {
	    return;
    }

    boolean linkIncluded = StringUtils.isNotEmpty(request.getParameter("link"));
%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ page import="org.apache.commons.lang.StringUtils"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN"
   "http://www.w3.org/TR/html4/strict.dtd">
<html:html locale="true">
<head>
<title><bean:message key="oscarEncounter.oscarConsultationRequest.AttachDocPopup.title" /></title>
</head>
<body>
    <% if (linkIncluded) { %> 
    <a id="link" href="<%= request.getParameter("link") %>" style="border:0;" target="_blank">
    <% } %>
        <img id="image" src="<%= request.getParameter("url") %>" style="border:0;" />
    <% if (linkIncluded) { %> 
    </a>
    <% } %>
</body>
</html:html>
