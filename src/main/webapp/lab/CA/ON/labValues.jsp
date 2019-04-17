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
      String roleName$ = session.getAttribute("userrole") + "," + session.getAttribute("user");
	  boolean authed=true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_lab" rights="r" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect("../../../securityError.jsp?type=_lab");%>
</security:oscarSec>
<%
	if(!authed)
	{
		return;
	}
%>

<%@page import="org.oscarehr.util.LoggedInInfo"%>
<%@page import="java.io.Serializable"%>
<%@page import="org.w3c.dom.Document"%>
<%@page import="org.oscarehr.caisi_integrator.ws.CachedDemographicLabResult"%>
<%@page import="oscar.oscarLab.ca.all.web.LabDisplayHelper"%>
<%@page import="java.util.*"%>
<%@page import="oscar.oscarLab.ca.on.*"%>
<%@page import="oscar.oscarDemographic.data.*"%>
<%@page import="org.oscarehr.util.MiscUtils" %>
<%@page import="org.oscarehr.util.DateMapComparator" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%
	String labType = request.getParameter("labType");
	String demographicNo = request.getParameter("demo");
	String testName = request.getParameter("testName");
	String identifier = request.getParameter("identifier");
	String remoteFacilityIdString = request.getParameter("remoteFacilityId");
	String remoteLabKey = request.getParameter("remoteLabKey");

	if (identifier == null)
	{
		identifier = "NULL";
	}
	LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);

	DemographicData dData = new DemographicData();
	org.oscarehr.common.model.Demographic demographic =  dData.getDemographic(loggedInInfo, demographicNo);

	ArrayList list = null;

	DateMapComparator comparator = new DateMapComparator("collDate");
	if (!(demographicNo == null || demographicNo.equals("null")))
	{
		if(remoteFacilityIdString==null)
		{
			list = CommonLabTestValues.findValuesForTest(labType, Integer.valueOf(demographicNo), testName, identifier);
		}
		else
		{
			CachedDemographicLabResult remoteLab = LabDisplayHelper.getRemoteLab(loggedInInfo, Integer.parseInt(remoteFacilityIdString), remoteLabKey,Integer.parseInt(demographicNo));
			Document labContentsAsXml = LabDisplayHelper.getXmlDocument(remoteLab);
			HashMap<String, ArrayList<Map<String, Serializable>>> mapOfTestValues=LabDisplayHelper.getMapOfTestValues(labContentsAsXml);
			list = mapOfTestValues.get(identifier);
		}

		// Attempt to sort the lab result dates
		try
		{
			Collections.sort(list, Collections.reverseOrder(comparator));
		} catch (RuntimeException e) {
			MiscUtils.getLogger().error("Cannot sort lab dates: " + e);
			MiscUtils.getLogger().error("Returning unsorted list.");
		}
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
	<html:base />
	<title><%=""%>, <%=""%>
		<bean:message key="oscarMDS.segmentDisplay.title" />
	</title>
	<link rel="stylesheet" type="text/css" href="../../../share/css/OscarStandardLayout.css">
	<link rel="stylesheet" type="text/css" media="all" href="../../../share/css/extractedFromPages.css"/>
</head>
<body>

  <%
	  if(demographic == null)
	  {
  %>
  <script type="text/javascript" language="JavaScript">
	  alert("The demographic number is not valid");
	  window.close();
  </script>

    <%
	  }
	%>
<form name="acknowledgeForm" method="post" action="../../../oscarMDS/UpdateStatus.do">

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top">
		<table width="100%" border="1" cellspacing="0" cellpadding="3" bgcolor="#9999CC">

			<tr>
				<td width="66%" align="center" class="Cell">
					<div class="Field2">
						<bean:message key="oscarMDS.segmentDisplay.formDetailResults" />
					</div>
				</td>
			</tr>

			<tr>
				<td bgcolor="white" valign="top">
					<table border="0" cellpadding="2" cellspacing="0" width="100%">
						<tr valign="top">
							<td valign="top" width="33%" align="left">
								<table width="100%" border="0" cellpadding="2" cellspacing="0">
									<tr>
										<td valign="top" align="left">
											<table border="0" cellpadding="3" cellspacing="0" width="50%">
												<tr>
													<td colspan="2" nowrap>
													<div class="FieldData">
														<strong><bean:message key="oscarMDS.segmentDisplay.formPatientName" />:</strong>
														<%=demographic.getLastName()%>,<%=demographic.getFirstName()%>
													</div>
													</td>
													<td colspan="2" nowrap>
													<div class="FieldData">
														<strong><bean:message key="oscarMDS.segmentDisplay.formSex" />: </strong>
														<%=demographic.getSex()%>
													</div>
													</td>
												</tr>
												<tr>
													<td colspan="2" nowrap>
													<div class="FieldData">
														<strong><bean:message key="oscarMDS.segmentDisplay.formDateBirth" />: </strong>
														<%=DemographicData.getDob(demographic,"-")%>
													</div>
													</td>
													<td colspan="2" nowrap>
													<div class="FieldData">
														<strong><bean:message key="oscarMDS.segmentDisplay.formAge" />: </strong>
														<%=demographic.getAge()%>
													</div>
													</td>
												</tr>
											</table>
										</td>
										<td width="33%" valign="top"></td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
				</td>
			</tr>

			<tr>
				<td align="center" bgcolor="white" colspan="2">
					<table width="100%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center" bgcolor="white">
							<div class="FieldData">
							</div>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>

		<table style="page-break-inside: avoid;" bgcolor="#003399" border="0"
			cellpadding="0" cellspacing="0" width="100%">
			<tr>
				<td colspan="4" height="7">&nbsp;</td>
			</tr>
			<tr>
				<td colspan="4" height="7">&nbsp;</td>
			</tr>

		</table>

		<table width="100%" border="0" cellspacing="0" cellpadding="2" bgcolor="#CCCCFF" name="tblDiscs" id="tblDiscs">
			<tr class="Field2">
				<td width="25%" align="center" valign="bottom" class="Cell">
					<bean:message key="oscarMDS.segmentDisplay.formTestName" />
				</td>
				<td width="15%" align="center" valign="bottom" class="Cell">
					<bean:message key="oscarMDS.segmentDisplay.formResult" />
				</td>
				<td width="5%" align="center" valign="bottom" class="Cell">
					<bean:message key="oscarMDS.segmentDisplay.formAbn" />
				</td>
				<td width="15%" align="center" valign="bottom" class="Cell">
					<bean:message key="oscarMDS.segmentDisplay.formReferenceRange" />
				</td>
				<td width="10%" align="center" valign="bottom" class="Cell">
					<bean:message key="oscarMDS.segmentDisplay.formUnits" /></td>
				<td width="15%" align="center" valign="bottom" class="Cell">
					<bean:message key="oscarMDS.segmentDisplay.formDateTimeCompleted" />
				</td>
			</tr>
			<%
				boolean canGraph = true;
				if (list != null)
				{
					for (int i = 0 ;  i < list.size(); i++)
					{
						Map hashMap = (Map) list.get(i);
						String lineClass = "NormalRes";
						if (hashMap.get("abn") != null && hashMap.get("abn").equals("A"))
						{
							lineClass = "AbnormalRes";
						}

						// Try casting result and referenceRange to double - if it works, we can graph
						if (hashMap.get("result") != null && hashMap.get("range") != null)
						{
							try
							{
								Double.parseDouble((String)hashMap.get("result"));
								Double.parseDouble((String)hashMap.get("range"));
							}
							catch (NumberFormatException ex)
							{
								canGraph = false;
							}
						}
			%>

			<tr class="<%=lineClass%>">
				<td valign="top" align="left"><%=hashMap.get("testName") %></td>
				<td align="center"><%=hashMap.get("result") %></td>
				<td align="center"><%=hashMap.get("abn") %></td>
				<td align="center"><%=hashMap.get("range")%></td>
				<td align="center"><%=hashMap.get("units") %></td>
				<td align="center"><%=hashMap.get("collDate")%></td>
			</tr>

			<%
					}
				}
			%>

		</table>

		<table width="100%" border="0" cellspacing="0" cellpadding="3"
			class="MainTableBottomRowRightColumn" bgcolor="#003399">
			<tr>
				<td align="left"><input type="button"
					value=" <bean:message key="global.btnClose"/> "
					onClick="window.close()"> <input type="button"
					value=" <bean:message key="global.btnPrint"/> "
					onClick="window.print()">
					<%
						if (canGraph)
						{
					%>
					<input type="button"
						   value="Plot"
						   onclick="window.location = 'labValuesGraph.jsp?demographic_no=<%=demographicNo%>&labType=<%=labType%>&identifier=<%=identifier%>&testName=<%=testName%>';"/>
					<%
						}
						else
						{
					%>
					<input type="button"
						   value="Plot"
						   title="Can't make a graph, either the range or the result can't be interpreted properly."
						   disabled/>
					<%
						}
					%>
				</td>
			</tr>
		</table>
		</td>
	</tr>
</table>
</form>
</body>
</html>
