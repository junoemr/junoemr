<%--

    Copyright (c) 2008-2012 Indivica Inc.

    This software is made available under the terms of the
    GNU General Public License, Version 2, 1991 (GPLv2).
    License details are available via "indivica.ca/gplv2"
    and "gnu.org/licenses/gpl-2.0.html".

--%>
<%@page import="org.oscarehr.util.LoggedInInfo"%>
<%@page import="org.apache.commons.lang.StringUtils,oscar.log.*"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="java.util.Map"%>
<%@page import="java.text.SimpleDateFormat" %>
<%@ page language="java" contentType="text/html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/oscar-tag.tld" prefix="oscar"%>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
'


<%
    SecurityInfoManager securityService = SpringUtils.getBean(SecurityInfoManager.class);

    LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
    String providerNo = loggedInInfo.getLoggedInProviderNo();

    HRMDocumentToDemographic demographicLink = (HRMDocumentToDemographic) request.getAttribute("demographicLink");
%>

<%
    if ((demographicLink == null || demographicLink.getDemographicNo() == null)
        && !securityService.hasPrivileges(providerNo, Permission.HRM_READ))
    {
        response.sendRedirect("../securityError.jsp?type=HRM_READ");
        return;
    }
    else if ((demographicLink != null && demographicLink.getDemographicNo() != null)
            && !securityService.hasPrivileges(providerNo, demographicLink.getDemographicNo(), Permission.HRM_READ))
    {
        String demoQueryComponent = URLEncoder.encode("demographic " + demographicLink.getDemographicNo());
        response.sendRedirect("../securityError.jsp?type=HRM_READ," + demoQueryComponent);
        return;
    }

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    HrmDocument hrmDocument = (HrmDocument) request.getAttribute("hrmDocument");
    HRMReport hrmReport = (HRMReport) request.getAttribute("hrmReport");
    Integer hrmReportId = (Integer) request.getAttribute("hrmReportId");

    List<HRMDocumentToProvider> providerLinkList = (List<HRMDocumentToProvider>) request.getAttribute("providerLinkList");

    ProviderDao providerDao = (ProviderDao) SpringUtils.getBean("providerDao");
    DemographicDao demographicDao = (DemographicDao) SpringUtils.getBean("demographic.dao.DemographicDao");

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
    String reportNumber = hrmReport.getSendingFacilityReportNo();

    LocalDateTime reportTime = findReportTime(hrmReport);

    // I think author is an optional field
    String author = hrmReport.getAuthorPhysician();

    String facilityName = (String) request.getAttribute("facilityName");
%>

