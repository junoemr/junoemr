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
	oscar.oscarLab.ca.on.LabResultData"%>
<%@ page import="oscar.util.ConversionUtils" %>
<%@ page import="oscar.util.StringUtils" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="org.oscarehr.dataMigration.model.hrm.HrmDocument" %>

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
	String[] docType = {ConsultDocs.DOCTYPE_DOC, ConsultDocs.DOCTYPE_LAB, ConsultDocs.DOCTYPE_EFORM, ConsultDocs.DOCTYPE_HRM};
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
<script type="text/javascript" src="attachConsultation.js"></script>
<link rel="stylesheet" type="text/css" href="attachConsultation.css" />

<title><bean:message key="oscarEncounter.oscarConsultationRequest.AttachDocPopup.title" /></title>
<%
    final String PRINTABLE_IMAGE = request.getContextPath() + "/images/printable.png";
	final String UNPRINTABLE_IMAGE = request.getContextPath() + "/images/notprintable.png";

	ResourceBundle oscarResources = ResourceBundle.getBundle("oscarResources", request.getLocale());
	final String PRINTABLE_TITLE = oscarResources.getString("oscarEncounter.oscarConsultationRequest.AttachConsultation.PrintableTitle");
	final String PRINTABLE_ALT = oscarResources.getString("oscarEncounter.oscarConsultationRequest.AttachConsultation.PrintableTitleAlt");
	final String UNPRINTABLE_TITLE = oscarResources.getString("oscarEncounter.oscarConsultationRequest.AttachConsultation.UnprintableTitle");
	final String UNPRINTABLE_ALT = oscarResources.getString("oscarEncounter.oscarConsultationRequest.AttachConsultation.UnprintableTitleAlt");

    String dateFormat = OscarProperties.getInstance().getDisplayDateFormat();

	List<EDoc> allDocuments = consultationAttachmentService.getAllDocuments(loggedInInfo, demoNo);
    List<LabResultData> allLabs = consultationAttachmentService.getAllLabs(loggedInInfo, demoNo, requestId);
    List<EFormData> allEForms = consultationAttachmentService.getAllEForms(demographicNo);
	List<HrmDocument> allHRM = consultationAttachmentService.getAllHRMList(demographicNo);

	List<EDoc> attachedDocuments;
	List<LabResultData> attachedLabs;
	List<EFormData> attachedEForms;
	List<HrmDocument> attachedHrm;

	boolean existingRequest = StringUtils.isNumeric(requestId);
	if(existingRequest)
	{
		attachedDocuments = consultationAttachmentService.getAttachedDocuments(loggedInInfo, demoNo, requestId);
		attachedLabs = consultationAttachmentService.getAttachedLabs(loggedInInfo, demoNo, requestId);
		attachedEForms = consultationAttachmentService.getAttachedEForms(demographicNo, Integer.parseInt(requestId));
		attachedHrm = consultationAttachmentService.getAttachedHRMList(demographicNo, Integer.parseInt(requestId));
	}
	else // new request
	{
		attachedDocuments = new ArrayList<>(0);
		attachedLabs = new ArrayList<>(0);
		attachedEForms = new ArrayList<>(0);
		attachedHrm = new ArrayList<>(0);
	}
	String attachedDocs = "";
	if(existingRequest)
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
		for (HrmDocument hrmDocument : attachedHrm)
		{
			attachedDocs += (attachedDocs.equals("") ? "" : "|") + ConsultDocs.DOCTYPE_HRM + hrmDocument.getId();
		}
	}
