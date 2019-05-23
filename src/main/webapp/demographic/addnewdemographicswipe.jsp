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

<%@ page import="org.oscarehr.util.MiscUtils" %>

<%
	String cardData = request.getParameter("card_no");
	String trimmedCardData = cardData.replaceAll("\"", "'");
	// TEMPORARY LOGGING
	// We want to determine what cards are being swiped that we don't know about
	MiscUtils.getLogger().info(cardData);
%>
<html>
<head>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/global.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/share/javascript/HealthCardParser.js"></script>
	<title>PATIENT DETAIL INFO</title>
	<link rel="stylesheet" href="../web.css"/>
</head>
<body topmargin="0" onLoad="setfocus()" leftmargin="0" rightmargin="0">
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr bgcolor="#486ebd">
		<th align=CENTER NOWRAP>
			<font face="Helvetica" color="#FFFFFF">PATIENT'S DETAIL RECORD</font>
		</th>
	</tr>
</table>
<table BORDER="0" CELLPADDING="1" CELLSPACING="0" WIDTH="100%" BGCOLOR="#C4D9E7">

	<script LANGUAGE="JavaScript">

		function setValueIfExists(element, value)
		{
			if (value)
			{
				value = value.trim();
				element.value = value;
			}
		}

		var rawCard = "<%=trimmedCardData%>";

		var healthCardData = Oscar.HealthCardParser.parse(rawCard);

		var form = self.opener.document.adddemographic;

		setValueIfExists(form.last_name,               healthCardData.data.lastName);
		setValueIfExists(form.first_name,              healthCardData.data.firstName);
		setValueIfExists(form.hin,                     healthCardData.data.hin);
		setValueIfExists(form.ver,                     healthCardData.data.versionCode);
		setValueIfExists(form.sex,                     healthCardData.data.sex);
		setValueIfExists(form.address,                 healthCardData.data.address);
		setValueIfExists(form.postal,                  healthCardData.data.postal);
		setValueIfExists(form.city,                    healthCardData.data.city);
		setValueIfExists(form.province,                healthCardData.data.province);
		setValueIfExists(form.hc_type,                 healthCardData.data.province);
		// If we somehow read from a card that has bad values, don't allow weird date values through
		// This will force the user inputting to the demographic form to correct errors before adding
        if (Oscar.HealthCardParser.validateDate(healthCardData.data.dobYear,
            healthCardData.data.dobMonth, healthCardData.data.dobDay))
        {
        	setValueIfExists(form.year_of_birth,       healthCardData.data.dobYear);
        	setValueIfExists(form.month_of_birth,      healthCardData.data.dobMonth);
        	setValueIfExists(form.date_of_birth,       healthCardData.data.dobDay);
        }

        if (Oscar.HealthCardParser.validateDate(healthCardData.data.effYear,
			healthCardData.data.effMonth, healthCardData.data.effDay))
        {
        	setValueIfExists(form.eff_date_year,       healthCardData.data.effYear);
			setValueIfExists(form.eff_date_month,      healthCardData.data.effMonth);
			setValueIfExists(form.eff_date_date,       healthCardData.data.effDay);
        }

        if (Oscar.HealthCardParser.validateDate(healthCardData.data.endYear,
            healthCardData.data.endMonth, healthCardData.data.endDay))
        {
			setValueIfExists(form.hc_renew_date_year,  healthCardData.data.endYear);
			setValueIfExists(form.hc_renew_date_month, healthCardData.data.endMonth);
			setValueIfExists(form.hc_renew_date_date,  healthCardData.data.endDay);
        }

		self.close();

	</script>
</table>
<br>
<br>
<form>
	<input type="button" name="Button" value="Cancel" onclick=self.close();>
</form>
</body>
</html>
