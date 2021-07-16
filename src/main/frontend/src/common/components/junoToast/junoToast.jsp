<div class="juno-toast" ng-class="$ctrl.getComponentClasses()" ng-click="$ctrl.onClick()">
	<div class="flex-row align-items-center justify-content-center">
		<span class="text-center">{{$ctrl.toast.message}}</span>
	</div>
	<i ng-if="$ctrl.toast.icon" class="toast-icon icon" ng-class="[$ctrl.toast.icon]"></i>
</div>