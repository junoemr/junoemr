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

<%@ page import="org.oscarehr.report.reportByTemplate.service.ReportByTemplateService"%>
<%@ page import="org.oscarehr.util.SpringUtils"%>
<%@ page import="oscar.oscarReport.reportByTemplate.ReportObject" %>
<%@ page import="java.util.List" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%
	ReportByTemplateService reportByTemplateService = SpringUtils.getBean(ReportByTemplateService.class);
	List<ReportObject> templates = reportByTemplateService.getLegacyReportObjectList(false);

	String templateViewId = StringUtils.trimToEmpty(request.getParameter("templateviewid"));

	String roleName$ = (String) session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
	boolean authed = true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_report,_admin.reporting,_admin" rights="r" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect("../../securityError.jsp?type=_report&type=_admin.reporting&type=_admin");%>
</security:oscarSec>
<%
if(!authed) {
	return;
}
%>
<div class="templatelist">
	<a href="addEditTemplate.jsp" style="color: #226d55; font-size: 10px;">Add Template</a>
	<div class="templatelistHeader">Select a template:</div>
	<ul class="templatelist">
		<li><a href="homePage.jsp"><b>Main Page</b></a>
				<%
		int itemDisplayNumber=1;
		for (ReportObject report : templates)
		{
            String templateId = report.getTemplateId();
            String templateTitle = report.getTitle();
            String selectedTemplate = "";
            if (templateId.equals(templateViewId))
            {
                selectedTemplate = "selectedTemplate";
            }%>

		<li class="<%=selectedTemplate%>"><%=String.valueOf(itemDisplayNumber)%>. <a
				href="reportConfiguration.jsp?templateid=<%=templateId%>"><%=templateTitle%>
		</a></li>
		<%
			itemDisplayNumber++;
		}
		%>
	</ul>
</div>
</form>
