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

<%@page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@page import="org.oscarehr.util.LoggedInInfo" %>
<%@page import="oscar.OscarProperties" %>

<%
	boolean fromMessenger = request.getParameter("fromMessenger") == null ? false : (request.getParameter("fromMessenger")).equalsIgnoreCase("true") ? true : false;
	String roleName = (String) session.getAttribute("userrole") + "," + (String) session.getAttribute("user");

	String perPageStr = request.getParameter("limit2");
	if(perPageStr == null || perPageStr.trim().isEmpty()) perPageStr = "10";
	Integer perPage = Integer.parseInt(perPageStr);
%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security" %>
<%@ taglib uri="/WEB-INF/caisi-tag.tld" prefix="caisi" %>

<script language="JavaScript">
	function searchInactive()
	{
		document.titlesearch.ptstatus.value = "inactive";
		if (checkTypeIn()) document.titlesearch.submit();
	}

	function searchAll()
	{
		document.titlesearch.ptstatus.value = "";
		if (checkTypeIn()) document.titlesearch.submit();
	}

	function searchOutOfDomain()
	{
		document.titlesearch.outofdomain.value = "true";
		if (checkTypeIn()) document.titlesearch.submit();
	}

</script>

<form method="get" name="titlesearch" action="<%=request.getContextPath()%>/demographic/demographiccontrol.jsp"
      onsubmit="return checkTypeIn()">
	<div class="searchBox">
		<div class="RowTop header">
			<div class="title">
				<bean:message key="demographic.search.msgSearchPatient"/>
			</div>
			<div class="createNew">
		<span class="HelpAboutLogout" style="font-size:12px; font-style:normal;">
			<oscar:help keywords="&Title=Patient+Search&portal_type%3Alist=Document" key="app.top1" style="color:black; font-size:10px;font-style:normal;"/> |
        		<a style="color:black; font-size:10px;font-style:normal;" href="<%=request.getContextPath()%>/oscarEncounter/About.jsp" target="_new"><bean:message
				        key="global.about"/></a>
		</span>
			</div>
		</div>
		<ul>
			<li>
				<div class="label">
				</div>
				<% String searchMode = request.getParameter("search_mode");
					String keyWord = request.getParameter("keyword");
					if (searchMode == null || searchMode.equals(""))
					{
						searchMode = OscarProperties.getInstance().getProperty("default_search_mode", "search_name");
					}
					if (keyWord == null)
					{
						keyWord = "";
					}
				%>
				<select class="wideInput" name="search_mode">
					<option value="search_name" <%=searchMode.equals("search_name") ? "selected" : ""%>>
						<bean:message key="demographic.zdemographicfulltitlesearch.formName"/>
					</option>
					<option value="search_phone" <%=searchMode.equals("search_phone") ? "selected" : ""%>>
						<bean:message key="demographic.zdemographicfulltitlesearch.formPhone"/>
					</option>
					<option value="search_dob" <%=searchMode.equals("search_dob") ? "selected" : ""%>>
						<bean:message key="demographic.zdemographicfulltitlesearch.formDOB"/>
					</option>
					<option value="search_address" <%=searchMode.equals("search_address") ? "selected" : ""%>>
						<bean:message key="demographic.zdemographicfulltitlesearch.formAddr"/>
					</option>
					<option value="search_hin" <%=searchMode.equals("search_hin") ? "selected" : ""%>>
						<bean:message key="demographic.zdemographicfulltitlesearch.formHIN"/>
					</option>
					<option value="search_email" <%=searchMode.equals("search_email") ? "selected" : ""%>>
						<bean:message key="demographic.zdemographicfulltitlesearch.formEmail"/>
					</option>
					<option value="search_chart_no" <%=searchMode.equals("search_chart_no") ? "selected" : ""%>>
						<bean:message key="demographic.zdemographicfulltitlesearch.formChart"/>
					</option>
					<option value="search_demographic_no" <%=searchMode.equals("search_demographic_no") ? "selected" : ""%>>
						<bean:message key="demographic.zdemographicfulltitlesearch.formDemographicNo"/>
					</option>
				</select>
			</li>
			<li>
				<div class="label">
				</div>
				<input class="wideInput" type="text" NAME="keyword" VALUE="<%=StringEscapeUtils.escapeHtml(keyWord)%>" SIZE="17" MAXLENGTH="100">
			</li>
			<li>
				<INPUT TYPE="hidden" NAME="orderby" VALUE="last_name, first_name">
				<INPUT TYPE="hidden" NAME="dboperation" VALUE="search_titlename">
				<INPUT TYPE="hidden" NAME="limit1" id="limit1" VALUE="0">
				<INPUT TYPE="hidden" NAME="limit2" id="limit2" VALUE="<%=perPage%>">
				<INPUT TYPE="hidden" NAME="displaymode" VALUE="Search">
				<INPUT TYPE="hidden" NAME="ptstatus" VALUE="active">
				<INPUT TYPE="hidden" NAME="fromMessenger" VALUE="<%=fromMessenger%>">
				<INPUT TYPE="hidden" NAME="outofdomain" VALUE="">
				<INPUT TYPE="SUBMIT" class="rightButton blueButton top" VALUE="<bean:message key="demographic.zdemographicfulltitlesearch.msgSearch" />" SIZE="17"
				       TITLE="<bean:message key="demographic.zdemographicfulltitlesearch.tooltips.searchActive"/>">
				&nbsp;&nbsp;&nbsp; <INPUT TYPE="button" onclick="searchInactive();"
				                          TITLE="<bean:message key="demographic.zdemographicfulltitlesearch.tooltips.searchInactive"/>"
				                          VALUE="<bean:message key="demographic.search.Inactive"/>">
				<INPUT TYPE="button" onclick="searchAll();"
				       TITLE="<bean:message key="demographic.zdemographicfulltitlesearch.tooltips.searchAll"/>"
				       VALUE="<bean:message key="demographic.search.All"/>">

				<%
					LoggedInInfo loggedInInfo2 = LoggedInInfo.getLoggedInInfoFromSession(request);
					if (loggedInInfo2.getCurrentFacility().isIntegratorEnabled())
					{
				%>
				<input type="checkbox" name="includeIntegratedResults" value="true"/>Include Integrator
				<%}%>

				<security:oscarSec roleName="<%=roleName%>" objectName="_search.outofdomain" rights="r">
					<INPUT TYPE="button" onclick="searchOutOfDomain();"
					       TITLE="<bean:message key="demographic.zdemographicfulltitlesearch.tooltips.searchOutOfDomain"/>"
					       VALUE="<bean:message key="demographic.search.OutOfDomain"/>">
				</security:oscarSec>

				<caisi:isModuleLoad moduleName="caisi">
					<input type="button" value="cancel" onclick="location.href='<html:rewrite page="/PMmodule/ProviderInfo.do"/>'">
				</caisi:isModuleLoad>

			</li>
			<li>
				<div class="pageLimiter">
					<label>Result Limit:</label>
					<select name="limit" onchange="document.getElementById('limit2').value = this.options[this.selectedIndex].value;">
						<option value="10" <%= perPage == 10 ? "selected='selected'" : "" %>>10</option>
						<option value="25" <%= perPage == 25 ? "selected='selected'" : "" %>>25</option>
						<option value="50" <%= perPage == 50 ? "selected='selected'" : "" %>>50</option>
						<option value="100"<%= perPage == 100 ? "selected='selected'" : "" %>>100</option>
					</select>
				</div>
			</li>
		</ul>
	</div>
</form>
