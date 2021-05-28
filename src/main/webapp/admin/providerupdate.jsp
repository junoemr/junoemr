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
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%
	String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
	boolean authed=true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_admin,_admin.userAdmin" rights="r" reverse="<%=true%>">
	<%response.sendRedirect("../securityError.jsp?type=_admin&type=_admin.userAdmin");%>
	<%authed=false; %>
</security:oscarSec>

<%
	if(!authed) {
		return;
	}
%>
<%@page import="oscar.oscarProvider.data.ProviderBillCenter" errorPage="errorpage.jsp"%>
<%@page import="org.oscarehr.common.dao.SiteDao"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@page import="org.oscarehr.common.model.Site"%>
<%@page import="org.oscarehr.common.model.Provider"%>
<%@page import="org.oscarehr.PMmodule.dao.ProviderDao"%>
<%@page import="org.oscarehr.common.model.ProviderArchive"%>
<%@page import="org.oscarehr.common.dao.ProviderArchiveDao"%>
<%@page import="org.oscarehr.util.SpringUtils"%>
<%@page import="org.oscarehr.common.model.ProviderSite"%>
<%@page import="org.oscarehr.common.model.ProviderSitePK"%>
<%@page import="org.oscarehr.common.dao.ProviderSiteDao"%>
<%@page import="org.oscarehr.common.dao.UserPropertyDAO"%>
<%@page import="org.oscarehr.common.model.UserProperty"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@ page import="org.oscarehr.managers.SecurityInfoManager" %>
<%@ page import="org.oscarehr.provider.model.ProviderData" %>
<%@ page import="org.oscarehr.providerBilling.model.ProviderBilling" %>
<%@ page import="org.oscarehr.provider.service.ProviderService" %>
<%@ page import="org.springframework.beans.BeanUtils" %>
<%@ page import="oscar.log.LogAction" %>
<%@ page import="oscar.log.LogConst" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="oscar.MyDateFormat" %>
<%@ page import="oscar.SxmlMisc" %>
<%@ page import="oscar.OscarProperties" %>
<%
	ProviderDao providerDao = (ProviderDao)SpringUtils.getBean("providerDao");
	ProviderSiteDao providerSiteDao = SpringUtils.getBean(ProviderSiteDao.class);
%>
<html:html locale="true">
	<head>
		<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
		<title><bean:message key="admin.providerupdate.title" /></title>
	</head>
	<link rel="stylesheet" href="../web.css" />


	<body bgproperties="fixed" topmargin="0" leftmargin="0" rightmargin="0">
	<center>
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr bgcolor="#486ebd">
				<th><font face="Helvetica" color="#FFFFFF"><bean:message
						key="admin.providerupdate.description" /></font></th>
			</tr>
		</table>

<%
  ProviderBillCenter billCenter = new ProviderBillCenter();
  billCenter.updateBillCenter(request.getParameter("provider_no"),request.getParameter("billcenter"));
  SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);


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


