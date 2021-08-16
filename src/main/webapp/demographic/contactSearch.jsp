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

<%@ page import="org.oscarehr.common.web.ContactAction"%>
<%@ page import="org.oscarehr.common.model.Contact"%>
<%@ page import="org.apache.commons.text.StringEscapeUtils"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Properties" %>
<%@ page import="java.net.URLEncoder" %>

<%@ include file="/taglibs.jsp"%>

<%
  String strLimit1="0";
  String strLimit2="10";
  if(request.getParameter("limit1")!=null) strLimit1 = request.getParameter("limit1");
  if(request.getParameter("limit2")!=null) strLimit2 = request.getParameter("limit2");

  int nItems = 0;
  Properties prop = null;
  String form = request.getParameter("form")==null?"":request.getParameter("form") ;
  String elementName = request.getParameter("elementName")==null?"":request.getParameter("elementName") ;
  String elementId = request.getParameter("elementId")==null?"":request.getParameter("elementId") ;
  String keyword = request.getParameter("keyword");

	if (request.getParameter("submit") != null 
		&& (request.getParameter("submit").equals("Search")
		|| request.getParameter("submit").equals("Next Page") 
		|| request.getParameter("submit").equals("Last Page")) ) {
			  
	  String search_mode = request.getParameter("search_mode")==null?"search_name":request.getParameter("search_mode");
	  String orderBy = request.getParameter("orderby")==null?"c.lastName,c.firstName":request.getParameter("orderby");
	  String list = request.getParameter("list");
	  List<Contact> contacts;
	  
	  if( "all".equalsIgnoreCase(list) ) {
		  contacts = ContactAction.searchAllContacts(search_mode, orderBy, keyword);
	  } else {
		  contacts = ContactAction.searchContacts(search_mode, orderBy, keyword);
	  }
	  List<String> existingContacts =  ContactAction.getDemographicContacts(request.getParameter("demoNo"));
	  pageContext.setAttribute("existingContacts", existingContacts);
	   
	  nItems = contacts.size();
	  pageContext.setAttribute("contacts", contacts);
	}
	
	
%>

<html:html locale="true">
<head>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
<title>Search Contacts</title>
<link rel="stylesheet" type="text/css" media="all" href="../share/css/extractedFromPages.css"  />
<script type="text/javascript" >

		function setfocus() {
		  this.focus();
		  document.forms[0].keyword.focus();
		  document.forms[0].keyword.select();
		}
		function check() {
		  document.forms[0].submit.value="Search";
		  return true;
		}
		function selectResult(data1, data2, existingContacts) {
		    if (existingContacts.includes(data1))
		    {
		        //put an alert here to prevent adding the existing contact
				alert(data2  + " is already recorded as a Contact.");
		        return;
		    }

			try {
				serializePopupData(data1, data2);
			} catch(error) {
				opener.document.<%=form%>.elements['<%=elementId%>'].value = data1;
				opener.document.<%=form%>.elements['<%=elementName%>'].value = data2;
				self.close();
			}

		}
		
		function serializePopupData(data1, data2) {
			var id1 = '<%=elementId%>';
			var id2 = '<%=elementName%>';
			var data = '{"' + id1 + '":"' + data1 + '","' + id2 + '":"' + data2 + '"}';		
			opener.popUpData(data);
			self.close();
		}
		                

</script>
</head>
<body onload="setfocus()">
	
<form method="post" name="titlesearch" action="contactSearch.jsp" onSubmit="return check();">
<table bgcolor="#CCCCFF" width="100%">
	<tr>
		<td class="searchTitle" colspan="4">Search Contacts</td>
	</tr>
	<tr>
		<td class="blueText" width="10%" nowrap>
			<input type="radio" name="search_mode" value="search_name" checked="checked"> Name
		</td>
		<td valign="middle" rowspan="2" align="left">
			<input type="text" name="keyword" value="" size="17" maxlength="100"> 
			<input type="hidden" name="orderby" value="c.lastName, c.firstName"> 
			<input type="hidden" name="limit1" value="0"> 
			<input type="hidden" name="limit2" value="10"> 
			<input type="hidden" name="submit" value='Search'> 
			<input type="submit" value='Search'>
		</td>
	</tr>	
</table>
<table>
	<tr>
		<td align="left">Results based on keyword(s): <%=keyword==null?"":keyword%></td>
	</tr>
