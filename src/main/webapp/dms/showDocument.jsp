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
<security:oscarSec roleName="<%=roleName$%>" objectName="_edoc" rights="r" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect("../securityError.jsp?type=_edoc");%>
</security:oscarSec>
<%
	if(!authed) {
		return;
	}
%>

<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ page import="org.apache.jcs.access.exception.InvalidArgumentException,org.oscarehr.PMmodule.dao.ProviderDao,org.oscarehr.common.dao.DemographicDao"%>
<%@page import="org.oscarehr.common.dao.OscarAppointmentDao"%>
<%@ page import="org.oscarehr.common.dao.ProviderInboxRoutingDao,org.oscarehr.common.dao.QueueDao" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/rewrite-tag.tld" prefix="rewrite"%>
<%@ taglib uri="/WEB-INF/oscar-tag.tld" prefix="oscar"%>
<%@ page import="org.oscarehr.common.dao.UserPropertyDAO,org.oscarehr.common.model.Appointment"%>
<%@ page import="org.oscarehr.common.model.Demographic" %>
<%@ page import="org.oscarehr.common.model.Provider" %>
<%@ page import="org.oscarehr.common.model.ProviderInboxItem" %>
<%@page import="org.oscarehr.common.model.UserProperty"%>
<%@page import="org.oscarehr.myoscar.utils.MyOscarLoggedInInfo,org.oscarehr.phr.util.MyOscarUtils,org.oscarehr.util.SpringUtils"%>
<%@page import="org.oscarehr.util.WebUtils,org.springframework.web.context.WebApplicationContext,org.springframework.web.context.support.WebApplicationContextUtils,oscar.OscarProperties"%>
<%@ page import="oscar.dms.EDoc" %>
<%@ page import="oscar.dms.EDocUtil" %>
<%@ page import="oscar.dms.IncomingDocUtil" %>
<%@ page import="oscar.log.LogAction" %>
<%@ page import="oscar.log.LogConst" %>
<%@ page import="oscar.oscarLab.ca.all.AcknowledgementData" %>
<%@ page import="oscar.oscarMDS.data.ReportStatus" %>
<%@ page import="oscar.util.ConversionUtils" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.List" %>
<%

			WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
			ProviderInboxRoutingDao providerInboxRoutingDao = (ProviderInboxRoutingDao) ctx.getBean("providerInboxRoutingDAO");
			UserPropertyDAO userPropertyDAO = (UserPropertyDAO)SpringUtils.getBean("UserPropertyDAO");
			OscarAppointmentDao appointmentDao = SpringUtils.getBean(OscarAppointmentDao.class);
			ProviderDao providerDao = (ProviderDao)SpringUtils.getBean("providerDao");
			OscarProperties props = OscarProperties.getInstance();

			String providerNo = request.getParameter("providerNo");
			if(providerNo == null)
			{
				providerNo = (String) session.getAttribute("user");
			}

			UserProperty uProp = userPropertyDAO.getProp(providerNo, UserProperty.LAB_ACK_COMMENT);
			boolean skipComment = false;
			boolean autoLinkDocsToProvider = props.isPropertyActive("assign_document.link_docs_to_provider");

			if( uProp != null && uProp.getValue().equalsIgnoreCase("yes")) {
				skipComment = true;
			}

			uProp = userPropertyDAO.getProp(providerNo, UserProperty.DISPLAY_DOCUMENT_AS);
			String displayDocumentAs=UserProperty.IMAGE;
			if( uProp != null && uProp.getValue().equals(UserProperty.PDF)) {
				displayDocumentAs = UserProperty.PDF;
			}

			String demoName=request.getParameter("demoName");
			String documentNo = request.getParameter("segmentID");
			String searchProviderNo = request.getParameter("searchProviderNo");
			String status = request.getParameter("status");
			String inQueue=request.getParameter("inQueue");
			String chartView = request.getParameter("chartView");

			if(documentNo == null || documentNo.equalsIgnoreCase("null"))
				throw new InvalidArgumentException("Invalid document_no: " + documentNo);

			boolean inQueueB = inQueue != null;
			boolean inChart = chartView != null;

			String defaultQueue = IncomingDocUtil.getAndSetIncomingDocQueue(providerNo, null);
			QueueDao queueDao = (QueueDao) ctx.getBean("queueDao");
			List<Hashtable> queues = queueDao.getQueues();
			int queueId=1;
			if (defaultQueue != null) {
				defaultQueue = defaultQueue.trim();
				queueId = Integer.parseInt(defaultQueue);
			}

			String creator = (String) session.getAttribute("user");
			ArrayList doctypes = EDocUtil.getActiveDocTypes("demographic");
			EDoc curdoc = EDocUtil.getDoc(documentNo);
			boolean isValidPdf = !curdoc.hasEncodingError() && curdoc.getContentType().toLowerCase().contains("pdf");

			String demographicID = curdoc.getModuleId();
			if ((demographicID != null) && !demographicID.isEmpty() && !demographicID.equals("-1")){
				DemographicDao demographicDao = (DemographicDao)SpringUtils.getBean("demographicDao");
				Demographic demographic = demographicDao.getDemographic(demographicID);
				demoName = demographic.getLastName()+","+demographic.getFirstName();
				LogAction.addLog((String) session.getAttribute("user"), LogConst.ACTION_READ, LogConst.CON_DOCUMENT, documentNo, request.getRemoteAddr(),demographicID);
			}

			String docId = curdoc.getDocId();

			String ackFunc;
			if( skipComment ) {
				ackFunc = "updateStatus('acknowledgeForm_" + docId + "'," + inQueueB + ");";
			}
			else {
				ackFunc = "getDocComment('" + docId + "','" + providerNo + "'," + inQueueB + ");";
			}

			int slash = 0;
			String contentType = "";
			if ((slash = curdoc.getContentType().indexOf('/')) != -1) {
				contentType = curdoc.getContentType().substring(slash + 1);
			}
			int numOfPage=curdoc.getNumberOfPages();
			String numOfPageStr="";
			if(numOfPage==0)
				numOfPageStr="unknown";
			else
				numOfPageStr=(new Integer(numOfPage)).toString();
			String contextPath = request.getContextPath();
			String url = contextPath +"/dms/ManageDocument.do?method=viewDocPage&doc_no=" + docId+"&curPage=1&rand=" + Math.random();
			String url2 = contextPath +"/dms/ManageDocument.do?method=display&doc_no=" + docId;
			String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
