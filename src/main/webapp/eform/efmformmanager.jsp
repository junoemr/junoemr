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
<!DOCTYPE html>
<%@ page import="oscar.eform.EFormUtil, java.util.ArrayList, java.util.HashMap"%>
<%@ page import="org.apache.commons.text.StringEscapeUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%
	String orderByRequest = request.getParameter("orderby");
	String orderBy = "";
	if(orderByRequest == null) orderBy = EFormUtil.DATE;
	else if(orderByRequest.equals("form_subject")) orderBy = EFormUtil.SUBJECT;
	else if(orderByRequest.equals("form_name")) orderBy = EFormUtil.NAME;
	else if(orderByRequest.equals("file_name")) orderBy = EFormUtil.FILE_NAME;
%>
<html:html locale="true">
<head>

<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>

</head>
<script language="javascript">
  function checkFormAndDisable(){
    if(document.forms[0].formHtml.value==""){
      alert("<bean:message key="eform.uploadhtml.msgFileMissing"/>");
    } else {
      document.forms[0].subm.value = "<bean:message key="eform.uploadimages.processing"/>";
      document.forms[0].subm.disabled = true;
      document.forms[0].submit();
    }
  }

  function newWindow(url, id) {
        Popup = window.open(url,id,'toolbar=no,location=no,status=yes,menubar=no, scrollbars=yes,resizable=yes,width=900,height=600,left=200,top=0');
  }
  function confirmNDelete(url) {
    if (confirm("<bean:message key="eform.uploadhtml.confirmDelete"/>")) {
        document.location = url;
    }
  }

  var normalStyle = "eformInputHeading"
  var activeStyle = "eformInputHeading eformInputHeadingActive"
  function closeInputs() {
      document.getElementById("uploadDiv").style.display = 'none';
      document.getElementById("importDiv").style.display = 'none';
      document.getElementById("uploadHeading").className = normalStyle;
      document.getElementById("importHeading").className = normalStyle;
  }

  function openUpload() {
      closeInputs();
      document.getElementById("uploadHeading").className = activeStyle;
      document.getElementById("uploadDiv").style.display = 'block';
  }

  function openImport() {
      closeInputs();
      document.getElementById("importHeading").className = activeStyle;
      document.getElementById("importDiv").style.display = 'block';
  }
  
  function openDownload() {
      closeInputs();
      document.getElementById("downloadHeading").className = activeStyle;
      document.getElementById("downloadDiv").style.display = 'block';
  }

  function doOnLoad() {
    <%String input = request.getParameter("input");
    if (input == null) input = (String) request.getAttribute("input");
    if (input != null && input.equals("import")) {%>
    openImport();
    <%}%>
  }

$(function ()  { 

	  $("[rel=popover]").popover(); 

});

</script>

<body>


<%@ include file="efmTopNav.jspf"%>
<%@ include file="efmShowError.jspf"%>

<h3 style='display:inline;padding-right:10px'><bean:message key="eform.uploadhtml.msgLibrary" /></h3> <a href="<%= request.getContextPath() %>/eform/efmformmanagerdeleted.jsp" class="contentLink">View Deleted<!--<bean:message key="eform.uploadhtml.btnDeleted" />--> </a> 

    
<ul class="nav nav-pills" id="eformOptions">
<li><a href="#upload" >Upload</a></li>
<li><a href="#import">Import</a></li>
<li><a href="#download">Download</a></li>
</ul>
 
<div class="tab-content">
<div class="tab-pane" id="upload">
<div class="well">

<iframe id="uploadFrame" name="uploadFrame" frameborder="0" width="100%" height="auto" scrolling="no" src="<%=request.getContextPath()%>/eform/partials/upload.jsp"></iframe>

</div>
</div>

<div class="tab-pane" id="import">
<div class="well">
                        
<iframe id="importFrame" name="importFrame" frameborder="0" width="100%" height="auto" src="<%=request.getContextPath()%>/eform/partials/import.jsp"></iframe>

</div>
</div>

<div class="tab-pane" id="download">
<div class="well">
                        
<iframe id="downloadFrame" name="downloadFrame" onload="downloadFrameLoaded()" frameborder="0" width="100%" height="auto" scrolling="no" src="<%=request.getContextPath()%>/eform/partials/download.jsp"></iframe>

</div>
</div>

</div><!-- tab content eformOptions -->
    
<h4><bean:message key="eform.uploadhtml.msgCurrentResources" /></h4>

