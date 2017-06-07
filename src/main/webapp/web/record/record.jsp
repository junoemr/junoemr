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
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<div> 
	<div class="row vertical-align">
		<div class="col-xs-4">	
			<h2 class="patient-header-name" ng-cloak>
				{{recordCtrl.demographic.lastName}}, {{recordCtrl.demographic.firstName}}  
				<span ng-show="recordCtrl.demographic.alias">({{recordCtrl.demographic.alias}})</span> 
			</h2>
		</div>
		<div class="col-xs-8">	
			<div class="pull-right"> 
				<p>
					<span class="patient-header-text">
						<bean:message key="demographic.patient.context.born"/>:
					</span>
					{{recordCtrl.demographic.dobYear}}-{{recordCtrl.demographic.dobMonth}}-{{recordCtrl.demographic.dobDay}} ({{recordCtrl.demographic.age | age}})  
					<span class="patient-header-text">
						<bean:message key="demographic.patient.context.sex"/>: 
					</span>
					{{recordCtrl.demographic.sex}}
					<span class="patient-header-text">
						<bean:message key="Appointment.msgTelephone"/>:
					</span>
					{{recordCtrl.demographic.phone}}
				</p>
				<!-- <span class="glyphicon glyphicon-new-window"></span>-->
			</div>
		</div>
	</div>
	
	<nav class="navbar navbar-default record-navbar" role="navigation" id="record-nav">
		<!-- Brand and toggle get grouped for better mobile display -->
		<div class="navbar-header">
			<button type="button" class="navbar-toggle" data-toggle="collapse"
				data-target="#record-nav-collapse">
				<span class="sr-only">Toggle navigation</span> <span
					class="icon-bar"></span> <span class="icon-bar"></span> <span
					class="icon-bar"></span>
			</button>
		</div>

		<!-- Collect the nav links, forms, and other content for toggling   removed data-toggle="tab"  from a ngclick changeTab3 -->

		<div class="collapse navbar-collapse" id="record-nav-collapse">

				<%-- Large view --%>
			<ul class="nav navbar-nav visible-nav-lg" id="myTabs">
				<li ng-repeat="tab in recordCtrl.recordtabs2" ng-class="recordCtrl.isTabActive(tab)">
					<a href="javascript:void(0)" ng-click="recordCtrl.changeTab(tab)">
						{{tab.label}} 
						<strong class="text-danger" ng-show="tab.extra=='outstanding'" title="<bean:message key="oscarEncounter.Index.ConsultOutstanding"/>">!</strong>
					</a>
				</li>
			</ul>
	
			<%-- Medium view --%>
			<ul class="nav navbar-nav visible-nav-md">
					<li ng-repeat="tab in recordCtrl.recordtabs2 | filter: recordCtrl.mediumNavItemFilter(false)"
							ng-class="recordCtrl.isTabActive(tab)">
						<a href="javascript:void(0)" ng-click="recordCtrl.changeTab(tab)">
							 {{tab.label}}
						</a>

					</li>

					<li class="dropdown hand-hover">
						<a href="javascript:void(0)" class="dropdown-toggle" data-toggle="dropdown">
							More <b class="caret"></b>
						</a>

						<ul class="dropdown-menu" role="menu">
							<li ng-repeat="tab in recordCtrl.recordtabs2 | filter: recordCtrl.mediumNavItemFilter(true)" ng-class="recordCtrl.isTabActive(tab)">
								<a href="javascript:void(0)" ng-if="!tab.dropdown" ng-click="recordCtrl.changeTab(tab)" >{{tab.label}} 
									<strong class="text-danger" ng-show="tab.extra=='outstanding'" title="<bean:message key="oscarEncounter.Index.ConsultOutstanding"/>">!</strong>
								</a>
								
							</li>
						</ul>
					</li>
				</ul>

				<%-- Small view --%>
				<ul class="nav navbar-nav visible-nav-sm">
					<li class="dropdown hand-hover">
						<a href="javascript:void(0)" class="dropdown-toggle" data-toggle="dropdown">
							Menu <b class="caret"></b>
						</a>

						<ul class="dropdown-menu" role="menu">
							<li ng-repeat="tab in recordCtrl.recordtabs2" ng-class="recordCtrl.isTabActive(tab)">
								<a href="javascript:void(0)" ng-if="!tab.dropdown" ng-click="recordCtrl.changeTab(tab)" >{{tab.label}} 
									<strong class="text-danger" ng-show="tab.extra=='outstanding'" title="<bean:message key="oscarEncounter.Index.ConsultOutstanding"/>">!</strong>
								</a>
							</li>
						</ul>
					</li>
				</ul>
		</div>
		<!-- /.navbar-collapse -->
	</nav>
			<!-- -->
	<div class="row">
        <div class="include-record-peice" ui-view></div>
    </div>
    
    <div class="row noprint">
    	<div id="noteInput2" class="center-block well col-md-4 col-md-offset-3 text-center hand-hover" style="padding:0px;" ng-click="toggleNote();" ng-show="!hideNote">
    		<span class="glyphicon glyphicon-chevron-up"></span><span class="glyphicon glyphicon-chevron-up"></span><span class="glyphicon glyphicon-chevron-up"></span>
    	</div>
    	<div id="noteInput" class="center-block well col-md-4 col-md-offset-3" ng-show="hideNote" ng-click="recordCtrl.checkAction($event)" ng-keypress="recordCtrl.checkAction($event)">
			<div style="position:absolute;top:0px;Right:0px;font-size:10px">
			<span class="glyphicon glyphicon-arrow-left hand-hover" ng-click="recordCtrl.moveNote('l');" title="move left"></span>  			
			<span class="glyphicon glyphicon-stop hand-hover" ng-click="recordCtrl.moveNote('c');" title="center"></span>   
			<span class="glyphicon glyphicon-arrow-right hand-hover" ng-click="recordCtrl.moveNote('r');" title="move right"></span>
			</div>
			<div class="col-xs-4">
			
			    
			    <input type="text" ng-model="recordCtrl.options.magicVal" placeholder="Template" 
				uib-typeahead="t.encounterTemplateName as t.encounterTemplateName for t in recordCtrl.searchTemplates($viewValue)" 
				typeahead-on-select="recordCtrl.insertTemplate($item, $model, $label)"
				class="form-control">
				
				
		    </div>
			<div class="col-xs-3 text-center hand-hover" ng-click="recordCtrl.toggleNote();"  >
				<span class="glyphicon glyphicon-chevron-down"></span>
				<span class="glyphicon glyphicon-chevron-down"></span>
				<span class="glyphicon glyphicon-chevron-down"></span>
			
			</div>
			<div class="col-xs-4 " >
			    <input type="text" class="form-control" placeholder="Search" data-ng-disabled="true">
			</div>
    		
    		
    		<textarea class="form-control input-lg col-lg-4" rows="6" ng-model="recordCtrl.page.encounterNote.note" ng-disabled="recordCtrl.page.cannotChange" id="noteEditor{{demographicNo}}" ng-change="recordCtrl.setEditingNoteFlag()"></textarea>
    		
    		<div style="font-size:8pt" ng-if="recordCtrl.page.assignedCMIssues != null  && recordCtrl.page.assignedCMIssues.length > 0">
			    <label>Assigned Issues:</label>
			    <table class="table">
					<tr ng-repeat="i in recordCtrl.page.assignedCMIssues">
						<td>
							<input type="button" value="remove" ng-click="recordCtrl.removeIssue(i)" ng-if="i.unchecked==null || i.unchecked==false"/>
						</td>
						<td>{{i.issue.description}} ({{i.issue.code}})</td>
					</tr>
					
				</table>
			</div>
			
			<div class="pull-left">
				<input type="text" class="form-control" placeholder="Assign Issue"  
					uib-typeahead="i.issueId as i.code for i in recordCtrl.searchIssues($viewValue)" 
					typeahead-on-select="recordCtrl.assignIssue($item, $model, $label);selectedIssue='';" 
					ng-model="selectedIssue" 
					typeahead-loading="loadingIssues"
					typeahead-min-length="3" typeahead-append-to-body="true"/>
			</div>
			
			
			<input type="hidden" id="startTag" value="<bean:message key="oscarEncounter.Index.startTime"/>">
			<input type="hidden" id="endTag" value="<bean:message key="oscarEncounter.Index.endTime"/>">
			
    		<div class="btn-group btn-group-sm pull-right">
				<button type="button" class="btn btn-default" ng-click="recordCtrl.pasteTimer()" id="aTimer" title="<bean:message key="oscarEncounter.Index.pasteTimer"/>">
					00:00
				</button>
				<button type="button" class="btn btn-default" ng-click="recordCtrl.toggleTimer()" title="<bean:message key="oscarEncounter.Index.toggleTimer"/>">
					<span class="glyphicon glyphicon-pause"  id="aToggle"></span>
				</button>
				<button type="button" class="btn btn-default" ng-click="recordCtrl.saveNote()" id="saveButton"  data-ng-disabled="recordCtrl.page.encounterNote.isSigned" title="<bean:message key="oscarEncounter.Index.btnSave"/>">
					<span class="glyphicon glyphicon-save"  id="theSave"></span>
				</button>
				<button type="button" class="btn btn-default" ng-click="recordCtrl.saveSignNote()" title="<bean:message key="oscarEncounter.Index.btnSignSave"/>">
					<span class="glyphicon glyphicon-pencil"  id="Sign"></span>
				</button>
				<button type="button" class="btn btn-default" ng-click="recordCtrl.saveSignVerifyNote()" title="<bean:message key="oscarEncounter.Index.btnSign"/>">
					<span class="glyphicon glyphicon-thumbs-up"  id="SaveSignVerify"></span>
				</button>
				<button type="button" class="btn btn-default" ng-click="recordCtrl.saveSignBillNote()" title="<bean:message key="oscarEncounter.Index.btnSignSaveBill"/>">
					<span class="glyphicon glyphicon-usd"  id="bill"></span>
				</button>
			</div>
    		
    	</div>
    </div>
</div>
    