%>
</head>
<body onload="Oscar.AttachConsultation.init(<%=!existingRequest%>,'<%=attachedDocs%>')">
	<div class="header">
		<h3><bean:message key="oscarEncounter.oscarConsultationRequest.AttachDocPopup.header"/>&nbsp<%=patientName%></h3>
	</div>
	<html:form action="/oscarConsultationRequest/attachDoc">
	<html:hidden property="requestId" value="<%=requestId%>" />
	<html:hidden property="demoNo" value="<%=demoNo%>" />
	<html:hidden property="providerNo" value="<%=providerNo%>" />
	<div id="contentFrame" class="flexH flexGrow">
		<div id="tableFrame" class="flexV">
			<%
			if (allLabs.isEmpty() && allDocuments.isEmpty() && allEForms.isEmpty() && allHRM.isEmpty())
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
									       type="checkbox" onclick="Oscar.AttachConsultation.toggleSelectAll()"
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
			            String date = "No Date";
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
			                    onClick = "Oscar.AttachConsultation.previewPDF('" + curDoc.getDocId() + "','" +
					                    StringEscapeUtils.escapeJavaScript(url) + "','" + request.getContextPath() + "');";
			                }
			                else if (curDoc.isImage()) {
			                    onClick = "Oscar.AttachConsultation.previewImage('" + url + "','" + request.getContextPath() + "');";
			                }
			                else {
			                    onClick = "Oscar.AttachConsultation.previewHTML('" + url + "');";
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
			                String dateString = org.apache.commons.lang.StringUtils.trimToNull(curDoc.getObservationDate());
			                if (dateString != null)
			                {
			                    date = ConversionUtils.toDateString(MyDateFormat.getCalendar(dateString).getTime(), dateFormat);
			                }
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
						                        value="<%=curDoc.getDocId()%>"
				                                <%=curDoc.isPrintable() ? "":"disabled=\"disabled\""%>/>
					                    <div class="hiddenLabel doc"><%=truncatedDisplayName%></div>
					                    <img title="<%= printTitle %>" src="<%= printImage %>" alt="<%= printAlt %>">
					                    <a class="preview-link-name" href="#" onclick="<%=onClick%>" >
					                        <span class="text"><%=truncatedDisplayName%></span>
					                    </a>

					               </div>
					               <div>
					                    <a class="preview-link-date" href="#" onclick="<%=onClick%>" >
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
										<a class="preview-link-name" href="#" onclick="Oscar.AttachConsultation.previewHTML('<%=url%>');">
											<span class="text"><%=truncatedDisplayName%></span>
										</a>
									</div>
									<div>
										<a class="preview-link-date" href="#" onclick="Oscar.AttachConsultation.previewHTML('<%=url%>');">
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
									<div  class="flexH">
										<input class="tightCheckbox1" type="checkbox"
										       name="eFormNo" id="eFormNo<%=eForm.getId()%>"
										       value="<%=eForm.getId()%>"/>
										<div class="hiddenLabel eform"><%=eFormDisplayName%></div>
										<img title="<%= printTitle %>" src="<%= printImage %>" alt="<%= printAlt %>">
										<a class="preview-link-name" href="#" onclick="Oscar.AttachConsultation.previewHTML('<%=url%>');">
											<span class="text"><%=eFormDisplayName%></span>
										</a>
									</div>
									<div class="flexH">
										<a class="preview-link-date" href="#" onclick="Oscar.AttachConsultation.previewHTML('<%=url%>');">
											<span>... <%=date%></span>
										</a>
									</div>
								</div>
							</td>
							<%
		                    }
							if(!allHRM.isEmpty())
							{
							%>
							<tr>
								<th>HRM Reports</th>
							</tr>
							<%
							}
							for(HrmDocument hrmDocument : allHRM)
							{
								String hrmDisplayName = hrmDocument.getDescription();
								url = request.getContextPath() + "/hospitalReportManager/displayHRMReport.jsp?id=" + hrmDocument.getId();
								date = ConversionUtils.toDateString(hrmDocument.getReportDateTime(), dateFormat);
							%>
							<td class="flexV">
								<div class="hrm itemGroup flexH" title="<%=hrmDisplayName%>" id="<%=docType[3]+hrmDocument.getId()%>">
									<div  class="flexH">
										<input class="tightCheckbox1" type="checkbox"
										       name="hrmId" id="hrmId<%=hrmDocument.getId()%>"
										       value="<%=hrmDocument.getId()%>"/>
										<div class="hiddenLabel hrm"><%=hrmDisplayName%></div>
										<img title="<%= printTitle %>" src="<%= printImage %>" alt="<%= printAlt %>">
										<a class="preview-link-name" href="#" onclick="Oscar.AttachConsultation.previewHTML('<%=url%>');">
											<span class="text"><%=hrmDisplayName%></span>
										</a>
									</div>
									<div class="flexH">
										<a class="preview-link-date" href="#" onclick="Oscar.AttachConsultation.previewHTML('<%=url%>');">
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
			       onclick="return Oscar.AttachConsultation.save('<bean:message key="oscarEncounter.oscarConsultationRequest.AttachDoc.Empty"/>');" />
		</div>
		<div class="flexV flexGrow">
			<h2 style="text-align: center"><bean:message
					key="oscarEncounter.oscarConsultationRequest.AttachDocPopup.preview" /></h2>
            <div id="previewPane" class="flexV flexGrow">
            </div>
		</div>
	</div>
</html:form>
</body>
</html:html>
