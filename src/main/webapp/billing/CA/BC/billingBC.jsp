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
<security:oscarSec roleName="<%=roleName$%>" objectName="_billing" rights="w" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect("../../../securityError.jsp?type=_billing");%>
</security:oscarSec>
<%
if(!authed) {
	return;
}
%>

<%@page import="org.apache.commons.lang.StringEscapeUtils"%>

<%@taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@taglib uri="/WEB-INF/rewrite-tag.tld" prefix="rewrite"%>
<%@ page import="org.oscarehr.util.LoggedInInfo"%>
<%@ page import="org.oscarehr.util.SpringUtils"%>
<%@page import="oscar.OscarProperties"%>
<%@page import="oscar.entities.PaymentType, oscar.oscarBilling.ca.bc.data.BillingFormData, oscar.oscarBilling.ca.bc.data.SupServiceCodeAssocDAO,oscar.oscarBilling.ca.bc.pageUtil.BillingAssociationPersistence,oscar.oscarBilling.ca.bc.pageUtil.BillingCreateBillingForm,oscar.oscarBilling.ca.bc.pageUtil.BillingSessionBean"%>
<%@page import="oscar.oscarBilling.ca.bc.pageUtil.ServiceCodeAssociation" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.GregorianCalendar" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="oscar.oscarBilling.ca.bc.data.BillingPreference" %>
<%@ page import="org.oscarehr.common.dao.ProviderPreferenceDao" %>
<%@ page import="org.oscarehr.common.model.ProviderPreference" %>
<%@ page import="org.oscarehr.common.dao.SiteDao" %>
<%@ page import="org.oscarehr.common.model.Site" %>
<%@ page import="org.oscarehr.common.model.Appointment" %>
<%@ page import="org.oscarehr.managers.AppointmentManager" %>
<%@ page import="org.oscarehr.preferences.service.SystemPreferenceService" %>
<%@ page import="oscar.util.ConversionUtils" %>
<%@ page import="org.oscarehr.clinic.service.ClinicService" %>
<%@ page import="org.oscarehr.provider.service.ProviderService" %>
<%@ page import="org.oscarehr.provider.model.ProviderData" %>
<%@ page import="org.oscarehr.site.service.SiteService" %>
<%@ page import="oscar.oscarBilling.ca.bc.pageUtil.BillingForm" %>
<%@ page import="org.oscarehr.common.dao.BillingBCDao" %>
<%@ page import="org.oscarehr.common.dao.BillingServiceDao" %>
<%@ page import="oscar.oscarBilling.ca.bc.data.BillingVisit" %>
<%!
  public void fillDxcodeList(BillingFormData.BillingService[] servicelist, Map dxcodeList) {
    for (int i = 0; i < servicelist.length; i++) {
      BillingAssociationPersistence per = new BillingAssociationPersistence();
      ServiceCodeAssociation assoc = per.getServiceCodeAssocByCode(servicelist[i].getServiceCode());
      List dxcodes = assoc.getDxCodes();
      if (!dxcodes.isEmpty()) {
        dxcodeList.put(servicelist[i].getServiceCode(), (String) dxcodes.get(0));
      }
    }
  }
  public String createAssociationJS(Map assocCodeMap,String assocArrayName) {

    Set e = assocCodeMap.keySet();
    StringBuffer out = new StringBuffer();
    out.append("var " + assocArrayName + "  = new Array();");
    out.append("\n");
    int index = 0;
    for (Iterator iter = e.iterator(); iter.hasNext(); ) {
      String key = (String) iter.next();
      String value = (String) assocCodeMap.get(key);
      String rowName = assocArrayName + "row";
      out.append("var " + rowName + index + " = new Array(2);\n");
      out.append(rowName + index + "[0]='" + key + "'; ");
      out.append(rowName + index + "[1]='" + value + "';\n");
      out.append(assocArrayName + "[" + index + "]=" + rowName  + index + ";\n");
      index++;
    }
    return out.toString();
  }

  private void populateInitialDxCodeList(HttpServletRequest request, BillingCreateBillingForm form, String apptProviderNo, String userProviderNo)
  {
      String diag_code_1 = request.getParameter("diag_code_1");
      String diag_code_2 = request.getParameter("diag_code_2");
      String diag_code_3 = request.getParameter("diag_code_3");

      if (diag_code_1 == null && diag_code_2 == null && diag_code_3 == null)
      {
          // Mirror what billing Ontario does.  Try the default dx code of the appt provider first, then the user's.
          String defaultDxCode = getDefaultDxCode(apptProviderNo);
          if (defaultDxCode == null)
          {
              defaultDxCode = getDefaultDxCode(userProviderNo);
          }

          if (defaultDxCode != null)
          {
              form.setXml_diagnostic_detail1(defaultDxCode);
          }
      }
      else
      {
          if (diag_code_1 != null)
          {
              form.setXml_diagnostic_detail1(diag_code_1);
          }
          if (diag_code_2 != null)
          {
              form.setXml_diagnostic_detail2(diag_code_2);
          }
          if (diag_code_3 != null)
          {
              form.setXml_diagnostic_detail3(diag_code_3);
          }
      }
  }

  private String getDefaultDxCode(String providerNo)
  {
      ProviderPreferenceDao prefDao = SpringUtils.getBean(ProviderPreferenceDao.class);
      ProviderPreference preference = prefDao.find(providerNo);

      if (preference != null)
      {
          return preference.getDefaultDxCode();
      }
      else
      {
          return null;
      }
  }

    /**
     * Get the Service Location code to use with this billing, going down the following priorities until one is not null or empty
     * 1) The billing provider's default SLC code
     * 2) (If multisite) The default SLC code for the site associated with the appointment
     * 3) The clinic's default billing code
     * @return
     */
    private String getDefaultVisitType(LoggedInInfo info, String billingProviderNo, String appointmentNo)
    {
        BillingBCDao billingBCDao = SpringUtils.getBean(BillingBCDao.class);
        Map<String, String> slcCodesMap = new HashMap<String, String>();
        List<Object[]> visitCodes = billingBCDao.findBillingVisits(BillingServiceDao.BC);
        for (Object[] visitCode : visitCodes)
        {
            slcCodesMap.put((String)visitCode[0], (String)visitCode[1]);
        }

        // Provider and site are formatted a little differently, they store the code key, whereas the clinic
        // property is stored as "key|description".  Since the front end expects that latter, we need to properly
        // format the codes associated with provider and site.
        ProviderService providerService = SpringUtils.getBean(ProviderService.class);
        ProviderData provider = providerService.getProviderEager(billingProviderNo);
        if (provider != null && provider.getBillingOpts() != null && ConversionUtils.hasContent(provider.getBillingOpts().getBcServiceLocationCode()))
        {
            String providerCode = provider.getBillingOpts().getBcServiceLocationCode();
            return providerCode + "|" + slcCodesMap.get(providerCode);
        }

        // appointmentNo = 0 is passed in when direct billing via master record
        if (OscarProperties.getInstance().isMultisiteEnabled() && !appointmentNo.equals("0"))
        {
            AppointmentManager appointmentManager = SpringUtils.getBean(AppointmentManager.class);
            Appointment appointment = appointmentManager.getAppointment(info, Integer.parseInt(appointmentNo));

            SiteService siteService = SpringUtils.getBean(SiteService.class);
            Site site = siteService.getSiteByName(appointment.getLocation());

            if (site != null && ConversionUtils.hasContent(site.getBcServiceLocationCode()))
            {
                String siteCode = site.getBcServiceLocationCode();
                return siteCode + "|" + slcCodesMap.get(siteCode);
            }
        }

        SystemPreferenceService systemPreferences = SpringUtils.getBean(SystemPreferenceService.class);
        String clinicSLCCode = systemPreferences.getPreferenceValue("service_location_code", "");

        return clinicSLCCode;
    }

    /**
     * Get the provider to use for this billing, going down the following priorities until one is not null or empty.
     *
     * 1) The provider on the billing session bean
     * 2) Property: auto_populate_billing_bc_billingPhysicianID
     * 3) The provider associaed with the appointment being billed
     * 3) The demographic's MRP
     *
     * This method has no side effects
     * @param bean BillingSessionBean
     * @param properties OscarProperties
     * @param demo demographic being billed

     * @return providerNo of the provider to use for the billing
     */
  private String getDefaultProvider(BillingSessionBean bean, OscarProperties properties, org.oscarehr.common.model.Demographic demo)
  {
  	String sxml_provider = bean.getBillingProvider();

  	if(!ConversionUtils.hasContent(sxml_provider) || sxml_provider.trim().equals("none"))
      {
          // OHSUPORT-2718 - set the default billing physician. Overrides appointment physician
          sxml_provider = properties.getProperty("auto_populate_billing_bc_billingPhysicianID", bean.getApptProviderNo());

          // OHSUPORT-2883 - autofill based on assigned provider. only if not already autofilled
          if(properties.isPropertyActive("auto_populate_billing_bc_billingPhysician") && sxml_provider.trim().equals("none"))
          {
              sxml_provider = demo.getProviderNo();
          }
      }

      return sxml_provider;
  }
