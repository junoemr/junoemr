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

<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%
      String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
      boolean authed=true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_con" rights="w" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect("../../securityError.jsp?type=_con");%>
</security:oscarSec>
<%
if(!authed) {
	return;
}
%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/rewrite-tag.tld" prefix="rewrite"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!-- add for special encounter -->
<%@ taglib uri="http://www.caisi.ca/plugin-tag" prefix="plugin" %>
<%@ taglib uri="/WEB-INF/special_tag.tld" prefix="special" %>
<!-- end -->
<%@ taglib uri="/WEB-INF/oscar-tag.tld" prefix="oscar"%>

<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="org.apache.commons.lang.StringUtils" %>
<%@page import="org.oscarehr.PMmodule.dao.ProgramDao"%>
<%@page import="org.oscarehr.PMmodule.model.Program"%>
<%@page import="org.oscarehr.casemgmt.model.CaseManagementNote"%>
<%@page import="org.oscarehr.casemgmt.model.Issue"%>
<%@page import="org.oscarehr.casemgmt.service.CaseManagementManager"%>
<%@page import="org.oscarehr.common.dao.ConsultationRequestDao"%>
<%@page import="org.oscarehr.common.dao.ConsultationServiceDao"%>
<%@page import="org.oscarehr.common.dao.SiteDao"%>
<%@page import="org.oscarehr.common.dao.UserPropertyDAO"%>
<%@page import="org.oscarehr.common.model.ConsultationRequest"%>
<%@page import="org.oscarehr.common.model.ConsultationServices"%>
<%@page import="org.oscarehr.common.model.Site"%>
<%@page import="org.oscarehr.common.model.UserProperty"%>
<%@page import="org.oscarehr.demographic.dao.DemographicExtDao"%>
<%@page import="org.oscarehr.demographic.model.DemographicExt"%>
<%@page import="org.oscarehr.fax.service.FaxUploadService"%>
<%@page import="org.oscarehr.ui.servlet.ImageRenderingServlet"%>
<%@page import="org.oscarehr.util.DigitalSignatureUtils"%>
<%@page import="org.oscarehr.util.EmailUtilsOld"%>
<%@page import="org.oscarehr.util.LoggedInInfo"%>
<%@page import="org.oscarehr.util.SpringUtils"%>
<%@page import="org.oscarehr.util.WebUtilsOld"%>
<%@page import="org.springframework.web.context.WebApplicationContext" %>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@page import="oscar.OscarProperties"%>
<%@page import="oscar.SxmlMisc"%>
<%@page import="oscar.oscarClinic.ClinicData"%>
<%@page import="oscar.oscarDemographic.data.DemographicData"%>
<%@page import="oscar.oscarEncounter.oscarConsultationRequest.pageUtil.EctConsultationFormRequestForm" %>
<%@page import="oscar.oscarEncounter.oscarConsultationRequest.pageUtil.EctConsultationFormRequestUtil" %>
<%@page import="oscar.oscarEncounter.oscarConsultationRequest.pageUtil.EctViewRequestAction"%>
<%@page import="oscar.oscarRx.data.RxProviderData" %>
<%@page import="oscar.oscarRx.data.RxProviderData.Provider" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.GregorianCalendar" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="oscar.util.ConversionUtils" %>
<%@ page import="static org.caisi.comp.web.WebComponentUtil.getServletContext" %>
<jsp:useBean id="displayServiceUtil" scope="request" class="oscar.oscarEncounter.oscarConsultationRequest.config.pageUtil.EctConDisplayServiceUtil" />

<html:html locale="true">

<%! boolean bMultisites=org.oscarehr.common.IsPropertiesOn.isMultisitesEnable(); %>

<%
	LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);

	DemographicExtDao demographicExtDao = (DemographicExtDao) SpringUtils.getBean("demographicExtDao");
	FaxUploadService faxUploadService = SpringUtils.getBean(FaxUploadService.class);
	boolean faxEnabled = faxUploadService.isOutboundFaxEnabled();

	displayServiceUtil.estSpecialist();

	//multi-site support
	String appNo = request.getParameter("appNo");
	appNo = (appNo == null ? "" : appNo);

	String multisiteLetterheadSelectionName = "Referring Site Name";
	String defaultSiteName = "";
	Integer defaultSiteId = 0;
	List<Site> sites = new ArrayList<Site>();
	if(bMultisites)
	{
		SiteDao siteDao = (SiteDao) WebApplicationContextUtils.getWebApplicationContext(application).getBean("siteDao");

		sites = siteDao.getActiveSitesByProviderNo((String) session.getAttribute("user"));
		if(!appNo.isEmpty())
		{
			defaultSiteName = siteDao.getSiteNameByAppointmentNo(appNo);
		}
	}

	String demo = request.getParameter("de");
	if (demo == null)
	{
		demo = (String)request.getAttribute("demo");
	}

	String requestId = request.getParameter("requestId");
	if (requestId == null || requestId.isEmpty() || requestId.equals("null"))
	{
		requestId = (String)request.getAttribute("reqId");
	}

	boolean isNewConsultation = (requestId == null);
	// segmentId is != null when viewing a remote consultation request from an hl7 source
	String segmentId = request.getParameter("segmentId");
	String team = request.getParameter("teamVar");
	String providerNo = (String) session.getAttribute("user");
	String providerNoFromChart = null;
	DemographicData demoData = null;
	org.oscarehr.common.model.Demographic demographic = null;

	RxProviderData rx = new RxProviderData();
	List<Provider> prList = rx.getAllProviders();
	ClinicData clinic = new ClinicData();

	EctConsultationFormRequestUtil consultUtil = new EctConsultationFormRequestUtil();

	if(requestId != null)
	{
		consultUtil.estRequestFromId(loggedInInfo, requestId);
	}

	if(demo == null)
	{
		demo = consultUtil.demoNo;
	}

	ArrayList<String> users = (ArrayList<String>) session.getServletContext().getAttribute("CaseMgmtUsers");
	boolean useNewCmgmt = false;
	WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
	CaseManagementManager cmgmtMgr = null;
	if(users != null && users.size() > 0 && (users.get(0).equalsIgnoreCase("all") || Collections.binarySearch(users, providerNo) >= 0))
	{
		useNewCmgmt = true;
		cmgmtMgr = (CaseManagementManager) ctx.getBean("caseManagementManager");
	}

	UserPropertyDAO userPropertyDAO = (UserPropertyDAO) ctx.getBean("UserPropertyDAO");
	UserProperty fmtProperty = userPropertyDAO.getProp(providerNo, UserProperty.CONSULTATION_REQ_PASTE_FMT);
	String pasteFmt = fmtProperty != null ? fmtProperty.getValue() : null;


	if(demo != null)
	{
		demoData = new oscar.oscarDemographic.data.DemographicData();
		demographic = demoData.getDemographic(loggedInInfo, demo);
		consultUtil.estPatient(loggedInInfo, demo);

		providerNoFromChart = demographic.getProviderNo();
	}

	consultUtil.estActiveTeams();

	boolean validPatientEmail = false;
	if(demographic != null)
	{
		validPatientEmail = EmailUtilsOld.isValidEmailAddress(demographic.getEmail());
	}

	if (request.getParameter("error") != null)
	{
		String errorToDisplay = (String)request.getAttribute("errorMessage");
%>
<script type="text/javascript">
	alert("The form could not be printed due to the following error:\n<%=errorToDisplay%>");
	window.location.replace("<%=request.getContextPath()%>/oscarEncounter/ViewRequest.do?de=<%=demo%>&requestId=<%=requestId%>");
</script>
<%
	}
	  	LocalDate localDate = LocalDate.now();
		String formattedDate = ConversionUtils.toDateString(localDate);

		OscarProperties props = OscarProperties.getInstance();
		ConsultationServiceDao consultationServiceDao = SpringUtils.getBean(ConsultationServiceDao.class);
%><head>
<c:set var="ctx" value="${pageContext.request.contextPath}" scope="request"/>
<script>
	var ctx = '<%=request.getContextPath()%>';
	var requestId = '<%=requestId%>';
	var demographicNo = '<%=demo%>';
	var demoNo = '<%=demo%>';
	var appointmentNo = '<%=appNo%>';

	//JS multisite object
	function Multisite(id, name, address, city, province, postal, phone, fax, bgColour)
	{
		this.id = id;
		this.name = name;
		this.address = address;
		this.bgColour = bgColour;
		this.fax = fax;
		this.phone = phone;
		this.postal = postal;
		this.city = city;
		this.province = province;
	}

	// multisite info
	var selectedMultisiteIndex = 0;
	var multisite = [];
	var siteIndex = 0;
	var multisiteSpecialVal = "multisite";
	<%
	// Sites is empty list if multisites disabled
	for(Site site:sites)
	{%>
		multisite[siteIndex] = new Multisite("<%=site.getSiteId()%>", "<%=site.getName()%>", "<%=site.getAddress()%>",
			"<%=site.getCity()%>", "<%=site.getProvince()%>", "<%=site.getPostal()%>", "<%=site.getPhone()%>", "<%=site.getFax()%>", "<%=site.getBgColor()%>");
		siteIndex++;
	<%
	} %>
</script>
	<script src="<c:out value="${ctx}/share/javascript/jquery/jquery-2.2.4.min.js"/>"></script>

<script type="text/javascript" src="<%= request.getContextPath() %>/js/util/date.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/util/fax.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery_oscar_defaults.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/moment.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/share/javascript/prototype.js"></script>
<link rel="stylesheet" type="text/css" media="all"
	href="<%=request.getContextPath()%>/share/calendar/calendar.css" title="win2k-cold-1" />
<!-- main calendar program -->
<script type="text/javascript" src="<%=request.getContextPath()%>/share/calendar/calendar.js"></script>
<!-- language for the calendar -->
<script type="text/javascript"
	src="<%=request.getContextPath()%>/share/calendar/lang/calendar-en.js"></script>
<!-- the following script defines the Calendar.setup helper function, which makes
       adding a calendar a matter of 1 or 2 lines of code. -->
<script type="text/javascript"
	src="<%=request.getContextPath()%>/share/calendar/calendar-setup.js"></script>

<!-- featherlight is used for emailing code -->
<script src="//cdn.rawgit.com/noelboss/featherlight/1.2.3/release/featherlight.min.js" type="text/javascript" charset="utf-8"></script>
<link href="//cdn.rawgit.com/noelboss/featherlight/1.2.3/release/featherlight.min.css" type="text/css" rel="stylesheet" title="Featherlight Styles" />


   <script>
     jQuery.noConflict();
   </script>


	<oscar:customInterface section="conreq"/>

<title><bean:message
	key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.title" />
</title>
<html:base />
<style type="text/css">

/* Used for "import from enctounter" button */
input.btn{
   color:black;
   font-family:'trebuchet ms',helvetica,sans-serif;
   font-size:84%;
   font-weight:bold;
   background-color:#B8B8FF;
   border:1px solid;
   border-top-color:#696;
   border-left-color:#696;
   border-right-color:#363;
   border-bottom-color:#363;
}

.doc {
    color:blue;
}

.lab {
    color: #CC0099;
	word-break: break-word;
}

.eform {
	color: #008000;
	word-break: break-word;
}
td.tite {

background-color: #bbbbFF;
color : black;
font-size: 12pt;

}

td.tite1 {

background-color: #ccccFF;
color : black;
font-size: 12pt;

}

th,td.tite2 {

background-color: #BFBFFF;
color : black;
font-size: 12pt;

}

td.tite3 {

background-color: #B8B8FF;
color : black;
font-size: 12pt;

}

td.tite4 {

background-color: #ddddff;
color : black;
font-size: 12pt;

}

td.stat{
font-size: 10pt;
}

input.righty{
text-align: right;
}

#faxRecipients .autoAdded {
	display: none;
}
</style>
</head>



<link type="text/javascript" src="../consult.js" />

<script language="JavaScript" type="text/javascript">

var featherlight = null;
var servicesName = new Object();   		// used as a cross reference table for name and number
var services = new Array();				// the following are used as a 2D table for makes and models
var specialists = new Array();
<%oscar.oscarEncounter.oscarConsultationRequest.config.data.EctConConfigurationJavascriptData configScript;
				configScript = new oscar.oscarEncounter.oscarConsultationRequest.config.data.EctConConfigurationJavascriptData();
				out.println(configScript.getJavascript());%>

