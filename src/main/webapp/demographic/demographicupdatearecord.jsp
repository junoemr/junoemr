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
<%@ page
	import="java.sql.*, java.util.*, oscar.MyDateFormat,oscar.oscarWaitingList.WaitingList, oscar.oscarWaitingList.util.WLWaitingListUtil, oscar.log.*, org.oscarehr.common.OtherIdManager"
	errorPage="errorpage.jsp"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@page import="org.oscarehr.common.dao.DemographicExtDao" %>
<%@page import="org.oscarehr.common.model.DemographicExt" %>
<%@page import="org.oscarehr.util.SpringUtils" %>
<jsp:useBean id="apptMainBean" class="oscar.AppointmentMainBean" scope="session" />
<%
	oscar.OscarProperties oscarVariables = oscar.OscarProperties.getInstance();
	DemographicExtDao demographicExtDao = SpringUtils.getBean(DemographicExtDao.class);
%>
<%
    if(session.getValue("user") == null) response.sendRedirect("../../logout.jsp");
%>

<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.oscarehr.util.MiscUtils"%><html:html locale="true">
<%@page import="org.oscarehr.util.SpringUtils" %>
<%@page import="org.oscarehr.common.model.Demographic" %>
<%@page import="org.oscarehr.common.dao.DemographicDao" %>
<%@page import="org.oscarehr.common.dao.DemographicArchiveDao" %>
<%@page import="org.oscarehr.common.model.DemographicArchive" %>
<%@page import="org.oscarehr.common.model.DemographicCust" %>
<%@page import="org.oscarehr.common.dao.DemographicCustDao" %>
<%@page import="org.apache.commons.lang.math.NumberUtils" %>
	<%
	DemographicDao demographicDao = (DemographicDao)SpringUtils.getBean("demographicDao");
	DemographicArchiveDao demographicArchiveDao = (DemographicArchiveDao)SpringUtils.getBean("demographicArchiveDao");
	DemographicCustDao demographicCustDao = (DemographicCustDao)SpringUtils.getBean("demographicCustDao");
%>
<head>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script></head>

<body>
<center>
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr bgcolor="#486ebd">
		<th align="CENTER"><font face="Helvetica" color="#FFFFFF">
		UPDATE demographic RECORD</font></th>
	</tr>
