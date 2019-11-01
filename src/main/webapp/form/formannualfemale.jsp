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
    String roleName2$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
    boolean authed=true;
%>
<security:oscarSec roleName="<%=roleName2$%>" objectName="_form" rights="r" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect("../securityError.jsp?type=_form");%>
</security:oscarSec>
<%
	if(!authed) {
		return;
	}
%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ page import="oscar.form.*"%>
<%@page import="org.oscarehr.util.LoggedInInfo"%>
<html:html locale="true">
<head>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
	<script type="text/javascript" src="OscarFormHelpers.js"></script>
	<script src="<%= request.getContextPath() %>/share/javascript/jquery/jquery-2.2.4.min.js"></script>
	<title><bean:message key="oscarEncounter.formFemaleAnnual.title" /></title>
<link rel="stylesheet" type="text/css" href="annualStyle.css">
<link rel="stylesheet" type="text/css" media="print" href="print.css">
<html:base />
</head>

<script type="text/javascript" language="Javascript">
    function onPrint() {
        var ret = checkAllDates();
        if(ret==true)
        {
            window.print();
        }
        return ret;
    }
    function onSave() {
        document.forms[0].submit.value="save";
        Oscar.FormHelpers.forceSubmitUncheckedCheckboxes();
        var ret = checkAllDates();
        if(ret==true)
        {
            ret = confirm("<bean:message key="oscarEncounter.formFemaleAnnual.msgWannaSave"/>");
        }
        return ret;
    }
    
    function onSaveExit() {
        document.forms[0].submit.value="exit";
        Oscar.FormHelpers.forceSubmitUncheckedCheckboxes();
        var ret = checkAllDates();
        if(ret == true)
        {
            ret = confirm("<bean:message key="oscarEncounter.formFemaleAnnual.msgSaveExit"/>");
        }
        return ret;
    }
/**
 * DHTML date validation script. Courtesy of SmartWebby.com (http://www.smartwebby.com/dhtml/)
 */
// Declaring valid date character, minimum year and maximum year
var dtCh= "/";
var minYear=1900;
var maxYear=3100;

    function isInteger(s){
        var i;
        for (i = 0; i < s.length; i++){
            // Check that current character is number.
            var c = s.charAt(i);
            if (((c < "0") || (c > "9"))) return false;
        }
        // All characters are numbers.
        return true;
    }

    function stripCharsInBag(s, bag){
        var i;
        var returnString = "";
        // Search through string's characters one by one.
        // If character is not in bag, append to returnString.
        for (i = 0; i < s.length; i++){
            var c = s.charAt(i);
            if (bag.indexOf(c) == -1) returnString += c;
        }
        return returnString;
    }

    function daysInFebruary (year){
        // February has 29 days in any year evenly divisible by four,
        // EXCEPT for centurial years which are not also divisible by 400.
        return (((year % 4 == 0) && ( (!(year % 100 == 0)) || (year % 400 == 0))) ? 29 : 28 );
    }
    function DaysArray(n) {
        for (var i = 1; i <= n; i++) {
            this[i] = 31
            if (i==4 || i==6 || i==9 || i==11) {this[i] = 30}
            if (i==2) {this[i] = 29}
       }
       return this
    }

    function isDate(dtStr){
        var daysInMonth = DaysArray(12)
        var pos1=dtStr.indexOf(dtCh)
        var pos2=dtStr.indexOf(dtCh,pos1+1)
        var strMonth=dtStr.substring(0,pos1)
        var strDay=dtStr.substring(pos1+1,pos2)
        var strYear=dtStr.substring(pos2+1)
        strYr=strYear
        if (strDay.charAt(0)=="0" && strDay.length>1) strDay=strDay.substring(1)
        if (strMonth.charAt(0)=="0" && strMonth.length>1) strMonth=strMonth.substring(1)
        for (var i = 1; i <= 3; i++) {
            if (strYr.charAt(0)=="0" && strYr.length>1) strYr=strYr.substring(1)
        }
        month=parseInt(strMonth)
        day=parseInt(strDay)
        year=parseInt(strYr)
        if (pos1==-1 || pos2==-1){
            return "format"
        }
        if (month<1 || month>12){
            return "month"
        }
        if (day<1 || day>31 || (month==2 && day>daysInFebruary(year)) || day > daysInMonth[month]){
            return "day"
        }
        if (strYear.length != 4 || year==0 || year<minYear || year>maxYear){
            return "year"
        }
        if (dtStr.indexOf(dtCh,pos2+1)!=-1 || isInteger(stripCharsInBag(dtStr, dtCh))==false){
            return "date"
        }
    return true
    }


    function checkTypeIn(obj) {
      if(!checkTypeNum(obj.value) ) {
          alert ("<bean:message key="oscarEncounter.formFemaleAnnual.msgTypeANumber"/>");
        }
    }

    function valDate(dateBox)
    {
        try
        {
            var dateString = dateBox.value;
            if(dateString == "")
            {
    //            alert('dateString'+dateString);
                return true;
            }
            var dt = dateString.split('/');
            var y = dt[0];
            var m = dt[1];
            var d = dt[2];
            var orderString = m + '/' + d + '/' + y;
            var pass = isDate(orderString);

            if(pass!=true)
            {
                alert('Invalid '+pass+' in field ' + dateBox.name);
                dateBox.focus();
                return false;
            }
        }
        catch (ex)
        {
            alert('Catch Invalid Date in field ' + dateBox.name);
            dateBox.focus();
            return false;
        }
        return true;
    }

    function checkAllDates()
    {
        var b = true;
        if(valDate(document.forms[0].formDate)==false){
            b = false;
        }else
        if(valDate(document.forms[0].lmp)==false){
            b = false;
        }

        return b;

    }