%>
<%
    OscarProperties oscarProperties = OscarProperties.getInstance();
	SystemPreferenceService systemPreferenceService = SpringUtils.getBean(SystemPreferenceService.class);

	GregorianCalendar now = new GregorianCalendar();
	String sxml_location = "", sxml_provider = "", sxml_visittype = "";
	BillingSessionBean bean = (BillingSessionBean) pageContext.findAttribute("billingSessionBean");
	oscar.oscarDemographic.data.DemographicData demoData = new oscar.oscarDemographic.data.DemographicData();
	org.oscarehr.common.model.Demographic demo = demoData.getDemographic(LoggedInInfo.getLoggedInInfoFromSession(request), bean.getPatientNo());
	oscar.oscarBilling.ca.bc.MSP.ServiceCodeValidationLogic lgc = new oscar.oscarBilling.ca.bc.MSP.ServiceCodeValidationLogic();
	BillingFormData billform = new BillingFormData();
	BillingFormData.BillingService[] billlist1 = lgc.filterServiceCodeList(billform.getServiceList("Group1", bean.getBillForm(), bean.getBillRegion(), new Date()), demo);
	BillingFormData.BillingService[] billlist2 = lgc.filterServiceCodeList(billform.getServiceList("Group2", bean.getBillForm(), bean.getBillRegion(), new Date()), demo);
	BillingFormData.BillingService[] billlist3 = lgc.filterServiceCodeList(billform.getServiceList("Group3", bean.getBillForm(), bean.getBillRegion(), new Date()), demo);
	String group1Header = billform.getServiceGroupName("Group1", bean.getBillForm());
	String group2Header = billform.getServiceGroupName("Group2", bean.getBillForm());
	String group3Header = billform.getServiceGroupName("Group3", bean.getBillForm());
	BillingFormData.BillingPhysician[] billphysician = billform.getProviderList();
	BillingVisit[] billvisit = billform.getVisitType(bean.getBillRegion());
	BillingFormData.Location[] billlocation = billform.getLocationList(bean.getBillRegion());
	BillingFormData.Diagnostic[] diaglist = billform.getDiagnosticList(bean.getBillForm(), bean.getBillRegion());
	BillingFormData.BillingForm[] billformlist = billform.getFormList();
	SupServiceCodeAssocDAO supDao = SpringUtils.getBean(SupServiceCodeAssocDAO.class);
	HashMap assocCodeMap = new HashMap();
	fillDxcodeList(billlist1, assocCodeMap);
	fillDxcodeList(billlist2, assocCodeMap);
	fillDxcodeList(billlist3, assocCodeMap);
	String frmType = request.getParameter("billType");
	if(frmType != null && frmType.equals("Pri"))
	{
		billform.setPrivateFees(billlist1);
		billform.setPrivateFees(billlist2);
		billform.setPrivateFees(billlist3);
	}
	String loadFromSession = request.getParameter("loadFromSession");
	if(request.getAttribute("loadFromSession") != null)
	{
		loadFromSession = "y";
	}
	if(loadFromSession == null)
	{
		request.getSession().removeAttribute("BillingCreateBillingForm");
	}

	String newWCBClaim = (String) request.getAttribute("newWCBClaim");
%>
<html>
<head>
<title>
<bean:message key="billing.bc.title"/>
</title>
<html:base/>
<link rel="stylesheet" type="text/css" media="all" href="../../../share/calendar/calendar.css" title="win2k-cold-1"/>
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/js/jquery_css/smoothness/jquery-ui-1.10.2.custom.min.css"/>
<script type="text/javascript" src="../../../share/calendar/calendar.js"></script>
<script type="text/javascript" src="../../../share/calendar/lang/<bean:message key="global.javascript.calendar"/>"></script>
<script type="text/javascript" src="../../../share/calendar/calendar-setup.js"></script>
<script type="text/javascript" src="../../../share/javascript/prototype.js"></script>
<script type="text/javascript" src="../../../share/javascript/Oscar.js"></script>
<script type="text/javascript" src="../../../share/javascript/boxover.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/jquery-1.9.1.min.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/jquery-ui-1.10.2.custom.min.js"></script>
<script type="text/javascript" src="./js/junoBillingHelper.js"></script>
<style type="text/css">
  <!--
    A, BODY, INPUT, OPTION ,SELECT , TABLE, TEXTAREA, TD, TR {font-family:tahoma,Verdana, Arial, Helvetica,sans-serif; font-size:10px;}
  -->

  .popUp{
  visibility: hidden;
  background-color:#ffffcc;
  }

  .odd{
  background-color:#EEEEFF;
  }
  .even{
  background-color:#FFFFFF;
  }

  .popupHeader{
  background-color:#ccccff;
  font-size:10pt;
  }
</style>
<script language="JavaScript">

    jQuery.noConflict();

    // Autocompletion of referral doctors
    jQuery(document).ready(function(){
        var xml_refer1 = jQuery('input[name=xml_refer1]');
        var xml_refer2 = jQuery('input[name=xml_refer2]');

        xml_refer1.removeAttr('onkeypress');
        xml_refer2.removeAttr('onkeypress');

        xml_refer1.keypress(function(){
            xml_refer1.autocomplete({
                minLength: 3,
                delay: 300,
                source: "billingReferCodeSearchApi.jsp?name=&name1=&name2=&search=&outputType=json&valueType=",
                close: function(event, ui) {
                    xml_refer1.keypress(function(event) {
                        return grabEnter(event, function()
                        {
                            return ReferralScriptAttach('xml_refer1');
                        });
                    });
                }
            });
        });

        xml_refer2.keypress(function() {
            xml_refer2.autocomplete({
                minLength: 3,
                delay: 300,
                source: "billingReferCodeSearchApi.jsp?name=&name1=&name2=&search=&outputType=json&valueType=",
                close: function(event, ui) {
                    xml_refer2.keypress(function(event){
                        return grabEnter(event, function()
                        {
                            return ReferralScriptAttach('xml_refer2');
                        });
                    });
                }
            });
        });

        var $providerSelect = jQuery('#billing-provider-select');
        var $facilityNumber = jQuery('#facility-number');
        var $siteSelect = jQuery('#site-select');
        var $ruralRetentionSelect = jQuery('select[name=xml_location]');
		var $serviceLocationSelect = jQuery('select[name=xml_visittype]');

        if ($siteSelect.length > 0)
        {
            Juno.BillingHelper.BC.initAutoApplyBCPMultiSite("<%=request.getContextPath() %>", $providerSelect, $facilityNumber, $siteSelect);
        }
        else
        {
            Juno.BillingHelper.BC.initAutoApplyBCP("<%=request.getContextPath() %>", $providerSelect, $facilityNumber);
        }

        Juno.BillingHelper.BC.initRuralRetentionCodeHook("<%=request.getContextPath() %>", $providerSelect, $ruralRetentionSelect);
        Juno.BillingHelper.BC.initServiceLocationCodeHook("<%=request.getContextPath() %>", $providerSelect, $siteSelect, $serviceLocationSelect);
    });

//creates a javaspt array of associated dx codes
<%=createAssociationJS(assocCodeMap,"jsAssocCodes")%>
<%=createAssociationJS(supDao.getAssociationKeyValues(),"trayAssocCodes")%>


function codeEntered(svcCode)
{
	var myform = document.forms[0];
	return ((myform.xml_other1.value == svcCode) || (myform.xml_other2.value == svcCode) || (myform.xml_other3.value == svcCode))
}

