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
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
        "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="org.oscarehr.util.LoggedInInfo"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="oscar.OscarProperties"%>
<%@page import="org.oscarehr.util.SpringUtils"%>
<%@page import="org.oscarehr.common.dao.BillingONCHeader1Dao" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="oscar.oscarDemographic.data.DemographicNameAgeString" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="oscar.oscarPrevention.pageUtil.PreventionReportDisplay" %>
<%@ page import="oscar.oscarDemographic.data.DemographicData" %>
<%@ page import="org.oscarehr.PMmodule.utility.UtilDateUtilities" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="oscar.oscarReport.data.RptSearchData" %>
<%@ page import="org.oscarehr.contact.entity.DemographicContact" %>
<%@ page import="org.oscarehr.contact.entity.Contact" %>
<%@ page import="java.util.List" %>
<%@ page import="org.oscarehr.report.prevention.model.PreventionReportModel" %>
<%@ page import="oscar.util.ConversionUtils" %>
<%@ page import="static oscar.oscarPrevention.reports.PreventionReport.FIRST_LETTER" %>
<%@ page import="static oscar.oscarPrevention.reports.PreventionReport.PHONE_CALL" %>
<%@ page import="static oscar.oscarPrevention.reports.PreventionReport.REFUSED" %>
<%@ page import="static oscar.oscarPrevention.reports.PreventionReport.SECOND_LETTER" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/oscar-tag.tld" prefix="oscar" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="providerBean" class="java.util.Properties" scope="session" />
<c:set var="ctx" value="${pageContext.request.contextPath}" scope="request"/>

<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%
      String roleName$ = session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
	  boolean authed=true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_prevention" rights="r" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect("../securityError.jsp?type=_prevention");%>
</security:oscarSec>
<%
if(!authed) {
	return;
}
%>

<%
  oscar.oscarReport.data.RptSearchData searchData  = new oscar.oscarReport.data.RptSearchData();
  List<RptSearchData.SearchCriteria> queryArray = searchData.getQueryTypes();

  BillingONCHeader1Dao bCh1Dao = (BillingONCHeader1Dao)SpringUtils.getBean("billingONCHeader1Dao");
%>

<html:html locale="true">

<head>
<html:base/>
<title><bean:message key="oscarprevention.index.oscarpreventiontitre" /></title><!-- i18n -->

<script type="text/javascript" src="../share/javascript/Oscar.js"></script>
<link rel="stylesheet" type="text/css" href="../share/css/OscarStandardLayout.css">
<link rel="stylesheet" type="text/css" media="all" href="../share/calendar/calendar.css" title="win2k-cold-1" >

