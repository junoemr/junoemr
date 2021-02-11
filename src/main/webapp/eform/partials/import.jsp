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
<html>
<%@ page import="java.util.List" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%
    String status = (String) request.getAttribute("status");
%>
<head>
    <link href="<%=request.getContextPath()%>/css/bootstrap.min.css" rel="stylesheet" type="text/css">
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/js/jquery_css/smoothness/jquery-ui-1.10.2.custom.min.css"/>
    <script type="text/javascript" src="<%= request.getContextPath() %>/js/jquery-1.9.1.js"></script>
    <script type="text/javascript" src="<%= request.getContextPath() %>/js/jquery-ui-1.10.2.custom.min.js"></script>

    <script>
		$(function ()
		{
			//x$( document ).tooltip();
		});

		function reload()
		{
			var url = "<%= request.getContextPath() %>/eform/efmformmanager.jsp";
			$("#eformTbl", window.parent.document).load(url + " #eformTbl");
		}

		function getHeight()
		{
			window.frames.frameElement.height = "210";
		}

    </script>
</head>

<style>
    body {
        background-color: #f5f5f5;
    }
</style>

<body>

<form action="<%=request.getContextPath()%>/eform/manageEForm.do" method="POST" enctype="multipart/form-data" id="eformImportForm">
    <input type="hidden" name="method" value="importEForm">
    Import eForm:<br/>
    <%
        if(status != null)
        {
    %>
    <script>reload();</script>
    <%
        List<String> importErrors = (List<String>) request.getAttribute("importErrors");
        if(importErrors != null && importErrors.size() > 0)
        {
            for(String importError : importErrors)
            { %>
    <span class="text-error">
                    <%
                        if(importError.startsWith("Skipped"))
                        {
                            out.write("<b>" + importError + "</b>");
                        }
                        else
                        {
                            out.write(importError);
                        }
                    %>
                </span><br>
    <% }
    }
    else
    { %>
    <div class="alert alert-success">
        <button type="button" class="close" data-dismiss="alert">&times;</button>
        <strong>Success!</strong> Your eform was imported.
    </div>
    <% }
    } %>

    <input type="file" name="zippedForm" accept=".zip" size="50">
    <span title="<bean:message key='global.uploadWarningBody'/>"
          style="vertical-align:middle;font-family:arial;font-size:20px;font-weight:bold;color:#ABABAB;cursor:pointer"><img
            border="0" src="../../images/icon_alertsml.gif"/></span></span>
    <input type="button" name="subm" value="Import" class="btn"
           onclick="this.value = 'Importing...'; this.disabled = true; this.form.submit(); getHeight();"><br>
    <span class="label label-info">Info: </span>
    <strong>
        <bean:message key='global.importZipOnly'/>
    </strong>
</form>


</body>
</html>
