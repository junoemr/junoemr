<p class="input-group datepicker-wrapper">
    <input type="text" 
        class="form-control" 
        ng-model="model"
        ng-click="openDatepicker()"
        placeholder="{{placeholder}}" 
        uib-datepicker-popup="{{format}}" 
        is-open="opened" 
        datepicker-options="dateOptions" 
        ng-required="true" 
        close-text="Close"/>
    <span class="input-group-addon clickable" ng-if="showIcon" ng-click="openDatepicker()">        
        <i class="glyphicon glyphicon-calendar"></i>
    </span>
</p>