%>
<% if (request.getParameter("inWindow") != null && request.getParameter("inWindow").equalsIgnoreCase("true")) {  %>
<html>
<title>Show Document <%=documentNo%></title>
<head>
	<script type="text/javascript" src="<%= contextPath %>/share/calendar/calendar.js"></script>
	<!-- language for the calendar -->
	<script type="text/javascript" src="<%= contextPath %>/share/calendar/lang/<bean:message key='global.javascript.calendar'/>"></script>
	<!-- the following script defines the Calendar.setup helper function, which makes
		   adding a calendar a matter of 1 or 2 lines of code. -->
	<script type="text/javascript" src="<%= contextPath %>/share/calendar/calendar-setup.js"></script>
	<!-- calendar stylesheet -->
	<link rel="stylesheet" type="text/css" media="all" href="<%= contextPath %>/share/calendar/calendar.css" title="win2k-cold-1" />
	<script type="text/javascript"
	        src="<%= contextPath %>/js/util/common.js"></script>
	<script type="text/javascript"
	        src="<%= contextPath %>/js/moment.min.js"></script>
	<script type="text/javascript"
	        src="<%= contextPath %>/js/util/date.js"></script>
	<script type="text/javascript"
			src="<%= contextPath %>/share/javascript/prototype.js"></script>
	<script type="text/javascript"
			src="<%= contextPath %>/share/javascript/effects.js"></script>
	<script type="text/javascript"
			src="<%= contextPath %>/share/javascript/controls.js"></script>
	<!-- jquery -->
	<script type="text/javascript"
			src="<%= contextPath %>/js/jquery-1.9.1.js"></script>
	<script type="text/javascript"
			src="<%= contextPath %>/js/jquery-ui-1.10.2.custom.min.js"></script>
	<script language="javascript" type="text/javascript"
			src="<%= contextPath %>/share/javascript/Oscar.js"></script>


	<script type="text/javascript"
			src="<%= contextPath %>/js/demographicProviderAutocomplete.js"></script>

	<script type="text/javascript"
			src="<%= contextPath %>/share/javascript/oscarMDSIndex.js"></script>

	<link rel="stylesheet" type="text/css"
		  href="<%= contextPath %>/share/yui/css/fonts-min.css"/>
	<link rel="stylesheet" type="text/css"
		  href="<%= contextPath %>/share/yui/css/autocomplete.css"/>
	<link rel="stylesheet" type="text/css" media="all"
		  href="<%= contextPath %>/share/css/demographicProviderAutocomplete.css"/>

	<script type="text/javascript" src="<%=contextPath%>/dms/showDocument.js"></script>


	<style type="text/css">
			.multiPage {
				background-color: RED;
				color: WHITE;
				font-weight:bold;
				padding: 0px 5px;
				font-size: medium;
			}
			.singlePage {

			}
	</style>
	<script type="text/javascript">
		jQuery.noConflict();
		var contextPath = '<%=contextPath%>';

		var _in_window = <%=( "true".equals(request.getParameter("inWindow")) ? "true" : "false" )%>;
	</script>
</head>
<body>
<% } %>
		<div id="labdoc_<%=docId%>">
			<%
			 ArrayList ackList = AcknowledgementData.getAcknowledgements("DOC",docId);
			 ReportStatus reportStatus = null;
			 String docCommentTxt = "";
			 String rptStatus = "";
			 boolean ackedOrFiled = false;
			 for( int idx = 0; idx < ackList.size(); ++idx ) {
				 reportStatus = (ReportStatus) ackList.get(idx);

				 if( reportStatus.getOscarProviderNo() != null && reportStatus.getOscarProviderNo().equals(providerNo) ) {
					docCommentTxt = reportStatus.getComment();
					if( docCommentTxt == null ) {
						docCommentTxt = "";
					}

					rptStatus = reportStatus.getStatus();

					if( rptStatus != null ) {
						ackedOrFiled = rptStatus.equalsIgnoreCase("A") ? true : rptStatus.equalsIgnoreCase("F") ? true : false;
					}
					break;
				 }
			 }
			%>
			<form name="acknowledgeForm_<%=docId%>" id="acknowledgeForm_<%=docId%>" onsubmit="<%=ackFunc%>" method="post" action="javascript:void(0);">

								<input type="hidden" name="segmentID" value="<%= docId%>"/>
								<input type="hidden" name="multiID" value="<%= docId%>" />
								<input type="hidden" name="providerNo" value="<%= providerNo%>"/>
								<input type="hidden" name="status" value="A" id="status_<%=docId%>">
								<input type="hidden" name="labType" value="DOC"/>
								<input type="hidden" name="ajaxcall" value="yes"/>
								<input type="hidden" name="comment" id="comment_<%=docId%>" value="<%=docCommentTxt%>">
							<% if (demographicID != null && !demographicID.equals("") && !demographicID.equalsIgnoreCase("null") && !ackedOrFiled ) {%>
								<input type="submit" id="ackBtn_<%=docId%>" value="<bean:message key="oscarMDS.segmentDisplay.btnAcknowledge"/>">
								<input type="button" value="Comment" onclick="addDocComment('<%=docId%>','<%=providerNo%>',true)"/>
								<%if (MyOscarUtils.isMyOscarEnabled((String) session.getAttribute("user"))){
									MyOscarLoggedInInfo myOscarLoggedInInfo=MyOscarLoggedInInfo.getLoggedInInfo(session);
									boolean enabledMyOscarButton=MyOscarUtils.isMyOscarSendButtonEnabled(myOscarLoggedInInfo, Integer.valueOf(demographicID));
								%>
								<input type="button" <%=WebUtils.getDisabledString(enabledMyOscarButton)%> value="<bean:message key="global.btnSendToPHR"/>" onclick="popup(450, 600, '../phr/SendToPhrPreview.jsp?module=document&documentNo=<%=docId%>&demographic_no=<%=demographicID%>', 'sendtophr')"/>
								<%}%>
							<%}%>
								<input type="button" id="fwdBtn_<%=docId%>"  value="<bean:message key="oscarMDS.index.btnForward"/>" onClick="popupStart(355, 685, '../oscarMDS/SelectProvider.jsp?docId=<%=docId%>', 'providerselect');">
							<%if( !ackedOrFiled ) { %>
								<input type="button" id="fileBtn_<%=docId%>"  value="<bean:message key="oscarMDS.index.btnFile"/>" onclick="fileDoc('<%=docId%>');">
							<%} %>
								<input type="button" id="closeBtn_<%=docId%>" value=" <bean:message key="global.btnClose"/> " onClick="window.close()">
								<input type="button" id="printBtn_<%=docId%>" value=" <bean:message key="global.btnPrint"/> " onClick="popup(700,960,'<%=url2%>','file download')">
								<%
								String btnDisabled = "disabled";
								if (demographicID != null && !demographicID.equals("") && !demographicID.equalsIgnoreCase("null") && !demographicID.equals("-1") ) {
									btnDisabled = "";
								}

								%>
								<input type="button" id="msgBtn_<%=docId%>" value="Msg" onclick="Oscar.ShowDocument.popupPatient(700,960,'<%= contextPath %>/oscarMessenger/SendDemoMessage.do?demographic_no=','msg', '<%=docId%>')" <%=btnDisabled %>/>
								<input type="button" id="mainTickler_<%=docId%>" value="Tickler" onClick="Oscar.ShowDocument.popupPatientTickler(710, 1024,'<%= contextPath %>', 'Tickler','<%=docId%>')" <%=btnDisabled %>>
								<input type="button" id="mainEchart_<%=docId%>" value=" <bean:message key="oscarMDS.segmentDisplay.btnEChart"/> " onClick="Oscar.ShowDocument.popupPatient(710, 1024,'<%= contextPath %>/oscarEncounter/IncomingEncounter.do?reason=<bean:message key="oscarMDS.segmentDisplay.labResults"/>&curDate=<%=currentDate%>>&appointmentNo=&appointmentDate=&startTime=&status=&demographicNo=', 'encounter', '<%=docId%>')" <%=btnDisabled %>>
								<input type="button" id="mainMaster_<%=docId%>" value=" <bean:message key="oscarMDS.segmentDisplay.btnMaster"/>" onClick="Oscar.ShowDocument.popupPatient(710,1024,'<%= contextPath %>/demographic/demographiccontrol.jsp?displaymode=edit&dboperation=search_detail&demographic_no=','master','<%=docId%>')" <%=btnDisabled %>>
								<input type="button" id="mainApptHistory_<%=docId%>" value=" <bean:message key="oscarMDS.segmentDisplay.btnApptHist"/>" onClick="Oscar.ShowDocument.popupPatient(710,1024,'<%= contextPath %>/demographic/demographiccontrol.jsp?orderby=appttime&displaymode=appt_history&dboperation=appt_history&limit1=0&limit2=25&demographic_no=','ApptHist','<%=docId%>')" <%=btnDisabled %>>

								<%if (inQueueB) { %>
								<input type="button" id="refileDoc_<%=docId%>" value="<bean:message key="oscarEncounter.noteBrowser.msgRefile"/>" onclick="refileDoc('<%=docId%>');" >
								<select  id="queueList_<%=docId%>" name="queueList">
									<%
									for (Hashtable ht : queues) {
										int id = (Integer) ht.get("id");
										String qName = (String) ht.get("queue");
									%>
									<option value="<%=id%>" <%=((id == queueId) ? " selected" : "")%>><%= qName%> </option>
									<%}%>
								</select>
								<%}%>
			</form>
			<table class="docTable">
				<tr>


					<td colspan="8">
						<div style="text-align: right;font-weight: bold">
						<% if( numOfPage > 1 && displayDocumentAs.equals(UserProperty.IMAGE) ) {%>
							<a id="firstP_<%=docId%>" style="visibility: hidden;" href="javascript:void(0);" onclick="firstPage('<%=docId%>','<%=contextPath%>');">First</a>
							<a id="prevP_<%=docId%>" style="visibility: hidden;"  href="javascript:void(0);" onclick="prevPage('<%=docId%>','<%=contextPath%>');">Prev</a>
							<a id="nextP_<%=docId%>" href="javascript:void(0);" onclick="nextPage('<%=docId%>','<%=contextPath%>');">Next</a>
							<a id="lastP_<%=docId%>" href="javascript:void(0);" onclick="lastPage('<%=docId%>','<%=contextPath%>');">Last</a>
							<%} %>
						</div>
						<% if (displayDocumentAs.equals(UserProperty.IMAGE)) { %>
							<a href="<%=url2%>" target="_blank"><img alt="document" id="docImg_<%=docId%>"  src="<%=url%>" /></a>
						<%} else {%>
							<div id="docDispPDF_<%=docId%>"></div>
						<%}%>
					</td>

					<td align="left" valign="top">
						<fieldset><legend><bean:message key="inboxmanager.document.PatientMsg"/><span id="assignedPId_<%=docId%>"><%=demoName%></span> </legend>
							<table border="0">
								<tr>
									<td><bean:message key="inboxmanager.document.DocumentUploaded"/></td>
									<td><%=curdoc.getDateTimeStamp()%></td>
								</tr>
								<tr>
									<td><bean:message key="inboxmanager.document.ContentType"/></td>
									<td><%=contentType%></td>
								</tr>
								<tr>
									<td><bean:message key="inboxmanager.document.NumberOfPages"/></td>
									<td>
										<input id="shownPage_<%=docId %>" type="hidden" value="1" />
										<%if (displayDocumentAs.equals(UserProperty.IMAGE)) { %>
											<span id="viewedPage_<%=docId%>" class="<%= numOfPage > 1 ? "multiPage" : "singlePage" %>">1</span>&nbsp; of &nbsp;<%}%>
										<span id="numPages_<%=docId %>" class="<%= numOfPage > 1 ? "multiPage" : "singlePage" %>"><%=numOfPageStr%></span>
									</td>
								</tr>

								<tr><td></td>
									<td>
										<% boolean updatableContent = isValidPdf && !inChart; %>
										<oscar:oscarPropertiesCheck property="ALLOW_UPDATE_DOCUMENT_CONTENT" value="false" defaultVal="false">
											<%
												if(!demographicID.equals("-1")) { updatableContent=false; }
											%>
										</oscar:oscarPropertiesCheck>

										<div style="<%=(updatableContent)?"":"visibility: hidden"%>">
											<input onclick="split('<%=docId%>','<%=StringEscapeUtils.escapeJavaScript(demoName) %>')" type="button" value="<bean:message key="inboxmanager.document.split" />" />
											<input id="rotate180btn_<%=docId %>" onclick="rotate180('<%=docId %>')" type="button" value="<bean:message key="inboxmanager.document.rotate180" />" />
											<input id="rotate90btn_<%=docId %>" onclick="rotate90('<%=docId %>')" type="button" value="<bean:message key="inboxmanager.document.rotate90" />" />
											<% if (numOfPage > 1) { %><input id="removeFirstPagebtn_<%=docId %>" onclick="removePage('<%=docId %>', 1)" type="button" value="<bean:message key="inboxmanager.document.removeFirstPage" />" /><% } %>
											<% if (numOfPage > 1 && displayDocumentAs.equals(UserProperty.IMAGE))
											{ %>
												<input id="removePagebtn_<%=docId %>" onclick="removePage('<%=docId %>', $('curPage_' + <%=docId %>).value)" type="button" value="<bean:message key="inboxmanager.document.removeCurrentPage" />"/>
											<% } %>
										</div>
									</td>
								</tr>

							</table>

							<form id="forms_<%=docId%>" onsubmit="validateAndUpdateDocument(this.id); return false">
								<input type="hidden" name="method" value="documentUpdate" />
								<input type="hidden" name="documentId" value="<%=docId%>" />
								<!-- segmentID is needed in the event of a page refresh on submit,
								which shouldn't happen anymore  since we are forcing a false return -->
								<input type="hidden" name="segmentID" value="<%=docId%>" />
								<input type="hidden" name="curPage_<%=docId%>" id="curPage_<%=docId%>" value="1"/>
								<input type="hidden" name="totalPage_<%=docId%>" id="totalPage_<%=docId%>" value="<%=numOfPage%>"/>
								<input type="hidden" name="displayDocumentAs_<%=docId%>" id="displayDocumentAs_<%=docId%>" value="<%=displayDocumentAs%>">
								<table border="0">
									<tr>
										<td><bean:message key="dms.documentReport.msgDocType" />:</td>
										<td>
											<select name ="docType" id="docType_<%=docId%>">
												<option value=""><bean:message key="dms.addDocument.formSelect" /></option>
												<%for (int j = 0; j < doctypes.size(); j++) {
				String doctype = (String) doctypes.get(j);%>
												<option value="<%= doctype%>" <%=(doctype.equals(curdoc.getType())) ? " selected" : ""%>><%= doctype%></option>
												<%}%>
											</select>
										</td>
									</tr>
									<tr>
										<td><bean:message key="dms.documentReport.msgDocDesc" />:</td>
										<td><input id="docDesc_<%=docId%>"  type="text" name="documentDescription" value="<%=curdoc.getDescription()%>" /></td>
									</tr>
									<tr>
										<td><bean:message key="inboxmanager.document.ObservationDateMsg" /></td>
										<td>
											<input   id="observationDate<%=docId%>" name="observationDate" type="text" value="<%=curdoc.getObservationDate().replaceAll("/","-")%>">
											<a id="obsdate<%=docId%>" href="javascript:void(0);"
											   onmouseover="Calendar.setup({ inputField : 'observationDate<%=docId%>', ifFormat : '%Y-%m-%d', showsTime :false, button : this.id });" >
												<img title="Calendar" src="<%=contextPath%>/images/cal.gif" alt="Calendar"border="0" />
											</a>
										</td>
									</tr>
									<tr>
										<td><bean:message key="inboxmanager.document.DemographicMsg" /></td>
										<td style="width:400px;"><%
										if(!demographicID.equals("-1")){%>
											<input id="saved<%=docId%>" type="hidden" name="saved" value="true"/>
											<input type="hidden" value="<%=demographicID%>" name="demog" id="demofind<%=docId%>" />
											<input type="hidden" name="demofindName" value="<%=demoName%>" id="demofindName<%=docId%>"/>
											<%=demoName%>
										<%}else{%>
											<input id="saved<%=docId%>" type="hidden" name="saved" value="false"/>
											<input type="hidden" name="demog" value="<%=demographicID%>" id="demofind<%=docId%>"/>
											<input type="hidden" name="demofindName" value="<%=demoName%>" id="demofindName<%=docId%>"/>

											<input type="checkbox" id="activeOnly<%=docId%>" name="activeOnly" checked="checked" value="true"
											       onclick="Oscar.ShowDocument.setupDemoAutoCompletion('<%=contextPath%>', '<%=docId%>',<%=autoLinkDocsToProvider%>)">Active Only<br>
											<input type="text" style="width:400px;" id="autocompletedemo<%=docId%>" onchange="checkSave('<%=docId%>');" name="demographicKeyword" />
											<div id="autocomplete_choices<%=docId%>" class="autocomplete"></div>

										<%}
										if (!demographicID.equals("-1")) {%>
											<button id="mrp_<%=docId%>" onclick="sendMRP(this, 'DOC')" name="linkMRP">Send to MRP</button>
											<a id="mrp_fail_<%=docId%>" style="color:red;font-style: italic;display: none;" ><bean:message key="inboxmanager.document.SendToMRPFailedMsg" /></a>
											<a id="mrp_success_<%=docId%>" style="color:green;font-style: italic;display: none;" ><bean:message key="inboxmanager.document.SendToMRPSuccessMsg" /></a>
										<%}else if (!inChart){%>
											<input type="button" id="createNewDemo" value="Create New Demographic"  onclick="popup(700,960,'<%= contextPath %>/demographic/demographicaddarecordhtm.jsp','demographic')"/>
										<%}%>
											<input id="saved_<%=docId%>" type="hidden" name="saved" value="false"/>
										</td>
									</tr>

									<tr>
										<td valign="top"><bean:message key="inboxmanager.document.FlagProviderMsg" /> </td>
										<td>
											<input type="hidden" name="provi" id="provfind<%=docId%>" />
											<input type="text" id="autocompleteprov<%=docId%>" name="demographicKeyword"/>
											<div id="autocomplete_choicesprov<%=docId%>" class="autocomplete"></div>


											<div id="providerList<%=docId%>"></div>
										</td>
									</tr>

									<tr>
										<td><bean:message key="dms.documentReport.msgCreator"/>:</td>
										<td><%=curdoc.getCreatorName()%></td>
									</tr>

									<tr>
										<td width="30%" colspan="1" align="left">
											<a id="saveSucessMsg_<%=docId%>" style="display:none;color:blue;"><bean:message key="inboxmanager.document.SuccessfullySavedMsg"/></a>
										</td>
										<td width="30%" colspan="1" align="left">
											<input type="submit" name="save" id="save<%=docId%>" <%=demographicID.equals("-1") ? "disabled" : ""%> value="Save" />
											<%if (!inChart) { %>
											<input type="button" name="save" id="saveNext<%=docId%>" onclick="saveNext(<%=docId%>)" <%=demographicID.equals("-1") ? "disabled" : ""%> value='<bean:message key="inboxmanager.document.SaveAndNext"/>' />
											<%}%>
										</td>
									</tr>

									<tr>
										<td colspan="2">
											<bean:message key="inboxmanager.document.LinkedProvidersMsg"/>
											<ul>
												<%
												List<ProviderInboxItem> routeList = providerInboxRoutingDao.getProvidersWithRoutingForDocument("DOC", Integer.parseInt(docId));
												for (ProviderInboxItem pItem : routeList) {
													Provider p = providerDao.getProvider(pItem.getProviderNo());
													String s = (p != null)? p.getDisplayName() : pItem.getProviderNo();

													if(!s.equals("0")&&!s.equals("null")&& !pItem.getStatus().equals(ProviderInboxItem.ARCHIVED)){  %>
														<li><%=s%><a href="#" onclick="removeLink('DOC', '<%=docId %>', '<%=pItem.getProviderNo() %>', this);return false;"><bean:message key="inboxmanager.document.RemoveLinkedProviderMsg" /></a></li>
												<%}
												}%>
											</ul>
										</td>
									</tr>
								</table>

							</form>
						</fieldset>


							<%

											if (ackList.size() > 0){%>
											<fieldset>
												<table width="100%" height="20" cellpadding="2" cellspacing="2">
													<tr>
															<td align="center" bgcolor="white">
															<div class="FieldData">
																<!--center-->
																	<% for (int i=0; i < ackList.size(); i++) {
																		ReportStatus report = (ReportStatus) ackList.get(i); %>

																		<% String ackStatus = report.getStatus();
																			if(ackStatus.equals(ProviderInboxItem.ACK)){
																				ackStatus = "Acknowledged";
																			}else if(ackStatus.equals(ProviderInboxItem.FILE)){
																				ackStatus = "Filed but not Acknowledged";
																			}else{
																				ackStatus = "Not Acknowledged";
																			}
																		// Only show providers that weren't removed from the document
																		if(!report.getStatus().equals(ProviderInboxItem.ARCHIVED)) { %>
																			<%= report.getProviderName() %> :
																			<font color="red"><%= ackStatus %></font>
																				<span id="timestamp_<%=docId + "_" + report.getOscarProviderNo()%>"><%= report.getTimestamp() == null ? "&nbsp;" : report.getTimestamp() + "&nbsp;"%></span>,
																				comment: <span id="comment_<%=docId + "_" + report.getOscarProviderNo()%>"><%=report.getComment() == null || report.getComment().equals("") ? "no comment" : report.getComment()%></span>

																			<br>
																		<%}
																	 }
																	if (ackList.size() == 0){
																		%><font color="red">N/A</font><%
																	}
																	%>
																<!--/center-->
															</div>
														</td>
													</tr>
												</table>
											</fieldset>
											<%}

