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

<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<html>
    <head>
        <script type="text/javascript" src="<%= request.getContextPath()%>/js/global.js"></script>
        <script type="text/javascript" src="<%= request.getContextPath()%>/js/jquery.js"></script>
        <script type="text/javascript" src="<%= request.getContextPath() %>/share/javascript/HealthCardParser.js"></script>
        <link href="<%=request.getContextPath()%>/css/bootstrap.min.css" rel="stylesheet">
        <title>PATIENT DETAIL INFO</title>
        <link rel="stylesheet" href="../web.css" />
        <script language="JavaScript">
            <!--
            
            function handleSubmit(event) {
            	var cardData = document.forms[0].magneticStripe.value;
                if (cardData.length !== 0)
                {
                    let healthCardData = Oscar.HealthCardParser.parse(cardData);
                    if (healthCardData.data.lastName.length !== 0 || healthCardData.data.firstName.length !== 0)
                    {
                        addCardDataToForm(event.target, healthCardData)
                        return true
                    }
                }
            	
            	alert("I didn't get that.  Try scanning again");
            	document.forms[0].magneticStripe.value = "";
            	return false;
            }

            function addCardDataToForm (form, hcData)
            {
                $(form).append("<input type='hidden' name='firstName' value='" + (hcData.data.firstName !== undefined ? hcData.data.firstName : "") + "'/>")
                $(form).append("<input type='hidden' name='lastName' value='" + (hcData.data.lastName !== undefined ? hcData.data.lastName : "") + "'/>")
                $(form).append("<input type='hidden' name='hin' value='" + (hcData.data.hin !== undefined ? hcData.data.hin : "") + "'/>")
                $(form).append("<input type='hidden' name='hinVer' value='" + (hcData.data.versionCode !== undefined ? hcData.data.versionCode : "") + "'/>")

                $(form).append("<input type='hidden' name='endYear' value='" + (hcData.data.endYear !== undefined ? hcData.data.endYear : "") + "'/>")
                $(form).append("<input type='hidden' name='endMonth' value='" + (hcData.data.endMonth !== undefined ? hcData.data.endMonth : "") + "'/>")
                $(form).append("<input type='hidden' name='endDay' value='" + (hcData.data.endDay !== undefined ? hcData.data.endDay : "") + "'/>")

                $(form).append("<input type='hidden' name='dobYear' value='" + (hcData.data.dobYear !== undefined ? hcData.data.dobYear : "") + "'/>")
                $(form).append("<input type='hidden' name='dobMonth' value='" + (hcData.data.dobMonth !== undefined ? hcData.data.dobMonth : "") + "'/>")
                $(form).append("<input type='hidden' name='dobDay' value='" + (hcData.data.dobDay !== undefined ? hcData.data.dobDay : "") + "'/>")

                $(form).append("<input type='hidden' name='effYear' value='" + (hcData.data.effYear !== undefined ? hcData.data.effYear : "") + "'/>")
                $(form).append("<input type='hidden' name='effMonth' value='" + (hcData.data.effMonth !== undefined ? hcData.data.effMonth: "") + "'/>")
                $(form).append("<input type='hidden' name='effDay' value='" + (hcData.data.effDay !== undefined ? hcData.data.effDay : "") + "'/>")

                $(form).append("<input type='hidden' name='sex' value='" + (hcData.data.sex !== undefined ? hcData.data.sex : "") + "'/>")
            }
            
            function setfocus() {
            	document.forms[0].magneticStripe.focus();
            }
            //-->
        </script>
    </head>
    <body background="../images/gray_bg.jpg" bgproperties="fixed" onLoad="setfocus()" topmargin="0" leftmargin="0" rightmargin="0" onload="setfocus()">

        <table border="0" cellspacing="0" cellpadding="0" width="100%">
            <tr bgcolor="#486ebd">
                <th align=CENTER NOWRAP>
                    <font face="Helvetica" color="#FFFFFF">PATIENT'S DETAIL RECORD</font>
                </th>
            </tr>
        </table>

        <html:form action="/demographic/ValidateSwipeCard" onsubmit="return handleSubmit(event);">

            <div class="container">

                <p class="row">
                    <p class="span">
                        Swipe card
                    </p>
                    <p class="span">
                        <html:text property="magneticStripe" size="79"/>
                    </p>
                </p>

                <p class="row">
                    <p class="span2">
                        <html:submit value="Validate" />
                    </p>
                </p>
            </div>

        </html:form>        
        <br>
        <br>
        <form>
            <input type="button" name="Button" value="Cancel" onclick=self.close();>
        </form>
    </body>
</html>