</table>
<input type='hidden' name='form' value="<%=StringEscapeUtils.escapeHtml4(form)%>"/>
<input type='hidden' name='elementName' value="<%=StringEscapeUtils.escapeHtml4(elementName)%>"/>
<input type='hidden' name='elementId' value="<%=StringEscapeUtils.escapeHtml4(elementId)%>"/>
</form>

<table bgcolor="#C0C0C0" width="100%">
	<tr class="title" >
		<th>Last Name</th>
		<th>First Name</th>		
		<th>Home Phone</th>
        <th>Cell Phone</th>
        <th>Note</th>
	</tr>
	
	<c:forEach var="contact" items="${ contacts }" varStatus="i">
		<%
			Contact contact = (Contact)pageContext.getAttribute("contact");
			List<String> existingContacts = (List<String>)pageContext.getAttribute("existingContacts");
			///put an array of the existing contact id's here.
			javax.servlet.jsp.jstl.core.LoopTagStatus i = (javax.servlet.jsp.jstl.core.LoopTagStatus) pageContext.getAttribute("i");
			String bgColor = i.getIndex()%2==0?"#EEEEFF":"ivory";	
			
			String contactFullName = contact.getLastName() + "," + contact.getFirstName();
			// This seems to just be for display purposes
			// The actual thing being used to eventually link the entry is the ID being passed along
			contactFullName = contactFullName.replaceAll("\"", "");

		%>
		<tr bgcolor="<%=bgColor%>"
			onMouseOver="this.style.cursor='hand';this.style.backgroundColor='pink';"
			onMouseout="this.style.backgroundColor='<%=bgColor%>';"
			onClick="selectResult('<%=contact.getId()%>', '<%=StringEscapeUtils.escapeEcmaScript(contactFullName)%>', '<%=existingContacts%>');">
			<td><c:out value="${contact.lastName}"/></td>
			<td><c:out value="${contact.firstName}"/></td>
			<td><c:out value="${contact.residencePhone}" default=""/></td>
            <td><c:out value="${contact.cellPhone}" default=""/></td>
            <td><c:out value="${contact.note}" default=""/></td>
		</tr>
	</c:forEach>
	
	
</table>

<%
  int nLastPage=0,nNextPage=0;
  nNextPage=Integer.parseInt(strLimit2)+Integer.parseInt(strLimit1);
  nLastPage=Integer.parseInt(strLimit1)-Integer.parseInt(strLimit2);
%> <%
  if(nItems==0 && nLastPage<=0) {
%> <bean:message key="demographic.search.noResultsWereFound" /> <%
  }
%> 
<script type="text/javascript" >

function last() {
  document.nextform.action="contactSearch.jsp?form=<%=URLEncoder.encode(form,"UTF-8")%>&elementName=<%=URLEncoder.encode(elementName,"UTF-8")%>&elementId=<%=URLEncoder.encode(elementId,"UTF-8")%>&keyword=<%=request.getParameter("keyword")%>&search_mode=<%=request.getParameter("search_mode")%>&orderby=<%=request.getParameter("orderby")%>&limit1=<%=nLastPage%>&limit2=<%=strLimit2%>" ;
  document.nextform.submit();
}
function next() {
  document.nextform.action="contactSearch.jsp?form=<%=URLEncoder.encode(form,"UTF-8")%>&elementName=<%=URLEncoder.encode(elementName,"UTF-8")%>&elementId=<%=URLEncoder.encode(elementId,"UTF-8")%>&keyword=<%=request.getParameter("keyword")%>&search_mode=<%=request.getParameter("search_mode")%>&orderby=<%=request.getParameter("orderby")%>&limit1=<%=nNextPage%>&limit2=<%=strLimit2%>" ; 
  document.nextform.submit();
}

</script>

<form method="post" name="nextform" action="contactSearch.jsp">
<%
  if(nLastPage>=0) {
%> <input type="submit" class="mbttn" name="submit"
	value="<bean:message key="demographic.demographicsearch2apptresults.btnPrevPage"/>"
	onClick="last()"> <%
  }
  if(nItems==Integer.parseInt(strLimit2)) {
%> <input type="submit" class="mbttn" name="submit"
	value="<bean:message key="demographic.demographicsearch2apptresults.btnNextPage"/>"
	onClick="next()"> <%
}
%>
</form>
<br>
<a href="Contact.do?method=addContact">Add/Edit Contact</a>
</body>
</html:html>
