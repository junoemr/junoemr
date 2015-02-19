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
<%@page import="org.oscarehr.common.dao.EncounterTemplateDao"%>
<%@page import="org.oscarehr.common.model.EncounterTemplate"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="oscar.util.*"%>
<%@page import="java.util.*"%>
<%@page import="org.oscarehr.util.SpringUtils"%>
<%@page import="org.json.*"%>
<%@ page language="java" contentType="application/json; charset=UTF-8"%>
<%
String searchterm = request.getParameter("term");
String templateName = request.getParameter("templateName");
EncounterTemplateDao encounterTemplateDao = SpringUtils.getBean(EncounterTemplateDao.class);

// See if we're using autocomplete to search for a term
if(searchterm != null && searchterm != ""){
	List<EncounterTemplate> encounterTemplates = encounterTemplateDao.findLike(searchterm);

	int MaxLen = 20;
	int TruncLen = 17;
	String ellipses = "...";
	JSONArray templateArray = new JSONArray();
	for (int j = 0; j < encounterTemplates.size(); j++)
	{
		EncounterTemplate template = encounterTemplates.get(j);

		String encounterTmp = template.getEncounterTemplateName();
		encounterTmp = oscar.util.StringUtils.maxLenString(encounterTmp, MaxLen, TruncLen, ellipses);
		encounterTmp = StringEscapeUtils.escapeJavaScript(encounterTmp);
		
		JSONObject obj = new JSONObject();
		obj.put("label", encounterTmp.replace("\r\n","\\n").replace("\n","\\n").replace("\"","\\\""));
		obj.put("value", encounterTmp.replace("\r\n","\\n").replace("\n","\\n").replace("\"","\\\""));
		
		templateArray.put(obj);
		
	}
	
	%><%=templateArray.toString()%><%
// Or grabbing a specific template...
}else if(templateName != null && templateName != ""){
	JSONObject obj = new JSONObject();	
	
	EncounterTemplate encounterTemplate = encounterTemplateDao.find(templateName);
	String templateValue = encounterTemplate.getEncounterTemplateValue();
	
	obj.put("templateValue", templateValue);
	%><%=obj.toString()%><%
}
%>