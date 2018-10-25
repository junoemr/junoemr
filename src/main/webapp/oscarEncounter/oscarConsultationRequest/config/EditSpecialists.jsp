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

<%@ page import="java.net.URLDecoder" %>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%
	  String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
	  boolean authed=true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_admin,_admin.consult" rights="w" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect("../../../securityError.jsp?type=_admin&type=_admin.consult");%>
</security:oscarSec>
<%
if(!authed) {
	return;
}
%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<html:html locale="true">
<jsp:useBean id="displayServiceUtil" scope="request"
	class="oscar.oscarEncounter.oscarConsultationRequest.config.pageUtil.EctConDisplayServiceUtil" />
<%
	int currentPage = 1;
	String ajaxSearch = "";
	long numOfSpecialists;
	int pageLimit = 25;
	String searchType = request.getParameter("searchType") == null ? "active" : request.getParameter("searchType");
	boolean activateSpecialists = false;

	if (searchType.equals("deleted"))
	{
		activateSpecialists = true;
	}

	if ( request.getParameter("page") != null ) {
		currentPage = Integer.parseInt(request.getParameter("page"));
	}

	if ( request.getParameter("pageLimit") != null) {
		if ( !request.getParameter("pageLimit").trim().equals("") ) {
			pageLimit = Integer.parseInt(request.getParameter("pageLimit"));
		}
	}


	if ( request.getParameter("search") != null ) { // If the current request to the page was requested by searching via search input
		ajaxSearch = URLDecoder.decode(request.getParameter("search"), "UTF-8").trim();

		if ( !ajaxSearch.equals("") ) {
			displayServiceUtil.estSpecialistVector(ajaxSearch, currentPage, pageLimit, searchType); // Pass the search query to the DB method
			numOfSpecialists = displayServiceUtil.getNumOfSpecialists(ajaxSearch, searchType); // Get count of search results for pagination
		} else {
			numOfSpecialists = displayServiceUtil.getNumOfSpecialists(searchType); // Get all specialists
			displayServiceUtil.estSpecialistVector(currentPage, pageLimit, searchType); // Get count of all specialists for pagination
		}
	} else {
		numOfSpecialists = displayServiceUtil.getNumOfSpecialists(searchType); // Get all specialists
		displayServiceUtil.estSpecialistVector(currentPage, pageLimit, searchType); // Get count of all specialists for pagination
	}

	int resultSize = displayServiceUtil.specIdVec.size();

	double approxNumOfPages = (double) numOfSpecialists / pageLimit;
	int numOfPages = (int) Math.ceil(approxNumOfPages);
	int currentPageIdx = currentPage - 1;

	int startIdx = 0;
	int endIdx = 10; // Controls how many pages you can select from at a time
	int paginationLimit = endIdx;

	// This set of code is intended to control the pagination functionality
	//
	// Intended functionality:
	//	- We want to only show 10 pages at a time, as the alternative is having a screen with too many page options
	//	- The first 1 - 9 pages that you click on won't effect the page options
	//	- As you click on page 10, it will begin to show pages after 10
	//	- Each page you click on between 10 and the maximum number of pages minus 5 will update the page options with the
	//	  4 pages before the current page, and the 5 pages after. The last 5 page options will act similar to the first 10
	//
	//	- e.g. 1 2 3 4 5 6 7 8 9 10
	//         ^ ^ ^ ^ ^ ^ ^ ^ ^
	//	       6 7 8 9 10 11 12 13 14 15
	//                  ^
	//         9 10 11 12 13 14 15 16 17 18
	//                     ^
	//	       11 12 13 14 15 16 17 18 19 20
	//                      ^  ^  ^  ^  ^  ^
	if ( numOfPages >= paginationLimit ) { // Logic for if there are more pages than the current page limit (10)
		// Since we want to always show the next 5 pages from the current page, if the total number of pages minus
		// the currently selected page is less than 5, then the page options will remain the same
		if ( numOfPages - currentPage < 5 ) {
			startIdx = numOfPages - paginationLimit; // Start counting pages from the maximum number of pages minus 10
			endIdx = numOfPages;
		} else if ( currentPage >= paginationLimit ) { // If you're not in the last 5 page options, but you are past the first 10
			startIdx = currentPageIdx - 4; // Start counting at current page minus 4
			endIdx = currentPageIdx + 6; // Finish at current page plus 6 to get the next 5 pages from the current page
		} else if ( currentPage < paginationLimit ) { // If your current page is below your pagination limit (10)
			startIdx = 0;
			endIdx = 10; // Get the first 10 specialists
		}
	} else { // If you have less than 10 pages, show all the pages
		startIdx = 0;
		endIdx = numOfPages;
	}


%>
<head>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
<title><bean:message
	key="oscarEncounter.oscarConsultationRequest.config.EditSpecialists.title" />
