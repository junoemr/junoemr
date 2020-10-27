<div class="juno-time-select" ng-class="$ctrl.componentClasses()">
	<label  ng-if="$ctrl.label"
	        ng-class="$ctrl.labelClasses()">
		{{$ctrl.label}}
	</label>
	<div class="fields flex-row">
		<juno-select class="hour"
		             ng-model="$ctrl.hour"
		             options="$ctrl.hourOptions"
		             disabled="$ctrl.disabled"
		             component-style="$ctrl.componentStyle"
		>
		</juno-select>
		<juno-select class="minute"
		             ng-model="$ctrl.minute"
		             options="$ctrl.minuteOptions"
		             disabled="$ctrl.disabled"
		             component-style="$ctrl.componentStyle"
		>
		</juno-select>
		<juno-select class="am_pm"
		             ng-model="$ctrl.amPm"
		             options="$ctrl.amPmOptions"
		             disabled="$ctrl.disabled"
		             component-style="$ctrl.componentStyle"
		>
		</juno-select>
	</div>
</div>