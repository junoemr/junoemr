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

<%@page import="org.oscarehr.common.dao.FlowSheetCustomizationDao"%>
<%@page import="org.oscarehr.common.model.FlowSheetCustomization"%>
<%@page import="oscar.oscarEncounter.oscarMeasurements.MeasurementFlowSheet"%>
<%@page import="oscar.oscarEncounter.oscarMeasurements.MeasurementTemplateFlowSheetConfig"%>
<%@page import="oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementTypesBean"%>
<%@page import="oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementsDataBeanHandler"%>
<%@page import="oscar.oscarEncounter.oscarMeasurements.pageUtil.EctMeasurementsForm"%>
<%@page import="java.util.Hashtable"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="oscar.util.ConversionUtils" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="org.oscarehr.util.SpringUtils" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/oscar-tag.tld" prefix="oscar" %>

<%
	if(session.getValue("user") == null)
	{
		response.sendRedirect("../logout.jsp");
	}
	String demographic_no = request.getParameter("demographic_no");
	String id = request.getParameter("id");
	String[] measurements = request.getParameterValues("measurement");
	String template = request.getParameter("template");
	String uuid = request.getParameter("uuid");

	FlowSheetCustomizationDao flowSheetCustomizationDao = (FlowSheetCustomizationDao) SpringUtils.getBean("flowSheetCustomizationDao");
	MeasurementTemplateFlowSheetConfig templateConfig = MeasurementTemplateFlowSheetConfig.getInstance();

	List<FlowSheetCustomization> custList = flowSheetCustomizationDao.getFlowSheetCustomizations(template, (String) session.getAttribute("user"), Integer.parseInt(demographic_no));
	MeasurementFlowSheet mFlowsheet = templateConfig.getFlowSheet(template, custList);

	String prevDate = ConversionUtils.toDateTimeNoSecString(LocalDateTime.now());
%>


<html:html locale="true">

