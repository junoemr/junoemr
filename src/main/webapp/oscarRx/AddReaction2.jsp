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
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ page import="oscar.util.ConversionUtils" %>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%
	String roleName2$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
	boolean authed=true;
%>
<security:oscarSec roleName="<%=roleName2$%>" objectName="_allergy" rights="r" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect("../securityError.jsp?type=_allergy");%>
</security:oscarSec>
<%
	if(!authed) {
		return;
	}
%>

<html:html locale="true">
	<head>
		<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
		<title><bean:message key="AddReaction.title" /></title>
		<html:base />

		<logic:notPresent name="RxSessionBean" scope="session">
			<logic:redirect href="error.html" />
		</logic:notPresent>
		<logic:present name="RxSessionBean" scope="session">
			<bean:define id="bean" type="oscar.oscarRx.pageUtil.RxSessionBean"
						 name="RxSessionBean" scope="session" />
			<logic:equal name="bean" property="valid" value="false">
				<logic:redirect href="error.html" />
			</logic:equal>
		</logic:present>

		<%
			oscar.oscarRx.pageUtil.RxSessionBean sessionBean = (oscar.oscarRx.pageUtil.RxSessionBean)pageContext.findAttribute("bean");
			String name = (String) request.getAttribute("name");
			String type = (String) request.getAttribute("type");
			String allergyId = (String) request.getAttribute("allergyId");

			Allergy allergyToArchive = (Allergy)request.getAttribute("allergyToArchive");
			if (allergyToArchive == null)
			{
				allergyToArchive = new Allergy();
			}
			request.setAttribute("allergy", allergyToArchive);
		%>

		<link rel="stylesheet" type="text/css" href="styles.css">
	</head>
	<body topmargin="0" leftmargin="0" vlink="#0000FF">

	<table border="0" cellpadding="0" cellspacing="0"
		   style="border-collapse: collapse" bordercolor="#111111" width="100%"
		   id="AutoNumber1" height="100%">
		<%@ include file="TopLinks.jsp"%>
		<tr>
			<%@ include file="SideLinksNoEditFavorites2.jsp"%>
			<td width="100%" style="border-left: 2px solid #A9A9A9;" height="100%"
				valign="top">
				<table cellpadding="0" cellspacing="2"
					   style="border-collapse: collapse" bordercolor="#111111" width="100%"
					   height="100%">
					<tr>
						<td width="0%" valign="top">
							<div class="DivCCBreadCrumbs">
								<a href="SearchDrug3.jsp"> <bean:message key="SearchDrug.title" /></a>&nbsp;&gt;&nbsp;
								<a href="ShowAllergies2.jsp"> <bean:message key="EditAllergies.title" /></a>&nbsp;&gt;&nbsp;
								<b><bean:message key="AddReaction.title" /></b>
							</div>
						</td>
					</tr>
					<!----Start new rows here-->

					<tr>
						<td>
							<div class="DivContentSectionHead"><%=name%></div>
						</td>
					</tr>
					<tr>
						<td id="addAllergyDialogue">
							<html:form action="/oscarRx/addAllergy2" focus="reactionDescription" onsubmit="return validateAllergySubmit();">
							<table>
								<tr id="addReactionSubheading">
									<td>
										Adding Allergy: <%=name%>
									</td>
								</tr>
								<tr valign="center">
									<td>
										<span class="label">Comment: </span>
										<html:textarea property="reactionDescription" cols="40" rows="3" value="${allergy.reaction}" />
										<html:hidden property="ID" value="<%=allergyId%>" />
										<html:hidden property="name" value="<%=name%>" />
										<html:hidden property="type" value="<%=type%>" />
										<html:hidden property="allergyToArchive" value="${allergy.id}" />
									</td>
								</tr>

								<tr valign="center">
									<td>
										<span class="label">Start Date:</span>
										<input id="startDate"
											   name="startDate"
											   value="<%=ConversionUtils.toDateString(allergyToArchive.getStartDate(), ConversionUtils.DEFAULT_DATE_PATTERN)%>">
										<label for="startDate">(yyyy-mm-dd OR yyyy-mm OR yyyy)</label>
									</td>
								</tr>

								<tr valign="center">
									<td>
										<span class="label">Age Of Onset:</span>
										<html:text property="ageOfOnset" size="4" maxlength="4" value="${allergy.ageOfOnset}" />
									</td>
								</tr>


								<tr valign="center">
									<td> <span class="label"><bean:message key="oscarEncounter.lifestage.title"/>:</span>
										<html:select property="lifeStage">
											<c:if test="${allergy != null}">
												<html:option value="${allergy.lifeStage}">${allergy.lifeStageDesc}</html:option>
											</c:if>
											<c:forTokens items=",N,I,C,T,A" delims="," var="item">
												<c:if test="${allergy.lifeStage != item}">
													<html:option value="${item}">
														${allergy.getDescForLifeStageCode(item, pageContext.response.locale)}
													</html:option>
												</c:if>
											</c:forTokens>
										</html:select>
									</td>
								</tr>
								<tr valign="center">
									<td>
										<span class="label">Severity Of Reaction:</span>
										<html:select property="severityOfReaction">
											<c:if test="${allergy.severityOfReaction != null}">
												<html:option value="${allergy.severityOfReaction}">${allergy.severityOfReactionDesc}</html:option>
											</c:if>
											<c:forTokens items="4,1,2,3" delims="," var="item">
												<c:if test="${allergy.severityOfReaction != item}">
													<html:option value="${item}">${allergy.getDescForSeverityValue(item)}</html:option>
												</c:if>
											</c:forTokens>
										</html:select>
									</td>
								</tr>

								<tr valign="center">
									<td>
										<span class="label">Onset Of Reaction:</span>
										<html:select property="onSetOfReaction">
											<c:if test="${allergy.onsetOfReaction != null}">
												<html:option value="${allergy.onsetOfReaction}">${allergy.onSetOfReactionDesc}</html:option>
											</c:if>
											<c:forTokens items="4,1,2,3" delims="," var="item">
												<c:if test="${allergy.onsetOfReaction != item}">
													<html:option value="${item}">${allergy.getDescForOnsetCode(item)}</html:option>
												</c:if>
											</c:forTokens>
										</html:select>
									</td>
								</tr>


								<tr>
									<td >
										<input type="submit" value="Add Allergy">
										<input type=button class="ControlPushButton" id="cancelAddReactionButton"
											onclick="window.location='ShowAllergies2.jsp?demographicNo=<%=sessionBean.getDemographicNo() %>'"
											value="Cancel" />
									</td>
								</tr>
							</table>

						</html:form></td>
					</tr>

					<tr>
						<td>
							<%
								String sBack="ShowAllergies2.jsp";
							%> <input type=button class="ControlPushButton"
									  onclick="javascript:window.location.href='<%=sBack%>';"
									  value="Back to View Allergies" /></td>
					</tr>
					<!----End new rows here-->
					<tr height="100%">
						<td></td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td height="0%"
				style="border-bottom: 2px solid #A9A9A9; border-top: 2px solid #A9A9A9;"></td>
			<td height="0%"
				style="border-bottom: 2px solid #A9A9A9; border-top: 2px solid #A9A9A9;"></td>
		</tr>
		<tr>
			<td width="100%" height="0%" colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td width="100%" height="0%" style="padding: 5" bgcolor="#DCDCDC"
				colspan="2"></td>
		</tr>
	</table>

	</body>

</html:html>