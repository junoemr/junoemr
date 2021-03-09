<%--

    Copyright (c) 2006-. OSCARservice, OpenSoft System. All Rights Reserved.
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

--%>
<%@ page import="java.util.*,org.oscarehr.common.model.*"%>
<%@ page import="org.springframework.web.util.UriComponentsBuilder" %>
<%@ page import="java.util.regex.Pattern" %>
<%@ page import="java.util.regex.Matcher" %>
<%@ page import="org.oscarehr.util.LoggedInInfo" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%@ taglib uri="/WEB-INF/oscar-tag.tld" prefix="oscar" %>

<%
    String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
%>
<security:oscarSec roleName="<%=roleName$%>"
	objectName="_admin,_admin.userAdmin,_admin.schedule" rights="r" reverse="<%=true%>">
	<%response.sendRedirect("../logout.jsp");%>
</security:oscarSec>


<html>
<head>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
<title><bean:message key="admin.appt.status.mgr.title" /></title>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
   <script>
     jQuery.noConflict();
   </script>
<oscar:customInterface section="apptStatusList"/>
</head>
<link rel="stylesheet" type="text/css" media="all" href="../share/css/extractedFromPages.css"  />
<link rel="stylesheet" type="text/css" media="all" href="../css/font/junoIcons/stylesheet.css" />
<body>
    <%
        final String baseUrl = request.getContextPath() + "/appointment/apptStatusSetting.do";
		final Provider provider = LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProvider();
		final boolean isSuperAdmin = provider.convertToProviderData().isSuperAdmin();

        UriComponentsBuilder addUrl = UriComponentsBuilder.fromPath(baseUrl);
        addUrl.queryParam("method", "create");
    %>
<table border=0 cellspacing=0 cellpadding=0 width="100%">
	<tr bgcolor="#486ebd">
		<th align="CENTER" NOWRAP><font face="Helvetica" color="#FFFFFF">
			<bean:message key="admin.appt.status.mgr.title" />
		</font></th>
	</tr>
</table>

<table class="borderAll" width="100%">
	<tr>
        <th style="width: 10%; text-align: left;">Relative Position</th>
		<th style="width: 10%; text-align: left;"><bean:message key="admin.appt.status.mgr.label.status" /></th>
		<th style="width: 15%; text-align: left;"><bean:message key="admin.appt.status.mgr.label.desc" /></th>
        <th style="width: 10%; text-align: left;">Classic UI</th>
        <th style="width: 10%; text-align: left;">Juno UI</th>
		<th style="width: 10%; text-align: left;">Enabled</th>
		<% if (isSuperAdmin) { %>
			<th style="width: 10%; text-align: left;">Actions</th>
		<% } %>
	</tr>
	<%
        List<AppointmentStatus> statuses = (List<AppointmentStatus>) request.getAttribute("appointmentStatuses");

        boolean rowColoring = false;    // alternate background color of rows, this should really be in CSS.
        Pattern junoClassRegex = Pattern.compile("(.+)\\.gif");

        for (AppointmentStatus status : statuses)
        {
        	rowColoring = !rowColoring;

        	boolean isActive =  status.getActive() == 1;
        	boolean isEditable = status.getEditable() == 1;

            UriComponentsBuilder editUrl = UriComponentsBuilder.fromPath(baseUrl);
            editUrl.queryParam("method", "modify");
            editUrl.queryParam("statusId", status.getId());

            String imgUrl = "../images/" + status.getIcon();
            String junoIconClass = "";

            Matcher matcher = junoClassRegex.matcher(status.getIcon());

            if (matcher.find())
            {
            	junoIconClass = "icon-" + matcher.group(1);
            }
    %>
	<tr class=<%= (rowColoring) ? "even" : "odd" %>>
        <td><%= status.getId() %></td>
		<td class="nowrap"><%= status.getStatus() %></td>
		<td class="nowrap"><%= status.getDescription() %></td>
        <td bgcolor="<%= status.getColor() %>"><img src="<%=imgUrl%>"></img></td>
		<td bgcolor="<%= status.getJunoColor() %>"><i class="<%="icon " + junoIconClass %>"></i></td>
        <td class="nowrap" <%= isActive ? "" : "style=\"color:  #808080;\"" %>><%= isActive ? "Enabled" : "Disabled" %></td>
        <% if (isSuperAdmin && isEditable) { %>
		<td class="nowrap"><a href=<%= editUrl.build().toString() %>>Edit</a></td>
        <% } %>
	</tr>
    <% } %>
</table>
<% if (isSuperAdmin) { %>
	<div style="margin-top: 32px; text-align: center;">
		<a href=<%=addUrl.build().toString()%>><b>Create New Appointment Status</b></a>
	</div>
<% } %>
<br>

<%
	List inactiveUseStatus = (List) request.getAttribute("useStatus");
	if (null != inactiveUseStatus && inactiveUseStatus.size() > 0)
	{
		for (int i = 0; i < inactiveUseStatus.size(); i++)
		{
%>
			The code [<%=inactiveUseStatus.get(i)%>] has been used before, please enable that
			status.<br/>
<%
		}
	}
%>
</body>
</html>
