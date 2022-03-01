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
<%@ page import="org.oscarehr.common.dao.UserPropertyDAO"%>
<%@ page import="org.oscarehr.common.model.Demographic"%>
<%@ page import="org.oscarehr.common.model.UserProperty"%>
<%@ page import="org.oscarehr.fax.service.FaxUploadService"%>
<%@ page import="org.oscarehr.util.LoggedInInfo"%>
<%@ page import="org.oscarehr.util.SpringUtils"%>
<%@ page import="org.springframework.web.context.WebApplicationContext"%>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils,oscar.SxmlMisc"%>
<%@ page import="oscar.oscarDemographic.data.DemographicData" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<jsp:useBean id="displayServiceUtil" scope="request" class="oscar.oscarEncounter.oscarConsultationRequest.config.pageUtil.EctConDisplayServiceUtil" />
<%
	FaxUploadService faxUploadService = SpringUtils.getBean(FaxUploadService.class);

	displayServiceUtil.estSpecialist();
	String demo = request.getParameter("demographicNo");
	DemographicData demoData = null;
	Demographic demographic = null;
	String rdohip = "";
	if (!"".equals(demo)) {
		demoData = new oscar.oscarDemographic.data.DemographicData();
		demographic = demoData.getDemographic(LoggedInInfo.getLoggedInInfoFromSession(request), demo);
		rdohip = SxmlMisc.getXmlContent(demographic.getFamilyDoctor(), "rdohip").trim();
	}
	boolean faxEnabled = faxUploadService.isOutboundFaxEnabled();
%>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/util/fax.js"></script>
<script type="text/javascript">
	Oscar.Util.Fax.updateFaxButton();
</script>

<table width="100%">
	<input type="hidden" value=<%=faxEnabled%> id="faxControl_faxEnabled">
	<%
		String rdName = "";
		String rdFaxNo = "";
		if (displayServiceUtil.specIdVec.size() != 0)
		{
	%>
	<tr>
		<td class="tite4" width="10%">  Providers: </td>
		<td class="tite3" width="20%">

		<select id="otherFaxSelect">
			<option value="">--Select Provider--</option>
		<%
			for (int i=0;i < displayServiceUtil.specIdVec.size(); i++) {
				String  specId     = (String) displayServiceUtil.specIdVec.elementAt(i);
				String  fName      = (String) displayServiceUtil.fNameVec.elementAt(i);
				String  lName      = (String) displayServiceUtil.lNameVec.elementAt(i);
				String  proLetters = (String) displayServiceUtil.proLettersVec.elementAt(i);
				String  address    = (String) displayServiceUtil.addressVec.elementAt(i);
				String  phone      = (String) displayServiceUtil.phoneVec.elementAt(i);
				String  fax        = (String) displayServiceUtil.faxVec.elementAt(i);
				String  referralNo = (displayServiceUtil.referralNoVec.size() > 0 ? displayServiceUtil.referralNoVec.get(i).trim() : "");
				if (rdohip != null && !"".equals(rdohip) && rdohip.equals(referralNo)) {
					rdName = String.format("%s, %s", lName, fName);
					rdFaxNo = fax;
				}
				if (!"".equals(fax)) {
				%>
				<option value="<%= fax %>"> <%= String.format("%s, %s", lName, fName) %> </option>
				<%
				}
			}
		%>
		</select>
	</td>
	<td class="tite3">				
		<button onclick="Oscar.Util.Fax.AddOtherFaxProvider(); return false;">Add Provider</button>
	</td>
</tr>
	<%
		}
	%>
<tr>
	<td class="tite4" width="10%"> Other Fax Number: </td>											
	<td class="tite3" width="20%">
		<input type="text" id="otherFaxInput"></input>	
		<font size="1">(xxx-xxx-xxxx)  </font>					
	</td>
	<td class="tite3">
		<button onclick="Oscar.Util.Fax.AddOtherFax(); return false;">Add Other Fax Recipient</button>
	</td>		
</tr>
<tr>
	<td colspan=3>
		<ul id="faxRecipients">
		<%
		WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        UserPropertyDAO userPropertyDAO = (UserPropertyDAO) ctx.getBean("UserPropertyDAO");
        String provider = (String) request.getSession().getAttribute("user");
        UserProperty prop = userPropertyDAO.getProp(provider, UserProperty.EFORM_REFER_FAX);
        boolean eformFaxRefer = prop != null && !"no".equals(prop.getValue());
		
		if (eformFaxRefer && !"".equals(rdName) && !"".equals(rdFaxNo)) {
			%>
			<li>
			<%=rdName %> <b>Fax No: </b><%= rdFaxNo %> <a href="javascript:void(0);" onclick="Oscar.Util.Fax.removeRecipient(this)">remove</a>
				<input type="hidden" name="faxRecipients" value="<%= rdFaxNo %>" />
			</li>
			<%
		}
		%>
		</ul>
	</td>	
</tr>
</table>