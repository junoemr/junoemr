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
<div id="patient-record-page" class="h-min-100 flex-col">
    <juno-security-check permissions="recordCtrl.SecurityPermissions.DemographicRead">
        <div class="flex-row align-items-center patient-record-header">
            <div class="flex-row align-items-center flex-item-grow">
                <h3 class="patient-header-name" ng-cloak>
                    {{recordCtrl.demographic.displayName}}
                    <span ng-show="recordCtrl.demographic.alias">({{recordCtrl.demographic.alias}})</span>
                </h3>
                <h5 class="patient-header-info">
					<span class="patient-header-label">
						<bean:message key="demographic.patient.context.sex"/>:
					</span>
                    {{recordCtrl.demographic.displaySex}}
                    <span class="patient-header-label">
						<bean:message key="demographic.patient.context.born"/>:
					</span>
                    {{recordCtrl.demographic.displayDateOfBirth}}
                    (<span>{{recordCtrl.demographic.displayAge}}</span>)
                </h5>
                <div ng-if="recordCtrl.demographic.primaryPhone" class="flex-row">
                    <i class="icon icon-tele-call"></i>
                    <h5 class="patient-header-info">
                        {{recordCtrl.demographic.primaryPhone.formattedForDisplay}}
                    </h5>
                </div>
                <div ng-if="recordCtrl.canMHACallPatient"
                     class="flex-item-grow flex-row justify-content-end p-8">
                    <juno-button title="Call patient through the MHA app"
                                 class="flex-item-no-grow"
                                 click="recordCtrl.openMhaCallPanel()"
                                 button-color="JUNO_BUTTON_COLOR.GREYSCALE_LIGHT"
                                 button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.DEFAULT">
                        <div class="flex-row align-items-center">
                            <i class="icon icon-tele-call"></i>
                            Call Patient
                        </div>
                    </juno-button>
                </div>
            </div>
            <div class="flex-row align-items-center justify-content-end">
                <div ng-if="recordCtrl.netcareModuleEnabled" class="w-128 m-l-4 m-r-4">
                    <netcare-button ng-if="recordCtrl.demographic"
                                    phn="recordCtrl.demographic.healthNumber">
                    </netcare-button>
                </div>
            </div>
        </div>
    </juno-security-check>

	<record-nav></record-nav>

	<div class="page-fill-wrapper flex-item-grow">
		<div class="primary-content-window flex-col">
			<!-- page content -->
			<div class="record-page-content flex-item-grow" ui-view></div>

			<!-- note editor-->
            <juno-security-check permissions="recordCtrl.SecurityPermissions.EncounterNoteCreate">
                <div class="row no-print" id="note-editor-container">
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
                        <div class="grid-column-3 grid-gap-4 m-b-4">
                            <div id="note-editor-enc-date" class="flex-row align-items-center justify-content-start">
                                <p>Encounter:
                                    <span>
                                    <juno-datepicker-popup juno-model="recordCtrl.page.encounterNote.observationDate" type="Link">
                                    </juno-datepicker-popup>
                                    </span>
                                </p>
                            </div>
                            <div class="dropup">
                                <input type="text" ng-model="recordCtrl.options.magicVal" placeholder="Template"
                                uib-typeahead="t.encounterTemplateName as t.encounterTemplateName for t in recordCtrl.searchTemplates($viewValue)"
                                typeahead-on-select="recordCtrl.insertTemplate($item, $model, $label)"
                                class="form-control">
                            </div>
                            <div class="dropup">
                                <juno-security-check permissions="recordCtrl.SecurityPermissions.EncounterIssueRead">
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
                                          ng-ref="recordCtrl.encounterNoteTextAreaRef"
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
                        <div id="note-editor-footer" class="flex-row grid-gap-4 justify-content-between m-b-8 flex-wrap">
                            <div class="flex-row">
                                <input type="hidden" id="startTag" value="<bean:message key="oscarEncounter.Index.startTime"/>">
                                <input type="hidden" id="endTag" value="<bean:message key="oscarEncounter.Index.endTime"/>">
                                <div class="pull-left">
                                    <button class="btn btn-danger"
                                            title="{{recordCtrl.cancelButtonTooltip()}}"
                                            ng-click="recordCtrl.cancelNoteEdit()">
                                        {{recordCtrl.cancelButtonText()}}
                                    </button>
                                </div>
                            </div>
                            <div class="flex-row justify-content-end align-items-center">
                                <div ng-if="!recordCtrl.noteDirty" class="m-r-4">
                                    <span class="body-smallest"
                                          title="{{recordCtrl.draftSavedMessage()}}">
                                        Draft Saved
                                    </span>
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

			<mha-call-panel ng-if="recordCtrl.mhaCallPanelOpen"
			                class="audio-call-panel"
			                demographic-no="recordCtrl.demographicNo">
			</mha-call-panel>
		</div>
	</div>
</div>
    
