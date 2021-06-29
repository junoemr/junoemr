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
	<juno-security-check permissions="recordCtrl.SecurityPermissions.DEMOGRAPHIC_READ">
		<div class="flex-row align-items-center patient-record-header">
			<div class="flex-row align-items-center">
				<h3 class="patient-header-name" ng-cloak>
					{{recordCtrl.demographic.lastName}}, {{recordCtrl.demographic.firstName}}
					<span ng-show="recordCtrl.demographic.alias">({{recordCtrl.demographic.alias}})</span>
				</h3>
				<h5 class="patient-header-info">
					<span class="patient-header-label">
						<bean:message key="demographic.patient.context.sex"/>:
					</span>
					{{recordCtrl.demographic.sex}}
					<span class="patient-header-label">
						<bean:message key="demographic.patient.context.born"/>:
					</span>
					{{recordCtrl.demographic.dobYear}}-{{recordCtrl.demographic.dobMonth}}-{{recordCtrl.demographic.dobDay}}
					(<span ng-if="!recordCtrl.isNaN(recordCtrl.demographic.age)">{{recordCtrl.demographic.age}}</span>)

					<%--<span class="patient-header-label">--%>
						<%--<bean:message key="Appointment.msgTelephone"/>:--%>
					<%--</span>--%>
				</h5>
				<i class="icon icon-tele-call"></i>
				<h5 class="patient-header-info">
					{{recordCtrl.demographic.phone}}
				</h5>
			</div>
		</div>
	</juno-security-check>

	<nav class="nav record-navbar" role="navigation" id="record-nav">
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
			<ul class="nav navbar-nav breakpoint-lg-visible-exclusive" id="myTabs">
				<li ng-repeat="tab in recordCtrl.recordtabs2"
					ng-class="{'active': recordCtrl.isActive(tab) }">
					<a href="javascript:void(0)"
					   ng-if="!tab.dropdown"
					   ng-click="recordCtrl.changeTab(tab)">
						{{tab.label}}
						<%-- <strong class="text-danger" ng-show="tab.extra == 'outstanding'" title="<bean:message key="oscarEncounter.Index.ConsultOutstanding"/>">!</strong> --%>
						<span ng-show="tab.extra == 'outstanding'"
							  title="<bean:message key="oscarEncounter.Index.ConsultOutstanding"/>"
							  class="badge badge-danger ng-binding ng-scope">
							!
						</span>
					</a>

					<a href="javascript:void(0)"
					   ng-if="tab.dropdown"
					   class="dropdown-toggle"
					   data-toggle="dropdown">{{tab.label}}
						<span class="caret"></span>
					</a>

					<ul ng-if="tab.dropdown"
						class="dropdown-menu"
						role="menu">
						<li ng-repeat="dropdownItem in tab.dropdownItems"
							ng-class="{'active': recordCtrl.isActive(dropdownItem) }">
							<a href="javascript:void(0)"
							   ng-click="recordCtrl.changeTab(dropdownItem)">{{dropdownItem.label}}</a>
						</li>
					</ul>
				</li>
			</ul>

			<%-- Medium view --%>
			<ul class="nav navbar-nav breakpoint-md-visible-exclusive">
				<li ng-repeat="tab in recordCtrl.recordtabs2 | filter: recordCtrl.mediumNavItemFilter(false)"
					ng-class="{'active': recordCtrl.isActive(tab) }">
					<a href="javascript:void(0)"
					   ng-if="!tab.dropdown"
					   ng-click="recordCtrl.changeTab(tab)">
						{{tab.label}}
					</a>


					<a href="javascript:void(0)"
					   ng-if="tab.dropdown"
					   class="dropdown-toggle"
					   data-toggle="dropdown">{{tab.label}}
						<span class="caret"></span>
					</a>
					<ul ng-if="tab.dropdown"
						class="dropdown-menu"
						role="menu">
						<li ng-repeat="dropDownItem in tab.dropdownItems"
							ng-class="{'active': recordCtrl.isActive(dropDownItem) }">
							<a href="javascript:void(0)"
							   ng-click="recordCtrl.changeTab(dropDownItem)">
								{{dropDownItem.label}}
							</a>
						</li>
					</ul>

				</li>

				<li class="dropdown hand-hover">
					<a href="javascript:void(0)" class="dropdown-toggle" data-toggle="dropdown">
						More <b class="caret"></b>
					</a>

					<ul class="dropdown-menu" role="menu">
						<li ng-repeat="tab in recordCtrl.recordtabs2 | filter: recordCtrl.mediumNavItemFilter(true)"
							ng-class="{'active': recordCtrl.isActive(tab) }">
							<a href="javascript:void(0)" ng-if="!tab.dropdown"
							   ng-click="recordCtrl.changeTab(tab)">{{tab.label}}
								<strong class="text-danger" ng-show="tab.extra == 'outstanding'"
										title="<bean:message key="oscarEncounter.Index.ConsultOutstanding"/>">!</strong>
							</a>

							<a href="javascript:void(0)"
							   ng-if="tab.dropdown"
							   ng-repeat="dropdownItem in tab.dropdownItems"
							   ng-class="{'active': recordCtrl.isActive(dropdownItem) }"
							   ng-click="recordCtrl.changeTab(dropdownItem)">
								{{dropdownItem.label}}
							</a>
						</li>
					</ul>
				</li>
			</ul>

			<%-- Small view --%>
			<ul class="nav navbar-nav breakpoint-sm-visible-exclusive">
				<li class="dropdown hand-hover">
					<a href="javascript:void(0)" class="dropdown-toggle" data-toggle="dropdown">
						Menu <b class="caret"></b>
					</a>

					<ul class="dropdown-menu" role="menu">
						<li ng-repeat="tab in recordCtrl.recordtabs2"
							ng-class="{'active': recordCtrl.isActive(tab) }">
							<a href="javascript:void(0)" ng-if="!tab.dropdown"
							   ng-click="recordCtrl.changeTab(tab)">{{tab.label}}
								<strong class="text-danger"
										ng-show="tab.extra == 'outstanding'"
										title="<bean:message
										key="oscarEncounter.Index.ConsultOutstanding"/>">!</strong>
							</a>

							<a href="javascript:void(0)"
							   ng-if="tab.dropdown"
							   ng-repeat="dropdownItem in tab.dropdownItems"
							   ng-class="{'active': recordCtrl.isActive(dropdownItem) }"
							   ng-click="recordCtrl.changeTab(dropdownItem)">
								{{dropdownItem.label}}
							</a>
						</li>
					</ul>
				</li>
			</ul>
		</div>
		<!-- /.navbar-collapse -->
	</nav>
			<!-- -->
	<div class="primary-content-window">
		<div class="row">
	        <div class="include-record-peice" ui-view></div>
	    </div>

		<juno-security-check permissions="recordCtrl.SecurityPermissions.ENCOUNTER_NOTE_CREATE">
			<div class="row" id="note-editor-container">
				<div id="note-editor-minimized"
					class="col-sm-3 col-xs-12 text-center hand-hover"
					ng-click="recordCtrl.toggleNote();"
					ng-show="recordCtrl.$storage.hideNote">
					Open note editor <span class="fa fa-chevron-up"></span>
				</div>

				<div id="note-editor"
					class="col-sm-5 col-xs-12"
					resizable
					draggable
					ng-show="!recordCtrl.$storage.hideNote"
					ng-click="recordCtrl.checkAction($event)"
					ng-keypress="recordCtrl.checkAction($event)">
					<div class="row hand-hover" id="note-editor-header">
						<%--<div class="col-sm-12 text-center hand-hover" ng-click="recordCtrl.toggleNote();"  >
							Minimize
							<span class="fa fa-chevron-down"></span>
						</div>--%>
						<div class="pull-right text-right" >
							<button class="btn btn-icon" id="note-editor-minimize-button" ng-click="recordCtrl.toggleNote();">
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
								</span>
							</p>
						</div>
						<div class="col-xs-4 dropup">
							<input type="text" ng-model="recordCtrl.options.magicVal" placeholder="Template"
							uib-typeahead="t.encounterTemplateName as t.encounterTemplateName for t in recordCtrl.searchTemplates($viewValue)"
							typeahead-on-select="recordCtrl.insertTemplate($item, $model, $label)"
							class="form-control">
						</div>
						<div class="col-xs-4 dropup">
							<juno-security-check permissions="recordCtrl.SecurityPermissions.ENCOUNTER_ISSUE_READ">
								<input type="text" class="form-control" placeholder="Assign Issue"
								       ng-disabled="!recordCtrl.canSaveIssues()"
								       uib-typeahead="i.issueId as i.code for i in recordCtrl.searchIssues($viewValue)"
								       typeahead-on-select="recordCtrl.assignIssue($item, $model, $label);recordCtrl.selectedIssue='';"
								       ng-model="recordCtrl.selectedIssue"
								       typeahead-loading="loadingIssues"
								       typeahead-min-length="3"/>
							</juno-security-check>
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
									data-ng-disabled="recordCtrl.isWorking() || recordCtrl.page.encounterNote.isSigned || recordCtrl.page.encounterNote.isSaved"
									title="<bean:message key="oscarEncounter.Index.btnSave"/>">
									<span class="fa fa-save"  id="theSave"></span>
								</button>
								<button type="button" class="btn btn-success"
								        ng-click="recordCtrl.saveSignNote()"
								        data-ng-disabled="recordCtrl.isWorking()"
								        title="<bean:message key="oscarEncounter.Index.btnSignSave"/>">
									<span class="fa fa-pencil-square-o"  id="Sign"></span>
								</button>
								<button type="button" class="btn btn-success"
								        ng-click="recordCtrl.saveSignVerifyNote()"
								        data-ng-disabled="recordCtrl.isWorking()"
								        title="<bean:message key="oscarEncounter.Index.btnSign"/>">
									<span class="fa fa-thumbs-o-up"  id="SaveSignVerify"></span>
								</button>
								<button type="button" class="btn btn-success"
								        ng-click="recordCtrl.saveSignBillNote()"
								        data-ng-disabled="recordCtrl.isWorking()"
								        title="<bean:message key="oscarEncounter.Index.btnSignSaveBill"/>">
									<span class="fa fa-dollar"  id="bill"></span>
								</button>
							</div>
						</div>
					</div>
				</div>
			</div>
		</juno-security-check>
	</div>
</div>
    
