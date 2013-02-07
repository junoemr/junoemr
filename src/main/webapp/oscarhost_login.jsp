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
<%@page import="oscar.OscarProperties, javax.servlet.http.Cookie, oscar.oscarSecurity.CookieSecurity, oscar.login.UAgentInfo" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/caisi-tag.tld" prefix="caisi" %>
<caisi:isModuleLoad moduleName="ticklerplus"><%
    if(session.getValue("user") != null) {
        response.sendRedirect("provider/providercontrol.jsp");
    }
%></caisi:isModuleLoad><%
OscarProperties props = OscarProperties.getInstance();

// clear old cookies
Cookie rcpCookie = new Cookie(CookieSecurity.receptionistCookie, "");
Cookie prvCookie = new Cookie(CookieSecurity.providerCookie, "");
Cookie admCookie = new Cookie(CookieSecurity.adminCookie, "");
rcpCookie.setPath("/");
prvCookie.setPath("/");
admCookie.setPath("/");
response.addCookie(rcpCookie);
response.addCookie(prvCookie);
response.addCookie(admCookie);

// Initialize browser info variables
String userAgent = request.getHeader("User-Agent");
String httpAccept = request.getHeader("Accept");
UAgentInfo detector = new UAgentInfo(userAgent, httpAccept);

// This parameter exists only if the user clicks the "Full Site" link on a mobile device
if (request.getParameter("full") != null) {
    session.setAttribute("fullSite","true");
}

// If a user is accessing through a smartphone (currently only supports mobile browsers with webkit),
// and if they haven't already clicked to see the full site, then we set a property which is
// used to bring up iPhone-optimized stylesheets, and add or remove functionality in certain pages.
if (detector.detectSmartphone() && detector.detectWebkit()  && session.getAttribute("fullSite") == null) {
    session.setAttribute("mobileOptimized", "true");
} else {
    session.removeAttribute("mobileOptimized");
}
Boolean isMobileOptimized = session.getAttribute("mobileOptimized") != null;
%>
<!DOCTYPE html>
<head>
    <meta name="Description" content="">
    <meta http-equiv="X-UA-Compatible" content="IE=9" />
    <meta name="google-site-verification" content="wkkWfXYDSP4fhwOBAhlxyYURFcRtPJ6HfSPHCNkpKos" />  
    <title>Oscar Host - OSCAR Electronic Medical Records Hosting | </title>
    <link rel="stylesheet" href="https://www.oscarhost.ca/css/default.css"/>
    <link rel="stylesheet" href="https://www.oscarhost.ca/css/bootstrap.css" type="text/css">
    <link rel="stylesheet" href="https://www.oscarhost.ca/css/bootstrap-responsive.css" type="text/css">
</head>
<body>
    <div id="header" class="navbar">
        <div class="redline"></div>
                <div id="logo">
                    <div class="logo"></div>
                    <div class="logotext"><a href="https://www.oscarhost.ca" class="brand"><span class="redfont">Oscar</span><span class="darkfont">Host</span><span class="greyfont">.ca</span></a></div>
                </div>
    </div>
            <div id="container_box" class="container page login">
        <div id="content">
            <html:form action="login" >
                <div class="header">
                    <span>Account Login:</span>
                    <span class="red">
                         <% if (request.getParameter("instance_id") !=null ) { %>
                            <%=request.getParameter("instance_id")%>
                            <input type="hidden" name="practice_name" value="<%=request.getParameter("instance_id")%>"/>
                         <% } %>
                    </span>
                </div>
                <div class="login_form">
                    <%
                    String key = "loginApplication.formLabel" ;
                    if(request.getParameter("login")!=null && request.getParameter("login").equals("failed") ){
                        key = "loginApplication.formFailedLabel" ;
                        %>
                        <div class="alert alert-error">
                            <bean:message key="<%=key%>"/> 
                        </div>
                        <%
                    }
                    %>
                    
                    <div class="text">
                        <label for="username">Username</label>
                        <span class="form_element"><input type="text" id="username" name="username" class="input-medium"/><script type="text/javascript">document.getElementById('username').focus()</script></span>
                    </div>
                    <div class="text">
                        <label for="password">Password</label>
                        <span class="form_element"><input autocomplete="off" type="password" name="password" class="input-medium"/></span>
                    </div>
                    <div class="text">
                        <label for="pin">2nd Level Passcode</label>
                        <span class="form_element"><input autocomplete="off" type="password" name="pin" class="input-small"/></span>
                    </div>
                    <div class="text">
                        <label>&nbsp;</label>
                        <span class="form_element">
                            <button type="submit" class="btn">Sign In</button>      
                            <a class="forgotpassword" href="#">Forgot your password?</a>
                        </span>
                    </div>
                    <div id="forgotpassword">
                        <div class="alert alert-block">
                            <h4>Forgot your password?</h4>
                            If you need help logging in to OSCAR , please contact us at <a href="mailto:support@oscarhost.ca">support@oscarhost.ca</a>
                        </div>
                    </div>
                    <div id="error_message" class="login_error_text"></div>
                </div>
            </html:form>
        </div>
        <div class="content">
            <p><strong>Please note:</strong></p>
            <p>Most browsers will probably work with OSCAR but the development team has been primarily using the latest versions of Firefox. The user interface assumes that the resolution of the screen is at least 1024x768.</p>
            <p>You can redistribute it and/or modify it under the terms of the GNU General Public License version 2 as published by the Free Software Foundation.</p>
            <p>By using this service provided by Oscar Host by Cloud Practice Inc you agree to the terms and conditions <a href="terms">here</a>.</p>
        </div>
    </div>
</body>
