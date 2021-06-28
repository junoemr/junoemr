<juno-modal class="ds-rule-edit-modal" component-style="$ctrl.resolve.style">

	<modal-title>
		<h3>Decision Rules</h3>
	</modal-title>
	<modal-ctl-buttons>
		<juno-simple-close-button click="$ctrl.onCancel()"></juno-simple-close-button>
	</modal-ctl-buttons>
	<modal-body>
		<div class="flex-column height-100">
			<div class="flex-row">
				<div class="row-item">
					<juno-check-box label="Use Existing Rule"
					                label-position="$ctrl.LABEL_POSITION.LEFT"
					                change="$ctrl.toggleRuleSelectionMode(value)"
					                ng-model="$ctrl.checkUseExisting">
					</juno-check-box>
				</div>
				<div class="row-item flex-grow">
					<juno-select placeholder="Select Rule"
					             options="$ctrl.ruleSelectionOptions"
					             disabled="!$ctrl.selectionModeExisting()"
					             on-change="$ctrl.onRuleSelect(value, option)"
					             ng-model="$ctrl.selectedRuleId">
					</juno-select>
				</div>
			</div>
			<div class="flex-row overflow-auto">
				<juno-check-box label="Create New Rule"
				                label-position="$ctrl.LABEL_POSITION.LEFT"
				                change="$ctrl.toggleRuleSelectionMode(value)"
				                ng-model="$ctrl.checkCreateNew">
				</juno-check-box>
				<ds-rule-builder ng-if="$ctrl.selectionModeNewRule()"
				                 model="$ctrl.newRule"
				                 disabled="!$ctrl.selectionModeNewRule()"
				                 class="width-100">
				</ds-rule-builder>
			</div>
		</div>

	</modal-body>
	<modal-footer>
		<div class="flex-row justify-content-end">
			<div class="footer-button-wrapper">
				<juno-button button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
				             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.DEFAULT"
				             click="$ctrl.onCancel()">
					Cancel
				</juno-button>
			</div>
			<div class="footer-button-wrapper">
				<juno-button button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
				             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
				             disabled="!$ctrl.canSubmit()"
				             click="$ctrl.onSubmit()">
					Submit
				</juno-button>
			</div>
		</div>

	</modal-footer>
</juno-modal>
