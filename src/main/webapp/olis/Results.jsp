<%--

    Copyright (c) 2008-2012 Indivica Inc.

    This software is made available under the terms of the
    GNU General Public License, Version 2, 1991 (GPLv2).
    License details are available via "indivica.ca/gplv2"
    and "gnu.org/licenses/gpl-2.0.html".

--%>
<%@ page contentType="text/html;" %>
<%@ page import="java.util.List" %>
<%@ page import="oscar.oscarLab.ca.all.parsers.OLIS.OLISHL7Handler" %>
<%@ page import="org.oscarehr.olis.OLISResultsAction" %>
<%@ page import="org.oscarehr.dataMigration.model.demographic.Demographic" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.stream.Collectors" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script type="text/javascript">
    jQuery.noConflict();
</script>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/share/css/OscarStandardLayout.css">
<script type="text/javascript" src="<%=request.getContextPath()%>/share/javascript/Oscar.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/share/javascript/oscarMDSIndex.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/share/javascript/sortable.js"></script>

<script type="text/javascript">
	function addToInbox(uuid, params="")
	{
		jQuery(uuid).attr("disabled", "disabled");
		jQuery.ajax({
			url: "<%=request.getContextPath() %>/olis/AddToInbox.do" + params,
			data: "uuid=" + uuid,
			success: function(data) {
				jQuery("#" + uuid + "_result").html(data);
			}
		});
	}
	function save(uuid)
	{
		return addToInbox(uuid);
	}
	function saveAndFile(uuid)
	{
		return addToInbox(uuid, "?file=true");
	}
	function saveAndAck(uuid)
	{
		return addToInbox(uuid, "?ack=true");
	}
	function preview(uuid)
	{
		reportWindow('<%=request.getContextPath()%>/lab/CA/ALL/labDisplayOLIS.jsp?segmentID=0&preview=true&uuid=' + uuid);
	}

	var patientFilter = "";
	var labFilter = "";
	function filterResults(select) {
		if (select.name == "labFilter") {
			labFilter = select.value;
		} else if(select.name == "patientFilter") {
			patientFilter = select.value;
		}
		var performFilter = function() {
			var visible = (patientFilter == "" || jQuery(this).attr("patientName") == patientFilter)
					   && (labFilter == "" || jQuery(this).attr("reportingLaboratory") == labFilter);
			if (visible) { jQuery(this).show(); }
			else { jQuery(this).hide(); }
		};
		jQuery(".evenLine").each(performFilter);
		jQuery(".oddLine").each(performFilter);
	}
</script>
<style type="text/css">
.oddLine { 
	background-color: #cccccc;
}
.evenLine { } 

.error {
	border: 1px solid red;
	color: red;
	font-weight: bold;
	margin: 10px;
	padding: 10px;
}

.width-md {
	min-width: 128px;
}
.width-sm {
	min-width: 64px;
}
.width-xs {
	min-width: 32px;
}

#patientTable {
	border: 1px solid lightgrey;
}
#patientTable td {
	font-weight: normal;
}
#patientTable .table-title {
	background-color: lightgrey;
}
.page-wrapper {
	display: flex;
	flex-direction: column;
}

</style>
	
<title>OLIS Search Results</title>
</head>
<body>
<div class="page-wrapper">

