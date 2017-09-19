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

<div class="col-xs-12 summary-section">
	<div class="row">
		<div class="col-lg-12" ng-hide="summaryCtrl.page.canRead">
			<bean:message key="oscarEncounter.accessDenied"/>
		</div>
	</div>

	<div class="row">
		<div class="col-md-3 col-sm-4 col-xs-12" id="summary-section-left" ng-show="summaryCtrl.page.canRead">
			<fieldset class="module-list" ng-repeat="mod in summaryCtrl.page.columnOne.modules">
				<div class="row vertical-align">
					<div class="col-xs-8">
						<h5 class="summary-section-heading">
							{{mod.displayName}}
						</h5>
					</div>
					<div class="col-xs-4">
						<button class="btn btn-xs btn-success pull-right"
								ng-click="summaryCtrl.openPreventions(summaryCtrl.demographicNo)"
								ng-if="mod.summaryCode=='preventions'">
							Add <%--<span class="glyphicon glyphicon-plus-sign" title="{{mod.summaryCode}}"></span>--%>
						</button>

						<%--<a href="javascript:void(0)" class="btn btn-xs btn-success pull-right" ng-click="gotoState('add', mod)" ng-disabled="page.cannotAdd" 
							ng-hide="mod.summaryCode=='meds' || mod.summaryCode=='assessments' || mod.summaryCode=='allergies' || mod.summaryCode=='preventions' || page.cannotAdd">
								Add <span class="glyphicon glyphicon-plus-sign" title="{{mod.summaryCode}}"></span>
						</a>--%>

						<button class="btn btn-xs btn-success pull-right"
								ng-click="summaryCtrl.gotoState('add', mod)"
								ng-disabled="summaryCtrl.page.cannotAdd"
								ng-if="mod.summaryCode=='othermeds' || mod.summaryCode=='ongoingconcerns' ||
								mod.summaryCode=='medhx' || mod.summaryCode=='sochx' || mod.summaryCode=='famhx' || 
								mod.summaryCode=='reminders' || mod.summaryCode=='riskfactors' || mod.summaryCode=='riskfactors' ">
							Add <%--<span class="glyphicon glyphicon-plus-sign" title="{{mod.summaryCode}}"></span>--%>
						</button>

						<button class="btn btn-xs btn-success pull-right"
								ng-click="summaryCtrl.openRx(summaryCtrl.demographicNo)"
								ng-if="mod.summaryCode=='meds'">
							Add <%--<span class="glyphicon glyphicon-plus-sign" title="{{mod.summaryCode}}"></span>--%>
						</button>

						<button class=" btn btn-xs btn-success pull-right"
								ng-click="summaryCtrl.openAllergies(summaryCtrl.demographicNo)"
								ng-if="mod.summaryCode=='allergies'">
							Add <%--<span class="glyphicon glyphicon-plus-sign" title="{{mod.summaryCode}}"></span>--%>
						</button>

						<button class="btn btn-xs btn-success pull-right"
								ng-click="summaryCtrl.gotoState('add', mod)"
								ng-if="mod.summaryCode=='assessments'">
							Add <%--<span class="glyphicon glyphicon-plus-sign" title="{{mod.summaryCode}}"></span>--%>
						</button>

						<%--<a href="#/record/{{summaryCtrl.demographicNo}}/forms" class="btn btn-xs btn-success pull-right" 
							ng-if="mod.summaryCode=='assessments'">
							Add <span class="glyphicon glyphicon-plus-sign" title="{{mod.summaryCode}}"></span>
						</a>--%>
					</div>
				</div>


				<ul>
					<li ng-repeat="item in mod.summaryItem | limitTo: mod.displaySize">
						<span class="pull-right">{{item.date | date : 'dd MMM yyyy'}}</span>
						<a ng-click="summaryCtrl.gotoState(item,mod,item.id)" href="javascript:void(0)" ng-class="item.indicatorClass">
							{{item.displayName | limitTo: 34 }} {{item.displayName.length > 34 ? '...' : '' }}
							<small ng-show="item.classification">({{item.classification}})</small>
						</a>
					</li>
					<%--<a href="#" class="text-muted add-summary" ng-if="mod.summaryItem==null" ng-click="openPreventions(demographicNo)" ng-show="mod.summaryCode=='preventions'"><bean:message key="global.btnAdd"/> {{mod.displayName}}</a>
					<a href="#" class="text-muted add-summary" ng-if="mod.summaryItem==null" ng-click="gotoState('add', mod)" ng-hide="mod.summaryCode=='meds' || mod.summaryCode=='assessments' || mod.summaryCode=='allergies' || mod.summaryCode=='preventions' || page.cannotAdd"><bean:message key="global.btnAdd"/> {{mod.displayName}}</a>
					<a href="#" class="text-muted add-summary" ng-if="mod.summaryItem==null" ng-click="openRx(demographicNo)" ng-show="mod.summaryCode=='meds'"><bean:message key="global.btnAdd"/> {{mod.displayName}}</a>
					<a href="#" class="text-muted add-summary" ng-if="mod.summaryItem==null" ng-click="openAllergies(demographicNo)" ng-show="mod.summaryCode=='allergies'"><bean:message key="global.btnAdd"/> {{mod.displayName}}</a>
					<a href="#/record/{{demographicNo}}/forms" class="text-muted add-summary" ng-if="mod.summaryItem==null" ng-show="mod.summaryCode=='assessments'"><bean:message key="global.btnAdd"/> {{mod.displayName}}</a>--%>
				</ul>
				<span class="glyphicon hand-hover pull-right glyphicon-chevron-up"
					  ng-click="summaryCtrl.toggleList(mod)"
					  ng-show="summaryCtrl.isSectionExpanded(mod) && !summaryCtrl.isSectionEmpty(mod)" >
				</span>
				<span class="glyphicon hand-hover pull-right glyphicon-chevron-down"
					  ng-click="summaryCtrl.toggleList(mod)"
					  ng-show="!summaryCtrl.isSectionExpanded(mod) && !summaryCtrl.isSectionEmpty(mod)">
				</span>

			</fieldset>
		</div>

		<div class="col-md-6 col-sm-7 col-xs-10 col-sm-offset-0 col-xs-offset-1"
			 ng-show="summaryCtrl.page.canRead"
			 ng-click="summaryCtrl.checkAction($event)"
			 ng-keypress="summaryCtrl.checkAction($event)">
			<div class="col-sm-12 summary-tabs" ng-show="summaryCtrl.page.canRead" ng-click="summaryCtrl.checkAction($event)" ng-keypress="summaryCtrl.checkAction($event)">
				<ul class="nav nav-tabs">
					<li class="active">
						<a data-target="#all" data-toggle="tab" class="hand-hover">Notes</a>
					</li>
					<li>
						<a ng-click="summaryCtrl.getTrackerUrl(summaryCtrl.demographicNo)" data-target="#tracker" role="tab" data-toggle="tab" >Tracker</a>
					</li>
					<li class="pull-right">
						<button class="btn btn-sm btn-primary" ng-click="summaryCtrl.showPrintModal(summaryCtrl.page.notes.notelist)">
							<span class="fa fa-print"></span>
							Print
						</button>
					</li>
				</ul>
			</div>
			<div class="col-md-12 note-list">
				<div class="tab-content">
					<div class="tab-pane active" id="all">
						<div id="note-list-filters">
							<label class="checkbox-inline">
								<input type="checkbox"
									   ng-model="summaryCtrl.page.onlyMine"
									   value="">
								Just Mine
							</label>
							<label class="checkbox-inline">
								<input type="checkbox"
									   ng-model="summaryCtrl.page.onlyNotes"
									   value="">
								Just Encounter Notes
							</label>
						</div>
						<div infinite-scroll="summaryCtrl.addMoreItems()">

							<div class="note-container col-xs-12 note"
								 ng-repeat="note in summaryCtrl.page.notes.notelist"
								 ng-class="{'note-in-edit': summaryCtrl.isNoteBeingEdited(note)}"
								 ng-style="summaryCtrl.setColor(note)"
								 ng-show="summaryCtrl.showNote(note)">
								<div class="row note-header vertical-align" ng-show="summaryCtrl.showNoteHeader(note)" >
									<div class="col-md-9 col-xs-8 pull-left note-header-info vertical-align">
										<h6>{{note.observationDate | date : 'dd-MMM-yyyy'}}
											<span class="note-header-title" style="word-break: break-all">{{summaryCtrl.getNoteHeader(note)}}</span>
										</h6>
									</div>
									<div class="col-md-3 col-xs-4 pull-right text-right note-header-buttons">
										<button class="btn btn-primary btn-xs" ng-click="summaryCtrl.editNote(note)" ng-show="note.editable && summaryCtrl.isRegularNote(note)">
											Edit
										</button>
										<button class="btn btn-primary btn-xs" ng-click="summaryCtrl.editGroupNote(note)" ng-show="note.editable && note.cpp && !note.archived && !note.ticklerNote">
											Edit
										</button>
										<button class="btn btn-primary btn-xs" ng-click="summaryCtrl.viewEform(note)" ng-show="note.eformData">
											View
										</button>
										<button class="btn disabled btn-xs" ng-show="note.archived == true">Archived</button>
										<%--<input type="checkbox" ng-model="note.isSelected"/> --%>
										<button class="btn btn-default btn-xs"
												ng-click="summaryCtrl.toggleIsSelectedForPrint(note)"
												ng-class="{'btn-success': note.isSelected, 'btn-default': !note.isSelected }"
												title="Add note to print list">
											<span class="fa fa-print" aria-hidden="true"></span>
										</button>
									</div>
								</div>
								<div class="row note-body"
									 ng-show="summaryCtrl.showNote(note)"
									 ng-hide="note.cpp==true || note.document==true || note.eformData == true">
									<p>{{note.note}}</p>
									<hr class="note-divider">
								</div>

								<div class="row note-footer"
									 ng-show="summaryCtrl.showNote(note)"
									 ng-hide="note.document==true || note.eformData == true">
									<div class="col-md-7">
										<div class="p-inline">
											<p class="text-muted"><bean:message key="oscarEncounter.editors.title"/>:</p>
											<p>{{note.editorNames}}</p>
										</div>

										<div class="p-inline">
											<p class="text-muted"><bean:message key="oscarEncounter.assignedIssues.title"/>:</p>
											<p>{{note.issueDescriptions}}</p>
										</div>
									</div>

									<div class="col-md-5 note-footer-right">
										<div class="p-inline">
											<p class="text-muted"><bean:message key="oscarEncounter.noteRev.title"/>:</p>
											<a ng-click="summaryCtrl.openRevisionHistory(note)" class="hand-hover">{{note.revision}}</a>
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
								ng-src="{{ summaryCtrl.trackerUrl }}"
								width="100%"
								style="min-height:820px"
						></iframe>
					</div>
				</div><!-- tab content -->
			</div>
		</div>

		<div class="col-md-3 col-md-offset-0 col-xs-10 col-xs-offset-1" id="summary-section-right" ng-show="summaryCtrl.page.canRead"
			 ng-click="summaryCtrl.checkAction($event)"
			 ng-keypress="summaryCtrl.checkAction($event)">
			<fieldset ng-repeat="mod in summaryCtrl.page.columnThree.modules">
				<legend style="margin-bottom:0px;">{{mod.displayName}}
					<div class="form-group">
						<input type="text" class="form-control search-query" ng-model="incomingQ" placeholder="Search">
					</div>
				</legend>
				<ul style="padding-left:12px;">
					<%-- href="{{item.action}}" --%>
					<li ng-repeat="item in mod.summaryItem | filter: incomingQ" ng-show="$index < mod.displaySize"  >
						<span class="pull-right">{{item.date | date : 'dd-MM-yyyy'}}</span>
						<a ng-click="summaryCtrl.gotoState(item)" class="hand-hover" ng-class="{true: 'abnormal', false: ''}[item.abnormalFlag]">{{item.displayName}}
							<small ng-show="item.classification">({{item.classification}})</small>
						</a>
					</li>
				</ul>

				<%--<span ng-class="showMoreItemsSymbol(mod)" ng-click="toggleList(mod)" ng-show="showMoreItems(mod)"></span>--%>
				<span class="glyphicon hand-hover pull-right glyphicon-chevron-up"
					  ng-click="summaryCtrl.toggleList(mod)"
					  ng-show="summaryCtrl.isSectionExpanded(mod) && !summaryCtrl.isSectionEmpty(mod)" >
				</span>
				<span class="glyphicon hand-hover pull-right glyphicon-chevron-down"
					  ng-click="summaryCtrl.toggleList(mod)"
					  ng-show="!summaryCtrl.isSectionExpanded(mod) && !summaryCtrl.isSectionEmpty(mod)">
				</span>
			</fieldset>
		</div>
	</div>
</div>