function addSvcCode(serviceCheckbox, svcCode)
{
	var billingForm = document.forms[0];

	if (serviceCheckbox.checked)
	{
		if (!billingForm.xml_other1.value)
		{
			billingForm.xml_other1.value = svcCode;
			linkServiceAssocCode(svcCode, billingForm.xml_other2);
			linkDxAssocCode(svcCode, billingForm.xml_diagnostic_detail1);
		}
		else if (!billingForm.xml_other2.value)
		{
            billingForm.xml_other2.value = svcCode;
            linkServiceAssocCode(svcCode, billingForm.xml_other3);
			linkDxAssocCode(svcCode, billingForm.xml_diagnostic_detail2);
		}
		else if (!billingForm.xml_other3.value)
		{
			billingForm.xml_other3.value = svcCode;
			linkDxAssocCode(svcCode, billingForm.xml_diagnostic_detail3);
		}
		else
		{
			alert("There are already three service codes entered");
			serviceCheckbox.checked = false;
			return false;
		}
	}
	else
	{
		if (billingForm.xml_other1.value === svcCode)
		{
			billingForm.xml_other1.value = "";
		}
		else if (billingForm.xml_other2.value === svcCode)
		{
			billingForm.xml_other2.value = "";
		}
		else if (billingForm.xml_other3.value === svcCode)
		{
			billingForm.xml_other3.value = "";
		}
	}
	return true;
}

function linkServiceAssocCode(serviceCode, serviceCodeElement)
{
    var trayCode = getAssocCode(serviceCode, trayAssocCodes);

    if (trayCode)
    {
        serviceCodeElement.value = trayCode;
    }
}

function linkDxAssocCode(serviceCode, dxCodeElement)
{
    var assocCode = getAssocCode(serviceCode, jsAssocCodes);

    if (assocCode)
    {
        dxCodeElement.value = assocCode;
    }
}

function getAssocCode(svcCode, assocCodes)
{
	var retcode = "";
	for (var i = 0; i < assocCodes.length; i++)
	{
		var row = assocCodes[i];

		if (row[0] == svcCode)
		{
			return row[1];
		}
	}
	return retcode;
}

function checkSelectedCode(service)
{
	if (service.checked)
	{
		if (!codeEntered(service.value))
		{
			service.checked = false;
		}
	}
}

function checkSelectedCodes()
{
	var myform = document.forms[0];
	var service = myform.service;

	if (service.length)
	{
		for (var i = 0; i < service.length; i++)
		{
			checkSelectedCode(service[i]);
		}
	}
	else
	{
		checkSelectedCode(service);
	}
}


function HideElementById(ele){
	document.getElementById(ele).style.display='none';
}

function ShowElementById(ele){
	document.getElementById(ele).style.display='';
}
function CheckType(){
	if (document.BillingCreateBillingForm.xml_billtype.value == "ICBC"){
		ShowElementById('ICBC');
		document.BillingCreateBillingForm.mva_claim_code.options[1].selected = true;
	}else{
		HideElementById('ICBC');
		document.BillingCreateBillingForm.mva_claim_code.options[0].selected = true;
	}
         toggleWCB();




}

function callReplacementWebService(url,id){
               var ran_number=Math.round(Math.random()*1000000);
               var params = "demographicNo=<%=bean.getPatientNo()%>&wcb=&rand="+ran_number;  //hack to get around ie caching the page
               new Ajax.Updater(id,url, {method:'get',parameters:params,asynchronous:true}); 
         } 
          <%
          String wcb = "";
          Integer wcbid = (Integer) request.getAttribute("WCBFormId");
          if (wcbid != null){
              wcb = "?wcbid="+wcbid;
          }
          %>


function toggleWCB(){
        <%
        if(!"1".equals(newWCBClaim)){
        %>
       if (document.BillingCreateBillingForm.xml_billtype.value == "WCB"){
        document.BillingCreateBillingForm.fromBilling.value = "true";
       }
       else{
          document.BillingCreateBillingForm.fromBilling.value = "false";
       }
        <%}
        %>

       if (document.BillingCreateBillingForm.xml_billtype.value == "WCB"){
         callReplacementWebService("wcbForms.jsp<%=wcb%>",'wcbForms');
       }

}

function replaceWCB(id){
    oscarLog("In replaceWCB");
  var ur = "wcbForms.jsp?wcbid="+id;
  callReplacementWebService(ur,'wcbForms');
  oscarLog("replaceWCB out == "+ur);
}

function gotoPrivate()
{
	if (document.BillingCreateBillingForm.xml_billtype.value == "Pri")
	{
		document.location.href = "<%=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/" %>billing.do?billRegion=<%=bean.getBillRegion()%>&billForm=Pri&hotclick=&appointment_no=<%=bean.getApptNo()%>&demographic_name=<%=StringEscapeUtils.escapeHtml(bean.getPatientName())%>&demographic_no=<%=bean.getPatientNo()%>&user_no=<%=bean.getCreator()%>&apptProvider_no=<%=bean.getApptProviderNo()%>&providerview=<%=bean.getProviderView()%>&appointment_date=<%=bean.getApptDate()%>&status=<%=bean.getApptStatus()%>&start_time=<%=bean.getApptStart()%>&bNewForm=1&billType=Pri";
	}
	if (document.BillingCreateBillingForm.xml_billtype.value == "MSP")
	{
		document.location.href = "<%=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/" %>billing.do?billRegion=<%=bean.getBillRegion()%>&billForm=<%=OscarProperties.getInstance().getProperty("default_view")%>&hotclick=&appointment_no=<%=bean.getApptNo()%>&demographic_name=<%=StringEscapeUtils.escapeHtml(bean.getPatientName())%>&demographic_no=<%=bean.getPatientNo()%>&user_no=<%=bean.getCreator()%>&apptProvider_no=<%=bean.getApptProviderNo()%>&providerview=<%=bean.getProviderView()%>&appointment_date=<%=bean.getApptDate()%>&status=<%=bean.getApptStatus()%>&start_time=<%=bean.getApptStart()%>&bNewForm=1&billType=MSP";
	}
}

function correspondenceNote(){
	if (document.BillingCreateBillingForm.correspondenceCode.value == "0" ){
		HideElementById('CORRESPONDENCENOTE');
	}else if (document.BillingCreateBillingForm.correspondenceCode.value == "C" ){
		HideElementById('CORRESPONDENCENOTE');
	}else if (document.BillingCreateBillingForm.correspondenceCode.value == "N" ){
	  ShowElementById('CORRESPONDENCENOTE');
	}else {(document.BillingCreateBillingForm.correspondenceCode.value == "B" )
     ShowElementById('CORRESPONDENCENOTE');
	}
}

function quickPickDiagnostic(diagnos){

	if (document.BillingCreateBillingForm.xml_diagnostic_detail1.value == ""){
		document.BillingCreateBillingForm.xml_diagnostic_detail1.value = diagnos;
        }else if ( document.BillingCreateBillingForm.xml_diagnostic_detail2.value == ""){
		document.BillingCreateBillingForm.xml_diagnostic_detail2.value= diagnos;
	}else if ( document.BillingCreateBillingForm.xml_diagnostic_detail3.value == "" ){
		document.BillingCreateBillingForm.xml_diagnostic_detail3.value = diagnos;
	}else{
		alert("All of the Diagnostic Coding Boxes are full");
	}
}

function isNumeric(strString){
        var validNums = "0123456789.";
        var strChar;
        var retval = true;

        for (i = 0; i < strString.length && retval == true; i++){
           strChar = strString.charAt(i);
           if (validNums.indexOf(strChar) == -1){
              retval = false;
           }
        }
         return retval;
   }

function checkUnits(){
	if  (!isNumeric(document.BillingCreateBillingForm.xml_other1_unit.value)){
		alert("Units have be of numeric value");
	        document.BillingCreateBillingForm.xml_other1_unit.focus();
		return false;
	}else if (!isNumeric(document.BillingCreateBillingForm.xml_other2_unit.value)){
		alert("Units have be of numeric value");
                document.BillingCreateBillingForm.xml_other2_unit.focus();
                return false;
	}else if (!isNumeric(document.BillingCreateBillingForm.xml_other3_unit.value)){
		alert("Units have be of numeric value");
                document.BillingCreateBillingForm.xml_other3_unit.focus();
                return false;
	}else if (document.BillingCreateBillingForm.xml_provider.value == "000000"){
	   alert("Please select a Billing Physician");
	   document.BillingCreateBillingForm.xml_provider.focus();
	   return false;
	}
	return true;

}

