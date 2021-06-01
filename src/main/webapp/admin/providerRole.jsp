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

<%@ taglib uri="/WEB-INF/security.tld" prefix="security" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ page errorPage="../errorpage.jsp" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.oscarehr.common.dao.SecRoleDao" %>
<%@ page import="org.oscarehr.common.model.SecRole" %>
<%@ page import="org.oscarehr.provider.dao.ProviderDataDao" %>
<%@ page import="org.oscarehr.provider.model.ProviderData" %>
<%@ page import="org.oscarehr.util.SpringUtils" %>
<%@ page import="oscar.OscarProperties" %>
<%@ page import="oscar.admin.transfer.ProviderRoleTransfer" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="oscar.util.ConversionUtils" %>

<%
	SecRoleDao secRoleDao = SpringUtils.getBean(SecRoleDao.class);
	ProviderDataDao providerDao = SpringUtils.getBean(ProviderDataDao.class);

	OscarProperties props = OscarProperties.getInstance();

	String roleName$ = (String) session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
	String curUser_no = (String) session.getAttribute("user");

	ProviderData currentProvider = providerDao.findByProviderNo(curUser_no);

	boolean isSiteAccessPrivacy = false;
	boolean authed = true;
	boolean isCurrentLoginSuperAdmin = currentProvider.isSuperAdmin();

%>

<security:oscarSec roleName="<%=roleName$%>" objectName="_admin,_admin.userAdmin" rights="r" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect("../securityError.jsp?type=_admin&type=_admin.userAdmin");%>
</security:oscarSec>
<%
	if(!authed)
	{
		return;
	}
%>
<security:oscarSec objectName="_site_access_privacy" roleName="<%=roleName$%>" rights="r" reverse="false"><%isSiteAccessPrivacy=true; %></security:oscarSec>

<%
	String msg = StringUtils.trimToEmpty((String)request.getAttribute("message"));
	boolean unauthorizedMSG = ConversionUtils.parseBoolean((String) request.getAttribute("messageNotAuthorized"));
	// get role from database
	List<SecRole> secRoles;
	String[] omitList = null;

	if(isSiteAccessPrivacy)
	{
		String omit = props.getProperty("multioffice.admin.role.name");
		if(omit != null)
		{
			omitList = new String[1];
			omitList[0] = omit;
		}
	}
	secRoles = secRoleDao.findAllOrderByRole(omitList);
	String keyword = request.getParameter("keyword") != null ? request.getParameter("keyword") : "";

	String lastName = "";
	String firstName = "";
	String[] temp = keyword.split("\\,");
	if(temp.length > 1)
	{
		lastName = temp[0] + "%";
		firstName = temp[1] + "%";
	}
	else
	{
		lastName = keyword + "%";
		firstName = "%";
	}

	List<ProviderRoleTransfer> providerList = providerDao.findProviderSecUserRoles(lastName, firstName);
%>
<html>
<head>
	<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/admin/provider/providerProfile.js"></script>
	<title>
		PROVIDER
	</title>
	<link rel="stylesheet" href="../css/receptionistapptstyle.css">
	<script src="../js/jquery-1.7.1.min.js"></script>

	<script>
		$(document).ready(function()
		{
				<%
			if(unauthorizedMSG)
			{
				%>
				alert(document.getElementById("no_authorization_error_msg").value);
				<%
			}
				%>
		});

		function setfocus()
		{
			this.focus();
			document.forms[0].keyword.select();
		}

		function submit(form)
		{
			form.submit();
		}
		var items = [];
		<%
		for(ProviderRoleTransfer transfer : providerList)
		{
			%>
			item = {
				providerNo: "<%=transfer.getProviderId()%>",
				role_id: "<%=transfer.getRoleId()%>",
				roleName: "<%=transfer.getRoleName()%>"};
			items.push(item);
			<%
		}
		%>
		$(document).ready(function ()
		{
			$("#primaryRoleProvider").val("");
		});

		function primaryRoleChooseProvider()
		{
			$("#primaryRoleRole").find('option').remove();
			var provider = $("#primaryRoleProvider").val();
			for (var i = 0; i < items.length; i++)
			{
				if (items[i].providerNo == provider && items[i].role_id != "")
				{
					$("#primaryRoleRole").append('<option value="' + items[i].roleName + '">' + items[i].roleName + '</option>');
				}
			}
		}

		function setPrimaryRole()
		{
			var providerNo = $("#primaryRoleProvider").val();
			var roleName = $("#primaryRoleRole").val();
			if (providerNo != '' && roleName != '')
			{
				return true;
			}
			else
			{
				alert('Please enter in a provider and a corresponding role');
				return false;
			}
		}

		function updateProviderRoles(form, actionMethod, isClickedProviderSuperAdmin)
		{
			var isLoginUserSuperAdmin = <%=isCurrentLoginSuperAdmin%>;
			var hasPermission = Juno.Admin.Provider.Profile.checkProviderPermission(isLoginUserSuperAdmin, isClickedProviderSuperAdmin,document.getElementById("no_authorization_error_msg").value);
			if (hasPermission)
			{
				form.action = "providerRole.do?method=" + actionMethod;
			}
			return hasPermission;
		}
	</script>

