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
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/oscar-tag.tld" prefix="oscar" %>
<%@ page import="oscar.oscarDemographic.data.DemographicData"%>
<%@ page import="oscar.oscarMessenger.data.MsgDisplayMessage" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%
      String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
	  boolean authed=true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_msg" rights="r" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect("../securityError.jsp?type=_msg");%>
</security:oscarSec>
<%
if(!authed) {
	return;
}
%>

<%
    final int PAGE_TYPE_DEFAULT = 0;
    final int PAGE_TYPE_SENT = 1;
    final int PAGE_TYPE_DELETED = 2;
    final int PAGE_TYPE_DEMOGRAPHIC = 3;

    int pageType = PAGE_TYPE_DEFAULT;

    String boxType = request.getParameter("boxType");
    if ("1".equals(boxType))
    {
        pageType = PAGE_TYPE_SENT;
    }
    else if ("2".equals(boxType))
    {
        pageType = PAGE_TYPE_DELETED;
    }
    else if ("3".equals(boxType))
    {
        pageType = PAGE_TYPE_DEMOGRAPHIC;
    }

String demographic_no = request.getParameter("demographic_no");
String demographic_name = "";
if ( demographic_no != null ) {
    DemographicData demographic_data = new DemographicData();
    org.oscarehr.common.model.Demographic demographic = demographic_data.getDemographic(LoggedInInfo.getLoggedInInfoFromSession(request), demographic_no);    
    if (demographic != null){
       demographic_name = demographic.getLastName() + ", " + demographic.getFirstName();
    }
}

    String providerNo = LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo();

pageContext.setAttribute("pageType",""+pageType);

if (request.getParameter("orderby") != null){
    String orderby = request.getParameter("orderby");
    String sessionOrderby = (String) session.getAttribute("orderby");
    if (sessionOrderby != null && sessionOrderby.equals(orderby)){
        orderby = "!"+orderby;
    }
    session.setAttribute("orderby",orderby);
}
String orderby = (String) session.getAttribute("orderby");

int pageNum = request.getParameter("page")==null ? 1 : Integer.parseInt(request.getParameter("page"));
%>
<jsp:useBean id="DisplayMessagesBeanId" scope="session" class="oscar.oscarMessenger.pageUtil.MsgDisplayMessagesBean" />
<jsp:setProperty name="DisplayMessagesBeanId" property="*" />
<jsp:useBean id="ViewMessageForm" scope="session" class="oscar.oscarMessenger.pageUtil.MsgViewMessageForm"/>


<html:html locale="true">
<head>
<html:base />
<link rel="stylesheet" type="text/css" href="encounterStyles.css">
<title>
<bean:message key="oscarMessenger.DisplayMessages.title"/>
</title>
<style type="text/css">
td.messengerButtonsA{
    /*background-color: #6666ff;*/
    /*background-color: #6699cc;*/
    background-color: #003399;
}
td.messengerButtonsD{
    /*background-color: #84c0f4;*/
    background-color: #555599;
}
a.messengerButtons{
    color: #ffffff;
    font-size: 9pt;
    text-decoration: none;
}


table.messButtonsA{
border-top: 2px solid #cfcfcf;
border-left: 2px solid #cfcfcf;
border-bottom: 2px solid #333333;
border-right: 2px solid #333333;
}

table.messButtonsD{
border-top: 2px solid #333333;
border-left: 2px solid #333333;
border-bottom: 2px solid #cfcfcf;
border-right: 2px solid #cfcfcf;
}

tr.newMessage {

}

tr.newMessage td {
     font-weight: bold;
}

.TopStatusBar{
width:100% !important;
}
</style>

<script type="text/javascript">
function BackToOscar()
{
    if (opener.callRefreshTabAlerts) {
	opener.callRefreshTabAlerts("oscar_new_msg");
        setTimeout("window.close()", 100);
    } else {
        window.close();
    }
}

function uload(){
    if (opener.callRefreshTabAlerts) {
	opener.callRefreshTabAlerts("oscar_new_msg");
        setTimeout("window.close()", 100);
        return false;
    }
    return true;
}

function checkAll(formId){
   var f = document.getElementById(formId);
   var val = f.checkA.checked;
   for (i =0; i < f.messageNo.length; i++){
      f.messageNo[i].checked = val;
   }
}

</script>
</head>

