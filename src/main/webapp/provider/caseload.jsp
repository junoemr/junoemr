<%--

    Copyright (c) 2008-2012 Indivica Inc.

    This software is made available under the terms of the
    GNU General Public License, Version 2, 1991 (GPLv2).
    License details are available via "indivica.ca/gplv2"
    and "gnu.org/licenses/gpl-2.0.html".

--%>
<%@ page import="oscar.*, org.oscarehr.util.*"%>
<%@ page import="java.util.*, java.sql.*, java.net.URLEncoder "%>
<%@ page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ page import="org.oscarehr.PMmodule.dao.ProviderDao, org.oscarehr.common.model.Provider" %>
<%@ page import="org.oscarehr.common.dao.DemographicDao" %>

<%

	String caseloadProv = (request.getParameter("clProv") != null) ? URLEncoder.encode(request.getParameter("clProv"), "UTF-8") : "";
	String caseloadDx = (request.getParameter("clDx") != null) ? request.getParameter("clDx") : "";
	String caseloadRoster = (request.getParameter("clRo") != null) ? request.getParameter("clRo") : "RO";
	String caseloadQ = (request.getParameter("clQ") != null) ? request.getParameter("clQ") : "";

	GregorianCalendar caseloadCal = new GregorianCalendar();
	int caseloadCurYear = caseloadCal.get(Calendar.YEAR);
	int caseloadCurMonth = (caseloadCal.get(Calendar.MONTH)+1);
	int caseloadCurDay = caseloadCal.get(Calendar.DAY_OF_MONTH);

	String caseloadCurUser_no = (String) session.getAttribute("user");

%>
<script type="text/javascript" src="<%= request.getContextPath() %>/share/javascript/prototype.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/share/javascript/jquery/jquery-1.4.2.js"></script>

<tr><td colspan="3">
<style>
#caseloadTable {
	border-collapse: collapse;
}
.caseloadRow td {
	padding: 1px;
	padding-left: 0.5em;
}
#caseloadTable form {
	display: inline;
}
#caseloadTable .selectedCategory {
	background-color: #bfefff;
}
#caseloadHeader b {
	cursor: pointer;
}

#leftSearch, #rightSearch { display: inline; }
#rightSearch { float:right; }
<% if (OscarProperties.getInstance().isPropertyActive("navigation_always_on_top")) { %>
#caseloadDisplayOptions {
	padding-top: 21px;
}
<% } %>
</style>
<%
String[] clH = new String[] {"Demographic", "", "Age", "Sex", "Last Appt", "Next Appt", "Appts LYTD", "Lab", "Doc", "Tickler", "Msg", "BMI", "BP", "WT", "SMK", "A1C", "ACR", "SCR", "LDL", "HDL", "TCHD", "EGFR", "EYEE"};
%>
<script type="text/javascript">
clH = ["<bean:message key="caseload.msgDemographic" />", 
			"", 
			"<bean:message key="caseload.msgAge" />", 
			"<bean:message key="caseload.msgSex" />", 
			"<bean:message key="caseload.msgLastAppt" />", 
			"<bean:message key="caseload.msgNextAppt" />", 
			"<bean:message key="caseload.msgApptsLYTD" />", 
			"<bean:message key="caseload.msgLab" />", 
			"<bean:message key="caseload.msgDoc" />", 
			"<bean:message key="caseload.msgTickler" />", 
			"<bean:message key="caseload.msgMsg" />", 
			"<bean:message key="caseload.msgBMI" />", 
			"<bean:message key="caseload.msgBP" />", 
			"<bean:message key="caseload.msgWT" />", 
			"<bean:message key="caseload.msgSMK" />", 
			"<bean:message key="caseload.msgA1C" />", 
			"<bean:message key="caseload.msgACR" />", 
			"<bean:message key="caseload.msgSCR" />", 
			"<bean:message key="caseload.msgLDL" />", 
			"<bean:message key="caseload.msgHDL" />", 
			"<bean:message key="caseload.msgTCHD" />", 
			"<bean:message key="caseload.msgEGFR"/>" , 
			"<bean:message key="caseload.msgEYEE" />" ];
