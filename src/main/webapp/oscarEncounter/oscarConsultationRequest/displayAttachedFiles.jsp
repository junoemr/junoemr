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

<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%
      String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
      boolean authed=true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_con" rights="r" reverse="<%=true%>">
    <%authed=false; %>
    <%response.sendRedirect("../../securityError.jsp?type=_con");%>
</security:oscarSec>
<%
if(!authed) {
    return;
}
%>
<%@ page import="java.util.List"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%
    List<String> labLabels = (List<String>) request.getAttribute("labArray");
    List<String> docLabels = (List<String>) request.getAttribute("docArray");
    List<String> eFormLabels = (List<String>) request.getAttribute("eFormArray");
	List<String> hrmLabels = (List<String>) request.getAttribute("hrmLabels");

	String displayValue = "display: none;";
	if(docLabels.isEmpty() && labLabels.isEmpty() && eFormLabels.isEmpty() && hrmLabels.isEmpty())
	{
		displayValue = "";
	}

%>
<ul id="attachedList"
    style="background-color: white; padding-left: 20px; list-style-position: outside; list-style-type: lower-roman;">
    <%
        for(String docLabel : docLabels)
        {
    %>
    <li class="doc"><%=docLabel%></li>
    <%
        }
        for(String labLabel : labLabels)
        {
    %>
    <li class="lab"><%=labLabel%></li>
    <%
        }
        for(String eFormLabel : eFormLabels)
        {
    %>
	<li class="eform"><%=eFormLabel%></li>
	<%
        }
		for(String hrmLabel : hrmLabels)
		{
	%>
	<li class="hrm"><%=hrmLabel%></li>
	<%
		}
    %>
</ul>
<p id="attachDefault"
    style="background-color: white; text-align: center; <%= displayValue %>"><bean:message
    key="oscarEncounter.oscarConsultationRequest.AttachDoc.Empty" /></p>
