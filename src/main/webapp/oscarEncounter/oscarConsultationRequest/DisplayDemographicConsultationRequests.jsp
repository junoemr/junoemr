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
<%@page
	import="oscar.oscarEncounter.pageUtil.*,oscar.oscarEncounter.data.*"%>
<%@ page import="oscar.OscarProperties"%>
<%@ page import="java.util.*, java.net.*"%>

<%
if(session.getAttribute("user") == null) response.sendRedirect("../../logout.jsp");
String demo = request.getParameter("de");
String proNo = (String) session.getAttribute("user");
oscar.oscarDemographic.data.DemographicData demoData=null;
org.oscarehr.common.model.Demographic demographic=null;

OscarProperties oscarProps = OscarProperties.getInstance();

oscar.oscarProvider.data.ProviderData pdata = new oscar.oscarProvider.data.ProviderData(proNo);
String team = pdata.getTeam();

if (demo != null ){ 
    demoData = new oscar.oscarDemographic.data.DemographicData();
    demographic = demoData.getDemographic(demo);    
}
else
    response.sendRedirect("../error.jsp");

oscar.oscarEncounter.oscarConsultationRequest.pageUtil.EctConsultationFormRequestUtil consultUtil;
consultUtil = new  oscar.oscarEncounter.oscarConsultationRequest.pageUtil.EctConsultationFormRequestUtil();
consultUtil.estPatient(demo);

oscar.oscarEncounter.oscarConsultationRequest.pageUtil.EctViewConsultationRequestsUtil theRequests;
theRequests = new  oscar.oscarEncounter.oscarConsultationRequest.pageUtil.EctViewConsultationRequestsUtil();
theRequests.estConsultationVecByDemographic(demo);
%>

<html:html locale="true">
<head>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
<title><bean:message
	key="oscarEncounter.oscarConsultationRequest.DisplayDemographicConsultationRequests.title" />
</title>
<html:base />

<!--META HTTP-EQUIV="Refresh" CONTENT="20;"-->

<link rel="stylesheet" type="text/css" media="all" href="../share/css/extractedFromPages.css"  />





</head>
<script language="javascript">
function BackToOscar()
{
       window.close();
}
function popupOscarRx(vheight,vwidth,varpage) { //open a new popup window
  var page = varpage;
  windowprops = "height="+vheight+",width="+vwidth+",location=no,scrollbars=yes,menubars=no,toolbars=no,resizable=yes,screenX=0,screenY=0,top=0,left=0";
  var popup=window.open(varpage, "<bean:message key="oscarEncounter.oscarConsultationRequest.DisplayDemographicConsultationRequests.msgConsReq"/>", windowprops);
  //if (popup != null) {
  //  if (popup.opener == null) {
  //    popup.opener = self;
  //  }
  //}
}
function popupOscarConS(vheight,vwidth,varpage) { //open a new popup window
  var page = varpage;
  windowprops = "height="+vheight+",width="+vwidth+",location=no,scrollbars=yes,menubars=no,toolbars=no,resizable=yes,screenX=0,screenY=0,top=0,left=0";
  var popup=window.open(varpage, "<bean:message key="oscarEncounter.oscarConsultationRequest.ConsultChoice.oscarConS"/>", windowprops);
  window.close();
}
</script>

