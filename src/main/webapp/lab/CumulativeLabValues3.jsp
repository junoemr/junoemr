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

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/oscar-tag.tld" prefix="oscar"%>
<%@ taglib uri="/WEB-INF/rewrite-tag.tld" prefix="rewrite"%>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%@ page import="oscar.oscarLab.ca.all.util.LabValuesByReverseDateComparator" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="oscar.oscarLab.ca.on.CommonLabTestValues" %>
<%@ page import="java.util.Date" %>
<%@ page import="oscar.util.ConversionUtils" %>
<%@ page import="oscar.util.StringUtils" %>
<%@ page import="oscar.oscarLab.ca.all.util.LabGridDisplay" %>

<%
      String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
	  boolean authed=true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_lab" rights="r" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect("../../securityError.jsp?type=_lab");%>
</security:oscarSec>
<%
	if(!authed) {
		return;
	}

	String demographic_no = request.getParameter("demographic_no");
	Map<String, List<LabGridDisplay>>  uniqueLabs = CommonLabTestValues.getUniqueLabsForPatients(demographic_no);
	List<Map<String, String>> dateList = CommonLabTestValues.buildDateList(uniqueLabs);
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html:html locale="true">
	<head>
		<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
		<title>Cumulative Lab 3</title>
		<!--I18n-->
		<link rel="stylesheet" type="text/css" href="../share/css/OscarStandardLayout.css" />
		<script type="text/javascript" src="../share/javascript/Oscar.js"></script>
		<script type="text/javascript" src="../share/javascript/prototype.js"></script>
		<script type="text/javascript" src="../share/javascript/boxover.js"></script>
		<link rel="stylesheet" type="text/css" media="all" href="../share/css/extractedFromPages.css"  />
		<link rel="stylesheet" type="text/css" href="../share/css/niftyCorners.css" />
		<link rel="stylesheet" type="text/css" href="../share/css/niftyPrint.css" media="print" />
		<script type="text/javascript" src="../share/javascript/nifty.js"></script>
		<script type="text/javascript">
			window.onload=function()
			{
				if(!NiftyCheck())
				{
					return;
				}

				Rounded("div.headPrevention","all","#CCF","#efeadc","small border blue");
				Rounded("div.preventionProcedure","all","transparent","#F0F0E7","small border #999");

				Rounded("div.leftBox","top","transparent","#CCCCFF","small border #ccccff");
				Rounded("div.leftBox","bottom","transparent","#EEEEFF","small border #ccccff");
			};

			function reportWindow(page)
			{
				windowprops="height=660, width=960, location=no, scrollbars=yes, menubars=no, toolbars=no, resizable=yes, top=0, left=0";
				var popup = window.open(page, "labreport", windowprops);
				popup.focus();
			}
		</script>
	</head>

	<body class="BodyStyle">
	<table class="MainTable" id="scrollNumber1">
		<tr class="MainTableTopRow">
			<td class="MainTableTopRowLeftColumn">lab</td>
			<td class="MainTableTopRowRightColumn">
				<table class="TopStatusBar">
					<tr>
						<td><oscar:nameage demographicNo="<%=demographic_no%>" /></td>
						<td>&nbsp;</td>
						<td style="text-align: right"><oscar:help keywords="lab" key="app.top1"/>
							| <a href="javascript:popupStart(300,400,'About.jsp')"><bean:message key="global.about" /></a>
							| <a href="javascript:popupStart(300,400,'License.jsp')"><bean:message key="global.license" /></a>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td class="MainTableLeftColumn" valign="top"></td>
			<td valign="top" class="MainTableRightColumn">
				<table class="cumlatable">
					<tr>
						<th>&nbsp;</th>
						<th>Latest Value</th>
						<th>Last Done</th>
				<!-- Dates start here. Need to have all the dates of the different labs -->
				<%
					// use a custom comparator to compare the HashMaps in the array
					// This has to be here unfortunately, otherwise we can't guarantee order
					LabValuesByReverseDateComparator comp = new LabValuesByReverseDateComparator();
                    dateList.sort(comp);
                    for (Map<String, String> dateIdHash : dateList)
                    {
                        String dateString = dateIdHash.get("date");
                        Date labDate = ConversionUtils.fromDateString(dateString, ConversionUtils.DEFAULT_DATE_PATTERN);
                        String lab_no = dateIdHash.get("id");
                %>
						<th>
							<a href="javascript:reportWindow('../lab/CA/ALL/labDisplay.jsp?segmentID=<%=lab_no%>&providerNo=<%= session.getAttribute("user") %>')">
						<%=ConversionUtils.toDateString(labDate, "dd-MMM yy")%>
							</a>
						</th>
					<%}%>
					</tr>
			<%
				for (String testName : uniqueLabs.keySet())
				{
					List<LabGridDisplay> labsToDisplay = uniqueLabs.get(testName);
					//preserve spaces in the test names
					testName = testName.replaceAll("\\s", "&#160;");

					LabGridDisplay latestLab = labsToDisplay.get(0);
					String abnormal = latestLab.getAbnormal();
			%>
					<tr>
						<td><%=testName%></td>
						<td class="<%=abnormal%>"><%=StringUtils.maxLenString(latestLab.getResult(), 9, 8, "...")%></td>
						<td><%=latestLab.getDateObserved()%></td>
					<%
						// display all of values from all the labs for the given test
						for (Map<String, String> dateIdHash : dateList)
						{
							String dateString = dateIdHash.get("date");
							String labNo = dateIdHash.get("id");
							String testAbnormal = "";
							String testResult = "";
							// Loop over all results for this test again to find the one that matches up with date & labno
							for (LabGridDisplay labGridDisplay : labsToDisplay)
							{
								if (!dateString.equals(labGridDisplay.getDateObserved()) || !labNo.equals(labGridDisplay.getLabId()))
								{
									continue;
								}
								testAbnormal = labGridDisplay.getAbnormal();
								testResult = labGridDisplay.getResult();
							}
					%>
						<td class="<%= testAbnormal %>"><%=StringUtils.maxLenString(testResult, 9, 8, "...")%></td>
						<%

						}
				}
						%>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td class="MainTableBottomRowLeftColumn">&nbsp;</td>
			<td class="MainTableBottomRowRightColumn" valign="top">&nbsp;</td>
		</tr>
	</table>
	</body>
</html:html>
