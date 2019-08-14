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
    <meta name="viewport" content="width=device-width,initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <title>MyHealthAccess Login</title>
    <link rel="stylesheet" type="text/css" href="myhealthaccess.css">
</head>
<body>

<div class="mha-container">
    <div class="left"></div>
    <div class="right">
        <div class="mha-content">
            <% if(request.getParameter("errorMessage") != null && !request.getParameter("errorMessage").isEmpty())
            {
            %>
                <br>
                <strong><%= request.getParameter("errorMessage") %></strong>
                <br>
            <%
                }
            %>
            <h3>Connect your MyHealthAccess Account</h3>
            <p>
                It looks like you have a MyHealthAccess account, but you haven't authenticated it from your Juno EMR.
                Log in, and we'll complete the connection.
            </p>
            <form action="<%= request.getContextPath() %>/telehealth/myhealthaccess.do?method=login" method="post">
                <input type="hidden" name="siteName" value="<%=request.getParameter("siteName")%>"/>
                <input type="hidden" name="remoteUser" value="<%=request.getParameter("remoteUser")%>"/>
                <input type="hidden" name="appt" value="<%=request.getParameter("appt")%>"/>
                <div class="mha-input">
                    <label for="email">Email address</label>
                    <input type="text" id="email" name="email" value="<%=email%>"/>
                </div>
                <div class="mha-input">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password"/>
                </div>
                <div class="mha-button-container">
                    <button type="submit" class="primary">Log on</button>
                </div>
            </form>
        </div>
    </div>
</div>
</body>
</html>

<!--         - This Juno user has a MHA account, but has not been authenticated from Juno yet
<br>
- Authenticate once, and in the future Juno will Auto log into MHA --!>
