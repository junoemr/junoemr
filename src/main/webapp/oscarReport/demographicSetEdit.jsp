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
<security:oscarSec roleName="<%=roleName$%>" objectName="_report,_admin.reporting" rights="r" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect("../securityError.jsp?type=_report&type=_admin.reporting");%>
</security:oscarSec>
<%
if(!authed) {
	return;
}
%>

<!-- page updated to support better use of CRUD operations -->

<%@page import="org.oscarehr.util.LoggedInInfo"%>
<%@page import="oscar.oscarDemographic.data.*,java.util.*,oscar.oscarPrevention.*,oscar.oscarProvider.data.*,oscar.util.*,oscar.oscarPrevention.pageUtil.*"%>
<%@page	import="oscar.oscarReport.data.*, org.oscarehr.common.model.DemographicSets, org.oscarehr.common.model.Demographic"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/oscar-tag.tld" prefix="oscar"%>
<jsp:useBean id="providerBean" class="java.util.Properties"
	scope="session" />

<%

  //int demographic_no = Integer.parseInt(request.getParameter("demographic_no"));
  String demographic_no = request.getParameter("demographic_no");

  DemographicSetManager  ds = new DemographicSetManager();
  List<String> setNames = ds.getDemographicSets();

  DemographicData dd = new DemographicData();

%>

<html:html locale="true">

<head>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
<title>Demographic Set Edit I18n</title>
<script src="../share/javascript/Oscar.js"></script>
<link rel="stylesheet" type="text/css"
	href="../share/css/OscarStandardLayout.css">
<link rel="stylesheet" type="text/css" media="all"
	href="../share/calendar/calendar.css" title="win2k-cold-1" />

<script type="text/javascript" src="../share/calendar/calendar.js"></script>
<script type="text/javascript"
	src="../share/calendar/lang/<bean:message key="global.javascript.calendar"/>"></script>
<script type="text/javascript" src="../share/calendar/calendar-setup.js"></script>
<link rel="stylesheet" type="text/css" media="all" href="../share/css/extractedFromPages.css"  />

<!-- Added for delete functionality 2017 -->
<script type="text/javascript" src="../share/javascript/jquery/jquery-2.2.4.min.js"></script>
<script src="../share/javascript/jquery/jquery-ui-1.12.0.min.js"></script>
<link rel="stylesheet" href="../share/javascript/jquery/jquery-ui-1.12.0.min.css">

<style>
	.alert {
	float:left;
	margin:12px 12px 20px 0;
	}
	.deleteSuccess {
		background-color:#6fdb6f;
	}
	.deleteError {
	background-color: red;
	}
</style>
<script>

function showHideItem(id){
    if(document.getElementById(id).style.display == 'none')
        document.getElementById(id).style.display = '';
    else
        document.getElementById(id).style.display = 'none';
}

function showItem(id){
        document.getElementById(id).style.display = '';
}

function hideItem(id){
        document.getElementById(id).style.display = 'none';
}

function showHideNextDate(id,nextDate,nexerWarn){
    if(document.getElementById(id).style.display == 'none'){
        showItem(id);
    }else{
        hideItem(id);
        document.getElementById(nextDate).value = "";
        document.getElementById(nexerWarn).checked = false ;

    }
}

function disableifchecked(ele,nextDate){
    if(ele.checked == true){
       document.getElementById(nextDate).disabled = true;
    }else{
       document.getElementById(nextDate).disabled = false;
    }
}

CONFIRM_SET_DELETE_TITLE="Confirm Set Delete";
CONFIRM_SET_DELETE_MESSAGE="This will permanently delete the set. The process is irreversible. Do you want to continue?";

/** create a confirmation dialogue box element
 *  call jquery dialog constructor on the returned div element selector */
function createConfirmationDialogueElements(title, message) {
    return $("<div>", {
        title: title,
        class: "alert"
    }).append($("<p>", {
        text: message
    }));
}
// run on page load
$(function () {
	$("#set_delete").click(function(event) {
		if($("#demographicSetName").val() != "-1") { // a set is selected
	        var $confirm = createConfirmationDialogueElements(CONFIRM_SET_DELETE_TITLE, CONFIRM_SET_DELETE_MESSAGE);
	        $confirm.dialog({
	            resizable: false,
	            height: "auto",
	            width: 400,
	            modal: true,
	            position: { my: 'top', at: 'top+150' },
	            buttons: {
	                "Delete Set": function() {
	                	$(this).dialog("close");
	                	// submit form to delete the set.
	                	$("#demographicSetDeleteForm").submit();
	                },
	                "Cancel": function() {
	                    $(this).dialog("close");
	                }
	            },
	            close: function() {
	                $(this).remove();
	            }
	        });
	        event.preventDefault();
		}
	})
});

</script>




<link rel="stylesheet" type="text/css" media="all" href="../share/css/extractedFromPages.css"  />
</head>

