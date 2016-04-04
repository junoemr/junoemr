<%--

    Copyright (c) 2006-. OSCARservice, OpenSoft System. All Rights Reserved.
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

--%>
<%@ taglib uri="/WEB-INF/oscar-tag.tld" prefix="oscar" %>
<%! boolean bMultisites = org.oscarehr.common.IsPropertiesOn.isMultisitesEnable();
	List<String> mgrSites = new ArrayList<String>();
%>
<%
if (bMultisites)
{
        	SiteDao siteDao = (SiteDao)WebApplicationContextUtils.getWebApplicationContext(application).getBean("siteDao");
          	List<Site> sites = siteDao.getActiveSitesByProviderNo((String) session.getAttribute("user"));
          	for (Site s : sites) {
          		mgrSites.add(s.getName());
          	}
}
 %>
<%@ page import="oscar.login.DBHelp"%>
<%// start
			if (session.getAttribute("user") == null)
				response.sendRedirect("../../../logout.htm");
			String curUser_no, userfirstname, userlastname;
			curUser_no = (String) session.getAttribute("user");
			int MAXRECORDS = 6;  //number of billing items to display if record has less than 6
			String UpdateDate = "";
			String DemoNo = "";
			String DemoName = "";
			String DemoAddress = "";
			String DemoCity = "";
			String DemoProvince = "";
			String DemoPostal = "";
			String DemoDOB = "";
                        String DemoRS = "";
			String DemoSex = "";
			String hin = "";
			String location = "";
			String BillLocation = "";
			String BillLocationNo = "";
			String BillDate = "";
			String Provider = "";
			String BillType = "";
			String payProgram = "";
			String BillTotal = "";
			String visitdate = "";
			String visittype = "";
			String sliCode = "";
			String BillDTNo = "";
			String HCTYPE = "";
			String HCSex = "";
			String r_doctor_ohip = "";
			String r_doctor = "";
			String r_doctor_ohip_s = "";
			String r_doctor_s = "";
			String m_review = "";
			String specialty = "";
			String r_status = "";
			String roster_status = "";
			String comment = "";
                        String payer = "";
			String htmlPaid = "";
			int rowCount = 0;
			int rowReCount = 0;
			ResultSet rslocation = null;
			ResultSet rsPatient = null;

			%>

<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%
	if(session.getAttribute("user") == null ) response.sendRedirect("../logout.jsp");
	String curProvider_no = (String) session.getAttribute("user");

	if(session.getAttribute("userrole") == null )  response.sendRedirect("../logout.jsp");
	String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");

	boolean isTeamBillingOnly=false;
    boolean isSiteAccessPrivacy=false;
    boolean isTeamAccessPrivacy=false;

	boolean isMultiSiteProvider = true;
%>
<security:oscarSec objectName="_team_billing_only" roleName="<%= roleName$ %>" rights="r" reverse="false">
<% isTeamBillingOnly=true; %>
</security:oscarSec>
<security:oscarSec objectName="_site_access_privacy" roleName="<%=roleName$%>" rights="r" reverse="false">
	<%isSiteAccessPrivacy=true; %>
</security:oscarSec>
<security:oscarSec objectName="_team_access_privacy" roleName="<%=roleName$%>" rights="r" reverse="false">
	<%isTeamAccessPrivacy=true; %>
</security:oscarSec>

<%
HashMap<String,String> providerMap = new HashMap<String,String>();
//multisites function
if (isSiteAccessPrivacy || isTeamAccessPrivacy) {
	String sqlStr = "select provider_no from provider ";
	if (isSiteAccessPrivacy)
		sqlStr = "select distinct p.provider_no from provider p inner join providersite s on s.provider_no = p.provider_no "
		 + " where s.site_id in (select site_id from providersite where provider_no = " + curProvider_no + ")";
	if (isTeamAccessPrivacy)
		sqlStr = "select distinct p.provider_no from provider p where team in (select team from provider "
				+ " where team is not null and team <> '' and provider_no = " + curProvider_no + ")";
	DBHelp dbObj = new DBHelp();
	ResultSet rs = dbObj.searchDBRecord(sqlStr);
	while (rs.next()) {
		providerMap.put(rs.getString("provider_no"),"true");
	}
	rs.close();
}
%>

<%@ page import="java.math.*,java.util.*,java.sql.*,oscar.*,java.net.*"
	errorPage="errorpage.jsp"%>
<%@ page import="oscar.oscarBilling.ca.on.data.*"%>
<%@ page import="oscar.oscarBilling.ca.on.pageUtil.*"%>
<%@ page import="oscar.oscarDemographic.data.*"%>
<%@ page import="oscar.util.UtilDateUtilities"  %>
<%@page import="org.oscarehr.util.MiscUtils"%>

<%GregorianCalendar now = new GregorianCalendar();
			int curYear = now.get(Calendar.YEAR);
			int curMonth = (now.get(Calendar.MONTH) + 1);
			int curDay = now.get(Calendar.DAY_OF_MONTH);
%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>

<%@page import="org.oscarehr.common.dao.SiteDao"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@page import="org.oscarehr.common.model.Site"%>
<%@page import="org.oscarehr.common.model.Provider"%>
<%@page import="org.oscarehr.util.SpringUtils"%>
<%@page import="org.oscarehr.common.model.ClinicNbr"%>
<%@page import="org.oscarehr.common.dao.ClinicNbrDao"%>

<html:html locale="true">
<head>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/checkDate.js"></script>
<title><bean:message key="billing.billingCorrection.title" /></title>
<link rel="stylesheet" type="text/css" href="billingON.css" />
<!-- calendar stylesheet -->
<link rel="stylesheet" type="text/css" media="all"
	href="../../../share/calendar/calendar.css" title="win2k-cold-1" />
<!-- main calendar program -->
<script type="text/javascript" src="../../../share/calendar/calendar.js"></script>
<!-- language for the calendar -->
<script type="text/javascript"
	src="../../../share/calendar/lang/calendar-en.js"></script>
<!-- the following script defines the Calendar.setup helper function, which makes
       adding a calendar a matter of 1 or 2 lines of code. -->
<script type="text/javascript"
	src="../../../share/calendar/calendar-setup.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
   <script>
     jQuery.noConflict();
   </script>
<oscar:customInterface section="editInvoice"/>

<script language="JavaScript">
<!--


