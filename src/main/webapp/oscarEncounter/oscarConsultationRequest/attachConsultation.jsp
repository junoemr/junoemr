<%--

    Copyright (c) 2008-2012 Indivica Inc.
    
    This software is made available under the terms of the
    GNU General Public License, Version 2, 1991 (GPLv2).
    License details are available via "indivica.ca/gplv2"
    and "gnu.org/licenses/gpl-2.0.html".
    
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

<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%
String user_no = (String) session.getAttribute("user");
String userfirstname = (String) session.getAttribute("userfirstname");
String userlastname = (String) session.getAttribute("userlastname");
%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<jsp:useBean id="oscarVariables" class="java.util.Properties" scope="page" />
<%@ page
	import="org.oscarehr.consultations.dao.ConsultRequestDao,
	org.oscarehr.consultations.model.ConsultDocs,
	org.oscarehr.consultations.service.ConsultationAttachmentService,
	org.oscarehr.eform.model.EFormData,
	org.oscarehr.util.LoggedInInfo,
	org.oscarehr.util.SpringUtils, oscar.MyDateFormat,
	oscar.OscarProperties,
	oscar.dms.EDoc,
	oscar.dms.EDocUtil,
	oscar.oscarLab.ca.on.CommonLabResultData"%>
<%@ page import="oscar.oscarLab.ca.on.LabResultData" %>
<%@ page import="oscar.util.ConversionUtils" %>
<%@ page import="oscar.util.StringUtils" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.List" %>

<%
	//preliminary JSP code
	LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
	ConsultationAttachmentService consultationAttachmentService = SpringUtils.getBean(ConsultationAttachmentService.class);
	ConsultRequestDao consultRequestDao = SpringUtils.getBean(ConsultRequestDao.class);

	String demoNo = request.getParameter("demo");
	String requestId = request.getParameter("requestId");
	String providerNo = request.getParameter("provNo");

	if(demoNo == null && requestId == null)
	{
		response.sendRedirect("../error.jsp");
	}

	if(demoNo == null || demoNo.equals("null"))
	{
		demoNo = consultRequestDao.find(Integer.parseInt(requestId)).getDemographicId().toString();
	}
	Integer demographicNo = Integer.parseInt(demoNo);

	String patientName = EDocUtil.getDemographicName(loggedInInfo, demoNo);
	String[] docType = {ConsultDocs.DOCTYPE_DOC, ConsultDocs.DOCTYPE_LAB, ConsultDocs.DOCTYPE_EFORM};
	String http_user_agent = request.getHeader("User-Agent");
	boolean onIPad = http_user_agent.indexOf("iPad") >= 0;
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN"
   "http://www.w3.org/TR/html4/strict.dtd">
<html:html locale="true">
<head>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery_oscar_defaults.js"></script>

<title><bean:message
	key="oscarEncounter.oscarConsultationRequest.AttachDocPopup.title" /></title>

<link rel="stylesheet" type="text/css" media="all" href="../../share/css/extractedFromPages.css" />

