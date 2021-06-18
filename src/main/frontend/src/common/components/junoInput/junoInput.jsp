<div class="juno-input" ng-class="$ctrl.componentClasses()">

	<label ng-if="$ctrl.label" ng-class="$ctrl.labelClasses()">
		{{$ctrl.label}}
	</label>

	<div>
		<i ng-if="$ctrl.icon" class="icon" ng-class="$ctrl.icon"></i>
		<input type="{{$ctrl.type}}"
						ng-model="$ctrl.ngModel"
						ng-change="$ctrl.onChange()"
						ng-class="$ctrl.inputClasses()"
						ng-readonly="$ctrl.readonly"
						ng-focus="$ctrl.onFocus()"
						ng-blur="$ctrl.onBlur()"
						maxlength="{{$ctrl.characterLimit}}"
						ng-disabled="$ctrl.disabled"
						placeholder="{{$ctrl.placeholder}}">
		<div ng-if="!$ctrl.hideCharacterLimit" class="character-display">
			{{$ctrl.ngModel.length}}/{{$ctrl.characterLimit}}
		</div>
	</div>
</div>