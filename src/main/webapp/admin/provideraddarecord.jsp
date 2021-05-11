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
    String curProvider_no = (String) session.getAttribute("user");

    boolean isSiteAccessPrivacy=false;
    boolean authed=true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_admin,_admin.userAdmin" rights="r" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect("../securityError.jsp?type=_admin&type=_admin.userAdmin");%>
</security:oscarSec>
<%
	if(!authed) {
		return;
	}
%>


<security:oscarSec objectName="_site_access_privacy" roleName="<%=roleName$%>" rights="r" reverse="false">
	<%isSiteAccessPrivacy=true; %>
</security:oscarSec>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ page errorPage="errorpage.jsp"%>
<%@ page import="oscar.log.LogAction,oscar.log.LogConst"%>

<%@ page import="org.oscarehr.common.dao.SiteDao"%>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@ page import="org.oscarehr.common.model.Site"%>

<%@ page import="oscar.oscarProvider.data.ProviderBillCenter"%>
<%@ page import="org.apache.commons.lang.StringUtils"%>

<%@ page import="org.oscarehr.util.SpringUtils" %>
<%@ page import="org.oscarehr.PMmodule.dao.ProviderDao" %>
<%@ page import="org.oscarehr.common.model.ProviderSite"%>
<%@ page import="org.oscarehr.common.model.ProviderSitePK"%>
<%@ page import="org.oscarehr.common.dao.ProviderSiteDao"%>
<%@ page import="org.oscarehr.provider.service.ProviderRoleService" %>
<%@ page import="org.oscarehr.provider.model.ProviderData" %>
<%@ page import="org.oscarehr.provider.service.ProviderService" %>
<%@ page import="org.oscarehr.providerBilling.model.ProviderBilling" %>
<%@ page import="oscar.OscarProperties" %>
<%@ page import="oscar.MyDateFormat" %>
<%@ page import="oscar.SxmlMisc" %>
<%@ page import="oscar.oscarDB.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>

<%
	ProviderDao providerDao = (ProviderDao)SpringUtils.getBean("providerDao");
    ProviderService providerService = SpringUtils.getBean(ProviderService.class);
	ProviderSiteDao providerSiteDao = SpringUtils.getBean(ProviderSiteDao.class);
	boolean alreadyExists=false;
%>
<html:html locale="true">
<head>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
<title><bean:message key="admin.provideraddrecord.title" /></title>
<link rel="stylesheet" href="../web.css">
</head>

<body bgproperties="fixed" topmargin="0" leftmargin="0" rightmargin="0">
<center>
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr bgcolor="#486ebd">
		<th><font face="Helvetica" color="#FFFFFF">
			<bean:message key="admin.provideraddrecord.description" />
		</font></th>
	</tr>
</table>
<%
boolean isOk = false;
int retry = 0;
String curUser_no = (String)session.getAttribute("user");

ProviderData provider = new ProviderData();

provider.set(request.getParameter("provider_no"));
provider.setLastName(request.getParameter("last_name"));
provider.setFirstName(request.getParameter("first_name"));
provider.setProviderType(request.getParameter("provider_type"));
provider.setSpecialty(request.getParameter("specialty"));
provider.setTeam(request.getParameter("team"));
provider.setSex(request.getParameter("sex"));
provider.setDob(MyDateFormat.getSysDate(request.getParameter("dob")));
provider.setAddress(request.getParameter("address"));
provider.setPhone(request.getParameter("phone"));
provider.setWorkPhone(request.getParameter("workphone"));
provider.setEmail(request.getParameter("email"));
provider.setOhipNo(request.getParameter("ohip_no"));
provider.setRmaNo(request.getParameter("rma_no"));
provider.setBillingNo(request.getParameter("billing_no"));
provider.setHsoNo(request.getParameter("hso_no"));
provider.setAlbertaTakNo(StringUtils.trimToNull(request.getParameter("alberta_tak_no")));
provider.setAlbertaConnectCareId(StringUtils.trimToNull(request.getParameter("alberta_connect_care_provider_id")));
provider.setOntarioLifeLabsId(StringUtils.trimToNull(request.getParameter("ontario_lifelabs_id")));
provider.setStatus(request.getParameter("status"));
provider.setComments(SxmlMisc.createXmlDataString(request, "xml_p"));
provider.setProviderActivity(request.getParameter("provider_activity"));
provider.setPractitionerNo(request.getParameter("practitionerNo"));
provider.setLastUpdateUser((String)session.getAttribute("user"));
provider.setLastUpdateDate(new java.util.Date());
provider.setSupervisor(StringUtils.trimToNull(request.getParameter("supervisor")));
provider.setSuperAdmin(false);


ProviderBilling providerBilling = new ProviderBilling();

if (request.getParameter("bc_bcp_eligible") != null)
{
    providerBilling.setBcBCPEligible(request.getParameter("bc_bcp_eligible").equals("1"));
}

String serviceLocationCode = request.getParameter("bc_service_location_code");
if (serviceLocationCode != null)
{
	providerBilling.setBcServiceLocationCode(StringUtils.trimToNull(serviceLocationCode));
}

provider.setBillingOpts(providerBilling);

String albertaEDeliveryIds = StringUtils.trimToNull(request.getParameter("alberta_e_delivery_ids"));
// Only strip non-numeric characters if we are on an Alberta instance
if(albertaEDeliveryIds != null && OscarProperties.getInstance().getProperty("instance_type").equals("AB")) {
	albertaEDeliveryIds = albertaEDeliveryIds.replaceAll("[^0-9.,]", ""); // strip non-numbers
}
provider.setAlbertaEDeliveryIds(albertaEDeliveryIds);