<script type="text/javascript">
//<!--   
<%
    final String PRINTABLE_IMAGE = request.getContextPath() + "/images/printable.png";
    final String PRINTABLE_TITLE = "This file can be automatically printed to PDF with the consultation request.";
    final String PRINTABLE_ALT = "Printable";
    final String UNPRINTABLE_IMAGE = request.getContextPath() + "/images/notprintable.png";
    final String UNPRINTABLE_TITLE = "This file must be manually printed.";
    final String UNPRINTABLE_ALT = "Unprintable";

    String dateFormat = OscarProperties.getInstance().getDisplayDateFormat();

	List<EDoc> allDocuments = EDocUtil.listDocs(loggedInInfo, "demographic", demoNo, null, EDocUtil.PRIVATE, EDocUtil.EDocSort.OBSERVATIONDATE);
	CommonLabResultData labData = new CommonLabResultData();
    List<LabResultData> allLabs = labData.populateLabResultsData(loggedInInfo, "",demoNo, "", "","","U");
    Collections.sort(allLabs);
    List<EFormData> allEForms = consultationAttachmentService.getAllEForms(demographicNo);

	List<EDoc> attachedDocuments;
	List<LabResultData> attachedLabs;
	List<EFormData> attachedEForms;

	if(StringUtils.isNumeric(requestId))
	{
		attachedDocuments = consultationAttachmentService.getAttachedDocuments(loggedInInfo, demoNo, requestId);
		attachedLabs = consultationAttachmentService.getAttachedLabs(loggedInInfo, demoNo, requestId);
		attachedEForms = consultationAttachmentService.getAttachedEForms(demographicNo, Integer.parseInt(requestId));
	}
	else // new request
	{
		attachedDocuments = new ArrayList<EDoc>(0);
		attachedLabs = new ArrayList<LabResultData>(0);
		attachedEForms = new ArrayList<EFormData>(0);
	}
	String attachedDocs = "";
	if(requestId == null || requestId.isEmpty() || requestId.equals("null"))
	{
		attachedDocs = "window.opener.document.EctConsultationFormRequestForm.documents.value";
	}
	else
	{
		for (EDoc document : attachedDocuments)
		{
		    attachedDocs += (attachedDocs.equals("") ? "" : "|") + ConsultDocs.DOCTYPE_DOC + document.getDocId();
		}
		for (LabResultData lab : attachedLabs)
		{
		    attachedDocs += (attachedDocs.equals("") ? "" : "|") + ConsultDocs.DOCTYPE_LAB + lab.getSegmentID();
		}
		for (EFormData eForm : attachedEForms)
		{
		    attachedDocs += (attachedDocs.equals("") ? "" : "|") + ConsultDocs.DOCTYPE_EFORM + eForm.getId();
		}
		attachedDocs = "\"" + attachedDocs + "\"";
	}
