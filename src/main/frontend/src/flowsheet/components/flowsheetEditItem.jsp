<div class="flowsheet-edit-item">
	<div class="flex-row justify-content-between align-items-center">
		<div class="flex-column flex-grow">
			<h6>{{$ctrl.model.name}} ({{$ctrl.model.typeCode}})</h6>
			<div>{{$ctrl.model.description}}</div>
		</div>
		<div class="flex-row width-70">
			<div class="flex-column">
				<juno-input label="Input Label"
				            label-position="$ctrl.LABEL_POSITION.TOP"
				            ng-model="$ctrl.model.valueLabel">
				</juno-input>
			</div>
			<div class="flex-column row-padding">
				<juno-select label="Input Restriction"
				             label-position="$ctrl.LABEL_POSITION.TOP"
				             options="$ctrl.valueTypeOptions"
				             ng-model="$ctrl.model.valueType">
				</juno-select>
			</div>
			<div class="flex-column flex-grow">
				<juno-input label="Guideline"
				            label-position="$ctrl.LABEL_POSITION.TOP"
				            ng-model="$ctrl.model.guideline">
				</juno-input>
			</div>
		</div>
	</div>
	<div class="flex-column">
		<flowsheet-item-rule ng-repeat="rule in $ctrl.model.rules" model="rule">
		</flowsheet-item-rule>
	</div>
	<div class="flex-row flex-grow">
		<div class="add-button-wrapper">
			<juno-button component-style="$ctrl.componentStyle"
			             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
			             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.DEFAULT"
			             disabled="$ctrl.isLoading"
			             click="$ctrl.addNewRule()">
				<i class="icon icon-add"></i>
				<span>Add Rule</span>
			</juno-button>
		</div>
	</div>
</div>