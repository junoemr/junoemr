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

<%@ page import="org.oscarehr.util.LoggedInInfo"%>
<%@ page import="java.io.Serializable"%>
<%@ page import="org.w3c.dom.Document"%>
<%@ page import="org.oscarehr.caisi_integrator.ws.CachedDemographicLabResult"%>
<%@ page import="oscar.oscarLab.ca.all.web.LabDisplayHelper"%>
<%@ page import="org.oscarehr.util.MiscUtils" %>
<%@ page import="org.oscarehr.util.DateMapComparator" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Collections" %>
<%@ page import="oscar.oscarDemographic.data.DemographicData" %>
<%@ page import="oscar.oscarLab.ca.on.CommonLabTestValues" %>
<%@ page import="oscar.oscarEncounter.oscarMeasurements.pageUtil.MeasurementGraphAction2" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.List" %>
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

	List<Map<String, Serializable>> list = new ArrayList<Map<String, Serializable>>();

	DateMapComparator comparator = new DateMapComparator("collDate");

	int demographicNoAsNumber = 0;

	try
	{
		demographicNoAsNumber = Integer.parseInt(demographicNo);
	}
	catch (NumberFormatException exception)
	{
		MiscUtils.getLogger().warn("Failed to parse '" + demographicNo + "' as an integer");
	}

	if (!(demographicNo == null || demographicNo.equals("null") || demographicNoAsNumber == 0))
	{
		if (remoteFacilityIdString == null)
		{
			list = CommonLabTestValues.findValuesForTest(labType, demographicNoAsNumber, testName, identifier);
		}
		else
		{
			CachedDemographicLabResult remoteLab = LabDisplayHelper.getRemoteLab(loggedInInfo, Integer.parseInt(remoteFacilityIdString), remoteLabKey,Integer.parseInt(demographicNo));
			Document labContentsAsXml = LabDisplayHelper.getXmlDocument(remoteLab);
			HashMap<String, ArrayList<Map<String, Serializable>>> mapOfTestValues = LabDisplayHelper.getMapOfTestValues(labContentsAsXml);
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

	// Scriptlets are dumb and compute first before the invalid demographic logic actually triggers
	// This would result in a 500 error if the demographic number given is invalid even with error checking
	// Prefer pre-pulling desired demographic attrs before displaying
	// instead of using a ternary every time we want to display a demographic field
	String lastName = "";
	String firstName = "";
	String sex = "";
	String dateOfBirth = "";
	String age = "";

	if (demographic != null)
	{
		lastName = demographic.getLastName();
		firstName = demographic.getFirstName();
		sex = demographic.getSex();
		dateOfBirth = DemographicData.getDob(demographic, "-");
		age = demographic.getAge();
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

<script type="text/javascript" language="JavaScript">

  <%
	  if(demographic == null || demographicNoAsNumber == 0)
	  {
  %>
	  alert("The demographic number is not valid");
	  window.close();

    <%
	  }
	%>
</script>

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
														<%=lastName%>,<%=firstName%>
													</div>
													</td>
													<td colspan="2" nowrap>
													<div class="FieldData">
														<strong><bean:message key="oscarMDS.segmentDisplay.formSex" />: </strong>
														<%=sex%>
													</div>
													</td>
												</tr>
												<tr>
													<td colspan="2" nowrap>
													<div class="FieldData">
														<strong><bean:message key="oscarMDS.segmentDisplay.formDateBirth" />: </strong>
														<%=dateOfBirth%>
													</div>
													</td>
													<td colspan="2" nowrap>
													<div class="FieldData">
														<strong><bean:message key="oscarMDS.segmentDisplay.formAge" />: </strong>
														<%=age%>
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
					for (Map<String, Serializable> hashMap : list)
					{
						String lineClass = "NormalRes";
						if (hashMap.get("abn") != null && hashMap.get("abn").equals("A"))
						{
							lineClass = "AbnormalRes";
						}

						// Try casting result and referenceRange to double - if it works, we can graph
						if (canGraph && hashMap.get("result") != null && hashMap.get("range") != null)
						{
							String result = (String)hashMap.get("result");
							try
							{
								result = result.replaceAll("<|>|=", "");
								Double.parseDouble(result);
							}
							catch (NumberFormatException ex)
							{
								MiscUtils.getLogger().warn("Cannot graph '" + result + "':" + ex);
								canGraph = false;
							}

							String refRange = (String)hashMap.get("range");
							String units = (String)hashMap.get("units");

							double[] possibleMeasurements = MeasurementGraphAction2.getParameters(refRange, units);
							if (possibleMeasurements == null)
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
						   onclick="window.location = 'labValuesGraph.jsp?demographic_no=<%=demographicNo%>&labType=<%=labType%>&identifier=<%=URLEncoder.encode(identifier.replaceAll("&","%26"),"UTF-8")%>&testName=<%=testName%>';"/>
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
