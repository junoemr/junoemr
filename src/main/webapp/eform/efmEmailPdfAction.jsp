<%--

    Copyright (c) 2005-2012. OscarHost Inc. All Rights Reserved.
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
    OscarHost, a Division of Cloud Practice Inc.

--%>
<%/* <%@page import="org.oscarehr.web.eform.EfmEmailPdfAction"> --*/%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="org.oscarehr.util.MiscUtils"%>
<%@page import="oscar.eform.actions.EmailAction"%>
<% String featherlight = (request.getParameter("featherlight") == null)?"false":request.getParameter("featherlight");%>
<% if(!featherlight.equals("true")){%>
<html>
<head>
        <script type="text/javascript" language="JavaScript">
            function startCloseWindowTimeout() {
                var sec=1500;
                setTimeout("closeWindow()",sec);
            }

            function closeWindow() {
                window.opener=self;
                window.close();
            }
        </script>
</head>
<%
}
//fdid
String eform_id=(String)request.getAttribute("fdid");

EmailAction bean=new EmailAction(request);

String clientId = request.getParameter("clientId");
String toEmailAddress = request.getParameter("toEmail");
String toName = request.getParameter("toName");
String emailSubject = request.getParameter("emailSubject");
String emailBodyText = request.getParameter("emailBodyText");
try{
    bean.sendEformToEmail(toEmailAddress, toName, eform_id, emailSubject, emailBodyText);
    if(!featherlight.equals("true")){
    	%>
    	<div class="success">Email sent successfully</div>
    	<%
    }else{
    	%>success,<%=toEmailAddress%><%
    }
}catch( Exception e ){
    MiscUtils.getLogger().error("Error", e);
    if(!featherlight.equals("true")){
    	%>
    	An error occurred sending the email, please contact an administrator.
		<%
    }else{
    	%><%=e.getMessage()%>,<%=toEmailAddress%><%
    }
    
}

%>
<% if(!featherlight.equals("true")){%>
</body>
</html>
<%}%>