</script>
<table border="1" cellpadding="0" cellspacing="0" width="100%" id="caseloadTable">
<tr>
	<td colspan='<%=clH.length%>'>
	<table border='0' cellpadding='0' bgcolor='#fffff0' cellspacing='0' width='100%'>
		<tr class='caseloadRow'>
		<td colspan='<%=clH.length%>' id="caseloadDisplayOptions">
			<div>
				<div id="leftSearch">
					<form>
						<b>Notes: </b>
						<input type='text' id='caseloadQ' size='20' onclick='this.value=""; document.getElementById("caseloadDx").value="";' value="<%=StringEscapeUtils.escapeHtml(caseloadQ).replace("'","\\\'")%>" />
						<input type='submit' value='Search' onclick='search("noteSearch"); return false;'>
					</form>
				</div>
				<div id="rightSearch">
					<form>
					<b>Provider: </b>
					<select id='caseloadProv' onChange='search("search");'>
						<option value='all'>All Providers</option>
		<%
		ProviderDao providerDao = (ProviderDao) SpringUtils.getBean("providerDao");
		List<Provider> providerList = providerDao.getProviders();
		for (Provider provider : providerList) {
		%>
						<option value='<%=provider.getProviderNo()%>' <%=caseloadProv.equals(provider.getProviderNo())?"selected":""%>><%=provider.getLastName()+", "+provider.getFirstName()%></option>
		<% } %>
					</select>
					<b>Rostered: </b>
					<select id='caseloadRoster' onChange='search("search");' style='width: 120'>
						<option value=''></option>
						<option value='RO' <%=caseloadRoster.equals("RO")?"selected":""%>><bean:message key='demographic.demographiceditdemographic.optRostered'/></option>
						<option value='NR' <%=caseloadRoster.equals("NR")?"selected":""%>><bean:message key='demographic.demographiceditdemographic.optNotRostered'/></option>
						<option value='TE' <%=caseloadRoster.equals("TE")?"selected":""%>><bean:message key='demographic.demographiceditdemographic.optTerminated'/></option>
						<option value='FS' <%=caseloadRoster.equals("FS")?"selected":""%>><bean:message key='demographic.demographiceditdemographic.optFeeService'/></option>
		<%

		DemographicDao caseloadDemographicDao = (DemographicDao) SpringUtils.getBean("demographicDao");
		List<String> rosterStatusList = caseloadDemographicDao.getRosterStatuses();
		for (String rosterStatus : rosterStatusList) {
			if (rosterStatus != null && rosterStatus.trim().length() > 0) {
		%>
						<option value='<%=StringEscapeUtils.escapeHtml(rosterStatus)%>' <%=caseloadRoster.equals(rosterStatus)?"selected":""%>><%=StringEscapeUtils.escapeHtml(rosterStatus)%></option>
		<% 	}
		} %>
					</select>
					<b>DxReg: </b>
					<input type='text' id='caseloadDx' size='10' onclick='this.value=""; document.getElementById("caseloadQ").value="";' value="<%= StringEscapeUtils.escapeHtml(caseloadDx).replace("'","\\\'") %>" />
					<input type='submit' value='<bean:message key="caseload.msgSearch" />' onclick='search("search"); return false;'>
					| <a href='providercontrol.jsp?year=<%=caseloadCurYear%>&month=<%=caseloadCurMonth%>&day=<%=caseloadCurDay%>&view=0&displaymode=day&dboperation=searchappointmentday'><bean:message key="caseload.msgSchedule" /></a> &nbsp;
					</form>
				</div>
			</div>
		</td>
		</tr>
	</table>
</td>
</tr>
<tr id='loadingHeader' class='caseloadRow'><td style='text-align: center;' bgcolor='#fffff0' colspan='<%=clH.length%>'><img src='../images/DMSLoader.gif' /> Loading results...</td></tr>
<tr id='totalResults2' class='caseloadRow'><td style='text-align: center;' bgcolor='#fffff0' colspan='<%=clH.length%>'> <span id="rows2"></span>/<span id="totalRows2"></span> results retrieved </td></tr>
<tr class='caseloadRow' id='caseloadHeader'>
<% for (int i=0; i < clH.length; i++) { %>
	<% if (i == 0) { %>
	<td class='selectedCategory' bgcolor='#c0c0c0'><b onclick='changeCategory("<%=i%>")'><%=clH[i]%> &Delta;</b></td>
	<% } else { %>
	<td bgcolor='#c0c0c0' <%=(i==1) ? "style='width:307px'" : "" %>><b onclick='changeCategory("<%=i%>")'><%=clH[i]%></b></td>
	<% } %>
<% } %>
<tr id='loadingFooter' class='caseloadRow'><td colspan='<%=clH.length%>' style='text-align: center;' bgcolor='#fffff0' colspan='<%=clH.length%>'><img src='../images/DMSLoader.gif' /> Loading results...</td></tr>
<tr id='noResults' class='caseloadRow'><td colspan='<%=clH.length%>' style='text-align: center;' bgcolor='#fffff0' > No results found. Please try a different search.</td></tr>
<tr id='totalResults1' class='caseloadRow'><td colspan='<%=clH.length%>' style='text-align: center;' bgcolor='#fffff0' ><span id="rows1"></span>/<span id="totalRows1"></span> results retrieved.</td></tr>
</table>
<script type="text/javascript">

var rows = 0;
var totalRows = 0;
var page = 0;
var pageSize = 35;
var sortAsc = true;
var category = 0;
var xhr;
var searchMethod = "search";
// Indicates if there is more content to load from server
var canLoad = true;
// Indicates if contents is being loaded from the server.
var loading = false;

