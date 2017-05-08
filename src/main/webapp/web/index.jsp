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

<%
// Force the page to un-cache itself so user cannot go back after logout
// The 3 lines ensure that all browsers are covered
// They are necessary for URL not showing this file name (index.jsp)
response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0);

session.setAttribute("useIframeResizing", "true");  //Temporary Hack
%>

<!DOCTYPE html>
<!-- ng* attributes are references into AngularJS framework -->
<html lang="en" ng-app="oscarProviderViewModule">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">
<link rel="shortcut icon" href="../images/Oscar.ico">

<title><bean:message key="global.title" bundle="ui"/></title>

<link href="../library/bootstrap/3.0.0/css/bootstrap.css" rel="stylesheet">
<link href="../css/font-awesome.css" rel="stylesheet">
<link href="../css/loading-bar.css" rel="stylesheet">


<!-- we will combine/minify later -->
<link href="css/navbar-fixed-top.css" rel="stylesheet">
<link href="css/navbar-demo-search.css" rel="stylesheet">
<link href="css/patient-list.css" rel="stylesheet">

<link href="../library/ng-table/ng-table.css" rel="stylesheet">

<link href="../library/bootstrap2-datepicker/datepicker3.css" rel="stylesheet">

<link href="../css/bootstrap-timepicker.min.css" rel="stylesheet">

<link href="../library/bootstrap/3.0.0/assets/css/bootstrap3_badge_colours.css" rel="stylesheet">

<%--Place custom styles here to override bootstap styles--%>
<link href="./css/index.css" rel="stylesheet">
<link href="./css/patient-list.css" rel="stylesheet">
<link href="./css/record.css" rel="stylesheet">

</head>
	