function checkTextLimit(textField, maximumlength) {
   if (textField.value.length > maximumlength + 1){
      alert("Maximun "+maximumlength+" characters");
   }
   if (textField.value.length > maximumlength){
      textField.value = textField.value.substring(0, maximumlength);
   }
}


function setfocus() {
		  //document.serviceform.xml_diagnostic_code.focus();
		  //document.serviceform.xml_diagnostic_code.select();
		}

function RecordAttachments(Files, File0, File1, File2) {
  window.document.serviceform.elements["File0Data"].value = File0;
  window.document.serviceform.elements["File1Data"].value = File1;
  window.document.serviceform.elements["File2Data"].value = File2;
    window.document.all.Atts.innerText = Files;
  }

var remote=null;

function rs(n,u,w,h,x) {
  var args = "width=" + w +",height=" + h + ",resizable=yes,scrollbars=yes,status=0,top=60,left=30";

  remote=window.open(u,n,args);
  if (remote !== null && remote.opener === null) {
      remote.opener = self;
  }

  if (x === 1) {
      return remote;
  }
}


var awnd=null;
function ScriptAttach() {


  t0 = escape(document.BillingCreateBillingForm.xml_diagnostic_detail1.value);
  t1 = escape(document.BillingCreateBillingForm.xml_diagnostic_detail2.value);
  t2 = escape(document.BillingCreateBillingForm.xml_diagnostic_detail3.value);
  awnd=rs('att','<rewrite:reWrite jspPage="billingDigNewSearch.jsp"/>?name='+t0 + '&name1=' + t1 + '&name2=' + t2 + '&search=',850,740,1);
  awnd.focus();



}

function OtherScriptAttach() {
  t0 = escape(document.BillingCreateBillingForm.xml_other1.value);
  t1 = escape(document.BillingCreateBillingForm.xml_other2.value);
  t2 = escape(document.BillingCreateBillingForm.xml_other3.value);
 // f1 = document.serviceform.xml_dig_search1.value;
 // f2 = escape(document.serviceform.elements["File2Data"].value);
 // fname = escape(document.Compose.elements["FName"].value);
  awnd=rs('att','<rewrite:reWrite jspPage="billingCodeNewSearch.jsp"/>?name='+t0 + '&name1=' + t1 + '&name2=' + t2 + '&search=',820,740,1);
  awnd.focus();
}


function ReferralScriptAttach(elementName) {
     var d = elementName,
         t0 = encodeURIComponent(document.BillingCreateBillingForm.elements[d].value),
         t1 = encodeURIComponent("");
     awnd=rs('att','<rewrite:reWrite jspPage="billingReferCodeSearch.jsp"/>?name='+ t0 + '&name1=' + t1 + '&name2=&search=&formElement=' + d + '&formName=BillingCreateBillingForm', 600, 600,1);
     awnd.focus();
}


function ResearchScriptAttach() {
  t0 = escape(document.serviceform.xml_referral1.value);
  t1 = escape(document.serviceform.xml_referral2.value);

  awnd=rs('att','../<rewrite:reWrite jspPage="billingReferralCodeSearch.jsp"/>?name='+t0 + '&name1=' + t1 +  '&search=',600,600,1);
  awnd.focus();
}

function POP(n,h,v) {
  window.open(n,'OSCAR','toolbar=no,location=no,directories=no,status=yes,menubar=no,resizable=yes,copyhistory=no,scrollbars=yes,width='+h+',height='+v+',top=100,left=200');
}


function grabEnter(event, callback) {
    var enterCode = 13;

    if ((window.event && window.event.keyCode === enterCode) || (event && event.which === enterCode)) {
        callback();
        return false;
    }
}

function reloadPage(init) {  //reloads the window if Nav4 resized
  if (init==true) with (navigator) {if ((appName=="Netscape")&&(parseInt(appVersion)==4)) {
    document.pgW=innerWidth; document.pgH=innerHeight; onresize=reloadPage; }}
  else if (innerWidth!=document.pgW || innerHeight!=document.pgH) location.reload();
}
reloadPage(true);

function findObj(n, d) { //v4.0
  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=findObj(n,d.layers[i].document);
  if(!x && document.getElementById) x=document.getElementById(n); return x;
}
function getOffsetLeft (el) {
	var ol=el.offsetLeft;
	while ((el=el.offsetParent) != null) { ol += el.offsetLeft; }
	return ol;
	}

function getOffsetTop (el) {
	var ot=el.offsetTop;
	while((el=el.offsetParent) != null) { ot += el.offsetTop; }
	return ot;
	}
var objPopup = null;
var shim = null;
function formPopup(event,objectId){
  objPopTrig = document.getElementById(event);
  objPopup = document.getElementById(objectId);
  shim = document.getElementById('DivShim');
 xPos = getOffsetLeft(objPopTrig);
  yPos = getOffsetTop(objPopTrig) + objPopTrig.offsetHeight;
//  objPopup.style.left = xPos + 'px' ;
//  objPopup.style.top = yPos + 'px' ;
  objPopup.style.zIndex = 9999;

  shim.style.width = objPopup.offsetWidth + 2;
  shim.style.height = objPopup.offsetHeight;
  shim.style.top = objPopup.style.top;
  shim.style.left = objPopup.style.left;
  shim.style.zIndex = objPopup.style.zIndex - 1;
  shim.style.display = "block";
  objPopup.style.display = "block";
  shim.style.visibility = 'visible';
  objPopup.style.visibility = 'visible';
}

function formPopupHide(){
  objPopup.style.visibility = 'hidden';
  shim.style.visibility = 'hidden';
  objPopup = null;
  shim = null;
}
//-->

function addCodeToList(svcCode){
    if (myform.xml_other1.value == "") {
        myform.xml_other1.value = svcCode;
        return true;
    }else if (myform.xml_other2.value == "") {
        myform.xml_other2.value = svcCode;
        return true;
    }else if (myform.xml_other3.value == "") {
        myform.xml_other3.value  = svcCode;
        return true;
    }
}

function setCodeToChecked(svcCode){
	var myform = document.forms[0];
	var service = myform.service;
    var codeset = false;

	if (service.length)
	{
		for (var i = 0; i < service.length; i++)
		{
			if (service[i].value == svcCode)
			{
				var wasAbleToAddCode = addCodeToList(svcCode);
				if (wasAbleToAddCode)
				{
					service[i].checked = true;
					codeset = true;
				}
				return;
			}
		}
	}
	else if (service.value == svcCode) {
		var wasAbleToAddCode = addCodeToList(svcCode);
		if(wasAbleToAddCode) {
			service.checked = true;
			codeset = true;
		}
		return;
	}
    
    if(codeEntered(svcCode) == false){
        if (myform.xml_other1.value == "") {
            myform.xml_other1.value = svcCode;
            return;
            //myform.xml_diagnostic_detail1.value = "";
        }else if (myform.xml_other2.value == "") {
            myform.xml_other2.value = svcCode;
            //myform.xml_diagnostic_detail2.value = "";
        }else if (myform.xml_other3.value == "") {
            myform.xml_other3.value  = svcCode;
            //myform.xml_diagnostic_detail3.value = "";
        }
    }
    
    
    
}


function checkifSet(icd9,feeitem,extrafeeitem){
   myform = document.forms[0]; 
   oscarLog("icd9 "+icd9+" ,feeitem "+feeitem+" "+codeEntered(feeitem)+" extrafeeitem "+extrafeeitem+ " "+codeEntered(extrafeeitem));
   if (myform.xml_diagnostic_detail1.value == ""){
       myform.xml_diagnostic_detail1.value = icd9;
   }
   setCodeToChecked(feeitem);
   oscarLog("feeitem did put "+codeEntered(feeitem));
   setCodeToChecked(extrafeeitem);
   
   oscarLog("extra feeitem did put"+codeEntered(extrafeeitem));
}

