<span>
    <a  ng-model="model"
        ng-click="openDatepicker()"
        uib-datepicker-popup="{{format}}" 
        is-open="opened" 
        datepicker-options="dateOptions" 
        ng-required="true" 
        close-text="Close">
        {{model | date : 'dd-MMM-yyyy'}}
    </a>
</span>