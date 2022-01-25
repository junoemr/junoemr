<div class="juno-select-text" ng-class="$ctrl.componentClasses()">
	<label ng-if="$ctrl.label" ng-class="$ctrl.labelClasses()">
		{{$ctrl.label}}
	</label>
	<div class="flex-row juno-select-text-container">
		<select
				ng-model="$ctrl.selectModel"
				ng-options="option.value as $ctrl.resolveSelectLabel(option) disable when option.disabled for option in $ctrl.fullOptionsList"
				ng-disabled="$ctrl.disabled"
				ng-click="$ctrl.onSelectClick()"
				ng-change="$ctrl.onSelectChange()">
		</select>
		<juno-input
				ng-model="$ctrl.textModel"
				placeholder="{{$ctrl.textPlaceholder}}"
				disabled="$ctrl.disabled"
		></juno-input>
	</div>
</div>