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

<juno-security-check show-placeholder="true" permissions="summaryCtrl.SecurityPermissions.EchartRead">
	<div class="col-xs-12 summary-section">
		<div class="row">
			<div class="col-md-3 col-sm-4 col-xs-12" id="summary-section-left">
				<div class="module-list" ng-repeat="mod in summaryCtrl.page.columnOne.modules">
					<summary-module
							module="mod"
							item-display-count="mod.displaySize"
							onclick-item="summaryCtrl.gotoState(item, module, successCallback, dismissCallback)"
							add-button="summaryCtrl.showAddButton(mod)"
							add-button-enabled="summaryCtrl.summaryModAddEnabled(mod)"
							onclick-add="summaryCtrl.onSummaryModAdd(module, successCallback, dismissCallback)"
							onclick-title="summaryCtrl.onSummaryModClickTitle(module)"
							clickable-title="summaryCtrl.isModTitleClickable(mod)"
							hide-date="summaryCtrl.hideSummaryModuleDate(mod)"
					>
					</summary-module>
				</div>
            </div>
			<div class="col-md-6 col-sm-7 col-xs-10 col-sm-offset-0 col-xs-offset-1"
				 ng-click="summaryCtrl.checkAction($event)"
				 ng-keypress="summaryCtrl.checkAction($event)">
				<div class="col-sm-12 summary-tabs"
					 ng-click="summaryCtrl.checkAction($event)"
					 ng-keypress="summaryCtrl.checkAction($event)">
					<ul class="nav nav-tabs round-top m-b-8 flex-row justify-content-center">
						<li class="active">
							<a ng-click="summaryCtrl.onOpenNotesTab()"
							   data-target="#all"
							   data-toggle="tab"
							   class="hand-hover">
								Notes
							</a>
						</li>
						<li>
							<a ng-click="summaryCtrl.onOpenTrackerTab()"
							   data-target="#tracker"
							   role="tab"
							   data-toggle="tab">
								Tracker
							</a>
						</li>
					</ul>
					<div ng-if="summaryCtrl.showSummaryPrintButton()" class="print-button-wrapper">
						<button class="btn btn-sm btn-primary"
						        ng-click="summaryCtrl.showPrintModal()">
							<span class="fa fa-print"></span>
							Print
						</button>

					</div>
				</div>
				<div class="col-md-12 note-list">
					<div class="tab-content">
						<div class="tab-pane active" id="all">
							<juno-security-check show-placeholder="true" permissions="summaryCtrl.SecurityPermissions.EncounterNoteRead">
								<encounter-note-list
										user-id="summaryCtrl.user.providerNo"
										on-edit-cpp="summaryCtrl.onEditCpp(note, successCallback, dismissCallback)"
										on-edit-note="summaryCtrl.bubbleUpEditNoteCallback(note, successCallback, dismissCallback)"
										selected-note-hash="summaryCtrl.page.selectedNoteHash"
										register-functions="summaryCtrl.registerEncNoteListFunctions(refresh)"
								>
								</encounter-note-list>
							</juno-security-check>
						</div>

						<div class="tab-pane h-100vh" id="tracker">
							<ui-view></ui-view>
						</div>
					</div><!-- tab content -->
				</div>
			</div>
			<div class="col-md-3 col-md-offset-0 col-xs-10 col-xs-offset-1"
				 id="summary-section-right"
				 ng-click="summaryCtrl.checkAction($event)"
				 ng-keypress="summaryCtrl.checkAction($event)">
				<div ng-repeat="mod in summaryCtrl.page.columnThree.modules">
					<summary-module
							module="mod"
							item-display-count="mod.displaySize"
							onclick-item="summaryCtrl.gotoState(item, module, successCallback, dismissCallback)"
							onclick-title="summaryCtrl.onSummaryModClickTitle(module)"
							clickable-title="summaryCtrl.isModTitleClickable(mod)"
							enable-filter="true"
                            				add-button="summaryCtrl.showAddButton(mod)"
                            				filter-type-options="summaryCtrl.getSummaryModuleFilterOptions(mod)"
					>
					</summary-module>
				</div>
			</div>
		</div>
	</div>
</div>
</juno-security-check>