<script type="text/javascript" src="../share/calendar/calendar.js" ></script>
<script type="text/javascript" src="../share/calendar/lang/<bean:message key="global.javascript.calendar"/>" ></script>
<script type="text/javascript" src="../share/calendar/calendar-setup.js" ></script>
<script type="text/javascript" src="../share/javascript/prototype.js"></script>
<script type="text/javascript" src="../share/javascript/sortable.js"></script>
<style type="text/css">
  div.ImmSet { background-color: #ffffff; }
  div.ImmSet h2 {  }
  div.ImmSet ul {  }
  div.ImmSet li {  }
  div.ImmSet li a { text-decoration:none; color:blue;}
  div.ImmSet li a:hover { text-decoration:none; color:red; }
  div.ImmSet li a:visited { text-decoration:none; color:blue;}
</style>

<script type="text/javascript">

//update all selected patient's records with next contact method
//still need to generate before values are saved
function setNextContactMethod(selectElem) {
	var nextSelectedContactMethod = selectElem.options[selectElem.selectedIndex].value;
	
	var chckbxSelectedContactMethod = document.getElementsByName("nsp");
	var displayId;
	var currentValue;
	var idNum;

	if( nextSelectedContactMethod == "other" ) {
		nextSelectedContactMethod = prompt("Enter next contact method: ");
		if( nextSelectedContactMethod == null ) {
			return;
		}
	}
	
	for( var idx = 0; idx < chckbxSelectedContactMethod.length; ++idx ) {
		if( chckbxSelectedContactMethod[idx].checked ) {
			currentValue = chckbxSelectedContactMethod[idx].value.split(",");		
			currentValue[0] += "," + nextSelectedContactMethod;
			chckbxSelectedContactMethod[idx].value = currentValue[0];		
			
			idNum = chckbxSelectedContactMethod[idx].id.substr(9);
			
			displayId = "nextSuggestedProcedure" + idNum;
			$(displayId).update(nextSelectedContactMethod);
		}
	}
	
}

var nspChecked = false;
function selectAllnsp() {
	var chckbxSelectedContactMethod = document.getElementsByName("nsp");
	
	for( var idx = 0; idx < chckbxSelectedContactMethod.length; ++idx ) {
		if( nspChecked ) {
			chckbxSelectedContactMethod[idx].checked = false;
		}
		else {
			chckbxSelectedContactMethod[idx].checked = true;			
		}
	}
	
	nspChecked = !nspChecked;
}

function showHideItem(id){
    if(document.getElementById(id).style.display == 'none')
        document.getElementById(id).style.display = '';
    else
        document.getElementById(id).style.display = 'none';
}

function showItem(id){
        document.getElementById(id).style.display = '';
}

function hideItem(id){
        document.getElementById(id).style.display = 'none';
}

function showHideNextDate(id,nextDate,nexerWarn){
    if(document.getElementById(id).style.display == 'none'){
        showItem(id);
    }else{
        hideItem(id);
        document.getElementById(nextDate).value = "";
        document.getElementById(nexerWarn).checked = false ;

    }
}

function disableifchecked(ele,nextDate){
    if(ele.checked == true){
       document.getElementById(nextDate).disabled = true;
    }else{
       document.getElementById(nextDate).disabled = false;
    }
}

function batchBill() {
    var frm = document.forms["frmBatchBill"];
    var url = "<c:out value="${ctx}"/>" + "/billing/CA/ON/BatchBill.do";

    new Ajax.Request(
        url,
        {
            method: 'post',
            postBody: Form.serialize(frm),
            asynchronous: true,
            onSuccess: function(ret) {
                alert("Billing Complete!");
            },
            onFailure: function(ret) {
                alert( ret.status + " Billing Failed");
            }
        }

    );

    return false;
}

function saveContacts() {
	var frm = document.forms["frmBatchBill"];
	var url = "<c:out value="${ctx}"/>" + "/oscarMeasurement/AddShortMeasurement.do?method=addMeasurements";
	
    new Ajax.Request(
            url,
            {
                method: 'post',
                postBody: Form.serialize(frm),
                asynchronous: true,
                onSuccess: function(ret) {
                    window.location.reload();
                },
                onFailure: function(ret) {
                    alert( ret.status + " There was a problem saving contacts.");
                }
            }
        );
        return false;
}

</script>


<script type="text/javascript">



    //Function sends AJAX request to action
    function completedProcedure(idval,followUpType,procedure,demographic){
       var comment = prompt('Are you sure you want to added this to patients record \n\nAdd Comment Below ','');
       if (comment != null){
          var params = "id="+idval+"&followupType="+followUpType+"&followupValue="+procedure+"&demos="+demographic+"&message="+comment;
          var url = "../oscarMeasurement/AddShortMeasurement.do";

          new Ajax.Request(url, {method: 'get',parameters:params,asynchronous:true,onComplete: followUp});
       }
       return false;
    }

    function followUp(origRequest){
        //alert(origRequest.responseText);
        var hash = origRequest.responseText.parseQuery();
        //alert( hash['id'] + " " + hash['followupValue']+" "+hash['Date'] );
        //("id="+id+"&followupValue="+followUpValue+"&Date=
        var lastFollowupTD = $('lastFollowup'+hash['id']);
        var nextProcedureTD = $('nextSuggestedProcedure'+hash['id']);
        //alert(nextProcedureTD);
        nextProcedureTD.innerHTML = "------";
        lastFollowupTD.innerHTML = hash['followupValue']+" "+hash['Date'];
    }
</script>



<style type="text/css">
	table.outline{
	   margin-top:50px;
	   border-bottom: 1pt solid #888888;
	   border-left: 1pt solid #888888;
	   border-top: 1pt solid #888888;
	   border-right: 1pt solid #888888;
	}
	table.grid{
	   border-bottom: 1pt solid #888888;
	   border-left: 1pt solid #888888;
	   border-top: 1pt solid #888888;
	   border-right: 1pt solid #888888;
	}
	td.gridTitles{
		border-bottom: 2pt solid #888888;
		font-weight: bold;
		text-align: center;
	}
        td.gridTitlesWOBottom{
                font-weight: bold;
                text-align: center;
        }
	td.middleGrid{
	   border-left: 1pt solid #888888;
	   border-right: 1pt solid #888888;
           text-align: center;
	}


label{
float: left;
width: 120px;
font-weight: bold;
}

span.labelLook{
font-weight:bold;

}

input, textarea,select{

margin-bottom: 5px;
}

textarea{
width: 250px;
height: 150px;
}

.boxes{
width: 1em;
}

#submitbutton{
margin-left: 120px;
margin-top: 5px;
width: 90px;
}

