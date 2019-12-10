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
<%@ page import="org.oscarehr.common.model.UserProperty" %>
<%@ page import="org.oscarehr.common.dao.UserPropertyDAO" %>
<%@ page import="org.oscarehr.util.SpringUtils" %>
<%@ page import="org.oscarehr.util.LoggedInInfo" %>
<%@ page import="oscar.util.ConversionUtils" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
	// Check for property to default assigned provider and if present - default to user logged in
	UserProperty ticklerOnlyMineProp = ((UserPropertyDAO) SpringUtils.getBean(UserPropertyDAO.class)).getProp(LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProvider().getProviderNo(),
					UserProperty.TICKLER_VIEW_ONLY_MINE);
	boolean ticklerOnlyMine = false;
	boolean isError = ConversionUtils.parseBoolean(request.getParameter("error"));

	if (ticklerOnlyMineProp != null && ConversionUtils.parseBoolean(ticklerOnlyMineProp.getValue()))
	{
		ticklerOnlyMine = true;
	}


%>
<html>
<head>
	<title>Provider Tickler Settings</title>
	<link rel="stylesheet" type="text/css" href="../oscarEncounter/encounterStyles.css">
	<style>
		.page-header {
			background-color: #003399;
			color: white;
			padding-left: 20px
		}

		.error-text {
			color: red;
		}

		h3 {
			font-size: 16px;
			padding-bottom: 10px;
		}
		input {
			margin-top: 4px;
			margin-bottom: 8px;
		}
	</style>
</head>
<body>
	<h2 class="page-header">Tickler Settings</h2>
	<form name="tickler-settings" action="../tickler/updateTicklerSettings.do" method="POST">
		<input type="hidden" name="method" value="setSettings">
		<%
			if (isError)
			{
		%>
			<h4 class="error-text">Error Updating Settings!</h4>
		<%
			}
		%>
		<h3>Default Tickler View</h3>
		<hr>
		<label for="view-onlymine-off">View All</label>
		<input id="view-onlymine-off" type="radio" name="ticklerViewOnlyMine" value="false" <%=!ticklerOnlyMine ? "checked" : ""%>/>
		<label for="view-onlymine-on">View Only Mine</label>
		<input id="view-onlymine-on" type="radio" name="ticklerViewOnlyMine" value="true" <%=ticklerOnlyMine ? "checked" : ""%>/>
		<br>
		<input type="submit" value="save">
	</form>
</body>
</html>
