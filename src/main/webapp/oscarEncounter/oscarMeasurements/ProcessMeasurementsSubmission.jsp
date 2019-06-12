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
  String pasteEncounterNote = request.getParameter("pasteEncounterNote");
  if(session.getValue("user") == null) response.sendRedirect("../../logout.jsp");
%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ page import="java.lang.*,oscar.oscarEncounter.oscarMeasurements.pageUtil.*"%>

<html:html locale="true">
<head>
<title><bean:message key="oscarEncounter.Measurements.msgProcessMeasurementsSubmission" /></title>
<html:base />
</head>

<script language="javascript"> 
function closeWin() {
  if (self.opener != null)
  {
    <% if (pasteEncounterNote == null || !pasteEncounterNote.equals("true")) { %>
      self.opener.location.reload();
    <% }
      else {
    %>
      self.opener.pasteToEncounterNote("<%=request.getAttribute("textOnEncounter")%>");
      self.opener.location.reload();
    <%}%>
  }
  self.close();
}
</script>

<body onload="closeWin();">
<html:errors />
Processing...
<%
//clear so values don't repeat after added to note
session.setAttribute("textOnEncounter", null);
%>

</body>
</html:html>
