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
	import="java.sql.*, java.util.*, java.net.URLEncoder, oscar.oscarDB.*, oscar.MyDateFormat, oscar.oscarWaitingList.WaitingList, org.oscarehr.common.OtherIdManager"
	errorPage="errorpage.jsp"%>
<%@ page import="oscar.log.*"%>
<%@ page import="org.oscarehr.common.model.DemographicExt" %>
<%@ page import="org.oscarehr.common.dao.DemographicExtDao" %>
<%@ page import="org.oscarehr.util.SpringUtils" %>
<%@ page import="org.oscarehr.PMmodule.model.Admission" %>
<%@ page import="org.oscarehr.PMmodule.dao.AdmissionDao" %>
<%@ page import="org.oscarehr.common.dao.WaitingListDao" %>
<jsp:useBean id="apptMainBean" class="oscar.AppointmentMainBean" scope="session" />
<% java.util.Properties oscarVariables = oscar.OscarProperties.getInstance(); %>
<%
	AdmissionDao admissionDao = (AdmissionDao)SpringUtils.getBean("admissionDao");
	WaitingListDao waitingListDao = (WaitingListDao)SpringUtils.getBean("waitingListDao");
	DemographicExtDao demographicExtDao = SpringUtils.getBean(DemographicExtDao.class);
%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.oscarehr.util.SpringUtils" %>
<%@page import="org.oscarehr.common.model.Demographic" %>
<%@page import="org.oscarehr.common.dao.DemographicDao" %>
<%@page import="org.oscarehr.common.model.DemographicCust" %>
<%@page import="org.oscarehr.common.dao.DemographicCustDao" %>
<%
	DemographicDao demographicDao = (DemographicDao)SpringUtils.getBean("demographicDao");
	DemographicCustDao demographicCustDao = (DemographicCustDao)SpringUtils.getBean("demographicCustDao");
%>
<html:html locale="true">
<head>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
<link rel="stylesheet" href="../web.css" />
<script LANGUAGE="JavaScript">
    <!--
    function start(){
      this.focus();
      this.resizeTo(1000,700);
    }
    function closeit() {
      //parent.refresh();
      close();
    }
    //-->
</script>
</head>
<body onload="start()" bgproperties="fixed" topmargin="0" leftmargin="0"
	rightmargin="0">
<center>
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr bgcolor="#486ebd">
		<th align="CENTER"><font face="Helvetica" color="#FFFFFF">
		<bean:message key="demographic.demographicaddarecord.title" /></font></th>
	</tr>
</table>
<form method="post" name="addappt">
<%
        //If this is from adding appointment screen, then back to there
        String fromAppt = request.getParameter("fromAppt");
        String originalPage2 = request.getParameter("originalPage");
        String provider_no2 = request.getParameter("provider_no");
        String bFirstDisp2 = request.getParameter("bFirstDisp");
        String year2 = request.getParameter("year");
        String month2 = request.getParameter("month");
        String day2 = request.getParameter("day");
        String start_time2 = request.getParameter("start_time");
        String end_time2 = request.getParameter("end_time");
        String duration2 = request.getParameter("duration");
%>

