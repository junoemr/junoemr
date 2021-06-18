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
</div>