function setfocus() {	
	document.form1.billing_no.focus();
	document.form1.billing_no.select();
}
function rs(n,u,w,h,x) {
	args="width="+w+",height="+h+",resizable=yes,scrollbars=yes,status=0,top=60,left=30";
	remote=window.open(u,n,args);
	if (remote != null) {
		if (remote.opener == null)
			remote.opener = self;
	}
	if (x == 1) { return remote; }
}


var awnd=null;
function ScriptAttach() {
	f0 = escape(document.serviceform.xml_diagnostic_detail.value);
	f1 = document.serviceform.xml_dig_search1.value;
	// f2 = escape(document.serviceform.elements["File2Data"].value);
	// fname = escape(document.Compose.elements["FName"].value);
	awnd=rs('att','billingDigSearch.jsp?name='+f0 + '&search=' + f1,600,600,1);
	awnd.focus();
}
function referralScriptAttach2(elementName, name2) {
     var d = elementName;
     t0 = escape("document.forms[1].elements[\'"+d+"\'].value");
     t1 = escape("document.forms[1].elements[\'"+name2+"\'].value");
     awnd=rs('att',('searchRefDoc.jsp?param='+t0+'&param2='+t1),600,600,1);
     awnd.focus();
}
function scScriptAttach(nameF) {
	f0 = document.forms[1].elements[nameF].value;
    f1 = escape("document.forms[1].elements[\'"+nameF+ "\'].value");
	awnd=rs('att','billingCodeSearch.jsp?name='+f0 + '&search=&name1=&name2=&nameF='+f1,600,600,1);
	awnd.focus();
}

function search3rdParty(elementName) {
    var d = elementName;
    t0 = escape("document.forms[1].elements[\'"+d+"\'].value");
    popupPage('600', '700', 'onSearch3rdBillAddr.jsp?param='+t0);
}

function validateNum(el){
   var val = el.value;
   var tval = ""+val;

   if (isNaN(val)){
      alert("Item value must be numeric.");
      el.select();
      el.focus();
      return false;
   }
   if ( val > 9999.99 ){
     alert("Item value must be below $10000");
     el.select();
     el.focus();
     return false;
   }
   decLen = tval.indexOf(".");
   if (decLen != -1  &&   ( tval.length - decLen ) > 3  ){
      alert("Item value has a maximum of 2 decimal places");
      el.select();
      el.focus();
      return false;
   }
   
   return true;
}

function validateAllItems(){

   var provider = document.getElementById("provider_no");
   if( provider.options[provider.selectedIndex].value == "" ) {
      alert("Billing provider must be set");
      return false;
   }

   var billamt;
   for( idx = 0; idx < <%=MAXRECORDS%>; ++idx ) {
       billamt = document.getElementById("billingamount" + idx);       
       if( billamt != undefined && !validateNum(billamt) ) {    	   
           return false;
       }
   }
   
   var statusOpts = document.getElementById("status");
   var status = statusOpts.options[statusOpts.selectedIndex].value;
   var payPrgrmOpts = document.getElementById("payProgram");
   var payPrgrm = payPrgrmOpts.options[payPrgrmOpts.selectedIndex].value;   
   if( status == "P" && (payPrgrm == "HCP" || payPrgrm == "RMB" || payPrgrm == "WCB" )) {
	   alert("Pay Program does not match bill status.");
	   return false;
   }
   
   // validate dates
   var billingDate  = document.getElementById("xml_appointment_date").value;
   var admisionDate = document.getElementById("xml_vdate").value;
   if(!( admisionDate.trim() === "" || checkAndValidateDate(admisionDate, null) )) {
	   return false;
   }
   if(!checkAndValidateDate(billingDate, null) ) {
	   return false;
   }
   return true;
}
function popupPage(vheight,vwidth,varpage) {
  var page = "" + varpage;
  windowprops = "height="+vheight+",width="+vwidth+",location=no,scrollbars=yes,menubars=no,toolbars=no,resizable=yes,screenX=0,screenY=0,top=0,left=0";
  var popup=window.open(page, "billcorrection", windowprops);
    if (popup != null) {
	    if (popup.opener == null) {
	      popup.opener = self;
	    }
    	popup.focus();
    }
}

function sanityCheck(id) {
    if( id != "" ) {
        location.href = "billingON3rdInv.jsp?billingNo=" + id;
    }
    else {
        alert("Please search a valid invoice number");
    }
    return false;
}

function checkSettle(status) {
    if( status == 'S' ) {
        var payElem = document.getElementById("payment");
        if( payElem != null ) {
            payElem.value = document.getElementById("billTotal").value;
        }
    }        
    else if( status == 'P') {
    	document.getElementById("thirdParty").style.display = "inline";
    	document.getElementById("thirdPartyPymnt").style.display = "inline";
    	
    	document.getElementById("payment").disabled = false;
    	document.getElementById("oldPayment").disabled = false;
    	document.getElementById("payDate").disabled = false;
    	document.getElementById("refund").disabled = false;
    	document.getElementById("billTo").disabled = false;
    }
    else {
    	document.getElementById("thirdParty").style.display = "none";
    	document.getElementById("thirdPartyPymnt").style.display = "none";
    	
    	document.getElementById("payment").disabled = true;
    	document.getElementById("oldPayment").disabled = true;
    	document.getElementById("payDate").disabled = true;
    	document.getElementById("refund").disabled = true;
    	document.getElementById("billTo").disabled = true;
    }

}

//-->
</script>
</head>

<body bgcolor="ivory" text="#000000" topmargin="0" leftmargin="0"
	rightmargin="0" onLoad="setfocus()">
<!--  <table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr bgcolor="#000000">
		<th height="40" width="10%"></th>
		<th width="90%" align="left"><font face="Verdana, Arial" color="#FFFFFF" size="4"><bean:message
			key="billing.billingCorrection.msgCorrection" /></font></th>
	</tr>
