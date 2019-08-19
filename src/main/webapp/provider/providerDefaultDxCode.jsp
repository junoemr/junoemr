<%--


    Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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
    Centre for Research on Inner City Health, St. Michael's Hospital,
    Toronto, Ontario, Canada

--%>
<%@page import="org.oscarehr.common.dao.ProviderPreferenceDao"%>
<%@page import="org.oscarehr.common.model.ProviderPreference"%>
<%@page import="org.oscarehr.util.SpringUtils"%>
<%@ page import="org.oscarehr.util.LoggedInInfo" %>


<script type="text/javascript">
	var remote = null;
	var awnd = null;

	function rs(n, u, w, h, x)
	{
		args = "width=" + w + ",height=" + h + ",resizable=yes,scrollbars=yes,status=0,top=60,left=30";
		remote = window.open(u, n, args);
	}

	function dxScriptAttach(name2)
	{
		ff = eval("document.forms[0].elements['" + name2 + "']");
		f0 = ff.value;
		f1 = escape("document.forms[0].elements[\'" + name2 + "\'].value");
		awnd = rs('att', '../billing/CA/ON/billingDigSearch.jsp?name=' + f0 + '&search=&name2=' + f1, 600, 600, 1);
		awnd.focus();
	}
</script>

<%
	LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);
	String providerNo = loggedInInfo.getLoggedInProviderNo();

	ProviderPreferenceDao preferenceDao = (ProviderPreferenceDao) SpringUtils.getBean("providerPreferenceDao");
	ProviderPreference preference = preferenceDao.find(providerNo);

	String defaultDxCode;
	if(preference != null && preference.getDefaultDxCode() != null)
	{
		defaultDxCode = preference.getDefaultDxCode();		
	}
	else
	{
	    defaultDxCode = "";
	}
%>



<form id="preference_form" name="preference_form" action="preference_action.jsp" method="post">
	<table style="margin-left:auto;margin-right:auto;background-color:#f0f0f0;border-collapse:collapse">
		<tr>
			<td>
				<label for="dxCode">Dx Code</label>
			 	<input type="text" id="dxCode" name="dxCode" size="5" maxlength="5"
					   ondblClick="dxScriptAttach('dxCode')" onchange="changeCodeDesc();" value="<%=defaultDxCode%>" />
					<a href=# onclick="dxScriptAttach('dxCode');">Search</a>
			</td>
		</tr>
		<tr>
			<td>
				<input type="submit" name="submit" value="Save"/>
				<input type="button" name="close" value="Close" onclick="window.close()" />
			</td>
		</tr>
	</table>
	
</form>
