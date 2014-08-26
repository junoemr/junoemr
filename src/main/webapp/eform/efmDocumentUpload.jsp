<%--

    Copyright (c) 2005-2012. OscarHost Inc. All Rights Reserved.
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

    This software was written for
    OscarHost, a Division of Cloud Practice Inc.

--%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ page import="java.util.*, oscar.util.*, oscar.OscarProperties, oscar.dms.*, oscar.dms.data.*, org.oscarehr.util.SpringUtils, org.oscarehr.common.dao.CtlDocClassDao"%>
<%
String curUser = "";
if(request.getParameter("showform") != null && request.getParameter("showform").equals("true")){
	OscarProperties props = OscarProperties.getInstance();
	String appointment = request.getParameter("appointmentNo");
	String user_no = (String) session.getAttribute("user");
	String defaultType = (String) props.getProperty("eDocAddTypeDefault", "");
	String defaultHtml = "Enter Link URL";

	if(request.getParameter("defaultDocType") != null) {
		defaultType = request.getParameter("defaultDocType");
	}
	String module = "";
	String moduleid = "";
	if (request.getParameter("function") != null) {
		module = request.getParameter("function");
		moduleid = request.getParameter("functionid");
	} else if (request.getAttribute("function") != null) {
		module = (String) request.getAttribute("function");
		moduleid = (String) request.getAttribute("functionid");
	}
	if (request.getParameter("curUser") != null) {
		curUser = request.getParameter("curUser");
	} else if (request.getAttribute("curUser") != null) {
		curUser = (String) request.getAttribute("curUser");
	}
	AddEditDocumentForm formdata = new AddEditDocumentForm();
	formdata.setAppointmentNo(appointment);
	if (request.getAttribute("completedForm") != null) {
		formdata = (AddEditDocumentForm) request.getAttribute("completedForm");
	} else {
		formdata.setFunction(module);  //"module" and "function" are the same
		formdata.setFunctionId(moduleid);
		formdata.setDocType(defaultType);
		formdata.setDocDesc(defaultType.equals("")?"":defaultType);
		formdata.setDocCreator(user_no);
		formdata.setObservationDate(UtilDateUtilities.DateToString(UtilDateUtilities.now(), "yyyy/MM/dd"));
		formdata.setHtml(defaultHtml);
		formdata.setAppointmentNo(appointment);
	}
	ArrayList doctypes = EDocUtil.getActiveDocTypes(formdata.getFunction());
	CtlDocClassDao docClassDao = (CtlDocClassDao)SpringUtils.getBean("ctlDocClassDao");
	List<String> reportClasses = docClassDao.findUniqueReportClasses();

	//Retrieve encounter id for updating encounter navbar if info this page changes anything
	String parentAjaxId;
	if( request.getParameter("parentAjaxId") != null )
		parentAjaxId = request.getParameter("parentAjaxId");
	else if( request.getAttribute("parentAjaxId") != null )
		parentAjaxId = (String)request.getAttribute("parentAjaxId");
	else
		parentAjaxId = "";
	%>
	<form id="addImage" method="POST" name="eDocsAddEditDocument" action="/clubtinytots/dms/addEditDocument.do" enctype="multipart/form-data" onsubmit="return checkRequired();">
		<input type="hidden" name="function" value="<%=formdata.getFunction()%>" size="20">
		<input type="hidden" name="functionId" value="<%=formdata.getFunctionId()%>" size="20">
		<input type="hidden" name="parentAjaxId" value="<%=parentAjaxId%>">
		<input type="hidden" name="curUser" value="<%=curUser%>">
		<input type="hidden" name="appointmentNo" value="<%=formdata.getAppointmentNo()%>"/>
		<input type="hidden" name="docType" value="photo"/>
		<input type="hidden" name="docCreator" value="<%=formdata.getDocCreator()%>" size="20">
		<input type="file" name="docFile" id="docFile" size="20" >
		<input type="hidden" name="mode" value="add">
		<input type="hidden" name="eformUpload" value="true">
		<div>
			Photo Label: <input type="text" name="docDesc" id="docDesc" size="30" value="<%=formdata.getDocDesc()%>" placeholder="Description">
		</div>
		<div>
			<span class="fieldlabel" title="Observation Date">
				Observation Date (yyyy/mm/dd):
			</span>
			<input type="text" name="observationDate" id="observationDate" value="<%=formdata.getObservationDate()%>" onclick="checkDefaultDate(this, '<%=UtilDateUtilities.DateToString(UtilDateUtilities.now(), "yyyy/MM/dd")%>')" size="10" style="text-align: center;">
			<a id="obsdate"><img title="Calendar" src="../images/cal.gif" alt="Calendar" border="0" /></a>
		</div>
		<div>
			<input type="submit" name="Submit" value="Add">
		</div>
	</form>
<script type="text/javascript">
function checkRequired(){
    showLoader();
    if(document.getElementById("docDesc").value == ""){
        alert("Please enter a photo label");
        hideLoader();
        return false;
    }
    if(document.getElementById("docFile").value == ""){
        alert("Please choose a file");
        hideLoader();
        return false;
    }
    return true;
}
function showLoader(){
    document.getElementById("loader").style.display = "block";
    document.getElementById("addImage").style.display = "none";
}
function hideLoader(){
    document.getElementById("loader").style.display = "none";
    document.getElementById("addImage").style.display = "block";
}
</script>
<%
}else{

String document_no = request.getParameter("document_no");
%>
<%=document_no%>
<script type="text/javascript">
    parent.document.getElementById("uploaded_document_no").value = "<%=document_no%>";
    parent.document.getElementById("current_document_no").value = "<%=document_no%>";
	var img = document.createElement("img");
	img.setAttribute("src", "../dms/ManageDocument.do?method=display&doc_no=<%=document_no%>&providerNo=<%=curUser%>");
	img.setAttribute("class", "uploadedimage");
	img.setAttribute("height", "375");
	img.setAttribute("width", "375");
    parent.document.getElementById("photodisplay").appendChild(img);
    parent.document.getElementById("photodisplay").style.display = "block";
	parent.document.getElementById("uploadiframe").style.display = "none";
</script>
<%}%>