<link rel="stylesheet" type="text/css" href="../encounterStyles.css">
<body class="BodyStyle" vlink="#0000FF" onload="window.focus()">
<!--  -->
<table class="MainTable" id="scrollNumber1" name="encounterTable">
	<tr class="MainTableTopRow">
		<td class="MainTableTopRowLeftColumn">Consultation</td>
		<td class="MainTableTopRowRightColumn">
		<table class="TopStatusBar">
			<tr>
				<td class="Header" NOWRAP><bean:message
					key="oscarEncounter.oscarConsultationRequest.DisplayDemographicConsultationRequests.msgConsReqFor" />
				<%=demographic.getLastName() %>, <%=demographic.getFirstName()%> <%=demographic.getSex()%>
				<%=demographic.getAge()%>
				</td>
			</tr>
		</table>
		</td>
	</tr>
	<tr style="vertical-align: top">
		<td class="MainTableLeftColumn">
		<table>
			<tr>
				<td NOWRAP><a
					href="javascript:popupOscarRx(700,960,'ConsultationFormRequest.jsp?de=<%=demo%>&teamVar=<%=team%>')">
				<bean:message
					key="oscarEncounter.oscarConsultationRequest.ConsultChoice.btnNewCon" /></a>
				</td>
			</tr>
			<%
			/* -- Begin custom work OHSUPPORT 2883 - add invoice link to consultation page -- */
			if(oscarProps.isPropertyActive("enable_consultation_invoice_link")){
				String prov= (oscarProps.getProperty("billregion","")).trim().toUpperCase();
				String billingServiceType = URLEncoder.encode(oscarProps.getProperty("default_view"));
				
			    GregorianCalendar now=new GregorianCalendar();
			    int curYear = now.get(Calendar.YEAR);
			    int curMonth = (now.get(Calendar.MONTH)+1);
			    int curDay = now.get(Calendar.DAY_OF_MONTH);
			    
				String strYear=""+curYear;
				String strMonth=curMonth>9?(""+curMonth):("0"+curMonth);
				String strDay=curDay>9?(""+curDay):("0"+curDay);
				String newDateString = strYear+"-"+strMonth+"-"+strDay;
				String dateString = curYear+"-"+curMonth+"-"+curDay;
				String linkProvider=proNo;
				/*if(apptProvider!=null){
					linkProvider=apptProvider;
				}*/

    			%>
				<tr>
				<%
				if(oscarProps.isPropertyActive("clinicaid_billing")){
					String clinicaid_link = "../../billing/billingClinicAid.jsp?demographic_no="+demographic.getDemographicNo()+
						"&service_start_date="+URLEncoder.encode(newDateString, "UTF-8")+
						"&chart_no="+demographic.getChartNo()+
						"&appointment_start_time=0"+
						"&appointment_provider_no="+linkProvider+
						"&billing_action=create_invoice&appointment_no=0";
					%>
					<td>
						<a href="<%=clinicaid_link%>" target="_blank" title="<bean:message key="demographic.demographiceditdemographic.msgBillPatient"/>">
							<bean:message key="demographic.demographiceditdemographic.msgCreateInvoice"/>
						</a>
					</td>
				<%
				}
				else {
					String invoice_link = "../../billing.do?billRegion=" + URLEncoder.encode(prov) + "&billForm=" + billingServiceType
							+ "&hotclick=&appointment_no=0&demographic_name=" + URLEncoder.encode(demographic.getLastName()) + "%2C"
						 	+ URLEncoder.encode(demographic.getFirstName()) + "&demographic_no=" + demographic.getDemographicNo() + "&providerview=1&user_no=" 
							+ proNo + "&apptProvider_no=none&appointment_date=" + dateString + "&start_time=0:00&bNewForm=1&status=t";
				%>
				<td NOWRAP><a 
	                href="<%=invoice_link%>"
					title="<bean:message key="demographic.demographiceditdemographic.msgBillPatient"/>"><bean:message key="demographic.demographiceditdemographic.msgCreateInvoice"/>
				</a></td> <%
				}%>
				</tr>
			<%
			}
		/* -- End custom work OHSUPPORT 2883 -- */%>
		</table>
		</td>
		<td class="MainTableRightColumn">
		<table width="100%">
			<tr>
				<td><bean:message
					key="oscarEncounter.oscarConsultationRequest.DisplayDemographicConsultationRequests.msgClickLink" />
				</td>
			</tr>
			<tr>
				<td>

				<table border="0" width="80%" cellspacing="1">
					<tr>
						<th align="left" class="VCRheads" width="75"><bean:message
							key="oscarEncounter.oscarConsultationRequest.DisplayDemographicConsultationRequests.msgStatus" />
						</th>
						<th align="left" class="VCRheads"><bean:message
							key="oscarEncounter.oscarConsultationRequest.DisplayDemographicConsultationRequests.msgPat" />
						</th>
						<th align="left" class="VCRheads"><bean:message
							key="oscarEncounter.oscarConsultationRequest.DisplayDemographicConsultationRequests.msgProvider" />
						</th>
						<th align="left" class="VCRheads"><bean:message
							key="oscarEncounter.oscarConsultationRequest.DisplayDemographicConsultationRequests.msgService" />
						</th>
						<th align="left" class="VCRheads"><bean:message
							key="oscarEncounter.oscarConsultationRequest.DisplayDemographicConsultationRequests.msgRefDate" />
						</th>
					</tr>
					<%  
                                    for (int i = 0; i < theRequests.ids.size(); i++){
                                    String id      = (String) theRequests.ids.elementAt(i);
                                    String status  = (String) theRequests.status.elementAt(i);
                                    String patient = (String) theRequests.patient.elementAt(i);
                                    String provide = (String) theRequests.provider.elementAt(i);
                                    String service = (String) theRequests.service.elementAt(i);
                                    String date    = (String) theRequests.date.elementAt(i);
                                %>
					<tr>
						<td class="stat<%=status%>" width="75">
						<% if (status.equals("1")){ %> <bean:message
							key="oscarEncounter.oscarConsultationRequest.DisplayDemographicConsultationRequests.msgNothingDone" />
						<% }else if(status.equals("2")) { %> <bean:message
							key="oscarEncounter.oscarConsultationRequest.DisplayDemographicConsultationRequests.msgSpecialistCall" />
						<% }else if(status.equals("3")) { %> <bean:message
							key="oscarEncounter.oscarConsultationRequest.DisplayDemographicConsultationRequests.msgPatCall" />
						<% }else if(status.equals("4")) { %> <bean:message
							key="oscarEncounter.oscarConsultationRequest.DisplayDemographicConsultationRequests.msgAppMade" />
						<% } %>
						</td>
						<td class="stat<%=status%>"><a
							href="javascript:popupOscarRx(700,960,'../../oscarEncounter/ViewRequest.do?de=<%=demo%>&requestId=<%=id%>')">
						<%=patient%> </a></td>
						<td class="stat<%=status%>"><%=provide%></td>
						<td class="stat<%=status%>"><a
							href="javascript:popupOscarRx(700,960,'../../oscarEncounter/ViewRequest.do?de=<%=demo%>&requestId=<%=id%>')">
						<%=service%> </a></td>
						<td class="stat<%=status%>"><%=date%></td>
					</tr>
					<%}%>
				</table>
				</td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td class="MainTableBottomRowLeftColumn"></td>
		<td class="MainTableBottomRowRightColumn"></td>
	</tr>
</table>
</body>
</html:html>