<%@page import="java.util.LinkedList, java.util.List, org.oscarehr.util.SpringUtils, org.oscarehr.PMmodule.dao.ProviderDao, java.util.Date" %>
<%@ page import="org.oscarehr.dataMigration.model.hrm.HrmObservation" %>
<%@ page import="oscar.util.ConversionUtils" %>
<%@ page import="org.oscarehr.demographic.entity.Demographic" %>
<%@ page import="org.oscarehr.demographic.dao.DemographicDao" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="org.oscarehr.hospitalReportManager.model.HRMDocumentToDemographic" %>
<%@ page import="org.oscarehr.hospitalReportManager.model.HRMDocumentComment" %>
<%@ page import="org.oscarehr.hospitalReportManager.HRMReport" %>
<%@ page import="org.oscarehr.hospitalReportManager.model.HRMDocumentToProvider" %>
<%@ page import="org.oscarehr.hospitalReportManager.HRMDisplayReportAction" %>
<%@ page import="org.oscarehr.hospitalReportManager.model.HRMDocument" %>
<%@ page import="org.oscarehr.managers.SecurityInfoManager" %>
<%@ page import="org.oscarehr.security.model.Permission" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="org.oscarehr.dataMigration.model.hrm.HrmDocument" %>
<%@ page import="org.oscarehr.hospitalReportManager.service.HRMCategoryService" %>
<%@ page import="org.oscarehr.dataMigration.model.hrm.HrmCategoryModel" %>
<%@ page import="java.time.LocalDateTime" %>
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
                dataType: "json",
                type: "POST",
                data: {
                    documentId: documentId,
                    categoryId: categoryId,
                    method: "recategorize",
                },
                success: function (data) {},
                error: function(err){console.log(err)}
            });
        }

        reclassifyFutureReports = function(documentId)
        {
            var categoryId = $("#category-select").val();
            $.ajax({
                dataType: "json",
                url: "<%=request.getContextPath() %>/hospitalReportManager/Modify.do",
                type: "POST",
                data: {
                    documentId: documentId,
                    categoryId: categoryId,
                    method: "recategorizeFuture"
                },
                success: function (data) {},
                error: function (err) {console.log(err)}
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
        #hrmReportContent {
            position: relative;
            padding: 24px;
            margin: 24px auto;
            border: 1px solid black;
            width: 90%;
        }

        .infoBox {
            overflow: hidden;
            padding: 25px;
            margin-bottom: 24px;
            border: 1px solid black;
        }

        .infoBox table {
            margin: 8px 0;
        }

        .infoBox th {
            text-align: left;
        }

        .description-container {
            padding: 8px;
        }

        .description-container input {
            margin: 4px 0;
        }

        .comment-container {
            padding: 8px;
        }

        .comment-container textarea {
            margin: 4px 0;
        }


        #hrmHeader {
            display: none;
        }

        #hrmNotice {
            border-bottom: 1px solid black;
            padding-bottom: 15px;
            margin-bottom: 24px;
            font-style: italic;
        }

        .hrm-action-container {
            clear: both;
            border: 1px solid black;
            margin-top: 24px;
        }

        .documentComment {
            border: 1px solid black;
            margin: 10px;
        }

        .attention {
            background-color: #FFEA17;
            color: #EB0000;
            font-weight: bold;
        }

        .hrm-content {
            width: 70%;
            float: left;
            white-space: pre;
            margin-right: 24px;
            overflow-x: scroll;
        }

        .description-container .label {
            width: 30%;
            display: inline-block;
        }

        .description-container .input {
            width: 30%;
        }

        .description-container .action-button {
            width: 15%;
        }

        .description-container select {
            width: 30%;
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
					if (data.success)
					{
						alert("Successfully assigned provider");
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
						$("subclassstatus" + reportId).innerHTML = data;
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
						$("commentstatus" + reportId).innerHTML = data;
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
						$("commentstatus" + reportId).innerHTML = data;
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
			jQuery.ajax({
				type: "POST",
				url: "<%=request.getContextPath() %>/hospitalReportManager/Modify.do",
				data: "method=setDescription&reportId=" + reportId + "&description=" + comment,
				success: function(data) {
					if (data != null)
						$("descriptionstatus" + reportId).innerHTML = data;
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
<body>

<% if (hrmReport==null) { %>
<h1>HRM report not found! Please check the file location.</h1>
<%  return; } %>

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
    <br />
    <div id="hrmNotice">
        This report was received from the Hospital Report Manager (HRM) at <%= ConversionUtils.toDateString(hrmDocument.getReceivedDateTime()) %>.
        <% if (request.getAttribute("hrmDuplicateNum") != null && ((Integer) request.getAttribute("hrmDuplicateNum")) > 0) { %><br /><i>OSCAR has received <%=request.getAttribute("hrmDuplicateNum") %> duplicates of this report.</i><% } %>
        <%
            List<HRMDocument> allDocumentsWithRelationship = (List<HRMDocument>) request.getAttribute("allDocumentsWithRelationship");
            if (allDocumentsWithRelationship != null && allDocumentsWithRelationship.size() >= 1) {
        %>
        OSCAR has detected that this is similar to the following reports:
        <%
            List<Integer> seenBefore = new LinkedList<Integer>();
            for (HRMDocument relationshipDocument : allDocumentsWithRelationship) {
                if (!seenBefore.contains(relationshipDocument.getId())) { %>
        <span class="documentLink_status<%=relationshipDocument.getReportStatus() %>" title="<%=relationshipDocument.getReportDate().toString() %>">
			<% if (relationshipDocument.getId().intValue() != hrmReportId.intValue()) { %><a href="<%=request.getContextPath() %>/hospitalReportManager/Display.do?id=<%=relationshipDocument.getId() %>"><% } %>[<%=relationshipDocument.getId() %>]<% if (relationshipDocument.getId().intValue() != hrmReportId.intValue()) { %></a><% } %>
			</span>&nbsp;&nbsp;
        <% 	seenBefore.add(relationshipDocument.getId());
        }
        } %>
        <div class="boxButton">
            <input type="button" onClick="makeIndependent('<%=hrmReportId %>')" value="Make Independent" />
        </div>
        <% } %>
    </div>
    <div class="hrm-container">
        <%-- Document content --%>
        <div class="hrm-content">
            <% if(hrmReport.isBinary()) {

                Integer documentId = hrmDocument.getId();
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
                String confidentialityStatement = (String) request.getAttribute("confidentialityStatement");
                if (confidentialityStatement != null && confidentialityStatement.trim().length() > 0) {
            %>
            <hr/>
            <em><strong>Provider Confidentiality Statement</strong><br /><%--<%=confidentialityStatement %>--%> CONFIDENTIALITY STATEMENT</em>
            <% } %>
        </div>
        <%-- Right side infobox --%>
        <div class="infoBox hide-on-print">
            <table>
                <tr>
                    <th colspan="2"><%=hrmDocument.getDescription()%></th>
                </tr>
                <tr></tr>
                <tr>
                    <td>Date:</td>
                    <td><span class="<%=getFieldDisplayClass(reportTime)%>"><%=getFieldDisplayValue(reportTime)%></span></td>
                </tr>
                <%if (author != null) {%>
                <tr>
                    <td>Author:</td>
                    <td><%=author%></td>
                </tr>
                <% } %>
                <tr>
                    <td>Facility:</td>
                    <td><span class="<%=getFieldClass(facilityName)%>"><%=getFieldDisplayValue(facilityName)%></span> <span class="<%=getFieldClass(sendingFacilityId)%>">(<%=getFieldDisplayValue(sendingFacilityId)%>)</span></td>
                </tr>
                <tr>
                    <td>Status:</td>
                    <% if (hrmDocument.getReportStatus().equals(HrmDocument.ReportStatus.SIGNED)) { %>
                    <td>Signed by author</td>
                    <% } else if (hrmDocument.getReportStatus().equals(HrmDocument.ReportStatus.CANCELLED)) { %>
                    <td class="attention">Cancelled</td>
                    <% } else { %>
                    <td>Unsigned / Unknown</td>
                    <% } %>
                </tr>
                <tr>
                    <td>Category:</td>
                    <td><%= hrmDocument.getCategory() != null ? hrmDocument.getCategory().getName() : "Unmatched to category" %></td>
                </tr>
            </table>
            <hr>
            <table>
                <tr>
                    <th colspan="2">Embedded HRM Patient Record</th>
                </tr>
                <tr>
                    <td style="min-width: 64px">Name:</td>
                    <td><span class="<%=getFieldClass(lastName)%>"><%=getFieldDisplayValue(lastName)%></span>, <span class="<%=getFieldClass(firstName)%>"><%=getFieldDisplayValue(firstName)%></span> <span class="<%=getFieldClass(gender)%>">(<%=getFieldDisplayValue(gender)%>)</span></td>
                </tr>
                <tr>
                    <td>HCN:</td>
                    <td><span class="<%=getFieldClass(HCN)%>"><%=getFieldDisplayValue(HCN)%></span> <span class="<%=getFieldClass(HCNVersion)%>"><%=getFieldDisplayValue(HCNVersion)%></span> <span class="<%=getFieldClass(HCNProvince)%>"><%=getFieldDisplayValue(HCNProvince)%></span></td>
                </tr>
                <tr>
                    <td>DOB:</td>
                    <td><span class="<%=getFieldClass(dateOfBirth)%>"><%=getFieldDisplayValue(dateOfBirth)%></span></td>
                </tr>
                <tr>
                    <td>Address:</td>
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
                    <td><%=demographic.getFormattedName()%> (<%=demographic.getSex()%>)</td>
                    <td><a href="#" onclick="removeDemoFromHrm('<%=hrmReportId %>')">(remove)</a></td>
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
                <tr>
                    <td>Search Demographics by Name</td>
                </tr>
                <tr>
                    <td>
                        <input type="hidden" id="demographic-no" name="demographicNo">
                        <input type="text" autocomplete="off" id="demographic-search"<%--onchange="checkSave('<%=hrmReportId%>hrm')"--%>>
                    </td>
                    <td>
                        <a href="#" onclick="addDemoToHrm('<%=hrmReportId %>')">(link)</a>
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
                    <td>Name:</td>
                    <td><span class="<%=getFieldClass(deliverToLastName)%>"><%=getFieldDisplayValue(deliverToLastName)%></span>, <span class="<%=getFieldClass(deliverToFirstName)%>"><%=getFieldDisplayValue(deliverToFirstName)%></span></td>
                </tr>
                <tr>
                    <td><%=deliverToId.startsWith("N") ? "CNO:" : "CPSID"%></td>
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
                    <td><a href="#" onclick="removeProvFromHrm('<%=providerLink.getId() %>', '<%=hrmReportId %>')">(remove)</a></td>
                </tr>
                <% } %>
                <% } %>
            </table>
            <table>
                <tr><th>Search Providers by Name</th></tr>
                <tr>
                    <td>
                        <div id="providerList<%=hrmReportId %>hrm"></div>
                        <input type="hidden" name="providerNo" id="provider-no"/>
                        <input type="text" id="provider-search" autocomplete="off" name="providerKeyword"/>
                    </td>
                    <td><a href="#" onclick="addProvToHrm(<%=hrmReportId%>, $('#provider-no').val())">(assign)</a></td>
                </tr>
            </table>
            <hr>
            <table>
                <tr><th colspan="2">Embedded Report Information</th></tr>
                <tr>
                    <td>Report Class:</td>
                    <td><%=hrmReport.getClassName()%></td>
                </tr>
                <% if (hrmReport.getClassName().equals(HrmDocument.ReportClass.DIAGNOSTIC_IMAGING.getValue()) ||
							   hrmReport.getClassName().equals(HrmDocument.ReportClass.CARDIO_RESPIRATORY.getValue())) { %>
                    <%
                        List<HrmObservation> hrmObservations = hrmReport.getObservations();
                    %>
                    <tr>
                        <td colspan="2">Accompanying SubClasses:</td>
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
                        <td>Subclass:</td>
                        <td><%=subClassDisplay%></td>
                    </tr>
                    <% } %>
                <% } %>
                <tr>
                    <td>Report No:</td>
                    <td><span class="<%=getFieldClass(reportNumber)%>"><%=getFieldDisplayValue(reportNumber)%></span></td>
                </tr>
            </table>
            <table>
                <tr>
                    <td>
                        <input type="button" style="display: none" value="Save" id="save<%=hrmReportId %>hrm" />
                    </td>
                    <td>
                        <input type="button" value="Print" onClick="window.print()" />
                    </td>
                    <td>
                        <%
                            HRMDocumentToProvider hrmDocumentToProvider = HRMDisplayReportAction.getHRMDocumentFromProvider(loggedInInfo.getLoggedInProviderNo(), hrmReportId);
                            if (hrmDocumentToProvider != null && hrmDocumentToProvider.isSignedOff()) {
                        %>
                        <input type="button" id="signoff<%=hrmReportId %>" value="Revoke Sign-Off" onClick="revokeSignOffHrm('<%=hrmReportId %>')"/>
                        <%
                        } else {
                        %>
                        <input type="button" id="signoff<%=hrmReportId %>" value="Sign-Off" onClick="signOffHrm('<%=hrmReportId %>')"/>
                        <%
                            }
                        %>
                    </td>
                </tr>
            </table>
        </div>
    </div>

    <div class="description-container hrm-action-container hide-on-print">
        <div style="width: 75%">
            <b class="label">Change Report Description:</b>
            <input class="input" type="text" id="descriptionField_<%=hrmReportId %>_hrm" value="<%=StringEscapeUtils.escapeHtml(hrmDocument.getDescription())%>"/>
            <input class="action-button" type="button" onClick="setDescription('<%=hrmReportId %>')" value="Save"/><span id="descriptionstatus<%=hrmReportId %>"></span>
        </div>
        <div style="width: 75%">
            <b class="label">Change Report Category:</b>
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
            <input class="action-button" type="button" onClick="reclassifyReport('<%=hrmReportId%>')" value="This Report"/>
            <input class="action-button" id="recategorize-future" type="button" onClick="reclassifyFutureReports('<%=hrmReportId%>')" value="Future Reports"/>
        </div>
    </div>

    <div class="comment-container hrm-action-container hide-on-print">
        <div>
            <b>Add a comment to this report:</b><br>
            <textarea rows="10" cols="50" id="commentField_<%=hrmReportId %>_hrm"></textarea><br />
            <input type="button" onClick="addComment('<%=hrmReportId %>')" value="Add Comment" /><span id="commentstatus<%=hrmReportId %>"></span><br /><br />
        </div>
        <%
            List<HRMDocumentComment> documentComments = (List<HRMDocumentComment>) request.getAttribute("hrmDocumentComments");

            if (documentComments != null) {
        %>Displaying <%=documentComments.size() %> comment<%=documentComments.size() != 1 ? "s" : "" %><br />
        <% for (HRMDocumentComment comment : documentComments) { %>
        <div class="documentComment"><strong><%=providerDao.getProviderName(comment.getProvider().getId()) %> on <%=comment.getCommentTime().toString() %> wrote...</strong><br />
            <%=comment.getComment() %><br />
            <a href="#" onClick="deleteComment('<%=comment.getId() %>', '<%=hrmReportId %>'); return false;">(Delete this comment)</a></div>
        <% }
        }
        %>
    </div>
</div>
<div class="footer" style="white-space: pre">
    Message Unique ID: <%=hrmReport.getMessageUniqueId()%>
    <% Integer duplicates = (Integer) request.getAttribute("hrmDuplicateNum"); %>
    <%= (duplicates != null && duplicates > 0) ? "Duplicates of this report have been received " + duplicates + " time(s)" : ""%>
    <% if (StringUtils.trimToNull(request.getParameter("duplicateLabIds")) != null) {
        Map<Integer, Date> dupReportDates = (Map<Integer,Date>)request.getAttribute("dupReportDates");
        Map<Integer, Date> dupTimeReceived = (Map<Integer,Date>)request.getAttribute("dupTimeReceived");

        String[] duplicateLabIds = request.getParameter("duplicateLabIds").split(",");
    %>
    <table>
        <tr><th>Report History</th></tr>
        <tr>
            <th>ID</th>
            <th>Report Date</th>
            <th>Received</th>
        </tr>
        <% for (String duplicateLabId : duplicateLabIds) { %>
        <tr>
            <td><%=duplicateLabId%></td>
            <td><%=dateFormat.format(dupReportDates.get(Integer.parseInt(duplicateLabId))) %></td>
            <td><%=dateFormat.format(dupTimeReceived.get(Integer.parseInt(duplicateLabId))) %></td>
            <input type="button" value="Open Report" onclick="window.open('?id=<%=duplicateLabId%>&segmentId=<%=duplicateLabId%>&providerNo=<%=request.getParameter("providerNo")%>&searchProviderNo=<%=request.getParameter("searchProviderNo")%>&status=<%=request.getParameter("status")%>&demoName=<%=StringEscapeUtils.escapeHtml(request.getParameter("demoName"))%>', null)" /> </td>
        </tr>
        <% } %>
        <% } %>
    </table>
</div>
</body>
</html>