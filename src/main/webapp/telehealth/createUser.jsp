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
</head>
<body>
- You do not currently have a linked myhealthaccess account.
<br>
- Fill out a valid email address to create an account.
<br>
- If an account already exists with this email, forward to the login.jsp page to link account
<br>
- You will get an email sent with a confirmation link
<br>
- You may optionally set a password to log directly into MHA
<br>
- In the future this Juno account will auto log into this MHA account
<br>
<form action="<%= request.getContextPath() %>/telehealth/myhealthaccess.do?method=createUser" method="post">
		<input type="hidden" id="demographicNo" name="demographicNo" value="<%=request.getParameter("demographicNo")%>"/>
		<input type="hidden" id="siteName" name="siteName" value="<%=request.getParameter("siteName")%>"/>

        Email: <input type="text" id="email" name="email"/> <br>
        Password: <input type="password" id="password" name="password"/> <br>
        Confirm Password: <input type="password" id="password_confirmation" name="password_confirmation"/> <br>

    <button type="submit">Submit</button>
</form>
</body>
</html>

