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
<%@ page
	import="java.util.*, oscar.oscarEncounter.oscarMeasurements.data.MeasurementMapConfig, oscar.OscarProperties, oscar.util.StringUtils"%>

<%

%>

<link rel="stylesheet" type="text/css"
	href="../../oscarMDS/encounterStyles.css">

<html>
<head>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
<title>Measurement Mapping Configuration</title>

<script type="text/javascript" language=javascript>
            
            function newWindow(varpage, windowname){
                var page = varpage;
                windowprops = "fullscreen=yes,toolbar=yes,directories=no,resizable=yes,dependent=yes,scrollbars=yes,location=yes,status=yes,menubar=yes";
                var popup=window.open(varpage, windowname, windowprops);
            }
            
            function addLoinc(){
                var loinc_code = document.LOINC.loinc_code.value;
                var name = document.LOINC.name.value;
                
                if (loinc_code.length > 0 && name.length > 0){
                    if (modCheck(loinc_code)){
                        document.LOINC.identifier.value=loinc_code+',PATHL7,'+name;
                        return true;
                    }
                }else{
                    alert("Please specify both a loinc code and a name before adding.");
                }
                
                return false;
            }

			/**
			 * Checks to see if a lonic code is valid. Lonic code validations are done by a mod 10
			 * check digit algorithm (https://en.wikipedia.org/wiki/Luhn_algorithm). Where, in the
			 * lonic code, the number before the dash is the code and the number after is the check
			 * digit.
			 * @param {String} code - A lonic code
			 * @return {Boolean} True or False depending if the lonic code is valid or not
			 */
            function modCheck(code){
                if (code.charAt(0) == 'x' || code.charAt(0) == 'X'){
                    return true;
                }else{

                    var codeArray = new Array();
                    codeArray = code.split('-');

                    //Validate the format of the Lonic code before validating that it's a correct code
					if(codeArray.length != 2 || codeArray[0].length < 2 || codeArray[1].length > 1) {
						alert("Invalid lonic code format, please start the code with an 'X' if you would like to make your own.");
						return false;
					}

                    var length = codeArray[0].length;
                    var sum = 0;
					var codeIntArray = new Array();

                    for(var i = 0; i < length; i++) {
                    	codeIntArray[i] = parseInt(codeArray[0].charAt(i));
					}

					//Double every other digit starting from rightmost digit
					for (var i = codeIntArray.length-1; i >= 0; i-=2){
						codeIntArray[i] = codeIntArray[i]*2;

						//If doubling is greater than 10, sum the digits(same as subtracting 9)
						if(codeIntArray[i] >= 10) {
							codeIntArray[i] = codeIntArray[i] - 9;
						}
					}

					for (var i = 0; i < codeIntArray.length; i++){
						sum = sum + codeIntArray[i];
					}

					sum = sum * 9;

					var checkDigit = sum % 10;

                    if (checkDigit == codeArray[1]){
                        return true;
                    }else{
                        alert("The loinc code specified is not a valid loinc code, please start the code with an 'X' if you would like to make your own.");
                        return false;
                    }
                    
                }
            
            }
            
            <%String outcome = request.getParameter("outcome");
            if (outcome != null){
                if (outcome.equals("success")){
                    %>
                      alert("Successfully added loinc code");
                      window.opener.location.reload()
                      window.close();
                    <%
                }else if (outcome.equals("failedcheck")){
                    %>
                      alert("Unable to add code: The specified code already exists in the database");
                    <%
                }else{    
                    %>
                      alert("Failed to add the new code");
                    <%
                }   
            }%>

        </script>
<link rel="stylesheet" type="text/css" media="all" href="../share/css/extractedFromPages.css"  />
</head>

<body>
<form method="post" name="LOINC" action="NewMeasurementMap.do"><input
	type="hidden" name="identifier" value="">
<table width="100%" height="100%" border="0">
	<tr class="MainTableTopRow">
		<td class="MainTableTopRow" colspan="9" align="left">
		<table width="100%">
			<tr>
				<td align="left"><input type="button"
					value=" <bean:message key="global.btnClose"/> "
					onClick="window.close()"></td>
				<td align="right"><oscar:help keywords="measurement" key="app.top1"/> | <a
					href="javascript:popupStart(300,400,'../About.jsp')"><bean:message
					key="global.about" /></a> | <a
					href="javascript:popupStart(300,400,'../License.jsp')"><bean:message
					key="global.license" /></a></td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td valign="middle">
		<center>
		<table width="80%">
			<tr>
				<td colspan="2" valign="bottom" class="Header">Add New Loinc
				Code</td>
			</tr>
			<tr>
				<td class="Cell" width="20%">Loinc Code:</td>
				<td class="Cell" width="80%"><input type="text"
					name="loinc_code"></td>
			</tr>
			<tr>
				<td class="Cell" width="20%">Name:</td>
				<td class="Cell" width="80%"><input type="text" name="name"></td>
			</tr>
			<tr>
				<td colspan="2" class="Cell" align="center"><input
					type="submit" value=" Add Loinc Code " onclick="return addLoinc()">
				</td>
			</tr>
			<tr>
				<td colspan="2" class="Cell" align="center">NOTE: <a
					href="javascript:newWindow('http://www.regenstrief.org/medinformatics/loinc/relma','RELMA')">It
				is suggested that you use the RELMA application to help determine
				correct loinc codes.</a></td>
			</tr>
		</table>
		</center>
		</td>
	</tr>
</table>
</form>
</body>
</html>