</table>
-->
<%//
                                JdbcBillingRAImpl raObj = new JdbcBillingRAImpl();
				BillingCorrectionPrep obj = new BillingCorrectionPrep();
				List recordObj = null;
				BillingClaimHeader1Data ch1Obj = new BillingClaimHeader1Data();
				BillingItemData itemObj = null;

				// bFlag - fill in data?
				boolean bFlag = false;
				String billNo = request.getParameter("billing_no");
				if (billNo != null)
					billNo.trim();
					
                                String claimNo = request.getParameter("claim_no");

                                if( billNo == null || billNo.length() == 0 ) {
                                    if( claimNo != null && claimNo.length() > 0 ) {
                                        claimNo = claimNo.trim();
                                        billNo = raObj.getRABilllingNo4ClaimNo(claimNo);
                                    }

                                }
				
				if (billNo != null && billNo.length() > 0) {
					bFlag = true;
				}

				if (bFlag) {
					recordObj = obj.getBillingRecordObj(billNo);
					if (recordObj != null && recordObj.size() > 0) {

						ch1Obj = (BillingClaimHeader1Data) recordObj.get(0);

						//multisite. check provider no
					    if ((isSiteAccessPrivacy || isTeamAccessPrivacy) && (providerMap.get(ch1Obj.getProviderNo())== null || !mgrSites.contains(ch1Obj.getClinic())))
					    		isMultiSiteProvider = false;

					    if 	(isMultiSiteProvider) {
						UpdateDate = ch1Obj.getUpdate_datetime(); //.substring(0,10);
						DemoNo = ch1Obj.getDemographic_no();
						DemoName = ch1Obj.getDemographic_name();
						DemoAddress = "";
						DemoCity = "";
						DemoProvince = "";
						DemoPostal = "";
						DemoDOB = ch1Obj.getDob();
						DemoSex = ch1Obj.getSex().equals("1") ? "M" : "F";

						org.oscarehr.common.model.Demographic sdemo = (new DemographicData()).getDemographic(DemoNo);
						hin = sdemo.getHin()+sdemo.getVer();
						DemoDOB = sdemo.getYearOfBirth() + sdemo.getMonthOfBirth() + sdemo.getDateOfBirth();
						DemoSex = sdemo.getSex();
						DemoRS = sdemo.getRosterStatus();
						//hin = ch1Obj.getHin() + ch1Obj.getVer();
						location = ch1Obj.getFacilty_num();
						BillLocation = "";
						BillLocationNo = ch1Obj.getFacilty_num();
						BillDate = ch1Obj.getBilling_date();
						Provider = ch1Obj.getProviderNo();
						BillType = ch1Obj.getStatus();
						payProgram = ch1Obj.getPay_program();
						BillTotal = ch1Obj.getTotal();
						visitdate = ch1Obj.getAdmission_date();
						visittype = ch1Obj.getVisittype();
						sliCode = ch1Obj.getLocation();
						BillDTNo = "";
						HCTYPE = ch1Obj.getProvince();
						HCSex = ch1Obj.getSex();
						r_doctor_ohip = ch1Obj.getRef_num();
						r_doctor = "";
						r_doctor_ohip_s = "";
						r_doctor_s = "";
						m_review = ch1Obj.getMan_review();
						specialty = "";
						r_status = "";
						roster_status = "";
						comment = ch1Obj.getComment();
						//paid = ch1Obj.getPaid();
						
						// get ohip claim number
						claimNo = raObj.getRAClaimNo4BillingNo( billNo );
						
					}
					    else {
							UpdateDate = "";
							DemoNo = "";
							DemoName = "";
							DemoAddress = "";
							DemoCity = "";
							DemoProvince = "";
							DemoPostal = "";
							DemoDOB = "";
							DemoSex = "";
							r_doctor = "";
							r_doctor_ohip_s = "";
							r_doctor_s = "";
							m_review = ch1Obj.getMan_review();
							specialty = "";
							r_status = "";
							roster_status = "";

							out.write("<script>window.alert('sorry, billing access denied.')</script>");
					    }
					}
				}
				boolean thirdParty = false;
				Billing3rdPartPrep tObj = new Billing3rdPartPrep();
				
				if("HCP".equals(payProgram) || "RMB".equals(payProgram) || "WCB".equals(payProgram)
						|| billNo.length() < 1) {
					
					Properties tProp = null;					
					if( billNo.length() > 0 ) {
						tProp = tObj.get3rdPartBillPropInactive(billNo.trim());						
					}
					
					if( tProp == null || tProp.size() == 0 ) {
						htmlPaid = "Paid<br><input type='text' id='payment' name='payment' size=5 value='0.00'/>" +
							"<input type='hidden' id='oldPayment' name='oldPayment' value='0.00'/> <input type='hidden' id='payDate' name='payDate' value='" +
                        	UtilDateUtilities.getToday("yyyy-MM-dd HH:mm:ss") + "'/><br> Refund<br><input type='text' id='refund' name='refund' size=5 value='0.00'/><br>";
                        	payer = "";
					}
					else {
						htmlPaid = "Paid<br><input type='text' id='payment' name='payment' size=5 value='"
					    	+ tProp.getProperty("payment","0.00") + "' /><input type='hidden' id='oldPayment' name='oldPayment' value='"
		                    + tProp.getProperty("payment","0.00") + "' /><input type='hidden' id='payDate' name='payDate' value='"
		                    + UtilDateUtilities.getToday("yyyy-MM-dd HH:mm:ss") + "'/><br>";
						htmlPaid += "Refund<br><input type='text' id='refund' name='refund' size=5 value='"
							+ tProp.getProperty("refund") + "' /><br>";
						payer = tProp.getProperty("billTo");
                        if( payer == null ) {
                        	payer = "";
                       	}
					}
				} else {
					thirdParty = true;
					Properties tProp = tObj.get3rdPartBillProp(billNo.trim());	
					htmlPaid = "Paid<br><input type='text' id='payment' name='payment' size=5 value='"
						+ tProp.getProperty("payment") + "' /><input type='hidden' id='oldPayment' name='oldPayment' value='"
                                                + tProp.getProperty("payment") + "' /><input type='hidden' id='payDate' name='payDate' value='"
                                                + UtilDateUtilities.getToday("yyyy-MM-dd HH:mm:ss") + "'/><br>";
					htmlPaid += "Refund<br><input type='text' id='refund' name='refund' size=5 value='"
						+ tProp.getProperty("refund") + "' /><br>";
                                        payer = tProp.getProperty("billTo");
                                        if( payer == null ) {
                                            payer = "";
                                        }
				}

%>

