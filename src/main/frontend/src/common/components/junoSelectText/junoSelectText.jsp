<div class="juno-select-text" ng-class="$ctrl.componentClasses()">
	<label ng-if="$ctrl.label" ng-class="$ctrl.labelClasses()">
		{{$ctrl.label}}
	</label>
	<div class="flex-row juno-select-text-container">
		<juno-select
				ng-class="$ctrl.selectModel"
				options="$ctrl.selectOptions"
				disabled="$ctrl.disabled"
		>
		</juno-select>
		<juno-input
				ng-model="$ctrl.textModel"
				placeholder="{{$ctrl.textPlaceholder}}"
				disabled="$ctrl.disabled"
		></juno-input>
	</div>
</div>
