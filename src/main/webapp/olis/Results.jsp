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
<%@ page import="java.util.stream.Collectors" %>
<%@ page import="org.oscarehr.olis.transfer.OLISSearchResultTransfer" %>
<%@ page import="oscar.OscarProperties" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.stream.IntStream" %>

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

	function hideResult(uuid, accessionNo, versionId, demographicId, isHidden=true)
	{
		// require confirmation for hiding/removing items
		if(!isHidden || window.confirm("You are about to remove this result from the search results. Are you sure?"))
		{
			jQuery.ajax({
				url: "<%=request.getContextPath() %>/olis/Results.do?method=hideResult",
				data: {
					"accessionNo" : accessionNo,
					"version" : versionId,
					"isHidden" : isHidden,
					"demographicId" : demographicId,
				},
				success: function (data)
				{
					let selector = jQuery("." + uuid + "_row_selector");
					selector.toggleClass("removed", isHidden);
					selector.toggle(showRemoved || !isHidden);

					selector.find(".remove_button").toggle(!isHidden);
					selector.find(".unremove_button").toggle(isHidden);
				}
			});
		}
	}
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

	let showDuplicates = false;
	function toggleShowDuplicates()
	{
		showDuplicates = !showDuplicates;
		jQuery(".duplicate").toggle(showDuplicates);
	}

	let showRemoved = false;
	function toggleShowRemoved()
	{
		showRemoved = !showRemoved;
		jQuery(".removed").toggle(showRemoved);
	}

	const filter = {
		patientName: {
			attribute: "patientName",
			value: "",
			exactMatch: true,
		},
		performingLab: {
			attribute: "performingLaboratory",
			value: "",
			exactMatch: true,
		},
		reportingLab: {
			attribute: "reportingLaboratory",
			value: "",
			exactMatch: true,
		},
		discipline: {
			attribute: "discipline",
			value: "",
			exactMatch: true,
		},
		abnormal: {
			attribute: "abnormal",
			value: "",
			exactMatch: true,
		},
		requestStatus: {
			attribute: "requestStatus",
			value: "",
			exactMatch: true,
		},
		reportStatus: {
			attribute: "reportStatus",
			value: "",
			exactMatch: false,
		},
		orderingPractitioner: {
			attribute: "orderingPractitioner",
			value: "",
			exactMatch: true,
		},
		admittingPractitioner: {
			attribute: "admittingPractitioner",
			value: "",
			exactMatch: true,
		},
		attendingPractitioner: {
			attribute: "attendingPractitioner",
			value: "",
			exactMatch: true,
		},
		ccPractitioner: {
			attribute: "ccPractitioner",
			value: "",
			exactMatch: false,
		},
	}

	function filterResults(filterKey, filterValue)
	{
		console.info("filtering", filterKey, filterValue);
		filterKey.value = filterValue;

		const performFilter = function ()
		{
			const element = jQuery(this);
			let visible = true;

			// determine visibility of element based on applied filters
			for (const [key, options] of Object.entries(filter))
			{
				if(options.exactMatch)
				{
					visible = (!options.value || element.attr(options.attribute) === options.value);
				}
				else
				{
					visible = (!options.value ||
							String(element.attr(options.attribute)).includes(String(options.value)));
				}
				if(!visible)
				{
					break;
				}
			}
			element.toggle(visible);
		};
		jQuery(".evenLine").each(performFilter);
		jQuery(".oddLine").each(performFilter);
	}

	jQuery( document ).ready(function()
	{
		// initialize various display states
		let rowSelector = jQuery(".result_row");
		rowSelector.not(".removed").find(".unremove_button").toggle(false);
		rowSelector.filter(".removed").find(".remove_button").toggle(false);

		let duplicateSelector = rowSelector.filter(".duplicate");
		duplicateSelector.find(".remove_button").attr("disabled", true);
		duplicateSelector.find(".unremove_button").attr("disabled", true);
	});
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

#patientTable,
#filterTable {
	border: 1px solid lightgrey;
}
#patientTable td,
#filterTable td {
	font-weight: normal;
}
#patientTable .table-title,
#filterTable .table-title {
	background-color: lightgrey;
}
#filterTable select {
	width: 100%;
}

