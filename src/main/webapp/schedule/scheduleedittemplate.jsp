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

<%

%>
<%@ page
		import="org.apache.commons.lang.StringEscapeUtils,
		        org.oscarehr.schedule.dao.ScheduleTemplateCodeDao,
		        org.oscarehr.schedule.dao.ScheduleTemplateDao,
		        org.oscarehr.schedule.model.ScheduleTemplate,
		        org.oscarehr.schedule.model.ScheduleTemplateCode"
	errorPage="../appointment/errorpage.jsp"%>
<%@ page import="org.oscarehr.schedule.model.ScheduleTemplatePrimaryKey"%>
<%@ page import="org.oscarehr.util.SpringUtils"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>

<jsp:useBean id="myTempBean" class="oscar.ScheduleTemplateBean"	scope="page" />
<%@ page import="oscar.OscarProperties" %>
<%@ page import="oscar.SxmlMisc" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.List" %>
<%
	ScheduleTemplateDao scheduleTemplateDao = SpringUtils.getBean(ScheduleTemplateDao.class);
	ScheduleTemplateCodeDao scheduleTemplateCodeDao = SpringUtils.getBean(ScheduleTemplateCodeDao.class);
	OscarProperties props = OscarProperties.getInstance();
	
	String dboperation = request.getParameter("dboperation");
	String providerId = request.getParameter("providerid");
	String providerName = (request.getParameter("providername") != null) ? request.getParameter("providername") : "";
	String name = (request.getParameter("name") != null) ? request.getParameter("name") : "";
	String summary = request.getParameter("summary");
	String stepStr = request.getParameter("step");
	
	
	boolean isPublicTemplate = ScheduleTemplatePrimaryKey.DODGY_FAKE_PROVIDER_NO_USED_TO_HOLD_PUBLIC_TEMPLATES.equals(providerId);
	String publicPrefix = "P:";
	String prefix = (isPublicTemplate && !name.startsWith(publicPrefix)) ? publicPrefix : "";
	String templateName = prefix + name;
	
	// determine the appointment length (in min).
	int STEP = 15;
	if(stepStr != null && !stepStr.isEmpty()) {
		STEP = Integer.parseInt(stepStr);
	}
	else {
		String propTemplateTime = props.getProperty("template_time");
		if(propTemplateTime != null && !propTemplateTime.trim().isEmpty()) {
			STEP = Integer.parseInt(propTemplateTime);
		}
	}
	
	boolean bEdit = " Edit ".equals(dboperation);

	//save or delete the settings
	int rowsAffected = 0;

	//TODO-legacy move this to an action file
	if(dboperation != null) {
		if (dboperation.equals(" Save ")) {
			// remove old template by same name/provider. it will be replaced
			scheduleTemplateDao.remove(new ScheduleTemplatePrimaryKey(providerId, templateName));
			
			ScheduleTemplate scheduleTemplate = new ScheduleTemplate();
			scheduleTemplate.setId(new ScheduleTemplatePrimaryKey());
			scheduleTemplate.getId().setName(templateName);
			scheduleTemplate.getId().setProviderNo(providerId);
			scheduleTemplate.setSummary(summary);
			scheduleTemplate.setTimecode(SxmlMisc.createDataString(request, "timecode", "_", 300));
			
			scheduleTemplateDao.persist(scheduleTemplate);
		}
		else if (dboperation.equals("Delete")) {
			scheduleTemplateDao.remove(new ScheduleTemplatePrimaryKey(providerId, templateName));
		}
	}
	int nameMaxLength = (isPublicTemplate) ? 20 - publicPrefix.length() : 20;
	
%>

<html:html locale="true">
<head>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
<title><bean:message key="schedule.scheduleedittemplate.title" /></title>

<script language="JavaScript">

	function onLoad() {
		setfocus();
	}
	function setfocus() {
		this.focus();
		document.addtemplatecode.name.focus();
		document.addtemplatecode.name.select();
	}
	function changeGroup(s) {
		var newGroupNo = s.options[s.selectedIndex].value;
		newGroupNo = s.options[s.selectedIndex].value;
		self.location.href = "scheduleedittemplate.jsp?providerid=<%=providerId%>&providername=<%=StringEscapeUtils.escapeJavaScript(providerName)%>&step=" + newGroupNo;
	}
</script>
</head>
<body bgcolor="ivory" bgproperties="fixed" onLoad="onLoad();"
	topmargin="0" leftmargin="0" rightmargin="0">

