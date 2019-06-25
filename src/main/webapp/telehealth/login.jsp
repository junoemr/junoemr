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
<% String email = request.getParameter("email") == null ? "" : request.getParameter("email"); %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
		<title>MyHealthAccess Login</title>
</head>
<body>
- This Juno user has a MHA account, but has not been authenticated from Juno yet
<br>
- Authenticate once, and in the future Juno will Auto log into MHA
<br>
<form action="<%= request.getContextPath() %>/telehealth/myhealthaccess.do?method=login" method="post">
		<input
						type="hidden"
						id="demographicNo"
						name="demographicNo"
						value="<%=request.getParameter("demographicNo")%>"
		/>
		<input
						type="hidden"
						id="siteName"
						name="siteName"
						value="<%=request.getParameter("siteName")%>"
		/>
		<input type="text" id="email" name="email" value="<%=email%>"/>

		<input type="password" id="password" name="password"/>
		<button type="submit">Submit</button>
</form>
</body>
</html>
