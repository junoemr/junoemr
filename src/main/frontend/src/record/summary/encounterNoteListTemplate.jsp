<div class="encounter-note-list-base">
	<div id="note-list-filters form-horizontal">
		<ca-field-boolean
				ca-name="check-filter-basic-notes"
				ca-title="Just Encounter Notes"
				ca-label-size="col-md-2"
				ca-input-size="col-md-4"
				ca-model="$ctrl.filter.onlyNotes"
				ca-template="juno"
		>
		</ca-field-boolean>
		<ca-field-boolean
				ca-name="check-filter-my-notes"
				ca-title="Just Mine"
				ca-label-size="col-md-2"
				ca-input-size="col-md-4"
				ca-model="$ctrl.filter.onlyMine"
				ca-template="juno"
		>
		</ca-field-boolean>

		<ca-field-text
				ca-name="encounter-note-filter"
				ca-title="Filter"
				ca-no-label="true"
				ca-input-size="col-md-12"
				ca-model="$ctrl.filter.textFilter"
				ca-rows="1"
		>
		</ca-field-text>
	</div>
	<div 	infinite-scroll="$ctrl.addMoreItems()"
	        infinite-scroll-distance="2"
	        infinite-scroll-use-document-bottom="true">

		<div class="col-xs-12"
		     ng-repeat="note in $ctrl.noteList"
		     ng-show="$ctrl.showNote(note)"
		>
			<encounter-note
					note="note"
					on-edit-cpp="$ctrl.bubbleUpEditCppCallback(note, successCallback, dismissCallback)"
					on-edit-note="$ctrl.bubbleUpEditNoteCallback(note, successCallback, dismissCallback)"
			>
			</encounter-note>
		</div>
	</div>
</div>