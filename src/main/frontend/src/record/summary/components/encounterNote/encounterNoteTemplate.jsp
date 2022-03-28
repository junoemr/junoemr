
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<div class="form-horizontal col-lg-12 encounter-note-container"
     ng-class="{
            'note-type-cpp': ($ctrl.note.cpp && !$ctrl.note.ticklerNote),
            'note-type-eform': $ctrl.note.eformData,
            'note-type-document': $ctrl.note.document,
            'note-type-rx': $ctrl.note.rxAnnotation,
            'note-type-enc-form': $ctrl.note.encounterForm,
            'note-type-invoice': $ctrl.note.invoice,
            'note-type-tickler': $ctrl.note.ticklerNote,

            'note-in-edit' : $ctrl.inOpenEdit
     }"
>
	<div class="row note-header"
		 ng-class="{'minimized': ($ctrl.minimized || !$ctrl.allowNoteExpansion())}">
		<div class="flex-row vertical-align justify-content-between">
			<div class="flex-row justify-content-evenly note-header-info">
				<div class="body-extra-small-bold">{{$ctrl.note.observationDate | date : $ctrl.displayDateFormat }}
					<span class="note-header-title" style="word-break: break-all">{{$ctrl.getNoteHeader()}}</span>
				</div>
			</div>
			<div class="flex-row justify-content-evenly note-header-buttons">
				<button class="btn btn-xs btn-warning-static"
				        ng-show="$ctrl.isUnsignedEncounterNote()"
				        title="This note is unsigned!">
					<span class="fa fa-exclamation-triangle"></span>
				</button>
				<button class="btn btn-primary btn-xs"
				        ng-click="$ctrl.viewButtonClick()"
				        ng-show="$ctrl.showNoteViewButton()">
					View
				</button>
				<button class="btn btn-primary btn-xs"
				        ng-disabled="!$ctrl.editButtonEnabled()"
				        ng-click="$ctrl.editButtonClick()"
				        ng-show="$ctrl.showNoteEditButton()">
					Edit
				</button>
				<button class="btn disabled btn-xs"
				        ng-show="$ctrl.note.archived == true">
					Archived
				</button>
				<button class="btn btn-default btn-xs"
				        ng-disabled="!$ctrl.note.noteId"
				        ng-click="$ctrl.toggleIsSelectedForPrint()"
				        ng-class="{
				            'btn-success': $ctrl.selectedForPrint,
				            'btn-default': !$ctrl.selectedForPrint
				         }"
				        title="Add note to print list">
					<span class="fa fa-print" aria-hidden="true"></span>
				</button>
				<button class="btn btn-icon"
				        ng-disabled="!$ctrl.allowNoteExpansion()"
				        ng-click="$ctrl.toggleMinimizeNote()">
					<i class="icon icon-chevron-down" ng-if="$ctrl.minimized || !$ctrl.allowNoteExpansion()"></i>
					<i class="icon icon-chevron-up" ng-if="!$ctrl.minimized && $ctrl.allowNoteExpansion()"></i>
				</button>
			</div>
		</div>
	</div>
	<div class="row note-body"
	     ng-hide="$ctrl.minimized || !$ctrl.allowNoteExpansion()">
		<%-- use pre tags to preserve line breaks when copypasting to some places (this is browser specific) --%>
		<pre>{{$ctrl.note.note}}</pre>
		<hr class="note-divider">
	</div>

	<div class="row note-footer"
	     ng-hide="$ctrl.minimized || !$ctrl.allowNoteExpansion()">
		<div class="col-md-7">
			<div class="p-inline">
				<p class="text-muted"><bean:message key="oscarEncounter.editors.title"/>:</p>
				<p>{{$ctrl.note.editorNames}}</p>
			</div>

			<div class="p-inline">
				<p class="text-muted"><bean:message key="oscarEncounter.assignedIssues.title"/>:</p>
				<p>{{$ctrl.note.issueDescriptions}}</p>
			</div>
		</div>

		<div class="col-md-5 note-footer-right">
			<div class="p-inline">
				<p class="text-muted"><bean:message key="oscarEncounter.noteRev.title"/>:</p>
				<a ng-click="$ctrl.openRevisionHistory()" class="hand-hover">{{$ctrl.note.revision}}</a>
			</div>

			<div class="p-inline">
				<p class="text-muted"><bean:message key="oscarEncounter.encType.title"/>:</p>
				<p>{{$ctrl.note.encounterType}}</p>
			</div>
		</div>
	</div>
</div>