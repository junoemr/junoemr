<juno-modal id="flowsheet-item-modal" component-style="$ctrl.resolve.style">
	<modal-title>
		<h3>{{$ctrl.getModalTitle()}}</h3>
	</modal-title>

	<modal-ctl-buttons>
		<juno-modal-close-button click="$ctrl.cancel()">
		</juno-modal-close-button>
	</modal-ctl-buttons>
	<modal-body>
		<div class="flex-row">
			<juno-typeahead
				name="flowsheet_item_search"
				title="Search Flowsheet Item"
				class="flex-grow lg-margin-right"
				model="$ctrl.model.name"
				options="$ctrl.itemList"
				placeholder="Search...">
			</juno-typeahead>
		</div>
	</modal-body>
	<modal-footer>
		<div class="flex-row justify-content-end height-100 align-items-center">
			<div class="footer-button-wrapper row-padding-r">
				<juno-button
						button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
						button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.DEFAULT"
						click="$ctrl.cancel()">
					Cancel
				</juno-button>
			</div>
			<div class="footer-button-wrapper row-padding-l">
				<juno-button
						button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
						button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
						click="$ctrl.onComplete()">
					Add
				</juno-button>
			</div>
		</div>
	</modal-footer>
</juno-modal>