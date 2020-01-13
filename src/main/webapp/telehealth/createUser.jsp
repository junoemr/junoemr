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
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>MyHealthAccess Create User</title>
    <link rel="stylesheet" type="text/css" href="css/myhealthaccess.css">
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
            <span class="red"><strong><%= request.getParameter("errorMessage") %></strong></span>
            <br>
            <%
                }
            %>
            <h3>Create a MyHealthAccess account</h3>
            <p>
                It looks like you don't have a MyHealthAccess account.
                Let's create one and connect it with your Juno EMR provider
            </p>
            <form action="<%= request.getContextPath() %>/telehealth/myhealthaccess.do?method=createUser" method="post">
                <input type="hidden" name="siteName" value="<%=request.getParameter("siteName")%>"/>
                <input type="hidden" name="appt" value="<%=request.getParameter("appt")%>"/>
                <div class="mha-input">
                    <label for="email">Email address</label>
                    <input type="text" id="email" name="email"/>
                </div>
                <div class="mha-button-container">
                    <button type="submit" class="primary">Sign up</button>
                </div>
            </form>
        </div>
    </div>
</div>
</body>
</html>