</title>
<html:base />
<%--<link rel="stylesheet" type="text/css" media="all" href="../../../share/css/extractedFromPages.css"  />--%>
	<style>
		.specialistPageLink {
			width: 25px;
			height: 12px;
			text-align: center;
			display: inline-block;
		}
		.ajaxSearch {
			padding-top: 10px;
			text-align: center;
		}
		.pageLimiter {
			padding-bottom: 8px;
		}
		.queryResult {
			color: #191919;
			font-size: 0.8em;
			position: absolute;
			bottom: 10px;
			left: 20px;
		}
        .navBtn {
            text-decoration: none;
            margin: 0 2px;
        }
	</style>
</head>
<script language="javascript">
function BackToOscar()
{
	   window.close();
}

</script>
<link rel="stylesheet" type="text/css" href="../../encounterStyles.css">
<body class="BodyStyle" vlink="#0000FF">
<html:errors />
<!--  -->
<table class="MainTable" id="scrollNumber1" name="encounterTable">
	<tr class="MainTableTopRow">
		<td class="MainTableTopRowLeftColumn">Consultation</td>
		<td class="MainTableTopRowRightColumn">
		<table class="TopStatusBar">
			<tr>
				<td class="Header"><bean:message
					key="oscarEncounter.oscarConsultationRequest.config.EditSpecialists.title" />
				</td>
			</tr>
		</table>
		</td>
	</tr>
	<tr style="vertical-align: top">
		<td class="MainTableLeftColumn">
		<%oscar.oscarEncounter.oscarConsultationRequest.config.pageUtil.EctConTitlebar titlebar = new oscar.oscarEncounter.oscarConsultationRequest.config.pageUtil.EctConTitlebar(request);
				  out.print(titlebar.estBar(request));
				  %>
		</td>
		<td class="MainTableRightColumn" style="width: 90%;">
		<table cellpadding="0" cellspacing="2"
			style="border-collapse: collapse" bordercolor="#111111" width="100%"
			height="100%">
			<tr>
				<td>
					<div class="ajaxSearch">
						<div class="searchInput">
							<input type="text" id="searchInput" placeholder="Search specialists..." onkeypress="runUpdate(event)" autofocus>
							<input type="button" onclick="updateBySearch('active')" value="Search">
							<input type="button" onclick="updateBySearch('deleted')" value="Search Deleted">
							<input type="button" onclick="updateBySearch('all')" value="Search All">

							<label>Page Limit:</label>
							<select name="limit" onchange="nextPage(this.options[this.selectedIndex].value, 1, '', '<%=searchType%>')">
								<option value="25" <%= pageLimit != 50 && pageLimit != 100 ? "selected='selected'" : "" %> >25</option>
								<option value="50" <%= pageLimit == 50 ? "selected='selected'" : "" %> >50</option>
								<option value="100" <%= pageLimit == 100 ? "selected='selected'" : "" %> >100</option>
							</select>
						</div>
					</div>
				</td>
			</tr>
			<!----Start new rows here-->
			<tr>
							<td><%--bean:message
					key="oscarEncounter.oscarConsultationRequest.config.EditSpecialists.msgCheckOff" /--%><br>
				<bean:message
					key="oscarEncounter.oscarConsultationRequest.config.EditSpecialists.msgClickOn" /><br>


				</td>
			</tr>
			<tr>
				<td><html:form action="/oscarEncounter/EditSpecialists">
					<div class="ChooseRecipientsBox1">
					<table>
						<tr>
							<th>&nbsp;</th>
							<th><bean:message
								key="oscarEncounter.oscarConsultationRequest.config.EditSpecialists.specialist" />
							</th>
							<th><bean:message
								key="oscarEncounter.oscarConsultationRequest.config.EditSpecialists.address" />
							</th>
							<th><bean:message
								key="oscarEncounter.oscarConsultationRequest.config.EditSpecialists.phone" />
							</th>
							<th><bean:message
								key="oscarEncounter.oscarConsultationRequest.config.EditSpecialists.fax" />
							</th>

						</tr>
						<tr>
							<td><!--<div class="ChooseRecipientsBox1">--> <%

								 for(int i=0; i < resultSize; i++){
									 String  specId     = displayServiceUtil.specIdVec.elementAt(i);
									 String  fName      = displayServiceUtil.fNameVec.elementAt(i);
									 String  lName      = displayServiceUtil.lNameVec.elementAt(i);
									 String  proLetters = displayServiceUtil.proLettersVec.elementAt(i);
									 String  address    = displayServiceUtil.addressVec.elementAt(i);
									 String  phone      = displayServiceUtil.phoneVec.elementAt(i);
									 String  fax        = displayServiceUtil.faxVec.elementAt(i);
							  %>

						<tr>
							<td><input type="checkbox" name="specialists"
								value="<%=specId%>"></td>
							<td>
							<%
									  out.print("<a href=\"../../EditSpecialists.do?specId="+specId+"\">");
									  out.print(lName+", "+fName+" "+(proLetters==null?"":proLetters));
									  out.print("</a>");
									%>
							</td>
							<td><%=address %></td>
							<td><%=phone%></td>
							<td><%=fax%></td>
						</tr>
						<% }%>
						</td>
						</tr>

					</table>
						</br>
						<% if(activateSpecialists)
						{
						%>
							<button type="submit" name="activate" value="activate"><bean:message key="oscarEncounter.oscarConsultationRequest.config.EditSpecialists.btnActivateSpecialist"/></button>
						<%
						} else
						{
						%>
							<button type="submit" name="delete" value="delete"><bean:message key="oscarEncounter.oscarConsultationRequest.config.EditSpecialists.btnDeleteSpecialist"/></button>
						<%
							}
						%>
						<div style="text-align: center; padding-top: 20px; font-size: 1.3em;">
							<% if (currentPageIdx != 0)
							{%>
                            	<a href="javascript:void(0)" class="navBtn" onclick="toPage('first', '<%=searchType%>')"><<</a>
                            	<a href="javascript:void(0)" class="navBtn" onclick="toPage('previous', '<%=searchType%>')"><</a>
							<%}
							for(int i = startIdx; i < endIdx; i++) {
								int pageNum = i + 1;%>
								<a href="javascript:void(0)" class="specialistPageLink" id="page<%=pageNum%>" onclick="nextPage(<%=pageLimit%>, <%=pageNum%>, '<%=ajaxSearch%>', '<%=searchType%>')"><%=pageNum%></a>
							<% }
							if (currentPageIdx != endIdx-1)
							{%>
                            	<a href="javascript:void(0)" class="navBtn" onclick="toPage('next', '<%=searchType%>')">></a>
                            	<a href="javascript:void(0)" class="navBtn" onclick="toPage('last', '<%=searchType%>')">>></a>
							<%}%>
						</div>
					</div>
				</html:form></td>
			</tr>
			<!----End new rows here-->

			<tr height="100%">
				<td></td>
			</tr>
			<span class="queryResult">
			<%
				if ( !ajaxSearch.equals("") ) {
					out.print("Showing " + resultSize + " of " + numOfSpecialists + " results for '"  + ajaxSearch + "' (" + currentPage + " of " + numOfPages + ")");
				} else {
					out.print("Showing " + resultSize + " of " + numOfSpecialists + " results (" + currentPage + " of " + numOfPages + ")");
				}
			%>
			</span>
		</table>
		</td>
	</tr>

	<tr>
		<td class="MainTableBottomRowLeftColumn"></td>
		<td class="MainTableBottomRowRightColumn"></td>
	</tr>
