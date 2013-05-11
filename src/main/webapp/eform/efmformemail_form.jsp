<%--

    Copyright (c) 2008-2012 Indivica Inc.

    This software is made available under the terms of the
    GNU General Public License, Version 2, 1991 (GPLv2).
    License details are available via "indivica.ca/gplv2"
    and "gnu.org/licenses/gpl-2.0.html".

--%>
<%--  

This Page creates the fax form for eforms.
 
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
	if (props.isEFormFaxEnabled()) {
		
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
	  
%> 
<div style="width: 100px; float: left;">
	Providers:
</div>
<div style="width: 500px; float: left;">
		<select id="emailSelect" onchange="chooseEmail()">
			<option value=""></option>
		<%
		String rdName = "";
		String rdFaxNo = "";
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
                            	 rdFaxNo = fax;
                             }
			if (!"".equals(email)) {
			%>
			<option value="<%= email%>"><%= String.format("%s, %s &lt;%s&gt;", lName, fName, email) %> </option>
			<%						
			}
		} %>		                        
		</select>
</div>
<% } // end if (props.isRichEFormFaxEnabled()) { %>
