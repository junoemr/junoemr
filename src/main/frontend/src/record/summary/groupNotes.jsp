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

<!-- #996633 cpp -->
<!-- #006600 famhx -->
<!-- #306754 othermeds -->
<!-- not sure colours are staying -->
<!--ng-style="page.code == 'famhx' && {'background-color' : '#006600'} || page.code == 'othermeds' && {'background-color' : '#306754'}"-->

<!-- make div layout more fluid see medical history as an example -->
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<div class="modal-content">

	<div class="modal-header"> <!-- ng-style="setColor(note.cpp)" -->
		<button type="button" class="close" data-dismiss="modal" aria-label="<bean:message key="global.close"/>" ng-click="groupNotesCtrl.cancel()">
			<span aria-hidden="true" >&times;</span>
		</button>
		<h3 class="modal-title">{{groupNotesCtrl.page.title}}</h3>
	</div>

	<div class="modal-body">
		<%--Remove this?--%>
		<%--<div class="row">			
			<!-- TODO-legacy: what happens here if there is a long list of notes??? -->
			<div class="col-sm-10 col-sm-offset-1">
				<div class="well well-note">
					<ul data-brackets-id="12674" id="sortable" class="list-unstyled ui-sortable" >
						
					
						<li class="cpp-note-list" ng-repeat="item in page.items" ng-click="changeNote(item,item.id)" ng-class="isSelected(item)">
						<small class="pull-left text-muted">{{item.editor}}</small>
						<small class="pull-right text-muted">
						<span class="glyphicon glyphicon-calendar"></span> {{item.date | date : 'dd-MMM-yyyy'}}
						</small>
						<br>
						{{item.displayName}} <small ng-show="item.classification">({{item.classification}})</small>
						</li>
						<span class="text-muted" ng-if="page.items==null">No entries</span>
					</ul>
				</div><!-- well --> 
			</div>
			
		</div>

		<div class="row">
			<div class="col-sm-10 col-sm-offset-1">
				<!-- hidden not needed -->
				<input type="hidden" id="issueChange" name="issueChange" value="">

				<input type="hidden" id="annotation_attrib" name="annotation_attrib" ng-model="groupNotesForm.encounterNote.annotation_attrib">


				<input type="hidden" name="id" ng-model="groupNotesForm.encounterNote.id">

				<em><small><bean:message key="oscarEncounter.editors.title"/>: <span>{{ groupNotesForm.encounterNote.providerName }}</span></small></em>  
				<div class="pull-right">
					<em>
						<small>
							<bean:message key="oscarEncounter.encounterDate.title"/>: <span>{{groupNotesForm.encounterNote.updateDate | date : 'dd-MMM-yyyy'}}</span>  
							<bean:message key="oscarEncounter.noteRev.title"/>: <a href="javascript:void(0)" ng-click="openRevisionHistory(groupNotesForm.encounterNote)">{{groupNotesForm.encounterNote.revision}}</a>
						</small>
					</em>
				</div>
				<hr>	
			</div>
		</div>  --%>
		
		<div class="row">
			<div class="col-sm-12">
				<form id="frmIssueNotes"> 
					<div class="form-group col-xs-12"> 
						<label class=" control-label">Note</label>
						<textarea class="form-control" rows="8" placeholder="Enter Note" 
							ng-model="groupNotesCtrl.groupNotesForm.encounterNote.note" 
							ng-change="groupNotesCtrl.setEditingNoteFlag()" 
							required>
						</textarea>
						<em><small><bean:message key="oscarEncounter.editors.title"/>: 
							<span>{{ groupNotesCtrl.groupNotesForm.encounterNote.editorNames }}</span>
						</small></em>  
						<div class="pull-right">
							<em>
								<small>
									<bean:message key="oscarEncounter.encounterDate.title"/>: 
									<span>{{groupNotesCtrl.groupNotesForm.encounterNote.updateDate | date : 'dd-MMM-yyyy'}}</span>  
									<bean:message key="oscarEncounter.noteRev.title"/>: 
									<a href="javascript:void(0)" ng-click="groupNotesCtrl.openRevisionHistory(groupNotesCtrl.groupNotesForm.encounterNote)">
										{{groupNotesCtrl.groupNotesForm.encounterNote.revision}}
									</a>
								</small>
							</em>
						</div>
						<hr>
					</div>
							
					<%--<div class="form-group col-xs-6" ng-if="groupNotesForm.assignedCMIssues != null && groupNotesForm.assignedCMIssues.length > 0">
						<label class="control-label">Assigned Issues</label>
						<table class="table">
							<tr ng-repeat="i in groupNotesForm.assignedCMIssues">
								<td>
									<input type="button" value="restore" ng-click="restoreIssue(i)" ng-if="i.unchecked!=null && i.unchecked"/>
									<input type="button" value="remove" ng-click="removeIssue(i)" ng-if="i.unchecked==null || i.unchecked==false"/>
								</td>
								<td>{{i.issue.description}} ({{i.issue.code}})  <a ng-click="addToDxRegistry(i.issue)">( add to dx registry )</a></td>
							</tr>
						</table>
						
					</div>--%>
						
					<div class="form-group col-xs-6" ng-if="groupNotesCtrl.page.code == 'ongoingconcerns' " >
						<label class="control-label">
							<bean:message key="oscarEncounter.problemdescription.title" />
						</label>				
						<input type="text" class="form-control" id="problemdescription"	name="problemdescription" 
							ng-model="groupNotesCtrl.groupNotesForm.groupNoteExt.problemDesc" 
							placeholder="<bean:message key="oscarEncounter.problemdescription.title" />" />
					</div>

					<div class="form-group col-xs-6" ng-if="groupNotesCtrl.page.code == 'ongoingconcerns' ">
						<label class="control-label">
							<bean:message key="oscarEncounter.problemStatus.title" />
							<span class="glyphicon glyphicon-info-sign" tooltip="Examples: <bean:message key="oscarEncounter.problemStatusExample.msg" />"> </span>
						</label>
						<input type="text" class="form-control" id="problemstatus" name="problemstatus" 
							ng-model="groupNotesCtrl.groupNotesForm.groupNoteExt.problemStatus" 
							placeholder="<bean:message key="oscarEncounter.problemStatus.title" /> " 
						/>
						<!-- example: <bean:message key="oscarEncounter.problemStatusExample.msg" /> -->
					</div><!-- row -->
				
					<div class="form-group col-xs-6">		    
						<label class="control-label"><bean:message key="oscarEncounter.startdate.title" /></label>
						<juno-partial-date-select
								ng-model="groupNotesCtrl.groupNotesForm.groupNoteExt.startDate">
						</juno-partial-date-select>
					</div>	
							
					<div class="form-group col-xs-6">
						<label class="control-label"><bean:message key="oscarEncounter.resolutionDate.title" /></label>
						<juno-partial-date-select
								ng-model="groupNotesCtrl.groupNotesForm.groupNoteExt.resolutionDate">
						</juno-partial-date-select>
					</div>		    

					<div class="form-group col-xs-6" ng-if="groupNotesCtrl.page.code == 'famhx' || groupNotesCtrl.page.code == 'riskfactors'">
						<label class="control-label"><bean:message key="oscarEncounter.ageAtOnset.title" /></label>
						<input type="text" class="form-control" id="ageatonset" 
							name="ageatonset" 
							ng-model="groupNotesCtrl.groupNotesForm.groupNoteExt.ageAtOnset" 
							placeholder="<bean:message key="oscarEncounter.ageAtOnset.title" />" 
						/>
					</div>

					<div class="form-group col-xs-6" ng-if="groupNotesCtrl.page.code == 'famhx'">
						<label><bean:message key="oscarEncounter.relationship.title" /></label>
						<input type="text" class="form-control" id="relationship" 
							name="relationship" 
							ng-model="groupNotesCtrl.groupNotesForm.groupNoteExt.relationship" 
							placeholder="<bean:message key="oscarEncounter.relationship.title" />" 
						/>
					</div>

					<div class="form-group col-xs-6" ng-if="groupNotesCtrl.page.code == 'medhx' || groupNotesCtrl.page.code == 'famhx' " >
						<label class="control-label"><bean:message key="oscarEncounter.treatment.title" /></label>
						<input  type="text" class="form-control" id="treatment"
							name="treatment"
							ng-model="groupNotesCtrl.groupNotesForm.groupNoteExt.treatment"
							placeholder="<bean:message key="oscarEncounter.treatment.title" />"
						/>
					</div>

					<div class="form-group col-xs-6" ng-if="groupNotesCtrl.page.code == 'medhx'" >	
						<label class="control-label"><bean:message key="oscarEncounter.procedureDate.title" /></label>
						<juno-partial-date-select
								ng-model="groupNotesCtrl.groupNotesForm.groupNoteExt.procedureDate">
						</juno-partial-date-select>
					</div>

					<div class="form-group col-xs-6"  ng-if="groupNotesCtrl.page.code == 'riskfactors' ">
						<label class="control-label"><bean:message key="oscarEncounter.exposureDetail.title" /></label>				    							
						<input  type="text" class="form-control" id="exposuredetail" 
							name="exposuredetail" 
							ng-model="groupNotesCtrl.groupNotesForm.groupNoteExt.exposureDetail" 
							placeholder="<bean:message key="oscarEncounter.exposureDetail.title" />" 
						/>
					</div>		
				
					<div class="form-group col-xs-6" ng-if="groupNotesCtrl.page.code == 'medhx' || groupNotesCtrl.page.code == 'famhx' || groupNotesCtrl.page.code == 'ongoingconcerns' || groupNotesCtrl.page.code == 'riskfactors' ">		    
						<label class="control-label"><bean:message key="oscarEncounter.lifestage.title" /></label>
						
						<select class="form-control" name="lifestage" 
								id="lifestage" 
								ng-model="groupNotesCtrl.groupNotesForm.groupNoteExt.lifeStage">
							<option value="">
								<bean:message key="oscarEncounter.lifestage.opt.notset" />
							</option>
							<option value="N">
								<bean:message key="oscarEncounter.lifestage.opt.newborn" />
							</option>
							<option value="I">
								<bean:message key="oscarEncounter.lifestage.opt.infant" />
							</option>
							<option value="C">
								<bean:message key="oscarEncounter.lifestage.opt.child" />
							</option>
							<option value="T">
								<bean:message key="oscarEncounter.lifestage.opt.adolescent" />
							</option>
							<option value="A">
								<bean:message key="oscarEncounter.lifestage.opt.adult" />
							</option>
						</select>

					</div>

					<div class="form-group col-xs-6">
						<label class="control-label"><bean:message key="oscarEncounter.Index.btnPosition" /></label>
						<select class="form-control" id="position" ng-model="groupNotesCtrl.groupNotesForm.encounterNote.position" >
							<option ng-value="i" ng-repeat="i in groupNotesCtrl.availablePositions" >{{i}}</option>
						</select>	
					</div> <!-- row -->

					<div class="form-group col-xs-6">
						<label class="control-label"><bean:message key="oscarEncounter.hideFromPrint.title" /></label>
						<div class="form-control" ng-init="groupNotesCtrl.groupNotesForm.groupNoteExt.hideCpp=0">
							<label class="radio-inline" id="hidecpp" name="hidecpp">
								<input type="radio" id="hidecpp" name="hidecpp" ng-model="groupNotesCtrl.groupNotesForm.groupNoteExt.hideCpp" value="0"> No
							</label>
							<label class="radio-inline" >
								<input type="radio" id="hidecpp" name="hidecpp" ng-model="groupNotesCtrl.groupNotesForm.groupNoteExt.hideCpp" value="1"> Yes
							</label>
						</div><!-- form-group -->
					</div>

					<div class="form-group col-xs-6">
						<label class="control-label"><bean:message key="oscarEncounter.Index.assnIssue" /></label>
						<input type="text" class="form-control" placeholder="<bean:message key="oscarEncounter.Index.assnIssue" />"
							   uib-typeahead="i.issueId as i.code for i in groupNotesCtrl.searchIssues($viewValue)"
							   typeahead-on-select="groupNotesCtrl.assignIssue($item, $model, $label); groupNotesCtrl.selectedIssue='';"
							   ng-model="groupNotesCtrl.selectedIssue"
							   typeahead-loading="loadingIssues"
							   typeahead-min-length="3"
						/>
					</div>

					<div class="form-group col-xs-6">
						<label class="control-label"><bean:message key="oscarEncounter.addFromDxReg.title" /></label>
						<div class="input-group">
							<div class="btn-group" ng-repeat="qlist in groupNotesCtrl.page.quickLists">
								<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
									{{qlist.label}} <span class="caret"></span>
								</button>
								<ul class="dropdown-menu">
									<li><a ng-repeat="item in qlist.dxList" ng-click="groupNotesCtrl.addDxItem(item)" >{{item.description}}</a></li>
								</ul>
							</div>
						</div>
					</div>
					<div class="col-sm-12">
						<label >Assigned Issues:</label>
						<table class="table table-condensed">
							<%-- TEMPORARILY FILTER OUT SYSTEM ISSUES UNTIL WE CAN STORE THEM IN THEIR OWN FIELD --%>
							<tr ng-repeat="i in groupNotesCtrl.groupNotesForm.assignedCMIssues | filter: {issue: { type: '!' + 'system'} }" class="note-editor-issue-row">
								<td>{{i.issue.description}} ({{i.issue.code}})</td>
								<td class="text-right">
									<button class="btn btn-xs btn-danger" type="button"
									        ng-click="groupNotesCtrl.removeGroupNoteIssue(i)"
									        ng-if="i.unchecked == null || i.unchecked == false">
										Remove
									</button>
								</td>
							</tr>
						</table>
					</div>
				</form>
			</div> 
		</div>	
	</div><!-- modal-body -->		
		
	<div class="modal-footer">
		<div class="annotation-button-wrapper pull-left">
			<juno-button click="groupNotesCtrl.openAnnotation()"
			             title="open annotation window"
			             disabled="!groupNotesCtrl.groupNotesForm.encounterNote.noteId">
				<i class="icon icon-day-sheet"></i>
			</juno-button>
		</div>
		<!-- TODO-legacy: see what of these can be functions inline or maybe obsolete???
		<input type="image"	src="<c:out value="${ctx}/oscarEncounter/graphics/copy.png"/>" title='<bean:message key="oscarEncounter.Index.btnCopy"/>' onclick="copyCppToCurrentNote(); return false;"> 
		<input type="image"	src="<c:out value="${ctx}/oscarEncounter/graphics/annotation.png"/>" title='<bean:message key="oscarEncounter.Index.btnAnnotation"/>' id="anno" style="padding-right: 10px;"> 
		<input type="image"	src="<c:out value="${ctx}/oscarEncounter/graphics/edit-cut.png"/>" title='<bean:message key="oscarEncounter.Index.btnArchive"/>' onclick="$('archived').value='true';" style="padding-right: 10px;">-->
		<button type="button" class="btn"
		        ng-click="groupNotesCtrl.cancel()">
			<bean:message key="modal.newPatient.cancel" bundle="ui"/>
		</button>
		<button type="button" class="btn btn-danger"
		        ng-click="groupNotesCtrl.archiveGroupNotes()"
		        data-ng-disabled="groupNotesCtrl.isWorking()"
		        ng-hide="groupNotesCtrl.page.cannotChange">
			<bean:message key="oscarEncounter.Index.btnArchive"/>
		</button>	
		<button type="button" class="btn btn-success"
		        ng-click="groupNotesCtrl.saveGroupNotes()"
		        data-ng-disabled="groupNotesCtrl.isWorking()"
		        ng-hide="groupNotesCtrl.page.cannotChange">
			<bean:message key="oscarEncounter.Index.btnSignSave"/>
		</button>	
	</div>
</div>
