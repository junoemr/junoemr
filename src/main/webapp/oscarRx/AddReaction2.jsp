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
<%@ page import="org.oscarehr.common.dao.PartialDateDao" %>
<%@ page import="org.oscarehr.util.SpringUtils" %>
<%@ page import="org.oscarehr.common.model.PartialDate" %>
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

			String prevId = "";
			if (allergyToArchive.getId() != null)
			{
				prevId = allergyToArchive.getId().toString();
			}

			PartialDateDao partialDateDao = SpringUtils.getBean(PartialDateDao.class);
			String startDate = "";
			if (allergyToArchive.getStartDate() != null)
			{
				startDate = partialDateDao.getDatePartial(allergyToArchive.getStartDate(),
						PartialDate.TABLE_ALLERGIES,
						allergyToArchive.getAllergyId(),
						PartialDate.ALLERGIES_STARTDATE);
			}

			String submitHelpText = "Add Allergy";
			if (allergyToArchive.getId() != null)
			{
				if (allergyToArchive.getArchived())
				{
					submitHelpText = "Reactivate Allergy";
				}
				else
				{
					submitHelpText = "Modify Allergy";
				}
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
										<span class="label">Reaction: </span>
										<html:textarea property="reactionDescription" cols="40" rows="3" value="${allergy.reaction}" />
										<input type="hidden" name="ID" id="drugrefId" value="<%=allergyId%>">
										<input type="hidden" name="name" id="drugName" value="<%=name%>">
										<input type="hidden" name="type" id="type" value="<%=type%>">
										<input type="hidden" name="allergyToArchive" id="allergyId" value="<%=prevId%>">
									</td>
								</tr>

								<tr valign="center">
									<td>
										<span class="label">Start Date:</span>
										<input id="startDate"
											   name="startDate"
											   value="<%=startDate%>">
										<label for="startDate">(yyyy-mm-dd OR yyyy-mm OR yyyy)</label>
									</td>
								</tr>

								<tr valign="center">
									<td>
										<span class="label">
											<label for="ageOfOnset">Age Of Onset:</label>
										</span>
										<input id="ageOfOnset"
											   name="ageOfOnset"
											   value="${allergy.ageOfOnset}"/>
									</td>
								</tr>


								<tr valign="center">
									<td> <span class="label"><bean:message key="oscarEncounter.lifestage.title"/>:</span>
										<select property="lifeStage" name="lifeStage" id="lifeStage">
											<%
												// This exists in this form for a few reasons:
												// - we need to select the value of the lifeStage to be able to validate input later
												// - when we pull out an older entry to modify it, we should be pre-selecting the option that was recorded before
												// - we don't want to duplicate the option being selected
												String[] lifestageCodes = {"", "N", "I", "C", "T", "A"};
												for (String code : lifestageCodes)
												{
													if (code.equals(allergyToArchive.getLifeStage()))
													{
											%>
											<option value="<%=code%>" selected><%=allergyToArchive.getDescForLifeStageCode(code, pageContext.getResponse().getLocale())%></option>
											<%
													}
													else
													{
											%>
											<option value="<%=code%>"><%=allergyToArchive.getDescForLifeStageCode(code, pageContext.getResponse().getLocale())%></option>
											<%
													}
												}
											%>
										</select>
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
										<input type="submit" value="<%=submitHelpText%>">
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
			<td width="100%" height="0%" style="padding: 5px" bgcolor="#DCDCDC"
				colspan="2"></td>
		</tr>
	</table>

	</body>

</html:html>