<table border="0" width="100%">
	<tr>
		<td width="50" bgcolor="#009966">&nbsp;</td>
		<td align="center">

		<form name="addtemplatecode1" method="post"
			action="scheduleedittemplate.jsp">
		<table width="100%" border="0" cellspacing="0" cellpadding="5">
			<input type="hidden" name="dboperation" value="">
			<input type="hidden" name="step" value="">
			<tr bgcolor="#CCFFCC">
				<td nowrap>
				<p><bean:message
					key="schedule.scheduleedittemplate.formProvider" />: <%=providerName%></p>
				</td>
				<td align='right'><select name="name">
					<%
						if (bEdit) {
							for (ScheduleTemplate st : scheduleTemplateDao.findByProviderNoAndName(providerId, name)) {
								myTempBean.setScheduleTemplateBean(st.getId().getProviderNo(), st.getId().getName(), st.getSummary(), st.getTimecode());
							}
						}

						for (ScheduleTemplate st : scheduleTemplateDao.findByProviderNo(providerId)) {
							%>
							<option value="<%=st.getId().getName()%>"><%=st.getId().getName()+" |"+st.getSummary()%></option>
							<%
						} %>
				</select> 
				<input type="hidden" name="providerid" value="<%=providerId%>"> 
				<input type="hidden" name="providerName" value="<%=providerName%>">
				<td align='right'>
					<input type="button" value='<bean:message key="schedule.scheduleedittemplate.btnEdit"/>'
					onclick="document.forms['addtemplatecode1'].dboperation.value=' Edit '; document.forms['addtemplatecode1'].submit();">
				</td>
			</tr>
		</table>
		</form>

		<form name="addtemplatecode2" method="post"
			action="scheduleedittemplate.jsp">
		<table BORDER="0" CELLPADDING="0" CELLSPACING="0" WIDTH="95%">
			<tr>
				<td width="50%" align="right">&nbsp; 
					<select name="step1" onChange="changeGroup(this)">
						<% 
						for(int i=5; i<35; i+=5) {
	      					if(i==25) continue;%>
								<option value="<%=i%>" <%=STEP==i? "selected":""%>><%=i%></option><% 
						} %>
					</select>
					<input type="hidden" name="providerid"
						value="<%=providerId%>"> 
					<input type="hidden" name="providername" value="<%=providerName%>"> 
					<input type="button" value='Go'
						onclick="document.forms['addtemplatecode1'].step.value=document.forms[1].step1.options[document.forms[1].step1.selectedIndex].value; document.forms['addtemplatecode1'].submit();">
				</td>
			</tr>
		</table>
		</form>
		<form name="addtemplatecode" method="post"
			action="scheduleedittemplate.jsp">
		<table width="95%" border="1" cellspacing="0" cellpadding="2"
			bgcolor="silver">
			<tr bgcolor="#FOFOFO" align="center">
				<td colspan=3><font FACE="VERDANA,ARIAL,HELVETICA" SIZE="2"
					color="red"><bean:message
					key="schedule.scheduleedittemplate.msgMainLabel" /></font></td>
			</tr>
			<tr bgcolor='ivory'>
				<td nowrap><bean:message
					key="schedule.scheduleedittemplate.formTemplateName" />:</td>
				<td><input type="text" name="name" size="30" maxlength="<%=nameMaxLength%>" value="<%=bEdit?myTempBean.getName().replaceFirst("^" + publicPrefix, ""):""%>">
				<font size='-2'><bean:message
					key="schedule.scheduleedittemplate.msgLessTwentyChars" /></font></td>
				<td></td>
			</tr>
			<tr bgcolor='ivory'>
				<td><bean:message
					key="schedule.scheduleedittemplate.formSummary" />:</td>
				<td><input type="text" name="summary" size="30" maxlength="30"
					<%=bEdit?("value='"+myTempBean.getSummary()+"'"):"value=''"%>></td>
				<td nowrap><a href=#
					title="	<%
					
					List<ScheduleTemplateCode> stcs = scheduleTemplateCodeDao.findAll();
					Collections.sort(stcs,ScheduleTemplateCode.CodeComparator);
					
					for (ScheduleTemplateCode stc:stcs) {   %>
						<%=String.valueOf(stc.getCode())+" - "+stc.getDescription()%>  <%
					}	%>
             "><bean:message
					key="schedule.scheduleedittemplate.formTemplateCode" /></a></td>
			</tr>
			<tr bgcolor='ivory'>
				<td colspan='3' align='center'>
				<table>
					<%
             int cols=4, rows=6, step=bEdit?myTempBean.getStep():STEP;

             int icols=60/step, n=0;
             for(int i=0; i<rows; i++) {
             %>
					<tr>
						<% for(int j=0; j<cols; j++) { %>
						<td bgcolor='silver'><%=(n<10?"0":"")+n+":00"%></td>
						<%   for(int k=0; k<icols; k++) { %>
						<td><input type="text"
							name="timecode<%=i*(cols*icols)+j*icols+k%>" size="1"
							maxlength="1"
							<%=bEdit?("value='"+myTempBean.getTimecodeCharAt(i*(cols*icols)+j*icols+k)+"'"):"value=''"%>></td>
						<%   }
                n++;
                }%>
					</tr>
					<%} %>
				</table>
				</td>
			</tr>
		</table>


		<table width="100%" border="0" cellspacing="0" cellpadding="2"
			bgcolor="silver">
			<tr bgcolor="#FOFOFO">
				<td><input type="button"
					value='<bean:message key="schedule.scheduleedittemplate.btnDelete"/>'
					onclick="document.forms['addtemplatecode'].dboperation.value='Delete'; document.forms['addtemplatecode'].submit();"></td>
				<td align="right"><input type="hidden" name="providerid"
					value="<%=providerId%>"> <input
					type="hidden" name="providername"
					value="<%=providerName%>"> <input
					type="hidden" name="dboperation" value=""> <input
					type="button"
					value='<bean:message key="schedule.scheduleedittemplate.btnSave"/>'
					onclick="document.forms['addtemplatecode'].dboperation.value=' Save '; document.forms['addtemplatecode'].submit();">
				<input type="button" name="Button"
					value='<bean:message key="global.btnExit"/>'
					onclick="window.close()"></td>
			</tr>
		</table>
		</form>

		</td>
	</tr>
</table>

</body>
</html:html>