<body class="preview" id="top" data-spy="scroll" data-target=".subnav" data-offset="180">

  <div class="page-header">
    <h1><bean:message key="oscarReport.oscarReportDemoSetEdit.msgDemographic"/> - <bean:message key="oscarReport.oscarReportDemoSetEdit.msgSetEdit"/></h1>
  </div>

  	<section id="mainContent">

		<%
		if(request.getAttribute("deleteSuccess") != null) {
			if((Boolean)request.getAttribute("deleteSuccess")) {
				%>
				<div><span class="deleteSuccess">Patient Set Deleted</span></div>
				<%
			}
			else {
				%>
				<div><span class="deleteError">An Error Occured</span></div>
				<%
			}
		}
		%>
		<div class="row">
		<div class="span12">
		<html:form styleClass="form-horizontal well form-search" action="/report/DemographicSetEdit">
			<div><bean:message key="oscarReport.oscarReportDemoSetEdit.msgPatientSet"/>: <html:select property="patientSet">
				<html:option value="-1"><bean:message key="oscarReport.oscarReportDemoSetEdit.msgOptionSet"/></html:option>
				<% for ( int i = 0 ; i < setNames.size(); i++ ){
                            String s = setNames.get(i);%>
				<html:option value="<%=s%>"><%=s%></html:option>
				<%}%>
			</html:select> <input type="submit" value="<bean:message key="oscarReport.oscarReportDemoSetEdit.btnDisplaySet"/>" /></div>
		</html:form>
			<%if( request.getAttribute("SET") != null ) {
                   List<DemographicSets> list = (List<DemographicSets>) request.getAttribute("SET");
                   String setName = (String) request.getAttribute("setname");%>
		<div><html:form action="/report/SetEligibility">
			<input type="submit" value="<bean:message key="oscarReport.oscarReportDemoSetEdit.btnSetIneligible"/>" /> <bean:message key="oscarReport.oscarReportDemoSetEdit.msgIneligible"/><br>
			<input type="submit" name="delete" value="<bean:message key="oscarReport.oscarReportDemoSetEdit.btnDelete"/>"/><bean:message key="oscarReport.oscarReportDemoSetEdit.msgDelete"/>
			<input type="hidden" name="setName" value="<%=setName%>" />
			<table class="ele">
				<tr>
					<th>&nbsp;</th>
					<th><bean:message key="oscarReport.oscarReportDemoSetEdit.msgDemo"/></th>
					<th><bean:message key="oscarReport.oscarReportDemoSetEdit.msgName"/></th>
					<th><bean:message key="oscarReport.oscarReportDemoSetEdit.msgDOB"/></th>
					<th><bean:message key="oscarReport.oscarReportDemoSetEdit.msgAge"/></th>
					<th><bean:message key="oscarReport.oscarReportDemoSetEdit.msgRoster"/></th>
					<th><bean:message key="oscarReport.oscarReportDemoSetEdit.msgDoctor"/></th>
					<th><bean:message key="oscarReport.oscarReportDemoSetEdit.msgEligibility" /></th>
				</tr>
				<%for (int i=0; i < list.size(); i++){
					DemographicSets h = list.get(i);
					String demoNoStr = String.valueOf(h.getDemographicNo());
                    Demographic demo = h.getDemographic();%>
				<tr>
					<td><input type="checkbox" name="demoNo" value="<%=demoNoStr%>" />
					<td><%=demoNoStr%></td>
					<td><%=demo.getDisplayName()%></td>
					<td><%=demo.getFormattedDob()%></td>
					<td><%=demo.getAge()%></td>
					<td><%=demo.getRosterStatus()%></td>
					<td><%=providerBean.getProperty(String.valueOf(demo.getProviderNo()),"")%></td>
					<td><%=elle(h.getEligibility())%></td>
				</tr>
				<%}%>
			</table>
		</html:form></div>
		<%}%>
		</div>
	</div>
</section>

<html:form styleId="demographicSetDeleteForm" method="POST" action="/report/DemographicSetDelete">
	<div><bean:message key="oscarReport.oscarReportDemoSetEdit.msgPatientSet"/>: 
	<select id="demographicSetName" name="demographicSetName">
		<option value="-1"><bean:message key="oscarReport.oscarReportDemoSetEdit.msgOptionSet"/></option>
		<% 
		for ( int i = 0 ; i < setNames.size(); i++ ) {
			String s = setNames.get(i);%>
		<option value="<%=s%>"><%=s%></option>
		<%}%>
		</select> 
		<button id="set_delete" type="button" >Delete Patient Set</button>
	</div>
</html:form>
</body>
</html:html>
<%!
String elle(Object s){
    ResourceBundle prop = ResourceBundle.getBundle("oscarResources");
    String ret = prop.getString("oscarReport.oscarReportDemoSetEdit.msgStatusEligibile");
    if (s != null && s instanceof String && ((String) s).equals("1")){
        ret = prop.getString("oscarReport.oscarReportDemoSetEdit.msgStatusIneligibile");
    }
    return ret;
}
%>
