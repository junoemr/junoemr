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
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.TreeMap" %>
<%@ page import="java.util.Vector" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%
	String roleName2$ = session.getAttribute("userrole") + "," + session.getAttribute("user");
	boolean authed=true;
%>
<security:oscarSec roleName="<%=roleName2$%>" objectName="_allergy" rights="r" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect("../securityError.jsp?type=_allergy");%>
</security:oscarSec>
<%
	if (!authed)
	{
		return;
	}
%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<html:html locale="true" lang="US-EN">
	<head>
		<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
		<script type="text/javascript" src="<%= request.getContextPath() %>/share/javascript/prototype.js"></script>
		<script type="text/javascript" src="<%= request.getContextPath() %>/share/javascript/screen.js"></script>
		<script type="text/javascript" src="<%= request.getContextPath() %>/share/javascript/rx.js"></script>
		<script type="text/javascript" src="<%= request.getContextPath() %>/share/javascript/scriptaculous.js"></script>
		<script type="text/javascript" src="<%= request.getContextPath() %>/share/javascript/effects.js"></script>


		<title><bean:message key="ChooseAllergy.title" /></title>
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

		<link rel="stylesheet" type="text/css" href="styles.css">

		<script type="text/javascript">
			function isEmpty()
			{
				if (document.RxSearchAllergyForm.searchString.value.length === 0)
				{
					alert("Search Field is Empty");
					document.RxSearchAllergyForm.searchString.focus();
					return false;
				}
				return true;
			}

			function addCustomAllergy()
			{
				var name = document.getElementById('searchString').value;
				if(isEmpty() === true)
				{
					name = name.toUpperCase();
					alert(name);
					window.location="addReaction2.do?ID=0&type=0&name="+name;
				}
			}

			function addPenicillinAllergy()
			{
				window.location="addReaction2.do?ID=44452&name=PENICILLINS&type=10";
			}

			function addSulfonamideAllergy()
			{
				window.location="addReaction2.do?ID=44159&name=SULFONAMIDES&type=10";
			}

			function addCustomNKDA()
			{
				window.location="addReaction2.do?ID=0&type=0&name=NKDA";
			}

			function toggleSection(typeCode)
			{
				var imgSrc = document.getElementById(typeCode + "_img").src;
				if (imgSrc.indexOf('expander') !==- 1)
				{
					document.getElementById(typeCode + "_img").src = '../images/collapser.png';
					Effect.BlindDown(document.getElementById(typeCode + "_content"), {duration: 0.1 });
				}
				else
				{
					document.getElementById(typeCode + "_img").src = '../images/expander.png';
					Effect.BlindUp(document.getElementById(typeCode + "_content"), {duration: 0.1 });
				}
			}
		</script>
	</head>
	<body topmargin="0" leftmargin="0" vlink="#0000FF">
	<table border="0" cellpadding="0" cellspacing="0" style="border-collapse: collapse" width="100%" id="AutoNumber1">
		<%@ include file="TopLinks.jsp"%>
		<!-- Row One included here-->
		<tr>
			<%@ include file="SideLinksNoEditFavorites2.jsp"%>
			<td width="100%" style="border-left: 2px solid #A9A9A9;" height="100%" valign="top">
				<table cellpadding="0" cellspacing="2" style="border-collapse: collapse" width="100%">
					<tr>
						<td width="0%" valign="top">
							<div class="DivCCBreadCrumbs">
								<a href="SearchDrug3.jsp"> <bean:message key="SearchDrug.title" /></a>&nbsp;&gt;&nbsp;
								<a href="ShowAllergies2.jsp"> <bean:message key="EditAllergies.title" /></a>&nbsp;&gt;&nbsp;
								<b><bean:message key="ChooseAllergy.title" /></b>
							</div>
						</td>
					</tr>
					<!----Start new rows here-->
					<tr>
						<td>
							<div class="DivContentTitle">
								<bean:message key="ChooseAllergy.title" />
							</div>
						</td>
					</tr>
					<tr>
						<td>
							<div class="DivContentSectionHead">
								<bean:message key="ChooseAllergy.section1Title" />
							</div>
						</td>
					</tr>
					<tr>
						<td>
							<html:form action="/oscarRx/searchAllergy2" focus="searchString" onsubmit="return isEmpty()">
							<table>
								<tr valign="center">
									<td>Search:</td>
									<td><html:text property="searchString" size="16" styleId="searchString" maxlength="16" /></td>
								</tr>
								<tr>
									<td>
										<html:submit property="submit" value="Search" styleClass="ControlPushButton" />
									</td>
									<td>
										<input type=button class="ControlPushButton"
											   onclick=document.forms.RxSearchAllergyForm.searchString.value='';document.forms.RxSearchAllergyForm.searchString.focus();"
											   value="Reset" />
										<input type=button class="ControlPushButton" onclick="addCustomAllergy();" value="Custom Allergy" />
										<input type=button class="ControlPushButton" onclick="addCustomNKDA();" value="NKDA" />
										<input type=button class="ControlPushButton" onclick="addPenicillinAllergy();" value="Penicillin" />
										<input type=button class="ControlPushButton" onclick="addSulfonamideAllergy();" value="Sulfa" />
									</td>
								</tr>
							</table>
							&nbsp;
							<table bgcolor="#F5F5F5" cellspacing=2 cellpadding=2>
								<tr>
									<td colspan=4>
										Search the following categories: <i>(Listed general to specific)</i>
									</td>
								</tr>
								<tr>
									<td><html:checkbox property="typeDrugClass" /> Drug Classes</td>
									<td><html:checkbox property="typeIngredient" /> Ingredients</td>
									<td><html:checkbox property="typeGenericName" /> Generic Names</td>
									<td><html:checkbox property="typeBrandName" /> Brand Names</td>
								</tr>
								<tr>
									<td colspan=4>
										<script language=javascript>
											function typeSelect()
											{
												var frm = document.forms.RxSearchAllergyForm;

												frm.type1.checked = true;
												frm.type2.checked = true;
												frm.type3.checked = true;
												frm.type4.checked = true;
											}

											function typeClear()
											{
												var frm = document.forms.RxSearchAllergyForm;
												frm.type1.checked = false;
												frm.type2.checked = false;
												frm.type3.checked = false;
												frm.type4.checked = false;
											}
										</script>
										<input type=button
											   class="ControlPushButton"
											   onclick="typeSelect();"
											   value="Select All" />
										<input type=button
											   class="ControlPushButton"
											   onclick="typeClear();"
											   value="Clear All" />
									</td>
								</tr>
							</table>
						</html:form>
						</td>
					</tr>
					<tr>
						<td id="searchResultsContainer" >
							<table>
								<tr>
									<td>
										<div class="DivContentSectionHead">
											<bean:message key="ChooseAllergy.section2Title" />
										</div>
									</td>
								</tr>
								<tr>
									<td>
										<%
											Hashtable drugClassHash = (Hashtable) request.getAttribute("drugClasses");
										%>
										<div class="LeftMargin">
											<logic:notPresent name="allergies">Search returned no results. Revise your search and try again.</logic:notPresent>
											<logic:present name="allergies">
												<%
													boolean flatResults = Boolean.valueOf(oscar.OscarProperties.getInstance().getProperty("allergies.flat_results", "false"));
													if (flatResults)
													{
														TreeMap<String,Allergy> flatMap = (TreeMap<String,Allergy>)request.getAttribute("flatMap");
														if (flatMap.size() > 0)
														{
															for(String key : flatMap.keySet())
															{
																Allergy allergy = flatMap.get(key);
												%>
												<a href="addReaction.do?ID=<%= allergy.getDrugrefId() %>&name=<%=java.net.URLEncoder.encode(allergy.getDescription())%>&type=<%=allergy.getTypeCode()%>"><%=allergy.getDescription()%></a>
												<%
													java.util.Vector vect = (Vector) drugClassHash.get(""+allergy.getDrugrefId());
													if (vect != null)
													{
														for (int k = 0; k < vect.size() ; k++)
														{
															String[] drugClassPair = (String[]) vect.get(k);
												%>
												&nbsp;&nbsp;&nbsp;
												<a style="color: orange" href="addReaction.do?ID=<%=drugClassPair[0] %>&name=<%=java.net.URLEncoder.encode(drugClassPair[1])%>&type=10"><%=drugClassPair[1] %></a>
												<%
														}
													}
												%>
												<br/>
												<%
														}
													}
												%>


												<% } else { // Not flat results %>

												<%
													Map<Integer, List<Allergy>> allergyResults = (Map<Integer,List<Allergy>>)request.getAttribute("allergyResults");
													if (allergyResults.get(8).size() > 0)
													{
												%>
												<div class="DivContentSectionHead">
													<a href="javascript:void(0)" onclick="toggleSection('8');return false;">
														<img border="0" id="8_img" src="../images/collapser.png"/>
													</a>ATC Class
												</div>
												<!-- ANATOMICAL -->
												<div id="8_content">
													<%
														for(Allergy allergy:allergyResults.get(8))
														{
													%>
												<a href="addReaction.do?ID=<%= allergy.getDrugrefId() %>&name=<%=java.net.URLEncoder.encode(allergy.getDescription())%>&type=<%=allergy.getTypeCode()%>"><%=allergy.getDescription()%></a>
												<br/>
													<%
														}
													%>
												</div>
												<%
													}

													if(allergyResults.get(10).size() > 0)
													{
												%>
												<div class="DivContentSectionHead">
													<a href="javascript:void(0)" onclick="toggleSection('10');return false;">
														<img border="0" id="10_img" src="../images/collapser.png"/>
													</a>AHFS Class
												</div>
												<!-- THERAPEUTIC -->
												<div id="10_content">
													<%
														for (Allergy allergy : allergyResults.get(10))
														{
													%>
													<a href="addReaction.do?ID=<%= allergy.getDrugrefId() %>&name=<%=java.net.URLEncoder.encode(allergy.getDescription())%>&type=<%=allergy.getTypeCode()%>"><%=allergy.getDescription()%></a>
													<br/>
													<%
														}
													%>
												</div>
												<%
													}

													if (allergyResults.get(13).size() > 0)
													{
												%>
												<div class="DivContentSectionHead">
													<a href="javascript:void(0)" onclick="toggleSection('13');return false;">
														<img border="0" id="13_img" src="../images/collapser.png"/>
													</a>Brand Name
												</div>
												<!-- BRANDED_PRODUCT -->
												<div id="13_content">
													<%
														for (Allergy allergy : allergyResults.get(13))
														{
													%>
													<a href="addReaction.do?ID=<%= allergy.getDrugrefId() %>&name=<%=java.net.URLEncoder.encode(allergy.getDescription())%>&type=<%=allergy.getTypeCode()%>"><%=allergy.getDescription() %></a>
													<%
														java.util.Vector vect = (Vector) drugClassHash.get(""+allergy.getDrugrefId());
														if (vect != null){
															for (int k = 0; k < vect.size() ; k++)
															{
																String[] drugClassPair = (String[]) vect.get(k);
													%>
												&nbsp;&nbsp;&nbsp;
													<a style="color: orange" href="addReaction.do?ID=<%=drugClassPair[0] %>&name=<%=java.net.URLEncoder.encode(drugClassPair[1])%>&type=10"><%=drugClassPair[1] %></a>
													<%
															}
														}
													%>
													<br/>
													<%
														}
													%>
												</div>
												<%
													}
													if (allergyResults.get(11).size() > 0)
													{
												%>
												<div class="DivContentSectionHead">
													<a href="javascript:void(0)" onclick="toggleSection('11');return false;">
														<img border="0" id="11_img" src="../images/expander.png"/>
													</a>Generic Name
												</div>
												<!-- GENERIC -->
												<div id="11_content" style="display:none">
													<%
														for (Allergy allergy : allergyResults.get(11))
														{
													%>
													<a href="addReaction.do?ID=<%= allergy.getDrugrefId() %>&name=<%=java.net.URLEncoder.encode(allergy.getDescription())%>&type=<%=allergy.getTypeCode()%>"><%=allergy.getDescription() %></a>
													<br/>
													<%
														}
													%>
												</div>
												<%
													}

													if (allergyResults.get(12).size() > 0)
													{
												%>
												<div class="DivContentSectionHead">
													<a href="javascript:void(0)" onclick="toggleSection('12');return false;">
														<img border="0" id="12_img" src="../images/expander.png"/>
													</a>Compound
												</div>
												<!-- COMPOSITE_GENERIC -->
												<div id="12_content" style="display:none">
													<%
														for (Allergy allergy : allergyResults.get(12))
														{
													%>
													<a href="addReaction.do?ID=<%= allergy.getDrugrefId() %>&name=<%=java.net.URLEncoder.encode(allergy.getDescription())%>&type=<%=allergy.getTypeCode()%>"><%=allergy.getDescription() %></a>
													<br/>
												<%
														}
												%>
												</div>
												<%
													}
													if (allergyResults.get(14).size() > 0)
													{
												%>
												<div class="DivContentSectionHead">
													<a href="javascript:void(0)" onclick="toggleSection('14');return false;">
														<img border="0" id="14_img" src="../images/collapser.png"/>
													</a>Ingredient
												</div>
												<!-- INGREDIENT -->
												<div id="14_content">
													<%
														for (Allergy allergy : allergyResults.get(14))
														{
													%>
													<a href="addReaction.do?ID=<%= allergy.getDrugrefId() %>&name=<%=java.net.URLEncoder.encode(allergy.getDescription())%>&type=<%=allergy.getTypeCode()%>"><%=allergy.getDescription() %></a>
													<br/>
													<%
														}
													%>
												</div>
												<%
													}

													}
												%>
											</logic:present>
										</div>

										<%
											String sBack="ShowAllergies2.jsp";
										%>
										<input type=button class="ControlPushButton"
											   onclick="javascript:window.location.href='<%=sBack%>';"
											   value="Back to View Allergies" />
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<!----End new rows here-->
					<tr>
						<td></td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td height="0%" style="border-bottom: 2px solid #A9A9A9; border-top: 2px solid #A9A9A9;"></td>
			<td height="0%" style="border-bottom: 2px solid #A9A9A9; border-top: 2px solid #A9A9A9;"></td>
		</tr>
		<tr>
			<td width="100%" height="0%" colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td width="100%" height="0%" style="padding: 5px" bgcolor="#DCDCDC" colspan="2"></td>
		</tr>
	</table>
	</body>
</html:html>