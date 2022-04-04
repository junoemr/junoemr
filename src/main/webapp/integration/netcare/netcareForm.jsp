<%--

    Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
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
    CloudPractice Inc.
    Victoria, British Columbia
    Canada

--%>
<%@ page import="org.oscarehr.util.SpringUtils" %>
<%@ page import="org.oscarehr.demographic.service.DemographicService" %>
<%@ page import="org.oscarehr.demographic.model.DemographicModel" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<%
	DemographicService demographicService = (DemographicService) SpringUtils.getBean("demographic.service.DemographicService");
	DemographicModel demographicModel = demographicService.getDemographicModel(Integer.parseInt(request.getParameter("demoNo")));

	String appCommandLineValue = "plb.albertanetcare.ca/cha/PLBLogin.htm" +
			"?contextView=EMRPatient" +
			"&userID=PLBTEST1" + //todo
			"&applicationName=Aligndex+EMPI" +
			"&entryPointName=Search+for+a+Patient-EMR" +
			"&EMRPatient.Id.idType=AB_ULI" +
			"&EMRPatient.Id.id=" + demographicModel.getHealthNumber() +
			"&confCode=ABCDEF"; //todo
%>
<html>
<head>
	<title>CareConnect</title>
	<script src="../../js/jquery-1.7.1.min.js"></script>
	<script type="text/javascript">
		jQuery(document).ready(function ()
		{
			jQuery("#CareConnectForm").submit();
		});
	</script>
</head>
<body>
	<div>
		<p>
			Connecting to Netcare site, please hold...
		</p>
	</div>

	<form name="a1" method="GET"
	      action="https://plb.albertanetcare.ca/Citrix/AccessPlatform16/site/launcher.aspx"
	      target="NetcareLoginWindow">
		<input type="hidden" name="CTX_Application" value="Citrix.MPS.App.Portal.PLB">
		<input type="hidden" name="NFuse_AppCommandLine" value="<%=appCommandLineValue%>">
	</form>
</body>
</html>