<body ng-controller="OscarCtrl">

	<!-- Fixed navbar -->
	<div class="navbar navbar-default navbar-fixed-top" ng-controller="NavBarCtrl" ng-show="me != null" ng-cloak>
		<div class="container-fluid">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
					<span class="icon-bar"></span> <span class="icon-bar"></span> <span class="icon-bar"></span>
				</button>
				
				<!-- link back to 'classic' view -->
				<a  href="../provider/providercontrol.jsp"><img id="navbarlogo" src="../images/logo-white.png" title="<bean:message key="global.goToClassic" bundle="ui"/>" border="0" /></a>
			</div>
			
			 
			<div class="navbar-collapse collapse">
			
				<form class="navbar-form navbar-left" role="search">
	 				<div class="form-group">
		 				<div class="input-group">
			 				<input type="text" class="form-control search-query" placeholder="<bean:message key="navbar.searchPatients" bundle="ui"/>" id="demographicQuickSearch" autocomplete="off" value="">
			 				<span class="input-group-addon btn-default hand-hover" ng-click="goToPatientSearch()" title="<bean:message key="navbar.searchPatients" bundle="ui"/>"><span class="glyphicon glyphicon-search" ></span></span>
			 				<span class="input-group-addon btn-default hand-hover"  ng-click="newDemographic('sm')" title="<bean:message key="navbar.newPatient" bundle="ui"/>"><span class="glyphicon glyphicon-plus"></span></span>	 				
						</div>						
					</div>			
				</form>
			
				<!-- large view -->
				<ul class="nav navbar-nav visible-lg hidden-md hidden-sm hidden-xs">
					<li style="margin-right:5px"><span class="navbar-text glyphicon glyphicon-chevron-right hand-hover" 
						ng-show="showPtList === false" ng-click="showPatientList()" 
						title="<bean:message key="navbar.showPatientList" bundle="ui"/>"></span></li>

					<li ng-repeat="item in menuItems" ng-class="isActive(item)">
						<a href="javascript:void(0)" ng-if="!item.dropdown" ng-click="transition(item)" >{{item.label}} 
							<span ng-if="item.label=='Inbox' && unAckLabDocTotal>0" class="badge badge-danger">{{unAckLabDocTotal}}</span>
						</a>
						<a href="javascript:void(0)" ng-if="item.dropdown"  class="dropdown-toggle" data-toggle="dropdown">{{item.label}}
							<span class="caret more"></span>
						</a>
						<ul ng-if="item.dropdown" class="dropdown-menu" role="menu">
							<li ng-repeat="dropdownItem in item.dropdownItems" >
								<a href="javascript:void(0)" ng-click="transition(dropdownItem)" >{{dropdownItem.label}}</a>
							</li>
						</ul>
					</li>
				</ul>

				<!-- more condensed version -->
				<ul class="nav navbar-nav hidden-lg visible-md visible-sm visible-xs">	
					<li style="margin-right:5px"><span class="navbar-text glyphicon glyphicon-chevron-right hand-hover" ng-show="showPtList === false" ng-click="showPatientList()" title="<bean:message key="navbar.showPatientList" bundle="ui"/>"></span></li>
						
					<li class="dropdown hand-hover"><a href="void()" class="dropdown-toggle"><bean:message key="navbar.modules" bundle="ui"/><b class="caret"></b></a>
						<ul class="dropdown-menu">
						<li ng-repeat="item in menuItems"  ng-class="{'active': isActive(item) }">
						<a ng-click="transition(item)" data-toggle="tab" >{{item.label}}
							<span ng-if="item.extra.length>0">({{item.extra}})</span>
						</a>
					</li>
						<li class="divider"></li>
							<li ng-repeat="item in moreMenuItems">
								<a ng-class="{'active': isActive(item) }" ng-click="transition(item)">{{item.label}}
								<span ng-if="item.extra.length>0" class="badge">{{item.extra}}</span></a>
							</li>
						</ul>
					</li>		
				</ul>
				
				
				<div class="navbar-text pull-right" style="line-height:20px">
					<a onClick="popup(700,1024,'../scratch/index.jsp','scratch')" title="<bean:message key="navbar.scratchpad" bundle="ui"/>" class="hand-hover">
					 	<span class="glyphicon glyphicon-edit"></span>
					</a>
					&nbsp;&nbsp;
					<span ng-show="messageRights === true">
						<a ng-click="openMessenger()" title="<bean:message key="navbar.messenger" bundle="ui"/>" class="hand-hover">
							<span  class="glyphicon glyphicon-envelope"></span> 
						</a>
						<%--Remove demographic message icon; Awaiting JSON fix so that a list 
						with one item is returned instead of an object. After fix we can use ng-repeat and refer change messengerMenu to item--%>
						<%--<span ng-repeat="item in messengerMenu">--%>
						   <a ng-click="openMessenger(messengerMenu)"  title="{{messengerMenu.label}}" class="hand-hover">{{messengerMenu.extra}}</a> <%--<span ng-if="!$last">|</span>--%>
						<%--</span>--%>
					</span>
					&nbsp; &nbsp;
					
					<span class="dropdown">
						<ul class="dropdown-menu" role="menu">
	                    	<li ng-repeat="item in programDomain">
	                        	<a ng-click="changeProgram(item.program.id)">
						    		<span ng-if="item.program.id === currentProgram.id">&#10004;</span>
						    		<span ng-if="item.program.id != currentProgram.id">&nbsp;&nbsp;</span>
									{{item.program.name}}
						    	</a>
						    </li>
					 	</ul>
				 	</span>
				 	
					&nbsp;
					
					<span class="dropdown-toggle hand-hover" data-toggle="dropdown" title="<bean:message key="navbar.user" bundle="ui"/>"><span class="glyphicon glyphicon-user"></span>{{me.firstName}}</span>
					<ul class="dropdown-menu" role="menu">
						<li ng-repeat="item in userMenuItems">
							<a ng-click="transition(item)" ng-class="{'more-tab-highlight': isActive(item) }" class="hand-hover" >{{item.label}}</a>
							<a ng-if="item.url" href="{{item.url}}" target="_blank">{{dropdownItem.label}}</a>
						</li>
				  	</ul>
				</div>
			</div>
			<!--/.nav-collapse -->
		</div>
	</div>

	<!-- nav bar is done here -->

	 
	 <!-- Start patient List template --> 

	<div class="container-fluid" ng-controller="PatientListCtrl" >
		<div class="row">
			<div id="left_pane" class="col-md-3 noprint" ng-if="showPatientList()">
			
				<%--<ul class="nav nav-tabs">			
					<li ng-repeat="item in tabItems" ng-class="{'active': isActive(item.id)}" class="hand-hover">
						<a ng-click="changeTab(item.id)" data-toggle="tab">{{item.label}}</a>
					</li>
					
					<li class="dropdown" ng-class="{'active': currentmoretab != null}"><a class="dropdown-toggle hand-hover" ><b class="caret"></b></a>
							<ul class="dropdown-menu">
								<li ng-repeat="item in moreTabItems">
								<a ng-class="getMoreTabClass(item.id)" ng-click="changeMoreTab(item.id)" class="hand-hover">{{item.label}}<span ng-if="item.extra.length>0" class="badge">{{item.extra}}</span></a></li>
							</ul>
					</li>
					
				</ul>--%>

				<div id="left-pane-header" class="row">
					<div >
						<div class="col-md-2">
						<button id="hide-patient-list-button" type="button" class="pull-left" ng-click="hidePatientList()" title="<bean:message key="patientList.hide" bundle="ui"/>">
							<span class="glyphicon glyphicon-chevron-left"></span> 
						</button>
					</div>
					<div  class="col-md-8 col-md-offset-1">
						<h1 id="left-pane-header-title">Your Appointments</h1>
					</div>
					</div>

					
					
					
				</div>
				<div class="row" ng-cloak>
				
					
					
					
					<button type="button" class="btn btn-default" ng-click="refresh()" title="<bean:message key="patientList.refresh" bundle="ui"/>"> 
						<span class="glyphicon glyphicon-refresh"></span> 
					</button>
					
					<%--Remove these? --%>
					<%--<button type="button" class="btn btn-default" ng-disabled="currentPage == 0" ng-click="changePage(currentPage-1)" title="<bean:message key="patientList.pageUp" bundle="ui"/>">
						<span class="glyphicon glyphicon-circle-arrow-up"></span> 
					</button>
					
					<button type="button" class="btn btn-default" ng-disabled="currentPage == nPages-1"  ng-click="changePage(currentPage+1)" title="<bean:message key="patientList.pageDown" bundle="ui"/>">
						<span class="glyphicon glyphicon-circle-arrow-down"></span> 
					</button>--%>

					<div ng-include="sidebar.location"></div>
					
					<form class="form-search" role="search">
						<span ng-show="showFilter === true" class="form-group twitter-typeahead">
							<input type="text"  class="form-control" placeholder="<bean:message key="patientList.filter" bundle="ui"/>" ng-model="query"/>
						</span>
					</form>
				
					<span class="pull-right" title="<bean:message key="patientList.pagination" bundle="ui"/>">{{currentPage+1}}/{{numberOfPages()}}</span>
			</div>
		</div>
		<!-- End patient List template -->
			
		<div id="right_pane" class="col-md-9" ui-view ng-cloak></div>
	</div>
	
	
	<!-- Bootstrap core JavaScript
    ================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	<script src="../js/jquery-1.9.1.js"></script>
	
	<script src="../library/bootstrap/3.0.0/js/bootstrap.min.js"></script>
	<!-- script src="//netdna.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js"></script -->
	
	<script src="../library/hogan-2.0.0.js"></script>
	<script src="../library/typeahead.js/typeahead.min.js"></script>
	<script src="../library/angular.min.js"></script>
	<!-- script src="../library/angular-route.min.js"></script  -->
	<script src="../library/angular-ui-router.js"></script>
 	<script src="../library/angular-resource.min.js"></script>
 	
 	<script src="../library/ui-bootstrap-tpls-0.11.0.js"></script>
 	<script src="../library/pym.js"></script>
 	
 	<script src="../library/ng-infinite-scroll.min.js"></script>
 	
 	<script src="../library/ng-table/ng-table.js"></script>
 	<script src="../js/loading-bar.js"></script>
 	

	<!-- we'll combine/minify later -->
	<script src="common/demographicServices.js"></script>
	<script src="common/programServices.js"></script>
	<script src="common/scheduleServices.js"></script>
	<script src="common/securityServices.js"></script>
	<script src="common/staticDataServices.js"></script>
	<script src="common/billingServices.js"></script>
	<script src="common/ticklerServices.js"></script>
	<script src="common/formServices.js"></script>
	<script src="common/noteServices.js"></script>
	<script src="common/providerServices.js"></script>
	<script src="common/patientDetailStatusServices.js"></script>
	<script src="common/uxServices.js"></script>
	<script src="common/messageServices.js"></script>
	<script src="common/inboxServices.js"></script>
	<script src="common/k2aServices.js"></script>
	<script src="common/personaServices.js"></script>
	<script src="common/consultServices.js"></script>
	<script src="common/appServices.js"></script>
	<script src="common/diseaseRegistryServices.js"></script>
	<script src="filters.js"></script>
	<script src="app.js"></script>
	
	<script src="oscarController.js"></script>
	<script src="dashboard/dashboardController.js"></script>
	<script src="common/navBarController.js"></script>
	<script src="patientlist/patientListController.js"></script>
	<script src="record/recordController.js"></script>
	<script src="record/summary/summaryController.js"></script>
	<script src="record/forms/formsController.js"></script>
	<script src="record/details/detailsController.js"></script>
	<script src="record/phr/phrController.js"></script>
	<script src="record/tracker/trackerController.js"></script>
	
	<script src="tickler/ticklerController.js"></script>
	<script src="tickler/ticklerViewController.js"></script>
	<script src="tickler/ticklerAddController.js"></script>
	
	<script src="schedule/scheduleController.js"></script>
	<script src="admin/adminController.js"></script>
	<script src="billing/billingController.js"></script>
	<script src="consults/consultRequestListController.js"></script>
	<script src="consults/consultRequestController.js"></script>	
	<script src="consults/consultResponseListController.js"></script>
	<script src="consults/consultResponseController.js"></script>	
	<script src="inbox/inboxController.js"></script>
	<script src="patientsearch/patientSearchController.js"></script>
	
	<script src="report/reportsController.js"></script>
	<script src="document/documentsController.js"></script>
	<script src="settings/settingsController.js"></script>
	<script src="help/supportController.js"></script>
	<script src="help/helpController.js"></script>
	
	<script src="schedule/appointmentAddController.js"></script>
	<script src="schedule/appointmentViewController.js"></script>
	
	<!-- 
	
	<script src="js/providerViewController.js"></script>
	<script src="js/messengerController.js"></script  -->	
	
	<script type="text/javascript" src="../share/javascript/Oscar.js"></script>

	<script type="text/javascript" src="../js/bootstrap-timepicker.min.js"></script>

