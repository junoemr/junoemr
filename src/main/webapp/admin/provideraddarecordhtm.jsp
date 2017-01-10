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
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/caisi-tag.tld" prefix="caisi"%>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%@ page import="org.oscarehr.common.model.ClinicNbr"%>
<%@ page import="org.oscarehr.common.dao.ClinicNbrDao"%>
<%@ page import="org.oscarehr.util.SpringUtils"%>
<%@ page import="java.util.*,oscar.oscarProvider.data.*"%>
<%@ page import="oscar.OscarProperties"%>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@ page import="org.oscarehr.common.dao.SiteDao"%>
<%@ page import="org.oscarehr.common.model.Site"%>
<%
	if(session.getAttribute("user") == null)
    response.sendRedirect("../logout.jsp");
  String curProvider_no,userfirstname,userlastname;
  curProvider_no = (String) session.getAttribute("user");
  userfirstname = (String) session.getAttribute("userfirstname");
  userlastname = (String) session.getAttribute("userlastname");
  //display the main provider page
  //includeing the provider name and a month calendar

  java.util.Locale vLocale =(java.util.Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY);

  ArrayList<Hashtable<String,String>> list = ProviderData.getProviderListOfAllTypes(true);
  ArrayList<Integer> providerList = new ArrayList<Integer>();
  for (Hashtable<String,String> h : list) {
	  try{
      String pn = h.get("providerNo");
      providerList.add(Integer.valueOf(pn));
	  }catch(Exception alphaProviderNumber){} /*No need to do anything. Just want to avoid a NumberFormatException from provider numbers with alphanumeric Characters*/
  }

  String suggestProviderNo = "";
  for (Integer i=1; i<1000000; i++) {
      if (!providerList.contains(i)) {
          suggestProviderNo = i.toString();
          break;
      }
  }
%>

<%
	if(session.getAttribute("userrole") == null )  response.sendRedirect("../logout.jsp");
    String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");

    boolean isSiteAccessPrivacy=false;
%>

<security:oscarSec objectName="_site_access_privacy" roleName="<%=roleName$%>" rights="r" reverse="false">
	<%
		isSiteAccessPrivacy=true;
	%>
</security:oscarSec>
<html:html locale="true">
<head>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/global.js"></script>
<title><bean:message key="admin.provideraddrecordhtm.title" /></title>
<link rel="stylesheet" href="../web.css">
<script LANGUAGE="JavaScript">
<!--
function setfocus() {
  document.searchprovider.provider_no.focus();
  document.searchprovider.provider_no.select();
}

function onsub() {
  if(document.searchprovider.provider_no.value=="" ||
     document.searchprovider.last_name.value=="" ||
	 document.searchprovider.first_name.value=="" ||
     document.searchprovider.provider_type.value==""  ) {
     alert("<bean:message key="global.msgInputKeyword"/>");
     return false;
  }
  if(!(document.searchprovider.provider_no.value=="-new-" || document.searchprovider.provider_no.value.match(/^\d+$/))){
  		alert("Provider No. must be a number.");
  		return false;
  }
  else {
    	return true;
  }

}
function upCaseCtrl(ctrl) {
  ctrl.value = ctrl.value.toUpperCase();
}
//-->
</script>
</head>

<body background="../images/gray_bg.jpg" bgproperties="fixed"
	onLoad="setfocus()" topmargin="0" leftmargin="0" rightmargin="0">
<center>
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr bgcolor="#486ebd">
		<th align="CENTER"><font face="Helvetica" color="#FFFFFF"><bean:message
			key="admin.provideraddrecordhtm.description" /></font></th>
	</tr>
</table>
<form method="post" action="admincontrol.jsp" name="searchprovider"
	onsubmit="return onsub()">
<table cellspacing="0" cellpadding="2" width="90%" border="0">
	<tr>
		<td width="50%" align="right"><bean:message
			key="admin.provider.formProviderNo" /><font color="red">:</font></td>
		<td>
		<%
			if(OscarProperties.getInstance().isProviderNoAuto()){
		%> <input
			type="text" name="provider_no" maxlength="6" readonly="readonly"
			value="-new-"> <%
 	} else {
 %> <input type="text"
			name="provider_no" maxlength="6"> <input type="button" value=<bean:message key="admin.provideraddrecordhtm.suggest"/>
                        onclick="provider_no.value='<%=suggestProviderNo%>'"<%}%>
		</td>
	</tr>
	<tr>
		<td>
		<div align="right"><bean:message
			key="admin.provider.formLastName" /><font color="red">:</font></div>
		</td>
		<td><input type="text" name="last_name" maxlength="30"></td>
	</tr>
	<tr>
		<td>
		<div align="right"><bean:message
			key="admin.provider.formFirstName" /><font color="red">:</font></div>
		</td>
		<td><input type="text" name="first_name" maxlength="30">
		</td>
	</tr>

