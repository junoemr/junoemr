<div class="encounter-note-list-base flex-column">
	<juno-security-check show-placeholder="true" permissions="$ctrl.SecurityPermissions.EncounterNoteRead">
	<div class="note-list-header">
		<filter-panel ng-if="$ctrl.enableFilterHeader"
		              label="Filter Encounter Notes"
		              tooltip="Show/Hide note filters">
			<div class="row">
				<div class="col-md-3">
					<ca-field-boolean
							ca-name="check-filter-basic-notes"
							ca-title="Just Encounter Notes"
							ca-label-size="col-md-9"
							ca-input-size="col-md-3"
							ca-model="$ctrl.filter.onlyNotes"
							ca-template="juno"
					>
					</ca-field-boolean>
				</div>
				<div class="col-md-9">
					<ca-field-text
							ca-name="encounter-note-filter"
							ca-title="Text"
							ca-label-size="col-md-1"
							ca-input-size="col-md-11"
							ca-model="$ctrl.filter.textFilter"
							ca-rows="1"
					>
					</ca-field-text>
				</div>
			</div>
			<div class="row">
				<div class="col-md-3">
					<ca-field-boolean
							ca-name="check-filter-my-notes"
							ca-title="Just Mine"
							ca-label-size="col-md-9"
							ca-input-size="col-md-3"
							ca-model="$ctrl.filter.onlyMine"
							ca-template="juno"
					>
					</ca-field-boolean>
				</div>
				<div class="col-md-9">
					<div class="col-md-12">
						<div class="form-group pull-right">
							<button class="btn btn-sm btn-default"
							        ng-click="$ctrl.clearFilters()">
								Clear Filters
							</button>
						</div>
					</div>
				</div>
			</div>
		</filter-panel>
	</div>
	<div class="note-list-body">
		<div class="row"
		     infinite-scroll="$ctrl.addMoreItems()"
		     infinite-scroll-distance="0.5"
		     infinite-scroll-container='".primary-content-window"'
		     infinite-scroll-disabled="!$ctrl.moreNotes"
		     infinite-scroll-parent="true"
		>
			<div class="col-xs-12"
			     ng-repeat="note in $ctrl.noteList"
			     ng-show="$ctrl.showNote(note)"
			>
				<encounter-note
						note="note"
						on-edit-cpp="$ctrl.bubbleUpEditCppCallback(note, successCallback, dismissCallback)"
						on-edit-note="$ctrl.bubbleUpEditNoteCallback(note, successCallback, dismissCallback)"
						on-toggle-select="$ctrl.onSelectionStateChange(note, selected)"
						minimized="$ctrl.setNoteMinimized(note)"
				>
				</encounter-note>
			</div>
			<!-- This div prevents issues with infinite scroll loading. keep it here -->
			<div style="clear:both;"></div>
		</div>
	</div>
	</juno-security-check>
</div>