<table width="100%" border="0" class="myYellow">
	<form name="form1" method="post" action="billingONCorrection.jsp">
            <input type="hidden" id="billTotal" value="<%=BillTotal%>" />
	<tr>
		<th width="30%" align="left"><a
			href="#" onclick="return sanityCheck('<%=nullToEmpty(ch1Obj.getId())%>');">
		<bean:message key="billing.billingCorrection.formInvoiceNo" /></a></th>
		<th width="10%"><input type="text" name="billing_no"
			value="<%=nullToEmpty(ch1Obj.getId()) %>" maxsize="10"></th>
		<th width="50%" align="left"><bean:message
			key="billing.billingCorrection.msgLastUpdate" />: <%=nullToEmpty(ch1Obj.getUpdate_datetime())%></th>
		<th><input type="submit" name="submit" value="Search"></th>
	</tr>
        <tr>
		<th width="30%" align="left">
                    OHIP Claim No
		</th>
		<th style="text-align:left;" colspan="3" width="10%"><input type="text" name="claim_no"
			value="<%=nullToEmpty(claimNo)%>" maxsize="10"></th>
	</tr>
	</form>
</table>

<!-- RA error -->
<%
if(bFlag) {
	//billNo = "44071";
	List lReject = obj.getBillingRejectList(billNo);
	List lError = obj.getBillingExplanatoryList(billNo);
	lError.addAll(lReject);
	JdbcBillingErrorCodeImpl errorObj = new JdbcBillingErrorCodeImpl();
%>
<table width="100%" border="0" class="myIvory">
	<% for(int i=0; i<lError.size(); i++) {
	String codeNo = (String) lError.get(i);
	if("".equals(codeNo)) continue;
	String codeDesc = errorObj.getCodeDesc((String)lError.get(i));
	codeDesc = codeDesc == null ? "Unknown" : codeDesc;
%>
	<tr>
		<th width="10%"><b><%=codeNo %></b></th>
		<td align="left"><%=codeDesc %></td>
	</tr>
	<% } %>
</table>
<% } %>

<form name="serviceform" method="post"
	action="billingONCorrectionSave.jsp">
	<input type="hidden"
	name="xml_billing_no" value="<%=billNo%>" /> <input type="hidden"
	name="update_date" value="<%=UpdateDate%>" />
    <input type="hidden" name="payDate" value="<%=UtilDateUtilities.getToday("yyyy-MM-dd HH:mm:ss")%>" />
    <input type="hidden" name="demoNo" value="<%=DemoNo%>" />
    <input type="hidden" name="oldStatus" value="<%=thirdParty ? "thirdParty" : "" %>" />
<table width="600" border="0">
	<tr class="myGreen">
		<th align="left" colspan="2"><b><bean:message
			key="billing.billingCorrection.msgPatientInformation" /></b></th>
	</tr>
	<tr>
		<td width="54%"><b><bean:message
			key="billing.billingCorrection.msgPatientName" />: <a href=#
			onclick="popupPage(720,860,'../../../demographic/demographiccontrol.jsp?demographic_no=<%=DemoNo %>&displaymode=edit&dboperation=search_detail');return false;">
		<%=DemoName%></a> <input type="hidden" name="demo_name"
			value="<%=DemoName%>"> </b></td>
		<td width="46%"><b><bean:message
			key="billing.billingCorrection.formHealth" />: <%=hin%> <input
			type="hidden" name="xml_hin" value="<%=hin%>">&nbsp; &nbsp;
		RS: <%=DemoRS%> </b></td>
	</tr>
	<tr>
		<td><b><bean:message key="billing.billingCorrection.msgSex" />:
		<%=DemoSex%> <input type="hidden" name="demo_sex" value="<%=DemoSex%>">
		<input type="hidden" name="hc_sex" value="<%=HCSex%>"> </b></td>
		<td><b><bean:message key="billing.billingCorrection.formDOB" />:
		<input type="hidden" name="xml_dob" value="<%=DemoDOB%>"> <%=DemoDOB%>
		</b></td>
	</tr>
	<tr>
		<td><strong><bean:message
			key="billing.billingCorrection.msgDoctor" />: <input type="text"
			name="rd" value="<%=r_doctor%>" size=20 readonly></strong></td>
		<td><strong><bean:message
			key="billing.billingCorrection.msgDoctorNo" />: <input type="text"
			name="rdohip" value="<%=r_doctor_ohip%>" size=8 readonly /></strong> <a
			href="javascript:referralScriptAttach2('rdohip','rd')">Search</a></td>
	</tr>
</table>