//multi-office provide id formalize check, can be turn off on properties multioffice.formalize.provider.id
boolean isProviderFormalize = true;
String  errMsgProviderFormalize = "admin.provideraddrecord.msgAdditionFailure";
Integer min_value = 0;
Integer max_value = 0;

if (org.oscarehr.common.IsPropertiesOn.isProviderFormalizeEnable()) {

	String StrProviderId = request.getParameter("provider_no");
	OscarProperties props = OscarProperties.getInstance();

	String[] provider_sites = {};

	// get provider id ranger
	if (request.getParameter("provider_type").equalsIgnoreCase("doctor")) {
		//provider is doctor, get provider id range from Property
		min_value = new Integer(props.getProperty("multioffice.formalize.doctor.minimum.provider.id", ""));
		max_value = new Integer(props.getProperty("multioffice.formalize.doctor.maximum.provider.id", ""));
	}
	else {
		//non-doctor role
		provider_sites = request.getParameterValues("sites");
		provider_sites = (provider_sites == null ? new String[] {} : provider_sites);

		if (provider_sites.length > 1) {
			//non-doctor can only have one site
			isProviderFormalize = false;
			errMsgProviderFormalize = "admin.provideraddrecord.msgFormalizeProviderIdMultiSiteFailure";
		}
		else {
			if (provider_sites.length == 1) {
				//get provider id range from site
				String provider_site_id =  provider_sites[0];
				SiteDao siteDao = (SiteDao)WebApplicationContextUtils.getWebApplicationContext(application).getBean("siteDao");
				Site provider_site = siteDao.getById(new Integer(provider_site_id));
				min_value = provider_site.getProviderIdFrom();
				max_value = provider_site.getProviderIdTo();
			}
		}
	}
	if (isProviderFormalize) {
		try {
			    Integer providerId = Integer.parseInt(StrProviderId);
			    if (request.getParameter("provider_type").equalsIgnoreCase("doctor") ||  provider_sites.length == 1) {
				    if  (!(providerId >= min_value && providerId <=max_value)) {
				    	// providerId is not in the range
						isProviderFormalize = false;
						errMsgProviderFormalize = "admin.provideraddrecord.msgFormalizeProviderIdFailure";
				    }
			    }
		} catch(NumberFormatException e) {
			//providerId is not a number
			isProviderFormalize = false;
			errMsgProviderFormalize = "admin.provideraddrecord.msgFormalizeProviderIdFailure";
		}
	}
}

if (!org.oscarehr.common.IsPropertiesOn.isProviderFormalizeEnable() || isProviderFormalize) {

DBPreparedHandler dbObj = new DBPreparedHandler();

boolean isDefaultRoleNameExist = true;

	// check if the provider no need to be auto generated
  if (OscarProperties.getInstance().isProviderNoAuto())
  {
  	provider.setProviderNo(Integer.parseInt(dbObj.getNewProviderNo()));
  }
  
  if(providerDao.providerExists(provider.getId())) {
	  isOk=false;
	  alreadyExists=true;
  }
  	else
	{
		providerService.saveProvider(provider);
 	 	isOk=true;

 	 	// make newly added provider by default a 'doctor' and 'primary' role
		ProviderRoleService providerRoleService = SpringUtils.getBean(ProviderRoleService.class);

		isDefaultRoleNameExist = providerRoleService.setDefaultRoleForNewProvider(provider.getProviderNo());
	}

    if (org.oscarehr.common.IsPropertiesOn.isMultisitesEnable() && isOk)
    {
        List<String> sites = new ArrayList<String>();
        List<String> bcpSites = new ArrayList<String>();

        if (request.getParameterValues("sites") != null)
        {
            sites.addAll(Arrays.asList(request.getParameterValues("sites")));
        }

        if (request.getParameterValues("sitesBCP") != null)
        {
            bcpSites.addAll(Arrays.asList(request.getParameterValues("sitesBCP")));
        }

        for (String siteString : sites)
        {
            Integer siteId = Integer.parseInt(siteString);

            ProviderSite provSite = new ProviderSite();
            provSite.setId(new ProviderSitePK(provider.getId(), siteId));


            provSite.setBcBCPEligible(bcpSites.contains(siteString));

            providerSiteDao.persist(provSite);
        }
    }

if (isOk) {
	String proId = provider.getPractitionerNo();
	String ip = request.getRemoteAddr();
	LogAction.addLogEntry(curUser_no, LogConst.ACTION_ADD, LogConst.CON_PROVIDER, LogConst.STATUS_SUCCESS, proId, ip);

	ProviderBillCenter billCenter = new ProviderBillCenter();
	billCenter.addBillCenter(request.getParameter("provider_no"),request.getParameter("billcenter"));

%>
<h1><bean:message key="admin.provideraddrecord.msgAdditionSuccess" />
</h1>
	<%
		/*
			if default role name 'doctor' not exist, add the record to provider table,
		 	but let user know that they will need to assign role manually
		 */
	  if(!isDefaultRoleNameExist)
	  {
	%>
		<h3 style="color:red">
			<bean:message key="admin.provideraddrecord.msgNoDefaultRoleName" />
		</h3>
	<%
	  }


  } else {
%>
<h1><bean:message key="admin.provideraddrecord.msgAdditionFailure" /></h1>
<%
	if(alreadyExists) {
		%><h2><bean:message key="admin.provideraddrecord.msgAlreadyExists" /></h2><%
	}

  }

}
else {
		if 	(!isProviderFormalize) {
	%>
		<h1><bean:message key="<%=errMsgProviderFormalize%>" /> </h1>
		Provider # range from : <%=min_value %> To : <%=max_value %>
	<%
		}
	}
%>
</center>
</body>
</html:html>