%>

						<fieldset>
						  <%--  <input id="test1Regex_<%=docId%>" type="text"/><input id="test2Regex_<%=docId%>" type="text"/>
							<a href="javascript:void(0);" onclick="testShowDoc();">click</a>--%>
							<legend><span class="FieldData"><i><bean:message key="inboxmanager.document.NextAppointmentMsg"/> <oscar:nextAppt demographicNo="<%=demographicID%>"/></i></span></legend>
							<%
								int iPageSize = 5;
								Provider prov;
								boolean HighlightUserAppt = false;
								if (!demographicID.equals("-1")) {

									List<Appointment> appointmentList = appointmentDao.getAppointmentHistory(Integer.parseInt(demographicID), 0, iPageSize);
									if (appointmentList != null && appointmentList.size() > 0) {
							%>

							<table bgcolor="#c0c0c0" align="center" valign="top">
								<tr bgcolor="#ccccff">
									<th colspan="4"><bean:message key="appointment.addappointment.msgOverview" /></th>
								</tr>
								<tr bgcolor="#ccccff">
									<th><bean:message key="Appointment.formDate" /></th>
									<th><bean:message key="Appointment.formStartTime" /></th>
									<th><bean:message key="appointment.addappointment.msgProvider" /></th>
									<th><bean:message key="appointment.addappointment.msgComments" /></th>
								</tr>
								<%
									for (Appointment a : appointmentList) {
										prov = providerDao.getProvider(a.getProviderNo());
										HighlightUserAppt = false;
										if (creator.equals(a.getProviderNo())) {
											HighlightUserAppt = true;
										}
								%>
								<tr bgcolor="<%=HighlightUserAppt == false ? "#FFFFFF" : "#CCFFCC"%>">
									<td ><%=ConversionUtils.toDateString(a.getAppointmentDate())%></td>
									<td ><%=ConversionUtils.toTimeString(a.getStartTime())%></td>
									<td ><%=prov == null ? "N/A" : prov.getFormattedName()%></td>
									<td ><% if (a.getStatus() == null) {%>"" <% } else if (a.getStatus().equals("N")) {%><bean:message key="oscar.appt.ApptStatusData.msgNoShow" /><% } else if (a.getStatus().equals("C")) {%><bean:message key="oscar.appt.ApptStatusData.msgCanceled" /> <%}%>
									</td>
								</tr>
								<%}%>
							</table>
							<%}
									}%>
							<form name="reassignForm_<%=docId%>" id="reassignForm_<%=docId%>">
								<input type="hidden" name="flaggedLabs" value="<%= docId%>" />
								<input type="hidden" name="selectedProviders" value="" />
								<input type="hidden" name="labType" value="DOC" />
								<input type="hidden" name="labType<%= docId%>DOC" value="imNotNull" />
								<input type="hidden" name="providerNo" value="<%= providerNo%>" />
								<input type="hidden" name="favorites" value="" />
								<input type="hidden" name="ajax" value="yes" />
							</form>
						 </fieldset>
					</td>
				</tr>
				<tr>
					<td colspan="8">
						<div style="text-align: right;font-weight: bold">
							<% if( numOfPage > 1 && displayDocumentAs.equals(UserProperty.IMAGE)) {%>
							<a id="firstP2_<%=docId%>" style="visibility: hidden;" href="javascript:void(0);" onclick="firstPage('<%=docId%>','<%=contextPath%>');">First</a>
							<a id="prevP2_<%=docId%>" style="visibility: hidden;"  href="javascript:void(0);" onclick="prevPage('<%=docId%>','<%=contextPath%>');">Prev</a>
							<a id="nextP2_<%=docId%>" href="javascript:void(0);" onclick="nextPage('<%=docId%>','<%=contextPath%>');">Next</a>
							<a id="lastP2_<%=docId%>" href="javascript:void(0);" onclick="lastPage('<%=docId%>','<%=contextPath%>');">Last</a>
							<%} %>
						</div>
					</td>
					<td>&nbsp;</td>
				</tr>
				<tr><td colspan="9" ><hr width="100%" color="red"></td></tr>
			</table>
		</div>