br{
clear: left;
}

table.ele {

   border-collapse:collapse;
}

table.ele td{
    border:1px solid grey;
    padding:2px;
}

/* Sortable tables */
table.ele thead {
    background-color:#eee;
    color:#666666;
    font-size: x-small;
    cursor: default;
}
.error {
	color: red;
	font-size: 24px;
	padding: 10px;
}
</style>

<style type="text/css" media="print">
.MainTable {
    display:none;
}
.hiddenInPrint{
    display:none;
}
.shownInPrint{
    display:block;
}
</style>


</head>

<body class="BodyStyle" vlink="#0000FF">
    <table  class="MainTable" id="scrollNumber1" >
        <tr class="MainTableTopRow">
            <td class="MainTableTopRowLeftColumn" width="100" >
               <bean:message key="oscarprevention.index.oscarpreventiontitre" />
            </td>
            <td class="MainTableTopRowRightColumn">
                <table class="TopStatusBar">
                    <tr>
                        <td >
                            Prevention Reporting
                        </td>
                        <td  >&nbsp;
		               <a href="../report/ManageLetters.jsp?goto=success_manage_from_prevention" target="_blank">manage letters</a>
                        </td>
                        <td style="text-align:right">
                                <oscar:help keywords="report" key="app.top1"/> | <a href="javascript:popupStart(300,400,'About.jsp')" ><bean:message key="global.about" /></a> | <a href="javascript:popupStart(300,400,'License.jsp')" ><bean:message key="global.license" /></a>
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
               <html:form action="/oscarPrevention/PreventionReport.do" method="get">
                   <input type="hidden" name="method" value="runReport">
               <div>
                   Saved Query:
                  <html:select property="patientSet">
                      <html:option value="-1" >--Select Query--</html:option>
                      <%for (int i =0 ; i < queryArray.size(); i++)
                      {
                        RptSearchData.SearchCriteria sc = queryArray.get(i);
                        String qId = sc.id;
                        String qName = sc.queryName;%>
                        <html:option value="<%=qId%>"><%=qName%></html:option>
                      <%}%>
                  </html:select>
               </div>
               <div>
                  Prevention Query:
                  <html:select property="prevention">
			<html:option value="-1" >--Select Query--</html:option>
			<html:option value="PAP" >PAP</html:option>
			<html:option value="Mammogram" >Mammogram</html:option>
			<html:option value="Flu" >Flu</html:option>
			<html:option value="Child_Immunizations" >Child Immunizations</html:option>
			<html:option value="FIT" >FIT</html:option>
                  </html:select>
               </div>
               <div>
                  As of:
                    <html:text property="asofDate" size="9" styleId="asofDate" /> <a id="date"><img title="Calendar" src="../images/cal.gif" alt="Calendar" border="0" /></a> <br>



               </div>
               <input type="submit" />
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

    <div>
                <%List<Integer> overDueList = new ArrayList<>();
                  List<Integer> firstLetter = new ArrayList<>();
                  List<Integer> secondLetter = new ArrayList<>();
                  List<Integer> refusedLetter = new ArrayList<>();
                  List<Integer> phoneCall = new ArrayList<>();


                    String type = null;
                    String ineligible = null;
                    String done = null;
                    String percentage = null;
                    String percentageWithGrace = null;
                    String followUpType = null;
                    String billCode = null;
                    List<PreventionReportDisplay> list = null;
                    Date asDate = null;

                    PreventionReportModel model = (PreventionReportModel) request.getAttribute("report");
                    if(model != null)
                    {
                        type = model.getReportType();
                        ineligible = model.getInEligible();
                        done = model.getUpToDate();
                        percentage = model.getPercent();
                        percentageWithGrace = model.getPercentWithGrace();
                        followUpType = model.getFollowUpType();
                        billCode = model.getBillCode();
                        list = model.getReturnReport();
                        asDate = model.getAsOfDateTime();

                        firstLetter = model.getL1LetterDemographicIds();
                        secondLetter = model.getL2LetterDemographicIds();
                        phoneCall = model.getP1LetterDemographicIds();
                        refusedLetter = model.getRefusedLetterDemographicIds();
                        overDueList = model.getOverdueLetterDemographicIds();
                    }
                    if(asDate == null)
                    {
                        asDate = Calendar.getInstance().getTime();
                    }
                  String lastDate = null;
                  
                  String error = (String) request.getAttribute("error");

                  if(error != null) {
                	  %>
                	  <span class="error"><%=error%></span>
                	  <%
                  }
                  else if (model != null && list != null ){ %>
                  <form name="frmBatchBill" action="" method="post">
                      <input type="hidden" name="clinic_view" value="<%=OscarProperties.getInstance().getProperty("clinic_view","")%>">
                      <input type="hidden" name="followUpType" value="<%=followUpType%>">
              <table class="ele" width="90%">
                       <tr>
                       <td>&nbsp;</td>
                       <td style="width: 10%;">Total patients: <%=list.size()%><br/>Ineligible:<%=ineligible%></td>
                       <td style="width: 10%;">Up to Date: <%=done%> = <%=percentage %> %
                         <%if (percentageWithGrace != null){  %>
                           <%-- <br/> With Grace <%=percentageWithGrace%> %
                           --%>
                         <%}%>
                       </td>
                       
                       <td style="width: 40%;">&nbsp;<%=request.getAttribute("patientSet")%> </td>
                       <td>	
                       		<select onchange="setNextContactMethod(this)">
                       			<option value="------">Select Contact Method</option>
                       			<option value="Email">Email</option>
                       			<option value="L1">Letter 1</option>
                       			<option value="L2">Letter 2</option>
                       			<option value="myOSCARmsg">MyOSCAR Message</option>
                       			<option value="Newsletter">Newsletter</option>
                       			<option value="other">Other</option>
                       	  	</select>
                       	  	&nbsp;&nbsp;
                       	  	<input type="button" value="Save Contacts" onclick="return saveContacts();">
                       </td>                                                                                                                   
                       <td style="width: 10%;"><input style="float: right" type="button" value="Bill" onclick="return batchBill();"></td>
                       </tr>
             </table>
             <table id="preventionTable" class="sortable ele" width="80%">
                       <thead>
                       <tr>
                          <th class="unsortable">&nbsp;</th>
                          <th>DemoNo</th>
                          <th>DOB</th>
                          <th>Age as of <br/><%=UtilDateUtilities.DateToString(asDate)%></th>
                          <th>Sex</th>
                          <th>Lastname</th>
                          <th>Firstname</th>
                          <th>HIN</th>
                          <%if (type != null ){ %>
                          <th>Guardian</th>
                          <%}%>
                          <th>Phone</th>
                          <th>Address</th>
                          <th>Next Appt.</th>
                          <th>Status</th>
                          <%if (type != null ){ %>
                          <th># Shots</th>
                          <%}%>                          
                          <th>Bonus Stat</th>
                          <th>Since Last Procedure Date</th>
                          <th>Last Procedure Date</th>
                          <th>Last Contact Method</th>
                          <th>Next Contact Method</th>
                          <th class="unsortable">Select Contact<br><input type="checkbox" onclick="selectAllnsp()"></th>
                          <th>Roster Physician</th>
                          <th class="unsortable">Bill</th>
                       </tr>
                       </thead>
                       <tbody>
                       <%
                           DemographicNameAgeString deName = DemographicNameAgeString.getInstance();
                         DemographicData demoData= new DemographicData();
                         boolean setBill;
                         String enabled = "";
                         int numDays;

                         for (int i = 0; i < list.size(); i++){
                             setBill = false;
                            PreventionReportDisplay dis = list.get(i);
                            Hashtable h = deName.getNameAgeSexHashtable(LoggedInInfo.getLoggedInInfoFromSession(request), dis.demographicNo.toString());
                            org.oscarehr.common.model.Demographic demo = demoData.getDemographic(LoggedInInfo.getLoggedInInfoFromSession(request),  dis.demographicNo.toString());
                            
                            lastDate = dis.lastDate;

                             if(PHONE_CALL.equals(dis.nextSuggestedProcedure)
                                     || REFUSED.equals(dis.state)
                                     || "Y".equals(dis.billStatus))
                             {
                                 setBill = true;
                             }
                            %>
                       <tr>
                          <td><%=i+1%></td>
                          <td>
                              <a href="javascript: return false;" onClick="popup(724,964,'../demographic/demographiccontrol.jsp?demographic_no=<%=dis.demographicNo%>&amp;displaymode=edit&amp;dboperation=search_detail','MasterDemographic')"><%=dis.demographicNo%></a>
                          </td>
                          <td><%=DemographicData.getDob(demo,"-")%></td>

                          <%if (type == null ){ %>
                          <td><%=demo.getAgeAsOf(asDate)%></td>
                          <td><%=h.get("sex")%></td>
                          <td><%=h.get("lastName")%></td>
                          <td><%=h.get("firstName")%></td>
                          <td><%=demo.getHin()+demo.getVer()%></td>
                          <td><%=demo.getPhone()%> </td>
                          <td><%=demo.getAddress()+" "+demo.getCity()+" "+demo.getProvince()+" "+demo.getPostal()%> </td>
                          <td><oscar:nextAppt demographicNo="<%=demo.getDemographicNo().toString()%>"/></td>
                          <td bgcolor="<%=dis.color%>"><%=dis.state%></td>                          
                          <td bgcolor="<%=dis.color%>"><%=dis.bonusStatus%></td>
                          <td bgcolor="<%=dis.color%>"><%=dis.numMonths%></td>
                          <td bgcolor="<%=dis.color%>"><%=dis.lastDate%></td>


                          <% }else {
                              LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
                              DemographicContact demographicContact = demoData.getSubstituteDecisionMaker(dis.demographicNo.toString());
                              // internal
                              String contactFullName = "";
                              String contactPhone = "";
                              String contactAddress = "";
                              if (demographicContact != null && demographicContact.getType() == DemographicContact.TYPE_DEMOGRAPHIC)
                              {
                                  org.oscarehr.common.model.Demographic demographic = demoData.getInternalContact(loggedInInfo, demographicContact);
                                  if (demographic != null)
                                  {
                                      contactFullName = demographic.getFullName();
                                      contactPhone = demographic.getPhone();
                                      contactAddress = StringUtils.trimToEmpty(demographic.getAddress()) + " "
                                              + StringUtils.trimToEmpty(demographic.getCity()) + " "
                                              + StringUtils.trimToEmpty(demographic.getProvince()) + " "
                                              + StringUtils.trimToEmpty(demographic.getPostal());
                                  }
                              }
                              // external
                              else if (demographicContact != null && demographicContact.getType() == DemographicContact.TYPE_CONTACT)
                              {
                                  Contact contact = demoData.getExternalContact(demographicContact);
                                  if (contact != null)
                                  {
                                      contactFullName = contact.getFormattedName();
                                      contactPhone = contact.getCellPhone();
                                      contactAddress = contact.getAddress();
                                  }
                              }
                          %>
                          <td><%=demo.getAgeAsOf(asDate)%></td>
                          <td><%=h.get("sex")%></td>
                          <td><%=h.get("lastName")%></td>
                          <td><%=h.get("firstName")%></td>
                          <td><%=demo.getHin()+demo.getVer()%></td>
                          <td><%=contactFullName %>&nbsp;</td>
                          <td><%=contactPhone%> &nbsp;</td>
                          <td><%=contactAddress%> &nbsp;</td>
                          <td><oscar:nextAppt demographicNo="<%=demo.getDemographicNo().toString()%>"/></td>
                          <td bgcolor="<%=dis.color%>"><%=dis.state%></td>
                          <td bgcolor="<%=dis.color%>"><%=dis.numShots%></td>                          
                          <td bgcolor="<%=dis.color%>"><%=dis.bonusStatus%></td>
                          <td bgcolor="<%=dis.color%>"><%=dis.numMonths%></td>
                          <td bgcolor="<%=dis.color%>"><%=dis.lastDate%></td>

                          <%}%>
                          <td bgcolor="<%=dis.color%>" id="lastFollowup<%=i+1%>">
                             <% if (dis.lastFollowup != null ){ %>
                                 <%=dis.lastFollupProcedure%>
                                 <%=UtilDateUtilities.DateToString(dis.lastFollowup)%>
                                 <%=UtilDateUtilities.getNumMonths(dis.lastFollowup,new Date())%>M
                             <% }else{ %>
                                ------
                             <% } %>
                          </td>
                          <td bgcolor="<%=dis.color%>" id="nextSuggestedProcedure<%=i+1%>">
                              <%if ( dis.nextSuggestedProcedure != null && dis.nextSuggestedProcedure.equals("P1")){ %>
                                 <a href="javascript: return false;" onclick="return completedProcedure('<%=i+1%>','<%=followUpType%>','<%=dis.nextSuggestedProcedure%>','<%=dis.demographicNo%>');"><%=dis.nextSuggestedProcedure%></a>                              
                              <%}else{%>
                                    <%=dis.nextSuggestedProcedure%>
                              <%}%>
                          </td>
                          <td bgcolor="<%=dis.color%>">		
                          	<%if( !setBill ) {%>					                          
                          		<input type="checkbox"  id="selectnsp<%=i+1%>" name="nsp" value="<%=dis.demographicNo%>">
                          	<%} else { %>
                          		&nbsp;
                          	<%} %>
                          </td>
                          <%
                          	String providerName=providerBean.getProperty(String.valueOf(demo.getProviderNo()),"");
                          	providerName=StringUtils.trimToEmpty(providerName);
                          %>
                          <td bgcolor="<%=dis.color%>"><%=providerName%></td>
                          <td bgcolor="<%=dis.color%>">
                              <% if( billCode != null && setBill ) {
                                  numDays = bCh1Dao.getDaysSinceBilled(billCode, dis.demographicNo);
                                  //we only want to enable billing if it has been a year since the last invoice was created
                                  enabled = numDays >= 0 && numDays < 365 ? "disabled" : "checked";
                              %>
                              <input type="checkbox" name="bill" <%=enabled%> value="<%=billCode + ";" + dis.demographicNo + ";" + demo.getProviderNo()%>">
                              <%}%>
                          </td>

                       </tr>
                      <%}%>
                    	</tbody>
                    </table>
                    <table class="ele" style="width:80%;">
                      <tr>
                          <td style="text-align:right;"><input type="button" value="Bill" onclick="return batchBill();"></td>

                      </tr>
                    </table>

                    </form>

                  <%}
                  if(!firstLetter.isEmpty())
                  {
                        %>
                    <form name="frmL1Generate" target="_blank" method="POST" action="<%=request.getContextPath()%>/oscarPrevention/PreventionReport.do">
                        <input type="hidden" name="method" value="generateLetter">
                        <input type="hidden" name="letterType" value="<%=FIRST_LETTER%>">
                        <input type="hidden" name="message" value="<%="Letter 1 Reminder Letter sent for: "+model.getPreventionType()%>">
                        <input type="hidden" name="followupType" value="<%=followUpType%>">
                        <input type="hidden" name="followupValue" value="L1">
                        <input type="hidden" name="lastDate" value="<%=lastDate%>">

                        <input type="hidden" name="queryName" value="<%=model.getPatientSet()%>">
                        <input type="hidden" name="prevention" value="<%=model.getPreventionType()%>">
                        <input type="hidden" name="asofDate" value="<%=ConversionUtils.toDateString(model.getAsOfDateTime())%>">

                        <button type="submit">Generate First Letter</button>
                    </form>
                  <%}
                  if ( secondLetter.size() > 0 )
                  {
                        %>
                    <form name="frmL2Generate" target="_blank" method="POST" action="<%=request.getContextPath()%>/oscarPrevention/PreventionReport.do">
                        <input type="hidden" name="method" value="generateLetter">
                        <input type="hidden" name="letterType" value="<%=SECOND_LETTER%>">
                        <input type="hidden" name="message" value="<%="Letter 2 Reminder Letter sent for: "+model.getPreventionType()%>">
                        <input type="hidden" name="followupType" value="<%=followUpType%>">
                        <input type="hidden" name="followupValue" value="L2">
                        <input type="hidden" name="lastDate" value="<%=lastDate%>">

                        <input type="hidden" name="queryName" value="<%=model.getPatientSet()%>">
                        <input type="hidden" name="prevention" value="<%=model.getPreventionType()%>">
                        <input type="hidden" name="asofDate" value="<%=ConversionUtils.toDateString(model.getAsOfDateTime())%>">

                        <button type="submit">Generate Second Letter</button>
                    </form>
                  <%}
                  if ( refusedLetter.size() > 0 )
                  {
                        %>
                    <form name="frmRefusedGenerate" target="_blank" method="POST" action="<%=request.getContextPath()%>/oscarPrevention/PreventionReport.do">
                        <input type="hidden" name="method" value="generateLetter">
                        <input type="hidden" name="letterType" value="<%=REFUSED%>">
                        <input type="hidden" name="message" value="<%="Letter 1 Reminder Letter sent for: "+model.getPreventionType()%>">
                        <input type="hidden" name="followupType" value="<%=followUpType%>">
                        <input type="hidden" name="followupValue" value="L1">
                        <input type="hidden" name="lastDate" value="<%=lastDate%>">

                        <input type="hidden" name="queryName" value="<%=model.getPatientSet()%>">
                        <input type="hidden" name="prevention" value="<%=model.getPreventionType()%>">
                        <input type="hidden" name="asofDate" value="<%=ConversionUtils.toDateString(model.getAsOfDateTime())%>">

                        <button type="submit">Generate Refused Letter</button>
                    </form>
                  <%}%>
               </div>

<script type="text/javascript">
    Calendar.setup( { inputField : "asofDate", ifFormat : "%Y-%m-%d", showsTime :false, button : "date", singleClick : true, step : 1 } );
</script>

</body>
</html:html>

