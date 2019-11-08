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
    <title>MyHealthAccess User Confirmation</title>
    <link rel="stylesheet" type="text/css" href="css/myhealthaccess.css">
</head>
<body>
<div class="mha-container">
    <div class="left"></div>
    <div class="right">
        <div class="mha-content">
            <h3>Almost done</h3>
            <p>
                We just need you to go to your inbox at <span class="bold"><%=request.getParameter("email")%></span> and click the link in your MyHealthAccess
                registration email to confirm your account.  After you've confirmed, click the continue button below.
            </p>
            <p>Don't worry, you will only need to do this once to complete linking your new MyHealthAccess account</p>
            <form action="<%= request.getContextPath() %>/telehealth/myhealthaccess.do?method=confirmUser" method="post">
                <input type=hidden name="siteName" value="<%=request.getParameter("siteName")%>"/>
                <input type=hidden name="appt" value="<%=request.getParameter("appt")%>"/>
                <input type=hidden name="remoteUser" value="<%=request.getParameter("remoteUser")%>"/>
                <div class="mha-button-container">
                    <button type="submit" class="primary">Continue to MyHealthAccess</button>
                </div>
            </form>
        </div>
    </div>
</div>
</body>
</html>


