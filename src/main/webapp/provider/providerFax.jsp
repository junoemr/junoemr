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

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ page import="org.oscarehr.common.dao.UserPropertyDAO"%>
<%@ page import="org.oscarehr.common.model.UserProperty"%>
<%@ page import="org.oscarehr.util.SpringUtils"%>

<%
	if(session.getValue("user") == null)
	{
		response.sendRedirect("../logout.htm");
	}
	String curUser_no = (String) session.getAttribute("user");
%>
<html:html locale="true">
<head>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/global.js"></script>
	<html:base />
	<link rel="stylesheet" type="text/css" href="../oscarEncounter/encounterStyles.css">
	<title><bean:message key="provider.editRxFax.title" /></title>
	<script type="text/javascript">
		function validate()
		{
			var msg = "<bean:message key="provider.editRxFax.msgPhoneFormat" />";
			var strnum = document.forms[0].elements[0].value;

			if (strnum.length === 0)
			{
				alert("Please enter a fax number!");
				return false;
			}

			if(!strnum.match(/^\d{3}-\d{3}-\d{4}$/))
			{
				alert(msg);
				return false;
			}

			return true;
		}
	</script>
</head>

<body class="BodyStyle" vlink="#0000FF">

<table class="MainTable" id="scrollNumber1" name="encounterTable">
	<tr class="MainTableTopRow">
		<td class="MainTableTopRowLeftColumn"><bean:message key="provider.editRxFax.msgPrefs" /></td>
		<td style="color: white" class="MainTableTopRowRightColumn"><bean:message key="provider.editRxFax.msgProviderFaxNumber" /></td>
	</tr>
	<tr>
		<td class="MainTableLeftColumn">&nbsp;</td>
		<td class="MainTableRightColumn">
		<%

			UserPropertyDAO propertyDao = SpringUtils.getBean(UserPropertyDAO.class);
			UserProperty prop = propertyDao.getProp(curUser_no, UserProperty.PROVIDER_FAXNUMBER);
			String faxNum = "";
			if(prop != null)
			{
				faxNum = prop.getValue();
			}

			if(request.getAttribute("status") == null)
			{
		%>
			<html:form action="/EditFaxNum.do">
				<span style="color:blue">By entering in a value, you will
					<ul>
						<li>Override the fax # in prescriptions</li>
						<li> When choosing your letterhead in consult requests, the clinic fax # and your provider record's fax # will be overridden
						</li>
					</ul>
				</span>
				<br/>
				<html:text property="faxNumber" value="<%=faxNum%>" size="40" />
				<br>

				<input type="submit" onclick="return validate();"
					   value="<bean:message key="provider.editRxFax.btnSubmit"/>" />
			</html:form>
			<%

			}
			else if(request.getAttribute("status").equals("complete"))
			{
            %>
			<bean:message key="provider.editRxFax.msgSuccess" /> <br><%=faxNum%>
			<%
			}
            %>
		</td>
	</tr>
	<tr>
		<td class="MainTableBottomRowLeftColumn"></td>
		<td class="MainTableBottomRowRightColumn"></td>
	</tr>
</table>
</body>
</html:html>