<table width="600" border="0">
	<tr class="myGreen">
		<td colspan=2><strong><bean:message
			key="billing.billingCorrection.msgAditInfo" /></strong></td>
		<!--  td width="270"><strong><bean:message
			key="billing.billingCorrection.formSpecialty" /> </strong> <select name="specialty" style="font-size:80%;">
			<option value="none"><bean:message key="billing.billingCorrection.formNone" /></option>
			<option value="flu" <%=specialty.equals("flu")?"selected":""%>><bean:message key="billing.billingCorrection.formFlu" /></option></td>-->
	</tr>
	<tr class="myIvory">
		<td width="320"><strong><bean:message
			key="billing.billingCorrection.formHCType" />:</strong> <select
			name="hc_type" style="font-size: 80%;">
                        <option value="OT" <%=HCTYPE.equals("OT")?" selected":""%>>OT-Other</option>
			<option value="AB" <%=HCTYPE.equals("AB")?" selected":""%>>AB-Alberta</option>
                        <option value="BC" <%=HCTYPE.equals("BC")?" selected":""%>>BC-British Columbia</option>
                        <option value="MB" <%=HCTYPE.equals("MB")?" selected":""%>>MB-Manitoba</option>
                        <option value="NB" <%=HCTYPE.equals("NB")?" selected":""%>>NB-New Brunswick</option>
                        <option value="NL" <%=HCTYPE.equals("NL")?" selected":""%>>NL-Newfoundland & Labrador</option>
                        <option value="NT" <%=HCTYPE.equals("NT")?" selected":""%>>NT-Northwest Territory</option>
                        <option value="NS" <%=HCTYPE.equals("NS")?" selected":""%>>NS-Nova Scotia</option>
                        <option value="NU" <%=HCTYPE.equals("NU")?" selected":""%>>NU-Nunavut</option>
                        <option value="ON" <%=HCTYPE.equals("ON")?" selected":""%>>ON-Ontario</option>
                        <option value="PE" <%=HCTYPE.equals("PE")?" selected":""%>>PE-Prince Edward Island</option>
                        <option value="QC" <%=HCTYPE.equals("QC")?" selected":""%>>QC-Quebec</option>
                        <option value="SK" <%=HCTYPE.equals("SK")?" selected":""%>>SK-Saskatchewan</option>
                        <option value="YT" <%=HCTYPE.equals("YT")?" selected":""%>>YT-Yukon</option>
                        <option value="US" <%=HCTYPE.equals("US")?" selected":""%>>US resident</option>
                        <option value="US-AK" <%=HCTYPE.equals("US-AK")?" selected":""%>>US-AK-Alaska</option>
                        <option value="US-AL" <%=HCTYPE.equals("US-AL")?" selected":""%>>US-AL-Alabama</option>
                        <option value="US-AR" <%=HCTYPE.equals("US-AR")?" selected":""%>>US-AR-Arkansas</option>
                        <option value="US-AZ" <%=HCTYPE.equals("US-AZ")?" selected":""%>>US-AZ-Arizona</option>
                        <option value="US-CA" <%=HCTYPE.equals("US-CA")?" selected":""%>>US-CA-California</option>
                        <option value="US-CO" <%=HCTYPE.equals("US-CO")?" selected":""%>>US-CO-Colorado</option>
                        <option value="US-CT" <%=HCTYPE.equals("US-CT")?" selected":""%>>US-CT-Connecticut</option>
                        <option value="US-CZ" <%=HCTYPE.equals("US-CZ")?" selected":""%>>US-CZ-Canal Zone</option>
                        <option value="US-DC" <%=HCTYPE.equals("US-DC")?" selected":""%>>US-DC-District Of Columbia</option>
                        <option value="US-DE" <%=HCTYPE.equals("US-DE")?" selected":""%>>US-DE-Delaware</option>
                        <option value="US-FL" <%=HCTYPE.equals("US-FL")?" selected":""%>>US-FL-Florida</option>
                        <option value="US-GA" <%=HCTYPE.equals("US-GA")?" selected":""%>>US-GA-Georgia</option>
                        <option value="US-GU" <%=HCTYPE.equals("US-GU")?" selected":""%>>US-GU-Guam</option>
                        <option value="US-HI" <%=HCTYPE.equals("US-HI")?" selected":""%>>US-HI-Hawaii</option>
                        <option value="US-IA" <%=HCTYPE.equals("US-IA")?" selected":""%>>US-IA-Iowa</option>
                        <option value="US-ID" <%=HCTYPE.equals("US-ID")?" selected":""%>>US-ID-Idaho</option>
                        <option value="US-IL" <%=HCTYPE.equals("US-IL")?" selected":""%>>US-IL-Illinois</option>
                        <option value="US-IN" <%=HCTYPE.equals("US-IN")?" selected":""%>>US-IN-Indiana</option>
                        <option value="US-KS" <%=HCTYPE.equals("US-KS")?" selected":""%>>US-KS-Kansas</option>
                        <option value="US-KY" <%=HCTYPE.equals("US-KY")?" selected":""%>>US-KY-Kentucky</option>
                        <option value="US-LA" <%=HCTYPE.equals("US-LA")?" selected":""%>>US-LA-Louisiana</option>
                        <option value="US-MA" <%=HCTYPE.equals("US-MA")?" selected":""%>>US-MA-Massachusetts</option>
                        <option value="US-MD" <%=HCTYPE.equals("US-MD")?" selected":""%>>US-MD-Maryland</option>
                        <option value="US-ME" <%=HCTYPE.equals("US-ME")?" selected":""%>>US-ME-Maine</option>
                        <option value="US-MI" <%=HCTYPE.equals("US-MI")?" selected":""%>>US-MI-Michigan</option>
                        <option value="US-MN" <%=HCTYPE.equals("US-MN")?" selected":""%>>US-MN-Minnesota</option>
                        <option value="US-MO" <%=HCTYPE.equals("US-MO")?" selected":""%>>US-MO-Missouri</option>
                        <option value="US-MS" <%=HCTYPE.equals("US-MS")?" selected":""%>>US-MS-Mississippi</option>
                        <option value="US-MT" <%=HCTYPE.equals("US-MT")?" selected":""%>>US-MT-Montana</option>
                        <option value="US-NC" <%=HCTYPE.equals("US-NC")?" selected":""%>>US-NC-North Carolina</option>
                        <option value="US-ND" <%=HCTYPE.equals("US-ND")?" selected":""%>>US-ND-North Dakota</option>
                        <option value="US-NE" <%=HCTYPE.equals("US-NE")?" selected":""%>>US-NE-Nebraska</option>
                        <option value="US-NH" <%=HCTYPE.equals("US-NH")?" selected":""%>>US-NH-New Hampshire</option>
                        <option value="US-NJ" <%=HCTYPE.equals("US-NJ")?" selected":""%>>US-NJ-New Jersey</option>
                        <option value="US-NM" <%=HCTYPE.equals("US-NM")?" selected":""%>>US-NM-New Mexico</option>
                        <option value="US-NU" <%=HCTYPE.equals("US-NU")?" selected":""%>>US-NU-Nunavut</option>
                        <option value="US-NV" <%=HCTYPE.equals("US-NV")?" selected":""%>>US-NV-Nevada</option>
                        <option value="US-NY" <%=HCTYPE.equals("US-NY")?" selected":""%>>US-NY-New York</option>
                        <option value="US-OH" <%=HCTYPE.equals("US-OH")?" selected":""%>>US-OH-Ohio</option>
                        <option value="US-OK" <%=HCTYPE.equals("US-OK")?" selected":""%>>US-OK-Oklahoma</option>
                        <option value="US-OR" <%=HCTYPE.equals("US-OR")?" selected":""%>>US-OR-Oregon</option>
                        <option value="US-PA" <%=HCTYPE.equals("US-PA")?" selected":""%>>US-PA-Pennsylvania</option>
                        <option value="US-PR" <%=HCTYPE.equals("US-PR")?" selected":""%>>US-PR-Puerto Rico</option>
                        <option value="US-RI" <%=HCTYPE.equals("US-RI")?" selected":""%>>US-RI-Rhode Island</option>
                        <option value="US-SC" <%=HCTYPE.equals("US-SC")?" selected":""%>>US-SC-South Carolina</option>
                        <option value="US-SD" <%=HCTYPE.equals("US-SD")?" selected":""%>>US-SD-South Dakota</option>
                        <option value="US-TN" <%=HCTYPE.equals("US-TN")?" selected":""%>>US-TN-Tennessee</option>
                        <option value="US-TX" <%=HCTYPE.equals("US-TX")?" selected":""%>>US-TX-Texas</option>
                        <option value="US-UT" <%=HCTYPE.equals("US-UT")?" selected":""%>>US-UT-Utah</option>
                        <option value="US-VA" <%=HCTYPE.equals("US-VA")?" selected":""%>>US-VA-Virginia</option>
                        <option value="US-VI" <%=HCTYPE.equals("US-VI")?" selected":""%>>US-VI-Virgin Islands</option>
                        <option value="US-VT" <%=HCTYPE.equals("US-VT")?" selected":""%>>US-VT-Vermont</option>
                        <option value="US-WA" <%=HCTYPE.equals("US-WA")?" selected":""%>>US-WA-Washington</option>
                        <option value="US-WI" <%=HCTYPE.equals("US-WI")?" selected":""%>>US-WI-Wisconsin</option>
                        <option value="US-WV" <%=HCTYPE.equals("US-WV")?" selected":""%>>US-WV-West Virginia</option>
                        <option value="US-WY" <%=HCTYPE.equals("US-WY")?" selected":""%>>US-WY-Wyoming</option>
		</select></td>
		<td width="270"><strong><bean:message
			key="billing.billingCorrection.formManualReview" />: <input
			type="checkbox" name="m_review" value="Y"
			<%=m_review.equals("Y")?"checked":""%>> </strong></td>
	</tr>
	<!--  tr bgcolor="#EEEEFF">
		<td><strong><bean:message key="billing.billingCorrection.msgDoctor" />:
		<input type="checkbox" name="referral" value="checkbox" <%=r_status.equals("checked")?"checked":""%>> </strong></td>
		<td><strong><bean:message key="billing.billingCorrection.msgRoster" />:
		<input type="hidden" name="roster" value="<%=roster_status%>"><%=roster_status%> </strong></td>
	</tr>-->