%>  

	//if consultation has not been saved, load existing docs into proper select boxes
	function init()
	{
		var docs = <%= attachedDocs %>;
		docs = docs.split("|");
		checkDocuments(docs);
	}

	// set the checked state of attachments
	function checkDocuments(docs)
	{
		if (docs == null)
		{
			return;
		}
		for(var idx = 0; idx < docs.length; ++idx)
		{
	        if (docs[idx].length < 2)
	        {
	            continue;
	        }

	        let inputName = "";
	        switch (docs[idx].charAt(0))
	        {
		        case "<%=ConsultDocs.DOCTYPE_DOC%>": inputName = "docNo"; break;
		        case "<%=ConsultDocs.DOCTYPE_LAB%>": inputName = "labNo"; break;
		        case "<%=ConsultDocs.DOCTYPE_EFORM%>": inputName = "eFormNo"; break;
		        default: console.error("Invalid doctype: " + docs[idx].charAt(0)); continue;
	        }
	        $("input[name='" + inputName + "'][value='" + docs[idx].substring(1) + "']").attr("checked", "checked");
	    }
	}

	function save()
	{
		var ret;
		console.info(document.forms[0].requestId.value);
		if (document.forms[0].requestId.value == null || document.forms[0].requestId.value === "null")
		{
			var saved = "";
			var list = window.opener.document.getElementById("attachedList");
			var paragraph = window.opener.document.getElementById("attachDefault");

			paragraph.innerHTML = "";

			//delete what we have before adding new docs to list
			while (list.firstChild)
			{
				list.removeChild(list.firstChild);
			}

		    $("input[name='docNo']:checked").each(function ()
		    {
			    saved += (saved === "" ? "" : "|") + "<%=ConsultDocs.DOCTYPE_DOC%>" + $(this).attr("value");
			    listElem = window.opener.document.createElement("li");
			    listElem.innerHTML = $(this).next().get(0).innerHTML;
			    listElem.className = "doc";
			    list.appendChild(listElem);
		    });
		    $("input[name='labNo']:checked").each(function ()
		    {
			    saved += (saved === "" ? "" : "|") + "<%=ConsultDocs.DOCTYPE_LAB%>" + $(this).attr("value");
			    listElem = window.opener.document.createElement("li");
			    listElem.innerHTML = $(this).next().get(0).innerHTML;
			    listElem.className = "lab";
			    list.appendChild(listElem);
		    });
		    $("input[name='eFormNo']:checked").each(function ()
		    {
			    saved += (saved === "" ? "" : "|") + "<%=ConsultDocs.DOCTYPE_EFORM%>" + $(this).attr("value");
			    listElem = window.opener.document.createElement("li");
			    listElem.innerHTML = $(this).next().get(0).innerHTML;
			    listElem.className = "eform";
			    list.appendChild(listElem);
		    });

			window.opener.document.EctConsultationFormRequestForm.documents.value = saved;

			if (list.childNodes.length === 0)
			{
				paragraph.innerHTML = "<bean:message key="oscarEncounter.oscarConsultationRequest.AttachDoc.Empty"/>";
			}
			ret = false;
		}
		else
		{
			window.opener.updateAttached();
			ret = true;
		}
		if (!ret) window.close();
			return ret;
	}

	function previewPDF(docId, url)
	{
		$("#previewPane").attr("src",
			"<%= request.getContextPath() %>/oscarEncounter/oscarConsultationRequest/displayImage.jsp?url="
			+ encodeURIComponent("<%= request.getContextPath() %>" + "/dms/ManageDocument.do?method=view&doc_no=" + docId)
			+ "&link=" + encodeURIComponent(url));
	}

	function previewHTML(url)
	{
		$("#previewPane").attr("src", url);
	}

	function previewImage(url)
	{
		$("#previewPane").attr("src", "<%= request.getContextPath() %>/oscarEncounter/oscarConsultationRequest/displayImage.jsp?url=" + encodeURIComponent(url));
	}

	function toggleSelectAll()
	{
		$("input[type='checkbox']").attr("checked", $("#selectAll").attr("checked"));
	}

	</script>

	<style type="text/css">
		.flexV {
			display: flex;
			flex-direction: column;
		}
		.flexH {
			display: flex;
			flex-direction: row;
		}
		.flexGrow {
			flex-grow: 1;
		}
		.itemGroup {
			align-items: baseline;
			justify-content: space-between;
		}
		.hiddenLabel {
			display: none;
		}
		.submitButton {
			padding: 5px;
			margin: 10px;
			color: #ffffff;
			background-color: #5cb85c;
			border-color: #4cae4c;
		}
		body {
			font-family: Verdana, Tahoma, Arial, sans-serif;
			background-color: #ddddff;
		}
		body h3 {
			text-align: left;
			margin-left: 5px;
		}
		#contentFrame {
			flex-wrap: nowrap;
			width: auto;
			border: solid 1px blue;
			font-size: x-small;
			background-color:white;
			margin: 5px;
		}
		#tableFrame {
			position:relative;
			border-right: solid 1px blue;
			padding:5px;
		}
		.table-scroll-wrapper {
			height:600px;
			overflow:auto;
		}
		#contentTable {
			width: 250px;
			overflow:auto;
			font-size: x-small;
		}
		#previewPane {
			width:100%;
			min-height: 600px;
			overflow: auto;
			border:0;
		}
		/*.content-wrapper {*/
			/**/
		/*}*/


	</style>
</head>
<body onload="init()">
	<h3><bean:message key="oscarEncounter.oscarConsultationRequest.AttachDocPopup.header"/>&nbsp<%=patientName%></h3>
