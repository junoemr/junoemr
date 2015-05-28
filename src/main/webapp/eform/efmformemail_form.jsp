<%--

    Copyright (c) 2005-2012. OscarHost Inc. All Rights Reserved.
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
    OscarHost, a Division of Cloud Practice Inc.

--%>

<%--  

This Page creates the email form for eforms.
 
--%>
<%@ page import="java.sql.*, java.util.ArrayList, oscar.eform.data.*, oscar.SxmlMisc, org.oscarehr.common.model.Demographic, oscar.oscarDemographic.data.DemographicData,oscar.OscarProperties,org.springframework.web.context.support.WebApplicationContextUtils, org.springframework.web.context.WebApplicationContext"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ page import="org.oscarehr.common.model.*,org.oscarehr.common.dao.*"%>
<jsp:useBean id="displayServiceUtil" scope="request" class="oscar.oscarEncounter.oscarConsultationRequest.config.pageUtil.EctConDisplayServiceUtil" />
<%

	OscarProperties props = OscarProperties.getInstance();
	if (props.isEFormEmailEnabled()) {
		
		displayServiceUtil.estSpecialist();		
		String demo = request.getParameter("demographicNo");
		DemographicData demoData = null;
		Demographic demographic = null;
		String rdohip = "";
		if (!"".equals(demo))
		{
			demoData = new oscar.oscarDemographic.data.DemographicData();
			demographic = demoData.getDemographic(demo);
			rdohip = SxmlMisc.getXmlContent(StringUtils.trimToEmpty(demographic.getFamilyDoctor()),"rdohip");
			rdohip = SxmlMisc.getXmlContent(demographic.getFamilyDoctor(),"rdohip").trim();
		}
		
	String default_subject = props.getProperty("eform_email_subject");
	String default_text_patients = (props.getProperty("eform_email_text_patients")==null)?"":props.getProperty("eform_email_text_patients");
	String default_text_providers = (props.getProperty("eform_email_text_providers")==null)?"":props.getProperty("eform_email_text_providers");
	
  
%> 
<div style="width: 100px; float: left;">
	Providers:
</div>
<div style="width: 500px; float: left;">
		<select id="emailSelect" onchange="chooseEmail()">
			<option value=""></option>
		<%
		String rdName = "";
		for (int i=0;i < displayServiceUtil.specIdVec.size(); i++) {
                             String  specId     = (String) displayServiceUtil.specIdVec.elementAt(i);
                             String  fName      = (String) displayServiceUtil.fNameVec.elementAt(i);
                             String  lName      = (String) displayServiceUtil.lNameVec.elementAt(i);
                             String  proLetters = (String) displayServiceUtil.proLettersVec.elementAt(i);
                             String  address    = (String) displayServiceUtil.addressVec.elementAt(i);
                             String  phone      = (String) displayServiceUtil.phoneVec.elementAt(i);
                             String  fax        = (String) displayServiceUtil.faxVec.elementAt(i);
                             String  email      = (String) displayServiceUtil.emailVec.elementAt(i);
                             String  referralNo = (displayServiceUtil.referralNoVec.size() > 0 ? displayServiceUtil.referralNoVec.get(i).trim() : "");
                             if (rdohip != null && !"".equals(rdohip) && rdohip.equals(referralNo)) {
                            	 rdName = String.format("%s, %s", lName, fName);
                             }
			if (!"".equals(email)) {
			%>
			<option value="<%= email%>"><%= String.format("%s, %s &lt;%s&gt;", lName, fName, email) %> </option>
			<%						
			}
		} %>		                        
		</select>
		
</div>
<div id="emailFormBox" style="display: none">
	<div id="additionalInfoForm">
		<div>
			<label>To:</label>
			<span id="emailTo">
		</div>
		<div>
			<label>Subject:</label>
			<input type="text" name="subject" id="subject" value="<%=default_subject%>">
		</div>
		<div>
			<label>Body text:</label>
			<textarea id="bodytext"></textarea>
		</div>
		<div>
			<input type="button" onClick="emailEForm()" value="Email eForm">
		</div>
	</div>
	
	<span class="progress"></span>
	
</div>
<input type="hidden" id="default_text_patients" value="<%=default_text_patients%>"/>
<input type="hidden" id="default_text_providers" value="<%=default_text_providers%>"/>
<% } // end if (props.isEFormEmailEnabled()) { %>

