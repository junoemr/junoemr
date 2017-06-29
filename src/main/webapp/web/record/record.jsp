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
<div id="patient-record-page"> 
	<div class="row vertical-align patient-record-header">
		<div class="col-xs-4">	
			<h2 class="patient-header-name" ng-cloak>
				{{recordCtrl.demographic.lastName}}, {{recordCtrl.demographic.firstName}}  
				<span ng-show="recordCtrl.demographic.alias">({{recordCtrl.demographic.alias}})</span> 
			</h2>
		</div>
		<div class="col-xs-8">	
			<div class="pull-right"> 
				<h4 class="patient-header-info">
					<span class="patient-header-label">
						<bean:message key="demographic.patient.context.born"/>:
					</span>
					{{recordCtrl.demographic.dobYear}}-{{recordCtrl.demographic.dobMonth}}-{{recordCtrl.demographic.dobDay}} ({{recordCtrl.demographic.age | age}})  
					<span class="patient-header-label">
						<bean:message key="demographic.patient.context.sex"/>: 
					</span>
					{{recordCtrl.demographic.sex}}
					<span class="patient-header-label">
						<bean:message key="Appointment.msgTelephone"/>:
					</span>
					{{recordCtrl.demographic.phone}}
				</h4>
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
				<li ng-repeat="tab in recordCtrl.recordtabs2" 
					ng-class="{'active': recordCtrl.isActive(tab) }">
					<a href="javascript:void(0)" ng-click="recordCtrl.changeTab(tab)">
						{{tab.label}} 
						<strong class="text-danger" ng-show="tab.extra == 'outstanding'" title="<bean:message key="oscarEncounter.Index.ConsultOutstanding"/>">!</strong>
					</a>
				</li>
			</ul>
	
			<%-- Medium view --%>
			<ul class="nav navbar-nav visible-nav-md">
					<li ng-repeat="tab in recordCtrl.recordtabs2 | filter: recordCtrl.mediumNavItemFilter(false)"
							ng-class="{'active': recordCtrl.isActive(tab) }">
						<a href="javascript:void(0)" ng-click="recordCtrl.changeTab(tab)">
							 {{tab.label}}
						</a>

					</li>

					<li class="dropdown hand-hover">
						<a href="javascript:void(0)" class="dropdown-toggle" data-toggle="dropdown">
							More <b class="caret"></b>
						</a>

						<ul class="dropdown-menu" role="menu">
							<li ng-repeat="tab in recordCtrl.recordtabs2 | filter: recordCtrl.mediumNavItemFilter(true)" 
								ng-class="{'active': recordCtrl.isActive(tab) }">
								<a href="javascript:void(0)" ng-if="!tab.dropdown" ng-click="recordCtrl.changeTab(tab)" >{{tab.label}} 
									<strong class="text-danger" ng-show="tab.extra == 'outstanding'" title="<bean:message key="oscarEncounter.Index.ConsultOutstanding"/>">!</strong>
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
							<li ng-repeat="tab in recordCtrl.recordtabs2" 
								ng-class="{'active': recordCtrl.isActive(tab) }">
								<a href="javascript:void(0)" ng-if="!tab.dropdown" ng-click="recordCtrl.changeTab(tab)" >{{tab.label}} 
									<strong class="text-danger" ng-show="tab.extra == 'outstanding'" title="<bean:message key="oscarEncounter.Index.ConsultOutstanding"/>">!</strong>
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

	<div class="row" id="note-editor-container">
		<div id="note-editor-minimized"
			class="col-sm-3 col-xs-12 text-center hand-hover" 
			ng-click="recordCtrl.toggleNote();" 
			ng-show="!recordCtrl.hideNote">
			Open note editor <span class="fa fa-chevron-up"></span>
		</div>
	
		<div id="note-editor"
			class="col-sm-5 col-xs-12"
			resizable
			draggable
			ng-show="recordCtrl.hideNote" 
			ng-click="recordCtrl.checkAction($event)" 
			ng-keypress="recordCtrl.checkAction($event)">
			<div class="row hand-hover" id="note-editor-header">
				<%--<div class="col-sm-12 text-center hand-hover" ng-click="recordCtrl.toggleNote();"  >
					Minimize
					<span class="fa fa-chevron-down"></span>
				</div>--%>
				<div class="pull-right text-right" >
					<button class="icon-button" id="note-editor-minimize-button" ng-click="recordCtrl.toggleNote();">
						<span class="btn btn-xs fa fa-window-minimize" role="button"></span>
					</button>
				</div>
			</div>
			<div class="row vertical-align">
				<div class="col-xs-4" id="note-editor-enc-date">
					<p>Encounter:
						<span>
							<juno-datepicker-popup juno-model="recordCtrl.page.encounterNote.observationDate" type="Link">
							</juno-datepicker-popup>
						<span>
					</p>
				</div>
				<div class="col-xs-4 dropup">
					<input type="text" ng-model="recordCtrl.options.magicVal" placeholder="Template" 
					uib-typeahead="t.encounterTemplateName as t.encounterTemplateName for t in recordCtrl.searchTemplates($viewValue)" 
					typeahead-on-select="recordCtrl.insertTemplate($item, $model, $label)"
					class="form-control">	
				</div>
				<div class="col-xs-4 dropup">
					<input type="text" class="form-control" placeholder="Assign Issue"  
						uib-typeahead="i.issueId as i.code for i in recordCtrl.searchIssues($viewValue)" 
						typeahead-on-select="recordCtrl.assignIssue($item, $model, $label);recordCtrl.selectedIssue='';" 
						ng-model="recordCtrl.selectedIssue" 
						typeahead-loading="loadingIssues"
						typeahead-min-length="3" />
				</div>
			</div>
			<div class="row">
				<div class="col-sm-12">
					<textarea class="form-control input-md col-lg-4 note-editor-textarea"
						rows="6"
						ng-model="recordCtrl.page.encounterNote.note"
						ng-disabled="recordCtrl.page.cannotChange" 
						id="noteEditor{{recordCtrl.demographicNo}}" 
						ng-change="recordCtrl.setEditingNoteFlag()">
					</textarea>
				</div>

			</div>    		
			<div class="row note-editor-issues">
				<div class="col-sm-12">
					<label >Assigned Issues:</label>
					<table class="table table-condensed">
						<tr ng-repeat="i in recordCtrl.page.assignedCMIssues" class="note-editor-issue-row">
							<td>{{i.issue.description}} ({{i.issue.code}})</td>
							<td class="text-right">
								<button class="btn btn-xs btn-danger" type="button" ng-click="recordCtrl.removeIssue(i)" ng-if="i.unchecked == null || i.unchecked == false">Remove</button>
							</td>
						</tr>
						
					</table>
				</div>
			</div>
			<div class="row" id="note-editor-footer">
				<div class="col-sm-12">
					
					<input type="hidden" id="startTag" value="<bean:message key="oscarEncounter.Index.startTime"/>">
					<input type="hidden" id="endTag" value="<bean:message key="oscarEncounter.Index.endTime"/>">
					<div class="pull-left">
						<button class="btn btn-danger" ng-click="recordCtrl.cancelNoteEdit()"> 
							Cancel
						</button>
					</div>
					<div class="btn-group btn-group-md pull-right">
						<button type="button" class="btn btn-default" ng-click="recordCtrl.pasteTimer()" id="aTimer" title="<bean:message key="oscarEncounter.Index.pasteTimer"/>">
							00:00
						</button>
						<button type="button" class="btn btn-default" ng-click="recordCtrl.toggleTimer()" title="<bean:message key="oscarEncounter.Index.toggleTimer"/>">
							<span class="fa fa-pause"  id="aToggle"></span>
						</button>
						<button type="button" class="btn btn-success" 
							ng-click="recordCtrl.saveNote()" 
							id="saveButton" 
							data-ng-disabled="recordCtrl.page.encounterNote.isSigned || recordCtrl.page.encounterNote.isSaved" 
							title="<bean:message key="oscarEncounter.Index.btnSave"/>">
							<span class="fa fa-save"  id="theSave"></span>
						</button>
						<button type="button" class="btn btn-success" ng-click="recordCtrl.saveSignNote()" title="<bean:message key="oscarEncounter.Index.btnSignSave"/>">
							<span class="fa fa-pencil-square-o"  id="Sign"></span>
						</button>
						<button type="button" class="btn btn-success" ng-click="recordCtrl.saveSignVerifyNote()" title="<bean:message key="oscarEncounter.Index.btnSign"/>">
							<span class="fa fa-thumbs-o-up"  id="SaveSignVerify"></span>
						</button>
						<button type="button" class="btn btn-success" ng-click="recordCtrl.saveSignBillNote()" title="<bean:message key="oscarEncounter.Index.btnSignSaveBill"/>">
							<span class="fa fa-dollar"  id="bill"></span>
						</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
    
