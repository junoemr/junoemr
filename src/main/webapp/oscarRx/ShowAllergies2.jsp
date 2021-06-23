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
<%@page import="org.oscarehr.util.LoggedInInfo"%>
<%@page import="org.oscarehr.util.WebUtilsOld"%>
<%@page import="org.oscarehr.myoscar.utils.MyOscarLoggedInInfo"%>
<%@page import="org.oscarehr.util.LocaleUtils"%>
<%@page import="org.oscarehr.phr.util.MyOscarUtils"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>

<%@page import="org.oscarehr.util.SpringUtils" %>
<%@page import="org.oscarehr.casemgmt.service.CaseManagementManager" %>
<%@page import="org.oscarehr.common.dao.PartialDateDao" %>
<%@page import="org.oscarehr.common.model.PartialDate" %>
<%@ page import="oscar.oscarRx.pageUtil.RxSessionBean" %>
<%@ page import="org.oscarehr.casemgmt.model.CaseManagementNoteLink" %>
<%@ page import="org.oscarehr.demographic.dao.DemographicDao" %>
<%@ page import="org.oscarehr.demographic.model.Demographic" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.Period" %>

<%
	String roleName2$ = session.getAttribute("userrole") + "," + session.getAttribute("user");
	boolean authenticated = true;
%>
<security:oscarSec roleName="<%=roleName2$%>" objectName="_allergy" rights="r" reverse="<%=true%>">
	<%authenticated =false; %>
	<%response.sendRedirect("../securityError.jsp?type=_allergy");%>
</security:oscarSec>
<%
	if(!authenticated) {
		return;
	}

	PartialDateDao partialDateDao = (PartialDateDao) SpringUtils.getBean("partialDateDao");
	DemographicDao demographicDao = (DemographicDao) SpringUtils.getBean("demographic.dao.DemographicDao");
%>

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
	RxSessionBean sessionBean = (RxSessionBean)pageContext.findAttribute("sessionBean");
	String annotation_display = CaseManagementNoteLink.DISP_ALLERGY;
	com.quatro.service.security.SecurityManager securityManager = new com.quatro.service.security.SecurityManager();

	String demographicNo = request.getParameter("demographicNo");

	if (demographicNo == null)
	{
		demographicNo = (String)session.getAttribute("demographicNo");
	}
	else
	{
		session.setAttribute("demographicNo", demographicNo);
	}

	if (demographicNo == null || demographicNo.equals("null"))
	{
		demographicNo = String.valueOf(sessionBean.getDemographicNo());
	}

	Demographic demographic = demographicDao.find(Integer.parseInt(demographicNo));
	String demoBirthday = demographic.getDateOfBirth().toString();
	int demoAge = Period.between(demographic.getDateOfBirth(), LocalDate.now()).getYears();

