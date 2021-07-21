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

<%@ taglib uri="/WEB-INF/rewrite-tag.tld" prefix="rewrite" %>

<%@ page import="org.oscarehr.common.dao.TicklerLinkDao"%>
<%@ page import="org.oscarehr.common.model.Tickler"%>
<%@ page import="org.oscarehr.common.model.TicklerLink" %>
<%@ page import="org.oscarehr.managers.TicklerManager" %>
<%@ page import="org.oscarehr.util.LoggedInInfo" %>
<%@ page import="org.oscarehr.util.MiscUtils" %>
<%@ page import="org.oscarehr.util.SpringUtils"%>
<%@ page import="oscar.util.UtilDateUtilities" %>
<%@ page import="org.oscarehr.encounterNote.service.TicklerNoteService" %>

<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%
    String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
    boolean authed=true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_tickler" rights="w" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect("../securityError.jsp?type=_tickler");%>
</security:oscarSec>
<%
	if(!authed) {
		return;
	}
%>

<%
	TicklerManager ticklerManager = SpringUtils.getBean(TicklerManager.class);
   	LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);
%>

<%!
	// create new tickler.
	void AddTickler(String demographic_no,
	                String docpriority,
	                String docType,
	                String docId,
	                String docassigned,
	                String doccreator,
	                String message,
	                String docdate,
	                Boolean writeEncounterNote,
	                LoggedInInfo loggedInInfo,
	                TicklerManager ticklerManager)
	{
		Tickler tickler = new Tickler();
		Integer demographicNo = Integer.parseInt(demographic_no);
		tickler.setDemographicNo(demographicNo);
		tickler.setUpdateDate(new java.util.Date());
		if (docpriority != null && docpriority.equalsIgnoreCase("High"))
		{
			tickler.setPriority(Tickler.PRIORITY.High);
		}
		if (docpriority != null && docpriority.equalsIgnoreCase("Low"))
		{
			tickler.setPriority(Tickler.PRIORITY.Low);
		}
		tickler.setTaskAssignedTo(docassigned);
		tickler.setCreator(doccreator);
		tickler.setMessage(message);
		tickler.setServiceDate(UtilDateUtilities.StringToDate(docdate));


		ticklerManager.addTickler(loggedInInfo, tickler);

		if(writeEncounterNote)
		{
			TicklerNoteService ticklerNoteService = SpringUtils.getBean(TicklerNoteService.class);
			ticklerNoteService.saveTicklerNote(message, tickler, doccreator, demographicNo);
		}

		if (docType != null && docId != null && !docType.trim().equals("") && !docId.trim().equals("") && !docId.equalsIgnoreCase("null"))
		{

			int ticklerNo = tickler.getId();
			if (ticklerNo > 0)
			{
				try
				{
					TicklerLink tLink = new TicklerLink();
					tLink.setTableId(docId);
					tLink.setTableName(docType);
					tLink.setTickler(tickler);
					TicklerLinkDao ticklerLinkDao = (TicklerLinkDao) SpringUtils.getBean("ticklerLinkDao");
					ticklerLinkDao.save(tLink);
				} catch (Exception e)
				{
					MiscUtils.getLogger().error("No link with this tickler", e);
				}
			}
		}
	}
%>

<%
	String demographic_no = request.getParameter("demographic_no");
	String doccreator = request.getParameter("user_no");
	String docdate = request.getParameter("xml_appointment_date");
	String textArea = request.getParameter("textarea");
	String docpriority = request.getParameter("priority");
	String docassigned = request.getParameter("task_assigned_to");
	Boolean writeEncounterNote = Boolean.parseBoolean(request.getParameter("writeEncounterNote"));
	boolean multiDemo = "true".equalsIgnoreCase(request.getParameter("multiple_demographics"));

	String docType = request.getParameter("docType");

	String docId = request.getParameter("docId");
	if(docId == null)
	{
		docId = loggedInInfo.getLoggedInProvider().getProviderNo();
	}

	if(multiDemo)
	{
		String[] demographicNumbers = demographic_no.split("&");
		for(String demoNo : demographicNumbers)
		{
			AddTickler(demoNo, docpriority, docType, docId, docassigned, doccreator, textArea, docdate, writeEncounterNote, loggedInInfo, ticklerManager);
		}
	}
	else
	{
		AddTickler(demographic_no, docpriority, docType, docId, docassigned, doccreator, textArea, docdate, writeEncounterNote, loggedInInfo, ticklerManager);
	}

	String parentAjaxId = request.getParameter("parentAjaxId");
	String updateParent = request.getParameter("updateParent");

%>
<script LANGUAGE="JavaScript">

      var parentId = "<%=parentAjaxId%>";
      var updateParent = <%=updateParent%>;
      var demo = "<%=demographic_no%>";
      var Url = window.opener.URLs;

      /*because the url for demomaintickler is truncated by the delete action, we need
        to reconstruct it if necessary
      */
      if( parentId != "" && updateParent == true && !window.opener.closed ) {
        var ref = window.opener.location.href;
        if( ref.indexOf("?") > -1 && ref.indexOf("updateParent") == -1 )
            ref = ref + "&updateParent=true";
        else if( ref.indexOf("?") == -1 )
            ref = ref + "?demoview=" + demo + "&parentAjaxId=" + parentId + "&updateParent=true";

        window.opener.location = ref;
      }
      else if( parentId != "" && !window.opener.closed ) {
        if (window.opener.document.forms['encForm']) { window.opener.document.forms['encForm'].elements['reloadDiv'].value=parentId; }
        window.opener.updateNeeded = true;
      }
      else if( updateParent == true && !window.opener.closed )
        window.opener.location.reload();

      self.close();
</script>
