<div class="ds-rule-builder">
	<div class="flex-column">
		<juno-input label="Rule Name"
		            class="text-input"
		            label-position="$ctrl.LABEL_POSITION.TOP"
		            disabled="$ctrl.disabled"
		            ng-model="$ctrl.model.name">
		</juno-input>
		<juno-input label="Description"
		            class="text-input"
		            label-position="$ctrl.LABEL_POSITION.TOP"
		            disabled="$ctrl.disabled"
		            ng-model="$ctrl.model.description">
		</juno-input>
		<div class="flex-column">
			<h6 class="section-header">Conditions</h6>
			<div class="flex-row rule-condition" ng-repeat="condition in $ctrl.model.conditions">
				<juno-simple-close-button ng-if="!$ctrl.disabled"
				                          class="close-button"
				                          click="$ctrl.removeCondition(condition)">
				</juno-simple-close-button>
				<juno-select label="{{$first ? 'If' : 'and'}}"
				             label-position="$ctrl.LABEL_POSITION.LEFT"
				             class="first-row-input"
				             options="$ctrl.conditionTypeOptions"
				             disabled="$ctrl.disabled"
				             ng-model="condition.type">
				</juno-select>
				<juno-input ng-if="$ctrl.showConditionValueInput(condition)"
				            label="{{$ctrl.getConditionValueInputLabel(condition)}}"
				            label-position="$ctrl.LABEL_POSITION.LEFT"
				            class="row-input"
				            disabled="$ctrl.disabled"
				            ng-model="condition.value">
				</juno-input>
				<juno-select ng-if="$ctrl.showConditionValueSelect(condition)"
				             label="{{$ctrl.getConditionValueInputLabel(condition)}}"
				             label-position="$ctrl.LABEL_POSITION.LEFT"
				             class="row-input"
				             options="$ctrl.getConditionSelectOptions(condition)"
				             disabled="$ctrl.disabled"
				             ng-model="condition.value">
				</juno-select>
			</div>
			<div class="divider" ng-if="!$ctrl.disabled && $ctrl.model.conditions.length > 0"></div>
			<div ng-if="!$ctrl.disabled" class="add-button-wrapper">
				<juno-button component-style="$ctrl.componentStyle"
				             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
				             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.DEFAULT"
				             disabled="$ctrl.disabled"
				             click="$ctrl.onAddNewCondition()">
					<i class="icon icon-add"></i>
					<span>New Condition</span>
				</juno-button>
			</div>
		</div>
		<div class="flex-column">
			<h6 class="section-header">Consequences</h6>
			<div class="flex-column rule-consequence" ng-repeat="consequence in $ctrl.model.consequences">
				<juno-simple-close-button ng-if="!$ctrl.disabled"
				                          class="close-button"
				                          click="$ctrl.removeConsequence(consequence)">
				</juno-simple-close-button>
				<div class="flex-row row-padding">
					<juno-select label="{{$first ? 'Then' : 'and'}}"
					             label-position="$ctrl.LABEL_POSITION.LEFT"
					             class="first-row-input"
					             options="$ctrl.consequenceTypeOptions"
					             disabled="$ctrl.disabled"
					             ng-model="consequence.type">
					</juno-select>
					<juno-select ng-if="$ctrl.showConsequenceSeveritySelect(consequence)"
					             label="at severity level"
					             label-position="$ctrl.LABEL_POSITION.LEFT"
					             class="row-input"
					             options="$ctrl.consequenceSeverityOptions"
					             disabled="$ctrl.disabled"
					             ng-model="consequence.severityLevel">
					</juno-select>
				</div>
				<div class="flex-row row-padding">
					<div class="first-row-input flex-grow">
						<juno-input ng-if="$ctrl.showConsequenceValueInput(consequence)"
						            class="width-100"
						            label="{{$ctrl.getConsequenceValueInputLabel(consequence)}}"
						            label-position="$ctrl.LABEL_POSITION.LEFT"
						            disabled="$ctrl.disabled"
						            ng-model="consequence.message">
						</juno-input>
					</div>
				</div>
			</div>
			<div class="divider" ng-if="!$ctrl.disabled && $ctrl.model.consequences.length > 0"></div>
			<div ng-if="!$ctrl.disabled" class="add-button-wrapper">
				<juno-button component-style="$ctrl.componentStyle"
				             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
				             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.DEFAULT"
				             disabled="$ctrl.disabled"
				             click="$ctrl.onAddNewConsequence()">
					<i class="icon icon-add"></i>
					<span>New Consequence</span>
				</juno-button>
			</div>
		</div>
	</div>
</div>