<%
		if (org.oscarehr.common.IsPropertiesOn.isMultisitesEnable()) {
	%>
	<tr>
		<td>
		<div align="right"><bean:message key="admin.provider.sitesAssigned" /><font color="red">:</font></div>
		</td>
		<td>
<%
	SiteDao siteDao = (SiteDao)WebApplicationContextUtils.getWebApplicationContext(application).getBean("siteDao");
List<Site> sites = ( isSiteAccessPrivacy ? siteDao.getActiveSitesByProviderNo(curProvider_no) : siteDao.getAllActiveSites());
for (int i=0; i<sites.size(); i++) {
%>
	<input type="checkbox" name="sites" value="<%=sites.get(i).getSiteId()%>"><%=sites.get(i).getName()%><br />
<%
	}
%>
		</td>
	</tr>
<%
	}
%>

	<tr>
		<td align="right"><bean:message key="admin.provider.formType" /><font
			color="red">:</font></td>
		<td><!--input type="text" name="provider_type" --> <%
 	if (vLocale.getCountry().equals("BR")) {
 %>
		<select name="provider_type">
			<option value="receptionist"><bean:message
				key="admin.provider.formType.optionReceptionist" /></option>
			<option value="doctor"><bean:message
				key="admin.provider.formType.optionDoctor" /></option>
			<option value="doctor"><bean:message
				key="admin.provider.formType.optionNurse" /></option>
			<option value="doctor"><bean:message
				key="admin.provider.formType.optionResident" /></option>
			<option value="admin"><bean:message
				key="admin.provider.formType.optionAdmin" /></option>
			<option value="admin_billing"><bean:message
				key="admin.provider.formType.optionAdminBilling" /></option>
			<option value="billing"><bean:message
				key="admin.provider.formType.optionBilling" /></option>
		</select> <%
 	} else {
 %> <select name="provider_type">
			<option value="receptionist"><bean:message
				key="admin.provider.formType.optionReceptionist" /></option>
			<option value="doctor"><bean:message
				key="admin.provider.formType.optionDoctor" /></option>
			<option value="nurse"><bean:message
				key="admin.provider.formType.optionNurse" /></option>
			<option value="resident"><bean:message
				key="admin.provider.formType.optionResident" /></option>
			<option value="midwife"><bean:message
				key="admin.provider.formType.optionMidwife" /></option>
			<option value="admin"><bean:message
				key="admin.provider.formType.optionAdmin" /></option>
			<caisi:isModuleLoad moduleName="survey">
				<option value="er_clerk"><bean:message
					key="admin.provider.formType.optionErClerk" /></option>
			</caisi:isModuleLoad>
		</select> <%
 	}
 %>
		</td>
	</tr>
	<caisi:isModuleLoad moduleName="TORONTO_RFQ" reverse="true">
		<tr>
			<td align="right"><bean:message
				key="admin.provider.formSpecialty" />:</td>
			<td><input type="text" name="specialty"
				onBlur="upCaseCtrl(this)" maxlength="40"></td>
		</tr>
		<tr>
			<td align="right"><bean:message key="admin.provider.formTeam" />:
			</td>
			<td><input type="text" name="team" maxlength="20"></td>
		</tr>
		<tr>
			<td align="right"><bean:message key="admin.provider.formSex" />:
			</td>
			<td><input type="text" name="sex" maxlength="1"
				onBlur="upCaseCtrl(this)"></td>
		</tr>
		<tr>
			<td align="right"><bean:message key="admin.provider.formDOB" />(<font
				size="-1"><i><bean:message
				key="admin.provideraddrecordhtm.dateFormat" /></i></font>):</td>
			<td><input type="text" name="dob" maxlength="11"></td>
		</tr>
		<tr>
			<td align="right"><bean:message key="admin.provider.formAddress" />:
			</td>
			<td><input type="text" name="address" size="40" maxlength="40">
			</td>
		</tr>
		<tr>
			<td align="right"><bean:message
				key="admin.provider.formHomePhone" />:</td>
			<td><input type="text" name="phone" maxlength="20"></td>
		</tr>
		<tr>
			<td align="right"><bean:message
				key="admin.provider.formWorkPhone" />:</td>
			<td><input type="text" name="workphone" value="" maxlength="20">
			</td>
		</tr>
		<tr>
			<td align="right"><bean:message key="admin.provider.formEmail" />:
			</td>
			<td><input type="text" name="email" value=""></td>
		</tr>
		<tr>
			<td align="right"><bean:message key="admin.provider.formPager" />:
			</td>
			<td><input type="text" name="xml_p_pager" value=""></td>
		</tr>
		<tr>
			<td align="right"><bean:message key="admin.provider.formCell" />:
			</td>
			<td><input type="text" name="xml_p_cell" value=""></td>
		</tr>
		<tr>
			<td align="right"><bean:message
				key="admin.provider.formOtherPhone" />:</td>
			<td><input type="text" name="xml_p_phone2" value=""></td>
		</tr>
		<tr>
			<td align="right"><bean:message key="admin.provider.formFax" />:
			</td>
			<td><input type="text" name="xml_p_fax" value=""></td>
		</tr>
		<tr>
			<td align="right"><bean:message key="admin.provider.formOhipNo" />:
			</td>
			<td><input type="text" name="ohip_no" maxlength="20"></td>
		</tr>
		<tr>
			<td align="right"><bean:message key="admin.provider.formRmaNo" />:
			</td>
			<td><input type="text" name="rma_no" maxlength="20"></td>
		</tr>
		<tr>
			<td align="right"><bean:message
				key="admin.provider.formBillingNo" />:</td>
			<td><input type="text" name="billing_no" maxlength="20">
			</td>
		</tr>
		<tr>
			<td align="right"><bean:message key="admin.provider.formHsoNo" />:
			</td>
			<td><input type="text" name="hso_no" maxlength="10"></td>
		</tr>
		<%
			if (OscarProperties.getInstance().getProperty("billregion").equals("AB")) {
		%>
		<tr>
			<td align="right"><bean:message key="admin.provider.formEDeliveryIds" />
			</td>
			<td><input type="text" name="e_delivery_ids"></td>
		</tr>
		<%} %>
		<tr>
			<td align="right"><bean:message
				key="admin.provider.formSpecialtyCode" />:</td>
			<td><input type="text" name="xml_p_specialty_code"></td>
		</tr>
		<tr>
			<td align="right"><bean:message
				key="admin.provider.formBillingGroupNo" />:</td>
			<td><input type="text" name="xml_p_billinggroup_no"></td>
		</tr>
		<tr>
			<td align="right"><bean:message key="admin.provider.formCPSID" />:
			</td>
			<td><input type="text" name="practitionerNo"></td>
		</tr>

		<%
			if (OscarProperties.getInstance().getBooleanProperty("rma_enabled", "true")) {
		%>
		<tr>
			<td align="right">Default Clinic NBR:</td>
			<td colspan="3">
			<select name="xml_p_nbr">
			<%
				ClinicNbrDao clinicNbrDAO = (ClinicNbrDao)SpringUtils.getBean("clinicNbrDao");
					List<ClinicNbr> nbrList = clinicNbrDAO.findAll();
					Iterator<ClinicNbr> nbrIter = nbrList.iterator();
					while (nbrIter.hasNext()) {
						ClinicNbr tempNbr = nbrIter.next();
						String valueString = tempNbr.getNbrValue() + " | " + tempNbr.getNbrString();
			%>
				<option value="<%=tempNbr.getNbrValue()%>" ><%=valueString%></option>
			<%}%>

			</select>
			</td>
		</tr>
		<%} %>

		<tr>
			<td align="right">Bill Center:</td>
			<td><select name="billcenter">
				<option value=""></option>
				<%
                    ProviderBillCenter billCenter = new ProviderBillCenter();
                    String billCode = "";
                    String codeDesc = "";
                    Enumeration<?> keys = billCenter.getAllBillCenter().propertyNames();
