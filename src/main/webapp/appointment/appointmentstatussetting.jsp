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
<%@ page import="org.springframework.web.util.UriComponentsBuilder" %>
<%@ page import="java.util.regex.Pattern" %>
<%@ page import="java.util.regex.Matcher" %>
<%@ page import="org.oscarehr.util.LoggedInInfo" %>
<%@ page import="org.oscarehr.common.model.Provider" %>
<%@ page import="org.oscarehr.common.model.AppointmentStatus" %>
<%@ page import="java.util.List" %>
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
<style>
    table {
        width: 100%;
    }

    .margin-t {
        margin-top: 32px;
    }

    .text-l {
        text-align: left;
        width: 10%;
    }

    .text-c {
        text-align: center;
    }

    .wider {
        width: 15%;
    }

    .preview {
        max-height: 10px;
        padding-left: 4px;
    }

    .active {
        color: black;
    }

    .inactive {
        color:  #808080;
    }
</style>
<body>
    <%
        final String baseUrl = request.getContextPath() + "/appointment/apptStatusSetting.do";
		final Provider provider = LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProvider();
		final boolean isSuperAdmin = provider.convertToProviderData().isSuperAdmin();

        UriComponentsBuilder addUrl = UriComponentsBuilder.fromPath(baseUrl);
        addUrl.queryParam("method", "create");
    %>
<table border=0 cellspacing=0 cellpadding=0>
	<tr bgcolor="#486ebd">
		<th align="CENTER" NOWRAP><font face="Helvetica" color="#FFFFFF">
			<bean:message key="admin.appt.status.mgr.title" />
		</font></th>
	</tr>
</table>

<table class="borderAll margin-t">
	<tr>
        <th class="header text-l">Relative Position</th>
		<th class="header text-l"><bean:message key="admin.appt.status.mgr.label.status" /></th>
		<th class="header text-l wider"><bean:message key="admin.appt.status.mgr.label.desc" /></th>
        <th class="header text-l">Classic UI</th>
        <th class="header text-l">Juno UI</th>
		<th class="header text-l">Enabled</th>
		<th class="header text-l wider" colspan=3>Editing Options</th>
	</tr>
	<%
        List<AppointmentStatus> statuses = (List<AppointmentStatus>) request.getAttribute("appointmentStatuses");

        Pattern junoClassRegex = Pattern.compile("(.+)\\.gif");

        for (AppointmentStatus status : statuses)
        {
            boolean isActive = status.isActive();

            UriComponentsBuilder editUrl = UriComponentsBuilder.fromPath(baseUrl);
            editUrl.queryParam("method", "modify");
            editUrl.queryParam("statusId", status.getId());

            UriComponentsBuilder upUrl = UriComponentsBuilder.fromPath(baseUrl);
            upUrl.queryParam("method", "moveUp");
            upUrl.queryParam("statusId", status.getId());

            UriComponentsBuilder downUrl = UriComponentsBuilder.fromPath(baseUrl);
            downUrl.queryParam("method", "moveDown");
            downUrl.queryParam("statusId", status.getId());

            String imgUrl = "../images/" + status.getIcon();
            String junoIconClass = "";

            Matcher matcher = junoClassRegex.matcher(status.getIcon());

            if (matcher.find())
            {
                junoIconClass = "icon-" + matcher.group(1);
            }
    %>
    <tr>
        <td><%= status.getId() %>
        </td>
        <td class="nowrap"><%= status.getStatus() %>
        </td>
        <td class="nowrap"><%= status.getDescription() %>
        </td>
        <td style="background-color: <%= status.getColor() %>"><img class=preview src="<%=imgUrl%>" alt="Classic icon"/>
        </td>
        <td style="background-color: <%= status.getJunoColor() %>"><i class="preview <%="icon " + junoIconClass %>"
                                                                      alt="Juno icon"></i></td>
        <td class="nowrap <%= isActive ? "active" : "inactive" %>"><%= isActive ? "Enabled" : "Disabled" %>
        </td>
        <td class="nowrap text-l"><a href=<%= editUrl.build().toString() %>>Edit</a></td>
        <%
            if (isSuperAdmin)
            { %>
        <td class="nowrap text-l">
            <a href=<%= upUrl.build().toString() %>>Move Up</a>
        </td>
        <td class="nowrap text-l">
            <a href=<%= downUrl.build().toString() %>>Move Down</a>
        </td>
        <% } %>
    </tr>
    <% } %>
</table>
<% if (isSuperAdmin) { %>
	<div class="margin-t text-c">
		<a href=<%=addUrl.build().toString()%>><b>Create New Appointment Status</b></a>
	</div>
<% } %>
<br>

<%
	List<String> alertStatuses = (List<String>)request.getAttribute("alertStatuses");
	if (alertStatuses != null)
	{
		for (String code : alertStatuses)
		{
%>
			Status code [<%=code%>] has been used before, but is currently disabled.
			<br/>
<%
		}
	}
%>
</body>
</html>