</table>

<table width="600" border="0">
	<tr class="myGreen">
		<td><b><bean:message
			key="billing.billingCorrection.msgBillingInf" /></b></td>
		<td width="46%"><bean:message
			key="billing.billingCorrection.btnBillingDate" /><img
			src="../../../images/cal.gif" id="xml_appointment_date_cal" />: <input
			type="text" id="xml_appointment_date" name="xml_appointment_date"
			value="<%=BillDate%>" size=10 /></td>
	</tr>
	<tr>
		<td width="54%"><b><bean:message
			key="billing.billingCorrection.formBillingType" />: </b> <input
			type="hidden" name="xml_status" value="<%=BillType%>"> <select
                        style="font-size: 80%;" id="status" name="status" onchange="checkSettle(this.options[this.selectedIndex].value);">
			<option value=""><bean:message
				key="billing.billingCorrection.formSelectBillType" /></option>
			<option value="H" <%=BillType.equals("H")?"selected":""%>><bean:message
				key="billing.billingCorrection.formBillTypeH" /></option>
			<option value="O" <%=BillType.equals("O")?"selected":""%>><bean:message
				key="billing.billingCorrection.formBillTypeO" /></option>
			<option value="P" <%=BillType.equals("P")?"selected":""%>><bean:message
				key="billing.billingCorrection.formBillTypeP" /></option>
			<option value="N" <%=BillType.equals("N")?"selected":""%>><bean:message
				key="billing.billingCorrection.formBillTypeN" /></option>
			<option value="W" <%=BillType.equals("W")?"selected":""%>><bean:message
				key="billing.billingCorrection.formBillTypeW" /></option>
			<option value="B" <%=BillType.equals("B")?"selected":""%>><bean:message
				key="billing.billingCorrection.formBillTypeB" /></option>
			<option value="S" <%=BillType.equals("S")?"selected":""%>>S
			| Settled</option>
			<option value="X" <%=BillType.equals("X")?"selected":""%>><bean:message
				key="billing.billingCorrection.formBillTypeX" /></option>
			<option value="D" <%=BillType.equals("D")?"selected":""%>><bean:message
				key="billing.billingCorrection.formBillTypeD" /></option>
                        <option value="I" <%=BillType.equals("I")?"selected":""%>><bean:message
				key="billing.billingCorrection.formBillTypeI" /></option>
		</select></td>
		<td width="46%"><b> Pay Program:</b> <input type="hidden"
			name="xml_payProgram" value="<%=BillDate%>" /><select
			style="font-size: 80%;" id="payProgram" name="payProgram">
			<%for (int i = 0; i < BillingDataHlp.vecPaymentType.size(); i = i + 2) {

					%>
			<option value="<%=BillingDataHlp.vecPaymentType.get(i) %>"
				<%=payProgram.equals(BillingDataHlp.vecPaymentType.get(i))? "selected":"" %>><%=BillingDataHlp.vecPaymentType.get(i + 1)%></option>
			<%}

				%>
		</select></td>
	</tr>
	<tr class="myGreen">
		<td width="54%"><b><bean:message
			key="billing.billingCorrection.formVisit" />:</b> <input type="hidden"
			name="xml_clinic_ref_code" value="<%=location%>"> <select
			name="clinic_ref_code">
			<option value=""><bean:message
				key="billing.billingCorrection.msgSelectLocation" /></option>
			<%//
				List lLocation = obj.getFacilty_num();
				for (int i = 0; i < lLocation.size(); i = i + 2) {
					BillLocationNo = (String) lLocation.get(i);
					BillLocation = (String) lLocation.get(i + 1);
%>
			<option value="<%=BillLocationNo%>"
				<%=location.equals(BillLocationNo)?"selected":""%>><%=BillLocationNo%>
			| <%=BillLocation%></option>

			<%}

				%>
		</select></td>
		<td width="46%"><b><bean:message
			key="billing.billingCorrection.formBillingPhysician" />: </b><br />

