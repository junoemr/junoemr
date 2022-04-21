<%--

    Copyright (c) 2008-2012 Indivica Inc.

    This software is made available under the terms of the
    GNU General Public License, Version 2, 1991 (GPLv2).
    License details are available via "indivica.ca/gplv2"
    and "gnu.org/licenses/gpl-2.0.html".

--%>
<%@page import="org.oscarehr.util.LoggedInInfo"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="java.text.SimpleDateFormat" %>
<%@ page language="java" contentType="text/html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/oscar-tag.tld" prefix="oscar"%>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>

<%
    SecurityInfoManager securityService = SpringUtils.getBean(SecurityInfoManager.class);
	HRMDocumentService hrmDocumentService = SpringUtils.getBean(HRMDocumentService.class);
	HRMDocumentToDemographicDao hrmDocumentToDemographicDao = SpringUtils.getBean(HRMDocumentToDemographicDao.class);
	HRMDocumentToProviderDao hrmDocumentToProviderDao = SpringUtils.getBean(HRMDocumentToProviderDao.class);
	HRMDocumentCommentDao hrmDocumentCommentDao = SpringUtils.getBean(HRMDocumentCommentDao.class);
	HRMProviderConfidentialityStatementDao hrmProviderConfidentialityStatementDao = SpringUtils.getBean(HRMProviderConfidentialityStatementDao.class);
	ProviderDao providerDao = SpringUtils.getBean(ProviderDao.class);
	DemographicDao demographicDao = (DemographicDao) SpringUtils.getBean("demographic.dao.DemographicDao");

    LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
    String providerNo = loggedInInfo.getLoggedInProviderNo();
	String documentId = request.getParameter("id");
	String remoteIp = request.getRemoteAddr();

	boolean previewMode = request.getParameter("preview") != null && request.getParameter("preview").equals("true");

	if (!securityService.hasPrivileges(providerNo, Permission.HRM_READ))
	{
		LogAction.addLogEntry(providerNo, LogConst.ACTION_READ, LogConst.CON_HRM, LogConst.STATUS_FAILURE, documentId, remoteIp);
		response.sendRedirect("../securityError.jsp?type=HRM_READ");
		return;
	}

	if (documentId == null) {
%>
		<h1>HRM report not found! Please check the report ID.</h1>
<%
		return;
	}

	HrmDocument hrmDocument = hrmDocumentService.getHrmDocument(Integer.parseInt(documentId));
	if (hrmDocument == null) {
%>
	<h1>HRM report not found! Please check the report ID.</h1>
<%
		return;
	}

	List<HRMDocumentToDemographic> demoLinks = hrmDocumentToDemographicDao.findByHrmDocumentId(hrmDocument.getId());
	HRMDocumentToDemographic demographicLink = (demoLinks != null && demoLinks.size() > 0 ? demoLinks.get(0) : null);

	Integer demoNo = null;
	if (demographicLink != null)
	{
		demoNo = demographicLink.getDemographicNo();
		if (!securityService.hasPrivileges(providerNo, demoNo))
		{
			LogAction.addLogEntry(providerNo, demoNo, LogConst.ACTION_READ, LogConst.CON_HRM, LogConst.STATUS_FAILURE, documentId, remoteIp);
			// Load both values into type query key
			String demoQueryComponent = URLEncoder.encode("demographic " + demoNo);
			response.sendRedirect("../securityError.jsp?type=HRM_READ," + demoQueryComponent);
			return;
		}
	}


	HRMReport hrmReport = HRMReportParser.parseReport(hrmDocument.getReportFile().getPath(), hrmDocument.getReportFileSchemaVersion());
	if (hrmReport == null) {
		LogAction.addLogEntry(loggedInInfo.getLoggedInProviderNo(), demoNo, LogConst.ACTION_READ, LogConst.CON_HRM, LogConst.STATUS_FAILURE, documentId, remoteIp);

%>
	<h1>HRM report has invalid format, please contact support.</h1>
<%
		return;
	}

	HRMDocumentToProvider linkForThisProvider = hrmDocumentToProviderDao.findByHrmDocumentIdAndProviderNo(hrmDocument.getId(), providerNo);
	if (linkForThisProvider != null && !linkForThisProvider.isViewed())
	{
		linkForThisProvider.setViewed(true);
		hrmDocumentToProviderDao.merge(linkForThisProvider);
	}
	LogAction.addLogEntry(loggedInInfo.getLoggedInProviderNo(), demoNo, LogConst.ACTION_READ, LogConst.CON_HRM, LogConst.STATUS_SUCCESS, documentId, remoteIp);

	List<HRMDocumentToProvider> providerLinkList = hrmDocumentToProviderDao.findByHrmDocumentIdNoSystemUser(hrmDocument.getId());
	List<HRMDocumentComment> documentComments = hrmDocumentCommentDao.getCommentsForDocument(Integer.parseInt(documentId));

	String confidentialityStatement = hrmProviderConfidentialityStatementDao.getConfidentialityStatementForProvider(loggedInInfo.getLoggedInProviderNo());
	Integer duplicateCount = hrmDocument.getMatchingData().getNumDuplicatesReceived();

    // These are all elements which will be flagged with an attention class if they are missing
    String lastName = hrmReport.getLegalLastName();
    String firstName = hrmReport.getLegalFirstName();
    String HCN = hrmReport.getHCN();
    String HCNVersion = hrmReport.getHCNVersion();

    String HCNProvince = hrmReport.getHCNProvinceCode();
    if (ConversionUtils.hasContent(HCNProvince))
    {
        HCNProvince.replaceAll("\\w{2}-", "");
    }

    String gender = hrmReport.getGender();
    String dateOfBirth = hrmReport.getDateOfBirth().map(ConversionUtils::toDateString).orElse("");

    String address1 = hrmReport.getAddressLine1();
    String address2 = hrmReport.getAddressLine2();
    String city = hrmReport.getAddressCity();

    String postalCode = hrmReport.getPostalCode();
    String province = hrmReport.getCountrySubDivisionCode().replaceAll("\\w{2}-", "");

    String deliverToLastName = hrmReport.getDeliverToUserLastName();
    String deliverToFirstName = hrmReport.getDeliverToUserFirstName();
    String deliverToId = hrmReport.getDeliverToUserId();

    String sendingFacilityId = hrmReport.getSendingFacilityId();
	String facilityName = hrmDocument.getSendingFacility();
    String reportNumber = hrmReport.getSendingFacilityReportNo();

    LocalDateTime reportTime = findReportTime(hrmReport);

    // I think author is an optional field
    String author = hrmReport.getAuthorPhysician();