if (securityInfoManager.userCanModify(request.getParameter("current_user"),request.getParameter("provider_no")))
{
	if (!org.oscarehr.common.IsPropertiesOn.isProviderFormalizeEnable() || isProviderFormalize)
	{
		ProviderService providerService = SpringUtils.getBean(ProviderService.class);
		ProviderData provider = providerService.getProviderEager(request.getParameter("provider_no"));

		if (provider != null)
		{
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
			provider.setLastUpdateUser((String) session.getAttribute("user"));
			provider.setLastUpdateDate(new Date());

			String supervisor = request.getParameter("supervisor");
			if (supervisor.equalsIgnoreCase("null"))
			{
				supervisor = null;
			}
			String albertaEDeliveryIds = StringUtils.trimToNull(request.getParameter("alberta_e_delivery_ids"));
			// Only strip non-numeric characters if we are on an Alberta instance
			if (albertaEDeliveryIds != null && OscarProperties.getInstance().getProperty("instance_type").equals("AB"))
			{
				albertaEDeliveryIds = albertaEDeliveryIds.replaceAll("[^0-9.,]", ""); // strip non-numbers
			}
			provider.setAlbertaEDeliveryIds(albertaEDeliveryIds);
			provider.setSupervisor(StringUtils.trimToNull(supervisor));


			if (provider.getBillingOpts() == null || provider.getBillingOpts().getId() == null)
			{
				provider.setBillingOpts(new ProviderBilling());
			}

			// Since each provincial billing option is only presented to the user if the appropriate province is selected
			// we should check that each parameter is present before writing, otherwise we'll overwrite existing data

			String bcBCPEligible = request.getParameter("bc_bcp_eligible");
			if (bcBCPEligible != null)
			{
				provider.getBillingOpts().setBcBCPEligible(Integer.parseInt(bcBCPEligible) == 1);
			}

			String bcServiceLocationCode = request.getParameter("bc_service_location_code");
			if (bcServiceLocationCode != null)
            {
            	// In this case, empty string corresponds to "None set", so it should be null in the db.
            	provider.getBillingOpts().setBcServiceLocationCode(StringUtils.trimToNull(bcServiceLocationCode));
            }

			UserPropertyDAO userPropertyDAO = (UserPropertyDAO) SpringUtils.getBean("UserPropertyDAO");

			String officialFirstName = request.getParameter("officialFirstName");
			String officialSecondName = request.getParameter("officialSecondName");
			String officialLastName = request.getParameter("officialLastName");
			String officialOlisIdtype = request.getParameter("officialOlisIdtype");

			userPropertyDAO.saveProp(provider.getId(), UserProperty.OFFICIAL_FIRST_NAME, officialFirstName);
			userPropertyDAO.saveProp(provider.getId(), UserProperty.OFFICIAL_SECOND_NAME, officialSecondName);
			userPropertyDAO.saveProp(provider.getId(), UserProperty.OFFICIAL_LAST_NAME, officialLastName);
			userPropertyDAO.saveProp(provider.getId(), UserProperty.OFFICIAL_OLIS_IDTYPE, officialOlisIdtype);


			if (org.oscarehr.common.IsPropertiesOn.isMultisitesEnable())
			{
				String provider_no = request.getParameter("provider_no");
				List<ProviderSite> pss = providerSiteDao.findByProviderNo(provider_no);

				for (ProviderSite ps : pss)
				{
					providerSiteDao.remove(ps.getId());
				}

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
					provSite.setId(new ProviderSitePK(provider_no, siteId));


					provSite.setBcBCPEligible(bcpSites.contains(siteString));

					providerSiteDao.persist(provSite);
				}
			}

			ProviderArchiveDao providerArchiveDao = (ProviderArchiveDao) SpringUtils.getBean("providerArchiveDao");
			ProviderArchive pa = new ProviderArchive();
			// unfortunately the providerData provider_no is keyed on 'id' (aka provider_no), which conflicts with the id field
			// of the providerArchive object.  We have to ignore the field and set it manually.
			BeanUtils.copyProperties(provider, pa, "id");
			pa.setProviderNo(provider.getId());

			providerArchiveDao.persist(pa);

			providerService.saveProvider(provider);
			LogAction.addLogEntry((String)session.getAttribute("user"), LogConst.ACTION_UPDATE, LogConst.CON_ADMIN, LogConst.STATUS_SUCCESS,
					request.getParameter("keyword"), request.getRemoteAddr());
		%>
			<p>
			<h2><bean:message key="admin.providerupdate.msgUpdateSuccess" />
			<a href="providerupdateprovider.jsp?keyword=<%=request.getParameter("provider_no")%>"><%= request.getParameter("provider_no") %></a>
			</h2>
			<%
		}
		else
		{
			%>
			<h1><bean:message key="admin.providerupdate.msgUpdateFailure" /><%= request.getParameter("provider_no") %>.</h1>
			<%
		}
	}
	else
	{
		if (!isProviderFormalize)
		{
				//output ProviderFormalize error message
			%>
				<h1><bean:message key="<%=errMsgProviderFormalize%>" />  </h1>
				Provider # range from : <%=min_value %> To : <%=max_value %>
			<%
		}
	}

}
else
{
	%>

	<h1><bean:message key="admin.securityaddsecurity.msgProviderNoAuthorization" /> <%= request.getParameter("provider_no") %></h1>
	<%
}
%>
<p></p>

	</center>
	</body>
</html:html>