<table style="width:600px;" class="MainTable" align="left">
	<tbody><tr class="MainTableTopRow">
		<td class="MainTableTopRowLeftColumn" width="175">OLIS</td>
		<td class="MainTableTopRowRightColumn">
		<table class="TopStatusBar">
			<tbody><tr>
				<td>Results</td>
				<td>&nbsp;</td>
				<td style="text-align: right"><a href="javascript:popupStart(300,400,'Help.jsp')"><u>H</u>elp</a> | <a href="javascript:popupStart(300,400,'About.jsp')">About</a> | <a href="javascript:popupStart(300,400,'License.jsp')">License</a></td>
			</tr>
			</tbody>
		</table>
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<%
			Demographic demographic = (Demographic) request.getAttribute("demographic");

			if (request.getAttribute("searchException") != null) {
			%>
				<div class="error">Could not perform the OLIS query due to the following exception:<br /><%=((Exception) request.getAttribute("searchException")).getLocalizedMessage() %></div>
			<%
			} %>
			
			<%
			if (request.getAttribute("errors") != null) {
				// Show the errors to the user				
				for (String error : (List<String>) request.getAttribute("errors")) { %>
					<div class="error"><%=error.replaceAll("\\n", "<br />") %></div>
				<% }
			}
			String resp = StringUtils.trimToEmpty((String) request.getAttribute("olisResponseContent"));
			List<String> resultList = (List<String>) request.getAttribute("resultList");
			String continuationPointer = (String) request.getAttribute("continuationPointer");
			boolean hasBlockedContent = (boolean) request.getAttribute("blockedContent");

			// ordering must be preserved for matching uuid to handler below
			List<OLISHL7Handler> resultHandlers = resultList.stream().map(OLISResultsAction::getHandlerByUUID).collect(Collectors.toList());

			if (hasBlockedContent) { 
			%>
			<form  action="<%=request.getContextPath()%>/olis/Search.do"
			       onsubmit="return confirm('Are you sure you want to resubmit this query with a patient consent override?')">
				<input type="hidden" name="redo" value="true" />
				<input type="hidden" name="uuid" value="<%=(String)request.getAttribute("searchUuid")%>" />
				<input type="hidden" name="force" value="true" />
				<input type="hidden" name="blockedInformationConsent" value="T" />
				<input type="hidden" name="demographic" value="<%=(demographic != null) ? demographic.getId() : "" %>" />
				<input type="submit" value="Submit Override Consent" /> 
				Authorized by: 
				<select id="blockedInformationIndividual" name="blockedInformationIndividual">
					<option value="Z">Patient</option>
					<option value="X">Substitute Decision Maker</option>
				</select>
				<div>
					<span>Substitute Decision Maker</span>
					<label>Given Name:
						<input type="text" name="substituteGivenName">
					</label>
					<label>Last Name:
						<input type="text" name="substituteLastName">
					</label>
					<label>Relationship:
						<select name="substituteRelationship">
							<option value="A0">Guardian for the Person</option>
							<option value="A1">Attorney for Personal Care</option>
							<option value="A2">Representative appointed by Consent and Capacity Board</option>
							<option value="A3">Spouse/Partner</option>
							<option value="A4">Parent</option>
							<option value="A5">Child</option>
							<option value="A6">Sibling</option>
							<option value="A7">Other Relative</option>
						</select>
					</label>
				</div>
			</form>
			<%
			}

			if (resultList != null) {
			%>
			<table>
				<tr>
					<td colspan=8>Showing <%=resultList.size() %> result(s)</td>
				</tr>
				<% if (resultList.size() > 0) { %>
					<tr>
						<td colspan="4">
						Filter by patient name:
						<select name="patientFilter" onChange="filterResults(this)">
							<option value="">All Patients</option>
							<%  List<String> names = new ArrayList<String>();
								String name;
								for (OLISHL7Handler result : resultHandlers)
								{
									name = oscar.Misc.getStr(result.getPatientName(), "").trim();
									if (!name.equals("")) { names.add(name); }
								}
								for (String tmp: new HashSet<String>(names)) {
							%>
								<option value="<%=tmp%>"><%=tmp%></option>
							<% } %>
						</select>
						</td>
						<td colspan="5">
						Filter by reporting laboratory:
						<select name="labFilter" onChange="filterResults(this)">
							<option value="">All Labs</option>
							<%  List<String> labs = new ArrayList<String>();
								for (OLISHL7Handler result : resultHandlers)
								{
									name = oscar.Misc.getStr(result.getReportingFacilityName(), "").trim();
									if (!name.equals("")) { labs.add(name); }
								}
								for (String tmp: new HashSet<String>(labs)) {
							%>
								<option value="<%=tmp%>"><%=tmp%></option>
							<% } %>
						</select>
						</td>
					</tr>
				<% if (demographic != null) { %>
					<tr>
						<th colspan="10">
							<table id="patientTable">
								<tbody>
								<tr class="table-title">
									<th colspan="6">
										<span>Patient Info</span>
									</th>
								</tr>
								<tr>
									<th class="width-md">
										<span>Name</span>
									</th>
									<th class="width-sm">
										<span>Sex</span>
									</th>
									<th class="width-md">
										<span>Date of Birth</span>
									</th>
									<th class="width-md">
										<span>Hin</span>
									</th>
									<th class="width-md">
										<span>Contact Phone #</span>
									</th>
								</tr>
								<tr>
									<td>
										<span><%=demographic.getDisplayName()%></span>
									</td>
									<td>
										<span><%=demographic.getSexString()%></span>
									</td>
									<td>
										<span><%=demographic.getDateOfBirth()%></span>
									</td>
									<td>
										<span><%=demographic.getDisplayHealthNumber()%></span>
									</td>
									<td>
										<span><%=((demographic.getPreferredPhone().isPresent()) ? demographic.getPreferredPhone().get().getNumberFormattedDisplay() : "")%></span>
									</td>
								</tr>
								</tbody>
							</table>
						</th>
					</tr>
				<% } %>
					<tr><td colspan="10">
					<table class="sortable" id="resultsTable">
					<tr>
						<th class="unsortable"></th>
						<th class="unsortable"></th>
						<th class="unsortable"></th>
						<th class="unsortable"></th>
						<th class="width-md">Order #</th>
						<th class="width-md">Collection Date/Time</th>
						<th class="width-md">Last Updated in OLIS</th>
						<th class="width-sm">Discipline</th>
						<th class="width-md">Specimen Type</th>
						<th class="width-md">Test Request Name</th>
						<th class="width-sm">Test Request Status</th>
						<th class="width-sm">Ordering Practitioner</th>
						<th class="width-sm">Admitting Practitioner</th>
					</tr>
					
					<%  int lineNum = 0;
						for(int handlerIndex=0; handlerIndex< resultHandlers.size(); handlerIndex++)
						{
							String resultUuid = resultList.get(handlerIndex);
							OLISHL7Handler result = resultHandlers.get(handlerIndex);

							// show one row per OBR, so that individual statuses can be displayed. Required feature for OLIS conformance.
							for(int i=0; i < result.getOBRCount(); i++)
							{
								int obrRep = result.getMappedOBR(i); // aka use sort keys
							%>
							<tr class="<%=++lineNum % 2 == 1 ? "oddLine" : "evenLine"%>"
							    patientName="<%=result.getPatientName()%>"
							    reportingLaboratory="<%=result.getReportingFacilityName()%>">
								<td>
									<div id="<%=resultUuid %>_result"></div>
									<input type="button" onClick="save('<%=resultUuid %>'); return false;" id="<%=resultUuid %>" value="Add to Inbox" />
								</td>
								<td>
									<input type="button" onClick="saveAndFile('<%=resultUuid %>'); return false;" id="<%=resultUuid %>_save" value="Save/File" />
								</td>
								<td>
									<input type="button" onClick="saveAndAck('<%=resultUuid %>'); return false;" id="<%=resultUuid %>_ack" value="Acknowledge" />
								</td>
								<td>
									<input type="button" onClick="preview('<%=resultUuid %>'); return false;" id="<%=resultUuid %>_preview" value="Preview" />
								</td>
								<td><%=result.getAccessionNum()%></td>
								<td><%=result.getSpecimenReceivedDateTime()%></td>
								<td><%=result.getLastUpdateInOLIS()%></td>
								<td><%=result.getOBRCategory(obrRep)%></td>
								<td><%=result.getObrSpecimenSource(obrRep)%></td>
								<td><%=result.getOBRName(obrRep)%></td>
								<td><%=result.getObrStatusDisplayValue(obrRep)%></td>
								<td><%=result.getShortDocName()%></td>
								<td><%=result.getAdmittingProviderNameShort()%></td>

							</tr>
						<%
							}
						}%>
					</table></td></tr>
				<%
				}
				if(StringUtils.isNotBlank(continuationPointer))
				{
				%>
				<tr>
					<td>
						<form  action="<%=request.getContextPath()%>/olis/Search.do"
						       onsubmit="return confirm('Are you sure you want to resubmit this query?')">
							<input type="hidden" name="redo" value="true" />
							<input type="hidden" name="continuationPointer" value="<%=continuationPointer%>" />
							<input type="hidden" name="uuid" value="<%=(String)request.getAttribute("searchUuid")%>" />
							<input type="hidden" name="demographic" value="<%=(demographic != null) ? demographic.getId() : "" %>" />
							<input type="submit" value="Retrieve more Results" />
						</form>
				</tr>
				<%
				}%>
			</table>
			<%
			}
			%>
		</td>
	</tr></tbody>
</table>
<pre style="display: none;"><%=resp%></pre>
</div>
</body>
</html>
