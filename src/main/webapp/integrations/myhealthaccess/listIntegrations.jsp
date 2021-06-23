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

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" scope="request"/>
<%@ page import="org.oscarehr.integration.model.Integration" %>
<%@ page import="java.util.List" %>
<%@ page import="org.oscarehr.util.MiscUtils" %>
<%@ page import="org.oscarehr.integration.model.IntegrationStatus" %>
<%@ page import="org.oscarehr.integration.myhealthaccess.dto.ClinicStatusResponseTo1" %>

<%
    List<IntegrationStatus> integrationStatusList = (List<IntegrationStatus>) session.getAttribute("integrations");
    String context = request.getContextPath();
%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>MyHealthAccess User Confirmation</title>
    <link rel="stylesheet" href="${ctx}/library/bootstrap/3.0.0/css/bootstrap.min.css">
    <link rel="stylesheet" href="./css/myhealthaccess.css">
    <script src="${ctx}/js/jquery-1.9.1.min.js"></script>
    <script src="${ctx}/library/bootstrap/3.0.0/js/bootstrap.min.js"></script>
</head>
<body>
    <div class="container">
        <div class="starter-template">
            <div class="jumbotron">
                <h1>MyHealthAccess Integrations</h1>
            </div>
            <%
                if (integrationStatusList == null || integrationStatusList.size() == 0)
                {
            %>
                    <p class="lead">
                        You don't have an active MyHealthAccess integration. Verify your connection settings in
                        MyHealthAccess and press <em>Generate API Key</em> at the bottom of the page.
                    </p>
            <%
                }
                else
                {
            %>
                    <div class="row header">
                        <div class="col-md-4">Site</div>
                        <div class="col-md-4">MyHealthAccess ID</div>
                        <div class="col-md-4">Status</div>
                    </div>
                    <ul class="list-group">
            <%
                for (IntegrationStatus integrationStatus : integrationStatusList)
                {
                	Integration integration = integrationStatus.getIntegration();
                    ClinicStatusResponseTo1 status = integrationStatus.getConnectionStatus();
                    String statusId = status.getStatusIdentifier();
                    String siteName = "";

                    if (integration.getSite() != null)
                    {
                        siteName = integration.getSite().getName();
                    }

                    String connectUrl = context + "/integrations/myhealthaccess.do?method=connect&siteName=" + siteName;
                    String classList = "list-group-item list-group-item-action row";

                    if (status.getStatus() == 200)
                    {
                %>
                        <a href="<%=connectUrl%>" class="connected <%=classList%>">
                            <div class="col-md-4"><%=siteName == "" ? "No Site" : siteName%></div>
                            <div class="col-md-4"><%=integration.getRemoteId()%></div>
                            <div class="col-md-4 status"><%=status.getMessage()%></div>
                        </a>
                <%
                    }
                    else
                    {
                %>
                        <div class="row list-group-item disconnected">
                            <div class="col-md-4"><%=siteName%></div>
                            <div class="col-md-4"><%=integration.getRemoteId()%></div>
                            <div class="col-md-4 status"><%=status.getMessage()%></div>
                        </div>
<%--                    <div class="row integration">--%>
<%--                        <div class="col-md-4">--%>
<%--                            <a href="${ctx}/integrations/myhealthaccess.do?method=connect&siteName=<%=siteName%>"><%=siteName%></a>--%>
<%--                        </div>--%>
<%--                        <div class="col-md-4"><%=integration.getRemoteId()%></div>--%>
<%--                        <div class="col-md-4">TODO-legacy: $STATUS</div>--%>
<%--                    </div>--%>
        <%
                    }
                }
            }
        %>
            </ul>
        </div>
    </div>
</body>
</html>


