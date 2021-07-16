<div class="juno-partial-date-select" ng-class="$ctrl.componentClasses()">
    <div class="fields flex-row">
        <input class="year"
               ng-model="$ctrl.year" maxlength="4"
               ng-class="$ctrl.getInvalidClass(!$ctrl.dateValidations.yearValid())"
               ng-blur="$ctrl.onYearBlur($ctrl.year)"
               placeholder="year">
        <input class="month"
               ng-model="$ctrl.month" maxlength="2"
               ng-class="$ctrl.getInvalidClass(!$ctrl.dateValidations.monthValid())"
               ng-blur="$ctrl.onMonthBlur($ctrl.month)"
               placeholder="month">
        <input class="day"
               ng-model="$ctrl.day" maxlength="2"
               ng-class="$ctrl.getInvalidClass(!$ctrl.dateValidations.dayValid())"
               ng-blur="$ctrl.onDayBlur($ctrl.day)"
               placeholder="day">
    </div>
</div>