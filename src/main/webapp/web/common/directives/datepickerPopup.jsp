<p class="input-group">
    <input type="text" 
        class="form-control" 
        ng-model="model" 
        uib-datepicker-popup="{{format}}" 
        is-open="opened" 
        datepicker-options="dateOptions" 
        ng-required="true" 
        close-text="Close"/>
    <span class="input-group-addon clickable"  ng-click="openDatepicker()">        
        <i class="glyphicon glyphicon-calendar"></i>
    </span>
</p>