%>

<%@page import="java.util.List, org.oscarehr.util.SpringUtils, org.oscarehr.PMmodule.dao.ProviderDao, java.util.Date" %>
<%@ page import="org.oscarehr.dataMigration.model.hrm.HrmObservation" %>
<%@ page import="oscar.util.ConversionUtils" %>
<%@ page import="org.oscarehr.demographic.entity.Demographic" %>
<%@ page import="org.oscarehr.demographic.dao.DemographicDao" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="org.oscarehr.hospitalReportManager.model.HRMDocumentToDemographic" %>
<%@ page import="org.oscarehr.hospitalReportManager.model.HRMDocumentComment" %>
<%@ page import="org.oscarehr.hospitalReportManager.HRMReport" %>
<%@ page import="org.oscarehr.hospitalReportManager.model.HRMDocumentToProvider" %>
<%@ page import="org.oscarehr.managers.SecurityInfoManager" %>
<%@ page import="org.oscarehr.security.model.Permission" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="org.oscarehr.dataMigration.model.hrm.HrmDocument" %>
<%@ page import="org.oscarehr.hospitalReportManager.service.HRMCategoryService" %>
<%@ page import="org.oscarehr.dataMigration.model.hrm.HrmCategoryModel" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="org.oscarehr.hospitalReportManager.model.HRMDocument" %>
<%@ page import="org.oscarehr.hospitalReportManager.service.HRMDocumentService" %>
<%@ page import="oscar.log.LogAction" %>
<%@ page import="oscar.log.LogConst" %>
<%@ page import="org.oscarehr.hospitalReportManager.dao.HRMDocumentToDemographicDao" %>
<%@ page import="org.oscarehr.hospitalReportManager.HRMReportParser" %>
<%@ page import="org.oscarehr.hospitalReportManager.dao.HRMDocumentCommentDao" %>
<%@ page import="org.oscarehr.hospitalReportManager.model.HRMProviderConfidentialityStatement" %>
<%@ page import="org.oscarehr.hospitalReportManager.dao.HRMProviderConfidentialityStatementDao" %>
<%@ page import="org.oscarehr.hospitalReportManager.dao.HRMDocumentToProviderDao" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%!
    String getFieldClass(String fieldContent)
    {
        if (!ConversionUtils.hasContent(fieldContent))
        {
            return "attention";
        }

        return "";
    }

    String getFieldDisplayValue(String fieldContent)
    {
        if (!ConversionUtils.hasContent(fieldContent))
        {
            return "UNKNOWN";
        }

        return fieldContent;
    }

    LocalDateTime findReportTime(HRMReport report)
    {
    	LocalDateTime reportTime = null;

    	if (report.getEventTime().isPresent())
        {
        	reportTime = report.getEventTime().get();
        }
    	else if (report.getFirstAccompanyingSubClassDateTime().isPresent())
        {
        	reportTime = report.getFirstAccompanyingSubClassDateTime().get();
        }

    	return reportTime;
    }

    String getFieldDisplayClass(LocalDateTime fieldContent)
    {
    	if (fieldContent == null)
        {
        	return "attention";
        }

    	return "";
    }

    String getFieldDisplayValue(LocalDateTime fieldContent)
    {
        if (fieldContent == null)
        {
            return "UNKNOWN";
        }

        return ConversionUtils.toDateTimeString(fieldContent);
    }
