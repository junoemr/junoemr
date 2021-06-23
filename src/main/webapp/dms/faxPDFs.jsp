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

<%@page import="org.oscarehr.util.LocaleUtils"%>
<%@page import="org.oscarehr.phr.util.MyOscarUtils"%>
<%@page import="org.oscarehr.util.MiscUtils"%>
<%@page import="org.oscarehr.util.LoggedInInfo"%>
<%@page import="org.oscarehr.common.dao.UserPropertyDAO, org.oscarehr.common.model.UserProperty, org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@page import="org.oscarehr.common.model.Demographic"%>
<%@page import="oscar.oscarDemographic.data.DemographicData"%>
<%@page import="oscar.SxmlMisc"%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/rewrite-tag.tld" prefix="rewrite"%>
<%@ taglib uri="/WEB-INF/oscarProperties-tag.tld" prefix="oscarProp"%>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%@ taglib uri="/WEB-INF/oscar-tag.tld" prefix="oscar" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="displayServiceUtil" scope="request" class="oscar.oscarEncounter.oscarConsultationRequest.config.pageUtil.EctConDisplayServiceUtil" />
<%
    LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);

	// Load the specialists
	displayServiceUtil.estSpecialist();

	String providerId = (String) session.getAttribute("user");
	String demoId = request.getParameter("demoId");

	DemographicData demoData = null;
	Demographic demographic = null;
	if (demoId != null)
	{
		demoData = new oscar.oscarDemographic.data.DemographicData();
		demographic = demoData.getDemographic(loggedInInfo, demoId);
	}

%>
<html:html locale="true">
<head>

<title>Fax Documents</title>

<link rel="stylesheet" type="text/css" href="../share/css/OscarStandardLayout.css" />

<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/util/fax.js"></script>
<script type="text/javascript">
	Oscar.Util.Fax.updateFaxButton();
</script>
<script src="//code.jquery.com/jquery-latest.js"></script>
<script type="text/javascript">

function submitForm(actionPath)
{
	var form = document.forms[0];
	form.action = actionPath;
	form.submit();
	return true;
}
</script>



</head>
<body class="bodystyle">

<table class="MainTable" id="scrollNumber1" name="encounterTable"               
	style="margin: 0px;">                                                       
	<tr class="MainTableRowTop">                                                
		<td class="MainTableTopRowLeftColumn" width="60px">eDocs</td>           
		<td class="MainTableTopRowRightColumn">                                 
			<table class="TopStatusBar">                                            
				<tr>                                                                
					<td>Send Documents By Fax</td>                                  
				</tr>                                                               
			</table>                                                                
		</td>                                                                   
	</tr>                                                                       
</table>    


	<%
	//if (props.isConsultationFaxEnabled()) {
	%>
	<table>
		<tr><td class="tite4">Additional Fax Recipients:</td></tr>
		<tr>
			<td>
				<%
				String rdohip = "";
				if (demographic!=null) 
				{
					String famDoc = demographic.getFamilyDoctor();
					if (famDoc != null && famDoc.trim().length() > 0) 
					{ 
						rdohip = SxmlMisc.getXmlContent(famDoc,"rdohip"); 
						rdohip = rdohip == null ? "" : rdohip.trim(); 
					}
				}
				%>
				<table width="100%">
				<tr>

					<td class="tite4" width="10%">  Providers: </td>
					<td class="tite3" width="20%">
						<select id="otherFaxSelect">
							<option value="">--Select Provider--</option>
						<%
						String rdName = "";
						String rdFaxNo = "";
						for (int i=0;i < displayServiceUtil.specIdVec.size(); i++) 
						{
							 String  specId     = (String) displayServiceUtil.specIdVec.elementAt(i);
							 String  fName      = (String) displayServiceUtil.fNameVec.elementAt(i);
							 String  lName      = (String) displayServiceUtil.lNameVec.elementAt(i);
							 String  proLetters = (String) displayServiceUtil.proLettersVec.elementAt(i);
							 String  address    = (String) displayServiceUtil.addressVec.elementAt(i);
							 String  phone      = (String) displayServiceUtil.phoneVec.elementAt(i);
							 String  fax        = (String) displayServiceUtil.faxVec.elementAt(i);
							 String  referralNo = ""; // TODO-legacy: add referal number to specialists ((String) displayServiceUtil.referralNoList.get(i)).trim();
							 if (rdohip != null && !"".equals(rdohip) && rdohip.equals(referralNo)) 
							 {
								 rdName = String.format("%s, %s", lName, fName);
								 rdFaxNo = fax;
							 }
							 if (!"".equals(fax)) 
							 {
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
				<tr>
					<td class="tite4" width="20%"> Other Fax Number: </td>
					<td class="tite3" width="32%">
						<input type="text" id="otherFaxInput"></input>

					<font size="1">(xxx-xxx-xxxx)  </font></td>
					<td class="tite3">
						<button onclick="Oscar.Util.Fax.AddOtherFax(); return false;">Add Other Fax Recipient</button>
					</td>
				</tr>
				</table>
			</td>
		</tr>
	</table>
	<% 
//	} 
%>

<html:form action="/dms/sendFaxPDFs.do?method=faxDocument">

	<ul id="faxRecipients">
	</ul>

	<input type="hidden" name="demoId" value="<%=demoId%>">
	<input type="hidden" name="providerId" value="<%=providerId%>">
    <logic:iterate name="docNo" id="doc">
        <input type="hidden" name="docNo" value='<bean:write name="doc" />' \>
    </logic:iterate>

	<!-- Show options for fax recipients -->

	<!-- Show free-form phone number box -->


    <input type="button" class="faxButton" disabled="disabled"
        value="<bean:message key="dms.documentReport.btnFaxPDF"/>"
        onclick="return submitForm('<rewrite:reWrite jspPage="sendFaxPDFs.do?method=faxDocument"/>');" />

</html:form>
</body>
</html:html>