</table>
<%

  ResultSet rs = null;
  java.util.Locale vLocale =(java.util.Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY);

  Demographic demographic = demographicDao.getDemographic(request.getParameter("demographic_no"));
  if(demographic == null) {
	  //we have a problem!
  }
  demographic.setLastName(request.getParameter("last_name"));
  demographic.setFirstName(request.getParameter("first_name"));
  demographic.setAddress(request.getParameter("address"));
  demographic.setCity(request.getParameter("city"));
  demographic.setProvince(request.getParameter("province"));
  demographic.setPostal(request.getParameter("postal"));
  demographic.setPhone(request.getParameter("phone"));
  demographic.setPhone2(request.getParameter("phone2"));
  demographic.setEmail(request.getParameter("email"));
  demographic.setMyOscarUserName(StringUtils.trimToNull(request.getParameter("myOscarUserName")));
  demographic.setYearOfBirth(request.getParameter("year_of_birth"));
  demographic.setMonthOfBirth(request.getParameter("month_of_birth")!=null && request.getParameter("month_of_birth").length()==1 ? "0"+request.getParameter("month_of_birth") : request.getParameter("month_of_birth"));
  demographic.setDateOfBirth(request.getParameter("date_of_birth")!=null && request.getParameter("date_of_birth").length()==1 ? "0"+request.getParameter("date_of_birth") : request.getParameter("date_of_birth"));
  demographic.setHin(request.getParameter("hin"));
  demographic.setVer(request.getParameter("ver"));
  demographic.setRosterStatus(request.getParameter("roster_status"));
  demographic.setPatientStatus(request.getParameter("patient_status"));
  demographic.setChartNo(request.getParameter("chart_no"));
  demographic.setProviderNo(request.getParameter("provider_no"));
  demographic.setSex(request.getParameter("sex"));
  demographic.setPcnIndicator(request.getParameter("pcn_indicator"));
  demographic.setHcType(request.getParameter("hc_type"));
  demographic.setFamilyDoctor("<rdohip>" + request.getParameter("r_doctor_ohip") + "</rdohip><rd>" + request.getParameter("r_doctor") + "</rd>" + (request.getParameter("family_doc")!=null? ("<family_doc>" + request.getParameter("family_doc") + "</family_doc>") : ""));
  demographic.setFamilyDoctor2("<fd>" + request.getParameter("family_doctor") + "</fd>" + (request.getParameter("family_doctor_name")!=null? ("<fdname>" + request.getParameter("family_doctor_name") + "</fdname>") : ""));
  demographic.setCountryOfOrigin(request.getParameter("countryOfOrigin"));
  demographic.setNewsletter(request.getParameter("newsletter"));
  demographic.setSin(request.getParameter("sin"));
  demographic.setTitle(request.getParameter("title"));
  demographic.setOfficialLanguage(request.getParameter("official_lang"));
  demographic.setSpokenLanguage(request.getParameter("spoken_lang"));
  demographic.setRosterTerminationReason(request.getParameter("roster_termination_reason"));
  demographic.setLastUpdateUser((String)session.getAttribute("user"));
  demographic.setLastUpdateDate(new java.util.Date());
  demographic.setScannedChart((request.getParameter("scanned_chart")!= null && request.getParameter("scanned_chart").equals("scanned"))?"1":"0");

  // Allow null value
  String customDemographicStatusVal = StringUtils.trimToNull(request.getParameter("custom_demographic_status"));
  if( customDemographicStatusVal == null || StringUtils.isNumeric(customDemographicStatusVal))
  {
	  demographic.setCustomStatusId(NumberUtils.createInteger(customDemographicStatusVal));
  }
  else 
  {
      MiscUtils.getLogger().error("Invalid value for custom status ID: " + customDemographicStatusVal);
  }

  // Patient parental name OHSUPPORT-3228
  if(oscarVariables.isPropertyActive("demographic_parent_names")) {
		demographic.setNameOfMother(request.getParameter("nameOfMother"));
		demographic.setNameOfFather(request.getParameter("nameOfFather"));
  }
	// Patient veteran number OHSUPPORT-3523
	if(oscarVariables.isPropertyActive("demographic_veteran_no")) {
		demographic.setVeteranNo(request.getParameter("veteranNo"));
	}

  //if action is good, then give me the result
    String[] param =new String[31];

		java.sql.Date [] dtparam = new java.sql.Date[7];

		String yearTmp=StringUtils.trimToNull(request.getParameter("date_joined_year"));
		String monthTmp=StringUtils.trimToNull(request.getParameter("date_joined_month"));
		String dayTmp=StringUtils.trimToNull(request.getParameter("date_joined_date"));
		if( yearTmp != null && monthTmp!=null && dayTmp!=null )
		{
			demographic.setDateJoined(MyDateFormat.getSysDate(yearTmp+'-'+monthTmp+'-'+dayTmp));
		}
		else
		{
			demographic.setDateJoined(null);
		}

		yearTmp=StringUtils.trimToNull(request.getParameter("end_date_year"));
		monthTmp=StringUtils.trimToNull(request.getParameter("end_date_month"));
		dayTmp=StringUtils.trimToNull(request.getParameter("end_date_date"));
		if( yearTmp != null && monthTmp!=null && dayTmp!=null )
		{
			demographic.setEndDate(MyDateFormat.getSysDate(yearTmp+'-'+monthTmp+'-'+dayTmp));
		}
		else
		{
			demographic.setEndDate(null);
		}

		yearTmp=StringUtils.trimToNull(request.getParameter("eff_date_year"));
		monthTmp=StringUtils.trimToNull(request.getParameter("eff_date_month"));
		dayTmp=StringUtils.trimToNull(request.getParameter("eff_date_date"));
		if( yearTmp != null && monthTmp!=null && dayTmp!=null )
		{
			demographic.setEffDate(MyDateFormat.getSysDate(yearTmp+'-'+monthTmp+'-'+dayTmp));
		}
		else
		{
			demographic.setEffDate(null);
		}

		yearTmp=StringUtils.trimToNull(request.getParameter("hc_renew_date_year"));
		monthTmp=StringUtils.trimToNull(request.getParameter("hc_renew_date_month"));
		dayTmp=StringUtils.trimToNull(request.getParameter("hc_renew_date_date"));
		if( yearTmp != null && monthTmp!=null && dayTmp!=null )
		{
			demographic.setHcRenewDate(MyDateFormat.getSysDate(yearTmp+'-'+monthTmp+'-'+dayTmp));
		}
		else
		{
			demographic.setHcRenewDate(null);
		}

                yearTmp=StringUtils.trimToNull(request.getParameter("roster_date_year"));
		monthTmp=StringUtils.trimToNull(request.getParameter("roster_date_month"));
		dayTmp=StringUtils.trimToNull(request.getParameter("roster_date_day"));

		if( yearTmp != null && monthTmp!=null && dayTmp!=null )
		{
			demographic.setRosterDate(MyDateFormat.getSysDate(yearTmp+'-'+monthTmp+'-'+dayTmp));
		}
		else
		{
			demographic.setRosterDate(null);
		}
		yearTmp=StringUtils.trimToNull(request.getParameter("roster_termination_date_year"));
		monthTmp=StringUtils.trimToNull(request.getParameter("roster_termination_date_month"));
		dayTmp=StringUtils.trimToNull(request.getParameter("roster_termination_date_day"));

		if( yearTmp != null && monthTmp!=null && dayTmp!=null )
		{
			demographic.setRosterTerminationDate(MyDateFormat.getSysDate(yearTmp+'-'+monthTmp+'-'+dayTmp));
		}
		else
		{
			demographic.setRosterTerminationDate(null);
		}

                yearTmp=StringUtils.trimToNull(request.getParameter("patientstatus_date_year"));
		monthTmp=StringUtils.trimToNull(request.getParameter("patientstatus_date_month"));
		dayTmp=StringUtils.trimToNull(request.getParameter("patientstatus_date_day"));

		if( yearTmp != null && monthTmp!=null && dayTmp!=null )
		{
			demographic.setPatientStatusDate(MyDateFormat.getSysDate(yearTmp+'-'+monthTmp+'-'+dayTmp));
		}
		else
		{
			demographic.setPatientStatusDate(null);
		}


	  int []intparam=new int[] {Integer.parseInt(request.getParameter("demographic_no"))};

  //DemographicExt
     String proNo = (String) session.getValue("user");
	 String demoNo = request.getParameter("demographic_no");
	 demographicExtDao.addKey(proNo, demoNo, "demo_cell", request.getParameter("demo_cell"), request.getParameter("demo_cellOrig"));
     demographicExtDao.addKey(proNo, demoNo, "hPhoneExt", request.getParameter("hPhoneExt"), request.getParameter("hPhoneExtOrig"));
     demographicExtDao.addKey(proNo, demoNo, "wPhoneExt", request.getParameter("wPhoneExt"), request.getParameter("wPhoneExtOrig"));
     demographicExtDao.addKey(proNo, demoNo, "cytolNum",  request.getParameter("cytolNum"),  request.getParameter("cytolNumOrig"));

     demographicExtDao.addKey(proNo, demoNo, "ethnicity",  request.getParameter("ethnicity"),  request.getParameter("ethnicityOrig"));
     demographicExtDao.addKey(proNo, demoNo, "area",		  request.getParameter("area"),		  request.getParameter("areaOrig"));
     demographicExtDao.addKey(proNo, demoNo, "statusNum",  request.getParameter("statusNum"),  request.getParameter("statusNumOrig"));
     demographicExtDao.addKey(proNo, demoNo, "fNationCom", request.getParameter("fNationCom"), request.getParameter("fNationComOrig"));

     demographicExtDao.addKey(proNo, demoNo, "given_consent", request.getParameter("given_consent"), request.getParameter("given_consentOrig"));
     demographicExtDao.addKey(proNo, demoNo, "rxInteractionWarningLevel", request.getParameter("rxInteractionWarningLevel"), request.getParameter("rxInteractionWarningLevelOrig"));

     demographicExtDao.addKey(proNo, demoNo, "primaryEMR", request.getParameter("primaryEMR"), request.getParameter("primaryEMROrig"));

     // for the IBD clinic
	 OtherIdManager.saveIdDemographic(demoNo, "meditech_id", request.getParameter("meditech_id"));

     // customized key
     if(oscarVariables.getProperty("demographicExt") != null) {
	       String [] propDemoExt = oscarVariables.getProperty("demographicExt","").split("\\|");
	       for(int k=0; k<propDemoExt.length; k++) {
	    	   demographicExtDao.addKey(proNo,request.getParameter("demographic_no"),propDemoExt[k].replace(' ','_'),request.getParameter(propDemoExt[k].replace(' ','_')),request.getParameter(propDemoExt[k].replace(' ','_')+"Orig"));
	       }
     }
     // customized key
  //DemographicExt



     // added check to see if patient has a bc health card and has a version code of 66, in this case you are aloud to have dup hin
     boolean hinDupCheckException = false;
     String hcType = request.getParameter("hc_type");
     String ver  = request.getParameter("ver");
     if (hcType != null && ver != null && hcType.equals("BC") && ver.equals("66")){
        hinDupCheckException = true;
     }

     if(request.getParameter("hin")!=null && request.getParameter("hin").length()>5 && !hinDupCheckException) {
            //oscar.oscarBilling.ca.on.data.BillingONDataHelp dbObj = new oscar.oscarBilling.ca.on.data.BillingONDataHelp();
            //String sql = "select demographic_no from demographic where hin=? and year_of_birth=? and month_of_birth=? and date_of_birth=?";
            String paramNameHin =new String();
            paramNameHin=request.getParameter("hin").trim();
        ResultSet rsHin = apptMainBean.queryResults(paramNameHin, "search_hin");
        while (rsHin.next()) {

            if (!(rsHin.getString("demographic_no").equals(request.getParameter("demographic_no")))) {
                if (rsHin.getString("ver") != null && !rsHin.getString("ver").equals("66")){%>
***<font color='red'><bean:message
	key="demographic.demographicaddarecord.msgDuplicatedHIN" /></font>***<br>
<br>
<a href=# onClick="history.go(-1);return false;"><b>&lt;-<bean:message
	key="global.btnBack" /></b></a> <% return;
                }
            }
        }
    }

    demographicArchiveDao.archiveRecord(demographic);
    //DemographicArchive da = new DemographicArchive();
	//da.setDemographicNo(Integer.parseInt(request.getParameter("demographic_no")));
    //demographicArchiveDao.persist(da);

    demographicDao.save(demographic);

    // save custom licensed producer if enabled
    if(oscarVariables.isPropertyActive("show_demographic_licensed_producers")) {
	    try {
		    int licensedProducerID = Integer.parseInt(request.getParameter("licensed_producer"));
		    int licensedProducerID2 = Integer.parseInt(request.getParameter("licensed_producer2"));
		    int licensedProducerAddressID = Integer.parseInt(request.getParameter("licensed_producer_address"));
	    	demographicDao.saveDemographicLicensedProducer(demographic.getDemographicNo(), licensedProducerID, licensedProducerID2, licensedProducerAddressID);
	    }
	    catch(NumberFormatException e) {
	    	// unable to save licensed producer info
	    }
    }

    int rowsAffected=1;
  if (rowsAffected ==1) {
    //find the democust record for update
    try{
    	oscar.oscarDemographic.data.DemographicNameAgeString.resetDemographic(request.getParameter("demographic_no"));
    }catch(Exception nameAgeEx){
    	MiscUtils.getLogger().error("ERROR RESETTING NAME AGE", nameAgeEx);
    }
    DemographicCust demographicCust = demographicCustDao.find(Integer.parseInt(request.getParameter("demographic_no")));
    if(demographicCust != null) {
    	demographicCust.setResident(request.getParameter("resident"));
    	demographicCust.setNurse(request.getParameter("nurse"));
    	demographicCust.setAlert(request.getParameter("alert"));
    	demographicCust.setMidwife(request.getParameter("midwife"));
    	demographicCust.setNotes("<unotes>"+ request.getParameter("notes")+"</unotes>");
    	demographicCustDao.merge(demographicCust);
    	rowsAffected=1;
    } else {
    	demographicCust = new DemographicCust();
    	demographicCust.setResident(request.getParameter("resident"));
    	demographicCust.setNurse(request.getParameter("nurse"));
    	demographicCust.setAlert(request.getParameter("alert"));
    	demographicCust.setMidwife(request.getParameter("midwife"));
    	demographicCust.setNotes("<unotes>"+ request.getParameter("notes")+"</unotes>");
    	demographicCust.setId(Integer.parseInt(request.getParameter("demographic_no")));
    	demographicCustDao.persist(demographicCust);
    	rowsAffected=1;
    }

    //add to waiting list if the waiting_list parameter in the property file is set to true

    WaitingList wL = WaitingList.getInstance();
    if(wL.getFound()){
        //Use  WLWaitingListUtil.updateWaitingListRecord()  instead of the original approach

 	  WLWaitingListUtil.updateWaitingListRecord(
 	  request.getParameter("list_id"), request.getParameter("waiting_list_note"),
 	  request.getParameter("demographic_no"), request.getParameter("waiting_list_referral_date"));

        %>
<form name="add2WLFrm" action="../oscarWaitingList/Add2WaitingList.jsp">
<input type="hidden" name="listId"
	value="<%=request.getParameter("list_id")%>" /> <input type="hidden"
	name="demographicNo"
	value="<%=request.getParameter("demographic_no")%>" /> <input
	type="hidden" name="demographic_no"
	value="<%=request.getParameter("demographic_no")%>" /> <input
	type="hidden" name="waitingListNote"
	value="<%=request.getParameter("waiting_list_note")%>" /> <input
	type="hidden" name="onListSince"
	value="<%=request.getParameter("waiting_list_referral_date")%>" /> <input
	type="hidden" name="displaymode" value="edit" /> <input type="hidden"
	name="dboperation" value="search_detail" /> <%
        if(!request.getParameter("list_id").equalsIgnoreCase("0")){
            String[] paramWLChk = new String[2];
            paramWLChk[0] = request.getParameter("demographic_no");
            paramWLChk[1] = request.getParameter("list_id");
            //check if patient has already added to the waiting list and check if the patient already has an appointment in the future
            rs = apptMainBean.queryResults(paramWLChk, "search_demo_waiting_list");

            if(!rs.next()){
                ResultSet rsAppt = apptMainBean.queryResults(paramWLChk[0], "search_future_appt");
                if(rsAppt.next()){
            %> <script language="JavaScript">
                    var add2List = confirm("The patient already has an appointment, do you still want to add him/her to the waiting list?");
                    if(add2List){
                        document.add2WLFrm.action = "../oscarWaitingList/Add2WaitingList.jsp?demographicNo=<%=request.getParameter("demographic_no")%>&listId=<%=request.getParameter("list_id")%>&waitingListNote=<%=request.getParameter("waiting_list_note")==null?"":request.getParameter("waiting_list_note")%>&onListSince=<%=request.getParameter("waiting_list_referral_date")==null?"":request.getParameter("waiting_list_referral_date")%>";
                    }
                    else{
                        document.add2WLFrm.action ="demographiccontrol.jsp?demographic_no=<%=request.getParameter("demographic_no")%>&displaymode=edit&dboperation=search_detail";
                    }
                    document.add2WLFrm.submit();
                    </script> <%}
                else{%> <script language="JavaScript">
                   document.add2WLFrm.action = "../oscarWaitingList/Add2WaitingList.jsp?demographicNo=<%=request.getParameter("demographic_no")%>&listId=<%=request.getParameter("list_id")%>&waitingListNote=<%=request.getParameter("waiting_list_note")==null?"":request.getParameter("waiting_list_note")%>&onListSince=<%=request.getParameter("waiting_list_referral_date")==null?"":request.getParameter("waiting_list_referral_date")%>";
                    document.add2WLFrm.submit();
                </script> <%}
            }
            else{
                response.sendRedirect("demographiccontrol.jsp?demographic_no=" + request.getParameter("demographic_no") + "&displaymode=edit&dboperation=search_detail");
            }
        }
        else{
            response.sendRedirect("demographiccontrol.jsp?demographic_no=" + request.getParameter("demographic_no") + "&displaymode=edit&dboperation=search_detail");
        }
        %>
</form>
<%
    }
    else{
        response.sendRedirect("demographiccontrol.jsp?demographic_no=" + request.getParameter("demographic_no") + "&displaymode=edit&dboperation=search_detail");
    }
%>
<h2>Update a Provider Record Successfully !
<p><a
	href="demographiccontrol.jsp?demographic_no=<%=request.getParameter("demographic_no")%>&displaymode=edit&dboperation=search_detail"><%= request.getParameter("demographic_no") %></a></p>
</h2>
<%--
<script LANGUAGE="JavaScript">
     	self.opener.refresh();
      //self.close();
</script>
--%> <%
    //response.sendRedirect("demographiccontrol.jsp?demographic_no=" + request.getParameter("demographic_no") + "&displaymode=edit&dboperation=search_detail");
    //response.sendRedirect("search.jsp");
    String ip = request.getRemoteAddr();
    String user = (String)session.getAttribute("user");
    LogAction.addLog((String) session.getAttribute("user"), LogConst.UPDATE, LogConst.CON_DEMOGRAPHIC,  request.getParameter("demographic_no") , request.getRemoteAddr(),request.getParameter("demographic_no"));

  } else {
%>
<h1>Sorry, fail to update !!! <%= request.getParameter("demographic_no") %>.</h1>
<%
  }
%>
<p></p>

</center>
</body>
</html:html>