<html:form action="/oscarConsultationRequest/attachDoc">
	<html:hidden property="requestId" value="<%=requestId%>" />
	<html:hidden property="demoNo" value="<%=demoNo%>" />
	<html:hidden property="providerNo" value="<%=providerNo%>" />
	<div id="contentFrame" class="flexH">
		<div id="tableFrame" class="flexV">
			<%
			if (allLabs.isEmpty() && allDocuments.isEmpty() && allEForms.isEmpty())
			{
			%>
			<span> There are no documents to attach. </span>
			<%
			}
			else
			{
			%>
			<h2 style="text-align: center"><bean:message
					key="oscarEncounter.oscarConsultationRequest.AttachDocPopup.available" /></h2>
			<div class="table-scroll-wrapper">
				<table id="contentTable">
					<tbody>
						<td>
							<div class="itemGroup flexH">
								<div>
									<input class="tightCheckbox1" id="selectAll"
									       type="checkbox" onclick="toggleSelectAll()"
									       value="" title="Select/un-select all documents."
									/> Select all
								</div>
							</div>
						</td>
			            <% if(!allDocuments.isEmpty())
			            {%>
						<tr>
							<th>Documents</th>
						</tr>
			            <%
			            }
			            String url;
			            String printTitle;
			            String printImage;
			            String printAlt;
			            String date;
			            String truncatedDisplayName;
			            for(EDoc curDoc : allDocuments)
			            {
			                String dStatus = "";
			                if ((curDoc.getStatus() + "").compareTo("A") == 0) dStatus="active";
			                else if ((curDoc.getStatus() + "").compareTo("H") == 0) dStatus="html";
			                url = request.getContextPath() + "/oscarEncounter/oscarConsultationRequest/"
			                    + "documentGetFile.jsp?document=" + StringEscapeUtils.escapeJavaScript(curDoc.getFileName())
			                    + "&type=" + dStatus + "&doc_no=" + curDoc.getDocId();
			                String onClick = "";

			                if (curDoc.isPDF()) {
			                    onClick = "javascript:previewPDF('" + curDoc.getDocId() + "','" + StringEscapeUtils.escapeJavaScript(url) + "');";
			                }
			                else if (curDoc.isImage()) {
			                    onClick = "javascript:previewImage('" + url + "');";
			                }
			                else {
			                    onClick = "javascript:previewHTML('" + url + "');";
			                }

			                if (curDoc.isPrintable()) {
			                    printImage = PRINTABLE_IMAGE;
			                    printTitle = PRINTABLE_TITLE;
			                    printAlt   = PRINTABLE_ALT;

			                }
			                else {
			                    printImage = UNPRINTABLE_IMAGE;
			                    printTitle = UNPRINTABLE_TITLE;
			                    printAlt   = UNPRINTABLE_ALT;
			                }
				            date = ConversionUtils.toDateString(MyDateFormat.getCalendar(curDoc.getObservationDate()).getTime(), dateFormat);
			                truncatedDisplayName = StringUtils.maxLenString(curDoc.getDescription(),14,11,"");
			                if (StringUtils.isNullOrEmpty(truncatedDisplayName))
			                {
			                    truncatedDisplayName = "(none)";
			                }
			                %>
							<td class="flexV">
				                <div class="doc itemGroup flexH" title="<%=curDoc.getDescription()%>" id="<%=docType[0]+curDoc.getDocId()%>">
				                    <div>
				                        <input class="tightCheckbox1"
						                        type="checkbox" name="docNo" id="docNo<%=curDoc.getDocId()%>"
						                        value="<%=curDoc.getDocId()%>"/>
					                    <div class="hiddenLabel doc"><%=truncatedDisplayName%></div>
					                    <img title="<%= printTitle %>" src="<%= printImage %>" alt="<%= printAlt %>">
					                    <a class="docPreview" href="#" onclick="<%=onClick%>" >
					                        <span class="text"><%=truncatedDisplayName%></span>
					                    </a>

					               </div>
					               <div>
					                    <a class="docPreview" href="#" onclick="<%=onClick%>" >
					                        <span>... <%=date%></span>
					                    </a>
				                   </div>
			                   </div>
							</td>
			                <%
			                }
			                if(!allLabs.isEmpty())
			                {
			                %>
							<tr>
								<th>Labs</th>
							</tr>
			                <%
			                }

			                String labDisplayName;
			                printImage = PRINTABLE_IMAGE;
			                printTitle = PRINTABLE_TITLE;
			                printAlt = PRINTABLE_ALT;
			                for(LabResultData result : allLabs)
			                {
			                     if ( result.isMDS() ){
			                         url ="../../oscarMDS/SegmentDisplay.jsp?providerNo="+providerNo+"&segmentID="+result.segmentID+"&status="+result.getReportStatus();
			                         labDisplayName = result.getDiscipline();
			                     }else if (result.isCML()){
			                         url ="../../lab/CA/ON/CMLDisplay.jsp?providerNo="+providerNo+"&segmentID="+result.segmentID;
			                         labDisplayName = result.getDiscipline();
			                     }else if (result.isHL7TEXT()){
			                         // Modified display name to append the lab's date and time.
			                         labDisplayName = result.getDiscipline();
			                         url ="../../lab/CA/ALL/labDisplay.jsp?providerNo="+providerNo+"&segmentID="+result.segmentID;
			                     }else{
			                         url ="../../lab/CA/BC/labDisplay.jsp?segmentID="+result.segmentID+"&providerNo="+providerNo;
			                         labDisplayName = result.getDiscipline();
			                     }

			                     if (onIPad) {
			                         truncatedDisplayName = labDisplayName;
			                     }
			                     else {
			                         truncatedDisplayName = StringUtils.maxLenString(labDisplayName,14,11,"");
			                     }
				                date = ConversionUtils.toDateString(result.getDateObj(), dateFormat);
				                if(StringUtils.isNullOrEmpty(truncatedDisplayName))
				                {
					                truncatedDisplayName = "(none)";
				                }
				                %>
							<td class="flexV">
								<div class="lab itemGroup flexH" title="<%=labDisplayName%>" id="<%=docType[1]+result.labPatientId%>">
									<div>
										<input class="tightCheckbox1" type="checkbox"
										       name="labNo" id="labNo<%=result.segmentID%>"
										       value="<%=result.segmentID%>"/>
										<div class="hiddenLabel lab"><%=labDisplayName%></div>
										<img title="<%= printTitle %>" src="<%= printImage %>" alt="<%= printAlt %>">
										<a class="labPreview" href="#" onclick="javascript:previewHTML('<%=url%>');">
											<span class="text"><%=truncatedDisplayName%></span>
										</a>
									</div>
									<div>
										<a class="labPreview" href="#" onclick="javascript:previewHTML('<%=url%>');">
											<span>... <%=date%></span>
										</a>
									</div>
								</div>
							</td>
								<%
			                }
							if(!allEForms.isEmpty())
							{
							%>
							<tr>
								<th>EForms</th>
							</tr>
							<%
							}
		                    for(EFormData eForm : allEForms)
		                    {
		                        String eFormDisplayName = eForm.getFormName();
		                        url = request.getContextPath() + "/eform/efmshowform_data.jsp?fdid=" + eForm.getId() + "&parentAjaxId=consult";
		                        date = ConversionUtils.toDateString(eForm.getFormDate(), dateFormat);
								%>
							<td class="flexV">
								<div class="eform itemGroup flexH" title="<%=eFormDisplayName%>" id="<%=docType[2]+eForm.getId()%>">
									<div>
										<input class="tightCheckbox1" type="checkbox"
										       name="eFormNo" id="eFormNo<%=eForm.getId()%>"
										       value="<%=eForm.getId()%>"/>
										<div class="hiddenLabel eform"><%=eFormDisplayName%></div>
										<img title="<%= printTitle %>" src="<%= printImage %>" alt="<%= printAlt %>">
										<a class="labPreview" href="#" onclick="javascript:previewHTML('<%=url%>');">
											<span class="text"><%=eFormDisplayName%></span>
										</a>
									</div>
									<div>
										<a class="labPreview" href="#" onclick="javascript:previewHTML('<%=url%>');">
											<span>... <%=date%></span>
										</a>
									</div>
								</div>
							</td>
							<%
		                    }
						}
		                %>
					</tbody>
				</table>
			</div>
			<input type="submit" class="btn submitButton" name="submit"
			       value="<bean:message key="oscarEncounter.oscarConsultationRequest.AttachDocPopup.submit"/>"
			       onclick="return save();" />
		</div>
		<div class="flexV flexGrow">
			<h2 style="text-align: center"><bean:message
					key="oscarEncounter.oscarConsultationRequest.AttachDocPopup.preview" /></h2>
			<iframe id="previewPane" class="flexGrow"></iframe>
		</div>
	</div>
</html:form>
</body>
</html:html>