<!--

//-->
<script type="text/javascript">

		if($('displayDocumentAs_<%=docId%>').value=="<%=UserProperty.PDF%>") {
			showPDF('<%=docId%>',contextPath);
		}

		jQuery(Oscar.ShowDocument.setupDemoAutoCompletion('<%=contextPath%>', '<%=docId%>', <%=autoLinkDocsToProvider%>));
		jQuery(Oscar.ShowDocument.setupProviderAutoCompletion('<%=contextPath%>', '<%=docId%>'));

		if (<%=inQueueB%>)
		{
			window.validateAndUpdateDocument = function(formId)
			{
				if (Oscar.ShowDocument.checkObservationDate(formId))
					updateDocument(formId);

			};
		} else {
			window.validateAndUpdateDocument = function(formId)
			{
				Oscar.ShowDocument.validateAndUpdateDocument(formId);
				if (<%=inChart%>)
					window.opener.location.reload();
			};

			// queues view implements its own version of this
			window.forwardDocument = function(docId) {
				var frm = "#reassignForm_" + docId;
				var query = jQuery(frm).serialize();

				jQuery.ajax({
					type: "POST",
					url:  "<%= request.getContextPath()%>/oscarMDS/ReportReassign.do",
					data: query,
					success: function (data) {
						window.location.reload();
					},
					error: function(jqXHR, err, exception) {
						alert("Error " + jqXHR.status + " " + err);
					}
				});
			};
		}
</script>
<% if (request.getParameter("inWindow") != null && request.getParameter("inWindow").equalsIgnoreCase("true")) {  %>
</body>
</html>
<%}%>