function updateBillForm(formCode)
{
	var start_hr = jQuery('[name=xml_starttime_hr]').val();
	var start_min = jQuery('[name=xml_starttime_min]').val();
	var end_hr = jQuery('[name=xml_endtime_hr]').val();
	var end_min = jQuery('[name=xml_endtime_min]').val();

	document.location.href = "../../../billing.do?billRegion=<%=bean.getBillRegion()%>&billForm="+formCode+"&hotclick=&appointment_no=<%=bean.getApptNo()%>"+
								"&demographic_name=<%=StringEscapeUtils.escapeHtml(bean.getPatientName())%>&demographic_no=<%=bean.getPatientNo()%>"+
								"&user_no=<%=bean.getCreator()%>&apptProvider_no=<%=bean.getApptProviderNo()%>&providerview=<%=bean.getProviderView()%>"+
								"&appointment_date=<%=bean.getApptDate()%>&status=<%=bean.getApptStatus()%>&start_time=<%=bean.getApptStart()%>"+
								"&bNewForm=1&billType=<%=bean.getBillForm()%>&xml_starttime_hr="+start_hr+"&xml_starttime_min="+start_min+
								"&xml_endtime_hr="+end_hr+"&xml_endtime_min="+end_min;
}

</script>
<link rel="stylesheet" href="../billing/billing.css" type="text/css">
</head>
<%!
  /**
   Generates a string list of option tags in numeric order
   **/
  String generateNumericOptionList(int range,String selected) {
    selected = selected == null?"":selected;
    StringBuffer buff = new StringBuffer();
    buff.append("<option value=''></option>");
    for (int i = 0; i < range; i++) {
      String prefix = i < 10 ? "0" : "";
      String val = prefix + String.valueOf(i);
      String sel = "";
      if(val.equals(selected)){
        sel = "selected";
      }
      buff.append("<option value='" + val + "' " + sel + ">" + val + "</option>");
    }
    return buff.toString();
  }
%>
<body bgcolor="#FFFFFF" text="#000000" rightmargin="0" leftmargin="0" topmargin="10" marginwidth="0" marginheight="0" onLoad="setfocus();CheckType();correspondenceNote();">
<iframe id="DivShim" src="javascript:false;" scrolling="no" frameborder="0" style="position:absolute; top:0px; left:0px; display:none;"></iframe>
<div id="Layer2" class="popUp" style="position:absolute; left:298px; top:26px; width:332px; height:600px;">
  <table width="100%">
    <tr class="popupHeader">
      <th>Dx Code</th>
      <th>Description</th>
      <th>
        <a href="#" onClick="formPopupHide();return false; return false;">X</a>
      </th>
    </tr>
  <%
    boolean flag = false;
    for (int i = 0; i < diaglist.length; i++) {
      flag = !flag;
      String rowClass = flag ? "odd" : "even";
  %>
    <tr class="<%=rowClass%>">
      <td>
        <b>
          <a href="#" onClick="quickPickDiagnostic('<%=diaglist[i].getDiagnosticCode()%>');formPopupHide();return false;return false;"><%=diaglist[i].getDiagnosticCode()%>          </a>
        </b>
      </td>
      <td colspan="2"><%=diaglist[i].getDescription()%>      </td>
    </tr>
  <%}  %>
  </table>
</div>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr bgcolor="#000000">
    <td height="40" width="10%">    </td>
    <td width="90%" align="left">
      <p>
        <font color="#FFFFFF" size="4">
          <b>oscar<bean:message key="billing.bc.title"/></b>
        </font>
      </p>
    </td>
  </tr>
</table>
<div id="Layer1" class="popUp" style="position:absolute; left:1px; top:159px; width:410px; height:300px;">
  <table width="100%">
    <tr class="popupHeader">
      <th>
        Billing
        Form
</th>
      <th>
        <a href="#" onClick="formPopupHide();return false;">x</a>
      </th>
    </tr>
  <%
    flag = false;
    for (int i = 0; i < billformlist.length; i++) {
      flag = !flag;
      String rowClass = flag ? "odd" : "even";
  %>
    <tr class="<%=rowClass%>">
      <td colspan="2">
        <a href="javascript: updateBillForm('<%=billformlist[i].getFormCode()%>')"><%=billformlist[i].getDescription()%></a>
      </td>
    </tr>
  <%}  %>
  </table>
</div>
<html:errors/>
<!-- above here -->
<%
List<String> wcbneeds = (List) request.getAttribute("WCBFormNeeds");
if(wcbneeds != null){%>
<div>
    <h3>WCB Form needs:</h3>
    <ul>
    <%for (String s: wcbneeds) { %>
    <li><bean:message key="<%=s%>"/></li>
    <%}%>
    </ul>
</div>
<%}%>
</h1><html:form action="/billing/CA/BC/CreateBilling" onsubmit="toggleWCB();return checkUnits();">
  <input type="hidden" name="fromBilling" value=""/>

<%
	BillingCreateBillingForm thisForm;
	thisForm = (BillingCreateBillingForm) request.getSession().getAttribute("BillingCreateBillingForm");
	if(thisForm != null)
	{
		sxml_provider = ((String) thisForm.getXml_provider());
		sxml_location = ((String) thisForm.getXml_location());
		sxml_visittype = ((String) thisForm.getXml_visittype());

		if(sxml_location.compareTo("") == 0)
		{
            sxml_provider = getDefaultProvider(bean, OscarProperties.getInstance(), demo);
            thisForm.setXml_provider(sxml_provider);

			sxml_location = OscarProperties.getInstance().getProperty("visitlocation");
			thisForm.setXml_location(sxml_location);

			LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request.getSession());
			sxml_visittype = getDefaultVisitType(loggedInInfo, sxml_provider, bean.getApptNo());
			thisForm.setXml_visittype(sxml_visittype);

			if("YES".equalsIgnoreCase(OscarProperties.getInstance().getProperty("BC_DEFAULT_ALT_BILLING")))
			{
				thisForm.setXml_encounter("8");
			}
		}

		String apDate = thisForm.getXml_appointment_date();
		if(apDate != null && apDate.trim().length() == 0)
		{
			thisForm.setXml_appointment_date(bean.getApptDate());
		}
		if(bean != null && bean.getBillType() != null)
		{
			thisForm.setXml_billtype(bean.getBillType());
		}
		else if(request.getParameter("billType") != null)
		{
			thisForm.setXml_billtype(request.getParameter("billType"));
		}
		if(demo != null && demo.getVer() != null && demo.getVer().equals("66"))
		{
			thisForm.setDependent("66");
		}
		thisForm.setCorrespondenceCode(bean.getCorrespondenceCode());
		oscar.oscarBilling.ca.bc.data.BillingPreferencesDAO dao = SpringUtils.getBean(oscar.oscarBilling.ca.bc.data.BillingPreferencesDAO.class);
		oscar.oscarBilling.ca.bc.data.BillingPreference pref = null;
		//checking for a bug where the passed in provider number is actually "none" rather than numeral 0
		if(oscar.util.StringUtils.isNumeric(thisForm.getXml_provider()))
		{
			pref = dao.getUserBillingPreference(thisForm.getXml_provider());
		}

		// -- autofill referral type code --
		String refType1 = "";
		String refType2 = "";
	    // url parameters override properties file, but not user pref
	    if ( request.getParameter("referral_type_1") != null) { refType1 = request.getParameter("referral_type_1").toUpperCase(); }
	    if ( request.getParameter("referral_type_2") != null) { refType2 = request.getParameter("referral_type_2").toUpperCase(); }

		// OHSUPORT-2718 - default the referral type fields to B=refer By or T=refer To. ''="Select Type"
	    String propReferralPref = oscarProperties.getProperty("auto_populate_billingreferral_type_bc", "").toUpperCase();
		if(refType1.isEmpty()) { refType1 = propReferralPref; }
		if(refType2.isEmpty()) { refType2 = propReferralPref; }

		String propConsultationReferralType = oscarProperties.getProperty("the auto_populate_billing_bc_billingreferral_type1_consult", "");
		String fromConsult = request.getParameter("from_consult");

		// Preferences overrides the properties file settings. Don't override if coming from a consultation and have
		// the auto_populate_billing_bc_billingreferral_type1_consult property set
		if (pref != null && (fromConsult == null || propConsultationReferralType.isEmpty()))
		{
			if (pref.getReferral() == BillingPreference.REFER_TO_CODE)
			{
				refType1 = "T";
				refType2 = "T";
			}
			else if (pref.getReferral() == BillingPreference.REFER_FROM_CODE)
			{
				refType1 = "B";
				refType2 = "B";
			}
		}
		thisForm.setRefertype1(refType1);
		thisForm.setRefertype2(refType2);

		// -- autofill referral physician --
		if(oscarProperties.isPropertyActive("auto_populate_billingreferral_bc"))
		{
			thisForm.setXml_refer1(bean.getReferral1());
		}

		populateInitialDxCodeList(request, thisForm, bean.getApptProviderNo(), bean.getCreator());

		// OHSUPORT-2883 - autofill other codes with specific values (from url)
		if ( request.getParameter("other_code_1") != null ) { thisForm.setXml_other1(request.getParameter("other_code_1")); }
		if ( request.getParameter("other_code_2") != null ) { thisForm.setXml_other2(request.getParameter("other_code_2")); }
		if ( request.getParameter("other_code_3") != null ) { thisForm.setXml_other3(request.getParameter("other_code_3")); }
	}
