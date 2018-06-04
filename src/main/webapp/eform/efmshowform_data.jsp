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
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.oscarehr.eform.service.EFormDataService" %>
<%@ page import="org.oscarehr.util.SpringUtils" %>
<%@ page import="oscar.eform.data.EForm" %>
<%
	EFormDataService eFormDataService = SpringUtils.getBean(EFormDataService.class);

	String id = request.getParameter("fid");
	String messageOnFailure = "No eform or appointment is available";
	String providerNo = (String) session.getValue("user");
	boolean showInstancedWarning = false;

	if(id == null)
	{  // form exists in patient
		id = request.getParameter("fdid");
		String appointmentNo = request.getParameter("appointment");
		String eformLink = request.getParameter("eform_link");

		EForm eForm = new EForm(id);
		showInstancedWarning = !eFormDataService.isLatestInstancedVersion(Integer.parseInt(id));

		eForm.setLoggedInProvider(providerNo);
		eForm.setContextPath(request.getContextPath());
		eForm.setOscarOPEN(request.getRequestURI());
		if(appointmentNo != null) eForm.setAppointmentNo(appointmentNo);
		if(eformLink != null) eForm.setEformLink(eformLink);

		String parentAjaxId = StringUtils.trimToNull(request.getParameter("parentAjaxId"));
		eForm.setParentAjaxId(parentAjaxId);
		eForm.setAction();
		eForm.setDatabaseUpdateAPs();
		out.print(eForm.getFormHtml());
	}
	else
	{  //if form is viewed from admin screen
		EForm eForm = new EForm(id, "-1"); //form cannot be submitted, demographic_no "-1" indicate this specialty
		eForm.setLoggedInProvider(providerNo);
		eForm.setContextPath(request.getContextPath());
		eForm.setupInputFields();
		eForm.setOscarOPEN(request.getRequestURI());
		eForm.setImagePath();
		eForm.setDatabaseUpdateAPs();
		out.print(eForm.getFormHtml());
	}

	String iframeResize = (String) session.getAttribute("useIframeResizing");
	if("true".equalsIgnoreCase(iframeResize))
	{ %>
<script src="<%=request.getContextPath() %>/library/pym.js"></script>
<script>
	var pymChild = new pym.Child({polling: 500});

</script>
<%
	}
	if(showInstancedWarning)
	{
%>
	<script>
		alert("You are editing an outdated version of an instanced eForm. Saving this version will overwrite the existing version and could result in a data loss.");
	</script>
<%
	}
%>