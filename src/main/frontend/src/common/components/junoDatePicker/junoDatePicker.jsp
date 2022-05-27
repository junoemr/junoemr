<div class="juno-date-picker">
	<form-label-wrapper label="{{$ctrl.label}}"
	                    label-position="$ctrl.labelPosition"
	                    label-class-list="$ctrl.labelClassList"
	                    disabled="$ctrl.disabled"
	                    component-style="$ctrl.componentStyle"
	>
		<div ng-class="$ctrl.componentClasses()"
		     class="flex-row justify-content-center align-items-center picker-input">
			<input ng-model="$ctrl.internalModel"
			       class="w-100"
			       ng-change="$ctrl.onYearChange($ctrl.year)"
			       placeholder="{{$ctrl.placeholderText}}">
		</div>
	</form-label-wrapper>
</div>