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
<%@ page import="org.oscarehr.demographic.dao.DemographicDao" %>
<%@ page import="org.oscarehr.demographic.model.Demographic" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
	DemographicDao demographicDao = (DemographicDao)SpringUtils.getBean("demographic.dao.DemographicDao");
	Demographic demographic = demographicDao.find(Integer.parseInt(request.getParameter("demoNo")));
%>
<html>
<head>
	<title>CareConnect</title>
	<script src="../../js/jquery-1.7.1.min.js"></script>
	<script type="module" src="careConnect.js"></script>
	<script type="text/javascript">
		jQuery(document).ready(function () {
			Juno.CareConnect.Util.determineCareConnectURL(function response(url) {
				var selector = jQuery("#CareConnectForm");
				selector.get(0).setAttribute('action', url);
				selector.submit();
			});
		});
	</script>
</head>
<body>
	<div>
		<p>
			Connecting to CareConnect site, please hold...
		</p>
		<p>
			If you are connecting outside of the PPN, you will need the following application for two-factor authentication:
			<a href="https://www2.gov.bc.ca/gov/content/governments/government-id/bc-services-card/log-in-with-card/set-up-mobile-card/download-bcsc-app">
				https://www2.gov.bc.ca/gov/content/governments/government-id/bc-services-card/log-in-with-card/set-up-mobile-card/download-bcsc-app
			</a>
		</p>
		<p>
			Download link for iOS:
			<a href="https://apps.apple.com/us/app/id1234298467">
				https://apps.apple.com/us/app/id1234298467
			</a>
		</p>
		<p>
			Download link for Android:
			<a href="https://play.google.com/store/apps/details?id=ca.bc.gov.id.servicescard">
				https://play.google.com/store/apps/details?id=ca.bc.gov.id.servicescard
			</a>
		</p>

	</div>
	<form action="https://health.careconnect.ca" method="POST" id="CareConnectForm" style="display:none">
		<input type="hidden" name="phn" value="<%=demographic.getHin()%>">
		<input type="hidden" name="fn" value="<%=demographic.getFirstName()%>">
		<input type="hidden" name="ln" value="<%=demographic.getLastName()%>">
		<input type="hidden" name="dob" value="<%=demographic.getYearOfBirth()+demographic.getMonthOfBirth()+demographic.getDayOfBirth()%>">
		<input type="hidden" name="g" value="<%=demographic.getSex()%>">
		<input type="hidden" name="O" value="Nan">
	</form>
</body>
</html>
