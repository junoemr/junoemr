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
<%@ page import="org.oscarehr.common.model.UserProperty" %>
<%@ page import="org.oscarehr.common.dao.UserPropertyDAO" %>
<%@ page import="org.oscarehr.util.SpringUtils" %>
<%@ page import="oscar.util.ConversionUtils" %>
<%@ page import="org.oscarehr.util.LoggedInInfo" %>
<%
	String providerNo = LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProvider().getProviderNo();
	UserPropertyDAO userPropertyDAO = SpringUtils.getBean(UserPropertyDAO.class);
	UserProperty eformPopupWidthProp = userPropertyDAO.getProp(providerNo, UserProperty.EFORM_POPUP_WIDTH);
	UserProperty eformPopupHeightProp = userPropertyDAO.getProp(providerNo, UserProperty.EFORM_POPUP_HEIGHT);

	boolean isError = ConversionUtils.parseBoolean(request.getParameter("error"));

	String eformPopupWidth = "700";
	if (request.getParameter("eformPopupWidth") != null)
	{
		eformPopupWidth = request.getParameter("eformPopupWidth");
	}
	else if (eformPopupWidthProp != null)
	{
		eformPopupWidth = eformPopupWidthProp.getValue();
	}

	String eformPopupHeight = "800";
	if (request.getParameter("eformPopupHeight") != null)
	{
		eformPopupHeight = request.getParameter("eformPopupHeight");
	}
	else if (eformPopupHeightProp != null)
	{
		eformPopupHeight = eformPopupHeightProp.getValue();
	}

%>
<html>
<head>
	<title>Provider Eform Settings</title>
	<link rel="stylesheet" type="text/css" href="../oscarEncounter/encounterStyles.css">
	<style>
		.page-header {
			background-color: #003399;
			color: white;
			padding-left: 20px
		}

		.error-text {
			color: red;
		}

		h3 {
			font-size: 16px;
			padding-bottom: 10px;
		}
		input {
			margin-top: 4px;
			margin-bottom: 8px;
		}
	</style>

	<script type="text/javascript">
		function validateWidthHeight()
		{
			var width = document.getElementById("eform_width").value;
			var height = document.getElementById("eform_height").value;

			var isValid = true;

			if (isNaN(width))
			{
				alert("Width of '" + width + "' is not valid (was expecting a number).");
				isValid = false;
			}

			if (isNaN(height))
			{
				alert("Height of '" + height + "' is not valid (was expecting a number).");
				isValid = false;
			}

			return isValid;
		}
	</script>
</head>
<body>
	<h2 class="page-header">Eform Settings</h2>
	<form action="../tickler/updateEformSettings.do">
		<% if (isError) { %>
		<div>
			<h4 class="error-text" style="min-height: 25px">Invalid Eform width/height</h4>
		</div>
		<% } %>
		<input type="hidden" name="method" value="setSettings">
		<div>
			<label for="eform_width">Popup Width:</label>
			<input id="eform_width" name="eformPopupWidth" type="text" value="<%=eformPopupWidth%>" maxlength="10">
		</div>
		<div>
			<label for="eform_height">Popup Height:</label>
			<input id="eform_height" name="eformPopupHeight" type="text" value="<%=eformPopupHeight%>" maxlength="10">
		</div>
		<input type="submit" value="Save" onclick="return validateWidthHeight()">
	</form>
</body>
</html>