<% // multisite start ==========================================
String curSite = request.getParameter("site")==null?ch1Obj.getClinic():request.getParameter("site");
if (bMultisites)
{
        	SiteDao siteDao = (SiteDao)WebApplicationContextUtils.getWebApplicationContext(application).getBean("siteDao");
          	List<Site> sites = siteDao.getActiveSitesByProviderNo((String) session.getAttribute("user"));
          	// now get all providers eligible


			List pList;

			if (isTeamBillingOnly || isTeamAccessPrivacy) {
				pList = (new JdbcBillingPageUtil()).getCurTeamProviderStr((String) session.getAttribute("user"));
			}
			else if (isSiteAccessPrivacy) {
				pList = (new JdbcBillingPageUtil()).getCurSiteProviderStr((String) session.getAttribute("user"));
			}
			else {
				pList =  (new JdbcBillingPageUtil()).getCurProviderStr();
			}

          	HashSet<String> pros=new HashSet<String>();
          	for (Object s:pList) {
          		pros.add(((String)s).substring(0, ((String)s).indexOf("|")));
          	}
      %>
      <script>
var _providers = [];
<%	for (int i=0; i<sites.size(); i++) { %>
	_providers["<%= sites.get(i).getName() %>"]="<% Iterator<Provider> iter = sites.get(i).getProviders().iterator();
	while (iter.hasNext()) {
		Provider p=iter.next();
		if (pros.contains(p.getProviderNo())) {
	%><option value='<%= p.getProviderNo() %>'><%= p.getLastName() %>, <%= p.getFirstName() %></option><% }} %>";
<% } %>
function changeSite(sel) {
	sel.form.provider_no.innerHTML=sel.value=="none"?"":_providers[sel.value];
	sel.style.backgroundColor=sel.options[sel.selectedIndex].style.backgroundColor;
}
      </script>
      	<select id="site" name="site" style="font-size: 80%;" onchange="changeSite(this)">
      		<option value="none" style="background-color:white">---select clinic---</option>
      	<%
      	for (int i=0; i<sites.size(); i++) {
      	%>
      		<option value="<%= sites.get(i).getName() %>" style="background-color:<%= sites.get(i).getBgColor() %>"
      			 <%=sites.get(i).getName().toString().equals(curSite)?"selected":"" %>><%= sites.get(i).getName() %></option>
      	<% } %>
      	</select>
      	<select id="provider_no" name="provider_no" style="font-size: 80%;width:140px"></select>
      	<script>
     	changeSite(document.getElementById("site"));
      	document.getElementById("provider_no").value='<%=Provider%>';
      	</script>
<%  // multisite end ==========================================
} else {
%>
		<select
			id="provider_no" style="font-size: 80%;" name="provider_no">
			<option value=""><bean:message
				key="billing.billingCorrection.msgSelectProvider" /></option>
			<%

			List pList;

			if (isTeamBillingOnly || isTeamAccessPrivacy) {
				pList = (new JdbcBillingPageUtil()).getCurTeamProviderStr((String) session.getAttribute("user"));
			}
			else if (isSiteAccessPrivacy) {
				pList = (new JdbcBillingPageUtil()).getCurSiteProviderStr((String) session.getAttribute("user"));
			}
			else {
				pList =  (new JdbcBillingPageUtil()).getCurProviderStr();
			}


				for (int i = 0; i < pList.size(); i++) {
					String temp[] = ((String) pList.get(i)).split("\\|");

					%>
			<option value="<%=temp[0]%>"
				<%=Provider.equals(temp[0])?"selected":""%>><%=temp[0]%> |
			<%=temp[1]%>, <%=temp[2]%></option>
			<%}

				%>
		</select>
<% } %>


		 <input type="hidden" name="xml_provider_no" value="<%=Provider%>"></td>
	</tr>
	<tr>
		<td width="54%"><b> <%if (OscarProperties.getInstance().getBooleanProperty("rma_enabled", "true")) { %> Clinic Nbr <% } else { %> <bean:message key="billing.billingCorrection.formVisitType"/> <% } %>: </b> <input
			type="hidden" name="xml_visittype" value="<%=visittype%>">
			<select style="font-size: 80%;" name="visittype">
			<option value=""><bean:message key="billing.billingCorrection.msgSelectVisitType" /></option>
			<% if (OscarProperties.getInstance().getBooleanProperty("rma_enabled", "true")) { %>
			 <%
			    ClinicNbrDao cnDao = (ClinicNbrDao) SpringUtils.getBean("clinicNbrDao");
				ArrayList<ClinicNbr> nbrs = cnDao.findAll();
               	for (ClinicNbr clinic : nbrs) {
					String valueString = String.format("%s | %s", clinic.getNbrValue(), clinic.getNbrString());
					%>
				<option value="<%=valueString%>" <%=visittype.startsWith(clinic.getNbrValue())?"selected":""%>><%=valueString%></option>
		 	<%	}
			} else { %>
			<option value="00" <%=visittype.equals("00")?"selected":""%>><bean:message key="billing.billingCorrection.formClinicVisit" /></option>
			<option value="01" <%=visittype.equals("01")?"selected":""%>><bean:message key="billing.billingCorrection.formOutpatientVisit" /></option>
			<option value="02" <%=visittype.equals("02")?"selected":""%>><bean:message key="billing.billingCorrection.formHospitalVisit" /></option>
			<option value="03" <%=visittype.equals("03")?"selected":""%>><bean:message key="billing.billingCorrection.formER" /></option>
			<option value="04" <%=visittype.equals("04")?"selected":""%>><bean:message key="billing.billingCorrection.formNursingHome" /></option>
			<option value="05" <%=visittype.equals("05")?"selected":""%>><bean:message key="billing.billingCorrection.formHomeVisit" /></option>
			<% } %>
		</select></td>
		<td width="46%"><b> <input type="hidden" name="xml_visitdate"
			value="<%=visitdate%>" /> <bean:message
			key="billing.billingCorrection.btnAdmissionDate" /><img
			src="../../../images/cal.gif" id="xml_vdate_cal" />: <input
			type="text" id="xml_vdate" name="xml_vdate" value="<%=visitdate%>"
			size=10 /></b></td>
	</tr>
	<tr>
		<%String clinicNo = OscarProperties.getInstance().getProperty("clinic_no", "").trim();%>
		<td colspan="2"><b><bean:message key="oscar.billing.CA.ON.billingON.OB.SLIcode"/>: </b>
			<select name="xml_slicode">
				<option value="<%=clinicNo%>" ><bean:message key="oscar.billing.CA.ON.billingON.OB.SLIcode.NA" /></option>
				<option value="HDS " <%=sliCode.startsWith("HDS")?"selected":""%>><bean:message key="oscar.billing.CA.ON.billingON.OB.SLIcode.HDS" /></option>
				<option value="HED " <%=sliCode.startsWith("HED")?"selected":""%>><bean:message key="oscar.billing.CA.ON.billingON.OB.SLIcode.HED" /></option>
				<option value="HIP " <%=sliCode.startsWith("HIP")?"selected":""%>><bean:message key="oscar.billing.CA.ON.billingON.OB.SLIcode.HIP" /></option>
				<option value="HOP " <%=sliCode.startsWith("HOP")?"selected":""%>><bean:message key="oscar.billing.CA.ON.billingON.OB.SLIcode.HOP" /></option>
				<option value="HRP " <%=sliCode.startsWith("HRP")?"selected":""%>><bean:message key="oscar.billing.CA.ON.billingON.OB.SLIcode.HRP" /></option>
				<option value="IHF " <%=sliCode.startsWith("IHF")?"selected":""%>><bean:message key="oscar.billing.CA.ON.billingON.OB.SLIcode.IHF" /></option>
				<option value="OFF " <%=sliCode.startsWith("OFF")?"selected":""%>><bean:message key="oscar.billing.CA.ON.billingON.OB.SLIcode.OFF" /></option>
				<option value="OTN " <%=sliCode.startsWith("OTN")?"selected":""%>><bean:message key="oscar.billing.CA.ON.billingON.OB.SLIcode.OTN" /></option>
			</select>
	   </td>
