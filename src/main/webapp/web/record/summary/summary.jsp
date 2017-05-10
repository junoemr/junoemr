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
<div class="container">
	<div class="row">
		<div class="col-lg-12" ng-hide="page.canRead">
			<bean:message key="oscarEncounter.accessDenied"/>
		</div>
	</div>

	<div class="row margin-bottom">
		<div class="col-md-12" ng-show="page.canRead" ng-click="checkAction($event)" ng-keypress="checkAction($event)">
			<ul class="nav nav-pills">
				<li ng-class="isCurrentStatus('none')"><a data-target="#all" ng-click="removeFilter(0)" data-toggle="tab" class="hand-hover">All</a></li>
				<li ng-class="isCurrentStatus('Just My Notes')"><a ng-click="changeNoteFilter('Just My Notes')" class="hand-hover">Just My Notes</a></li>
				<li ng-class="isOnlyNotesStatus()"><a ng-click="setOnlyNotes()" class="hand-hover">Just Notes</a></li>
				<li><a href="#" data-target="#tracker" role="tab" data-toggle="tab" ng-click="getTrackerUrl(demographicNo)" >Tracker</a></li>
				<li class="pull-right"><span style="margin-top:12px;" class="glyphicon glyphicon-print" ng-click="showPrintModal(page.notes.notelist)"></span></li>
			</ul>
		</div><!-- middleSpace -->
	</div>
	<div class="row">
		<div class="col-md-3 margin-bottom" ng-show="page.canRead">
			<fieldset class="summary-section" ng-repeat="mod in page.columnOne.modules">
				<div class="row vertical-align">
					<div class="col-md-8 col-sm-8">
						<h5 class="summary-section-heading">
							{{mod.displayName}} 
						</h5>
					</div>
					<div class="col-md-4 col-sm-4">
						<a href="javascript:void(0)" class="btn btn-xs btn-success pull-right" ng-click="openPreventions(demographicNo)" ng-if="mod.summaryCode=='preventions'">
							Add <%--<span class="glyphicon glyphicon-plus-sign" title="{{mod.summaryCode}}"></span>--%>
						</a>

						<%--<a href="javascript:void(0)" class="btn btn-xs btn-success pull-right" ng-click="gotoState('add', mod)" ng-disabled="page.cannotAdd" 
							ng-hide="mod.summaryCode=='meds' || mod.summaryCode=='assessments' || mod.summaryCode=='allergies' || mod.summaryCode=='preventions' || page.cannotAdd">
								Add <span class="glyphicon glyphicon-plus-sign" title="{{mod.summaryCode}}"></span>
						</a>--%>

						<a href="javascript:void(0)" class="btn btn-xs btn-success pull-right" ng-click="gotoState('add', mod)" ng-disabled="page.cannotAdd" 
							ng-if="mod.summaryCode=='othermeds' || mod.summaryCode=='ongoingconcerns' || 
							mod.summaryCode=='medhx' || mod.summaryCode=='sochx' || mod.summaryCode=='famhx' || 
							mod.summaryCode=='reminders' || mod.summaryCode=='riskfactors' || mod.summaryCode=='riskfactors' ">
							Add <%--<span class="glyphicon glyphicon-plus-sign" title="{{mod.summaryCode}}"></span>--%>
						</a>

						<a href="javascript:void(0)" class="btn btn-xs btn-success pull-right" ng-click="openRx(demographicNo)" ng-if="mod.summaryCode=='meds'">
							Add <%--<span class="glyphicon glyphicon-plus-sign" title="{{mod.summaryCode}}"></span>--%>
						</a>
						
						<a href="javascript:void(0)"  class=" btn btn-xs btn-success pull-right" ng-click="openAllergies(demographicNo)" ng-if="mod.summaryCode=='allergies'">
							Add <%--<span class="glyphicon glyphicon-plus-sign" title="{{mod.summaryCode}}"></span>--%>
						</a>

						<a href="#/record/{{demographicNo}}/forms" class="btn btn-xs btn-success pull-right" ng-if="mod.summaryCode=='assessments'">
							Add <%--<span class="glyphicon glyphicon-plus-sign" title="{{mod.summaryCode}}"></span>--%>
						</a>
					</div>
				</div>
				

				<ul class="list-unstyled" style="padding-left:5px;">
					<%-- href="{{item.action}}" --%>
					<li ng-repeat="item in mod.summaryItem" ng-show="$index < mod.displaySize"><p><span class="pull-right">{{item.date | date : 'dd MMM yyyy'}}</span></p><a ng-click="gotoState(item,mod,item.id)" href="javascript:void(0)" ng-class="item.indicatorClass">{{item.displayName | limitTo: 34 }} {{item.displayName.length > 34 ? '...' : '' }}<small ng-show="item.classification">({{item.classification}})</small></a> </li> 			
					<%--<a href="#" class="text-muted add-summary" ng-if="mod.summaryItem==null" ng-click="openPreventions(demographicNo)" ng-show="mod.summaryCode=='preventions'"><bean:message key="global.btnAdd"/> {{mod.displayName}}</a>
					<a href="#" class="text-muted add-summary" ng-if="mod.summaryItem==null" ng-click="gotoState('add', mod)" ng-hide="mod.summaryCode=='meds' || mod.summaryCode=='assessments' || mod.summaryCode=='allergies' || mod.summaryCode=='preventions' || page.cannotAdd"><bean:message key="global.btnAdd"/> {{mod.displayName}}</a>
					<a href="#" class="text-muted add-summary" ng-if="mod.summaryItem==null" ng-click="openRx(demographicNo)" ng-show="mod.summaryCode=='meds'"><bean:message key="global.btnAdd"/> {{mod.displayName}}</a>
					<a href="#" class="text-muted add-summary" ng-if="mod.summaryItem==null" ng-click="openAllergies(demographicNo)" ng-show="mod.summaryCode=='allergies'"><bean:message key="global.btnAdd"/> {{mod.displayName}}</a>
					<a href="#/record/{{demographicNo}}/forms" class="text-muted add-summary" ng-if="mod.summaryItem==null" ng-show="mod.summaryCode=='assessments'"><bean:message key="global.btnAdd"/> {{mod.displayName}}</a>--%>
				</ul>

			<span ng-class="showMoreItemsSymbol(mod)" ng-click="toggleList(mod)" ng-show="showMoreItems(mod)"></span>
		</fieldset>   
		</div>

		<div class="col-md-6" id="middleSpace" ng-show="page.canRead" ng-click="checkAction($event)" ng-keypress="checkAction($event)">
			<div class="tab-content">
				<div class="tab-pane active" id="all">
					<%--<dl infinite-scroll="addMoreItems()">
						<dt ng-style="setColor(note)" ng-repeat-start="note in page.notes.notelist" ng-show="showNoteHeader(note)">
								<div class="btn-group btn-group-xs pull-right">
								<button class="btn btn-default btn-xs" type="button">
										edit 
								</button>
								<button class="btn btn-default btn-xs dropdown-toggle" type="button" data-toggle="dropdown">
										<span class="caret"></span>
										<span class="sr-only">Toggle Dropdown</span>
								</button>
								<ul class="dropdown-menu" role="menu">
										<li><a href="#">print</a></li> <li><a href="#">annotate</a></li><li><a href="#">set Encounter Date</a></li><li><a href="#">set Encounter Type</a></li>
								</ul>
								</div>{{note.observationDate | date : 'dd-MMM-yyyy'}} {{firstLine(note)}} <input type="checkbox" ng-model="note.isSelected" class="pull-right" style="margin-right:3px;"/>
						</dt>
						<dd ng-repeat-end  ng-show="showNote(note)"><pre ng-class="isNoteBeingEdited(note)" style="margin-bottom:0px;" ng-show="showNote(note)" ng-hide="note.cpp==true" ng-dblclick="editNote(note)">{{note.note}}</pre>
								<h6 style="margin-top:1px;margin-bottom:0px;"><bean:message key="oscarEncounter.editors.title"/>: <small>{{note.editorNames}}</small> <span class="pull-right"><bean:message key="oscarEncounter.encounterDate.title"/>: <small>{{note.observationDate | date: 'medium'}}</small> <bean:message key="oscarEncounter.noteRev.title"/>: <small ng-click="openRevisionHistory(note)" class="hand-hover">{{note.revision}}</small></span></h6>

								<h6 style="margin-top:0px;"><bean:message key="oscarEncounter.assignedIssues.title"/>: <small>{{note.issueDescriptions}}</small> <span class="pull-right"><bean:message key="oscarEncounter.encType.title"/>: <small>{{note.encounterType}}</small></span></h6>
						</dd>                                   
					</dl>--%>

					<div class="container" infinite-scroll="addMoreItems()">
						
						<div class="container note" ng-repeat="note in page.notes.notelist" >
							<div class="row note-header" ng-show="showNoteHeader(note)">
								<div class="col-md-1 pull-left">
									<input type="checkbox" ng-model="note.isSelected"/> 
								</div>
								<div class="col-md-6 pull-left note-header-name">
									<h5>{{firstLine(note)}}</h5>
								</div>
								<div class="col-md-4 note-header-date pull-right">
									<h6>{{note.observationDate | date : 'dd-MMM-yyyy'}}</h6>
								</div>
							</div>
							<div class="row note-body" ng-class="isNoteBeingEdited(note)" ng-show="showNote(note)"  ng-hide="note.cpp==true" ng-dblclick="editNote(note)">
								<p >
									{{note.note}}
								</p>
								<hr class="note-divider">
							</div>

							<div class="row note-footer" ng-show="showNote(note)">
								<div class="col-md-9">
									<div class="p-inline">
										<p class="text-muted"><bean:message key="oscarEncounter.editors.title"/>:</p>
										<p>{{note.editorNames}}</p>
									</div>

									<div class="p-inline">
										<p class="text-muted"><bean:message key="oscarEncounter.assignedIssues.title"/>:</p> 
										<p>{{note.issueDescriptions}}</p> 
									</div>
								</div>

								<div class="col-md-3 note-footer-right">
									<div class="p-inline">
										<p class="text-muted"><bean:message key="oscarEncounter.noteRev.title"/>:</p>
										<p ng-click="openRevisionHistory(note)" class="hand-hover">{{note.revision}}</p>
									</div>

									<div class="p-inline">
										<p class="text-muted"><bean:message key="oscarEncounter.encType.title"/>:</p> 
										<p>{{note.encounterType}}</p>
									</div>
								</div>
							</div>
						</div>
						
					</div>
				</div>

				<div class="tab-pane" id="tracker">
					<iframe
					id="trackerSlim"
					scrolling="No"
					frameborder="0"
					ng-src="{{ trackerUrl }}"
					width="100%"
					style="min-height:820px"
					></iframe>
				</div>

			</div><!-- tab content -->  
		</div>
		
		<div class="col-sm-3" ng-show="page.canRead" ng-click="checkAction($event)" ng-keypress="checkAction($event)">
			<fieldset ng-repeat="mod in page.columnThree.modules">
				<legend style="margin-bottom:0px;">{{mod.displayName}}
					<div class="form-group">
						<input type="text" class="form-control search-query" ng-model="incomingQ" placeholder="Search">
					</div>
				</legend>
				<ul style="padding-left:12px;">
					<%-- href="{{item.action}}" --%>
					<li ng-repeat="item in mod.summaryItem | filter:incomingQ" ng-show="$index < mod.displaySize"  >
						<span class="pull-right">{{item.date | date : 'dd-MMM-yyyy'}}</span>
						<a ng-click="gotoState(item)" class="hand-hover" ng-class="{true: 'abnormal', false: ''}[item.abnormalFlag]">{{item.displayName}}
							<small ng-show="item.classification">({{item.classification}})</small>
						</a> 
					</li> 
				</ul>
						
				<span ng-class="showMoreItemsSymbol(mod)" ng-click="toggleList(mod)" ng-show="showMoreItems(mod)"></span>
			</fieldset>
			
		<%-- 
			<fieldset>
				<legend style="margin-bottom:0px;">Incoming 
					<div class="form-group">
						<input type="text" class="form-control search-query" ng-model="incomingQ" placeholder="Search">
					</div>
				</legend>     		
				
				<h5 ng-repeat="item in documentlabs | filter:incomingQ" ng-show="$index < documentlabsSize" ><a ng-click="changeTab(12)" >{{item.desc}}<small>({{item.type}})</small></a><span class="pull-right">{{item.date}}</span></h5>
					<a ng-click="expandlist()" ng-show="showMoreDocuments()" ><span class="glyphicon glyphicon-chevron-down pull-right"></span></a>
					<a ng-click="expandlist()" ng-hide="showMoreDocuments()" ><span class="glyphicon glyphicon-chevron-up pull-right"></span></a>
			</fieldset>
		
		--%>	
		</div>
	</div>
</div>