%>
<html:html locale="true">
	<head>
		<title><bean:message key="EditAllergies.title" /></title>

		<script type="text/javascript" src="<%=request.getContextPath()%>/share/javascript/jquery/jquery-2.2.4.min.js"></script>
		<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/js/moment.min.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/oscarRx/JunoAllergyHelpers.js"></script>

		<script type="text/javascript">
			/**
			 * Called as part of AddReaction2.jsp's onsubmit for adding or modifying an allergy.
			 * Validation logic for checking that inputted dates and ages make sense for the patient.
			 * Also checks to see if the user is attempting to add an allergy that already exists.
			 * @return boolean
			 */
			function validateAllergySubmit()
			{
				var startDateSelector = $("#startDate");
				var startDate = startDateSelector.val();

				// Only check date if we're actually inputting something
				if (startDate.length > 0)
				{
					var dateFormat = Juno.AllergyHelpers.isValidStartDate(startDate);
					if (dateFormat == null)
					{
						alert("Invalid date in Start Date. Please use one of the following formats:" +
							"\nyyyy-mm-dd" +
							"\nyyyy-mm" +
							"\nyyyy");
						return false;
					}

					startDate = Juno.AllergyHelpers.padStartDate(startDate, dateFormat);

					var lifestage = $("#lifeStage").children("option:selected").val();

					if (startDate[0].length > 0 && !validateStartDate("<%=demoBirthday%>", startDate, lifestage, dateFormat))
					{
						return false;
					}
				}

				var typeCode = $("#type").val();
				var drugName = $("#drugName").val();
				var allergyId = $("#allergyId").val();

				var isDuplicate = Juno.AllergyHelpers.isDuplicateAllergy(<%=demographicNo%>, typeCode, drugName, allergyId);

				if (isDuplicate)
				{
					alert("You're attempting to add an allergy that is already active!");
					return false;
				}

				// Somewhat ugly by directly checking against a regex. Alternative is to cast the string
				// to an integer and then check all of the cases where the conversion could have gone wrong
				// and that it's a positive number
				// Previous validation allowed for any string that was, at most, 4 chars. Enforcing that here too
				var ageOfOnset = $("#ageOfOnset").val();
				var validAge = ageOfOnset.length === 0 || /^(0|[1-9]\d{0,3})$/.test(ageOfOnset);
				if (!validAge)
				{
					alert("Invalid value in Age of Onset. Check that your age of onset value is one of the following:" +
						"\n- is empty" +
						"\n- is 0" +
						"\n- is a positive number");
				}
				else if (parseInt(ageOfOnset) > <%=demoAge%>)
				{
					alert("Input age of onset is greater than the patient's age!");
					validAge = false;
				}

				return validAge;
			}

			/**
			 * Given a (possibly estimated) start date for the allergy, check the following:
			 * - that it is after their birthday
			 * - that it fits within the date range defined by the inputted lifestage
			 * - that it is not in the future (not greater than today)
			 */
			function validateStartDate(birthday, startDate, lifestage, dateFormat)
			{
				startDate = moment(startDate).format(dateFormat);
				var today = moment().format(dateFormat);
				if (startDate > today)
				{
					alert("Your start date is in the future. Please add a start date of today or prior.");
					return false;
				}

				// Birthday has to be re-coalesced using the same date format for fairness
				birthday = moment(birthday).format(dateFormat);
				if (startDate !== 0 && startDate.length > 0)
				{
					if (birthday > startDate)
					{
						alert("Start date entered does not align with what the demographic's birth date is recorded as." +
							"\nExpected start date to occur after: " + birthday);
						return false;
					}

					var validDate = Juno.AllergyHelpers.checkDateAgainstLifestage(lifestage, birthday, startDate);
					if (!validDate)
					{
						var lowerDate = Juno.AllergyHelpers.getLowerBoundLifestageDate(lifestage, birthday).toISOString().split("T")[0];
						var upperDate = Juno.AllergyHelpers.getUpperBoundLifestageDate(lifestage, birthday).toISOString().split("T")[0];
						alert("The start date doesn't match with the life stage and birth date of the patient. " +
							"\nIf they were in the " + Juno.AllergyHelpers.getLifestageName(lifestage) + " lifestage, then " +
							"the start date should fall between the following dates: " +
							"\n - " + lowerDate +
							"\n - " + upperDate);
						return false;
					}

				}
				return true;
			}
		</script>

		<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/allergies.css">
		<style type="text/css">
			.ajax-loader {
				background: url(../images/ui-anim_basic_16x16.gif) center right no-repeat;
			}
		</style>
		<script type="text/javascript">

			$(document).ready(function()
			{
				$.fn.bindActionEvents = function()
				{
					// Cache all the selectors we're using
					var deleteLink = $(".deleteAllergyLink");
					var modifyLink = $(".modifyAllergyLink");
					var searchContainer = $("#searchResultsContainer a");
					var sectionHead = $(".DivContentSectionHead a img");

					// Unbind click events first to avoid multiple binds
					deleteLink.unbind("click");
					modifyLink.unbind("click");
					searchContainer.unbind("click");
					sectionHead.unbind("click");

					//--> action for selecting from search results.
					$("#searchResultsContainer div[id $= '_content'] a").bind("click", function(event)
					{
						event.preventDefault();
						// override the old addReaction.do with the new addReaction2.do
						var path = "${ pageContext.servletContext.contextPath }/oscarRx/addReaction2.do";
						var param = this.href.split("?")[1];

						sendSearchRequest(path, param, "#addAllergyDialogue");
						$("#searchResultsContainer").html("");
					});

					//--> delete allergy.
					deleteLink.bind("click", function()
					{
						var ids = this.id.split("_");
						var action = ids[0].split(":")[1];
						var param = ids[1].trim();
						var allergyId = param.split("&")[0];
						allergyId = allergyId.split("=")[1].trim();
						$("#allergy_" + allergyId).addClass("highLightRow");

						var path = "${ pageContext.servletContext.contextPath }/oscarRx/deleteAllergy2.do";

						if (confirm(action + " this Allergy?"))
						{
							sendSearchRequest(path, param, ".Step1Text");
						}
					});

					//--> modify allergy.
					modifyLink.bind("click", function()
					{
						var ids = this.id.split("_");
						var param = ids[1].trim();
						sendSearchRequest("${ pageContext.servletContext.contextPath }/oscarRx/addReaction2.do",
							param, "#addAllergyDialogue");
					});

					//--> Toggle search results listing.
					$.fn.toggleSection = function(typeCode)
					{
						var imgSrc = document.getElementById(typeCode + "_img").src;
						if (imgSrc.indexOf('expander') !== -1)
						{
							document.getElementById(typeCode + "_img").src='../images/collapser.png';
							Effect.BlindDown(document.getElementById(typeCode + "_content"), {duration: 0.1 });
						}
						else
						{
							document.getElementById(typeCode + "_img").src='../images/expander.png';
							Effect.BlindUp(document.getElementById(typeCode + "_content"), {duration: 0.1 });
						}
					};

					//--> Toggle search results listing.
					sectionHead.bind("click", function(event)
					{
						event.preventDefault();
						var typeCode = this.id.split("_")[0];
						var imgSrc = document.getElementById(typeCode + "_img").src;
						if (imgSrc.indexOf('expander') !== -1)
						{
							document.getElementById(typeCode + "_img").src='../images/collapser.png';
							$("#" + typeCode + "_content").show();
						}
						else
						{
							document.getElementById(typeCode + "_img").src='../images/expander.png';
							$("#" + typeCode + "_content").hide();
						}
					})

				}; //--> end bind events function.

				//--> set default checkboxes on load
				$.fn.setDefaults = function() {
					// default set Drug Classes checked.
					document.getElementById("typeDrugClass").checked = true;
				};


				$("#searchString").keypress(function(event)
				{
					if (event.which === 13)
					{
						triggerSearchRequest();
					}
				});


				//--> Send allergy search to server
				// Want to copy the functionality of this to the event above
				$("#searchStringButton").click(function()
				{
					if (!isEmpty())
					{
						triggerSearchRequest();
					}
				});

				//--> Toggle checkboxes all or none
				$("#typeSelectAll").change( function(){
					if(this.checked) {
						typeSelect();
					} else {
						typeClear();
					}
				});

				//--> Cancel add allergy dialogue
				$("#cancelAddReactionButton").click(function(event){
					event.preventDefault();
					document.forms.RxAddAllergyForm.reactionDescription.value='';
					document.forms.RxAddAllergyForm.startDate.value='';
					document.forms.RxAddAllergyForm.ageOfOnset.value='';
					location.reload();
				});

				//--> Actions after allergy has been added.
				$("input[value='Add Allergy'], .ControlPushButton").click(function()
				{
					$(".ControlPushButton").removeClass("highLightButton");
				});

				$().bindActionEvents();
				$().setDefaults();

				$("searchAllergy2").submit(function(event)
				{
					event.preventDefault();
					if (!isEmpty())
					{
						triggerSearchRequest();
					}
				});

			}); //--> end document ready

			function triggerSearchRequest()
			{
				$(".highLightButton").removeClass("highLightButton");
				var form = $("#searchAllergy2");
				var url = "${ pageContext.servletContext.contextPath }" + form.attr('action');
				document.getElementById("inputSearchString").value = document.getElementById("searchString").value;
				var params = form.serializeArray();
				var json = {};
				$.each(params, function() {
					if (!(this.name === "iNKDA" || this.name === "hasDrugAllergy" || this.name === "submit"))
					{
						json[this.name] = this.value || '';
					}
				});

				// servlet looks for "jsonData" request parameter
				var param = "jsonData=" + JSON.stringify(json);

				// thinking action
				if (!isEmpty())
				{
					$('#searchString').addClass('ajax-loader');
					sendSearchRequest(url, param, "#searchResultsContainer");
				}
				return false;
			}


			//--> AJAX the data to the server.
			function sendSearchRequest(path, param, target)
			{
				var iNKDA = document.forms.searchAllergy2.iNKDA.value;
				if (param.indexOf(paramNKDA) >= 0)
				{
					var hasDrugAllergy = document.forms.searchAllergy2.hasDrugAllergy.value;
					if (hasDrugAllergy === "true")
					{
						alert("Active drug allergy exists!");
						return;
					}
					if (iNKDA > 0)
					{
						var iAtoA = param.indexOf("allergyToArchive=");
						if (iAtoA > 0)
						{
							iAtoA = param.substring(iAtoA + "allergyToArchive=".length);
						}
						if (iAtoA < 0 || iNKDA > iAtoA)
						{
							alert("\"No Known Drug Allergies\" already exists!");
							return;
						}
					}
				}
				else if (param.indexOf("ID=0") < 0 && iNKDA > 0)
				{
					param += "&nkdaId=" + iNKDA;
				}
				$.ajax({
					url: path,
					type: 'POST',
					data: param,
					dataType: 'html',
					success: function(data)
					{
						renderSearchResults(data, target);
						if (path.indexOf("deleteAllergy") >= 0)
						{
							location.reload();
						}
					}
				});
			}

			//--> Render response html
			function renderSearchResults(html, id) {
				if( id instanceof Array ) {
					$.each(id, function(i, val){
						$(val).replaceWith( jQuery(val, html) );
					});
				} else {
					$(id).replaceWith( jQuery(id, html) );
				}

				// This is really gross. The replacement operations(s) above are already doing similar replacement
				// If or when we rework the search to return JSON instead of HTML we can do a proper length check on input data
				// This replacement is specifically intended for searching drugref, when we get zero results back.
				if (id === "#searchResultsContainer" && $("div.LeftMargin > div").length === 0)
				{
					$("div.LeftMargin").append("<div>Search returned no results. Revise your search and try again.</div>")
				}

				$('.ajax-loader').removeClass('ajax-loader');
				$().bindActionEvents();
			}

			//--> Check if search field is empty.
			function isEmpty()
			{
				var searchStrField = document.getElementById("searchString");
				if (searchStrField.value.length === 0)
				{
					alert("Search Field is Empty");
					searchStrField.focus();
					return true;
				}
				return false;
			}

			//--> Checkboxes for search allergy criteria
			// To have the 'all' checkbox function properly, we need to visually toggle the checkbox
			// as well as get the DOM to recognize that we're toggling the checkbox.
			function typeSelect()
			{
				var typeBrand = $("#typeBrandName");
				typeBrand.checked = true;
				document.getElementById("typeBrandName").checked = true;
				toggleInput('inputBrandName', typeBrand);

				var typeGeneric = $("#typeGenericName");
				typeGeneric.checked = true;
				document.getElementById("typeGenericName").checked = true;
				toggleInput('inputGenericName', typeGeneric);

				var typeIngredient = $("#typeIngredient");
				typeIngredient.checked = true;
				document.getElementById("typeIngredient").checked = true;
				toggleInput('inputIngredient', typeIngredient);

				var typeDrugClass = $("#typeDrugClass");
				typeDrugClass.checked = true;
				document.getElementById("typeDrugClass").checked = true;
				toggleInput('inputDrugClass', typeDrugClass);
			}

			function toggleInput(input, parentBox)
			{
				if (parentBox.checked === true)
				{
					document.getElementById(input).value = "true";
				}
				else
				{
					document.getElementById(input).value = "false";
				}
			}

			function typeClear()
			{
				var typeBrand = $("#typeBrandName");
				typeBrand.checked = false;
				document.getElementById("typeBrandName").checked = false;
				toggleInput('inputBrandName', typeBrand);

				var typeGeneric = $("#typeGenericName");
				typeGeneric.checked = false;
				document.getElementById("typeGenericName").checked = false;
				toggleInput('inputGenericName', typeGeneric);

				var typeIngredient = $("#typeIngredient");
				typeIngredient.checked = false;
				document.getElementById("typeIngredient").checked = false;
				toggleInput('inputIngredient', typeIngredient);

				var typeDrugClass = $("#typeDrugClass");
				typeDrugClass.checked = false;
				document.getElementById("typeDrugClass").checked = false;
				toggleInput('inputDrugClass', typeDrugClass);
			}

			function addCustomAllergy()
			{
				$(".highLightButton").removeClass("highLightButton");
				var name = document.getElementById('searchString').value;
				if(!isEmpty()){
					name = name.toUpperCase();
					if (confirm("Adding custom allergy: " + name))
					{
						var searchPath = "${pageContext.servletContext.contextPath}/oscarRx/addReaction2.do";
						var param = "ID=0&type=0&name=" + name;
						sendSearchRequest(searchPath, param, "#addAllergyDialogue");
						$("input[value='Custom Allergy']").addClass("highLightButton");
					}
				}
			}

			function addPenicillinAllergy()
			{
				$(".highLightButton").removeClass("highLightButton");
				var param = "ID=44452&name=PENICILLINS&type=10";
				var searchPath = "${pageContext.servletContext.contextPath}/oscarRx/addReaction2.do";
				sendSearchRequest(searchPath, param, "#addAllergyDialogue");
				$("input[value='Penicillin']").addClass("highLightButton");
			}

			function addSulfonamideAllergy()
			{
				$(".highLightButton").removeClass("highLightButton");
				var param = "ID=44159&name=SULFONAMIDES&type=10";
				var searchPath = "${pageContext.servletContext.contextPath}/oscarRx/addReaction2.do";
				sendSearchRequest(searchPath, param, "#addAllergyDialogue");
				$("input[value='Sulfa']").addClass("highLightButton");
			}

			var paramNKDA = "name=No Known Drug Allergies";

			function addCustomNKDA()
			{
				$(".highLightButton").removeClass("highLightButton");
				var param = "ID=0&type=0&" + paramNKDA;
				var searchPath = "${pageContext.servletContext.contextPath}/oscarRx/addReaction2.do";
				sendSearchRequest(searchPath, param, "#addAllergyDialogue");
				$("input[value='NKDA']").addClass("highLightButton");
			}
		</script>

	</head>
	<bean:define id="patient" type="oscar.oscarRx.data.RxPatientData.Patient" name="Patient" />

	<body>
	<%=WebUtilsOld.popErrorAndInfoMessagesAsHtml(session)%>
	<table id="AutoNumber1">
		<tr id="allergiesRowOne" >
			<td colspan="2">
				<jsp:include page="TopLinks.jsp">
					<jsp:param value="Allergies" name="title" />
					<jsp:param value="${ patient.surname }, ${ patient.firstName }" name="patientName" />
					<jsp:param value="${ patient.sex }" name="sex" />
					<jsp:param value="${ patient.age }" name="age" />
					<jsp:param value="<%= roleName2$ %>" name="security" />
					<jsp:param value='<%= (String)session.getAttribute("demographicNo") %>' name="demographicNo" />
				</jsp:include>
			</td>
		</tr>
		<tr>
			<td id="allergiesColumnOneRowTwo" >
				<%@ include file="SideLinksEditFavorites2.jsp" %>
			</td>
			<td id="allergiesColumnTwoRowTwo" >
				<table>
					<tr class="DivCCBreadCrumbs" >
						<td>
							<a href="SearchDrug3.jsp" >
								<bean:message key="SearchDrug.title" />
							</a>
							<!-- &nbsp;&gt;&nbsp; -->
							<b><bean:message key="EditAllergies.title" /></b>
						</td>
					</tr>
					<!----Start new rows here-->

					<tr class="DivContentSectionHead" >
						<td>
							<bean:message key="EditAllergies.section1Title" />
						</td>
					</tr>
					<tr id="patientDataRow">
						<td>
							<table>
								<tr>
									<td><b><bean:message key="SearchDrug.nameText" /></b>
										<jsp:getProperty name="patient" property="surname" />,
										<jsp:getProperty name="patient" property="firstName" />
									</td>
									<td></td>
									<td><b>Age</b> <jsp:getProperty name="patient" property="age" /></td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td class="DivContentSectionHead" >
							<bean:message key="EditAllergies.section2Title" />
							<span class="view_menu">View:

						<%
							String strView = request.getParameter("view");
							if (strView == null)
							{
								strView = "Active";
							}

							String[] navArray = {"Active","All","Inactive"};

							for (String navItem : navArray)
							{
								if (strView.equals(navItem))
								{
									out.print(" <span class='view selected'>" + navItem + "</span>");
								}
								else
								{
									out.print("<span class='view_menu'><a href='ShowAllergies2.jsp?demographicNo=" + demographicNo + "&view=" + navItem + "'>");
									out.print(navItem);
									out.print("</a></span>");
								}
							}

							// Can probably revisit this for minor optimization (index [0] unused)
							String[] ColourCodesArray=new String[6];
							ColourCodesArray[1]="#F5F5F5"; // Mild Was set to yellow (#FFFF33)
							ColourCodesArray[2]="#FF6600"; // Moderate
							ColourCodesArray[3]="#CC0000"; // Severe
							ColourCodesArray[4]="#E0E0E0"; // unknown
							ColourCodesArray[5]="#FFFFFF"; // no reaction
						%>
							</span>
							<%
								if (MyOscarUtils.isMyOscarEnabled((String) session.getAttribute("user")))
								{
									MyOscarLoggedInInfo myOscarLoggedInInfo=MyOscarLoggedInInfo.getLoggedInInfo(session);
									boolean enabledMyOscarButton=MyOscarUtils.isMyOscarSendButtonEnabled(myOscarLoggedInInfo, Integer.valueOf(demographicNo));
									if (enabledMyOscarButton)
									{
										String sendDataPath = request.getContextPath() + "/phr/send_medicaldata_to_myoscar.jsp?"
												+ "demographicId=" + demographicNo + "&"
												+ "medicalDataType=Allergies" + "&"
												+ "parentPage=" + request.getRequestURI();
							%>
							| <a href="<%=sendDataPath%>"><%=LocaleUtils.getMessage(request, "SendToPHR")%></a>
							<%
							}
							else
							{
							%>
							| <span style="color:grey;text-decoration:underline"><%=LocaleUtils.getMessage(request, "SendToPHR")%></span>
							<%
									}
								}
							%>
						</td>
					</tr>
					<tr>
						<td>
							<table border="0">
								<tr>
									<td class="Step1Text">
										<table class='allergy_legend' cellspacing='0'>
											<tr class="">
												<td>
													<b>Legend:</b>
												</td>
												<td >
													<table class='colour_codes' bgcolor='#F5F5F5'>
														<tr></tr>
													</table>
												</td>
												<td>Mild</td>
												<td >
													<table class='colour_codes' bgcolor='#FF6600'>
														<tr></tr>
													</table>
												</td>
												<td>Moderate</td>
												<td>
													<table class='colour_codes' bgcolor='#CC0000'>
														<tr></tr>
													</table>
												</td>
												<td>Severe</td>
											</tr>
										</table>

										<table class="allergy_table" >
											<tr>
												<td><b>Status</b></td>
												<td><b>Entry Date</b></td>
												<td><b>Description</b></td>
												<td><b>Allergy Type</b></td>
												<td><b>Severity</b></td>
												<td><b>Onset of Reaction</b></td>
												<td><b>Reaction</b></td>
												<td><b>Start Date</b></td>
												<td><b>Life Stage</b></td>
												<td><b>Age of Onset</b></td>
												<td><img src="../images/notes.gif" border="0" width="13" height="16" alt="Annotation"></td>
												<td><b>Action</b></td>
											</tr>
											<%
												String strArchived;
												int intArchived = 0;
												String labelStatus;
												String labelAction;
												String actionPath;
												String trColour;
												String strSOR;
												int intSOR;
												boolean hasDrugAllergy = false;
												int iNKDA = 0;

												for(Allergy allergy : patient.getAllergies(LoggedInInfo.getLoggedInInfoFromSession(request)))
												{
													if (!allergy.getArchived())
													{
														if (allergy.getTypeCode() > 0)
														{
															hasDrugAllergy = true;
														}
														if (allergy.getDescription().equals("No Known Drug Allergies"))
														{
															iNKDA = allergy.getId();
														}
													}

													String title = "";
													if (allergy.getRegionalIdentifier() != null && !allergy.getRegionalIdentifier().trim().equalsIgnoreCase("null") && !allergy.getRegionalIdentifier().trim().equals(""))
													{
														title = " title=\"Din: "+allergy.getRegionalIdentifier()+"\" ";
													}

													boolean filterOut = false;
													strArchived = allergy.getArchived() ? "1" : "0";

													try
													{
														intArchived = Integer.parseInt(strArchived);

														if (strView.equals("Active") && intArchived == 1)
														{
															filterOut = true;
														}

														if (strView.equals("Inactive") && intArchived == 0)
														{
															filterOut = true;
														}
													}
													catch (Exception e)
													{
														// that's okay , most likely the value is not set so we don't know, leave blank
													}

													strSOR = allergy.getSeverityOfReaction();
													intSOR = Integer.parseInt(strSOR);
													String sevColour;

													if (intArchived == 1)
													{
														//if allergy is set as archived
														labelStatus = "Inactive";
														labelAction = "Reactivate";
														actionPath = "activate";
														trColour = "#C0C0C0";
														sevColour = "#C0C0C0"; //clearing severity bgcolor
													}
													else
													{
														labelStatus = "Active";
														labelAction = "Inactivate";
														actionPath = "delete";
														trColour = "#E0E0E0";
														sevColour = ColourCodesArray[intSOR];
													}


													if (!filterOut)
													{
														String entryDate = partialDateDao.getDatePartial(allergy.getEntryDate(), PartialDate.TABLE_ALLERGIES, allergy.getAllergyId(), PartialDate.ALLERGIES_ENTRYDATE);
														String startDate = partialDateDao.getDatePartial(allergy.getStartDate(), PartialDate.TABLE_ALLERGIES, allergy.getAllergyId(), PartialDate.ALLERGIES_STARTDATE);
											%>
											<tr bgcolor="<%=trColour%>" id="allergy_<%= allergy.getAllergyId() %>" >
												<td><%=labelStatus%></td>
												<td><%=entryDate==null ? "" : entryDate %></td>
												<td <%=title%> ><%=allergy.getDescription() %></td>
												<td><%=allergy.getTypeDesc() %></td>
												<td bgcolor="<%=sevColour%>"><%=allergy.getSeverityOfReactionDesc() %></td>
												<td><%=allergy.getOnSetOfReactionDesc() %></td>
												<td><%=allergy.getReaction() == null ? "" : allergy.getReaction()%></td>
												<td><%=startDate == null ? "" : startDate %></td>
												<td><%=allergy.getLifeStageDesc() %></td>
												<td><%=allergy.getAgeOfOnset() == null ? "" : allergy.getAgeOfOnset()%></td>
												<%
													CaseManagementManager cmm = (CaseManagementManager) SpringUtils.getBean("caseManagementManager");
													@SuppressWarnings("unchecked")

													int numAnnotations = (cmm.getLinkByTableId(CaseManagementNoteLink.ALLERGIES, Long.valueOf(allergy.getAllergyId()))).size();
												%>
												<td>
													<%
														if (!allergy.isIntegratorResult())
														{
													%>
													<a href="#" title="Annotation" onclick="window.open('../annotation/annotation.jsp?display=<%=annotation_display%>&table_id=<%=String.valueOf(allergy.getAllergyId())%>&demo=<jsp:getProperty name="patient" property="demographicNo"/>','anwin','width=400,height=500');">
														<%
															if(numAnnotations > 0)
															{
														%>
														<img alt="filled notes" src="../images/filledNotes.gif" border="0"/>
														<%
															}
															else
															{
														%>
														<img alt="note" src="../images/notes.gif" border="0">
														<%	} %>
													</a>
													<%	} %>
												</td>

												<td>
													<%
														if (!allergy.isIntegratorResult() && securityManager.hasDeleteAccess("_allergies", roleName$))
														{
															if (intArchived == 0)
															{
													%>
													<a href="#"
													   class="deleteAllergyLink"
													   id="deleteAllergy:<%=labelAction%>_ID=<%=allergy.getAllergyId() %>&demographicNo=<%=demographicNo%>&action=<%=actionPath %>">
														<%=labelAction%></a>

													|
													<%		} %>
													<a href="#" class="modifyAllergyLink"
													   id="modifyAllergy:<%= labelAction %>_ID=<%=allergy.getDrugrefId() %>&name=<%=allergy.getDescription() %>&type=<%=allergy.getTypeCode() %>&allergyToArchive=<%=allergy.getId() %>" >
														<%=intArchived==0 ? "Modify" : labelAction%>
													</a>
													<%	} %>
												</td>
											</tr>
											<%
													}
												} //end of iterate
											if (hasDrugAllergy)
											{
												iNKDA = 0;
											}
											%>

										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>

					<tr id="addAllergyInterface">
						<td>
							<form action="/oscarRx/searchAllergy2.do"
								  id="searchAllergy2"
								  onsubmit="triggerSearchRequest();">
								<input type="hidden" name="iNKDA" value="<%=iNKDA%>"/>
								<input type="hidden" name="hasDrugAllergy" value="<%=hasDrugAllergy%>"/>
								<!-- Duplicated fields so the jquery form submission is more idiomatic -->
								<input type="hidden" id="inputDrugClass" name="typeDrugClass" value="true" />
								<input type="hidden" id="inputIngredient" name="typeIngredient" value="false" />
								<input type="hidden" id="inputGenericName" name="typeGenericName" value="false" />
								<input type="hidden" id="inputBrandName" name="typeBrandName" value="false" />
								<input type="hidden" id="inputSearchString" name="searchString" value="" />
							</form>
							<table>
								<tr>
									<th>Add an Allergy</th>
								</tr>
								<tr id="allergyQuickButtonRow">
									<td>
										<button type=button class="ControlPushButton" onclick="addCustomNKDA();">
											NKDA
										</button>
										<button type=button class="ControlPushButton" onclick="addPenicillinAllergy();">
											Penicillin
										</button>
										<button type=button class="ControlPushButton" onclick="addSulfonamideAllergy();">
											Sulfa
										</button>
									</td>
								</tr>
								<tr>
									<td id="addAllergyDialogue"></td>
								</tr>
								<tr id="allergySearchCriteriaRow">
									<td>
										<div id="allergySearchSelectors">
											<input type="checkbox" checked name="typeDrugClass" id="typeDrugClass" onchange="toggleInput('inputDrugClass', this)"/>
											<label for="typeDrugClass" >Drug Classes</label>
											<input type="checkbox" name="typeIngredient" id="typeIngredient" onchange="toggleInput('inputIngredient', this)"/>
											<label for="typeIngredient" >Ingredients</label>
											<input type="checkbox" checked name="typeGenericName" id="typeGenericName" onchange="toggleInput('inputGenericName', this)"/>
											<label for="typeGenericName" >Generic Names</label>
											<input type="checkbox" checked name="typeBrandName" id="typeBrandName" onchange="toggleInput('inputBrandName', this)"/>
											<label for="typeBrandName" >Brand Names</label>
											<input type="checkbox" name="typeSelectAll" id="typeSelectAll" />
											<label for="typeSelectAll" >All</label>
										</div>
										<input type="text" name="searchString" value="" size="16" id="searchString" maxlength="16" />
										<button type="submit" value="Search" id="searchStringButton" class="ControlPushButton">
											Search
										</button>
										OR
										<button type=button class="ControlPushButton" onclick="addCustomAllergy();" value="Custom Allergy">
											Custom Allergy
										</button>
									</td>
								</tr>
							</table>
						</td>
					</tr>

					<tr>
						<td id="searchResultsContainer" ></td>
					</tr>

				</table>
			</td>
		</tr>

		<tr class="lastRow">
			<td colspan="2"></td>
		</tr>
	</table>
	</body>
</html:html>