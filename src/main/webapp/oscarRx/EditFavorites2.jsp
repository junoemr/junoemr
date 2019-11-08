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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page import="oscar.oscarRx.pageUtil.RxSessionBean" %>
<%@ page import="oscar.oscarRx.data.RxPrescriptionData" %>
<%@ page import="oscar.oscarRx.data.RxCodesData" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>

<c:set var="context" value="${pageContext.request.contextPath}" />
<html:html locale="true">
<head>
	<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
	<script type="text/javascript" src="<c:out value="${ctx}/share/javascript/prototype.js"/>"></script>
	<script type="text/javascript" src="<c:out value="${ctx}/share/javascript/screen.js"/>"></script>
	<script type="text/javascript" src="<c:out value="${ctx}/share/javascript/rx.js"/>"></script>
	<script type="text/javascript" src="<c:out value="${ctx}/share/javascript/scriptaculous.js"/>"></script>
	<title>Edit Favorites</title>
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
	<link rel="stylesheet" type="text/css" href="../css/allergies.css">
	<style type="text/css">
		.drugForm {
			background-color: #F5F5F5;
		}
	</style>
</head>



<%
	RxSessionBean rxSessionBean = (RxSessionBean) pageContext.getAttribute("bean");

	RxPrescriptionData rxData = new oscar.oscarRx.data.RxPrescriptionData();
	RxPrescriptionData.Favorite[] favorites = rxData.getFavorites(rxSessionBean.getProviderNo());
	RxCodesData.FrequencyCode[] frequencies = new oscar.oscarRx.data.RxCodesData().getFrequencyCodes();
%>

<script language=javascript>

    function deleteRow(favoriteID, favoriteName) {
        var deleteObj = {favoriteId: favoriteID};
        var endpoint = '/oscarRx/deleteFavorite2.do';

        if (confirm('Are you sure you want to delete favorite: \n' + favoriteName + '?')) {
            new Ajax.Request('<c:out value="${ctx}"/>' + endpoint,
                {
                    method: 'delete',
                    parameters: deleteObj,
                    onSuccess:function(result)
                    {
                        location.reload(true);
                    },
                    onFailure:function(err)
                    {
                        alert("An error occured while deleting");
                    }
                });
        }
    }

    function ajaxUpdateRow(formID, saveNotificationID) {
        var form = document.getElementById(formID)
        var formObj = formToObject(form);
        var endpoint =  '/oscarRx/updateFavorite2.do?method=ajaxEditFavorite';

        if (validateUpdate(formObj))
		{
            new Ajax.Request('<c:out value="${ctx}"/>' + endpoint,
                {
                    method: 'post',
                    parameters: formObj,
                    onSuccess:function(result)
                    {
						var saveElement = document.getElementById(saveNotificationID);
                        saveElement.style.visibility = "visible";
                        saveElement.scrollIntoView();
                    },
                    onFailure:function(err)
                    {
                        alert("An error occured while saving");
                    }
                });
		}
	}

	function formToObject(form)
    {
        var formElements = form.elements;
        var formObj = {};

        for (var i = 0; i < formElements.length; i++)
        {
            var element = formElements[i];
            if (element.type && element.type.toLowerCase() === 'checkbox')
            {
                // Serialize all checkboxes, regardless of whether they are checked or not
                formObj[element.name] = element.checked
            }
            else
            {
                formObj[element.name] = element.value;
            }
        }

        return formObj;
    }

	/**
	 * Pre-validate the form on the front end
	 *
	 * @param formFields: Object containing containing form elements as {name: value} pairs
	 * @returns boolean: true if the form is valid
	 */
	function validateUpdate(formFields)
	{
	    isValid = true;

        if (formFields.favoriteName === '')
        {
            alert('Please enter a favorite name.');
            return !isValid;
        }
        if (formFields.takeMin === '' || isNaN(formFields.takeMin))
        {
            alert('Incorrect entry in field Take Min.');
            return !isValid;
        }
        if (formFields.takeMax === '' || isNaN(formFields.takeMax))
        {
            alert('Incorrect entry in field Take Max.');
            return !isValid;
        }
        if (formFields.duration === '' || isNaN(formFields.duration))
        {
            alert('Incorrect entry in field Duration.');
            return !isValid;
        }
        if (formFields.quantity === '')
        {
            alert('Incorrect entry in field Quantity.');
            return !isValid;
        }
        if (formFields.repeat === '' || isNaN(formFields.repeat))
        {
            alert('Incorrect entry in field Repeat.');
            return !isValid;
        }

        return isValid;
	}

    document.onload = function() {document.forms['DispForm'].reset()}
