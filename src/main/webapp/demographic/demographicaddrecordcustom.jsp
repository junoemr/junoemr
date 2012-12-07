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
<%@page import="org.oscarehr.util.SessionConstants"%>
<%
	if(session.getAttribute("user") == null) response.sendRedirect("../logout.jsp");
	String curUser_no = (String) session.getAttribute("user");
	String str = null;
  
	String protocol = "http://";
	if(request.isSecure()){
		protocol = "https://";
	}
%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>

<%@ page
	import="java.util.*, java.sql.*, oscar.*, oscar.oscarDemographic.data.ProvinceNames, oscar.oscarWaitingList.WaitingList"
	errorPage="errorpage.jsp"%>
<%@ page
	import="org.springframework.web.context.*,org.springframework.web.context.support.*,org.oscarehr.common.dao.*,org.oscarehr.common.model.*"%>
<%@page import="org.oscarehr.common.dao.CountryCodeDao" %>
<jsp:useBean id="providerBean" class="java.util.Properties"
	scope="session" />
<jsp:useBean id="addDemoBean" class="oscar.AppointmentMainBean"	scope="page" />
<%@ include file="../admin/dbconnection.jsp"%>
<%
  String [][] dbQueries=new String[][] {
    {"search_provider", "select * from provider where provider_type='doctor' and status='1' order by last_name"},
    {"search_rsstatus", "select distinct roster_status from demographic where roster_status != '' and roster_status != 'RO' and roster_status != 'NR' and roster_status != 'TE' and roster_status != 'FS' "},
    {"search_ptstatus", "select distinct patient_status from demographic where patient_status != '' and patient_status != 'AC' and patient_status != 'IN' and patient_status != 'DE' and patient_status != 'MO' and patient_status != 'FI'"},
    {"search_waiting_list", "select * from waitingListName where group_no='" + ((ProviderPreference)session.getAttribute(SessionConstants.LOGGED_IN_PROVIDER_PREFERENCE)).getMyGroupNo() +"' and is_history='N'  order by name"}
  };
  String[][] responseTargets=new String[][] {  };
  addDemoBean.doConfigure(dbQueries,responseTargets);

  java.util.Locale vLocale =(java.util.Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY);

  OscarProperties props = OscarProperties.getInstance();

  GregorianCalendar now=new GregorianCalendar();
  String curYear = Integer.toString(now.get(Calendar.YEAR));
  String curMonth = Integer.toString(now.get(Calendar.MONTH)+1);
  if (curMonth.length() < 2) curMonth = "0"+curMonth;
  String curDay = Integer.toString(now.get(Calendar.DAY_OF_MONTH));
  if (curDay.length() < 2) curDay = "0"+curDay;

  int nStrShowLen = 20;
  OscarProperties oscarProps = OscarProperties.getInstance();

  ProvinceNames pNames = ProvinceNames.getInstance();
  String prov= ((String ) props.getProperty("billregion","")).trim().toUpperCase();

  String billingCentre = ((String ) props.getProperty("billcenter","")).trim().toUpperCase();
  String defaultCity = prov.equals("ON")&&billingCentre.equals("N") ? "Toronto":"";

  WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
  CountryCodeDao ccDAO =  (CountryCodeDao) ctx.getBean("countryCodeDao");

  List<CountryCode> countryList = ccDAO.getAllCountryCodes();

  // Used to retrieve properties from user (i.e. HC_Type & default_sex)
  UserPropertyDAO userPropertyDAO = (UserPropertyDAO) ctx.getBean("UserPropertyDAO");

  String HCType = "";
  // Determine if curUser has selected a default HC Type
  UserProperty HCTypeProp = userPropertyDAO.getProp(curUser_no,  UserProperty.HC_TYPE);
  if (HCTypeProp != null) {
     HCType = HCTypeProp.getValue();
  } else {
     // If there is no user defined property, then determine if the hctype system property is activated
     HCType = props.getProperty("hctype","");
     if (HCType == null || HCType.equals("")) {
           // The system property is not activated, so use the billregion
           String billregion = props.getProperty("billregion", "");
           HCType = billregion;
     }
  }
  // Use this value as the default value for province, as well
  String defaultProvince = HCType;
  String demographic_string = props.getProperty("cust_demographic_fields");
  if(demographic_string == null){
	  demographic_string = "";
  }
  String all_fields = "last_name,first_name,official_lang,title,address,city,province,postal,phone,phone2,cellphone,newsletter,email,pin,dob,sex,hin,eff_date,hc_type,countryOfOrigin,sin,cytolNum,doctor,nurse,midwife,resident,referral_doc,roster_status,date_rostered,patient_status,chart_no,waiting_list,date_joined,end_date,alert,form_notes";
  List<String> custom_demographic_fields = new ArrayList<String>(Arrays.asList(demographic_string.split(",")));
  List<String> hidden_demographic_fields = new ArrayList<String>(Arrays.asList(all_fields.split(",")));
  if( !custom_demographic_fields.contains("last_name") ){
	  custom_demographic_fields.add(new String("last_name"));
  }
  if( !custom_demographic_fields.contains("first_name") ){
	  custom_demographic_fields.add(new String("first_name"));
  }
  if( !custom_demographic_fields.contains("dob") ){
	  custom_demographic_fields.add(new String("dob"));
  }
  if( !custom_demographic_fields.contains("sex") ){
	  custom_demographic_fields.add(new String("sex"));
  }
  if( !custom_demographic_fields.contains("hin") ){
	  custom_demographic_fields.add(new String("hin"));
  }
  if( !custom_demographic_fields.contains("doctor") ){
	  custom_demographic_fields.add(new String("doctor"));
  }
  if( !custom_demographic_fields.contains("patient_status") ){
	  custom_demographic_fields.add(new String("patient_status"));
  }
  /*
  if( !custom_demographic_fields.contains("hc_type") ){
	  hc_type_hidden = true;
  }
  
  if( !custom_demographic_fields.contains("province") ){
	  custom_demographic_fields.add(new String("province_hidden"));
  }
  if( !custom_demographic_fields.contains("form_notes") ){
	  custom_demographic_fields.add(new String("form_notes_hidden"));
  }
  if( !custom_demographic_fields.contains("referral_doc") ){
	  custom_demographic_fields.add(new String("referral_doc_hidden"));
  }
  if( !custom_demographic_fields.contains("date_joined") ){
	  custom_demographic_fields.add(new String("date_joined_hidden"));
  }
  if( !custom_demographic_fields.contains("nurse") ){
	  custom_demographic_fields.add(new String("nurse_hidden"));
  }
  if( !custom_demographic_fields.contains("midwife") ){
	  custom_demographic_fields.add(new String("midwife_hidden"));
  }
  if( !custom_demographic_fields.contains("alert") ){
	  custom_demographic_fields.add(new String("alert_hidden"));
  }
  if( !custom_demographic_fields.contains("resident") ){
	  custom_demographic_fields.add(new String("resident_hidden"));
  }
  */
  //String[] demographic_fields = custom_demographic_fields.toArray(); 
  
%>

<html:html locale="true">
<head>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
<title><bean:message
	key="demographic.demographicaddrecordhtm.title" /></title>
<meta http-equiv="Expires" content="Monday, 8 Aug 88 18:18:18 GMT">
<!-- calendar stylesheet -->
<link rel="stylesheet" type="text/css" media="all"
	href="../share/calendar/calendar.css" title="win2k-cold-1" />

<!-- main calendar program -->
<script type="text/javascript" src="../share/calendar/calendar.js"></script>

<!-- language for the calendar -->
<script type="text/javascript"
	src="../share/calendar/lang/<bean:message key="global.javascript.calendar"/>"></script>

<!-- the following script defines the Calendar.setup helper function, which makes
       adding a calendar a matter of 1 or 2 lines of code. -->
<script type="text/javascript" src="../share/calendar/calendar-setup.js"></script>

<script type="text/javascript" src="<%=request.getContextPath() %>/js/check_hin.js"></script>

<link rel="stylesheet" type="text/css" href="<%=protocol%>ajax.googleapis.com/ajax/libs/jqueryui/1.8.17/themes/blitzer/jquery-ui.css"/>
<link rel="stylesheet" href="../web.css" />

<!-- Stylesheet for zdemographicfulltitlesearch.jsp -->
<link rel="stylesheet" type="text/css" href="../share/css/searchBox.css" />

