<div class="juno-time-select" ng-class="$ctrl.componentClasses()">
	<label  ng-if="$ctrl.label"
	        ng-class="$ctrl.labelClasses()">
		{{$ctrl.label}}
	</label>
	<div class="fields flex-row">
		<juno-select class="hour"
		             ng-model="$ctrl.hour"
		             options="$ctrl.hourOptions"
		             change="$ctrl.onTimeChange"
		>
		</juno-select>
		<juno-select class="minute"
		             ng-model="$ctrl.minute"
		             options="$ctrl.minuteOptions"
		             change="$ctrl.onTimeChange"
		>
		</juno-select>
		<juno-select class="am_pm"
		             ng-model="$ctrl.amPm"
		             options="$ctrl.amPmOptions"
		             change="$ctrl.onTimeChange"
		>
		</juno-select>
	</div>
</div>