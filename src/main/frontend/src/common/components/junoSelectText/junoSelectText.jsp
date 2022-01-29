<div class="juno-select-text" ng-class="$ctrl.componentClasses()">
	<label ng-if="$ctrl.label" ng-class="$ctrl.labelClasses()">
		{{$ctrl.label}}
	</label>
	<div class="flex-row juno-select-text-container">
		<select
				ng-model="$ctrl.selectModel"
				ng-disabled="$ctrl.disabled"
				ng-mousedown="$ctrl.onSelectTouched()"
				ng-change="$ctrl.onSelectChange($ctrl.selectModel)">
			<option ng-repeat="option in $ctrl.fullOptionsList" value="{{option.value}}" ng-hide="$ctrl.isPlaceHolder(option)">
				{{ $ctrl.resolveSelectLabel(option) }}
			</option>
		</select>
		<juno-input
				ng-model="$ctrl.textModel"
				placeholder="{{$ctrl.textPlaceholder}}"
				disabled="$ctrl.disabled"
		></juno-input>
	</div>
</div>