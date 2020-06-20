<div class="juno-date-select" ng-class="$ctrl.componentClasses()">
	<label ng-class="$ctrl.labelClasses()">
		{{$ctrl.label}}
	</label>
	<div class="fields flex-row">
		<input class="year" ng-model="$ctrl.year"
						ng-class="$ctrl.getInvalidClass(!$ctrl.yearValid)"
						ng-change="$ctrl.year = $ctrl.onYearChange($ctrl.year)"
						ng-disabled="!$ctrl.monthValid || !$ctrl.dayValid"
						placeholder="year">
		<input class="month"
						ng-model="$ctrl.month"
						ng-class="$ctrl.getInvalidClass(!$ctrl.monthValid)"
						ng-change="$ctrl.month = $ctrl.onMonthChange($ctrl.month)"
						ng-disabled="!$ctrl.yearValid || !$ctrl.dayValid"
						placeholder="month">
		<input class="day"
						ng-model="$ctrl.day"
						ng-class="$ctrl.getInvalidClass(!$ctrl.dayValid)"
						ng-change="$ctrl.day = $ctrl.onDayChange($ctrl.day)"
						ng-disabled="!$ctrl.monthValid || !$ctrl.yearValid"
						placeholder="day">
	</div>
	<div ng-if="$ctrl.showAge">
		({{$ctrl.getAge()}})
	</div>
</div>