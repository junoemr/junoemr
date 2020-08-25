<div class="juno-input" ng-class="$ctrl.componentClasses()">

	<label ng-if="$ctrl.label" ng-class="$ctrl.labelClasses()">
		{{$ctrl.label}}
	</label>

	<input type="{{$ctrl.type}}"
					ng-model="$ctrl.ngModel"
					ng-change="$ctrl.onChange()"
					ng-class="$ctrl.inputClasses()"
					ng-readonly="$ctrl.readonly"
					ng-focus="$ctrl.onFocus()"
					ng-blur="$ctrl.onBlur()"
					placeholder="{{$ctrl.placeholder}}">
</div>