<%--

    Copyright (c) 2008-2012 Indivica Inc.

    This software is made available under the terms of the
    GNU General Public License, Version 2, 1991 (GPLv2).
    License details are available via "indivica.ca/gplv2"
    and "gnu.org/licenses/gpl-2.0.html".

--%>
<%@page contentType="text/html"%>
<%@page import="oscar.dms.data.*,java.util.*,oscar.oscarLab.ca.on.CommonLabResultData,org.oscarehr.util.SpringUtils,org.oscarehr.common.dao.QueueDao, oscar.oscarMDS.data.ProviderData" %>
<%@page import="org.oscarehr.PMmodule.dao.ProviderDao, org.oscarehr.common.model.Provider" %>
<%@page import="oscar.OscarProperties"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%

ProviderDao providerDao = (ProviderDao) SpringUtils.getBean("providerDao");
ArrayList<Provider> providers = new ArrayList<Provider>(providerDao.getActiveProviders());
String provider = CommonLabResultData.NOT_ASSIGNED_PROVIDER_NO;

QueueDao queueDao = (QueueDao) SpringUtils.getBean("queueDao");
HashMap queues = queueDao.getHashMapOfQueues();
String queueIdStr = (String) request.getSession().getAttribute("preferredQueue");
int queueId = 1;
if (queueIdStr != null) {
    queueIdStr = queueIdStr.trim();
    queueId = Integer.parseInt(queueIdStr);
}

String context = request.getContextPath();
String resourcePath = context + "/share/documentUploader/";

String userAgent = request.getHeader("User-Agent");
int isFirefox = userAgent.indexOf("Firefox");
if (isFirefox > 0) {
	if (Float.parseFloat(userAgent.substring(isFirefox + 8, isFirefox + 11)) < 4.0) {
%>
	<jsp:forward page="documentUploaderFirefox36.jsp" />
<%
	}
}
%>
<!DOCTYPE HTML>
<html lang="en" class="no-js">
<head>
	<meta charset="utf-8">
	<title><bean:message key="inboxmanager.document.title" /></title>
	<link rel="stylesheet" href="<%=context%>/css/cupertino/jquery-ui-1.8.18.custom.css" id="theme">
	<link rel="stylesheet" href="<%=resourcePath%>jquery.fileupload-ui.css">
	<link rel="stylesheet" href="<%=resourcePath%>style.css">
	<link rel="stylesheet" type="text/css" href="<%=context%>/share/css/OscarStandardLayout.css" />


	<script type="text/javascript">
	function setProvider(select){
		jQuery("#provider").val(select.options[select.selectedIndex].value);
	}
	</script>
	<style type="text/css">
	body {
		background-color: #c0c0c0;
	}
	</style>
</head>
<body>
<div id="fileupload">
    <form action="<%=context%>/dms/documentUpload.do" method="POST" enctype="multipart/form-data">
        <div class="fileupload-buttonbar">
            <label class="fileinput-button">
                <span id="add">Add files...</span>
                <input type="file" name="filedata" multiple>
            </label>
            <button id="start" type="submit" class="start">Start upload</button>
            <button itd="cancel" type="reset" class="cancel">Cancel upload</button>
            <span>
				<input type="hidden" id="provider" name="provider" value="<%=provider%>" />
				<input type="hidden" name="queue" value="<%=queueId%>"/>
				<label style="font-family:Arial; font-weight:normal; font-size:12px" for="providerDrop" class="fields">Send to Provider:</label>
				<select onchange="javascript:setProvider(this);" id="providerDrop" name="providerDrop">
					<option value="0" <%=("0".equals(provider) ? " selected" : "")%>>None</option>
					<%
					for (int i = 0; i < providers.size(); i++) {
	                	Provider h = providers.get(i);
	                %>
					<option value="<%= h.getProviderNo()%>" <%= (h.getProviderNo().equals(provider) ? " selected" : "")%>><%= h.getLastName()%> <%= h.getFirstName()%></option>
					<%
					}
					%>
				</select>
				<%
				if(OscarProperties.getInstance().isPropertyActive("INBOX_SET_RESPONSIBLE"))
				{
				%>
				&nbsp;<label for="use_provider_as_responsible" style="font-family:Arial; font-weight:normal; font-size:12px"><bean:message key="dms.documentReport.msgResponsibleProvider"/></label>
				<input type="checkbox" id="use_provider_as_responsible" name="use_provider_as_responsible" checked="checked" />
				<%
				}
				%>
			</span>
        </div>
    </form>
    <div class="fileupload-content">
        <table class="files"></table>
        <div class="fileupload-progressbar"></div>
    </div>
</div>
<script id="template-upload" type="text/x-jquery-tmpl">
    <tr class="template-upload{{if error}} ui-state-error{{/if}}">
        <td class="preview"></td>
        <td class="name">\${name}</td>
        <td class="size">\${sizef}</td>
        {{if error}}
            <td class="error" colspan="2">Error:
                {{if error === 'maxFileSize'}}File is too big
                {{else error === 'minFileSize'}}File is too small
                {{else error === 'acceptFileTypes'}}Filetype not allowed
                {{else error === 'maxNumberOfFiles'}}Max number of files exceeded
                {{else}}\${error}
                {{/if}}
            </td>
        {{else}}
            <td class="progress"><div></div></td>
            <td class="start"><button>Start</button></td>
        {{/if}}
        <td class="cancel"><button>Cancel</button></td>
    </tr>
</script>
<script id="template-download" type="text/x-jquery-tmpl">
    <tr class="template-download{{if error}} ui-state-error{{/if}}">
        {{if error}}
            <td></td>
            <td class="name">\${name}</td>
            <td class="size">\${sizef}</td>
            <td class="error" colspan="2">Error:
                {{if error === 1}}File exceeds upload_max_filesize (php.ini directive)
                {{else error === 2}}File exceeds MAX_FILE_SIZE (HTML form directive)
                {{else error === 3}}File was only partially uploaded
                {{else error === 4}}No File was uploaded
                {{else error === 5}}Missing a temporary folder
                {{else error === 6}}Failed to write file to disk
                {{else error === 7}}File upload stopped by extension
                {{else error === 'maxFileSize'}}File is too big
                {{else error === 'minFileSize'}}File is too small
                {{else error === 'acceptFileTypes'}}Filetype not allowed
                {{else error === 'maxNumberOfFiles'}}Max number of files exceeded
                {{else error === 'uploadedBytes'}}Uploaded bytes exceed file size
                {{else error === 'emptyResult'}}Empty file upload result
                {{else}}\${error}
                {{/if}}
            </td>
        {{else}}
            <td class="preview"> </td>
            <td class="name"> \${name} </td>
            <td class="size"> \${sizef} </td>
            <td colspan="2">Uploaded successfully</td>
        {{/if}}
    </tr>
</script>
<script src="<%=context%>/js/jquery-1.7.1.min.js"></script>
<script src="<%=context%>/js/jquery-ui-1.8.18.custom.min.js"></script>
<script src="<%=resourcePath%>jquery.tmpl.min.js"></script>
<script src="<%=resourcePath%>jquery.iframe-transport.js"></script>
<script src="<%=resourcePath%>jquery.fileupload.js"></script>
<script src="<%=resourcePath%>jquery.fileupload-ui.js"></script>
<script type="text/javascript">
jQuery(function () {
    'use strict';
    jQuery('#fileupload').fileupload({
    	sequentialUploads: true
    });
});
</script>
</body>
</html>
