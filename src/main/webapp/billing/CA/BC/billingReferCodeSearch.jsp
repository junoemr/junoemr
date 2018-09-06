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

<%@page import="java.util.*"%>
<%@page import="org.oscarehr.util.SpringUtils" %>
<%@page import="org.oscarehr.common.model.Billingreferral" %>
<%@page import="org.oscarehr.common.dao.BillingreferralDao" %>

<%!
	private String formatReferralCodeForSearch(String referralCode)
	{
		final String SQL_WILDCARD = "%";

		if (referralCode == null || referralCode.isEmpty())
		{
			return(" ");
		}

		return referralCode + SQL_WILDCARD;
	}

	private ArrayList<String> formatNameForSearch(String name)
	{
		String firstName = "";
		String lastName = "";

		final String SQL_WILDCARD = "%";
		ArrayList<String> toReturn = new ArrayList<String>();

		// Entering an empty name on the web page results in an empty (not null) string here.
		name = name.trim();

		if (!name.isEmpty())
		{
			if (name.contains(","))
			{
				int commaIndex = name.indexOf(",");

				firstName = name.substring(0, commaIndex) + SQL_WILDCARD;
				lastName = name.substring(commaIndex + 1) + SQL_WILDCARD;
			}
			else
			{
				lastName = name + SQL_WILDCARD;
				firstName = SQL_WILDCARD;
			}
		}

		toReturn.add(lastName);
		toReturn.add(firstName);

		return toReturn;
	}
%>

<%
	BillingreferralDao billingReferralDao = (BillingreferralDao)SpringUtils.getBean("BillingreferralDAO");

	String user_no = (String) session.getAttribute("user");
	String search = request.getParameter("search");

	if (search == null || search.isEmpty()) {
		search = "search_referral_code";
	}

	String codeName = request.getParameter("name");
	String codeName1 = request.getParameter("name1");
	String codeName2 = request.getParameter("name2");

	String formName = request.getParameter("formName");
	String formElement = request.getParameter("formElement");

	if ( formName == null || formElement == null){
		formName = "";
		formElement = "";
	}
	ArrayList<String> paramList = new ArrayList<String>();

	paramList.add(formatReferralCodeForSearch(codeName));
	paramList.add(formatReferralCodeForSearch(codeName1));
	paramList.add(formatReferralCodeForSearch(codeName2));
	paramList.addAll(formatNameForSearch(codeName));
	paramList.addAll(formatNameForSearch(codeName1));
	paramList.addAll(formatNameForSearch(codeName2));

	List<Billingreferral> billingReferrals = billingReferralDao.searchReferralCode(paramList.get(0), paramList.get(1), paramList.get(2), paramList.get(3),
																				   paramList.get(4), paramList.get(5), paramList.get(6), paramList.get(7),
																				   paramList.get(8));
	int intCount = billingReferrals.size();
%>

<html>
<head>
	<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
	<title>Diagnostic Code Search</title>
	<style>
		tr:nth-child(even) {background-color: #FFFFFF;}
		tr:nth-child(odd) {background-color: #FFEEFF;}
		tr:first-child {background-color: #CCCCFF;}
		td {
			font-family: Arial, Helvetica, sans-serif;
			font-size: 0.82em;
		}
		.header {background-color: #CCCCFF;}
		.strong {font-weight: bold;}
	</style>
</head>

<body bgcolor="#FFFFFF" text="#000000">
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<th align=CENTER NOWRAP class="header">
			<span style="color:#000000">Referral Doctor</span> <span style="color:#FF0000">(Maximum 3 selections)</span>
		</th>
	</tr>
</table>
<form name="servicecode" id="servicecode" method="post" action="billingReferCodeUpdate.jsp">
	<input type="hidden" name="formName" value="<%=formName%>"/>
	<input type="hidden" name="formElement" value="<%=formElement%>"/>
	<table width="600" border="1">
		<tr>
			<td width="12%" class="header, strong">Code</td>
			<td width="22%" class="header, strong">Name</td>
			<td width="22%" class="header, strong">Specialty</td>
			<td width="22%" class="header, strong">City</td>
			<td width="22%" class="header, strong">Phone</td>
		</tr>
		<% for(Billingreferral billingReferral:billingReferrals) {
		    String Dcode = billingReferral.getReferralNo();
			String DcodeDesc = billingReferral.getLastName() + "," + billingReferral.getFirstName();
			String DcodeCity = billingReferral.getCity();
			String DcodeSpecialty = billingReferral.getSpecialty();
			String DcodePhone =billingReferral.getPhone();
		%>
		<tr>
			<td width="12%">
				<% if (Dcode.equals(codeName) || Dcode.equals(codeName1) || Dcode.equals(codeName2)) { %>
				<input type="checkbox" name="code_<%=Dcode%>" checked>
				<%} else {%>
				<input type="checkbox" name="code_<%=Dcode%>">
				<%}%>
				<%=Dcode%>
			</td>
			<td width="22%"><%=DcodeDesc != null ? DcodeDesc : ""%></td>
			<td width="22%"><%=DcodeSpecialty != null ? DcodeSpecialty : ""%></td>
			<td width="22%"><%=DcodeCity != null ? DcodeCity : ""%></td>
			<td width="22%"><%=DcodePhone != null ? DcodePhone : ""%></td>
		</tr>
		<% } %>
		<%  if (intCount == 0 ) { %>
		<tr>
			<td colspan="5">No match found.</td>
		</tr>
		<% } %>
	</table>
	<% if (intCount > 0) { %>
	<input type="submit" name="update" value="Confirm">
	<% } %>
	<input type="button" name="cancel" value="Cancel" onclick="window.close()">
</form>
</body>
</html>