//                    Enumeration keys = billCenter.getAllBillCenter().propertyNames();
                    for(int i=0;i<billCenter.getAllBillCenter().size();i++){
                        billCode=(String)keys.nextElement();
                        codeDesc=billCenter.getAllBillCenter().getProperty(billCode);
                %>
				<option value=<%= billCode %>><%= codeDesc%></option>
				<%
                    }
                %>
			</select></td>
		</tr>
		<% if (vLocale.getCountry().equals("BR")) { %>
		<tr>
			<td align="right"><bean:message
				key="admin.provider.formProviderActivity" />:</td>
			<td><input type="text" name="provider_activity" size="5"
				maxlength="3"></td>
		</tr>
		<% } else { %>
		<input type="hidden" name="provider_activity" value="">
		<% }  %>
		<tr>
			<td align="right"><bean:message
				key="admin.provider.formSlpUsername" />:</td>
			<td><input type="text" name="xml_p_slpusername"></td>
		</tr>
		<tr>
			<td align="right"><bean:message
				key="admin.provider.formSlpPassword" />:</td>
			<td><input type="text" name="xml_p_slppassword"></td>
		</tr>
		<tr>
			<td align="right"><bean:message key="admin.provider.formStatus" />:
			</td>
			<td><input type="text" name="status" value='1' maxlength="1">
			</td>
		</tr>
	</caisi:isModuleLoad>
	<tr>
		<td colspan="2">
		<div align="center"><%-- not quite sure why we need both dboperation and displaymode set to the same thing, but
                 that's the way I found it so that's the way I'll leave it... --%>
		<input type="hidden" name="displaymode" value="Provider_Add_Record">
		<input type="submit" name="submitbtn"
			value="<bean:message key="admin.provideraddrecordhtm.btnProviderAddRecord"/>">
		</div>
		</td>
	</tr>
</table>
</form>

<p></p>
<hr width="100%" color="orange">
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td><a href="admin.jsp"> <img src="../images/leftarrow.gif"
			border="0" width="25" height="20" align="absmiddle"><bean:message
			key="global.btnBack" /></a></td>
		<!--  td align="right"><a href="../logout.jsp"><bean:message key="global.btnLogout"/><img src="../images/rightarrow.gif"  border="0" width="25" height="20" align="absmiddle"></a></td -->
	</tr>
</table>

</center>
</body>
</html:html>
