<div class="juno-select" ng-class="$ctrl.componentClasses()">
	<label  ng-if="$ctrl.label"
	        ng-class="$ctrl.labelClasses()">
		{{$ctrl.label}}
	</label>
	<div class="select-container">
		<select ng-model="$ctrl.ngModel"
		        ng-options="option.value as option.label for option in $ctrl.options"
		        ng-change="$ctrl.onChange()">
			<option ng-if="$ctrl.placeholder" value="" disabled selected>
				{{$ctrl.placeholder}}
			</option>
		</select>
		<i class="icon icon-chevron-down"></i>
	</div>
</div>