</table>

<table width="600" border="0" cellspacing="1" cellpadding="0">
	<tr class="myYellow">
		<td width="30%" colspan=2><b><bean:message
			key="billing.billingCorrection.formServiceCode" /></b></td>
		<th width="50%"><b><bean:message
			key="billing.billingCorrection.formDescription" /></b></th>
		<th width="3%"><b><bean:message
			key="billing.billingCorrection.formUnit" /></b></th>
		<th width="13%" align="right"><b><bean:message
			key="billing.billingCorrection.formFee" /></b></th>
		<th><font size="-1">Settle</font></th>
	</tr>
	<%//
				String serviceCode = "";
				String serviceDesc = "";
				String billAmount = "";
				String diagCode = "";
				String diagDesc = "";
				String billingunit = "";
				String itemStatus = "";

				if (bFlag) {
					if (recordObj.size() > 1) {
						int maxRecs = Math.max(recordObj.size(), MAXRECORDS);
						for (int i = 1; i <= maxRecs; i++) {
							//multisite. skip display if billing provider_no not in current access privacy
							if (!isMultiSiteProvider) continue;

							if( i < recordObj.size() ) {
								itemObj = (BillingItemData) recordObj.get(i);

								serviceCode = itemObj.getService_code();
								serviceDesc = obj.getBillingCodeDesc(serviceCode);
								billAmount = itemObj.getFee();
								diagCode = itemObj.getDx();
								billingunit = itemObj.getSer_num();								
								itemStatus = itemObj.getStatus().equals("S") ? "checked" : "";
							}
							else {
								serviceCode = "";
								serviceDesc = "";
								billAmount = "";								
								billingunit = "";								
								itemStatus = "";
							}
							rowCount = rowCount + 1;
							%>

	<tr>
		<th width="25%"><input type="hidden"
			name="xml_service_code<%=rowCount%>" value="<%=serviceCode%>">
		<input type="text" style="width: 100%"
			name="servicecode<%=rowCount-1%>" value="<%=serviceCode%>"></th>
		<td><a href=# onClick="scScriptAttach('servicecode<%=rowCount-1%>')">Search</a></td>
		<th><font size="-1"><%=serviceDesc%></th>
		<th><input type="hidden" name="xml_billing_unit<%=rowCount%>"
			value="<%=billingunit%>"> <input type="text"
			style="width: 100%" name="billingunit<%=rowCount-1%>"
			value="<%=billingunit%>" size="5" maxlength="5"></th>
		<th align="right"><input type="hidden"
			name="xml_billing_amount<%=rowCount%>" value="<%=billAmount%>">
		<input type="text" style="width: 100%" size="5" maxlength="6"
			id="billingamount<%=rowCount-1%>" name="billingamount<%=rowCount-1%>"
			value="<%=billAmount%>" onchange="javascript:validateNum(this)"></th>
		<td align="center"><input type="checkbox"
			name="itemStatus<%=rowCount-1%>" id="itemStatus<%=rowCount-1%>"
			value="S" <%=itemStatus %>></td>
	</tr>
	<%//
						}
					}

						%>
	

	<%//}

				}
%>

	<tr class="myGreen">
		<td colspan="4"><b> <bean:message
			key="billing.billingCorrection.formDiagnosticCode" /></b></td>
		<td colspan="2"><b></b></td>
	</tr>
	<tr>
		<td colspan="4"><input type="hidden" name="xml_diagnostic_code"
			value="<%=diagCode%>"> <input type="text"
			style="font-size: 80%;" name="xml_diagnostic_detail"
			value="<%=diagCode%>" size="50"> <input type="hidden"
			name="xml_dig_search1"> <a href="javascript:ScriptAttach()"><bean:message
			key="billing.billingCorrection.btnDXSearch" /></a></td>
		<td colspan="2"></td>
	</tr>
	<tr>
		<td colspan="2"><input type="submit" name="submit" onclick="return validateAllItems();"
			value="<bean:message key="billing.billingCorrection.btnSubmit"/>"></td>
		<td colspan="4" align='right'><input type="submit" name="submit" onclick="return validateAllItems();"
			value="Submit&Correct Another"></td>
	</tr>
	<tr>
            <td colspan="6">
                
                    <span id="thirdParty" style="float:right; <%=thirdParty ? "" : "display:none"%>">
                        <a href="#" onclick="search3rdParty('billTo');return false;"><bean:message key="billing.billingCorrection.msgPayer"/></a><br>
                        <textarea id="billTo" name="billTo" value="" cols="32" rows=4><%=payer %></textarea>
                    </span>
                
                <span style="float:left;">
                    <bean:message key="billing.billingCorrection.msgNotes"/>:<br>
                    <textarea name="comment" cols="32" value="" rows=4><%=comment %></textarea>
                </span>
            </td>
	</tr>
	<tr>
		<td><a href="billingON3rdInv.jsp?billingNo=<%=billNo%>">Reprint</a>
		</td>
	</tr>
</table>
<div id="thirdPartyPymnt" style="<%=thirdParty ? "" : "display:none"%>"">
<%=htmlPaid %>
</div>
</form>
</body>
<script type="text/javascript">
Calendar.setup( { inputField : "xml_appointment_date", ifFormat : "%Y-%m-%d", showsTime :false, button : "xml_appointment_date_cal", singleClick : true, step : 1 } );
Calendar.setup( { inputField : "xml_vdate", ifFormat : "%Y-%m-%d", showsTime :false, button : "xml_vdate_cal", singleClick : true, step : 1 } );
</script>
<%!String nullToEmpty(String str) {
		return (str == null ? "" : str);
	}

	%>

</html:html>