function handleScroll(e) {
	if (!canLoad || loading) { return false; }
	if (jQuery(window).scrollTop() >= jQuery(document).height() - jQuery(window).height()){
		update(searchMethod);
	}
}
function windowHasScrollBar() {
    return jQuery("body").height() > jQuery(window).height();
}

function hideAllMessages() {
	jQuery("#noResults").hide();
	jQuery("#loadingFooter").hide();
	jQuery("#loadingHeader").hide();
	jQuery("#totalResults1").hide();
	jQuery("#totalResults2").hide();
}

function showLoading() {
	if (windowHasScrollBar()) { jQuery("#loadingHeader").show(); }
	jQuery("#loadingFooter").show();
}
function showNoResults() {
	jQuery("#noResults").show();
}

function updateTotalRows(count) {
	if (count !== undefined) {
		totalRows = count;
		jQuery("#totalRows1").html(totalRows);
		jQuery("#totalRows2").html(totalRows);
	}
	jQuery("#rows1").html(rows);
	jQuery("#totalResults1").show();
	if (windowHasScrollBar()) {
		jQuery("#rows2").html(rows);
		jQuery("#totalResults2").show();
	}
}

function getSortDir() {
	return sortAsc ? "&Delta;" : "&nabla;";
}

function generateQuery(method) {
	var query = "method="+method+"&year=<%=caseloadCurYear%>&month=<%=caseloadCurMonth%>&day=<%=caseloadCurDay%>";
	if (method == "search") {
		query += "&clDx=" + escape(document.getElementById("caseloadDx").value);
		query += "&clProv=" + escape(document.getElementById("caseloadProv").value);
	    query += "&clRo=" + escape(document.getElementById("caseloadRoster").value);
	} else {
		query += "&clProv=" + encodeURI("<%=caseloadCurUser_no%>");
		query += "&clQ=" + encodeURI(document.getElementById("caseloadQ").value);
	}
	query += "&clPage=" + page;
    query += "&clPSize=" + pageSize;
    query += "&clCat=" + escape(clH[category]);
    query += "&clSortAsc=" + escape(sortAsc);
    return query;
}

function changeCategory(cat) {
	var headers = jQuery("#caseloadHeader").children();
	if (cat == category) {
		sortAsc = !sortAsc;
		headers.get(category).innerHTML = "<b onclick='changeCategory(\""+category+"\")'>"+clH[category] + " " + getSortDir() + "</b>";
	}
	else {
		var oldCat = jQuery(headers.get(category));
		var newCat = jQuery(headers.get(cat));
		sortAsc = true;
		oldCat.removeClass("selectedCategory");
		newCat.addClass("selectedCategory");
		oldCat.html("<b onclick='changeCategory(\""+category+"\")'>"+clH[category]+"</b>");
		newCat.html("<b onclick='changeCategory(\""+cat+"\")'>" + clH[cat] + " " + getSortDir() + "</b>");
		category = cat;
	}
	search(searchMethod);
}

function search(method) {
	if (xhr != null) { xhr.abort(); }
	searchMethod = method;
	canLoad = true;
	rows = 0;
	page = 0;
	var url = "<%=request.getContextPath()%>/caseload/CaseloadContent.do";
	var query  = generateQuery(method);
	jQuery(".caseloadEntry").remove();
	hideAllMessages();
	showLoading();
	xhr = jQuery.getJSON(url, query, draw);
	return false;
}

function update(method) {
	if (xhr != null) { xhr.abort(); }
	loading = true;
	searchMethod = method;
	page++;
	var url = "<%=request.getContextPath()%>/caseload/CaseloadContent.do";
	var query  = generateQuery(method);
	hideAllMessages();
	showLoading();
	updateTotalRows();
	xhr = jQuery.getJSON(url, query, draw);
	return false;
}

function draw(json) {
	var tableStr = "";
	var oldRows = rows;
	jQuery.each(json.data, function() {
		tableStr += "<tr class='caseloadRow caseloadEntry' bgcolor='" + ((++rows%2==0)?"#FDFEC7":"#FFBBFF") + "'><td>"+this.join("</td><td>")+"</td></tr>\n";
	});
	if (rows == 0) {
		hideAllMessages();
		showNoResults();
	}
	else if (oldRows == rows) {
		canLoad = false;
		hideAllMessages();
		updateTotalRows();
	}
	else {
		jQuery("#loadingFooter").before(tableStr);
		hideAllMessages();
		// json.size is only defined on page 1
		updateTotalRows(json.size);
		if (!windowHasScrollBar()) { update(searchMethod); }
	}
	xhr = null;
	loading = false;
}

jQuery(document).ready(function(){
	hideAllMessages();
	jQuery(window).scroll(handleScroll);
	search(searchMethod);
});
</script>
</td></tr>