<table class="table table-condensed table-striped" id="eformTbl">
<thead>
    <tr>
        <th><!--<a href="<%= request.getContextPath() %>/eform/efmformmanager.jsp?orderby=file_name" class="contentLink"class="contentLink"><bean:message key="eform.uploadhtml.btnFile" /></a>--></th>

        <th style="width:15%"><a href="<%= request.getContextPath() %>/eform/efmformmanager.jsp?orderby=form_name" class="contentLink"><bean:message key="eform.uploadhtml.btnFormName" /></a></th>
        <th style="width:20%"><a href="<%= request.getContextPath() %>/eform/efmformmanager.jsp?orderby=form_subject" class="contentLink"><bean:message key="eform.uploadhtml.btnSubject" /></a></th>

        <th><a href="<%= request.getContextPath() %>/eform/efmformmanager.jsp?" class="contentLink"><bean:message key="eform.uploadhtml.btnDate" /></a></th>
        <th><bean:message key="eform.uploadhtml.btnTime" /></th>
        <th><bean:message key="eform.uploadhtml.btnRoleType"/></th>
        <th style="width:15%"><bean:message key="eform.uploadhtml.msgAction" /></th>
        <th style="width:10%">Send <i class="icon-question-sign" rel="popover" data-html="true" data-placement="bottom" data-animation="true" data-trigger="hover" data-content="This is an online utility designed for users to easily share eForms to the OSCAR community." data-original-title="Send eForm to Emporium"></i></th>
    </tr>
</thead>

<tbody>
    <%
    ArrayList<HashMap<String, ? extends Object>> eForms = EFormUtil.listEForms(orderBy, EFormUtil.CURRENT);
  for (int i=0; i<eForms.size(); i++) {
	  HashMap<String, ? extends Object> curForm = eForms.get(i);

	  String roleType = (String)curForm.get("roleType");

	  String escapedFileName = StringEscapeUtils.escapeHtml4(StringUtils.trimToEmpty((String) curForm.get("formFileName")));
	  String escapedFormName = StringEscapeUtils.escapeHtml4((String) curForm.get("formName"));
	  String escapedFormSubject = StringEscapeUtils.escapeHtml4(StringUtils.trimToEmpty((String) curForm.get("formSubject")));

	  // Don't show roletype if it's null
	  if(roleType == null)
	  {
	  	roleType = "";
	  }
%>
    <tr>
        <td><i class="icon-file" title="<%=escapedFileName%>"></i></td>
        <td title="<%=escapedFormName%>">
        <a href="#" onclick="newWindow('<%= request.getContextPath() %>/eform/efmshowform_data.jsp?fid=<%=curForm.get("fid")%>', '<%="Form"+i%>'); return false;"><%=escapedFormName%></a>
        </td>
        <td><%=escapedFormSubject%> </td>
        <td align='center' ><%=curForm.get("formDate")%></td>
        <td align='center' ><%=curForm.get("formTime")%></td>
        <td align='center' ><%=roleType%></td>
        <td align='center'>
                    
                
<a href="<%= request.getContextPath() %>/eform/efmformmanageredit.jsp?fid=<%= curForm.get("fid")%>" class="contentLink" style="padding-right:6px" title='<bean:message key="eform.uploadhtml.editform" /><%=escapedFormName%>'><i class="icon-pencil" title="<bean:message key="eform.uploadhtml.editform" />"></i></a>

                 
<a href='<%= request.getContextPath() %>/eform/manageEForm.do?method=exportEForm&fid=<%=curForm.get("fid")%>' style="padding-right:6px" title='<bean:message key="eform.uploadhtml.btnExport" /> <%=escapedFormName%>' ><i class="icon-download-alt" title="<bean:message key="eform.uploadhtml.btnExport" />"></i></a>
                    

<a href='<%= request.getContextPath() %>/eform/delEForm.do?fid=<%=curForm.get("fid")%>' style="padding-right:6px" title='<bean:message key="eform.uploadhtml.btnDelete" /> <%=escapedFormName%>' class="contentLink"><i class="icon-trash" title="<bean:message key="eform.uploadhtml.btnDelete" />"></i></a>
		</td>			
		
		<td align='center'><a href="<%= request.getContextPath() %>/eform/efmSendform.jsp?fid=<%=curForm.get("fid")%>" title='Send <%=escapedFormName%> to Emporium' class="contentLink"><i class="icon-share" title="send"></i></a>
					
    </tr>
    <% } %>
</tbody>
</table>

<%@ include file="efmFooter.jspf"%>

<script>
    $('#eformOptions a').click(function (e) {
    e.preventDefault();
    if(this.href.indexOf('download') != -1) {
    	document.getElementById("downloadFrame").src = document.getElementById("downloadFrame").src;
    }
    $(this).tab('show');
    });

	function downloadFrameLoaded() {
		var iFrame = document.getElementById('downloadFrame');
		if(iFrame) {
            iFrame.height = "";
            iFrame.height = iFrame.contentWindow.document.body.scrollHeight + "px";
      	}   
  	}
  
registerFormSubmit('eformImportForm', 'dynamic-content');

$('#eformTbl').dataTable({
	"bPaginate": false,
	"aoColumnDefs": [{"bSortable":false, "aTargets":[0]}]
});
</script>
</body>
</html:html>