%>
<html>
<head>
    <title>HRM Report</title>
    <script type="text/javascript" src="../share/javascript/jquery/jquery-2.2.4.min.js"></script>
    <script type="text/javascript" src="../share/javascript/jquery/jquery-ui-1.12.0.min.js"></script>

    <script type="text/javascript">

		initProviderAutoComplete = function initProviderAutoComplete() {
			var providerLookup = {}; // closure to share data between autocomplete and hidden input

			$("#provider-search").autocomplete({
				minLength: 2,
				source: function (request, response)
				{
					$.ajax({
						url: "../provider/SearchProvider.do",
						dataType: "json",
						data: {
							query: request.term
						},
						success: function (data)
						{
							var formatted = [];
							data.results.forEach(function(result)
							{
								var nameString = result.lastName + ", " + result.firstName;
								formatted.push(nameString);
								providerLookup[nameString] = result.providerNo;
							});
							response(formatted);
						},
					})
				},
				select: function (event,data)
				{
					$('#provider-no').val(providerLookup[data.item.label]);
				}
			})
		}

		initDemographicAutoComplete = function initDemographicAutoComplete()
		{
			var demoLookup = {}; // closure to share data between autocomplete and hidden input

			$("#demographic-search").autocomplete({
				minLength: 2,
				source: function (request, response)
				{
					$.ajax({
						url: "../demographic/SearchDemographic.do",
						dataType: "json",
						data: {
							name: request.term
						},
						success: function (data)
						{
							var results = []
							data.results.forEach(function(result)
							{
								results.push(result.formattedName);
								demoLookup[result.formattedName] = result.demographicNo;
							})
							response(results);
						}
					});
				},
				select: function (event, data)
				{
					$('#demographic-no').val(demoLookup[data.item.label])
				}
			});
		}

		initCategorizeButtons = function initCategorizeButtons()
		{
			var uncategorized = "-1";
			var categoryId = $("#category-select").val();

			if (categoryId === uncategorized)
			{
				$('#recategorize-future').attr('disabled' , true);
			}

			$("#category-select").change(function ()
			{
				var categoryId = $("#category-select").val();
				if (categoryId === uncategorized)
				{
					$('#recategorize-future').attr('disabled' , true);
				}
				else
				{
					$('#recategorize-future').attr('disabled' , false);
				}
			})
		}

		reclassifyReport = function(documentId)
        {
            var categoryId = $("#category-select").val();
            $.ajax({
                url: "<%=request.getContextPath() %>/hospitalReportManager/Modify.do",
                type: "POST",
                data: {
                    documentId: documentId,
                    categoryId: categoryId,
                    method: "recategorize",
                },
                success: function (data) {
					window.location.reload();
				},
                error: function(err)
				{
					console.error(err)
				}
            });
        }

        reclassifyFutureReports = function(documentId)
        {
            var categoryId = $("#category-select").val();
            var categoryText =  $("#category-select option:selected").text()
            $.ajax({
                url: "<%=request.getContextPath() %>/hospitalReportManager/Modify.do",
                type: "POST",
                data: {
                    documentId: documentId,
                    categoryId: categoryId,
                    method: "recategorizeFuture"
                },
                success: function (data) {
                	alert("Success, future reports like this will be classified as " + categoryText);
					window.location.reload();
				},
                error: function (err) {console.error(err)}
            })
        }

		jQuery(document).ready(function() {
			initCategorizeButtons();
			initProviderAutoComplete();

			var demographicSearch = $("#demographic-search");
			if (demographicSearch.length)
			{
				initDemographicAutoComplete();
			}
		});

    </script>

    <link rel="stylesheet" href="../js/jquery_css/smoothness/jquery-ui-1.7.3.custom.css" type="text/css" />

    <style type="text/css">
		#hrmReport {
			font-family: "Helvetica", "Arial", sans-serif;
			font-size: 10px;
			background-color: #FFFFFF;
		}

		#hrmReport input[type="text"], select {
			appearance: none;
			border: 1px solid #70778C;
			border-radius: 2px;
			width: 336px;
			box-sizing: border-box;
			height: 24px;
			font-size: 12px;
			font: unset;
			background: #FFFFFF;
			padding-left: 6px;
			margin-right: 4px;
		}

		.input-button {
			border: 1px solid #455899;
			border-radius: 4px;
			background-color: #FFFFFF;
			color: #455899;
			font-size: 12px;
			line-height: 12px;
			height: 24px;
			width: 102px;
			margin-right: 4px;
		}

		.input-button.danger {
			border-color: #CC2929;
			color: #CC2929;
		}

		.input-button:disabled {
			border-color: #70778C;
			color: #70778C;
			cursor: not-allowed;
		}

		#hrmReportContent {
            position: relative;
            padding: 24px;
            margin: 24px auto;
            border: 2px solid #D0D5E3;
			border-radius: 4px;
            width: 90%;
			line-height: 14px;
        }

		.container {
			border: 2px solid #D0D5E3;
			border-radius: 8px;
			margin-top: 16px;
			color: #45474D;
		}

		.container .label {
			font-weight: 700;
			color: #737780;
			width: 196px;
			margin-left: 4px;
			display: inline-block;
			text-align: left;
		}

		.infoBox {
            overflow: hidden;
			border-width: 1px;
            padding: 16px;
			margin-top: 16px;
			background-color: #F2F4F7;
			line-height: 12px;
			color: #45474D;
        }

		.infoBox table {
            margin: 8px 0;
			font-size: 10px;
			font-style: normal;
        }

		.infoBox th {
            text-align: left;
			color: #45474D;
			font-weight: bold;
        }

		.infoBox td {
			padding: 2px 0;
		}

		.infoBox .hrm-info-header {
			width: 72px;
			color: #737780;
			font-weight: normal;
		}

		.infoBox a {
			margin-left: 4px;
		}

		.infoBox input {
			box-sizing: border-box;
			margin-right: 2px;
		}

		.infoBox hr {
			border: 1px solid #D0D5E3;
			border-bottom: none;
			height: 0;
		}

		#hrmReport .add-comments {
			margin: 8px;
		}

		.add-comments > .label {
			display: block;
		}

		.add-comments > textarea {
            margin: 4px 0 8px 0;
			height: 128px;
			width: 360px;
			display: block;
        }

		.comments-list {
			margin-top: 16px;
		}

		.comments-list > .hrm-info-header {
			margin-bottom: 8px;
			background: unset;
		}

		.comment {
			border-top: 1px solid #D0D5E3;
			display: block;
		}

		.comment > .author {
			margin: 2px 0;
			font-weight: 700;
		}

		.comment > .message {
			display: inherit;
		}

		.comment > .delete-message
		{
			display: block;
			margin-bottom: 4px;
		}

		#hrmHeader {
            display: none;
        }

		#hrmNotice {
            border-bottom: 1px solid #D0D5E3;
            padding-bottom: 16px;
            font-style: normal;
			white-space: pre-line;
			font-weight: 700;
        }

		.attention {
            background-color: #fff7cc;
            color: #cc2929;
            font-weight: bold;
        }

		.hrm-content {
            width: 70%;
            float: left;
            white-space: pre-wrap;
            margin-right: 24px;
			font-size: 12px;
            overflow-x: scroll;
        }

		.description-container .label {
            width: 30%;
            display: inline-block;
        }

		.description-container .input {
            width: 30%;
        }

		.description-container select {
            width: 30%;
        }

		.hide-on-preview {
			display: none;
		}

        @media print {
            .hide-on-print {
                display: none;
            }

            .hrm-content {
                width: 100%;
                background-color:red;
            }

            #hrmHeader {
                display: block;
            }
        }
    </style>

    <script type="text/javascript">
		function makeIndependent(reportId) {
			jQuery.ajax({
				type: "POST",
				url: "<%=request.getContextPath() %>/hospitalReportManager/Modify.do",
				data: "method=makeIndependent&reportId=" + reportId,
				success: function(data) {
				}
			});
		}

		function addDemoToHrm(reportId) {
			var demographicNo = $("#demographic-no").val();
			jQuery.ajax({
				type: "POST",
				url: "<%=request.getContextPath() %>/hospitalReportManager/Modify.do",
				data: "method=assignDemographic&reportId=" + reportId + "&demographicNo=" + demographicNo,
				success: function(data) {
					if (data != null) {
						$("demostatus" + reportId).innerHTML = data;
						toggleButtonBar(true,reportId);
					}

					window.location.reload();
				}
			});
		}

		function removeDemoFromHrm(reportId) {
			jQuery.ajax({
				type: "POST",
				url: "<%=request.getContextPath() %>/hospitalReportManager/Modify.do",
				data: "method=removeDemographic&reportId=" + reportId,
				success: function(data) {
					if (data != null) {
						$("demostatus" + reportId).innerHTML = data;
						toggleButtonBar(false,reportId);
					}

					window.location.reload();
				}
			});
		}

		function toggleButtonBar(show, reportId) {
			jQuery("#msgBtn_"+reportId).prop('disabled',!show);
			jQuery("#mainTickler_"+reportId).prop('disabled',!show);
			jQuery("#mainEchart_"+reportId).prop('disabled',!show);
			jQuery("#mainMaster_"+reportId).prop('disabled',!show);
			jQuery("#mainApptHistory_"+reportId).prop('disabled',!show);

		}

		function addProvToHrm(reportId, providerNo) {
			jQuery.ajax({
				type: "POST",
				url: "<%=request.getContextPath() %>/hospitalReportManager/Modify.do",
				data: "method=assignProvider&reportId=" + reportId + "&providerNo=" + providerNo,
				success: function(data) {
					if (data.trim())
					{
						alert("Successfully assigned provider");
						window.location.reload();
					}
					else
					{
						alert("Could not assign provider to report");
					}
				},
				error: function(err) {
					alert("Could not assign provider to report");
				},
			});
		}

		function removeProvFromHrm(mappingId, reportId) {
			jQuery.ajax({
				type: "POST",
				url: "<%=request.getContextPath() %>/hospitalReportManager/Modify.do",
				data: "method=removeProvider&providerMappingId=" + mappingId,
				success: function(data) {
					alert("Provider removed from report");
					window.location.reload();
				}
			});
		}

		function makeActiveSubClass(reportId, subClassId) {
			jQuery.ajax({
				type: "POST",
				url: "<%=request.getContextPath() %>/hospitalReportManager/Modify.do",
				data: "method=makeActiveSubClass&reportId=" + reportId + "&subClassId=" + subClassId,
				success: function(data) {
					if (data != null)
					{
						$("subclassstatus" + reportId).innerHTML = data;
					}
					window.location.reload();
				}
			});

			window.location.reload();
		}

		function addComment(reportId) {
			var comment = jQuery("#commentField_" + reportId + "_hrm").val();
			jQuery.ajax({
				type: "POST",
				url: "<%=request.getContextPath() %>/hospitalReportManager/Modify.do",
				data: "method=addComment&reportId=" + reportId + "&comment=" + comment,
				success: function(data) {
					if (data != null)
					{
						$("commentstatus" + reportId).innerHTML = data;
					}
					window.location.reload();
				}
			});
		}

		function deleteComment(commentId, reportId) {
			jQuery.ajax({
				type: "POST",
				url: "<%=request.getContextPath() %>/hospitalReportManager/Modify.do",
				data: "method=deleteComment&commentId=" + commentId,
				success: function(data) {
					if (data != null)
					{
						$("commentstatus" + reportId).innerHTML = data;
					}
					window.location.reload();
				}
			});
		}


		function doSignOff(reportId, isSign) {
			var data;
			if (isSign)
				data = "method=signOff&signedOff=1&reportId=" + reportId;
			else
				data = "method=signOff&signedOff=0&reportId=" + reportId;

			jQuery.ajax({
				type: "POST",
				url: "<%=request.getContextPath() %>/hospitalReportManager/Modify.do",
				data: data,
				success: function(data) {
					window.location.reload();
				}
			});
		}

		function signOffHrm(reportId) {

			doSignOff(reportId, true);
		}

		function revokeSignOffHrm(reportId) {
			doSignOff(reportId, false);
		}

		function setDescription(reportId) {
			var comment = jQuery("#descriptionField_" + reportId + "_hrm").val();

			if (!comment.trim())
			{
				alert("Description cannot be empty");
				return;
			}

			jQuery.ajax({
				type: "POST",
				url: "<%=request.getContextPath() %>/hospitalReportManager/Modify.do",
				data: "method=setDescription&reportId=" + reportId + "&description=" + comment,
				success: function(data) {
					if (data != null)
					{
						$("descriptionstatus" + reportId).innerHTML = data;
					}
					window.location.reload();
				}
			});
		}

		function popupPatient(height, width, url, windowName, docId, d) {
			urlNew = url + d;
			return popup2(height, width, 0, 0, urlNew, windowName);
		}

		function popupPatientTickler(height, width, url, windowName,docId,d,n) {
			urlNew = url + "method=edit&tickler.demographic_webName=" + n + "&tickler.demographicNo=" +  d + "&docType=DOC&docId="+docId;
			return popup2(height, width, 0, 0, urlNew, windowName);
		}
    </script>