</head>
<body bgproperties="fixed" bgcolor="ivory" onLoad="setfocus()" topmargin="0" leftmargin="0" rightmargin="0">
<input type="hidden" id="no_authorization_error_msg" value="<bean:message key="admin.securityaddsecurity.msgProviderNoAuthorization" />">
<form name="myform" action="providerRole.jsp" method="POST">
	<table border="0" cellspacing="0" cellpadding="0" width="100%">
		<tr bgcolor="#486ebd">
			<th align="CENTER" width="90%">
				<font face="Helvetica" color="#FFFFFF">
					<% if(msg.length() > 1)
					{%>
					<%=msg%>
					<% } %>
				</font>
			</th>
			<td nowrap>
				<font size="-1" color="#FFFFFF">
					Name:
					<input type="text" name="keyword" size="15" value="<%=keyword%>"/>
					<input type="submit" name="search" value="Search">
				</font>
			</td>
		</tr>
	</table>
</form>

<table width="100%" border="0" bgcolor="ivory" cellspacing="1" cellpadding="1">
	<tr bgcolor="mediumaquamarine">
		<th colspan="6" align="left">
	</tr>
	<tr bgcolor="silver">
		<th width="10%" nowrap>ID</th>
		<th width="20%" nowrap><b>First Name</b></th>
		<th width="20%" nowrap><b>Last Name</b></th>
		<th width="10%" nowrap>
			Role
		</th>
		<th width="10%" nowrap>
			Primary Role
		</th>
		<th nowrap>Action</th>
	</tr>
	<%
		String[] colors = {"#ccCCFF", "#EEEEFF"};
		int i=-1;
		for(ProviderRoleTransfer transfer : providerList)
		{
			i++;
			String providerNo = transfer.getProviderId();
			boolean isClickedAccountSuperAdmin = transfer.isSuperAdmin();

			String roleIdStr = (transfer.getRoleId() == null)? "" : String.valueOf(transfer.getRoleId());
	%>
	<form name="myform<%= providerNo %>" action="providerRole.do" method="POST">
		<tr bgcolor="<%=colors[i%2]%>">
			<td><%= providerNo %>
			</td>
			<td><%= StringUtils.trimToEmpty(transfer.getFirstName())%>
			</td>
			<td><%= StringUtils.trimToEmpty(transfer.getLastName()) %>
			</td>
			<td align="center">
				<select name="roleNew">
					<option value="-">-</option>
					<%
						for(SecRole role : secRoles)
						{
							String secRoleName = role.getName();
					%>
					<option value="<%=secRoleName%>" <%=secRoleName.equals(transfer.getRoleName()) ? "selected" : "" %>>
						<%=secRoleName%>
					</option>
					<%
						}
					%>
				</select>
			</td>
			<td align="center">
				<%=(transfer.hasPrimaryRoleId()) ? "Yes" : "" %>
			</td>

			<td align="center">
				<input type="hidden" name="keyword" value="<%=keyword%>"/>
				<input type="hidden" name="providerId" value="<%=providerNo%>">
				<input type="hidden" name="roleId" value="<%= roleIdStr %>">
				<input type="hidden" name="roleOld" value="<%= StringUtils.trimToEmpty(transfer.getRoleName())%>">
				<input type="submit" name="submit" value="Add"
				       onclick="return updateProviderRoles(this.form, 'addRole', <%=isClickedAccountSuperAdmin%>);">
				-
				<input type="submit" name="buttonUpdate" value="Update"
				       onclick="return updateProviderRoles(this.form, 'updateRole', <%=isClickedAccountSuperAdmin%>);" <%= transfer.hasRole()?"":"disabled"%>>
				-
				<input type="submit" name="submit" value="Delete"
				       onclick="return updateProviderRoles(this.form, 'deleteRole', <%=isClickedAccountSuperAdmin%>);" <%= transfer.hasRole()?"":"disabled"%>>
			</td>
		</tr>
	</form>
	<%
		}
	%>
</table>
<hr>
<form name="myform" action="programProviderRole.do?method=setPrimaryRole" method="POST">
	<table>
		<tr>
			<td colspan="2">Set primary role</td>
		</tr>
		<tr>
			<td>Provider:</td>
			<td>
				<select id="primaryRoleProvider" name="primaryRoleProvider" onChange="primaryRoleChooseProvider()">
					<option value="">Select Below</option>
					<%
						Set<String> temp1 = new HashSet<String>();
						for(ProviderRoleTransfer transfer : providerList)
						{
							String providerNo = transfer.getProviderId();
							if(!temp1.contains(providerNo))
							{
					%>
					<option value="<%=providerNo%>"><%=transfer.getLastName() + "," + transfer.getFirstName() %>
					</option>
					<%
								temp1.add(providerNo);
							}
						}
					%>
				</select>
			</td>
		</tr>
		<tr>
			<td>Role:</td>
			<td>
				<select id="primaryRoleRole" name="primaryRoleRole"></select>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<input type="submit" name="buttonSetPrimaryRole" value="Set Primary Role" onClick="return setPrimaryRole();"/>
			</td>
		</tr>
	</table>
</form>


</body>
</html>
