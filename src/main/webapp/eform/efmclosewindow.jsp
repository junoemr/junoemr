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

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>eForm Closing</title>
    <script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-1.12.3.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-ui-1.10.2.custom.min.js"></script>

    <script type="text/javascript">

		function closePage()
		{
			if (window.opener && window.opener.closed == false)
			{
				var parentAjaxId = "<%=request.getParameter("parentAjaxId")%>";
				if (window.opener.writeToEncounterNote)
				{
					window.opener.reloadNav(parentAjaxId);
				}
				else
				{
					if (window.opener.location.href.indexOf("efmpatientformlist.jsp") >= 0)
						window.opener.location.reload();
				}
				window.opener.focus();
			}
			window.close();
		}
    </script>
</head>
<body>
<div id="closeDialog">
    <p>
		This eForm is attempting to record measurements to the patient's eChart that do not match your current measurement types:
    </p>
    <c:forEach var="unsavedMeasurement" items="${measurements_unsaved}">
        <li>${unsavedMeasurement}</li>
    </c:forEach>
    <p>
        If you would like to adjust your measurement types, please consult
        <a target="_blank"
           href="https://help.junoemr.com/support/solutions/articles/3000088272-measurement-customization">
            this guide.
        </a>
    </p>
</div>

<script type="text/javascript">
	<%
        if (request.getAttribute("measurements_unsaved") != null)
        {
    %>
	$(function ()
	{
		$("#closeDialog").dialog({
			autoOpen: true,
			dialogClass: "no-close",
			buttons: [
				{
					text: "OK, Close Window",
					click: function ()
					{
						closePage();
					}
				}
			],
			modal: false,
			open: function ()
			{
				$(".ui-dialog-titlebar-close").hide();
			},
			width: 600
		});
	});
	<%
        }
        else
        {
    %>
	closePage();
	<%
        }
    %>
</script>
</body>
</html>
