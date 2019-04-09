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
<%@ page import="org.oscarehr.util.MiscUtils" %>
<%
	EFormDataService eFormDataService = SpringUtils.getBean(EFormDataService.class);

	String providerNo = (String) session.getValue("user");
	boolean showInstancedWarning = false;

	if(request.getParameter("fdid") != null)
	{  // form exists in patient
		String id = request.getParameter("fdid");
		String appointmentNo = request.getParameter("appointment");
		String eformLink = request.getParameter("eform_link");
		String parentAjaxId = StringUtils.trimToNull(request.getParameter("parentAjaxId"));

		EForm eForm = new EForm(id);
		if(!"consult".equalsIgnoreCase(parentAjaxId))
		{
			showInstancedWarning = !eFormDataService.isLatestInstancedVersion(Integer.parseInt(id));
		}

		eForm.setLoggedInProvider(providerNo);
		eForm.setContextPath(request.getContextPath());
		eForm.setOscarOPEN(request.getRequestURI());
		if(appointmentNo != null) eForm.setAppointmentNo(appointmentNo);
		if(eformLink != null) eForm.setEformLink(eformLink);

		eForm.setParentAjaxId(parentAjaxId);
		eForm.setAction();
		eForm.setDatabaseUpdateAPs();
		out.print(eForm.getFormHtml());
	}
	else
	{   // the form is viewed from admin screen
		String id = request.getParameter("fid");

		if (id != null)
		{
			EForm eForm = new EForm(id, "-1");        // demographicID -1 is a placeholder.  We will not be submitting this form.
			eForm.setLoggedInProvider(providerNo);
			eForm.setContextPath(request.getContextPath());
			eForm.setupInputFields();
			eForm.setOscarOPEN(request.getRequestURI());
			eForm.setImagePath();
			eForm.setDatabaseUpdateAPs();
			eForm.disableSubmitControls();
			out.print(eForm.getFormHtml());
		}
		else
		{
		    // If despite our best efforts, a user still manages to submit an invalid e-form from the admin screen or otherwise.
			MiscUtils.getLogger().warn("An e-form was submitted without a fdid or fid, ignoring submission.\n Params: " + request.getParameterMap().toString());
		}
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
	if(showInstancedWarning) {
%>
	<script>
		alert("You are editing an outdated version of an instanced eForm. Saving this version will overwrite the existing version and could result in a data loss.");
	</script>
<%
	}
%>