<head>
<title>
<bean:message key="oscarEncounter.Index.measurements" />
</title><!--I18n-->
<html:base/>
    <link rel="stylesheet" type="text/css" href="../../share/css/OscarStandardLayout.css">
    <link rel="stylesheet" type="text/css" media="all" href="../../share/calendar/calendar.css" title="win2k-cold-1" />

    <script type="text/javascript" src="../../share/calendar/calendar.js" ></script>
    <script type="text/javascript" src="../../share/calendar/lang/<bean:message key="global.javascript.calendar"/>" ></script>
    <script type="text/javascript" src="../../share/calendar/calendar-setup.js" ></script>
    <script type="text/javascript" src="../../share/javascript/prototype.js"></script>

    <style type="text/css">
        div.ImmSet h2 {  }
        div.ImmSet ul {  }
        div.ImmSet li {  }
        div.ImmSet li a { text-decoration:none; color:blue;}
        div.ImmSet li a:hover { text-decoration:none; color:red; }
        div.ImmSet li a:visited { text-decoration:none; color:blue;}

        div.prevention {  background-color: #999999; }
        div.prevention fieldset {width:35em; font-weight:bold; }
        div.prevention legend {font-weight:bold; }

        Body {
            background-color: #fff;
        }

        label {
            float: left;
            width: 120px;
            font-weight: bold;
        }

        label.fields {
            float: left;
            width: 125px;
            font-weight: bold;
        }

        input, textarea, select {
            margin-bottom: 5 px;
        }

        textarea {
            width: 450px;
            height: 100px;
        }

        br {
            clear: left;
        }
    </style>

	<script type="text/javascript">

		function showHideItem(id)
		{
			if (document.getElementById(id).style.display == 'none')
				document.getElementById(id).style.display = '';
			else
				document.getElementById(id).style.display = 'none';
		}

		function showItem(id)
		{
			document.getElementById(id).style.display = '';
		}

		function hideItem(id)
		{
			document.getElementById(id).style.display = 'none';
		}

		function showHideNextDate(id, nextDate, nexerWarn)
		{
			if (document.getElementById(id).style.display == 'none')
			{
				showItem(id);
			}
			else
			{
				hideItem(id);
				document.getElementById(nextDate).value = "";
				document.getElementById(nexerWarn).checked = false;

			}
		}

		function disableifchecked(ele, nextDate)
		{
			if (ele.checked == true)
			{
				document.getElementById(nextDate).disabled = true;
			}
			else
			{
				document.getElementById(nextDate).disabled = false;
			}
		}

		/**
		 * Given some new date, go and update every single input on the page relating to a date.
		 * @param newDate new date value from "master" selector to propagate across page
		 */
		function masterDateFill(newDate)
		{
			var x =<%=measurements.length%>;

			for (var i = 0; i <= x; i++)
			{
				document.getElementById('prevDate' + i).value = newDate;
			}
		}

		function validateSubmit()
        {
            if (opener !== undefined)
            {
                if (!opener.isEchartOriginal())
                {
                    if (!confirm("<bean:message key="oscarEncounter.oscarMeasurements.Measurements.msgParentChanged.start"/>" +
                        "<oscar:nameage demographicNo="<%=demographic_no%>"/> <bean:message key="oscarEncounter.oscarMeasurements.Measurements.msgParentChanged.end"/> "))
                    {
                        return false;
                    }
                }
            }
            return true;
        }

		function hideExtraName(ele)
		{
			if (ele.options[ele.selectedIndex].value != -1)
			{
				hideItem('providerName');
			}
			else
			{
				showItem('providerName');
				document.getElementById('providerName').focus();
			}
		}
	</script>
</head>

<body class="BodyStyle" vlink="#0000FF" onload="Field.focus('value(inputValue-0)');">
    <table  class="MainTable" id="scrollNumber1" name="encounterTable">
        <tr class="MainTableTopRow">
            <td class="MainTableTopRowLeftColumn" width="100" >
               measurement
            </td>
            <td class="MainTableTopRowRightColumn">
                <table class="TopStatusBar">
                    <tr>
                        <td >
                            <oscar:nameage demographicNo="<%=demographic_no%>"/>
                        </td>
                        <td  >&nbsp;

                        </td>
                        <td style="text-align:right">
                                <oscar:help keywords="measurement" key="app.top1"/> | <a href="javascript:popupStart(300,400,'About.jsp')" ><bean:message key="global.about" /></a> | <a href="javascript:popupStart(300,400,'License.jsp')" ><bean:message key="global.license" /></a>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td class="MainTableLeftColumn" valign="top">
               &nbsp;
            </td>
            <td valign="top" class="MainTableRightColumn">
               <html:errors/>
               <% String val = "";
                  String saveAction = "/oscarEncounter/Measurements2?pasteEncounterNote=true";
                  String comment = "";
                  Hashtable hashtable = null;
                  if ( id != null ) {
                     saveAction = "/oscarEncounter/oscarMeasurements/DeleteData2";
                     hashtable = EctMeasurementsDataBeanHandler.getMeasurementDataById(id);
					 prevDate = (String) hashtable.get("dateObserved");
                     val = (String) hashtable.get("value");
                     comment = (String) hashtable.get("comments");
                  }
               %>

           <!-- Place Master Calendar Input Here -->
            <%
            int iDate;
            //only display if more than one measurement
            if(measurements.length>1){
            	iDate = measurements.length; 	//create a master date value
            %>
            <fieldset>
               <legend><b>Master Date/Time</b></legend>
               <div style="float:left;margin-left:30px;">
        		<label for="prevDate<%=iDate%>" class="fields" >Obs Date/Time: </label>
				<input type="text" name="value(date-<%=iDate%>)" id="prevDate<%=iDate%>" value="<%=prevDate%>" size="17" onchange="javascript:masterDateFill(this.value);">
				<% if ( id == null ) { %>
				<a id="date<%=iDate%>"><img title="Calendar" src="../../images/cal.gif" alt="Calendar" border="0" /></a>
				<%}%>
				<br /><font size="1">*Use this field to change the observation date/time for all items below.</font>
				</div>
			</fieldset>
			<br />
			<%
				iDate = iDate + 1; //after names are assigned to input above increasing iDate for the Cal script
            }else{
				iDate = measurements.length;
			}
			%>
            <!-- END of Master Calendar Input -->

               <html:form action="<%=saveAction%>" styleId="measurementForm" onsubmit="return validateSubmit()">

               <input type="hidden" name="value(numType)" value="<%=measurements.length%>"/>
               <input type="hidden" name="value(groupName)" value=""/>
               <input type="hidden" name="value(css)" value=""/>
               <input type="hidden" name="demographic_no" value="<%=demographic_no%>"/>
               <input type="hidden" name="inputFrom" value="AddMeasurementData"/>
               <input type="hidden" name="template" value="<%=template%>"/>
               <input type="hidden" name="uuid" value="<%=uuid%>"/>

               <%
                   int ctr = 0;
                   EctMeasurementsForm ectMeasurementsForm = (EctMeasurementsForm) request.getAttribute("EctMeasurementsForm");

                   for (String measurement : measurements)
                   {
                       Map<String, String> h2 = mFlowsheet.getMeasurementFlowSheetInfo(measurement);

                       EctMeasurementTypesBean mtypeBean = mFlowsheet.getFlowsheetMeasurement(measurement);

                       boolean isMeasurement = mtypeBean != null;

                       String type = "";
                       String typeDisplayName = measurement;
                       String validation = "";
                       String measuringInstruction = "";

                       if (mtypeBean != null)
                       {
                           type = mtypeBean.getType();
                           typeDisplayName = mtypeBean.getTypeDisplayName();
                           validation = mtypeBean.getValidation();
                           measuringInstruction = mtypeBean.getMeasuringInstrc();
                       }

                       if (ectMeasurementsForm != null && !ectMeasurementsForm.isEmpty())
                       {
                           prevDate = ectMeasurementsForm.getValue("date-" + ctr);
                           val = ectMeasurementsForm.getValue("inputValue-" + ctr);
                           comment = ectMeasurementsForm.getValue("comments-" + ctr);
                       }
               %>


               <input type="hidden" name="measurement" value="<%=measurement%>"/>

                   <input type="hidden" name="<%= "value(inputType-" + ctr + ")" %>" value="<%=type%>"/>
                   <input type="hidden" name="<%= "value(inputTypeDisplayName-" + ctr + ")" %>" value="<%=typeDisplayName%>"/>
                   <input type="hidden" name="<%= "value(validation-" + ctr + ")" %>" value="<%=validation%>"/>

               <% if ( id != null ) { %>
               <input type="hidden" name="id" value="<%=id%>"/>
               <input type="hidden" name="deleteCheckbox" id="deleteCheck" value="<%=id%>"/>
               <% } %>

               <div class="prevention">
                   <fieldset>
                      <legend><%=isMeasurement ? "Measurement" : "Prevention"%>> : <%=typeDisplayName%></legend>
                         <div style="float:left;display:none;">
                           <input type="radio" name="<%= "value(inputMInstrc-" + ctr + ")" %>" value="<%=measuringInstruction%>" checked/>
                         </div>
                         <div style="float:left;margin-left:30px;">
                            <label for="prevDate<%=ctr%>" class="fields" >Obs Date/Time:</label>
							<input type="text" name="<%= "value(date-" + ctr + ")" %>" id="prevDate<%=ctr%>" value="<%=prevDate%>" size="17" >

							<% if ( id == null ) { %>
							<a id="date<%=ctr%>"><img title="Calendar" src="../../images/cal.gif" alt="Calendar" border="0" /></a>
							<%}%>
							<br />

  						<label for="<%="value(inputValue-"+ctr+")"%>" class="fields"><%=h2.get("value_name")%>:</label>
                            <% if (mtypeBean != null && mtypeBean.getValidationName() != null && (mtypeBean.getValidationName().equals("Yes/No") || mtypeBean.getValidationName().equals("Yes/No/NA") || mtypeBean.getValidationName().equals("Yes/No/Maybe"))){ %>
                            <select  id="<%= "value(inputValue-" + ctr + ")" %>" name="<%= "value(inputValue-" + ctr + ")" %>" >
                                <%if (measurements.length > 1){ %>
                                <option value="" >Not Answered</option>
                                <%}%>
                                <option value="Yes" <%="Yes".equals(val) ? "selected" : ""%>>Yes</option>
                                <option value="No"  <%="No".equals(val) ? "selected" : ""%>>No</option>
                                
                                <% if(mtypeBean.getValidationName().equals("Yes/No/Maybe")){ %>
                                <option value="Maybe" <%="Maybe".equals(val) ? "selected" : ""%>>Maybe</option>
                                <%}else{ %>
                                <option value="NotApplicable" <%="NotApplicable".equals(val) ? "selected" : ""%>>Not Applicable</option>
                                <%} %>
                                
                            </select>
                            <%}else{%>
                            <input type="text" id="<%= "value(inputValue-" + ctr + ")" %>" name="<%= "value(inputValue-" + ctr + ")" %>" size="5" value="<%=val%>" /> <br/>
                            <%}%>
                         </div>
                          <br/>
                         <fieldset >
                          <legend >Comments</legend>
                           <textarea name="<%= "value(comments-" + ctr + ")" %>" ><%=comment%></textarea>
                         </fieldset>
                   </fieldset>


               </div>
               <%ctr++;
                }%>
               <script type="text/javascript">
                  hideExtraName(document.getElementById('providerDrop'));
               </script>

               <br/>

               <% if ( id != null ) { %>
               <input type="submit" name="delete" value="Delete" id="deleteButton" disabled="false"/>
               <% }else{ %>
               <input type="submit" value="Save">
               <%}%>
               </html:form>
            </td>
        </tr>
        <tr>
            <td class="MainTableBottomRowLeftColumn">
            &nbsp;
            </td>
            <td class="MainTableBottomRowRightColumn" valign="top">
            &nbsp;
            </td>
        </tr>
    </table>
    <script type="text/javascript">
	    <% if ( id != null ) { %>
	    Form.disable('measurementForm');
	    document.getElementById('deleteButton').disabled = false;
	    document.getElementById('deleteCheck').disabled = false;

	    <% } %>
	    <% for (int i =0; i < iDate; i++){ %>
	    Calendar.setup({inputField: "prevDate<%=i%>", ifFormat: "%Y-%m-%d %H:%M", showsTime: true, button: "date<%=i%>", singleClick: true, step: 1});
	    <%}%>
    </script>
</body>
</html:html>
