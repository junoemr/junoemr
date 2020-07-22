<%--

    Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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

    This software was written for the
    Department of Family Medicine
    McMaster University
    Hamilton
    Ontario, Canada

--%>

<%@page import="javax.servlet.http.Cookie, oscar.oscarSecurity.CookieSecurity, oscar.login.UAgentInfo" %>
<%@ page import="oscar.OscarProperties" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/caisi-tag.tld" prefix="caisi" %>
<caisi:isModuleLoad moduleName="ticklerplus"><%
    if (session.getValue("user") != null)
    {
        response.sendRedirect("provider/providercontrol.jsp");
    }
%></caisi:isModuleLoad><%
    // clear old cookies
    Cookie prvCookie = new Cookie(CookieSecurity.providerCookie, "");
    prvCookie.setPath("/");
    response.addCookie(prvCookie);


    // Initialize browser info variables
    String userAgent = request.getHeader("User-Agent");
    String httpAccept = request.getHeader("Accept");
    UAgentInfo detector = new UAgentInfo(userAgent, httpAccept);

    // This parameter exists only if the user clicks the "Full Site" link on a mobile device
    if (request.getParameter("full") != null)
    {
        session.setAttribute("fullSite", "true");
    }

    // If a user is accessing through a smartphone (currently only supports mobile browsers with webkit),
    // and if they haven't already clicked to see the full site, then we set a property which is
    // used to bring up iPhone-optimized stylesheets, and add or remove functionality in certain pages.
    if (detector.detectSmartphone() && detector.detectWebkit() && session.getAttribute("fullSite") == null)
    {
        session.setAttribute("mobileOptimized", "true");
    }
    else
    {
        session.removeAttribute("mobileOptimized");
    }

    OscarProperties props = oscar.OscarProperties.getInstance();
    String googleClientID = props.getGoogleClientID();
%>
<html:html locale="true">
    <head>
        <link rel="shortcut icon" href="images/Oscar.ico"/>
        <% if (googleClientID != null && !googleClientID.isEmpty())
        {
        %>
        <meta name="google-signin-client_id" content="<%=googleClientID%>">

        <!-- jQuery -->
        <script type="text/javascript"
                src="<%=request.getContextPath()%>/share/javascript/jquery/jquery-2.2.4.min.js"></script>

        <!-- Google OAuth -->
        <script>
            function init()
            {
                gapi.signin2.render('juno-signin2', {
                    'scope': 'profile email',
                    'theme': 'dark',
                    'onsuccess': onSuccess,
                    'onfailure': onFailure
                });
            }

            <% if (request.getParameter("login") != null && request.getParameter("login").equals("failed"))
            { %>

            function onSuccess(googleUser)
            {
                console.warn("Login failed, " + googleUser.getBasicProfile().getEmail() + " does not have access");
            }

            <% }
            else
            { %>

            function onSuccess(googleUser)
            {
                var id_token = googleUser.getAuthResponse().id_token;
                var form = jQuery('<form action="./action.do" method="post">' +
                    '<input type="hidden" name="idtoken" value="' + id_token + '" />' +
                    '</form>');
                jQuery('body').append(form);
                form.submit();
            }

            <% } %>

            function onFailure(error)
            {
                console.error(error);
            }
        </script>

        <script src="https://apis.google.com/js/platform.js?onload=init" async defer></script>
        <%
            }
        %>
    </head>

    <body>

    <% if (request.getParameter("login") != null && request.getParameter("login").equals("failed"))
    { %>
    <span style="color:red;"><bean:message key="loginApplication.formFailedLabel"/></span>
    <% } %>
    <div id="juno-signin2"></div>
    </body>
</html:html>