.page-wrapper {
	display: flex;
	flex-direction: column;
}
.duplicate {
	color: darkgreen;
	display: none;
}
.removed {
	color: red;
	display: none;
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
			boolean removeSearchResults = OscarProperties.getInstance().isPropertyActive("olis.enable_search_result_removal");

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
			List<OLISSearchResultTransfer> resultList = (List<OLISSearchResultTransfer>) request.getAttribute("resultList");
			String continuationPointer = (String) request.getAttribute("continuationPointer");
			boolean hasBlockedContent = (boolean) request.getAttribute("blockedContent");

			// ordering must be preserved for matching uuid to handler below
			List<OLISHL7Handler> resultHandlers = resultList.stream()
					.map(OLISSearchResultTransfer::getUuid)
					.map(OLISResultsAction::getHandlerByUUID)
					.collect(Collectors.toList());

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
			}%>
			<table>
				<tr>
					<td colspan=8>Found <%=resultList.size() %> result(s)</td>
				</tr>
				<% if (resultList.size() > 0) { %>
					<tr>
						<td>
							<table id="filterTable">
								<tbody>
								<tr class="table-title">
									<th colspan="4">
										<span>Filter Results</span>
									</th>
								</tr>
								<tr>
									<td>
										<span>Patient name:</span>
									</td>
									<td>
										<select name="patientFilter" onChange="filterResults(filter.patientName, this.value)">
											<option value="">All Patients</option>
											<% Set<String> patientNames = resultHandlers.stream()
													.map(OLISHL7Handler::getPatientName).map(String::trim)
													.filter(StringUtils::isNotBlank)
													.collect(Collectors.toSet());
												for (String name : patientNames)
												{%>
											<option value="<%=name%>"><%=name%></option>
											<% } %>
										</select>
									</td>
									<td>
										<span>Reporting laboratory:</span>
									</td>
									<td>
										<select name="reportingLabFilter" onChange="filterResults(filter.reportingLab, this.value)">
											<option value="">All Labs</option>
											<%
												Set<String> reportingLabs = resultHandlers.stream()
														.map(OLISHL7Handler::getReportingFacilityName).map(String::trim)
														.filter(StringUtils::isNotBlank)
														.collect(Collectors.toSet());
												for (String name: reportingLabs)
												{%>
											<option value="<%=name%>"><%=name%></option>
											<% } %>
										</select>
									</td>
								</tr>
								<tr>
									<td></td>
									<td></td>
									<td>
										<span>Performing laboratory:</span>
									</td>
									<td>
										<select name="performingLabFilter" onChange="filterResults(filter.performingLab, this.value)">
											<option value="">All Labs</option>
											<%
												Set<String> performingLabs = resultHandlers.stream()
														.map(OLISHL7Handler::getPerformingFacilityName).map(String::trim)
														.filter(StringUtils::isNotBlank)
														.collect(Collectors.toSet());
												for (String name: performingLabs)
												{%>
											<option value="<%=name%>"><%=name%></option>
											<% } %>
										</select>
									</td>

								</tr>
								<tr>
									<td>
										<span>Ordering Provider:</span>
									</td>
									<td>
										<select name="orderingPractitionerFilter" onChange="filterResults(filter.orderingPractitioner, this.value)">
											<option value="">Any Provider</option>
											<% Set<String> orderingProviders = resultHandlers.stream()
													.map(OLISHL7Handler::getDocName)
													.filter(StringUtils::isNotBlank)
													.collect(Collectors.toSet());
												for (String provider : orderingProviders)
												{%>
											<option value="<%=provider%>"><%=provider%></option>
											<% } %>
										</select>
									</td>
									<td>
										<span>Discipline:</span>
									</td>
									<td>
										<select name="disciplineFilter" onChange="filterResults(filter.discipline, this.value)">
											<option value="">All Disciplines</option>
											<% Set<String> disciplines = resultHandlers.stream()
													.map(OLISHL7Handler::getDisciplines).flatMap(List::stream)
													.filter(StringUtils::isNotBlank)
													.collect(Collectors.toSet());
												for (String discipline : disciplines)
												{%>
											<option value="<%=discipline%>"><%=discipline%></option>
											<% } %>
										</select>
									</td>
								</tr>
								<tr>
									<td>
										<span>Admitting Provider:</span>
									</td>
									<td>
										<select name="admittingPractitionerFilter" onChange="filterResults(filter.admittingPractitioner, this.value)">
											<option value="">Any Provider</option>
											<% Set<String> admittingProviders = resultHandlers.stream()
													.map(OLISHL7Handler::getAdmittingProviderNameShort)
													.filter(StringUtils::isNotBlank)
													.collect(Collectors.toSet());
												for (String provider : admittingProviders)
												{%>
											<option value="<%=provider%>"><%=provider%></option>
											<% } %>
										</select>
									</td>
									<td>
										<span>Abnormal:</span>
									</td>
									<td>
										<select name="abnormalFilter" onChange="filterResults(filter.abnormal, this.value)">
											<option value="">All</option>
											<option value="true">Yes</option>
											<option value="false">No</option>
										</select>
									</td>
								</tr>
								<tr>
									<td>
										<span>Attending Provider:</span>
									</td>
									<td>
										<select name="attendingPractitionerFilter" onChange="filterResults(filter.attendingPractitioner, this.value)">
											<option value="">Any Provider</option>
											<% Set<String> attendingProviders = resultHandlers.stream()
													.map(OLISHL7Handler::getAttendingProviderNameShort)
													.filter(StringUtils::isNotBlank)
													.collect(Collectors.toSet());
												for (String provider : attendingProviders)
												{%>
											<option value="<%=provider%>"><%=provider%></option>
											<% } %>
										</select>
									</td>
									<td>
										<span>Request Status:</span>
									</td>
									<td>
										<select name="requestStatusFilter" onChange="filterResults(filter.requestStatus, this.value)">
											<option value="">Any Status</option>
											<% Set<String> requestStatuses = resultHandlers.stream()
													.map(OLISHL7Handler::getAllObrStatuses)
													.flatMap(Set::stream)
													.collect(Collectors.toSet());
												for (String status : requestStatuses)
												{%>
											<option value="<%=status%>"><%=OLISHL7Handler.getObrTestResultStatusValue(status)%></option>
											<% } %>
										</select>
									</td>
								</tr>
								<tr>
									<td>
										<span>CC Provider:</span>
									</td>
									<td>
										<select name="ccPractitionerFilter" onChange="filterResults(filter.ccPractitioner, this.value)">
											<option value="">Any Provider</option>
											<% Set<String> ccProviders = resultHandlers.stream()
													.map(OLISHL7Handler::getCCDocsList).flatMap(List::stream)
													.filter(StringUtils::isNotBlank)
													.collect(Collectors.toSet());
												for (String provider : ccProviders)
												{%>
											<option value="<%=provider%>"><%=provider%></option>
											<% } %>
										</select>
									</td>
									<td>
										<span>Report Status:</span>
									</td>
									<td>
										<select name="reportStatusFilter" onChange="filterResults(filter.reportStatus, this.value)">
											<option value="">Any Status</option>
											<% Set<String> reportStatuses = resultHandlers.stream()
													.map(OLISHL7Handler::getAllObxStatuses)
													.flatMap(Set::stream)
													.collect(Collectors.toSet());
												for (String status : reportStatuses)
												{%>
											<option value="<%=status%>"><%=OLISHL7Handler.getObxTestResultStatusValue(status)%></option>
											<% } %>
										</select>
									</td>
								</tr>
								</tbody>
							</table>
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
					<tr>
						<td colspan="10">
							<input type="button" onClick="toggleShowDuplicates()"
								   value="Show/Hide duplicates"
								   title="Show or hide results already within the system"/>
							<% if(removeSearchResults)
							{ %>
							<input type="button" onClick="toggleShowRemoved()"
								   value="Show/Hide removed"
								   title="Show or hide removed results"/>
							<%}%>
						</td>
					</tr>
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
						<% if(removeSearchResults)
						{%>
						<th class="unsortable"></th>
						<%
						}
						%>
					</tr>
					
					<%  int lineNum = 0;
						for(int handlerIndex=0; handlerIndex< resultHandlers.size(); handlerIndex++)
						{
							OLISSearchResultTransfer transfer = resultList.get(handlerIndex);
							String resultUuid = transfer.getUuid();
							OLISHL7Handler result = resultHandlers.get(handlerIndex);

							// show one row per OBR, so that individual statuses can be displayed. Required feature for OLIS conformance.
							for(int i=0; i < result.getOBRCount(); i++)
							{
								int obrRep = result.getMappedOBR(i); // aka use sort keys
								String cssClass =
										(++lineNum % 2 == 1 ? "oddLine" : "evenLine") +
										(transfer.isDuplicate() ? " duplicate" : "") +
										(transfer.isHiddenByUser() ? " removed" : "") +
										(" result_row " + resultUuid + "_row_selector");
							%>
							<tr class="<%=cssClass%>"
								patientName="<%=result.getPatientName()%>"
							    reportingLaboratory="<%=result.getReportingFacilityName()%>"
								performingLaboratory="<%=result.getPerformingFacilityName()%>"
								orderingPractitioner="<%=result.getDocName()%>"
								admittingPractitioner="<%=result.getAdmittingProviderNameShort()%>"
								attendingPractitioner="<%=result.getAttendingProviderNameShort()%>"
								ccPractitioner="<%=result.getCCDocs()%>"
								discipline="<%=result.getOBRCategory(obrRep)%>"
								abnormal="<%=result.isOBRAbnormal(obrRep)%>"
								requestStatus="<%=result.getObrStatus(obrRep)%>"
								reportStatus="<%=
								IntStream.range(0, result.getOBXCount(obrRep))
								.mapToObj(obxRep -> result.getOBXResultStatus(obrRep, obxRep))
								.distinct()
								.collect(Collectors.joining(","))%>"
							>
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
								<% if(removeSearchResults)
								{%>
								<td>
									<input type="button"
										   value="Remove"
										   class="remove_button"
										   onClick="hideResult('<%=resultUuid %>','<%=transfer.getAccessionId()%>','<%=transfer.getVersionId()%>', '<%=(demographic != null) ? demographic.getId() : "" %>', true); return false;"/>
									<input type="button"
										   value="Un-Remove"
										   class="unremove_button"
										   onClick="hideResult('<%=resultUuid %>','<%=transfer.getAccessionId()%>','<%=transfer.getVersionId()%>', '<%=(demographic != null) ? demographic.getId() : "" %>', false); return false;"/>
								</td> <%
								}%>

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
		</td>
	</tr></tbody>
</table>
<pre style="display: none;"><%=resp%></pre>
</div>
</body>
</html>
