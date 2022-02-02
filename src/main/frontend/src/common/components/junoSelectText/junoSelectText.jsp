<div class="juno-select-text" ng-class="$ctrl.componentClasses()">
	<label ng-if="$ctrl.label" ng-class="$ctrl.labelClasses()">
		{{$ctrl.label}}
	</label>
	<div class="flex-row juno-select-text-container"
		 ng-class="$ctrl.fieldClasses()"
	>
		<div class="select-container">
		<select ng-model="$ctrl.selectModel"
				ng-class="$ctrl.fieldClasses()"
				ng-mousedown="$ctrl.onSelectTouched()"
				ng-change="$ctrl.onSelectChange($ctrl.selectModel)"
				ng-focus="$ctrl.onSelectFocus(true)"
				ng-blur="$ctrl.onSelectFocus(false)">
			<option ng-repeat="option in $ctrl.fullOptionsList" value="{{option.value}}" ng-hide="$ctrl.isPlaceHolder(option)">
				{{ $ctrl.resolveSelectLabel(option) }}
			</option>
		</select>
		<i class="icon select-icon icon-chevron-down"></i>
		</div>
		<div id="divider-border"
			 ng-class="$ctrl.fieldClasses()"></div>
		<input ng-model="$ctrl.textModel"
				ng-class="$ctrl.fieldClasses()"
				placeholder="{{$ctrl.textPlaceholder}}"
				ng-change="$ctrl.onTextChange()"
				ng-focus="$ctrl.onInputFocus(true)"
				ng-blur="$ctrl.onInputFocus(false)"
		>
	</div>
</div>