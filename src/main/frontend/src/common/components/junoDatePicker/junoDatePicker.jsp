<div class="juno-date-picker">
	<form-label-wrapper label="{{$ctrl.label}}"
	                    label-position="$ctrl.labelPosition"
	                    label-class-list="$ctrl.labelClassList"
	                    disabled="$ctrl.disabled"
	                    component-style="$ctrl.componentStyle"
	>
		<div ng-class="$ctrl.componentClasses()"
		     ng-ref="$ctrl.datepickerContainerRef"
		     class="w-100 picker-input">
			<input ng-model="$ctrl.internalModel"
			       ng-ref="$ctrl.datepickerInputRef"
			       class="w-100"
			       ng-change="$ctrl.updateExternalModel()"
			       ng-click="$ctrl.showDatePicker()"
			       ng-disabled="$ctrl.disabled"
			       placeholder="{{$ctrl.placeholderText}}">
			<i class="icon icon-calendar"
			   ng-click="$ctrl.showDatePicker()"></i>
		</div>
	</form-label-wrapper>
</div>