// initialize anything that needs it when the document has loaded
function onDocumentLoad()
{
	<%
	// intitalize to the correct site and set the letterhead
	if(bMultisites)
	{ %>
		updateSelectedMultisite(document.getElementById("siteName"));
	<%
	}

	// update fax button state (enabled/disabled) on load
	if (faxEnabled)
	{ %>
	Oscar.Util.Fax.updateFaxButton();
	<%
	} %>
}

jQuery(document).ready(onDocumentLoad);

/////////////////////////////////////////////////////////////////////
function initMaster() {
	makeSpecialistslist(2);
}
//-------------------------------------------------------------------

/////////////////////////////////////////////////////////////////////
// create car make objects and fill arrays
//==========
function K( serviceNumber, service ){

	//servicesName[service] = new ServicesName(serviceNumber);
        servicesName[service] = serviceNumber;
	services[serviceNumber] = new Service( );
}
//-------------------------------------------------------------------

//-----------------disableDateFields() disables date fields if "Patient Will Book" selected
var disableFields=false;

function disableDateFields(){
	if(document.forms[0].patientWillBook.checked){
		setDisabledDateFields(document.forms[0], true);
	}
	else{
		setDisabledDateFields(document.forms[0], false);
	}
}

function setDisabledDateFields(form, disabled)
{
	//form.appointmentYear.disabled = disabled;
	//form.appointmentMonth.disabled = disabled;
	//form.appointmentDay.disabled = disabled;
	form.appointmentHour.disabled = disabled;
	form.appointmentMinute.disabled = disabled;
	form.appointmentPm.disabled = disabled;
}

function disableEditing()
{
	if (disableFields)
	{
		form=document.forms[0];

		setDisabledDateFields(form, disableFields);

		form.status[0].disabled = disableFields;
		form.status[1].disabled = disableFields;
		form.status[2].disabled = disableFields;
		form.status[3].disabled = disableFields;

		form.referalDate.disabled = disableFields;
		form.service.disabled = disableFields;
		form.urgency.disabled = disableFields;
		form.phone.disabled = disableFields;
		form.fax.disabled = disableFields;
		form.address.disabled = disableFields;
		form.patientWillBook.disabled = disableFields;
		form.sendTo.disabled = disableFields;

		form.appointmentNotes.disabled = disableFields;
		form.reasonForConsultation.disabled = disableFields;
		form.clinicalInformation.disabled = disableFields;
		form.concurrentProblems.disabled = disableFields;
		form.currentMedications.disabled = disableFields;
		form.allergies.disabled = disableFields;
                form.annotation.disabled = disableFields;

		disableIfExists(form.update, disableFields);
		disableIfExists(form.updateAndPrint, disableFields);
		disableIfExists(form.updateAndSendElectronically, disableFields);
    disableIfExists(form.updateAndEmailDetails, disableFields);
		disableIfExists(form.updateAndFax, disableFields);

		disableIfExists(form.submitSaveOnly, disableFields);
		disableIfExists(form.submitAndPrint, disableFields);
		disableIfExists(form.submitAndSendElectronically, disableFields);
		disableIfExists(form.submitAndFax, disableFields);
	}
}

function disableIfExists(item, disabled)
{
	if (item!=null) item.disabled=disabled;
}

//------------------------------------------------------------------------------------------
/////////////////////////////////////////////////////////////////////
// create car model objects and fill arrays
//=======
function D( servNumber, specNum, phoneNum ,SpecName,SpecFax,SpecAddress, specRefNo){
    var specialistObj = new Specialist(servNumber,specNum,phoneNum, SpecName, SpecFax, SpecAddress, specRefNo);
    services[servNumber].specialists.push(specialistObj);
}
//-------------------------------------------------------------------

/////////////////////////////////////////////////////////////////////
function Specialist(makeNumber,specNum,phoneNum,SpecName, SpecFax, SpecAddress, specRefNo){

	this.specId = makeNumber;
	this.specNbr = specNum;
	this.phoneNum = phoneNum;
	this.specName = SpecName;
	this.specFax = SpecFax;
	this.specAddress = SpecAddress;
	this.specRefNo = specRefNo;
}
//-------------------------------------------------------------------

/////////////////////////////////////////////////////////////////////
// make name constructor
function ServicesName( makeNumber ){

	this.serviceNumber = makeNumber;
}
//-------------------------------------------------------------------

/////////////////////////////////////////////////////////////////////
// make constructor
function Service(  ){

	this.specialists = new Array();
}
//-------------------------------------------------------------------

/////////////////////////////////////////////////////////////////////
// construct model selection on page
function fillSpecialistSelect( aSelectedService ){


	var selectedIdx = aSelectedService.selectedIndex;
	var makeNbr = (aSelectedService.options[ selectedIdx ]).value;

	document.EctConsultationFormRequestForm.specialist.options.selectedIndex = 0;
	document.EctConsultationFormRequestForm.specialist.options.length = 1;

	document.EctConsultationFormRequestForm.phone.value = ("");
	document.EctConsultationFormRequestForm.fax.value = ("");
	document.EctConsultationFormRequestForm.address.value = ("");

	if ( selectedIdx == 0)
	{
		return;
	}

        var i = 1;
	var specs = (services[makeNbr].specialists);
	for ( var specIndex = 0; specIndex < specs.length; ++specIndex ){
		   aPit = specs[ specIndex ];	   	
           document.EctConsultationFormRequestForm.specialist.options[ i++ ] = new Option( aPit.specName , aPit.specNbr );
	}

}
//-------------------------------------------------------------------

/////////////////////////////////////////////////////////////////////
function fillSpecialistSelect1( makeNbr )
{
	//document.EctConsultationFormRequestForm.specialist.options.length = 1;

	var specs = (services[makeNbr].specialists);
	var i=1;
    var match = false;
        
	for ( var specIndex = 0; specIndex < specs.length; ++specIndex )
	{
		aPit = specs[specIndex];

		if(aPit.specNbr=="<%=consultUtil.specialist%>"){
			//look for matching specialist on spec list and make option selected
			match=true;
			document.EctConsultationFormRequestForm.specialist.options[i] = new Option(aPit.specName, aPit.specNbr,false ,true );
		}else{
			//add specialist on list as normal
			document.EctConsultationFormRequestForm.specialist.options[i] = new Option(aPit.specName, aPit.specNbr );
		}

		i++;
	}

	<%if(requestId!=null){ %>
		if(!match){ 
			//if no match then most likely doctor has been removed from specialty list so just add specialist
			document.EctConsultationFormRequestForm.specialist.options[0] = new Option(
				"<%=StringEscapeUtils.escapeJavaScript(consultUtil.getSpecailistsName(consultUtil.specialist))%>",
				"<%=consultUtil.specialist%>",false ,true );

			//don't display if no consultant was saved
			<%if(!StringUtils.trimToEmpty(consultUtil.specialist).equals("null")){%>
			document.getElementById("consult-disclaimer").style.display='inline';
			<%}else{%>
			//display so user knows why field is empty
			document.EctConsultationFormRequestForm.specialist.options[0] = new Option("No Consultant Saved", "-1");
			<%}%>
		}
	<%}%>

}
//-------------------------------------------------------------------

/////////////////////////////////////////////////////////////////////
function setSpec(servNbr,specialNbr){
//    //window.alert("get Called");
    specs = (services[servNbr].specialists);
//    //window.alert("got specs");
    var i=1;
    var NotSelected = true;
 
    for ( var specIndex = 0; specIndex < specs.length; ++specIndex ){
//      //  window.alert("loop");
        aPit = specs[specIndex];
        if (aPit.specNbr == specialNbr){
//        //    window.alert("if");
            document.EctConsultationFormRequestForm.specialist.options[i].selected = true;
            NotSelected = false;
        }

        i++;
    }

    if( NotSelected )
        document.EctConsultationFormRequestForm.specialist.options[0].selected = true;
//    window.alert("exiting");

}
//=------------------------------------------------------------------

/////////////////////////////////////////////////////////////////////
//insert first option title into specialist drop down list select box
function initSpec() {
	<%if(requestId==null){ %>
	var aSpecialist = services["-1"].specialists[0];
    document.EctConsultationFormRequestForm.specialist.options[0] = new Option(aSpecialist.specNbr, aSpecialist.specId);
    <%}%>
}

/////////////////////////////////////////////////////////////////////
function initService(ser,name,spec,specname,phone,fax,address,specReferralNum){
	var i = 0;
	var isSel = 0;
	var strSer = new String(ser);
	var strNa = new String(name);
	var strSpec = new String(spec);
	var strSpecNa = new String(specname);
	var strPhone = new String(phone);
	var strFax = new String(fax);
	var strAddress = new String(address);
	var strReferralNum = new String(specReferralNum);

	var isSerDel=1;//flagging service if deleted: 1=deleted 0=active

	$H(servicesName).each(function(pair){
	if( pair.value == strSer ) {
	isSerDel = 0;
	}
	});

	if (isSerDel==1 && strSer != "null") {
	K(strSer,strNa);
	D(strSer,strSpec,strPhone,strSpecNa,strFax,strAddress,strReferralNum);
    }

        $H(servicesName).each(function(pair){
              var opt = new Option( pair.key, pair.value );
              if( pair.value == strSer ) {
                opt.selected = true;
                fillSpecialistSelect1( pair.value );
              }
              $("service").options.add(opt);

        });

/*	for (aIdx in servicesName){
	   var serNBR = servicesName[aIdx].serviceNumber;
   	   document.EctConsultationFormRequestForm.service.options[ i ] = new Option( aIdx, serNBR );
	   if (serNBR == strSer){
	      document.EctConsultationFormRequestForm.service.options[ i ].selected = true;
	      isSel = 1;
          //window.alert("get here"+serNBR);
	      fillSpecialistSelect1( serNBR );
          //window.alert("and here");
	   }
	   if (isSel != 1){
	      document.EctConsultationFormRequestForm.service.options[ 0 ].selected = true;
	   }
	   i++;
	}*/
	}
//-------------------------------------------------------------------

/////////////////////////////////////////////////////////////////////
function onSelectSpecialist(SelectedSpec)	{
	var selectedIdx = SelectedSpec.selectedIndex;
	var form=document.EctConsultationFormRequestForm;

	<% if (faxEnabled)
	{ %>
		jQuery("#faxRecipients").find(".autoAdded").remove();
	<% } %>


	if (selectedIdx==null || selectedIdx==-1 || (SelectedSpec.options[ selectedIdx ]).value == "-1") {   		//if its the first item set everything to blank
		form.phone.value = ("");
		form.fax.value = ("");
		form.address.value = ("");

		enableDisableRemoteReferralButton(form, true);

		<%
		if(props.isPropertyActive("enable_consultation_invoice_link"))
		{ %>
			var newLink = jQuery("#invoice_link").attr("data-href");
			jQuery("#invoice_link").attr("href", newLink);
		<%
		} %>

		return;
	}
	var selectedService = document.EctConsultationFormRequestForm.service.value;  				// get the service that is selected now
	var specs = (services[selectedService].specialists); 			// get all the specs the offer this service
    
	// load the text fields with phone fax and address for past consult review even if spec has been removed from service list
	<%if(requestId!=null && !(consultUtil.specialist == null) && !consultUtil.specialist.equals("null")){ %>
	form.phone.value = '<%=StringEscapeUtils.escapeJavaScript(consultUtil.specPhone)%>';
	form.fax.value = '<%=StringEscapeUtils.escapeJavaScript(consultUtil.specFax)%>';					
	form.address.value = '<%=StringEscapeUtils.escapeJavaScript(consultUtil.specAddr) %>';

	//make sure this dislaimer is displayed
	document.getElementById("consult-disclaimer").style.display='inline';
	<%}%>
	
								
        for( var idx = 0; idx < specs.length; ++idx ) {
            aSpeci = specs[idx];									// get the specialist Object for the currently selected spec
            aSpeci.specNbr = (aSpeci.specNbr=="null")? null : aSpeci.specNbr; // hack to prevent searching null string
            if( aSpeci.specNbr == SelectedSpec.value ) {
            	form.phone.value = (aSpeci.phoneNum.replace(null,""));
            	form.fax.value = (aSpeci.specFax.replace(null,""));					// load the text fields with phone fax and address
            	form.address.value = (aSpeci.specAddress.replace(null,""));

       			//since there is a match make sure the dislaimer is hidden
       			document.getElementById("consult-disclaimer").style.display='none';
        	
            	<%
				if (faxEnabled)
				{ %>
					Oscar.Util.Fax.AddFax(aSpeci.specName, aSpeci.specFax.trim())
        		<% }

				if(props.isPropertyActive("enable_consultation_invoice_link"))
				{ %>
					var newLink = jQuery("#invoice_link").attr("data-href") + "&referral_no_1=" + encodeURIComponent(aSpeci.specRefNo);
					jQuery("#invoice_link").attr("href", newLink);
				<%
				} %>
            	
            	jQuery.getJSON("getProfessionalSpecialist.json", {id: aSpeci.specNbr},
                    function(xml)
                    {
                		var hasUrl=xml.eDataUrl!=null&&xml.eDataUrl!="";
                		enableDisableRemoteReferralButton(form, !hasUrl);

                                var annotation = document.getElementById("annotation");
                                annotation.value = xml.annotation;
                	}
            	);

            	break;
            }
        }//spec loop

	<% if (faxEnabled)
	{ %>
		Oscar.Util.Fax.updateFaxButton();
	<% } %>

	}