</script>




<body topmargin="0" leftmargin="0" vlink="#0000FF">
<table border="0" cellpadding="0" cellspacing="0" style="border-collapse: collapse; position: absolute; left: 0; top:0;" bordercolor="#111111" width="100%" id="AutoNumber1" height="100%">
	<%@ include file="TopLinks.jsp"%><!-- Row One included here-->
	<tr>
		<td></td>


		<td width="100%" style="border-left: 2px solid #A9A9A9;" height="100%"
			valign="top">
		<table style="border-collapse: collapse" width="100%">
			<tr>
				<td width="0%" valign="top">
				<div class="DivCCBreadCrumbs"><a href="SearchDrug3.jsp"> <bean:message
					key="SearchDrug.title" /></a> > <b><bean:message
					key="StaticScript.title.EditFavorites" /></b></div>
				</td>
			</tr>

			<!----Start new rows here-->

			<tr>
				<td>
					<div class=DivContentPadding>
						<input type=button value="Back to Search For Drug" class="ControlPushButton" onClick="window.location.href='SearchDrug3.jsp';" />
					</div>
				</td>
			</tr>
			<tr>
				<td>
					<div class="DivContentPadding">
						<div class="DivContentSectionHead">Favorites</div>
					</div>
				</td>
			</tr>

			<tr>
				<td>
				<div class="DivContentPadding">
				<table cellspacing=0 cellpadding=2>
				<%
					for (RxPrescriptionData.Favorite fav : favorites)
					{
						boolean isCustom = fav.getGCN_SEQNO() == 0;
                %>
					<form name="DispForm" id="DispForm_<%=fav.getFavoriteId()%>">
					<!-- Record line 1 -->
					<tr class='tblRow drugForm'>
						<td colspan=2><b>Favorite Name:&nbsp;</b><input type=hidden name="favoriteId" value="<%=fav.getFavoriteId()%>" />
							<input type=text size="50" name="favoriteName" class=tblRow value="<%=fav.getFavoriteName()%>" />&nbsp;&nbsp;&nbsp;
						</td>
						<td>
							<a id="SaveSuccess_<%=fav.getFavoriteId()%>" style="visibility: hidden; color:red">Changes saved!</a>
						</td>
						<td colspan=5>
							<a href="javascript:void(0);" onclick='ajaxUpdateRow("DispForm_<%=fav.getFavoriteId()%>", "SaveSuccess_<%=fav.getFavoriteId()%>")'>Save Changes</a>&nbsp;&nbsp;&nbsp;
							<a href="javascript:deleteRow('<%=fav.getFavoriteId()%>', '<%=fav.getFavoriteName()%>');">Delete Favorite</a>
                        </td>
					</tr>
					<% if(!isCustom) { %>
					<!-- Record line 2 -->
					<tr class='tblRow drugForm'>
						<input type=hidden name="customName" value="" />
						<td><b>Brand Name:&nbsp;</b><%=fav.getBN()%></td>
						<td colspan=5><b>Generic Name:&nbsp;</b><%=fav.getGN()%></td>
					</tr>
					<% } else { %>
					<!-- Record line 2 -->
					<tr class='tblRow drugForm'>
						<td colspan=7>
							<b>Custom Drug Name:&nbsp;</b>
							<input type=text name="customName" class=tblRow size=80 value="<%=fav.getCustomName()%>"/>
						</td>
					</tr>
					<% } %>
					<!-- Record line 3

					We do not use the XHTML compliant selected="selected" and disabled="disabled" in the
					frequency and durationUnit dropdowns because they seem to be ignored in Firefox.
					-->
					<tr class='tblRow drugForm'>
						<td nowrap><b>Take:</b>
							<input type=text name="takeMin" class=tblRow size=3 value="<%=fav.getTakeMin()%>" /> <span>to</span>
							<input type=text name="takeMax" class=tblRow size=3 value="<%=fav.getTakeMax()%>"/>
							<%
								String favFreqCode = fav.getFrequencyCode() != null ? fav.getFrequencyCode() : "";
							%>
							<select name="frequencyCode" class=tblRow>
								<% if (favFreqCode.isEmpty()) { %>
								<option selected disabled value="">Frequency</option>
								<% }
								   for (RxCodesData.FrequencyCode freq : frequencies) {
								   String freqCode = freq.getFreqCode();
								%>
								<option value="<%=freqCode%>" <%=favFreqCode.equals(freqCode) ? "selected" : ""%>><%=freqCode%></option>
								<%
								   }

								String duration = fav.getDuration() != null ? fav.getDuration() : "";
								String durationUnit = fav.getDurationUnit() != null ? fav.getDurationUnit() : "";
								%>
							</select> <b>For:</b>
								<input type=text name="duration" class=tblRow size=3 value="<%=duration%>"/>
								<select name="durationUnit" class=tblRow>
								<% if (durationUnit.isEmpty()) { %>
								<option value="" selected disabled>Duration</option>
								<% } %>
								<option value="D" <%=durationUnit.equals("D") ? "selected" : ""%>>Day(s)</option>
								<option value="W" <%=durationUnit.equals("W") ? "selected" : ""%>>Week(s)</option>
								<option value="M" <%=durationUnit.equals("M") ? "selected" : ""%>>Month(s)</option>
							</select>
						</td>
						<td></td>
						<td nowrap><b>Quantity:</b>
							<input type=text name="quantity" class=tblRow size=5 value="<%=fav.getQuantity()%>"/>
						</td>
						<td></td>
						<td><b>Repeats:</b>
							<input type=text name="repeat" class=tblRow size=3 value="<%=fav.getRepeat()%>"/>
						</td>
						<td><b>No Subs:</b>
							<input type=checkbox name="nosubs" <%=fav.getNosubs() ? "checked" : ""%> class=tblRow size=1/>
						</td>
						<td><b>PRN:</b>
							<input type=checkbox name="prn" <%=fav.getPrn() ? "checked" : ""%> class=tblRow size=1/>
						</td>
					</tr>
					<tr class='drugForm'>
						<td colspan=7>
							<table>
								<tr>
									<td><b>Special Instructions:</b><br/>
										Custom Instructions:&nbsp;<input type="checkbox" name="customInstr" <%= fav.getCustomInstr() ? "checked" : ""%>>
									</td>
									<td width="100%">
										<%
											String specialInst = fav.getSpecial();
										%>
										<textarea name="special" style="width: 100%" rows=5 ><%=StringUtils.trimToEmpty(specialInst)%></textarea></td>
								</tr>
							</table>
						</td>
					</tr>

					<tr class='drugform'>
						<td colspan=7>
							<% boolean dispenseInternal = fav.getDispenseInternal() != null && fav.getDispenseInternal();%>
							Dispense Internally:&nbsp; <input type="checkbox" name="dispenseInternal" <%=dispenseInternal ? "checked" : ""%>>
						</td>
					</tr>

					<tr>
						<td colspan=7 valign=center>
							<hr width=100%>
						</td>
					</tr>
					<tr>
						<td colspan="7"></td>
					</tr>
					<tr>
						<td colspan="7"></td>
					</tr>
					</form>
					<% 	//for i
					}
					%>
				</table>
				</div>
				</td>
			</tr>

			<tr>
				<td>
				<div class=DivContentPadding><input type=button
					value="Back to Search For Drug" class="ControlPushButton"
					onClick="javascript:window.location.href='SearchDrug3.jsp';" /></div>
				</td>
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