</table>
<script src="<%=request.getContextPath()%>/share/javascript/jquery/jquery-2.2.4.min.js"></script>
<script>
	var currentPage = document.getElementById("page<%=currentPage%>");
	var searchInput = document.getElementById("searchInput");

	searchInput.focus();

	if ( currentPage != null ) {
		currentPage.style.fontWeight = "bold";
		currentPage.style.textDecoration = "none";
	}

	function nextPage(pageLimit, page, searchText, type) {
		$(document.body).load("<%=request.getContextPath()%>/oscarEncounter/oscarConsultationRequest/config/EditSpecialists.jsp?pageLimit=" + pageLimit + "&page=" + page + "&search=" + encodeURIComponent(searchText) + "&searchType=" + type);
	}

	function updateBySearch(type) {
		var searchText = searchInput.value;
		var page = 1;
		$(document.body).load("<%=request.getContextPath()%>/oscarEncounter/oscarConsultationRequest/config/EditSpecialists.jsp?pageLimit=" + <%=pageLimit%> + "&page=" + page + "&search=" + encodeURIComponent(searchText) + "&searchType=" + type);
	}

	function toPage(toPagePosition, type) {
	    var searchText = searchInput.value;
	    var page = 1;
	    var currentPage = <%=currentPage%>;

	    switch(toPagePosition) {
            case 'first':
                page = 1;
                break;
            case 'previous':
                page = currentPage - 1;
                break;
            case 'next':
                page = currentPage + 1;
                break;
            case 'last':
                page = <%=numOfPages%>
                break;
            default:
                page = 1;
        }

        $(document.body).load("<%=request.getContextPath()%>/oscarEncounter/oscarConsultationRequest/config/EditSpecialists.jsp?pageLimit=" + <%=pageLimit%> + "&page=" + page + "&search=" + encodeURIComponent(searchText) + "&searchType=" + type);
    }

	function runUpdate(e) {
		if ( e.keyCode == 13 ) {
			updateBySearch('active');
		}
	}

</script>
</body>
</html:html>
