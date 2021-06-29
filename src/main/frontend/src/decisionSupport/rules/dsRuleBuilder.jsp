<div class="ds-rule-builder">
	<div class="flex-column">
		<juno-input label="Rule Name"
		            label-position="$ctrl.LABEL_POSITION.TOP"
		            disabled="$ctrl.disabled"
		            ng-model="$ctrl.model.name">
		</juno-input>
		<juno-input label="Description"
		            label-position="$ctrl.LABEL_POSITION.TOP"
		            disabled="$ctrl.disabled"
		            ng-model="$ctrl.model.description">
		</juno-input>
		<div class="flex-column">
			<h6>Conditions</h6>
			<div class="flex-row rule-condition" ng-repeat="condition in $ctrl.model.conditions">
				<juno-simple-close-button class="close-button"
				                          click="$ctrl.removeCondition(condition)">
				</juno-simple-close-button>
				<juno-select label="{{$first ? 'If' : 'and'}}"
				             label-position="$ctrl.LABEL_POSITION.LEFT"
				             options="$ctrl.conditionTypeOptions"
				             disabled="$ctrl.disabled"
				             ng-model="condition.type">
				</juno-select>
				<juno-input ng-if="$ctrl.showConditionValueInput(condition)"
				            label="{{$ctrl.getConditionValueInputLabel(condition)}}"
				            label-position="$ctrl.LABEL_POSITION.LEFT"
				            disabled="$ctrl.disabled"
				            ng-model="condition.value">
				</juno-input>
				<juno-select ng-if="$ctrl.showConditionValueSelect(condition)"
				             label="{{$ctrl.getConditionValueInputLabel(condition)}}"
				             label-position="$ctrl.LABEL_POSITION.LEFT"
				             options="$ctrl.getConditionSelectOptions(condition)"
				             disabled="$ctrl.disabled"
				             ng-model="condition.value">
				</juno-select>
			</div>
			<div class="add-button-wrapper">
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
			<h6>Consequences</h6>
			<div class="flex-column rule-consequence" ng-repeat="consequence in $ctrl.model.consequences">
				<juno-simple-close-button class="close-button"
				                          click="$ctrl.removeConsequence(consequence)">
				</juno-simple-close-button>
				<div class="flex-row row-padding">
					<juno-select label="{{$first ? 'Then' : 'and'}}"
					             label-position="$ctrl.LABEL_POSITION.LEFT"
					             options="$ctrl.consequenceTypeOptions"
					             disabled="$ctrl.disabled"
					             ng-model="consequence.type">
					</juno-select>

					<juno-select ng-if="$ctrl.showConsequenceSeveritySelect(consequence)"
					             label="at severity level"
					             label-position="$ctrl.LABEL_POSITION.LEFT"
					             options="$ctrl.consequenceSeverityOptions"
					             disabled="$ctrl.disabled"
					             ng-model="consequence.severityLevel">
					</juno-select>
				</div>
				<div class="flex-row row-padding">
					<juno-input ng-if="$ctrl.showConsequenceValueInput(consequence)"
					            class="width-100"
					            label="{{$ctrl.getConsequenceValueInputLabel(consequence)}}"
					            label-position="$ctrl.LABEL_POSITION.LEFT"
					            disabled="$ctrl.disabled"
					            ng-model="consequence.message">
					</juno-input>
				</div>
			</div>
			<div class="add-button-wrapper">
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