//-----------------------------------------------------------------

/////////////////////////////////////////////////////////////////////
function FillThreeBoxes(serNbr)	{

	var selectedService = document.EctConsultationFormRequestForm.service.value;  				// get the service that is selected now
	var specs = (services[selectedService].specialists);					// get all the specs the offer this service

        for( var idx = 0; idx < specs.length; ++idx ) {
            aSpeci = specs[idx];									// get the specialist Object for the currently selected spec
            if( aSpeci.specNbr == serNbr ) {
                document.EctConsultationFormRequestForm.phone.value = (aSpeci.phoneNum);
                document.EctConsultationFormRequestForm.fax.value = (aSpeci.specFax);					// load the text fields with phone fax and address
                document.EctConsultationFormRequestForm.address.value = (aSpeci.specAddress);
                <%
        		if (faxEnabled) {//
				%>
					Oscar.Util.Fax.AddFax(aSpeci.specName, aSpeci.specFax.trim());
					Oscar.Util.Fax.updateFaxButton();
				<% } %>
                break;
           }
        }
}
//-----------------------------------------------------------------

function enableDisableRemoteReferralButton(form, disabled)
{
	var button=form.updateAndSendElectronically;
	if (button!=null) button.disabled=disabled;
	button=form.submitAndSendElectronically;
	if (button!=null) button.disabled=disabled;

        var button=form.updateAndSendElectronicallyTop;
	if (button!=null) button.disabled=disabled;
	button=form.submitAndSendElectronicallyTop;
	if (button!=null) button.disabled=disabled;
}

//-->

function BackToOscar() {
       window.close();
}
function rs(n,u,w,h,x){
	args="width="+w+",height="+h+",resizalbe=yes,scrollbars=yes,status=0,top=60,left=30";
        remote=window.open(u,n,args);
        if(remote != null){
	   if (remote.opener == null)
		remote.opener = self;
	}
	if ( x == 1 ) { return remote; }
}

var DocPopup = null;
function popup(location) {
    DocPopup = window.open(location,"_blank","height=800,width=1280");

    if (DocPopup != null) {
        if (DocPopup.opener == null) {
            DocPopup.opener = self;
        }
    }
}

function popupAttach( height, width, url, windowName){
  var page = url;
  windowprops = "height="+height+",width="+width+",location=no,scrollbars=yes,menubars=no,toolbars=no,resizable=yes,screenX=0,screenY=0,top=0,left=0";
  var popup=window.open(url, windowName, windowprops);
  if (popup != null){
    if (popup.opener == null){
      popup.opener = self;
    }
  }
  popup.focus();
  return false;
}

