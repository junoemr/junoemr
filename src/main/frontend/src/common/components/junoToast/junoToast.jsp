<div class="juno-toast" ng-class="$ctrl.getComponentClasses()" ng-click="$ctrl.onClick()">
	<div class="flex-row align-items-end justify-content-center">
		<span class="text-center">{{$ctrl.toast.message}}</span>
		<i ng-if="$ctrl.toast.icon" class="icon m-l-8" ng-class="[$ctrl.toast.icon]"></i>
	</div>
</div>