%>
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <td valign="top" height="221">
        <table width="107%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td>
                <b><bean:message key="billing.patient"/></b>:
            </td>
            <td>
                <u><%=demo.getLastName()%>,<%=demo.getFirstName()%></u>
                <a href="javascript: return false;" onclick="popup(800, 1000, 'billStatus.jsp?lastName=<%=demo.getLastName()%>&firstName=<%=demo.getFirstName()%>&filterPatient=true&demographicNo=<%=demo.getDemographicNo()%>','InvoiceList');return false;">
				<bean:message key="demographic.demographiceditdemographic.msgInvoiceList"/></a>
            </td>
            <td>
                <b><bean:message key="billing.patient.status"/>:<%=demo.getPatientStatus()%></b>    &nbsp;&nbsp;&nbsp;&nbsp;
                <b><bean:message key="billing.patient.roster"/>:<%=demo.getRosterStatus()%></b>
            </td>
            <td width="14%">
              <font size="1" face="Verdana, Arial, Helvetica, sans-serif">
                <strong>
                  <bean:message key="billing.provider.assignedProvider"/>
                </strong>
              </font>
            </td>
            <td width="29%"><%=billform.getProviderName(demo.getProviderNo())%>            </td>
          </tr>
          <tr>
            <td>
                <b><bean:message key="billing.patient.age"/>:</b>
            </td>
            <td>
             <%=demo.getAge()%> 
            </td>
            <td  >
                <a href="#" id="pop1" onClick="formPopup(this.id,'Layer1');return false;"><b><bean:message key="billing.billingform"/></b></a>:<%=billform.getBillingFormDesc(billformlist, bean.getBillForm())%>              
            </td>
            <td width="14%" >  
                  <b><bean:message key="billing.provider.billProvider"/></b>
            </td>
            <td width="29%">
                <html:select property="xml_provider" value="<%=sxml_provider%>" styleId="billing-provider-select">
                  <html:option value="000000">
                    <b>Select Provider</b>
                  </html:option>
                <%for (int j = 0; j < billphysician.length; j++) {                %>
                  <html:option value="<%=billphysician[j].getProviderNo()%>"><%=billphysician[j].getProviderName()%>                  </html:option>
                <%}                %>
                </html:select>
            </td>
          </tr>

          <% if (org.oscarehr.common.IsPropertiesOn.isMultisitesEnable())
          {
          	    SiteDao siteDao = (SiteDao) SpringUtils.getBean(SiteDao.class);
          	    List<Site> sites = siteDao.getAllActiveSites();
          	    List<Site> providerSites = siteDao.getActiveSitesByProviderNo(sxml_provider);
          	    List<Integer> siteIds = new ArrayList<Integer>();

          	    for (Site site : providerSites)
                {
                	siteIds.add(site.getId());
                }

                LoggedInInfo info = LoggedInInfo.getLoggedInInfoFromSession(request);

              AppointmentManager apptManager = SpringUtils.getBean(AppointmentManager.class);
              Appointment appt = apptManager.getAppointment(info, Integer.parseInt(bean.getApptNo()));

                //OscarAppointmentDao apptDao = SpringUtils.getBean(OscarAppointmentDao.class);
                //Appointment appt = apptDao.find(Integer.parseInt(bean.getApptNo()));
          %>
          <tr>
              <td></td>
              <td></td>
              <td></td>
              <td><b>Site</b></td>
              <td>
                  <select name="site" id="site-select">
                      <option value="-1">Select Site</option>
                  <% for (Site site : sites) {
                  	    boolean isSelected = appt != null && appt.getLocation().equals(site.getName());
                  	    boolean isDisabled = !isSelected && !siteIds.contains(site.getId());
                  %>
                      <option value="<%= site.getId()%>" <%=isSelected ? " selected" : ""%> <%=isDisabled ? " disabled" : ""%>><%=site.getName()%></option>
                  <% } %>
                  </select>
              </td>
          </tr>
          <% } %>
          <tr>
            <td>
                  <bean:message key="billing.billingtype"/>
            </td>
            <td width="12%">
                <html:select property="xml_billtype" onchange="CheckType();gotoPrivate();">
                  <html:option value="MSP">Bill MSP</html:option>
                  <html:option value="WCB">Bill WCB</html:option>
                  <html:option value="ICBC">Bill ICBC</html:option>
                  <html:option value="Pri">Private</html:option>
                  <html:option value="DONOTBILL">Do Not Bill</html:option>
                </html:select>
				<span>
					<bean:message key="billing.insurer.pay_patient"/>
					<html:checkbox property="oin_insurer_pay_patient"/>
				</span>
            </td>
            <td width="33%">
                <b>Clarification Code:</b>
                <html:select property="xml_location">
                <%
                  for (int i = 0; i < billlocation.length; i++) {
                    String locationDescription = billlocation[i].getBillingLocation() + "|" + billlocation[i].getDescription();
                %>
                  <html:option value="<%=locationDescription%>"><%=billlocation[i].getDescription()%>                  </html:option>
                <%}                %>
                </html:select>            
            </td>
            <td width="14%">
                <b>Service Location</b>
            </td>
            <td width="29%">
	            <select name="xml_visittype" id="xml_visittype">
                <%
                  for (int i = 0; i < billvisit.length; i++) {
                      String visitTypeDescription = billvisit[i].getVisitType() + "|" + billvisit[i].getDescription();
                      boolean isDefault = sxml_visittype.equals(visitTypeDescription);
                %>
		            <option value="<%= visitTypeDescription%>" <%=isDefault ? " selected" : ""%> ><%=visitTypeDescription%></option>

                <%}%>
                </select>
            </td>
          </tr>
        </table>
        <!--
          <table>
          <tr>
          <td>Service Date:</td>    <td>	 Service to date: </td>  <td> 	 After Hours: </td>  <td>	 Time Call (HHMM 24hr): </td>  <td> 	 Start (HHMM 24hr): </td>  <td> 	 End (HHMM 24hr): </td>   <td>	 Dependent: </td>  <td>	 Sub Code: </td>  <td>	 Alt. Payment:</td>
          </tr>
          </table>
        -->
        <table width="100%" border=0>
          <tr>
            <td>
              <a href="javascript: function myFunction() {return false; }" id="hlSDate">
                  <strong><bean:message key="billing.servicedate"/>:</strong>
              </a>
              <html:text property="xml_appointment_date" size="10" readonly="true" styleId="xml_appointment_date"/>
              <!--<a id="hlSDate"><img title="Calendar" src="../../../images/cal.gif" alt="Calendar" border="0" /></a>-->
            </td>
            <td>
              <a href="javascript: function myFunction() {return false; }" id="serviceToDate">
                  <strong>Service to date:</strong>
              </a>
              <html:text property="service_to_date" size="2" maxlength="2" styleId="service_to_date"/>
            </td>
            <td>              After Hours:
              <html:select property="afterHours">
                <html:option value="0">No</html:option>
                <html:option value="E">Evening</html:option>
                <html:option value="N">Night</html:option>
                <html:option value="W">Weekend</html:option>
              </html:select>
            </td>
            <td title="(HHMM 24hr):">
                <strong>Time Call:</strong>
              <html:text property="timeCall" size="4" maxlength="4"/>
            </td>
            <td>
                <strong>
                  <bean:message key="billing.servicedate.starttime"/>
                </strong>
              <table cellpadding="0" cellspacing="0" border="0">
                <tr>
                  <td>
                    <select name="xml_starttime_hr"><%=generateNumericOptionList(24, bean.getStartTimeHr())%>                    </select>
                  </td>
                  <td>
                    <select name="xml_starttime_min"><%=generateNumericOptionList(61,bean.getStartTimeMin())%>                    </select>
                  </td>
                </tr>
              </table>
            </td>
            <td>
                <strong>
                  <bean:message key="billing.servicedate.endtime"/>
                </strong>
              <table cellpadding="0" cellspacing="0" border="0">
                <tr>
                  <td>
                    <select name="xml_endtime_hr"><%=generateNumericOptionList(24,bean.getEndTimeHr())%>                    </select>
                  </td>
                  <td>
                    <select name="xml_endtime_min"><%=generateNumericOptionList(61,bean.getEndTimeMin())%>                    </select>
                  </td>
                </tr>
              </table>
            </td>
            <td>              Dependent:
              <html:select property="dependent">
                <html:option value="00">No</html:option>
                <html:option value="66">Yes</html:option>
              </html:select>
            </td>
            <td title="Submission Code">              Sub Code:
              <html:select property="submissionCode">
                <html:option value="0">O - Normal</html:option>
                <html:option value="D">D - Duplicate</html:option>
                <html:option value="E">E - Debit</html:option>
                <html:option value="C">C - Subscriber Coverage</html:option>
                <html:option value="R">R - Resubmitted</html:option>
                <html:option value="I">I - ICBC Claim > 90 Days</html:option>
                <html:option value="A">A - Requested Preapproval</html:option>
                <html:option value="W">W - WCB Rejected Claim</html:option>
                <html:option value="X">X - Resubmitting Refused / Partially Paid Claim</html:option>
              </html:select>
            </td>
            <td>
                <b>Payment Method:</b>
            <%
              ArrayList types = billform.getPaymentTypes();
              if ("Pri".equalsIgnoreCase(thisForm.getXml_billtype())) {
                for (int i = 0; i < types.size(); i++) {
                  PaymentType item = (PaymentType) types.get(i);
                  if (item.getId().equals("6") || item.getId().equals("8")) {
                    types.remove(i);
                    break;
                  }
                }
              }
              else {
                for (int i = 0; i < types.size(); i++) {
                  PaymentType item = (PaymentType) types.get(i);
                  if (!item.getId().equals("6") && !item.getId().equals("8")) {
                    types.remove(i);
                    i = i - 1;
                  }
                }
              }
              request.setAttribute("paymentMethodList", types);
            %>
              <html:select property="xml_encounter">
                <html:options collection="paymentMethodList" property="id" labelProperty="paymentType"/>
              </html:select>
            </td>
            <td nowrap>
              <strong>Facility</strong>
                <table>
                    <tr>
                        <td title="Facilty Number">No. <html:text property="facilityNum" size="7" maxlength="7" styleId="facility-number"/></td>
                        <td title="Facilty Sub Number">Sub <html:text property="facilitySubNum" size="5" maxlength="5"/></td>
                      </tr>
                </table>
            </td>
          </tr>
        </table>
        <div style="display: none">
          <table>
            <tr>
              <td>
                <bean:message key="billing.admissiondate"/>
                :
                <html:text property="xml_vdate" readonly="true" value="" size="10" styleId="xml_vdate"/>
                <a id="hlADate">
                  <img title="Calendar" src="../../../images/cal.gif" alt="Calendar" border="0"/>
                </a>
              </td>
            </tr>
          </table>
        </div>
