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
					ng-change="$ctrl.onSelectChange($ctrl.selectModel)"
					ng-focus="$ctrl.onSelectFocus(true)"
					ng-blur="$ctrl.onSelectFocus(false)"
					ng-options="option.value as option.label for option in $ctrl.selectOptions"
			>
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
			   autocomplete="{{$ctrl.autocompleteValue()}}"
		>
	</div>
</div>