<body class="BodyStyle" vlink="#0000FF" onload="window.focus()" onunload="return uload()">
    <table  class="MainTable" id="scrollNumber1" name="encounterTable">
        <tr class="MainTableTopRow">
            <td class="MainTableTopRowLeftColumn">
                <bean:message key="oscarMessenger.DisplayMessages.msgMessenger"/>
            </td>
            <td class="MainTableTopRowRightColumn">
                <table class="TopStatusBar">
                    <tr>
                        <td >
                        <% String inbxStyle = "messengerButtonsA";
                           String sentStyle = "messengerButtonsA";
                           String delStyle  = "messengerButtonsA";
                        switch(pageType){
                            case PAGE_TYPE_DEFAULT: %>
     		                    <div class="DivContentTitle"><bean:message key="oscarMessenger.DisplayMessages.msgInbox"/></div>
                        <%      inbxStyle = "messengerButtonsD";
                            break;
                            case PAGE_TYPE_SENT: %>
                                <div class="DivContentTitle"><bean:message key="oscarMessenger.DisplayMessages.msgSentTitle"/></div>
                        <%      sentStyle = "messengerButtonsD";
                            break;
                            case PAGE_TYPE_DELETED: %>
                                <div class="DivContentTitle"><bean:message key="oscarMessenger.DisplayMessages.msgArchived"/></div>
                        <%      delStyle =  "messengerButtonsD";
                            break;
                            case PAGE_TYPE_DEMOGRAPHIC: %>
                                <div class="DivContentTitle">Messages related to <%=demographic_name%> </div> 
                        <%      delStyle =  "messengerButtonsD";
                            break;
                        }%>
                        </td>
                        <td  >
                            <!-- edit 2006-0811-01 by wreby -->
                            <html:form action="/oscarMessenger/DisplayMessages">
                            <input name="boxType" type="hidden" value="<%=pageType%>">
                            <input name="searchString" type="text" size="20" value="<jsp:getProperty name="DisplayMessagesBeanId" property="filter"/>">
                            <input name="btnSearch" type="submit" value="<bean:message key="oscarMessenger.DisplayMessages.btnSearch"/>">
                            <input name="btnClearSearch" type="submit" value="<bean:message key="oscarMessenger.DisplayMessages.btnClearSearch"/>">
                            </html:form>
                            <!-- end edit 2006-0811-01 by wreby -->
                        </td>
                        <td style="text-align:right">	
									<oscar:help keywords="&Title=Messenger&portal_type%3Alist=Document" key="app.top1"/>&nbsp;|
        							<a href="<%=request.getContextPath()%>/oscarEncounter/About.jsp" target="_new"><bean:message key="global.about" /></a>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td class="MainTableLeftColumn">
            &nbsp;
            </td>
            <td class="MainTableRightColumn">
                <table width="100%">
                    <tr>
                        <td>
                            <table  cellspacing=3 >
                                <tr>
                                    <td >
                                        <table class=messButtonsA cellspacing=0 cellpadding=3><tr><td class="messengerButtonsA">
                                        <html:link page="/oscarMessenger/CreateMessage.jsp" styleClass="messengerButtons">
                                         <bean:message key="oscarMessenger.DisplayMessages.btnCompose"/>
                                        </html:link>
                                        </td></tr></table>
                                    </td>
                                    <td >
                                        <table class=messButtonsA cellspacing=0 cellpadding=3><tr><td class="messengerButtonsA">
                                        <html:link page="/oscarMessenger/DisplayMessages.jsp" styleClass="messengerButtons">
                                         <bean:message key="oscarMessenger.DisplayMessages.btnRefresh"/>
                                        </html:link>
                                        </td></tr></table>
                                    </td>
                                    <td >
                                        <table class=messButtonsA cellspacing=0 cellpadding=3><tr><td class="messengerButtonsA">
                                        <html:link page="/oscarMessenger/DisplayMessages.jsp?boxType=1" styleClass="messengerButtons">
                                         <bean:message key="oscarMessenger.DisplayMessages.btnSent"/><!--sentMessage--link-->
                                        </html:link>
                                        </td></tr></table>
                                    </td>
                                    <td >
                                        <table class=messButtonsA cellspacing=0 cellpadding=3><tr><td class="messengerButtonsA">
                                        <html:link page="/oscarMessenger/DisplayMessages.jsp?boxType=2" styleClass="messengerButtons">
                                         <bean:message key="oscarMessenger.DisplayMessages.btnDeletedMessage"/><!--deletedMessage--link-->
                                        </html:link>
                                        </td></tr></table>
                                    </td>
                                    <td >
                                        <table class=messButtonsA cellspacing=0 cellpadding=3><tr><td class="messengerButtonsA">
                                        <a href="javascript:BackToOscar()" class="messengerButtons"><bean:message key="oscarMessenger.DisplayMessages.btnExit"/></a>
                                        </td></tr></table>
                                    </td>
                                </tr>
                            </table>

                        </td>
                    </tr>
                    <%String strutsAction = "/oscarMessenger/DisplayMessages";
                        if (pageType == PAGE_TYPE_DELETED){
                            strutsAction = "/oscarMessenger/ReDisplayMessages";
                        }
                    %>

                         <html:form action="<%=strutsAction%>" styleId="msgList" >
                    <%
                        List<MsgDisplayMessage> theMessages2;
                        switch(pageType)
                        {
                            case PAGE_TYPE_SENT:
                                theMessages2 = DisplayMessagesBeanId.estSentItemsInbox(orderby, pageNum, providerNo);
                                break;
                            case PAGE_TYPE_DELETED:
                                theMessages2 = DisplayMessagesBeanId.estDeletedInbox(orderby, pageNum, providerNo);
                                break;
                            case PAGE_TYPE_DEMOGRAPHIC:
                                theMessages2 = DisplayMessagesBeanId.estDemographicInbox(orderby, demographic_no);
                                break;
                            case PAGE_TYPE_DEFAULT:
                            default:
                                theMessages2 = DisplayMessagesBeanId.estInbox(orderby, pageNum, providerNo);
                                break;

                        }   //messageid