function popupOscarCal(vheight,vwidth,varpage) { //open a new popup window
  var page = varpage;
  windowprops = "height="+vheight+",width="+vwidth+",location=no,scrollbars=no,menubars=no,toolbars=no,resizable=no,screenX=0,screenY=0,top=20,left=20";
  var popup=window.open(varpage, "<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.msgCal"/>", windowprops);

  if (popup != null) {
    if (popup.opener == null) {
      popup.opener = self;
    }
  }
}
function sendEmail(type)
{
	var box_id = null
		if(type == 'patient')
		{
			box_id = "#emailFormBoxPatient";
		}
		else if(type == 'provider')
		{
			box_id = "#emailFormBoxProvider";
			chooseEmail();
			jQuery("#emailToProvider").val(jQuery("#emailSelect").find(":selected").val());
			jQuery(".featherlight-content #emailToProvider").val(jQuery("#emailSelect").find(":selected").val());
		}
	jQuery(box_id).show();

	if(featherlight == null)
{
		jQuery.isFunction("featherlight");
		featherlight = jQuery.featherlight(box_id,{closeOnClick:false, closeOnEsc:false});
	}
	else
	{
		featherlight.open();
	}
}


function checkForm(submissionVal,formName){

    jQuery("#emailSubject").val(jQuery(".featherlight-content #emailSubjectForm").val());
    jQuery("#patientEmailBody").val(jQuery(".featherlight-content #patientEmailBodyForm").val());
    jQuery("#providerEmailBody").val(jQuery(".featherlight-content #providerEmailBodyForm").val());
    jQuery("#emailToProvider").val(jQuery(".featherlight-content #emailToProvider").val());
    jQuery("#emailToPatient").val(jQuery(".featherlight-content #emailToPatient").val());

	//if document attach to consultation is still active user needs to close before submitting
	if (DocPopup != null && !DocPopup.closed)
	{
		alert("Please close Consultation Documents window before proceeding");
		return false;
	}

	var msg = "<bean:message key="Errors.service.noServiceSelected"/>";
	msg = msg.replace('<li>', '');
	msg = msg.replace('</li>', '');
	if (document.EctConsultationFormRequestForm.service.options.selectedIndex == 0)
	{
		alert(msg);
		document.EctConsultationFormRequestForm.service.focus();
		return false;
	}

	var dateElem = document.EctConsultationFormRequestForm.referalDate;
	if (!Oscar.Util.Date.validateDateInput(dateElem, false))
	{
		dateElem.focus();
		alert("<bean:message key="oscarEncounter.oscarConsultationRequest.msgInvalidDate"/>");
		return false;
	}

	$("saved").value = "true";
	document.forms[formName].submission.value = submissionVal;
	document.forms[formName].submit();
	return true;
}

//enable import from encounter
function importFromEnct(reqInfo,txtArea)
{
    var info = "";
    switch( reqInfo )
    {
        case "MedicalHistory":
            <%String value = "";
				if (demo != null)
				{
					if (useNewCmgmt)
					{
						value = listNotes(cmgmtMgr, "MedHistory", providerNo, demo);
					}
					else
					{
						oscar.oscarDemographic.data.EctInformation EctInfo = new oscar.oscarDemographic.data.EctInformation(LoggedInInfo.getLoggedInInfoFromSession(request), demo);
						value = EctInfo.getMedicalHistory();
					}
					if (pasteFmt != null && pasteFmt.equalsIgnoreCase("single"))
					{
						value = StringUtils.trimToEmpty(value).replaceAll("\n", " ").replaceAll("\r", " ");
					}
					value = StringEscapeUtils.escapeJavaScript(value);
					out.println("info = '" + value + "'");
				}%>
             break;
		case "SocialHistory":
		<%if (demo != null)
			{
				if (useNewCmgmt)
				{
					value = listNotes(cmgmtMgr, "SocHistory", providerNo, demo);
				}
				else
				{
					oscar.oscarDemographic.data.EctInformation EctInfo = new oscar.oscarDemographic.data.EctInformation(LoggedInInfo.getLoggedInInfoFromSession(request), demo);
					value = EctInfo.getSocialHistory();
				}
				if (pasteFmt != null && pasteFmt.equalsIgnoreCase("single"))
				{
					value = StringUtils.trimToEmpty(value).replaceAll("\n", " ").replaceAll("\r", " ");
				}
				value = StringEscapeUtils.escapeJavaScript(value);
				out.println("info = '" + value + "'");
			}%>
			break;
          case "ongoingConcerns":
             <%if (demo != null)
				{
					if (useNewCmgmt)
					{
						value = listNotes(cmgmtMgr, "Concerns", providerNo, demo);
					}
					else
					{
						oscar.oscarDemographic.data.EctInformation EctInfo = new oscar.oscarDemographic.data.EctInformation(LoggedInInfo.getLoggedInInfoFromSession(request),demo);
						value = EctInfo.getOngoingConcerns();
					}
					if (pasteFmt != null && pasteFmt.equalsIgnoreCase("single"))
					{
						value = StringUtils.trimToEmpty(value).replaceAll("\n", " ").replaceAll("\r", " ");
					}
					value = StringEscapeUtils.escapeJavaScript(value);
					out.println("info = '" + value + "'");
				}%>
             break;
           case "FamilyHistory":
              <%if (demo != null)
				{
					if (OscarProperties.getInstance().getBooleanProperty("caisi", "on"))
					{
						oscar.oscarDemographic.data.EctInformation EctInfo = new oscar.oscarDemographic.data.EctInformation(LoggedInInfo.getLoggedInInfoFromSession(request),demo);
						value = EctInfo.getFamilyHistory();
					}
					else
					{
						if (useNewCmgmt)
						{
							value = listNotes(cmgmtMgr, "FamHistory", providerNo, demo);
						}
						else
						{
							oscar.oscarDemographic.data.EctInformation EctInfo = new oscar.oscarDemographic.data.EctInformation(LoggedInInfo.getLoggedInInfoFromSession(request),demo);
							value = EctInfo.getFamilyHistory();
						}
					}
					if (pasteFmt != null && pasteFmt.equalsIgnoreCase("single"))
					{
						value = StringUtils.trimToEmpty(value).replaceAll("\n", " ").replaceAll("\r", " ");
					}
					value = StringEscapeUtils.escapeJavaScript(value);
					out.println("info = '" + value + "'");
				}%>
              break;
            case "OtherMeds":
              <%if (demo != null)
				{
					if (OscarProperties.getInstance().getBooleanProperty("caisi", "on"))
					{
						value = "";
					}
					else
					{
						if (useNewCmgmt)
						{
							value = listNotes(cmgmtMgr, "OMeds", providerNo, demo);
						}
						else
						{
							//family history was used as bucket for Other Meds in old encounter
							oscar.oscarDemographic.data.EctInformation EctInfo = new oscar.oscarDemographic.data.EctInformation(LoggedInInfo.getLoggedInInfoFromSession(request),demo);
							value = EctInfo.getFamilyHistory();
						}
					}
					if (pasteFmt != null && pasteFmt.equalsIgnoreCase("single"))
					{
						value = StringUtils.trimToEmpty(value).replaceAll("\n", " ").replaceAll("\r", " ");
					}
					value = StringEscapeUtils.escapeJavaScript(value);
					out.println("info = '" + value + "'");

				}%>
                break;
            case "Reminders":
              <%if (demo != null)
				{
					if (useNewCmgmt)
					{
						value = listNotes(cmgmtMgr, "Reminders", providerNo, demo);
					}
					else
					{
						oscar.oscarDemographic.data.EctInformation EctInfo = new oscar.oscarDemographic.data.EctInformation(LoggedInInfo.getLoggedInInfoFromSession(request),demo);
						value = EctInfo.getReminders();
					}
					//if( !value.equals("") ) {
					if (pasteFmt != null && pasteFmt.equalsIgnoreCase("single"))
					{
						value = StringUtils.trimToEmpty(value).replaceAll("\n", " ").replaceAll("\r", " ");
					}

					value = StringEscapeUtils.escapeJavaScript(value);
					out.println("info = '" + value + "'");
					//}
				}%>
    } //end switch

    if( txtArea.value.length > 0 && info.length > 0 )
        txtArea.value += '\n';

    txtArea.value += info;
    txtArea.scrollTop = txtArea.scrollHeight;
    txtArea.focus();

}


// used by consultation attachments
function updateAttached() {
    var t = setTimeout('fetchAttached()', 2000);
}

function fetchAttached() {
    var updateElem = 'tdAttachedDocs';
    var params = "demo=<%=demo%>&requestId=<%=requestId%>";
    var url = ctx + "/oscarEncounter/displayAttached.do";

    var objAjax = new Ajax.Request (
                url,
                {
                    method: 'get',
                    parameters: params,
                    onSuccess: function(request) {
                                    $(updateElem).innerHTML = request.responseText;
                                },
                    onFailure: function(request) {
                                    $(updateElem).innerHTML = "<h3>Error: " + + request.status + "</h3>";
                                }
                }

            );
}

function addCCName(){
        if (document.EctConsultationFormRequestForm.ext_cc.value.length<=0)
                document.EctConsultationFormRequestForm.ext_cc.value=document.EctConsultationFormRequestForm.docName.value;
        else document.EctConsultationFormRequestForm.ext_cc.value+="; "+document.EctConsultationFormRequestForm.docName.value;
}

</script>


<script>

var providerData = new Object(); //{};
<%
for (Provider p : prList) {
	if (!p.getProviderNo().equalsIgnoreCase("-1")) {
		String prov_no = "prov_"+p.getProviderNo();

		%>
	 providerData['<%=prov_no%>'] = new Object(); //{};

	providerData['<%=prov_no%>'].address = "<%=p.getFullAddress() %>";
	providerData['<%=prov_no%>'].phone = "<%=p.getClinicPhone().trim() %>";
	providerData['<%=prov_no%>'].fax = "<%=p.getClinicFax().trim() %>";

<%	}
}

ProgramDao programDao = (ProgramDao) SpringUtils.getBean("programDao");
List<Program> programList = programDao.getAllActivePrograms();

if (OscarProperties.getInstance().getBooleanProperty("consultation_program_letterhead_enabled", "true")) {
	if (programList != null) {
		for (Program p : programList) {
			String progNo = "prog_" + p.getId();
%>
		providerData['<%=progNo %>'] = new Object();
		providerData['<%=progNo %>'].address = "<%=(p.getAddress() != null && p.getAddress().trim().length() > 0) ? p.getAddress().trim() : ((clinic.getClinicAddress() + "  " + clinic.getClinicCity() + "   " + clinic.getClinicProvince() + "  " + clinic.getClinicPostal()).trim()) %>";
		providerData['<%=progNo %>'].phone = "<%=(p.getPhone() != null && p.getPhone().trim().length() > 0) ? p.getPhone().trim() : clinic.getClinicPhone().trim() %>";
		providerData['<%=progNo %>'].fax = "<%=(p.getFax() != null && p.getFax().trim().length() > 0) ? p.getFax().trim() : clinic.getClinicFax().trim() %>";
<%
		}
	}
} %>

function isLetterheadMultisiteSelected()
{
	return document.getElementById("letterheadName").value === multisiteSpecialVal;
}

function updateSelectedMultisite(element)
{
	// set the background colour
	element.style.backgroundColor = element.options[element.selectedIndex].style.backgroundColor;

	// update the global index
	var siteName = element.value;
	selectedMultisiteIndex = 0;
	for (i = 0; i < multisite.length; i++)
	{
		if (multisite[i].name === siteName)
		{
			selectedMultisiteIndex = i;
			break;
		}
	}
	console.info("set site index " + selectedMultisiteIndex);
	if (isLetterheadMultisiteSelected())
	{
		setLetterheadToMultisiteInfo(selectedMultisiteIndex);
	}
}

function setLetterheadToMultisiteInfo(multisiteIndex)
{

	if (multisite.length <= 0)
	{
		return;
	}
	site = multisite[multisiteIndex];
	console.info("set letterhead to site index " + multisiteIndex);

	var addressline = site.address + " " + site.city + " " + site.province + " " + site.postal;

	/* apparently letterheadName.value is actually a providerNo. Until this is fixed we pass in a special 'multisite' value that is specifically checked for */
	document.getElementById("letterheadName").value = multisiteSpecialVal;
	//document.getElementById("letterheadName").value = site.name;
	document.getElementById("letterheadAddress").value = addressline;
	document.getElementById("letterheadAddressSpan").innerHTML = addressline;
	document.getElementById("letterheadPhone").value = site.phone;
	document.getElementById("letterheadPhoneSpan").innerHTML = site.phone;
	document.getElementById("letterheadFax").value = site.fax;
	document.getElementById("letterheadFaxSpan").innerHTML = site.fax;
}

function switchProvider(value)
{
	if (value === multisiteSpecialVal)
	{
		setLetterheadToMultisiteInfo(selectedMultisiteIndex);
	}
	else if (value == -1)
	{
		document.getElementById("letterheadName").value = value;
		document.getElementById("letterheadAddress").value = "<%=(clinic.getClinicAddress() + "  " + clinic.getClinicCity() + "   " + clinic.getClinicProvince() + "  " + clinic.getClinicPostal()).trim() %>";
		document.getElementById("letterheadAddressSpan").innerHTML = "<%=(clinic.getClinicAddress() + "  " + clinic.getClinicCity() + "   " + clinic.getClinicProvince() + "  " + clinic.getClinicPostal()).trim() %>";
		document.getElementById("letterheadPhone").value = "<%=clinic.getClinicPhone().trim() %>";
		document.getElementById("letterheadPhoneSpan").innerHTML = "<%=clinic.getClinicPhone().trim() %>";
		document.getElementById("letterheadFax").value = "<%=clinic.getClinicFax().trim() %>";
		document.getElementById("letterheadFaxSpan").innerHTML = "<%=clinic.getClinicFax().trim() %>";
	}
	else
	{
		document.getElementById("letterheadName").value = value;
		if (typeof providerData["prov_" + value] != "undefined")
		{
			value = "prov_" + value;
		}
		document.getElementById("letterheadAddress").value = providerData[value]['address'];
		document.getElementById("letterheadAddressSpan").innerHTML = providerData[value]['address'].replace(" ", "&nbsp;");
		document.getElementById("letterheadPhone").value = providerData[value]['phone'];
		document.getElementById("letterheadPhoneSpan").innerHTML = providerData[value]['phone'];
		document.getElementById("letterheadFax").value = providerData[value]['fax'];
		document.getElementById("letterheadFaxSpan").innerHTML = providerData[value]['fax'];

	}
}
</script>
<script type="text/javascript">
<%
String signatureRequestId=DigitalSignatureUtils.generateSignatureRequestId(loggedInInfo.getLoggedInProviderNo());
String imageUrl=request.getContextPath()+"/imageRenderingServlet?source="+ImageRenderingServlet.Source.signature_preview.name()+"&"+DigitalSignatureUtils.SIGNATURE_REQUEST_ID_KEY+"="+signatureRequestId;
String storedImgUrl=request.getContextPath()+"/imageRenderingServlet?source="+ImageRenderingServlet.Source.signature_stored.name()+"&digitalSignatureId=";
%>
var POLL_TIME=1500;
var counter=0;
function refreshImage()
{
	counter=counter+1;
	document.getElementById('signatureImgTag').src='<%=imageUrl%>&rand='+counter;
	document.getElementById('signatureImg').value='<%=signatureRequestId%>';
}

function showSignatureImage()
{
	if (document.getElementById('signatureImg') != null && document.getElementById('signatureImg').value.length > 0) {
		document.getElementById('signatureImgTag').src = "<%=storedImgUrl %>" + document.getElementById('signatureImg').value;

		<% if (OscarProperties.getInstance().getBooleanProperty("topaz_enabled", "true")) {
		  //this is empty
		%>

		document.getElementById('clickToSign').style.display = "none";

		<% } else {
		  //this is empty
		%>

		document.getElementById("signatureFrame").style.display = "none";

		<% } %>


		document.getElementById('signatureShow').style.display = "block";
	}

	return true;
}

function requestSignature()
{

function AddOtherFaxProvider() {
	var selected = jQuery("#otherFaxSelect option:selected");
	_AddOtherFax(selected.text(),selected.val());
}
function AddOtherFax() {
	var number = jQuery("#otherFaxInput").val();
	if (checkPhone(number)) {
		_AddOtherFax(number,number);
	}
	else {
		alert("The fax number you entered is invalid.");
	}
}

	<% if (OscarProperties.getInstance().getBooleanProperty("topaz_enabled", "true")) { %>
	document.getElementById('newSignature').value = "true";
	document.getElementById('signatureShow').style.display = "block";
	document.getElementById('clickToSign').style.display = "none";
	document.getElementById('signatureShow').style.display = "block";
	setInterval('refreshImage()', POLL_TIME);
	document.location='<%=request.getContextPath()%>/signature_pad/topaz_signature_pad.jnlp.jsp?<%=DigitalSignatureUtils.SIGNATURE_REQUEST_ID_KEY%>=<%=signatureRequestId%>';

	<% } %>
}

var isSignatureDirty = false;
var isSignatureSaved = <%= consultUtil.signatureImg != null && !"".equals(consultUtil.signatureImg) ? "true" : "false" %>;

function signatureHandler(e) {
	isSignatureDirty = e.isDirty;
	isSignatureSaved = e.isSave;
	<%
	if (faxEnabled) { //
	%>
	Oscar.Util.Fax.updateFaxButton();
	<% } %>
	if (e.isSave) {
		refreshImage();
		document.getElementById('newSignature').value = "true";
	}
	else {
		document.getElementById('newSignature').value = "false";
	}
}

var requestIdKey = "<%=signatureRequestId %>";
</script>

<%=WebUtilsOld.popErrorMessagesAsAlert(session)%>
<link rel="stylesheet" type="text/css" href="../encounterStyles.css">
<body topmargin="0" leftmargin="0" vlink="#0000FF"
	onload="window.focus();disableDateFields();fetchAttached();disableEditing();showSignatureImage();">
<html:errors />
<html:form action="/oscarEncounter/RequestConsultation"
	onsubmit="alert('HTHT'); return false;">
	<%
		EctConsultationFormRequestForm thisForm = (EctConsultationFormRequestForm)request.getAttribute("EctConsultationFormRequestForm");

		if (requestId != null && !requestId.equals("null"))
		{
			EctViewRequestAction.fillFormValues(LoggedInInfo.getLoggedInInfoFromSession(request), thisForm, new Integer(requestId));
                thisForm.setSiteName(consultUtil.siteName);
                defaultSiteName = consultUtil.siteName ;

		}
		else if (segmentId != null)
		{
			EctViewRequestAction.fillFormValues(thisForm, segmentId);
                thisForm.setSiteName(consultUtil.siteName);
                defaultSiteName = consultUtil.siteName ;
		}
		else if (request.getAttribute("validateError") == null)
		{
			//  new request
			if (demo != null)
			{
				oscar.oscarDemographic.data.RxInformation RxInfo = new oscar.oscarDemographic.data.RxInformation();
                EctViewRequestAction.fillFormValues(thisForm,consultUtil);
				thisForm.setAllergies(RxInfo.getAllergies(loggedInInfo, demo));

				if (props.getProperty("currentMedications", "").equalsIgnoreCase("otherMedications"))
				{
					oscar.oscarDemographic.data.EctInformation EctInfo = new oscar.oscarDemographic.data.EctInformation(LoggedInInfo.getLoggedInInfoFromSession(request),demo);
					thisForm.setCurrentMedications(EctInfo.getFamilyHistory());
				}
				else
				{
					thisForm.setCurrentMedications(RxInfo.getCurrentMedication(demo));
				}

				team = consultUtil.getProviderTeam(consultUtil.mrp);
			}

			thisForm.setStatus("1");

			thisForm.setSendTo(team.toLowerCase());

       		if (bMultisites) {
        		thisForm.setSiteName(defaultSiteName);
       		}
		}

		if (thisForm.iseReferral())
		{
			%>
				<SCRIPT LANGUAGE="JavaScript">
					disableFields=true;
				</SCRIPT>
			<%
		}

		int demographic_no = demographic.getDemographicNo();
		DemographicExt demoExt = demographicExtDao.getLatestDemographicExt(demographic_no, "demo_cell");
		String patientCPhone = (demoExt == null) ? "" : StringUtils.trimToEmpty(demoExt.getValue());
		thisForm.setPatientCPhone(patientCPhone);


	%>

	<input type="hidden" name="demographicNo" value="<%=demo%>">
	<input type="hidden" name="requestId" value="<%=requestId%>">
	<input type="hidden" name="documents" value="">
	<input type="hidden" name="ext_appNo" value="<%=request.getParameter("appNo") %>">
	<input type="hidden" name="source" value="<%=(requestId!=null)?thisForm.getSource():request.getParameter("source") %>">

        <input type="hidden" id="saved" value="false">
	<!--  -->
	<table class="MainTable" id="scrollNumber1" name="encounterTable">
		<tr class="MainTableTopRow">
			<td class="MainTableTopRowLeftColumn">Consultation</td>
			<td class="MainTableTopRowRightColumn">
			<table class="TopStatusBar">
				<tr>
					<td class="Header"
						style="padding-left: 2px; padding-right: 2px; border-right: 2px solid #003399; text-align: left; font-size: 80%; font-weight: bold; width: 100%;"
						NOWRAP><%=thisForm.getPatientName()%> <%=thisForm.getPatientSex()%>	<%=thisForm.getPatientAge()%>
					</td> <%
					if (props.isPropertyActive("enable_consultation_invoice_link"))
					{
						String billingType = props.getBillingType();
						String billingServiceType = URLEncoder.encode(props.getProperty("default_view"));

						String linkProvider = providerNo;
						String service_date_parameter = "";
						// set the invoice date to match the consultation referral date (if all date fields exists)
						if (consultUtil.referalDate != null)
						{
							service_date_parameter = consultUtil.referalDate.replace('/', '-');
						}
						else
						{
							// default todays date
							GregorianCalendar now = new GregorianCalendar();
							int apptYear = now.get(Calendar.YEAR);
							int apptMonth = (now.get(Calendar.MONTH) + 1);
							int apptDay = now.get(Calendar.DAY_OF_MONTH);

							// format date to yyyy-mm-dd
							String strYear = "" + apptYear;
							String strMonth = apptMonth > 9 ? ("" + apptMonth) : ("0" + apptMonth);
							String strDay = apptDay > 9 ? ("" + apptDay) : ("0" + apptDay);

							service_date_parameter = strYear + "-" + strMonth + "-" + strDay;
						}

						if (props.isClinicaidBillingType())
						{
							String clinicaid_link = "../../billing/billingClinicAid.jsp?demographic_no=" + demographic.getDemographicNo() +
									"&service_start_date=" + URLEncoder.encode(service_date_parameter, "UTF-8") +
									"&chart_no=" + demographic.getChartNo() +
									"&appointment_start_time=0" +
									"&appointment_provider_no=" + linkProvider +
									"&billing_action=create_invoice&appointment_no=0";
						%>
							<td NOWRAP>
								<a href="<%=clinicaid_link%>" target="_blank"
						   			title="<bean:message key="demographic.demographiceditdemographic.msgBillPatient"/>">
										<bean:message key="demographic.demographiceditdemographic.msgCreateInvoice"/>
								</a>
							</td>
					<%
						}
						else
						{
							String diagnosticCode1 = props.getProperty("auto_populate_billing_bc_diagnostic_codesVal1_consult", "");
							if (!diagnosticCode1.equals(""))
							{
								diagnosticCode1 = "&diag_code_1=" + diagnosticCode1;
							}
							String otherCode1 = props.getProperty("auto_populate_billing_bc_other_codesVal1_consult", "");
							if (!otherCode1.equals(""))
							{
								otherCode1 = "&other_code_1=" + otherCode1;
							}
							String referralType1 = props.getProperty("auto_populate_billing_bc_billingreferral_type1_consult", "");
							if (!referralType1.equals(""))
							{
								referralType1 = "&referral_type_1=" + referralType1;
							}

							// should not include referral_no_1 parameter ()
							String invoice_link = "../../billing.do?billRegion=" + URLEncoder.encode(billingType) + "&billForm=" + billingServiceType
									+ "&hotclick=&appointment_no=0&demographic_name=" + URLEncoder.encode(demographic.getLastName()) + "%2C"
									+ URLEncoder.encode(demographic.getFirstName()) + "&demographic_no=" + demographic.getDemographicNo() + "&providerview=1&user_no="
									+ providerNo + "&apptProvider_no=none&appointment_date=" + service_date_parameter + "&start_time=0:00&bNewForm=1&status=t"
									+ diagnosticCode1 + otherCode1 + referralType1 + "&from_consult=true";
					%>
							<td NOWRAP align='right'><a id="invoice_link"
													href="<%=invoice_link%>"
													data-href="<%=invoice_link%>" target="_blank"
													title="<bean:message key="demographic.demographiceditdemographic.msgBillPatient"/>"><bean:message
								key="demographic.demographiceditdemographic.msgCreateInvoice"/>
							</a></td>
						<%
						}
					}%>
				</tr>
			</table>
			</td>
		</tr>
		<tr style="vertical-align: top">
			<td class="MainTableLeftColumn">
			<table>
				<tr>
					<td class="tite4" colspan="2">
					<table>
						<tr>
							<td class="stat" colspan="2"><bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.msgCreated" />:</td>
						</tr>
						<tr>
							<td class="stat" colspan="2" align="right" nowrap><%=thisForm.getProviderName()%>
							</td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td class="tite4" colspan="2"><bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.msgStatus" />
					</td>
				</tr>
				<tr>
					<td class="tite4" colspan="2">
					<table>
						<tr>
							<td class="stat"><html:radio property="status" value="1" />
							</td>
							<td class="stat"><bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.msgNoth" />
							</td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td class="tite4" colspan="2">
					<table>
						<tr>
							<td class="stat"><html:radio property="status" value="2" />
							</td>
							<td class="stat"><bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.msgSpecCall" />
							</td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td class="tite4" colspan="2">
					<table>
						<tr>
							<td class="stat"><html:radio property="status" value="3" />
							</td>
							<td class="stat"><bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.msgPatCall" />
							</td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td class="tite4" colspan="2">
					<table>
						<tr>
							<td class="stat"><html:radio property="status" value="4" />
							</td>
							<td class="stat"><bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.msgCompleted" /></td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td class="tite4" colspan="2">
						<table>
							<tr>
								<td class="stat"><html:radio property="status" value="5" />
								</td>
								<td class="stat"><bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.msgCancelled" /></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td class="tite4" colspan="2">
						<table>
							<tr>
								<td class="stat"><html:radio property="status" value="6" />
								</td>
								<td class="stat"><bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.msgApptBooked" /></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td class="tite4" colspan="2">
						<table>
							<tr>
								<td class="stat"><html:radio property="status" value="7" />
								</td>
								<td class="stat"><bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.msgDeleted" /></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td class="tite4" colspan="2">
					<table>
						<tr>
							<td class="stat">&nbsp;</td>
						</tr>
						<tr>
							<td style="text-align: center" class="stat">
							<%
								if (thisForm.iseReferral())
								{
									%>
										<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.attachDoc" />
									<%
								}
								else
								{
									%>
									<a href="#" onclick="popup('<rewrite:reWrite jspPage="attachConsultation.jsp"/>?provNo=<%=consultUtil.providerNo%>&demo=<%=demo%>&requestId=<%=requestId%>');return false;">
										<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.attachDoc" />
									</a>
									<%
								}
							%>
							</td>
						</tr>
						<tr>
							<td style="text-align: center"><bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.curAttachDoc"/>:</td>
						</tr>
						<tr>
							<td id="tdAttachedDocs"></td>
						</tr>
						<tr>
							<td style="text-align: center"><bean:message
								key="oscarEncounter.oscarConsultationRequest.AttachDoc.Legend" /><br />
							<span class="doc"><bean:message
								key="oscarEncounter.oscarConsultationRequest.AttachDoc.LegendDocs" /></span><br />
							<span class="lab"><bean:message
								key="oscarEncounter.oscarConsultationRequest.AttachDoc.LegendLabs" /></span><br />
							<span class="eform"><bean:message
								key="oscarEncounter.oscarConsultationRequest.AttachDoc.LegendEForms" /></span>
							</td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			</td>
			<td class="MainTableRightColumn">
			<table cellpadding="0" cellspacing="2"
				style="border-collapse: collapse" bordercolor="#111111" width="100%"
				height="100%" border=1>

				<!----Start new rows here-->
				<tr>
					<td class="tite4" colspan=2>
					<% if (request.getAttribute("id") != null) { %>
						<input name="update" type="button" value="<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnUpdate"/>" onclick="return checkForm('Update Consultation Request','EctConsultationFormRequestForm');" />
						<input name="updateAndPrint" type="button" value="<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnUpdateAndPrint"/>" onclick="return checkForm('Update Consultation Request And Print Preview','EctConsultationFormRequestForm');" />
						<input name="printPreview" type="button" value="Print Preview" onclick="return checkForm('And Print Preview','EctConsultationFormRequestForm');" />
						<input name="updateAndSendElectronicallyTop" type="button" value="<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnUpdateAndSendElectronicReferral"/>" onclick="return checkForm('Update_esend','EctConsultationFormRequestForm');" />
						<% if (faxEnabled) { %>
						<input id="fax_button" name="updateAndFax" type="button" value="<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnUpdateAndFax"/>" onclick="return checkForm('Update And Fax','EctConsultationFormRequestForm');" />
						<% } %>

            <% if (OscarProperties.getInstance().isPropertyActive("appointment_reminder_enabled")) { %>
              <input type="button"
                <% if(!validPatientEmail) { %> disabled="disabled" <% } %>
                name="updateAndEmailDetails"
                value='<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnUpdateAndEmailAppointmentTime"/>'
                onclick="return checkForm('Update And Email Details','EctConsultationFormRequestForm');" />
						<% } %>
						<%
							if (OscarProperties.getInstance().isPropertyActive("consultation_notification_enabled"))
							{
								boolean disableNotification = !validPatientEmail;
								if (requestId != null)
								{
									ConsultationRequestDao consultationRequestDao = (ConsultationRequestDao) SpringUtils.getBean("consultationRequestDao");
									ConsultationRequest consult = consultationRequestDao.find(Integer.parseInt(requestId));
									disableNotification = disableNotification || consult.isNotificationSent();
						%>
								<input type="button"
								<% if(disableNotification) { %> disabled="disabled" <% } %>
							   id="updateAndEmailNotification" name="updateAndEmailNotification"
							   value="<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnUpdateAndEmailNotification"/>"
							   onclick="return checkForm('Update And Email Notification','EctConsultationFormRequestForm');"/>
						<%
								}
							}
						}
						else
						{ %>
						<input name="submitSaveOnly" type="button" value="<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnSubmit"/>" onclick="return checkForm('Submit Consultation Request','EctConsultationFormRequestForm'); " />
						<input name="submitAndPrint" type="button" value="<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnSubmitAndPrint"/>" onclick="return checkForm('Submit Consultation Request And Print Preview','EctConsultationFormRequestForm'); " />
						<input name="submitAndSendElectronicallyTop" type="button" value="<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnSubmitAndSendElectronicReferral"/>" onclick="return checkForm('Submit_esend','EctConsultationFormRequestForm');" />
						<% if (faxEnabled) { %>
						<input id="fax_button" name="submitAndFax" type="button" value="<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnSubmitAndFax"/>" onclick="return checkForm('Submit And Fax','EctConsultationFormRequestForm');" />
					<%  }
						if (OscarProperties.getInstance().isPropertyActive("consultation_notification_enabled")) {
					%>
						<input type="button"
								<% if(!validPatientEmail) { %> disabled="disabled" <% } %>
							   id="submitAndEmailNotification" name="submitAndEmailNotification"
							   value="<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnSubmitAndEmailNotification"/>"
							   onclick="return checkForm('Submit And Email Notification','EctConsultationFormRequestForm');" />
					<% 	   }

				    }
						if(OscarProperties.getInstance().isPropertyActive("consultation_email_enabled"))
						{
							%>
							<div>
								<%
								if(request.getAttribute("id")!=null){
								%>
								<input id="updateAndEmailPatient" name="updateAndEmailPatient" type="button" value="<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnUpdateAndEmailPatient"/>" onclick="return sendEmail('patient');" />
								<input id="updateAndEmailProvider" name="updateAndEmailProvider" type="button" value="<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnUpdateAndEmailProvider"/>" onclick="return sendEmail('provider');" />
								<%
								}else{
								%>
								<input id="submitAndEmailPatient" name="submitAndEmailPatient" type="button" value="<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnSubmitAndEmailPatient"/>" onclick="return sendEmail('patient');" />
								<input id="submitAndEmailProvider" name="submitAndEmailProvider" type="button" value="<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnSubmitAndEmailProvider"/>" onclick="return sendEmail('provider');" />
							    <%}

								// Get email addresses
								displayServiceUtil.estSpecialist();
								String rdohip = "";
								if (!"".equals(demo))
								{
								 rdohip = SxmlMisc.getXmlContent(StringUtils.trimToEmpty(demographic.getFamilyDoctor()),"rdohip");
								 rdohip = SxmlMisc.getXmlContent(demographic.getFamilyDoctor(),"rdohip").trim();
								}

								%>
								<div style="display: inline-block;">
									Provider:
									<select id="emailSelect" name="emailSelect" onchange="chooseEmail()">
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
										<option value="<%=email%>"><%= String.format("%s, %s &lt;%s&gt;", lName, fName, email) %> </option>
										<%
										}
									} %>
									</select>

									<input value="" name="toProviderName" id="toProviderName" type="hidden" />
									<input value="" name="toProviderEmail" id="toProviderEmail" type="hidden" />
									<input name="providerEmailBody" id="providerEmailBody" type="hidden" value="<%= props.getProperty("eform_email_text_providers") %>" />

									<input value="<%=demographic.getDisplayName()%>" name="toPatientName" id="toPatientName" type="hidden" />
									<input value="<%=demographic.getEmail()%>" name="toPatientEmail" id="toPatientEmail" type="hidden" />
									<input name="patientEmailBody" id="patientEmailBody" type="hidden" value="<%= props.getProperty("eform_email_text_patients") %>" />

									<input name="emailSubject" id="emailSubject" type="hidden" value="<%= props.getProperty("eform_email_subject") %>" />


								</div>
							</div>
							<%
					   }
					   if (thisForm.iseReferral()) { %>
						<input type="button" value="Send eResponse" onclick="$('saved').value='true';document.location='<%=thisForm.getOruR01UrlString(request)%>'" />
					<% } %>
					</td>
                                </tr>
                    <tr>
					<td>

					<table border=0 width="100%">
						<tr>
							<td class="tite4"><bean:message key="oscarEncounter.oscarConsultationRequest.consultationFormPrint.msgAssociated2" />:</td>
							<td align="right" class="tite1">
								<html:select property="providerNo">
									<%
										for (Provider p : prList) {
											if (p.getProviderNo().compareTo("-1") != 0) {
									%>
									<option value="<%=p.getProviderNo() %>" <%=((consultUtil.providerNo != null && consultUtil.providerNo.equalsIgnoreCase(p.getProviderNo())) || (consultUtil.providerNo == null &&  providerNo.equalsIgnoreCase(p.getProviderNo())) ? "selected='selected'" : "") %>>
										<%=p.getFirstName() %> <%=p.getSurname() %>
									</option>
									<% }

								}
								%>
								</html:select>
							</td>
						</tr>
						<tr>
							<td class="tite4"><bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.formRefDate" />:
							</td>
							<td align="right" class="tite1">
							<%
								if (request.getAttribute("id") != null)
										{
							%> <html:text styleClass="righty" property="referalDate" /> <%
 	}
 			else
 			{
 %> <html:text styleClass="righty" property="referalDate" value="<%=formattedDate%>" /> <%
 	}
 %>
							</td>
						</tr>
						<tr>
							<td class="tite4"><bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.formService" />:
							</td>
							<td align="right" class="tite1">
								<html:select styleId="service" property="service" onchange="fillSpecialistSelect(this);">
								<!-- <option value="-1">------ <bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.formServSelect"/> ------</option>
					<option/>
				    	<option/>
			    		<option/>
		    			<option/> -->
							</html:select></td>
						</tr>
						<tr>
							<td class="tite4"><bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.formCons" />:
							</td>
							<td align="right" class="tite2">
							<%
								if (thisForm.iseReferral())
								{
									%>
										<%=thisForm.getProfessionalSpecialistName()%>
									<%
								}
								else
								{
									%>

									<span id="consult-disclaimer" title="When consult was saved this was the saved consultant but is no longer on this specialist list." style="display:none;font-size:24px;">*</span>
									<html:select styleId="specialist" property="specialist" size="1" onchange="onSelectSpecialist(this)">
									</html:select>
									<%
								}
							%>
							</td>
						</tr>
                                                <tr>
                                                    <td class="tite4">
                                                        <bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.formInstructions" />
                                                    </td>
                                                    <td align="right" class="tite2">
                                                        <textarea id="annotation" style="color: blue;" readonly></textarea>
                                                    </td>
                                                </tr>
						<tr>
							<td class="tite4"><bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.formUrgency" /></td>
							<td align="right" class="tite2">
								<html:select property="urgency">
									<html:option value="2">
										<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.msgNUrgent" />
									</html:option>
									<html:option value="1">
										<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.msgUrgent" />
									</html:option>
									<html:option value="3">
										<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.msgReturn" />
									</html:option>
								</html:select>
							</td>
						</tr>
						<tr>
							<td class="tite4"><bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.formPhone" />:
							</td>
							<td align="right" class="tite2"><input readonly="readonly" type="text" name="phone" class="righty" value="<%=thisForm.getProfessionalSpecialistPhone()%>" /></td>
						</tr>
						<tr>
							<td class="tite4"><bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.formFax" />:
							</td>
							<td align="right" class="tite3"><input readonly="readonly" type="text" onchange="onChangeSpecialistFax();" name="fax" class="righty" /></td>
						</tr>

						<tr>
							<td class="tite4">
								<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.formAddr" />:
							</td>
							<td align="right" class="tite3">
								<textarea readonly="readonly" name="address" cols=20 ><%=thisForm.getProfessionalSpecialistAddress()%></textarea>
							</td>
						</tr>
						<tr>
							<td class="tite4"><bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.formPatientBook" />:</td>
							<td align="right" class="tite3"><html:checkbox property="patientWillBook" value="1" onclick="disableDateFields()">
							</html:checkbox></td>
						</tr>
						<tr>
							<td class="tite4"><bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnAppointmentDate" />:
							</td>
                            <td align="right" class="tite3"><img alt="calendar" id="appointmentDate_cal" src="../../images/cal.gif">
 							<html:text styleId="appointmentDate" property="appointmentDate" readonly="true" ondblclick="this.value='';" />
							</td>
						</tr>
						<tr>
							<td class="tite4"><bean:message	key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.formAppointmentTime" />:
							</td>
							<td align="right" class="tite3">
							<table>
								<tr>
									<td><html:select property="appointmentHour">
										<html:option value=""></html:option>
										<%
											for (int i = 1; i < 13; i = i + 1)
														{
															String hourOfday = Integer.toString(i);
										%>
										<html:option value="<%=hourOfday%>"><%=hourOfday%></html:option>
										<%
											}
										%>
									</html:select></td>
									<td><html:select property="appointmentMinute">
										<html:option value=""></html:option>
										<%
											for (int i = 0; i < 60; i = i + 1)
														{
															String minuteOfhour = Integer.toString(i);
															if (i < 10)
															{
																minuteOfhour = "0" + minuteOfhour;
															}
										%>
										<html:option value="<%=String.valueOf(i)%>"><%=minuteOfhour%></html:option>
										<%
											}
										%>
									</html:select></td>
									<td><html:select property="appointmentPm">
										<html:option value="AM">AM</html:option>
										<html:option value="PM">PM</html:option>
									</html:select></td>
								</tr>
							</table>
							</td>
						</tr>
						<%if (bMultisites) { %>
						<tr>
							<td  class="tite4">
								<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.siteName" />:
							</td>
							<td style="text-align: right;background-color: #B8B8FF;">
								<html:select property="siteName" styleId="siteName"
											 onchange='updateSelectedMultisite(this);'>
									<%
										for (Site site : sites)
										{
											String addrName = site.getName();
											String bgColour = site.getBgColor();
											if (addrName.equals(defaultSiteName))
											{
												defaultSiteId = site.getId();
											}
									%>
											<html:option value="<%=addrName%>"
												 style='<%="background-color: "+bgColour%>'><%=addrName%>
											</html:option>
									<% }%>
								</html:select>
							</td>
						</tr>
						<%} %>
					</table>
					</td>
					<td valign="top" cellspacing="1" class="tite4">
					<table border=0 width="100%" bgcolor="white">
						<tr>
							<td class="tite4"><bean:message
								key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.msgPatient" />:
							</td>
                                                        <td class="tite1"><a href="javascript:void();" onClick="popupAttach(600,900,'<%=request.getContextPath()%>/demographic/demographiccontrol.jsp?demographic_no=<%=demo%>&displaymode=edit&dboperation=search_detail')"><%=thisForm.getPatientName()%></a></td>
						</tr>
						<tr>
							<td class="tite4"><bean:message
								key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.msgAddress" />:
							</td>
							<td class="tite1"><%=thisForm.getPatientAddress().replace("null", "")%></td>
						</tr>
						<tr>
							<td class="tite4"><bean:message
								key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.msgPhone" />:
							</td>
							<td class="tite2"><%=thisForm.getPatientPhone()%></td>
						</tr>
						<tr>
							<td class="tite4"><bean:message
								key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.msgWPhone" />:
							</td>
							<td class="tite2"><%=thisForm.getPatientWPhone()%></td>
						</tr>
						<tr>
							<td class="tite4"><bean:message
									key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.msgCPhone" />:
							</td>
							<td class="tite2"><%=thisForm.getPatientCPhone()%></td>
						</tr>
						<tr>
							<td class="tite4"><bean:message
								key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.msgEmail" />:
							</td>
							<td class="tite2"><%=thisForm.getPatientEmail()%></td>
						</tr>
						<tr>
							<td class="tite4"><bean:message
								key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.msgBirthDate" />:
							</td>
							<td class="tite2"><%=thisForm.getPatientDOB()%></td>
						</tr>
						<tr>
							<td class="tite4"><bean:message
								key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.msgSex" />:
							</td>
							<td class="tite3"><%=thisForm.getPatientSex()%></td>
						</tr>
						<tr>
							<td class="tite4"><bean:message
								key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.msgHealthCard" />:
							</td>
							<td class="tite3"><%=thisForm.getPatientHealthNum()%>&nbsp;<%=thisForm.getPatientHealthCardVersionCode()%>&nbsp;<%=thisForm.getPatientHealthCardType()%>
							</td>
						</tr>
						<tr id="conReqSendTo">
							<td class="tite4"><bean:message
								key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.msgSendTo" />:
							</td>
							<td class="tite3"><html:select property="sendTo">
								<html:option value="-1">---- <bean:message
										key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.msgTeams" /> ----</html:option>
								<%
									for (int i = 0; i < consultUtil.teamVec.size(); i++)
												{
													String te = (String)consultUtil.teamVec.elementAt(i);
								%>
								<html:option value="<%=te.toLowerCase()%>"><%=te%></html:option>
								<%
									}
								%>
							</html:select></td>
						</tr>

