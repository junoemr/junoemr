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

<%@page import="java.util.ArrayList"%>
<%@page import="org.oscarehr.util.LocaleUtils"%>
<%@page import="org.oscarehr.phr.util.MyOscarUtils"%>
<%@page import="org.oscarehr.util.MiscUtils"%>
<%@page import="org.oscarehr.util.LoggedInInfo"%>
<%@page import="org.oscarehr.common.dao.UserPropertyDAO, org.oscarehr.common.model.UserProperty, org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/rewrite-tag.tld" prefix="rewrite"%>
<%@ taglib uri="/WEB-INF/oscarProperties-tag.tld" prefix="oscarProp"%>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%@ taglib uri="/WEB-INF/oscar-tag.tld" prefix="oscar" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
	ArrayList errors = (ArrayList) request.getAttribute("errors"); 
%>

<html:html locale="true">
<head>

<title>Fax PDFs</title>

<link rel="stylesheet" type="text/css" href="../share/css/OscarStandardLayout.css" />

<script language="javascript">
function BackToOscar() {
       window.close();
}

function finishPage(secs){
    setTimeout("window.close()",secs*1000);
}

</script>

</head>
<%
if(errors.size() == 0) {
%>
<body class="bodystyle" onload="finishPage(5);">
<%
} else {
%>
<body class="bodystyle">
<%
}
%>

<table class="MainTable" id="scrollNumber1" name="encounterTable"               
	style="margin: 0px;">                                                       
	<tr class="MainTableRowTop">                                                
		<td class="MainTableTopRowLeftColumn" width="60px">eDocs</td>           
		<td class="MainTableTopRowRightColumn">                                 
			<table class="TopStatusBar">                                            
				<tr>                                                                
					<td>Send PDFs By Fax</td>
				</tr>                                                               
			</table>                                                                
		</td>                                                                   
	</tr>                                                                       
</table>    


<%
if(errors.size() == 0) {
%>

    <div style="padding: 5px;"><bean:message key="oscarEncounter.oscarConsultationRequest.ConfirmConsultationRequest.msgClose5Sec" /></div>

<%
} else {
%>

    <div style="padding: 5px;">Not everything was faxed, the following errors occurred:</div>
    <ul>
    <logic:iterate name="errors" id="error">
        <li><bean:write name="error" /></li>
    </logic:iterate>
    </ul>
<%
}
%>

<div style="padding: 5px;"><a href="javascript: BackToOscar();"><bean:message key="global.btnClose" /></a></div>


</body>
</html:html>
