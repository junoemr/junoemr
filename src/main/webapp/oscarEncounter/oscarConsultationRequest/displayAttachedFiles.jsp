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

<%@page
	import="java.util.ArrayList, oscar.dms.*, oscar.oscarLab.ca.on.*, oscar.util.StringUtils"%>
<%@page import="org.oscarehr.util.SessionConstants"%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>

<%
  String demo = request.getParameter("demo") ;
  String requestId = request.getParameter("requestId");
%>
<ul id="attachedList"
	style="background-color: white; padding: 7px 0; list-style-position: outside; list-style-type: lower-roman;">
	<%
            ArrayList privatedocs = new ArrayList();
            privatedocs = EDocUtil.listDocs(demo, requestId, EDocUtil.ATTACHED);
            EDoc curDoc;                                        
            for(int idx = 0; idx < privatedocs.size(); ++idx)
            {                    
                curDoc = (EDoc)privatedocs.get(idx);                                            
        %>
	<li class="doc"><%=StringUtils.maxLenString(curDoc.getDescription(),19,16,"...")%></li>
	<%                                           
            }

                CommonLabResultData labData = new CommonLabResultData();
                ArrayList labs = labData.populateLabResultsData(demo, requestId, CommonLabResultData.ATTACHED);
                LabResultData resData;
                for(int idx = 0; idx < labs.size(); ++idx) 
                {
                    resData = (LabResultData)labs.get(idx);
        %>
	<li class="lab"><%=resData.getDiscipline()+" "+resData.getDateTime()%></li>
	<%
                }
        %>
</ul>
<%
           if( privatedocs.size() == 0 && labs.size() == 0 ) {
        %>
<p id="attachDefault"
	style="background-color: white; text-align: center;"><bean:message
	key="oscarEncounter.oscarConsultationRequest.AttachDoc.Empty" /></p>
<%
           }
         %>