<%
    String dem = null;

    String curUser_no = (String)session.getAttribute("user");

  //if action is good, then give me the result
	  //param[0]=Integer.parseIntdemographicaddarecord((new GregorianCalendar()).get(Calendar.MILLISECOND) ); //int
	  //temp variables for test/set null dates
	  DBPreparedHandlerParam [] param =new DBPreparedHandlerParam[34];

	  String year, month, day;

	  Demographic demographic = new Demographic();
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
	  demographic.setDateJoined(MyDateFormat.getSysDate(request.getParameter("date_joined_year")+"-"+request.getParameter("date_joined_month")+"-"+request.getParameter("date_joined_date")));
	  demographic.setChartNo(request.getParameter("chart_no"));
	  demographic.setProviderNo(request.getParameter("staff"));
	  demographic.setSex(request.getParameter("sex"));

	  year = StringUtils.trimToNull(request.getParameter("end_date_year"));
	  month = StringUtils.trimToNull(request.getParameter("end_date_month"));
	  day = StringUtils.trimToNull(request.getParameter("end_date_date"));
		if (year!=null && month!=null && day!=null) {
	  		demographic.setEndDate(MyDateFormat.getSysDate(year + "-" + month + "-" + day));
		} else {
			demographic.setEndDate(null);
		}

	    year = StringUtils.trimToNull(request.getParameter("eff_date_year"));
	    month = StringUtils.trimToNull(request.getParameter("eff_date_month"));
	    day = StringUtils.trimToNull(request.getParameter("eff_date_date"));
		if (year!=null && month!=null && day!=null) {
			demographic.setEffDate(MyDateFormat.getSysDate(year + "-" + month + "-" + day));
		} else {
			demographic.setEffDate(null);
		}
		
	    year = StringUtils.trimToNull(request.getParameter("hc_renew_date_year"));
	    month = StringUtils.trimToNull(request.getParameter("hc_renew_date_month"));
	    day = StringUtils.trimToNull(request.getParameter("hc_renew_date_date"));
		if (year!=null && month!=null && day!=null) {
			demographic.setHcRenewDate(MyDateFormat.getSysDate(year + "-" + month + "-" + day));
		} else {
			demographic.setHcRenewDate(null);
		}

		demographic.setPcnIndicator(request.getParameter("pcn_indicator"));
		demographic.setHcType(request.getParameter("hc_type"));

	    year = StringUtils.trimToNull(request.getParameter("roster_date_year"));
	    month = StringUtils.trimToNull(request.getParameter("roster_date_month"));
	    day = StringUtils.trimToNull(request.getParameter("roster_date_date"));
		if (year!=null && month!=null && day!=null) {
			demographic.setRosterDate(MyDateFormat.getSysDate( year + "-" + month + "-" + day));
		} else {
			demographic.setRosterDate(null);
		}
		demographic.setFamilyDoctor("<rdohip>" + request.getParameter("r_doctor_ohip") + "</rdohip>" + "<rd>" + request.getParameter("r_doctor") + "</rd>"+ (request.getParameter("family_doc")!=null? ("<family_doc>" + request.getParameter("family_doc") + "</family_doc>") : ""));
  		demographic.setFamilyDoctor2("<fd>" + request.getParameter("family_doctor") + "</fd>" + (request.getParameter("family_doctor_name")!=null? ("<fdname>" + request.getParameter("family_doctor_name") + "</fdname>") : ""));
		demographic.setCountryOfOrigin(request.getParameter("countryOfOrigin"));
		demographic.setNewsletter(request.getParameter("newsletter"));
		demographic.setSin(request.getParameter("sin"));
		demographic.setTitle(request.getParameter("title"));
		demographic.setOfficialLanguage(request.getParameter("official_lang"));
		demographic.setSpokenLanguage(request.getParameter("spoken_lang"));
		demographic.setLastUpdateUser(curUser_no);
		demographic.setLastUpdateDate(new java.util.Date());
		demographic.setPatientStatusDate(new java.util.Date());
		demographic.setScannedChart((request.getParameter("scanned_chart")!= null && request.getParameter("scanned_chart").equals("scanned"))?"1":"0");

		List<Demographic> duplicateList = demographicDao.getDemographicWithLastFirstDOBExact(demographic.getLastName(),demographic.getFirstName(),
				demographic.getYearOfBirth(),demographic.getMonthOfBirth(),demographic.getDateOfBirth());

		if(duplicateList.size()>0) {
%>
   ***<font color='red'><bean:message key="demographic.demographicaddarecord.msgDuplicatedRecord" /></font>***<br>
	<br>
   <a href=# onClick="history.go(-1);return false;"><b>&lt;-<bean:message key="global.btnBack" /></b></a>
   <% return; }
	
	StringBuilder bufChart = null, bufName = null, bufNo = null, bufDoctorNo = null;
    // add checking hin duplicated record, if there is a HIN number
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
		List<Demographic> demographics = demographicDao.searchByHealthCard(paramNameHin);
		if(demographics.size()>0){ %>
	   ***<font color='red'><bean:message key="demographic.demographicaddarecord.msgDuplicatedHIN" /></font>***<br>
<br>
<a href=# onClick="history.go(-1);return false;"><b>&lt;-<bean:message key="global.btnBack" /></b></a>
	<% return; }  }
    
    bufName = new StringBuilder(request.getParameter("last_name")+ ","+ request.getParameter("first_name") );
    bufNo = new StringBuilder( (apptMainBean.getString("demographic_no")) );
    bufChart = new StringBuilder(apptMainBean.getString("chart_no"));
    bufDoctorNo = new StringBuilder( apptMainBean.getString("provider_no") );

    demographicDao.save(demographic);
    
    // save custom licensed producer if enabled
    if(Boolean.parseBoolean(oscarVariables.getProperty("show_demographic_licensed_producers"))) {
	    try {
		    int licensedProducerID = Integer.parseInt(request.getParameter("licensed_producer"));
		    int licensedProducerAddressID = Integer.parseInt(request.getParameter("licensed_producer_address"));
	    	demographicDao.saveDemographicLicensedProducer(demographic.getDemographicNo(), licensedProducerID, licensedProducerAddressID);
	    }
	    catch(NumberFormatException e) {
	    	// unable to save licensed producer info
	    }
    }


	String [][] dbQueries=new String[][] {
		{"search_waitingListPosition", "select max(position) as position from waitingList where listID=? AND is_history='N' "},
		{"search_program", "select id from program where name = ?"}
	};
	apptMainBean.doConfigure( dbQueries );

         //propagate demographic to caisi admission table
        
            //fetch programId associated with provider
            //if none(0) then check for OSCAR program; if available set it as default
            oscar.oscarEncounter.data.EctProgram program = new oscar.oscarEncounter.data.EctProgram(request.getSession());
            String progId = program.getProgram(request.getParameter("staff"));
            if( progId.equals("0") ) {
                ResultSet rsProg = apptMainBean.queryResults("OSCAR", "search_program");
                if( rsProg.next() )
                    progId = rsProg.getString("id");
            }
            String admissionDate=null;

    		String yearTmp = StringUtils.trimToNull(request.getParameter("date_joined_year"));
    		String monthTmp = StringUtils.trimToNull(request.getParameter("date_joined_month"));
    		String dayTmp = StringUtils.trimToNull(request.getParameter("date_joined_date"));
    		if (yearTmp!=null && monthTmp!=null && dayTmp!=null) {
    			admissionDate = yearTmp+"-"+monthTmp+"-"+dayTmp;
    		} else {
    			GregorianCalendar cal=new GregorianCalendar();
    			admissionDate=""+cal.get(GregorianCalendar.YEAR)+'-'+(cal.get(GregorianCalendar.MONTH)+1)+'-'+cal.get(GregorianCalendar.DAY_OF_MONTH);
    		}

    		Admission admission = new Admission();
    		admission.setClientId(demographic.getDemographicNo());
    		admission.setProgramId(Integer.parseInt(progId));
    		admission.setProviderNo(request.getParameter("staff"));
    		admission.setAdmissionDate(MyDateFormat.getSysDate(admissionDate));
    		admission.setAdmissionStatus("current");
    		admission.setTeamId(0);
    		admission.setTemporaryAdmission(false);
    		admission.setAdmissionFromTransfer(false);
    		admission.setDischargeFromTransfer(false);
    		admission.setRadioDischargeReason("0");
    		admission.setClientStatusId(0);
            admissionDao.saveAdmission(admission);
        

        //add democust record for alert
        String[] param2 =new String[6];
	    param2[0]=demographic.getDemographicNo().toString();

        DemographicCust demographicCust = new DemographicCust();
       	demographicCust.setResident(request.getParameter("cust2"));
    	demographicCust.setNurse(request.getParameter("cust1"));
    	demographicCust.setAlert(request.getParameter("cust3"));
    	demographicCust.setMidwife(request.getParameter("cust4"));
    	demographicCust.setNotes("<unotes>"+ request.getParameter("content")+"</unotes>");
    	demographicCust.setId(demographic.getDemographicNo());
    	demographicCustDao.persist(demographicCust);
        int rowsAffected=1;

       dem = demographic.getDemographicNo().toString();

       String proNo = (String) session.getValue("user");
       demographicExtDao.addKey(proNo, dem, "hPhoneExt", request.getParameter("hPhoneExt"), "");
       demographicExtDao.addKey(proNo, dem, "wPhoneExt", request.getParameter("wPhoneExt"), "");
       demographicExtDao.addKey(proNo, dem, "demo_cell", request.getParameter("cellphone"), "");
       demographicExtDao.addKey(proNo, dem, "cytolNum",  request.getParameter("cytolNum"),  "");

       demographicExtDao.addKey(proNo, dem, "ethnicity",     request.getParameter("ethnicity"),     "");
       demographicExtDao.addKey(proNo, dem, "area",          request.getParameter("area"),          "");
       demographicExtDao.addKey(proNo, dem, "statusNum",     request.getParameter("statusNum"),     "");
       demographicExtDao.addKey(proNo, dem, "fNationCom",    request.getParameter("fNationCom"),    "");
       demographicExtDao.addKey(proNo, dem, "given_consent", request.getParameter("given_consent"), "");

       demographicExtDao.addKey(proNo, dem, "rxInteractionWarningLevel", request.getParameter("rxInteractionWarningLevel"), "");

       demographicExtDao.addKey(proNo, dem, "primaryEMR", request.getParameter("primaryEMR"), "");


       //for the IBD clinic
		OtherIdManager.saveIdDemographic(dem, "meditech_id", request.getParameter("meditech_id"));

       // customized key
       if(oscarVariables.getProperty("demographicExt") != null) {
	       String [] propDemoExt = oscarVariables.getProperty("demographicExt","").split("\\|");
	       for(int k=0; k<propDemoExt.length; k++) {
	    	   demographicExtDao.addKey(proNo,dem,propDemoExt[k],request.getParameter(propDemoExt[k].replace(' ','_')),"");
	       }
       }
       // customized key

		// add log
		String ip = request.getRemoteAddr();
		LogAction.addLog(curUser_no, "add", "demographic", param2[0], ip,param2[0]);

        //add to waiting list if the waiting_list parameter in the property file is set to true

        WaitingList wL = WaitingList.getInstance();
        if(wL.getFound()){

            String[] paramWLPosition = new String[1];
            paramWLPosition[0] = request.getParameter("list_id");
            if(!StringUtils.isEmpty(paramWLPosition[0])){
                ResultSet rsWL = apptMainBean.queryResults(paramWLPosition, "search_waitingListPosition");

                if(rsWL.next()){

                    String listId = request.getParameter("list_id");
                    if(listId != null && !listId.equals("") && !listId.equals("0")) {
	                    org.oscarehr.common.model.WaitingList waitingList = new org.oscarehr.common.model.WaitingList();
	                    waitingList.setListId(Integer.parseInt(request.getParameter("list_id")));
	                    waitingList.setDemographicNo(demographic.getDemographicNo());
	                    waitingList.setNote(request.getParameter("waiting_list_note"));
	                    waitingList.setPosition(rsWL.getInt("position")+1);
	                    waitingList.setOnListSince(MyDateFormat.getSysDate(request.getParameter("waiting_list_referral_date")));
	                    waitingList.setIsHistory("N");
	                    waitingList.setOnListSince(new java.util.Date());
	                    waitingListDao.persist(waitingList);
                    }
                }
            }


        } //end of waitingl list

        //if(request.getParameter("fromAppt")!=null && request.getParameter("provider_no").equals("1")) {
        if(start_time2!=null && !start_time2.equals("null")) {
	%>
	<script language="JavaScript">
	<!--
	document.addappt.action="../appointment/addappointment.jsp?user_id=<%=request.getParameter("creator")%>&provider_no=<%=provider_no2%>&bFirstDisp=<%=bFirstDisp2%>&appointment_date=<%=request.getParameter("appointment_date")%>&year=<%=year2%>&month=<%=month2%>&day=<%=day2%>&start_time=<%=start_time2%>&end_time=<%=end_time2%>&duration=<%=duration2%>&name=<%=URLEncoder.encode(bufName.toString())%>&chart_no=<%=URLEncoder.encode(bufChart.toString())%>&bFirstDisp=false&demographic_no=<%=dem.toString()%>&messageID=<%=request.getParameter("messageId")%>&doctor_no=<%=bufDoctorNo.toString()%>&notes=<%=request.getParameter("notes")%>&reason=<%=request.getParameter("reason")%>&location=<%=request.getParameter("location")%>&resources=<%=request.getParameter("resources")%>&type=<%=request.getParameter("type")%>&style=<%=request.getParameter("style")%>&billing=<%=request.getParameter("billing")%>&status=<%=request.getParameter("status")%>&createdatetime=<%=request.getParameter("createdatetime")%>&creator=<%=request.getParameter("creator")%>&remarks=<%=request.getParameter("remarks")%>";
	document.addappt.submit();
	//-->
	</SCRIPT> 
	<% } %>
</form>



<p>
<h2><bean:message key="demographic.demographicaddarecord.msgSuccessful" /></h2>
    <a href="demographiccontrol.jsp?demographic_no=<%=dem%>&displaymode=edit&dboperation=search_detail"><bean:message key="demographic.demographicaddarecord.goToRecord"/></a>

<p></p>
<%@ include file="footer.jsp"%></center>
</body>
</html:html>