<!--add for special encounter-->
<plugin:hideWhenCompExists componentName="specialencounterComp" reverse="true">
<special:SpecialEncounterTag moduleName="eyeform">

<%
	String aburl1 = "/EyeForm.do?method=addCC&demographicNo=" + demo;
					if (requestId != null) aburl1 += "&requestId=" + requestId;
%>
<plugin:include componentName="specialencounterComp" absoluteUrl="<%=aburl1 %>"></plugin:include>
</special:SpecialEncounterTag>
</plugin:hideWhenCompExists>
<!-- end -->

						<tr>
							<td colspan="2" class="tite4"><bean:message
								key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.formAppointmentNotes" />:
							</td>
						</tr>
						<tr>
							<td colspan="2" class="tite3"><html:textarea cols="50"
								rows="3" property="appointmentNotes"></html:textarea></td>
						</tr>


						<tr>
							<td class="tite4"><bean:message
								key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.formLastFollowup" />:
							</td>
							<td colspan="2" class="tite3"><img alt="calendar" id="followUpDate_cal" src="../../images/cal.gif">&nbsp;<html:text styleId="followUpDate" property="followUpDate" readonly="true" ondblclick="this.value='';"/>
						</tr>

						<%
							if(thisForm.getFdid() != null) {
						%>
						<tr>
							<td class="tite4">EForm:
							</td>
							<td class="tite2">
								<a href="<%=request.getContextPath()%>/eform/efmshowform_data.jsp?fdid=<%=thisForm.getFdid() %>">Click to view</a>
							</td>
						</tr>
						<%
							}
						%>
					</table>
					</td>
				</tr>
				<tr>
					<td colspan=2>
					<table border=0 width="100%">
						<tr>
						<%
						String lhndType = "provider"; //set default as provider
						String providerDefault = providerNo;

						if(consultUtil.letterheadName == null ){
						//nothing saved so find default
						UserProperty lhndProperty = userPropertyDAO.getProp(providerNo, UserProperty.CONSULTATION_LETTERHEADNAME_DEFAULT);
						String lhnd = lhndProperty != null?lhndProperty.getValue():null;
						//1 or null = provider, 2 = MRP and 3 = clinic

							if(lhnd!=null){
								if(lhnd.equals("2")){
									//mrp
									providerDefault = providerNoFromChart;
								}else if(lhnd.equals("3")){
									//clinic
									lhndType="clinic";
								}
							}

						}
						%>
							<td class="tite4"><bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.letterheadName" />:
							</td>
							<td align="right" class="tite3">
								<select name="letterheadName" id="letterheadName" onchange="switchProvider(this.value)">
									<%
										if (bMultisites && sites.size() > 0)
										{ %>
											<option value="multisite"><%=multisiteLetterheadSelectionName %>
											</option>
									<%
										}%>

									<option value="-1" <%=(consultUtil.letterheadName != null && consultUtil.letterheadName.equalsIgnoreCase("-1") ? "selected='selected'"  : "") %>>
										<%=clinic.getClinicName() %>
									</option>
									<%
									for (Provider p : prList) {
										if (p.getProviderNo().compareTo("-1") != 0 && (p.getFirstName() != null || p.getSurname() != null)) {
								%>
								<option value="<%= p.getProviderNo() %>" <%= (consultUtil.letterheadName != null && consultUtil.letterheadName.equalsIgnoreCase(p.getProviderNo()) ? "selected='selected'" : consultUtil.letterheadName == null && p.getProviderNo().equalsIgnoreCase(providerDefault) && lhndType.equals("provider") && requestId != null ? "selected='selected'" : "") %> >
									<%=p.getSurname() %>, <%=p.getFirstName().replace("Dr.", "") %>
								</option>
								<% }
								}

								if (props.isPropertyActive("consultation_program_letterhead_enabled")) {
								for (Program p : programList) {
								%>
									<option value="prog_<%=p.getId() %>" <%=(consultUtil.letterheadName != null && consultUtil.letterheadName.equalsIgnoreCase("prog_" + p.getId()) ? "selected='selected'"  : "") %>>
									<%=p.getName() %>
									</option>
								<% }
								}%>
								</select>
								<%if (faxEnabled) {
										// This checkbox adds the doctor name to the letterhead when checked. 
										// Because of how it gets stored in a key value table, the entry only exists when it is checked:
										boolean defaultChecked = props.isPropertyActive("consultation_letterhead_title.include_dr_by_default");
										boolean savedCheckState = (consultUtil.letterheadTitle != null && consultUtil.letterheadTitle.equals("Dr"));
										String checked = (savedCheckState || isNewConsultation && defaultChecked) ? "checked" : "";
									%>
									<div style="font-size:12px"><input type="checkbox" name="ext_letterheadTitle" value="Dr" <%=checked%> >Include Dr. with name</div>
								<%}%>
							</td>
						</tr>
						<tr>
							<td class="tite4"><bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.letterheadAddress" />:
							</td>
							<td align="right" class="tite3">
								<% if (consultUtil.letterheadAddress != null) { %>
									<input type="hidden" name="letterheadAddress" id="letterheadAddress" value="<%=StringEscapeUtils.escapeHtml(consultUtil.letterheadAddress) %>" />
									<span id="letterheadAddressSpan">
										<%=consultUtil.letterheadAddress %>
									</span>
								<% } else { %>
									<input type="hidden" name="letterheadAddress" id="letterheadAddress" value="<%=StringEscapeUtils.escapeHtml(clinic.getClinicAddress()) %>  <%=StringEscapeUtils.escapeHtml(clinic.getClinicCity()) %>  <%=StringEscapeUtils.escapeHtml(clinic.getClinicProvince()) %>  <%=StringEscapeUtils.escapeHtml(clinic.getClinicPostal()) %>" />
									<span id="letterheadAddressSpan">
										<%=clinic.getClinicAddress() %>&nbsp;&nbsp;<%=clinic.getClinicCity() %>&nbsp;&nbsp;<%=clinic.getClinicProvince() %>&nbsp;&nbsp;<%=clinic.getClinicPostal() %>
									</span>
								<% } %>
							</td>
						</tr>
						<tr>
							<td class="tite4"><bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.letterheadPhone" />:
							</td>
							<td align="right" class="tite3">
								<% if (consultUtil.letterheadPhone != null) {
								%>
									<input type="hidden" name="letterheadPhone" id="letterheadPhone" value="<%=StringEscapeUtils.escapeHtml(consultUtil.letterheadPhone) %>" />
								 	<span id="letterheadPhoneSpan">
										<%=consultUtil.letterheadPhone%>
									</span>
								<% } else { %>
									<input type="hidden" name="letterheadPhone" id="letterheadPhone" value="<%=StringEscapeUtils.escapeHtml(clinic.getClinicPhone()) %>" />
									<span id="letterheadPhoneSpan">
										<%=clinic.getClinicPhone()%>
									</span>
								<% } %>
							</td>
						</tr>
						<tr>
							<td class="tite4"><bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.letterheadFax" />:
							</td>
							<td align="right" class="tite3">
                            <% if (consultUtil.letterheadFax != null) { %>
                                <input type="hidden" name="letterheadFax" id="letterheadFax" value="<%=StringEscapeUtils.escapeHtml(consultUtil.letterheadFax) %>"/>
                                <span id="letterheadFaxSpan">
                                    <%= consultUtil.letterheadFax %>
                                </span>
                            <% } else { %>
                                <input type="hidden" name="letterheadFax" id="letterheadFax" value="<%=StringEscapeUtils.escapeHtml(clinic.getClinicFax()) %>" />
                                <span id="letterheadFaxSpan">
                                    <%= clinic.getClinicFax() %>
                                </span>
                            <% } %>
                        </td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td colspan=2>
					<td>
				</tr>
				<tr>
					<td colspan="2" class="tite4"><bean:message
						key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.formReason" />
					</td>
				</tr>
				<tr>
					<td colspan=2><html:textarea property="reasonForConsultation"
						cols="90" rows="3"></html:textarea></td>
				</tr>
				<tr>
					<td colspan=2 class="tite4">
					<table width="100%">
						<tr>
							<td width="30%" class="tite4"><bean:message
								key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.formClinInf" />:
							</td>
							<td id="clinicalInfoButtonBar">
								<input type="button"
									   class="btn"
									   value="<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnImportFamHistory"/>"
									   onclick="importFromEnct('FamilyHistory',document.forms[0].clinicalInformation);" />&nbsp;
								<input type="button"
									   class="btn"
									   value="<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnImportMedHistory"/>"
									   onclick="importFromEnct('MedicalHistory',document.forms[0].clinicalInformation);" />&nbsp;
								<input type="button"
									   class="btn"
									   value="<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnImportSocHistory"/>"
									   onclick="importFromEnct('SocialHistory',document.forms[0].clinicalInformation);" />&nbsp;
								<input id="btnOngoingConcerns"
									   type="button"
									   class="btn" value="<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnImportConcerns"/>"
									   onclick="importFromEnct('ongoingConcerns',document.forms[0].clinicalInformation);" />&nbsp;
								<input type="button"
									   class="btn" value="<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnImportOtherMeds"/>"
									   onclick="importFromEnct('OtherMeds',document.forms[0].clinicalInformation);" />&nbsp;
								<input id="btnReminders"
									   type="button"
									   class="btn"
									   value="<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnImportReminders"/>"
									   onclick="importFromEnct('Reminders',document.forms[0].clinicalInformation);" />&nbsp;
								<span id="clinicalInfoButtons"></span>
							</td>
						</tr>
					</table>
				</tr>
				<tr>
					<td colspan=2><html:textarea cols="90" rows="10"
						styleId="clinicalInformation" property="clinicalInformation"></html:textarea></td>
				</tr>
				<tr>
					<td colspan=2 class="tite4">
					<table width="100%">
						<tr>
							<td width="30%" class="tite4">
							<%
								if (props.getProperty("significantConcurrentProblemsTitle", "").length() > 1)
										{
											out.print(props.getProperty("significantConcurrentProblemsTitle", ""));
										}
										else
										{
							%> <bean:message
								key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.formSignificantProblems" />:
							<%
 	}
 %>
							</td>
							<td id="concurrentProblemsButtonBar">
								<input type="button"
									   class="btn"
									   value="<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnImportFamHistory"/>"
									   onclick="importFromEnct('FamilyHistory',document.forms[0].concurrentProblems);" />&nbsp;
								<input type="button"
									   class="btn"
									   value="<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnImportMedHistory"/>"
									   onclick="importFromEnct('MedicalHistory',document.forms[0].concurrentProblems);" />&nbsp;
								<input type="button"
									   class="btn"
									   value="<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnImportSocHistory"/>"
									   onclick="importFromEnct('SocialHistory',document.forms[0].concurrentProblems);" />&nbsp;
								<input id="btnOngoingConcerns2"
									   type="button"
									   class="btn"
									   value="<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnImportConcerns"/>"
									   onclick="importFromEnct('ongoingConcerns',document.forms[0].concurrentProblems);" />&nbsp;
								<input type="button"
									   class="btn"
									   value="<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnImportOtherMeds"/>"
									   onclick="importFromEnct('OtherMeds',document.forms[0].concurrentProblems);" />&nbsp;
								<input id="btnReminders2"
									   type="button"
									   class="btn"
									   value="<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnImportReminders"/>"
									   onclick="importFromEnct('Reminders',document.forms[0].concurrentProblems);" />&nbsp;
							</td>
						</tr>
					</table>

					</td>
				</tr>
				<tr id="trConcurrentProblems">
					<td colspan=2><html:textarea cols="90" rows="3" styleId="concurrentProblems"
						property="concurrentProblems">

					</html:textarea></td>
				</tr>
 <!--add for special encounter-->
