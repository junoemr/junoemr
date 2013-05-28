<%--

    Copyright (c) 2005-2012. OscarHost Inc. All Rights Reserved.
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

    This software was written for
    OscarHost, a Division of Cloud Practice Inc.

--%>

<%@ page import="java.sql.*, java.util.List, oscar.eform.data.*, oscar.SxmlMisc, org.oscarehr.common.model.Demographic, oscar.oscarDemographic.data.DemographicData,oscar.OscarProperties,org.springframework.web.context.support.WebApplicationContextUtils, org.springframework.web.context.WebApplicationContext"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ page import="org.oscarehr.common.model.*,org.oscarehr.common.dao.*"%>
<%@ page import="org.oscarehr.common.model.Provider"%>
<%@ page import="org.oscarehr.PMmodule.service.ProviderManager"%>

<%
WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
ProviderManager providerManager = (ProviderManager)ctx.getBean("providerManager");
List<Provider> providerList = providerManager.getProviders();

String selectField = request.getParameter("selectField") != null ? request.getParameter("selectField") : "provider_no";
String filterType = request.getParameter("filterType") != null ? request.getParameter("filterType") : "";
%> 
<select id="providerSelect">
    <option value=""></option>
<%
String rdName = "";
for( Provider provider : providerList) {
	// Note: getProviderType should be deprecated, but there's currently
	// no other way to check if a user is a doctor or not
	if( filterType.equals("") ||
		( filterType.equals("doctor") && provider.getProviderType().equals(filterType ))){
     
		String fullName = provider.getFormattedName();
	    String value = "";
		if(selectField.equals("practitioner_no")){
		    value = provider.getPractitionerNo();
		}else if(selectField.equals("provider_no")){
			value = provider.getProviderNo();
		}
	
		if (value != null && ! value.equals("null") && !"".equals(value)) {
		%>
	<option value="<%=value%>"><%= String.format("%s", fullName) %> </option>
		<%
		}
    }
} %>                                
</select>