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

<%
	String cardData = request.getParameter("card_no");
	cardData = cardData.replaceAll("\"", "'");
%>
<html>
<head>
	<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/web/common/util/HealthCardParser.js"></script>
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
				element.value = value.trim();
			}
		}

		function setYearIfValid(element, year)
		{
			if (year > 1800 && year < 9999)
			{
				element.value = year.trim();
			}
		}

		function setMonthIfValid(element, month)
		{
			if (month >= 1 && month <= 12)
			{
				element.value = month.trim();
			}
		}

		function setDateIfValid(element, year, month, day)
		{

			// Valid month in [1, 12] range so pad arr[0] to allow us to plug in a valid month as-is
			var month_to_date_mapping = [0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
			// Take into account leap years
			if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0))
			{
				month_to_date_mapping[2] = 29;
			}
			// Need to also check month since we're using it as a possible index
			if (month >= 1 && month <= 12 &&
					value >= 1 && value <= month_to_date_mapping[month])
			{
				element.value = day.trim();
			}
		}

		var healthCardData = Oscar.HealthCardParser.parse("<%=cardData%>");

		var form = self.opener.document.adddemographic;

		setValueIfExists(form.last_name,            healthCardData.data.lastName);
		setValueIfExists(form.first_name,           healthCardData.data.firstName);
		setValueIfExists(form.hin,                  healthCardData.data.hin);
		setValueIfExists(form.ver,                  healthCardData.data.versionCode);
		setValueIfExists(form.sex,                  healthCardData.data.sex);
		setValueIfExists(form.address,              healthCardData.data.address);
		setValueIfExists(form.postal,               healthCardData.data.postal);
		setValueIfExists(form.city,                 healthCardData.data.city);
		setValueIfExists(form.province,             healthCardData.data.province);
		setValueIfExists(form.hc_type,              healthCardData.data.province);
		// If we somehow read from a card that has bad values, don't allow weird date values through
		// This will force the user inputting to the demographic form to correct errors before adding
		setYearIfValid(form.year_of_birth,          healthCardData.data.dobYear);
		setMonthIfValid(form.month_of_birth,        healthCardData.data.dobMonth);
		setDateIfValid(form.date_of_birth, healthCardData.data.dobYear,
				healthCardData.data.dobMonth, healthCardData.data.dobDay);
		setYearIfValid(form.eff_date_year,          healthCardData.data.effYear);
		setMonthIfValid(form.eff_date_month,        healthCardData.data.effMonth);
		setDateIfValid(form.eff_date_date, healthCardData.data.effYear,
				healthCardData.data.effMonth, healthCardData.data.effDay);
		setYearIfValid(form.hc_renew_date_year,     healthCardData.data.endYear);
		setMonthIfValid(form.hc_renew_date_month,   healthCardData.data.endMonth);
		setDateIfValid(form.hc_renew_date_date, healthCardData.data.endYear,
				healthCardData.data.endMonth, healthCardData.data.endDay);

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