<script language='javascript'>
           Calendar.setup({inputField:"xml_appointment_date",ifFormat:"%Y-%m-%d",showsTime:false,button:"hlSDate",singleClick:true,step:1});
           //Calendar.setup({inputField:"xml_appointment_date", ifFormat:""%d/%m/%Y",",button:"hlSDate", align:"Bl", singleClick:true});
           Calendar.setup({inputField:"xml_vdate",ifFormat:"%Y-%m-%d",showsTime:false,button:"hlADate",singleClick:true,step:1});
           Calendar.setup({inputField:"service_to_date", ifFormat:"%d",button:"serviceToDate", align:"Bl", singleClick:true});

        </script>
        <div align="left">        </div>
        <div id="ICBC">
          <table width="100%">
            <tr>
              <td>                ICBC Claim No:
                <html:text property="icbc_claim_no" maxlength="8"/>
                MVA:
                <html:select property="mva_claim_code">
                  <html:option value="N">No</html:option>
                  <html:option value="Y">Yes</html:option>
                </html:select>
              </td>
            </tr>
          </table>
        </div>
        <table width="100%" border="2" cellspacing="0" cellpadding="0" height="137">
          <tr>
            <td valign="top" width="33%">
              <table width="100%" border="1" cellspacing="0" cellpadding="0" height="0">
                <tr bgcolor="#CCCCFF">
                  <td width="25%">
                    <b>                    </b>
                    <div align="left">
                        <b>
                          <font size="1" color="#000000"><%=group1Header%>                          </font>
                        </b>
                    </div>
                  </td>
                  <td width="61%" bgcolor="#CCCCFF">
                    <b>
                      <font face="Verdana, Arial, Helvetica, sans-serif" size="1" color="#000000">
                        <bean:message key="billing.service.desc"/>
                      </font>
                    </b>
                  </td>
                  <td width="14%">
                    <div align="right">
                      <b>$ <bean:message key="billing.service.fee"/></b>
                    </div>
                  </td>
                </tr>
              <%for (int i = 0; i < billlist1.length; i++) {              %>
                <tr bgcolor>
                <%String svcCall = "addSvcCode(this, '" + billlist1[i].getServiceCode() + "')";                %>
                  <td width="25%" height="14">
                    <b>                    </b>
                    <font face="Verdana, Arial, Helvetica, sans-serif">
                      <html:multibox property="service" value="<%=billlist1[i].getServiceCode()%>" onclick="<%=svcCall%>"/>
                      <font size="1"><%=billlist1[i].getServiceCode()%>                      </font>
                    </font>
                  </td>
                  <td width="61%" height="14">
                    <%=billlist1[i].getDescription()%> 
                  </td>
                  <td width="14%" height="14">
                    <div align="right">
                      <%=billlist1[i].getPrice()%>                 
                    </div>
                  </td>
                </tr>
              <%}              %>
              </table>
              <table width="100%" border="0" cellpadding="2" cellspacing="2" bgcolor="#CC0000">
                <tr>
                  <td width="91%" valign="top">
                    <table width="100%" border="0" cellspacing="0" cellpadding="0" height="67" bgcolor="#EEEEFF">
                      <tr>
                        <td>
                          <b>
                           
                              <bean:message key="billing.referral.doctor"/>
                           
                          </b>
                        </td>
                        <td>
                          <b>
                              <bean:message key="billing.referral.type"/>
                          </b>
                        </td>
                      </tr>
                      <tr>
                        <td>
                            <html:text property="xml_refer1" size="40"/>
                        </td>
                        <td>
                            <html:select property="refertype1">
                              <html:option value="">Select Type</html:option>
                              <html:option value="T">Refer To</html:option>
                              <html:option value="B">Refer By</html:option>
                            </html:select>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2">
                          <a href="javascript:ReferralScriptAttach('xml_refer1')">
                            <img src="../../../images/search_code.jpg" border="0">
                          </a>
                        </td>
                      </tr>
                      <tr>
                        <td>
                            <html:text property="xml_refer2" size="40"/>
                        </td>
                        <td>
                            <html:select property="refertype2">
                              <html:option value="">Select Type</html:option>
                              <html:option value="T">Refer To</html:option>
                              <html:option value="B">Refer By</html:option>
                            </html:select>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2">
                          <a href="javascript:ReferralScriptAttach('xml_refer2')">
                            <img src="../../../images/search_code.jpg" border="0">
                          </a>
                        </td>
                      </tr>
                    </table>
                  </td>
                  <td width="9%">
                   
                  </td>
                </tr>
              </table>
              
            </td>
            <td valign="top" width="31%">
              <table width="100%" border="1" cellspacing="0" cellpadding="0">
                <tr bgcolor="#CCCCFF">
                  <td width="21%">
                    <b>                    </b>
                    <div align="left">
                        <b>
                          <font size="1" color="#000000"><%=group2Header%>                          </font>
                        </b>
                    </div>
                  </td>
                  <td width="60%" bgcolor="#CCCCFF">
                    <b><bean:message key="billing.service.desc"/></b>
                  </td>
                  <td width="19%">
                    <div align="right">
                      <b>$<bean:message key="billing.service.fee"/></b>
                    </div>
                  </td>
                </tr>
              <%for (int i = 0; i < billlist2.length; i++) {              %>
                <tr bgcolor>
                <%String svcCall = "addSvcCode(this, '" + billlist2[i].getServiceCode() + "')";                %>
                  <td width="21%" height="14">  d
                      <html:multibox property="service" value="<%=billlist2[i].getServiceCode()%>" onclick="<%=svcCall%>"/>
                      <%=billlist2[i].getServiceCode()%>
                  </td>
                  <td width="60%" height="14">
                   <%=billlist2[i].getDescription()%>
                  </td>
                  <td width="19%" height="14">
                    <div align="right">
                     <%=billlist2[i].getPrice()%> 
                    </div>
                  </td>
                </tr>
              <%}              %>
              </table>
              <table width="100%" height="105" border="0" cellpadding="2" cellspacing="2" bgcolor="#999900">
                <tr>
                  <td width="91%" valign="top">
                    <table width="100%" border="0" cellspacing="0" cellpadding="0" height="67" bgcolor="#EEEEFF">
                      <tr>
                        <td width="85%">
                          <b><bean:message key="billing.service.otherservice"/></b>
                        </td>
                        <td width="15%">
                          <b><bean:message key="billing.service.unit"/></b>
                        </td>
                      </tr>
                      <tr>
                        <td nowrap>
                            <html:text property="xml_other1" onblur="checkSelectedCodes()" size="40" onkeypress="return grabEnter(event, OtherScriptAttach)"/>
                            <input type="button" value=".5" onClick="$('xml_other1_unit').value = '0.5'"/>
                        </td>
                        <td>
                            <html:text property="xml_other1_unit" size="6" maxlength="6" styleId="xml_other1_unit"/>
                        </td>
                      </tr>
                      <tr>
                        <td nowrap>
                            <html:text property="xml_other2" onblur="checkSelectedCodes()" size="40" onkeypress="return grabEnter(event, OtherScriptAttach)"/>
                            <input type="button" value=".5" onClick="$('xml_other2_unit').value = '0.5'"/>
                        </td>
                        <td>
                            <html:text property="xml_other2_unit" size="6" maxlength="6" styleId="xml_other2_unit"/>
                        </td>
                      </tr>
                      <tr>
                        <td nowrap>
                            <html:text property="xml_other3" onblur="checkSelectedCodes()" size="40" onkeypress="return grabEnter(event, OtherScriptAttach)"/>
                            <input type="button" value=".5" onClick="$('xml_other3_unit').value = '0.5'"/>
                        </td>
                        <td>
                            <html:text property="xml_other3_unit" size="6" maxlength="6" styleId="xml_other3_unit"/>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2">
                          <a href="javascript:OtherScriptAttach()">
                            <img src="../../../images/search_code.jpg" border="0">
                          </a>
                        </td>
                      </tr>
                    </table>
                  </td>
                  <td width="9%">
                 &nbsp;
                  </td>
                </tr>
              </table>
            </td>
            <td valign="top" width="36%" rowspan="2">
              <table width="100%" border="1" cellspacing="0" cellpadding="0" height="0">
                <tr bgcolor="#CCCCFF">
                  <td width="25%">
                    <div align="left">
                        <b><%=group3Header%></b>
                    </div>
                  </td>
                  <td width="61%" bgcolor="#CCCCFF">
                    <b><bean:message key="billing.service.desc"/></b>
                  </td>
                  <td width="14%">
                    <div align="right">
                      <b><bean:message key="billing.service.fee"/></b>
                    </div>
                  </td>
                </tr>
              <%for (int i = 0; i < billlist3.length; i++) {              %>
                <tr bgcolor>
                <%String svcCall = "addSvcCode(this, '" + billlist3[i].getServiceCode() + "')";                %>
                  <td width="25%" height="14">
                      <html:multibox property="service" value="<%=billlist3[i].getServiceCode()%>" onclick="<%=svcCall%>"/>
                      <%=billlist3[i].getServiceCode()%>
                  </td>
                  <td width="61%" height="14">
                    <%=billlist3[i].getDescription()%>
                  </td>
                  <td width="14%" height="14">
                    <div align="right">
                      <%=billlist3[i].getPrice()%>
                    </div>
                  </td>
                </tr>
              <%}              %>
              </table>
              <!-- ONSCREEN DX CODE DISPLAY -->
              <table width="100%" border="3" cellpadding="2" cellspacing="2" bgcolor="#CCCCFF">
                <tr>
                  <td  height="103" valign="top" width="10">
                    <table border="2" cellspacing="0" cellpadding="0" height="67" bgcolor="#EEEEFF">
                      <tr>
                        <th align="left">
                              <a href="#" id="pop2" onClick="formPopup(this.id,'Layer2');return false;">
                                <bean:message key="billing.diagnostic.code"/>
                              </a>   
                        </th>
                      </tr>
                      <tr>
                        <td>
                            <html:text property="xml_diagnostic_detail1" size="25" onkeypress="return grabEnter(event, ScriptAttach)"/>
                        </td>
                      </tr>
                      <tr>
                        <td>
                            <html:text property="xml_diagnostic_detail2" size="25" onkeypress="return grabEnter(event, ScriptAttach)"/>
                        </td>
                      </tr>
                      <tr>
                        <td>
                            <html:text property="xml_diagnostic_detail3" size="25" onkeypress="return grabEnter(event, ScriptAttach)"/>
                        </td>
                      </tr>
                      <tr>
                        <td>
                            <a href="javascript:ScriptAttach()"><img src="../../../images/search_dx_code.jpg" border="0"></a> 
                        </td>
                      </tr>
                    </table>
                  </td>
                  <td align="left" width="*" valign="top">
                      <div id="DX_REFERENCE"></div>
                       <oscar:oscarPropertiesCheck property="BILLING_DX_REFERENCE" value="yes">
                         <script type="text/javascript">
                         function getDxInformation(origRequest){
                               var url = "DxReference.jsp";
                               var ran_number=Math.round(Math.random()*1000000);
                               var params = "demographicNo=<%=bean.getPatientNo()%>&rand="+ran_number;  //hack to get around ie caching the page
                               //alert(params);
                               new Ajax.Updater('DX_REFERENCE',url, {method:'get',parameters:params,asynchronous:true}); 
                               //alert(origRequest.responseText);
                         }
                         getDxInformation();
                         </script>
                       </oscar:oscarPropertiesCheck>
                     
                  </td>
                </tr>
              </table>
              <!-- ONSCREEN DX CODE DISPLAY END-->
              
              <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td >
                    <font size="-2">
                      <strong>                        Short Claim Note:
                        &nbsp;
                      </strong>
                    </font>
                    <html:text property="shortClaimNote" size="23" maxlength="20"/>
                  </td>
                  <td align="right">
                        <input type="checkbox" name="ignoreWarn"/> Ignore Warnings &nbsp;
                  </td>
                </tr>
                
                <tr>
                  <td align="left">
                    <html:select property="correspondenceCode" onchange="correspondenceNote();">
                      <html:option value="0">No Correspondence</html:option>
                      <html:option value="N">Electronic Correspondence</html:option>
                      <html:option value="C">Paper Correspondence</html:option>
                      <html:option value="B">Both</html:option>
                    </html:select>
                  </td>
                  <td align="right">
                    <input type="submit" name="Submit" value="Continue">
                    <input type="button" name="Button" value="Cancel" onClick="window.close();">                 
                  </td>
                </tr>
                <tr>
                  <td colspan="2" valign="top">
                    <div id="CORRESPONDENCENOTE">                      &nbsp;
                      <html:textarea cols="60" rows="5" property="notes" onkeyup="checkTextLimit(this.form.notes,400);">                      </html:textarea>
                      <br>
                      &nbsp;
                      400 characters max.
                    </div>
                    <br/>
                    <hr/>
                    <!-- br/>
                    <br/ -->
                    <div style="background-color: #CCCCFF;">
                      <div style="background-color : #EEEEFF;">Billing Notes (Notes are for internal use and will not be sent to MSP)</div>
                      &nbsp;
                      <html:textarea cols="60" rows="5" property="messageNotes">                      </html:textarea>
                    </div>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
          <tr valign="top">
              <td colspan="2"><div id="wcbForms" style="float:left;"/></td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</html:form>
<p>&nbsp;</p>

</body>
</html>