</head>
<body id="hrmReport">
<%
    String btnDisabled = "disabled";
    String demographicNo = "";
    if(demographicLink != null) {
        btnDisabled="";
        demographicNo = String.valueOf(demographicLink.getDemographicNo());
    }
    String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
%>
<div id="hrmReportContent">
    <div id="hrmHeader"><b>HRM Patient Record</b><br/>
        <b>Name: </b><span class="<%=getFieldClass(lastName)%>"><%=getFieldDisplayValue(lastName)%></span>, <span class="<%=getFieldClass(firstName)%>"><%=getFieldDisplayValue(firstName)%></span> <span class="<%=getFieldClass(gender)%>">(<%=getFieldDisplayValue(gender)%>)</span><br/>
        <b>DOB: </b><span class="<%=getFieldClass(dateOfBirth)%>"><%=getFieldDisplayValue(dateOfBirth)%></span><br>
        <b>HCN: </b><span class="<%=getFieldClass(HCN)%>"><%=getFieldDisplayValue(HCN)%></span> <span class="<%=getFieldClass(HCNVersion)%>"><%=getFieldDisplayValue(HCNVersion)%></span><br/>
    </div>
    <div id="hrmNotice" class="<%= previewMode ? "hide-on-preview" : ""%>">
        This report was received from the Hospital Report Manager (HRM) at <%= ConversionUtils.toDateTimeString(hrmDocument.getReceivedDateTime()) %>.

	Message Unique ID: <%=hrmReport.getMessageUniqueId()%>
	<% if (duplicateCount != null && duplicateCount > 0) { %>Juno has received <%=duplicateCount%> duplicates of this report.<% } %>
    </div>
    <div class="hrm-container">
        <%-- Document content --%>
        <div class="hrm-content">
            <% if(hrmReport.isBinary()) {
                List<String> imageFormats = Arrays.asList(".gif", ".jpg", ".jpeg", ".png", ".jpeg");    // *.tiff is not supported on modern browsers

                if (hrmReport.getFileExtension() != null && imageFormats.contains(hrmReport.getFileExtension())) {
            %>
            <img src="<%=request.getContextPath() %>/hospitalReportManager/HRMDownloadFile.do?id=<%=documentId%>"/><br/>
            <% } else { %>
            <div style="display: inline-block; margin:auto; color:red; white-space: pre-line">
                This report contains an attachment which cannot be viewed in your browser.
                Please use the link to view/download the content contained within.
            </div>
            <% } %>
            <a href="<%=request.getContextPath() %>/hospitalReportManager/HRMDownloadFile.do?id=<%=documentId%>"><%=(hrmReport.getLegalLastName() + "_" + hrmReport.getLegalFirstName() + "_" +  hrmReport.getClassName() + hrmReport.getFileExtension()).replaceAll("\\s", "_") %></a>
            <% } else { %>
            <div class="<%=getFieldClass(hrmReport.getTextContent())%>"><%=ConversionUtils.hasContent(hrmReport.getTextContent()) ? hrmReport.getTextContent() : "NO CONTENT"%></div>
            <% } %>
            <%
                if (confidentialityStatement != null && confidentialityStatement.trim().length() > 0) {
            %>
            <hr/>
            <em><strong>Provider Confidentiality Statement</strong><br /><<%=confidentialityStatement%></em>
            <% } %>
        </div>
        <%-- Right side infobox --%>
        <div class="infoBox container hide-on-print">
            <table>
                <tr>
                    <th colspan="2"><%=hrmDocument.getDescription()%></th>
                </tr>
                <tr></tr>
                <tr>
                    <td class="hrm-info-header">Date:</td>
                    <td><span class="<%=getFieldDisplayClass(reportTime)%>"><%=getFieldDisplayValue(reportTime)%></span></td>
                </tr>
                <%if (author != null) {%>
                <tr>
                    <td class="hrm-info-header">Author:</td>
                    <td><%=author%></td>
                </tr>
                <% } %>
                <tr>
                    <td class="hrm-info-header">Facility:</td>
                    <td><span class="<%=getFieldClass(facilityName)%>"><%=getFieldDisplayValue(facilityName)%></span> <span class="<%=getFieldClass(sendingFacilityId)%>">(<%=getFieldDisplayValue(sendingFacilityId)%>)</span></td>
                </tr>
                <tr>
                    <td class="hrm-info-header">Status:</td>
                    <% if (hrmDocument.getReportStatus().equals(HrmDocument.ReportStatus.SIGNED)) { %>
                    <td>Signed by author</td>
                    <% } else if (hrmDocument.getReportStatus().equals(HrmDocument.ReportStatus.CANCELLED)) { %>
                    <td class="attention">Cancelled</td>
                    <% } else { %>
                    <td>Unsigned / Unknown</td>
                    <% } %>
                </tr>
                <tr>
                    <td class="hrm-info-header">Category:</td>
                    <td><%= hrmDocument.getCategory() != null ? hrmDocument.getCategory().getName() : "Unmatched to category" %></td>
                </tr>
            </table>
            <hr>
            <table>
                <tr>
                    <th colspan="2">Embedded HRM Patient Record</th>
                </tr>
                <tr>
                    <td class="hrm-info-header" style="min-width: 64px">Name:</td>
                    <td><span class="<%=getFieldClass(lastName)%>"><%=getFieldDisplayValue(lastName)%></span>, <span class="<%=getFieldClass(firstName)%>"><%=getFieldDisplayValue(firstName)%></span> <span class="<%=getFieldClass(gender)%>">(<%=getFieldDisplayValue(gender)%>)</span></td>
                </tr>
                <tr>
                    <td class="hrm-info-header">HCN:</td>
                    <td><span class="<%=getFieldClass(HCN)%>"><%=getFieldDisplayValue(HCN)%></span> <span class="<%=getFieldClass(HCNVersion)%>"><%=getFieldDisplayValue(HCNVersion)%></span> <span class="<%=getFieldClass(HCNProvince)%>"><%=getFieldDisplayValue(HCNProvince)%></span></td>
                </tr>
                <tr>
                    <td class="hrm-info-header">DOB:</td>
                    <td><span class="<%=getFieldClass(dateOfBirth)%>"><%=getFieldDisplayValue(dateOfBirth)%></span></td>
                </tr>
                <tr>
                    <td class="hrm-info-header">Address:</td>
                    <td><span class="<%=getFieldClass(address1)%>"><%=getFieldDisplayValue(address1)%></span></td>
                </tr>
                <% if (ConversionUtils.hasContent(address2)) { %>
                <tr>
                    <td></td>
                    <td><span class="<%=getFieldClass(address2)%>"><%=getFieldDisplayValue(address2)%></span></td>
                </tr>
                <% } %>
                <tr>
                    <td></td>
                    <td><span class="<%=getFieldClass(city)%>"><%=getFieldDisplayValue(city)%></span>, <span class="<%=getFieldClass(province)%>"><%=getFieldDisplayValue(province)%></span>, <span class="<%=getFieldClass(postalCode)%>"><%=getFieldDisplayValue(postalCode)%></span></td>
                </tr>
            </table>
            <table>
                <tr>
                    <th>Assigned Demographic</th>
                </tr>
                <% if (demographicLink != null) {
                    Demographic demographic = demographicDao.find(demographicLink.getDemographicNo());
                %>
                <tr>
                    <td><%=demographic.getFormattedName()%> (<%=demographic.getSex()%>) <a class="<%= previewMode ? "hide-on-preview" : ""%>" href="#" onclick="removeDemoFromHrm('<%=hrmDocument.getId()%>')">(remove)</a></td>
                </tr>
                <tr>
                    <td><%=demographic.getHin()%> <%=demographic.getVer()%> <%=demographic.getHcType()%></td>
                </tr>
                <tr>
                    <td><%=demographic.getDateOfBirth()%></td>
                </tr>
                <% } else { %>
                <tr>
                    <td><i class="attention">Not currently linked</i></td>
                </tr>
                <tr class="<%= previewMode ? "hide-on-preview" : ""%>">
                    <td>Search Demographics by Name</td>
                </tr>
                <tr class="<%= previewMode ? "hide-on-preview" : ""%>">
                    <td>
                        <input type="hidden" id="demographic-no" name="demographicNo">
                        <input type="text" autocomplete="off" id="demographic-search"<%--onchange="checkSave('<%=hrmReportId%>hrm')"--%>>
                    </td>
                    <td>
                        <a href="#" onclick="addDemoToHrm('<%=hrmDocument.getId()%>')">(link)</a>
                    </td>
                </tr>
                <% } %>
            </table>
            <hr>
            <table>
                <tr>
                    <th colspan="2">Embedded HRM Recipient</th>
                </tr>
                <% if (hrmReport.getDeliverToUserId() == null) {%>
                <tr>
                    <td colspan="2" class="attention">NO RECIPIENTS FOUND</td>
                </tr>
                <% } else { %>
                <tr>
                    <td class="hrm-info-header">Name:</td>
                    <td><span class="<%=getFieldClass(deliverToLastName)%>"><%=getFieldDisplayValue(deliverToLastName)%></span>, <span class="<%=getFieldClass(deliverToFirstName)%>"><%=getFieldDisplayValue(deliverToFirstName)%></span></td>
                </tr>
                <tr>
                    <td class="hrm-info-header"><%=deliverToId.startsWith("N") ? "CNO:" : "CPSID:"%></td>
                    <td><span class="<%=getFieldClass(deliverToId)%>"><%=getFieldDisplayValue(deliverToId)%></span></td>
                </tr>
                <% } %>
            </table>
            <table>
                <tr>
                    <th>Assigned Providers</th>
                </tr>
                <% if (providerLinkList == null || providerLinkList.isEmpty()) { %>
                <tr>
                    <td>No providers currently assigned</td>
                </tr>
                <% } else { %>
                <% for (HRMDocumentToProvider providerLink : providerLinkList) {
                    String providerName = providerDao.getProviderName(providerLink.getProviderNo());
                %>
                <tr>
                    <td><%=providerName%></td>
                    <td><%= providerLink.isSignedOff() ? "(Signed-off " + providerLink.getSignedOffTimestamp() + ")" : "" %></td>
                    <td class="<%= previewMode ? "hide-on-preview" : ""%>"><a href="#" onclick="removeProvFromHrm('<%=providerLink.getId() %>', '<%=hrmDocument.getId()%>')">(remove)</a></td>
                </tr>
                <% } %>
                <% } %>
            </table>
            <table class="<%= previewMode ? "hide-on-preview" : ""%>">
                <tr><th>Search Providers by Name</th></tr>
                <tr>
                    <td>
                        <div id="providerList<%=hrmDocument.getId()%>hrm"></div>
                        <input type="hidden" name="providerNo" id="provider-no"/>
                        <input type="text" style="width: 100%" id="provider-search" autocomplete="off" name="providerKeyword"/>
                    </td>
                    <td><a href="#" onclick="addProvToHrm(<%=hrmDocument.getId()%>, $('#provider-no').val())">(assign)</a></td>
                </tr>
            </table>
            <hr>
            <table>
                <tr><th colspan="2">Embedded Report Information</th></tr>
                <tr>
                    <td class="hrm-info-header">Report Class:</td>
                    <td><%=hrmReport.getClassName()%></td>
                </tr>
                <% if (hrmReport.getClassName().equals(HrmDocument.ReportClass.DIAGNOSTIC_IMAGING.getValue()) ||
							   hrmReport.getClassName().equals(HrmDocument.ReportClass.CARDIO_RESPIRATORY.getValue())) { %>
                    <%
                        List<HrmObservation> hrmObservations = hrmReport.getObservations();
                    %>
                    <tr>
                        <td class="hrm-info-header" colspan="2">Accompanying SubClasses:</td>
                    </tr>
                    <% for (HrmObservation observation: hrmObservations) { %>
                        <%
                            String mnemonic = observation.getAccompanyingMnemonic();
                            String subclass = observation.getAccompanyingSubClass();
                            String description = observation.getAccompanyingDescription();
                            String observationDate = ConversionUtils.toDateString(observation.getObservationDateTime());
                        %>
                        <tr>
                            <td><span class="<%=getFieldClass(mnemonic)%>">(<%=getFieldDisplayValue(mnemonic)%>)</span> <span class="<%=getFieldClass(subclass)%>"><%=getFieldDisplayValue(subclass)%></span> <span class="<%=getFieldClass(description)%>"><%=getFieldDisplayValue(description)%></span></td>
                            <td class="<%=getFieldClass(observationDate)%>"><%=getFieldDisplayValue(observationDate)%></td>
                        </tr>
                    <% } %>
                <% } else {
                    if (!hrmReport.getSubClassName().isEmpty()) {
                        String[] subClassFromReport = hrmReport.getSubClassName().split("\\^");
                        String subClassDisplay = "";
                        if (subClassFromReport.length == 1)
                        {
                            subClassDisplay = subClassFromReport[0];  // subclass was not sent with a short code
                        }
                        else if (subClassFromReport.length == 2)
                        {
                            subClassDisplay = "(" + subClassFromReport[0] + ") " + subClassFromReport[1];
                        }
                %>
                    <tr>
                        <td class="hrm-info-header">Subclass:</td>
                        <td><%=subClassDisplay%></td>
                    </tr>
                    <% } %>
                <% } %>
                <tr>
                    <td class="hrm-info-header">Report No:</td>
                    <td><span class="<%=getFieldClass(reportNumber)%>"><%=getFieldDisplayValue(reportNumber)%></span></td>
                </tr>
            </table>
            <table class="<%= previewMode ? "hide-on-preview" : ""%>">
                <tr>
                    <td>
                        <input type="button" class="input-button" style="display: none" value="Save" id="save<%=documentId%>hrm" />
                    </td>
                    <td>
                        <input type="button" class="input-button" value="Print" onClick="window.print()" />
                    </td>
                    <td>
                        <%
                            if (linkForThisProvider != null && linkForThisProvider.isSignedOff()) {
                        %>
                        <input type="button" class="input-button danger" id="signoff<%=documentId%>" value="Revoke Sign-Off" onClick="revokeSignOffHrm('<%=documentId%>')"/>
                        <%
                        } else {
                        %>
                        <input type="button" class="input-button" id="signoff<%=documentId%>" value="Sign-Off" onClick="signOffHrm('<%=documentId%>')"/>
                        <%
                            }
                        %>
                    </td>
                </tr>
            </table>
        </div>
    </div>
    <div class="container hrm-action-container hide-on-print <%= previewMode ? "hide-on-preview" : ""%>">
        <div style="padding: 8px 8px 4px 8px">
			<div class="label">Change Report Description:</div>
            <input class="input" type="text" id="descriptionField_<%=hrmDocument.getId()%>_hrm" value="<%=StringEscapeUtils.escapeHtml(hrmDocument.getDescription())%>"/>
			<input class="input-button" type="button" onClick="setDescription('<%=hrmDocument.getId()%>')" value="Save"/><span id="descriptionstatus<%=hrmDocument.getId()%>"></span>
        </div>
		<div style="padding: 0 8px 8px 8px">
			<div class="label">Change Report Category:</div>
            <select class="input" id="category-select">
				<option value="-1">Uncategorized</option>
                <%
                    HRMCategoryService categoryService = SpringUtils.getBean(HRMCategoryService.class);
                    List<HrmCategoryModel> categories = categoryService.getActiveCategories();

                    if (hrmDocument.getCategory() != null)
                    {
                        categories.remove(hrmDocument.getCategory());
                    }

                %>
                <% for (HrmCategoryModel category : categories) { %>
				<% boolean isSelected = hrmDocument.getCategory() != null && hrmDocument.getCategory().getId().equals(category.getId()); %>
                <option value="<%=category.getId()%>" <%= isSelected ? "selected" : ""%>><%=category.getName()%></option>
                <% } %>
            </select>
            <input class="input-button" type="button" onClick="reclassifyReport('<%=hrmDocument.getId()%>')" value="This Report"/>
            <input class="input-button" id="recategorize-future" type="button" onClick="reclassifyFutureReports('<%=hrmDocument.getId()%>')" value="Future Reports"/>
        </div>
    </div>
    <div class="container hrm-action-container hide-on-print <%= previewMode ? "hide-on-preview" : ""%>">
        <div class="add-comments">
            <div class="label">Add a comment to this report:</div>
            <textarea rows="10" cols="50" id="commentField_<%=hrmDocument.getId()%>_hrm"></textarea>
            <input class="input-button" type="button" onClick="addComment('<%=hrmDocument.getId()%>')" value="Add Comment" /><span id="commentstatus<%=hrmDocument.getId()%>"></span>
		<div class="comments-list">
			<%
				if (documentComments != null) {
			%>
			<div class="hrm-info-header">Displaying <%=documentComments.size() %> comment<%=documentComments.size() != 1 ? "s:" : ":" %></div>
			<% for (HRMDocumentComment comment : documentComments) { %>
			<div class="comment">
				<span class="author"><%=providerDao.getProviderName(comment.getProvider().getId()) %> on <%=comment.getCommentTime().toString() %> wrote...</span>
				<span class="message"><%=comment.getComment()%></span>
				<a class="delete-message" href="#" onClick="deleteComment('<%=comment.getId() %>', '<%=hrmDocument.getId()%>'); return false;">(delete comment)</a>
			</div>
			<% } %>
			<% } %>
		</div>
    </div>
</div>
</div>
</body>
</html>