%>

                    <tr>
                        <td>
                            <table border="0" width="90%" cellspacing="1">

                    <tr><td colspan="6">
                    <table style="width:100%;">
                    <tr>
                        <td>
                            <%
                                if (pageType == PAGE_TYPE_DEFAULT)
                                {
                            %>
                                    <input name="btnDelete" type="submit" value="<bean:message key="oscarMessenger.DisplayMessages.formArchive"/>">
                            <%
                                }
                                else if (pageType == PAGE_TYPE_DELETED)
                                {
                            %>
                                    <input type="submit" value="<bean:message key="oscarMessenger.DisplayMessages.formUnarchive"/>">
                            <%}%>
                            &nbsp;
                        </td>
                        <td align="right">
		                    <%
		                    int recordsToDisplay = 25;
		                    
		                    String previous = "";
		                    String next = "";
		                    String path = request.getContextPath()+"/oscarMessenger/DisplayMessages.jsp?boxType=" + pageType + "&page=";

		                    if (pageType != PAGE_TYPE_DEMOGRAPHIC)
		                    {
                                int totalMsgs = DisplayMessagesBeanId.getTotalMessages(pageType);
                                int totalPages = totalMsgs / recordsToDisplay + (totalMsgs % recordsToDisplay == 0 ? 0 : 1);
                                if(pageNum > 1)
                                {
                                    previous = "<a href='" + path + (pageNum - 1) + "' title='previous page'><< Previous</a> ";
                                    out.print(previous);
                                }

                                if(pageNum < totalPages)
                                {
                                    next = "<a href='" + path + (pageNum + 1) + "' title='next page'>Next >></a>";
                                    out.print(next);
                                }
		                    }
                            %>
                        </td>
                   </tr>
                   </table>
                   </td></tr>
                   
                                <tr>
                                    <th align="left" bgcolor="#DDDDFF" width="75">
                                    <%
                                        if (pageType != PAGE_TYPE_SENT)
                                        {
                                    %>
                                       <input type="checkbox" name="checkAll2" onclick="checkAll('msgList')" id="checkA" />
                                    <%} %>   
                                    </th>
                                    <th align="left" bgcolor="#DDDDFF">
                                        <html:link page="/oscarMessenger/DisplayMessages.jsp?orderby=status"  paramId="boxType" paramName="pageType">
                                        <bean:message key="oscarMessenger.DisplayMessages.msgStatus"/>
                                        </html:link>
                                    </th>
                                    <th align="left" bgcolor="#DDDDFF">
                                      <%
                                          if (pageType == PAGE_TYPE_SENT)
                                          {
                                      %>
                                                <html:link page="/oscarMessenger/DisplayMessages.jsp?orderby=sentto" paramId="boxType" paramName="pageType">
                                                <bean:message key="oscarMessenger.DisplayMessages.msgTo"/>
                                                </html:link>
                                       <%
                                          }
                                          else
                                          {
                                       %>
                                                <html:link page="/oscarMessenger/DisplayMessages.jsp?orderby=from" paramId="boxType" paramName="pageType">
                                                <bean:message key="oscarMessenger.DisplayMessages.msgFrom"/>
                                                </html:link>
                                       <% } %>   
                                    </th>
                                    <th align="left" bgcolor="#DDDDFF">
                                            <html:link page="/oscarMessenger/DisplayMessages.jsp?orderby=subject" paramId="boxType" paramName="pageType">
                                            <bean:message key="oscarMessenger.DisplayMessages.msgSubject"/>
                                            </html:link>
                                    </th>
                                    <th align="left" bgcolor="#DDDDFF">
                                            <html:link page="/oscarMessenger/DisplayMessages.jsp?orderby=date" paramId="boxType" paramName="pageType">
                                            <bean:message key="oscarMessenger.DisplayMessages.msgDate"/>
                                            </html:link>
                                    </th>
                                    <th align="left" bgcolor="#DDDDFF">
                                            <html:link page="/oscarMessenger/DisplayMessages.jsp?orderby=patient" paramId="boxType" paramName="pageType">
                                            <bean:message key="oscarMessenger.DisplayMessages.msgLinked"/>
                                            </html:link>
                                    </th>
                                </tr>
                                
                               
                                <!--   for loop Control Initiliation variabe changed to nextMessage   -->
                            <%
                                for (MsgDisplayMessage displayMessage : theMessages2)
                                {
                                    String key = "oscarMessenger.DisplayMessages.msgStatus" + displayMessage.status.substring(0, 1).toUpperCase() + displayMessage.status.substring(1);
                                    if ("oscarMessenger.DisplayMessages.msgStatusNew".equals(key))
                                    {
                                %>
                                <tr class="newMessage">
                                <%
                                    }
                                    else
                                    {
                                %>
                                <tr>
                                    <%}%>
                                    <td bgcolor="#EEEEFF" width="75">
                                        <%
                                            if (pageType != PAGE_TYPE_SENT)
                                            {
                                        %>
                                        <html:checkbox property="messageNo" value="<%=displayMessage.messageId %>"/>
                                        <% } %>
                                        &nbsp;
                                        <%
                                            String atta = displayMessage.attach;
                                            String pdfAtta = displayMessage.pdfAttach;
                                            if (atta.equals("1") || pdfAtta.equals("1"))
                                            { %>
                                        <img src="img/clip4.jpg">
                                        <% } %>


                                    </td>
                                    <td bgcolor="#EEEEFF">
                                        <bean:message key="<%= key %>"/>
                                    </td>
                                    <td bgcolor="#EEEEFF">
                                        <%
                                            if (pageType == PAGE_TYPE_SENT)
                                            {
                                                int pos = displayMessage.sentto.indexOf(",");
                                                if (pos == -1)
                                                {
                                                    out.print(displayMessage.sentto);
                                                }
                                                else
                                                {
                                                    out.print(displayMessage.sentto.substring(0, pos));
                                                }
                                            }
                                            else
                                            {
                                                out.print(displayMessage.sentby);
                                            }
                                        %>

                                    </td>
                                    <td bgcolor="#EEEEFF">
                                        <a href="<%=request.getContextPath()%>/oscarMessenger/ViewMessage.do?messageID=<%=displayMessage.messageId%>&boxType=<%=pageType%>">
                                            <%=displayMessage.thesubject%>
                                        </a>

                                    </td>
                                    <td bgcolor="#EEEEFF">
                                        <%= displayMessage.thedate %>
                                        &nbsp;&nbsp;
                                        <%= displayMessage.theime %>
                                    </td>
                                    <td bgcolor="#EEEEFF">
                                        <%
                                            if (displayMessage.demographic_no != null && !displayMessage.demographic_no.equalsIgnoreCase("null"))
                                            {
                                        %>
                                        <oscar:nameage
                                                demographicNo="<%=displayMessage.demographic_no%>"></oscar:nameage>
                                        <%} %>
                                    </td>
                                </tr>
                                <%}%>
                            
                            <tr><td colspan="6">
                               <table width="100%">
                                <tr>
                                    <td>
                                         <%
                                             if (pageType == PAGE_TYPE_DEFAULT)
                                             {
                                         %>
                                            <input name="btnDelete" type="submit" value="<bean:message key="oscarMessenger.DisplayMessages.formArchive"/>">
                                         <%
                                             }
                                             else if (pageType == PAGE_TYPE_DELETED)
                                             {
                                         %>
                                            <input type="submit" value="<bean:message key="oscarMessenger.DisplayMessages.formUnarchive"/>">
                                            <%}%>  
                                    </td>

                                    <td align="right">
                                    <%                                    	
                                    if (pageType != PAGE_TYPE_DEMOGRAPHIC)
                                    {
                                    	out.print(previous + next);
                                    }
                                    %>    
                                    </td>
                                </tr>
                              </table>
                            </td></tr>
                            </table>  

                                    
                         </html:form>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td class="MainTableBottomRowLeftColumn">

            </td>
            <td class="MainTableBottomRowRightColumn">

            </td>
        </tr>
    </table>
</body>
</html:html>