function popupPage(vheight,vwidth,varpage) { //open a new popup window
  var page = "" + varpage;
  windowprops = "height="+vheight+",width="+vwidth+",location=no,scrollbars=yes,menubars=no,toolbars=no,resizable=yes,top=5,left=5";//360,680
  var popup=window.open(page, "aplan", windowprops);
}

</script>


<%
    String formClass = "Annual";
    String formLink = "formannualfemale.jsp";

    int demoNo = Integer.parseInt(request.getParameter("demographic_no"));
    int formId = Integer.parseInt(request.getParameter("formId"));
    int provNo = Integer.parseInt((String) session.getAttribute("user"));
    FrmRecord rec = (new FrmRecordFactory()).factory(formClass);
    java.util.Properties props = rec.getFormRecord(LoggedInInfo.getLoggedInInfoFromSession(request),demoNo, formId);
%>

<BODY bgproperties="fixed" onLoad="javascript:window.focus()"
	topmargin="0" leftmargin="0" rightmargin="0">
<html:form action="/form/formname">


	<input type="hidden" name="demographic_no"
		value="<%= props.getProperty("demographic_no", "0") %>" />
	<input type="hidden" name="ID"
		value="<%= props.getProperty("ID", "0") %>" />
	<input type="hidden" name="provider_no"
		value=<%=request.getParameter("provNo")%> />
	<input type="hidden" name="formCreated"
		value="<%= props.getProperty("formCreated", "") %>" />
	<input type="hidden" name="form_class" value="<%=formClass%>" />
	<input type="hidden" name="form_link" value="<%=formLink%>" />
	<input type="hidden" name="provNo"
		value="<%= request.getParameter("provNo") %>" />
	<input type="hidden" name="submit" value="exit" />

	<table class="Head" class="hidePrint">
		<tr>
			<td align="left"><input type="submit"
				value="<bean:message key="oscarEncounter.formFemaleAnnual.btnSave"/>"
				onclick="javascript:return onSave();" /> <input type="submit"
				value="<bean:message key="oscarEncounter.formFemaleAnnual.btnSaveExit"/>"
				onclick="javascript:return onSaveExit();" /> <input type="submit"
				value="<bean:message key="oscarEncounter.formFemaleAnnual.btnExit"/>"
				onclick="javascript:return onExit();" /> <input type="button"
				value="<bean:message key="oscarEncounter.formFemaleAnnual.btnPrint"/>"
				onclick="javascript:return onPrint();" /> <input type="button"
				value="<bean:message key="oscarEncounter.formFemaleAnnual.btnPrintPage"/>"
				onclick="javascript: popupPage(700,950,'formannualfemaleprint.jsp?demographic_no=<%=demoNo%>&formId=<%=formId%>&provNo=<%=provNo%>')" />
			</td>
			<td align='right'><a
				href="javascript: popupPage(700,950,'../decision/annualreview/annualreviewplanner.jsp?demographic_no=<%=demoNo%>&formId=<%=formId%>&provNo=<%=provNo%>');"><bean:message
				key="oscarEncounter.formFemaleAnnual.btnAnnualReview" /></a></td>
		</tr>
	</table>

	<table cellspacing="3" cellpadding="0" width="100%">
		<tr>
			<td><big><i><b><bean:message
				key="oscarEncounter.formFemaleAnnual.msgAnnualFemaleReview" /></b></i></big></td>
			<td><b><bean:message
				key="oscarEncounter.formFemaleAnnual.formName" />:</b> <input
				type="text" class="Input" name="pName" readonly="true" size="30"
				value="<%= props.getProperty("pName", "") %>" /></td>
			<td><b><bean:message
				key="oscarEncounter.formFemaleAnnual.formAge" />:</b> <input type="text"
				class="Input" readonly="true" name="age" size="11"
				value="<%= props.getProperty("age", "") %>" readonly="true" /></td>
			<td><b><bean:message
				key="oscarEncounter.formFemaleAnnual.formDate" /></b><small>(yyyy/mm/dd)</small>:
			<input type="text" class="Input" name="formDate" size="11"
				value="<%=props.getProperty("formDate", "") %>" /></td>
		</tr>
	</table>
	<table width="100%">
		<tr>
			<td rowspan="4">
			<table class="DashedBorder" width="100%">
				<tr>
					<td><b><bean:message
						key="oscarEncounter.formFemaleAnnual.msgCurrentConcerns" />:</b></td>
				</tr>
				<tr>
					<td><textarea style="height: 480px; width: 400px;"
						name="currentConcerns"><%= props.getProperty("currentConcerns", "") %></textarea></td>
				</tr>
				<tr>
					<td align="center"><bean:message
						key="oscarEncounter.formFemaleAnnual.msgSeeChart" />
					&nbsp;&nbsp;&nbsp; <input type="checkbox" name="currentConcernsNo"
						<%= props.getProperty("currentConcernsNo", "") %> /> &nbsp;<bean:message
						key="oscarEncounter.formFemaleAnnual.brtNo" />&nbsp;&nbsp;&nbsp; <input
						type="checkbox" name="currentConcernsYes"
						<%= props.getProperty("currentConcernsYes", "") %> /> &nbsp;<bean:message
						key="oscarEncounter.formFemaleAnnual.btnYes" /></td>
				</tr>
			</table>
			</td>
			<td>
			<table width="100%">
				<tr>
					<td colspan="3"><b><bean:message
						key="oscarEncounter.formFemaleAnnual.msgSystemReview" />:</b></td>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td><b><bean:message
						key="oscarEncounter.formFemaleAnnual.formN" /></b></td>
					<td colspan="2"><b><bean:message
						key="oscarEncounter.formFemaleAnnual.formAbN" /></b></td>
				</tr>
				<tr>
					<td><input type="checkbox" name="headN"
						<%= props.getProperty("headN", "") %> /></td>
					<td><input type="checkbox" name="headAbN"
						<%= props.getProperty("headAbN", "") %> /></td>
					<td align="left" nowrap="true"><bean:message
						key="oscarEncounter.formFemaleAnnual.formHeadNeck" />:</td>
					<td align="right"><input type="text" name="head"
						class="SystemsReview" value="<%= props.getProperty("head", "") %>" /></td>
				</tr>
				<tr>
					<td><input type="checkbox" name="respN"
						<%= props.getProperty("respN", "") %> /></td>
					<td><input type="checkbox" name="respAbN"
						<%= props.getProperty("respAbN", "") %> /></td>
					<td><bean:message
						key="oscarEncounter.formFemaleAnnual.fomrResp" />:</td>
					<td align="right"><input type="text" name="resp"
						class="SystemsReview" value="<%= props.getProperty("resp", "") %>" /></td>
				</tr>
				<tr>
					<td><input type="checkbox" name="cardioN"
						<%= props.getProperty("cardioN", "") %> /></td>
					<td><input type="checkbox" name="cardioAbN"
						<%= props.getProperty("cardioAbN", "") %> /></td>
					<td><bean:message
						key="oscarEncounter.formFemaleAnnual.formCardio" />:</td>
					<td align="right"><input type="text" name="cardio"
						class="SystemsReview"
						value="<%= props.getProperty("cardio", "") %>" /></td>
				</tr>
				<tr>
					<td><input type="checkbox" name="giN"
						<%= props.getProperty("giN", "") %> /></td>
					<td><input type="checkbox" name="giAbN"
						<%= props.getProperty("giAbN", "") %> /></td>
					<td><bean:message key="oscarEncounter.formFemaleAnnual.formGI" />:</td>
					<td align="right"><input type="text" name="gi"
						class="SystemsReview" value="<%= props.getProperty("gi", "") %>" /></td>
				</tr>
				<tr>
					<td><input type="checkbox" name="guN"
						<%= props.getProperty("guN", "") %> /></td>
					<td><input type="checkbox" name="guAbN"
						<%= props.getProperty("guAbN", "") %> /></td>
					<td><bean:message key="oscarEncounter.formFemaleAnnual.formGU" />:</td>
					<td align="right"><input type="text" name="gu"
						class="SystemsReview" value="<%= props.getProperty("gu", "") %>" /></td>
				</tr>
				<tr>
					<td><input type="checkbox" name="skinN"
						<%= props.getProperty("skinN", "") %> /></td>
					<td><input type="checkbox" name="skinAbN"
						<%= props.getProperty("skinAbN", "") %> /></td>
					<td><bean:message
						key="oscarEncounter.formFemaleAnnual.formSkin" />:</td>
					<td colspan="3" align="right"><input type="text" name="skin"
						class="SystemsReview" value="<%= props.getProperty("skin", "") %>" /></td>
				</tr>
				<tr>
					<td><input type="checkbox" name="mskN"
						<%= props.getProperty("mskN", "") %> /></td>
					<td><input type="checkbox" name="mskAbN"
						<%= props.getProperty("mskAbN", "") %> /></td>
					<td><bean:message
						key="oscarEncounter.formFemaleAnnual.formMSK" />:</td>
					<td colspan="3" align="right"><input type="text" name="msk"
						class="SystemsReview" value="<%= props.getProperty("msk", "") %>" /></td>
				</tr>
				<tr>
					<td><input type="checkbox" name="endocrinN"
						<%= props.getProperty("endocrinN", "") %> /></td>
					<td><input type="checkbox" name="endocrinAbN"
						<%= props.getProperty("endocrinAbN", "") %> /></td>
					<td><bean:message
						key="oscarEncounter.formFemaleAnnual.formEndocrin" />:</td>
					<td colspan="3" align="right"><input type="text"
						name="endocrin" class="SystemsReview"
						value="<%= props.getProperty("endocrin", "") %>" /></td>
				</tr>
				<tr>
					<td valign="top"><input type="checkbox" name="otherN"
						<%= props.getProperty("otherN", "") %> /></td>
					<td valign="top"><input type="checkbox" name="otherAbN"
						<%= props.getProperty("otherAbN", "") %> /></td>
					<td valign="top"><bean:message
						key="oscarEncounter.formFemaleAnnual.formOther" />:</td>
					<td colspan="3" align="right"><textarea name="other"
						class="SystemsReview" style="height: 50px;"><%= props.getProperty("other", "") %></textarea></td>
				</tr>

			</table>
			</td>
		</tr>
		<tr>
			<td>
			<table width="100%">
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td nowrap="true"><bean:message
						key="oscarEncounter.formFemaleAnnual.msgGTPALRevisions" />&nbsp; <input
						type="checkbox" name="noGtpalRevisions"
						<%= props.getProperty("noGtpalRevisions", "") %> /> <bean:message
						key="oscarEncounter.formFemaleAnnual.brtNo" /> <input
						type="checkbox" name="yesGtpalRevisions"
						<%= props.getProperty("yesGtpalRevisions", "") %> /> <bean:message
						key="oscarEncounter.formFemaleAnnual.btnYes" /> <input
						type="checkbox" name="frontSheet"
						<%= props.getProperty("frontSheet", "") %> /> <bean:message
						key="oscarEncounter.formFemaleAnnual.formFrontSheeyUpdated" /></td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td><bean:message
						key="oscarEncounter.formFemaleAnnual.formLMP" /><small>(yyyy/mm/dd)</small>:
					<input type="text" name="lmp"
						value="<%= props.getProperty("lmp", "") %>" size="11" />
					&nbsp;&nbsp;&nbsp; <bean:message
						key="oscarEncounter.formFemaleAnnual.formMenopause" />: <input
						type="text" name="menopause" size="3" maxlength="3"
						value="<%= props.getProperty("menopause", "") %>" /> /<bean:message
						key="oscarEncounter.formFemaleAnnual.formMenopauseUnit" /></td>
				</tr>
				<tr>
					<td><input type="checkbox" name="papSmearsN"
						<%= props.getProperty("papSmearsN", "") %> /></td>
					<td><input type="checkbox" name="papSmearsAbN"
						<%= props.getProperty("papSmearsAbN", "") %> /></td>
					<td nowrap="true"><bean:message
						key="oscarEncounter.formFemaleAnnual.formPreviousPap" />: <input
						type="text" name="papSmears" style="width: 285px;"
						value="<%= props.getProperty("papSmears", "") %>" /></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td>
			<table>
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td colspan="4"><bean:message
						key="oscarEncounter.formFemaleAnnual.forReview" />:</td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td><input type="checkbox" name="drugs"
						<%= props.getProperty("drugs", "") %> /></td>
					<td><bean:message
						key="oscarEncounter.formFemaleAnnual.formDrugs" /></td>
					<td style="width: 190px;">&nbsp;</td>
					<td align="right"><input type="checkbox" name="medSheet"
						<%= props.getProperty("medSheet", "") %> /></td>
					<td><bean:message
						key="oscarEncounter.formFemaleAnnual.formMedSheet" /></td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td><input type="checkbox" name="allergies"
						<%= props.getProperty("allergies", "") %> /></td>
					<td colspan="2" nowrap="true"><bean:message
						key="oscarEncounter.formFemaleAnnual.formAllergies" /></td>
					<td align="right"><input type="checkbox" name="frontSheet1"
						<%= props.getProperty("frontSheet1", "") %> /></td>
					<td><bean:message
						key="oscarEncounter.formFemaleAnnual.formFrontSheet" /></td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td><input type="checkbox" name="familyHistory"
						<%= props.getProperty("familyHistory", "") %> /></td>
					<td colspan="2"><bean:message
						key="oscarEncounter.formFemaleAnnual.formFamilyHist" /></td>
					<td align="right"><input type="checkbox" name="frontSheet2"
						<%= props.getProperty("frontSheet2", "") %> /></td>
					<td><bean:message
						key="oscarEncounter.formFemaleAnnual.formFrontSheet" /></td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
	<table width="100%">
		<tr>
			<td class="DashedBorder">
			<table>
				<tr>
					<td colspan="3" nowrap="true"><b><bean:message
						key="oscarEncounter.formFemaleAnnual.msgLifestyleReview" />:</b></td>
					<td><b><i><small>("<bean:message
						key="oscarEncounter.formFemaleAnnual.msgAnyConcerns" />")</small></i></b></td>
				</tr>
				<tr>
					<td><bean:message key="oscarEncounter.formFemaleAnnual.formNo" /></td>
					<td colspan="2"><bean:message
						key="oscarEncounter.formFemaleAnnual.formYes" /></td>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td><input type="checkbox" name="smokingNo"
						<%= props.getProperty("smokingNo", "") %> /></td>
					<td><input type="checkbox" name="smokingYes"
						<%= props.getProperty("smokingYes", "") %> /></td>
					<td><bean:message
						key="oscarEncounter.formFemaleAnnual.formSmoking" />:</td>
					<td align="right"><input type="text" name="smoking"
						class="LifestyleReview"
						value="<%= props.getProperty("smoking", "") %>" /></td>
				</tr>
				<tr>
					<td><input type="checkbox" name="alcoholNo"
						<%= props.getProperty("alcoholNo", "") %> /></td>
					<td><input type="checkbox" name="alcoholYes"
						<%= props.getProperty("alcoholYes", "") %> /></td>
					<td><bean:message
						key="oscarEncounter.formFemaleAnnual.formAlcohol" />:</td>
					<td align="right"><input type="text" name="alcohol"
						class="LifestyleReview"
						value="<%= props.getProperty("alcohol", "") %>" /></td>
				</tr>
				<tr>
					<td><input type="checkbox" name="otcNo"
						<%= props.getProperty("otcNo", "") %> /></td>
					<td><input type="checkbox" name="otcYes"
						<%= props.getProperty("otcYes", "") %> /></td>
					<td><bean:message
						key="oscarEncounter.formFemaleAnnual.formIllicitDrugs" />:</td>
					<td align="right"><input type="text" name="otc"
						class="LifestyleReview"
						value="<%= props.getProperty("otc", "") %>" /></td>
				</tr>
				<tr>
					<td><input type="checkbox" name="exerciseNo"
						<%= props.getProperty("exerciseNo", "") %> /></td>
					<td><input type="checkbox" name="exerciseYes"
						<%= props.getProperty("exerciseYes", "") %> /></td>
					<td><bean:message
						key="oscarEncounter.formFemaleAnnual.formExercise" /></td>
					<td align="right"><input type="text" name="exercise"
						class="LifestyleReview"
						value="<%= props.getProperty("exercise", "") %>" /></td>
				</tr>
				<tr>
					<td><input type="checkbox" name="nutritionNo"
						<%= props.getProperty("nutritionNo", "") %> /></td>
					<td><input type="checkbox" name="nutritionYes"
						<%= props.getProperty("nutritionYes", "") %> /></td>
					<td><bean:message
						key="oscarEncounter.formFemaleAnnual.formNutrition" />:</td>
					<td align="right"><input type="text" name="nutrition"
						class="LifestyleReview"
						value="<%= props.getProperty("nutrition", "") %>" /></td>
				</tr>
				<tr>
					<td><input type="checkbox" name="dentalNo"
						<%= props.getProperty("dentalNo", "") %> /></td>
					<td><input type="checkbox" name="dentalYes"
						<%= props.getProperty("dentalYes", "") %> /></td>
					<td><bean:message
						key="oscarEncounter.formFemaleAnnual.formDentalHygiene" />:</td>
					<td align="right"><input type="text" name="dental"
						class="LifestyleReview"
						value="<%= props.getProperty("dental", "") %>" /></td>
				</tr>
				<tr>
					<td valign="top"><input type="checkbox" name="relationshipNo"
						<%= props.getProperty("relationshipNo", "") %> /></td>
					<td valign="top"><input type="checkbox" name="relationshipYes"
						<%= props.getProperty("relationshipYes", "") %> /></td>
					<td valign="top"><bean:message
						key="oscarEncounter.formFemaleAnnual.formRelationship" />:</td>
					<td align="right"><textarea name="relationship"
						class="LifestyleReview" rows="2"><%= props.getProperty("relationship", "") %></textarea></td>
				</tr>
				<tr>
					<td><input type="checkbox" name="sexualityNo"
						<%= props.getProperty("sexualityNo", "") %> /></td>
					<td><input type="checkbox" name="sexualityYes"
						<%= props.getProperty("sexualityYes", "") %> /></td>
					<td nowrap="true"><bean:message
						key="oscarEncounter.formFemaleAnnual.formSexualityRisks" />:</td>
					<td align="right"><input type="text" name="sexuality"
						class="LifestyleReview"
						value="<%= props.getProperty("sexuality", "") %>" /></td>
				</tr>
				<tr>
					<td><input type="checkbox" name="occupationalNo"
						<%= props.getProperty("occupationalNo", "") %> /></td>
					<td><input type="checkbox" name="occupationalYes"
						<%= props.getProperty("occupationalYes", "") %> /></td>
					<td nowrap="true"><bean:message
						key="oscarEncounter.formFemaleAnnual.formOccupationalRisks" />:</td>
					<td align="right"><input type="text" name="occupational"
						class="LifestyleReview"
						value="<%= props.getProperty("occupational", "") %>" /></td>
				</tr>
				<tr>
					<td><input type="checkbox" name="drivingNo"
						<%= props.getProperty("drivingNo", "") %> /></td>
					<td><input type="checkbox" name="drivingYes"
						<%= props.getProperty("drivingYes", "") %> /></td>
					<td nowrap="true"><bean:message
						key="oscarEncounter.formFemaleAnnual.formDrivingSafety" />:</td>
					<td align="right"><input type="text" name="driving"
						class="LifestyleReview"
						value="<%= props.getProperty("driving", "") %>" /></td>
				</tr>
				<tr>
					<td><input type="checkbox" name="travelNo"
						<%= props.getProperty("travelNo", "") %> /></td>
					<td><input type="checkbox" name="travelYes"
						<%= props.getProperty("travelYes", "") %> /></td>
					<td nowrap="true"><bean:message
						key="oscarEncounter.formFemaleAnnual.formForeignTravel" />:</td>
					<td align="right"><input type="text" name="travel"
						class="LifestyleReview"
						value="<%= props.getProperty("travel", "") %>" /></td>
				</tr>
				<tr>
					<td valign="top"><input type="checkbox" name="otherNo"
						<%= props.getProperty("otherNo", "") %> /></td>
					<td valign="top"><input type="checkbox" name="otherYes"
						<%= props.getProperty("otherYes", "") %> /></td>
					<td nowrap="true" valign="top"><bean:message
						key="oscarEncounter.formFemaleAnnual.formOther" />:</td>
					<td rowspan="3" align="right"><textarea name="otherLifestyle"
						class="LifestyleReview" rows="6"><%= props.getProperty("otherLifestyle", "") %></textarea></td>
				</tr>
			</table>
			</td>
			<td width="100%" valign="top" class="DashedBorder">
			<table width="100%">
				<tr>
					<td width="50%" colspan="2"><b><bean:message
						key="oscarEncounter.formFemaleAnnual.msgScreeningReview" />:</b></td>
				</tr>
				<tr>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td><a
						href="javascript: popupPage(700,950,'../decision/annualreview/annualreviewplanner.jsp?demographic_no=<%=demoNo%>&formId=<%=formId%>&provN
o=<%=provNo%>');"><bean:message
						key="oscarEncounter.formFemaleAnnual.btnRisk" /></a></td>
				</tr>
				<!--tr>
                    <td><input type="checkbox" name="mammogram" <%= props.getProperty("mammogram", "") %> /></td>
                    <td><bean:message key="oscarEncounter.formFemaleAnnual.formMammogram"/></td>
                </tr>
                <tr>
                    <td><input type="checkbox" name="breast" <%= props.getProperty("breast", "") %> /></td>
                    <td><bean:message key="oscarEncounter.formFemaleAnnual.formBreastSelf"/></td>
                </tr>
                <tr>
                    <td><input type="checkbox" name="pap" <%= props.getProperty("pap", "") %> /></td>
                    <td><bean:message key="oscarEncounter.formFemaleAnnual.formPapSmear"/></td>
                </tr>
                <tr>
                    <td><input type="checkbox" name="femaleImmunization" <%= props.getProperty("femaleImmunization", "") %> /></td>
                    <td><bean:message key="oscarEncounter.formFemaleAnnual.formImmunization"/></td>
                </tr>
                <tr>
                    <td><input type="checkbox" name="precontraceptive" <%= props.getProperty("precontraceptive", "") %> /></td>
                    <td nowrap="true"><bean:message key="oscarEncounter.formFemaleAnnual.formPrecontraceptive"/></td>
                </tr>
                <tr>
                    <td><input type="checkbox" name="femaleCardiac" <%= props.getProperty("femaleCardiac", "") %> /></td>
                    <td><bean:message key="oscarEncounter.formFemaleAnnual.formCardiacRiskFactors"/></td>
                </tr>
                <tr>
                    <td><input type="checkbox" name="osteoporosis" <%= props.getProperty("osteoporosis", "") %> /></td>
                    <td><bean:message key="oscarEncounter.formFemaleAnnual.formOsteoporosisRisk"/></td>
                </tr>
                <tr>
                    <td><input type="checkbox" name="femaleOther1c" <%= props.getProperty("femaleOther1c", "") %> /></td>
                    <td><input type="text" name="femaleOther1" class="ScreeningReview" value="<%= props.getProperty("femaleOther1", "") %>" /></td>
                </tr>
                <tr>
                    <td><input type="checkbox" name="femaleOther2c" <%= props.getProperty("femaleOther2c", "") %> /></td>
                    <td><input type="text" name="femaleOther2" class="ScreeningReview" value="<%= props.getProperty("femaleOther2", "") %>" /></td>
                </tr-->
			</table>
			</td>
		</tr>
	</table>
	<table width="100%" class="tableWithBorder">
		<tr>
			<td colspan="9"><b><bean:message
				key="oscarEncounter.formFemaleAnnual.msgPhysicalExam" />:</b></td>
		</tr>
		<tr>
			<td><b><bean:message
				key="oscarEncounter.formFemaleAnnual.msgVitals" />: </b></td>
			<td><bean:message key="oscarEncounter.formFemaleAnnual.formBP" />:
			<input type="text" name="bprTop" size="5" maxlength="3"
				value="<%= props.getProperty("bprTop", "") %>" />/ <input
				type="text" name="bprBottom" size="5" maxlength="3"
				value="<%= props.getProperty("bprBottom", "") %>" /> <bean:message
				key="oscarEncounter.formFemaleAnnual.msgR" /></td>
			<td align="right"><bean:message
				key="oscarEncounter.formFemaleAnnual.formPulse" />:</td>
			<td><input type="text" name="pulse" size="10" maxlength="10"
				value="<%= props.getProperty("pulse", "") %>" /> /min</td>
			<td align="right"><bean:message
				key="oscarEncounter.formFemaleAnnual.formHeight" />:</td>
			<td><input type="text" name="height" size="10" maxlength="4"
				value="<%= props.getProperty("height", "") %>" /> <bean:message
				key="oscarEncounter.formFemaleAnnual.HeightUnit" /></td>
			<td align="right"><bean:message
				key="oscarEncounter.formFemaleAnnual.formWeight" />: <input
				type="text" name="weight" size="10" maxlength="4"
				value="<%= props.getProperty("weight", "") %>" /> <bean:message
				key="oscarEncounter.formFemaleAnnual.formWeightUnit" /></td>
		<tr>
			<td>&nbsp;</td>
			<td><bean:message key="oscarEncounter.formFemaleAnnual.formBP" />:
			<input type="text" name="bplTop" size="5" maxlength="3"
				value="<%= props.getProperty("bplTop", "") %>" />/ <input
				type="text" name="bplBottom" size="5" maxlength="3"
				value="<%= props.getProperty("bplBottom", "") %>" /> <bean:message
				key="oscarEncounter.formFemaleAnnual.msgL" /></td>
			<td align="right"><bean:message
				key="oscarEncounter.formFemaleAnnual.formRhythm" />:</td>
			<td><input type="text" name="rhythm" size="10" maxlength="10"
				value="<%= props.getProperty("rhythm", "") %>" /></td>
			<td align="right"><bean:message
				key="oscarEncounter.formFemaleAnnual.formUrineDipstick" />:</td>
			<td><input type="text" name="urine" size="20" maxlength="30"
				value="<%= props.getProperty("urine", "") %>" /></td>
		</tr>
	</table>
	<table style="page-break-before: always;" width="100%">
		<tr>
			<td rowspan="3">
			<table width="100%" class="DashedBorder">
				<tr>
					<td><b> <bean:message
						key="oscarEncounter.formFemaleAnnual.msgPhysicalExam" />: </b></td>
				</tr>
				<tr>
					<td><textarea name="physicalSigns" class="PhysicalSigns"><%= props.getProperty("physicalSigns", "") %></textarea></td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
	<table width="100%" class="TableWithBorder">
		<tr>
			<td>
			<table width="100%">
				<tr>
					<td><b><bean:message
						key="oscarEncounter.formFemaleAnnual.formAssessment" /></b></td>
				</tr>
				<tr>
					<td align="center"><textarea name="assessment"
						class="AssessmentPlan"><%= props.getProperty("assessment", "") %></textarea></td>
				</tr>
			</table>
			</td>
			<td>
			<table width="100%">
				<tr>
					<td align="center"><b><bean:message
						key="oscarEncounter.formFemaleAnnual.formPlan" /></b></td>
				</tr>
				<tr>
					<td align="center"><textarea name="plan"
						class="AssessmentPlan"><%= props.getProperty("plan", "") %></textarea></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td colspan="2" align="right"><bean:message
				key="oscarEncounter.formFemaleAnnual.formSignature" />: <input
				type="text" name="signature" size="30"
				value="<%= props.getProperty("signature", "") %>" /></td>
		</tr>
	</table>
	</td>
	</tr>
	</table>

	<table class="Head" class="hidePrint">
		<tr>
			<td align="left"><input type="submit"
				value="<bean:message key="oscarEncounter.formFemaleAnnual.btnSave"/>"
				onclick="javascript:return onSave();" /> <input type="submit"
				value="<bean:message key="oscarEncounter.formFemaleAnnual.btnSaveExit"/>"
				onclick="javascript:return onSaveExit();" /> <input type="submit"
				value="<bean:message key="oscarEncounter.formFemaleAnnual.btnExit"/>"
				onclick="javascript:return onExit();" /> <input type="button"
				value="<bean:message key="oscarEncounter.formFemaleAnnual.btnPrint"/>"
				onclick="javascript:return onPrint();" /></td>
			<td align='right'><a
				href="javascript: popupPage(700,950,'../decision/annualreview/annualreviewplanner.jsp?demographic_no=<%=demoNo%>&formId=<%=formId%>&provNo=<%=provNo%>');"><bean:message
				key="oscarEncounter.formFemaleAnnual.btnAnnualReview" /></a></td>
		</tr>
	</table>

</html:form>
</body>
</html:html>