<script>

$(document).ready(function(){

	$('#demographicQuickSearch').typeahead({
		name: 'patients',
		valueKey:'name',
		limit: 11,
		
		remote: {
	        url: '../ws/rs/demographics/quickSearch?query=%QUERY',
	        cache:false,
	        //I needed to override this to handle the differences in the JSON when it's a single result as opposed to multiple.
	        filter: function (parsedResponse) {
	            retval = [];
	            if(parsedResponse.content instanceof Array) {
	            	for (var i = 0;  i < parsedResponse.content.length;  i++) {
	            		var tmp = parsedResponse.content[i];
	            		if(tmp.hin != null && tmp.hin == '') {
	            			tmp.hin = null;
	            		}
	            		if(tmp.formattedDOB != null && tmp.formattedDOB == '') {
	            			tmp.formattedDOB = null;
	            		}
	            		
	            		tmp.name = tmp.lastName + ", " + tmp.firstName;
	            		tmp.blah = "";
	            		retval.push(tmp);
	                 }
	            } else {
	            	retval.push(parsedResponse.content);
	            }
	            
	            console.log("total:"+parsedResponse.total);
	            var scope = angular.element($("#demographicQuickSearch")).scope();
	            scope.setQuickSearchTerm("");
	            
	            if(parsedResponse.total > 10) {
	            	retval.push({name:"<bean:message key="navbar.moreResults" bundle="ui"/>",hin:parsedResponse.total+" total","demographicNo":-1,"more":true});
	            	scope.setQuickSearchTerm(parsedResponse.query);
	            }
	            
	            return retval;
	        }
	    },
	    
		template: [
		        "<p class='demo-quick-name'>{{name}}</p>",
		        '{{#hin}}<p class="demo-quick-hin">&nbsp;<em>{{hin}}</em></p>{{/hin}}',
		       	'{{#dob}}<p class="demo-quick-dob">&nbsp;{{formattedDOB}}</p>{{/dob}}'
		 ].join(''),
		       	engine: Hogan
		}).on('typeahead:selected', function (obj, datum) {
			$('input#demographicQuickSearch').on('blur',function(event){$("#demographicQuickSearch").val("");});

			var scope = angular.element($("#demographicQuickSearch")).scope();
						
			if(datum.more != null && datum.more == true) {
				scope.switchToAdvancedView();
			} else {
				scope.loadRecord(datum.demographicNo);
			}
			
	});
});

</script>
</body>
</html>
