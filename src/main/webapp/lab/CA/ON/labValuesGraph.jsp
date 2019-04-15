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
if(!authed) {
	return;
}
%>

<%@page import="org.oscarehr.util.LoggedInInfo"%>
<%@ page import="oscar.oscarRx.data.RxPrescriptionData" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="oscar.oscarDemographic.data.DemographicData" %>
<%@ page import="org.oscarehr.common.model.Demographic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%
    String labType = request.getParameter("labType");
    String demographicNo = request.getParameter("demographic_no");
    String testName = request.getParameter("testName");
    String identifier = request.getParameter("identifier");
    if (identifier == null)
    {
        identifier = "NULL";
    }

    DemographicData dData = new DemographicData();

    Demographic demographic = dData.getDemographic(LoggedInInfo.getLoggedInInfoFromSession(request), demographicNo);

    StringBuilder buffer = new StringBuilder();
    Hashtable<String, String> drugTable = new Hashtable<String, String>();
    String drugForGraph = "";
    if (request.getParameterValues("drug") != null)
    {
        String[] drugs = request.getParameterValues("drug");
        for (String drug : drugs)
        {
            String drugParam = "&drug=" + drug;
            buffer.append(drugParam);
            drugTable.put(drug, "drug");
        }
        drugForGraph = buffer.toString();
    }
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
    <head>
        <script type="text/javascript" src="<%= request.getContextPath()%>/js/global.js"></script>
        <html:base />
        <title><bean:message key="oscarMDS.segmentDisplay.title" /></title>
        <link rel="stylesheet" type="text/css" href="../../../share/css/OscarStandardLayout.css">
        <link rel="stylesheet" type="text/css" media="all" href="../../../share/css/extractedFromPages.css"  />
    </head>
    <body>
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
                                                                    <strong><bean:message key="oscarMDS.segmentDisplay.formPatientName" />: </strong>
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
                                                                    <strong><bean:message key="oscarMDS.segmentDisplay.formAge" />: </strong><%=demographic.getAge()%>
                                                                </div>
                                                            </td>
                                                        </tr>


                                                    </table>
                                                </td>
                                                <td width="33%" valign="top">

                                                </td>
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
                <table style="page-break-inside: avoid;" bgcolor="#003399" border="0" cellpadding="0" cellspacing="0" width="100%">
                    <tr>
                        <td colspan="4" height="7">&nbsp;</td>
                    </tr>
                    <tr>
                        <td colspan="4" height="7">&nbsp;</td>
                    </tr>
                </table>
                <img alt="Graph" src="../../../oscarEncounter/GraphMeasurements.do?method=actualLab&demographic_no=<%=demographicNo%>&labType=<%=labType%>&identifier=<%=identifier%>&testName=<%=testName%><%=drugForGraph%>"/>

                <table width="100%" border="0" cellspacing="0" cellpadding="3" class="MainTableBottomRowRightColumn" bgcolor="#003399">
                    <tr>
                        <td align="left">
                            <input type="button" value=" <bean:message key="global.btnClose"/> " onClick="window.close()">
                            <input type="button" value=" <bean:message key="global.btnPrint"/> " onClick="window.print()">

                        </td>
                    </tr>
                </table>
                <form action="labValuesGraph.jsp">
                    <input type="hidden" name="labType" value="<%=labType%>" />
                    <input type="hidden" name="demographic_no" value="<%=demographicNo%>" />
                    <input type="hidden" name="testName" value="<%=testName%>" />
                    <input type="hidden" name="identifier" value="<%=identifier%>" />
                    <ul>
                        <%
                        RxPrescriptionData prescriptData = new RxPrescriptionData();
                        RxPrescriptionData.Prescription[] arr = prescriptData.getUniquePrescriptionsByPatient(Integer.parseInt(demographicNo));

                        for (RxPrescriptionData.Prescription drug : arr)
                        {
                            if (drug.isArchived() || drug.isCustom())
                            {
                                continue;
                            }
                            %>
                            <li><input type="checkbox" <%=getChecked(drugTable, drug.getRegionalIdentifier())%> name="drug" value="<%=drug.getRegionalIdentifier()%>" /> <%=drug.getFullOutLine().replaceAll(";", " ")%> </li>
                            <%
                         }
                        %>
                    </ul>
                    <input type="submit" value="Add Meds to Graph"/>
                </form>

            </td>
        </tr>
    </table>
    </body>
</html>

<%!
String getChecked(Hashtable table, String reg) {
    if (table != null && reg != null && table.containsKey(reg))
    {
        return "checked";
    }
    return "";
}
%>
