<div class="juno-date-select" ng-class="$ctrl.componentClasses()">
	<label ng-class="$ctrl.labelClasses()">
		{{$ctrl.label}}
	</label>
	<div class="fields flex-row">
		<input class="year" ng-model="$ctrl.year"
		       ng-class="$ctrl.getInvalidClass(!$ctrl.yearValid && !$ctrl.fieldsBlank )"
		       ng-change="$ctrl.onYearChange($ctrl.year)"
		       ng-readonly="$ctrl.readonly"
		       placeholder="year">
		<input class="month"
		       ng-model="$ctrl.month"
		       ng-class="$ctrl.getInvalidClass(!$ctrl.monthValid && !$ctrl.fieldsBlank)"
		       ng-change="$ctrl.onMonthChange($ctrl.month)"
		       ng-readonly="$ctrl.readonly"
		       placeholder="month">
		<input class="day"
		       ng-model="$ctrl.day"
		       ng-class="$ctrl.getInvalidClass(!$ctrl.dayValid && !$ctrl.fieldsBlank)"
		       ng-change="$ctrl.onDayChange($ctrl.day)"
		       ng-readonly="$ctrl.readonly"
		       placeholder="day">
	</div>
	<div ng-if="$ctrl.showAge">
		({{$ctrl.getAge()}})
	</div>
</div>