<plugin:hideWhenCompExists componentName="specialencounterComp" reverse="true">
<special:SpecialEncounterTag moduleName="eyeform">
<%
	String aburl2 = "/EyeForm.do?method=specialConRequest&demographicNo=" + demo + "&appNo=" + request.getParameter("appNo");
					if (requestId != null) aburl2 += "&requestId=" + requestId;
if (defaultSiteId!=0) aburl2+="&site="+defaultSiteId;
%>
<html:hidden property="specialencounterFlag" value="true"/>
<plugin:include componentName="specialencounterComp" absoluteUrl="<%=aburl2 %>"></plugin:include>
</special:SpecialEncounterTag>
</plugin:hideWhenCompExists>
				<tr>
					<td colspan="2" class="tite4">
					<table width="100%">
						<tr>
							<td width="30%" class="tite4">
							<%
								if (props.getProperty("currentMedicationsTitle", "").length() > 1)
										{
											out.print(props.getProperty("currentMedicationsTitle", ""));
										}
										else
										{
							%> <bean:message
								key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.formCurrMedications" />:
							<%
 	}
 %>
							</td>
							<td id="medsButtonBar">
								<input type="button" class="btn" value="<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnImportOtherMeds"/>" onclick="importFromEnct('OtherMeds',document.forms[0].currentMedications);" />
								<span id="medsButtons"></span>
							</td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td colspan=2><html:textarea cols="90" rows="3" styleId="currentMedications"
						property="currentMedications"></html:textarea></td>
				</tr>
				<tr>
					<td colspan=2 class="tite4"><bean:message
						key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.formAllergies" />:
					</td>
				</tr>
				<tr>
					<td colspan=2><html:textarea cols="90" rows="3"
						property="allergies"></html:textarea></td>
				</tr>
				<%
				if (faxEnabled) {
				%>
				<tr><td colspan=2 class="tite4">Additional Fax Recipients:</td></tr>
				<tr>
					<td colspan=2>
					    <%
					    String rdohip = "";
					    if (demographic!=null) {
					    	String famDoc = demographic.getFamilyDoctor();
					    	if (famDoc != null && famDoc.trim().length() > 0) { rdohip = SxmlMisc.getXmlContent(famDoc,"rdohip"); rdohip = rdohip == null ? "" : rdohip.trim(); }
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
								for (int i=0;i < displayServiceUtil.specIdVec.size(); i++) {
		                                 String  specId     =  displayServiceUtil.specIdVec.elementAt(i);
		                                 String  fName      =  displayServiceUtil.fNameVec.elementAt(i);
		                                 String  lName      =  displayServiceUtil.lNameVec.elementAt(i);
		                                 String  proLetters =  displayServiceUtil.proLettersVec.elementAt(i);
		                                 String  address    =  displayServiceUtil.addressVec.elementAt(i);
		                                 String  phone      =  displayServiceUtil.phoneVec.elementAt(i);
		                                 String  fax        =  displayServiceUtil.faxVec.elementAt(i);
		                                 String  referralNo = ""; // TODO-legacy: add referal number to specialists ((String) displayServiceUtil.referralNoList.get(i)).trim();
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
						<tr>
							<td class="tite4" width="20%"> Other Fax Number: </td>
							<td class="tite3" width="32%">
								<input type="text" id="otherFaxInput"></input>

							<font size="1">(xxx-xxx-xxxx)  </font></td>
							<td class="tite3">
								<button onclick="Oscar.Util.Fax.AddOtherFax(); return false;">Add Other Fax Recipient</button>
							</td>
						</tr>
						<tr>
							<td colspan=3>
								<ul id="faxRecipients">
								<%
								if (!"".equals(rdName) && !"".equals(rdFaxNo)) {
									%>
								<!--<li>-->
										<!-- <%--= rdName %> <b>Fax No: </b><%= rdFaxNo --%> <a href="javascript:void(0);" onclick="removeRecipient(this)">remove</a>-->
										<input type="hidden" name="faxRecipients" value="<%= rdFaxNo %>" />
								<!--</li>-->
									<%
								}
								%>
								</ul>
							</td>
						</tr>
						</table>
					</td>
				</tr>
				<% } %>



				<tr>
					<td colspan=2><input type="hidden" name="submission" value="">
					<%
						if (request.getAttribute("id") != null)
								{
					%>
						<input name="update" type="button" value="<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnUpdate"/>" onclick="return checkForm('Update Consultation Request','EctConsultationFormRequestForm');" />
						<input name="updateAndPrint" type="button" value="<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnUpdateAndPrint"/>" onclick="return checkForm('Update Consultation Request And Print Preview','EctConsultationFormRequestForm');" />
						<input name="updateAndSendElectronically" type="button" value="<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnUpdateAndSendElectronicReferral"/>" onclick="return checkForm('Update_esend','EctConsultationFormRequestForm');" />
						<%
							if (faxEnabled)
										{
						%>
						<input id="fax_button2" class="faxButton" name="updateAndFax" type="button" value="<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnUpdateAndFax"/>" onclick="return checkForm('Update And Fax','EctConsultationFormRequestForm');" />
						<%
							}
						%>

            <% if (OscarProperties.getInstance().isPropertyActive("appointment_reminder_enabled")) { %>
              <input type="button"
                <% if(!validPatientEmail) { %> disabled="disabled" <% } %>
                name="updateAndEmailDetails"
                value='<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnUpdateAndEmailAppointmentTime"/>'
                onclick="return checkForm('Update And Email Details','EctConsultationFormRequestForm');" />
            <% } %>

			<% if (OscarProperties.getInstance().isPropertyActive("consultation_notification_enabled")) { %>
			<input type="button"
					<% if(!validPatientEmail) { %> disabled="disabled" <% } %>
				   id="updateAndEmailNotification" name="updateAndEmailNotification"
				   value="<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnUpdateAndEmailNotification"/>"
				   onclick="return checkForm('Update And Email Notification','EctConsultationFormRequestForm');" />
			<% }
						}
								else
								{
					%>
						<input name="submitSaveOnly" type="button" value="<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnSubmit"/>" onclick="return checkForm('Submit Consultation Request','EctConsultationFormRequestForm'); " />
						<input name="submitAndPrint" type="button" value="<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnSubmitAndPrint"/>" onclick="return checkForm('Submit Consultation Request And Print Preview','EctConsultationFormRequestForm'); " />
						<input name="submitAndSendElectronically" type="button" value="<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnSubmitAndSendElectronicReferral"/>" onclick="return checkForm('Submit_esend','EctConsultationFormRequestForm');" />
						<%
							if (faxEnabled)
										{
						%>
						<input id="fax_button2" class="faxButton" name="submitAndFax" type="button" value="<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnSubmitAndFax"/>" onclick="return checkForm('Submit And Fax','EctConsultationFormRequestForm');" />
						<%
							}
						%>
						<% if (OscarProperties.getInstance().isPropertyActive("consultation_notification_enabled")) { %>
						<input type="button"
								<% if(!validPatientEmail) { %> disabled="disabled" <% } %>
							   id="submitAndEmailNotification" name="submitAndEmailNotification"
							   value="<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultationFormRequest.btnSubmitAndEmailNotification"/>"
							   onclick="return checkForm('Submit And Email Notification','EctConsultationFormRequestForm');" />
						<% }
						}

						if (thisForm.iseReferral())
						{
							%>
								<input type="button" value="Send eResponse" onclick="$('saved').value='true';document.location='<%=thisForm.getOruR01UrlString(request)%>'" />
							<%
						}
						%>
					</td>
				</tr>

				<script type="text/javascript">

	        initMaster();
	        initService('<%=consultUtil.service%>',
		        '<%=((consultUtil.service==null)?"":StringEscapeUtils.escapeJavaScript(consultUtil.getServiceName(consultUtil.service.toString())))%>',
		        '<%=consultUtil.specialist%>',
		        '<%=((consultUtil.specialist==null)?"":StringEscapeUtils.escapeJavaScript(consultUtil.getSpecailistsName(consultUtil.specialist.toString())))%>',
		        '<%=StringEscapeUtils.escapeJavaScript(consultUtil.specPhone)%>',
		        '<%=StringEscapeUtils.escapeJavaScript(consultUtil.specFax)%>',
		        '<%=StringEscapeUtils.escapeJavaScript(consultUtil.specAddr)%>',
				'<%=StringEscapeUtils.escapeJavaScript(consultUtil.specReferralNo)%>');
            initSpec();
            document.EctConsultationFormRequestForm.phone.value = ("");
        	document.EctConsultationFormRequestForm.fax.value = ("");
        	document.EctConsultationFormRequestForm.address.value = ("");
            <%if (request.getAttribute("id") != null)
					{%>
                setSpec('<%=consultUtil.service%>','<%=consultUtil.specialist%>');
                FillThreeBoxes('<%=consultUtil.specialist%>');
            <%}
					else
					{%>
                document.EctConsultationFormRequestForm.service.options.selectedIndex = 0;
                document.EctConsultationFormRequestForm.specialist.options.selectedIndex = 0;
            <%}%>

            onSelectSpecialist(document.EctConsultationFormRequestForm.specialist);
            
            <%
            	//new with BORN referrals. Allow form to be loaded with service and 
            	//specialist pre-selected
            	String reqService = request.getParameter("service");
            	
            	String reqSpecialist = request.getParameter("specialist");
            	if(reqService != null && reqSpecialist != null) {
            		ConsultationServices consultService = consultationServiceDao.findByDescription(reqService);
            		if(consultService != null) {
            		%>
            		jQuery("#service").val('<%=consultService.getId()%>');
            		fillSpecialistSelect(document.getElementById('service'));
            		jQuery("#specialist").val('<%=reqSpecialist%>');
            		onSelectSpecialist(document.getElementById('specialist'));
            		<%
            	} }
            	
            	String serviceId = request.getParameter("serviceId");
            	if(serviceId != null) {
            		%>
            		jQuery("#service").val('<%=serviceId%>');
            		fillSpecialistSelect(document.getElementById('service'));
            		<%
            	}
            %>
        //-->
        </script>




				<!----End new rows here-->

				<tr height="100%">
					<td></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td class="MainTableBottomRowLeftColumn"></td>
			<td class="MainTableBottomRowRightColumn"></td>
		</tr>
	</table>
	<input value="" name="toProviderName" id="toProviderName" type="hidden" />
    <input value="" name="toProviderEmail" id="toProviderEmail" type="hidden" />

    <input value="<%=demographic.getDisplayName()%>" name="toPatientName" id="toPatientName" type="hidden" />
    <input value="<%=demographic.getEmail()%>" name="toPatientEmail" id="toPatientEmail" type="hidden" />
    <input name="providerEmailBody" id="providerEmailBody" type="hidden" value="" />

    <input name="patientEmailBody" id="patientEmailBody" type="hidden" value="" />

    <input name="emailSubject" id="emailSubject" type="hidden" value="" />

	<div id="emailFormBoxProvider" style="display: none">
        <div id="additionalInfoForm">
            <div>
                <label>To:</label>
                <input type=text id="emailToProvider" name="emailToProvider" value="">
            </div>
            <div>
                <label>Subject:</label>
                <input type="text" name="emailSubjectForm" id="emailSubjectForm" value="<%= props.getProperty("eform_email_subject") %>">
            </div>
            <div>
                <label>Body text:</label>
                <textarea name="providerEmailBodyForm" id="providerEmailBodyForm"><%= props.getProperty("eform_email_text_providers") %></textarea>
            </div>
            <div>
				<%
				if(request.getAttribute("id")!=null){
				%>
                <input type="button" onClick="checkForm('Update And Email Provider','EctConsultationFormRequestForm')" value="Email eForm">
				<% } else { %>
                <input type="button" onClick="checkForm('Submit And Email Provider','EctConsultationFormRequestForm')" value="Email eForm">
				<% } %>
            </div>
        </div>

        <span class="progress"></span>

    </div>
    <div id="emailFormBoxPatient" style="display: none">
        <div id="additionalInfoForm">
            <div>
                <label>To:</label>
                <input type=text id="emailToPatient" name="emailToPatient" value="<%=demographic.getEmail()%>">
            </div>
            <div>
                <label>Subject:</label>
                <input type="text" name="emailSubjectForm" id="emailSubjectForm" value="<%= props.getProperty("eform_email_subject") %>">
            </div>
            <div>
                <label>Body text:</label>
                <textarea name="patientEmailBodyForm" id="patientEmailBodyForm"><%= props.getProperty("eform_email_text_patients") %></textarea>
            </div>
            <div>
				<%
				if(request.getAttribute("id")!=null){
				%>
                <input type="button" onClick="checkForm('Update And Email Patient','EctConsultationFormRequestForm')" value="Email eForm">
				<% } else { %>
                <input type="button" onClick="checkForm('Submit And Email Patient','EctConsultationFormRequestForm')" value="Email eForm">
				<% } %>
            </div>
        </div>

        <span class="progress"></span>

    </div>




</html:form>
</body>

<script type="text/javascript" language="javascript">

Calendar.setup( { inputField : "followUpDate", ifFormat : "%Y/%m/%d", showsTime :false, button : "followUpDate_cal", singleClick : true, step : 1 } );
Calendar.setup( { inputField : "appointmentDate", ifFormat : "%Y/%m/%d", showsTime :false, button : "appointmentDate_cal", singleClick : true, step : 1 } );
</script>
</html:html>

<%!protected String listNotes(CaseManagementManager cmgmtMgr, String code, String providerNo, String demoNo)
	{
		// filter the notes by the checked issues
		List<Issue> issues = cmgmtMgr.getIssueInfoByCode(providerNo, code);

		String[] issueIds = new String[issues.size()];
		int idx = 0;
		for (Issue issue : issues)
		{
			issueIds[idx] = String.valueOf(issue.getId());
		}

		// need to apply issue filter
		List<CaseManagementNote> notes = cmgmtMgr.getNotes(demoNo, issueIds);

		// Order by position set in echart
		Collections.sort(notes, CaseManagementNote.getPositionComparator());
		StringBuffer noteStr = new StringBuffer();
		for (CaseManagementNote n : notes)
		{
			if (!n.isLocked() && !n.isArchived()) noteStr.append(n.getNote() + "\n");
		}

		return noteStr.toString();
	}%>