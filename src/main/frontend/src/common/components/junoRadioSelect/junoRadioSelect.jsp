<div class="juno-radio-select" ng-class="$ctrl.componentClasses()">
	<label  ng-if="$ctrl.label"
			ng-class="$ctrl.labelClasses()">
		{{$ctrl.label}}
	</label>
	<div class="flex-row flex-gap-8 radio-select-container">
		<label ng-repeat="option in $ctrl.options"
		       class="radio-selector flex-row align-items-center justify-content-center flex-gap-4">
			{{option.label}}

			<input type="radio"
			       name="$ctrl.name"
                   value="{{option.value}}"
                   ng-disabled="$ctrl.disabled"
                   ng-model="$ctrl.ngModel"
                   ng-change="$ctrl.onChange(option)">
			<span class="selection-indicator" ng-class="$ctrl.radioClasses()">
				<i class="icon" ng-class="$ctrl.iconClass()"></i>
			</span>

		</label>
	</div>
</div>