<link rel="stylesheet" type="text/css" media="all" href="../share/css/extractedFromPages.css"  />
<style>
	label{ width: 200px; display: inline-block; }
	form div{ width: 600px; }
	form{ width: 680px; margin-left: auto; margin-right: auto; background-color: #CCCCFF; padding: 10px; }
	h2{ text-align: center; }
</style>
<script language="JavaScript">

function setfocus() {
  this.focus();
  document.adddemographic.last_name.focus();
  document.adddemographic.last_name.select();
  window.resizeTo(1000,700);
}
function upCaseCtrl(ctrl) {
	ctrl.value = ctrl.value.toUpperCase();
}
//function showDate(){
//  var now=new Date();
//  var year=now.getYear();
//  var month=now.getMonth()+1;
//  var date=now.getDate();
//  //var DateVal=""+year+"-"+month+"-"+date;
//  document.adddemographic.date_joined_year.value=year;
//  document.adddemographic.date_joined_month.value=month;
//  document.adddemographic.date_joined_date.value=date;
//}

function checkTypeIn() {
  var dob = document.titlesearch.keyword; typeInOK = false;

  if (dob.value.indexOf('%b610054') == 0 && dob.value.length > 18){
     document.titlesearch.keyword.value = dob.value.substring(8,18);
     document.titlesearch.search_mode[4].checked = true;
  }

  if(document.titlesearch.search_mode[2].checked) {
    if(dob.value.length==8) {
      dob.value = dob.value.substring(0, 4)+"-"+dob.value.substring(4, 6)+"-"+dob.value.substring(6, 8);
      //alert(dob.value.length);
      typeInOK = true;
    }
    if(dob.value.length != 10) {
      alert("<bean:message key="demographic.search.msgWrongDOB"/>");
      typeInOK = false;
    }

    return typeInOK ;
  } else {
    return true;
  }
}

function checkTypeInAdd() {
	var typeInOK = false;
	if(document.adddemographic.last_name.value!="" && document.adddemographic.first_name.value!="" && document.adddemographic.sex.value!="") {
      if(checkTypeNum(document.adddemographic.year_of_birth.value) && checkTypeNum(document.adddemographic.month_of_birth.value) && checkTypeNum(document.adddemographic.date_of_birth.value) ){
	    typeInOK = true;
	  }
	}
	if(!typeInOK) alert ("<bean:message key="demographic.demographicaddrecordhtm.msgMissingFields"/>");
	return typeInOK;
}

function newStatus() {
    newOpt = prompt("Please enter the new status:", "");
    if (newOpt != "") {
        document.adddemographic.patient_status.options[document.adddemographic.patient_status.length] = new Option(newOpt, newOpt);
        document.adddemographic.patient_status.options[document.adddemographic.patient_status.length-1].selected = true;
    } else {
        alert("Invalid entry");
    }
}
function newStatus1() {
    newOpt = prompt("Please enter the new status:", "");
    if (newOpt != "") {
        document.adddemographic.roster_status.options[document.adddemographic.roster_status.length] = new Option(newOpt, newOpt);
        document.adddemographic.roster_status.options[document.adddemographic.roster_status.length-1].selected = true;
    } else {
        alert("Invalid entry");
    }
}

function formatPhoneNum() {
    if (document.adddemographic.phone.value.length == 10) {
        document.adddemographic.phone.value = document.adddemographic.phone.value.substring(0,3) + "-" + document.adddemographic.phone.value.substring(3,6) + "-" + document.adddemographic.phone.value.substring(6);
        }
    if (document.adddemographic.phone.value.length == 11 && document.adddemographic.phone.value.charAt(3) == '-') {
        document.adddemographic.phone.value = document.adddemographic.phone.value.substring(0,3) + "-" + document.adddemographic.phone.value.substring(4,7) + "-" + document.adddemographic.phone.value.substring(7);
    }

    if (document.adddemographic.phone2.value.length == 10) {
        document.adddemographic.phone2.value = document.adddemographic.phone2.value.substring(0,3) + "-" + document.adddemographic.phone2.value.substring(3,6) + "-" + document.adddemographic.phone2.value.substring(6);
        }
    if (document.adddemographic.phone2.value.length == 11 && document.adddemographic.phone2.value.charAt(3) == '-') {
        document.adddemographic.phone2.value = document.adddemographic.phone2.value.substring(0,3) + "-" + document.adddemographic.phone2.value.substring(4,7) + "-" + document.adddemographic.phone2.value.substring(7);
    }
}
function rs(n,u,w,h,x) {
  args="width="+w+",height="+h+",resizable=yes,scrollbars=yes,status=0,top=60,left=30";
  remote=window.open(u,n,args);
}
function referralScriptAttach2(elementName, name2) {
     var d = elementName;
     t0 = escape("document.forms[1].elements[\'"+d+"\'].value");
     t1 = escape("document.forms[1].elements[\'"+name2+"\'].value");
     rs('att',('../billing/CA/ON/searchRefDoc.jsp?param='+t0+'&param2='+t1),600,600,1);
}

function checkName() {
	var typeInOK = false;
	if(document.adddemographic.last_name.value!="" && document.adddemographic.first_name.value!="" && document.adddemographic.last_name.value!=" " && document.adddemographic.first_name.value!=" ") {
	    typeInOK = true;
	} else {
		alert ("You must type in the following fields: Last Name, First Name.");
    }
	return typeInOK;
}

function checkDob() {
	var typeInOK = false;
	var yyyy = document.adddemographic.year_of_birth.value;
	var selectBox = document.adddemographic.month_of_birth;
	var mm = selectBox.options[selectBox.selectedIndex].value
	selectBox = document.adddemographic.date_of_birth;
	var dd = selectBox.options[selectBox.selectedIndex].value

	if(checkTypeNum(yyyy) && checkTypeNum(mm) && checkTypeNum(dd) ){
        //alert(yyyy); alert(mm); alert(dd);
        var check_date = new Date(yyyy,(mm-1),dd);
        //alert(check_date);
		var now = new Date();
		var year=now.getFullYear();
		var month=now.getMonth()+1;
		var date=now.getDate();
		//alert(yyyy + " | " + mm + " | " + dd + " " + year + " " + month + " " +date);

		var young = new Date(year,month,date);
		var old = new Date(1800,01,01);
		//alert(check_date.getTime() + " | " + young.getTime() + " | " + old.getTime());
		if (check_date.getTime() <= young.getTime() && check_date.getTime() >= old.getTime() && yyyy.length==4) {
		    typeInOK = true;
		    //alert("failed in here 1");
		}
		if ( yyyy == "0000"){
        typeInOK = false;
      }
	}

	if (!typeInOK){
      alert ("You must type in the right DOB.");
   }

   if (!isValidDate(dd,mm,yyyy)){
      alert ("DOB Date is an incorrect date");
      typeInOK = false;
   }

	return typeInOK;
}


function isValidDate(day,month,year){
   month = ( month - 1 );
   dteDate=new Date(year,month,day);
//alert(dteDate);
   return ((day==dteDate.getDate()) && (month==dteDate.getMonth()) && (year==dteDate.getFullYear()));
}

function checkHin() {
	var hin = document.adddemographic.hin.value;
	var province = document.adddemographic.hc_type.value;

	if (!isValidHin(hin, province))
	{
		alert ("You must type in the right HIN.");
		return(false);
	}

	return(true);
}

function checkAllDate() {
	var typeInOK = false;
	typeInOK = checkDateYMD( document.adddemographic.date_joined_year.value , document.adddemographic.date_joined_month.value , document.adddemographic.date_joined_date.value , "Date Joined" );
	if (!typeInOK) { return false; }

	typeInOK = checkDateYMD( document.adddemographic.end_date_year.value , document.adddemographic.end_date_month.value , document.adddemographic.end_date_date.value , "End Date" );
	if (!typeInOK) { return false; }

	typeInOK = checkDateYMD( document.adddemographic.hc_renew_date_year.value , document.adddemographic.hc_renew_date_month.value , document.adddemographic.hc_renew_date_date.value , "PCN Date" );
	if (!typeInOK) { return false; }

	typeInOK = checkDateYMD( document.adddemographic.eff_date_year.value , document.adddemographic.eff_date_month.value , document.adddemographic.eff_date_date.value , "EFF Date" );
	if (!typeInOK) { return false; }

	return typeInOK;
}
	function checkDateYMD(yy, mm, dd, fieldName) {
		var typeInOK = false;
		if((yy.length==0) && (mm.length==0) && (dd.length==0) ){
			typeInOK = true;
		} else if(checkTypeNum(yy) && checkTypeNum(mm) && checkTypeNum(dd) ){
			if (checkDateYear(yy) && checkDateMonth(mm) && checkDateDate(dd)) {
				typeInOK = true;
			}
		}
		if (!typeInOK) { alert ("You must type in the right '" + fieldName + "'."); return false; }
		return typeInOK;
	}

	function checkDateYear(y) {
		if (y>1900 && y<2045) return true;
		return false;
	}
	function checkDateMonth(y) {
		if (y>=1 && y<=12) return true;
		return false;
	}
	function checkDateDate(y) {
		if (y>=1 && y<=31) return true;
		return false;
	}

function checkFormTypeIn() {
	if ( !checkName() ) return false;
	if ( !checkDob() ) return false;
	if ( !checkHin() ) return false;
	if ( !checkAllDate() ) return false;
	return true;
}

function checkTitleSex(ttl) {
    if (ttl=="MS" || ttl=="MISS" || ttl=="MRS" || ttl=="SR") document.adddemographic.sex.selectedIndex=1;
	else if (ttl=="MR" || ttl=="MSSR") document.adddemographic.sex.selectedIndex=0;
}

function removeAccents(s){
        var r=s.toLowerCase();
        r = r.replace(new RegExp("\\s", 'g'),"");
        r = r.replace(new RegExp("[������]", 'g'),"a");
        r = r.replace(new RegExp("�", 'g'),"c");
        r = r.replace(new RegExp("[����]", 'g'),"e");
        r = r.replace(new RegExp("[����]", 'g'),"i");
        r = r.replace(new RegExp("�", 'g'),"n");
        r = r.replace(new RegExp("[�����]", 'g'),"o");
        r = r.replace(new RegExp("[����]", 'g'),"u");
        r = r.replace(new RegExp("[��]", 'g'),"y");
        r = r.replace(new RegExp("\\W", 'g'),"");
        return r;
}

function autoFillHin(){
   var hcType = document.getElementById('hc_type').value;
   var hin = document.getElementById('hin').value;
   if(	hcType == 'QC' && hin == ''){
   	  var last = document.getElementById('last_name').value;
   	  var first = document.getElementById('first_name').value;
      var yob = document.getElementById('year_of_birth').value;
      var mob = document.getElementById('month_of_birth').value;
      var dob = document.getElementById('date_of_birth').value;

   	  last = removeAccents(last.substring(0,3)).toUpperCase();
   	  first = removeAccents(first.substring(0,1)).toUpperCase();
   	  yob = yob.substring(2,4);
   	  
   	  var sex = document.getElementById('sex').value;
   	  if(sex == 'F'){
   		  mob = parseInt(mob) + 50; 
   	  }

      document.getElementById('hin').value = last + first + yob + mob + dob;
      hin.focus();
      hin.value = hin.value;
   }
}

</script>
</head>
<body bgproperties="fixed" onLoad="setfocus();" topmargin="0"
	leftmargin="0" rightmargin="0">
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr bgcolor="#CCCCFF">
		<th class="subject"><bean:message
			key="demographic.demographicaddrecordhtm.msgMainLabel" /></th>
	</tr>
</table>

<%@ include file="zdemographicfulltitlesearch.jsp"%>
<!--<form method="post" name="adddemographic" action="demographiccontrol.jsp" onsubmit="return checkFormTypeIn()">-->
<form method="post" name="adddemographic" action="demographicaddarecord.jsp" onsubmit="return checkFormTypeIn()">
<%
for(int i=0; i<custom_demographic_fields.size(); i++){
	if(hidden_demographic_fields.indexOf(custom_demographic_fields.get(i)) >= 0){
		hidden_demographic_fields.remove(hidden_demographic_fields.indexOf(custom_demographic_fields.get(i)));
	}
	if(custom_demographic_fields.get(i).equals("last_name"))
	{
		%>
		<div>
			<label><b><bean:message key="demographic.demographicaddrecordhtm.formLastName"/><font color="red">:</font> </b></label>
			<input type="text" name="last_name" onBlur="upCaseCtrl(this)" size=30 />
		</div>
		<%
	}else if(custom_demographic_fields.get(i).equals("first_name"))
	{
		%>
		<div>
			<label><b><bean:message key="demographic.demographicaddrecordhtm.formFirstName"/><font color="red">:</font> </b></label>
			<input type="text" name="first_name" onBlur="upCaseCtrl(this)"  size=30>
		</div>
		<%		
	}else if(custom_demographic_fields.get(i).equals("official_lang")){
		%>
		<div>
			<label><b><bean:message key="demographic.demographicaddrecordhtm.msgDemoLanguage"/> <font color="red">:</font></b></label>
			
			<select name="official_lang">
			<option value="English" <%= vLocale.getLanguage().equals("en") ? " selected":"" %>><bean:message key="demographic.demographiceaddrecordhtm.msgEnglish"/></option>
			<option value="French"  <%= vLocale.getLanguage().equals("fr") ? " selected":"" %>><bean:message key="demographic.demographiceaddrecordhtm.msgFrench"/></option>
		    </select>
		    &nbsp;&nbsp;
		    <b><bean:message key="demographic.demographicaddrecordhtm.msgSpoken"/>:</b>
		    <input name="spoken_lang" size="15" />
		</div>
		<%
	}else if(custom_demographic_fields.get(i).equals("title")){
		%>
		<div>
			<label><b><bean:message key="demographic.demographicaddrecordhtm.msgDemoTitle"/><font color="red">:</font></b></label>
		    <select name="title" onchange="checkTitleSex(value);">
               <option value="" selected><bean:message key="demographic.demographicaddrecordhtm.msgNotSet"/></option>
               <option value="MS"><bean:message key="demographic.demographicaddrecordhtm.msgMs"/></option>
               <option value="MISS"><bean:message key="demographic.demographicaddrecordhtm.msgMiss"/></option>
               <option value="MRS"><bean:message key="demographic.demographicaddrecordhtm.msgMrs"/></option>
               <option value="MR"><bean:message key="demographic.demographicaddrecordhtm.msgMr"/></option>
               <option value="MSSR"><bean:message key="demographic.demographicaddrecordhtm.msgMssr"/></option>
               <option value="PROF"><bean:message key="demographic.demographicaddrecordhtm.msgProf"/></option>
               <option value="REEVE"><bean:message key="demographic.demographicaddrecordhtm.msgReeve"/></option>
               <option value="REV"><bean:message key="demographic.demographicaddrecordhtm.msgRev"/></option>
               <option value="RT_HON"><bean:message key="demographic.demographicaddrecordhtm.msgRtHon"/></option>
               <option value="SEN"><bean:message key="demographic.demographicaddrecordhtm.msgSen"/></option>
               <option value="SGT"><bean:message key="demographic.demographicaddrecordhtm.msgSgt"/></option>
               <option value="SR"><bean:message key="demographic.demographicaddrecordhtm.msgSr"/></option>
	   		</select>
		</div>
		<%
	}else if(custom_demographic_fields.get(i).equals("address")){
		%>
		<div>
			<label><b><bean:message key="demographic.demographicaddrecordhtm.formAddress" />: </b></label>
			<input type="text" name="address" size=40 />
		</div>
		<%
	}else if(custom_demographic_fields.get(i).equals("city")){
		%>
		<div>
			<label><b><bean:message key="demographic.demographicaddrecordhtm.formCity" />: </b></label>
			<input type="text" name="city" value="<%=defaultCity %>" />
		</div>
		<%
	}else if(custom_demographic_fields.get(i).equals("province")){
		%>
		<div>
			<label>
				<b> 
				<% if(oscarProps.getProperty("demographicLabelProvince") == null) { %>
					<bean:message key="demographic.demographicaddrecordhtm.formprovince" />
				<% } else {
	  				out.print(oscarProps.getProperty("demographicLabelProvince"));
		 		} %> : 
		 		</b>
	 		</label>
		
		<% if (vLocale.getCountry().equals("BR")) { %> 
			<input type="text" name="province" value="<%=props.getProperty("billregion", "ON")%>">
		<% } else { %> 
			<select name="province">
				<option value="OT" <%=defaultProvince.equals("")||defaultProvince.equals("OT")?" selected":""%>>Other</option>
				<% 
				if (pNames.isDefined()) {
					for (ListIterator li = pNames.listIterator(); li.hasNext(); ) {
					    String province = (String) li.next(); %>
						<option value="<%=province%>" <%=province.equals(defaultProvince)?" selected":""%>><%=li.next()%></option>
						<% 
					}
				} else { %>
				<option value="AB" <%=defaultProvince.equals("AB")?" selected":""%>>AB-Alberta</option>
				<option value="BC" <%=defaultProvince.equals("BC")?" selected":""%>>BC-British Columbia</option>
				<option value="MB" <%=defaultProvince.equals("MB")?" selected":""%>>MB-Manitoba</option>
				<option value="NB" <%=defaultProvince.equals("NB")?" selected":""%>>NB-New Brunswick</option>
				<option value="NL" <%=defaultProvince.equals("NL")?" selected":""%>>NL-Newfoundland & Labrador</option>
				<option value="NT" <%=defaultProvince.equals("NT")?" selected":""%>>NT-Northwest Territory</option>
				<option value="NS" <%=defaultProvince.equals("NS")?" selected":""%>>NS-Nova Scotia</option>
				<option value="NU" <%=defaultProvince.equals("NU")?" selected":""%>>NU-Nunavut</option>
				<option value="ON" <%=defaultProvince.equals("ON")?" selected":""%>>ON-Ontario</option>
				<option value="PE" <%=defaultProvince.equals("PE")?" selected":""%>>PE-Prince Edward Island</option>
				<option value="QC" <%=defaultProvince.equals("QC")?" selected":""%>>QC-Quebec</option>
				<option value="SK" <%=defaultProvince.equals("SK")?" selected":""%>>SK-Saskatchewan</option>
				<option value="YT" <%=defaultProvince.equals("YT")?" selected":""%>>YT-Yukon</option>
				<option value="US" <%=defaultProvince.equals("US")?" selected":""%>>US resident</option>
				<option value="US-AK" <%=defaultProvince.equals("US-AK")?" selected":""%>>US-AK-Alaska</option>
				<option value="US-AL" <%=defaultProvince.equals("US-AL")?" selected":""%>>US-AL-Alabama</option>
				<option value="US-AR" <%=defaultProvince.equals("US-AR")?" selected":""%>>US-AR-Arkansas</option>
				<option value="US-AZ" <%=defaultProvince.equals("US-AZ")?" selected":""%>>US-AZ-Arizona</option>
				<option value="US-CA" <%=defaultProvince.equals("US-CA")?" selected":""%>>US-CA-California</option>
				<option value="US-CO" <%=defaultProvince.equals("US-CO")?" selected":""%>>US-CO-Colorado</option>
				<option value="US-CT" <%=defaultProvince.equals("US-CT")?" selected":""%>>US-CT-Connecticut</option>
				<option value="US-CZ" <%=defaultProvince.equals("US-CZ")?" selected":""%>>US-CZ-Canal Zone</option>
				<option value="US-DC" <%=defaultProvince.equals("US-DC")?" selected":""%>>US-DC-District Of Columbia</option>
				<option value="US-DE" <%=defaultProvince.equals("US-DE")?" selected":""%>>US-DE-Delaware</option>
				<option value="US-FL" <%=defaultProvince.equals("US-FL")?" selected":""%>>US-FL-Florida</option>
				<option value="US-GA" <%=defaultProvince.equals("US-GA")?" selected":""%>>US-GA-Georgia</option>
				<option value="US-GU" <%=defaultProvince.equals("US-GU")?" selected":""%>>US-GU-Guam</option>
				<option value="US-HI" <%=defaultProvince.equals("US-HI")?" selected":""%>>US-HI-Hawaii</option>
				<option value="US-IA" <%=defaultProvince.equals("US-IA")?" selected":""%>>US-IA-Iowa</option>
				<option value="US-ID" <%=defaultProvince.equals("US-ID")?" selected":""%>>US-ID-Idaho</option>
				<option value="US-IL" <%=defaultProvince.equals("US-IL")?" selected":""%>>US-IL-Illinois</option>
				<option value="US-IN" <%=defaultProvince.equals("US-IN")?" selected":""%>>US-IN-Indiana</option>
				<option value="US-KS" <%=defaultProvince.equals("US-KS")?" selected":""%>>US-KS-Kansas</option>
				<option value="US-KY" <%=defaultProvince.equals("US-KY")?" selected":""%>>US-KY-Kentucky</option>
				<option value="US-LA" <%=defaultProvince.equals("US-LA")?" selected":""%>>US-LA-Louisiana</option>
				<option value="US-MA" <%=defaultProvince.equals("US-MA")?" selected":""%>>US-MA-Massachusetts</option>
				<option value="US-MD" <%=defaultProvince.equals("US-MD")?" selected":""%>>US-MD-Maryland</option>
				<option value="US-ME" <%=defaultProvince.equals("US-ME")?" selected":""%>>US-ME-Maine</option>
				<option value="US-MI" <%=defaultProvince.equals("US-MI")?" selected":""%>>US-MI-Michigan</option>
				<option value="US-MN" <%=defaultProvince.equals("US-MN")?" selected":""%>>US-MN-Minnesota</option>
				<option value="US-MO" <%=defaultProvince.equals("US-MO")?" selected":""%>>US-MO-Missouri</option>
				<option value="US-MS" <%=defaultProvince.equals("US-MS")?" selected":""%>>US-MS-Mississippi</option>
				<option value="US-MT" <%=defaultProvince.equals("US-MT")?" selected":""%>>US-MT-Montana</option>
				<option value="US-NC" <%=defaultProvince.equals("US-NC")?" selected":""%>>US-NC-North Carolina</option>
				<option value="US-ND" <%=defaultProvince.equals("US-ND")?" selected":""%>>US-ND-North Dakota</option>
				<option value="US-NE" <%=defaultProvince.equals("US-NE")?" selected":""%>>US-NE-Nebraska</option>
				<option value="US-NH" <%=defaultProvince.equals("US-NH")?" selected":""%>>US-NH-New Hampshire</option>
				<option value="US-NJ" <%=defaultProvince.equals("US-NJ")?" selected":""%>>US-NJ-New Jersey</option>
				<option value="US-NM" <%=defaultProvince.equals("US-NM")?" selected":""%>>US-NM-New Mexico</option>
				<option value="US-NU" <%=defaultProvince.equals("US-NU")?" selected":""%>>US-NU-Nunavut</option>
				<option value="US-NV" <%=defaultProvince.equals("US-NV")?" selected":""%>>US-NV-Nevada</option>
				<option value="US-NY" <%=defaultProvince.equals("US-NY")?" selected":""%>>US-NY-New York</option>
				<option value="US-OH" <%=defaultProvince.equals("US-OH")?" selected":""%>>US-OH-Ohio</option>
				<option value="US-OK" <%=defaultProvince.equals("US-OK")?" selected":""%>>US-OK-Oklahoma</option>
				<option value="US-OR" <%=defaultProvince.equals("US-OR")?" selected":""%>>US-OR-Oregon</option>
				<option value="US-PA" <%=defaultProvince.equals("US-PA")?" selected":""%>>US-PA-Pennsylvania</option>
				<option value="US-PR" <%=defaultProvince.equals("US-PR")?" selected":""%>>US-PR-Puerto Rico</option>
				<option value="US-RI" <%=defaultProvince.equals("US-RI")?" selected":""%>>US-RI-Rhode Island</option>
				<option value="US-SC" <%=defaultProvince.equals("US-SC")?" selected":""%>>US-SC-South Carolina</option>
				<option value="US-SD" <%=defaultProvince.equals("US-SD")?" selected":""%>>US-SD-South Dakota</option>
				<option value="US-TN" <%=defaultProvince.equals("US-TN")?" selected":""%>>US-TN-Tennessee</option>
				<option value="US-TX" <%=defaultProvince.equals("US-TX")?" selected":""%>>US-TX-Texas</option>
				<option value="US-UT" <%=defaultProvince.equals("US-UT")?" selected":""%>>US-UT-Utah</option>
				<option value="US-VA" <%=defaultProvince.equals("US-VA")?" selected":""%>>US-VA-Virginia</option>
				<option value="US-VI" <%=defaultProvince.equals("US-VI")?" selected":""%>>US-VI-Virgin Islands</option>
				<option value="US-VT" <%=defaultProvince.equals("US-VT")?" selected":""%>>US-VT-Vermont</option>
				<option value="US-WA" <%=defaultProvince.equals("US-WA")?" selected":""%>>US-WA-Washington</option>
				<option value="US-WI" <%=defaultProvince.equals("US-WI")?" selected":""%>>US-WI-Wisconsin</option>
				<option value="US-WV" <%=defaultProvince.equals("US-WV")?" selected":""%>>US-WV-West Virginia</option>
				<option value="US-WY" <%=defaultProvince.equals("US-WY")?" selected":""%>>US-WY-Wyoming</option>
				<% } %>
			</select> <% 
			} 
		%>
		</div>
		<%
	}else if(custom_demographic_fields.get(i).equals("postal")){
		%>
		<div>
			<label>
				<b> 
				<% if(oscarProps.getProperty("demographicLabelPostal") == null) { %>
					<bean:message key="demographic.demographicaddrecordhtm.formPostal" />
				<% } else {
	  				out.print(oscarProps.getProperty("demographicLabelPostal"));
		 		} %> : 
		 		</b>
		 	</label>
			<input type="text" name="postal" onBlur="upCaseCtrl(this)">
		</div>
		<%
	}else if(custom_demographic_fields.get(i).equals("phone")){
		%>
		<div>
			<label><b><bean:message key="demographic.demographicaddrecordhtm.formPhoneHome" />: </b></label>
			<input type="text" name="phone" onBlur="formatPhoneNum()" value="<%=props.getProperty("phoneprefix", "905-")%>">
			<bean:message key="demographic.demographicaddrecordhtm.Ext" />:
			<input type="text" name="hPhoneExt" value="" size="4" />
		</div>
		<%
		
	}else if(custom_demographic_fields.get(i).equals("phone2")){
		%>
		<div>
			<label><b><bean:message key="demographic.demographicaddrecordhtm.formPhoneWork" />:</b></label>
			<input type="text" name="phone2" onBlur="formatPhoneNum()" value="">
			<bean:message key="demographic.demographicaddrecordhtm.Ext" />:
			<input type="text" name="wPhoneExt" value="" style="display: inline" size="4" />
		</div>
		<%
	}else if(custom_demographic_fields.get(i).equals("cellphone")){
		%>
		<div>
			<label><b><bean:message key="demographic.demographicaddrecordhtm.formPhoneCell" />: </b></label>
			<input type="text" name="cellphone" onBlur="formatPhoneNum()">
		</div>
		<%
	}else if(custom_demographic_fields.get(i).equals("newsletter")){
		%>
		<div>
			<label><b><bean:message key="demographic.demographicaddrecordhtm.formNewsLetter" />: </b></label>
			<select name="newsletter">
				<option value="Unknown" selected><bean:message key="demographic.demographicaddrecordhtm.formNewsLetter.optUnknown" /></option>
				<option value="No"><bean:message key="demographic.demographicaddrecordhtm.formNewsLetter.optNo" /></option>
				<option value="Paper"><bean:message key="demographic.demographicaddrecordhtm.formNewsLetter.optPaper" /></option>
				<option value="Electronic"><bean:message key="demographic.demographicaddrecordhtm.formNewsLetter.optElectronic" /></option>
			</select>
		</div>
		<%
	}else if(custom_demographic_fields.get(i).equals("email")){
		%>
		<div>
			<label><b><bean:message key="demographic.demographicaddrecordhtm.formEMail" />: </b></label>
			<input type="text" name="email" value="">
		</div>
		<%
	}else if(custom_demographic_fields.get(i).equals("pin")){
		%>
		<div>
			<label><b><bean:message key="demographic.demographicaddrecordhtm.formPIN" />:</b></label>
			<input type="text" name="pin" value="">
		</div>
		<%
		
	}else if(custom_demographic_fields.get(i).equals("dob")){
		%>
		<div>
			<label><b><bean:message key="demographic.demographicaddrecordhtm.formDOB" /></b><font size="-2">(yyyymmdd)</font><b><font color="red">:</font></b></label>
			<input type="text" name="year_of_birth" size="4" maxlength="4" value="yyyy" onFocus="if(this.value=='yyyy')this.value='';" onBlur="if(this.value=='')this.value='yyyy';">
			-
			<select name="month_of_birth">
				<option value="01">01</option>
				<option value="02">02</option>
				<option value="03">03</option>
				<option value="04">04</option>
				<option value="05">05</option>
				<option selected value="06">06</option>
				<option value="07">07</option>
				<option value="08">08</option>
				<option value="09">09</option>
				<option value="10">10</option>
				<option value="11">11</option>
				<option value="12">12</option>
			</select>
			-
			<select name="date_of_birth">
				<option value="01">01</option>
				<option value="02">02</option>
				<option value="03">03</option>
				<option value="04">04</option>
				<option value="05">05</option>
				<option value="06">06</option>
				<option value="07">07</option>
				<option value="08">08</option>
				<option value="09">09</option>
				<option value="10">10</option>
				<option value="11">11</option>
				<option value="12">12</option>
				<option value="13">13</option>
				<option value="14">14</option>
				<option selected value="15">15</option>
				<option value="16">16</option>
				<option value="17">17</option>
				<option value="18">18</option>
				<option value="19">19</option>
				<option value="20">20</option>
				<option value="21">21</option>
				<option value="22">22</option>
				<option value="23">23</option>
				<option value="24">24</option>
				<option value="25">25</option>
				<option value="26">26</option>
				<option value="27">27</option>
				<option value="28">28</option>
				<option value="29">29</option>
				<option value="30">30</option>
				<option value="31">31</option>
			</select>
		</div>
		<%
	}else if(custom_demographic_fields.get(i).equals("sex")){
		%>
		<div>
			<label><b><bean:message key="demographic.demographicaddrecordhtm.formSex" /><font color="red">:</font></b></label>
			<% // Determine if curUser has selected a default sex in preferences
                UserProperty sexProp = userPropertyDAO.getProp(curUser_no,  UserProperty.DEFAULT_SEX);
                String sex = "";
                if (sexProp != null) {
                    sex = sexProp.getValue();
                } else {
                    // Access defaultsex system property
                    sex = props.getProperty("defaultsex","");
                }
             %>
	         <select name="sex">
	             <option value="M"  <%= sex.equals("M") ? " selected": "" %>><bean:message key="demographic.demographicaddrecordhtm.formM" /></option>
	             <option value="F"  <%= sex.equals("F") ? " selected": "" %>><bean:message key="demographic.demographicaddrecordhtm.formF" /></option>
	         </select>
		</div>
		<%
	}else if(custom_demographic_fields.get(i).equals("hin")){
		%>
		<div>
			<label><b><bean:message key="demographic.demographicaddrecordhtm.formHIN" />: </b></label>
			<input type="text" name="hin" size="15">
			<b><bean:message key="demographic.demographicaddrecordhtm.formVer" />:</b>
			<input type="text" name="ver" value="" size="3" onBlur="upCaseCtrl(this)">
		</div>
		<%
	}else if(custom_demographic_fields.get(i).equals("eff_date")){
		%>
		<div>
			<label><b><bean:message key="demographic.demographicaddrecordhtm.formEFFDate" />: </b></label>
			<input type="text" name="eff_date_year" size="4" maxlength="4">
			<input type="text" name="eff_date_month" size="2" maxlength="2">
			<input type="text" name="eff_date_date" size="2" maxlength="2">
		</div>
		<%
	}else if(custom_demographic_fields.get(i).equals("hc_type")){
		%>
		<div>
			<label><b><bean:message key="demographic.demographicaddrecordhtm.formHCType" />: </b></label>
			<% if(vLocale.getCountry().equals("BR")) { %> 
				<input type="text" name="hc_type" value=""> <% 
			} else { %>
				<select name="hc_type">
					<option value="OT" <%=HCType.equals("")||HCType.equals("OT")?" selected":""%>>Other</option>
					<% 
					if (pNames.isDefined()) {
                   		for (ListIterator li = pNames.listIterator(); li.hasNext(); ) {
                       		String province = (String) li.next(); %>
                       		<option value="<%=province%>"<%=province.equals(HCType)?" selected":""%>><%=li.next()%></option>
                   		<% } %>
            		<% } else { %>
						<option value="AB"<%=HCType.equals("AB")?" selected":""%>>AB-Alberta</option>
						<option value="BC"<%=HCType.equals("BC")?" selected":""%>>BC-British Columbia</option>
						<option value="MB"<%=HCType.equals("MB")?" selected":""%>>MB-Manitoba</option>
						<option value="NB"<%=HCType.equals("NB")?" selected":""%>>NB-New Brunswick</option>
						<option value="NL"<%=HCType.equals("NL")?" selected":""%>>NL-Newfoundland & Labrador</option>
						<option value="NT"<%=HCType.equals("NT")?" selected":""%>>NT-Northwest Territory</option>
						<option value="NS"<%=HCType.equals("NS")?" selected":""%>>NS-Nova Scotia</option>
						<option value="NU"<%=HCType.equals("NU")?" selected":""%>>NU-Nunavut</option>
						<option value="ON"<%=HCType.equals("ON")?" selected":""%>>ON-Ontario</option>
						<option value="PE"<%=HCType.equals("PE")?" selected":""%>>PE-Prince Edward Island</option>
						<option value="QC"<%=HCType.equals("QC")?" selected":""%>>QC-Quebec</option>
						<option value="SK"<%=HCType.equals("SK")?" selected":""%>>SK-Saskatchewan</option>
						<option value="YT"<%=HCType.equals("YT")?" selected":""%>>YT-Yukon</option>
						<option value="US"<%=HCType.equals("US")?" selected":""%>>US resident</option>
						<option value="US-AK" <%=HCType.equals("US-AK")?" selected":""%>>US-AK-Alaska</option>
						<option value="US-AL" <%=HCType.equals("US-AL")?" selected":""%>>US-AL-Alabama</option>
						<option value="US-AR" <%=HCType.equals("US-AR")?" selected":""%>>US-AR-Arkansas</option>
						<option value="US-AZ" <%=HCType.equals("US-AZ")?" selected":""%>>US-AZ-Arizona</option>
						<option value="US-CA" <%=HCType.equals("US-CA")?" selected":""%>>US-CA-California</option>
						<option value="US-CO" <%=HCType.equals("US-CO")?" selected":""%>>US-CO-Colorado</option>
						<option value="US-CT" <%=HCType.equals("US-CT")?" selected":""%>>US-CT-Connecticut</option>
						<option value="US-CZ" <%=HCType.equals("US-CZ")?" selected":""%>>US-CZ-Canal Zone</option>
						<option value="US-DC" <%=HCType.equals("US-DC")?" selected":""%>>US-DC-District Of Columbia</option>
						<option value="US-DE" <%=HCType.equals("US-DE")?" selected":""%>>US-DE-Delaware</option>
						<option value="US-FL" <%=HCType.equals("US-FL")?" selected":""%>>US-FL-Florida</option>
						<option value="US-GA" <%=HCType.equals("US-GA")?" selected":""%>>US-GA-Georgia</option>
						<option value="US-GU" <%=HCType.equals("US-GU")?" selected":""%>>US-GU-Guam</option>
						<option value="US-HI" <%=HCType.equals("US-HI")?" selected":""%>>US-HI-Hawaii</option>
						<option value="US-IA" <%=HCType.equals("US-IA")?" selected":""%>>US-IA-Iowa</option>
						<option value="US-ID" <%=HCType.equals("US-ID")?" selected":""%>>US-ID-Idaho</option>
						<option value="US-IL" <%=HCType.equals("US-IL")?" selected":""%>>US-IL-Illinois</option>
						<option value="US-IN" <%=HCType.equals("US-IN")?" selected":""%>>US-IN-Indiana</option>
						<option value="US-KS" <%=HCType.equals("US-KS")?" selected":""%>>US-KS-Kansas</option>
						<option value="US-KY" <%=HCType.equals("US-KY")?" selected":""%>>US-KY-Kentucky</option>
						<option value="US-LA" <%=HCType.equals("US-LA")?" selected":""%>>US-LA-Louisiana</option>
						<option value="US-MA" <%=HCType.equals("US-MA")?" selected":""%>>US-MA-Massachusetts</option>
						<option value="US-MD" <%=HCType.equals("US-MD")?" selected":""%>>US-MD-Maryland</option>
						<option value="US-ME" <%=HCType.equals("US-ME")?" selected":""%>>US-ME-Maine</option>
						<option value="US-MI" <%=HCType.equals("US-MI")?" selected":""%>>US-MI-Michigan</option>
						<option value="US-MN" <%=HCType.equals("US-MN")?" selected":""%>>US-MN-Minnesota</option>
						<option value="US-MO" <%=HCType.equals("US-MO")?" selected":""%>>US-MO-Missouri</option>
						<option value="US-MS" <%=HCType.equals("US-MS")?" selected":""%>>US-MS-Mississippi</option>
						<option value="US-MT" <%=HCType.equals("US-MT")?" selected":""%>>US-MT-Montana</option>
						<option value="US-NC" <%=HCType.equals("US-NC")?" selected":""%>>US-NC-North Carolina</option>
						<option value="US-ND" <%=HCType.equals("US-ND")?" selected":""%>>US-ND-North Dakota</option>
						<option value="US-NE" <%=HCType.equals("US-NE")?" selected":""%>>US-NE-Nebraska</option>
						<option value="US-NH" <%=HCType.equals("US-NH")?" selected":""%>>US-NH-New Hampshire</option>
						<option value="US-NJ" <%=HCType.equals("US-NJ")?" selected":""%>>US-NJ-New Jersey</option>
						<option value="US-NM" <%=HCType.equals("US-NM")?" selected":""%>>US-NM-New Mexico</option>
						<option value="US-NU" <%=HCType.equals("US-NU")?" selected":""%>>US-NU-Nunavut</option>
						<option value="US-NV" <%=HCType.equals("US-NV")?" selected":""%>>US-NV-Nevada</option>
						<option value="US-NY" <%=HCType.equals("US-NY")?" selected":""%>>US-NY-New York</option>
						<option value="US-OH" <%=HCType.equals("US-OH")?" selected":""%>>US-OH-Ohio</option>
						<option value="US-OK" <%=HCType.equals("US-OK")?" selected":""%>>US-OK-Oklahoma</option>
						<option value="US-OR" <%=HCType.equals("US-OR")?" selected":""%>>US-OR-Oregon</option>
						<option value="US-PA" <%=HCType.equals("US-PA")?" selected":""%>>US-PA-Pennsylvania</option>
						<option value="US-PR" <%=HCType.equals("US-PR")?" selected":""%>>US-PR-Puerto Rico</option>
						<option value="US-RI" <%=HCType.equals("US-RI")?" selected":""%>>US-RI-Rhode Island</option>
						<option value="US-SC" <%=HCType.equals("US-SC")?" selected":""%>>US-SC-South Carolina</option>
						<option value="US-SD" <%=HCType.equals("US-SD")?" selected":""%>>US-SD-South Dakota</option>
						<option value="US-TN" <%=HCType.equals("US-TN")?" selected":""%>>US-TN-Tennessee</option>
						<option value="US-TX" <%=HCType.equals("US-TX")?" selected":""%>>US-TX-Texas</option>
						<option value="US-UT" <%=HCType.equals("US-UT")?" selected":""%>>US-UT-Utah</option>
						<option value="US-VA" <%=HCType.equals("US-VA")?" selected":""%>>US-VA-Virginia</option>
						<option value="US-VI" <%=HCType.equals("US-VI")?" selected":""%>>US-VI-Virgin Islands</option>
						<option value="US-VT" <%=HCType.equals("US-VT")?" selected":""%>>US-VT-Vermont</option>
						<option value="US-WA" <%=HCType.equals("US-WA")?" selected":""%>>US-WA-Washington</option>
						<option value="US-WI" <%=HCType.equals("US-WI")?" selected":""%>>US-WI-Wisconsin</option>
						<option value="US-WV" <%=HCType.equals("US-WV")?" selected":""%>>US-WV-West Virginia</option>
						<option value="US-WY" <%=HCType.equals("US-WY")?" selected":""%>>US-WY-Wyoming</option>
          			<% } %>
          	</select>
         <% } %>
		</div>
		<%
	}else if(custom_demographic_fields.get(i).equals("countryOfOrigin")){
		%>
		<div>
			<label><b><bean:message key="demographic.demographicaddrecordhtm.msgCountryOfOrigin"/>:</b></label>
			<select name="countryOfOrigin">
	            <option value="-1"><bean:message key="demographic.demographicaddrecordhtm.msgNotSet"/></option>
	            <%for(CountryCode cc : countryList){ %>
	            	<option value="<%=cc.getCountryId()%>"><%=cc.getCountryName() %></option>
	            <%}%>
          </select>
		</div>
		<%
	}else if(custom_demographic_fields.get(i).equals("sin")){
		%>
		<div>
			<label><b><bean:message key="demographic.demographicaddrecordhtm.msgSIN"/>:</b></label>
		    <input type="text" name="sin">
		</div>
		<%
	}else if(custom_demographic_fields.get(i).equals("cytolNum")){
		%>
		<div>
			<label><b><bean:message key="demographic.demographicaddrecordhtm.cytolNum"/>:</b></label>
			<input type="text" name="cytolNum">
		</div>
		<%
	}else if(custom_demographic_fields.get(i).equals("doctor")){
		%>
		<div>
			<label>
				<b>
				<% if(oscarProps.getProperty("demographicLabelDoctor") != null) { 
					out.print(oscarProps.getProperty("demographicLabelDoctor","")); 
				} else { %>
                    <bean:message key="demographic.demographicaddrecordhtm.formDoctor"/>
                <% } %>:
                </b>
			</label>
			<select name="staff">
          		<%
				  ResultSet rsdemo = addDemoBean.queryResults("search_provider");
				  while (rsdemo.next()) {
				%>
					<option value="<%=rsdemo.getString("provider_no")%>"><%=Misc.getShortStr( (rsdemo.getString("last_name")+","+rsdemo.getString("first_name")),"",12)%></option>
				<%
				  }
				  rsdemo.close();
				%>
					<option value=""></option>
			</select>
		</div>
		<%
	}else if(custom_demographic_fields.get(i).equals("nurse")){
		%>
		<div>
			<label><b><bean:message key="demographic.demographicaddrecordhtm.formNurse" />: </b></label>
			<select name="cust1">
			<option value=""></option>
			<%
			ResultSet rsdemo=addDemoBean.queryResults("search_provider");
			while (rsdemo.next()) {
			%>
				<option value="<%=rsdemo.getString("provider_no")%>"><%=Misc.getShortStr( (Misc.getString(rsdemo,"last_name")+","+Misc.getString(rsdemo,"first_name")),"",12)%></option>
			<%
 				}
  			rsdemo.close();
			%>
			</select>
		</div>
		<%
	}else if(custom_demographic_fields.get(i).equals("midwife")){
		%>
		<div>
			<label><b><bean:message key="demographic.demographicaddrecordhtm.formMidwife" />: </b></label>
			<select name="cust4">
				<option value=""></option>
				<%
				ResultSet rsdemo=addDemoBean.queryResults("search_provider");
				while (rsdemo.next()) {
					%>
					<option value="<%=Misc.getString(rsdemo,"provider_no")%>">
					<%=Misc.getShortStr( (Misc.getString(rsdemo,"last_name")+","+Misc.getString(rsdemo,"first_name")),"",12)%></option>
					<%
				}
				rsdemo.close();
				%>
			</select>
		</div>
		<%
	}else if(custom_demographic_fields.get(i).equals("resident")){
		%>
		<div>
			<label><b><bean:message key="demographic.demographicaddrecordhtm.formResident" />: </b></label>
			<select name="cust2">
				<option value=""></option>
				<%
				ResultSet rsdemo=addDemoBean.queryResults("search_provider");
				while (rsdemo.next()) {
					%>
					<option value="<%=Misc.getString(rsdemo,"provider_no")%>">
					<%=Misc.getShortStr( (Misc.getString(rsdemo,"last_name")+","+Misc.getString(rsdemo,"first_name")),"",12)%></option>
					<%
				}
				rsdemo.close();
				%>
			</select>
		</div>
		<%
	}else if(custom_demographic_fields.get(i).equals("referral_doc")){
		%>
		<div>
			<label><b><bean:message key="demographic.demographicaddrecordhtm.formReferalDoctor" />:</b></label>
			<% if(oscarProps.getProperty("isMRefDocSelectList", "").equals("true") ) {
		          // drop down list
				  String sql = "select * from billingreferral order by last_name, first_name" ;
				  oscar.oscarBilling.ca.on.data.BillingONDataHelp dbObj = new oscar.oscarBilling.ca.on.data.BillingONDataHelp();
				  ResultSet rs1 = dbObj.searchDBRecord(sql);
					
				  Properties prop = null;
				  List<Properties> vecRef = new ArrayList<Properties>();
				  while (rs1.next()) {
				  	prop = new Properties();
				  	prop.setProperty("referral_no",rs1.getString("referral_no"));
				  	prop.setProperty("last_name",rs1.getString("last_name"));
				  	prop.setProperty("first_name",rs1.getString("first_name"));
				  	vecRef.add(prop);
		          }
	            %>
	            <select name="r_doctor" onChange="changeRefDoc()" style="width: 200px">
					<option value=""></option>
					<% for(int k=0; k<vecRef.size(); k++) {
	                    prop= (Properties) vecRef.get(k);%>
						<option value="<%=prop.getProperty("last_name")+","+prop.getProperty("first_name")%>">
						<%=Misc.getShortStr( (prop.getProperty("last_name")+","+prop.getProperty("first_name")),"",nStrShowLen)%></option>
					<% } %>
				</select>
				<script language="Javascript">
					<!--
					function changeRefDoc() {
					//alert(document.forms[1].r_doctor.value);
					var refName = document.forms[1].r_doctor.options[document.forms[1].r_doctor.selectedIndex].value;
					var refNo = "";
					  	<% for(int k=0; k<vecRef.size(); k++) {
					  		prop= (Properties) vecRef.get(k);
					  	%>
					if(refName.indexOf("<%=prop.getProperty("last_name")+","+prop.getProperty("first_name")%>")>=0) {
					  refNo = <%=prop.getProperty("referral_no", "")%>;
					}
					<% } %>
					document.forms[1].r_doctor_ohip.value = refNo;
					}
					//-->
				</script> <% 
			} else {%> 
				<input type="text" name="r_doctor" size="30" maxlength="40" value=""> <% 
			} %>
		</div>
		<div>
			<label><b><bean:message key="demographic.demographicaddrecordhtm.formReferalDoctorN" />:</b></label>
			<input type="text" name="r_doctor_ohip" maxlength="6">
			<% if("ON".equals(prov)) { %>
                 <!--add more if-else statements to include other languages for now if en and fr-->
                 <% if (vLocale.getLanguage().equals("en")) {%>
					<a href=# onClick ="popupPage(600,750,'<%=protocol%>www.cmq.org/en/RepertoireMembres/Recherche.aspx');return false;"> <bean:message key="demographic.demographicaddrecordhtm.Search"/></a>
                 <% }else if (vLocale.getLanguage().equals("fr")){%>
                     <a href=# onClick ="popupPage(600,750,'<%=protocol%>www.cmq.org/fr/RepertoireMembres/Recherche.aspx');return false;"> <bean:message key="demographic.demographicaddrecordhtm.Search"/></a>
                 <%}%>
            <% } %>
		</div>
		<%
	}else if(custom_demographic_fields.get(i).equals("roster_status")){
		%>
		<div>
			<label><b><bean:message key="demographic.demographicaddrecordhtm.formPCNRosterStatus" />: </b></label>
			<select name="roster_status" style="width: 160">
				<option value=""></option>
				<option value="RO"><bean:message key="demographic.demographicaddrecordhtm.RO-rostered" /></option>
				<option value="NR"><bean:message key="demographic.demographicaddrecordhtm.NR-notrostered" /></option>
				<option value="TE"><bean:message key="demographic.demographicaddrecordhtm.TE-terminated" /></option>
				<option value="FS"><bean:message key="demographic.demographicaddrecordhtm.FS-feeforservice" /></option>
				<% ResultSet rsstatus1 = addDemoBean.queryResults("search_rsstatus");
             	while (rsstatus1.next()) { %>
					<option value="<%=rsstatus1.getString("roster_status")%>"><%=rsstatus1.getString("roster_status")%></option>
				<% } // end while %>
			</select>
			<input type="button" onClick="newStatus1();" value="<bean:message key="demographic.demographicaddrecordhtm.AddNewRosterStatus"/> " />
		</div>
		<%
	}else if(custom_demographic_fields.get(i).equals("date_rostered")){
		%>
		<div>
			<label><b><bean:message key="demographic.demographicaddrecordhtm.formPCNDateJoined" />: </b></label>
			<input type="text" name="hc_renew_date_year" size="4" maxlength="4">
			<input type="text" name="hc_renew_date_month" size="2" maxlength="2">
			<input type="text" name="hc_renew_date_date" size="2" maxlength="2">
		</div>
		<%
	}else if(custom_demographic_fields.get(i).equals("patient_status")){
		%>
		<div>
			<label><b><bean:message key="demographic.demographicaddrecordhtm.formPatientStatus" />:</b></label>
			<% if (vLocale.getCountry().equals("BR")) { %> 
				<input type="text" name="patient_status" value="AC" onBlur="upCaseCtrl(this)">
			<% } else { %>
				<select name="patient_status" style="width: 160">
					<option value="AC"><bean:message key="demographic.demographicaddrecordhtm.AC-Active" /></option>
					<option value="IN"><bean:message key="demographic.demographicaddrecordhtm.IN-InActive" /></option>
					<option value="DE"><bean:message key="demographic.demographicaddrecordhtm.DE-Deceased" /></option>
					<option value="MO"><bean:message key="demographic.demographicaddrecordhtm.MO-Moved" /></option>
					<option value="FI"><bean:message key="demographic.demographicaddrecordhtm.FI-Fired" /></option>
					<% ResultSet rsstatus = addDemoBean.queryResults("search_ptstatus");
             		while (rsstatus.next()) { %>
						<option value="<%=rsstatus.getString("patient_status")%>"><%=rsstatus.getString("patient_status")%></option>
					<% } // end while %>
				</select>
				<input type="button" onClick="newStatus();" value="<bean:message key="demographic.demographicaddrecordhtm.AddNewPatient"/> ">
			<% } // end if...then...else %>
		</div>
		<%
	}else if(custom_demographic_fields.get(i).equals("chart_no")){
		%>
		<div>
			<label><b><bean:message key="demographic.demographicaddrecordhtm.formChartNo" />:</b></label>
			<input type="text" name="chart_no" value="">
		</div>
		<%
		
	
	}else if(custom_demographic_fields.get(i).equals("waiting_list")){
		String wLReadonly = "";
		WaitingList wL = WaitingList.getInstance();
		if(!wL.getFound()){
		    wLReadonly = "readonly";
		}
		%>
		<div>
			<label><b> <bean:message key="demographic.demographicaddarecordhtm.msgWaitList"/>: </b></label>
			<select name="list_id">
				<%if(wLReadonly.equals("")){ %>
					<option value="0">--Select Waiting List--</option>
				<%}else{ %>
					<option value="0"><bean:message key="demographic.demographicaddarecordhtm.optCreateWaitList"/></option>
				<%} 
			    ResultSet rsWL = addDemoBean.queryResults("search_waiting_list");
			    while (rsWL.next()) {
			    %>
					<option value="<%=rsWL.getString("ID")%>"><%=rsWL.getString("name")%></option>
				<%
			    }
			    %>
			</select>
		</div>
		<div>
			<label><b><bean:message key="demographic.demographicaddarecordhtm.msgWaitListNote"/>: </b></label>
			<input type="text" name="waiting_list_note"	size="36" <%=wLReadonly%>>
		</div>
		<div>
			<label><b><bean:message key="demographic.demographicaddarecordhtm.msgDateOfReq"/>:</b></label>
			<input type="text" name="waiting_list_referral_date" id="waiting_list_referral_date" value="" size="12" <%=wLReadonly%>> <img src="../images/cal.gif" id="referral_date_cal">(yyyy-mm-dd)
		</div>
		<script type="text/javascript">
		Calendar.setup({ inputField : "waiting_list_referral_date", ifFormat : "%Y-%m-%d", showsTime :false, button : "referral_date_cal", singleClick : true, step : 1 });
		</script>
		<%		
	}else if(custom_demographic_fields.get(i).equals("date_joined")){
		%>
		<div>
			<label><b><bean:message key="demographic.demographicaddrecordhtm.formDateJoined" />:</b></label>
			<input type="text" name="date_joined_year" size="4" maxlength="4" value="<%=curYear%>">
			<input type="text" name="date_joined_month" size="2" maxlength="2" value="<%=curMonth%>">
			<input type="text" name="date_joined_date" size="2" maxlength="2" value="<%=curDay%>">
		</div>
		<%
	}else if(custom_demographic_fields.get(i).equals("end_date")){
		%>
			<div>
				<label><b><bean:message key="demographic.demographicaddrecordhtm.formEndDate" />:</b></label>
				<input type="text" name="end_date_year" size="4" maxlength="4">
				<input type="text" name="end_date_month" size="2" maxlength="2">
				<input type="text" name="end_date_date" size="2" maxlength="2">
			</div>
	<%
	}else if(custom_demographic_fields.get(i).equals("alert")){
		%>
			<div>
				<label style="color: #FF0000;"><b><bean:message key="demographic.demographicaddrecordhtm.formAlert" />: </b></label>
				<textarea name="cust3" style="width: 100%" rows="2"></textarea>
			</div>
		<%
	}else if(custom_demographic_fields.get(i).equals("form_notes")){
		%>
			<div>
				<label><b><bean:message key="demographic.demographicaddrecordhtm.formNotes" /> : </b></label>
				<textarea name="content" style="width: 100%" rows="2"></textarea>
			</div>
	<% 
	}
	
	// START: Stuff that seems to be available only in Brazil that we may NEVER use
	if(vLocale.getCountry().equals("BR")){
		if(custom_demographic_fields.get(i).equals("address_no")){
			%> 
			<div>
				<label><b><bean:message key="demographic.demographicaddrecordhtm.formAddressNo" />:</b></label>
				<input type="text" name="address_no" size="6"> 
			</div>
			<%
		}else if(custom_demographic_fields.get(i).equals("complementary_address")){
			%>
			<div>
				<label><b><bean:message key="demographic.demographicaddrecordhtm.formComplementaryAddress" />:</b></label>
				<input type="text" name="complementary_address" onBlur="upCaseCtrl(this)">
			</div>

			<%
		}else if(custom_demographic_fields.get(i).equals("district")){
			%>
			<div>
			<label><b><bean:message key="demographic.demographicaddrecordhtm.formDistrict" />: </b></label>
				<input type="text" name="district" onBlur="upCaseCtrl(this)">
			</div>
			<%
		}else if(custom_demographic_fields.get(i).equals("rg")){
			%>
			<div>
				<label><b><bean:message	key="demographic.demographicaddrecordhtm.formRG" />:</b></label>
				<input type="text" name="rg" onBlur="upCaseCtrl(this)">
			</div>
			<%
		}else if(custom_demographic_fields.get(i).equals("cpf")){
			%>
			<div>
				<label align="right"><b><bean:message key="demographic.demographicaddrecordhtm.formCPF" />:</b></label>
				<input type="text" name="cpf" onBlur="upCaseCtrl(this)">
			</div>
			<%
		}else if(custom_demographic_fields.get(i).equals("marital_state")){
			%>
			<div>
				<label><b><bean:message	key="demographic.demographicaddrecordhtm.formMaritalState" />:</b></label>
				<select name="marital_state">
					<option value="-">-</option>
					<option value="S"><bean:message	key="demographic.demographicaddrecordhtm.formMaritalState.optSingle" /></option>
					<option value="M"><bean:message	key="demographic.demographicaddrecordhtm.formMaritalState.optMarried" /></option>
					<option value="R"><bean:message	key="demographic.demographicaddrecordhtm.formMaritalState.optSeparated" /></option>
					<option value="D"><bean:message	key="demographic.demographicaddrecordhtm.formMaritalState.optDivorced" /></option>
					<option value="W"><bean:message	key="demographic.demographicaddrecordhtm.formMaritalState.optWidower" /></option>
				</select>
			</div>
			<%
		}else if(custom_demographic_fields.get(i).equals("birth_certificate")){
			%>
			<div>
				<label><b><bean:message	key="demographic.demographicaddrecordhtm.formBirthCertificate" />:</b>
				</label>
				<input type="text" name="birth_certificate"	onBlur="upCaseCtrl(this)">
			</div>
			<%
		}else if(custom_demographic_fields.get(i).equals("marriage_certificate")){
			%>
			<div>
				<label><b><bean:message	key="demographic.demographicaddrecordhtm.formMarriageCertificate" />:</b></label>
				<input type="text" name="marriage_certificate" onBlur="upCaseCtrl(this)">
			</div>
			<%
		}else if(custom_demographic_fields.get(i).equals("partner_name")){
			%>
			<div>
				<label><b><bean:message	key="demographic.demographicaddrecordhtm.formPartnerName" />:</b></label>
				<input type="text" name="partner_name" onBlur="upCaseCtrl(this)">
			</div>
			<%
		}else if(custom_demographic_fields.get(i).equals("father_name")){
			%>
			<div>
				<label><b><bean:message	key="demographic.demographicaddrecordhtm.formFatherName" />:</b></label>
				<input type="text" name="father_name" onBlur="upCaseCtrl(this)">
			</div>
			<%
		}else if(custom_demographic_fields.get(i).equals("mother_name")){
			%>
			<div>
				<label><b><bean:message	key="demographic.demographicaddrecordhtm.formMotherName" />:</b></label>
				<input type="text" name="mother_name" onBlur="upCaseCtrl(this)">
			</div>
			<%
		}
	}//end of Brazil form fields
	
	
	
}

for(int i=0; i<hidden_demographic_fields.size(); i++){
	if(hidden_demographic_fields.get(i).equals("last_name"))
	{
		%>
		<input type="hidden" name="last_name" size=30 value=""/>
		<%
	}else if(hidden_demographic_fields.get(i).equals("first_name"))
	{
		%>
		<input type="hidden" name="first_name" size=30 value=""/>
		<%		
	}else if(hidden_demographic_fields.get(i).equals("official_lang")){
		if(vLocale.getLanguage().equals("en")){
			%>
			<input type="hidden" name="official_lang" value="English"/>
			<%
		}else if(vLocale.getLanguage().equals("fr")){
			%>
			<input type="hidden" name="official_lang" value="French"/>
			<%
		}
		%>
		<input type="hidden" name="spoken_lang" value=""/>
		<%
	}else if(hidden_demographic_fields.get(i).equals("title")){
		%>
		<input type="hidden" name="title" value=""/>
		<%
	}else if(hidden_demographic_fields.get(i).equals("address")){
		%>
		<input type="hidden" name="address" value="" />
		<%
	}else if(hidden_demographic_fields.get(i).equals("city")){
		%>
		<input type="hidden" name="city" value="<%=defaultCity %>" />
		<%
	}else if(hidden_demographic_fields.get(i).equals("province_hidden")){
		%>
		<input type="hidden" name="province" value="<%=defaultProvince%>"/>
		<%
	}else if(hidden_demographic_fields.get(i).equals("province")){
			
		if (vLocale.getCountry().equals("BR")) { 
			 %> <input type="hidden" name="province" value="<%=props.getProperty("billregion", "ON")%>"/><%
		} else { 
			 %> <input type="hidden" name="province" value="<%=defaultProvince%>"/><%
		}
	}else if(hidden_demographic_fields.get(i).equals("postal")){
		%>
		<input type="hidden" name="postal" value="">
		<%
	}else if(hidden_demographic_fields.get(i).equals("phone")){
		%>
		<input type="hidden" name="phone" value="<%=props.getProperty("phoneprefix", "905-")%>" />
		<input type="hidden" name="hPhoneExt" value="" />
		<%
	}else if(hidden_demographic_fields.get(i).equals("phone2")){
		%>
		<input type="hidden" name="phone2" value=""/>
		<input type="hidden" name="wPhoneExt" value="" />
		<%
	}else if(hidden_demographic_fields.get(i).equals("cellphone")){
		%>
		<input type="hidden" name="cellphone" value=""/>
		<%
	}else if(hidden_demographic_fields.get(i).equals("newsletter")){
		%>
		<input type="hidden" name="newsletter" value="Unknown"/>
		<%
	}else if(hidden_demographic_fields.get(i).equals("email")){
		%>
		<input type="hidden" name="email" value=""/>
		<%
	}else if(hidden_demographic_fields.get(i).equals("pin")){
		%>
		<input type="hidden" name="pin" value="">
		<%
	}else if(hidden_demographic_fields.get(i).equals("dob")){
		%>
		<input type="hidden" name="year_of_birth" value="">
		<input type="hidden" name="month_of_birth" value="">
		<input type="hidden" name="date_of_birth" value="">
		<%
	}else if(hidden_demographic_fields.get(i).equals("sex")){
		// Determine if curUser has selected a default sex in preferences
        UserProperty sexProp = userPropertyDAO.getProp(curUser_no,  UserProperty.DEFAULT_SEX);
        String sex = "";
        if (sexProp != null) {
            sex = sexProp.getValue();
        } else {
            // Access defaultsex system property
            sex = props.getProperty("defaultsex","");
        }
     %>
     	<input type="hidden" name="sex" value="<%=sex%>">
		<%
	}else if(hidden_demographic_fields.get(i).equals("hin")){
		%>
		<input type="hidden" name="hin" value=""/>
		<input type="hidden" name="ver" value=""/>
		<%
	}else if( hidden_demographic_fields.get(i).equals("hc_type") ){
		%>
		<input type="hidden" name="hc_type" value="<%=HCType%>"/>
		<%
	
	}else if(hidden_demographic_fields.get(i).equals("eff_date")){
		%>
		<input type="hidden" name="eff_date_year" value=""/>
		<input type="hidden" name="eff_date_month" value=""/>
		<input type="hidden" name="eff_date_date" value=""/>
		<%
	}else if(hidden_demographic_fields.get(i).equals("countryOfOrigin")){
		%>
		<input type="hidden" name="countryOfOrigin" value="-1"/>
		<%
	}else if(hidden_demographic_fields.get(i).equals("sin")){
		%>
		<input type="hidden" name="sin" value="">
		<%
	}else if(hidden_demographic_fields.get(i).equals("cytolNum")){
		%>
		<input type="hidden" name="cytolNum" value="">
		<%
	}else if(hidden_demographic_fields.get(i).equals("doctor")){
		%>
		<input type="hidden" name="staff" value=""/>
		<%
	}else if(hidden_demographic_fields.get(i).equals("nurse")){
		%>
		<input type="hidden" name="cust1" value="">
		<%
	}else if(hidden_demographic_fields.get(i).equals("midwife")){
		%>
		<input type="hidden" name="cust4" value="">
		<%
	}else if(hidden_demographic_fields.get(i).equals("resident")){
		%>
		<input type="hidden" name="cust2" value="">
		<%
	}else if(hidden_demographic_fields.get(i).equals("referral_doc")){
		%>
		<input type="hidden" name="r_doctor" value=""/>
		<input type="hidden" name="r_doctor_ohip" value=""/>
		<%
	}else if(hidden_demographic_fields.get(i).equals("roster_status")){
		%>
		<input type="hidden" name="roster_status" value=""/>
		<%
	}else if(hidden_demographic_fields.get(i).equals("date_rostered")){
		%>		
		<input type="hidden" name="hc_renew_date_year" value=""/>
		<input type="hidden" name="hc_renew_date_month" value=""/>
		<input type="hidden" name="hc_renew_date_date" value=""/>
		<%
	}else if(hidden_demographic_fields.get(i).equals("patient_status")){
		%>
		<input type="hidden" name="patient_status" value="AC"/>
		<%
	}else if(hidden_demographic_fields.get(i).equals("chart_no")){
		%>
		<input type="hidden" name="chart_no" value="">
		<%
	}else if(hidden_demographic_fields.get(i).equals("waiting_list")){
		String wLReadonly = "";
		WaitingList wL = WaitingList.getInstance();
		if(!wL.getFound()){
		    wLReadonly = "readonly";
		}
		%>
		<input type="hidden" name="list_id" value="0"/>
		<input type="hidden" name="waiting_list_note" value=""/>
		<input type="hidden" name="waiting_list_referral_date" value=""/>
		<%		
	}else if(hidden_demographic_fields.get(i).equals("date_joined")){
		%>
		<input type="hidden" name="date_joined_date" value="<%=curDay%>"> 
		<input type="hidden" name="date_joined_month" value="<%=curMonth%>"> 
		<input type="hidden" name="date_joined_year" value="<%=curYear%>"> 
		<%
	}else if(hidden_demographic_fields.get(i).equals("end_date")){
		%>
		<input type="hidden" name="end_date_year" value="">
		<input type="hidden" name="end_date_month" value="">
		<input type="hidden" name="end_date_date" value="">
		<% 
	}else if(hidden_demographic_fields.get(i).equals("alert")){
		%>
			<input type="hidden" name="cust3" value="">
		<%
	}else if(hidden_demographic_fields.get(i).equals("form_notes")){
		%>
			<input type="hidden" name="content" value=""/>
		<%
	}
}


// More stuff that we may NEVER use
if (oscarProps.getProperty("EXTRA_DEMO_FIELDS") !=null){
      String fieldJSP = oscarProps.getProperty("EXTRA_DEMO_FIELDS");
      fieldJSP+= ".jsp";
%>
	<jsp:include page="<%=fieldJSP%>" />
<%}

// Stuff underneath the form that seems to only be available to Brazil

//chart_address
if (vLocale.getCountry().equals("BR")) { %>
	<div>
		<label><b><bean:message key="demographic.demographicaddrecordhtm.formChartAddress" />:</b></label>
		<input type="text" name="chart_address" value="">
	</div>
<% }
if (props.isPropertyActive("meditech_id")) { %>
     <div>
         <label><b>Meditech ID:</b></label>
         <input type="text" name="meditech_id" value="">
     </<div>
<% }
%>




<% if(oscarVariables.getProperty("demographicExt") != null) {
    boolean bExtForm = oscarVariables.getProperty("demographicExtForm") != null ? true : false;
    String [] propDemoExtForm = bExtForm ? (oscarVariables.getProperty("demographicExtForm","").split("\\|") ) : null;
	String [] propDemoExt = oscarVariables.getProperty("demographicExt","").split("\\|");
	for(int k=0; k<propDemoExt.length; k=k+2) {
%>
	<div style="background-color: #CCCCFF">
		<label><b><%=propDemoExt[k] %>: </b></label>
		<% if(bExtForm) {
			out.println(propDemoExtForm[k] );
 		} else { %>
 			<input type="text" name="<%=propDemoExt[k].replace(' ', '_') %>" value=""> <% }  %>
			<%=(k+1)<propDemoExt.length?("<b>"+propDemoExt[k+1]+": </b>") : "&nbsp;" %>		
			<% if(bExtForm && (k+1)<propDemoExt.length) {
				out.println(propDemoExtForm[k+1] );
 			} else { %> 
 				<%=(k+1)<propDemoExt.length?"<input type=\"text\" name=\""+propDemoExt[k+1].replace(' ', '_')+"\"  value=''>" : "&nbsp;" %>
		<% 	}  %>
	</div>
<% 	}
}

if(oscarVariables.getProperty("demographicExtJScript") != null) {
	out.println(oscarVariables.getProperty("demographicExtJScript"));
}

%>

<div style="background-color: #CCCCFF;">
	<input type="hidden" name="dboperation"	value="add_record"> 
	<label></label>
	<%if (vLocale.getCountry().equals("BR")) { %>
		<input type="hidden" name="dboperation2" value="add_record_ptbr">
	<%}%>
	<input type="hidden" name="displaymode" value="Add Record">
	<input type="submit" name="submit" value="<bean:message key="demographic.demographicaddrecordhtm.btnAddRecord"/>">
	<input type="button" name="Button" value="<bean:message key="demographic.demographicaddrecordhtm.btnSwipeCard"/>" onclick="window.open('zadddemographicswipe.htm','', 'scrollbars=yes,resizable=yes,width=600,height=300')";>
	<input type="button" name="Button" value="<bean:message key="demographic.demographicaddrecordhtm.btnCancel"/>" onclick=self.close();>
</div>
</form>
<script src="<%=protocol%>www.google.com/jsapi"></script>
<script>
	google.load("jquery", "1");
	google.load("jqueryui", "1");
</script>
<script type="text/javascript">
$(document).ready(function(){
	// AJAX autocomplete referrer doctors 
	$("input[name=r_doctor]").keypress(function(){
		$("input[name=r_doctor]").autocomplete({
	    	source: "../billing/CA/BC/billingReferCodeSearchApi.jsp?name=&name1=&name2=&search=&outputType=json&valueType=name",
	    	select: function( event, ui){
	    		$("input[name=r_doctor_ohip]").val(ui.item.referral_no);
	    	}
		});
	});
	$("input[name=r_doctor_ohip]").keypress(function(){
		$("input[name=r_doctor_ohip]").autocomplete({
	    	source: "../billing/CA/BC/billingReferCodeSearchApi.jsp?name=&name1=&name2=&search=&outputType=json&valueType=",
	    	select: function( event, ui){
	    		$("input[name=r_doctor]").val(ui.item.namedesc);
	    	}
		});
	});	
});
</script>
</body>
</html:html>
